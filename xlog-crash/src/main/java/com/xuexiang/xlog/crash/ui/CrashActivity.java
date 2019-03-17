package com.xuexiang.xlog.crash.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xuexiang.xlog.crash.R;
import com.xuexiang.xlog.crash.XCrash;
import com.xuexiang.xlog.crash.mail.MailInfo;
import com.xuexiang.xlog.utils.FileUtils;
import com.xuexiang.xlog.utils.TimeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 崩溃信息展示
 *
 * @author xuexiang
 * @since 2019/3/13 下午11:16
 */
public class CrashActivity extends AppCompatActivity {

    public static final String KEY_CRASH_INFO = "key_crash_info";
    public static final String KEY_MAIL_INFO = "key_mail_info";
    public static final String KEY_THEME_ID = "key_theme_id";

    private CrashInfo mCrashInfo;
    private MailInfo mMailInfo;

    private ScrollView mScrollView;
    private ViewGroup mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(getIntent().getIntExtra(KEY_THEME_ID, R.style.XCrashTheme_Light));
        mCrashInfo = getIntent().getParcelableExtra(KEY_CRASH_INFO);
        mMailInfo = getIntent().getParcelableExtra(KEY_MAIL_INFO);
        if (mCrashInfo == null) {
            finish();
            return;
        }

        setContentView(R.layout.xlog_activity_crash);

        initView();

