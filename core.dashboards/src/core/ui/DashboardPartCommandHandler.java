package core.ui;


import org.eclipse.jface.window.Window;

import Core.Dashboards.DashboardPartDef;
import siteview.windows.forms.ICommandHandler;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

public class DashboardPartCommandHandler implements ICommandHandler {

	@Override
	public Object ProcessCommand(String action, ICollection actionParameters) {
		if (action.equals("DashboardPart.RUN")){
			return ProcessRun(action,actionParameters);
		}
		return null;
	}
	
    private Object ProcessRun(String strCommand, ICollection collCommandParams)
    {
    	
    	String strcontext="";
    	if(collCommandParams!=null&&collCommandParams.get_Count()>0){
    		
    		IEnumerator it = collCommandParams.GetEnumerator();
    		while(it.MoveNext()){
    			strcontext=(String) it.get_Current();
    			break;
    		}
    		
    	}
    	DashBoardPartCenterForm frm = new DashBoardPartCenterForm(DashboardPartDef.get_ClassName(),"Core.Security.Dashboards","仪表盘部件中心",strcontext, null, null, false,false);
		if(frm.open()==Window.OK){
			return frm.get_SelectedObject();
		}
		
		return null;
    }

}
