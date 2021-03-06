package com.sweetcam.app.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import com.lcw.library.imagepicker.ImagePicker
import com.lcw.library.imagepicker.provider.ImagePickerProvider
import com.pipipi.camhd.R
import com.sdsmdg.tastytoast.TastyToast
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.GlideLoader
import com.sweetcam.app.utils.MessageEvent
import com.sweetcam.app.utils.requestPermission
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

class MainActivity : BaseActivity(R.layout.activity_main_new) {

    private var mFilePath: String = ""

    private var isPer = false

    override fun onConvert() {
        registerEventBus()
        requestPermission()
    }

    private fun openGallery(targetAc: Int) {
        ImagePicker.getInstance()
            .setTitle("select")
            .showCamera(false)
            .showVideo(false)
            .showImage(true)
            .setSingleType(true)
            .setImageLoader(GlideLoader())
            .start(this, targetAc)
    }


    fun optionClick(view: View) {
        if (!isPer) return
        val desc = view.contentDescription.toString().toInt()
        if (desc == 5) {
            try {
                val fileDir = File(Environment.getExternalStorageDirectory(), "Pictures")
                if (!fileDir.exists()) {
                    fileDir.mkdir()
                }
                mFilePath = fileDir.absolutePath + "/IMG_" + System.currentTimeMillis() + ".jpg"

                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val uri: Uri = if (Build.VERSION.SDK_INT >= 24) {
                    FileProvider.getUriForFile(
                        this,
                        ImagePickerProvider.getFileProviderName(this),
                        File(mFilePath)
                    )
                } else {
                    Uri.fromFile(File(mFilePath))
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                startActivityForResult(intent, 100)
            } catch (e: Exception) {
                TastyToast.makeText(this, "camera error", TastyToast.LENGTH_SHORT, TastyToast.ERROR)
            }
        } else {
            openGallery(desc)
        }

    }

    private fun startNextActivity(clazz: Class<*>, url: String) {
        val i = Intent(this, clazz)
        i.putExtra("url", url)
        startActivity(i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                try {
                    sendBroadcast(
                        Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://$mFilePath")
                        )
                    )
                } catch (e: Exception) {
                    TastyToast.makeText(
                        this,
                        "scan imgs error",
                        TastyToast.LENGTH_SHORT,
                        TastyToast.ERROR
                    )
                }
            } else {
                data?.let {
                    val url: String =
                        (it.getStringArrayListExtra(ImagePicker.EXTRA_SELECT_IMAGES) as ArrayList<String>)[0]
                    if (TextUtils.isEmpty(url))
                        return@let
                    when (requestCode) {
                        1 -> {
                            startNextActivity(StickerActivity::class.java, url)
                        }
                        2 -> {
                            startNextActivity(SlimmingActivity::class.java, url)
                        }
                        3 -> {
                            startNextActivity(CartoonActivity::class.java, url)
                        }
                        4 -> {
                            startNextActivity(AgeActivity::class.java, url)
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        TastyToast.makeText(
            this,
            "${msg[0]}",
            TastyToast.LENGTH_SHORT,
            TastyToast.ERROR
        )
        when (msg[0]) {
            "onGranted" -> {
                isPer = true
            }
            "not all" -> {
                TastyToast.makeText(
                    this,
                    "some permissions were not granted normally",
                    TastyToast.LENGTH_SHORT,
                    TastyToast.ERROR
                )
                finish()
            }
            "onDenied" -> {
                TastyToast.makeText(
                    this,
                    "no permissions",
                    TastyToast.LENGTH_SHORT,
                    TastyToast.ERROR
                )
                finish()
            }
        }
    }
}