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

import com.squareup.otto.OttoBus;

/**
 * Augments the OttoBus interface with a postGuaranteed method.
 */
public interface GuaranteedDeliveryOttoBus extends OttoBus {
    /**
     * Posts an event to the wrapped bus.  If there is no registered subscriber for the event, throws a
     * NoSubscriberException.
     *
     * @param event  the event to post
     * @throws NoSubscriberException
     */
    void postGuaranteed(final Object event);

    /**
     * A runtime exception indicating that an event posted to a bus had no registered subscriber.
     */
    public static final class NoSubscriberException
            extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private final Object mEvent;

        /**
         * Creates a NoSubscriberException.
         *
         * @param event  the event that had no subscriber
         */
        public NoSubscriberException(final Object event) {
            super(generateMessage(event));
            mEvent = event;
        }

        private static String generateMessage(Object event) {
            return "No subscriber currently registered to receive event of type " + event.getClass().getName();
        }

        /**
         * Returns the event that had no subscriber.
         * @return the unsubscribed event
         */
        public Object getEvent() {
            return mEvent;
        }
    }
}

