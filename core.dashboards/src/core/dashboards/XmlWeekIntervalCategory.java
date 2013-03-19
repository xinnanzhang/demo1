package core.dashboards;
import system.*;
import system.Runtime.InteropServices.*;

public class XmlWeekIntervalCategory
{
    public String W1 = "W1";

    public String W2 = "W2";

    public String W3 = "W3";

    public String W4 = "W4";

    public String W5 = "W5";

    public String W6 = "W6";

    public static  Integer GetStartEndMonthOfQuarter(Integer quarter, /*TODO: OUT PARAMETER, Wrapper it to array[0] */Integer[] startMonth, /*TODO: OUT PARAMETER, Wrapper it to array[0] */Integer[] endMonth)
    {
        startMonth[0] = 1;
        endMonth[0] = 3;
        if(quarter == 1){
            startMonth[0] = 1;
            endMonth[0] = 3;

            return startMonth[0];
        }
        else if(quarter == 2){
            startMonth[0] = 4;
            endMonth[0] = 6;

            return startMonth[0];
        }
        else if(quarter == 3){
            startMonth[0] = 7;
            endMonth[0] = 9;

            return startMonth[0];
        }
        else if(quarter == 4){
            startMonth[0] = 10;
            endMonth[0] = 12;

            return startMonth[0];
        }

        return startMonth[0];

    }

    public static  WeekInterval ToCategory(String strWeekInterval)
    {
        if(clr.System.StringStaticWrapper.Compare(strWeekInterval,"W1",true) == 0){

            return WeekInterval.W1;
        }
        if(clr.System.StringStaticWrapper.Compare(strWeekInterval,"W2",true) == 0){

            return WeekInterval.W2;
        }
        if(clr.System.StringStaticWrapper.Compare(strWeekInterval,"W3",true) == 0){

            return WeekInterval.W3;
        }
        if(clr.System.StringStaticWrapper.Compare(strWeekInterval,"W4",true) == 0){

            return WeekInterval.W4;
        }
        if(clr.System.StringStaticWrapper.Compare(strWeekInterval,"W5",true) == 0){

            return WeekInterval.W5;
        }

        return WeekInterval.W6;

    }

    public static  String ToString(WeekInterval weekInterval)
    {
        String str = "";
        if(weekInterval == WeekInterval.W1){

            return "W1";
        }
        else if(weekInterval == WeekInterval.W2){

            return "W2";
        }
        else if(weekInterval == WeekInterval.W3){

            return "W3";
        }
        else if(weekInterval == WeekInterval.W4){

            return "W4";
        }
        else if(weekInterval == WeekInterval.W5){

            return "W5";
        }
        else if(weekInterval == WeekInterval.W6){

            return "W6";
        }

        return str;

    }

    public static  Integer ToWeekNumber(WeekInterval weekInterval)
    {
        if(weekInterval == WeekInterval.W1){

            return 1;
        }
        else if(weekInterval == WeekInterval.W2){

            return 2;
        }
        else if(weekInterval == WeekInterval.W3){

            return 3;
        }
        else if(weekInterval == WeekInterval.W4){

            return 4;
        }
        else if(weekInterval == WeekInterval.W5){

            return 5;
        }

        return 6;

    }

}