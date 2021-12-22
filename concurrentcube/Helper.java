package concurrentcube;

import java.util.concurrent.atomic.AtomicInteger;

public class Helper {
    /**
     * Rotate side clockwise
     * @param tab : side
     */
    static void rotate_total(AtomicInteger[][] tab) {

        int[][] res = new int[Cube.size][Cube.size];
        for (int i = 0; i < Cube.size; i++) {
            for (int j = 0; j < Cube.size; j++)
                res[i][j] = 0;
        }

        for (int i = 0; i < Cube.size; i++) {
            for (int j = 0; j < Cube.size; j++) {
                synchronized (tab[i][j]) {
                    res[j][Cube.size - i - 1] = tab[i][j].get();
                }}
        }
        for (int i = 0; i < Cube.size; i++) {
            for (int j = 0; j < Cube.size; j++) {
                synchronized (tab[i][j]) {
                    tab[i][j].set(res[i][j]);}
            }
        }
    }

    /**
     * Rotate side anti-clockwise
     * @param tab : side
     */
    static void rotate_total_1(AtomicInteger [][] tab) {

        int[][] res = new int[Cube.size][Cube.size];
        for (int i = 0; i < Cube.size; i++) {
            for (int j = 0; j < Cube.size; j++)
                res[i][j] = 0;
        }

        for (int i = 0; i < Cube.size; i++) {
            for (int j = 0; j < Cube.size; j++) {
                synchronized (tab[i][j]) {
                    res[Cube.size - j - 1][i] = tab[i][j].get();
                }}
        }
        for (int i = 0; i < Cube.size; i++) {
            for (int j = 0; j < Cube.size; j++) {
                synchronized (tab[i][j]) {
                    tab[i][j].set(res[i][j]);}
            }
        }
    }

    /**
     * Return sides to rotate it in properly order
     * @param toTurn : side
     * @return : ordering of side to rotate
     */
    static AtomicInteger[] toRotate(Integer toTurn) {
        assert(toTurn >= 0 && toTurn <= 5);
        AtomicInteger[] res = new AtomicInteger[4];
        for (int i=0; i<4; i++) {
            res[i] = new AtomicInteger(0);
        }
        if(toTurn == 1) {
            res[0].set(0);
            res[1].set(2);
            res[2].set(5);
            res[3].set(4);
        }
        if(toTurn == 3) {
            res[0].set(0);
            res[1].set(4);
            res[2].set(5);
            res[3].set(2);
        }

        if(toTurn == 2) {
            res[0].set(0);
            res[1].set(3);
            res[2].set(5);
            res[3].set(1);
        }
        if(toTurn == 4) {
            res[0].set(0);
            res[1].set(1);
            res[2].set(5);
            res[3].set(3);
        }
        if( toTurn == 5) {
            res[0].set(2);
            res[1].set(3);
            res[2].set(4);
            res[3].set(1);
        }
        if(toTurn == 0) {
            res[0].set(2);
            res[1].set(3);
            res[2].set(4);
            res[3].set(1);
        }

        return res;
    }

