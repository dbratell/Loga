
public class StringTools
{
    public static String DoubleToString(double tal, int decimalAntal)
    {
        int heltal = (int) tal;
        double decimaldel = tal - heltal;
        StringBuffer decimalStr�ng = new StringBuffer();

        for (int i = 0; i < decimalAntal; i++)
        {
            decimaldel = 10 * decimaldel;
            decimalStr�ng.append((int) decimaldel);
            decimaldel = decimaldel - (int) decimaldel;
        }
        return String.valueOf(heltal) + "." + decimalStr�ng;
    }


    public static String Suffix(String str�ngen)
    {
        int i = str�ngen.lastIndexOf('.');
        if (i == -1 || i == (str�ngen.length() - 1))
        {
            // Fanns inget suffix (antingen ingen punkt eller punkt som sista tecken)
            return "";
        }
        return str�ngen.substring(i + 1);
    }

    public static String StringToURLString(String str�ngen)
    {
        StringBuffer resultat;
        Character tecken;

        int startl�ngd = str�ngen.length();
        resultat = new StringBuffer((int) (startl�ngd * 1.2)); // G�r den lite l�ngre �n originalet

        int teckenV�rde;
        for (int i = 0; i < startl�ngd; i++)
        {
            tecken = new Character(str�ngen.charAt(i));
            teckenV�rde = tecken.charValue();
            if (teckenV�rde < 33 || teckenV�rde > 127)
            { // V�ldigt os�ker p� gr�nserna. Det h�r kanske funkar i alla fall
                resultat.append("%" + Integer.toString(teckenV�rde, 16));
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
    public static String URLStringToString(String startStr�ng)
    {

        int i = 0;
        StringBuffer arbetsKopia = new StringBuffer(startStr�ng.length());

        while (i < startStr�ng.length())
        {
            switch (startStr�ng.charAt(i))
            {
                case '%':
                    if (i + 2 >= startStr�ng.length())
                    { // Finns inte tillr�ckligt m�nga tecken kvar.
                        // Borde kasta en exception
                        System.err.println("Errenous string: '" + startStr�ng + "'");
                        return startStr�ng;
                    }
                    int asciiKod = Integer.parseInt(startStr�ng.substring(i + 1, i + 3), 16);
                    arbetsKopia.append((char) asciiKod);
                    i = i + 2; // Vi l�ste h�r tv� tecken extra
                    break;

                case '+':
                    arbetsKopia.append(' ');
                    break;

                default:
                    arbetsKopia.append(startStr�ng.charAt(i));
                    break;
            }
            i++;
        }
        return arbetsKopia.toString();
    }
}
