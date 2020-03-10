import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Visitor extends Thread {

    public static long time = System.currentTimeMillis();

    static AtomicBoolean isInSession = new AtomicBoolean();
    //  Clock tells visitor if the 4 sessions have been done.
    static AtomicBoolean theaterClose = new AtomicBoolean();

    //volatile static boolean sessionOver;
    volatile static Vector<Visitor> inTheater = new Vector<>();
    volatile static Vector<Visitor> waitingForTicket = new Vector<>();
    // Keeps tracks of their ids
    volatile static Vector<Visitor> visitorOrder = new Vector<>();
    // If the speaker gave a ticket or not.
    public AtomicBoolean gotTicket = new AtomicBoolean(false);
    public int ID;
    // Visitor brows around for a while, used as a bw.
    static AtomicBoolean isBrowsing = new AtomicBoolean(true);
    // Only if they saw a movie will it be true, if true  add them waitingForTicket
    // so speaker gives them a ticket.
    private boolean sawMovie;

    private int visitorId;
    volatile static AtomicInteger seats = new AtomicInteger();


    Visitor(int visotrId, AtomicInteger theaterCapacity, AtomicBoolean isInSession, Vector<Visitor> inTheater, Vector<Visitor> waitingForTicket, Vector<Visitor> visitOrder, AtomicBoolean theaterClose)
    {
        this.isInSession = isInSession;
        this.setName("visitorId " + visotrId);
        this.ID = visotrId;
        this.inTheater = inTheater;
        this.waitingForTicket = waitingForTicket;
        this.visitorOrder = visitOrder;
        this.theaterClose = theaterClose;
        this.seats = theaterCapacity;
    }
    @Override
    public void run() {
        // How many sessions
        while(!theaterClose.get())
        {

            while(isInSession.get()){}// bw, unitl the clock opens up the theater
;

            this.setPriority(this.getPriority()+1);

            try {
                Thread.currentThread().sleep(5000);
            }
            catch(InterruptedException e) { }

            this.setPriority(this.getPriority()-1);

            takeAseat(seats);

            // If they are in the theater (vector) have them sleep.
            // Sleep for a long time in order to be interrupted by speaker.
            // Else go to lobby
            if (inTheater.contains(this))
            {
                try {
                    Thread.currentThread().sleep(1000000);
                }
                catch(InterruptedException e)
                {
                    msg("leaves theater");
                }
                // Only visitors who saw a movie set this to true, in order to receive a ticket from speaker
                sawMovie = true;

                // Gives up a seat, therefore increment.
                seats.incrementAndGet();
                // Yields twice as in the story.
                this.yield();
                this.yield();
                // break, visitor can't return to the lobby.
                break;

            }
            else
                msg("returns to lobby and waits for next session");

            // Yields twice as in the story.
            this.yield();
            this.yield();
        }

        // If they saw a movie they group here to get a ticket from speaker
        if(sawMovie) {
            waitingForTicket.add(this);

            // bw, till they get the ticket.
            while (!gotTicket.get()) { }
            msg("got tickets");
        }


        // We let first one leave with out waiting for no one else
        if (ID==0) {
            isBrowsing.set(true);
        }

        //bw, as in story "browse around for a while and eventually leave the theater"
        while(isBrowsing.get()) {}

        // If is not visitor 0, then wait for the previous visitor then finish.
        // I believe this is where my bug is, I have written a statement for debugging.
        //Statement has been commented out.
        // I see most of the threads here, a few never shows up.
        if(ID>0) {
            if(visitorOrder.elementAt(ID-1).isAlive()) {
                try {
                    msg("is waiing for visitorID " + (ID-1));
                    visitorOrder.elementAt(ID-1).join();
                } catch (InterruptedException e) {}
            }
        }

        msg("leaves movie theater");
    }

    public static Vector<Visitor> getVector()
    {
        return inTheater;
    }

    public void setTicket()
    {
        this.gotTicket.set(true);
    }

    // Synchronized method, decrements seats and puts em in the inTheater vector.
    public synchronized void takeAseat(AtomicInteger seat)
    {
        synchronized (seats) {
            if(seats.get() > 0 )
            {
                seats.decrementAndGet();
                inTheater.addElement(this);;
                msg("Got a seat");
            }
        }
    }

    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"] "+getName()+": "+m);
    }
}// end of class