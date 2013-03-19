package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ObjectBrowserPartDef;
import Core.Dashboards.PartRefDef;
import Siteview.AutoTaskDef;
import Siteview.DefRequest;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.AggregateTreeBrowser;
import Siteview.Xml.SecurityRight;

public class ObjectBrowserPartControl extends DashboardPartControl {

	private AggregateTreeBrowser m_ObjectTreeBrowserApp;
	

	public ObjectBrowserPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.BORDER);
		initControl();
	}
	
	
	private void initControl(){
		m_ObjectTreeBrowserApp = new AggregateTreeBrowser(this.get_MainArea(), getApi(), "");
	}
	
	@Override
    protected  void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef)
    {
        super.DefineFromDef(def,partRefDef);
        ObjectBrowserPartDef dashboardPartDef = (ObjectBrowserPartDef)super.get_DashboardPartDef();
        if(dashboardPartDef != null){
            this.m_ObjectTreeBrowserApp.set_InstanceID(dashboardPartDef.get_Id());
            Boolean flag = dashboardPartDef.get_AllowDynamicRefresh() && super.getApi().get_SecurityService().HasModuleItemRight("Siteview.Security.AutoTasks","CalculateAggregateData",SecurityRight.True);
            AutoTaskDef definition = null;
            if(flag){
                definition = (AutoTaskDef)super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(dashboardPartDef.get_QuickActionScope(),dashboardPartDef.get_QuickActionScopeOwner(),AutoTaskDef.get_ClassName(),dashboardPartDef.get_QuickActionID(),false));
            }
            if(definition != null){
                this.m_ObjectTreeBrowserApp.set_AllowRefresh(true);
                this.m_ObjectTreeBrowserApp.set_AutoTaskDefToUseForRefreshing(definition);
            }
            else{
                this.m_ObjectTreeBrowserApp.set_AllowRefresh(false);
            }
            if((dashboardPartDef.get_ObjectBrowserIDs() != null) && (dashboardPartDef.get_ObjectBrowserIDs().get_Count() > 0)){
                this.m_ObjectTreeBrowserApp.set_IDsOfBrowsersToShow(dashboardPartDef.get_ObjectBrowserIDs());
            }
            this.m_ObjectTreeBrowserApp.reload();
            
        }

    }
}
