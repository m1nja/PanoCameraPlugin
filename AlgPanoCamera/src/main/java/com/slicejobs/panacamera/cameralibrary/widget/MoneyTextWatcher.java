package com.slicejobs.panacamera.cameralibrary.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MoneyTextWatcher implements TextWatcher {
    private EditText editText;
    private int digits = 2;

    public MoneyTextWatcher(EditText et) {
        this.editText = et;
    }

    public MoneyTextWatcher setDigits(int d) {
        this.digits = d;
        return this;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (((CharSequence)s).toString().contains(".") && ((CharSequence)s).length() - 1 - ((CharSequence)s).toString().indexOf(".") > this.digits) {
            s = ((CharSequence)s).toString().subSequence(0, ((CharSequence)s).toString().indexOf(".") + this.digits + 1);
            this.editText.setText((CharSequence)s);
            this.editText.setSelection(((CharSequence)s).length());
        }

        if (((CharSequence)s).toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            this.editText.setText((CharSequence)s);
            this.editText.setSelection(2);
        }

        if (((CharSequence)s).toString().startsWith("0") && ((CharSequence)s).toString().trim().length() > 1 && !((CharSequence)s).toString().substring(1, 2).equals(".")) {
            this.editText.setText(((CharSequence)s).subSequence(0, 1));
            this.editText.setSelection(1);
        }
    }

    public void afterTextChanged(Editable s) {
    }
}
