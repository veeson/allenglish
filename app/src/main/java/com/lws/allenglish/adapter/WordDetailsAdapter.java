package com.lws.allenglish.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lws.allenglish.R;
import com.lws.allenglish.bean.DetailedWord;
import com.lws.allenglish.connector.OnFinishPlayAudioListener;
import com.lws.allenglish.utils.PlayAudio;
import com.lws.allenglish.view.CommonExplains;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wilson on 2016/12/10.
 */

public class WordDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnFinishPlayAudioListener {
    private final static int TYPE_ONE = 0;
    private final static int TYPE_TWE = 1;
    private final static int TYPE_THESE = 2;
    private final static int TYPE_FOUR = 3;
    private final static int ITEM_COUNT_1 = 1;
    private final static int ITEM_COUNT_4 = 4;

    private PlayAudio mPlayAudio;
    private DetailedWord mDetailedWord;
    private Context mContext;
    private List<AnimationDrawable> mSentenceHornAnimations = new ArrayList<>();

    public WordDetailsAdapter(DetailedWord mDetailedWord, Context mContext, PlayAudio mPlayAudio, List<AnimationDrawable> mSentenceHornAnimations) {
        this.mDetailedWord = mDetailedWord;
        this.mContext = mContext;
        this.mSentenceHornAnimations.addAll(mSentenceHornAnimations);
        this.mPlayAudio = mPlayAudio;
        this.mPlayAudio.setOnFinishPlayAudioListener(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONE:
                return new BaseInfoViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
            case TYPE_TWE:
                return new ExampleSentenceViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
            case TYPE_THESE:
                return new PhraseViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
            case TYPE_FOUR:
                return new EEMeanViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sub_explain, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseInfoViewHolder) {
            ((BaseInfoViewHolder) holder).loadData();
        } else if (holder instanceof ExampleSentenceViewHolder) {
            ((ExampleSentenceViewHolder) holder).loadData();
        } else if (holder instanceof PhraseViewHolder) {
            ((PhraseViewHolder) holder).loadData();
        } else if (holder instanceof EEMeanViewHolder) {
            ((EEMeanViewHolder) holder).loadData();
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case TYPE_ONE:
                return TYPE_ONE;
            case TYPE_TWE:
                if (mDetailedWord.sentence != null) {
                    return TYPE_TWE;
                }
            case TYPE_THESE:
                if (mDetailedWord.phrase != null) {
                    return TYPE_THESE;
                }
            case TYPE_FOUR:
                if (mDetailedWord.ee_mean != null) {
                    return TYPE_FOUR;
                }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (mDetailedWord.baesInfo == null) {
            return ITEM_COUNT_1;
        }
        int itemCount = ITEM_COUNT_4;
        if (mDetailedWord.sentence == null) {
            --itemCount;
        }
        if (mDetailedWord.phrase == null) {
            --itemCount;
        }
        if (mDetailedWord.ee_mean == null) {
            --itemCount;
        }
        return itemCount;
    }

    /**
     * 单词基本释义ViewHolder
     */
    public class BaseInfoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sub_explain)
        CommonExplains commonExplains;

