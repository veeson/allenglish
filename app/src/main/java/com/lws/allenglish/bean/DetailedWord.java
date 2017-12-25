package com.lws.allenglish.bean;

import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.NativeADDataRef;

import java.util.List;

public class DetailedWord {
//    public int errno;
//    public String errmsg;
    public BaesInfoEntity baesInfo;
//    public int _word_flag;
    public List<SentenceEntity> sentence;
    public List<PhraseEntity> phrase;
    public List<EeMeanEntity> ee_mean;
//    public List<String> exchanges;

    public BaseWord baseWord;

    public NativeADDataRef nativeADDataRef;
    public IFLYNativeAd iflyNativeAd;

    public static class BaesInfoEntity {
        public String word_name;
//        public String is_CRI;
        public ExchangeEntity exchange;
//        public int translate_type;
        public List<SymbolsEntity> symbols;

        public static class ExchangeEntity {
            public List<String> word_pl;
//            public List<?> word_third;
//            public List<?> word_past;
//            public List<?> word_done;
//            public List<?> word_ing;
            public List<String> word_er;
            public List<String> word_est;
//            public List<?> word_prep;
//            public List<?> word_adv;
//            public List<?> word_verb;
//            public List<?> word_noun;
//            public List<?> word_adj;
//            public List<?> word_conn;
        }

        public static class SymbolsEntity {
            public String ph_en;
            public String ph_am;
//            public String ph_other;
//            public String ph_en_mp3;
//            public String ph_am_mp3;
//            public String ph_tts_mp3;
            public List<PartsEntity> parts;

            public static class PartsEntity {
                public String part;
                public List<String> means;
            }
        }
    }

    public static class SentenceEntity {
//        public String Network_id;
        public String Network_en;
        public String Network_cn;
        public String tts_mp3;
//        public String tts_size;
//        public int source_type;
//        public int source_id;
//        public String source_title;
    }

    public static class PhraseEntity {

        public String cizu_name;
        public List<JxEntity> jx;

        public static class JxEntity {
            public String jx_en_mean;
            public String jx_cn_mean;
//            public List<?> lj;
        }
    }

    public static class EeMeanEntity {
        public String part_name;
        public List<MeansEntity> means;

        public static class MeansEntity {

            public String word_mean;
            public List<SentencesEntity> sentences;

            public static class SentencesEntity {
                public String sentence;
            }
        }
    }
}
