
import java.io.PrintStream;
import java.util.Date;

public class TablePrint
{

    public TablePrint(PrintStream pStream)
    {
        ps = pStream;
    }


    public TablePrint(PrintStream pStream, boolean Hformat)
    {
        ps = pStream;
        HTML = Hformat;
    }

    public static String getVersion()
    {
        return VERSTRING;
    }

    public void PrintSubHeader(String header)
    {
        if (HTML)
        {
            ps.println("<H2 ALIGN=center>" + header + "</H2>");
        }
    }

    public void PrintText(String text)
    {
        if (HTML)
        {
            ps.println("<P>" + text);
        }
        else
        {
            ps.println(text);
        }
    }

    public void PrintStartUnorderedList(String header)
    {
        if (HTML)
        {
            ps.println("<H4>" + header + "</H4>");
            ps.println("<UL>");
        }
    }

    public void PrintStartUnorderedList()
    {
        if (HTML)
        {
            ps.println("<UL>");
        }
    }

    public void PrintEndUnorderedList()
    {
        if (HTML)
        {
            ps.println("</UL>");
        }
    }

    public void PrintListLink(String link, String description)
    {
        if (HTML)
        {
            ps.println("<LI><A HREF=" + '"' + link + '"' + ">" + description + "</A>");
        }
    }

    public void PrintLinkHandle(String link)
    {
        if (HTML)
        {
            ps.println("<A NAME=" + '"' + link + '"' + ">");
        }
    }


