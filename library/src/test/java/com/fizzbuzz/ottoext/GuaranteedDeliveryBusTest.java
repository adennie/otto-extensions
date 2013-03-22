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

import com.fizzbuzz.ottoext.GuaranteedDeliveryOttoBus.NoSubscriberException;
import com.squareup.otto.Subscribe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class GuaranteedDeliveryBusTest {
  private GuaranteedDeliveryBus mGuaranteeAlwaysBus;
  private GuaranteedDeliveryBus mGuaranteeOnDemandBus;

  @Before public void initTest() {
    mGuaranteeAlwaysBus = new GuaranteedDeliveryBus(MainThreadBusProvider.getInstance(), GuaranteedDeliveryBus.Policy.GUARANTEE_ALWAYS);
    mGuaranteeOnDemandBus = new GuaranteedDeliveryBus(MainThreadBusProvider.getInstance(), GuaranteedDeliveryBus.Policy.GUARANTEE_ON_DEMAND);

    mGuaranteeAlwaysBus.register(this);
    // no need to register with mGuaranteeOnDemandBus, since it wraps the same underlying Bus as mGuaranteeAlwaysBus, to which
    // we've already registered.
  }

  @Test public void testPostingSubscribedEventToGuaranteeAlwaysBusDoesntThrow() {
    boolean caughtException = false;
    try {
      mGuaranteeAlwaysBus.post(new SubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isFalse();
    }
  }

  @Test public void testPostingSubscribedEventToGuaranteeOnDemandBusDoesntThrow() {
    boolean caughtException = false;
    try {
      mGuaranteeOnDemandBus.post(new SubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isFalse();
    }
  }

  @Test public void testPostingUnsubscribedEventToGuaranteeAlwaysBusThrows() {
    boolean caughtException = false;
    try {
      mGuaranteeAlwaysBus.post(new UnsubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isTrue();
    }
  }

  @Test public void testPostingUnsubscribedEventToGuaranteeOnDemandBusDoesntThrow() {
    boolean caughtException = false;
    try {
      mGuaranteeOnDemandBus.post(new UnsubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isFalse();
    }
  }

  @Test public void testPostGuaranteedOfSubscribedEventToGuaranteeAlwaysBusDoesntThrow() {
    boolean caughtException = false;
    try {
      mGuaranteeAlwaysBus.postGuaranteed(new SubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isFalse();
    }
  }

  @Test public void testPostGuaranteedOfSubscribedEventToGuaranteeOnDemandBusDoesntThrow() {
    boolean caughtException = false;
    try {
      mGuaranteeOnDemandBus.postGuaranteed(new SubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isFalse();
    }
  }

  @Test public void testPostGuaranteedOfUnsubscribedEventToGuaranteeAlwaysBusThrows() {
    boolean caughtException = false;
    try {
      mGuaranteeAlwaysBus.postGuaranteed(new UnsubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isTrue();
    }
  }

  @Test public void testPostGuaranteedOfUnsubscribedEventToGuaranteeOnDemandBusThrows() {
    boolean caughtException = false;
    try {
      mGuaranteeOnDemandBus.postGuaranteed(new UnsubscribedEvent());
    } catch (NoSubscriberException e) {
      caughtException = true;
    } finally {
      assertThat(caughtException).isTrue();
    }
  }

  @Subscribe public void onSubscribedEvent(@SuppressWarnings("unused") final SubscribedEvent event) {
  }

  private static class UnsubscribedEvent {
  }

  private static class SubscribedEvent {
  }
}
