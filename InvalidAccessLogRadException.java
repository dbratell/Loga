
/**
 * @author Daniel Bratell
 */
public class InvalidAccessLogRadException extends Exception
{
    public InvalidAccessLogRadException(String s)
    {
        felmeddelande = s;
    }

    public String toString()
    {
        return felmeddelande;
    }

    private String felmeddelande;
}
