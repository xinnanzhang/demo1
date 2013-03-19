package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import Siteview.Api.ISiteviewApi;

public class OutlookInboxPartControl extends DashboardPartControl {

	public OutlookInboxPartControl(DashboardControl parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public OutlookInboxPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent,SWT.NONE);
	}

}