    public void PrintHeader(String headString, int timePeriod)
    {
        if (HTML)
        {
            ps.println("<HTML><HEAD><TITLE>" + headString + " OUTPUT</TITLE></HEAD>");
            if (NETSCAPE3)
            {
                ps.println("<BODY BGCOLOR=" + STDBG + ">");
            }
            else
            {
                ps.println("<BODY>");
            }
            ps.println("<H1 ALIGN=center>LOG STATISTICS</H1>");

            Date nuTid = new Date();
            // could also use nuTid.toString() or nuTid.toGMTString() but this I like best (now at least)
            ps.println("Log parsed " + nuTid.toLocaleString());

            // If we don't use all available statistics, print which we use
            if (timePeriod > 0)
            {
                ps.println("<BR>\nStatistics made for last " + timePeriod);
                if (timePeriod == 1)
                {
                    ps.println(" day.");
                }
                else
                {
                    ps.println(" days.");
                }
            }
            ps.println("<HR>");
        }
        else
        {
            ps.println(headString + "\n");
        }
    }



//***************************************************************************
//**
//** PrintTableStart(1): Startar en tabell som ska bestå av två strängar
//**
//***************************************************************************
    public void PrintTableStart(String header, String c1Header, String c2Header)
    {
        if (HTML)
        {
            ps.println("<TABLE BORDER WIDTH=100%>\n<CAPTION><H2>" + header + "</H2></CAPTION>");
            if (NETSCAPE3)
            {
                ps.print("<TR BGCOLOR=" + HEADERBG + ">");
            }
            else
            {
                ps.print("<TR>");
            }
            ps.println("<TH>" + c1Header + "</TH><TH>" + c2Header + "</TH></TR>");
        }
        else
        {
            ps.println(header);
            ps.println(c1Header + "\t\t" + c2Header);
        }
        numberOfLines = 0;
        föregåendeSträng = null;
    }

//***************************************************************************
//**
//** PrintTableStart(2): Startar en tabell som ska bestå av tre strängar
//**
//***************************************************************************
    public void PrintTableStart(String header, String c1Header, String c2Header, String c3Header)
    {
        if (HTML)
        {
            ps.println("<TABLE BORDER WIDTH=100%>\n<CAPTION><H2>" + header + "</H2></CAPTION>");
            if (NETSCAPE3)
            {
                ps.print("<TR BGCOLOR=" + HEADERBG + ">");
            }
            else
            {
                ps.print("<TR>");
            }
            ps.println("<TH>" + c1Header + "</TH><TH>" + c2Header + "</TH><TH>" + c3Header + "</TH></TR>");
        }
        else
        {
            ps.println(header);
            ps.println(c1Header + "\t\t" + c2Header + "\t\t" + c3Header);
        }
        numberOfLines = 0;
        föregåendeSträng = null;
    }

//***************************************************************************
//**
//** PrintTableStart(3): Startar en tabell som ska bestå av fyra strängar
//**
//***************************************************************************
    public void PrintTableStart(String header, String c1Header, String c2Header, String c3Header, String c4Header)
    {
        if (HTML)
        {
            ps.println("<TABLE BORDER WIDTH=100%>\n<CAPTION><H2>" + header + "</H2></CAPTION>");
            if (NETSCAPE3)
            {
                ps.print("<TR BGCOLOR=" + HEADERBG + ">");
            }
            else
            {
                ps.print("<TR>");
            }
            ps.println("<TH>" + c1Header + "</TH><TH>" + c2Header + "</TH><TH>" + c3Header + "</TH><TH>" + c4Header + "</TH></TR>");
        }
        else
        {
            ps.println(header);
            ps.println(c1Header + "\t\t" + c2Header + "\t\t" + c3Header + "\t\t" + c4Header);
        }
        numberOfLines = 0;
        föregåendeSträng = null;
    }

//***************************************************************************
//**
//** PrintGraphTableStart: Startar en tabell som ska bestå av sträng, siffra, graf
//**
//***************************************************************************
    public void PrintGraphTableStart(String header, String c1Header, String c2Header, String c3Header, int max)
    {
        if (HTML)
        {
            ps.println("<TABLE BORDER WIDTH=100%>\n<CAPTION><H2>" + header + "</H2></CAPTION>");
            if (NETSCAPE3)
            {
                ps.print("<TR BGCOLOR=" + HEADERBG + ">");
            }
            else
            {
                ps.print("<TR>");
            }
            ps.print("<TH>" + c1Header + "</TH><TH>" + c2Header + "</TH><TH NOWRAP ALIGN=left><TT>");

            ps.print("|");
            int j = 1;
            for (int i = 0; i < 4; i++)
            {
                while (j < ((i + 1) * 10 - 1))
                { // Fix! Antar 40 tecken (9 = 40/4)
                    ps.print("-");
                    j++;
                }
                ps.print("|");
                j++;
            }
            ps.print("(" + max + ")");
            ps.println(c3Header + "</TT></TH></TR>");
        }
        else
        {
            ps.println(header);
            ps.println(c1Header + "\t\t" + c2Header + "\t" + c3Header);
        }
        numberOfLines = 0;
        föregåendeSträng = null;
    }

//***************************************************************************
//**
//** PrintTableEntry(1): Skriver ut en rad bestående av två strängar i en tabell
//**
//***************************************************************************
    public void PrintTableEntry(String name, String info)
    {
        if (HTML)
        {
            // Varannan rad ljusgrå och varannan lite mörkare grå om netscape3 specificerad
            if (NETSCAPE3)
            {
                if ((numberOfLines % 2) == 0)
                {
                    ps.println("<TR BGCOLOR=" + LIGHTBG + ">");
                }
                else
                {
                    ps.println("<TR BGCOLOR=" + DARKBG + ">");
                }
            }
            else
            {
                ps.println("<TR>");
            }

            if (name.length() > 65)
            {
                ps.print("<TD>" + name.substring(0, 62) + "..." + "</TD>");
            }
            else
            {
                ps.print("<TD>" + name + "</TD>");
            }
            ps.print("<TD ALIGN=right>" + info + "</TD>");
            ps.println("</TR>");
        }
        else
        { // Doesn't make straight columns
            ps.println(name + "\t\t" + info);
        }
        numberOfLines++;
    }

//***************************************************************************
//**
//** PrintTableEntry(2): Skriver ut en rad bestående av ett nummer och två
//**                     strängar i en tabell
//**
//***************************************************************************
    public void PrintTableEntry(int number, String name, String info)
    {
        if (HTML)
        {
            if (NETSCAPE3)
            {
                if ((numberOfLines % 2) == 0)
                {
                    ps.println("<TR BGCOLOR=" + LIGHTBG + ">");
                }
                else
                {
                    ps.println("<TR BGCOLOR=" + DARKBG + ">");
                }
            }
            else
            {
                ps.println("<TR>");
            }
            ps.print("<TD ALIGN=center>" + number + "</TD>");
            if (name.length() > 65)
            {
                ps.print("<TD>" + name.substring(0, 62) + "..." + "</TD>");
            }
            else
            {
                ps.print("<TD>" + name + "</TD>");
            }
            ps.print("<TD ALIGN=right>" + info + "</TD>");
            ps.println("</TR>");
        }
        else
        { // Doesn't make straight columns
            ps.println(number + ".\t" + name + "\t\t" + info);
        }
        numberOfLines++;
    }


//***************************************************************************
//**
//** PrintNumberedTableEntry(1): Skriver ut en rad bestående av två strängar i
//**                             en tabell som är numrerad. Om samma värde på sträng 2
//**                             förekommer två gånger så skrivs inte numret ut
//**                             andra (eller tredje) gången
//**
//***************************************************************************
    public void PrintNumberedTableEntry(String name, String info)
    {
        if (HTML)
        {
            if (NETSCAPE3)
            {
                if ((numberOfLines % 2) == 0)
                {
                    ps.println("<TR BGCOLOR=" + LIGHTBG + ">");
                }
                else
                {
                    ps.println("<TR BGCOLOR=" + DARKBG + ">");
                }
            }
            else
            {
                ps.println("<TR>");
            }
            if (info.equals(föregåendeSträng))
            { // Skriver inte ut något nytt nummer
                ps.print("<TD ALIGN=center>|</TD>");
            }
            else
            { // Skriver ut ordningsnummer
                ps.print("<TD ALIGN=center>" + (numberOfLines + 1) + "</TD>");
            }

            if (name.length() > 65)
            {
                ps.print("<TD>" + name.substring(0, 62) + "..." + "</TD>");
            }
            else
            {
                ps.print("<TD>" + name + "</TD>");
            }
            ps.print("<TD ALIGN=right>" + info + "</TD>");
            ps.println("</TR>");
        }
        else
        { // Doesn't make straight columns
            ps.println((numberOfLines + 1) + ".\t" + name + "\t\t" + info);
        }
        numberOfLines++;
        föregåendeSträng = info;
    }

//***************************************************************************
//**
//** PrintNumberedTableEntry(2): Skriver ut en rad bestående av tre strängar i
//**                             en tabell som är numrerad. Om samma värde på sträng 3
//**                             förekommer två gånger så skrivs inte numret ut
//**                             andra (eller tredje) gången
//**
//***************************************************************************
    public void PrintNumberedTableEntry(String name, String name2, String info)
    {
        if (HTML)
        {
            if (NETSCAPE3)
            {
                if ((numberOfLines % 2) == 0)
                {
                    ps.println("<TR BGCOLOR=" + LIGHTBG + ">");
                }
                else
                {
                    ps.println("<TR BGCOLOR=" + DARKBG + ">");
                }
            }
            else
            {
                ps.println("<TR>");
            }
            if (info.equals(föregåendeSträng))
            { // Skriver inte ut något nytt nummer
                ps.print("<TD ALIGN=center>|</TD>");
            }
            else
            { // Skriver ut ordningsnummer
                ps.print("<TD ALIGN=center>" + (numberOfLines + 1) + "</TD>");
            }

            if (name.length() > 65)
            {
                ps.print("<TD>" + name.substring(0, 62) + "..." + "</TD>");
            }
            else
            {
                ps.print("<TD>" + name + "</TD>");
            }
            ps.print("<TD ALIGN=right>" + name2 + "</TD><TD ALIGN=right>" + info + "</TD>");
            ps.println("</TR>");
        }
        else
        { // Doesn't make straight columns
            ps.println((numberOfLines + 1) + ".\t" + name + "\t" + name2 + "\t" + info);
        }
        numberOfLines++;
        föregåendeSträng = info;
    }

