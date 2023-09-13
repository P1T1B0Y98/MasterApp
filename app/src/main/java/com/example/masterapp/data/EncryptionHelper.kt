package com.example.masterapp.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object EncryptionHelper {

    private const val ALGORITHM = "AES"
    private const val MODE = "AES/CBC/PKCS7Padding"
    private const val SECRET_KEY = "wilshere12345678"

    private fun getSecretKeySpec(): SecretKeySpec {
        val keyBytes = SECRET_KEY.toByteArray(Charsets.UTF_8)
        return SecretKeySpec(keyBytes, ALGORITHM)
    }
    fun encrypt(data: String): String {
        val iv = generateRandomIV()

        val cipher = Cipher.getInstance(MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(), IvParameterSpec(iv))

        val encryptedData = cipher.doFinal(data.toByteArray())
        val combinedArray = ByteArray(iv.size + encryptedData.size)

        System.arraycopy(iv, 0, combinedArray, 0, iv.size)
        System.arraycopy(encryptedData, 0, combinedArray, iv.size, encryptedData.size)

        return Base64.encodeToString(combinedArray, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)

        // Extracting the IV and encrypted bytes from the combined array
        val iv = decodedData.sliceArray(0 until 16)
        val encryptedBytes = decodedData.sliceArray(16 until decodedData.size)

        val cipher = Cipher.getInstance(MODE)
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(), IvParameterSpec(iv))

        return String(cipher.doFinal(encryptedBytes))
    }

    private fun generateRandomIV(): ByteArray {
        val random = SecureRandom()
        val iv = ByteArray(16)
        random.nextBytes(iv)
        return iv
    }
}
