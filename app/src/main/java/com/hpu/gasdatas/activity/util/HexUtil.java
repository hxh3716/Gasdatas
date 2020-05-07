package com.hpu.gasdatas.activity.util;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;

/**
 * @author Created by hexh on 2020-05-06 15:35.
 * @description
 */
public class HexUtil {
    private static String hexString = "0123456789ABCDEF";

    public HexUtil() {
    }

    public static String stringToHexString(String strPart) {
        String hexString = "";

        for(int i = 0; i < strPart.length(); ++i) {
            int ch = strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString = hexString + strHex;
        }

        return hexString;
    }

    public static String encode(String str) {
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte aByte = var3[var5];
            sb.append(hexString.charAt((aByte & 240) >> 4));
            sb.append(hexString.charAt(aByte & 15));
        }

        return sb.toString();
    }

    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);

        for(int i = 0; i < bytes.length(); i += 2) {
            baos.write(hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1)));
        }

        return new String(baos.toByteArray());
    }

    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}));
        _b0 = (byte)(_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}));
        return (byte)(_b0 | _b1);
    }

    public static byte[] HexString2Bytes(String src) {
        byte[] ret = new byte[6];
        byte[] tmp = src.getBytes();

        for(int i = 0; i < 6; ++i) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }

        return ret;
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src != null && src.length > 0) {
            byte[] var2 = src;
            int var3 = src.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte b = var2[var4];
                int v = b & 255;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }

                stringBuilder.append(hv);
            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString != null && !hexString.equals("")) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for(int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    private static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

    public static boolean checkHexString(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        } else {
            int len = value.length();

            for(int i = 0; i < len; ++i) {
                if (!checkHexChar(value.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean checkHexChar(char ch) {
        return ch >= '0' && ch <= '9' || ch >= 'A' && ch <= 'F' || ch >= 'a' && ch <= 'f';
    }
}

