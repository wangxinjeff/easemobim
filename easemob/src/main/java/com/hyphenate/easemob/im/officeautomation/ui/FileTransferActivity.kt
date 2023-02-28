package com.hyphenate.easemob.im.officeautomation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import com.hyphenate.easemob.R
import java.net.NetworkInterface.getNetworkInterfaces
import android.net.wifi.WifiManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.easemob.easeui.utils.FileHelper
import com.hyphenate.easemob.imlibs.mp.utils.FileUtil
import com.hyphenate.easemob.im.officeautomation.db_with_room.FileDao
import com.hyphenate.easemob.im.officeautomation.db_with_room.FileDataBase
import com.hyphenate.easemob.im.officeautomation.db_with_room.FileEntity
import com.hyphenate.easemob.im.officeautomation.utils.MyToast
import com.hyphenate.easemob.im.officeautomation.widget.CustomProgressPopup
import com.leon.lfilepickerlibrary.LFilePicker
import com.lxj.xpopup.XPopup
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.*
import java.net.*
import java.nio.channels.FileChannel
import java.security.MessageDigest


class FileTransferActivity : BaseActivity() {

    private var send: Button? = null
    var localAddress: String? = null
    private val port: Int = 30001
    private var checksums: String? = null
    private var fileLength: Long = 0
    private var serverJob: Job? = null
    private var clientJob: Job? = null
    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var fileDao: FileDao? = null