    /**
     * Rotate from side number 2
     * @param A : first
     * @param B : second
     * @param C : third
     * @param D : fourth
     * @param numer : layer
     */
    static void rotate_partial2(AtomicInteger[][] A, AtomicInteger[][] B, AtomicInteger [][] C, AtomicInteger [][] D, int numer) {
        int[] temp = new int[Cube.size];
        for (int i=0; i<Cube.size; i++)
            temp[i]=0;


        for(int i=0; i<Cube.size; i++) {
            synchronized (A[Cube.size-numer-1][i]) {
                temp[i] = A[Cube.size-numer-1][i].get();
                synchronized (D[i][Cube.size-numer-1]) {
                    A[Cube.size-numer-1][i].set(D[i][Cube.size-numer-1].get());}}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (B[i][numer]) {
                temp[i] = temp[i] + B[i][numer].get();
                B[i][numer].set(temp[i] - B[i][numer].get());
                temp[i] = temp[i] - B[i][numer].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (C[numer][i]) {
                temp[i] = temp[i] + C[numer][i].get();
                C[numer][i].set( temp[i] - C[numer][i].get());
                temp[i] = temp[i] - C[numer][i].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (D[i][Cube.size-numer-1]){
                temp[i] = temp[i] + D[i][Cube.size-numer-1].get();
                D[i][Cube.size-numer-1].set( temp[i] - D[i][Cube.size-numer-1].get());
                temp[i] = temp[i] - D[i][Cube.size-numer-1].get();}
        }
    }

    /**
     * Rotate from side number 1
     * @param A : first
     * @param B : second
     * @param C : third
     * @param D : fourth
     * @param numer : layer
     */
    static void rotate_partial1(AtomicInteger[][] A, AtomicInteger[][] B, AtomicInteger [][] C, AtomicInteger [][] D, int numer) {
        int[] temp = new int[Cube.size];
        for (int i=0; i<Cube.size; i++)
            temp[i]=0;


        for(int i=0; i<Cube.size; i++) {
            synchronized (A[i][numer]){
                temp[i] = A[i][numer].get();
                synchronized (D[i][Cube.size-numer-1]){
                    A[i][numer].set(D[i][Cube.size-numer-1].get());}}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (B[i][numer]){
                temp[i] = temp[i] + B[i][numer].get();
                B[i][numer].set(temp[i] - B[i][numer].get());
                temp[i] = temp[i] - B[i][numer].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (C[i][numer]){
                temp[i] = temp[i] + C[i][numer].get();
                C[i][numer].set(temp[i] - C[i][numer].get());
                temp[i] = temp[i] - C[i][numer].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (D[i][Cube.size-numer-1]){
                temp[i] = temp[i] + D[i][Cube.size-numer-1].get();
                D[i][Cube.size-numer-1].set(temp[i] - D[i][Cube.size-numer-1].get());
                temp[i] = temp[i] - D[i][Cube.size-numer-1].get();}
        }
    }

    /**
     * Rotate from side number 3
     * @param A : first
     * @param B : second
     * @param C : third
     * @param D : fourth
     * @param numer : layer
     */
    static void rotate_partial3(AtomicInteger[][] A, AtomicInteger[][] B, AtomicInteger [][] C, AtomicInteger [][] D, int numer) {
        int[] temp = new int[Cube.size];
        for (int i=0; i<Cube.size; i++)
            temp[i]=0;


        for(int i=0; i<Cube.size; i++) {
            synchronized (A[i][Cube.size-numer-1]){
                temp[i] = A[i][Cube.size-numer-1].get();
                synchronized (D[i][Cube.size-numer-1]) {
                    A[i][Cube.size-numer-1].set(D[i][Cube.size-numer-1].get());}}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (B[Cube.size-i-1][numer]){
                temp[i] = temp[i] + B[Cube.size-i-1][numer].get();
                B[Cube.size-i-1][numer].set(temp[i] - B[Cube.size-i-1][numer].get());
                temp[i] = temp[i] - B[Cube.size-i-1][numer].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (C[i][Cube.size-numer-1]){
                temp[i] = temp[i] + C[i][Cube.size-numer-1].get();
                C[i][Cube.size-numer-1].set(temp[i] - C[i][Cube.size-numer-1].get());
                temp[i] = temp[i] - C[i][Cube.size-numer-1].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (D[i][Cube.size-numer-1]){
                temp[i] = temp[i] + D[i][Cube.size-numer-1].get();
                D[i][Cube.size-numer-1].set(temp[i] - D[i][Cube.size-numer-1].get());
                temp[i] = temp[i] - D[i][Cube.size-numer-1].get();}
        }
    }

    /**
     * Rotate from side number 5
     * @param A : first
     * @param B : second
     * @param C : third
     * @param D : fourth
     * @param numer : layer
     */
    static void rotate_partial5(AtomicInteger[][] A, AtomicInteger[][] B, AtomicInteger [][] C, AtomicInteger [][] D, int numer) {
        int[] temp = new int[Cube.size];
        for (int i=0; i<Cube.size; i++)
            temp[i]=0;

        for(int i=0; i<Cube.size; i++) {
            synchronized (A[Cube.size-numer-1][i]) {
                temp[i] = A[Cube.size-numer-1][i].get();
                synchronized (D[Cube.size-numer-1][i]) {
                    A[Cube.size-numer-1][i].set(D[Cube.size-numer-1][i].get());}}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (B[Cube.size-numer-1][i]){
                temp[i] = temp[i] + B[Cube.size-numer-1][i].get();
                B[Cube.size-numer-1][i].set(temp[i] - B[Cube.size-numer-1][i].get());
                temp[i] = temp[i] - B[Cube.size-numer-1][i].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (C[Cube.size-numer-1][i]){
                temp[i] = temp[i] + C[Cube.size-numer-1][i].get();
                C[Cube.size-numer-1][i].set(temp[i] - C[Cube.size-numer-1][i].get());
                temp[i] = temp[i] - C[Cube.size-numer-1][i].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (D[Cube.size-numer-1][i]){
                temp[i] = temp[i] + D[Cube.size-numer-1][i].get();
                D[Cube.size-numer-1][i].set(temp[i] - D[Cube.size-numer-1][i].get());
                temp[i] = temp[i] - D[Cube.size-numer-1][i].get();}
        }
    }

    /**
     * Rotate from side number 0
     * @param A : first
     * @param B : second
     * @param C : third
     * @param D : fourth
     * @param numer : layer
     */
    static void rotate_partial0(AtomicInteger[][] A, AtomicInteger[][] B, AtomicInteger [][] C, AtomicInteger [][] D, int numer) {
        int[] temp = new int[Cube.size];
        for (int i=0; i<Cube.size; i++)
            temp[i]=0;

        for(int i=0; i<Cube.size; i++) {
            synchronized ( A[numer][i]){
                temp[i] = A[numer][i].get();
                synchronized (D[numer][i]) {
                    A[numer][i].set(D[numer][i].get());}}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (B[numer][i]){
                temp[i] = temp[i] + B[numer][i].get();
                B[numer][i].set(temp[i] - B[numer][i].get());
                temp[i] = temp[i] - B[numer][i].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized ( C[numer][i]){
                temp[i] = temp[i] + C[numer][i].get();
                C[numer][i].set(temp[i] - C[numer][i].get());
                temp[i] = temp[i] - C[numer][i].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized ( D[numer][i]){
                temp[i] = temp[i] + D[numer][i].get();
                D[numer][i].set(temp[i] - D[numer][i].get());
                temp[i] = temp[i] - D[numer][i].get();}
        }
    }

    /**
     * Rotate from side number 4
     * @param A : first
     * @param B : second
     * @param C : third
     * @param D : fourth
     * @param numer : layer
     */
    static void rotate_partial4(AtomicInteger[][] A, AtomicInteger[][] B, AtomicInteger [][] C, AtomicInteger [][] D, int numer) {
        int[] temp = new int[Cube.size];
        for (int i=0; i<Cube.size; i++)
            temp[i]=0;

        for(int i=0; i<Cube.size; i++) {
            synchronized (A[numer][Cube.size-i-1]){
                temp[i] = A[numer][Cube.size-i-1].get();
                synchronized (D[i][Cube.size-i-1]){
                    A[numer][Cube.size-i-1].set(D[Cube.size-i-1][Cube.size-numer-1].get());}}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized (B[i][numer]){
                temp[i] = temp[i] + B[i][numer].get();
                B[i][numer].set(temp[i] - B[i][numer].get());
                temp[i] = temp[i] - B[i][numer].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized ( C[Cube.size-numer-1][i]){
                temp[i] = temp[i] + C[Cube.size-numer-1][i].get();
                C[Cube.size-numer-1][i].set(temp[i] - C[Cube.size-numer-1][i].get());
                temp[i] = temp[i] - C[Cube.size-numer-1][i].get();}
        }
        for(int i=0; i<Cube.size; i++) {
            synchronized ( D[Cube.size-i-1][Cube.size-numer-1]){
                temp[i] = temp[i] + D[Cube.size-i-1][Cube.size-numer-1].get();
                D[Cube.size-i-1][Cube.size-numer-1].set(temp[i] - D[Cube.size-i-1][Cube.size-numer-1].get());
                temp[i] = temp[i] - D[Cube.size-i-1][Cube.size-numer-1].get();}
        }
    }

    /**
     * Return side if same, else return opposite sade
     * @param side : side
     * @param same : if same
     * @return : side or opposite side
     */
    static int whichGroup(int side, boolean same) {
        if(side == 0) {
            return same ? 0 : 5;
        }
        if(side == 1) {
            return same ? 1 : 3;
        }
        if(side == 2) {
            return same ? 2 : 4;
        }
        if(side == 3) {
            return same ? 3 : 1;
        }
        if(side == 4) {
            return same ? 4 : 2;
        }
        else {
            return same ? 5 : 0;
        }
    }

    /**
     * Return group of side
     * @param a : side
     * @return : group
     */
    static int group(Integer a) {
        if(a == 0 || a == 5)
            return 0;
        if(a == 1 || a == 3)
            return 1;
        else
            return 2;
    }

    /**
     * Check if notify od waiting thread is needed, if yes notify it
     * @param grupa : group of thread calling this method
     */
    public static void isNeedToNotify(int grupa) {
        if(grupa == 0) {
            if(Cube.G0_waiters.get() == 0 && Cube.now_doing_procc.get() == 0){
                synchronized (Cube.G0Wait) {
                    Cube.G0Wait.notify();
                }}
        } else if(grupa == 1) {
            if(Cube.G1_waiters.get() == 0 && Cube.now_doing_procc.get() == 0){
                synchronized (Cube.G1Wait) {
                    Cube.G1Wait.notify();
                }}
        } else {
            if(Cube.G2_waiters.get() == 0 && Cube.now_doing_procc.get() == 0){
                synchronized (Cube.G2Wait) {
                    Cube.G2Wait.notify();
                }}
        }
    }
}
