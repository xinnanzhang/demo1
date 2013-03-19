package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import core.busobmaint.BusObMaintView;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import Core.Dashboards.BusinessObjectPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ImagePartDef;
import Core.Dashboards.PartRefDef;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageHolder;

public class BusinessObjectPartControl extends DashboardPartControl {
	
	private BusinessObjectPartDef partDef;

	public BusinessObjectPartControl(DashboardControl parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public BusinessObjectPartControl(ISiteviewApi iSiteviewApi, DashboardControl parent) {
		super(iSiteviewApi,parent,SWT.NONE);
		
		stacklayout = new FormLayout(); 
		stacklayout.spacing = 10;
		this.get_MainArea().setLayout(stacklayout);
	}

	private FormLayout stacklayout;
	private Label m_Image;
	private Link m_objLink;
	private Label m_lblDesc;
	
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef) {
        super.DefineFromDef(def, partRefDef);
        BusinessObjectPartDef dashboardPartDef = (BusinessObjectPartDef) super.get_DashboardPartDef();
        partDef = dashboardPartDef;
        if (dashboardPartDef != null) {
//        	System.out.println(dashboardPartDef.get_ImageName());
            ImageHolder holder = ImageResolver.get_Resolver().ResolveImage(dashboardPartDef.get_ImageName());
            if (!holder.get_Empty()){
//                System.out.println(holder.GetAsImage());
                m_Image = new Label(this.get_MainArea(),SWT.NONE);
                //m_Image.setLocation(0, 0);
                m_Image.setAlignment(SWT.TOP|SWT.LEFT);
                if (holder !=null && holder.get_HasImage()) {
                	int width = holder.GetAsImage().get_Width();
                	int hight = holder.GetAsImage().get_Height();
                	m_Image.setImage(SwtImageConverter.ConvertToSwtImage(holder,width,hight));
                }else if (holder !=null && holder.get_HasIcon()) {
                	int width = holder.get_Icon().get_Width();
                	int hight = holder.get_Icon().get_Height();
                	m_Image.setImage(SwtImageConverter.ConvertToSwtImage(holder,width,hight));
                }
                m_Image.pack();
            }else{
            	m_Image = new Label(this.get_MainArea(),SWT.NONE);
            }
            String strObjName  = dashboardPartDef.get_BusinessObjectName();
            BusinessObjectDef boDef = super.getApi().get_BusObDefinitions().GetBusinessObjectDef(strObjName);
            if (boDef != null){
            	m_objLink = new Link(this.get_MainArea(),SWT.NONE);
            	m_objLink.setText("<a>" + boDef.get_Alias() + "</a>");
            	m_objLink.pack();
            	
            	m_objLink.addSelectionListener(new SelectionAdapter(){

					@Override
					public void widgetSelected(SelectionEvent e) {
						onLinkClick();
					}});
            }
            m_lblDesc = new Label(this.get_MainArea(),SWT.WRAP);
            m_lblDesc.setText(dashboardPartDef.get_Description());
            m_lblDesc.pack();
            
            String strImagePosition = dashboardPartDef.get_ImagePosition();
            if (strImagePosition.equals(BusinessObjectPartDef.ImagePosition.Left)){
            	FormData fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(0,2);
            	m_Image.setLayoutData(fd);
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(m_Image,0);
            	fd.top = new FormAttachment(0,10);
            	fd.right = new FormAttachment(100,0);
            	m_objLink.setLayoutData(fd);
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(m_Image,0);
            	fd.top = new FormAttachment(m_objLink,2);
            	fd.right = new FormAttachment(100,0);
            	fd.bottom = new FormAttachment(100,0);
            	
            	m_lblDesc.setLayoutData(fd);
            }
            
            
            if (strImagePosition.equals(BusinessObjectPartDef.ImagePosition.Right)){
            	
            	FormData fd = null;
            	
            	fd = new FormData();
            	fd.top = new FormAttachment(0,2);
            	fd.right = new FormAttachment(100,2);
            	m_Image.setLayoutData(fd);
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(0,10);
            	fd.right = new FormAttachment(m_Image,2);
            	m_objLink.setLayoutData(fd);
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(m_objLink,2);
            	fd.right = new FormAttachment(m_Image,0);
            	fd.bottom = new FormAttachment(100,0);
            	
            	m_lblDesc.setLayoutData(fd);
            }
            
            
            if (strImagePosition.equals(BusinessObjectPartDef.ImagePosition.Top)){
            	FormData fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(0,2);
            	m_Image.setLayoutData(fd);
            	
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(m_Image,2);
            	fd.right = new FormAttachment(100,0);
            	m_objLink.setLayoutData(fd);
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(m_objLink,2);
            	fd.right = new FormAttachment(100,0);
            	fd.bottom = new FormAttachment(100,0);
            	
            	m_lblDesc.setLayoutData(fd);
            }
            
            
            if (strImagePosition.equals(BusinessObjectPartDef.ImagePosition.Bottom)){
            	
            	FormData fd = null;
            	
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(0,2);
            	fd.right = new FormAttachment(100,0);
            	m_objLink.setLayoutData(fd);
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(m_objLink,2);
            	fd.right = new FormAttachment(100,0);
            	//fd.bottom = new FormAttachment(100,0);
            	
            	m_lblDesc.setLayoutData(fd);
            	m_lblDesc.pack();
            	
            	fd = new FormData();
            	fd.left = new FormAttachment(0,2);
            	fd.top = new FormAttachment(m_lblDesc,2);
            	m_Image.setLayoutData(fd);
            }
            
        }
    }

	protected void onLinkClick() {
		BusObMaintView.newBusOb(super.getApi(), partDef.get_BusinessObjectName());
		
	}

	
}