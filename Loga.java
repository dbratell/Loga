
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Date;

// TODO:  *referers m�ste f� tillbaks sina %20 och s�nt n�r de �r l�nkar.
//        *skriva dokumentation
//        optimera (g�r det?)
//        *procenttal                   g�ra till en option, in i dokmumentationen
//        *l�gga till option: -nohosts, -nodomains osv   ska in i dokumentation
//        *filtrera bort accesser till gif och jpg filer       dito

public class Loga
{
    public static final void main(String[] args)
    {
        Hashtable logDatabas = new Hashtable();

        if (!ParseArgs(args)) return;


        if (!ReadLogFile(logDatabas, inFilnamn)) return;


        TablePrint utskrift = new TablePrint(outPrintStream, HTML);

        utskrift.PrintHeader(VERINFO, TIDSPERIOD);

        // Generell information?
        if (DEBUG)
        {
            System.out.println("\nDet finns nu " + logDatabas.size() + " element i loggen");
        }
        ComputeIndex(utskrift); // M�ste �ndras i om nya rapporter l�ggs till

        if (SHOWHOSTS) ComputeHosts(utskrift, logDatabas);
        if (SHOWDOMAINS) ComputeDomains(utskrift, logDatabas);
        if (SHOWREFERERS) ComputeReferers(utskrift, logDatabas);
        if (SHOWFILES) ComputeFiles(utskrift, logDatabas);
        if (SHOWBROWSERS) ComputeBrowsers(utskrift, logDatabas);
        if (SHOWMONTHOFYEAR) ComputeMonthOfYear(utskrift, logDatabas);
        if (SHOWHOUROFDAY) ComputeTimeOfDay(utskrift, logDatabas);
        if (SHOWDAYOFWEEK) ComputeDayOfWeek(utskrift, logDatabas);

        utskrift.PrintFooter(VERINFO + "\n" + COPYRIGHTINFO, HOMEPAGE);
        Cleanup();
    }

//***************************************************************************
//**
//** Cleanup: Rensar upp lite grann (s� gott som det �r m�jligt)
//**
//***************************************************************************

    private static void Cleanup()
    {
        if (outPrintStream != null)
        {
            outPrintStream.flush();
            outPrintStream.close();
            outPrintStream = null;
        }

        System.gc(); // F�r skojs skull
    }


//***************************************************************************
//**
//** ParseArgs: Tar argumenten och s�tter l�mpliga globala variabler. returnerar
//**            false om saker och ting inte gick s� bra.
//**
//***************************************************************************

    private static boolean ParseArgs(String[] args)
    {
        // En av de fulaste funktioner jag skrivit
        // Skriv ut anv�ndningss�tt och avsluta om det inte skickas med ett argument
        if (args.length == 0)
        {
            PrintUsage();
            return false;
        }
        // Plocka ut de olika flaggorna borde g�ras med case, switch om det ginge
        for (int i = 0; i < args.length - 1; i++)
        {


            // HTML-flaggan specar att utdatat ska vara i HTML-format
            if (args[i].equalsIgnoreCase("-html"))
            {
                HTML = true;
                if (DEBUG)
                {
                    System.out.println("Utdata i HTML-format");
                }
                continue;
            }



            // Debug-flaggan ger en del extra information p� konsollen under k�rningen
            if (args[i].equalsIgnoreCase("-debug"))
            {
                DEBUG = true;
                continue;
            }



            // Med o-flaggan kan man speca var man vill ha utdatan. Om den inte ges antas
            // konsollen. Som standard tar flaggan ett filnamn. Om man ger filnamnet - s�
            // kommer utskriften p� konsollen.
            if (args[i].equalsIgnoreCase("-o"))
            {
                if (i < args.length - 2)
                { // Annars f�r inte ett extra argument plats
                    if (args[i + 1].equalsIgnoreCase("-"))
                    {
                        outPrintStream = System.out;
                        i++;    // L�ser ett extra argument
                    }
                    else
                    {
                        try
                        {
                            // L�gger en buffer p� outputstreamen f�r att ev. snabba upp lite grann
                            if (!(outPrintStream.equals(System.out)))
                            {
                                outPrintStream.flush();
                                outPrintStream.close();
                            }
                            outPrintStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(args[i + 1]), 8196));
                            i++;    // L�ser ett extra argument
                        }
                        catch (IOException e)
                        {
                            System.err.println(e);
                            return false;
                        }
                    }
                }
                else
                {
                    System.err.println("You have to specify a filename or '-' for console (the last argument is the infile)!");
                    PrintUsage();
                    return false;
                }
                continue;
            }