    public void PrintTableEntryWithGraph(String name, int antal, int max)
    {
        // Max= maximala värdet på antal. Behövs för grafiken
        if (HTML)
        {
            if (NETSCAPE3)
            {
                if ((numberOfLines % 2) == 0)
                {
                    ps.print("<TR BGCOLOR=" + LIGHTBG + ">");
                }
                else
                {
                    ps.print("<TR BGCOLOR=" + DARKBG + ">");
                }
            }
            else
            {
                ps.print("<TR>");
            }
            if (name.length() > 65)
            {
                ps.print("<TD>" + name.substring(0, 62) + "..." + "</TD>");
            }
            else
            {
                ps.print("<TD>" + name + "</TD>");
            }
            ps.print("<TD ALIGN=right>" + antal + "</TD>");
            ps.print("<TD NOWRAP ALIGN=left><TT>");
            for (int i = 0; i < (int) ((((double) antal) / max) * 40); i++)
            {
                ps.print("#");
            }
            ps.print("</TT></TD>");
            ps.println("</TR>");
        }
        else
        { // Doesn't make straight columns
            ps.print(name + "\t\t" + antal + "\t");
            for (int i = 0; i < (int) ((((double) antal) / max) * 40); i++)
            {
                ps.print("#");
            }
            ps.println(""); // ny rad
        }
        numberOfLines++;
    }

