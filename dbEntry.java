
import java.util.Date;


/**
 * @author Daniel Bratell
 */
public class dbEntry
{
    public dbEntry(String s)
    {
        dbNamn = s;
        dbAntal = 0;
    }

    public dbEntry(String s, Date d)
    {
        dbNamn = s;
        dbAntal = 0;
        startDate = d;
        endDate = d;
    }

    public String getName()
    {
        return dbNamn;
    }

    public int getAntal()
    {
        return dbAntal;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setAntal(int tal)
    {
        dbAntal = tal;
    }

    public void incAntal()
    {
        dbAntal++;
    }

    public void decAntal()
    {
        dbAntal--;
    }

    public void setStartDate(Date d)
    {
        startDate = d;
    }

    public void setEndDate(Date d)
    {
        endDate = d;
    }

    private final String dbNamn;
    private int dbAntal;
    private Date startDate;
    private Date endDate;
}
