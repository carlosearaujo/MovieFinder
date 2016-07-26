package br.com.zup.moviefinder.controller.utils;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Carlos on 25/07/2016.
 */

public abstract class AfterTextChangedTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
}
