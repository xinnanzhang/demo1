package core.ui;

import org.eclipse.swt.widgets.Shell;

import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;

import Core.Dashboards.DashboardPartDef;

import siteview.windows.forms.ICommandHandler;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

public class EditDashboardPartCommandHandler implements ICommandHandler {

	@Override
	public Object ProcessCommand(String action, ICollection actionParameters) {
		if (action.equals("EditDashboardPart.RUN")){
			return ProcessRun(action,actionParameters);
		}
		return null;
	}
	
    private  Object ProcessRun(String strCommand, ICollection collCommandParams)
    {
    	String Category = "";
    	Shell shell = null;
    	DashboardPartDef dashboardPartDef = null;
    	DefinitionLibrary m_Library = null;
    	ScopeUtil part_ScopeUtil = null;
    	ISiteviewApi m_Api = null;
    	String linkTo = "";
    	
    	int i = 0;
    	IEnumerator it = collCommandParams.GetEnumerator();
    	while(it.MoveNext())
    	{
    		Object value = it.get_Current();
    		
    		switch(i)
    		{
			case 0:
				Category = (String)value;
				break;
			case 1:
				shell = (Shell)value;
				break;
			case 2:
				dashboardPartDef = (DashboardPartDef)value;
				break;
			case 3:
				m_Library = (DefinitionLibrary)value;
				break;
			case 4:
				part_ScopeUtil = (ScopeUtil)value;
				break;
			case 5:
				m_Api = (ISiteviewApi)value;
				break;
			case 6:
				linkTo = (String)value;
    		}
    		i ++;
    	}
    	dashboardPartDef.set_EditMode(true);
    	dashboardPartDef = DashBoardPartCenterForm.getPartType(Category, shell, dashboardPartDef, m_Library, part_ScopeUtil, m_Api, linkTo, "Edit",null);
		return dashboardPartDef;
    }

}
