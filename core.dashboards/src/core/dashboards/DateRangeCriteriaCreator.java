package core.dashboards;
import Core.Dashboards.AmountOfTime;
import Core.Dashboards.DateRange;
import Core.Dashboards.DateRangeDef;
import Siteview.*;
import Siteview.Api.*;
import Siteview.Xml.*;
import system.*;
import system.Runtime.InteropServices.*;
import system.Threading.*;
import system.Threading.Thread;
import system.Xml.*;

public class DateRangeCriteriaCreator
{
    public static  SiteviewQuery AddAmountOfTimeDateRangeCriteria(SiteviewQuery query, DateRangeDef dateRangeDef)
    {
        XmlElement pastOrNextCriteria = GetPastOrNextCriteria(query, dateRangeDef);
        if(query.get_BusObSearchCriteria() != null){
            query.set_BusObSearchCriteria(query.get_CriteriaBuilder().AndExpressions(pastOrNextCriteria,query.get_BusObSearchCriteria()));

            return query;
        }
        query.set_BusObSearchCriteria(pastOrNextCriteria);

        return query;

    }

    public static  SiteviewQuery AddCustomDateRangeCriteria(SiteviewQuery query, String strField, DateTime dtStart, DateTime dtEnd)
    {
        XmlElement element = GetCustomCriteria(query, strField, dtStart, dtEnd);
        if(query.get_BusObSearchCriteria() != null){
            query.set_BusObSearchCriteria(query.get_CriteriaBuilder().AndExpressions(element,query.get_BusObSearchCriteria()));

            return query;
        }
        query.set_BusObSearchCriteria(element);

        return query;

    }