            // Last-flaggan specificerar vilken tidsrymd som ska tas h�nsyn till.
            // flaggan tar ett argument som kan best� av ett eller tv� ord. Till�tna
            // kontruktioner �r 1 week, day, 3 months, 34 days u.s.w. Om man inte ger
            // n�gon siffra antas 1 g�lla. T.ex. -last day �r ekvivalent med -last 1
            // day
            if (args[i].equalsIgnoreCase("-last"))
            {
                int antalTidsenheter;
                double tidsenhet;
                if (i < args.length - 2)
                { // F�r att ytterligare argument ska f� plats
                    try
                    {
                        antalTidsenheter = Integer.parseInt(args[i + 1]);
                        if (!(i < args.length - 3))
                        {// F�r att f� plats med enhet
                            System.err.println("You must specify a unit with the number in the -last-expression");
                            return false;
                        }
                        i++;
                    }
                    catch (NumberFormatException e)
                    {
                        antalTidsenheter = 1; // Det gick inte att parsa som ett tal: allts� var det en enhet (?)
                    }
                    i++;
                    if (args[i].equalsIgnoreCase("week") || args[i].equalsIgnoreCase("weeks"))
                    {
                        tidsenhet = 7; // 7 dagarsenheter (vecka)
                    }
                    else
                    {
                        if (args[i].equalsIgnoreCase("month") || args[i].equalsIgnoreCase("months"))
                        {
                            tidsenhet = 30.4; // Antag 30 dagar i m�naden (30.4 eller 31 ocks� alternativ)
                        }
                        else
                        {
                            if (args[i].equalsIgnoreCase("day") || args[i].equalsIgnoreCase("days"))
                            {
                                tidsenhet = 1;
                            }
                            else
                            {
                                System.err.println("You have to specify time period.\nFor instance '-last week' '-last 3 days' and '-last day' are legal.");
                                PrintUsage();
                                return false;
                            }
                        }
                    }
                }
                else
                {
                    System.err.println("You have to specify time period.\nFor instance '-last week' '-last 3 days' and '-last day' are legal.");
                    PrintUsage();
                    return false;
                }
                TIDSPERIOD = (int) (tidsenhet * antalTidsenheter);
                if (DEBUG)
                {
                    System.out.println("Tolkat som " + TIDSPERIOD + " dagar");
                }
                continue;
            }




            // Flaggan precent s�ger att procentsiffror ska vara med i statistiken
            if (args[i].equalsIgnoreCase("-percent"))
            {
                PROCENT = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att visa procentsiffror");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noPercent"))
            {
                PROCENT = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att visa procentsiffror");
                }
                continue;
            }



            // Flaggan Hosts s�ger att statistiken om vilka datorer som bes�kt sidorna
            // ska vara med i utdatan.
            if (args[i].equalsIgnoreCase("-Hosts"))
            {
                SHOWHOSTS = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r fr�n vilken dator bes�karna kommer");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noHosts"))
            {
                SHOWHOSTS = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r fr�n vilken dator bes�karna kommer");
                }
                continue;
            }


            // Flaggan domains s�ger att statistiken om vilka l�nder som bes�kt sidorna
            // ska vara med i utdatan.
            if (args[i].equalsIgnoreCase("-Domains"))
            {
                SHOWDOMAINS = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r fr�n vilket land bes�karna kommer");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noDomains"))
            {
                SHOWDOMAINS = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r fr�n vilket land bes�karna kommer");
                }
                continue;
            }



            // Flaggan Referers s�ger att statistiken om vilka sidor som har lett till dessa sidor
            // ska vara med i utdatan.
            if (args[i].equalsIgnoreCase("-Referers"))
            {
                SHOWREFERERS = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r vilka sidor som lett hit");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noReferers"))
            {
                SHOWREFERERS = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r vilka sidor som lett hit");
                }
                continue;
            }


            // Flaggan Files s�ger att statistiken om vilka sidor som bes�kts
            // ska vara med i utdatan.
            if (args[i].equalsIgnoreCase("-Files"))
            {
                SHOWFILES = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r vilka sidor som bes�kts");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noFiles"))
            {
                SHOWFILES = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r vilka sidor som bes�kts");
                }
                continue;
            }


            // Flaggan Browsers s�ger att det ska vara med statistik om vilka l�sare som anv�nts
            // ska vara med i utdatan.
            if (args[i].equalsIgnoreCase("-Browsers"))
            {
                SHOWBROWSERS = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r vilken l�sare som anv�nts");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noBrowsers"))
            {
                SHOWBROWSERS = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r vilken l�sare som anv�nts");
                }
                continue;
            }

            // Flaggan HourOfDay s�ger att statistiken om vilken timme p� dagen bes�ken sker
            // ska vara med i utdatan. Just nu �r den med i vilket fall som helst.
            if (args[i].equalsIgnoreCase("-HourOfDay"))
            {
                SHOWHOUROFDAY = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r n�r p� dagen folk bes�ker");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noHourOfDay"))
            {
                SHOWHOUROFDAY = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r n�r p� dagen folk bes�ker");
                }
                continue;
            }




            // Flaggan MonthOfYear s�ger att statistiken om vilken m�nad p� �ret bes�ken sker
            // ska vara med i utdatan. Just nu �r den med i vilket fall som helst.
            if (args[i].equalsIgnoreCase("-MonthOfYear"))
            {
                SHOWMONTHOFYEAR = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r n�r p� �ret folk bes�ker");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noMonthOfYear"))
            {
                SHOWMONTHOFYEAR = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r n�r p� �ret folk bes�ker");
                }
                continue;
            }



            // Flaggan DayOfWeek s�ger att statistiken om vilken dag i veckan bes�ken sker
            // ska vara med i utdatan. Just nu �r den med i vilket fall som helst.
            if (args[i].equalsIgnoreCase("-DayOfWeek"))
            {
                SHOWDAYOFWEEK = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna statistik f�r vilken dag i veckan folk tittar in");
                }
                continue;
            }
            if (args[i].equalsIgnoreCase("-noDayOfWeek"))
            {
                SHOWDAYOFWEEK = false;
                if (DEBUG)
                {
                    System.out.println("Kommer inte att ber�kna statistik f�r vilken dag i veckan folk tittar in");
                }
                continue;
            }

            // Flaggan All s�ger att all tillg�nglig statistik
            // ska vara med i utdatan.
            if (args[i].equalsIgnoreCase("-All"))
            {
                SHOWMONTHOFYEAR = true;
                SHOWDAYOFWEEK = true;
                SHOWHOUROFDAY = true;
                SHOWHOSTS = true;
                SHOWDOMAINS = true;
                SHOWREFERERS = true;
                SHOWFILES = true;
                SHOWBROWSERS = true;
                if (DEBUG)
                {
                    System.out.println("Kommer att ber�kna all tillg�nglig statistik");
                }
                continue;
            }



            // Med top-flaggan kan man speca hur m�nga rader man maximalt vill ha i tabeller.
            // Om den inte ges antas att o�ndligt m�nga rader �r till�tna. Man m�ste ge en
            // siffra. T.ex. 10 eller 20. Ger man 0 antas man vilja ha o�ndligt m�nga rader
            if (args[i].equalsIgnoreCase("-top"))
            {
                if (i < args.length - 2)
                { // Annars f�r inte ett extra argument plats
                    try
                    {
                        SHOWNUMBEROFLINES = Integer.parseInt(args[i + 1]);
                        i++;    // L�ser ett extra argument
                    }
                    catch (NumberFormatException e)
                    {
                        // Det gick inte att parsa som ett tal: allts� var det n�got fel
                        PrintUsage();
                        System.err.println("You must give a number together with the top flag.\nFor instance: -top 10 or -top 0");
                        return false;
                    }
                    if (DEBUG)
                    {
                        System.out.println("Kommer att visa " + SHOWNUMBEROFLINES + " rader i tabellerna");
                    }
                }
                else
                {
                    System.err.println("You have to specify a number together with tho flag (the last argument is the infile)!");
                    PrintUsage();
                    return false;
                }
                continue;
            }




            // Om flaggan help �r med sker ingen bearbetning av indata. Det enda som sker �r
            // att lite hj�lpinformation ges. Just nu endast den enklaste syntaxen.
            if (args[i].equalsIgnoreCase("-help"))
            {
                PrintUsage();
                return false;
            }

            // Kommer man hit betyder det att en flagga som inte k�nns igen har getts. Det kan
            // bero p� felstavning, fel i hj�lpen och m�nga andra saker.
            System.err.println("Unknown flag: " + args[i]);
            PrintUsage();
            return false;
        }

        // Sista argumentet ska vara filnamnet om det inte �r ett rop p� hj�lp
        if (args[args.length - 1].equalsIgnoreCase("-help"))
        {
            PrintUsage();
            return false;
        }
        else
        {
            inFilnamn = args[args.length - 1];
        }
        return true; // Allt gick bra
    }

    private static void PrintUsage()
    {
        System.err.println("Usage: java Loga [flags] <accesslog>");
        System.err.println("Flags can be:\n\t-last [<number>] <time-unit>\t-\tSpecifies which time-period should be considered.");
        System.err.println("\t-html\t-\tIf you want output in HTML-format");
        System.err.println("\t-debug\t-\tIf you want some debugging info");
        System.err.println("\t-percent\t-\tIf you want percantage information");
        System.err.println("\t-o <outfile>\t-\tLets you specify outfile. - equals the console");
        System.err.println("\t-all\t-\tSpecifies that you want all possible statistics");
        System.err.println("\t-MonthOfYear\t-\tSpecifies that you want statistics regarding which month people have accessed the pages");
        System.err.println("\t-DayOfWeek\t-\tSpecifies that you want statistics regarding which day of week people accesses the pages");
        System.err.println("\t-HourOfDay\t-\tSpecifies that you want statistics regarding which hour of the day people accesses the pages");
        System.err.println("\t-top <number>\t-\tLets you specify how long tables you want");
        System.err.println("Then there are some undocumented flags for you to find!");
    }


