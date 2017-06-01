package com.lws.allenglish.ui.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.bean.BaiduTranslation;
import com.lws.allenglish.bean.BaseTranslation;
import com.lws.allenglish.bean.GoogleTranslation;
import com.lws.allenglish.bean.TranslationRecord;
import com.lws.allenglish.bean.YoudaoTranslation;
import com.lws.allenglish.db.AllEnglishDatabaseManager;
import com.lws.allenglish.ui.activities.BlankActivity;
import com.lws.allenglish.ui.activities.MainActivity;
import com.lws.allenglish.utils.CommonUtils;
import com.lws.allenglish.utils.FileUtils;
import com.lws.allenglish.utils.GsonRequest;
import com.lws.allenglish.utils.InputMethodUtils;
import com.lws.allenglish.utils.MD5;
import com.lws.allenglish.utils.NetWorkUtils;
import com.lws.allenglish.utils.PlayAudio;
import com.lws.allenglish.utils.StringUtils;
import com.lws.allenglish.utils.VolleySingleton;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabTranslationFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.translation_source)
    TextView mTranslationSource;
    @BindView(R.id.translate)
    TextView mTranslate;
    @BindView(R.id.input_translation_text)
    TextView mInputTranslationText;
    @BindView(R.id.clear_input)
    ImageView mClearInput;
    @BindView(R.id.load_file)
    ImageView mLoadFile;
    @BindView(R.id.translation_result)
    TextView mTranslationResult;
    @BindView(R.id.read)
    ImageView mRead;
    @BindView(R.id.copy)
    ImageView mCopy;
    @BindView(R.id.share)
    ImageView mShare;
    @BindView(R.id.expand)
    ImageView mExpand;

    //    private static final String TAG = "TabTranslationFragment";
    private static final int FILE_SELECT_CODE = 0;
    private static final int BAIDU_TRANSLATION_WHAT = 1;
    private static final int YOUDAO_TRANSLATION_WHAT = 2;
    private static final int GOOGLE_TRANSLATION_WHAT = 3;

    private PlayAudio mPlayAudio;
    private Context mContext;
    private AllEnglishDatabaseManager mDatabaseManager;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BAIDU_TRANSLATION_WHAT:
                    BaiduTranslation baiduTranslation = (BaiduTranslation) msg.obj;
                    mTranslationResult.setText(baiduTranslation.trans_result.get(0).dst);
                    TranslationRecord record = new TranslationRecord();
                    record.text = baiduTranslation.trans_result.get(0).src;
                    record.result = baiduTranslation.trans_result.get(0).dst;
                    record.source = "百度翻译";
                    mDatabaseManager.saveTranslationRecord(record);
                    break;
                case YOUDAO_TRANSLATION_WHAT:
                    YoudaoTranslation youdaoTranslation = (YoudaoTranslation) msg.obj;
                    handleYoudaoTranslationContent(youdaoTranslation);
                    TranslationRecord record2 = new TranslationRecord();
                    record2.text = youdaoTranslation.query;
                    record2.result = youdaoTranslation.translation.get(0);
                    record2.source = "有道翻译";
                    mDatabaseManager.saveTranslationRecord(record2);
                    break;
                case GOOGLE_TRANSLATION_WHAT:
                    GoogleTranslation googleTranslation = (GoogleTranslation) msg.obj;
                    mTranslationResult.setText(googleTranslation.sentences.get(0).trans);
                    TranslationRecord record3 = new TranslationRecord();
                    record3.text = googleTranslation.sentences.get(0).orig;
                    record3.result = googleTranslation.sentences.get(0).trans;
                    record3.source = "谷歌翻译";
                    mDatabaseManager.saveTranslationRecord(record3);
                    break;
            }
        }
    };

    public TabTranslationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_translation, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        mPlayAudio = new PlayAudio();
        mDatabaseManager = AllEnglishDatabaseManager.getInstance(mContext);
        setUi();
        return view;
    }

    private void setUi() {
        mTranslationSource.setOnClickListener(this);
        mTranslate.setOnClickListener(this);
        mClearInput.setOnClickListener(this);
        mLoadFile.setOnClickListener(this);
        mRead.setOnClickListener(this);
        mCopy.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mExpand.setOnClickListener(this);

        mTranslationResult.setMovementMethod(new ScrollingMovementMethod()); // 设置TextView可滚动
        mTranslationResult.setTextIsSelectable(true); // 设置文本可选

//        TODO: 2016/12/15 后面考虑给用户加个是否每次变换翻译引擎后即刻翻译已有文本的选项
//        mTranslationSource.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String text = mInputTranslationText.getText().toString().trim();
//                if (!text.isEmpty()) {
//                    if (s.toString().equals("百度翻译")) {
//                        fetchBaiduTranslation(text);
//                    } else if (s.toString().equals("有道翻译")) {
//                        fetchYoudaoTranslation(text);
//                    } else if (s.toString().equals("谷歌翻译")) {
//                        fetchGoogleTranslation(text);
//                    }
//                } else {
//                    Toast.makeText(mContext, R.string.input_text_tip, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void fetchBaiduTranslation(String text) {
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        String sign = MD5.md5(AppConstants.BAIDU_APPID + text + salt + AppConstants.BAIDU_SECRET_KEY);
        String url = null;
        if (StringUtils.hasChinese(text)) {
            url = "http://api.fanyi.baidu.com/api/trans/vip/translate?salt=" + salt + "&appid=" +
                    AppConstants.BAIDU_APPID + "&sign=" + sign + "&from=auto&to=en&q=" +
                    StringUtils.encodeText(text);
        } else {
            url = "http://api.fanyi.baidu.com/api/trans/vip/translate?salt=" + salt + "&appid=" +
                    AppConstants.BAIDU_APPID + "&sign=" + sign + "&from=auto&to=zh&q=" +
                    StringUtils.encodeText(text);
        }
        VolleySingleton.getInstance(mContext).addToRequestQueue(new GsonRequest<>(url, BaiduTranslation.class, null, new Response.Listener<BaiduTranslation>() {
            @Override
            public void onResponse(BaiduTranslation response) {
                sendMessage(response, BAIDU_TRANSLATION_WHAT);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, R.string.error_translation, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void fetchYoudaoTranslation(String text) {
        VolleySingleton.getInstance(mContext).addToRequestQueue(new GsonRequest<>("http://fanyi.youdao.com/openapi.do?keyfrom=allenglish&key=1877329489&type=data&doctype=json&version=1.1&only=translate&q=" +
                StringUtils.encodeText(text), YoudaoTranslation.class, null, new Response.Listener<YoudaoTranslation>() {
            @Override
            public void onResponse(YoudaoTranslation response) {
                sendMessage(response, YOUDAO_TRANSLATION_WHAT);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, R.string.error_translation, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void fetchGoogleTranslation(String text) {
        String url = null;
        if (StringUtils.hasChinese(text)) {
            url = "https://translate.google.cn/translate_a/single?client=gtx&sl=auto&tl=auto&hl=en&dt=t&dt=tl&dj=1&source=icon&q=" +
                    StringUtils.encodeText(text);
        } else {
            url = "https://translate.google.cn/translate_a/single?client=gtx&sl=auto&tl=auto&hl=zh-CN&dt=t&dt=tl&dj=1&source=icon&q=" +
                    StringUtils.encodeText(text);
        }
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");
        VolleySingleton.getInstance(mContext).addToRequestQueue(new GsonRequest<>(url, GoogleTranslation.class, headers, new Response.Listener<GoogleTranslation>() {
            @Override
            public void onResponse(GoogleTranslation response) {
                sendMessage(response, GOOGLE_TRANSLATION_WHAT);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, R.string.error_translation, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    /**
     * 向handle发送信息
     *
     * @param t                 要发送的对象object
     * @param translationSource 翻译来源，1表示百度翻译，2表示有道翻译，3表示谷歌翻译。
     * @param <T>
     */
    private <T extends BaseTranslation> void sendMessage(T t, int translationSource) {
        Message msg = mHandler.obtainMessage();
        msg.what = translationSource;
        msg.obj = t;
        msg.sendToTarget();
    }

    private void handleYoudaoTranslationContent(YoudaoTranslation youdaoTranslation) {
        int errorCode = youdaoTranslation.errorCode;
        switch (errorCode) {
            case 0:
                mTranslationResult.setText(youdaoTranslation.translation.get(0));
                break;
            case 20:
                Toast.makeText(mContext, "要翻译的文本过长", Toast.LENGTH_SHORT).show();
                break;
            case 30:
                Toast.makeText(mContext, "无法进行有效的翻译", Toast.LENGTH_SHORT).show();
                break;
            case 40:
                Toast.makeText(mContext, "不支持的语言类型", Toast.LENGTH_SHORT).show();
                break;
            case 50:
                Toast.makeText(mContext, "无效的key", Toast.LENGTH_SHORT).show();
                break;
            case 60:
                Toast.makeText(mContext, "无词典结果，仅在获取词典结果生效", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 选择翻译引擎
     */
    private void selectTranslationSource() {
//        new MaterialDialog.Builder(mContext)
//                .items(R.array.trans_source_array)
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
//                        mTranslationSource.setText(text.toString());
//                    }
//                })
//                .contentColor(getResources().getColor(android.R.color.darker_gray))
//                .backgroundColor(getResources().getColor(android.R.color.white))
//                .show();

        final CharSequence[] items = {"百度翻译", "有道翻译", "谷歌翻译"};

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mTranslationSource.setText(items[item].toString());
            }
        }).show();
    }

    private void translate(String text) {
        String transSource = mTranslationSource.getText().toString();
        switch (transSource) {
            case "百度翻译":
                if (text.length() >= 2000) {
                    Toast.makeText(mContext, "百度翻译最长支持2000个字符，分成几段再试吧。", Toast.LENGTH_SHORT).show();
                }
                fetchBaiduTranslation(text);
                break;
            case "有道翻译":
                if (text.length() >= 200) {
                    Toast.makeText(mContext, "有道翻译最长支持200个字符，试试百度或谷歌翻译吧。", Toast.LENGTH_SHORT).show();
                }
                fetchYoudaoTranslation(text);
                break;
            case "谷歌翻译":
                if (text.length() >= 600) {
                    Toast.makeText(mContext, "谷歌翻译最长支持600个字符，分成百度翻译吧。", Toast.LENGTH_SHORT).show();
                }
                fetchGoogleTranslation(text);
                break;
        }
    }

    private void showAboutTxtTranslationDialog() {
//        new MaterialDialog.Builder(getActivity())
//                .content("文本翻译是一项测试功能，目前只支持TXT文本，比如filename.txt，暂不支持.doc等其它任何格式的文本。")
//                .positiveText("选择文本")
//                .negativeText("取消")
//                .onPositive(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        showFileChooser();
//                        dialog.dismiss();
//                    }
//                })
//                .onNegative(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        dialog.dismiss();
//                    }
//                })
//                .negativeColor(getResources().getColor(android.R.color.holo_red_dark))
//                .positiveColor(getResources().getColor(android.R.color.holo_red_dark))
//                .contentColor(getResources().getColor(android.R.color.darker_gray))
//                .backgroundColor(getResources().getColor(android.R.color.white))
//                .show();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setMessage("文本翻译是一项测试功能，目前只支持TXT文本，比如filename.txt，暂不支持.doc等其它任何格式的文本。")
                .setPositiveButton("选择文本", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        showFileChooser();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 选择文件
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "选择要翻译的txt文本"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(mContext, "请安装一个文件选择器.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 将.txt文本的文字转换为String并翻译
     *
     * @param path
     */
    private void translateFile(String path) {
        if (path.endsWith(".txt")) {
            String fileText = null;
            try {
                fileText = FileUtils.readFile(path, FileUtils.resolveCode(path)).replace("\n", "").replace("\r", " ").trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TextUtils.isEmpty(fileText)) {
                Toast.makeText(mContext, R.string.black_text, Toast.LENGTH_SHORT).show();
                return;
            }
            mInputTranslationText.setText(fileText);
        } else {
//            new MaterialDialog.Builder(mContext)
//                    .content("sorry~~~文本翻译目前只支持.txt文本格式.")
//                    .positiveText("")
//                    .onPositive(new MaterialDialog.SingleButtonCallback() {
//                        @Override
//                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .positiveColor(getResources().getColor(android.R.color.holo_red_dark))
//                    .contentColor(getResources().getColor(android.R.color.darker_gray))
//                    .backgroundColor(getResources().getColor(android.R.color.white))
//                    .show();

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setMessage("sorry~~~文本翻译目前只支持.txt文本格式.")
                    .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.translation_source:
                selectTranslationSource();
                break;
            case R.id.translate:
                if (CommonUtils.isFastDoubleClick()) {
                    return;
                }
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodUtils.closeSoftKeyboard(view);
                }
                String text = mInputTranslationText.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(mContext, R.string.input_text_tip, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (NetWorkUtils.getNetworkTypeName(mContext).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
                    Toast.makeText(mContext, R.string.bad_internet, Toast.LENGTH_SHORT).show();
                    return;
                }
                translate(text);
                break;
            case R.id.clear_input:
                mInputTranslationText.setText("");
                break;
            case R.id.load_file:
                showAboutTxtTranslationDialog();
                break;
            case R.id.read:
                String readResult = mTranslationResult.getText().toString();
                if (TextUtils.isEmpty(readResult)) {
                    return;
                }
                if (StringUtils.hasChinese(readResult)) {
                    mPlayAudio.play(mContext, readResult, 5);
                } else {
                    String url = "http://dict.youdao.com/speech?audio="
                            + StringUtils.encodeText(readResult);
                    mPlayAudio.play(mContext, url, 4);
                }
                break;
            case R.id.copy:
                String copyResult = mTranslationResult.getText().toString();
                if (!TextUtils.isEmpty(copyResult)) {
                    StringUtils.copyToClipboard(mContext, copyResult);
                }
                break;
            case R.id.share:
                String shareText = mTranslationResult.getText().toString();
                if (!TextUtils.isEmpty(shareText)) {
                    StringUtils.shareToApps(getActivity(), shareText);
                }
                break;
            case R.id.expand:
                Intent intent = new Intent(mContext, BlankActivity.class);
                intent.putExtra(AppConstants.TRANSLATION_RESULT, mTranslationResult.getText().toString());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == MainActivity.RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    String path = null;
                    try {
                        path = FileUtils.getPath(mContext, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                    translateFile(path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayAudio.killTTS();
    }
}
