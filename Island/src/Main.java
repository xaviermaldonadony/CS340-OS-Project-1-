import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    // Shared between visitor and clock, to signal when movie is playing.
    final static AtomicBoolean isInSession = new AtomicBoolean(true);
    // Shared between clock and speaker, clock signals speaker when movie is over.
    final static AtomicBoolean sessionOver = new AtomicBoolean();
    // Shared between clock and speaker, speaker signals clock when theater is empty.
    final static AtomicBoolean theaterEmpty = new AtomicBoolean();
    // Shared between visitor and clock, clock signals visitor the theater is closed after 4 movies have been played.
    final static AtomicBoolean theaterClose = new AtomicBoolean(false);
    // Shared between visitor and speaker. Vector that holds five seats for the five people who got a seat in the theater.
    volatile static Vector<Visitor> inTheater = new Vector<>();
    // Shared between visitor and speaker. Vector holds threads that are waiting to be served by speaker.
    volatile static Vector<Visitor> waitingForTicket = new Vector<>();

    volatile static Vector<Visitor> visitorOrder = new Vector<>();
    // Size of group that speaker will serve tickets to.
    static int party_size = 3;
    // Capacity of seats in theater.
    static AtomicInteger theaterCapacity = new AtomicInteger(5);

    public static void main (String [] args)
    {
        for (int i = 0; i < 23; i++) {
            Visitor visitor = new Visitor(i, theaterCapacity, isInSession,  inTheater, waitingForTicket, visitorOrder, theaterClose);
            // Adding them to a vector in order to release them from the theter.
            visitorOrder.addElement(visitor);
            visitor.start();
        }
        Thread clock = new Thread(new Clock(isInSession, sessionOver, theaterEmpty, theaterClose));
        clock.start();
        Thread  speaker = new Thread(new Speaker(party_size, sessionOver, inTheater, theaterEmpty, waitingForTicket));
        speaker.start();
    }

}// end of class