        findViewById(R.id.tv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(CrashActivity.this, v);
                if (mMailInfo != null) {
                    menu.inflate(R.menu.menu_more_enable_email);
                } else {
                    menu.inflate(R.menu.menu_more_disenable_email);
                }
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.xlog_crash_menu_copy_text) {
                            String crashText = getShareText(mCrashInfo);
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cm != null) {
                                ClipData clipData = ClipData.newPlainText("crash", crashText);
                                cm.setPrimaryClip(clipData);
                                showToast(getString(R.string.xlog_crash_copied));
                            }
                        } else if (id == R.id.xlog_crash_menu_share_text) {
                            String crashText = getShareText(mCrashInfo);
                            shareText(crashText);
                        } else if (id == R.id.xlog_crash_menu_share_image) {
                            if (ContextCompat.checkSelfPermission(CrashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                                    || ContextCompat.checkSelfPermission(CrashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                requestPermission(REQUEST_CODE_SHARE_IMAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            } else {
                                shareImage();
                            }
                        } else if (id == R.id.xlog_crash_menu_send_email) {
                            if (ContextCompat.checkSelfPermission(CrashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                                    || ContextCompat.checkSelfPermission(CrashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                requestPermission(REQUEST_CODE_SEND_EMAIL, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            } else {
                                sendEmail();
                            }
                        }
                        return true;
                    }
                });

            }
        });

    }

    private void initView() {
        mScrollView = findViewById(R.id.scrollView);
        mToolbar = findViewById(R.id.toolbar);
        TextView tvPackageName = findViewById(R.id.tv_packageName);
        TextView textMessage = findViewById(R.id.textMessage);
        TextView tvClassName = findViewById(R.id.tv_className);
        TextView tvMethodName = findViewById(R.id.tv_methodName);
        TextView tvLineNumber = findViewById(R.id.tv_lineNumber);
        TextView tvExceptionType = findViewById(R.id.tv_exceptionType);
        TextView tvFullException = findViewById(R.id.tv_fullException);
        TextView tvTime = findViewById(R.id.tv_time);
        TextView tvModel = findViewById(R.id.tv_model);
        TextView tvBrand = findViewById(R.id.tv_brand);
        TextView tvVersion = findViewById(R.id.tv_version);

        tvPackageName.setText(mCrashInfo.getPackageName());
        textMessage.setText(mCrashInfo.getExceptionMsg());
        tvClassName.setText(mCrashInfo.getFileName());
        tvMethodName.setText(mCrashInfo.getMethodName());
        tvLineNumber.setText(String.valueOf(mCrashInfo.getLineNumber()));
        tvExceptionType.setText(mCrashInfo.getExceptionType());
        tvFullException.setText(mCrashInfo.getFullException());
        tvTime.setText(getFormatDate(mCrashInfo));

        tvModel.setText(mCrashInfo.getDevice().getModel());
        tvBrand.setText(mCrashInfo.getDevice().getBrand());
        tvVersion.setText(mCrashInfo.getDevice().getVersion());
    }

    private String getShareText(CrashInfo model) {
        StringBuilder builder = new StringBuilder();

        builder.append(getString(R.string.xlog_crash_crash_info))
                .append("\n")
                .append(model.getExceptionMsg())
                .append("\n");

        //空一行，好看点，(#^.^#)
        builder.append("\n");

        builder.append(getString(R.string.xlog_crash_packet_name))
                .append(model.getPackageName()).append("\n");
        builder.append(getString(R.string.xlog_crash_class_name))
                .append(model.getFileName()).append("\n");
        builder.append(getString(R.string.xlog_crash_method_name)).append(model.getMethodName()).append("\n");
        builder.append(getString(R.string.xlog_crash_line_number)).append(model.getLineNumber()).append("\n");
        builder.append(getString(R.string.xlog_crash_exception_type)).append(model.getExceptionType()).append("\n");
        builder.append(getString(R.string.xlog_crash_time)).append(getFormatDate(model)).append("\n");

        //空一行，好看点，(#^.^#)
        builder.append("\n");

        builder.append(getString(R.string.xlog_crash_model)).append(model.getDevice().getModel()).append("\n");
        builder.append(getString(R.string.xlog_crash_brand)).append(model.getDevice().getBrand()).append("\n");
        builder.append(getString(R.string.xlog_crash_version)).append(model.getDevice().getVersion()).append("\n");

        //空一行，好看点，(#^.^#)
        builder.append("\n");

        builder.append(getString(R.string.xlog_crash_all_info))
                .append("\n")
                .append(model.getFullException()).append("\n");

        return builder.toString();
    }

    private String getFormatDate(CrashInfo model) {
        return TimeUtils.format(model.getTime(), "yyyy-MM-dd HH:mm");
    }

    private void shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.xlog_crash_crash_info));
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.xlog_crash_share_to)));
    }

    private static final int REQUEST_CODE_SHARE_IMAGE = 110;

    private static final int REQUEST_CODE_SEND_EMAIL = 111;

    private void requestPermission(int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //判断请求码，确定当前申请的权限
        if (requestCode == REQUEST_CODE_SHARE_IMAGE || requestCode == REQUEST_CODE_SEND_EMAIL) {
            //判断权限是否申请通过
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //授权成功
                if (requestCode == REQUEST_CODE_SHARE_IMAGE) {
                    shareImage();
                } else {
                    sendEmail();
                }
            } else {
                //授权失败
                showToast(R.string.xlog_crash_permission_sd);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public Bitmap getBitmapByView(ViewGroup toolbar, ScrollView scrollView) {
        if (toolbar == null || scrollView == null) {
            return null;
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int svHeight = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            svHeight += scrollView.getChildAt(i).getHeight();
        }
        int height = svHeight + toolbar.getHeight();
        //
        Bitmap resultBitmap = Bitmap.createBitmap(toolbar.getWidth(), height, Bitmap.Config.ARGB_8888);
        Canvas rootCanvas = new Canvas(resultBitmap);
        //
        Bitmap toolbarBitmap = Bitmap.createBitmap(toolbar.getWidth(), toolbar.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas toolbarCanvas = new Canvas(toolbarBitmap);
        toolbarCanvas.drawRGB(255, 255, 255);
        toolbar.draw(toolbarCanvas);
        //
        Bitmap svBitmap = Bitmap.createBitmap(toolbar.getWidth(), svHeight,
                Bitmap.Config.ARGB_8888);
        Canvas svCanvas = new Canvas(svBitmap);
        svCanvas.drawRGB(255, 255, 255);
        scrollView.draw(svCanvas);

        rootCanvas.drawBitmap(toolbarBitmap, 0, 0, paint);
        rootCanvas.drawBitmap(svBitmap, 0, toolbar.getHeight(), paint);
        return resultBitmap;
    }

    private File BitmapToFile(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        File imageFile = new File(path, "XLog_" + getFormatDate(mCrashInfo) + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }

    private void shareImage() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            showToast(R.string.xlog_crash_no_sdcard);
            return;
        }
        File file = BitmapToFile(getBitmapByView(mToolbar, mScrollView));
        if (file == null || !file.exists()) {
            showToast(R.string.xlog_crash_image_not_exist);
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, FileUtils.getUriForFile(file));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, getString(R.string.xlog_crash_share_to)));
    }

    /**
     * 发送邮件
     */
    private void sendEmail() {
        final MailInfo sender = mMailInfo
                .setTitle(getString(R.string.xlog_title_crash_report_email) + "【" + TimeUtils.getCurTime("yyyy-MM-dd HH:mm:ss") + "】")
                .setContent(getShareText(mCrashInfo));
        sender.init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sender.addAttachment(mCrashInfo.getCrashLogFilePath());
                    sender.send();
                    showToast(R.string.xlog_email_send_success);
                } catch (Exception e) {
                    showToast(R.string.xlog_email_send_failed);
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showToast(final String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showToast(@StringRes final int textId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(getApplicationContext(), textId, Toast.LENGTH_SHORT).show();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), textId, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
