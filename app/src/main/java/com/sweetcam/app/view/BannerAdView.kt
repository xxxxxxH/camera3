package com.sweetcam.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.sweetcam.app.utils.app
import com.sweetcam.app.utils.loge
import com.sweetcam.app.R
import kotlinx.android.synthetic.main.view_banner_ad.view.*

class BannerAdView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(app).inflate(R.layout.view_banner_ad, this)
        viewBannerAdContent.apply {
            setListener(object : MaxAdViewAdListener {
                override fun onAdExpanded(ad: MaxAd?) {
                    "MaxAdView onAdExpanded".loge()
                }

                override fun onAdCollapsed(ad: MaxAd?) {
                    "MaxAdView onAdCollapsed".loge()
                }

                override fun onAdLoaded(ad: MaxAd?) {
                    "MaxAdView onAdLoaded".loge()
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                    "MaxAdView onAdDisplayed".loge()
                }

                override fun onAdHidden(ad: MaxAd?) {
                    "MaxAdView onAdHidden".loge()
                }

                override fun onAdClicked(ad: MaxAd?) {
                    "MaxAdView onAdClicked".loge()
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError) {
                    "MaxAdView onAdLoadFailed $adUnitId $error".loge()
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    "MaxAdView onAdDisplayFailed $ad $error".loge()
                }
            })
            loadAd()
        }
    }
}