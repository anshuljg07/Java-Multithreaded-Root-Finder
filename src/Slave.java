import org.javatuples.Triplet;

/**
 * The Slave class implements Runnable and is a representation of a thread based polynomial solver. It takes the polynomials
 *  generated by the RootFinder class from the inputBuffer, solves them by calculating the roots and then adds them to the output buffer
 */
public class Slave implements Runnable{

    /**
     * The inputCircularBuffer inputBuffer contains the a, b, c triplets of the unsolved polynomials generated in the
     * rootfinder class.
     */
    private final InputCircularBuffer inputBuffer;
    /**
     * The OutputCircularBuffer outputBuffer contains the solutions triplets of strings of the solved polynomials in the input
     * buffer.
     */
    private final OutputCircularBuffer outputBuffer;

    /**
     * The numSolved represents the number of polynomials solved by the Slave threads
     */
    private int numSolved;

    /**
     * The Slave() constructor takes parameters InputCircularBuffer and OutputCircularBuffer and are then initialized to the inputBuffer
     * and outputBuffer.
     * @param inputBuffer the input buffer
     * @param outputBuffer the output buffer
     */
    public Slave(InputCircularBuffer inputBuffer, OutputCircularBuffer outputBuffer){
        this.inputBuffer = inputBuffer;
        this.outputBuffer = outputBuffer;
        numSolved = 0;
    }

    /**
     * The getNumSolved() method is a getter for the numSolved instance variable
     * @return
     */
    public int getNumSolved(){
        return numSolved;
    }

    /**
     * The run method is an overriden method of the Runnable class which takes a polynomial from the input buffer and
     * calculates it roots both real and imaginary and then appends it to the output buffer
     */
    @Override
    public void run() {
        while (!inputBuffer.getFinished()) {
            try {
                Triplet<Integer, Integer, Integer> polynomial = inputBuffer.blockingGet();
                double a = polynomial.getValue0();
                double b = polynomial.getValue1();
                double c = polynomial.getValue2();
                double root1, root2;
                String strpoly = String.format("%.1fx^2 + %.1fx + %.1f", a, b, c);
                numSolved++;

                double discriminant = b * b - 4.0 * a * c;
                if (discriminant > 0.0) {
                    root1 = (-b + Math.sqrt(discriminant)) / (2.0 * a);
                    root2 = (-b - Math.pow(discriminant, 0.5)) / (2.0 * a);
                    outputBuffer.blockingPut(new Triplet<>(strpoly, String.valueOf(root1), String.valueOf(root2)));
//                    System.out.println("Quadratic:" + strpoly + "\nRoots: " + String.valueOf(root1) + " and " + String.valueOf(root2) + "\n");
                } else if (discriminant == 0.0) {
                    root1 = -b / (2.0 * a);
                    root2 = root1;
                    outputBuffer.blockingPut(new Triplet<>(strpoly, String.valueOf(root1), String.valueOf(root2)));
//                    System.out.println("Quadratic:" + strpoly + "\nRoots: " + String.valueOf(root1) + " and " + String.valueOf(root2) + "\n");
                } else { // what to do when it is imaginary
                    root1 = -b / (2 * a); // real part
                    root2 = Math.sqrt(-discriminant) / (2 * a); // imaginary part
//                    System.out.println("imaginary roots: " + String.valueOf(root1) + String.valueOf(root2));
                    outputBuffer.blockingPut(new Triplet<>(strpoly, String.valueOf(root1), String.valueOf(root2) + "i"));
//                    System.out.println("Quadratic:" + strpoly + "\nRoots: " + String.valueOf(root1) + " and " + String.valueOf(root2) + "i\n");
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
