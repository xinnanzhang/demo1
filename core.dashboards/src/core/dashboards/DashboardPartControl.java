package core.dashboards;

import java.util.Timer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import siteview.windows.forms.ConvertUtil;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Convert;
import system.DateTime;
import system.EventArgs;
import system.Data.DataTable;
import system.Drawing.Drawing2D.LinearGradientMode;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRangeDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.TitleBarDef;
import Core.Dashboards.ViewDef;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.ColorResolver;
import Siteview.IRaisesAlerts;
import Siteview.StringUtils;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.FieldDef;
import Siteview.Api.GridDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageHolder;
import Siteview.Xml.DashboardPartCategory;
import Siteview.Xml.TimeUnit;
import Siteview.Xml.XmlDashboardPartCategory;

public class DashboardPartControl extends Composite implements IRaisesAlerts{
	private ISiteviewApi m_iSiteviewApi;
	private boolean m_bFirstTimeToLoad = true;
	private DashboardPartDef m_DashboardPartDef;
	private PartRefDef m_PartRefDef;

	private Composite m_pbSelectionArea;
	private Timer m_AutoRefreshTimer;
	private TitleDisplayControl m_TitleBar;
	public Object m_DataLoadingSync = new java.lang.Object();
	
	private DataTable m_parentDataTable;
	
	private DashboardControl m_Parent;
	private GradientPictureBox m_pbMainArea;
	private Boolean m_bTrapErrors = false;
	
	private ViewControl m_DateRangeControl;
	private ViewDef m_SelectedViewDef;
	private Boolean m_bSkipDrillDown = false;
	private Boolean m_bCanDrillBack = false;
	
	private IPartLoader m_PartLoader;
	
	private SelectionAdapter drillbackListener;
	
	private MouseAdapter titleDoubleListener;
	
	private SelectionAdapter popupListener;

	private SelectionAdapter refreshListener;
	
	private ViewControl m_ViewControl;
	
	private CustomDateRangeControl m_CustomDateRangeControl;
	
	private ViewDef m_SelectedDateRangeDef;
	
	public DashboardPartControl(DashboardControl parent, int style) {
		super(parent.get_MainArea(), style|SWT.BORDER);
		m_Parent = parent;
		m_bTrapErrors = true;
		m_bFirstTimeToLoad = true;
		initControl(parent);
	}
	
	public DashboardPartControl(Composite parent, int style) {
		super(parent, style);
		m_Parent = null;
		m_bTrapErrors = true;
		m_bFirstTimeToLoad = true;
		initControl(parent);
	}
	
