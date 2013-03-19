package core.dashboards;

public class XmlQuarterIntervalCategory
{
    public String Q1 = "Q1";

    public String Q2 = "Q2";

    public String Q3 = "Q3";

    public String Q4 = "Q4";

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

    public static  QuarterInterval ToCategory(String strQuarterInterval)
    {
        if(clr.System.StringStaticWrapper.Compare(strQuarterInterval,"Q1",true) == 0){

            return QuarterInterval.Q1;
        }
        if(clr.System.StringStaticWrapper.Compare(strQuarterInterval,"Q2",true) == 0){

            return QuarterInterval.Q2;
        }
        if(clr.System.StringStaticWrapper.Compare(strQuarterInterval,"Q3",true) == 0){

            return QuarterInterval.Q3;
        }

        return QuarterInterval.Q4;

    }

    public static  Integer ToQuarterNumber(QuarterInterval quarterInterval)
    {
        if(quarterInterval == QuarterInterval.Q1){

            return 1;
        }
        else if(quarterInterval == QuarterInterval.Q2){

            return 2;
        }
        else if(quarterInterval == QuarterInterval.Q3){

            return 3;
        }
        else if(quarterInterval == QuarterInterval.Q4){

            return 4;
        }

        return 0;

    }

    public static  String ToString(QuarterInterval quarterInterval)
    {
        String str = "";
        if(quarterInterval == QuarterInterval.Q1){

            return "Q1";
        }
        else if(quarterInterval == QuarterInterval.Q2){

            return "Q2";
        }
        else if(quarterInterval == QuarterInterval.Q3){

            return "Q3";
        }
        else if(quarterInterval == QuarterInterval.Q4){

            return "Q4";
        }

        return str;

    }

}