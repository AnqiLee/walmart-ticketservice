package ticketservice;

import java.time.Instant;

public class SeatHold {
    private Instant expiry;
    private int count;
    private int id;

    private static IDGenerator idGenerator = new IDGenerator();
    private String customer;

    boolean isExpired(Instant now) {
        return !expiry.isAfter(now);
    }

    public SeatHold(int numSeats, String customerEmail, Instant expireTime) {
        customer = customerEmail;
        count = numSeats;
        expiry = expireTime;
        id = idGenerator.getNextID();
    }

    public int getCount() {
        return count;
    }

    public Integer getID() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }
}
