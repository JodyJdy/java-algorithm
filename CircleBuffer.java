  public  class CircleBuffer{
        private final int[] buffer;
        private int writePos = 0;
        private final int   SIZE;

        private boolean full = false;

        public CircleBuffer(int size) {
            SIZE = size;
            buffer = new int[size];
        }

        public int[] toArray(){
            int[] a = new int[SIZE];
            if (full) {
                System.arraycopy(buffer, writePos, a, 0, SIZE - writePos);
                System.arraycopy(buffer,0,a,SIZE - writePos,writePos);
            } else{
                System.arraycopy(buffer,0,a,0,writePos);
            }
            return a;
        }

        public void write(int i) {
           buffer[writePos++] = i;
            if (!full && writePos == SIZE) {
                full = true;
            }
            writePos = writePos % SIZE;
        }
    }
