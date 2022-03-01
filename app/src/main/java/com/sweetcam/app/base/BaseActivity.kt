package com.sweetcam.app.base

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.anythink.core.api.ATAdConst
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.anythink.splashad.api.IATSplashEyeAd
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.sweetcam.app.*
import com.sweetcam.app.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity(layoutId: Int) : AppCompatActivity(layoutId) {

    private var isBackground = false
    private var maxInterstitialAd: MaxInterstitialAd? = null
    private var openAd: ATSplashAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createMaxInterstitialAd()
        createOpenAd()
        onConvert()
    }

    abstract fun onConvert()

    open fun onMaxInterstitialAdHidden() {}

    fun registerEventBus() {
        object : BaseLifeCycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                EventBus.getDefault().register(this@BaseActivity)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                EventBus.getDefault().unregister(this@BaseActivity)
            }
        }.bindWithLifecycle(this)
    }

    override fun onStop() {
        super.onStop()
        isBackground = isInBackground()
    }

    override fun onResume() {
        super.onResume()
        if (isBackground) {
            isBackground = false
            val content = findViewById<ViewGroup>(android.R.id.content)
            (content.getTag(R.id.open_ad_view_id) as? FrameLayout)?.let {
                showOpenAd(it)
            } ?: kotlin.run {
                FrameLayout(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    content.addView(this)
                    content.setTag(R.id.open_ad_view_id, this)
                    showOpenAd(this)
                }
            }
        }
    }

    private fun createOpenAd(offset: Long = 0L) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (offset > 0) {
                delay(offset)
            }
            withContext(Dispatchers.Main) {
                openAd?.onDestory()
                openAd = openAdCreator()
            }
        }
    }

    private fun createMaxInterstitialAd(offset: Long = 0L) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (offset > 0) {
                delay(offset)
            }
            withContext(Dispatchers.Main) {
                maxInterstitialAd?.destroy()
                maxInterstitialAd = maxInterstitialAdCreator()
            }
        }
    }


    private fun maxInterstitialAdCreator() =
        MaxInterstitialAd(getString(R.string.insert_ad_id), this).apply {
            "MaxInterstitialAd maxInterstitialAdCreator".loge()
            setListener(object : MaxAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    "MaxInterstitialAd onAdLoaded".loge()
                }

                override fun onAdDisplayed(ad: MaxAd?) {
                    "MaxInterstitialAd onAdDisplayed".loge()
                }

                override fun onAdHidden(ad: MaxAd?) {
                    "MaxInterstitialAd onAdHidden".loge()
                    adLastTime = System.currentTimeMillis()
                    createMaxInterstitialAd()
                    onMaxInterstitialAdHidden()
                }

                override fun onAdClicked(ad: MaxAd?) {
                    "MaxInterstitialAd onAdClicked".loge()
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    "MaxInterstitialAd onAdLoadFailed $adUnitId $error".loge()
                    createMaxInterstitialAd(3000)
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    "MaxInterstitialAd onAdDisplayFailed $ad $error".loge()
                    createMaxInterstitialAd(3000)
                }
            })
            loadAd()
        }


    private fun openAdCreator() =
        ATSplashAd(this, getString(R.string.open_ad_id), null, object : ATSplashAdListener {
            override fun onAdLoaded() {
                Log.e("openAdCreator", "onAdLoaded")
            }

            override fun onNoAdError(p0: AdError?) {
                Log.e("openAdCreator", "onNoAdError $p0")
                createOpenAd(3000)
            }

            override fun onAdShow(p0: ATAdInfo?) {
                Log.e("openAdCreator", "onAdShow $p0")
            }

            override fun onAdClick(p0: ATAdInfo?) {
                Log.e("openAdCreator", "onAdClick")
            }

            override fun onAdDismiss(p0: ATAdInfo?, p1: IATSplashEyeAd?) {
                Log.e("openAdCreator", "onAdDismiss")
                createOpenAd()

            }
        }, 5000).apply {
            setLocalExtra(
                mutableMapOf<String, Any>(
                    ATAdConst.KEY.AD_WIDTH to globalWidth,
                    ATAdConst.KEY.AD_HEIGHT to (globalHeight * 0.85).toInt()
                )
            )
            loadAd()
        }

    private fun showOpenAdImpl(viewGroup: ViewGroup, tag: String = ""): Boolean {
        openAd?.let {
            if (it.isAdReady) {
                it.show(this, viewGroup)
                return true
            }
        }
        return false
    }


    private fun showInsertAdImpl(tag: String = ""): Boolean {
        maxInterstitialAd?.let {
            if (it.isReady) {
                it.showAd(tag)
                return true
            }
        }
        return false
    }


    private fun showOpenAd(viewGroup: ViewGroup, tag: String = ""): Boolean {
        if (configEntity.isOpenAdReplacedByInsertAd()) {
            return showInsertAd(tag = tag)
        } else {
            return showOpenAdImpl(viewGroup, tag = tag)
        }
    }

    fun showInsertAd(showByPercent: Boolean = false, isForce: Boolean = false, tag: String = ""): Boolean {
        if (isForce) {
            return showInsertAdImpl(tag)
        } else {
            if (configEntity.isCanShowInsertAd()) {
                if ((showByPercent && configEntity.isCanShowByPercent()) || (!showByPercent)) {
                    if (System.currentTimeMillis() - adLastTime > configEntity.insertAdOffset() * 1000) {
                        if (adShownList.getOrNull(adShownIndex) == true) {
                            return showInsertAdImpl(tag)
                        }
                        adShownIndex++
                        if (adShownIndex >= adShownList.size) {
                            adShownIndex = 0
                        }
                    }
                }
            }
            return false
        }
    }
}