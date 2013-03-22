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

import com.squareup.otto.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ScopedBusTest {
  private ScopedBus mBus;

  @Before public void initTest() {
    mBus = new ScopedBus(BusProvider.getInstance());
  }

  @Test public void testPostIsNotReceivedIfBusIsNeverActivated() {

    EventHandler handler = new EventHandler();
    mBus.register(handler);
    mBus.post(new SubscribedEvent());

    assertThat(handler.wasCalled()).isFalse();
  }

  @Test public void testPostIsNotReceivedWhenBusIsDeactivated() {

    EventHandler handler = new EventHandler();
    mBus.register(handler);
    mBus.activate();
    mBus.deactivate();
    mBus.post(new SubscribedEvent());

    assertThat(handler.wasCalled()).isFalse();
  }

  @Test public void testPostIsNotReceivedIfBusIsReactivatedAfterUnregistering() {

    EventHandler handler = new EventHandler();
    mBus.register(handler);
    mBus.activate();
    mBus.deactivate();
    mBus.unregister(handler);
    mBus.activate();
    mBus.post(new SubscribedEvent());

    assertThat(handler.wasCalled()).isFalse();
  }

  @Test public void testPostIsReceivedIfRegistrationFollowsActivation() {

    EventHandler handler = new EventHandler();
    mBus.activate();
    mBus.register(handler);
    mBus.post(new SubscribedEvent());

    assertThat(handler.wasCalled()).isTrue();
  }

  @Test public void testPostIsReceivedIfActivationFollowsRegistration() {

    EventHandler handler = new EventHandler();
    mBus.register(handler);
    mBus.activate();
    mBus.post(new SubscribedEvent());

    assertThat(handler.wasCalled()).isTrue();
  }

  @Test public void testPostIsReceivedWhenBusIsReactivated() {

    EventHandler handler = new EventHandler();
    mBus.register(handler);
    mBus.activate();
    mBus.deactivate();
    mBus.activate();
    mBus.post(new SubscribedEvent());

    assertThat(handler.wasCalled()).isTrue();
  }

  private static class EventHandler {
    private boolean mWasCalled = false;

    @Subscribe public void onSubscribedEvent(@SuppressWarnings("unused") final SubscribedEvent event) {
      mWasCalled = true;
    }

    private boolean wasCalled() {
      return mWasCalled;
    }
  }

  private static class SubscribedEvent {
  }
}
