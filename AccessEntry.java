
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Daniel Bratell
 */
public class AccessEntry
{

    // Plockar ut information ur en rad ur loggen. Man kan göra det mer abstrakt med
    // Stringtokenizer men det här är snabbare (15%)
    public AccessEntry(String aLogRad, Hashtable domains) throws InvalidAccessLogRadException
    {
        try
        {
            // Gå till första mellanslaget
            int i = aLogRad.indexOf(' ');
            try
            {
                host = InetAddress.getByName(aLogRad.substring(0, i)).getHostName();
            }
            catch (UnknownHostException uhe)
            {
                // The hostname wasn't recognized
                host = aLogRad.substring(0, i);
            }

            String suffix = StringTools.Suffix(host);
            if (!(suffix.equals("")))
            {
                suffix = suffix.toUpperCase(); // Kanske tilldelningen är onödig.
                domain = (String) domains.get(suffix);
                if (domain == null) domain = "Unknown (numerical and other)";
            }
            else
                domain = "Unknown (numerical and other)";

            int startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            referer = StringTools.URLStringToString(aLogRad.substring(startIndex, i));

            startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            String temp = StringTools.URLStringToString(aLogRad.substring(startIndex, i));

            int j = temp.indexOf(' ');
            if (j != -1)
            { // Hittade inget mellanslag
                browser = temp.substring(0, j);
                browserAux = temp.substring(j + 1);
            }
            else
            {
                browser = temp;
                browserAux = null;
            }

            startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            String timeString = aLogRad.substring(startIndex, i);

            startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            timeString = timeString + " " + aLogRad.substring(startIndex, i);

            StringTokenizer tokTimeString = new StringTokenizer(timeString, " []/:");
            int date = Integer.valueOf(tokTimeString.nextToken()).intValue();
            String month = tokTimeString.nextToken();
            int year = Integer.valueOf(tokTimeString.nextToken()).intValue();
            int hour = Integer.valueOf(tokTimeString.nextToken()).intValue();
            int minute = Integer.valueOf(tokTimeString.nextToken()).intValue();
            int second = Integer.valueOf(tokTimeString.nextToken()).intValue();
            time = new Date(year - 1900, DateTools.monthToInt(month), date, hour, minute, second);

            startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            request = aLogRad.substring(startIndex, i);

            startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            file = StringTools.URLStringToString(aLogRad.substring(startIndex, i));
            if (file.charAt(file.length() - 1) == '/')
            {
                file = file + stdFile;
            }
            request = request + " " + file;

            startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            request = request + " " + aLogRad.substring(startIndex, i);

            startIndex = i + 1;
            i = aLogRad.indexOf(' ', startIndex);
            returkod = aLogRad.substring(startIndex, i);

            byteTransferred = aLogRad.substring(i + 1).trim(); // Resten av raden minus ev. mellanslag

        }
        catch (StringIndexOutOfBoundsException e)
        { // Om raden är för kort
            throw new InvalidAccessLogRadException(aLogRad);
        }
        catch (NumberFormatException e)
        {   // Om tidsomvandlingen misslyckas
            throw new InvalidAccessLogRadException(aLogRad);
        }
    }

    public String getHost()
    {
        return host;
    }

    public String getDomain()
    {
        return domain;
    }

    public String getReferer()
    {
        return referer;
    }

    public String getBrowser()
    {
        return browser;
    }

    public String getBrowserAux()
    {
        return browserAux;
    }

    public String getRequest()
    {
        return request;
    }

    public String getFile()
    {
        return file;
    }

    public Date getTime()
    {
        return time;
    }

    private String host;
    private String domain;
    private String referer;
    private String browser;
    private String browserAux;
    private Date time;
    private String request;
    private String file;
    // Vet inte ännu vad de två sista står för.
    // Jo nu vet jag: returkod och antalet byte överfört.
    private String returkod;
    private String byteTransferred;

    private static final String stdFile = "index.html";
}
