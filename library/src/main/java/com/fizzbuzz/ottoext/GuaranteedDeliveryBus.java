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

import com.squareup.otto.DeadEvent;
import com.squareup.otto.OttoBus;
import com.squareup.otto.Subscribe;

/**
 * An OttoBus implementation that can throw a NoSubscriberException if a posted event has no subscribers. This can be an
 * effective way to "fail fast" when the posting object has an expectation that a subscriber exists for the posted
 * event.
 * <p/>
 * A GuaranteedDeliveryBus can be configured in one of two ways, depending on the policy applied to it at construction.
 * It can
 * <ul>
 * <li>throw a NoSubscriberException for <em>any</em> posted and unsubscribed event.</li>
 * <li>throw a NoSubscriberException only for unsubscribed events that are posted via the
 * <code>postGuaranteed</code> method.
 * </ul>
 *
 * @author Andy Dennie
 */
public class GuaranteedDeliveryBus
        implements OttoBus, GuaranteedDeliveryOttoBus {

    /**
     * Defines the policy under which a GuaranteedDeliveryBus will throw NoSubscriberExceptions.
     */
    public enum Policy {
        /**
         * Under this policy, a GuaranteedDeliveryBus throws a NoSubscriberException only when
         * unsubscribed events are posted via the <code>postGuaranteed</code> method.  Events posted via the
         * <code>post</code> are treated normally.
         */
        GUARANTEE_ON_DEMAND,
        /**
         * Under this policy, a GuaranteedDeliveryBus throws a NoSubscriberException for <em>any</em>
         * posted and unsubscribed event, regardless of whether it was posted via the <code>postGuaranteed</code> or
         * <code>post</code> method.   This can be useful when you want to provide <code>postGuaranteed</code>'s
         * behavior while still allowing callers to interact with the bus via the standard OttoBus interface.
         */
         GUARANTEE_ALWAYS
    }

    private Policy mPolicy;
    private final OttoBus mBus;

    /**
     * Creates a GuaranteedDeliveryBus that wraps the supplied bus.
     *
     * @param bus     the underlying bus to wrap
     * @param policy  the policy to follow for unsubscribed posted events
     */
    public GuaranteedDeliveryBus(final OttoBus bus,
                                 final Policy policy) {
        if (bus == null) {
            throw new NullPointerException("bus must not be null");
        }
        mBus = bus;
        mPolicy = policy;
    }

    @Override
    public void register(final Object obj) {
        mBus.register(obj);
    }

    @Override
    public void unregister(final Object obj) {
        mBus.unregister(obj);
    }

    /**
     * Posts an event to the wrapped bus.  If the {@link Policy#GUARANTEE_ALWAYS} policy is in effect and there is no
     * registered subscriber for the event, throws a NoSubscriberException.
     *
     * @param event  the event to post
     * @throws NoSubscriberException
     */
    @Override
    public void post(final Object event) {
        if (mPolicy == Policy.GUARANTEE_ALWAYS) {
            postGuaranteed(event);
        } else {
            mBus.post(event);
        }
    }

    /**
    * @throws NoSubscriberException
    */

    @Override
    public void postGuaranteed(final Object event) {
        DeadEventHandler handler = new DeadEventHandler(event);
        mBus.register(handler);
        try {
            mBus.post(event);
            if (handler.receivedDeadEvent()) {
                throw new NoSubscriberException(event);
            }

        } finally {
            mBus.unregister(handler);
        }
    }

    private static final class DeadEventHandler {
        private final Object mEvent;
        private boolean mReceivedDeadEvent;

        private DeadEventHandler(final Object event) {
            mEvent = event;
            mReceivedDeadEvent = false;
        }

        @Subscribe
        public void onDeadEvent(final DeadEvent deadEvent) {
            if (mEvent == deadEvent.event) {
                mReceivedDeadEvent = true;
            }
        }

        private boolean receivedDeadEvent() {
            return mReceivedDeadEvent;
        }
    }
}
