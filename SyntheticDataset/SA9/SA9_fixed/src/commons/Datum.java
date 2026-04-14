package commons;

import java.util.Arrays;

public class Datum {
    private byte[] data;
    private int size;
    
    public Datum() {
        this.data = null;
        this.size = 0;
    }
    
    public Datum(byte[] data, int size) {
        this.data = data;
        this.size = size;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setData(byte[] data, int size) {
        this.data = data;
        this.size = size;
    }
    
    public Datum copy() {
        if (data == null) {
            return new Datum();
        }
        
        byte[] newData = Arrays.copyOf(data, size);
        return new Datum(newData, size);
    }
    
    public void clear() {
        data = null;
        size = 0;
    }
}
