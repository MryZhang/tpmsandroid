package com.ethan.morephone.presentation.authentication.login;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;


/**
 * Created by truongnguyen on 10/15/16.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View mView;

    public LoginPresenter(@NonNull LoginContract.View view) {
        mView = view;

        mView.setPresenter(this);
    }

    @Override
    public void doLogin(String email, String password) {
        mView.setLoading(true);
    }

    @Override
    public void checkMissingInfo(final EditText editTextFirstName, final EditText editTextLastName) {
        editTextFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInfo(editTextFirstName, editTextLastName);
            }
        });

        editTextLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateInfo(editTextFirstName, editTextLastName);
            }
        });
    }

    @Override
    public void start() {
        mView.setEnableNextStep(false);
    }

    public void validateInfo(EditText editTextFirstName, EditText editTextLastName) {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();

        if(!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName)){
            mView.setEnableNextStep(true);
        }else{
            mView.setEnableNextStep(false);
        }
    }
}
