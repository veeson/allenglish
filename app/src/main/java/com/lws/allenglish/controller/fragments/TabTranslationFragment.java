package com.lws.allenglish.controller.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lws.allenglish.Constants;
import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.base.BaseFragment;
import com.lws.allenglish.bean.BaiduTranslation;
import com.lws.allenglish.bean.GoogleTranslation;
import com.lws.allenglish.bean.YoudaoTranslation;
import com.lws.allenglish.controller.activities.BlankActivity;
import com.lws.allenglish.controller.activities.MainActivity;
import com.lws.allenglish.model.OnTabTranslationListener;
import com.lws.allenglish.model.TabTranslationModel;
import com.lws.allenglish.model.impl.TabTranslationModelImpl;
import com.lws.allenglish.util.CommonUtils;
import com.lws.allenglish.util.FileUtils;
import com.lws.allenglish.util.PlayAudio;
import com.lws.allenglish.util.StringUtils;
import com.lws.allenglish.util.common.InputMethodUtils;
import com.lws.allenglish.util.common.ToastUtils;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TabTranslationFragment extends BaseFragment implements View.OnClickListener {
    @BindView(R.id.translation_source)
    TextView mTranslationSource;
    @BindView(R.id.translate)
    TextView mTranslate;
    @BindView(R.id.input_translation_text)
    EditText mInputTranslationText;
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

    private static final int FILE_SELECT_CODE = 0;

    private PlayAudio mPlayAudio;
    private TabTranslationModel translationModel;

    public TabTranslationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_translation, container, false);
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        mPlayAudio = new PlayAudio();
        setUi();
        translationModel = new TabTranslationModelImpl(new CustomOnTranslationListener());
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

        CommonUtils.setTextView(mTranslationResult, null);
    }

    private void handleYoudaoTranslationContent(YoudaoTranslation youdaoTranslation) {
        int errorCode = youdaoTranslation.errorCode;
        switch (errorCode) {
            case 0:
                mTranslationResult.setText(youdaoTranslation.translation.get(0));
                break;
            case 20:
                ToastUtils.show(mContext, "要翻译的文本过长");
                break;
            case 30:
                ToastUtils.show(mContext, "无法进行有效的翻译");
                break;
            case 40:
                ToastUtils.show(mContext, "不支持的语言类型");
                break;
            case 50:
                ToastUtils.show(mContext, "无效的key");
                break;
            case 60:
                ToastUtils.show(mContext, "无词典结果，仅在获取词典结果生效");
                break;
        }
    }

    /**
     * 选择翻译引擎
     */
    private void selectTranslationSource() {
        final CharSequence[] items = {"百度翻译", "有道翻译", "谷歌翻译"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                mTranslationSource.setText(items[item].toString());
                String text = mInputTranslationText.getText().toString().trim();
                translate(text);
            }
        }).show();
    }

    private void translate(String text) {
        String transSource = mTranslationSource.getText().toString();
        if (TextUtils.isEmpty(text)){
            return;
        }
        switch (transSource) {
            case "百度翻译":
                if (text.length() >= 2000) {
                    ToastUtils.show(mContext, "百度翻译最长支持2000个字符，分成几段再试吧。");
                }
                translationModel.getBaiduTranslation(text);
                break;
            case "有道翻译":
                if (text.length() >= 200) {
                    ToastUtils.show(mContext, "有道翻译最长支持200个字符，试试百度或谷歌翻译吧。");
                }
                translationModel.getYoudaoTranslation(text);
                break;
            case "谷歌翻译":
                if (text.length() >= 600) {
                    ToastUtils.show(mContext, "谷歌翻译最长支持600个字符，试试百度翻译吧。");
                }
                translationModel.getGoogleTranslation(text);
                break;
        }
    }

    private void showAboutTxtTranslationDialog() {
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
            ToastUtils.show(mContext, "请安装一个文件选择器.");
        }
    }

    /**
     * 将.txt文本的文字转换为String并翻译
     *
     * @param path path
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
                ToastUtils.show(mContext, R.string.black_text);
                return;
            }
            mInputTranslationText.setText(fileText);
        } else {
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
                    mPlayAudio.play(BaseApplication.getInstance(), readResult, 5);
                } else {
                    String url = "http://dict.youdao.com/speech?audio="
                            + StringUtils.encodeText(readResult);
                    mPlayAudio.play(BaseApplication.getInstance(), url, 4);
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
                intent.putExtra(Constants.TRANSLATION_RESULT, mTranslationResult.getText().toString());
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
    public void onDestroy() {
        super.onDestroy();
        mPlayAudio.killTTS();
        mPlayAudio.killMediaPlayer();
    }

    private class CustomOnTranslationListener implements OnTabTranslationListener {

        @Override
        public void onBaiduTranslationSuccess(BaiduTranslation baiduTranslation) {
            mTranslationResult.setText(baiduTranslation.trans_result.get(0).dst);
        }

        @Override
        public void onYoudaoTranslationSuccess(YoudaoTranslation youdaoTranslation) {
            handleYoudaoTranslationContent(youdaoTranslation);
        }

        @Override
        public void onGoogleTranslationSuccess(GoogleTranslation googleTranslation) {
            mTranslationResult.setText(googleTranslation.sentences.get(0).trans);
        }

        @Override
        public void onError() {
            ToastUtils.show(mContext, R.string.bad_internet);
        }
    }
}
