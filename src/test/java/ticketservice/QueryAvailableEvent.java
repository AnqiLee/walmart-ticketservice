/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

import org.junit.Assert;

import java.time.Instant;
import java.util.List;
import java.util.function.Predicate;

public class QueryAvailableEvent extends Event<Integer> {
    QueryAvailableEvent(Instant t) {
        super(t);
    }

    static int seatsAvailable(Instant eventTime, List<Event<?>> priors, Venue v) {
        Predicate<HoldEvent> isUnexpired = h -> h.time.isAfter(eventTime.minus(v.getHoldLength()));
        Predicate<HoldEvent> isUnreserved =
                h -> priors
                        .stream()
                        .filter(e -> e instanceof ReserveEvent)
                        .map(r -> (ReserveEvent) r)
                        .noneMatch(r -> r.hold == h);
        int numHolds = priors
                .stream()
                .filter(e -> e instanceof HoldEvent)
                .map(h -> (HoldEvent) h)
                .filter(isUnexpired)
                .filter(isUnreserved)
                .mapToInt(e -> e.result.getCount())
                .sum();
        int numReserved = priors
                .stream()
                .filter(e -> e instanceof ReserveEvent)
                .map(r -> (ReserveEvent) r)
                .mapToInt(r -> r.hold.result.getCount())
                .sum();
        return v.getCapacity() - numHolds - numReserved;
    }

    @Override
    void test(List<Event<?>> priors, Venue v) {
        int expectation = seatsAvailable(time, priors, v);
        result = v.numSeatsAvailable(time);
        Assert.assertEquals(
                "The venue's capacity minus current holds minus reserved seats is the number of seats available",
                expectation,
                result.intValue());
    }
}
