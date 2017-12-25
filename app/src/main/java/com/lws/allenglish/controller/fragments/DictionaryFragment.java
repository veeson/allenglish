package com.lws.allenglish.controller.fragments;

import android.os.Bundle;
import android.text.InputFilter;
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
import com.lws.allenglish.bean.IdiomsDictionary;
import com.lws.allenglish.bean.XinhuaDictionary;
import com.lws.allenglish.model.DictionaryModel;
import com.lws.allenglish.model.OnDictionaryListener;
import com.lws.allenglish.model.impl.DictionaryModelImpl;
import com.lws.allenglish.util.StringUtils;
import com.lws.allenglish.util.common.InputMethodUtils;
import com.lws.allenglish.util.common.NetWorkUtils;
import com.lws.allenglish.util.common.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DictionaryFragment extends BaseFragment {
    @BindView(R.id.voa_english)
    EditText mWord;
    @BindView(R.id.query)
    TextView mQuery;
    @BindView(R.id.dictionary_detail)
    TextView mDictionaryDetail;
    @BindView(R.id.clear)
    ImageView mClear;
    @BindView(R.id.copy)
    ImageView mCopy;
    @BindView(R.id.share)
    ImageView mShare;

    // 表示viewpaper的页面
    private int mPageNumber = 0;
    private DictionaryModel dictionaryModel;

    public static DictionaryFragment newInstance(int page) {
        DictionaryFragment fragment = new DictionaryFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(Constants.PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        init();
        dictionaryModel = new DictionaryModelImpl(new CustomOnDictionaryListener());
        return view;
    }

    public void init() {
        mDictionaryDetail.setTextIsSelectable(true); // 设置文本可选
        if (mPageNumber == 0) {
            mWord.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            mWord.setHint("请输入一个需要查询的文字");
        } else {
            mWord.setHint("请输入需要查询的成语");
        }
        mQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodUtils.closeSoftKeyboard(view);
                }
                if (NetWorkUtils.getNetworkTypeName(BaseApplication.getInstance()).equals(NetWorkUtils.NETWORK_TYPE_DISCONNECT)) {
                    ToastUtils.show(mContext, R.string.bad_internet);
                    return;
                }
                String text = mWord.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    ToastUtils.show(mContext, "内容不能为空");
                    return;
                }
                if (mPageNumber == 0) {
                    dictionaryModel.getXinhuaDictionary(text);
                } else {
                    dictionaryModel.getIdiomsDictionary(text);
                }
            }
        });
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWord.setText("");
            }
        });
        mCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mDictionaryDetail.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                StringUtils.copyToClipboard(mContext, text);
            }
        });
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mDictionaryDetail.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }
                StringUtils.shareToApps(mContext, text);
            }
        });
    }

    private class CustomOnDictionaryListener implements OnDictionaryListener {

        @Override
        public void onXinhuaDictionarySuccess(XinhuaDictionary xinhuaDictionary) {
            if (xinhuaDictionary.error_code == 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(xinhuaDictionary.result.zi).append("\n拼音：").append(xinhuaDictionary.result.pinyin)
                        .append("\n部首：").append(xinhuaDictionary.result.bushou)
                        .append("\n笔画：").append(xinhuaDictionary.result.bihua)
                        .append("\n五笔：").append(xinhuaDictionary.result.wubi)
                        .append("\n\n");
                for (String string : xinhuaDictionary.result.jijie) {
                    sb.append(string).append("\n");
                }
                for (String string : xinhuaDictionary.result.xiangjie) {
                    sb.append(string).append("\n");
                }
                mDictionaryDetail.setText(sb);
            } else {
                ToastUtils.show(mContext, "查询不到记录这个字");
            }
        }

        @Override
        public void onIdiomsDictionarySuccess(IdiomsDictionary idiomsDictionary) {
            if (idiomsDictionary.error_code == 0) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(mWord.getText().toString().trim());
                sb2.append("\n拼音：").append(idiomsDictionary.result.pinyin).append("\n");
                if (!TextUtils.isEmpty(idiomsDictionary.result.chengyujs)) {
                    sb2.append("\n成语解释：").append(idiomsDictionary.result.chengyujs).append("\n");
                }
                if (!TextUtils.isEmpty(idiomsDictionary.result.from_)) {
                    sb2.append("\n成语出处：").append(idiomsDictionary.result.from_).append("\n");
                }
                if (!TextUtils.isEmpty(idiomsDictionary.result.example)) {
                    sb2.append("\n举例：").append(idiomsDictionary.result.example).append("\n");
                }
                if (!TextUtils.isEmpty(idiomsDictionary.result.yufa)) {
                    sb2.append("\n语法:").append(idiomsDictionary.result.yufa).append("\n");
                }
                if (!TextUtils.isEmpty(idiomsDictionary.result.yinzhengjs)) {
                    sb2.append("\n引证解释:").append(idiomsDictionary.result.yinzhengjs).append("\n");
                }
                if (idiomsDictionary.result.tongyi != null) {
                    sb2.append("\n同义词：");
                    for (String string : idiomsDictionary.result.tongyi) {
                        sb2.append(string);
                    }
                }
                if (idiomsDictionary.result.fanyi != null) {
                    sb2.append("\n\n反义词：");
                    for (String string : idiomsDictionary.result.fanyi) {
                        sb2.append(string);
                    }
                }
                mDictionaryDetail.setText(sb2);
            } else {
                ToastUtils.show(mContext, "查询不到该成语的相关信息");
            }
        }

        @Override
        public void onError() {
            ToastUtils.show(mContext, R.string.error_translation);
        }
    }
}
