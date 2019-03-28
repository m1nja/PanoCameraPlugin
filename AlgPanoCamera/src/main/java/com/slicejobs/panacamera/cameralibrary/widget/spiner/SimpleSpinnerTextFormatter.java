package com.slicejobs.panacamera.cameralibrary.widget.spiner;

import android.text.Spannable;
import android.text.SpannableString;

public class SimpleSpinnerTextFormatter implements SpinnerTextFormatter {
    public SimpleSpinnerTextFormatter() {
    }

    public Spannable format(String text) {
        return new SpannableString(text);
    }
}
