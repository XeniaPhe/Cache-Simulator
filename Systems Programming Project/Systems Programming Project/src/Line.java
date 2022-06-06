public class Line 
{
    public boolean valid = false;
    public int time = 0;
    public int tag;
    private String data;

    public String SaveData(int tag,long data,int time)
    {
        this.tag = tag;
        this.data = Long.toBinaryString(data);
        valid = true;
        return this.data;
    }

    public String SaveData(int tag,String data,int time)
    {
        this.tag = tag;
        this.data = data;
        valid = true;
        return data;
    }

    public String GetData()
    {
        return data;
    }

    public long FetchDataAsLong()
    {
        long result = 0;
        int len = data.length();
        for (int i = 0; i < len; i++) 
        {
            char c = data.charAt(len-i-1);
            switch (c) 
            {
                case '1': 
                    result |= (1L << i); 
                    break;
                case '0':
                    break;
                default : 
                    System.out.println("Invalid Binary!");
                    break;
            }
        }
        return result;
    }
}