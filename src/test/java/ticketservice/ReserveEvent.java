/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

import org.junit.Assert;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ReserveEvent extends Event<String> {
    HoldEvent hold;

    ReserveEvent(Instant t, HoldEvent h) {
        super(t);
        hold = h;
    }

    @Override
    void test(List<Event<?>> priors, Venue v) {
        boolean stillValid = hold.time.isAfter(time.minus(v.getHoldLength()));
        result = v.reserveSeats(time, hold.result.getID(), hold.result.getCustomer());
        if (stillValid){
            Set<Integer> seats = v.getReservedSeats(result);
            Predicate<Integer> isBetterSeat = s -> seats.stream().allMatch(mine -> s < mine);
            Predicate<Set<Integer>> areBetterSeats =
                    betterSeats -> betterSeats.stream().allMatch(isBetterSeat);
            // assertion that the seats we just got are worse than all prior reservations
            Assert.assertTrue(
                    "The reserved seats are worse than previously reserved seats",
                    priors
                            .stream()
                            .filter(e -> e instanceof ReserveEvent)
                            .map(r -> (ReserveEvent) r)
                            .map(r -> v.getReservedSeats(r.result))
                            .allMatch(areBetterSeats));
        } else {
            Assert.assertTrue(
                    "No seats are reserved if the hold is expired",
                    v.getReservedSeats(result).isEmpty());
        }
    }
}
