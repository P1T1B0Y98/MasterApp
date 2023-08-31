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
    private const val ASYMMETRIC_ALGORITHM = "RSA"
    private const val ASYMMETRIC_KEY_SIZE = 2048
    private const val KEY_ALIAS = "wilshere"

    fun generateSecretKey(alias: String): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setRandomizedEncryptionRequired(false)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    fun generateKeyPair() {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            ASYMMETRIC_ALGORITHM,
            "AndroidKeyStore"
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(ASYMMETRIC_KEY_SIZE)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()

        keyPairGenerator.initialize(keyGenParameterSpec)
        keyPairGenerator.generateKeyPair()
    }

    fun getSecretKey(alias: String): SecretKey? {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(alias, null) as? SecretKey
    }
    fun encrypt(data: String, alias: String): String {
        val iv = generateRandomIV()
        val secretKey = getSecretKey(alias)
            ?: throw IllegalStateException("Secret key not available")

        val cipher = Cipher.getInstance(MODE)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encryptedData = cipher.doFinal(data.toByteArray())
        val combinedArray = ByteArray(iv.size + encryptedData.size)

        System.arraycopy(iv, 0, combinedArray, 0, iv.size)
        System.arraycopy(encryptedData, 0, combinedArray, iv.size, encryptedData.size)

        return Base64.encodeToString(combinedArray, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String, secretKey: String): String {
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)

        // Extracting the IV and encrypted bytes from the combined array
        val iv = decodedData.sliceArray(0 until 16)
        val encryptedBytes = decodedData.sliceArray(16 until decodedData.size)

        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), ALGORITHM)
        val cipher = Cipher.getInstance(MODE)
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(iv)) // Use the extracted IV here

        return String(cipher.doFinal(encryptedBytes))
    }


    private fun generateRandomIV(): ByteArray {
        val random = SecureRandom()
        val iv = ByteArray(16)
        random.nextBytes(iv)
        return iv
    }
}
