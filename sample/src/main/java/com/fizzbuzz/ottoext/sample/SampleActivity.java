/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fizzbuzz.ottoext.sample;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.fizzbuzz.ottoext.*;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;

public class SampleActivity extends Activity {

    private OttoBus mMainThreadBus = MainThreadBusProvider.getInstance();
    private ScopedBus mScopedBus = new ScopedBus(BusProvider.getInstance());
    private GuaranteedDeliveryOttoBus mGuaranteedDeliveryBus = GuaranteedDeliveryBusProvider.getInstance();
    private ToastyHandler mGuaranteedHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // register handlers for each bus
        mMainThreadBus.register(new ThreadAwareToastyHandler(this));
        mScopedBus.register(new ToastyHandler(this));

        // set up a handler for use with the guaranteed delivery bus, but don't register it yet
        mGuaranteedHandler = new ToastyHandler(this);

        setContentView(R.layout.otto_extensions_sample);
    }

    public void onMainThreadBusPostClicked(final View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.threadRadioGroup);
        int checkedButton = radioGroup.getCheckedRadioButtonId();
        if (checkedButton == R.id.mainThreadButton) {
            // post from main thread
            mMainThreadBus.post(new Event());
        } else {
            // post from background thread
            new Thread() {
                public void run() {
                    mMainThreadBus.post(new Event());
                }
            }.start();
        }
    }

    public void onScopedBusActivationToggleClicked(final View view) {
        if (((ToggleButton)view).isChecked())
            mScopedBus.activate();
        else
            mScopedBus.deactivate();
    }

    public void onScopedBusPostClicked(final View view) {
        mScopedBus.post(new Event());
    }

    public void onGuaranteedBusRegistrationToggleClicked(final View view) {
        if (((ToggleButton)view).isChecked())
            mGuaranteedDeliveryBus.register(mGuaranteedHandler);
        else
            mGuaranteedDeliveryBus.unregister(mGuaranteedHandler);
    }

    public void onGuaranteedBusPostClicked(final View view) {
        try {
            mGuaranteedDeliveryBus.postGuaranteed(new Event());
        } catch (GuaranteedDeliveryOttoBus.NoSubscriberException e) {
            Toast.makeText(this, "no subscriber registered!", Toast.LENGTH_SHORT).show();
        }
    }

    private static class Event {
    }

    private static class ToastyHandler {
        private Context mContext;

        public ToastyHandler(Context context) {
            mContext = context;
        }

        @Subscribe
        public void onEvent(Event event) {
            Toast.makeText(mContext, "received event", Toast.LENGTH_SHORT).show();
        }
    }

    private static class ThreadAwareToastyHandler extends ToastyHandler {
        public ThreadAwareToastyHandler(Context context) {
            super(context);
        }

        @Override
        @Subscribe
        public void onEvent(Event event) {
            // show toast only if this is the main thread
            if (Looper.myLooper() == Looper.getMainLooper())
                super.onEvent(event);
        }
    }
}
