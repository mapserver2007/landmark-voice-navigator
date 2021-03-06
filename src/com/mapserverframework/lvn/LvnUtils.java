package com.mapserverframework.lvn;


/**
 * ユーティリティクラス
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class LvnUtils {
    /** 半角数字-漢数字変換リスト */
    private static final String[] nList = {
        "一", "二", "三", "四", "五", "六", "七", "八", "九"
    };
    
    /**
     * 丁目以降を削除する
     * @param text 住所
     * @return 丁目以降を削除した住所
     */
    public static String deleteStreet(String text) {
        // 0-9の置換を実行
        StringBuilder sb = new StringBuilder();
        String chunk = null;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '０' && c <= '９') {
                break;
            }
            else {
                chunk = String.valueOf(c);
            }
            sb.append(chunk);
        }
        
        return sb.toString();
    }
    
    /**
     * テキストに含まれる全角数字を漢数字に置き換える
     * @param text 文字列
     * @return 置換後文字列
     */
    public static String toKanji(String text) {
        // 「10」→「十」の置換のみ直接行う
        text = text.replace("１０", "十");
        // 0-9の置換を実行
        StringBuilder sb = new StringBuilder();
        String chunk = null;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '０' && c <= '９') {
                chunk = nList[c - '０'];
            }
            else {
                chunk = String.valueOf(c);
            }
            sb.append(chunk);
        }
        
        return sb.toString();
    }
}
