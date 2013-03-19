package core.dashboards;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.osgi.framework.Bundle;

import core.print.print;

import siteview.windows.forms.IBOMenuHandler;
import siteview.windows.forms.UltraGridExcelExporter;
import system.ClrInt32;
import system.Convert;
import system.DateTime;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Data.DataColumn;
import system.Data.DataRow;
import system.Windows.Forms.ContextMenu;
import system.Xml.XmlElement;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRange;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.ViewDef;
import Core.Dashboards.XmlDateRangeCategory;
import Siteview.DefRequest;
import Siteview.FieldUtils;
import Siteview.IVirtualKeyList;
import Siteview.QueryGroupDef;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.SiteviewQueryOrderByField;
import Siteview.VirtualBusObListSupportedTypes;
import Siteview.VirtualListIndex;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.ColumnDef;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.IVirtualList;
import Siteview.Api.Relationship;
import Siteview.Api.VirtualKeyList;
import Siteview.Windows.Forms.GridExportExcel;
import Siteview.Windows.Forms.VirtualListGrid;
import Siteview.Xml.GridAction;
import Siteview.Xml.SecurityRight;

public class GridPartControl extends DashboardPartControl {

	private Boolean m_bDescending = false;

	private Boolean m_bFreeVirtualList = false;

	private Boolean m_bLimitQuery = false;

	private BusinessObjectDef m_BusObDef;

	private ContextMenu m_cmContextMenu;

	private Siteview.Api.BusinessObject m_dependBusObj;

	private Siteview.Api.GridDef m_dependGridDef;

	private Boolean m_dependMode = false;

	private PartRefDef m_dependPartRefDef;

	private SiteviewQuery m_SiteviewQuery;

	private GridAction m_GridAction;

	private Siteview.Api.GridDef m_GridDef;

	private Integer m_nNumberOfRecordToReturn;

	private QueryGroupDef m_QueryGroupDef;

	private String m_strBusObName;

	private String m_strGridActionDetail;

	private String m_strGridId="";

	private String m_strOrderByFieldName;

	private VirtualListGrid m_Grid;
	
	
	/** Add by zhangfan 增加右键菜单 Start **/
	
	private Menu popMenu;
	private Action actopen;    			// 运行
	private Action actToExcel;    		// 导出excel
	private Action actToExcelAndOpen;   // 导出excel并打开
	private Action actPrint;			// 打印
	private Action actPrintPreview;		// 打印预览
	//sxf 设置扩展菜单可用性
	private List<Action> actCustoms; 
	
	/** Add by zhangfan 增加右键菜单 End **/
	
	public GridPartControl(ISiteviewApi iSiteviewApi, Composite parent,
			boolean virtualMode) {
		super(iSiteviewApi, parent, SWT.NONE);
		actCustoms = new Vector<Action>();
		initControl(iSiteviewApi,parent,virtualMode);
	}

	public GridPartControl(ISiteviewApi iSiteviewApi, DashboardControl parent,
			boolean virtualMode) {
		super(iSiteviewApi, parent, SWT.NONE);
		actCustoms = new Vector<Action>();
		initControl(iSiteviewApi,parent,virtualMode);
	}
	
	public GridPartControl(ISiteviewApi iSiteviewApi, TabFolder parent,
			boolean virtualMode) {
		super(iSiteviewApi, parent, SWT.NONE);
		actCustoms = new Vector<Action>();
		initControl(iSiteviewApi,parent,virtualMode);
	}
	
