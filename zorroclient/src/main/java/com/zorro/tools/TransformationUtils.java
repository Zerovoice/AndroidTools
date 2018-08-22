package com.zorro.tools;

import com.luhuiguo.chinese.ChineseUtils;

import java.util.Locale;
import java.util.logging.Logger;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */
public class TransformationUtils {

    public static int lang;
    static {
        switch (Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry()){
            case Language.CHS_STR:
                TransformationUtils.lang = TransformationUtils.Language.CHS;
                break;
            case Language.CHT_STR:
                TransformationUtils.lang = TransformationUtils.Language.CHT;
                break;
            case Language.ENG_STR:
                TransformationUtils.lang = TransformationUtils.Language.ENG;
                break;
            default:
                TransformationUtils.lang = TransformationUtils.Language.CHS;
        }
    }

    public static final class Language{
        public static final int CHS = 1;
        public static final int CHT = 2;
        public static final int ENG = 3;
        public static final String CHS_STR = "zh_CN";
        public static final String CHT_STR = "zh_TW";
        public static final String ENG_STR = "en_US";
        public static final String CHT_URL_APPEND = "_cht";
    }

    public static String toTraditional(String str){
        return ChineseUtils.toTraditional(str);
    }

    public static String toSimplified(String str){
        return ChineseUtils.toSimplified(str);
    }

    public static String getString(String string){
        switch (lang){
            case Language.CHS:
                //返回结果都为简体中文，暂时不做处理
                break;
            case Language.CHT:
                string = toTraditional(string);
                break;
            case Language.ENG:
                //暂不处理
                break;
            default:;
        }
        return string;
    }
}
