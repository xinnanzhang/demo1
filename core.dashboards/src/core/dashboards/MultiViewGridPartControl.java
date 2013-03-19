package core.dashboards;

import org.eclipse.swt.widgets.TabFolder;

import system.Xml.XmlElement;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.MultiViewGridPartDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.ViewDef;
import Siteview.SiteviewQuery;
import Siteview.Api.ISiteviewApi;

public class MultiViewGridPartControl extends GridPartControl {

//	public MultiViewGridPartControl(DashboardControl parent, int style) {
//		super(parent, style);
//		// TODO Auto-generated constructor stub
//	}

	public MultiViewGridPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent, boolean b) {
		super(iSiteviewApi,parent,true);
		
		super.set_GridId("MultiViewGridPartControl");
	}
	
	public MultiViewGridPartControl(ISiteviewApi api, TabFolder parent,
			boolean b) {
		// TODO Auto-generated constructor stub
		super(api,parent, true);
		
		super.set_GridId("MultiViewGridPartControl");
	}

	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
        super.DefineFromDef(def, partRefDef);
        MultiViewGridPartDef dashboardPartDef = (MultiViewGridPartDef) super.get_DashboardPartDef();
        if (dashboardPartDef != null){
        	this.set_GridId(dashboardPartDef.get_Name());
            super.set_ViewControlVisible(dashboardPartDef.get_ViewControlVisible());
            if (super.get_ViewControlVisible()){
                ViewDef viewDef = null;
                String gridSelectedView = super.getApi().get_SettingsService().get_DisplaySettings().GetGridSelectedView(super.get_GridId());
                for(ViewDef def4 : dashboardPartDef.get_ViewOptions()){
                	// System.out.println(def4.get_Alias());
                    viewDef = super.AddView(def4, gridSelectedView);
                }
                if (viewDef == null){
                    for (ViewDef def5 : dashboardPartDef.get_ViewOptions()){
                        viewDef = super.SetDefaultView(def5);
                        if (viewDef != null){
                            break;
                        }
                    }
                }
                if (viewDef != null){
                    if (viewDef.get_LimitQuery()){
                        super.set_Query(super.AddQueryLimit(super.get_Query(), viewDef.get_NumberOfRecordToReturn(), viewDef.get_IsDescending(), viewDef.get_OrderByFieldName()));
                    }
                    super.set_Query(this.AddViewCriteria(super.get_Query(), viewDef));
                }
                // 当有时间范围时,第一次加载无效果处理,增加如下问题解决.
                if(super.getM_DateRangeControl().get_Combo().getSelectionIndex()!=-1){
	                // System.out.println(super.getM_DateRangeControl().get_Combo().getSelectionIndex());
	                // System.out.println(super.getM_DateRangeControl().get_Combo().getItem(super.getM_DateRangeControl().get_Combo().getSelectionIndex()));
	                // System.out.println(super.getM_DateRangeControl().get_Combo().getData(super.getM_DateRangeControl().get_Combo().getItem(super.getM_DateRangeControl().get_Combo().getSelectionIndex())));
	                ViewDef vDateTimeDef = (ViewDef) super.getM_DateRangeControl().get_Combo().getData(super.getM_DateRangeControl().get_Combo().getItem(super.getM_DateRangeControl().get_Combo().getSelectionIndex()));
	                SiteviewQuery query = super.get_Query();
	                if (vDateTimeDef != null){
	                    if (vDateTimeDef.get_IsCustom()){
	                        query = DateRangeCriteriaCreator.AddCustomDateRangeCriteria(query, dashboardPartDef.get_DefaultDateRangeDef().get_DateTimeField(), vDateTimeDef.get_StartDateTime(), vDateTimeDef.get_EndDateTime());
	                    }else if (vDateTimeDef.get_IsFiscalPeriod()){
	                        query = DateRangeCriteriaCreator.AddFiscalDateRangeCriteria(super.getApi(), query, dashboardPartDef.get_DefaultDateRangeDef().get_DateTimeField(), vDateTimeDef.get_FiscalPeriod());
	                    }else{
	                        query = DateRangeCriteriaCreator.AddDateRangeCriteria(super.getApi(), query, vDateTimeDef.get_DateRange(), dashboardPartDef.get_DefaultDateRangeDef().get_DateTimeField());
	                    }
	                    super.set_Query(query);
	                }
                }
            }
            super.SetSelectionArea();
        }
    }
	
	@Override
	protected void ReQuery(ViewDef vViewDef, ViewDef vDateTimeDef){
        String iD = "";
        if (!super.get_FirstTimeToLoad()){
            MultiViewGridPartDef dashboardPartDef = (MultiViewGridPartDef) super.get_DashboardPartDef();
            SiteviewQuery query = super.CreateSiteviewQuery();
            if (vViewDef != null){
                iD = "Dashboard." + dashboardPartDef.get_QueryGroupScope() + "." + dashboardPartDef.get_QueryGroupScopeOwner() + "." + dashboardPartDef.get_QueryGroupName() + "." + vViewDef.get_Name();
                if (vViewDef.get_LimitQuery()){
                    query = super.AddQueryLimit(query, vViewDef.get_NumberOfRecordToReturn(), vViewDef.get_IsDescending(), vViewDef.get_OrderByFieldName());
                }
                query = this.AddViewCriteria(query, vViewDef);
                SiteviewQuery queryCopy = null;
                //SearchParameterResolver.ResolveSearchParameters(vViewDef.get_Alias(), query, queryCopy, iD, super.getApi());
                if (queryCopy != null){
                    query = queryCopy;
                }
            }
            if (vDateTimeDef != null){
                if (vDateTimeDef.get_IsCustom()){
                    query = DateRangeCriteriaCreator.AddCustomDateRangeCriteria(query, dashboardPartDef.get_DefaultDateRangeDef().get_DateTimeField(), vDateTimeDef.get_StartDateTime(), vDateTimeDef.get_EndDateTime());
                }else if (vDateTimeDef.get_IsFiscalPeriod()){
                    query = DateRangeCriteriaCreator.AddFiscalDateRangeCriteria(super.getApi(), query, dashboardPartDef.get_DefaultDateRangeDef().get_DateTimeField(), vDateTimeDef.get_FiscalPeriod());
                }else{
                    query = DateRangeCriteriaCreator.AddDateRangeCriteria(super.getApi(), query, vDateTimeDef.get_DateRange(), dashboardPartDef.get_DefaultDateRangeDef().get_DateTimeField());
                }
            }
            super.set_Query(query);
            this.DoDataRefresh();
            if (vViewDef != null){
                super.getApi().get_SettingsService().get_DisplaySettings().SetGridSelectedView(super.get_GridId(), vViewDef.get_Alias());
            }
            if (vDateTimeDef != null)
            {
                super.getApi().get_SettingsService().get_DisplaySettings().SetGridSelectedDateRange(super.get_GridId(), vDateTimeDef.toString());
                super.getApi().get_SettingsService().get_DisplaySettings().SetGridDateRangeIsFiscal(super.get_GridId(), vDateTimeDef.get_IsFiscalPeriod());
                if (vDateTimeDef.get_IsCustom())
                {
                    super.getApi().get_SettingsService().get_DisplaySettings().SetGridCustomDateRangeStartDateTime(super.get_GridId(), vDateTimeDef.get_StartDateTime().toString());
                    super.getApi().get_SettingsService().get_DisplaySettings().SetGridCustomDateRangeEndDateTime(super.get_GridId(), vDateTimeDef.get_EndDateTime().toString());
                }
            }
            super.getApi().get_SettingsService().get_DisplaySettings().FlushGridSetting(super.get_GridId());
        }
    }

	@Override
    protected void SetGridSelectedView(String strView)
    {
        super.getApi().get_SettingsService().get_DisplaySettings().SetGridSelectedView(super.get_GridId(), strView);
    }
    
	private SiteviewQuery AddViewCriteria(SiteviewQuery query, ViewDef viewDef)
    {
        if (((query != null) && (viewDef != null)) && (viewDef.get_Criteria() != null))
        {
            XmlElement element = query.get_CriteriaBuilder().ImportSearchCriteria(viewDef.get_Criteria());
            query.set_BusObSearchCriteria(query.get_CriteriaBuilder().AndExpressions(element, query.get_BusObSearchCriteria()));
            if (viewDef.get_HaveSearchParameters())
            {
                query.set_SearchParameters(viewDef.get_SearchParameters());
            }
        }
        return query;
    }
}
