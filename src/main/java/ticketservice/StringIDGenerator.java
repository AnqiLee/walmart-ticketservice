/*
 * Copyright (c) 2017 Alexander ter Weele
 */

package ticketservice;

public class StringIDGenerator extends IDGenerator<String> {
    @Override
    String makeID(int i) {
        return Integer.toHexString(i);
    }
}
