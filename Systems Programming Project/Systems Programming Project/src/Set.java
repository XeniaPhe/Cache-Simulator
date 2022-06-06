import java.util.ArrayList;

public class Set 
{
    public ArrayList<Line> lines;

    private Set() {}

    public Set(int lineCount) 
    {
        lines = new ArrayList<>();
        for(int i=0;i<lineCount;i++)
            lines.add(new Line());            
    }

    public Line SearchForTag(int tag)
    {
        for (Line line : lines) 
        {
            if(line.tag == tag && line.valid)
                return line;
        }
        
        return null;
    }

    public Line GetVictim()
    {
        int min = Integer.MAX_VALUE;
        Line line = null;

        for (Line ln : lines) 
        {
            if(!ln.valid)
            {
                line = ln;
                break;
            }
            else if(ln.time < min)
            {
                min = ln.time;
                line = ln;
            }
        }

        return line;
    }
}