package core.dashboards;

import org.eclipse.swt.widgets.TabFolder;
import org.jfree.chart.JFreeChart;

import system.ClrInt32;
import system.Convert;
import system.DateTime;
import system.TimeSpan;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Data.DataTable;
import system.Xml.XmlElement;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.ChartType;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DrilldownDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.TimeInterval;
import Core.Dashboards.ViewDef;
import Core.Dashboards.XmlTimeIntervalCategory;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertSettingCategory;
import Siteview.AlertType;
import Siteview.DefRequest;
import Siteview.FieldUtils;
import Siteview.GuidUtils;
import Siteview.IQueryResult;
import Siteview.PlaceHolder;
import Siteview.QueryFunctions;
import Siteview.QueryGroupDef;
import Siteview.QueryInfoToGet;
import Siteview.QueryRequestField;
import Siteview.SiteviewQuery;
import Siteview.StatisticRequest;
import Siteview.StatisticScheduling;
import Siteview.StringUtils;
import Siteview.Api.BusinessObject;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.ColumnDef;
import Siteview.Api.FieldDef;
import Siteview.Api.GridDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Xml.DashboardPartCategory;
import Siteview.Xml.GridAction;
import Siteview.Xml.SecurityRight;
import Siteview.Xml.ValueSources;
import Siteview.Xml.XmlDashboardPartCategory;

public class ChartPartControl extends DashboardPartControl {

	private ChartPartDef m_chartPartDef;
    private String m_strColumnName;

    private String m_strFieldName;

    private String m_strGridDefID;

    private String m_strGridOrderByFieldName;

    private String m_strGroupByFieldName1;

    private String m_strGroupByFieldName2;

    private String m_strQueryGroupId;

    private String m_strQueryGroupLinkedTo;

    private String m_strQueryGroupName;

    private String m_strQueryGroupScopeOwner;
	private String m_strBusObName;
	private ViewDef m_vCurrentRunTimeDateRange;
	private Boolean m_bForceRecalculation;
	private GridDef m_GridDef;
	private GridAction m_gridAction;
	private ArrayList m_alDrilldownDefs;
	private PartRefDef m_dependPartRefDef;
	private Boolean m_dependMode = false;
	private GridDef m_dependGridDef;
	private BusinessObject m_busObj;
	private int m_QueryGroupScope;
	private QueryFunctions m_queryFunction;
	private ArrayList m_arrLabelItems;
	private Integer m_nBackStatisticsInterval;
	private Boolean m_bBackStatisticsRequired;
	private Integer m_nNumOfBackStatistics;
	private SiteviewQuery m_baseQuery;
	

	public ChartPartControl(ISiteviewApi iSiteviewApi, DashboardControl parent,
			int style) {
		super(iSiteviewApi, parent, style);
	}
	
	public ChartPartControl(ISiteviewApi iSiteviewApi, TabFolder parent,
			int style) {
		super(iSiteviewApi, parent, style);
	}
	
    @Override
    protected  void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef)
    {
        super.DefineFromDef(def,partRefDef);
        Core.Dashboards.ChartPartDef dashboardPartDef = (Core.Dashboards.ChartPartDef)super.get_DashboardPartDef();
        if(dashboardPartDef != null){
            this.m_chartPartDef = dashboardPartDef;
            this.set_BusObName(dashboardPartDef.get_BusObName());
            String test = dashboardPartDef.get_Alias();
            this.set_FieldName(dashboardPartDef.get_EvaluateByDef().get_FieldName());
            this.set_GroupByFieldName1(dashboardPartDef.get_GroupByDef().get_FieldName());
            this.set_GroupByFieldName2(dashboardPartDef.get_SubGroupByDef().get_FieldName());
            this.set_QueryFunction(dashboardPartDef.get_EvaluateByDef().get_QueryFunction());
            this.set_QueryGroupId(dashboardPartDef.get_QueryGroupId());
            this.set_QueryGroupName(dashboardPartDef.get_QueryGroupName());
            this.set_QueryGroupScope(dashboardPartDef.get_QueryGroupScope());
            this.set_QueryGroupScopeOwner(dashboardPartDef.get_QueryGroupScopeOwner());
            this.set_QueryGroupLinkedTo(dashboardPartDef.get_QueryGroupLinkedTo());
            this.set_BackStatisticsRequired(dashboardPartDef.get_BackStatisticsRequired());
            this.set_NumOfBackStatistics(dashboardPartDef.get_NumberOfBackStatistics());
            this.set_BackStatisticsInterval(dashboardPartDef.get_BackStatisticsIntervalInMinutes());
            this.set_ForceRecalculation(dashboardPartDef.get_ForceRecalculation());
        }

    }
    
    protected String get_BusObName(){

        return this.m_strBusObName;
    }
    protected void set_BusObName(String value){
        this.m_strBusObName = value;
    }
    
    protected String get_ColumnName(){

        return this.m_strColumnName;
    }
    protected void set_ColumnName(String value){
        this.m_strColumnName = value;
    }
    
    protected ViewDef get_CurrentRunTimeDateRange(){

        return this.m_vCurrentRunTimeDateRange;
    }
    protected void set_CurrentRunTimeDateRange(ViewDef value){
        this.m_vCurrentRunTimeDateRange = value;
    }

    public BusinessObject get_DependBusObj(){

        return this.m_busObj;
    }
    public void set_DependBusObj(BusinessObject value){
        this.m_busObj = value;
    }

    public GridDef get_DependGridDef(){

        return this.m_dependGridDef;
    }
    public void set_DependGridDef(GridDef value){
        this.m_dependGridDef = value;
    }

    public Boolean get_DependMode(){

        return this.m_dependMode;
    }
    public void set_DependMode(Boolean value){
        this.m_dependMode = value;
    }

    public PartRefDef get_DependPartRef(){

        return this.m_dependPartRefDef;
    }
    public void set_DependPartRef(PartRefDef value){
        this.m_dependPartRefDef = value;
    }