//***************************************************************************
//**
//** ReadLogFile: L�ser in en specificerad logfil och l�gger AccessEntrys i databasen
//**
//***************************************************************************
    private static boolean ReadLogFile(Hashtable dataBas, String filnamn)
    {
        try
        { // �ppna filen. Det �r inte s�kert att det g�r bra
            // L�gger en buffer p� inputstreamen f�r att eventuellt snabba upp saker och ting
            DataInputStream aLogFilen = new DataInputStream(new BufferedInputStream(new FileInputStream(filnamn), 65536));

            Hashtable domains = DomainInit();

            String rad;
            Date gr�nsTid = new Date();
            Date logTid;
            // Anv�nds vid filtypsfiltrering
            String suffixet;
            boolean GOOD; // Anv�nds vid bortplockning av ol�mpliga entrys
            int j;

            if (TIDSPERIOD >= 0)
            {
                // Nej det �r inte vackert men vad g�r man n�r det finns buggar i spr�ket?
                gr�nsTid = new Date(gr�nsTid.getYear(), gr�nsTid.getMonth(), gr�nsTid.getDate() - TIDSPERIOD,
                                    gr�nsTid.getHours(), gr�nsTid.getMinutes(), gr�nsTid.getSeconds());
            }
            int i = 0;
            while ((rad = aLogFilen.readLine()) != null)
            {
                try
                {
                    AccessEntry ae = new AccessEntry(rad, domains);
                    GOOD = true; // Anta att raden �r ok att ha med i databasen

                    if (TIDSPERIOD >= 0)
                    { // Filtrera ut f�r gamla entrys
                        logTid = ae.getTime();
                        if (logTid.before(gr�nsTid)) GOOD = false;// Ska inte vara med
                    }

                    if (FILEFILTER && GOOD)
                    { // Filtrera ut filer som har n�got av ett antal suffix
                        suffixet = StringTools.Suffix(ae.getFile());
                        if (!(suffixet.equals("")))
                        {
                            j = 0;
                            while (GOOD && j < FILEFILTERSUFFIX.length)
                            {
                                if (suffixet.equalsIgnoreCase(FILEFILTERSUFFIX[j])) GOOD = false;
                                j++;
                            }
                        }
                    }
                    if (GOOD) dataBas.put(ae, ae);

                    if (DEBUG)
                    {
                        i++;
                        if (i % 10 == 0)
                        {
                            System.out.print(".");
                            System.out.flush();
                        }
                    }
                }
                catch (InvalidAccessLogRadException e)
                {
                    System.err.println("Felaktig rad: " + e);
                }
                // System.out.println(rad);
            }
            aLogFilen.close();
        }
        catch (IOException e)
        {
            System.err.println(e);
            return false;
        }
        return true;
    }

    private static Hashtable DomainInit()
    {
        Hashtable domains = new Hashtable(300);
        String[][] domainArray = {{"AD", "Andorra"},
                                  {"AE", "United Arab Emirates"}, {"AF", "Afghanistan"}, {"AG", "Antigua and Barbuda"}, {"AI", "Anguilla"}, {"AL", "Albania"}, {"AM", "Armenia"}, {"AN", "Netherland Antilles"}, {"AO", "Angola"},
                                  {"AQ", "Antarctica"}, {"AR", "Argentina"}, {"AS", "American Samoa"}, {"AT", "Austria"}, {"AU", "Australia"}, {"AW", "Aruba"}, {"AZ", "Azerbaidjan"}, {"BA", "Bosnia-Herzegovina"},
                                  {"BB", "Barbados"}, {"BD", "Banglades"}, {"BE", "Belgium"}, {"BF", "Burkina Faso"}, {"BG", "Bulgaria"}, {"BH", "Bahrain"}, {"BI", "Burundi"}, {"BJ", "Benin"},
                                  {"BM", "Bermuda"}, {"BN", "Brunei Darussalam"}, {"BO", "Bolivia"}, {"BR", "Brazil"}, {"BS", "Bahamas"}, {"BT", "Buthan"}, {"BV", "Bouvet Island"}, {"BW", "Botswana"},
                                  {"BY", "Belarus"}, {"BZ", "Belize"}, {"CA", "Canada"}, {"CC", "Cocos (Keeling) Islands"}, {"CF", "Central African Republic"}, {"CG", "Congo"}, {"CH", "Switzerland"}, {"CI", "Ivory Coast"},
                                  {"CK", "Cook Islands"}, {"CL", "Chile"}, {"CM", "Cameroon"}, {"CN", "China"}, {"CO", "Colombia"}, {"CR", "Costa Rica"}, {"CS", "Czechoslovakia"}, {"CU", "Cuba"},
                                  {"CV", "Cape Verde"}, {"CX", "Christmas Island"}, {"CY", "Cyprus"}, {"CZ", "Czech Republic"}, {"DE", "Germany"}, {"DJ", "Djibouti"}, {"DK", "Denmark"}, {"DM", "Dominica"},
                                  {"DO", "Dominican Republic"}, {"DZ", "Algeria"}, {"EC", "Ecuador"}, {"EE", "Estonia"}, {"EG", "Egypt"}, {"EH", "Western Sahara"}, {"ES", "Spain"}, {"ET", "Ethiopia"},
                                  {"FI", "Finland"}, {"FJ", "Fiji"}, {"FK", "Falkland Islands (Malvinas)"}, {"FM", "Micronesia"}, {"FO", "Faroe Islands"}, {"FR", "France"}, {"FX", "France (European Territory)"}, {"GA", "Gabon"},
                                  {"GB", "Great Britain (UK)"}, {"GD", "Grenada"}, {"GE", "Georgia"}, {"GH", "Ghana"}, {"GI", "Gibraltar"}, {"GL", "Greenland"}, {"GP", "Guadeloupe (French)"}, {"GQ", "Equatorial Guinea"},
                                  {"GF", "Guyana (French)"}, {"GM", "Gambia"}, {"GN", "Guinea"}, {"GR", "Greece"}, {"GT", "Guatemala"}, {"GU", "Guam (US)"}, {"GW", "Guinea Bissau"}, {"GY", "Guyana"},
                                  {"HK", "Hong Kong"}, {"HM", "Heard and McDonald Islands"}, {"HN", "Honduras"}, {"HR", "Croatia"}, {"HT", "Haiti"}, {"HU", "Hungary"}, {"ID", "Indonesia"}, {"IE", "Ireland"},
                                  {"IL", "Israel"}, {"IN", "India"}, {"IO", "British Indian Ocean Territory"}, {"IQ", "Iraq"}, {"IR", "Iran"}, {"IS", "Iceland"}, {"IT", "Italy"}, {"JM", "Jamaica"},
                                  {"JO", "Jordan"}, {"JP", "Japan"}, {"KE", "Kenya"}, {"KG", "Kirgistan"}, {"KH", "Cambodia"}, {"KI", "Kiribati"}, {"KM", "Comoros"}, {"KN", "Saint Kitts Nevis Anguilla"},
                                  {"KP", "North Korea"}, {"KR", "South Korea"}, {"KW", "Kuwait"}, {"KY", "Cayman Islands"}, {"KZ", "Kazachstan"}, {"LA", "Laos"}, {"LB", "Lebanon"}, {"LC", "Saint Lucia"},
                                  {"LI", "Liechtenstein"}, {"LK", "Sri Lanka"}, {"LR", "Liberia"}, {"LS", "Lesotho"}, {"LT", "Lithuania"}, {"LU", "Luxembourg"}, {"LV", "Latvia"}, {"LY", "Libya"},
                                  {"MA", "Morocco"}, {"MC", "Monaco"}, {"MD", "Moldavia"}, {"MG", "Madagascar"}, {"MH", "Marshall Islands"}, {"ML", "Mali"}, {"MM", "Myanmar"}, {"MN", "Mongolia"},
                                  {"MO", "Macau"}, {"MP", "Northern Mariana Islands"}, {"MQ", "Martinique (French)"}, {"MR", "Mauritania"}, {"MS", "Montserrat"}, {"MT", "Malta"}, {"MU", "Mauritius"}, {"MV", "Maldives"},
                                  {"MW", "Malawi"}, {"MX", "Mexico"}, {"MY", "Malaysia"}, {"MZ", "Mozambique"}, {"NA", "Namibia"}, {"NC", "New Caledonia (French)"}, {"NE", "Niger"}, {"NF", "Norfolk Island"},
                                  {"NG", "Nigeria"}, {"NI", "Nicaragua"}, {"NL", "Netherlands"}, {"NO", "Norway"}, {"NP", "Nepal"}, {"NR", "Nauru"}, {"NT", "Neutral Zone"}, {"NU", "Niue"},
                                  {"NZ", "New Zealand"}, {"OM", "Oman"}, {"PA", "Panama"}, {"PE", "Peru"}, {"PF", "Polynesia (French)"}, {"PG", "Papua New"}, {"PH", "Philippines"}, {"PK", "Pakistan"},
                                  {"PL", "Poland"}, {"PM", "Saint Pierre and Miquelon"}, {"PN", "Pitcairn"}, {"PT", "Portugal"}, {"PR", "Puerto Rico (US)"}, {"PW", "Palau"}, {"PY", "Paraguay"}, {"QA", "Qatar"},
                                  {"RE", "Reunion (French)"}, {"RO", "Romania"}, {"RU", "Russian Federation"}, {"RW", "Rwanda"}, {"SA", "Saudi Arabia"}, {"SB", "Solomon Islands"}, {"SC", "Seychelles"}, {"SD", "Sudan"},
                                  {"SE", "Sweden"}, {"SG", "Singapore"}, {"SH", "Saint Helena"}, {"SI", "Slovenia"}, {"SJ", "Svalbard and Jan Mayen Islands"}, {"SK", "Slovak Republic"}, {"SL", "Sierra Leone"}, {"SM", "San Marino"},
                                  {"SN", "Senegal"}, {"SO", "Somalia"}, {"SR", "Suriname"}, {"ST", "Saint Tome and Principe"}, {"SU", "Soviet Union"}, {"SV", "El Salvador"}, {"SY", "Syria"}, {"SZ", "Swaziland"},
                                  {"TC", "Turks and Caicos Islands"}, {"TD", "Chad"}, {"TF", "French Southern Territory"}, {"TG", "Togo"}, {"TH", "Thailand"}, {"TJ", "Tadjikistan"}, {"TK", "Tokelau"}, {"TM", "Turkmenistan"},
                                  {"TN", "Tunisia"}, {"TO", "Tonga"}, {"TP", "East Timor"}, {"TR", "Turkey"}, {"TT", "Trinidad and Tobago"}, {"TV", "Tuvalu"}, {"TW", "Taiwan"}, {"TZ", "Tanzania"},
                                  {"UA", "Ukraine"}, {"UG", "Uganda"}, {"UK", "United Kingdom"}, {"UM", "US Minor Outlying Islands"}, {"US", "United States"}, {"UY", "Uruguay"}, {"UZ", "Uzbekistan"}, {"VA", "Vatican City State"},
                                  {"VC", "Saint Vincent and Grenadines"}, {"VE", "Venezuela"}, {"VG", "Virgin Islands (British)"}, {"VI", "Virgin Islands (US)"}, {"VN", "Vietnam"}, {"VU", "Vanuatu"}, {"WF", "Wallis and Futuna Islands"}, {"WS", "Samoa"},
                                  {"YE", "Yemen"}, {"YU", "Yugoslavia"}, {"ZA", "South Africa"}, {"ZM", "Zambia"}, {"ZR", "Zaire"}, {"ZW", "Zimbabwe"}, {"ARPA", "Old style Arpanet"}, {"COM", "US Commercial"},
                                  {"EDU", "US Educational"}, {"GOV", "US Government"}, {"INT", "International"}, {"MIL", "US Military"}, {"NATO", "Nato field"}, {"NET", "Network"}, {"ORG", "Non-Profit"}};
        for (int i = 0; i < domainArray.length; i++)
        {
            domains.put(domainArray[i][0], domainArray[i][1]);
        }
        return domains;
    }