    private lateinit var recyclerView: RecyclerView
    private var data = mutableListOf<FileEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_file_transfer)

        fileDao = FileDataBase.getFileDataBase(this@FileTransferActivity).getFileDao()
        recyclerView = `$`(R.id.recylerview)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FileTransferActivity)
            adapter = FileListAdapter()
        }

        `$`<ImageView>(R.id.iv_back).setOnClickListener { finish() }

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
//        } else {
        if (networkInfo == null) {
            Toast.makeText(this, resources.getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show()
        } else {
            if (networkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                localAddress = getGprsIpAddress()
            } else if (networkInfo.type == ConnectivityManager.TYPE_WIFI) {
                localAddress = getWifiIpAddress()
            }
        }
//        }

        send = `$`(R.id.btn_send)
        send?.setOnClickListener {
            selectFileFromLocal()
        }

        serverJob = MainScope().launch(Dispatchers.IO) {

            val list = fileDao!!.getFileList()
            data.addAll(list)
            withContext(Dispatchers.Main) {
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            startServer()
        }
    }

    private suspend fun sendChecksumsMessage(address: String, checksums: String, file: File) = coroutineScope {

        try {
            clientSocket = Socket()
            clientSocket!!.connect(InetSocketAddress(address, port))
            val inputStream = clientSocket!!.getInputStream()

            val outputStream = clientSocket!!.getOutputStream()
            val dos = DataOutputStream(outputStream)
            val jsonBody = JSONObject()
            jsonBody.put("checksums-md5", checksums)
            jsonBody.put("fileLength", file.length())
            dos.writeUTF(jsonBody.toString())
//                outputStream.write(jsonBody.toString().toByteArray(charset("utf-8")))
            outputStream.flush()
            clientSocket!!.shutdownOutput()

            while (true) {
                if (inputStream.available() > 0) {
                    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                    var line: String?
                    var buffer = ""
                    while (bufferedReader.readLine().also { line = it } != null) {
                        buffer = line + buffer
                    }
                    bufferedReader.close()
                    if (buffer == "200") {
                        sendFile(address, file)
                    }
                    break
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this@FileTransferActivity, "确认设备是否在同一网络环境", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun sendFile(address: String, file: File) = coroutineScope {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(address, port))
            val outputStream = socket.getOutputStream()
            if (file.exists()) {
                val fileInput = FileInputStream(file)
                val dos = DataOutputStream(outputStream)
                dos.writeUTF(file.name)
                val bytes = ByteArray(1024)
                var length: Int
                val popup = XPopup.Builder(this@FileTransferActivity).dismissOnTouchOutside(false).asCustom(CustomProgressPopup(this@FileTransferActivity))
                while (fileInput.read(bytes).also { length = it } != -1) {
                    dos.write(bytes, 0, length)
                    withContext(Dispatchers.Main) {
                        if (!popup.isShow) popup.show()
                        progressListener?.progressChanged(file.length(), dos.size().toLong(), 0)
                    }
                }
                outputStream.flush()
                socket.shutdownOutput()
            }
            val inputStream = socket.getInputStream()
            while (true) {
                if (inputStream.available() > 0) {
                    val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
                    var line: String?
                    var buffer = ""
                    while (bufferedReader.readLine().also { line = it } != null) {
                        buffer = line + buffer
                    }
                    if (buffer == "201") {
                        println("----- send file 文件传输完成")
                    }
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private suspend fun startServer() = coroutineScope {
        serverSocket = ServerSocket()
        serverSocket?.reuseAddress = true
        serverSocket?.bind(InetSocketAddress(port))
        while (true) {
            try {
                if (serverSocket == null || serverSocket!!.isClosed) break
                val socket = serverSocket!!.accept()
                val inputStream = socket.getInputStream()
                val dis = DataInputStream(inputStream)
                val buffer = dis.readUTF()

                val outPutStream = socket.getOutputStream()
                if (buffer.contains("checksums-md5")) {
                    checksums = JSONObject(buffer).getString("checksums-md5")
                    fileLength = JSONObject(buffer).getLong("fileLength")
                    withContext(Dispatchers.Main) {
                        XPopup.Builder(this@FileTransferActivity).dismissOnTouchOutside(false).asConfirm("有新文件", "是否接收文件") {
                            MainScope().launch(Dispatchers.IO) {
                                outPutStream.write("200".toByteArray(charset("utf-8")))
                                outPutStream.flush()
                                socket.shutdownOutput()
                            }
                        }.show()
                    }
                } else {

                    outPutStream.write("201".toByteArray(charset("utf-8")))
                    outPutStream.flush()
                    socket.shutdownOutput()

                    val directory = getExternalFilesDir("p2pFile")
                    if (!directory!!.exists()) {
                        directory.mkdirs()
                    }
                    val fos = FileOutputStream(directory.absolutePath + "/" + buffer)
                    val bytes = ByteArray(1024)
                    var length = 0
                    var sum = 0
                    val popup = XPopup.Builder(this@FileTransferActivity).dismissOnTouchOutside(false).asCustom(CustomProgressPopup(this@FileTransferActivity))
                    while ({ length = dis.read(bytes, 0, bytes.size);length }() != -1) {
                        fos.write(bytes, 0, length)
                        sum += length
                        withContext(Dispatchers.Main) {
                            if (!popup.isShow) popup.show()
                            progressListener?.progressChanged(fileLength, sum.toLong(), 1)

                            if (sum.toLong() == fileLength) {
                                withContext(Dispatchers.IO) {
                                    val file = FileEntity()
                                    file.filePath = directory.absolutePath + "/" + buffer
                                    file.fileSize = fileLength
                                    fileDao!!.inserFile(file)
                                    data.add(file)
                                    val tempdata = data.distinct()
                                    data.clear()
                                    data.addAll(tempdata)
                                }
                                recyclerView.adapter!!.notifyDataSetChanged()
                            }
                        }
                        fos.flush()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getGprsIpAddress(): String? {
        try {
            val networkInterface = getNetworkInterfaces()
            while (networkInterface.hasMoreElements()) {
                val element = networkInterface.nextElement()
                val elementIp = element.inetAddresses
                while (elementIp.hasMoreElements()) {
                    if (!elementIp.nextElement().isLoopbackAddress) {
                        return elementIp.nextElement().hostAddress.toString()
                    }
                }
            }
        } catch (e: SocketException) {
            Log.i("info", e.message.toString())
        }
        return null
    }


    private fun getWifiIpAddress(): String {
        return try {
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val i = wifiInfo.ipAddress
            val sb = StringBuilder()
            sb.append(i and 0xFF).append(".").append(i shr 8 and 0xFF).append(".").append(i shr 16 and 0xFF).append(".").append(i shr 24 and 0xFF).toString()
        } catch (e: Exception) {
            e.message.toString()
        }
    }


    private fun selectFileFromLocal() {
        try {
            LFilePicker()
                    .withActivity(this@FileTransferActivity)
                    .withMutilyMode(false)
                    .withRequestCode(2000)
                    .withBackgroundColor("#405E7A")
                    .withShowHidden(false)
//                    .withStartPath("/storage/emulated/0")//指定初始显示路径
//                    .withStartPath("/storage/emulated/0/Download")//指定初始显示路径
//                    .withStartPath(Environment.getExternalStorageDirectory().path)
                    .withIsGreater(false)//过滤文件大小 小于指定大小的文件
                    .withFileSize((10240 * 1024 * 200).toLong())//指定文件大小
                    .start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2000 && data != null) {//附件选择
                val list = data.getStringArrayListExtra("paths")
                if (list != null && list.size > 0) {
                    val filePath = list[list.size - 1]
                    if (filePath != null) {
                        val file = File(filePath)
                        if (!file.exists() || !file.canRead()) {
                            MyToast.showToast(getString(R.string.file_cannot_read))
                            return
                        }

                        if (file.length() == 0L) {
                            MyToast.showToast(getString(R.string.file_zero_send_failed))
                            return
                        }
                        val fileHelper = FileHelper(this@FileTransferActivity)
                        val destFile = File(getExternalFilesDir("transferFile"), file.name)
                        try {
                            val isCopySuccess = fileHelper.copyFileTo(file, destFile)
                            if (isCopySuccess) {
                                val checksums = getMd5ByFile(destFile)
                                clientJob = MainScope().launch(Dispatchers.IO) {

                                    if (isPad()) {
                                        sendChecksumsMessage("172.17.1.151", checksums, destFile)
                                    } else {
                                        sendChecksumsMessage("172.17.3.55", checksums, destFile)
                                    }
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun isPad(): Boolean {
        return resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    private fun getMd5ByFile(file: File): String {
        var value = ""
        var `in`: FileInputStream? = null
        try {
            `in` = FileInputStream(file)
            val byteBuffer = `in`.channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length())
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(byteBuffer)
            val builder = StringBuilder()
            for (b in md5.digest()) {
                val `val` = b.toInt() and 0xff
                if (`val` < 16) {
                    builder.append("0")
                }
                builder.append(Integer.toHexString(`val`))
            }
            value = builder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (null != `in`) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return value
    }

    override fun onDestroy() {
        super.onDestroy()
        serverSocket?.let {
            it.close()
        }
        serverSocket = null
        clientSocket?.let {
            it.close()
        }
        clientSocket = null

        MainScope().launch {
            serverJob?.let {
                it.cancelAndJoin()
            }

            clientJob?.let {
                it.cancelAndJoin()
            }
        }
    }


    private inner class FileListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val view = layoutInflater.inflate(R.layout.file_list_item, parent, false)

            return FileListViewHolder(view)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            (holder as FileListViewHolder).bind(data[position])
        }


        inner class FileListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val name: TextView = view.findViewById(R.id.tv_file_name)
            val size: TextView = view.findViewById(R.id.tv_file_size)

            fun bind(file: FileEntity) {
                name.text = file.filePath
                size.text = FileUtil.fileLengthFormat(file.fileSize)
            }
        }
    }


    private var progressListener: ProgressListener? = null

    fun setProgressListener(listener: ProgressListener) {
        progressListener = listener
    }


    interface ProgressListener {
        fun progressChanged(total: Long, progress: Long, type: Int)
    }
}