//    protected Integer get_Depth(){
//        if(this.get_Chart() != null){
//
//            return this.get_Chart().get_Drill().get_Depth();
//        }
//
//        return 0;
//    }

    protected ArrayList get_DrilldownArrayList(){

        return this.m_alDrilldownDefs;
    }
    protected void set_DrilldownArrayList(ArrayList value){
        this.m_alDrilldownDefs = value;
    }

    protected GridAction get_DrilldownGridBehaior(){

        return this.m_gridAction;
    }
    protected void set_DrilldownGridBehaior(GridAction value){
        this.m_gridAction = value;
    }

    protected GridDef get_DrilldownGridDef(){

        return this.m_GridDef;
    }
    protected void set_DrilldownGridDef(GridDef value){
        this.m_GridDef = value;
    }

    protected String get_DrilldownGridDefID(){

        return this.m_strGridDefID;
    }
    protected void set_DrilldownGridDefID(String value){
        this.m_strGridDefID = value;
    }

    protected String get_DrilldownGridOrderByFieldName(){

        return this.m_strGridOrderByFieldName;
    }
    protected void set_DrilldownGridOrderByFieldName(String value){
        this.m_strGridOrderByFieldName = value;
    }

    protected String get_FieldName(){

        return this.m_strFieldName;
    }
    protected void set_FieldName(String value){
        this.m_strFieldName = value;
    }

    protected Boolean get_ForceRecalculation(){

        return this.m_bForceRecalculation;
    }
    protected void set_ForceRecalculation(Boolean value){
        this.m_bForceRecalculation = value;
    }


    protected String get_GroupByFieldName1(){

        return this.m_strGroupByFieldName1;
    }
    protected void set_GroupByFieldName1(String value){
        this.m_strGroupByFieldName1 = value;
    }

    protected String get_GroupByFieldName2(){

        return this.m_strGroupByFieldName2;
    }
    protected void set_GroupByFieldName2(String value){
        this.m_strGroupByFieldName2 = value;
    }

    public Boolean get_IsPrintControl(){

        return true;
    }

    public Boolean get_IsPrintTable(){

        return true;
    }
    
    protected QueryFunctions get_QueryFunction(){

        return this.m_queryFunction;
    }
    protected void set_QueryFunction(QueryFunctions value){
        this.m_queryFunction = value;
    }

    protected String get_QueryGroupId(){

        return this.m_strQueryGroupId;
    }
    protected void set_QueryGroupId(String value){
        this.m_strQueryGroupId = value;
    }

    protected String get_QueryGroupLinkedTo(){

        return this.m_strQueryGroupLinkedTo;
    }
    protected void set_QueryGroupLinkedTo(String value){
        this.m_strQueryGroupLinkedTo = value;
    }

    protected String get_QueryGroupName(){

        return this.m_strQueryGroupName;
    }
    protected void set_QueryGroupName(String value){
        this.m_strQueryGroupName = value;
    }

    protected int get_QueryGroupScope(){

        return this.m_QueryGroupScope;
    }
    protected void set_QueryGroupScope(int value){
        this.m_QueryGroupScope = value;
    }

    protected String get_QueryGroupScopeOwner(){

        return this.m_strQueryGroupScopeOwner;
    }
    protected void set_QueryGroupScopeOwner(String value){
        this.m_strQueryGroupScopeOwner = value;
    }

    public String get_Title(){
        if(this.get_Chart() != null){

            return this.get_Chart().getTitle().getText();
        }

        return "";
    }

    protected ArrayList get_TooltipLabelItems(){

        return this.m_arrLabelItems;
    }
    protected void set_TooltipLabelItems(ArrayList value){
        this.m_arrLabelItems = value;
    }
    
    public JFreeChart get_Chart(){

        return null;
    }
    
    protected Integer get_BackStatisticsInterval(){

        return this.m_nBackStatisticsInterval;
    }
    protected void set_BackStatisticsInterval(Integer value){
        this.m_nBackStatisticsInterval = value;
    }

    protected Boolean get_BackStatisticsRequired(){

        return this.m_bBackStatisticsRequired;
    }
    protected void set_BackStatisticsRequired(Boolean value){
        this.m_bBackStatisticsRequired = value;
    }
    
    protected Integer get_NumOfBackStatistics(){

        return this.m_nNumOfBackStatistics;
    }
    protected void set_NumOfBackStatistics(Integer value){
        this.m_nNumOfBackStatistics = value;
    }
	
    public static String get_ChartCategory(){

        return "CHART";
    }

    protected Core.Dashboards.ChartPartDef get_chartPart(){

        return this.m_chartPartDef;
    }

    protected Core.Dashboards.ChartPartDef get_ChartPartDef(){

        return ((Core.Dashboards.ChartPartDef)super.get_DashboardPartDef());
    }
    
    protected  Boolean CheckFieldsHaveViewRight()
    {
        Boolean flag = true;
        String strBusOb = "";
        String strField = "";
        try{
            if(this.get_ChartPartDef().get_GroupByDef().get_Apply() && !this.get_ChartPartDef().get_GroupByDef().get_Duration()){
                String[] __strBusOb_0 = new String[1];
                String[] __strField_1 = new String[1];
                FieldUtils.SplitBusObField(QueryRequestField.Create(this.get_ChartPartDef().get_GroupByDef().get_FieldName()).get_BusObFieldName(),__strBusOb_0,__strField_1);
                strBusOb = __strBusOb_0[0];
                strField = __strField_1[0];

                if(strBusOb.equals("") && !strField.equals("")){

                    if(!super.getApi().get_SecurityService().HasBusObFieldRight(this.get_ChartPartDef().get_BusObName(),strField,SecurityRight.View)){
                        flag = false;
                    }
                }
                else if(strBusOb.equals(this.get_ChartPartDef().get_BusObName())){

                    if(!super.getApi().get_SecurityService().HasBusObFieldRight(this.get_ChartPartDef().get_BusObName(),strField,SecurityRight.View)){
                        flag = false;
                    }
                }

                else if(!super.getApi().get_SecurityService().HasBusObFieldRight(strBusOb,strField,SecurityRight.View)){
                    flag = false;
                }
                if(!flag){
                    String str3 = StringUtils.SetToken(Res.get_Default().GetString("ChartPartControl.FieldsWithOutViewRights"), "FIELDS", strField.toString());
                    super.OnAlertRaised(this,new AlertRaisedEventArgs(str3, AlertType.Informational, null, true, AlertSettingCategory.ViewRights));

                    return flag;
                }
            }
            if(this.get_ChartPartDef().get_SubGroupByDef().get_Apply() && !this.get_ChartPartDef().get_SubGroupByDef().get_Duration()){
                String[] __strBusOb_2 = new String[1];
                String[] __strField_3 = new String[1];
                FieldUtils.SplitBusObField(QueryRequestField.Create(this.get_ChartPartDef().get_SubGroupByDef().get_FieldName()).get_BusObFieldName(),__strBusOb_2,__strField_3);
                strBusOb = __strBusOb_2[0];
                strField = __strField_3[0];

                if(strBusOb.equals("") && !strField.equals("")){

                    if(!super.getApi().get_SecurityService().HasBusObFieldRight(this.get_ChartPartDef().get_BusObName(),strField,SecurityRight.View)){
                        flag = false;
                    }
                }
                else if(strBusOb.equals(this.get_ChartPartDef().get_BusObName())){

                    if(!super.getApi().get_SecurityService().HasBusObFieldRight(this.get_ChartPartDef().get_BusObName(),strField,SecurityRight.View)){
                        flag = false;
                    }
                }

                else if(!super.getApi().get_SecurityService().HasBusObFieldRight(strBusOb,strField,SecurityRight.View)){
                    flag = false;
                }
                if(!flag){
                    String str4 = StringUtils.SetToken(Res.get_Default().GetString("ChartPartControl.FieldsWithOutViewRights"), "FIELDS", strField.toString());
                    super.OnAlertRaised(this,new AlertRaisedEventArgs(str4, AlertType.Informational, null, true, AlertSettingCategory.ViewRights));

                    return flag;
                }
            }
            if(this.get_ChartPartDef().get_EvaluateByDef().get_Duration() || super.getApi().get_SecurityService().HasBusObFieldRight(this.get_ChartPartDef().get_BusObName(),this.get_ChartPartDef().get_EvaluateByDef().get_FieldName(),SecurityRight.View)){

                return flag;
            }
            String strMessage = StringUtils.SetToken(Res.get_Default().GetString("ChartPartControl.FieldsWithOutViewRights"), "FIELDS", this.get_ChartPartDef().get_EvaluateByDef().get_FieldName().toString());
            super.OnAlertRaised(this,new AlertRaisedEventArgs(strMessage, AlertType.Informational, null, true, AlertSettingCategory.ViewRights));

            return false;
        }
        catch(system.Exception exception){
            if(!super.get_TrapErrors()){
                throw(exception);
            }
            super.OnAlertRaised(this,new AlertRaisedEventArgs(AlertType.Error, exception));
            flag = false;
        }

        return flag;

    }
    
    protected  IQueryResult ExcuteStatisticRequest(SiteviewQuery query, DrilldownDef drillDownDef)
    {
        ArrayList alSelectItems = new ArrayList();
        ArrayList alGroupByItems = new ArrayList();
        String strRequestId = this.get_ChartPartDef().get_Id() + "DrillDown" + GuidUtils.CreateGuid();
        String strRequestName = this.get_ChartPartDef().get_Name() + "DrillDown";
        String id = this.get_ChartPartDef().get_Id();
        QueryFunctions queryFunction = drillDownDef.get_EvaluateByDef().get_QueryFunction();
        String strQueryFunctionField = String.format("%s.%s", this.get_ChartPartDef().get_BusObName(), drillDownDef.get_EvaluateByDef().get_FieldName());
        String groupByName = "";
        String fieldName = "";
        if(drillDownDef.get_GroupByDef().get_Apply() && !drillDownDef.get_GroupByDef().get_Duration()){
            if(drillDownDef.get_GroupByDef().get_DateTimeField()){
                if(drillDownDef.get_GroupByDef().get_FieldName().indexOf(".") != -1){
                    
                    query = this.GenerateDateTimeQueryGroupByForDrilldownGroupBy(query, drillDownDef);
                }
                else{
                    
                    groupByName = String.format("%s.%s", this.get_ChartPartDef().get_BusObName(), ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_FieldName());
                    XmlElement xeItem = this.GenerateDateTimeGroupBy(query, ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval(), groupByName);
                    query.AddGroupBy(xeItem);
                }
            }
            else if(drillDownDef.get_GroupByDef().get_FieldName().indexOf(".") != -1){
                groupByName = drillDownDef.get_GroupByDef().get_FieldName();
                alGroupByItems.Add(groupByName);
                alSelectItems.Add(groupByName);
            }
            else{
                alGroupByItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),drillDownDef.get_GroupByDef().get_FieldName()));
                alSelectItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),drillDownDef.get_GroupByDef().get_FieldName()));
            }
        }
        if(drillDownDef.get_SubGroupByDef().get_Apply() && !drillDownDef.get_SubGroupByDef().get_Duration()){
            if(drillDownDef.get_SubGroupByDef().get_DateTimeField()){
                if(drillDownDef.get_SubGroupByDef().get_FieldName().indexOf(".") != -1){
                    this.GenerateDateTimeQueryGroupByForSubGroupBy(query);
                }
                else{
                    
                    fieldName = String.format("%s.%s", this.get_ChartPartDef().get_BusObName(), ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_FieldName());
                    XmlElement element2 = this.GenerateDateTimeGroupBy(query, ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval(), fieldName);
                    query.AddGroupBy(element2);
                }
            }
            else if(drillDownDef.get_SubGroupByDef().get_FieldName().indexOf(".") != -1){
                fieldName = drillDownDef.get_SubGroupByDef().get_FieldName();
                alGroupByItems.Add(fieldName);
                alSelectItems.Add(fieldName);
            }
            else{
                alGroupByItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),drillDownDef.get_SubGroupByDef().get_FieldName()));
                alSelectItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),drillDownDef.get_SubGroupByDef().get_FieldName()));
            }
        }
        if(!drillDownDef.get_SubGroupByDef().get_Apply()){
            if((!drillDownDef.get_GroupByDef().get_Duration() && drillDownDef.get_GroupByDef().get_DateTimeField()) && (drillDownDef.get_GroupByDef().get_FieldName().indexOf(".") != -1)){
                
                query = this.GenerateDateTimeQuerySelectFieldsForDrilldownGroupBy(query, drillDownDef);
            }
        }
        else{
            if((!drillDownDef.get_GroupByDef().get_Duration() && drillDownDef.get_GroupByDef().get_DateTimeField()) && (drillDownDef.get_GroupByDef().get_FieldName().indexOf(".") != -1)){
                
                query = this.GenerateDateTimeQuerySelectFieldsForDrilldownGroupBy(query, drillDownDef);
            }
            if((!drillDownDef.get_SubGroupByDef().get_Duration() && drillDownDef.get_SubGroupByDef().get_DateTimeField()) && (drillDownDef.get_SubGroupByDef().get_FieldName().indexOf(".") != -1)){
                
                query = this.GenerateDateTimeQuerySelectFieldsForDrilldownSubGroupBy(query, drillDownDef);
            }
        }
        Boolean backStatisticsRequired = drillDownDef.get_BackStatisticsRequired();
        Integer numberOfBackStatistics = drillDownDef.get_NumberOfBackStatistics();
        StatisticScheduling minutely = StatisticScheduling.Minutely;
        String strStatisticSchedulingValue = Convert.ToString(drillDownDef.get_BackStatisticsIntervalinMinutes());
        Boolean bForceRecalculation = true;
        TimeSpan minValue = TimeSpan.MinValue;


        return this.ExcuteStatisticRequest(strRequestId,strRequestName,id,query,queryFunction,strQueryFunctionField,alSelectItems,alGroupByItems,bForceRecalculation,minValue,backStatisticsRequired,numberOfBackStatistics,minutely,strStatisticSchedulingValue);

    }
    
    protected  IQueryResult ExcuteStatisticRequest(String strRequestId, String strRequestName, String strDefinitionId, SiteviewQuery siteviewQuery, QueryFunctions queryFunction, String strQueryFunctionField, ArrayList alSelectItems, ArrayList alGroupByItems, Boolean bForceRecalculation, TimeSpan tsStatisticDateTimeOffset, Boolean bBackStatisticsRequired, Integer nNumOfBackStatistics, StatisticScheduling statisticScheduling, String strStatisticSchedulingValue)
    {
        StatisticRequest request = new StatisticRequest();
        request.set_Id(strRequestId);
        request.set_Name(strRequestName);
        request.set_DefinitionId(strDefinitionId);
        if(!this.get_ChartPartDef().get_EvaluateByDef().get_Duration()){
            siteviewQuery.AddQueryFunction(Core.Dashboards.ChartPartDef.QueryFunctionToXml(siteviewQuery,queryFunction,strQueryFunctionField));
        }
        else{
            XmlElement element = this.GenerateDateTimeDurationGroupBy(siteviewQuery, this.get_ChartPartDef().get_EvaluateByDef().get_DurationUnit(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationStartValue(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationEndValue());
            siteviewQuery.AddQueryFunction(Core.Dashboards.ChartPartDef.QueryFunctionToXml(siteviewQuery,this.get_ChartPartDef().get_EvaluateByDef().get_QueryFunction().toString(),element));
        }



        if(((!this.get_ChartPartDef().get_GroupByDef().get_Duration() && 
        		this.get_ChartPartDef().get_GroupByDef().get_LimitQuery()) 
        		&& (!this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals("") && 
        				!this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(this.get_ChartPartDef().get_GroupByDef().get_FieldName()))) 
        		&& !this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(this.get_ChartPartDef().get_BusObName() + "." + this.get_ChartPartDef().get_EvaluateByDef().get_FieldName())){
            alSelectItems.Add(this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName());
            alGroupByItems.Add(this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName());
        }
        siteviewQuery.AddBusObQuery(siteviewQuery.get_BusinessObjectName(),QueryInfoToGet.RequestedFields,alSelectItems);
        IEnumerator it1 = alGroupByItems.GetEnumerator();
        while(it1.MoveNext()){
            String str = (String)it1.get_Current();
            siteviewQuery.AddGroupBy(str);
        }

        if(this.get_ChartPartDef().get_GroupByDef().get_LimitQuery() && !this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals("")){

            if(!this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(this.get_ChartPartDef().get_BusObName() + "." + this.get_ChartPartDef().get_EvaluateByDef().get_FieldName())){
                if(!this.get_chartPart().get_GroupByDef().get_Duration()){

                    if(this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(this.get_ChartPartDef().get_GroupByDef().get_FieldName())){
                        if(!this.get_ChartPartDef().get_GroupByDef().get_Duration()){
                            if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField()){
                                XmlElement xeItem = this.GenerateDateTimeGroupBy(siteviewQuery, this.get_ChartPartDef().get_GroupByDef().get_Interval(), this.get_ChartPartDef().get_GroupByDef().get_FieldName());
                                if(this.get_ChartPartDef().get_GroupByDef().get_Descending()){
                                    siteviewQuery.AddOrderByDesc(xeItem);
                                }
                                else{
                                    siteviewQuery.AddOrderBy(xeItem);
                                }
                            }
                            else if(this.get_ChartPartDef().get_GroupByDef().get_Descending()){
                                siteviewQuery.AddOrderByDesc(this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName());
                            }
                            else{
                                siteviewQuery.AddOrderBy(this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName());
                            }
                        }
                    }
                    else if(this.get_ChartPartDef().get_GroupByDef().get_Descending()){
                        siteviewQuery.AddOrderByDesc(this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName());
                    }
                    else{
                        siteviewQuery.AddOrderBy(this.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName());
                    }
                }
            }
            else if(!this.get_ChartPartDef().get_EvaluateByDef().get_Duration()){
                XmlElement element3 = Core.Dashboards.ChartPartDef.QueryFunctionToXml(siteviewQuery, queryFunction, strQueryFunctionField);
                if(this.get_ChartPartDef().get_GroupByDef().get_Descending()){
                    siteviewQuery.AddOrderBy(element3);
                }
                else{
                    siteviewQuery.AddOrderByDesc(element3);
                }
            }
            else{
                XmlElement element4 = this.GenerateDateTimeDurationGroupBy(siteviewQuery, this.get_ChartPartDef().get_EvaluateByDef().get_DurationUnit(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationStartValue(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_EvaluateByDef().get_DurationEndValue());
                XmlElement element5 = Core.Dashboards.ChartPartDef.QueryFunctionToXml(siteviewQuery, this.get_ChartPartDef().get_EvaluateByDef().get_QueryFunction().toString(), element4);
                if(this.get_ChartPartDef().get_GroupByDef().get_Descending()){
                    siteviewQuery.AddOrderBy(element5);
                }
                else{
                    siteviewQuery.AddOrderByDesc(element5);
                }
            }
        }
        request.set_SiteviewQuery(siteviewQuery);
        if(bBackStatisticsRequired==null){
        	bBackStatisticsRequired = false;
        }
        request.set_BackStatisticsRequired(bBackStatisticsRequired);
        if(request.get_BackStatisticsRequired()){
            request.set_NumOfBackStatistics(nNumOfBackStatistics);
            request.set_BackStatisticScheduling(statisticScheduling);
            request.set_BackStatisticSchedulingValue(strStatisticSchedulingValue);
        }
        request.set_StatisticDateTimeOffset(tsStatisticDateTimeOffset);
        request.set_ForceRecalculation(bForceRecalculation);
        if(super.getApi().get_LiveDefinitionLibrary().GetBusinessObjectDef(request.get_SiteviewQuery().get_BusinessObjectName()).get_ParentOfGroup()){
            request.get_SiteviewQuery().set_GetAllMembersOfGroup(true);
        }


        return super.getApi().get_StatisticsService().GetStatistics(request);

    }
    
    protected  IQueryResult ExcuteStatisticRequest(String strRequestId, String strRequestName, String strDefinitionId, ViewDef vRunTimeDateRange)
    {
        String groupByName = "";
        String fieldName = "";
        XmlElement busObSearchCriteria = null;
        XmlElement element2 = null;
        ArrayList alSelectItems = new ArrayList();
        ArrayList alGroupByItems = new ArrayList();
        QueryGroupDef definition = (QueryGroupDef)super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ByName(this.get_ChartPartDef().get_QueryGroupScope(),this.get_ChartPartDef().get_QueryGroupScopeOwner(),this.get_ChartPartDef().get_QueryGroupLinkedTo(),QueryGroupDef.get_ClassName(),this.get_ChartPartDef().get_QueryGroupName(),false));
        if(definition == null){
            definition = (QueryGroupDef)super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(this.get_ChartPartDef().get_QueryGroupScope(),this.get_ChartPartDef().get_QueryGroupScopeOwner(),this.get_ChartPartDef().get_QueryGroupLinkedTo(),QueryGroupDef.get_ClassName(),this.get_ChartPartDef().get_QueryGroupId(),false));
        }
        SiteviewQuery query = new SiteviewQuery();
        if(definition != null){
            query.set_BusinessObjectName(definition.get_SiteviewQuery().get_BusinessObjectName());
            busObSearchCriteria = definition.get_SiteviewQuery().get_BusObSearchCriteria();
            if(this.m_dependMode){
                query.set_BusinessObjectName(this.m_dependPartRefDef.get_DependBusObName());
                query.AddRelationship(this.m_dependPartRefDef.get_DependRelationName(),null,QueryInfoToGet.All);
                BusinessObjectDef businessObjectDef = super.getApi().get_BusObDefinitions().GetBusinessObjectDef(this.m_busObj.get_Name());
                if(busObSearchCriteria == null){
                    
                    element2 = query.get_CriteriaBuilder().FieldAndValueExpression(businessObjectDef.get_IdField().get_Name(), Siteview.Operators.Equals, this.m_busObj.get_Id());
                }
                else if(businessObjectDef != null){
                    
                    element2 = query.get_CriteriaBuilder().FieldAndValueExpression(businessObjectDef.get_IdField().get_Name(), Siteview.Operators.Equals, this.m_busObj.get_Id());
                    
                    busObSearchCriteria = query.get_CriteriaBuilder().AndExpressions(busObSearchCriteria, element2);
                }
            }
            query.set_BusObSearchCriteria(busObSearchCriteria);
        }
        if(this.get_ChartPartDef().get_DefaultDateRangeDef().get_ApplyDateRange()){
            XmlElement element4 = query.get_CriteriaBuilder().ImportSearchCriteria(query.get_BusObSearchCriteria());
            query.set_BusObSearchCriteria(element4);
            if(vRunTimeDateRange == null){
                
                query = DateRangeCriteriaCreator.AddDefaultDateRangeCriteria(super.getApi(), query, this.get_ChartPartDef().get_DefaultDateRangeDef());
            }
            else{
                
                query = DateRangeCriteriaCreator.AddDateRangeCriteria(super.getApi(), query, vRunTimeDateRange.get_DateRange(), this.get_ChartPartDef().get_DefaultDateRangeDef().get_DateTimeField());
            }
        }
        new ArrayList();
        if(this.get_ChartPartDef().get_GroupByDef().get_Apply()){
            if(!this.get_ChartPartDef().get_GroupByDef().get_Duration()){
                if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField()){
                    if(this.get_ChartPartDef().get_GroupByDef().get_FieldName().indexOf(".") != -1){
                        
                        query = this.GenerateDateTimeQueryGroupByForGroupBy(query);
                    }
                    else{
                        
                        groupByName = String.format("%s.%s", this.get_ChartPartDef().get_BusObName(), this.get_ChartPartDef().get_GroupByDef().get_FieldName());
                        XmlElement xeItem = this.GenerateDateTimeGroupBy(query, this.get_ChartPartDef().get_GroupByDef().get_Interval(), groupByName);
                        query.AddGroupBy(xeItem);
                    }
                }
                else if(this.get_ChartPartDef().get_GroupByDef().get_FieldName().indexOf(".") != -1){
                    groupByName = this.get_ChartPartDef().get_GroupByDef().get_FieldName();
                    alGroupByItems.Add(groupByName);
                    alSelectItems.Add(groupByName);
                }
                else{
                    alGroupByItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),this.get_ChartPartDef().get_GroupByDef().get_FieldName()));
                    alSelectItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),this.get_ChartPartDef().get_GroupByDef().get_FieldName()));
                }
            }
            else{
                XmlElement element6 = this.GenerateDateTimeDurationGroupBy(query, this.get_ChartPartDef().get_GroupByDef().get_DurationUnit(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartValue(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndValue());
                query.AddGroupBy(element6);
            }
        }
        if(this.get_ChartPartDef().get_SubGroupByDef().get_Apply()){
            if(!this.get_ChartPartDef().get_SubGroupByDef().get_Duration()){
                if(this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
                    if(this.get_ChartPartDef().get_SubGroupByDef().get_FieldName().indexOf(".") != -1){
                        
                        query = this.GenerateDateTimeQueryGroupByForSubGroupBy(query);
                    }
                    else{
                        
                        fieldName = String.format("%s.%s", this.get_ChartPartDef().get_BusObName(), this.get_ChartPartDef().get_SubGroupByDef().get_FieldName());
                        XmlElement element7 = this.GenerateDateTimeGroupBy(query, this.get_ChartPartDef().get_SubGroupByDef().get_Interval(), fieldName);
                        query.AddGroupBy(element7);
                    }
                }
                else if(this.get_ChartPartDef().get_SubGroupByDef().get_FieldName().indexOf(".") != -1){
                    fieldName = this.get_ChartPartDef().get_SubGroupByDef().get_FieldName();
                    alGroupByItems.Add(fieldName);
                    alSelectItems.Add(fieldName);
                }
                else{
                    alGroupByItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),this.get_ChartPartDef().get_SubGroupByDef().get_FieldName()));
                    alSelectItems.Add(String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),this.get_ChartPartDef().get_SubGroupByDef().get_FieldName()));
                }
            }
            else{
                XmlElement element8 = this.GenerateDateTimeDurationGroupBy(query, this.get_ChartPartDef().get_SubGroupByDef().get_DurationUnit(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationStartValue(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationEndValue());
                query.AddGroupBy(element8);
            }
        }
        String strB = XmlDashboardPartCategory.ToString(DashboardPartCategory.Speedometer);
        String str4 = XmlDashboardPartCategory.ToString(DashboardPartCategory.GaugeLinear);
        if((clr.System.StringStaticWrapper.Compare(this.get_ChartPartDef().get_CategoryAsString(),strB,true) != 0) || (clr.System.StringStaticWrapper.Compare(this.get_ChartPartDef().get_CategoryAsString(),str4,true) != 0)){
            if(!this.get_ChartPartDef().get_SubGroupByDef().get_Apply()){
                if(!this.get_ChartPartDef().get_GroupByDef().get_Duration()){
                    if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && (this.get_ChartPartDef().get_GroupByDef().get_FieldName().indexOf(".") != -1)){
                        
                        query = this.GenerateDateTimeQuerySelectFieldsForGroupBy(query);
                    }
                }
                else{
                    XmlElement xeQueryFunction = this.GenerateDateTimeDurationGroupBy(query, this.get_ChartPartDef().get_GroupByDef().get_DurationUnit(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartValue(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndValue());
                    query.AddQueryFunction(xeQueryFunction);
                }
            }
            else{
                if(!this.get_ChartPartDef().get_GroupByDef().get_Duration()){
                    if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && (this.get_ChartPartDef().get_GroupByDef().get_FieldName().indexOf(".") != -1)){
                        
                        query = this.GenerateDateTimeQuerySelectFieldsForGroupBy(query);
                    }
                }
                else{
                    XmlElement element10 = this.GenerateDateTimeDurationGroupBy(query, this.get_ChartPartDef().get_GroupByDef().get_DurationUnit(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartValue(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndValue());
                    query.AddQueryFunction(element10);
                }
                if(!this.get_ChartPartDef().get_SubGroupByDef().get_Duration()){
                    if(this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField() && (this.get_ChartPartDef().get_SubGroupByDef().get_FieldName().indexOf(".") != -1)){
                        
                        query = this.GenerateDateTimeQuerySelectFieldsForSubGroupBy(query);
                    }
                }
                else{
                    XmlElement element11 = this.GenerateDateTimeDurationGroupBy(query, this.get_ChartPartDef().get_SubGroupByDef().get_DurationUnit(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationStartValue(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_SubGroupByDef().get_DurationEndValue());
                    query.AddQueryFunction(element11);
                }
            }
        }

        return this.ExcuteStatisticRequest(strRequestId,strRequestName,strDefinitionId,query,this.get_ChartPartDef().get_EvaluateByDef().get_QueryFunction(),String.format("%s.%s",this.get_ChartPartDef().get_BusObName(),this.get_ChartPartDef().get_EvaluateByDef().get_FieldName()),alSelectItems,alGroupByItems,this.get_ChartPartDef().get_ForceRecalculation(),TimeSpan.MinValue,this.get_ChartPartDef().get_BackStatisticsRequired(),this.get_ChartPartDef().get_NumberOfBackStatistics(),StatisticScheduling.Minutely,Convert.ToString(this.get_ChartPartDef().get_BackStatisticsIntervalInMinutes()));

    }
    
    protected  XmlElement GenerateDateTimeDurationGroupBy(SiteviewQuery query, String unit, Boolean startDurationFunction, String startDurationName, Boolean endDurationFunction, String endDurationName)
    {
        XmlElement element = null;
        if(unit.equals("Years")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfYearsBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfYearsBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfYearsBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfYearsBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfYearsBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }
        else if(unit.equals("Quarters")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfQuartersBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfQuartersBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfQuartersBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfQuartersBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfQuartersBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }
        else if(unit.equals("Months")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfMonthsBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfMonthsBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfMonthsBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfMonthsBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfMonthsBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }
        else if(unit.equals("Weeks")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfWeeksBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfWeeksBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfWeeksBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfWeeksBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfWeeksBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }
        else if(unit.equals("Days")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfDaysBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfDaysBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfDaysBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfDaysBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfDaysBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }
        else if(unit.equals("Hours")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfHoursBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfHoursBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfHoursBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfHoursBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfHoursBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }
        else if(unit.equals("Minutes")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfMinutesBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfMinutesBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfMinutesBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfMinutesBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfMinutesBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }
        else if(unit.equals("Seconds")){
            if(!startDurationFunction || endDurationFunction){
                if(!startDurationFunction && endDurationFunction){


                    return query.get_QueryFunction().NumberOfSecondsBetween(ValueSources.Field,startDurationName,ValueSources.Function,endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfSecondsBetween(ValueSources.Field, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") == -1) && (endDurationName.indexOf(".") != -1)){
                    
                    element = query.get_QueryFunction().NumberOfSecondsBetween(ValueSources.Literal, startDurationName, ValueSources.Field, endDurationName);
                }
                if((startDurationName.indexOf(".") != -1) && (endDurationName.indexOf(".") == -1)){
                    
                    element = query.get_QueryFunction().NumberOfSecondsBetween(ValueSources.Field, startDurationName, ValueSources.Literal, endDurationName);
                }

                return element;
            }


            return query.get_QueryFunction().NumberOfSecondsBetween(ValueSources.Function,startDurationName,ValueSources.Field,endDurationName);
        }

        return element;

    }   
    
    protected  SiteviewQuery GenerateDateTimeQueryGroupByForDrilldownGroupBy(SiteviewQuery query, DrilldownDef drillDownDef)
    {
        String groupByName = "";
        groupByName = drillDownDef.get_GroupByDef().get_FieldName();
        if((drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
            XmlElement element = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element);
        }
        if((drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
            XmlElement element2 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element2);
            XmlElement element3 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddGroupBy(element3);
        }
        if(drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
            XmlElement element4 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element4);
            XmlElement element5 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddGroupBy(element5);
            XmlElement element6 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Daily), groupByName);
            query.AddGroupBy(element6);
        }
        XmlElement xeItem = this.GenerateDateTimeGroupBy(query, drillDownDef.get_GroupByDef().get_Interval(), groupByName);
        query.AddGroupBy(xeItem);

        return query;

    }
    
    protected  XmlElement GenerateDateTimeGroupBy(SiteviewQuery query, String interval, String groupByName)
    {
        if(interval.equals("None")){

            return null;
        }
        else if(interval.equals("Hourly")){


            return query.get_QueryFunction().GetHour(ValueSources.Field,groupByName);
        }
        else if(interval.equals("Daily")){


            return query.get_QueryFunction().GetDayOfYear(ValueSources.Field,groupByName);
        }
        else if(interval.equals("Weekly")){


            return query.get_QueryFunction().GetWeekOfYear(ValueSources.Field,groupByName);
        }
        else if(interval.equals("Monthly")){


            return query.get_QueryFunction().GetMonth(ValueSources.Field,groupByName);
        }
        else if(interval.equals("Quarterly")){


            return query.get_QueryFunction().GetQuarter(ValueSources.Field,groupByName);
        }
        else if(interval.equals("Yearly")){


            return query.get_QueryFunction().GetYear(ValueSources.Field,groupByName);
        }

        return null;

    }
    
    protected  SiteviewQuery GenerateDateTimeQuerySelectFieldsForDrilldownGroupBy(SiteviewQuery query, DrilldownDef drillDownDef)
    {
        String groupByName = "";
        groupByName = drillDownDef.get_GroupByDef().get_FieldName();
        if((drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
            XmlElement element = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element);
        }
        if((drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
            XmlElement element2 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element2);
            XmlElement element3 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element3);
        }
        if(drillDownDef.get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
            XmlElement element4 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element4);
            XmlElement element5 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element5);
            XmlElement element6 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Daily), groupByName);
            query.AddQueryFunction(element6);
        }
        XmlElement xeQueryFunction = this.GenerateDateTimeGroupBy(query, drillDownDef.get_GroupByDef().get_Interval(), groupByName);
        query.AddQueryFunction(xeQueryFunction);

        return query;

    }
    
    protected  SiteviewQuery GenerateDateTimeQueryGroupByForSubGroupBy(SiteviewQuery query)
    {
        String groupByName = "";
        groupByName = this.get_ChartPartDef().get_SubGroupByDef().get_FieldName();
        if((this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
            XmlElement element = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element);
        }
        if((this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
            XmlElement element2 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element2);
            XmlElement element3 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddGroupBy(element3);
        }
        if(this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
            XmlElement element4 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element4);
            XmlElement element5 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddGroupBy(element5);
            XmlElement element6 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Daily), groupByName);
            query.AddGroupBy(element6);
        }
        XmlElement xeItem = this.GenerateDateTimeGroupBy(query, this.get_ChartPartDef().get_SubGroupByDef().get_Interval(), groupByName);
        query.AddGroupBy(xeItem);

        return query;

    }
    
    
    protected  SiteviewQuery GenerateDateTimeQuerySelectFieldsForDrilldownSubGroupBy(SiteviewQuery query, DrilldownDef drillDownDef)
    {
        String groupByName = "";
        groupByName = drillDownDef.get_SubGroupByDef().get_FieldName();
        if((drillDownDef.get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (drillDownDef.get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
            XmlElement element = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element);
        }
        if((drillDownDef.get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (drillDownDef.get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
            XmlElement element2 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element2);
            XmlElement element3 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element3);
        }
        if(drillDownDef.get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
            XmlElement element4 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element4);
            XmlElement element5 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element5);
            XmlElement element6 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Daily), groupByName);
            query.AddQueryFunction(element6);
        }
        XmlElement xeQueryFunction = this.GenerateDateTimeGroupBy(query, drillDownDef.get_SubGroupByDef().get_Interval(), groupByName);
        query.AddQueryFunction(xeQueryFunction);

        return query;

    }
    
    protected  SiteviewQuery GenerateDateTimeQueryGroupByForGroupBy(SiteviewQuery query)
    {
        String groupByName = "";
        groupByName = this.get_ChartPartDef().get_GroupByDef().get_FieldName();
        if((this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
            XmlElement element = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element);
        }
        if((this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
            XmlElement element2 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element2);
            XmlElement element3 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddGroupBy(element3);
        }
        if(this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
            XmlElement element4 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddGroupBy(element4);
            XmlElement element5 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddGroupBy(element5);
            XmlElement element6 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Daily), groupByName);
            query.AddGroupBy(element6);
        }
        XmlElement xeItem = this.GenerateDateTimeGroupBy(query, this.get_ChartPartDef().get_GroupByDef().get_Interval(), groupByName);
        query.AddGroupBy(xeItem);

        return query;

    }   
    
    protected  SiteviewQuery GenerateDateTimeQuerySelectFieldsForGroupBy(SiteviewQuery query)
    {
        String groupByName = "";
        groupByName = this.get_ChartPartDef().get_GroupByDef().get_FieldName();
        if((this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
            XmlElement element = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element);
        }
        if((this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
            XmlElement element2 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element2);
            XmlElement element3 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element3);
        }
        if(this.get_ChartPartDef().get_GroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
            XmlElement element4 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element4);
            XmlElement element5 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element5);
            XmlElement element6 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Daily), groupByName);
            query.AddQueryFunction(element6);
        }
        XmlElement xeQueryFunction = this.GenerateDateTimeGroupBy(query, this.get_ChartPartDef().get_GroupByDef().get_Interval(), groupByName);
        query.AddQueryFunction(xeQueryFunction);

        return query;

    }
    
    protected  SiteviewQuery GenerateDateTimeQuerySelectFieldsForSubGroupBy(SiteviewQuery query)
    {
        String groupByName = "";
        groupByName = this.get_ChartPartDef().get_SubGroupByDef().get_FieldName();
        if((this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
            XmlElement element = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element);
        }
        if((this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
            XmlElement element2 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element2);
            XmlElement element3 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element3);
        }
        if(this.get_ChartPartDef().get_SubGroupByDef().get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
            XmlElement element4 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Yearly), groupByName);
            query.AddQueryFunction(element4);
            XmlElement element5 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Monthly), groupByName);
            query.AddQueryFunction(element5);
            XmlElement element6 = this.GenerateDateTimeGroupBy(query, XmlTimeIntervalCategory.ToString(TimeInterval.Daily), groupByName);
            query.AddQueryFunction(element6);
        }
        XmlElement xeQueryFunction = this.GenerateDateTimeGroupBy(query, this.get_ChartPartDef().get_SubGroupByDef().get_Interval(), groupByName);
        query.AddQueryFunction(xeQueryFunction);

        return query;

    }
    
    
    protected  SiteviewQuery GetDrillDownCriteria(Integer row, Integer column, DataTable dataSource, ViewDef vRunTimeDateRange)
    {
        QueryGroupDef definition = null;
        XmlElement oXe = null;
        definition = (QueryGroupDef)super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(this.get_ChartPartDef().get_QueryGroupScope(),this.get_ChartPartDef().get_QueryGroupScopeOwner(),this.get_ChartPartDef().get_QueryGroupLinkedTo(),QueryGroupDef.get_ClassName(),this.get_ChartPartDef().get_QueryGroupId(),false));
        if(definition != null){
            oXe = definition.get_SiteviewQuery().get_BusObSearchCriteria();
        }
        SiteviewQuery query = new SiteviewQuery();
        query.AddBusObQuery(this.get_ChartPartDef().get_BusObName(),QueryInfoToGet.All);
        if(!this.get_ChartPartDef().get_SubGroupByDef().get_Apply()){
            if(!this.get_ChartPartDef().get_GroupByDef().get_Duration()){
                if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField()){


                    return this.AppendDateTimeFieldGroupByCriteria(query,row,column,dataSource,oXe,false);
                }


                return this.AppendFieldGroupByCriteria(query,row,column,dataSource,oXe,false,true);
            }


            return this.AppendDateTimeFieldGroupByDurationCriteria(query,row,column,dataSource,oXe,false);
        }
        if(!this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && !this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            
            query = this.AppendFieldGroupByCriteria(query, row, column, dataSource, oXe, true, true);
        }
        if((!this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()) || (this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && !this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField())){
            
            query = this.AppendDateTimeFieldGroupByCriteria(query, row, column, dataSource, oXe, true);
        }
        if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            
            query = this.AppendDateTimeFieldGroupByCriteria(query, row, column, dataSource, oXe, true);
        }

        return query;

    }
    
    private  SiteviewQuery AppendDateTimeFieldGroupByCriteria(SiteviewQuery query, Integer row, Integer column, DataTable dataSource, XmlElement oXe, Boolean twoGroupBys)
    {
        String paraName = "";
        String paraValue = "";
        String interval = "";
        String caption = "";
        if(!twoGroupBys){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime startDateTimeForDrillDown = DataConverter.GetStartDateTimeForDrillDown(paraName, paraValue);
            DateTime endDateTimeForDrillDown = DataConverter.GetEndDateTimeForDrillDown(paraName, paraValue);
            XmlElement element = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement element2 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_GroupByDef().get_FieldName(), startDateTimeForDrillDown, endDateTimeForDrillDown);
            XmlElement element3 = query.get_CriteriaBuilder().AndExpressions(element, element2);
            query.set_BusObSearchCriteria(element3);

            return query;
        }
        if(!this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            interval = this.get_ChartPartDef().get_SubGroupByDef().get_Interval();
            caption = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime dtFirstDay = DataConverter.GetStartDateTimeFor2GroupByDrillDown(interval, caption);
            DateTime dtLastDay = DataConverter.GetEndDateTimeFor2GroupByDrillDown(interval, caption);
            XmlElement element4 = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement element5 = query.get_CriteriaBuilder().FieldAndValueExpression(paraName, Siteview.Operators.Equals, paraValue);
            XmlElement element6 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_SubGroupByDef().get_FieldName(), dtFirstDay, dtLastDay);
            XmlElement element7 = query.get_CriteriaBuilder().AndExpressions(element4, element5, element6);
            query.set_BusObSearchCriteria(element7);
        }
        if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && !this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            interval = this.get_ChartPartDef().get_SubGroupByDef().get_FieldName();
            caption = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime time5 = DataConverter.GetStartDateTimeForDrillDown(paraName, paraValue);
            DateTime time6 = DataConverter.GetEndDateTimeForDrillDown(paraName, paraValue);
            XmlElement element8 = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement element9 = query.get_CriteriaBuilder().FieldAndValueExpression(interval, Siteview.Operators.Equals, caption);
            XmlElement element10 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_GroupByDef().get_FieldName(), time5, time6);
            XmlElement element11 = query.get_CriteriaBuilder().AndExpressions(element8, element9, element10);
            query.set_BusObSearchCriteria(element11);
        }
        if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            interval = this.get_ChartPartDef().get_SubGroupByDef().get_Interval();
            caption = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime time7 = DataConverter.GetStartDateTimeForDrillDown(paraName, paraValue);
            DateTime time8 = DataConverter.GetEndDateTimeForDrillDown(paraName, paraValue);
            DateTime time9 = DataConverter.GetStartDateTimeFor2GroupByDrillDown(interval, caption);
            DateTime time10 = DataConverter.GetEndDateTimeFor2GroupByDrillDown(interval, caption);
            XmlElement element12 = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement element13 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_GroupByDef().get_FieldName(), time7, time8);
            XmlElement element14 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_SubGroupByDef().get_FieldName(), time9, time10);
            XmlElement element15 = query.get_CriteriaBuilder().AndExpressions(element12, element13, element14);
            query.set_BusObSearchCriteria(element15);
        }

        return query;

    }
    
    private  SiteviewQuery AppendFieldGroupByCriteria(SiteviewQuery query, Integer row, Integer column, DataTable dataSource, XmlElement oXe, Boolean twoGroupBys, Boolean appendDateRange)
    {
        String strField = "";
        String strValue = "";
        String str3 = "";
        String columnName = "";
        String[] strArray = null;
        XmlElement busObSearchCriteria = null;
        XmlElement element2 = null;
        XmlElement element3 = null;
        XmlElement element4 = null;
        XmlElement[] axeExpressions = null;
        XmlElement element5 = null;
        if(appendDateRange){
            
            query = this.AppendDateRange(query, oXe);
        }
        if(!twoGroupBys){
            String strB = XmlDashboardPartCategory.ToString(DashboardPartCategory.ChartPie);
            String str6 = XmlDashboardPartCategory.ToString(DashboardPartCategory.ChartFunnel);
            if((clr.System.StringStaticWrapper.Compare(this.get_ChartPartDef().get_CategoryAsString(),strB,true) == 0) || (clr.System.StringStaticWrapper.Compare(this.get_ChartPartDef().get_CategoryAsString(),str6,true) == 0)){
                strField = dataSource.get_Columns().get_Item(0).get_ColumnName();
                strValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
                
                strArray = strValue.split(",");
            }
            else{
                strField = dataSource.get_Columns().get_Item(0).get_ColumnName();

                if(!DataConverter.IsReservedColumnName(dataSource.get_Columns().get_Item(column))){
                    strValue = dataSource.get_Columns().get_Item(column).get_ColumnName();
                }
                
                strArray = strValue.split(",");
            }
            if(query.get_BusObSearchCriteria() == null){
                if(strArray.length > 2){
                    axeExpressions = new XmlElement[strArray.length];
                    for( Integer i = 0;i < strArray.length;i++){
                        
                        axeExpressions[i] = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strArray[i]);
                    }
                    
                    element4 = query.get_CriteriaBuilder().OrExpressions(axeExpressions);
                }
                else{
                    
                    element4 = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strValue);
                }
            }
            else{
                busObSearchCriteria = query.get_BusObSearchCriteria();
                if(strArray.length > 2){
                    axeExpressions = new XmlElement[strArray.length];
                    for(Integer j = 0;j < strArray.length;j++){
                        
                        axeExpressions[j] = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strArray[j]);
                    }
                    
                    element5 = query.get_CriteriaBuilder().OrExpressions(axeExpressions);
                    
                    element4 = query.get_CriteriaBuilder().AndExpressions(busObSearchCriteria, element5);
                }
                else{
                    
                    element2 = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strValue);
                    
                    element4 = query.get_CriteriaBuilder().AndExpressions(busObSearchCriteria, element2);
                }
            }
        }
        else{
            strField = dataSource.get_Columns().get_Item(0).get_Caption();
            strValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            if(this.get_GroupByFieldName2().indexOf(".") != -1){
                str3 = this.get_GroupByFieldName2();
            }
            else{
                
                str3 = String.format("%s.%s", this.get_BusObName(), this.get_GroupByFieldName2());
            }

            if(!DataConverter.IsReservedColumnName(dataSource.get_Columns().get_Item(column))){
                columnName = dataSource.get_Columns().get_Item(column).get_ColumnName();
            }
            if(query.get_BusObSearchCriteria() == null){
                
                busObSearchCriteria = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strValue);
                
                element2 = query.get_CriteriaBuilder().FieldAndValueExpression(str3, Siteview.Operators.Equals, columnName);
                
                element4 = query.get_CriteriaBuilder().AndExpressions(busObSearchCriteria, element2);
            }
            else{
                busObSearchCriteria = query.get_BusObSearchCriteria();
                
                element2 = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strValue);
                
                element3 = query.get_CriteriaBuilder().FieldAndValueExpression(str3, Siteview.Operators.Equals, columnName);
                
                element4 = query.get_CriteriaBuilder().AndExpressions(busObSearchCriteria, element2, element3);
            }
        }
        query.set_BusObSearchCriteria(element4);

        return query;

    }
    
    private  SiteviewQuery AppendDateTimeFieldGroupByDurationCriteria(SiteviewQuery query, Integer row, Integer column, DataTable dataSource, XmlElement oXe, Boolean twoGroupBys)
    {
        String strField = "";
        String strValue = "";
        String paraName = "";
        String paraValue = "";
        if(!twoGroupBys){
            strField = dataSource.get_Columns().get_Item(0).get_Caption();
            strValue = dataSource.get_Columns().get_Item(column).get_Caption().split(" ")[0];
            XmlElement element = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement xeQueryFunction = this.GenerateDateTimeDurationGroupBy(query, this.get_ChartPartDef().get_GroupByDef().get_DurationUnit(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationStartValue(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndFunction(), this.get_ChartPartDef().get_GroupByDef().get_DurationEndValue());
            XmlElement element3 = query.get_CriteriaBuilder().QueryFunctionExpression(xeQueryFunction, Siteview.Operators.Equals, ValueSources.Literal, strValue);
            XmlElement element4 = query.get_CriteriaBuilder().AndExpressions(element, element3);
            query.set_BusObSearchCriteria(element4);

            return query;
        }
        if(!this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            strField = dataSource.get_Columns().get_Item(0).get_Caption();
            strValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            paraName = this.get_ChartPartDef().get_SubGroupByDef().get_Interval();
            paraValue = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime dtFirstDay = DataConverter.GetStartDateTimeFor2GroupByDrillDown(paraName, paraValue);
            DateTime dtLastDay = DataConverter.GetEndDateTimeFor2GroupByDrillDown(paraName, paraValue);
            XmlElement element5 = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement element6 = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strValue);
            XmlElement element7 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_SubGroupByDef().get_FieldName(), dtFirstDay, dtLastDay);
            XmlElement element8 = query.get_CriteriaBuilder().AndExpressions(element5, element6, element7);
            query.set_BusObSearchCriteria(element8);
        }
        if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && !this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            strField = dataSource.get_Columns().get_Item(0).get_Caption();
            strValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            paraName = this.get_ChartPartDef().get_SubGroupByDef().get_FieldName();
            paraValue = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime startDateTimeForDrillDown = DataConverter.GetStartDateTimeForDrillDown(strField, strValue);
            DateTime endDateTimeForDrillDown = DataConverter.GetEndDateTimeForDrillDown(strField, strValue);
            XmlElement element9 = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement element10 = query.get_CriteriaBuilder().FieldAndValueExpression(paraName, Siteview.Operators.Equals, paraValue);
            XmlElement element11 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_GroupByDef().get_FieldName(), startDateTimeForDrillDown, endDateTimeForDrillDown);
            XmlElement element12 = query.get_CriteriaBuilder().AndExpressions(element9, element10, element11);
            query.set_BusObSearchCriteria(element12);
        }
        if(this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            strField = dataSource.get_Columns().get_Item(0).get_Caption();
            strValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            paraName = this.get_ChartPartDef().get_SubGroupByDef().get_Interval();
            paraValue = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime time5 = DataConverter.GetStartDateTimeForDrillDown(strField, strValue);
            DateTime time6 = DataConverter.GetEndDateTimeForDrillDown(strField, strValue);
            DateTime time7 = DataConverter.GetStartDateTimeFor2GroupByDrillDown(paraName, paraValue);
            DateTime time8 = DataConverter.GetEndDateTimeFor2GroupByDrillDown(paraName, paraValue);
            XmlElement element13 = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            XmlElement element14 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_GroupByDef().get_FieldName(), time5, time6);
            XmlElement element15 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, this.get_ChartPartDef().get_SubGroupByDef().get_FieldName(), time7, time8);
            XmlElement element16 = query.get_CriteriaBuilder().AndExpressions(element13, element14, element15);
            query.set_BusObSearchCriteria(element16);
        }

        return query;

    }
    
    private  SiteviewQuery AppendDateRange(SiteviewQuery query, XmlElement oXe)
    {
        if(oXe != null){
            XmlElement element = query.get_CriteriaBuilder().ImportSearchCriteria(oXe);
            query.set_BusObSearchCriteria(element);
        }
        if(this.get_ChartPartDef().get_DefaultDateRangeDef().get_ApplyDateRange()){
            if(this.m_vCurrentRunTimeDateRange == null){
                
                query = DateRangeCriteriaCreator.AddDefaultDateRangeCriteria(super.getApi(), query, this.get_ChartPartDef().get_DefaultDateRangeDef());

                return query;
            }
            
            query = DateRangeCriteriaCreator.AddDateRangeCriteria(super.getApi(), query, this.m_vCurrentRunTimeDateRange.get_DateRange(), this.get_ChartPartDef().get_DefaultDateRangeDef().get_DateTimeField());
        }

        return query;

    }
    
    protected  SiteviewQuery GetDrillDownSiteviewQuery(SiteviewQuery query, Integer row, Integer column, DataTable dataChildSource)
    {
        if(!((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Apply()){
            if(!((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Duration()){
                if(((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField()){
                    
                    query = this.AppendDrillDownDateTimeFieldGroupByCriteria(query, row, column, dataChildSource, false);

                    return query;
                }
                
                query = this.AppendDrillDownFieldGroupByCriteria(query, row, column, dataChildSource, false);
            }

            return query;
        }
        if(!this.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && !this.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()){
            
            query = this.AppendDrillDownFieldGroupByCriteria(query, row, column, dataChildSource, true);
        }

        return query;

    }
    
    private  SiteviewQuery AppendDrillDownDateTimeFieldGroupByCriteria(SiteviewQuery parentQuery, Integer row, Integer column, DataTable dataSource, Boolean twoGroupBys)
    {
        String paraName = "";
        String paraValue = "";
        String interval = "";
        String caption = "";
        XmlElement busObSearchCriteria = parentQuery.get_BusObSearchCriteria();
        SiteviewQuery query = new SiteviewQuery();
        query.AddBusObQuery(this.get_chartPart().get_BusObName(),QueryInfoToGet.All);
        if(!twoGroupBys){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime startDateTimeForDrillDown = DataConverter.GetStartDateTimeForDrillDown(paraName, paraValue);
            DateTime endDateTimeForDrillDown = DataConverter.GetEndDateTimeForDrillDown(paraName, paraValue);
            XmlElement element2 = query.get_CriteriaBuilder().ImportSearchCriteria(busObSearchCriteria);
            XmlElement element3 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_FieldName(), startDateTimeForDrillDown, endDateTimeForDrillDown);
            XmlElement element4 = query.get_CriteriaBuilder().AndExpressions(element2, element3);
            query.set_BusObSearchCriteria(element4);

            return query;
        }
        if(!((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField() && ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_DateTimeField()){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            interval = ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval();
            caption = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime dtFirstDay = DataConverter.GetStartDateTimeFor2GroupByDrillDown(interval, caption);
            DateTime dtLastDay = DataConverter.GetEndDateTimeFor2GroupByDrillDown(interval, caption);
            XmlElement element5 = query.get_CriteriaBuilder().ImportSearchCriteria(busObSearchCriteria);
            XmlElement element6 = query.get_CriteriaBuilder().FieldAndValueExpression(paraName, Siteview.Operators.Equals, paraValue);
            XmlElement element7 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_FieldName(), dtFirstDay, dtLastDay);
            XmlElement element8 = query.get_CriteriaBuilder().AndExpressions(element5, element6, element7);
            query.set_BusObSearchCriteria(element8);
        }
        if(((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField() && !((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_DateTimeField()){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            interval = ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_FieldName();
            caption = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime time5 = DataConverter.GetStartDateTimeForDrillDown(paraName, paraValue);
            DateTime time6 = DataConverter.GetEndDateTimeForDrillDown(paraName, paraValue);
            XmlElement element9 = query.get_CriteriaBuilder().ImportSearchCriteria(busObSearchCriteria);
            XmlElement element10 = query.get_CriteriaBuilder().FieldAndValueExpression(interval, Siteview.Operators.Equals, caption);
            XmlElement element11 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_FieldName(), time5, time6);
            XmlElement element12 = query.get_CriteriaBuilder().AndExpressions(element9, element10, element11);
            query.set_BusObSearchCriteria(element12);
        }
        if(((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField() && ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_DateTimeField()){
            paraName = dataSource.get_Columns().get_Item(0).get_Caption();
            paraValue = (String)dataSource.get_Rows().get_Item(row).get_ItemArray()[0];
            interval = ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval();
            caption = dataSource.get_Columns().get_Item(column).get_Caption();
            DateTime time7 = DataConverter.GetStartDateTimeForDrillDown(paraName, paraValue);
            DateTime time8 = DataConverter.GetEndDateTimeForDrillDown(paraName, paraValue);
            DateTime time9 = DataConverter.GetStartDateTimeFor2GroupByDrillDown(interval, caption);
            DateTime time10 = DataConverter.GetEndDateTimeFor2GroupByDrillDown(interval, caption);
            XmlElement element13 = query.get_CriteriaBuilder().ImportSearchCriteria(busObSearchCriteria);
            XmlElement element14 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_FieldName(), time7, time8);
            XmlElement element15 = DateRangeCriteriaCreator.GetCriteriaForDateRanges(query, ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_FieldName(), time9, time10);
            XmlElement element16 = query.get_CriteriaBuilder().AndExpressions(element13, element14, element15);
            query.set_BusObSearchCriteria(element16);
        }

        return query;

    }
    
    private  SiteviewQuery AppendDrillDownFieldGroupByCriteria(SiteviewQuery parentQuery, Integer row, Integer column, DataTable dataChildSource, Boolean twoDrillDownGroupBys)
    {
        String strField = "";
        String strValue = "";
        String fieldName = "";
        String caption = "";
        XmlElement xeSearchCriteria = null;
        XmlElement element2 = null;
        XmlElement element3 = null;
        XmlElement element4 = null;
        XmlElement element5 = null;
        xeSearchCriteria = parentQuery.get_BusObSearchCriteria();
        SiteviewQuery query = new SiteviewQuery();
        query.AddBusObQuery(this.get_chartPart().get_BusObName(),QueryInfoToGet.All);
        if(!twoDrillDownGroupBys){
            if(((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_DrilldownChartType() == ChartType.PieChart){
                strField = dataChildSource.get_Columns().get_Item(0).get_Caption();
                strValue = (String)dataChildSource.get_Rows().get_Item(row).get_ItemArray()[0];
            }
            else{
                strField = dataChildSource.get_Columns().get_Item(0).get_Caption();
                strValue = dataChildSource.get_Columns().get_Item(column).get_Caption();
            }

            if(strValue.equals(Res.get_Default().GetString("Chart.NullGroupByFieldValue"))){
                strValue = "";
            }
            
            element2 = query.get_CriteriaBuilder().ImportSearchCriteria(xeSearchCriteria);

            if(strValue.equals("")){
                
                element3 = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Empty);
            }
            else{
                
                element3 = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strValue);
            }
            
            element5 = query.get_CriteriaBuilder().AndExpressions(element2, element3);
        }
        else{
            strField = dataChildSource.get_Columns().get_Item(0).get_Caption();
            strValue = (String)dataChildSource.get_Rows().get_Item(row).get_ItemArray()[0];
            fieldName = ((DrilldownDef)this.get_ChartPartDef().get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_FieldName();
            caption = dataChildSource.get_Columns().get_Item(column).get_Caption();
            
            element2 = query.get_CriteriaBuilder().ImportSearchCriteria(xeSearchCriteria);
            
            element3 = query.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, strValue);
            
            element4 = query.get_CriteriaBuilder().FieldAndValueExpression(fieldName, Siteview.Operators.Equals, caption);
            
            element5 = query.get_CriteriaBuilder().AndExpressions(element2, element3, element4);
        }
        query.set_BusObSearchCriteria(element5);

        return query;

    }
    
    protected  void GenerateDefaultGridDef(String BusObName)
    {
        IEnumerator it1 = super.getApi().get_LiveDefinitionLibrary().GetPlaceHolderList(DefRequest.ForList(GridDef.get_ClassName())).GetEnumerator();
        while(it1.MoveNext()){
            PlaceHolder holder = (PlaceHolder)it1.get_Current();
            if(clr.System.StringStaticWrapper.Compare(holder.get_LinkedTo(),BusObName,true) == 0){
                this.set_DrilldownGridDefID(holder.get_Id());
                this.set_DrilldownGridDef(super.getApi().get_Presentation().GetGridDefById(this.get_DrilldownGridDefID()));
                break;
            }
        }

    }
    
    protected  ArrayList GetRequestedFields(GridDef gridDef)
    {
        BusinessObjectDef businessObjectDef = super.getApi().get_BusObDefinitions().GetBusinessObjectDef(this.get_chartPart().get_BusObName());
        ArrayList list = new ArrayList();
        Boolean flag = false;

        if(businessObjectDef.get_ChildOfGroup() && !BusinessObjectDef.IsDerivedClass(gridDef.get_BaseObject())){
            IEnumerator it1 = gridDef.get_ColumnDefs().GetEnumerator();
            while(it1.MoveNext()){
                ColumnDef def2 = (ColumnDef)it1.get_Current();
                IEnumerator it2 = businessObjectDef.get_FieldDefs().GetEnumerator();
                while(it2.MoveNext()){
                    FieldDef def3 = (FieldDef)it2.get_Current();
                    if(clr.System.StringStaticWrapper.Compare(def3.get_BaseQualifiedName(),def2.get_QualifiedName()) == 0){
                        list.Add(def3.get_QualifiedName());
                        if(clr.System.StringStaticWrapper.Compare(def3.get_QualifiedName(),businessObjectDef.get_IdField().get_QualifiedName()) == 0){
                            flag = true;
                        }
                        break;
                    }
                }
            }
            if(!flag){
                list.Add(businessObjectDef.get_IdField().get_QualifiedName());
            }
            FieldDef def4 = null;
            FieldDef[] __fDef_2 = new FieldDef[1];
            boolean ret = super.CheckGridRowColor(businessObjectDef,__fDef_2);
            def4 = __fDef_2[0];
            		
            if( ret && (def4 != null)){
                list.Add(def4.get_QualifiedName());
            }

            return list;
        }
        list.AddRange(gridDef.get_ColumnNames());
        FieldDef fDef = null;
        FieldDef[] __fDef_3 = new FieldDef[1];
        boolean ret2 = super.CheckGridRowColor(businessObjectDef,__fDef_3);
        fDef = __fDef_3[0];
        if(ret2 && (fDef != null)){
            list.Add(fDef.get_QualifiedName());
        }

        return list;

    }
    
    protected  void AddFiscalDateRangeIfAny(Core.Dashboards.ChartPartDef partDef)
    {
        Integer fiscalYear = 0;
        Integer fiscalQuarter = 0;
        
        Integer[] __fiscalYear_0 = new Integer[1];
        Integer[] __fiscalQuarter_1 = new Integer[1];
        super.getApi().get_SystemFunctions().GetFiscalYearAndQuarterGivenDate(DateTime.get_Now(),__fiscalYear_0,__fiscalQuarter_1);
        fiscalYear = __fiscalYear_0[0];
        fiscalQuarter = __fiscalQuarter_1[0];
        Integer result = 0;
        
        if((partDef.get_PrecedingFiscalQuarters() < 0) || (partDef.get_SucceedingFiscalQuarters() > 0)){
            for( Integer i = partDef.get_PrecedingFiscalQuarters(); i <= partDef.get_SucceedingFiscalQuarters();i++){
                Integer a = fiscalQuarter + i;
                ClrInt32 __integer_2 = new ClrInt32();
                Integer num6 = system.Math.DivRem(a, 4, __integer_2);
                result = __integer_2.getValue();
                if(result > 0){
                    a = result;
                }
                if(result <= 0){
                    num6--;
                    a = 4 + result;
                }
                super.AddFiscalDateRange(fiscalYear + num6,a);
            }
        }
        if((partDef.get_PrecedingFiscalYears() < 0) || (partDef.get_SucceedingFiscalYears() > 0)){
            for(            Integer j = partDef.get_PrecedingFiscalYears();
j <= partDef.get_SucceedingFiscalYears();j++){
                super.AddFiscalDateRange(fiscalYear + j,0);
            }
        }

    }
    
    public double getDoubleValue(Object obj){
    	if (obj instanceof system.ClrInt32){
    		return ((system.ClrInt32)obj).getValue();
    	}else if (obj instanceof system.ClrInt16){
    		return ((system.ClrInt16)obj).getValue();
    	}else if (obj instanceof system.ClrDouble){
    		return ((system.ClrDouble)obj).getValue();
    	}else if (obj instanceof system.ClrUInt16){
    		return ((system.ClrUInt16)obj).getValue();
    	}else if (obj instanceof system.ClrUInt32){
    		return ((system.ClrUInt32)obj).getValue();
    	}else if (obj instanceof system.ClrInt64){
    		return ((system.ClrInt64)obj).getValue();
    	}else if (obj instanceof system.ClrUInt64){
    		return ((system.ClrUInt64)obj).getValue();
    	}else if (obj instanceof system.ClrSingle){
    		return ((system.ClrSingle)obj).getValue();
    	}
    	
    	return 0;
    }
    
    protected SiteviewQuery get_BaseQuery(){

        return this.m_baseQuery;
    }
    protected void set_BaseQuery(SiteviewQuery value){
        this.m_baseQuery = value;
    }
}
