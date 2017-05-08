/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;

public class SeatHoldTest {
    @Test
    public void expirationTest() {
        Instant t0 = Instant.EPOCH;
        Instant t1 = t0.plus(Duration.ofDays(1));

        Assert.assertTrue(
                "If \"now\" is after the expiry time, the hold is expired",
                new SeatHold(0, "", t0).isExpired(t1));
        Assert.assertFalse(
                "If \"now\" is before the expiry time, the hold is not expired",
                new SeatHold(0, "", t1).isExpired(t0));
        Assert.assertTrue(
                "If \"now\" equals expiry, the hold is expired",
                new SeatHold(0, "", t0).isExpired(t0));
    }
}