    public static  SiteviewQuery AddDateRangeCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, DateRange dateRange, String strField)
    {
        XmlElement element = GetDateRangeCriteria(iSiteviewApi, query, dateRange, strField);
        if(element != null){
            if(query.get_BusObSearchCriteria() != null){
                query.set_BusObSearchCriteria(query.get_CriteriaBuilder().AndExpressions(element,query.get_BusObSearchCriteria()));

                return query;
            }
            query.set_BusObSearchCriteria(element);
        }

        return query;

    }

    public static  SiteviewQuery AddDefaultDateRangeCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, DateRangeDef dateRangeDef)
    {
        SiteviewQuery query2 = null;
        if(dateRangeDef.get_IsLengthOfTime()){


            return AddDateRangeCriteria(iSiteviewApi, query, dateRangeDef.get_DateRange(), dateRangeDef.get_DateTimeField());
        }
        if(dateRangeDef.get_IsAmountOfTime()){


            return AddAmountOfTimeDateRangeCriteria(query, dateRangeDef);
        }
        if(dateRangeDef.get_IsSpecificDateRange()){
            
            query2 = AddSpecificDateRangeCriteria(iSiteviewApi, query, dateRangeDef);
        }

        return query2;

    }

    public static  SiteviewQuery AddFiscalDateRangeCriteria(ISiteviewApi api, SiteviewQuery query, String strField, String strFiscalPeriod)
    {
        XmlElement element = GetDateRangeCriteria(api, query, strFiscalPeriod, strField);
        if(element != null){
            if(query.get_BusObSearchCriteria() != null){
                query.set_BusObSearchCriteria(query.get_CriteriaBuilder().AndExpressions(element,query.get_BusObSearchCriteria()));

                return query;
            }
            query.set_BusObSearchCriteria(element);
        }

        return query;

    }

    public static  SiteviewQuery AddSpecificDateRangeCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, DateRangeDef dateRangeDef)
    {
        DateTime startDateTimeForSpecificDateRange = GetStartDateTimeForSpecificDateRange(iSiteviewApi, dateRangeDef);
        DateTime endDateTimeForSpecificDateRange = GetEndDateTimeForSpecificDateRange(iSiteviewApi, dateRangeDef);


        return AddCustomDateRangeCriteria(query, dateRangeDef.get_DateTimeField(), startDateTimeForSpecificDateRange, endDateTimeForSpecificDateRange);

    }

    public static  XmlElement AppendDefaultDateRangeCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, DateRangeDef dateRangeDef, XmlElement xeCriteria)
    {
        XmlElement pastOrNextCriteria = null;
        if(dateRangeDef.get_IsLengthOfTime()){
            
            pastOrNextCriteria = GetDateRangeCriteria(iSiteviewApi, query, dateRangeDef.get_DateRange(), dateRangeDef.get_DateTimeField());
        }
        else if(dateRangeDef.get_IsAmountOfTime()){
            
            pastOrNextCriteria = GetPastOrNextCriteria(query, dateRangeDef);
        }
        else if(dateRangeDef.get_IsSpecificDateRange()){
            
            pastOrNextCriteria = GetSpecificDateRangeCriteria(iSiteviewApi, query, dateRangeDef, dateRangeDef.get_DateTimeField());
        }
        if(xeCriteria != null){
            
            xeCriteria = query.get_CriteriaBuilder().ImportSearchCriteria(xeCriteria);
            
            pastOrNextCriteria = query.get_CriteriaBuilder().AndExpressions(pastOrNextCriteria, xeCriteria);
        }

        return pastOrNextCriteria;

    }

    public static  XmlElement GetCriteriaForDateRanges(SiteviewQuery query, String strField, DateTime dtFirstDay, DateTime dtLastDay)
    {
        XmlElement xeFunction = query.get_CriteriaBuilder().get_QueryFunction().SearchByDateOnly(ValueSources.Field, strField);
        XmlElement xeListExpression = query.get_CriteriaBuilder().QueryFunctionBetweenExpression(xeFunction);
        XmlElement xeQueryFunction = query.get_CriteriaBuilder().get_QueryFunction().SearchByDateOnly(ValueSources.Literal, ConversionUtils.DateTimeToString(dtFirstDay,ConversionUtils.DateTimeFormat.ISOFormat));
        XmlElement element4 = query.get_CriteriaBuilder().get_QueryFunction().SearchByDateOnly(ValueSources.Literal, ConversionUtils.DateTimeToString(dtLastDay,ConversionUtils.DateTimeFormat.ISOFormat));
        query.get_CriteriaBuilder().AddQueryFunctionValue(xeListExpression,xeQueryFunction);
        query.get_CriteriaBuilder().AddQueryFunctionValue(xeListExpression,element4);

        return xeListExpression;

    }

    public static  XmlElement GetCustomCriteria(SiteviewQuery query, String strField, DateTime dtStart, DateTime dtEnd)
    {


        return GetCriteriaForDateRanges(query, strField, dtStart, dtEnd);

    }

    public static  XmlElement GetDateRangeCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, DateRange dateRange, String strField)
    {
        XmlElement element = null;
        if(dateRange == DateRange.Today){


            return GetTodayCriteria(query, strField);
        }
        else if(dateRange == DateRange.Yesterday){


            return GetYesterdayCriteria(query, strField);
        }
        else if(dateRange == DateRange.Tomorrow){


            return GetTomorrowCriteria(query, strField);
        }
        else if(dateRange == DateRange.ThisWeek){


            return GetThisWeekCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.ThisMonth){


            return GetThisMonthCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.ThisQuarter){


            return GetThisQuarterCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.ThisYear){


            return GetThisYearCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.LastWeek){


            return GetLastWeekCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.LastMonth){


            return GetLastMonthCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.LastQuarter){


            return GetLastQuarterCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.LastYear){


            return GetLastYearCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.NextWeek){


            return GetNextWeekCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.NextMonth){


            return GetNextMonthCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.NextQuarter){


            return GetNextQuarterCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.NextYear){


            return GetNextYearCriteria(iSiteviewApi, query, strField);
        }
        else if(dateRange == DateRange.All){

            return null;
        }
        else if(dateRange == DateRange.None){

            return element;
        }

        return element;

    }

    public static  XmlElement GetDateRangeCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String fiscalPeriod, String strField)
    {
        String strFunction = "FiscalStart(" + fiscalPeriod + ")";
        String str2 = "FiscalEnd(" + fiscalPeriod + ")";
        DateTime dtFirstDay = iSiteviewApi.get_SystemFunctions().GetSystemValue(strFunction).ToDateTime();
        DateTime dtLastDay = iSiteviewApi.get_SystemFunctions().GetSystemValue(str2).ToDateTime();


        return GetCriteriaForDateRanges(query, strField, dtFirstDay, dtLastDay);

    }

    public static  DateTime GetDateTimeFromFunctionCategory(ISiteviewApi iSiteviewApi, String strFunctionCategory)
    {
        DateTime today = DateTime.get_Today();
        FunctionCategory none = FunctionCategory.None;
        if("".compareTo(strFunctionCategory) != 0){
            
            none = XmlSystemFunctionCategory.ToCategory(strFunctionCategory);
        }
        if(none == FunctionCategory.CurrentDateTime){

            return iSiteviewApi.get_SystemFunctions().get_CurrentDateTime();
        }
        else if(none == FunctionCategory.CurrentDate){

            return iSiteviewApi.get_SystemFunctions().get_CurrentDate();
        }
        else if(none == FunctionCategory.Tomorrow){

            return iSiteviewApi.get_SystemFunctions().get_Tomorrow();
        }
        else if(none == FunctionCategory.Yesterday){

            return iSiteviewApi.get_SystemFunctions().get_Yesterday();
        }
        else if(none == FunctionCategory.StartOfThisWeek){

            return iSiteviewApi.get_SystemFunctions().get_StartOfThisWeek();
        }
        else if(none == FunctionCategory.EndOfThisWeek){

            return iSiteviewApi.get_SystemFunctions().get_EndOfThisWeek();
        }
        else if(none == FunctionCategory.StartOfThisMonth){

            return iSiteviewApi.get_SystemFunctions().get_StartOfThisMonth();
        }
        else if(none == FunctionCategory.EndOfThisMonth){

            return iSiteviewApi.get_SystemFunctions().get_EndOfThisMonth();
        }
        else if(none == FunctionCategory.StartOfThisQuarter){

            return iSiteviewApi.get_SystemFunctions().get_StartOfThisQuarter();
        }
        else if(none == FunctionCategory.EndOfThisQuarter){

            return iSiteviewApi.get_SystemFunctions().get_EndOfThisQuarter();
        }
        else if(none == FunctionCategory.StartOfThisYear){

            return iSiteviewApi.get_SystemFunctions().get_StartOfThisYear();
        }
        else if(none == FunctionCategory.EndOfThisYear){

            return iSiteviewApi.get_SystemFunctions().get_EndOfThisYear();
        }
        else if(none == FunctionCategory.StartOfLastWeek){

            return iSiteviewApi.get_SystemFunctions().get_StartOfLastWeek();
        }
        else if(none == FunctionCategory.EndOfLastWeek){

            return iSiteviewApi.get_SystemFunctions().get_EndOfLastWeek();
        }
        else if(none == FunctionCategory.StartOfLastMonth){

            return iSiteviewApi.get_SystemFunctions().get_StartOfLastMonth();
        }
        else if(none == FunctionCategory.EndOfLastMonth){

            return iSiteviewApi.get_SystemFunctions().get_EndOfLastMonth();
        }
        else if(none == FunctionCategory.StartOfLastQuarter){

            return iSiteviewApi.get_SystemFunctions().get_StartOfLastQuarter();
        }
        else if(none == FunctionCategory.EndOfLastQuarter){

            return iSiteviewApi.get_SystemFunctions().get_EndOfLastQuarter();
        }
        else if(none == FunctionCategory.StartOfLastYear){

            return iSiteviewApi.get_SystemFunctions().get_StartOfLastYear();
        }
        else if(none == FunctionCategory.EndOfLastYear){

            return iSiteviewApi.get_SystemFunctions().get_EndOfLastYear();
        }
        else if(none == FunctionCategory.StartOfNextWeek){

            return iSiteviewApi.get_SystemFunctions().get_StartOfNextWeek();
        }
        else if(none == FunctionCategory.EndOfNextWeek){

            return iSiteviewApi.get_SystemFunctions().get_EndOfNextWeek();
        }
        else if(none == FunctionCategory.StartOfNextMonth){

            return iSiteviewApi.get_SystemFunctions().get_StartOfNextMonth();
        }
        else if(none == FunctionCategory.EndOfNextMonth){

            return iSiteviewApi.get_SystemFunctions().get_EndOfNextMonth();
        }
        else if(none == FunctionCategory.StartOfNextQuarter){

            return iSiteviewApi.get_SystemFunctions().get_StartOfNextQuarter();
        }
        else if(none == FunctionCategory.EndOfNextQuarter){

            return iSiteviewApi.get_SystemFunctions().get_EndOfNextQuarter();
        }
        else if(none == FunctionCategory.StartOfNextYear){

            return iSiteviewApi.get_SystemFunctions().get_StartOfNextYear();
        }
        else if(none == FunctionCategory.EndOfNextYear){

            return iSiteviewApi.get_SystemFunctions().get_EndOfNextYear();
        }
        else if(none == FunctionCategory.ServerDateTime){

            return DateTime.get_Now();
        }

        return DateTime.get_Today();

    }

    public static  DateTime GetEndDateTimeForAmountOfTimeDateRange(DateRangeDef dateRangeDef)
    {
        DateTime today = DateTime.get_Today();
        if(!dateRangeDef.get_IsPast()){
            if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Hours){


                return DateTime.get_Now().AddHours((double)dateRangeDef.get_AmountOfTimeValue());
            }
            else if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Days){


                return DateTime.get_Today().AddDays((double)dateRangeDef.get_AmountOfTimeValue());
            }
            else if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Months){


                return DateTime.get_Today().AddMonths(dateRangeDef.get_AmountOfTimeValue());
            }
            else if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Years){


                return DateTime.get_Today().AddYears(dateRangeDef.get_AmountOfTimeValue());
            }
        }

        return DateTime.get_Today();

    }

    public static  DateTime GetEndDateTimeForSpecificDateRange(ISiteviewApi iSiteviewApi, DateRangeDef dateRangeDef)
    {
        DateTime today = DateTime.get_Today();
        if(dateRangeDef.get_IsEndValueFunction()){


            return GetDateTimeFromFunctionCategory(iSiteviewApi, dateRangeDef.get_EndValue());
        }
        try{


            return ConversionUtils.StringToDateTime(dateRangeDef.get_EndValue());
        }
        catch(InvalidCastException e){

            return DateTime.get_Today();
        }

    }

    public static  Integer GetFirstMonthOfQuarter()
    {
        if(DateTime.get_Today().get_Month() == 1){

            return 1;
        }
        else if(DateTime.get_Today().get_Month() == 2){

            return 1;
        }
        else if(DateTime.get_Today().get_Month() == 3){

            return 1;
        }
        else if(DateTime.get_Today().get_Month() == 4){

            return 4;
        }
        else if(DateTime.get_Today().get_Month() == 5){

            return 4;
        }
        else if(DateTime.get_Today().get_Month() == 6){

            return 4;
        }
        else if(DateTime.get_Today().get_Month() == 7){

            return 7;
        }
        else if(DateTime.get_Today().get_Month() == 8){

            return 7;
        }
        else if(DateTime.get_Today().get_Month() == 9){

            return 7;
        }
        else if(DateTime.get_Today().get_Month() == 10){

            return 10;
        }
        else if(DateTime.get_Today().get_Month() == 11){

            return 10;
        }
        else if(DateTime.get_Today().get_Month() == 12){

            return 10;
        }

        return 1;

    }

    public static  XmlElement GetLastFiscalQuarterCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime startOfLastFiscalQuarter = iSiteviewApi.get_SystemFunctions().get_StartOfLastFiscalQuarter();
        DateTime endOfLastFiscalQuarter = iSiteviewApi.get_SystemFunctions().get_EndOfLastFiscalQuarter();


        return GetCriteriaForDateRanges(query, strField, startOfLastFiscalQuarter, endOfLastFiscalQuarter);

    }

    public static  XmlElement GetLastFiscalYearCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime startOfLastFiscalYear = iSiteviewApi.get_SystemFunctions().get_StartOfLastFiscalYear();
        DateTime endOfLastFiscalYear = iSiteviewApi.get_SystemFunctions().get_EndOfLastFiscalYear();


        return GetCriteriaForDateRanges(query, strField, startOfLastFiscalYear, endOfLastFiscalYear);

    }

    public static  XmlElement GetLastMonthCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.LastMonth, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetLastQuarterCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.LastQuarter, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetLastWeekCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.LastWeek, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetLastYearCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.LastYear, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetNextFiscalQuarterCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime startOfNextFiscalQuarter = iSiteviewApi.get_SystemFunctions().get_StartOfNextFiscalQuarter();
        DateTime endOfNextFiscalQuarter = iSiteviewApi.get_SystemFunctions().get_EndOfNextFiscalQuarter();


        return GetCriteriaForDateRanges(query, strField, startOfNextFiscalQuarter, endOfNextFiscalQuarter);

    }

    public static  XmlElement GetNextFiscalYearCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime startOfNextFiscalYear = iSiteviewApi.get_SystemFunctions().get_StartOfNextFiscalYear();
        DateTime endOfNextFiscalYear = iSiteviewApi.get_SystemFunctions().get_EndOfNextFiscalYear();


        return GetCriteriaForDateRanges(query, strField, startOfNextFiscalYear, endOfNextFiscalYear);

    }

    public static  XmlElement GetNextMonthCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.NextMonth, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetNextQuarterCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.NextQuarter, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetNextWeekCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.NextWeek, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetNextYearCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.NextYear, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetPastOrNextCriteria(SiteviewQuery query, DateRangeDef dateRangeDef)
    {
        DateTime startDateTimeForAmountOfTimeDateRange = GetStartDateTimeForAmountOfTimeDateRange(dateRangeDef);
        DateTime endDateTimeForAmountOfTimeDateRange = GetEndDateTimeForAmountOfTimeDateRange(dateRangeDef);


        return GetCriteriaForDateRanges(query, dateRangeDef.get_DateTimeField(), startDateTimeForAmountOfTimeDateRange, endDateTimeForAmountOfTimeDateRange);

    }

    public static  XmlElement GetSpecificDateRangeCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, DateRangeDef dateRangeDef, String strField)
    {
        DateTime startDateTimeForSpecificDateRange = GetStartDateTimeForSpecificDateRange(iSiteviewApi, dateRangeDef);
        DateTime endDateTimeForSpecificDateRange = GetEndDateTimeForSpecificDateRange(iSiteviewApi, dateRangeDef);


        return GetCustomCriteria(query, strField, startDateTimeForSpecificDateRange, endDateTimeForSpecificDateRange);

    }

    public static  DateTime GetStartDateTimeForAmountOfTimeDateRange(DateRangeDef dateRangeDef)
    {
        DateTime today = DateTime.get_Today();
        if(dateRangeDef.get_IsPast()){
            if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Hours){


                return DateTime.get_Now().AddHours((double)-dateRangeDef.get_AmountOfTimeValue());
            }
            else if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Days){


                return DateTime.get_Today().AddDays((double)-dateRangeDef.get_AmountOfTimeValue());
            }
            else if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Months){


                return DateTime.get_Now().AddMonths(-dateRangeDef.get_AmountOfTimeValue());
            }
            else if(dateRangeDef.get_AmountOfTimeUnit() == AmountOfTime.Years){


                return DateTime.get_Today().AddYears(-dateRangeDef.get_AmountOfTimeValue());
            }

            return DateTime.get_Today();
        }

        return DateTime.get_Today();

    }

    public static  DateTime GetStartDateTimeForSpecificDateRange(ISiteviewApi iSiteviewApi, DateRangeDef dateRangeDef)
    {
        DateTime today = DateTime.get_Today();
        if(dateRangeDef.get_IsStartValueFunction()){


            return GetDateTimeFromFunctionCategory(iSiteviewApi, dateRangeDef.get_StartValue());
        }
        try{


            return ConversionUtils.StringToDateTime(dateRangeDef.get_StartValue());
        }
        catch(InvalidCastException e){

            return DateTime.get_Today();
        }

    }

    public static  void GetStartEndDateTimeForDateRange(ISiteviewApi iSiteviewApi, DateRange dateRange, /*TODO: OUT PARAMETER, Wrapper it to array[0] */DateTime[] dtStartDateTime, /*TODO: OUT PARAMETER, Wrapper it to array[0] */DateTime[] dtEndDateTime)
    {
        dtStartDateTime[0] = DateTime.get_Today();
        dtEndDateTime[0] = DateTime.get_Today();
        Integer firstMonthOfQuarter = 0;
        if(dateRange == DateRange.Today){
            dtStartDateTime[0] = DateTime.get_Today();
            dtEndDateTime[0] = DateTime.get_Today();

            return ;
        }
        else if(dateRange == DateRange.Yesterday){
            
            dtStartDateTime[0] = DateTime.get_Today().AddDays(-1.0);
            
            dtEndDateTime[0] = DateTime.get_Today().AddDays(-1.0);

            return ;
        }
        else if(dateRange == DateRange.Tomorrow){
            
            dtStartDateTime[0] = DateTime.get_Today().AddDays(1.0);
            
            dtEndDateTime[0] = DateTime.get_Today().AddDays(1.0);

            return ;
        }
        else if(dateRange == DateRange.ThisWeek){
            dtStartDateTime[0] = iSiteviewApi.get_SystemFunctions().get_StartOfThisWeek();
            
            dtEndDateTime[0] = dtStartDateTime[0].AddDays(6.0);

            return ;
        }
        else if(dateRange == DateRange.ThisMonth){
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(DateTime.get_Today().get_Year(), DateTime.get_Today().get_Month(), 1);
            dtEndDateTime[0] = new DateTime();
            dtEndDateTime[0].__Ctor__(DateTime.get_Today().get_Year(), DateTime.get_Today().get_Month(), Thread.get_CurrentThread().get_CurrentCulture().get_Calendar().GetDaysInMonth(DateTime.get_Today().get_Year(),DateTime.get_Today().get_Month()));

            return ;
        }
        else if(dateRange == DateRange.ThisQuarter){
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(DateTime.get_Today().get_Year(), GetFirstMonthOfQuarter(), 1);
            
            dtEndDateTime[0] = dtStartDateTime[0].AddMonths(3).AddDays(-1.0);

            return ;
        }
        else if(dateRange == DateRange.ThisYear){
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(DateTime.get_Today().get_Year(), 1, 1);
            
            dtEndDateTime[0] = dtStartDateTime[0].AddYears(1).AddDays(-1.0);

            return ;
        }
        else if(dateRange == DateRange.LastWeek){
            dtStartDateTime[0] = iSiteviewApi.get_SystemFunctions().get_StartOfLastWeek();
            
            dtEndDateTime[0] = dtStartDateTime[0].AddDays(6.0);

            return ;
        }
        else if(dateRange == DateRange.LastMonth){
            DateTime time = DateTime.get_Today().AddMonths(-1);
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(time.get_Year(), time.get_Month(), 1);
            dtEndDateTime[0] = new DateTime();
            dtEndDateTime[0].__Ctor__(time.get_Year(), time.get_Month(), Thread.get_CurrentThread().get_CurrentCulture().get_Calendar().GetDaysInMonth(time.get_Year(),time.get_Month()));

            return ;
        }
        else if(dateRange == DateRange.LastQuarter){
            
            firstMonthOfQuarter = GetFirstMonthOfQuarter();
            if(firstMonthOfQuarter <= 1){
                dtStartDateTime[0] = new DateTime();
                dtStartDateTime[0].__Ctor__(DateTime.get_Today().AddYears(-1).get_Year(), 10, 1);

            }else{
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(DateTime.get_Today().get_Year(), firstMonthOfQuarter - 3, 1);
            //NOTICE: break ignore!!!
            }
        }
        else if(dateRange == DateRange.LastYear){
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(DateTime.get_Today().AddYears(-1).get_Year(), 1, 1);
            
            dtEndDateTime[0] = dtStartDateTime[0].AddYears(1).AddDays(-1.0);

            return ;
        }
        else if(dateRange == DateRange.NextWeek){
            dtStartDateTime[0] = iSiteviewApi.get_SystemFunctions().get_StartOfNextWeek();
            
            dtEndDateTime[0] = dtStartDateTime[0].AddDays(6.0);

            return ;
        }
        else if(dateRange == DateRange.NextMonth){
            DateTime time2 = DateTime.get_Today().AddMonths(1);
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(time2.get_Year(), time2.get_Month(), 1);
            dtEndDateTime[0] = new DateTime();
            dtEndDateTime[0].__Ctor__(time2.get_Year(), time2.get_Month(), Thread.get_CurrentThread().get_CurrentCulture().get_Calendar().GetDaysInMonth(time2.get_Year(),time2.get_Month()));

            return ;
        }
        else if(dateRange == DateRange.NextQuarter){
            
            firstMonthOfQuarter = GetFirstMonthOfQuarter();
            if(firstMonthOfQuarter >= 10){
                dtStartDateTime[0] = new DateTime();
                dtStartDateTime[0].__Ctor__(DateTime.get_Today().AddYears(1).get_Year(), 1, 1);
            }
            else{
                dtStartDateTime[0] = new DateTime();
                dtStartDateTime[0].__Ctor__(DateTime.get_Today().get_Year(), firstMonthOfQuarter + 3, 1);
            }
            
            dtEndDateTime[0] = dtStartDateTime[0].AddMonths(3).AddDays(-1.0);

            return ;
        }
        else if(dateRange == DateRange.NextYear){
            dtStartDateTime[0] = new DateTime();
            dtStartDateTime[0].__Ctor__(DateTime.get_Today().AddYears(1).get_Year(), 1, 1);
            
            dtEndDateTime[0] = dtStartDateTime[0].AddYears(1).AddDays(-1.0);

            return ;
        }
        else if(dateRange == DateRange.None){

            return ;
        }
        else {

            return ;
        }
        
        dtEndDateTime[0] = dtStartDateTime[0].AddMonths(3).AddDays(-1.0);

    }

    public static  void GetStartEndDateTimeForDateRangeDef(ISiteviewApi iSiteviewApi, DateRangeDef dateRangeDef, /*TODO: OUT PARAMETER, Wrapper it to array[0] */DateTime[] dtStartDateTime, /*TODO: OUT PARAMETER, Wrapper it to array[0] */DateTime[] dtEndDateTime)
    {
        dtStartDateTime[0] = DateTime.get_Today();
        dtEndDateTime[0] = DateTime.get_Today();
        if(dateRangeDef.get_IsLengthOfTime()){
            DateTime[] __datetime_0 = new DateTime[1];
            DateTime[] __datetime_1 = new DateTime[1];
            GetStartEndDateTimeForDateRange(iSiteviewApi, dateRangeDef.get_DateRange(), __datetime_0, __datetime_1);
            dtStartDateTime[0] = __datetime_0[0];
            dtEndDateTime[0] = __datetime_1[0];
        }
        else if(dateRangeDef.get_IsAmountOfTime()){
            
            dtStartDateTime[0] = GetStartDateTimeForAmountOfTimeDateRange(dateRangeDef);
            
            dtEndDateTime[0] = GetEndDateTimeForAmountOfTimeDateRange(dateRangeDef);
        }
        else if(dateRangeDef.get_IsSpecificDateRange()){
            
            dtStartDateTime[0] = GetStartDateTimeForSpecificDateRange(iSiteviewApi, dateRangeDef);
            
            dtEndDateTime[0] = GetEndDateTimeForSpecificDateRange(iSiteviewApi, dateRangeDef);
        }

    }

    public static  XmlElement GetThisFiscalQuarterCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime startOfThisFiscalQuarter = iSiteviewApi.get_SystemFunctions().get_StartOfThisFiscalQuarter();
        DateTime endOfThisFiscalQuarter = iSiteviewApi.get_SystemFunctions().get_EndOfThisFiscalQuarter();


        return GetCriteriaForDateRanges(query, strField, startOfThisFiscalQuarter, endOfThisFiscalQuarter);

    }

    public static  XmlElement GetThisFiscalYearCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime startOfThisFiscalYear = iSiteviewApi.get_SystemFunctions().get_StartOfThisFiscalYear();
        DateTime endOfThisFiscalYear = iSiteviewApi.get_SystemFunctions().get_EndOfThisFiscalYear();


        return GetCriteriaForDateRanges(query, strField, startOfThisFiscalYear, endOfThisFiscalYear);

    }

    public static  XmlElement GetThisMonthCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.ThisMonth, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetThisQuarterCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.ThisQuarter, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetThisWeekCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.ThisWeek, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetThisYearCriteria(ISiteviewApi iSiteviewApi, SiteviewQuery query, String strField)
    {
        DateTime today = DateTime.get_Today();
        DateTime dtEndDateTime = DateTime.get_Today();
        DateTime[] __datetime_0 = new DateTime[1];
        DateTime[] __datetime_1 = new DateTime[1];
        GetStartEndDateTimeForDateRange(iSiteviewApi, DateRange.ThisYear, __datetime_0, __datetime_1);
        today = __datetime_0[0];
        dtEndDateTime = __datetime_1[0];


        return GetCriteriaForDateRanges(query, strField, today, dtEndDateTime);

    }

    public static  XmlElement GetTodayCriteria(SiteviewQuery query, String strField)
    {
        XmlElement xeQueryFunction = query.get_CriteriaBuilder().get_QueryFunction().SearchByDateOnly(ValueSources.Field, strField);


        return query.get_CriteriaBuilder().QueryFunctionExpression(xeQueryFunction,Operators.Equals,ValueSources.Literal,ConversionUtils.DateTimeToString(DateTime.get_Today(),ConversionUtils.DateTimeFormat.ISOFormat));

    }

    public static  XmlElement GetTomorrowCriteria(SiteviewQuery query, String strField)
    {
        XmlElement xeQueryFunction = query.get_CriteriaBuilder().get_QueryFunction().SearchByDateOnly(ValueSources.Field, strField);
        DateTime dtValue = DateTime.get_Today().AddDays(1.0);


        return query.get_CriteriaBuilder().QueryFunctionExpression(xeQueryFunction,Operators.Equals,ValueSources.Literal,ConversionUtils.DateTimeToString(dtValue,ConversionUtils.DateTimeFormat.ISOFormat));

    }

    public static  XmlElement GetYesterdayCriteria(SiteviewQuery query, String strField)
    {
        XmlElement xeQueryFunction = query.get_CriteriaBuilder().get_QueryFunction().SearchByDateOnly(ValueSources.Field, strField);
        DateTime dtValue = DateTime.get_Today().AddDays(-1.0);


        return query.get_CriteriaBuilder().QueryFunctionExpression(xeQueryFunction,Operators.Equals,ValueSources.Literal,ConversionUtils.DateTimeToString(dtValue,ConversionUtils.DateTimeFormat.ISOFormat));

    }

}