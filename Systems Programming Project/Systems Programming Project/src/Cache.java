import java.io.IOException;
import java.util.ArrayList;

public class Cache 
{
    private int setBitslength; //2^setBitsLength = setCount
    private int blockBitsLength; //2^blockBitsLength = blockSize (bits),(2^blockBitsLength)/8 = blockSize(bytes) [blockBitsLength-3 = # of bits to represent offset]
    private String name;
    private int setCount;    
    private int linesPerSet;
    private int blockSize;
    private int offSetBitsLength;
    private Cache upperCache = null;
    private RAM ram;

    public int hitCounter = 0;
    public int missCounter = 0;
    public int evictionCounter = 0;

    private ArrayList<Set> sets;

    public Cache(String name,int setBitsLength,int blockBitsLength,int linesPerSet) throws IOException
    {
        this.name = name;
        this.setBitslength = setBitsLength;
        this.blockBitsLength = blockBitsLength;
        this.offSetBitsLength = blockBitsLength -3;
        this.linesPerSet = linesPerSet;
        blockSize = (int)Math.pow(2,blockBitsLength);
        setCount = (int)Math.pow(2,setBitsLength);

        sets = new ArrayList<>();
        ram = RAM.GetInstance();
        for(int i=0;i<setCount;i++)
            sets.add(new Set(linesPerSet));
    }

    public Cache(String name,int setBitsLength,int blockBitsLength,int linesPerSet,Cache upperCash) throws IOException
    {
        this(name,setBitsLength, blockBitsLength, linesPerSet);
        this.upperCache = upperCash;
    }
    
    public String Load(int address,int size,int executionOrder,StringBuilder builder) throws IOException
    {
        int setIndex = GetSetIndex(address);
        Set set = sets.get(setIndex);
        Line line = set.SearchForTag(GetTag(address));
        String data;

        if(line == null)
        {
            //miss
            builder.append(name);
            builder.append(" miss");

            if(upperCache == null)
            {
                builder.append("\n    ");
                line = set.GetVictim();
                
                if(line.valid)
                    ++evictionCounter;
                
                data = line.SaveData(GetTag(address),ram.ReadAddress(address, size,builder),executionOrder); 
                builder.append("Place in ");
                builder.append(name);

                if(setBitslength > 0)
                {
                    builder.append(" set ");
                    builder.append(setIndex);
                    builder.append(", ");
                }
            }
            else
            {
                builder.append(", ");
                line = set.GetVictim();
                data = line.SaveData(GetTag(address),upperCache.Load(address, size, executionOrder,builder), executionOrder);

                builder.append(name);
                if(setBitslength > 0)
                {
                    builder.append(" set");
                    builder.append(setIndex);
                    builder.append("\n    ");
                }
            }
            
            ++missCounter;
        }
        else
        {
            builder.append(name);
            builder.append(" hit\n    ");
            line.time = executionOrder;
            data = line.GetData();
            ++hitCounter;
        }

        return data;
    }

    public void Store(int address,int size,int executionOrder,long data,StringBuilder builder,boolean lowerHit) throws IOException
    {
        int setIndex = GetSetIndex(address);
        Set set = sets.get(setIndex);
        Line line = set.SearchForTag(GetTag(address));

        if(line == null)
        {
            //write miss => no write allocate
            builder.append(name);
            builder.append(" miss");
            if(upperCache == null)
            {
                ram.WriteToAddress(address, data,builder);
                builder.append("\n    Store in RAM");
            }
            else
            {
                builder.append(", ");
                upperCache.Store(address, size, executionOrder, data, builder,false);
            }

            ++missCounter;
        }
        else
        {
            //write hit => write through
            builder.append(name);
            builder.append(" hit");

            line.SaveData(GetTag(address), data, executionOrder);
            if(upperCache == null)//L2
            {
                builder.append("\n    ");
                ram.WriteToAddress(address, data,builder);
                if(!lowerHit)
                    builder.append("Store in L2, RAM");
            }
            else//L1
            {
                builder.append(", ");
                upperCache.Store(address, size, executionOrder, data,builder,true);
                builder.append("Store in L1D, L2, RAM");
            }

            ++hitCounter;
        }
    }

    private int GetBlockOffset(int address)
    {
        return address << (32 - offSetBitsLength);
    }

    private int GetSetIndex(int address)
    {
        int intermediate = (address << (32-setBitslength-offSetBitsLength));
        return intermediate >> (offSetBitsLength-1);
    }

    private int GetTag(int address)
    {
        return address >> (setBitslength + offSetBitsLength);
    }
}