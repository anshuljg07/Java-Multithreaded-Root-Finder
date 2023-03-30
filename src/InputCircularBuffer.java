import org.javatuples.Triplet;

// Fig. 23.18: CircularBuffer.java
// Synchronizing access to a shared three-element bounded buffer.

/**
 * The class InputCircularBuffer stores Triplet of Integer array buffer and inputs and outputs and deletes in a circular fashion
 */
public class InputCircularBuffer{
   /**
    * The instance variable array of Triplets of Integer objects that acts as the buffer representation
    */
   private final Triplet<Integer, Integer, Integer>[] buffer; // shared buffer

   /**
    * The occupiedCells is the bumber of cells that are being currently used up
    */
   private int occupiedCells = 0; // count number of buffers used
   /**
    * The write index corresponds to the next index of the buffer that can be written to
    */
   private int writeIndex = 0; // index of next element to write to
   /**
    * The read index of the next element to write to the buffer
    */
   private int readIndex = 0; // index of next element to read

   /**
    * The Boolean finished states whether or not writing to the buffer is allowed
    */
   private Boolean finished = false;

   /**
    * The InputCircularBuffer() constructor creates a new Triplet Array of the parameter size
    * @param size is the int size of the Array to be created
    */

   public InputCircularBuffer(int size){
      buffer = new Triplet[size];
   }

   /**
    * It is a getter method for the finished instance variable
    * @return
    */
   public Boolean getFinished() {
      return finished;
   }

   /**
    * The setter method for the finished instance variable
    * @param finished it is the boolean parameter to set the instance variable
    */
   public void setFinished(Boolean finished) {
      this.finished = finished;
   }

   /**
    * The blockingPut() method is synchronized and writes to the buffer in a circular fashion using the writeIndex and then
    * changes the writeIndex to the next circular position.
    * @param value is a Triplet of integers that will be written to the buffer
    * @throws InterruptedException this is thrown by the method
    */
   public synchronized void blockingPut(Triplet<Integer, Integer, Integer> value)
      throws InterruptedException {
      // wait until buffer has space available, then write value; 
      // while no empty locations, place thread in blocked state  
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
    * The method blockingGet() is synchronized and pulls the relevant triplet using the readIndex in a circular fashion
    * @return returns the readValue which is a Triplet of integers
    * @throws InterruptedException
    */
   public synchronized Triplet<Integer, Integer, Integer> blockingGet() throws InterruptedException {
      // wait until buffer has data, then read value;
      // while no data to read, place thread in waiting state
      while (occupiedCells == 0 && !finished) {
         wait(); // wait until a buffer cell is filled
      } 

      Triplet<Integer, Integer, Integer> readValue = buffer[readIndex]; // read value from buffer

      // update circular read index
      readIndex = (readIndex + 1) % buffer.length;

      --occupiedCells; // one fewer buffer cells are occupied
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