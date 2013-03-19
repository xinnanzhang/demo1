package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Label;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ImagePartDef;
import Core.Dashboards.PartRefDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageHolder;

public class ImagePartControl extends DashboardPartControl {

	public ImagePartControl(DashboardControl parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public ImagePartControl(ISiteviewApi iSiteviewApi, DashboardControl parent) {
		super(iSiteviewApi,parent,SWT.NONE);
		
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);
	}

	private StackLayout stacklayout;
	private Label m_Image;
	
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef) {
        super.DefineFromDef(def, partRefDef);
        ImagePartDef dashboardPartDef = (ImagePartDef) super.get_DashboardPartDef();
        if (dashboardPartDef != null) {
//        	System.out.println(dashboardPartDef.get_ImageName());
            ImageHolder holder = ImageResolver.get_Resolver().ResolveImage(dashboardPartDef.get_ImageName());
            if (!holder.get_Empty()){
//                System.out.println(holder.GetAsImage());
                m_Image = new Label(this.get_MainArea(),SWT.NONE);
                m_Image.setLocation(0, 0);
                m_Image.setAlignment(SWT.TOP|SWT.LEFT);
                if (holder !=null && (holder.get_HasIcon() || holder.get_HasImage())) {
                	int width = holder.GetAsImage().get_Width();
                	int hight = holder.GetAsImage().get_Height();
                	m_Image.setImage(SwtImageConverter.ConvertToSwtImage(holder,width,hight));
                }
                m_Image.pack();
                
                showPanel(this.m_Image);
            }
        }
    }

	private void showPanel(Label p) {
		// TODO Auto-generated method stub
		this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
	}
	
}