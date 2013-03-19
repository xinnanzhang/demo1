package core.dashboards;

import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;

import system.ApplicationException;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.MSProjectPartDef;
import Core.Dashboards.PartRefDef;
import Siteview.Api.ISiteviewApi;

public class MSProjectPartControl extends DashboardPartControl {

	public MSProjectPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);
		
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);
	}

	private String m_strFilePath = "";
    private Browser m_WebBrowser;
    private StackLayout stacklayout;
    
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
        super.DefineFromDef(def, partRefDef);
        MSProjectPartDef dashboardPartDef = (MSProjectPartDef) super.get_DashboardPartDef();
        if (dashboardPartDef != null){
        	this.m_WebBrowser = new Browser(this.get_MainArea(), 0);
            this.m_strFilePath = dashboardPartDef.get_FilePath();
            System.out.println(this.m_strFilePath);
        }
    }
	
	@Override
	public void LoadData(){
	    try{
	        this.LoadFile();
	    }catch (ApplicationException e){
	    	//
	    }
	}
	
	private void LoadFile(){
	    if ("".compareTo(this.m_strFilePath) != 0){
	        try{
	        	System.out.println(isConnect("http://www.baidu.com"));
//	        	this.m_WebBrowser.setText(this.m_strFilePath);
	            this.m_WebBrowser.setUrl(this.m_strFilePath);
	            this.m_WebBrowser.setVisible(true);
                showPancel(this.m_WebBrowser);
	        }catch (Exception e){
	        	//
	        }
	    }
	}
	
	private void showPancel(Browser p){
		// TODO Auto-generated method stub
    	this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
    }
	
	private static URL urlStr;
	private static HttpURLConnection connection;
	private static int state = -1;
	private static String succ;

	private synchronized String isConnect(String url) {
		int counts = 0;
		succ = null;
		if (url == null || url.length() <= 0) {
			return succ;
		}
		while (counts < 5) {
			try {
				urlStr = new URL(url);
				connection = (HttpURLConnection) urlStr.openConnection();
				state = connection.getResponseCode();
				if (state == 200) {
//					succ = connection.getURL().toString();
					succ = "ok";
				}
				break;
			}catch (Exception ex) {
				counts++;
				continue;
			}
		}
		return succ;
	}

}
