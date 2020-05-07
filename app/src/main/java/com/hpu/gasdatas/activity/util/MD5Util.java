package com.hpu.gasdatas.activity.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Created by hexh on 2020-05-06 15:34.
 * @description
 */
public class MD5Util {
    private static final int SIXTEEN_K = 16384;

    public MD5Util() {
    }

    public static byte[] computeMD5Hash(InputStream is) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];

            int bytesRead;
            while((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }

            byte[] var5 = messageDigest.digest();
            return var5;
        } catch (NoSuchAlgorithmException var14) {
            throw new IllegalStateException(var14);
        } finally {
            try {
                bis.close();
            } catch (Exception var13) {
                var13.printStackTrace();
            }

        }
    }

    public static String md5AsBase64(InputStream is) throws IOException {
        return HexUtil.bytesToHexString(computeMD5Hash(is));
    }

    public static byte[] computeMD5Hash(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(input);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalStateException(var2);
        }
    }

    public static String md5AsBase64(byte[] input) {
        return HexUtil.bytesToHexString(computeMD5Hash(input));
    }

    public static byte[] computeMD5Hash(File file) throws IOException {
        return computeMD5Hash((InputStream)(new FileInputStream(file)));
    }

    public static String md5AsBase64(File file) throws IOException {
        return HexUtil.bytesToHexString(computeMD5Hash(file));
    }

    public static String md5AsBase64(String str) {
        return md5AsBase64(str.getBytes());
    }

    public static String md5AsBase64For16(String str) {
        return md5AsBase64(str).substring(8, 24);
    }

    public static String getHmacMd5Str(String s, String keyString) {
        String sEncodedString = null;

        try {
            SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), "HmacMD5");
            Mac mac = Mac.getInstance("HmacMD5");
            mac.init(key);
            byte[] bytes = mac.doFinal(s.getBytes("ASCII"));
            StringBuilder hash = new StringBuilder();
            byte[] var7 = bytes;
            int var8 = bytes.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                byte aByte = var7[var9];
                String hex = Integer.toHexString(255 & aByte);
                if (hex.length() == 1) {
                    hash.append('0');
                }

                hash.append(hex);
            }

            sEncodedString = hash.toString();
        } catch (UnsupportedEncodingException var12) {
        } catch (InvalidKeyException var13) {
        } catch (NoSuchAlgorithmException var14) {
        }

        return sEncodedString;
    }
}