//***************************************************************************
//**
//** ComputeIndex: Skapar ett index i r�ran. G�r inget om det inte �r HTML
//**
//***************************************************************************
    private static void ComputeIndex(TablePrint utskrift)
    {
        if (SHOWHOSTS || SHOWDOMAINS || SHOWREFERERS || SHOWFILES || SHOWBROWSERS || SHOWMONTHOFYEAR || SHOWHOUROFDAY || SHOWDAYOFWEEK)
        {
            utskrift.PrintLinkHandle("index");
            utskrift.PrintSubHeader("Sections");
            utskrift.PrintStartUnorderedList();
            if (SHOWHOSTS) utskrift.PrintListLink("#hosts", "Accessing hosts");
            if (SHOWDOMAINS) utskrift.PrintListLink("#domains", "Accessing domains");
            if (SHOWREFERERS) utskrift.PrintListLink("#referers", "Referers");
            if (SHOWFILES) utskrift.PrintListLink("#files", "Accessed files");
            if (SHOWBROWSERS) utskrift.PrintListLink("#browsers", "Used browsers");
            if (SHOWMONTHOFYEAR) utskrift.PrintListLink("#monthofyear", "Accesses by Month of Year");
            if (SHOWHOUROFDAY) utskrift.PrintListLink("#hourofday", "Accesses by Hour of Day");
            if (SHOWDAYOFWEEK) utskrift.PrintListLink("#dayofweek", "Accesses by Day of Week");
            utskrift.PrintEndUnorderedList();
        }
    }



