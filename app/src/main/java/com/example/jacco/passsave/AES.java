package com.example.jacco.passsave;

/**
 * Created by Jacco on 6-6-2018.
 */

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 Aes encryption
 */

public class AES extends Base64 {

    private static SecretKeySpec secretKey ;
    private static byte[] key ;

    private static String decryptedString;
    private static String encryptedString;

    public static void setKey(String myKey){

        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            System.out.println(key.length);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            System.out.println(key.length);
            System.out.println(new String(key,"UTF-8"));
            secretKey = new SecretKeySpec(key, "AES");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static String getDecryptedString() {
        return decryptedString;
    }

    public static void setDecryptedString(String decryptedString) {
        AES.decryptedString = decryptedString;
    }

    public static String getEncryptedString() {
        return encryptedString;
    }

    public static void setEncryptedString(String encryptedString) {
        AES.encryptedString = encryptedString;
    }

    public static String encrypt(String strToEncrypt, String key)
    {
        setKey(key);

        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            setEncryptedString(encode(cipher.doFinal(strToEncrypt.getBytes("UTF-8"))));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: "+e.toString());
        }
        return getEncryptedString();

    }

    public static String decrypt(String strToDecrypt, String key)
    {
        setKey(key);

        try
        {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");

            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            setDecryptedString(new String(cipher.doFinal(decode(strToDecrypt))));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: "+e.toString());
        }
        return getDecryptedString();
    }


    public static void main(String args[])
    {
        final String strToEncrypt = "My text to encrypt";
        final String strPssword = "encryptor key";
        AES.setKey(strPssword);

        AES.encrypt(strToEncrypt.trim(), "hoi");

        System.out.println("String to Encrypt: " + strToEncrypt);
        System.out.println("Encrypted: " + AES.getEncryptedString());

        final String strToDecrypt =  AES.getEncryptedString();
        AES.decrypt(strToDecrypt.trim(), "hoi");

        System.out.println("String To Decrypt : " + strToDecrypt);
        System.out.println("Decrypted : " + AES.getDecryptedString());
    }

}
