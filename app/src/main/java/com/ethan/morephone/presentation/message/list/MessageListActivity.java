package com.ethan.morephone.presentation.message.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.ethan.morephone.R;
import com.ethan.morephone.presentation.BaseActivity;
import com.ethan.morephone.utils.ActivityUtils;

/**
 * Created by Ethan on 2/16/17.
 */

public class MessageListActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment instanceof MessageListFragment) return;
        MessageListFragment browserFragment = MessageListFragment.getInstance(getIntent().getExtras());
        ActivityUtils.replaceFragmentToActivity(
                getSupportFragmentManager(),
                browserFragment,
                R.id.content_frame,
                MessageListFragment.class.getSimpleName());
    }

}