        public BaseInfoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            commonExplains.setExplainHeadText("基本释义");
            commonExplains.setSentenceRecyclerViewVisibility(View.GONE);
        }

        public void loadData() {
            commonExplains.setSymbolsText(mDetailedWord.baseWord.means);
            if (mDetailedWord.baesInfo != null) {
                String word_er;
                String word_est;
                String word_pl;
                StringBuilder sb = new StringBuilder();
                try {
                    word_er = mDetailedWord.baesInfo.exchange.word_er.get(0);
                    sb.append("比较级: ").append(word_er);
                } catch (Exception ignored) {
                }
                try {
                    word_est = mDetailedWord.baesInfo.exchange.word_est.get(0);
                    sb.append("最高级: ").append(word_est);
                } catch (Exception ignored) {
                }
                try {
                    word_pl = mDetailedWord.baesInfo.exchange.word_pl.get(0);
                    sb.append("复数: ").append(word_pl);
                } catch (Exception ignored) {
                }
                if (sb.length() > 0) {
                    commonExplains.setExchangeText(sb);
                } else {
                    commonExplains.setExchangeVisibility(View.GONE);
                }
            } else {
                commonExplains.setExchangeVisibility(View.GONE);
            }
        }
    }

    /**
     * 例句ViewHolder
     */
    public class ExampleSentenceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sub_explain)
        CommonExplains commonExplains;

        private RecyclerView sentenceRecyclerView;

        public ExampleSentenceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sentenceRecyclerView = commonExplains.getSentenceRecyclerView();
            commonExplains.setExplainHeadText("双语例句");
            commonExplains.setBaseInfoLinearLayoutVisibility(View.GONE);
        }

        public void loadData() {
            sentenceRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            sentenceRecyclerView.setLayoutManager(mLayoutManager);
            sentenceRecyclerView.setAdapter(new ExampleSentenceContentAdapter());
        }

        public class ExampleSentenceContentAdapter extends RecyclerView.Adapter<ExampleSentenceContentAdapter.ViewHolder> {

            @Override
            public ExampleSentenceContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
                return new ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(ExampleSentenceContentAdapter.ViewHolder holder, int position) {
                final int pos = position;
                StringBuilder sb = new StringBuilder();
                sb.append(mDetailedWord.sentence.get(pos).Network_en).append("\n").append(mDetailedWord.sentence.get(pos).Network_cn);
                if (pos != getItemCount() - 1) {
                    sb.append("\n");
                }
                holder.sentenceContent.setText(sb);
                ImageView horn = holder.horn;
                horn.setBackgroundResource(R.drawable.animation_horn_color);
                mSentenceHornAnimations.add((AnimationDrawable) horn.getBackground());
                horn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mSentenceHornAnimations.get(pos + 2).isRunning()) {
                            return;
                        }
                        stopAnimation();
                        mSentenceHornAnimations.get(pos + 2).start();
                        mPlayAudio.play(mContext, mDetailedWord.sentence.get(pos).tts_mp3, 3);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return mDetailedWord.sentence.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.sentence_content)
                TextView sentenceContent;
                @BindView(R.id.horn)
                ImageView horn;

                public ViewHolder(View itemView) {
                    super(itemView);
                    ButterKnife.bind(this, itemView);
                }
            }
        }
    }

    @Override
    public void OnFinishPlayAudio() {
        stopAnimation();
    }

    public void stopAnimation(){
        for (AnimationDrawable animation :
                mSentenceHornAnimations) {
            if (animation.isRunning()) {
                animation.selectDrawable(0); // 选择当前动画的第一帧，然后停止
                animation.stop();
            }
        }
    }

    /**
     * 词组ViewHolder
     */
    public class PhraseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sub_explain)
        CommonExplains commonExplains;

        private RecyclerView sentenceRecyclerView;

        public PhraseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sentenceRecyclerView = commonExplains.getSentenceRecyclerView();
            commonExplains.setExplainHeadText("词组搭配");
            commonExplains.setBaseInfoLinearLayoutVisibility(View.GONE);
        }

        public void loadData() {
            sentenceRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            sentenceRecyclerView.setLayoutManager(mLayoutManager);
            sentenceRecyclerView.setAdapter(new PhraseViewHolder.PhraseContentAdapter());
        }

        class PhraseContentAdapter extends RecyclerView.Adapter<PhraseContentAdapter.ViewHolder> {
            @Override
            public PhraseContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
                return new PhraseViewHolder.PhraseContentAdapter.ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(PhraseContentAdapter.ViewHolder holder, int position) {
                StringBuilder sb = new StringBuilder();
                try {
                    sb.append(mDetailedWord.phrase.get(position).cizu_name)
                            .append("\n")
                            .append(mDetailedWord.phrase.get(position).jx.get(0).jx_cn_mean)
                            .append("(")
                            .append(mDetailedWord.phrase.get(position).jx.get(0).jx_en_mean)
                            .append(")");
                } catch (Exception ignored) {
                }
                if (position != getItemCount() - 1) {
                    sb.append("\n");
                }
                holder.sentenceContent.setText(sb);
            }

            @Override
            public int getItemCount() {
                return mDetailedWord.phrase.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.sentence_content)
                TextView sentenceContent;
                @BindView(R.id.horn)
                ImageView horn;

                public ViewHolder(View itemView) {
                    super(itemView);
                    ButterKnife.bind(this, itemView);
                    horn.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 英英释义ViewHolder
     */
    public class EEMeanViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sub_explain)
        CommonExplains commonExplains;

        private RecyclerView sentenceRecyclerView;

        public EEMeanViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            sentenceRecyclerView = commonExplains.getSentenceRecyclerView();
            commonExplains.setExplainHeadText("英英释义");
            commonExplains.setBaseInfoLinearLayoutVisibility(View.GONE);
        }

        public void loadData() {
            sentenceRecyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            sentenceRecyclerView.setLayoutManager(mLayoutManager);
            sentenceRecyclerView.setAdapter(new EEMeanContentAdapter());
        }

        class EEMeanContentAdapter extends RecyclerView.Adapter<EEMeanContentAdapter.ViewHolder> {
            @Override
            public EEMeanContentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sentence, parent, false);
                return new EEMeanContentAdapter.ViewHolder(v);
            }

            @Override
            public void onBindViewHolder(EEMeanContentAdapter.ViewHolder holder, int position) {
                StringBuilder sb = new StringBuilder();
                try {
                    sb.append(mDetailedWord.ee_mean.get(position).part_name).append("\n");
                    for (DetailedWord.EeMeanEntity.MeansEntityXX xx : mDetailedWord.ee_mean.get(position).means) {
                        sb.append(xx.word_mean).append("\n");
                        for (DetailedWord.EeMeanEntity.MeansEntityXX.SentencesEntity sentences : xx.sentences) {
                            sb.append(sentences.sentence).append("\n");
                        }
                    }
                    sb.setLength(sb.length() - 1);
                } catch (Exception ignored) {
                }
                if (position != getItemCount() - 1) {
                    sb.append("\n");
                }
                holder.sentenceContent.setText(sb);
            }

            @Override
            public int getItemCount() {
                return mDetailedWord.ee_mean.size();
            }

            class ViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.sentence_content)
                TextView sentenceContent;
                @BindView(R.id.horn)
                ImageView horn;

                public ViewHolder(View itemView) {
                    super(itemView);
                    ButterKnife.bind(this, itemView);
                    horn.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

}
