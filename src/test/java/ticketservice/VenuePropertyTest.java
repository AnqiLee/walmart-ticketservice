/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Property-based testing. Each {@link Event} executes against the given venue and then checks that the result is
 * correct with respect to prior events.
 */
public class VenuePropertyTest {
    @Test
    public void propertyTest() {
        int capacity = 500;
        Duration holdLength = Duration.ofDays(2);
        Venue v = new Venue(capacity, holdLength);

        String email = "alice@example.com";
        Duration delta = Duration.ofDays(1);

        // events
        Instant t0 = Instant.EPOCH;
        QueryAvailableEvent e1 = new QueryAvailableEvent(t0);
        HoldEvent e2 = new HoldEvent(t0, 250, email);
        HoldEvent e3 = new HoldEvent(t0, 250, email);

        Instant t1 = t0.plus(delta);
        ReserveEvent e4 = new ReserveEvent(t1, e2);
        QueryAvailableEvent e5 = new QueryAvailableEvent(t1);

        Instant t2 = t1.plus(delta);
        QueryAvailableEvent e6 = new QueryAvailableEvent(t2);
        HoldEvent e7 = new HoldEvent(t2, 100, email);

        Instant t3 = t2.plus(delta);
        ReserveEvent e8 = new ReserveEvent(t3, e7);
        QueryAvailableEvent e9 = new QueryAvailableEvent(t3);

        propertyTestScenario(Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9), v);
    }

    private void propertyTestScenario(List<Event<?>> events, Venue v) {
        for (int i = 0; i < events.size(); i++) {
            List<Event<?>> priors = events.subList(0, i);
            Event<?> current = events.get(i);
            current.test(priors, v);
        }
    }
}
