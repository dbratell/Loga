
public class StringTools
{
    public static String DoubleToString(double tal, int decimalAntal)
    {
        int heltal = (int) tal;
        double decimaldel = tal - heltal;
        StringBuffer decimalSträng = new StringBuffer();

        for (int i = 0; i < decimalAntal; i++)
        {
            decimaldel = 10 * decimaldel;
            decimalSträng.append((int) decimaldel);
            decimaldel = decimaldel - (int) decimaldel;
        }
        return String.valueOf(heltal) + "." + decimalSträng;
    }


    public static String Suffix(String strängen)
    {
        int i = strängen.lastIndexOf('.');
        if (i == -1 || i == (strängen.length() - 1))
        {
            // Fanns inget suffix (antingen ingen punkt eller punkt som sista tecken)
            return "";
        }
        return strängen.substring(i + 1);
    }

    public static String StringToURLString(String strängen)
    {
        StringBuffer resultat;
        Character tecken;

        int startlängd = strängen.length();
        resultat = new StringBuffer((int) (startlängd * 1.2)); // Gör den lite längre än originalet

        int teckenVärde;
        for (int i = 0; i < startlängd; i++)
        {
            tecken = new Character(strängen.charAt(i));
            teckenVärde = tecken.charValue();
            if (teckenVärde < 33 || teckenVärde > 127)
            { // Väldigt osäker på gränserna. Det här kanske funkar i alla fall
                resultat.append("%" + Integer.toString(teckenVärde, 16));
            }
            else
            {
                resultat.append(tecken.toString());
            }
        }
        return resultat.toString();
    }

    /** Translates a string with %E7 codes in it to a pure string */
    /** Will also translate + to a space */
    public static String URLStringToString(String startSträng)
    {

        int i = 0;
        StringBuffer arbetsKopia = new StringBuffer(startSträng.length());

        while (i < startSträng.length())
        {
            switch (startSträng.charAt(i))
            {
                case '%':
                    if (i + 2 >= startSträng.length())
                    { // Finns inte tillräckligt många tecken kvar.
                        // Borde kasta en exception
                        System.err.println("Errenous string: '" + startSträng + "'");
                        return startSträng;
                    }
                    int asciiKod = Integer.parseInt(startSträng.substring(i + 1, i + 3), 16);
                    arbetsKopia.append((char) asciiKod);
                    i = i + 2; // Vi läste här två tecken extra
                    break;

                case '+':
                    arbetsKopia.append(' ');
                    break;

                default:
                    arbetsKopia.append(startSträng.charAt(i));
                    break;
            }
            i++;
        }
        return arbetsKopia.toString();
    }
}
