package com.hyphenate.easemob.im.officeautomation.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.ui.EaseGroupListener;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.widget.EaseTitleBar;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.MyToast;
import com.hyphenate.util.DateUtils;
import com.hyphenate.easemob.imlibs.mp.utils.MPPathUtil;
import com.hyphenate.util.TextFormater;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SharedFilesActivity extends BaseActivity {

    private static final int REQUEST_CODE_SELECT_FILE = 1;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int pageSize = 10;
    private int pageNum = 1;

    private String groupId;
    EMGroup group;
    private List<EMMucSharedFile> fileList;

    private FilesAdapter adapter;
    private boolean hasMoreData;
    private boolean isLoading;
    private ProgressBar loadmorePB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_files);


        groupId = getIntent().getStringExtra("groupId");
        group = EMClient.getInstance().groupManager().getGroup(groupId);

        fileList = new ArrayList<>();

        EaseTitleBar title_bar = findViewById(R.id.title_bar);
        title_bar.setLeftLayoutClickListener(view -> finish());
        listView = (ListView) findViewById(R.id.list_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        loadmorePB = (ProgressBar) findViewById(R.id.pb_load_more);
        registerForContextMenu(listView);
        showFileList(true);

        findViewById(R.id.tv_upload).setOnClickListener(view -> selectFileFromLocal());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showFile(fileList.get(position));
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int lasPos = view.getLastVisiblePosition();
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && hasMoreData
                        && !isLoading
                        && lasPos == fileList.size() - 1) {
                    showFileList(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showFileList(true);
            }
        });
        EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
    }



    EaseGroupListener groupChangeListener = new EaseGroupListener() {
        @Override
        public void onUserRemoved(String s, String s1) {

        }

        @Override
        public void onGroupDestroyed(String s, String s1) {

        }

        @Override
        public void onWhiteListAdded(String s, List<String> list) {

        }

        @Override
        public void onWhiteListRemoved(String s, List<String> list) {

        }

        @Override
        public void onAllMemberMuteStateChanged(String s, boolean b) {

        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile shareFile) {
            if (group.getGroupId().equals(groupId)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFileList(true);
                    }
                });

            }
        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {
            if (group.getGroupId().equals(groupId)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showFileList(true);
                    }
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().groupManager().removeGroupChangeListener(groupChangeListener);
    }

    private void showFileList(final boolean isRefresh) {
        isLoading = true;
        if (isRefresh) {
            pageNum = 1;
            swipeRefreshLayout.setRefreshing(true);
        } else {
            pageNum++;
            loadmorePB.setVisibility(View.VISIBLE);
        }
        EMClient.getInstance().groupManager().asyncFetchGroupSharedFileList(groupId, pageNum, pageSize, new EMValueCallBack<List<EMMucSharedFile>>() {
            @Override
            public void onSuccess(final List<EMMucSharedFile> value) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            loadmorePB.setVisibility(View.INVISIBLE);
                        }
                        isLoading = false;
                        if (isRefresh)
                            fileList.clear();
                        fileList.addAll(value);
                        if (value.size() == pageSize) {
                            hasMoreData = true;
                        } else {
                            hasMoreData = false;
                        }
                        if (adapter == null) {
                            adapter = new FilesAdapter(SharedFilesActivity.this, 1, fileList);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(SharedFilesActivity.this, "Load files fail", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * If local file doesn't exits, download it first.
     * else show file directly
     *
     * @param file
     */
    private void showFile(EMMucSharedFile file) {
        final File localFile = new File(MPPathUtil.getInstance().getFilePath(), file.getFileName());
        if (localFile.exists()) {
            openFile(localFile);
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.tip_downloading));
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        EMClient.getInstance().groupManager().asyncDownloadGroupSharedFile(
                groupId,
                file.getFileId(),
                localFile.getAbsolutePath(),
                new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                openFile(localFile);
                            }
                        });
                    }

                    @Override
                    public void onError(int code, final String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(SharedFilesActivity.this, "Download file fails, " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }
                }
        );
    }

    private void openFile(File file) {
        if (file != null && file.exists()) {
            String suffix = "";
            try{
                String fileName = file.getName();
                suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            }catch (Exception e){}
            try{
                EaseCommonUtils.openFileEx(file, EaseCommonUtils.getMap(suffix), activity);
            }catch (Exception e){
                Toast.makeText(activity, "未安装能打开此文件的软件", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (!(v instanceof ListView)){
            return;
        }
        int pos = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        EMMucSharedFile emMucSharedFile = fileList.get(pos);
        if (emMucSharedFile == null){
            return;
        }
        String currentUsername = EMClient.getInstance().getCurrentUser();
        if (currentUsername.equals(emMucSharedFile.getFileOwner())){
            menu.add(getString(R.string.delete_file));
        }else if (currentUsername.equals(group.getOwner())){
            menu.add(getString(R.string.delete_file));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.deleting));
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        final int position = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;

        EMClient.getInstance().groupManager().asyncDeleteGroupSharedFile(
                groupId,
                fileList.get(position).getFileId(),
                new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                fileList.remove(position);
                                adapter.notifyDataSetChanged();

                            }
                        });
                    }

                    @Override
                    public void onError(int code, final String error) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(SharedFilesActivity.this, R.string.delete_file_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }
                }
        );

        return super.onContextItemSelected(item);
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
//        Intent intent = null;
//        if (Build.VERSION.SDK_INT < 19) { //api 19 and later, we can't use this way, demo just select from images
//            intent = new Intent(Intent.ACTION_GET_CONTENT);
//            intent.setType("*/*");
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        } else {
//            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        }
//        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);

        try {
            new LFilePicker().withActivity(this)
                    .withMutilyMode(false)
                    .withRequestCode(REQUEST_CODE_SELECT_FILE)
                    .withBackgroundColor("#405E7A")
                    //                .withStartPath("/storage/emulated/0")//指定初始显示路径
                    //                .withStartPath("/storage/emulated/0/Download")//指定初始显示路径
//                    .withStartPath(Environment.getExternalStorageDirectory().getPath())
                    .withIsGreater(false)//过滤文件大小 小于指定大小的文件
                    .withFileSize(10240 * 1024 * 20)//指定文件大小为10M
                    .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_FILE) {
                if (data != null) {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        uploadFileWithUri(uri);
//                    }
                    List<String> list = data.getStringArrayListExtra("paths");
                    if (list != null && list.size() > 0){
                        String filePath = list.get(list.size() - 1);
                        if (filePath != null) {
                            File file = new File(filePath);
                            if (!file.exists() || !file.canRead()) {
                                MyToast.showToast(getString(R.string.file_cannot_read));
                                return;
                            }

                            if (file.length() == 0) {
                                MyToast.showToast(getString(R.string.file_zero_send_failed));
                                return;
                            }
                            if (file.length() > EaseConstant.MSG_FILE_SEND_LIMIT) {
                                Toast.makeText(this, R.string.send_file_limited, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            uploadFileWithUri(Uri.fromFile(file));
                        }
                    }
                }
            }
        }
    }

    public void sendUploadFileMessage(String fileName){
//        String title = "共享了群文件：" + fileName;
//        EMMessage message = EMMessage.createTxtSendMessage(title, groupId);
//        message.setChatType(EMMessage.ChatType.GroupChat);
//        JSONObject jsonExtMsg = new JSONObject();
//        try {
//            jsonExtMsg.put("type", "notice");
//            jsonExtMsg.put("title", title);
//            jsonExtMsg.put("direction", "front");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        message.setAttribute("extMsg", jsonExtMsg);
//        EMClient.getInstance().chatManager().sendMessage(message);
    }

    private void uploadFileWithUri(Uri uri) {
        String filePath = getFilePath(uri);
        if (filePath == null) {
//            Toast.makeText(this, "only support upload image when android os >= 4.4", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(this, R.string.File_does_not_exist, Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.tip_uploading));
        pd.show();
        EMClient.getInstance().groupManager().asyncUploadGroupSharedFile(groupId, filePath, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        if (adapter != null) {
                            fileList.clear();
                            fileList.addAll(group.getShareFileList());
                            adapter.notifyDataSetChanged();
                            Toast.makeText(SharedFilesActivity.this, R.string.tip_upload_success, Toast.LENGTH_SHORT).show();
                            sendUploadFileMessage(file.getName());
                        }
                    }
                });

            }

            @Override
            public void onError(int code, final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(SharedFilesActivity.this, R.string.tip_upload_fail, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
            }
        });
    }

    @Nullable
    private String getFilePath(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;

            try {
                cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return null;
        }
        return filePath;
    }

    private static class FilesAdapter extends ArrayAdapter<EMMucSharedFile> {
        private Context mContext;
        private LayoutInflater inflater;
        List<EMMucSharedFile> list;

        public FilesAdapter(@NonNull Context context, int resource, @NonNull List<EMMucSharedFile> objects) {
            super(context, resource, objects);
            mContext = context;
            this.inflater = LayoutInflater.from(context);
            list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(mContext, R.layout.em_shared_file_row, null);
                holder.tv_file_name = (TextView) convertView.findViewById(R.id.tv_file_name);
                holder.tv_file_size = (TextView) convertView.findViewById(R.id.tv_file_size);
                holder.tv_update_time = (TextView) convertView.findViewById(R.id.tv_update_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            EMMucSharedFile sharedFile = getItem(position);
            if (sharedFile == null){
                return convertView;
            }
            holder.tv_file_name.setText(sharedFile.getFileName());
            holder.tv_file_size.setText(TextFormater.getDataSize(sharedFile.getFileSize()));
            holder.tv_update_time.setText(DateUtils.getTimestampString(new Date(sharedFile.getFileUpdateTime())));
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView tv_file_name;
        TextView tv_file_size;
        TextView tv_update_time;
    }

}
