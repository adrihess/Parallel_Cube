package concurrentcube;



import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;


import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

public class CubeTest {
    private static class beforeShow implements Runnable {
        @Override
        public void run() {
            System.out.println("Before showing");
        }
    }

    private static class afterShow implements Runnable {
        @Override
        public void run() {
            System.out.println("After showing");
        }
    }

    @Test
    @DisplayName("Simple Test")
    static void testOne() throws InterruptedException {
        Cube basic = new Cube(3, (a,b) -> {}, (a,b) -> {}, () -> {}, () -> {});
        String expected = "111004004511511211220220220330330334444444555552552333";

        basic.rotate(1,2);
        basic.rotate(2,2);

        String res = basic.show();
        assert(res.equals(expected));

    }

    static boolean in_time_help(String res2[], int[][] count_tab) {
        for(int i=0; i<3; i++) {
            for(int j=0; j<res2[i].length(); j++) {
                if(res2[i].charAt(j) == '0') {
                    count_tab[i][0]++;
                }
                if(res2[i].charAt(j) == '1') {
                    count_tab[i][1]++;
                }
                if(res2[i].charAt(j) == '2') {
                    count_tab[i][2]++;
                }
                if(res2[i].charAt(j) == '3') {
                    count_tab[i][3]++;
                }
                if(res2[i].charAt(j) == '4') {
                    count_tab[i][4]++;
                }
                if(res2[i].charAt(j) == '5') {
                    count_tab[i][5]++;
                }
            }
        }
        for(int i=0; i<3; i++) {
            for(int j=0; j<5; j++) {
                if(count_tab[i][j] != count_tab[i][j+1]) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean end_helper(String res, int[] tab) {
        for(int i=0; i<res.length(); i++) {
            if(res.charAt(i) == '0') {
                tab[0]++;
            }
            if(res.charAt(i) == '1') {
                tab[1]++;
            }
            if(res.charAt(i) == '2') {
                tab[2]++;
            }
            if(res.charAt(i) == '3') {
                tab[3]++;
            }
            if(res.charAt(i) == '4') {
                tab[4]++;
            }
            if(res.charAt(i) == '5') {
                tab[5]++;
            }
        }
        for(int i=0; i<5; i++) {
            if(tab[i] != tab[i+1])
                return false;

            assert(tab[i] >=0 && tab[i] <=5);
        }
        return true;
    }
    @Test
    @DisplayName("Second Test")
    static void test_second() throws InterruptedException {
        Cube cube = new Cube(2, (a,b) -> {}, (a,b) -> {}, () -> {}, () -> {});
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int ILE_WATKOW = 100000;
        int ile_zrobic = 1;
        String res1;
        String[] res2 = new String[3];

        for(int ct=0; ct<ile_zrobic; ct++) {
            for (int i = 0; i < ILE_WATKOW; i++) {
                service.execute(new Thread(
                        () -> {
                            try {
                                Random nowy = new Random();
                                cube.rotate(nowy.nextInt(6), nowy.nextInt(Cube.size));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                ));

            }
        }

        for(int i=0; i<3; i++) {
            Thread.sleep(100);
            String temp = cube.show();
            res2[i] = temp;
            System.out.println(temp);
        }

        Thread.sleep(20000);
        service.shutdown();

        String res = cube.show();
        System.out.println(res);
        int[] tab = {0,0,0,0,0,0};
        int[][] count_tab = new int[3][];
        int[] tab2 = {0,0,0,0,0,0};
        int[] tab3 = {0,0,0,0,0,0};
        int[] tab4 = {0,0,0,0,0,0};
        count_tab[0] = tab2;
        count_tab[1] = tab3;
        count_tab[2] = tab4;

        boolean b1 = in_time_help(res2, count_tab);
        if(b1)
            System.out.println("In time show go OK");
        else
            System.out.println("In time goes WRONG");

        boolean b2 = end_helper(res, tab);
        if(b2)
            System.out.println("End result is OK");
        else
            System.out.println("End result is WRONG");
        assert(b1 && b2);
    }
    @Test
    @DisplayName("Big Cube Big rotationed Test")
    static boolean test_third() throws InterruptedException {
        Cube cube = new Cube(100, (a,b) -> {}, (a,b) -> {}, () -> {}, () -> {});
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int ILE_WATKOW = 1000000;

        String[] res2 = new String[3];


            for (int i = 0; i < ILE_WATKOW; i++) {
                service.execute(new Thread(
                        () -> {
                            try {
                                Random nowy = new Random();
                                cube.rotate(nowy.nextInt(6), nowy.nextInt(Cube.size));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                ));

            }


        for(int i=0; i<3; i++) {
            Thread.sleep(200);
            String temp = cube.show();
            res2[i] = temp;
            System.out.println(temp);
        }

        Thread.sleep(20000);
        service.shutdown();

        String res = cube.show();
        System.out.println(res);
        int[] tab = {0,0,0,0,0,0};
        int[][] count_tab = new int[3][];
        int[] tab2 = {0,0,0,0,0,0};
        int[] tab3 = {0,0,0,0,0,0};
        int[] tab4 = {0,0,0,0,0,0};
        count_tab[0] = tab2;
        count_tab[1] = tab3;
        count_tab[2] = tab4;


        boolean b1 = in_time_help(res2, count_tab);
        if(b1)
            System.out.println("In time show go OK");
        else
            System.out.println("In time goes WRONG");

        boolean b2 = end_helper(res, tab);
        if(b2)
            System.out.println("End result is OK");
        else
            System.out.println("End result is WRONG");
        return b1 && b2;
    }

    static boolean testOne(Cube basic) throws InterruptedException {
        String expected = "111004004511511211220220220330330334444444555552552333";

        basic.rotate(1,2);
        basic.rotate(2,2);

        String res = basic.show();
        return res.equals(expected);
    }

    static boolean test_second(Cube cube) throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int ILE_WATKOW = 100000;
        int ile_zrobic = 1;
        String res1;
        String[] res2 = new String[3];

        for(int ct=0; ct<ile_zrobic; ct++) {
            for (int i = 0; i < ILE_WATKOW; i++) {
                service.execute(new Thread(
                        () -> {
                            try {
                                Random nowy = new Random();
                                cube.rotate(nowy.nextInt(6), nowy.nextInt(Cube.size));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                ));

            }
        }

        for(int i=0; i<3; i++) {
            Thread.sleep(100);
            String temp = cube.show();
            res2[i] = temp;
            System.out.println(temp);
        }

        Thread.sleep(20000);
        service.shutdown();

        String res = cube.show();
        System.out.println(res);
        int[] tab = {0,0,0,0,0,0};
        int[][] count_tab = new int[3][];
        int[] tab2 = {0,0,0,0,0,0};
        int[] tab3 = {0,0,0,0,0,0};
        int[] tab4 = {0,0,0,0,0,0};
        count_tab[0] = tab2;
        count_tab[1] = tab3;
        count_tab[2] = tab4;

        boolean b1 = in_time_help(res2, count_tab);
        if(b1)
            System.out.println("In time show go OK");
        else
            System.out.println("In time goes WRONG");

        boolean b2 = end_helper(res, tab);
        if(b2)
            System.out.println("End result is OK");
        else
            System.out.println("End result is WRONG");
        return b1 && b2;
    }

    @Test
    @DisplayName("Interrupted Test")
    public void interruptedTest() throws InterruptedException {
        Cube cube = new Cube(12, (a,b) -> {}, (a,b) -> {}, () -> {}, () -> {});

        int how = 100;
        Thread[] th = new Thread[100];
        Thread[] th2 = new Thread[100];

        for(int i=0; i<how; i++) {
            th[i] = new Thread(() -> {
                try {
                    cube.rotate(4,4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            th2[i] = new Thread(() -> {
                try {
                    cube.rotate(1,7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        for(int i=0; i<how; i++) {
            th[i].start();
            th2[i].start();
            if(((i+1) % 15) == 0)
                th[i-1].interrupt();
        }
        for(int i=0; i<how; i++) {
            th[i].join();
            th2[i].join();
        }
        String res = cube.show();
        int[] tab = {0,0,0,0,0,0};
        boolean b = end_helper(res, tab);
        assert(b);
    }
    public static boolean interruptedTest(Cube cube) throws InterruptedException {


        int how = 100;
        Thread[] th = new Thread[100];
        Thread[] th2 = new Thread[100];

        for(int i=0; i<how; i++) {
            th[i] = new Thread(() -> {
                try {
                    cube.rotate(4,4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            th2[i] = new Thread(() -> {
                try {
                    cube.rotate(1,7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        for(int i=0; i<how; i++) {
            th[i].start();
            th2[i].start();
            if(((i+1) % 15) == 0) {
                th[i].interrupt();
            }
        }
        for(int i=0; i<how; i++) {
            th[i].join();
            th2[i].join();
        }
        String res = cube.show();
        int[] tab = {0,0,0,0,0,0};
        boolean b = end_helper(res, tab);
        return b;
    }


    @Test
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=========");

        BiConsumer<Integer, Integer> before =
                (a, b) -> System.out.println("Before rotation side: "+a+" layer: "+b);
        BiConsumer<Integer, Integer> after =
                (a, b) -> System.out.println("After rotation side: "+a+" layer: "+b);

        Cube basic = new Cube(3, (a,b) -> {}, (a,b) -> {}, () -> {}, () -> {});


        if(testOne(basic)) {
            System.out.println("Simple test succeed");
        } else {
            System.out.println("Simple test failed");
        }


            Cube small = new Cube(2, (a,b) -> {}, (a,b) -> {}, () -> {}, () -> {});

        if(test_second(small)) {
            System.out.println("Second test succeed");
        } else {
            System.out.println("Second test failed");
        }

        Cube medium = new Cube(12, (a,b) -> {}, (a,b) -> {}, () -> {}, () -> {});
        if(interruptedTest(medium)) {
            System.out.println("Interrupted test succeed");
        } else {
            System.out.println("Interrupted test failed");
        }


    }
}