//***************************************************************************
//**
//** ComputeHosts: Ber�knar statistik om vilka som tittar in
//**
//***************************************************************************
    private static void ComputeHosts(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in hosts i en speciell hostdatabas
        Hashtable hostDatabas = new Hashtable();
        int i = 0;
        Enumeration e = databas.elements();
        int total = databas.size();
        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            if (hostDatabas.containsKey(ae.getHost()))
            { // Finns redan i databasen
                dbEntry h = (dbEntry) hostDatabas.get(ae.getHost());
                h.incAntal();
                // Fixa start och slutdatum f�r hosten
                if (h.getStartDate().after(ae.getTime()))
                {
                    h.setStartDate(ae.getTime());
                }
                else
                {
                    if (h.getEndDate().before(ae.getTime()))
                    {
                        h.setEndDate(ae.getTime());
                    }
                }
            }
            else
            {
                dbEntry h = new dbEntry(ae.getHost(), ae.getTime());
                h.setAntal(1);
                hostDatabas.put(ae.getHost(), h);
            }
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }
        // Skriv ut en sorterad lista av bes�kande hosts
        utskrift.PrintLinkHandle("hosts");
        if (PROCENT)
        {
            utskrift.PrintTableStart("Most visiting hosts", "#", "Host", "Number of visits", "Percent");
        }
        else
        {
            utskrift.PrintTableStart("Most visiting hosts", "#", "Host", "Number of visits");
        }
        int skrivnaRader = 0;
        while ((!hostDatabas.isEmpty()) && (skrivnaRader < SHOWNUMBEROFLINES || SHOWNUMBEROFLINES == 0))
        {
            e = hostDatabas.elements();
            dbEntry topp = (dbEntry)e.nextElement();
            int max = topp.getAntal();
            while (e.hasMoreElements())
            {
                dbEntry he = (dbEntry) e.nextElement();
                if (he.getAntal() > max)
                {
                    topp = he;
                    max = he.getAntal();
                }
            }
            if (PROCENT)
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()),
                                                 StringTools.DoubleToString((100 * topp.getAntal() / (double) total), 2) + "%");
            }
            else
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()));
            }
            skrivnaRader++;
            hostDatabas.remove(topp.getName());
        }
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }


