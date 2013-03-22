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

import java.util.HashSet;
import java.util.Set;

// modeled after https://gist.github.com/JakeWharton/3057437

/**
 * An OttoBus implementation that wraps another bus and provides an activation scope.  Registrants
 * of the ScopedBus are automatically registered and deregistered with the wrapped bus upon activation and
 * deactivation of the ScopedBus. Posts to the ScopedBus are forwarded to the wrapped bus regardless of the ScopedBus'
 * activation state.
 */
public class ScopedBus
        implements OttoBus {
    private final OttoBus mBus;
    private final Set<Object> mRegistrants = new HashSet<Object>();
    private boolean mActive;

    /**
     * Creates a ScopedBus.  Note that the bus is initially inactive.
     *
     * @param bus  the OttoBus to wrap
     */
    public ScopedBus(final OttoBus bus) {
        if (bus == null) {
            throw new NullPointerException("bus must not be null");
        }
        mBus = bus;
        mActive = false;
    }

    /**
     * Adds an object to this ScopedBus' set of registrants.  If this ScopedBus is currently active, the object will
     * also be registered with the wrapped bus; if not, it will get registered with the wrapped bus upon activation.
     *
     * @param obj  the object to be registered
     */
    @Override
    public void register(Object obj) {
        mRegistrants.add(obj);
        if (mActive) {
            mBus.register(obj);
        }
    }

    /**
     * Removes an object from this ScopedBus' set of registrants.  If this ScopedBus is currently active, the object
     * will also be unregistered with the wrapped bus.
     *
     * @param obj  the object to be unregistered
     */
    @Override
    public void unregister(Object obj) {
        mRegistrants.remove(obj);
        if (mActive) {
            mBus.unregister(obj);
        }
    }

    @Override
    public void post(Object event) {
        mBus.post(event);
    }

    /**
     * Deactivates this ScopedBus.  All current registrants of this ScopedBus are deregistered from the wrapped bus.
     */
    public void deactivate() {
        mActive = false;
        for (Object obj : mRegistrants) {
            mBus.unregister(obj);
        }
    }

    /**
     * Activates this ScopedBus.  All current registrants of this ScopedBus are registered with the wrapped bus.
     */
    public void activate() {
        mActive = true;
        for (Object obj : mRegistrants) {
            mBus.register(obj);
        }
    }

    /**
     * Returns the set of objects currently registered with this ScopedBus.
     *
     * @return this ScopedBus' registrants
     */
    public Set<Object> getRegistrants() {
        return mRegistrants;
    }

    /**
     * Returns the bus that this ScopedBus wraps.
     *
     * @return the wrapped bus
     */
    public OttoBus getWrappedBus() {
        return mBus;
    }
}
