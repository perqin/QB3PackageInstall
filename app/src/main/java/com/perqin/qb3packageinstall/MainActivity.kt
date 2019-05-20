package com.perqin.qb3packageinstall

import android.content.Intent
import android.net.Uri
import android.os.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val handler = Handler(Looper.getMainLooper(), Handler.Callback { message ->
        when (message.what) {
            MSG_START -> {
//                Toast.makeText(this@MainActivity, "Installing...", Toast.LENGTH_SHORT).show()
                true
            }
            MSG_FINISH -> {
//                Toast.makeText(this@MainActivity, "Finish installation", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(FileProvider.getUriForFile(this@MainActivity, "$packageName.fileprovider", message.obj as File), "application/vnd.android.package-archive")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                })
                finish()
                true
            }
            MSG_ERROR -> {
                Toast.makeText(this@MainActivity, "Fail to retrieve apk file: ${(message.obj as Exception).message}", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            else -> false
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setFinishOnTouchOutside(false)

        (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
            val input = contentResolver.openInputStream(it)
            val outputFile = File(cacheDir, "temp-${System.currentTimeMillis()}.apk")
            val output = outputFile.outputStream()
            Thread {
                handler.obtainMessage(MSG_START).sendToTarget()
                try {
                    input?.copyTo(output)
                    handler.obtainMessage(MSG_FINISH, outputFile).sendToTarget()
                } catch (e: Exception) {
                    e.printStackTrace()
                    handler.obtainMessage(MSG_ERROR, e).sendToTarget()
                }
            }.start()
        }?: finish()
    }

    companion object {
        private const val MSG_START = 1
        private const val MSG_FINISH = 2
        private const val MSG_ERROR = 3
    }
}