    public void PrintNumberedTableEntryWithLinks(String name, String link, String info)
    {
        if (HTML)
        {
            if (NETSCAPE3)
            {
                if ((numberOfLines % 2) == 0)
                {
                    ps.print("<TR BGCOLOR=" + LIGHTBG + ">");
                }
                else
                {
                    ps.print("<TR BGCOLOR=" + DARKBG + ">");
                }
            }
            else
            {
                ps.print("<TR>");
            }

            if (info.equals(föregåendeSträng))
            { // Skriver inte ut något nytt nummer
                ps.print("<TD ALIGN=center>|</TD>");
            }
            else
            { // Skriver ut ordningsnummer
                ps.print("<TD ALIGN=center>" + (numberOfLines + 1) + "</TD>");
            }

            ps.print("<TD><A HREF=" + '"' + link + '"' + ">");
            if (name.length() > 65)
            {
                ps.println(name.substring(0, 62) + "...");
            }
            else
            {
                ps.println(name);
            }
            ps.print("</A></TD>");
            ps.print("<TD ALIGN=right>" + info + "</TD>");
            ps.println("</TR>");
        }
        else
        { // Doesn't make straight columns
            ps.println(name + "\t\t" + info);
        }
        numberOfLines++;
        föregåendeSträng = info;
    }

    public void PrintNumberedTableEntryWithLinks(String name, String link, String name2, String info)
    {
        if (HTML)
        {
            if (NETSCAPE3)
            {
                if ((numberOfLines % 2) == 0)
                {
                    ps.print("<TR BGCOLOR=" + LIGHTBG + ">");
                }
                else
                {
                    ps.print("<TR BGCOLOR=" + DARKBG + ">");
                }
            }
            else
            {
                ps.print("<TR>");
            }

            if (info.equals(föregåendeSträng))
            { // Skriver inte ut något nytt nummer
                ps.print("<TD ALIGN=center>|</TD>");
            }
            else
            { // Skriver ut ordningsnummer
                ps.print("<TD ALIGN=center>" + (numberOfLines + 1) + "</TD>");
            }

            ps.print("<TD><A HREF=" + '"' + link + '"' + ">");
            if (name.length() > 65)
            {
                ps.println(name.substring(0, 62) + "...");
            }
            else
            {
                ps.println(name);
            }
            ps.print("</A></TD>");
            ps.print("<TD ALIGN=right>" + name2 + "</TD><TD ALIGN=right>" + info + "</TD>");
            ps.println("</TR>");
        }
        else
        { // Doesn't make straight columns
            ps.println(name + "\t" + name2 + "\t" + info);
        }
        numberOfLines++;
        föregåendeSträng = info;
    }

    public void PrintTableEnd()
    {
        if (HTML)
        {
            ps.println("</TABLE><HR>");
        }
        else
        {
            ps.println("\n");
        }
    }


    public void PrintFooter(String footString, String url)
    {
        if (HTML)
        {
            ps.println("This page generated by <A HREF=" + '"' + url + '"' + ">" + footString + "</A>");
            ps.println("</BODY></HTML>");
        }
    }


    private PrintStream ps; // Utstreamen
    private boolean HTML = false;

    private int numberOfLines = 0; // Antalet rader utskrivna
    private String föregåendeSträng = null; // Används i numrerade tabeller
    private int lastnumber; // För att se om placeringsnumret behöver skrivas ut

    private static final String VERSTRING = "TablePrint 960531 by Daniel Bratell";
    // Följande konstanter gäller endast för HTML
    private static final boolean NETSCAPE3 = true;
    // Följande gäller endast Netscape3
    private static final String STDBG = "#c0c0c0";
    private static final String DARKBG = "#b9b9b9";
    private static final String LIGHTBG = "#c5c5c5";
    private static final String HEADERBG = "#f0f0f0";

}
