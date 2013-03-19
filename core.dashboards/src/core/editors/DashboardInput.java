package core.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import Siteview.Api.ISiteviewApi;

import Core.Dashboards.DashboardDef;

public class DashboardInput implements IEditorInput {
	
	private DashboardDef dashboardDef = null;
	private ISiteviewApi siteviewApi = null;
	private boolean hided =false;
	
	public boolean isHided() {
		return hided;
	}

	public void setHided(boolean hided) {
		this.hided = hided;
	}

	public DashboardInput(ISiteviewApi api,DashboardDef def){
		siteviewApi = api;
		dashboardDef = def;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		
		return null;
	}

	@Override
	public String getName() {
		//return "Dashboard";
		return "Dashboard:" + dashboardDef.get_Name();
		//return null;
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		return "";//dashboardDef.get_Alias();
		//return null;
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return getName();
	}
	
	
	public DashboardDef getDashboardDef(){
		return dashboardDef;
	}
	
	public ISiteviewApi getApi(){
		return siteviewApi;
	}
	
}
