package com.maogousoft.logisticsmobile.driver.activity.info;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import com.maogousoft.logisticsmobile.driver.Constants;
import com.maogousoft.logisticsmobile.driver.R;
import com.maogousoft.logisticsmobile.driver.activity.BaseActivity;
import com.maogousoft.logisticsmobile.driver.utils.LogUtil;
import com.maogousoft.logisticsmobile.driver.widget.HeaderView;

/**
 * Created by EdisonZhao on 15/5/19.
 */
public class AgreementCreateStep3Activity extends BaseActivity {

    private View loadingView;
    private WebView mWebView;
    private String url;
    private HeaderView mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_step3);
        mHeaderView = (HeaderView) findViewById(R.id.headerView);
        mHeaderView.setTitle(R.string.agreement_title);
        initView();
        initData();
    }

    private void initView() {
        loadingView = findViewById(R.id.loading);
        mWebView = (WebView) findViewById(R.id.webView);
    }

    private void initData() {
        url = getIntent().getStringExtra(Constants.COMMON_KEY);
        if(application.getUserType() == Constants.USER_DRIVER) {
            url = url + "&user_id=d" + application.getUserId();
        } else{
            url = url + "&user_id=u" + application.getUserId();
        }

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        /*自适应屏幕*/
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        /*支持缩放*/
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        /*可获取焦点*/
        mWebView.requestFocusFromTouch();
        mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(webViewClient);
        //这样就可以使用window.javatojs来调用它的方法
        mWebView.addJavascriptInterface(new JavaScriptInterface(), "contractJs");
        /*mWebView.addJavascriptInterface(new Object() {
            public void clickOnAndroid() {
                mHandler.post(new Runnable() {
                    public void run() {
                        webview.loadUrl("javascript:wave()");
                    }
                });
            }
        }, "demo");*/

        /*
        imageView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);*/
        mWebView.loadUrl(url);
        LogUtil.e(TAG, "AgreementCreateStep3Activity:" + url);
    }

    WebChromeClient webChromeClient = new MyWebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mHeaderView.setTitle(title);
        }

    };

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            LogUtil.d(TAG, "onPageFinished");
            //结束
            super.onPageFinished(view, url);
            loadingView.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //开始
            super.onPageStarted(view, url, favicon);
            LogUtil.d(TAG, "onPageFinished");
        }
    };

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
            return super.onCreateWindow(view, dialog, userGesture, resultMsg);
        }

        /**
         * 覆盖默认的window.alert展示界面，避免title里显示为“：来自file:////”
         */
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("提示")
                    .setMessage(message)
                    .setPositiveButton("确定", null);

            // 不需要绑定按键事件
            // 屏蔽keycode等于84之类的按键
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    LogUtil.d("onJsAlert", "keyCode==" + keyCode + "event=" + event);
                    return true;
                }
            });
            // 禁止响应按back键的事件
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
            return true;
            // return super.onJsAlert(view, url, message, result);
        }

        public boolean onJsBeforeUnload(WebView view, String url,
                                        String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        /**
         * 覆盖默认的window.confirm展示界面，避免title里显示为“：来自file:////”
         */
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("提示")
                    .setMessage(message)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    result.cancel();
                }
            });

            // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    LogUtil.d("onJsConfirm", "keyCode==" + keyCode + "event=" + event);
                    return true;
                }
            });
            // 禁止响应按back键的事件
            // builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
            // return super.onJsConfirm(view, url, message, result);
        }

        /**
         * 覆盖默认的window.prompt展示界面，避免title里显示为“：来自file:////”
         * window.prompt('请输入您的域名地址', '618119.com');
         */
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, final JsPromptResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            builder.setTitle("提示").setMessage(message);
            final EditText et = new EditText(view.getContext());
            et.setSingleLine();
            et.setText(defaultValue);
            builder.setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm(et.getText().toString());
                        }

                    })
                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });

            // 屏蔽keycode等于84之类的按键，避免按键后导致对话框消息而页面无法再弹出对话框的问题
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    LogUtil.d("onJsPrompt", "keyCode==" + keyCode + "event=" + event);
                    return true;
                }
            });

            // 禁止响应按back键的事件
            // builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
            // return super.onJsPrompt(view, url, message, defaultValue,
            // result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onRequestFocus(WebView view) {
            super.onRequestFocus(view);
        }
    }

    final class JavaScriptInterface {

        public void closeHtml() {
            LogUtil.e(TAG, "closeHtml!");
            finish();
        }

        public void openPayPasswordWindow() {
            openPayPasswordWindow("**", "**");
        }

        public void openPayPasswordWindow(String money1,String money2) {
            //验证支付密码
            Intent intent = new Intent(mContext, AgreementCreateStep4Activity.class);
            intent.putExtra(Constants.COMMON_KEY_1, money1);
            intent.putExtra(Constants.COMMON_KEY_2, money2);
            startActivity(intent);
            //startActivityForResult(intent, Constants.REQUEST_CODE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String msg = intent.getStringExtra(Constants.COMMON_KEY);
        int result = intent.getIntExtra(Constants.PAY_RESULT, -1);
        switch (result) {
            case 0:
                showMsg(msg);
                mWebView.loadUrl("javascript:sendContract()");
                break;
            case 1:
                showMsg(msg);
                break;
        }
    }
}