//***************************************************************************
//**
//** ComputeDomains: Ber�knar statistik om vilka l�nder som tittar in
//**
//***************************************************************************
    private static void ComputeDomains(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in dom�ner i en speciell dom�ndatabas
        Hashtable domainDatabas = new Hashtable();
        int i = 0;
        Enumeration e = databas.elements();
        int total = databas.size();

        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            if (domainDatabas.containsKey(ae.getDomain()))
            { // Finns redan i databasen
                dbEntry h = (dbEntry) domainDatabas.get(ae.getDomain());
                h.incAntal();
            }
            else
            {
                dbEntry h = new dbEntry(ae.getDomain());
                h.setAntal(1);
                domainDatabas.put(ae.getDomain(), h);
            }
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }
        // Skriv ut en sorterad lista av bes�kande dom�ner
        utskrift.PrintLinkHandle("domains");
        if (PROCENT)
        {
            utskrift.PrintTableStart("Most visiting domains", "#", "Domain", "Number of visits", "Percent");
        }
        else
        {
            utskrift.PrintTableStart("Most visiting domains", "#", "Domain", "Number of visits");
        }
        int skrivnaRader = 0;
        while ((!domainDatabas.isEmpty()) && (skrivnaRader < SHOWNUMBEROFLINES || SHOWNUMBEROFLINES == 0))
        {
            e = domainDatabas.elements();
            dbEntry topp = (dbEntry)e.nextElement();
            int max = topp.getAntal();
            while (e.hasMoreElements())
            {
                dbEntry he = (dbEntry) e.nextElement();
                if (he.getAntal() > max)
                {
                    topp = he;
                    max = he.getAntal();
                }
            }

            if (PROCENT)
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()),
                                                 StringTools.DoubleToString((100 * topp.getAntal() / (double) total), 2) + "%");
            }
            else
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()));
            }
            skrivnaRader++;
            domainDatabas.remove(topp.getName());
        }
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }

    private static void ComputeReferers(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in referer i en speciell refdatabas
        int i = 0;
        Hashtable refDatabas = new Hashtable();
        Enumeration e = databas.elements();
        int total = databas.size();
        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            if (refDatabas.containsKey(ae.getReferer()))
            { // Finns redan i databasen
                dbEntry h = (dbEntry) refDatabas.get(ae.getReferer());
                h.incAntal();
            }
            else
            {
                dbEntry h = new dbEntry(ae.getReferer());
                h.setAntal(1);
                refDatabas.put(ae.getReferer(), h);
            }
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }
        // Skriv ut en sorterad lista av referers
        // Borde specialbehandla referern: '-'
        utskrift.PrintLinkHandle("referers");
        if (PROCENT)
        {
            utskrift.PrintTableStart("The place most people came via", "#", "URL", "Number", "Percent");
        }
        else
        {
            utskrift.PrintTableStart("The place most people came via", "#", "URL", "Number");
        }
        int skrivnaRader = 0;
        while (!refDatabas.isEmpty() && (skrivnaRader < SHOWNUMBEROFLINES || SHOWNUMBEROFLINES == 0))
        {
            e = refDatabas.elements();
            dbEntry topp = (dbEntry)e.nextElement();
            int max = topp.getAntal();
            while (e.hasMoreElements())
            {
                dbEntry he = (dbEntry) e.nextElement();
                if (he.getAntal() > max)
                {
                    topp = he;
                    max = he.getAntal();
                }
            }
            if (PROCENT)
            {
                if (topp.getName().equalsIgnoreCase("-"))
                {
                    utskrift.PrintNumberedTableEntry("Nowhere", String.valueOf(topp.getAntal()),
                                                     StringTools.DoubleToString((100 * topp.getAntal() / (double) total), 2) + "%");
                }
                else
                {
                    utskrift.PrintNumberedTableEntryWithLinks(topp.getName(), StringTools.StringToURLString(topp.getName()),
                                                              String.valueOf(topp.getAntal()),
                                                              StringTools.DoubleToString((100 * topp.getAntal() / (double) total), 2) + "%");
                }
            }
            else
            {
                if (topp.getName().equalsIgnoreCase("-"))
                {
                    utskrift.PrintNumberedTableEntry("Nowhere", String.valueOf(topp.getAntal()));
                }
                else
                {
                    utskrift.PrintNumberedTableEntryWithLinks(topp.getName(), StringTools.StringToURLString(topp.getName()),
                                                              String.valueOf(topp.getAntal()));
                }
            }
            skrivnaRader++;
            refDatabas.remove(topp.getName());
        }
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }

    private static void ComputeFiles(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in file i en speciell filedatabas
        int i = 0;
        Hashtable fileDatabas = new Hashtable();
        Enumeration e = databas.elements();
        int total = databas.size();

        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            if (fileDatabas.containsKey(ae.getFile()))
            { // Finns redan i databasen
                dbEntry h = (dbEntry) fileDatabas.get(ae.getFile());
                h.incAntal();
            }
            else
            {
                dbEntry h = new dbEntry(ae.getFile());
                h.setAntal(1);
                fileDatabas.put(ae.getFile(), h);
            }
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }
        // Skriv ut en sorterad lista av file
        utskrift.PrintLinkHandle("files");
        if (PROCENT)
        {
            utskrift.PrintTableStart("Most accessed files", "#", "File", "Accesses", "Percent");
        }
        else
        {
            utskrift.PrintTableStart("Most accessed files", "#", "File", "Accesses");
        }
        int skrivnaRader = 0;
        while (!fileDatabas.isEmpty() && (skrivnaRader < SHOWNUMBEROFLINES || SHOWNUMBEROFLINES == 0))

        {
            e = fileDatabas.elements();
            dbEntry topp = (dbEntry)e.nextElement();
            int max = topp.getAntal();
            while (e.hasMoreElements())
            {
                dbEntry he = (dbEntry) e.nextElement();
                if (he.getAntal() > max)
                {
                    topp = he;
                    max = he.getAntal();
                }
            }

            if (PROCENT)
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()),
                                                 StringTools.DoubleToString((100 * topp.getAntal() / (double) total), 2) + "%");
            }
            else
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()));
            }
            skrivnaRader++;
            fileDatabas.remove(topp.getName());
        }
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }

//***************************************************************************
//**
//** ComputeBrowsers: Fixar statistik p� vilken l�sare folk anv�nt
//**
//***************************************************************************
    private static void ComputeBrowsers(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in browser i en speciell brodatabas
        int i = 0;
        Hashtable broDatabas = new Hashtable();
        Enumeration e = databas.elements();
        int total = databas.size();

        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            if (broDatabas.containsKey(ae.getBrowser()))
            { // Finns redan i databasen
                dbEntry h = (dbEntry) broDatabas.get(ae.getBrowser());
                h.incAntal();
            }
            else
            {
                dbEntry h = new dbEntry(ae.getBrowser());
                h.setAntal(1);
                broDatabas.put(ae.getBrowser(), h);
            }
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }
        // Skriv ut en sorterad lista av browsers
        utskrift.PrintLinkHandle("browsers");
        if (PROCENT)
        {
            utskrift.PrintTableStart("Most used browsers", "#", "Browsers", "Number", "Percent");
        }
        else
        {
            utskrift.PrintTableStart("Most used browsers", "#", "Browser", "Number");
        }
        int skrivnaRader = 0;
        while (!broDatabas.isEmpty() && (skrivnaRader < SHOWNUMBEROFLINES || SHOWNUMBEROFLINES == 0))
        {
            e = broDatabas.elements();
            dbEntry topp = (dbEntry)e.nextElement();
            int max = topp.getAntal();
            while (e.hasMoreElements())
            {
                // This will always be entered since broDatabas.isEmpty returned false above
                dbEntry he = (dbEntry) e.nextElement();
                if (he.getAntal() > max)
                {
                    topp = he;
                    max = he.getAntal();
                }
            }

            if (PROCENT)
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()),
                                                 StringTools.DoubleToString((100 * topp.getAntal() / (double) total), 2) + "%");
            }
            else
            {
                utskrift.PrintNumberedTableEntry(topp.getName(), String.valueOf(topp.getAntal()));
            }
            skrivnaRader++;
            broDatabas.remove(topp.getName());
        }
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }


