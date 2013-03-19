package core.dashboards;
import system.*;

public class XmlMonthIntervalCategory
{
    public String Apr = "Apr";

    public String Aug = "Aug";

    public String Dec = "Dec";

    public String Feb = "Feb";

    public String Jan = "Jan";

    public String Jul = "Jul";

    public String Jun = "Jun";

    public String Mar = "Mar";

    public String May = "May";

    public String Nov = "Nov";

    public String Oct = "Oct";

    public String Sep = "Sep";

    public static  MonthInterval ToCategory(String strMonthInterval)
    {
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Jan",true) == 0){

            return MonthInterval.Jan;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Feb",true) == 0){

            return MonthInterval.Feb;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Mar",true) == 0){

            return MonthInterval.Mar;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Apr",true) == 0){

            return MonthInterval.Apr;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"May",true) == 0){

            return MonthInterval.May;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Jun",true) == 0){

            return MonthInterval.Jun;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Jul",true) == 0){

            return MonthInterval.Jul;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Aug",true) == 0){

            return MonthInterval.Aug;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Sep",true) == 0){

            return MonthInterval.Sep;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Oct",true) == 0){

            return MonthInterval.Oct;
        }
        if(clr.System.StringStaticWrapper.Compare(strMonthInterval,"Nov",true) == 0){

            return MonthInterval.Nov;
        }

        return MonthInterval.Dec;

    }

    public static  Integer ToMonthNumber(MonthInterval monthInterval)
    {
        if(monthInterval == MonthInterval.Jan){

            return 1;
        }
        else if(monthInterval == MonthInterval.Feb){

            return 2;
        }
        else if(monthInterval == MonthInterval.Mar){

            return 3;
        }
        else if(monthInterval == MonthInterval.Apr){

            return 4;
        }
        else if(monthInterval == MonthInterval.May){

            return 5;
        }
        else if(monthInterval == MonthInterval.Jun){

            return 6;
        }
        else if(monthInterval == MonthInterval.Jul){

            return 7;
        }
        else if(monthInterval == MonthInterval.Aug){

            return 8;
        }
        else if(monthInterval == MonthInterval.Sep){

            return 9;
        }
        else if(monthInterval == MonthInterval.Oct){

            return 10;
        }
        else if(monthInterval == MonthInterval.Nov){

            return 11;
        }
        else if(monthInterval == MonthInterval.Dec){

            return 12;
        }

        return 0;

    }

    public static  String ToString(MonthInterval monthInterval)
    {
        String str = "";
        if(monthInterval == MonthInterval.Jan){

            return "Jan";
        }
        else if(monthInterval == MonthInterval.Feb){

            return "Feb";
        }
        else if(monthInterval == MonthInterval.Mar){

            return "Mar";
        }
        else if(monthInterval == MonthInterval.Apr){

            return "Apr";
        }
        else if(monthInterval == MonthInterval.May){

            return "May";
        }
        else if(monthInterval == MonthInterval.Jun){

            return "Jun";
        }
        else if(monthInterval == MonthInterval.Jul){

            return "Jul";
        }
        else if(monthInterval == MonthInterval.Aug){

            return "Aug";
        }
        else if(monthInterval == MonthInterval.Sep){

            return "Sep";
        }
        else if(monthInterval == MonthInterval.Oct){

            return "Oct";
        }
        else if(monthInterval == MonthInterval.Nov){

            return "Nov";
        }
        else if(monthInterval == MonthInterval.Dec){

            return "Dec";
        }

        return str;

    }

}