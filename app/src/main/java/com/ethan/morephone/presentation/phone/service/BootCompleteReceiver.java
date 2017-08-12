package com.ethan.morephone.presentation.phone.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.morephone.data.log.DebugTool;

/**
 * Created by Ethan on 4/27/17.
 */

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DebugTool.logD("REBOOT NOW");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            PhoneService.startPhoneService(context);
        } else {
            DebugTool.logD("REBOOT NOT WORK");
        }
    }
}
