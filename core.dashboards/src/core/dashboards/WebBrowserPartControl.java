package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;

import system.ApplicationException;
import system.Threading.AutoResetEvent;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.WebBrowserPartDef;
import Siteview.Api.ISiteviewApi;

public class WebBrowserPartControl extends DashboardPartControl {

	public WebBrowserPartControl(DashboardControl parent,
			int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public WebBrowserPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);
		
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);
	}

	
    private AutoResetEvent m_CompleteEvent;
    private ILoadingStatusSink m_Sink;
    private String m_strFilePath = "";
    private Browser m_WebBrowser;
    private StackLayout stacklayout;
    
    @Override
    protected  void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
        super.DefineFromDef(def, partRefDef);
        WebBrowserPartDef dashboardPartDef = (WebBrowserPartDef) super.get_DashboardPartDef();
        if (dashboardPartDef != null){
        	this.m_WebBrowser = new Browser(this.get_MainArea(), SWT.NONE);
            this.m_strFilePath = dashboardPartDef.get_FilePath();
//            System.out.println(this.m_strFilePath);
        }
    }
    
    @Override
    public Object GetData(ILoadingStatusSink sink){
        this.m_Sink = sink;
        if ("".compareTo(this.m_strFilePath) != 0){
            try{
                this.m_CompleteEvent = new AutoResetEvent(false);
                this.m_WebBrowser.setUrl(this.m_strFilePath);
                this.m_CompleteEvent.WaitOne();
                this.m_CompleteEvent.Close();
                this.m_CompleteEvent = null;
                this.m_WebBrowser.setVisible(true);
                
                showPanel(this.m_WebBrowser);
            }catch (Exception e){
            	//
            }
        }
        return null;
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
                this.m_WebBrowser.setUrl(this.m_strFilePath);
                this.m_WebBrowser.setVisible(true);
                showPanel(this.m_WebBrowser);
            }catch (Exception e){
            	//
            }
        }
    }
    
    private void showPanel(Browser p){
		// TODO Auto-generated method stub
    	this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
    }
}
