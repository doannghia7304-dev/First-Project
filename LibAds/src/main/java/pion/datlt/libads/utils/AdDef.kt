package pion.datlt.libads.utils

class AdDef {
    class NETWORK {
        companion object {
            const val GOOGLE = "google"
            const val PANGLE = "pangle"
            const val MINTEGRAL = "mintegral"
        }
    }


    class ADS_TYPE_ADMOB {
        companion object {
            const val OPEN_APP = "open_app"
            const val INTERSTITIAL = "interstitial"
            const val NATIVE = "native"
            const val NATIVE_FULL_SCREEN = "native_full_screen"
            const val BANNER = "banner"
            const val BANNER_ADAPTIVE = "banner_adaptive"
            const val BANNER_LARGE = "banner_large"
            const val BANNER_INLINE = "banner_inline"
            const val BANNER_COLLAPSIBLE = "banner_collapsible"
            const val REWARD_VIDEO = "reward_video"
            const val REWARD_INTERSTITIAL = "reward_interstitial"
        }
    }

    class ADS_TYPE_MINTEGRAL {
        companion object {
            const val OPEN_APP = "open_app"
            const val INTERSTITIAL = "interstitial"
            const val NATIVE = "native"
            const val NATIVE_AUTO = "native_auto"
            const val BANNER = "banner"
            const val BANNER_ADAPTIVE = "banner_adaptive"
            const val BANNER_INLINE = "banner_inline"
            const val REWARD_VIDEO = "reward_video"
        }
    }

    class GOOGLE_AD_BANNER_SIZE {
        companion object {
            const val BANNER_320x50 = "BANNER_320x50"
            const val LARGE_BANNER_320x100 = "LARGE_BANNER_320x100"
            const val MEDIUM_RECTANGLE_300x250 = "MEDIUM_RECTANGLE_300x250"
            const val FULL_BANNER_468x60 = "FULL_BANNER_468x60"
            const val LEADERBOARD_728x90 = "LEADERBOARD_728x90"
            const val SMART_BANNER = "SMART_BANNER "
        }
    }

    class MINTEGRAL_AD_BANNER_SIZE {
        companion object {
            const val LARGE_TYPE = 1 //Represents the fixed banner ad size - 320dp by 90dp.
            const val MEDIUM_TYPE = 2 //Represents the fixed banner ad size - 300dp by 250dp.
            const val SMART_TYPE =
                3 //if device height <=720,Represents the fixed banner ad size - 320dp by 50dp;
            const val STANDARD_TYPE = 4 //Represents the fixed banner ad size - 320dp by 50dp.
            const val DEV_SET_TYPE = 5 //Customize the size according to your needs.
        }
    }


}