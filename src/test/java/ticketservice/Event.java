/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

import java.time.Instant;
import java.util.List;

abstract class Event<Result> {
    final Instant time;
    Result result;

    Event(Instant t) {
        time = t;
    }

    abstract void test(List<Event<?>> priors, Venue v);
}
