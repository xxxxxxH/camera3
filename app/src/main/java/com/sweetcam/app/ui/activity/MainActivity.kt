package com.sweetcam.app.ui.activity

import android.content.Intent
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.sweetcam.app.base.BaseActivity
import com.sweetcam.app.utils.requestPermission
import com.sweetcam.app.R
import com.sweetcam.app.GlideEngine
import com.sweetcam.app.callback.IDialogCallBack
import com.sweetcam.app.ui.dialog.ContentDialog
import com.sweetcam.app.utils.click
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(R.layout.activity_main), IDialogCallBack {

    override fun onConvert() {
        activityMainTvLeft.click {
            showInsertAd(isForce = true, tag = "inter_loading").let {
                if (!it) {
                    openGallery(0)
                }
            }
        }
        activityMainTvRight.click {
            showInsertAd(tag = "inter_slim").let {
                if (!it) {
                    openGallery(1)
                }
            }
        }
        activityMainIvAction.click {
            showInsertAd(tag = "inter_camera").let {
                if (!it) {
                    openCamera()
                }
            }
        }

        requestPermission()
    }

    private fun openGallery(targetAc: Int) {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine)
            .setMaxSelectNum(1)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    result?.let {
                        val url = result[0].realPath
                        val intent: Intent?
                        when (targetAc) {
                            0 -> {
                                intent = Intent(this@MainActivity, StickerActivity::class.java)
                                intent.putExtra("url", url)
                            }
                            1 -> {
                                intent = Intent(this@MainActivity, ActionActivity::class.java)
                                intent.putExtra("url", url)
                            }
                            else -> return@let
                        }
                        startActivity(intent)
                    }
                }

                override fun onCancel() {}
            })
    }

    private fun openCamera() {
        PictureSelector.create(this@MainActivity)
            .openCamera(SelectMimeType.ofImage())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    result?.let {
                        val url = result[0].realPath
                        intent = Intent(this@MainActivity, ContentActivity::class.java)
                        intent.putExtra("url", url)
                        startActivity(intent)
                    }
                }

                override fun onCancel() {}
            })
    }

    override fun onBackPressed() {
        ContentDialog.newInstance("Are you sure to exit the application?").show(supportFragmentManager, "")
    }

    override fun onClick(position: Int) {
        if (position == 0) {
            super.onBackPressed()
        }
    }
}