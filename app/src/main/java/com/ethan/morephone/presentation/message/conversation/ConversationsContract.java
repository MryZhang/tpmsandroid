package com.ethan.morephone.presentation.message.conversation;

import com.android.morephone.data.entity.MessageItem;
import com.ethan.morephone.presentation.BasePresenter;
import com.ethan.morephone.presentation.BaseView;

import java.util.List;

/**
 * Created by Ethan on 2/17/17.
 */

public interface ConversationsContract {

    interface View extends BaseView<Presenter> {

        void showListMessage(List<MessageItem> smsEntities);

        void showLoading(boolean isActive);

    }

    interface Presenter extends BasePresenter {

        void loadListMessageResource();

        void loadMessagesIncoming(String phoneNumberIncoming);

        void loadMessageOutgoing(String phoneNumberOutgoing);

    }
}
