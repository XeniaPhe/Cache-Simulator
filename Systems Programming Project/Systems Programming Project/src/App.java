import java.io.*;
import java.util.*;

/*
 *      CACHE SIMULATOR BY,
 * Mustafa Tunahan Bas - 150119055
 * Talha Sezer - 150119022
 * Sadik Akgedik - 150119052
 * Mehmet Erdem - 150119054
 */

public class App 
{
    public static void main(String[] args) throws IOException
    {
        if(args.length == 0)
        {
            System.out.println("No no no");
            return;
        }
        
        String arg,traceFile="",value = "";
        String l1s="",l1E="",l1b="",l2s="",l2E="",l2b="";

        for(int i=args.length-1;i>-1;i--)
        {
            arg = args[i];

            switch(args[i])
            {
                case "-L1s" :
                    l1s = value;
                    break;
                case "-L1E" :
                    l1E = value;
                    break;
                case "-L1b" :
                    l1b = value;
                    break;
                case "-L2s" :
                    l2s = value;
                    break;
                case "-L2E" :
                    l2E = value;
                    break;
                case "-L2b" :
                    l2b = value;
                    break;
                case "-t" :
                    traceFile = value;
                    break;
                default : 
                    value = arg;
                    break;
            }
        }

        Cache L2 = new Cache("L2",Integer.parseInt(l2s),Integer.parseInt(l2b),Integer.parseInt(l2E));
        Cache L1d = new Cache("L1D",Integer.parseInt(l1s), Integer.parseInt(l1b),Integer.parseInt(l1E),L2);
        Cache L1i = new Cache("L1I",Integer.parseInt(l1s), Integer.parseInt(l1b),Integer.parseInt(l1E),L2);

        FileReader reader = new FileReader(traceFile);
        ArrayList<String> trace = new ArrayList<>();

        int ch;
        StringBuilder builder = new StringBuilder();

        while((ch = reader.read()) != -1)
        {
            if(ch == '\n')
            {
                trace.add(builder.toString());
                builder.delete(0, builder.length());
            }
            else
            {
                builder.append((char)ch);
            }
        }

        reader.close();
        CPU.GetInstance().RunSimulation(L1d, L1i,L2,trace);
    }
}