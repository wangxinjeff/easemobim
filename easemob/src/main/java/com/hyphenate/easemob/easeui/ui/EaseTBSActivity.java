package com.hyphenate.easemob.easeui.ui;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.easemob.R;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

public class EaseTBSActivity extends EaseBaseActivity implements TbsReaderView.ReaderCallback {


    private ImageView ivBack;
//    private WebView mWebView;
    private RelativeLayout tbsViewContainer;
    private TbsReaderView mTbsReaderView;

    private File localFile;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.ease_activity_file_tbs);
        initViews();
        initVideoArg();

        initDatas();

    }

    private void initDatas() {
        mTbsReaderView = new TbsReaderView(this, this);
        tbsViewContainer.addView(mTbsReaderView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        String localPath = getIntent().getStringExtra("localPath");
        localFile = new File(localPath);
        if (localFile.exists()) {
            displayFile();
        } else {
            Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 加载显示文件内容
     */
    private void displayFile() {
        if (localFile == null) {
            Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("filePath", localFile.getPath());
        bundle.putString("tempPath", getExternalFilesDir(null).getPath());
        boolean result = mTbsReaderView.preOpen(parseFormat(localFile.getName()), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        } else {
            Toast.makeText(getApplicationContext(), "内置浏览器不支持！", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
//        mWebView = findViewById(R.id.forum_context);
        tbsViewContainer = findViewById(R.id.fl_tbsView);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    private void initVideoArg() {

//        public static boolean canUseTbsPlayer(Context context)
//
////判断当前Tbs播放器是否已经可以使用。
//
//        public static void openVideo(Context context, String videoUrl)
//
////直接调用播放接口，传入视频流的url
//
//        public static void openVideo(Context context, String videoUrl, Bundle extraData)
//
////extraData对象是根据定制需要传入约定的信息，没有需要可以传如null
//
////extraData可以传入key: "screenMode", 值: 102, 来控制默认的播放UI
//
////类似: extraData.putInt("screenMode", 102); 来实现默认全屏+控制栏等UI
    }


    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTbsReaderView.onStop();

    }
}
