`TicketService`
==

Wal-Mart Labs coding challenge.

Testing and Building
==

Test with:
``` shellsession
$ ./gradlew test
```

Build a JAR with
``` shellsession
$ ./gradlew jar
...
$ ls -1 build/libs
ticketservice-1.0-SNAPSHOT.jar
```

Assumptions
==

* A `SeatHold` is a promise that _n_ seats are held for the given
  customer. If the hold is reserved, then the _n_ best seats available
  are reserved at that time. This is to fill the requirement that the
  best seats are held for the customer.
* The semantics of when one seat is better than another are not
  specified, nor does `TicketService` contain a mechanism by which to
  use confirmation codes to look up what seats have been reserved. To
  address this, I have extended `TicketService` to
  `ConfirmedTicketService`. I have used this to test that earlier
  reservations have better seats. I represent the seats of the venue
  as integers, with lower numbers corresponding to better seats.
* Methods in `TicketService` do not specify desired behavior for
  failure modes. I have chosen to return `null` from
  `findAndHoldSeats` when the specified number of seats cannot be
  reserved. `findAndHoldSeats` can also throw an
  `IllegalArgumentException` if an invalid number of seats (fewer than
  one) are requested. My implementation of `reserveSeats` returns
  `null` when the given hold cannot be found or has expired. It throws
  `IllegalArgumentException` when the given hold ID corresponds to a
  `SeatHold` whose e-mail address differs from the provided one.
* One customer is _not_ prevented from making an arbitrary number of
  holds.
* A `SeatHold` is considered expired if and only if its expiration
  time is not in the future.

Lack of License
==

This software is not licensed. Do not distribute.
