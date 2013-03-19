package core.ui;

import org.eclipse.jface.window.Window;

import siteview.windows.forms.ICommandHandler;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

public class DashboardCommandHandler implements ICommandHandler {

	@Override
	public Object ProcessCommand(String action, ICollection actionParameters) {
		if (action.equals("Dashboard.RUN")){
			return ProcessRun(action,actionParameters);
		}
		return null;
	}
	
    private  Object ProcessRun(String strCommand, ICollection collCommandParams)
    {
    	String strcontext="";
    	if(collCommandParams!=null&&collCommandParams.get_Count()>0){
    		
    		IEnumerator it = collCommandParams.GetEnumerator();
    		while(it.MoveNext()){
    			strcontext=(String) it.get_Current();
    			break;
    		}
    		
    	}
    	DashBoardCenterForm frm = new  DashBoardCenterForm(true,strcontext,true);
		frm.setBlockOnOpen(true);
		if(frm.open()==Window.OK){
		    return frm.getPlaceHolder();
		}
		return null;
    }

}
