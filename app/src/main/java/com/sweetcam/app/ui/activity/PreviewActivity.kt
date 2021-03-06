package com.sweetcam.app.ui.activity

import com.pipipi.camhd.R
import com.sweetcam.app.base.BaseActivity
import kotlinx.android.synthetic.main.activity_preview.*

class PreviewActivity : BaseActivity(R.layout.activity_preview) {

    override fun onConvert() {
        val url = intent.getStringExtra("url") as String
        iv.displayImage(url)
    }
}