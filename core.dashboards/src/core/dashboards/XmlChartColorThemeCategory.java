package core.dashboards;
import system.*;

public class XmlChartColorThemeCategory
{
    public String Accent = "Accent";

    public String AgedNaturals = "AgedNaturals";

    public String ArtNouveau = "ArtNouveau";

    public String Autumn = "Autumn";

    public String BrowserSafeI = "BrowserSafeI";

    public String BrowserSafeII = "BrowserSafeII";

    public String CoolHues = "CoolHues";

    public String Culture = "Culture";

    public String DarkenTones = "DarkenTones";

    public String Default = "Default";

    public String EclecticMix = "EclecticMix";

    public String Fashion = "Fashion";

    public String FRS = "FRS";

    public String HistoricalCombinations = "HistoricalCombinations";

    public String IntenseNaturals = "IntenseNaturals";

    public String Masters = "Masters";

    public String MixedCombinations = "MixedCombinations";

    public String Primary = "Primary";

    public String ProgressiveNaturals = "ProgressiveNaturals";

    public String QuietTones = "QuietTones";

    public String RichHues = "RichHues";

    public String Shasta = "Shasta";

    public String UrbanHues = "UrbanHues";

    public static  ChartColorTheme ToCategory(String strColorTheme)
    {
        ChartColorTheme urbanHues = ChartColorTheme.Default;
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Default",true) == 0){

            return ChartColorTheme.Default;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Accent",true) == 0){

            return ChartColorTheme.Accent;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"AgedNaturals",true) == 0){

            return ChartColorTheme.AgedNaturals;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"ArtNouveau",true) == 0){

            return ChartColorTheme.ArtNouveau;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Autumn",true) == 0){

            return ChartColorTheme.Autumn;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"BrowserSafeI",true) == 0){

            return ChartColorTheme.BrowserSafeI;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"BrowserSafeII",true) == 0){

            return ChartColorTheme.BrowserSafeII;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"CoolHues",true) == 0){

            return ChartColorTheme.CoolHues;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Culture",true) == 0){

            return ChartColorTheme.Culture;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"DarkenTones",true) == 0){

            return ChartColorTheme.DarkenTones;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"EclecticMix",true) == 0){

            return ChartColorTheme.EclecticMix;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Fashion",true) == 0){

            return ChartColorTheme.Fashion;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"FRS",true) == 0){

            return ChartColorTheme.FRS;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"HistoricalCombinations",true) == 0){

            return ChartColorTheme.HistoricalCombinations;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"IntenseNaturals",true) == 0){

            return ChartColorTheme.IntenseNaturals;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Masters",true) == 0){

            return ChartColorTheme.Masters;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"MixedCombinations",true) == 0){

            return ChartColorTheme.MixedCombinations;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Primary",true) == 0){

            return ChartColorTheme.Primary;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"ProgressiveNaturals",true) == 0){

            return ChartColorTheme.ProgressiveNaturals;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"QuietTones",true) == 0){

            return ChartColorTheme.QuietTones;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"RichHues",true) == 0){

            return ChartColorTheme.RichHues;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"Shasta",true) == 0){

            return ChartColorTheme.Shasta;
        }
        if(clr.System.StringStaticWrapper.Compare(strColorTheme,"UrbanHues",true) == 0){
            urbanHues = ChartColorTheme.UrbanHues;
        }

        return urbanHues;

    }

    public static  String ToString(ChartColorTheme colorTheme)
    {
        if(colorTheme == ChartColorTheme.Default){

            return "Default";
        }
        else if(colorTheme == ChartColorTheme.Accent){

            return "Accent";
        }
        else if(colorTheme == ChartColorTheme.AgedNaturals){

            return "AgedNaturals";
        }
        else if(colorTheme == ChartColorTheme.ArtNouveau){

            return "ArtNouveau";
        }
        else if(colorTheme == ChartColorTheme.Autumn){

            return "Autumn";
        }
        else if(colorTheme == ChartColorTheme.BrowserSafeI){

            return "BrowserSafeI";
        }
        else if(colorTheme == ChartColorTheme.BrowserSafeII){

            return "BrowserSafeII";
        }
        else if(colorTheme == ChartColorTheme.CoolHues){

            return "CoolHues";
        }
        else if(colorTheme == ChartColorTheme.Culture){

            return "Culture";
        }
        else if(colorTheme == ChartColorTheme.DarkenTones){

            return "DarkenTones";
        }
        else if(colorTheme == ChartColorTheme.EclecticMix){

            return "EclecticMix";
        }
        else if(colorTheme == ChartColorTheme.Fashion){

            return "Fashion";
        }
        else if(colorTheme == ChartColorTheme.FRS){

            return "FRS";
        }
        else if(colorTheme == ChartColorTheme.HistoricalCombinations){

            return "HistoricalCombinations";
        }
        else if(colorTheme == ChartColorTheme.IntenseNaturals){

            return "IntenseNaturals";
        }
        else if(colorTheme == ChartColorTheme.Masters){

            return "Masters";
        }
        else if(colorTheme == ChartColorTheme.MixedCombinations){

            return "MixedCombinations";
        }
        else if(colorTheme == ChartColorTheme.Primary){

            return "Primary";
        }
        else if(colorTheme == ChartColorTheme.ProgressiveNaturals){

            return "ProgressiveNaturals";
        }
        else if(colorTheme == ChartColorTheme.QuietTones){

            return "QuietTones";
        }
        else if(colorTheme == ChartColorTheme.RichHues){

            return "RichHues";
        }
        else if(colorTheme == ChartColorTheme.Shasta){

            return "Shasta";
        }
        else if(colorTheme == ChartColorTheme.UrbanHues){

            return "UrbanHues";
        }

        return "Default";

    }

}