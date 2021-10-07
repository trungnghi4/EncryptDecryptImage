package mycode.dev.encryptdecryptimage

import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object MyEncrypter {

    private const val DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024
    private const val ALGO_IMAGE_ENCRYPTOR = "AES/CBC/PKCS5Padding"
    private const val ALGO_SECRET_KEY = "AES"

    fun encryptToFile(keyStr: ByteArray, spec: ByteArray, input: InputStream, output: OutputStream) {
        var output = output
        try {
//            val iv = IvParameterSpec(spec.toByteArray(charset("UTF-8")))
//            val keySpec = SecretKeySpec(keyStr.toByteArray(charset("UTF-8")), ALGO_SECRET_KEY)

            val iv = IvParameterSpec(spec)
            val keySpec = SecretKeySpec(keyStr, ALGO_SECRET_KEY)

            val c = Cipher.getInstance(ALGO_IMAGE_ENCRYPTOR)
            c.init(Cipher.ENCRYPT_MODE, keySpec, iv)
            output = CipherOutputStream(output, c)

            val buffer = ByteArray(DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE)

            var bytesRead: Int = 0
            while (input.read(buffer).also { bytesRead = it } > 0)
                output.write(buffer, 0, bytesRead)


        } finally {
            output.close()
        }
    }

    fun decryptToFile(keyStr: ByteArray, spec: ByteArray, input: InputStream, output: OutputStream) {
        var output = output
        try {
//            val iv = IvParameterSpec(spec.toByteArray(charset("UTF-8")))
//            val keySpec = SecretKeySpec(keyStr.toByteArray(charset("UTF-8")), ALGO_SECRET_KEY)
            val iv = IvParameterSpec(spec)
            val keySpec = SecretKeySpec(keyStr, ALGO_SECRET_KEY)

            val c = Cipher.getInstance(ALGO_IMAGE_ENCRYPTOR)
            c.init(Cipher.DECRYPT_MODE, keySpec, iv)
            output = CipherOutputStream(output, c)

            val buffer = ByteArray(DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE)

            var bytesRead: Int = 0
            while (input.read(buffer).also { bytesRead = it } > 0)
                output.write(buffer, 0, bytesRead)


        } finally {
            output.close()
        }
    }

}