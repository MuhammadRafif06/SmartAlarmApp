package com.rafif.smartalarm.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DateDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var dialogDateListener : DialogDateSetListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogDateListener = context as DialogDateSetListener
    }

    override fun onDetach() {
        super.onDetach()
        if (dialogDateListener != null) dialogDateListener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(activity as Context, this, year, month, date)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayMonth: Int) {
        dialogDateListener?.onDialogDateSet(tag, year, month, dayMonth)
    }

    interface DialogDateSetListener {
        fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int)
    }
}