package ticketservice;

import java.util.Set;

/**
 * A {@link ConfirmedTicketService} provides the ability to look up which seats were reserved by a confirmation code
 * as returned by {@link #reserveSeats(int, String)}
 */
public interface ConfirmedTicketService extends TicketService {
    /**
     *
     * @param confirmationCode a confirmation code as given by {@link #reserveSeats(int, String)}
     * @return the seat numbers that were reserved when confirmationCode was issued, or the empty set if the
     * confirmation code was never issued.
     */
    Set<Integer> getReservedSeats(String confirmationCode);
}
