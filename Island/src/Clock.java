import java.util.concurrent.atomic.AtomicBoolean;

public class Clock implements Runnable  {

    public static long time = System.currentTimeMillis();
    // Intialized to one, set for the first movie
    private int session = 1;
    static AtomicBoolean isInSession = new AtomicBoolean();
    // Tells when a movie is over.
    static AtomicBoolean sessionOver = new AtomicBoolean();
    // Tells speaker theater is empty.
    static AtomicBoolean theaterEmpty = new AtomicBoolean();
    // After the 4 sessions theater is close. It should let all the visitors out.
    static AtomicBoolean theaterClose = new AtomicBoolean();



    public Clock(AtomicBoolean isInSession, AtomicBoolean sessionOver, AtomicBoolean theaterEmpty, AtomicBoolean theaterClose)
    {
        this.isInSession = isInSession;
        this.sessionOver = sessionOver;
        this.theaterEmpty = theaterEmpty;
        this.theaterClose = theaterClose;

    }

    @Override
    public void run() {
        while(session < 5)
        {

            try {
                Thread.currentThread().sleep(1000);
            }
            catch (InterruptedException e) {            }


            isInSession.set(false);
            msg("Theater is open");

            try {
                Thread.currentThread().sleep(5000);
            }
            catch (InterruptedException e) { }

            isInSession.set(true);

            msg("Movie " + session + " is in session");

            // Movie is playing
            sessionOver.set(false);

            try {
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
            }

            // speaker will be able to get out of bw
            theaterEmpty.set(false);
            // Movie is over
            sessionOver.set(true);

            msg("Movie " + session + " is finished ");

            session++;
            //  We bw, waiting for the speaker to empty the theater.
            while (!theaterEmpty.get()) {}


        }

        msg("closes the movie theater");

        theaterClose.set(true);


    }// end of run

    public void msg(String m) {
        System.out.println("["+(System.currentTimeMillis()-time)+"]" +" Clock: "+  m);
    }

}// end of class
