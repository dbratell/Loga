
/**
 * @author Daniel Bratell
 */
public class DateTools
{

    public static int monthToInt(String month)
    {
        if (month.equalsIgnoreCase("jan") || month.equalsIgnoreCase("january")) return 0;
        if (month.equalsIgnoreCase("feb") || month.equalsIgnoreCase("february")) return 1;
        if (month.equalsIgnoreCase("mar") || month.equalsIgnoreCase("mars")) return 2;
        if (month.equalsIgnoreCase("apr") || month.equalsIgnoreCase("april")) return 3;
        if (month.equalsIgnoreCase("may") || month.equalsIgnoreCase("may")) return 4;
        if (month.equalsIgnoreCase("jun") || month.equalsIgnoreCase("june")) return 5;
        if (month.equalsIgnoreCase("jul") || month.equalsIgnoreCase("july")) return 6;
        if (month.equalsIgnoreCase("aug") || month.equalsIgnoreCase("august")) return 7;
        if (month.equalsIgnoreCase("sep") || month.equalsIgnoreCase("september")) return 8;
        if (month.equalsIgnoreCase("oct") || month.equalsIgnoreCase("october")) return 9;
        if (month.equalsIgnoreCase("nov") || month.equalsIgnoreCase("november")) return 10;
        if (month.equalsIgnoreCase("dec") || month.equalsIgnoreCase("december")) return 11;

        System.err.println(month + " is not a legal month! (monthToInt)");
        return -1;
    }

    public static String intToMonthString(int number, int length)
    {
        String monthString;
        if (number > 11) number = number % 12;

        switch (number)
        {
            case 0:
                monthString = "January";
                break;

            case 1:
                monthString = "February";
                break;

            case 2:
                monthString = "Mars";
                break;

            case 3:
                monthString = "April";
                break;

            case 4:
                monthString = "May";
                break;

            case 5:
                monthString = "June";
                break;

            case 6:
                monthString = "July";
                break;

            case 7:
                monthString = "August";
                break;

            case 8:
                monthString = "September";
                break;

            case 9:
                monthString = "October";
                break;

            case 10:
                monthString = "November";
                break;

            case 11:
                monthString = "December";
                break;

            default: // Om number är negativ
                System.err.println(number + " is not a legal month number! (DateTools.intToMonthString)");
                return "";
        }
        if (length > 0 && length < monthString.length())
        {
            // Kapa längden på strängen såsom önskas
            monthString = monthString.substring(0, length);
        }

        return monthString;

    }

    public static String intToWeekdayString(int number, int length)
    {
        String weekdayString;
        if (number > 6) number = number % 7;

        switch (number)
        {
            case 0:
                weekdayString = "Sunday";
                break;

            case 1:
                weekdayString = "Monday";
                break;

            case 2:
                weekdayString = "Tuesday";
                break;

            case 3:
                weekdayString = "Wednesday";
                break;

            case 4:
                weekdayString = "Thursday";
                break;

            case 5:
                weekdayString = "Friday";
                break;

            case 6:
                weekdayString = "Saturday";
                break;

            default: // Om number är negativ
                System.err.println(number + " is not a legal weekday number! (DateTools.intToWeekdayString)");
                return "";
        }
        if (length > 0 && length < weekdayString.length())
        {
            // Kapa längden på strängen såsom önskas
            weekdayString = weekdayString.substring(0, length);
        }

        return weekdayString;
    }
}


