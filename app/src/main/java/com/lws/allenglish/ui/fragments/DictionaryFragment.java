package com.lws.allenglish.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lws.allenglish.AppConstants;
import com.lws.allenglish.R;
import com.lws.allenglish.base.BaseApplication;
import com.lws.allenglish.bean.IdiomsDictionary;
import com.lws.allenglish.bean.XinhuaDictionary;
import com.lws.allenglish.utils.GsonRequest;
import com.lws.allenglish.utils.InputMethodUtils;
import com.lws.allenglish.utils.NetWorkUtils;
import com.lws.allenglish.utils.StringUtils;
import com.lws.allenglish.utils.VolleySingleton;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryFragment extends Fragment {
    @BindView(R.id.word)
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

    private Context mContext;
    // 表示viewpaper的页面
    private int mPageNumber = 0;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) { // mPageNumber
                case 0:
                    XinhuaDictionary xinhuaDictionary = (XinhuaDictionary) msg.obj;
                    if (xinhuaDictionary.error_code == 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(xinhuaDictionary.result.zi).append("\n拼音：").append(xinhuaDictionary.result.pinyin)
                                .append("\n部首：").append(xinhuaDictionary.result.bihua)
                                .append("\n笔画：").append(xinhuaDictionary.result.bihua)
                                .append("\n五笔：").append(xinhuaDictionary.result.wubi)
                                .append("\n\n--------------------基本解释--------------------\n");
                        for (String string : xinhuaDictionary.result.jijie) {
                            sb.append(string).append("\n");
                        }
                        sb.append("\n--------------------详细解释--------------------\n");
                        for (String string : xinhuaDictionary.result.xiangjie) {
                            sb.append(string).append("\n");
                        }
                        mDictionaryDetail.setText(sb);
                    } else {
                        Toast.makeText(mContext, "查询不到记录这个字", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    IdiomsDictionary idiomsDictionary = (IdiomsDictionary) msg.obj;
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
                        Toast.makeText(mContext, "查询不到该成语的相关信息", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public static DictionaryFragment newInstance(int page) {
        DictionaryFragment fragment = new DictionaryFragment();
        Bundle args = new Bundle();
        args.putInt(AppConstants.PAGE_NUMBER, page);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AppConstants.PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);
        ButterKnife.bind(this, view);
        mContext = getActivity();
        setUi();
        return view;
    }

    public void setUi() {
        mDictionaryDetail.setMovementMethod(new ScrollingMovementMethod()); // 设置TextView可滚动
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
                    Toast.makeText(mContext, R.string.bad_internet, Toast.LENGTH_SHORT).show();
                    return;
                }
                String text = mWord.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(mContext, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPageNumber == 0) {
                    fetchXinhuaDictionary(text);
                } else {
                    fetchIdiomsDictionary(text);
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

    private void fetchXinhuaDictionary(String text) {
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>("http://v.juhe.cn/xhzd/query?key=63ca50d904e451ad97e42204eb84247d&word=" +
                StringUtils.encodeText(text), XinhuaDictionary.class, null, new Response.Listener<XinhuaDictionary>() {
            @Override
            public void onResponse(XinhuaDictionary response) {
                Message msg = mHandler.obtainMessage();
                msg.what = mPageNumber;
                msg.obj = response;
                msg.sendToTarget();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, R.string.error_translation, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void fetchIdiomsDictionary(String text) {
        VolleySingleton.getInstance().addToRequestQueue(new GsonRequest<>("http://v.juhe.cn/chengyu/query?key=49c4ec6b09923b57b6aa6ef64d670149&word=" +
                StringUtils.encodeText(text), IdiomsDictionary.class, null, new Response.Listener<IdiomsDictionary>() {
            @Override
            public void onResponse(IdiomsDictionary response) {
                Message msg = mHandler.obtainMessage();
                msg.what = mPageNumber;
                msg.obj = response;
                msg.sendToTarget();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext, "查询失败，请检查网络设置或稍后重试。", Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
