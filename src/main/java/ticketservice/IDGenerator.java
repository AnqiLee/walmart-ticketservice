package ticketservice;

class IDGenerator {
    private int nextID = Integer.MIN_VALUE;
    public synchronized int getNextID() {
        return nextID++;
    }
}
