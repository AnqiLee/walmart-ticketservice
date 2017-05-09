/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

import java.time.Instant;
import java.util.List;

/**
 * An action taken on the system at a given time.
 *
 * @param <Result> a member of this class is returned when the action is executed.
 */
abstract class Event<Result> {
    final Instant time;
    Result result;

    Event(Instant t) {
        time = t;
    }

    /**
     * Execute the action and make assertions about its behavior.
     * @param priors {@link Event}s in the chronological past. Use these to reason about the state of the system, and
     *                           therefore, the expected result of executing this action.
     * @param v the system on which to execute the action
     */
    abstract void test(List<Event<?>> priors, Venue v);
}
