package com.mindhack.camfire.news.hyperlocal.localnews.inews.eyenews.trendingnews.util

import android.app.Dialog
import android.os.Bundle

import androidx.appcompat.app.AppCompatDialogFragment

/**
 * Created by andrea on 23/08/16.
 */
class TopSheetDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return TopSheetDialog(context!!, theme)
    }
}