	private void initControl(ISiteviewApi iSiteviewApi, Composite parent,
			boolean virtualMode){
		m_Grid = new VirtualListGrid(this.get_MainArea(),iSiteviewApi);
		m_Grid.get_Viewer().addDoubleClickListener(new IDoubleClickListener(){

			@Override
			public void doubleClick(DoubleClickEvent event) {
				onGridDoubleClick(event);
				
			}});

		/** Add by zhangfan 增加右键菜单 Start **/
		createActions(); 			// 创建Action
		
		Table tb = m_Grid.get_Viewer().getTable();
		final MenuManager pm = new MenuManager();
		pm.setRemoveAllWhenShown(true);
		popMenu = pm.createContextMenu(tb);
		tb.setMenu(popMenu);
		pm.addMenuListener(new IMenuListener(){
			private static final long serialVersionUID = 8349259777102315722L;

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator());
				manager.add(actopen);
				manager.add(new Separator());
				manager.add(actToExcel);
				//manager.add(actToExcelAndOpen);
				manager.add(actPrint);
				manager.add(actPrintPreview);
				
				initMenuExtension(manager);
			}
		});
		tb.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = -746189351070889707L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				openclosecoolbar(m_Grid.get_Viewer().getTable());
			}
		});
		tb.addMouseListener(new MouseListener(){
			private static final long serialVersionUID = -1365456860288795318L;
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
			}
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button ==3){
					Table t = (Table)e.widget;
					openclosecoolbar(t);
					if (t.getSelectionCount() >0){
						popMenu.setLocation(t.toDisplay(e.x,e.y));
						popMenu.setVisible(true);
					}else{
						popMenu.setVisible(false);
					}
				}
			}
		});
		
		/** Add by zhangfan 增加右键菜单 End **/
	}
	
    protected void initMenuExtension(IMenuManager manager) {
    	actCustoms.clear();
    	IExtensionPoint extPoint =  Platform.getExtensionRegistry().getExtensionPoint("Siteview.Forms.Common.BusinessObjectMenuExtension");
    	for(IExtension ext: extPoint.getExtensions()){
    		for (IConfigurationElement c:ext.getConfigurationElements()){
    			String strClass = c.getAttribute("class");
    			String strName = c.getAttribute("name");
    			String strIco = c.getAttribute("ico");
    			String strId = c.getAttribute("id");
    			String bundleId =  ext.getContributor().getName();
    			final IBOMenuHandler handle;
    			try {
					Class<?> cls = Platform.getBundle(bundleId).loadClass(strClass);
					handle = (IBOMenuHandler) cls.newInstance();
					
					Action act = new Action(strName){
						@Override
						public void run() {
							if (handle!=null)
								handle.handle(m_Grid.get_SelectedBusOb(), getApi());
						}};
					
					if (strIco!=null && strIco.length()>0){
						final URL imgUrl = Platform.getBundle(bundleId).getResource(strIco);

						ImageDescriptor newImage = new ImageDescriptor(){

							@Override
							public ImageData getImageData() {
								try {
									Image img = new Image(Display.getCurrent(), imgUrl.openStream());
									return img.getImageData();
								} catch (IOException e) {
									e.printStackTrace();
								}
								return null;
							}};
						act.setImageDescriptor(newImage );
		    		}
					act.setId(strId);
					manager.add(act);
					actCustoms.add(act);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
    			
    			
    		}
    	}
	}

	protected void onGridDoubleClick(DoubleClickEvent e) {
    	TableViewer tv = (TableViewer) e.getSource();
		IStructuredSelection iss = (IStructuredSelection) e.getSelection();
		if (iss.size()> 0){
			String strA = "";
			if (iss.getFirstElement() instanceof DataRow){
				String strField = "";
				DataRow dr = (DataRow) iss.getFirstElement();
				for(int i = 0 ; i < dr.get_Table().get_Columns().get_Count(); i++){
					DataColumn dc  = dr.get_Table().get_Columns().get_Item(i);
					if (dc.get_ColumnName().contains("RecId")){
						strField = dc.get_ColumnName();
						strA = (String) dr.get_Item(dc);
					}
				}
				SiteviewQuery q = new SiteviewQuery();
                q.AddBusObQuery(m_strBusObName,QueryInfoToGet.All);
                q.set_BusObSearchCriteria(q.get_CriteriaBuilder().FieldAndValueExpression("RecId",Siteview.Operators.Equals,strA));
                IVirtualKeyList iVirtualKeyList = new Siteview.Api.VirtualKeyList(super.getApi(), q);
                iVirtualKeyList.set_CurrentPosition(0);
                String busName = null;
                if (dr.get_Table().get_Columns().Contains("BusObName"))
                	busName = dr.get_Item("BusObName").toString();
                else
                	busName =  m_strBusObName;
                if(super.get_Parent()!=null){
                	super.get_Parent().OnGoToBusOb(iVirtualKeyList,busName,strA,this.m_GridAction,this.m_strGridActionDetail);
                }else{
                	//
                	DashboardControl _parent_d = get_Parent_D(tv.getControl());
                	_parent_d.OnGoToBusOb(iVirtualKeyList,busName,strA,this.m_GridAction,this.m_strGridActionDetail);
                }
			
			}else{
			VirtualListIndex vi = (VirtualListIndex) iss.getFirstElement();

			String strField = this.m_BusObDef.get_IdField().get_QualifiedName();
			strA = vi.get_List().get_Query().get_BusinessObjectName();
			

			Object busName = vi.get_List().GetRowData(vi.get_Index()).get_Item("BusObName");
			if (busName != null) strA = (String) busName;
			
			if (strA.equals(strField)){
				DataRow dr = vi.get_List().GetRowData(vi.get_Index());
				super.get_Parent().OnGoToBusOb(m_Grid.get_VirtualKeyList(), strA, (String)dr.get_Item(strField), m_GridAction, m_strGridActionDetail);
			}else{
				DataRow dr = vi.get_List().GetRowData(vi.get_Index());
				
				SiteviewQuery q = new SiteviewQuery();
                q.AddBusObQuery(strA,QueryInfoToGet.All);
                q.set_BusObSearchCriteria(q.get_CriteriaBuilder().FieldAndValueExpression(strField,Siteview.Operators.Equals,(String)dr.get_Item(strField)));
                IVirtualKeyList iVirtualKeyList = new Siteview.Api.VirtualKeyList(super.getApi(), q);
                iVirtualKeyList.set_CurrentPosition(0);
                if(super.get_Parent()!=null){
                	super.get_Parent().OnGoToBusOb(iVirtualKeyList,strA,(String)dr.get_Item(strField),this.m_GridAction,this.m_strGridActionDetail);
                }else{
                	//
                	DashboardControl _parent_d = get_Parent_D(tv.getControl());
                	_parent_d.OnGoToBusOb(iVirtualKeyList,strA,(String)dr.get_Item(strField),this.m_GridAction,this.m_strGridActionDetail);
                }
			}
			}
		}
		
		
	}

    //得到父窗口部件,实现双击时弹出业务对象面板
    public DashboardControl get_Parent_D(org.eclipse.swt.widgets.Control a){
    	while(!(a instanceof DashboardControl)){
    		a = a.getParent();
    	}
    	return (DashboardControl) a;
    }
    
	@Override
    public  void DataBind(java.lang.Object dt)
    {
        super.DataBind(dt);
        if(!super.getApi().get_SecurityService().HasBusObRight(this.m_strBusObName,SecurityRight.View)){
            this.m_Grid.set_Visible(false);
            super.ShowNoRightsAlert(this.m_strBusObName);
        }
        else{
            Boolean flag = this.IsDependantOnObjectBrowser();

            if((this.get_DependMode() && !clr.System.StringStaticWrapper.IsNullOrEmpty(this.m_dependPartRefDef.get_DependRelationName())) && !flag){
                Relationship relationship = this.m_dependBusObj.GetRelationship(this.get_DependPartRef().get_DependRelationName());
                Siteview.Api.BusinessObject currentBusinessObject = relationship.get_CurrentBusinessObject();
                Siteview.Api.IVirtualList virtualKeyList = null;
                if(this.m_dependGridDef != null){
                    ICollection requestedFields = this.m_GridDef.GetRequestedFields(super.getApi(), this.m_GridDef.get_Id());
                    
                    virtualKeyList = relationship.GetVirtualObjects(VirtualBusObListSupportedTypes.VirtualListIndex, requestedFields, super.getApi());
                }
                else{
                    virtualKeyList = relationship.GetVirtualObjects(VirtualBusObListSupportedTypes.VirtualListIndex, null, super.getApi());
                }
                this.m_Grid.LoadData(virtualKeyList,this.m_GridDef);
            }
            else{
                this.m_Grid.set_Id(this.get_GridId());
                SiteviewQuery siteviewQuery = this.m_SiteviewQuery;
                if(flag){
                    siteviewQuery = this.m_SiteviewQuery.Clone();
                    this.AppendCriteriaIfDependantOnObjectBrowser(siteviewQuery);
                }
                this.m_Grid.LoadData(siteviewQuery,this.m_GridDef);

                if(this.m_Grid.get_Enabled() && !this.HasVisibleColumns(this.m_Grid)){
                    //this.m_Grid.set_Visible(false);
                }
            }
            if(super.get_DashboardPartDef() != null){
                super.setData(super.get_DashboardPartDef().get_TitleBarDef().get_Text());
            }
            
        }
    }
    
    
    @Override
    public  java.lang.Object GetData(ILoadingStatusSink sink)
    {
    	super.set_FirstTimeToLoad(false);
        return null;

    }

    protected  Boolean IsDependantOnObjectBrowser()
    {


        return (((this.get_DependMode() && (this.m_dependBusObj != null)) && !clr.System.StringStaticWrapper.IsNullOrEmpty(this.m_dependPartRefDef.get_LinkedTo())) && this.m_dependBusObj.get_Definition().HasAnnotation("AggregateBusinessObject"));

    }

    protected  void AppendCriteriaIfDependantOnObjectBrowser(SiteviewQuery fq)
    {

        if(this.IsDependantOnObjectBrowser()){
            String linkedTo = this.m_dependPartRefDef.get_LinkedTo();
            IEnumerator it1 = this.m_dependBusObj.get_Definition().get_FieldDefs().GetEnumerator();
            while(it1.MoveNext()){
                FieldDef def = (FieldDef)it1.get_Current();

                if(def.HasAnnotation("AggregateGroupBy")){
                    String str = null;
                    String strField = "";
                    String[] __strBusOb_1 = new String[1];
                    String[] __strField_2 = new String[1];
                    FieldUtils.SplitBusObField(def.GetAnnotationValue("AggregateGroupBy"),__strBusOb_1,__strField_2);
                    str = __strBusOb_1[0];
                    strField = __strField_2[0];
                    if((this.get_BusObDef() != null) && this.get_BusObDef().HasField(strField)){
                        String str3 = this.m_dependBusObj.GetField(def.get_Name()).get_Value().ToText();


                        if(!clr.System.StringStaticWrapper.IsNullOrEmpty(str3) && !str3.equals("<empty>")){
                            XmlElement element = fq.get_CriteriaBuilder().FieldAndValueExpression(strField, Siteview.Operators.Equals, str3);
                            if(fq.get_BusObSearchCriteria() == null){
                                fq.set_BusObSearchCriteria(element);
                            }
                            else{
                                fq.set_BusObSearchCriteria(fq.get_CriteriaBuilder().AndExpressions(fq.get_BusObSearchCriteria(),element));
                            }
                        }
                    }
                }
            }
        }

    }
    
    private  Boolean HasVisibleColumns(VirtualListGrid grid)
    {
    	return true;
    	
//        String str = "";
//        Boolean flag = false;
//        IEnumerator it1 = this.m_GridDef.get_ColumnDefs().GetEnumerator();
//        while(it1.MoveNext()){
//            ColumnDef def = (ColumnDef)it1.get_Current();
//
//
//            if(!grid.ExistsColumn(def.get_QualifiedName())){
//                str = str + "," + def.get_Alias();
//            }
////            else{
////                UltraGridColumn column = grid.get_DisplayLayout().get_Bands()[0].get_Columns()[def.get_QualifiedName()];
////                if(!column.get_Hidden()){
////                    flag = true;
////                }
////            }
//        }
//        if(!flag){
//            super.ShowNoRightsAlert(this.m_strBusObName);
//
//            return flag;
//        }
//
//        if(!str.equals("")){
//            String strMessage = StringUtils.SetToken(Res.get_Default().GetString("GridPartControl.FieldsWithOutViewRights"), "FIELDS", str.substring(1).toString());
//            super.OnAlertRaised(this,new AlertRaisedEventArgs(strMessage, AlertType.Informational, null, true, AlertSettingCategory.ViewRights));
//        }
//
//        return flag;

    }
    
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef) {
		
		super.DefineFromDef(def, partRefDef);
		Core.Dashboards.GridPartDef dashboardPartDef = (Core.Dashboards.GridPartDef) super
				.get_DashboardPartDef();
		
		if (dashboardPartDef != null) {
			this.m_Grid.set_Name(def.get_Id());
			this.m_strBusObName = dashboardPartDef.get_BusObName();

			this.m_BusObDef = super.getApi().get_BusObDefinitions()
					.GetBusinessObjectDef(this.m_strBusObName);
			if ((!dashboardPartDef.get_GridDefId().equals(""))) {

				this.m_GridDef = super.getApi().get_Presentation()
						.GetGridDefById(dashboardPartDef.get_GridDefId());
			}
			
			if ((!dashboardPartDef.get_QueryGroupId().equals(""))) {
				this.m_QueryGroupDef = (QueryGroupDef) super
						.getApi()
						.get_LiveDefinitionLibrary()
						.GetDefinition(
								DefRequest.ById(dashboardPartDef
										.get_QueryGroupScope(),
										dashboardPartDef
												.get_QueryGroupScopeOwner(),
										dashboardPartDef
												.get_QueryGroupLinkedTo(),
										QueryGroupDef.get_ClassName(),
										dashboardPartDef.get_QueryGroupId()));
			}
			
			this.m_GridAction = dashboardPartDef.get_GridAction();
			this.m_strGridActionDetail = dashboardPartDef
					.get_GridActionDetail();
			this.m_strOrderByFieldName = dashboardPartDef
					.get_OrderByFieldName();
			this.m_bLimitQuery = dashboardPartDef.get_LimitQuery();
			this.m_nNumberOfRecordToReturn = dashboardPartDef
					.get_NumberOfRecordToReturn();
			this.m_bDescending = dashboardPartDef.get_IsDescending();
			super.set_DateRangeControlVisible(dashboardPartDef
					.get_DateRangeControlVisible()
					&& dashboardPartDef.get_DefaultDateRangeDef()
							.get_ApplyDateRange());
			
			if (super.get_DateRangeControlVisible()) {
				String gridSelectedDateRange = super.getApi()
						.get_SettingsService().get_DisplaySettings()
						.GetGridSelectedDateRange(this.get_GridId());
				for (ViewDef def3 : dashboardPartDef.get_DateRangeOptions()) {
					if (def3.get_DateRange() != DateRange.None) {
						this.AddDateTimeRange(def3);
					}
				}
				if (dashboardPartDef.get_AllowDateTimeEntry()) {
					super.AddCustomDateRange();
				}
				this.AddFiscalDateRangeIfAny(dashboardPartDef);

				if (gridSelectedDateRange.equals("")) {
					super.SetDefaultDateRange(dashboardPartDef
							.get_DefaultDateRangeDef());
				} else {
					String strStartDateTime = "";
					String strEndDateTime = "";

					if (gridSelectedDateRange.equals(Res.get_Default()
							.GetString("ViewDef.Custom"))) {

						strStartDateTime = super
								.getApi()
								.get_SettingsService()
								.get_DisplaySettings()
								.GetGridCustomDateRangeStartDateTime(
										this.get_GridId());

						strEndDateTime = super
								.getApi()
								.get_SettingsService()
								.get_DisplaySettings()
								.GetGridCustomDateRangeEndDateTime(
										this.get_GridId());
					}
					super.SetSelectedDateRange(
							dashboardPartDef.get_DefaultDateRangeDef(),
							gridSelectedDateRange, strStartDateTime,
							strEndDateTime);
				}
			}
			// super.SetSelectionArea();
			this.PrepareQuery();
		}

	}

	@Override
	protected void ReQuery(ViewDef vViewDef, ViewDef vDateTimeDef)
    {
        if (!super.get_FirstTimeToLoad()){
            SiteviewQuery query = this.CreateSiteviewQuery();
            if (vDateTimeDef != null)
            {
                if (vDateTimeDef.get_IsCustom())
                {
                    query = DateRangeCriteriaCreator.AddCustomDateRangeCriteria(query, this.get_GridPartDef().get_DefaultDateRangeDef().get_DateTimeField(), vViewDef.get_StartDateTime(), vViewDef.get_EndDateTime());
                }
                else if (vDateTimeDef.get_IsFiscalPeriod())
                {
                    query = DateRangeCriteriaCreator.AddFiscalDateRangeCriteria(super.getApi(), query, this.get_GridPartDef().get_DefaultDateRangeDef().get_DateTimeField(), vDateTimeDef.get_FiscalPeriod());
                }
                else
                {
                    query = DateRangeCriteriaCreator.AddDateRangeCriteria(super.getApi(), query, vViewDef.get_DateRange(), this.get_GridPartDef().get_DefaultDateRangeDef().get_DateTimeField());
                }
            }
            this.set_Query(query);
            this.DoDataRefresh();
        }
    }
	
	protected void PrepareQuery() {
		DateRange all = DateRange.All;
		String gridSelectedDateRange = super.getApi().get_SettingsService()
				.get_DisplaySettings()
				.GetGridSelectedDateRange(this.get_GridId());
		Boolean gridDateRangeIsFiscal = super.getApi().get_SettingsService()
				.get_DisplaySettings()
				.GetGridDateRangeIsFiscal(this.get_GridId());

		if (!gridSelectedDateRange.equals("")
				&& !gridSelectedDateRange.equals(Res.get_Default().GetString(
						"ViewDef.Custom"))) {

			all = XmlDateRangeCategory.ToCategory(gridSelectedDateRange);
		}

		this.m_SiteviewQuery = this.CreateSiteviewQuery();
 
		if (gridSelectedDateRange.equals(Res.get_Default().GetString(
				"ViewDef.Custom"))) {
			String gridCustomDateRangeStartDateTime = super.getApi()
					.get_SettingsService().get_DisplaySettings()
					.GetGridCustomDateRangeStartDateTime(this.get_GridId());
			String gridCustomDateRangeEndDateTime = super.getApi()
					.get_SettingsService().get_DisplaySettings()
					.GetGridCustomDateRangeEndDateTime(this.get_GridId());
			DateTime dtStart = gridCustomDateRangeStartDateTime.equals("") ? DateTime
					.get_Today() : Convert
					.ToDateTime(gridCustomDateRangeStartDateTime);
			DateTime dtEnd = gridCustomDateRangeEndDateTime.equals("") ? DateTime
					.get_Today() : Convert
					.ToDateTime(gridCustomDateRangeEndDateTime);

			this.m_SiteviewQuery = DateRangeCriteriaCreator
					.AddCustomDateRangeCriteria(this.m_SiteviewQuery, this
							.get_GridPartDef().get_DefaultDateRangeDef()
							.get_DateTimeField(), dtStart, dtEnd);
		} else if (gridSelectedDateRange.equals("")) {
			if (this.get_GridPartDef().get_DefaultDateRangeDef()
					.get_ApplyDateRange()) {

				this.m_SiteviewQuery = DateRangeCriteriaCreator
						.AddDefaultDateRangeCriteria(super.getApi(),
								this.m_SiteviewQuery, this.get_GridPartDef()
										.get_DefaultDateRangeDef());
			}
		} else if (gridDateRangeIsFiscal) {

			this.m_SiteviewQuery = DateRangeCriteriaCreator
					.AddFiscalDateRangeCriteria(super.getApi(),
							this.m_SiteviewQuery, this.get_GridPartDef()
									.get_DefaultDateRangeDef()
									.get_DateTimeField(), gridSelectedDateRange);
		} else {

			this.m_SiteviewQuery = DateRangeCriteriaCreator
					.AddDateRangeCriteria(super.getApi(), this.m_SiteviewQuery,
							all, this.get_GridPartDef()
									.get_DefaultDateRangeDef()
									.get_DateTimeField());
		}

	}

	protected SiteviewQuery CreateSiteviewQuery() {
		SiteviewQuery query = null;
		if (this.m_QueryGroupDef != null) {
			query = new SiteviewQuery(this.m_QueryGroupDef.get_SiteviewQuery()
					.ToXml());
			if (this.m_bLimitQuery) {

				query = this.AddQueryLimit(query,
						this.m_nNumberOfRecordToReturn, this.m_bDescending,
						this.m_strOrderByFieldName);
			}
		}
		if (query == null) {
			query = new SiteviewQuery();
			query.AddBusObQuery(this.m_strBusObName, QueryInfoToGet.All);
			query.set_CacheRequested(true);
		}
		if (this.m_GridDef != null) {
			query.set_InfoToGet(QueryInfoToGet.RequestedFields);
			query.set_RequestedFields(this.m_GridDef.GetRequestedFields(
					super.getApi(), this.get_GridId()));
		}

		return query;

	}

	protected SiteviewQuery AddQueryLimit(SiteviewQuery query,
			Integer nNumberOfRecordToReturn, Boolean bDescending,
			String strOrderByFieldName) {
		if (query != null) {
			query.set_MaximumRecords(nNumberOfRecordToReturn);
			String[] astrField = null;
			if (query.get_OrderByList() != null) {
				astrField = new String[query.get_OrderByList().get_Count()];
				Integer index = 0;
				IEnumerator it1 = query.get_OrderByList().GetEnumerator();
				while (it1.MoveNext()) {
					SiteviewQueryOrderByField field = (SiteviewQueryOrderByField) it1
							.get_Current();
					astrField[index] = field.get_Name();
					index++;
				}
			}
			if ("".compareTo(strOrderByFieldName) != 0) {
				astrField = new String[] { strOrderByFieldName };
			} else if (((this.m_GridDef != null) && (this.m_GridDef
					.get_ColumnDefs() != null))
					&& (this.m_GridDef.get_ColumnDefs().get_Count() > 0)) {
				IEnumerator enumerator = this.m_GridDef.get_ColumnDefs()
						.GetEnumerator();
				enumerator.MoveNext();
				ColumnDef current = (ColumnDef) enumerator.get_Current();
				String[] strArray2 = current.get_QualifiedName().split(".");
				Integer length = strArray2.length;
				if (length > 1) {
					astrField = new String[] { this.m_strBusObName + "."
							+ strArray2[length - 1] };
				}
			}
			if (astrField == null) {

				return query;
			}
			if (bDescending) {
				query.AddOrderByDesc(astrField);

				return query;
			}
			query.AddOrderBy(astrField);
		}

		return query;

	}

	protected void AddFiscalDateRangeIfAny(Core.Dashboards.GridPartDef partDef) {
		Integer fiscalYear = 0;
		Integer fiscalQuarter = 0;
		Integer[] __fiscalYear_0 = new Integer[1];
		Integer[] __fiscalQuarter_1 = new Integer[1];
		super.getApi()
				.get_SystemFunctions()
				.GetFiscalYearAndQuarterGivenDate(DateTime.get_Now(),
						__fiscalYear_0, __fiscalQuarter_1);
		fiscalYear = __fiscalYear_0[0];
		fiscalQuarter = __fiscalQuarter_1[0];
		Integer result = 0;
		if ((partDef.get_PrecedingFiscalQuarters() < 0)
				|| (partDef.get_SucceedingFiscalQuarters() > 0)) {
			for (Integer i = partDef.get_PrecedingFiscalQuarters(); i <= partDef
					.get_SucceedingFiscalQuarters(); i++) {
				Integer a = fiscalQuarter + i;
				ClrInt32 __integer_2 = new ClrInt32();
				Integer num6 = system.Math.DivRem(a, 4, __integer_2);
				result = __integer_2.getValue();
				if (result > 0) {
					a = result;
				}
				if (result <= 0) {
					num6--;
					a = 4 + result;
				}
				super.AddFiscalDateRange(fiscalYear + num6, a);
			}
		}
		if ((partDef.get_PrecedingFiscalYears() < 0)
				|| (partDef.get_SucceedingFiscalYears() > 0)) {
			for (Integer j = partDef.get_PrecedingFiscalYears(); j <= partDef
					.get_SucceedingFiscalYears(); j++) {
				super.AddFiscalDateRange(fiscalYear + j, 0);
			}
		}

	}

	public BusinessObjectDef get_BusObDef() {

		return this.m_BusObDef;
	}

	public void set_BusObDef(BusinessObjectDef value) {
		this.m_BusObDef = value;
	}

	public String get_BusObName() {

		return this.m_strBusObName;
	}

	public void set_BusObName(String value) {
		this.m_strBusObName = value;
	}

	public DateTime get_DefaultCustomEndDateValue() {

		return DateTime.get_Now().get_Date();
	}

	public DateTime get_DefaultCustomStartDateValue() {

		return DateTime.get_Now().get_Date();
	}

	public Siteview.Api.BusinessObject get_DependBusObj() {

		return this.m_dependBusObj;
	}

	public void set_DependBusObj(Siteview.Api.BusinessObject value) {
		this.m_dependBusObj = value;
	}

	public Siteview.Api.GridDef get_DependGridDef() {

		return this.m_dependGridDef;
	}

	public void set_DependGridDef(Siteview.Api.GridDef value) {
		this.m_dependGridDef = value;
	}

	public Boolean get_DependMode() {

		return this.m_dependMode;
	}

	public void set_DependMode(Boolean value) {
		this.m_dependMode = value;
	}

	public PartRefDef get_DependPartRef() {

		return this.m_dependPartRefDef;
	}

	public void set_DependPartRef(PartRefDef value) {
		this.m_dependPartRefDef = value;
	}

	public GridAction get_DrillDownGridBehavior() {

		return this.m_GridAction;
	}

	public void set_DrillDownGridBehavior(GridAction value) {
		this.m_GridAction = value;
	}

	public Siteview.Api.GridDef get_GridDef() {

		return this.m_GridDef;
	}

	public void set_GridDef(Siteview.Api.GridDef value) {
		this.m_GridDef = value;
	}

	public String get_GridId() {
		if ("".compareTo(this.m_strGridId) != 0) {

			return this.m_strGridId;
		}

		return (super.get_DashboardPartDef().get_Scope().toString() + "."
				+ super.get_DashboardPartDef().get_ScopeOwner() + "."
				+ super.get_Parent().get_DashboardPartDef().get_Name() + "." + super
				.get_DashboardPartDef().get_Name());
	}

	public void set_GridId(String value) {
		this.m_strGridId = value;
	}

	protected Core.Dashboards.GridPartDef get_GridPartDef() {

		return ((Core.Dashboards.GridPartDef) super.get_DashboardPartDef());
	}

	public Boolean get_IsPrintControl() {

		return true;
	}

	public Boolean get_IsPrintTable() {

		return true;
	}

	public Boolean get_LimitQuery() {

		return this.m_bLimitQuery;
	}

	public void set_LimitQuery(Boolean value) {
		this.m_bLimitQuery = value;
	}

	public String get_OrderByFieldName() {

		return this.m_strOrderByFieldName;
	}

	public void set_OrderByFieldName(String value) {
		this.m_strOrderByFieldName = value;
	}

	public SiteviewQuery get_Query() {

		return this.m_SiteviewQuery;
	}

	public void set_Query(SiteviewQuery value) {
		this.m_SiteviewQuery = value;
	}

	public VirtualListGrid get_Grid() {
		return m_Grid;
	}

	public void set_Grid(VirtualListGrid m_Grid) {
		this.m_Grid = m_Grid;
	}
	
	
	public String getM_strGridActionDetail() {
		return m_strGridActionDetail;
	}

	public void setM_strGridActionDetail(String m_strGridActionDetail) {
		this.m_strGridActionDetail = m_strGridActionDetail;
	}

	/** Add by zhangfan 增加右键菜单 Start **/
	// TODO　创建Action  增加右键菜单
	private void createActions(){
		actopen = new Action("打开(&O)"){
			private static final long serialVersionUID = -2351944916673249516L;

			@Override
			public void run() {
				onGridDoubleClick();
			}
		};
		actopen.setEnabled(true);
		//actopen.setAccelerator(SWT.CTRL + 'O'); // 快捷键
		//actopen.setImageDescriptor(new ImageDescriptor(){ // 图标
		//	@Override
		//	public ImageData getImageData() {
		//		return SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[Image]Core#Images.Icons.Edit.ico"),0x12,0x12).getImageData();
		//	}
		//});
		final String tempPath="temp"+(new Date().getTime())+(new Random().nextInt(100))+".xls";
		actToExcel=new Action("导出到Excel"){
			private static final long serialVersionUID = -1101563835951018492L;
			@Override
			public void run(){
				try{
					IVirtualList m_virtualList = m_Grid.get_VirtualList();
					GridExportExcel gridExportExcel=new GridExportExcel(m_GridDef,m_virtualList);
					File tempFile=new File(tempPath);
					UltraGridExcelExporter exp=new UltraGridExcelExporter((VirtualKeyList)m_virtualList,m_GridDef,tempFile);
					exp.createFile();
					gridExportExcel.exportExcel2Local(tempFile);
				}
				catch (java.lang.Exception e) {
					MessageBox msg=new MessageBox(new Shell(),SWT.YES);
            		msg.setMessage("导出失败!原因:"+e.getMessage());
            		msg.setText("SiteView应用程序");
            		msg.open();
				}
			}
		};
		actToExcel.setEnabled(true);
		actToExcelAndOpen=new Action("导出并在Excel中打开"){
			private static final long serialVersionUID = -7013231485948356642L;
			@Override
			public void run(){
				try{
					IVirtualList m_virtualList = m_Grid.get_VirtualList();
					GridExportExcel gridExportExcel=new GridExportExcel(m_GridDef,m_virtualList);
					File tempFile=new File(tempPath);
					UltraGridExcelExporter exp=new UltraGridExcelExporter((VirtualKeyList)m_virtualList,m_GridDef,tempFile);
					exp.createFile();
					File newFile = gridExportExcel.exportExcel2Local(tempFile);
					exp.exportAndOpen(newFile);
				}
				catch (java.lang.Exception e) {
					System.out.println(e);
					MessageBox msg=new MessageBox(new Shell(),SWT.YES);
            		msg.setMessage("导出失败!原因:"+e.getMessage());
            		msg.setText("SiteView应用程序");
            		msg.open();
				}
			}
		};
		actToExcelAndOpen.setEnabled(true);
		
		actPrint=new Action("打印"){
			private static final long serialVersionUID = -604379924398926491L;

			public void run(){
				print.toPrint(m_Grid.getShell(),m_Grid.get_Viewer().getTable());
			}
		};
		
		actPrintPreview=new Action("打印预览"){
			private static final long serialVersionUID = -2018526897913551291L;

			public void run(){
				print.toPrintPreview(m_Grid.getShell(),m_Grid.get_Viewer().getTable());
			}
		};
	}

	protected void openclosecoolbar(Table tb) {
		boolean flag = false;
		boolean flag1 = false;
		if(tb.getSelectionCount()>0){
			flag = true;
		}
		actopen.setEnabled(flag);
		if(tb.getItemCount()>0){
			flag1 = true;
		}
		actToExcel.setEnabled(flag1);
		actPrint.setEnabled(flag1);
		actPrintPreview.setEnabled(flag1);
		// actToExcelAndOpen.setEnabled(flag1);

		//sxf 设置扩展菜单可用性
		for(Action mi :actCustoms){
			mi.setEnabled(tb.getSelectionCount()>0);
		}

	}
	
	// 右键 打开方法
	protected void onGridDoubleClick() {
    	TableViewer tv = m_Grid.get_Viewer();
		IStructuredSelection iss = (IStructuredSelection) tv.getSelection();
		if (iss.size()> 0){
			String strA = "";
			if (iss.getFirstElement() instanceof DataRow){
				String strField = "";
				DataRow dr = (DataRow) iss.getFirstElement();
				for(int i = 0 ; i < dr.get_Table().get_Columns().get_Count(); i++){
					DataColumn dc  = dr.get_Table().get_Columns().get_Item(i);
					if (dc.get_ColumnName().contains("RecId")){
						strField = dc.get_ColumnName();
						strA = (String) dr.get_Item(dc);
					}
				}
				SiteviewQuery q = new SiteviewQuery();
                q.AddBusObQuery(m_strBusObName,QueryInfoToGet.All);
                q.set_BusObSearchCriteria(q.get_CriteriaBuilder().FieldAndValueExpression("RecId",Siteview.Operators.Equals,strA));
                IVirtualKeyList iVirtualKeyList = new Siteview.Api.VirtualKeyList(super.getApi(), q);
                iVirtualKeyList.set_CurrentPosition(0);
                if(super.get_Parent()!=null){
                	super.get_Parent().OnGoToBusOb(iVirtualKeyList,m_strBusObName,strA,this.m_GridAction,this.m_strGridActionDetail);
                }else{
                	//
                	DashboardControl _parent_d = get_Parent_D(tv.getControl());
                	_parent_d.OnGoToBusOb(iVirtualKeyList,strA,(String)dr.get_Item(strField),this.m_GridAction,this.m_strGridActionDetail);
                }
			
			}else{
				VirtualListIndex vi = (VirtualListIndex) iss.getFirstElement();
	
				String strField = this.m_BusObDef.get_IdField().get_QualifiedName();
				strA = vi.get_List().get_Query().get_BusinessObjectName();
				
				Object busName = vi.get_List().GetRowData(vi.get_Index()).get_Item("BusObName");
				if (busName != null) strA = (String) busName;
				
				if (strA.equals(strField)){
					DataRow dr = vi.get_List().GetRowData(vi.get_Index());
					super.get_Parent().OnGoToBusOb(m_Grid.get_VirtualKeyList(), strA, (String)dr.get_Item(strField), m_GridAction, m_strGridActionDetail);
				}else{
					DataRow dr = vi.get_List().GetRowData(vi.get_Index());
					
					SiteviewQuery q = new SiteviewQuery();
	                q.AddBusObQuery(strA,QueryInfoToGet.All);
	                q.set_BusObSearchCriteria(q.get_CriteriaBuilder().FieldAndValueExpression(strField,Siteview.Operators.Equals,(String)dr.get_Item(strField)));
	                IVirtualKeyList iVirtualKeyList = new Siteview.Api.VirtualKeyList(super.getApi(), q);
	                iVirtualKeyList.set_CurrentPosition(0);
	                if(super.get_Parent()!=null){
	                	super.get_Parent().OnGoToBusOb(iVirtualKeyList,strA,(String)dr.get_Item(strField),this.m_GridAction,this.m_strGridActionDetail);
	                }else{
	                	//
	                	DashboardControl _parent_d = get_Parent_D(tv.getControl());
	                	_parent_d.OnGoToBusOb(iVirtualKeyList,strA,(String)dr.get_Item(strField),this.m_GridAction,this.m_strGridActionDetail);
	                }
				}
			}
		}
	}
	/** Add by zhangfan 增加右键菜单 End **/
}
