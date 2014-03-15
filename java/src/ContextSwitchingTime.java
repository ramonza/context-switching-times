/**
 *
 */
public class ContextSwitchingTime {

    volatile boolean done = false;
    Actor[] actors;
    long lastCounter = -1;

    class Actor extends Thread {

        int index;
        long counter = -1;

        Actor(int index ) { this.index = index; }

        @Override
        public void run() {
            while (!done) {
                long nextCounter;
                synchronized (this) {
                    while (counter == -1 && !done) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                    }
                    if (done) {
                        return;
                    }
                    nextCounter = counter + 1;
                    counter = -1;
                    lastCounter = nextCounter;
                }
                actors[(index + 1) % actors.length].send(nextCounter);
            }
        }

        public synchronized void send(long counter) {
            this.counter = counter;
            notify();
        }
    }

    public static void main(String[] args) throws Exception {
        new ContextSwitchingTime().run();
    }

    private  void run() throws InterruptedException {
        actors = new Actor[100];
        for (int i = 0; i < actors.length; i++) {
            actors[i] = new Actor(i);
        }
        long start = System.nanoTime();
        for (Actor actor : actors) {
            actor.start();
        }
        actors[0].send(0);
        Thread.sleep(1000);
        done = true;
        for (Actor actor : actors) {
            actor.interrupt();
            actor.join();
        }
        long duration  = System.nanoTime() - start;
        System.out.println(lastCounter / (duration / 1E9));
    }
}
