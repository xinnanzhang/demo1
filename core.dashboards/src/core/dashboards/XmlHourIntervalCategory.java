package core.dashboards;
import system.*;

public class XmlHourIntervalCategory
{
    public String EightAM = "EightAM";

    public String EightPM = "EightPM";

    public String ElevenAM = "ElevenAM";

    public String ElevenPM = "ElevenPM";

    public String FiveAM = "FiveAM";

    public String FivePM = "FivePM";

    public String FourAM = "FourAM";

    public String FourPM = "FourPM";

    public String NineAM = "NineAM";

    public String NinePM = "NinePM";

    public String OneAM = "OneAM";

    public String OnePM = "OnePM";

    public String SevenAM = "SevenAM";

    public String SevenPM = "SevenPM";

    public String SixAM = "SixAM";

    public String SixPM = "SixPM";

    public String TenAM = "TenAM";

    public String TenPM = "TenPM";

    public String ThreeAM = "ThreeAM";

    public String ThreePM = "ThreePM";

    public String TwelveAM = "TwelveAM";

    public String TwelvePM = "TwelvePM";

    public String TwoAM = "TwoAM";

    public String TwoPM = "TwoPM";

    public static  HourInterval ToCategory(String strHourInterval)
    {
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"OneAM",true) == 0){

            return HourInterval.OneAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"TwoAM",true) == 0){

            return HourInterval.TwoAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"ThreeAM",true) == 0){

            return HourInterval.ThreeAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"FourAM",true) == 0){

            return HourInterval.FourAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"FiveAM",true) == 0){

            return HourInterval.FiveAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"SixAM",true) == 0){

            return HourInterval.SixAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"SevenAM",true) == 0){

            return HourInterval.SevenAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"EightAM",true) == 0){

            return HourInterval.EightAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"NineAM",true) == 0){

            return HourInterval.NineAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"TenAM",true) == 0){

            return HourInterval.TenAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"ElevenAM",true) == 0){

            return HourInterval.ElevenAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"TwelveAM",true) == 0){

            return HourInterval.TwelveAM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"OnePM",true) == 0){

            return HourInterval.OnePM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"TwoPM",true) == 0){

            return HourInterval.TwoPM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"ThreePM",true) == 0){

            return HourInterval.ThreePM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"FourPM",true) == 0){

            return HourInterval.FourPM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"FivePM",true) == 0){

            return HourInterval.FivePM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"SixPM",true) == 0){

            return HourInterval.SixPM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"SevenPM",true) == 0){

            return HourInterval.SevenPM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"EightPM",true) == 0){

            return HourInterval.EightPM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"NinePM",true) == 0){

            return HourInterval.NinePM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"TenPM",true) == 0){

            return HourInterval.TenPM;
        }
        if(clr.System.StringStaticWrapper.Compare(strHourInterval,"ElevenPM",true) == 0){

            return HourInterval.ElevenPM;
        }

        return HourInterval.TwelvePM;

    }

    public static  Integer ToHourNumber(HourInterval hourInterval)
    {
        if(hourInterval == HourInterval.TwelveAM){

            return 0;
        }
        else if(hourInterval == HourInterval.OneAM){

            return 1;
        }
        else if(hourInterval == HourInterval.TwoAM){

            return 2;
        }
        else if(hourInterval == HourInterval.ThreeAM){

            return 3;
        }
        else if(hourInterval == HourInterval.FourAM){

            return 4;
        }
        else if(hourInterval == HourInterval.FiveAM){

            return 5;
        }
        else if(hourInterval == HourInterval.SixAM){

            return 6;
        }
        else if(hourInterval == HourInterval.SevenAM){

            return 7;
        }
        else if(hourInterval == HourInterval.EightAM){

            return 8;
        }
        else if(hourInterval == HourInterval.NineAM){

            return 9;
        }
        else if(hourInterval == HourInterval.TenAM){

            return 10;
        }
        else if(hourInterval == HourInterval.ElevenAM){

            return 11;
        }
        else if(hourInterval == HourInterval.TwelvePM){

            return 12;
        }
        else if(hourInterval == HourInterval.OnePM){

            return 13;
        }
        else if(hourInterval == HourInterval.TwoPM){

            return 14;
        }
        else if(hourInterval == HourInterval.ThreePM){

            return 15;
        }
        else if(hourInterval == HourInterval.FourPM){

            return 0x10;
        }
        else if(hourInterval == HourInterval.FivePM){

            return 0x11;
        }
        else if(hourInterval == HourInterval.SixPM){

            return 0x12;
        }
        else if(hourInterval == HourInterval.SevenPM){

            return 0x13;
        }
        else if(hourInterval == HourInterval.EightPM){

            return 20;
        }
        else if(hourInterval == HourInterval.NinePM){

            return 0x15;
        }
        else if(hourInterval == HourInterval.TenPM){

            return 0x16;
        }
        else if(hourInterval == HourInterval.ElevenPM){

            return 0x17;
        }

        return 0;

    }

    public static  String ToString(HourInterval hourInterval)
    {
        String str = "";
        if(hourInterval == HourInterval.TwelveAM){

            return "TwelveAM";
        }
        else if(hourInterval == HourInterval.OneAM){

            return "OneAM";
        }
        else if(hourInterval == HourInterval.TwoAM){

            return "TwoAM";
        }
        else if(hourInterval == HourInterval.ThreeAM){

            return "ThreeAM";
        }
        else if(hourInterval == HourInterval.FourAM){

            return "FourAM";
        }
        else if(hourInterval == HourInterval.FiveAM){

            return "FiveAM";
        }
        else if(hourInterval == HourInterval.SixAM){

            return "SixAM";
        }
        else if(hourInterval == HourInterval.SevenAM){

            return "SevenAM";
        }
        else if(hourInterval == HourInterval.EightAM){

            return "EightAM";
        }
        else if(hourInterval == HourInterval.NineAM){

            return "NineAM";
        }
        else if(hourInterval == HourInterval.TenAM){

            return "TenAM";
        }
        else if(hourInterval == HourInterval.ElevenAM){

            return "ElevenAM";
        }
        else if(hourInterval == HourInterval.TwelvePM){

            return "TwelvePM";
        }
        else if(hourInterval == HourInterval.OnePM){

            return "OnePM";
        }
        else if(hourInterval == HourInterval.TwoPM){

            return "TwoPM";
        }
        else if(hourInterval == HourInterval.ThreePM){

            return "ThreePM";
        }
        else if(hourInterval == HourInterval.FourPM){

            return "FourPM";
        }
        else if(hourInterval == HourInterval.FivePM){

            return "FivePM";
        }
        else if(hourInterval == HourInterval.SixPM){

            return "SixPM";
        }
        else if(hourInterval == HourInterval.SevenPM){

            return "SevenPM";
        }
        else if(hourInterval == HourInterval.EightPM){

            return "EightPM";
        }
        else if(hourInterval == HourInterval.NinePM){

            return "NinePM";
        }
        else if(hourInterval == HourInterval.TenPM){

            return "TenPM";
        }
        else if(hourInterval == HourInterval.ElevenPM){

            return "ElevenPM";
        }

        return str;

    }

}