//***************************************************************************
//**
//** ComputeTimeOfDay: Skriver ut statistik �ver n�r p� dagen folk kommer
//**
//***************************************************************************
    private static void ComputeTimeOfDay(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in tid i en speciell filedatabas
        int[] antalVidTimme = new int[24];
        int timme;
        int i = 0;
        Enumeration e = databas.elements();
        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            timme = ae.getTime().getHours();
            antalVidTimme[timme]++;
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }
        int max = antalVidTimme[0];
        for (int k = 1; k < 24; k++) if (max < antalVidTimme[k]) max = antalVidTimme[k];

        // Skriv ut en lista i tidsordning
        utskrift.PrintLinkHandle("hourofday");
        utskrift.PrintGraphTableStart("Which hour do people visit?", "Hour", "Number", "", max);
        for (int j = 0; j < 24; j++)
        {
            utskrift.PrintTableEntryWithGraph(j + "-" + (j + 1), antalVidTimme[j], max);
        }
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }

//***************************************************************************
//**
//** ComputeMonthOfYear: Skriver ut statistik �ver n�r p� �ret folk har kommit
//**
//***************************************************************************
    private static void ComputeMonthOfYear(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in tid i en speciell filedatabas
        int[] antalUnderM�nad = new int[12];
        int m�nad;
        int i = 0;
        Enumeration e = databas.elements();
        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            m�nad = ae.getTime().getMonth();
            antalUnderM�nad[m�nad]++;
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }

        // Plocka ut h�gst antalet som f�rekommit en m�nad.
        int max = antalUnderM�nad[0];
        for (int k = 1; k < 12; k++) if (max < antalUnderM�nad[k]) max = antalUnderM�nad[k];

        // Skriv ut en lista i tidsordning.
        utskrift.PrintLinkHandle("monthofyear");
        utskrift.PrintGraphTableStart("Which Month did people visit?", "Month", "Number", "", max);
        for (int j = 0; j < 12; j++)
        {
            utskrift.PrintTableEntryWithGraph(DateTools.intToMonthString(j, 0), antalUnderM�nad[j], max);
        }
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }



//***************************************************************************
//**
//** ComputeDayOfWeek: Skriver ut statistik �ver vilken dag i veckan folk har kommit
//**
//***************************************************************************
    private static void ComputeDayOfWeek(TablePrint utskrift, Hashtable databas)
    {
        // L�gg in tid i en speciell filedatabas
        int[] antalP�Veckodag = new int[7];
        int veckodag;
        int i = 0;
        Enumeration e = databas.elements();
        if (!e.hasMoreElements()) return; // Databasen tom
        AccessEntry ae = (AccessEntry) e.nextElement();
        for (; e.hasMoreElements(); ae = (AccessEntry) e.nextElement())
        {
            // System.err.println(ae);
            veckodag = ae.getTime().getDay();
            antalP�Veckodag[veckodag]++;
            if (DEBUG)
            {
                i++;
                if (i % 10 == 0)
                {
                    System.out.print(".");
                    System.out.flush();
                }
            }
        }

        // Plocka ut h�gst antalet som f�rekommit p� en veckodag.
        int max = antalP�Veckodag[0];
        for (int k = 1; k < 7; k++) if (max < antalP�Veckodag[k]) max = antalP�Veckodag[k];

        // Skriv ut en lista i tidsordning.
        utskrift.PrintLinkHandle("dayofweek");
        utskrift.PrintGraphTableStart("Which weekday do people visit?", "Weekday", "Number", "", max);

        // Varg�r ska amerikanerna ha s�ndag f�rst? Specialbehandlas!
        for (int j = 1; j < 7; j++)
        {
            utskrift.PrintTableEntryWithGraph(DateTools.intToWeekdayString(j, 0), antalP�Veckodag[j], max);
        }
        utskrift.PrintTableEntryWithGraph(DateTools.intToWeekdayString(0, 0), antalP�Veckodag[0], max);
        utskrift.PrintTableEnd();
        PrintLinkToIndex(utskrift);
    }

    private static void PrintLinkToIndex(TablePrint utskrift)
    {
        utskrift.PrintStartUnorderedList();
        utskrift.PrintListLink("#index", "Index");
        utskrift.PrintEndUnorderedList();
    }


    // Globala variabler
    private static String inFilnamn = null; // Inget default d� anv�ndaren m�ste ge ett filnamn sj�lv
    private static PrintStream outPrintStream = System.out; // Som default s� har vi konsollen som mottagare av utdata

    // Flaggvariabler
    private static boolean HTML = false;
    private static boolean DEBUG = false;
    private static final boolean FILEFILTER = true;
    private static boolean PROCENT = true; // Ska procenttal l�ggas till
    private static boolean SHOWHOUROFDAY = false;
    private static boolean SHOWMONTHOFYEAR = false;
    private static boolean SHOWDAYOFWEEK = false;
    private static boolean SHOWHOSTS = false;
    private static boolean SHOWDOMAINS = false;
    private static boolean SHOWREFERERS = false;
    private static boolean SHOWFILES = false;
    private static boolean SHOWBROWSERS = false;

    private static int SHOWNUMBEROFLINES = 0; // Hur m�nga rader i tabellerna 0=o�ndligt m�nga
    private static int TIDSPERIOD = -1; // Hur l�ngt tillbaks i tiden ska vi g�? -1 = hela logfilen

    private static final String[] FILEFILTERSUFFIX = {"gif", "jpg"}; // Vilka filtyper ska filtreras bort?

    // Konstanter
    private static final String VERINFO = "LOGA v0.7a";
    private static final String COPYRIGHTINFO = "(c) Daniel Bratell 1996 (bratell@lysator.liu.se)";
    private static final String HOMEPAGE = "http://www.lysator.liu.se/~bratell/loga/";
}

