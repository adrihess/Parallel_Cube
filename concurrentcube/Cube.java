package concurrentcube;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;


public class Cube {

    static int size;
    volatile ArrayList<AtomicInteger[][]> warstwy;

    final AtomicInteger change = new AtomicInteger(1);
    volatile Semaphore change_mut = new Semaphore(1, true);


    volatile public AtomicInteger liczba = new AtomicInteger(0);

    BiConsumer<Integer, Integer> beforeRotation;
    BiConsumer<Integer, Integer> afterRotation;
    Runnable beforeShowing;
    Runnable afterShowing;

    static volatile AtomicInteger actual_group = new AtomicInteger(-1);
    static volatile Semaphore mutex_akt = new Semaphore(1,true);

    static final Integer G0Wait =1;
    static final Integer G1Wait =2;
    static final Integer G2Wait=3;

    static volatile AtomicInteger G0_waiters = new AtomicInteger(0);
    static volatile AtomicInteger G1_waiters = new AtomicInteger(0);
    static volatile AtomicInteger G2_waiters = new AtomicInteger(0);

    static final AtomicInteger now_doing_procc = new AtomicInteger(0);
    static final AtomicInteger possible_doing_procc = new AtomicInteger(0);
    volatile static Semaphore [] mut_G0;
    volatile static Semaphore [] mut_G1;
    volatile static Semaphore [] mut_G2;

    /**
     * Standard constructor of class Cube
     * @param size : size of cube
     * @param beforeRotation : action directly before rotation cube
     * @param afterRotation : action directly after rotation cube
     * @param beforeShowing : action directly before showing cube
     * @param afterShowing : action directly after showing cube
     */
    public Cube(int size, BiConsumer<Integer, Integer> beforeRotation,  BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,  Runnable afterShowing) {
        if(size == 0) {
            System.out.println("Nie ma takiej kostki!!!");
            System.exit(-1);
        }
        Cube.size = size;
        this.beforeRotation=beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing=beforeShowing;
        this.afterShowing=afterShowing;

        mut_G0 = new Semaphore[size];
        mut_G1 = new Semaphore[size];
        mut_G2 = new Semaphore[size];
        for(int i=0; i<size; i++) {
            mut_G0[i] = new Semaphore(1,true);
            mut_G1[i] = new Semaphore(1,true);
            mut_G2[i] = new Semaphore(1,true);
        }

        warstwy = new  ArrayList<>(6);
        AtomicInteger[][][] warstwa = new AtomicInteger[6][size][size];
        for(int i=0; i<6; i++) {

            for(int x=0; x<size; x++) {
                for(int y=0; y<size; y++) {
                    warstwa[i][x][y] = new AtomicInteger(i);
                }
            }
            warstwy.add(warstwa[i]);
        }

    }


