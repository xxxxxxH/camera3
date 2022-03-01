package com.sweetcam.app.view.topon

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.anythink.core.api.ATAdInfo
import com.anythink.nativead.banner.api.ATNativeBannerConfig
import com.anythink.nativead.banner.api.ATNativeBannerListener
import com.anythink.nativead.banner.api.ATNativeBannerSize
import com.anythink.nativead.banner.api.ATNativeBannerView
import com.sweetcam.app.R
import com.sweetcam.app.utils.app
import com.sweetcam.app.utils.loge

class TopOnBannerAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    private val bannerView by lazy {
        ATNativeBannerView(context)
    }

    init {
        addView(bannerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        bannerView.apply {
            setUnitId(app.getString(R.string.top_on_banner_ad_id))
            val configAuto = ATNativeBannerConfig()
            configAuto.bannerSize = ATNativeBannerSize.BANNER_SIZE_AUTO
            configAuto.isCtaBtnShow = true
            configAuto.ctaBgColor = -0x1000000
            configAuto.ctaColor = -0xff0100
            configAuto.titleColor = -0x1
            setBannerConfig(configAuto)
            setLocalExtra(mutableMapOf())
            setBackgroundColor(-0x1)

            setAdListener(object : ATNativeBannerListener {
                override fun onAdLoaded() {
                    "TopOnBannerAdView onAdLoaded".loge()
                }

                override fun onAdError(p0: String?) {
                    "TopOnBannerAdView onAdError $p0".loge()
                }

                override fun onAdClick(p0: ATAdInfo?) {
                    "TopOnBannerAdView onAdClick".loge()
                }

                override fun onAdClose() {
                    "TopOnBannerAdView onAdClose".loge()
                }

                override fun onAdShow(p0: ATAdInfo?) {
                    "TopOnBannerAdView onAdShow".loge()
                }

                override fun onAutoRefresh(p0: ATAdInfo?) {
                    "TopOnBannerAdView onAutoRefresh".loge()
                }

                override fun onAutoRefreshFail(p0: String?) {
                    "TopOnBannerAdView onAutoRefreshFail".loge()
                }
            })
            loadAd(null)
        }
    }
}