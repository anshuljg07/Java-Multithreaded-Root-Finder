import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The class RootFinder serves as the polynomial generator that based on user input either generates 30 or 3000 polynomials.
 * It then instantiates 10 slave threads which concurrently solve the polynomials it generates as it produces them and adds
 * them to an input buffer
 */
public class RootFinder {
    /**
     * The main method takes user input as to the number of polynomials generated and instantiates the input buffer, output
     * buffers, and the 10 slave threads. It then adds the polynomials generated and gets the solved polynomials as a thread.
     * It then outputs the result of the solution to the console.
     * @param args the arguments provided to main
     */
    public static void main(String[] args) {

        Scanner userIn = new Scanner(System.in);
        int size;
        System.out.println("30 or 3000 polynomial solver? (Enter '30' or '3000'): ");
        size = Integer.parseInt(userIn.nextLine());

        ExecutorService executorService = Executors.newCachedThreadPool();
        InputCircularBuffer inputbuffer = new InputCircularBuffer(10);
        OutputCircularBuffer outputbuffer = new OutputCircularBuffer(size);
        ArrayList<Triplet<String, String, String>> summary = new ArrayList<>();
        Slave[] Slaves = new Slave[10];


        for(int i = 0; i < 10; i++){
            Slaves[i] = new Slave(inputbuffer, outputbuffer);
            executorService.execute(Slaves[i]);
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Triplet<Integer, Integer, Integer> abc;
                for (int i = 0; i < size; i++) {
                    abc = new Triplet<>((int) (Math.random() * 22 - 11), (int) (Math.random() * 22 - 11), (int) (Math.random() * 22 - 11));
                    try {
                        inputbuffer.blockingPut(abc);
                        Triplet<String, String, String> solvedPoly = outputbuffer.blockingGet();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                inputbuffer.setFinished(true);
                outputbuffer.setFinished(true);
            }
        });

        executorService.shutdown();
        try{
            Thread.sleep(3000);
            if(size == 30){
                Triplet<String, String, String>[] temp = outputbuffer.getBuffer();
                for (int i = 0; i < outputbuffer.getSize(); i++){
                    System.out.println(temp[i].getValue0() + "\t" + temp[i].getValue1() + "\t" + temp[i].getValue2());
                }
                System.out.println("summary done");
                System.exit(0);
            }
            else{
                System.out.println(size + " quadratic equations solved");
                for(int i = 0; i < 10; i++){
                    System.out.println("Slave " + i + " solved "  + Slaves[i].getNumSolved());
                }
                System.exit(0);
            }
        }
        catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }
    }
}
