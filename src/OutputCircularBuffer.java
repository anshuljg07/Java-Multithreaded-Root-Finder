import org.javatuples.Triplet;

/**
 * The OutputCircular from Fig. 23.18: CircularBuffer.java is the output buffer of the multithreaded application and contains
 * the solved polnomial equations
 */
public class OutputCircularBuffer{
   /**
    * The buffer is compromised of an array of Triplet of Strings that are stored in a circular fashion
    */
   private final Triplet<String, String, String>[] buffer;
   /**
    * The occupiedCells represents the number of indices of the buffer that are filled by triplet
    */
   private int occupiedCells = 0; // count number of buffers used
   /**
    * The writeIndex represents the index that can be written to in a circular fashipn
    */
   private int writeIndex = 0; // index of next element to write to
   /**
    * The readIndex represents the index that can be read from in a circular fashion
    */
   private int readIndex = 0; // index of next element to read
   /**
    * The Boolean finished corresponds to whether or not it is possible to write to the buffer
    */
   private Boolean finished = false;
   /**
    * The int size stands for the size of the buffer.
    */
   private final int size;

   /**
    * The OutputCircularBuffer() constructor has parameter size and creates a Triplet array of parameter size and
    * initializes the size instance variable
    * @param size int size of the buffer
    */
   public OutputCircularBuffer(int size){
//      buffer = new ArrayList<Triplet<Integer, Integer, Integer>>();
      buffer = new Triplet[size];
      this.size = size;

   }

   /**
    * The getter method for buffer
    * @return buffer of Triplet of strings
    */
   public Triplet<String, String, String>[] getBuffer() {
      return buffer;
   }

   /**
    * The getter method for size
    * @return int size
    */
   public int getSize() {
      return size;
   }

   /**
    * The getter method for finished instance variable
    * @return Boolean finished variable
    */
   public Boolean getFinished() {
      return finished;
   }

   /**
    * The setter method for the finished instance variable
    * @param finished boolean finished
    */
   public void setFinished(Boolean finished) {
      this.finished = finished;
   }

   /**
    * The synchronized blockingPut() method takes a Triplet of String as a parameter and writes it to the buffer
    * using the write index. It then modifies the write index in a circular fashion.
    * @param value
    * @throws InterruptedException
    */
   public synchronized void blockingPut(Triplet<String, String, String> value) throws InterruptedException {
      while (occupiedCells == buffer.length && !finished) {
         wait(); // wait until a buffer cell is free
      }

      buffer[writeIndex] = value; // set new buffer value

      // update circular write index
      writeIndex = (writeIndex + 1) % buffer.length;

      ++occupiedCells; // one more buffer cell is full
      notifyAll(); // notify threads waiting to read from buffer
   }

   /**
    * The synchronized method blockingGet() returns a Triplet of Strings in a ciruclar fashion using the readIndex
    * and then sets the read index to the next circular index.
    * @return a Triplet of strings representing a polynomial
    * @throws InterruptedException throws an InterruptedException
    */
   public synchronized Triplet<String, String, String> blockingGet() throws InterruptedException {
      // wait until buffer has data, then read value;
      // while no data to read, place thread in waiting state
      while (occupiedCells == 0 && !finished) {
//         System.out.printf(" finished = " + finished + " .Output Buffer is empty. Consumer waits.%n");
         wait(); // wait until a buffer cell is filled
      }

      Triplet<String, String, String> readValue = buffer[readIndex]; // read value from buffer

      // update circular read index
      readIndex = (readIndex + 1) % buffer.length;

      --occupiedCells; // one fewer buffer cells are occupied
//      System.out.println(" finished = " + finished + " .OutputBuffer Get sucessfull ");
//      displayState("Consumer reads " + readValue);
      notifyAll(); // notify threads waiting to write to buffer

      return readValue;
   }
}


/**************************************************************************
 * (C) Copyright 1992-2015 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/