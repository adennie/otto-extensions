/*
 * Copyright (c) 2013 Fizz Buzz LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fizzbuzz.ottoext;

import android.os.Handler;
import android.os.Looper;
import com.squareup.otto.OttoBus;

/* Adapted from pommdeterresautee's comment here: https://github.com/square/otto/issues/38 */

/**
 * An OttoBus implementation that wraps another bus and posts all events to the wrapped bus on the main thread.
 */
public class MainThreadBus implements OttoBus {
  private final OttoBus mBus;
  private final Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * Creates a MainThreadBus that wraps another bus.
     *
     * @param bus  the bus to wrap
     */
  public MainThreadBus(final OttoBus bus) {
    if (bus == null) {
      throw new NullPointerException("bus must not be null");
    }
    mBus = bus;
  }

  @Override public void register(Object obj) {
    mBus.register(obj);
  }

  @Override public void unregister(Object obj) {
    mBus.unregister(obj);
  }

    /**
     * {@inheritDoc}
     * <p>The event will always be posted on the main thread.  If called from a background thread, it will be
     * rerouted to the main thread.</p>
     */
  @Override public void post(final Object event) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
      mBus.post(event);
    } else {
      mHandler.post(new Runnable() {
        @Override public void run() {
          mBus.post(event);
        }
      });
    }
  }
}
