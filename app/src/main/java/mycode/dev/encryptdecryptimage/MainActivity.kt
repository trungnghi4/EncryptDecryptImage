package mycode.dev.encryptdecryptimage

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKeyFactory


class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val FILE_NAME_ENCRYPT = "image_test_encrypt"
        private const val FILE_NAME_DECRYPT = "image_test_decrypt.png"
        /* You can set the default value like this or generate like below */
//        private const val key = "PDY8o0tPHNYz1FG7" // 16 char = 128 bit
//        private const val specString = "yoe6Nd84MOZCzbb0" // 16 char = 128 bit
    }

    lateinit var myDir: File
    private lateinit var key: ByteArray
    private lateinit var specString: ByteArray

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val image = result.data?.extras!!["data"] as Bitmap?
                val stream = ByteArrayOutputStream()
                image?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val input = ByteArrayInputStream(stream.toByteArray())

                val outputFileEncrypt = File(myDir, FILE_NAME_ENCRYPT)
                try {
                    MyEncrypter.encryptToFile(
                        key,
                        specString,
                        input,
                        FileOutputStream(outputFileEncrypt)
                    )
                    Toast.makeText(this@MainActivity, "Encrypted", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Generate two key
        val generator: KeyGenerator = KeyGenerator.getInstance("AES")
        generator.init(128)
        key = generator.generateKey().encoded
        specString = generator.generateKey().encoded

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "You should accept permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    btnDecrypt.isEnabled = true
                    btnEncrypt.isEnabled = true
                    btnCamera.isEnabled = true
                    btnCameraDecrypt.isEnabled = true
                }
            })
            .check()

        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .apply { mkdirs() }

        myDir = File("$root")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        btnEncrypt.setOnClickListener(this)
        btnDecrypt.setOnClickListener(this)
        btnCamera.setOnClickListener(this)
        btnCameraDecrypt.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnCameraDecrypt, R.id.btnDecrypt -> {
                val outputFileDecrypt = File(myDir, FILE_NAME_DECRYPT)
                val encryptFile = File(myDir, FILE_NAME_ENCRYPT)

                try {
                    MyEncrypter.decryptToFile(
                        key,
                        specString,
                        FileInputStream(encryptFile),
                        FileOutputStream(outputFileDecrypt)
                    )

                    imgDecrypt.setImageURI(Uri.fromFile(outputFileDecrypt))
                    outputFileDecrypt.delete()
                    Toast.makeText(this@MainActivity, "Decrypted", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
            R.id.btnCamera -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                resultLauncher.launch(cameraIntent)
            }
            R.id.btnEncrypt -> {
                val drawble = ContextCompat.getDrawable(this@MainActivity, R.drawable.image_test)
                val bitmapDrawble = drawble as BitmapDrawable
                val bitmap = bitmapDrawble.bitmap
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val input = ByteArrayInputStream(stream.toByteArray())

                val outputFileEncrypt = File(myDir, FILE_NAME_ENCRYPT)

                try {
                    MyEncrypter.encryptToFile(
                        key,
                        specString,
                        input,
                        FileOutputStream(outputFileEncrypt)
                    )
                    Toast.makeText(this@MainActivity, "Encrypted", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.toString(), Toast.LENGTH_SHORT).show()

                    e.printStackTrace()
                }
            }
        }
    }
}