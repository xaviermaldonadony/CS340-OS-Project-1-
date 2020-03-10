import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Speaker implements Runnable{
    public static long time = System.currentTimeMillis();
    volatile static Vector<Visitor> inTheater = new Vector<>();
    static AtomicBoolean  sessionOver = new AtomicBoolean();
    static AtomicBoolean theaterEmpty = new AtomicBoolean();
    volatile static Vector<Visitor> waitingForTicket = new Vector<>();
    static int party_size;

    Speaker(int party_size,AtomicBoolean sessionOver, Vector<Visitor> inTheater, AtomicBoolean theaterEmpty, Vector<Visitor> waitingForTicket)
    {
        this.sessionOver = sessionOver;
        this.inTheater = inTheater;
        this.theaterEmpty = theaterEmpty;
        this.waitingForTicket = waitingForTicket;
        this.party_size = party_size;
    }

    @Override
    public void run() {
        while(true)
        {
            // If movie is over signal visitors to leave the theare and tell clock the theater is empty.
            if(sessionOver.get())
            {

                try {


                    for (int i = inTheater.size()-1; i >= 0; i--)
                    {
                        // Wake up thread, interrupt
                        inTheater.get(i).interrupt();
                        // Remove thread from vector (theater)
                        inTheater.remove(i);
                    }
                    theaterEmpty.set(true);
                    msg("Escorts visitors from theater");
                } catch (NullPointerException e) { }


            }
            // When a group is of a size of 3 give em a ticket
            while (waitingForTicket.size()>=3)
            {
                for (int i = party_size-1; i>=0; i--) {
                    waitingForTicket.elementAt(i).setTicket();
                    waitingForTicket.removeElementAt(i);
                }
                msg("Gives a group tickets");

            }

            while (theaterEmpty.get()) { } // Speaker bw
        }
    }

    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"]" +" Speaker: "+  m);
    }
}
