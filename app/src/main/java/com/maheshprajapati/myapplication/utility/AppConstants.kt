package com.maheshprajapati.myapplication.utility


interface AppConstants {

    interface BundleConstants {
        companion object {
            const val IS_NOTES_EDITABLE = "is_notes_editable"
            const val NOTE_STRING = "notes_id"
            const val LOCK_SCREEN = "lockscreen"
            const val COME_FROME = "camefrom"
            const val SPLASH_SCREEN = "splash_screen"
            const val FINISHED = "finished"
        }
    }

    interface OnBackFromDetailsScreen {
        fun onBackFromDetails(isSuccess: Boolean)
    }
}