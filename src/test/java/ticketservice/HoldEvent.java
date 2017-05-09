/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;


import org.junit.Assert;

import java.time.Instant;
import java.util.List;

public class HoldEvent extends Event<SeatHold> {
    private final int numSeats;
    private final String email;

    HoldEvent(Instant t, int numSeats, String email) {
        super(t);
        this.numSeats = numSeats;
        this.email = email;
    }

    @Override
    void test(List<Event<?>> priors, Venue v) {
        boolean enoughSeats =  numSeats <= QueryAvailableEvent.seatsAvailable(time, priors, v);
        result = v.findAndHoldSeats(time, numSeats, email);
        if (enoughSeats) {
            Assert.assertNotNull(result);
            Assert.assertEquals(numSeats, result.getCount());
            Assert.assertEquals(email, result.getCustomer());
        } else {
            Assert.assertNull(result);
        }
    }
}