	private void initControl(Composite parent){
		this.setLayout(new FormLayout());

		m_TitleBar = new TitleDisplayControl(this,SWT.NONE);
		FormData fd_titlebar = new FormData();
		fd_titlebar.left = new FormAttachment(0, 0);
		fd_titlebar.top = new FormAttachment(0, 0);
		fd_titlebar.right = new FormAttachment(100, 0);
		fd_titlebar.bottom = new FormAttachment(0, 28);
		m_TitleBar.setLayoutData(fd_titlebar);
		
		titleDoubleListener = new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				System.out.println("弹出窗口");
				OnPopupShowButtonClick(null, new EventArgs());
			}
		};
		
		m_pbMainArea = new GradientPictureBox(this,SWT.NONE);
		FormData fd_mainarea = new FormData();
		fd_mainarea.left = new FormAttachment(0, 0);
		fd_mainarea.top = new FormAttachment(m_TitleBar, 0);
		fd_mainarea.right = new FormAttachment(100, 0);
		fd_mainarea.bottom = new FormAttachment(100, 0);
		m_pbMainArea.setLayoutData(fd_mainarea);
		
		m_pbMainArea.setLayout(new FillLayout());
		
		m_AutoRefreshTimer = new Timer();
		
		drillbackListener = new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				DrillBack();
				
			}

			};
			
		popupListener = new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("弹出窗口");
				
				OnPopupShowButtonClick(null, new EventArgs());
			}

			};
	
		refreshListener = new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				Refresh();
			}
		};
			
		
		m_pbMainArea.addDisposeListener(new DisposeListener(){

			@Override
			public void widgetDisposed(DisposeEvent event) {
				if (m_AutoRefreshTimer!=null){
					m_AutoRefreshTimer.cancel();
				}
				
			}});
		
	}
	
	public DashboardPartControl(ISiteviewApi iSiteviewApi,Composite parent, int style) {
		this(parent, style);
		m_iSiteviewApi = iSiteviewApi;
	}
	
	public DashboardPartControl(ISiteviewApi iSiteviewApi,DashboardControl parent, int style) {
		this(parent, style);
		m_iSiteviewApi = iSiteviewApi;
	}

    public static DashboardPartControl Create(ISiteviewApi iSiteviewApi, DashboardPartCategory category, DashboardControl parent)
    {
        DashboardPartControl control = null;
        switch (category)
        {
            case Grid:
                return new GridPartControl(iSiteviewApi, parent, true);

            case MultiViewGrid:
                return new MultiViewGridPartControl(iSiteviewApi, parent, true);

            case MultiQueryGrid:
                return new MultiQueryGridPartControl(iSiteviewApi, parent);

            case OutlookInbox:
                return new OutlookInboxPartControl(iSiteviewApi, parent);

            case OutlookCalendar:
                return new OutlookCalendarPartControl(iSiteviewApi, parent);

            case Image:
                return new ImagePartControl(iSiteviewApi, parent);

            case WebBrowser:
                return new WebBrowserPartControl(iSiteviewApi, parent);

            case LinkList:
                return new LinkListPartControl(iSiteviewApi, parent);

            case PartRef:
                return control;

            case ChartFunnel:
                return new ChartFunnelPartControl(iSiteviewApi, parent);

            case ChartPipeline:
                return new ChartPipelinePartControl(iSiteviewApi, parent);

            case Speedometer:
                return new GaugeSpeedometerPartControl(iSiteviewApi, parent);

            case GaugeLinear:
                return new GaugeLinearPartControl(iSiteviewApi, parent);

            case ChartPie:
                return new ChartPiePartControl(iSiteviewApi, parent);

            case ChartLine:
                return new ChartLinePartControl(iSiteviewApi, parent);

            case ChartBar:
                return new ChartBarPartControl(iSiteviewApi, parent);

            case ChartColumn:
                return new ChartColumnPartControl(iSiteviewApi, parent);

            case TabbedPart:
                return new TabbedPartControl(iSiteviewApi, parent);

            case MSProject:
                return new MSProjectPartControl(iSiteviewApi, parent);

            case ObjectBrowser:
                return new ObjectBrowserPartControl(iSiteviewApi, parent);

            case BusinessObject:
            	return new BusinessObjectPartControl(iSiteviewApi, parent);
            	
            case Custom:
            	return new CustomPartControl(iSiteviewApi, parent);
            case ObjectTopologicalDiagram:
                return new ObjectTopologicalDiagramPartControl(iSiteviewApi, parent);
        }
        return control;
    }
    
    public static DashboardPartControl CreateFromDef(ISiteviewApi iSiteviewApi, DashboardControl parent, Core.Dashboards.DashboardPartDef def, Core.Dashboards.PartRefDef partRefDef)
    {
        DashboardPartControl control = null;
//        if (XmlDashboardPartCategory.ToCategory(def.get_CategoryAsString()) == DashboardPartCategory.ObjectTopologicalDiagram)
//        {
//            control = new ObjectTopologicalDiagramPartControl(iSiteviewApi, def, parent);
//        }
//        else
        {
            control = Create(iSiteviewApi, XmlDashboardPartCategory.ToCategory(def.get_CategoryAsString()), parent);
        }
        if (control != null)
        {
            control.DefineFromDef(def, partRefDef);
            control.LoadData();
        }
        return control;
    }
    
    protected void DefineFromDef(Core.Dashboards.DashboardPartDef def, Core.Dashboards.PartRefDef partRefDef)
    {
        this.m_bFirstTimeToLoad = true;
        this.m_DashboardPartDef = def;
        this.m_PartRefDef = partRefDef;
        if (this.m_DashboardPartDef != null)
        {
            this.SetRefresh();
            this.SetTitleBar();
            this.SetBackground();
            if (this.m_TitleBar!=null){
            //if (m_DrillBackButton==null)
            //	return;
            	this.m_TitleBar.removeMouseListener(this.titleDoubleListener);
            	this.m_TitleBar.addMouseListener(this.titleDoubleListener);
	            if (this.IsDrillBackChart(XmlDashboardPartCategory.ToCategory(def.get_CategoryAsString())))
	            {
	                this.m_TitleBar.get_DrillBackButton().setVisible(true);
	                this.m_TitleBar.get_DrillBackButton().removeSelectionListener(drillbackListener);
	                this.m_TitleBar.get_DrillBackButton().addSelectionListener(drillbackListener);
	            }
	            else
	            {
	                this.m_TitleBar.get_DrillBackButton().setVisible(false);
	            }
	            this.m_TitleBar.get_PopupButton().removeSelectionListener(popupListener);
                this.m_TitleBar.get_PopupButton().addSelectionListener(popupListener);
                
	            this.m_TitleBar.get_RefreshButton().setVisible(def.get_ShowRefreshButton());
	            
	            this.m_TitleBar.get_RefreshButton().removeSelectionListener(refreshListener);
	            this.m_TitleBar.get_RefreshButton().addSelectionListener(refreshListener);
            }
            
            if (!this.m_DashboardPartDef.get_SupportViewControl() && !this.m_DashboardPartDef.get_SupportDateRangeControl())
            {
                if (this.m_pbSelectionArea != null)
                {
                    this.m_pbSelectionArea.dispose();
                }
            }
            else if (!this.m_DashboardPartDef.get_ViewControlVisible() && !this.m_DashboardPartDef.get_DateRangeControlVisible())
            {
                if (this.m_pbSelectionArea != null)
                {
                    this.m_pbSelectionArea.dispose();
                }
            }
            else
            {
            	//增加条件下拉窗口
            	this.m_pbSelectionArea = new Composite(this,SWT.NONE);
        		FormData fd_sa = new FormData();
        		fd_sa.top = new FormAttachment(m_TitleBar,0,SWT.BOTTOM);
        		m_pbSelectionArea.setLayoutData(fd_sa);
        		m_pbSelectionArea.setLayout(new GridLayout(3, false));
        		
        		//创建条件下拉
            	this.CreateViewControl();
                this.CreateDateRangeControl();
                
                //调整主显示区位置 
                FormData fd_mainarea = (FormData) m_pbMainArea.getLayoutData();
        		fd_mainarea.top = new FormAttachment(m_pbSelectionArea, 0,SWT.BOTTOM);
        		m_pbMainArea.setLayoutData(fd_mainarea);
            }
        }
    }
    
    public void LoadData(){
    	this.DataBind(this.GetData(null));
    }
    
    public  void DataBind(java.lang.Object dt)
    {
        this.ClearAlert();
        this.UpdateContextMenu();
    }
    
    protected  void UpdateContextMenu()
    {
        if((this.get_DashboardPartDef() != null) && !this.get_DashboardPartDef().get_ShowRefreshButton()){
            //this.m_RefreshMenuItem.set_Enabled(false);
        }

    }
    
    protected  void ClearAlert()
    {

    }
    
    public  java.lang.Object GetData(ILoadingStatusSink sink)
    {
        this.m_bFirstTimeToLoad = false;

        return null;

    }
    
    private void CreateDateRangeControl() {
		// TODO Auto-generated method stub CreateDateRangeControl
    	/*add by zhangfan start*/
    	if (this.m_DashboardPartDef.get_SupportDateRangeControl()){
            this.m_DateRangeControl = new ViewControl(this.m_pbSelectionArea, SWT.NONE);
            this.m_DateRangeControl.setSize(200,30);
            this.m_DateRangeControl.setVisible(this.m_DashboardPartDef.get_DateRangeControlVisible());
            this.m_DateRangeControl.get_Label().setText(Res.get_Default().GetString("DashboardPartControl.DateRangeDropDownLabel"));
            this.m_DateRangeControl.get_Label().setSize(this.m_DateRangeControl.get_Label().getText().length()*15, this.m_DateRangeControl.get_Label().getSize().y);
            this.m_DateRangeControl.setSize(this.m_DateRangeControl.get_Label().getSize().x + this.m_DateRangeControl.get_Combo().getSize().x, 0x24);
            this.m_DateRangeControl.get_Combo().addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				DateRangeChangeCommitted(m_DateRangeControl,new EventArgs());
				}});
            this.AdjustDateRangeControlLocation();
            this.m_CustomDateRangeControl = new CustomDateRangeControl(this.m_pbSelectionArea,SWT.None);
            this.m_CustomDateRangeControl.setSize(300,30);
            this.m_CustomDateRangeControl.setVisible(false);
            this.m_CustomDateRangeControl.get_btnGo().addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				CustomDateRangeChangeCommitted(m_CustomDateRangeControl,new EventArgs());
    			}});
            this.AdjustCustomDateRangeControlLocation();
        }
    	/*add by zhangfan end*/
	}

	private void CreateViewControl() {
		// TODO Auto-generated method stub CreateViewControl
		/*add by zhangfan start*/
		if (this.m_DashboardPartDef.get_SupportViewControl()){
            this.m_ViewControl = new ViewControl(this.m_pbSelectionArea, SWT.NONE);
            this.m_ViewControl.setLocation(new Point(2, 1));
            this.m_ViewControl.setVisible(this.m_DashboardPartDef.get_ViewControlVisible());
            this.m_ViewControl.get_Label().setText(Res.get_Default().GetString("DashboardPartControl.ViewDropDownLabel"));
            this.m_ViewControl.get_Label().setSize(this.m_ViewControl.get_Label().getText().length()*15,this.m_ViewControl.get_Label().getSize().y);
            this.m_ViewControl.setSize(this.m_ViewControl.get_Label().getSize().x + this.m_ViewControl.get_Combo().getSize().x,this.m_ViewControl.get_Label().getSize().y);
            this.m_ViewControl.get_Combo().addSelectionListener(new SelectionAdapter() {
    			@Override
    			public void widgetSelected(SelectionEvent e) {
    				ViewChangeCommitted();
    			}
    		});
        }
		/*add by zhangfan end*/
	}

	protected void SetRefresh()
    {
        if ((this.m_DashboardPartDef != null) && (this.m_DashboardPartDef.get_RefreshFrequency() > 0) && (this.m_DashboardPartDef.get_RefreshFrequencyTimeUnit() != TimeUnit.None))
        {
        	if (m_AutoRefreshTimer == null) m_AutoRefreshTimer = new Timer();
        	
            int Interval = Convert.ToInt32(this.m_DashboardPartDef.get_RefreshFrequencyTimeSpan().get_TotalMilliseconds());
            this.m_AutoRefreshTimer.schedule(new RefreshTask(this,system.Threading.Thread.get_CurrentPrincipal()), Interval, Interval);
        }
    }
    
    protected void SetTitleBar()
    {
        TitleBarDef titleBarDef = this.m_DashboardPartDef.get_TitleBarDef();
        if (!titleBarDef.get_Visible())
        {
            if (this.m_TitleBar != null)
            {
                this.m_TitleBar.dispose();
                this.m_TitleBar = null;
            }
        }
        else
        {
        	if (this.m_TitleBar != null){
	            this.m_TitleBar.set_Title( " " + this.m_DashboardPartDef.get_TitleBarDef().get_Text());
	            this.m_TitleBar.set_TitleAlign(this.m_DashboardPartDef.get_TitleBarDef().get_TextAlign());
	            ImageHolder holder = ImageResolver.get_Resolver().ResolveImage(this.m_DashboardPartDef.get_TitleBarDef().get_ImageName());
	            if (!holder.get_Empty())
	            {
	                this.m_TitleBar.set_Image(SwtImageConverter.ConvertToSwtImage(holder));
	            }
	            this.m_TitleBar.set_ImageAlign(this.m_DashboardPartDef.get_TitleBarDef().get_ImageAlign());
	            //Font font = new Font(Display.getCurrent(),"simsun", 12, SWT.NORMAL);
	            //this.m_TitleBar.set_TitleFont(font);
	            if (titleBarDef.get_UseParentColor())
	            {
	            	if(this.m_Parent!=null){
		                if (this.m_Parent.get_DashboardDef().get_TitleBarDef().get_UseParentColor())
		                {
		                    this.SetTitleColorFromAppTitle(this.m_TitleBar);
		                }
		                else
		                {
		                    this.SetTitleColorFromDashboard(this.m_TitleBar);
		                }
	            	}
	            }
	            else
	            {
	                this.SetTitleColorFromDashboardPartDef(titleBarDef, this.m_TitleBar);
	            }
	            if (this.m_PartRefDef.get_ShowLinkIcon())
	            {
	                this.m_TitleBar.set_ShowLinkIcon(true);
	            }
	            else
	            {
	                this.m_TitleBar.set_ShowLinkIcon(false);
	            }
	            m_TitleBar.redraw();
        	}
        }
    }
    
    private void SetBackground()
    {
        if (!this.m_DashboardPartDef.get_BackgroundDef().get_ImageName().equals(""))
        {
            ImageHolder holder = ImageResolver.get_Resolver().ResolveImage(this.m_DashboardPartDef.get_BackgroundDef().get_ImageName());

            switch (this.m_DashboardPartDef.get_BackgroundDef().get_ImagePosition())
            {
                case Center:
                    this.setBackgroundImage(SwtImageConverter.ConvertToSwtImage(holder));
                    break;

                case Stretch:
                	this.setBackgroundImage(SwtImageConverter.ConvertToSwtImage(holder,this.getBounds().width,this.getBounds().height));
                    break;

                case Tile:
                	this.setBackgroundImage(SwtImageConverter.ConvertToSwtImage(holder));
                    break;
            }
        }
        this.m_pbMainArea.set_IsSolidColor(this.m_DashboardPartDef.get_BackgroundDef().get_IsSolidColor());
        this.m_pbMainArea.set_StartColor(ColorResolver.get_Resolver().ResolveColor(this.m_DashboardPartDef.get_BackgroundDef().get_FirstColor()));
        if (!this.m_DashboardPartDef.get_BackgroundDef().get_IsSolidColor())
        {
            this.m_pbMainArea.set_EndColor(ColorResolver.get_Resolver().ResolveColor(this.m_DashboardPartDef.get_BackgroundDef().get_SecondColor()));
            if (this.m_DashboardPartDef.get_BackgroundDef().get_IsLeftToRightGradient())
            {
                this.m_pbMainArea.set_LinearGradientMode(LinearGradientMode.Horizontal);
            }
            else
            {
                this.m_pbMainArea.set_LinearGradientMode(LinearGradientMode.Vertical);
            }
        }
    }
    
    private void SetTitleColorFromDashboardPartDef(TitleBarDef titleBarDef,
			TitleDisplayControl titleBar) {
    	titleBar.set_TitleForeColor(ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(titleBarDef.get_TextForeColor())));
        titleBar.set_IsSolidColor(titleBarDef.get_BackgroundDef().get_IsSolidColor());
        titleBar.set_StartColor(ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(titleBarDef.get_BackgroundDef().get_FirstColor())));
        if (!titleBar.get_IsSolidColor())
        {
            titleBar.set_EndColor(ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(titleBarDef.get_BackgroundDef().get_SecondColor())));
            if (titleBarDef.get_BackgroundDef().get_IsLeftToRightGradient())
            {
            	titleBar.set_LinearGradientMode(LinearGradientMode.Horizontal);
            }
            else
            {
            	titleBar.set_LinearGradientMode(LinearGradientMode.Vertical);
            }
        }
		
	}

	private void SetTitleColorFromDashboard(TitleDisplayControl titleBar) {
		java.awt.Color foreClr = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_Parent.get_DashboardDef().get_TitleBarDef().get_TextForeColor()));
		titleBar.set_TitleForeColor(foreClr);
        titleBar.set_IsSolidColor(this.m_Parent.get_DashboardDef().get_TitleBarDef().get_BackgroundDef().get_IsSolidColor());
        titleBar.set_StartColor(ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_Parent.get_DashboardDef().get_TitleBarDef().get_BackgroundDef().get_FirstColor())));
        if (!titleBar.get_IsSolidColor())
        {
            titleBar.set_EndColor( ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_Parent.get_DashboardDef().get_TitleBarDef().get_BackgroundDef().get_SecondColor())));
            if (this.m_Parent.get_DashboardDef().get_TitleBarDef().get_BackgroundDef().get_IsLeftToRightGradient())
            {
                titleBar.set_LinearGradientMode(LinearGradientMode.Horizontal);
            }
            else
            {
                titleBar.set_LinearGradientMode(LinearGradientMode.Vertical);
            }
        }
		
	}

	private void SetTitleColorFromAppTitle(TitleDisplayControl titleBar) {
		
		
        titleBar.set_TitleForeColor(java.awt.Color.GRAY);
        titleBar.set_StartColor(java.awt.Color.BLACK);
        titleBar.set_EndColor(new java.awt.Color(0,122,222));
	}

	static class RefreshTask extends java.util.TimerTask{
		
		private DashboardPartControl part;
		private system.Security.Principal.IPrincipal pal;
		private Display disp;
		
		public RefreshTask(DashboardPartControl pc,system.Security.Principal.IPrincipal pri){
			part = pc;
			pal = pri;
			disp = Display.getCurrent();
		}

		@Override
		public void run() {
			disp.syncExec(new Runnable(){

				@Override
				public void run() {
					if (part!=null){
						system.Threading.Thread.set_CurrentPrincipal(pal);
						part.Refresh();
					}
					
				}});
			
		}

    }
	
    public  void DrillBack()
    {
        

    }
    
    private boolean IsDrillBackChart(DashboardPartCategory category)
    {
        boolean flag = false;
        switch (category)
        {
            case LinkList:
            case ChartFunnel:
            case ChartPipeline:
            case ChartPie:
            case ChartLine:
            case ChartBar:
            case ChartColumn:
            case TabbedPart:
                return true;

            case PartRef:
            case Speedometer:
            case GaugeLinear:
                return flag;
        }
        return flag;
    }
    
    protected ISiteviewApi getApi(){
    	return this.m_iSiteviewApi;
    }

	@Override
	public Boolean get_TrapErrors() {
		return m_bTrapErrors;
	}

	@Override
	public void set_TrapErrors(Boolean value) {
		m_bTrapErrors = value;
		
	}
	
	public DashboardPartDef get_DashboardPartDef() {
		return m_DashboardPartDef;
	}
	
	public GradientPictureBox get_MainArea(){
		return this.m_pbMainArea;
	}

	public void ResizeControl(boolean bChangeHeight, boolean bChangeWidth) {
		if (this.m_PartRefDef != null){
			//this.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true, this.m_PartRefDef.get_EndColumn()- this.m_PartRefDef.get_StartColumn()+1,this.m_PartRefDef.get_EndRow()-this.m_PartRefDef.get_StartRow()+1));
			Rectangle rectangle = this.m_Parent.GetPartRectangleFromIndex(this.m_PartRefDef.get_StartRow(), this.m_PartRefDef.get_EndRow(), this.m_PartRefDef.get_StartColumn(), this.m_PartRefDef.get_EndColumn());
			this.setBounds(rectangle);
		}
		
	}
	
	protected TitleDisplayControl get_TitleBar(){
		return this.m_TitleBar;
	}
	
	protected int getStartColumn(){
		if (m_PartRefDef!=null){
			return this.m_PartRefDef.get_StartColumn();
		}else
			return 0;
	}
	
	protected int getStartRow(){
		if (m_PartRefDef!=null){
			return this.m_PartRefDef.get_StartRow();
		}else
			return 0;
	}
	
   public Siteview.Api.BusinessObject get_DependBusObj(){

        return null;
    }
    public void set_DependBusObj(Siteview.Api.BusinessObject value){
    }

    public GridDef get_DependGridDef(){

        return null;
    }
    public void set_DependGridDef(GridDef value){
    }

    public Boolean get_DependMode(){

        return false;
    }
    public void set_DependMode(Boolean value){
    }

    public Core.Dashboards.PartRefDef get_DependPartRef(){

        return null;
    }
    public void set_DependPartRef(Core.Dashboards.PartRefDef value){
    }
    
    
    public Boolean get_DateRangeControlVisible(){
        Boolean visible = false;
        if(this.m_DateRangeControl != null){
            visible = this.m_DateRangeControl.getVisible();
        }

        return visible;
    }
    public void set_DateRangeControlVisible(Boolean value){
        if(this.m_DateRangeControl != null){
            this.m_DateRangeControl.setVisible(value);
        }
    }
    
    protected boolean get_SkipDrillDown(){

        return this.m_bSkipDrillDown;
    }
    protected void set_SkipDrillDown(boolean value){
        this.m_bSkipDrillDown = value;
    }
    
    public DashboardControl get_Parent(){

        return this.m_Parent;
    }
    
    public void set_Parent(DashboardControl parent){
    	this.m_Parent = parent;
    }
    
    protected  void OnAlertRaised(java.lang.Object sender, AlertRaisedEventArgs e)
    {
//	        if(this.AlertRaised != null){
//	            if(this.get_MainArea().get_InvokeRequired()){
//	                this.get_MainArea().Invoke(new OnAlertRaisedHandler(this.OnAlertRaised),new java.lang.Object[]{sender,e});
//	            }
//	            else{
//	                this.AlertRaised(sender, e);
//	            }
//	        }

    }
    
    protected  Boolean CheckGridRowColor(BusinessObjectDef bDef, /*TODO: OUT PARAMETER, Wrapper it to array[0] */FieldDef[] fDef)
    {
        Boolean flag = false;
        
        fDef[0] = bDef.GetAnnotatedField("GridRowColor");
        if(fDef[0] != null){
            flag = true;
        }

        return flag;

    }
	    
    protected  void ShowNoRightsAlert(String strBusObName)
    {
        String strMessage = StringUtils.SetToken(Res.get_Default().GetString("DashboardPartControl.NoRights"), "BUSINESSOBJECTNAME", strBusObName);
//        if(this.get_MainArea().get_InvokeRequired()){
//            this.get_MainArea().Invoke(new ShowErrorMessageHandler(this.ShowNoRightsAlert),new java.lang.Object[]{strBusObName});
//        }
//        else{
            this.OnAlertRaised(this,new AlertRaisedEventArgs(strMessage, AlertType.Informational, null));
//        }

    }
    
    protected  void AddCustomDateRange()
    {
        this.get_DashboardPartDef().set_EditMode(true);
        ViewDef viewDef = new ViewDef(this.get_DashboardPartDef());
        viewDef.set_IsCustom(true);
        this.get_DashboardPartDef().set_EditMode(false);
        this.AddDateTimeRange(viewDef);

    }

    protected  void AddDateTimeRange(ViewDef viewDef)
    {
        if((this.m_DateRangeControl != null) && (viewDef != null)){
        	this.m_DateRangeControl.get_Combo().add(viewDef.toString());
//            this.m_DateRangeControl.get_Combo().add(viewDef.get_Alias());
            this.m_DateRangeControl.get_Combo().setData(viewDef.toString(), viewDef);
//            this.m_DateRangeControl.get_Combo().setData(viewDef.get_Alias(), viewDef);
            this.SetViewControlWidth(this.m_DateRangeControl);
            this.AdjustDateRangeControlLocation();
        }

    }
    
    private  void AdjustDateRangeControlLocation()
    {
        if(this.m_DateRangeControl != null){
            Integer num = 0;
            if(this.m_DashboardPartDef.get_ViewControlVisible()){
                num = this.m_ViewControl.get_Width() + 2;
            }
            else{
                num = 2;
            }
            this.m_DateRangeControl.setLocation(new Point(num + 4, 1));
            this.AdjustSelectionAreaWidth(this.m_DateRangeControl.get_Right());
            this.AdjustCustomDateRangeControlLocation();
        }

    }
    
    private  void AdjustCustomDateRangeControlLocation()
    {
        if(this.m_CustomDateRangeControl != null){
            this.m_CustomDateRangeControl.setLocation(new Point((this.m_DateRangeControl.get_Left() + this.m_DateRangeControl.get_Width()) + 4, this.m_DateRangeControl.get_Top()));
        }

    }
    
    protected  void AddFiscalDateRange(Integer fiscalYear, Integer fiscalQuarter)
    {
        this.get_DashboardPartDef().set_EditMode(true);
        ViewDef viewDef = new ViewDef(this.get_DashboardPartDef());
        viewDef.set_FiscalYear(fiscalYear);
        viewDef.set_FiscalQuarter(fiscalQuarter);
        viewDef.set_IsFiscalPeriod(true);
        this.get_DashboardPartDef().set_EditMode(false);
        this.AddDateTimeRange(viewDef);

    }
    
    
    protected  void SetSelectedDateRange(DateRangeDef dateRangeDef, String selectedDateRange, String strStartDateTime, String strEndDateTime)
    {
        if((this.m_DateRangeControl != null) && (dateRangeDef != null)){

            for(int i = 0; i <this.m_DateRangeControl.get_Combo().getItemCount(); i++){
            	String str = this.m_DateRangeControl.get_Combo().getItem(i);
                ViewDef def = (ViewDef)this.m_DateRangeControl.get_Combo().getData(str);

                if(!selectedDateRange.equals(Res.get_Default().GetString("ViewDef.Custom"))){

                    if(!def.toString().equals(selectedDateRange)){
                        continue;
                    }
                    this.m_DateRangeControl.get_Combo().select(i);
                    this.m_SelectedDateRangeDef = def;
                }
                else{
                    if(!def.get_IsCustom()){
                        continue;
                    }
                    this.m_DateRangeControl.get_Combo().select(i);
                    this.m_SelectedDateRangeDef = def;
                    if(strStartDateTime.equals("") || strEndDateTime.equals("")){

                        if(strStartDateTime.equals("")){
                            this.m_CustomDateRangeControl.set_StartDateTime(DateTime.get_Today());
                        }

                        if(strEndDateTime.equals("")){
                            this.m_CustomDateRangeControl.set_EndDateTime(DateTime.get_Today());
                        }
                    }
                    else{
                        this.m_CustomDateRangeControl.set_StartDateTime(Convert.ToDateTime(strStartDateTime));
                        this.m_CustomDateRangeControl.set_EndDateTime(Convert.ToDateTime(strEndDateTime));
                    }
                    this.DateRangeChangeCommitted(this.m_CustomDateRangeControl,new EventArgs());
                }
                break;
            }
            if(this.m_SelectedDateRangeDef == null){
                this.m_DateRangeControl.get_Combo().select(0);
                this.m_SelectedDateRangeDef = (ViewDef)this.m_DateRangeControl.get_Combo().getData(m_DateRangeControl.get_Combo().getItem(0));
                this.SetGridSelectedDateRange(this.m_SelectedDateRangeDef.toString());
            }
        }

    }
    
    protected  void SetGridSelectedDateRange(String strDateRange)
    {
        

    }
    
    private  void DateRangeChangeCommitted(java.lang.Object sender, EventArgs e)
    {
    	String str = this.m_DateRangeControl.get_Combo().getItem(this.m_DateRangeControl.get_Combo().getSelectionIndex());
        this.m_SelectedDateRangeDef = (ViewDef)this.m_DateRangeControl.get_Combo().getData(str);
        if(this.m_SelectedDateRangeDef != null){
            if(this.m_SelectedDateRangeDef.get_IsCustom()){
                this.m_CustomDateRangeControl.setVisible(true);
                this.CustomDateRangeChangeCommitted(sender,e);
            }
            else{
                this.m_CustomDateRangeControl.setVisible(false);
                this.ReQuery(this.m_SelectedViewDef,this.m_SelectedDateRangeDef);
            }
        }
    }
    
    protected  void SetDefaultDateRange(DateRangeDef dateRangeDef)
    {
        if((this.m_DateRangeControl != null) && (dateRangeDef != null)){
            for(int i = 0 ; i < this.m_DateRangeControl.get_Combo().getItemCount(); i++){
            	
            	String str = this.m_DateRangeControl.get_Combo().getItem(i);
                ViewDef def = (ViewDef)this.m_DateRangeControl.get_Combo().getData(str);
                if(dateRangeDef.get_IsLengthOfTime()){
                    if(def.get_DateRange() != dateRangeDef.get_DateRange()){
                        continue;
                    }
                    this.m_DateRangeControl.get_Combo().select(i);
                    this.m_SelectedDateRangeDef = def;
                }
                else{
                    if(!def.get_IsCustom()){
                        continue;
                    }
                    this.m_DateRangeControl.get_Combo().select(i);
                    this.m_SelectedDateRangeDef = def;
                    if(dateRangeDef.get_IsAmountOfTime()){
                        this.m_CustomDateRangeControl.set_StartDateTime(DateRangeCriteriaCreator.GetStartDateTimeForAmountOfTimeDateRange(dateRangeDef));
                        this.m_CustomDateRangeControl.set_EndDateTime(DateRangeCriteriaCreator.GetEndDateTimeForAmountOfTimeDateRange(dateRangeDef));
                    }
                    else{
                        this.m_CustomDateRangeControl.set_StartDateTime(DateRangeCriteriaCreator.GetStartDateTimeForSpecificDateRange(this.getApi(),dateRangeDef));
                        this.m_CustomDateRangeControl.set_EndDateTime(DateRangeCriteriaCreator.GetEndDateTimeForSpecificDateRange(this.getApi(),dateRangeDef));
                    }
                    this.DateRangeChangeCommitted(this.m_CustomDateRangeControl,new EventArgs());
                }

                return ;
            }
        }

    }
    
    private  void CustomDateRangeChangeCommitted(java.lang.Object sender, EventArgs e)
    {
        this.get_DashboardPartDef().set_EditMode(true);
        ViewDef def = new ViewDef(this.get_DashboardPartDef());
        def.set_IsCustom(true);
        def.set_StartDateTime(this.m_CustomDateRangeControl.get_StartDateTime().get_Date());
        def.set_EndDateTime(this.m_CustomDateRangeControl.get_EndDateTime().get_Date());
        this.get_DashboardPartDef().set_EditMode(false);
        this.m_SelectedDateRangeDef = def;
        this.ReQuery(this.m_SelectedViewDef,this.m_SelectedDateRangeDef);

    }
    
    protected  void ReQuery(ViewDef vViewDef, ViewDef vDateTimeDef)
    {
        

    }
    
    private  void AdjustSelectionAreaWidth(Integer nRight)
    {
//        if(nRight > this.m_pbSelectionArea.get_Right()){
//            Integer width = this.m_pbSelectionArea.get_Width() + (nRight - this.m_pbSelectionArea.get_Right());
//            if(this.m_CustomDateRangeControl.get_Visible()){
//                super.set_AutoScrollMinSize(new Size(width, 0));
//            }
//        }

    }
    
    private  void SetViewControlWidth(ViewControl viewControl)
    {
        if(viewControl != null){
        	/*add by zhangfan*/
        	if (viewControl != null){
                String strText = "";
                for (String def : viewControl.get_Combo().getItems()){
                    if (strText.length() < def.length()){
                        strText = def;
                    }
                }
                viewControl.setSize(100,50);
                this.AdjustSelectionAreaWidth(viewControl.get_Right());
            }        	
        	/**/
        	
        	viewControl.pack();
            //this.AdjustSelectionAreaWidth(viewControl.get_Right());
        }
        
    }
    
    public DataTable get_ParentDataTable(){

        return this.m_parentDataTable;
    }
    
    public void set_ParentDataTable(DataTable value){

        if((this.m_parentDataTable != null) && ((value == null) || !(value.hashCode()== this.m_parentDataTable.hashCode()))){
            this.m_parentDataTable.Dispose();
        }
        this.m_parentDataTable = value;
    }
    
    protected  DataTable GetChildTable(Integer row, Integer column, DataTable dataSource)
    {

        return null;

    }
    
    public  void DoDataRefresh()
    {
    	if (m_AutoRefreshTimer !=null){
	        this.m_AutoRefreshTimer.cancel();
	        this.m_AutoRefreshTimer = null;
    	}
        this.ClearAlert();
        if(this.get_PartLoader() != null){
            this.get_PartLoader().RefreshData();
        }
        else{
            this.LoadData();
        }
        this.SetRefresh();

    }
    
    public IPartLoader get_PartLoader(){

        return this.m_PartLoader;
    }
    
    public void set_PartLoader(IPartLoader value){
        this.m_PartLoader = value;
    }
    
    public void removeTitleBar(){
    	this.m_TitleBar.dispose();
    	this.m_TitleBar = null;
    }
    
    public Boolean get_CanDrillBack(){

        return this.m_bCanDrillBack;
    }
    public void set_CanDrillBack(Boolean value){
        this.m_bCanDrillBack = value;
    }
    
    protected void SetSelectionArea()
    {
        Boolean visible = false;
        if(this.m_ViewControl != null){
            visible = this.m_ViewControl.getVisible();
        }
        if(!visible && (this.m_DateRangeControl != null)){
            visible = this.m_DateRangeControl.getVisible();
        }
        if(this.get_SelectionArea()!=null && !this.get_SelectionArea().isDisposed()){
        	this.get_SelectionArea().setVisible(visible);
        }
    }

	private Composite get_SelectionArea() {
		return this.m_pbSelectionArea;
	}

	public PartRefDef get_PartRefDef() {
		return m_PartRefDef;
	}

	public void set_PartRefDef(PartRefDef m_PartRefDef) {
		this.m_PartRefDef = m_PartRefDef;
	}
	
	//add by zhangfan Start
	//弹出按钮事件
	private void OnPopupShowButtonClick(Object sender, EventArgs e){
		if(this.m_Parent!=null){
			new DashboardPartPopup(this.getShell(),SWT.None,this.getApi(), this.m_Parent, this.m_PartRefDef).open();
		}
    }
	
	//显示下拉 切换事件
	private void ViewChangeCommitted(){
    	int i = this.m_ViewControl.get_Combo().getSelectionIndex();
    	String str = this.m_ViewControl.get_Combo().getItem(i);
        this.m_SelectedViewDef = (ViewDef)this.m_ViewControl.get_Combo().getData(str);
        if (this.m_SelectedViewDef != null){
            this.ReQuery(this.m_SelectedViewDef, this.m_SelectedDateRangeDef);
        }
    }
	
	//增加显示下拉内容
	protected ViewDef AddView(ViewDef viewDef, String selectedView){
        if ((this.m_ViewControl != null) && (viewDef != null)){
        	this.m_ViewControl.get_Combo().add(viewDef.get_Alias());
        	this.m_ViewControl.get_Combo().setData(viewDef.get_Alias(), viewDef);
            this.SetViewControlWidth(this.m_ViewControl);
            if (selectedView.equals("")){
                if (viewDef.get_IsDefault()){
                	int index = get_i(this.m_ViewControl,viewDef.get_Alias());
                    this.m_ViewControl.get_Combo().select(index);
                    this.m_SelectedViewDef = viewDef;
                }
            }else if (viewDef.get_Alias().equals(selectedView)){
            	int index = get_i(this.m_ViewControl,viewDef.get_Alias());
            	this.m_ViewControl.get_Combo().select(index);
                this.m_SelectedViewDef = viewDef;
            }
            this.AdjustDateRangeControlLocation();
        }
        return this.m_SelectedViewDef;
    }
	
	//设置默认选中项
	protected ViewDef SetDefaultView(ViewDef viewDef){
		if (viewDef.get_IsDefault()){
			int index = get_i(this.m_ViewControl,viewDef.get_Alias());
            this.m_ViewControl.get_Combo().select(index);
            this.m_SelectedViewDef = viewDef;
            this.SetGridSelectedView(viewDef.get_Alias());
        }
        return this.m_SelectedViewDef;
    }
	
	//得到Item的index
	protected int get_i(ViewControl vc,String sv){
		int i = 0;
		for(int j=0;j<vc.get_Combo().getItemCount();j++){
			String a = vc.get_Combo().getItem(j);
			if(a.equalsIgnoreCase(sv)){
				i=j;
				break;
			}
		}
		return i;
	}

	public boolean get_ViewControlVisible() {
		boolean visible = false;
		if (this.m_ViewControl != null){
			visible =  this.m_ViewControl.getVisible();
        }
		return visible;
	}

	public void set_ViewControlVisible(boolean value) {
		if (this.m_ViewControl != null){
            this.m_ViewControl.setVisible(value);
        }
	}

	public boolean get_FirstTimeToLoad() {
		return m_bFirstTimeToLoad;
	}

	public void set_FirstTimeToLoad(boolean m_bFirstTimeToLoad) {
		this.m_bFirstTimeToLoad = m_bFirstTimeToLoad;
	}
	
	protected void SetGridSelectedView(String strView){
		//
    }
	
	public ViewControl getM_DateRangeControl() {
		return m_DateRangeControl;
	}

	public void setM_DateRangeControl(ViewControl m_DateRangeControl) {
		this.m_DateRangeControl = m_DateRangeControl;
	}
	//add by zhangfan End

	public void Refresh() {
		this.LoadData();
	}
}