    /**
     * Rotate cube in scheme: rotate $layer looking from side $side
     * @param side : side
     * @param layer : layer to rotate
     * @throws InterruptedException : thread could be interrupted
     */
    public void rotate(int side, int layer) throws InterruptedException {
        boolean total = false;
        int rotateSide=0;
        int group = Helper.group(side);

        if(Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }

        mutex_akt.acquireUninterruptibly();
        if(actual_group.get() == -1) {
            actual_group.set(group);

            now_doing_procc.incrementAndGet();
            possible_doing_procc.incrementAndGet();
            mutex_akt.release();
        }
        else if(actual_group.get() != group) {
            if(group == 0) {

                synchronized (G0Wait) {
                    G0_waiters.incrementAndGet();
                    mutex_akt.release();
                    G0Wait.wait();
                }

                now_doing_procc.incrementAndGet();
                possible_doing_procc.incrementAndGet();

                synchronized (G0Wait) {
                    G0_waiters.decrementAndGet();
                }

                if(Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

            }
            else if(group == 1) {


                synchronized (G1Wait) {
                    G1_waiters.incrementAndGet();
                    mutex_akt.release();
                    G1Wait.wait();
                }

                now_doing_procc.incrementAndGet();
                possible_doing_procc.incrementAndGet();

                synchronized (G1Wait) {
                    G1_waiters.decrementAndGet();
                }

                if(Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

            }
            else if(group == 2){


                synchronized (G2Wait) {
                    G2_waiters.incrementAndGet();
                    mutex_akt.release();
                    G2Wait.wait();
                }

                now_doing_procc.incrementAndGet();
                possible_doing_procc.incrementAndGet();

                synchronized (G2Wait) {
                    G2_waiters.decrementAndGet();
                }

                if(Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

            }
        } else {

            now_doing_procc.incrementAndGet();
            possible_doing_procc.incrementAndGet();
            mutex_akt.release();
        }


        int layer2 = (side < Helper.whichGroup(side, false)) ? layer : size-layer-1;

        Semaphore [] ref;
        if(group == 0) {
            ref = mut_G0;
        } else if(group == 1) {
            ref = mut_G1;
        } else {
            ref = mut_G2;
        }

        try {
            ref[layer2].acquire();
        } catch(InterruptedException exc) {
            now_doing_procc.decrementAndGet();
            possible_doing_procc.decrementAndGet();
            Helper.isNeedToNotify(group);
            throw new InterruptedException();
        }

        beforeRotation.accept(side,layer);

        if (layer == 0 || layer == size - 1) {
            total = true;
            rotateSide = Helper.whichGroup(side, layer == 0);
        }
        AtomicInteger[] toRotate = Helper.toRotate(side);

        if (total && layer == 0) {
            Helper.rotate_total(warstwy.get( rotateSide));
        } else if(total){
            Helper.rotate_total_1(warstwy.get( rotateSide));
        }

        if(side == 3)
            Helper.rotate_partial3(warstwy.get(toRotate[0].get()), warstwy.get(toRotate[1].get()), warstwy.get(toRotate[2].get()), warstwy.get(toRotate[3].get()), layer);
        else if(side == 4)
            Helper.rotate_partial4(warstwy.get(toRotate[0].get()), warstwy.get(toRotate[1].get()), warstwy.get(toRotate[2].get()), warstwy.get(toRotate[3].get()), layer);
        else if (side ==2)
            Helper.rotate_partial2(warstwy.get(toRotate[0].get()), warstwy.get(toRotate[1].get()), warstwy.get(toRotate[2].get()), warstwy.get(toRotate[3].get()), layer);
        else if (side == 5)
            Helper.rotate_partial5(warstwy.get(toRotate[0].get()), warstwy.get(toRotate[1].get()), warstwy.get(toRotate[2].get()), warstwy.get(toRotate[3].get()), layer);
        else if(side==0)
            Helper.rotate_partial0(warstwy.get(toRotate[0].get()), warstwy.get(toRotate[1].get()), warstwy.get(toRotate[2].get()), warstwy.get(toRotate[3].get()), layer);
        else if(side==1)
            Helper.rotate_partial1(warstwy.get(toRotate[0].get()), warstwy.get(toRotate[1].get()), warstwy.get(toRotate[2].get()), warstwy.get(toRotate[3].get()), layer);

        afterRotation.accept(side,layer);
        liczba.incrementAndGet();
        ref[layer2].release();

        if(change.get() == -1) {
            change_mut.acquireUninterruptibly();
            now_doing_procc.decrementAndGet();
            possible_doing_procc.decrementAndGet();

            Helper.isNeedToNotify(group);
            change_mut.release();

            return;
        }
        else {
            possible_doing_procc.decrementAndGet();
            mutex_akt.acquireUninterruptibly();

            now_doing_procc.decrementAndGet();
            if(now_doing_procc.get() > 0) {

                mutex_akt.release();
                return;
            }
        }

        if(now_doing_procc.get() == 0 && change.get() == 1) {

            change.set(-1);
            int turnover = 2;
            while (turnover > 0 && now_doing_procc.get() == 0) {

                group = (group + 1) % 3;
                AtomicInteger wsk;
                if(group == 0) wsk = G0_waiters;
                else if(group == 1) wsk = G1_waiters;
                else wsk = G2_waiters;

                if (wsk.get() > 0) {
                    actual_group.set(group);
                    if(group == 0) {
                        synchronized (G0Wait) {
                            G0Wait.notifyAll();
                            G0Wait.wait();
                        }
                    } else if(group == 1) {
                        synchronized (G1Wait) {
                            G1Wait.notifyAll();
                            G1Wait.wait();
                        }
                    } else {
                        synchronized (G2Wait) {
                            G2Wait.notifyAll();
                            G2Wait.wait();
                        }
                    }
                }
                turnover--;
            }

            actual_group.set(-1);
            change.set(1);
            mutex_akt.release();
        }

    }

    /**
     * Return state of cube
     * @return : String contains description of cube state
     * @throws InterruptedException : this action can be interrupted
     */
    public String show() throws InterruptedException {

        mutex_akt.acquire();

        while (possible_doing_procc.get() != 0) {
            Thread.sleep(1);
        }

        if(Thread.currentThread().isInterrupted()) {
            mutex_akt.release();
            throw new InterruptedException();
        }

        beforeShowing.run();

        StringBuilder res = new StringBuilder();
        for (AtomicInteger[][] integers : warstwy) {
            for(int x=0; x<size; x++) {
                for(int y=0; y<size; y++){
                    int c = integers[x][y].get();
                    res.append(c);
                }
            }
        }

        afterShowing.run();

        mutex_akt.release();

        return res.toString();
    }
}
