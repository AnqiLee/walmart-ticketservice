package ticketservice;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class VenueTest {
    @Test
    public void holdTest() {
        Instant t0 = Instant.EPOCH;
        String customer = "alice@example.com";
        int capacity = 1;
        Duration holdLength = Duration.ofDays(1);
        Venue v = new Venue(capacity, holdLength);
        Instant t1 = t0.plus(holdLength);
        Assert.assertEquals("Initially, all seats are available", capacity, v.numSeatsAvailable(t0));
        Assert.assertNull(
                "Attempting to hold more seats than are available returns null",
                v.findAndHoldSeats(t0,capacity + 1, customer));
        try {
            v.findAndHoldSeats(t0,0, customer);
            Assert.fail("Requests to hold fewer than one seat should be rejected.");
        } catch (IllegalArgumentException e) {}
        SeatHold hold = v.findAndHoldSeats(t0, capacity, customer);
        Assert.assertNotNull(hold);
        // TODO: more assertions on a successful hold.
        Assert.assertEquals(
                "After the hold has expired, all the seats are available",
                capacity,
                v.numSeatsAvailable(t1));
    }

    @Test
    public void reserveTest() {
        Instant t0 = Instant.EPOCH;
        String customer = "alice@example.com";
        int capacity = 1;
        Duration holdLength = Duration.ofDays(1);
        Instant t1 = t0.plus(holdLength);
        for (Instant reserveTime : Arrays.asList(t0, t1)){
            Venue v = new Venue(capacity, Duration.ofDays(1));
            SeatHold hold = v.findAndHoldSeats(t0, capacity, customer);
            String confirmation = v.reserveSeats(reserveTime, hold.getID(), customer);
            if (hold.isExpired(reserveTime)) {
                Assert.assertNull("Reserving on an expired hold returns null", confirmation);
            } else {
                Assert.assertNotNull(confirmation);
            }
            Assert.assertNull(
                "Reserving more than once yields null",
                v.reserveSeats(reserveTime, hold.getID(), customer));
        }
    }

    @Test
    public void holdAndReserveScenario() {
        Instant t0 = Instant.EPOCH;
        Duration delta = Duration.ofDays(1);
        Instant t1 = t0.plus(delta);
        Instant t2 = t1.plus(delta);

        String customer = "alice@example.com";

        int capacity = 5;
        Duration holdLength = Duration.ofDays(2);
        Venue v = new Venue(capacity, holdLength);

        // t0
        SeatHold s0 = v.findAndHoldSeats(t0, 1, customer);
        SeatHold s1 = v.findAndHoldSeats(t0, 4, customer);
        Assert.assertNotNull(s0);
        Assert.assertNotNull(s1);
        Assert.assertEquals(0, v.numSeatsAvailable(t0));

        // t1
        Assert.assertNull(v.findAndHoldSeats(t1, 1, customer));
        String c0 = v.reserveSeats(t1, s0.getID(), customer);
        Assert.assertNotNull(c0);
        Assert.assertEquals(0, v.numSeatsAvailable(t1));

        // t2
        Assert.assertNull(v.reserveSeats(t2, s1.getID(), customer));
        SeatHold s2 = v.findAndHoldSeats(t2, 2, customer);
        Assert.assertNotNull(s2);
        Assert.assertEquals(2, v.numSeatsAvailable(t2));
        String c2 = v.reserveSeats(t2, s2.getID(), customer);
        Assert.assertNotNull(c2);
        Assert.assertEquals(2, v.numSeatsAvailable(t2));
        for (int earlyReservedSeat : v.getReservedSeats(c0)) {
            for (int lateReservedSeat : v.getReservedSeats(c2)) {
                Assert.assertTrue(
                        "A seat reserved earlier should be a better seat than a seat reserved later.",
                        earlyReservedSeat < lateReservedSeat);
            }
        }
    }
}
