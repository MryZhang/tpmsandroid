package com.android.morephone.data.repository.message.source;

import android.support.annotation.NonNull;

import com.android.morephone.data.entity.MessageItem;

import java.util.List;

/**
 * Created by Ethan on 3/3/17.
 */

public interface MessageDataSource {

    interface LoadMessagesCallback {
        void onMessagesLoaded(List<MessageItem> messageItems);

        void onDataNotAvailable();
    }

    interface GetMessageCallback {
        void onMessageLoaded(MessageItem messageItem);

        void onDataNotAvailable();
    }

    interface ResultCallback {
        void onResult(boolean isResult);
    }

    void getMessages(@NonNull LoadMessagesCallback callback);

    void getMessages(String phoneNumberIncoming, String phoneNumberOutgoing, @NonNull LoadMessagesCallback callback);

    void getMessagesIncoming(String phoneNumberIncoming, @NonNull LoadMessagesCallback callback);

    void getMessagesOutgoing(String phoneNumberOutgoing, @NonNull LoadMessagesCallback callback);

    void getMessage(String messageSid, @NonNull GetMessageCallback callback);

    void createMessage(String to, String from, String body, @NonNull GetMessageCallback callback);

    void deleteMessage(String messageSid);

    void modifyMessage(String messageSid, @NonNull GetMessageCallback callback);

    void deleteMedia(String messageSid, String mediaSid, @NonNull ResultCallback callback);

    void getMedia(String messageSid, String mediaSid, @NonNull ResultCallback callback);

    void getMedias(String messageSid, @NonNull ResultCallback callback);

    void createFeedback(String messageSid, @NonNull ResultCallback callback);


}
