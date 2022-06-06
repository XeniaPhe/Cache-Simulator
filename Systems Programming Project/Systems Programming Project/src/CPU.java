import java.io.IOException;
import java.util.*;

/*
 *      CACHE SIMULATOR BY,
 * Mustafa Tunahan Bas - 150119055
 * Talha Sezer - 150119022
 * Sadik Akgedik - 150119052
 * Mehmet Erdem - 150119054
 */


public class CPU 
{
    private static CPU instance;
    private Cache L1d;
    private Cache L1i;
    private Cache L2;

    private CPU(){ }

    public static CPU GetInstance()
    {
        if(instance != null)
        {
            return instance;
        }
        else
        {
            instance = new CPU();
            return instance;
        }
    }

    public void RunSimulation(Cache _L1d,Cache _L1i,Cache _L2,ArrayList<String> trace) throws IOException
    {
        L1d = _L1d;
        L1i = _L1i;
        L2 = _L2;

        for (int i=1;i<=trace.size();i++)
            ReadTrace(trace.get(i-1),i);

        System.out.println("\n");
        System.out.println("L1I-hits:"+L1i.hitCounter+" L1I-misses:"+L1i.missCounter+" L1I-evictions:"+L1i.evictionCounter);
        System.out.println("L1D-hits:"+L1d.hitCounter+" L1D-misses:"+L1d.missCounter+" L1D-evictions:"+L1d.evictionCounter);
        System.out.println("L2-hits:"+L2.hitCounter+" L2-misses:"+L2.missCounter+" L2-evictions:"+L2.evictionCounter);
        System.out.println("\n");
    }

    private void ReadTrace(String trace,int executionOrder) throws IOException
    {
        int address=0,size=0;
        long data=0;
        char[] charArr = new char[trace.length()];
        trace.getChars(0, trace.length(), charArr, 0);
        String addressHex = trace.substring(2,10);
        address = Integer.parseInt(addressHex,16);
        size = charArr[12] - '0';
        char type = charArr[0];
        StringBuilder builder = new StringBuilder();

        builder.append(type);
        builder.append(" ");
        builder.append(addressHex.toUpperCase());
        builder.append(", ");
        builder.append(size);

        if(type == 'M' || type == 'S')
        {
            String dataHex = trace.substring(15,trace.length());
            data = Long.parseLong(dataHex,16);

            builder.append(", ");
            builder.append(dataHex.toUpperCase());
            builder.append("\n    ");
            if(charArr[0] == 'M')
                Modify(address, size, data,executionOrder,builder);
            else
                Store(address, size, data,executionOrder,builder);
        }
        else
        {
            builder.append("\n    ");

            if(type == 'L')
                Load(address, size,executionOrder,builder);
            else
                LoadInstruction(address, size,executionOrder,builder);
        }
        

        System.out.println(builder.toString());
    }

    private void Modify(int address,int size,long data,int executionOrder,StringBuilder builder) throws IOException
    {
        Load(address, size,executionOrder,builder);
        builder.append("\n    ");
        Store(address, size, data,executionOrder,builder);
    }

    private void Store(int address,int size,long data,int executionOrder,StringBuilder builder) throws IOException
    {
        L1d.Store(address, size, executionOrder, data,builder,true);
    }

    private void Load(int address,int size,int executionOrder,StringBuilder builder) throws IOException
    {
         L1d.Load(address, size, executionOrder,builder);
    }

    private void LoadInstruction(int address,int size,int executionOrder,StringBuilder builder) throws IOException
    {
        L1i.Load(address, size, executionOrder,builder);
    }
}