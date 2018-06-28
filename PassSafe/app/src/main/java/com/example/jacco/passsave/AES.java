package com.example.jacco.passsave;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 Aes encryption inspired by https://aesencryption.net/.
 */

public class AES {

    private static SecretKeySpec secretKey ;

    // Set key for encryption
    public static void setKey(String myKey){

        MessageDigest sha = null;
        try {
            byte[] key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKey = new SecretKeySpec(key, "AES");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static String encrypt(String strToEncrypt, String key)
    {
        setKey(key);

        String encryptedString;

        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            encryptedString = Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), Base64.DEFAULT);

            return encryptedString;
        }
        catch (Exception e)
        {
            // If encrypting doesn't work, return an empty string
            System.out.println("Error while encrypting: "+e.toString());
            return "";
        }
    }

    public static String decrypt(String strToDecrypt, String key)
    {
        setKey(key);

        String decryptedString;

        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            decryptedString = new String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)));
            return decryptedString;
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: "+e.toString());
            return "";
        }
    }

}
