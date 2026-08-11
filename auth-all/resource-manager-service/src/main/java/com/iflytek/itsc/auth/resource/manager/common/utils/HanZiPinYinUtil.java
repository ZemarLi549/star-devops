package com.iflytek.itsc.auth.resource.manager.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/15
 * @desc 汉字拼音
 **/
public class HanZiPinYinUtil {

    /**
     * 初始化汉字拼音字母对照表
     */
    private static void init(List<ChinesePinyinComparisonMap> chinesePinyinComparisonMapList) {
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-20319, -20284, 'A'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-20283, -19776, 'B'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-19775, -19219, 'C'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-19218, -18711, 'D'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-18710, -18527, 'E'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-18526, -18240, 'F'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-18239, -17923, 'G'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-17922, -17418, 'H'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-17417, -16475, 'J'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-16474, -16213, 'K'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-16212, -15641, 'L'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-15640, -15166, 'M'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-15165, -14923, 'N'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-14922, -14915, 'O'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-14914, -14631, 'P'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-14630, -14150, 'Q'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-14149, -14091, 'R'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-14090, -13319, 'S'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-13318, -12839, 'T'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-12838, -12557, 'W'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-12556, -11848, 'X'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-11847, -11056, 'Y'));
        chinesePinyinComparisonMapList.add(new ChinesePinyinComparisonMap(-11055, -10247, 'Z'));
    }

    /**
     * 遍历获取首字母
     */
    private static char getPY(char c) throws Exception {
        // 汉字拼音字母对照表
        List<ChinesePinyinComparisonMap> chinesePinyinComparisonMapList = new ArrayList<>();
        init(chinesePinyinComparisonMapList);

        byte[] bytes = String.valueOf(c).getBytes("GBK");

        //双字节汉字处理
        if (bytes.length == 2) {

            int hightByte = 256 + bytes[0];
            int lowByte = 256 + bytes[1];
            int asc = (256 * hightByte + lowByte) - 256 * 256;

            // 遍历转换
            for (ChinesePinyinComparisonMap map : chinesePinyinComparisonMapList) {
                if (asc >= map.getSAscll() && asc <= map.getEAscll()) {
                    return map.getCode();
                }
            }
        }

        // 单字节或其他直接输入，不执行编码
        return c;
    }

    /**
     * 获取汉字拼音
     */
    public static String getHanZiPY(String str) {
        try {
            StringBuilder pyStrBd = new StringBuilder();

            for (char c : str.toCharArray()) {
                pyStrBd.append(getPY(c));
            }

            return pyStrBd.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 汉字拼音字母对照类
     */
    @Data
    @AllArgsConstructor
    private static class ChinesePinyinComparisonMap {
        // 区间开头
        private int sAscll;

        // 区间结尾
        private int eAscll;

        // 对应字母
        private char code;
    }
}
