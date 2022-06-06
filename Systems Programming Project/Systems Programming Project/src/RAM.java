import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RAM
{
    private static RAM instance;

    private RAM() throws IOException
    { 
        Files.deleteIfExists(Paths.get("ModifiedRAM.dat"));
        File source = new File("RAM.dat");
        File dest = new File("ModifiedRAM.dat");
        Files.copy(source.toPath(), dest.toPath());
    }

    public static RAM GetInstance() throws IOException
    {
        if(instance != null)
        {
            return instance;
        }
        else
        {
            instance = new RAM();
            return instance;
        }
    }

    public long ReadAddress(int address,int size,StringBuilder builder) throws IOException
    {
        RandomAccessFile file = new RandomAccessFile("ModifiedRAM.dat", "r");
        file.seek(address);
        long data = file.readLong();    //EOF exception
        file.close();
        return data;
    }    

    public void WriteToAddress(int address,long data,StringBuilder builder) throws IOException
    {
        RandomAccessFile file = new RandomAccessFile("ModifiedRAM.dat", "rw");
        file.seek(address);
        file.writeLong(data);
        file.close();
    }
}