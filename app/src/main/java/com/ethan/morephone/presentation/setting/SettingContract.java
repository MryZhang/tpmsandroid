package com.ethan.morephone.presentation.setting;

import com.ethan.morephone.presentation.BasePresenter;
import com.ethan.morephone.presentation.BaseView;

/**
 * Created by Ethan on 4/25/17.
 */

public interface SettingContract {

    interface View extends BaseView<Presenter> {

        void showLoading(boolean isActive);

        void updateFriendlyName(String friendlyName);
    }

    interface Presenter extends BasePresenter {

        void changeFriendlyName(String incomingPhoneNumberSid, String friendlyName);

    }
}