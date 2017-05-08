/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A {@link ConfirmedTicketService} in which reservation guarantees the best available seats. Holds are "garbage
 * collected" only when it is necessary to determine the number of available seats.
 */
public class Venue implements ConfirmedTicketService {
    // Many methods take an Instant. This is to allow tests to explicitly set the time.
    private Duration holdLength;
    private int capacity;
    // TODO: (optimization) maintain a list of what seats are filled (for best seat finding) and a separate
    // Map<Confirmation, Set<Integer>>
    private List<String> seats;
    private Map<Integer, SeatHold> holds = new HashMap<>();
    private int reserveCount = 0;
    private IDGenerator idGenerator = new IDGenerator();

    public Venue(int capacity, Duration holdLength) {
        this.capacity = capacity;
        this.seats = Arrays.asList(new String[capacity]);
        this.holdLength = holdLength;
    }

    /**
     *
     * @param now the current time
     * @return the number of seats under a non-expired hold.
     */
    private synchronized int numHeld(Instant now) {
        holds.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
        return holds
                .entrySet()
                .stream()
                .mapToInt(e -> e.getValue().getCount())
                .sum();
    }

    synchronized int numSeatsAvailable(Instant now) {
        return capacity - numHeld(now) - reserveCount;
    }

    /**
     * Linear in the number holds, which is the number of seats in the venue in the worst case.
     */
    @Override
    public int numSeatsAvailable() {
        return numSeatsAvailable(Instant.now());
    }

    synchronized SeatHold findAndHoldSeats(Instant now, int numSeats, String customerEmail) {
        if (numSeats < 1) {
            throw new IllegalArgumentException("At least one seat must be reserved.");
        }
        if (numSeats > numSeatsAvailable(now)) {
            return null;
        }
        SeatHold hold = new SeatHold(numSeats, customerEmail, now.plus(holdLength));
        holds.put(hold.getID(), hold);
        return hold;
    }

    /**
     * Linear in the number of holds as it calls {@link #reserveSeats(Instant, int, String)}.
     */
    @Override
    public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
        return findAndHoldSeats(Instant.now(), numSeats, customerEmail);
    }

    synchronized String reserveSeats(Instant now, int seatHoldId, String customerEmail) {
        SeatHold hold = holds.get(seatHoldId);
        if (hold == null) {
            return null;
        }
        if (!hold.getCustomer().equals(customerEmail)) {
            throw new IllegalArgumentException();
        }
        if (hold.isExpired(now)) {
            return null;
        }
        String confirmation = Integer.toString(idGenerator.getNextID());
        holds.remove(seatHoldId);
        int holdCount = hold.getCount();
        reserveCount += holdCount;
        ListIterator<String> iter = seats.listIterator();
        while (0 < holdCount) {
            if (iter.next() == null) {
                iter.set(confirmation);
                holdCount--;
            }
        }
        return confirmation;
    }

    /**
     * Linear in the number of seats.
     */
    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        return reserveSeats(Instant.now(), seatHoldId, customerEmail);
    }

    @Override
    public synchronized Set<Integer> getReservedSeats(String confirmationCode) {
        return IntStream
                .range(0, seats.size())
                .filter(i -> confirmationCode.equals(seats.get(i)))
                .boxed()
                .collect(Collectors.toSet());
    }
}
