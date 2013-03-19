package core.ui.dialog;

import mainsoft.ibm.icu.util.Calendar;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Type;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.IList;
import Core.Dashboards.AmountOfTime;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRange;
import Core.Dashboards.GridPartDef;
import Core.Dashboards.MultiViewGridPartDef;
import Core.Dashboards.ViewDef;
import Core.Dashboards.XmlAmountOfTimeCategory;
import Core.Dashboards.XmlDateRangeCategory;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.PlaceHolder;
import Siteview.QueryGroupDef;
import Siteview.StringUtils;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.Field;
import Siteview.Api.FieldDef;
import Siteview.Api.GridDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.FunctionCategory;
import Siteview.Xml.GridAction;
import Siteview.Xml.LocalizeHelper;
import core.apploader.search.dialog.SimpleExpressSelector;
import core.search.form.SearchCenterForm;
import core.ui.DashBoardPartCenterForm;


public class ViewGrid extends Dialog{

	private Text txtName;
	private Text txtAlias;

	//查询
	private CCombo cb_BusObLink;  //业务对象
	private Menu menu;
	private MenuItem menuUp;  //上一条
	private MenuItem menuDown;  //下一条
	private int count=0;       //业务的 菜单数据总数
	private ICollection placeHolderList;  //业务的 菜单数据
	private Button Checked_querydate;   //将查询结果限制一个日期内
	private Text txt_searchgroup;         //搜索群组
	private Combo cb_DateTimeField;    //日期时间字段
	private Combo cb_timelength;         //日期时间长度
	private Button Radio_timelength;      //使用日期长度
	private Button Radio_timeRange;       //使用日期范围
	private Button Radio_timequantity;    //使用日期量
	private CCombo cb_startdate;    //开始日期
	private CCombo cb_enddate;		//结束日期
	private Combo cb_last;           
	private Spinner sp_number;
	private Combo cb_dateortime;
	private ArrayList m_collFieldDefHolders;
	
	//网格
	private Button Radio_GoToRecord;   //双击钻取业务对象
	private Button Radio_GoToParent;   //双击钻取业务对象的父对象
	private Combo cb_grid;  //网格
	private GridPartDef gredpartdef;
	
	//运行时选 项
	
	private MultiViewGridPartDef multidef;
	
	private Table table_showoption;
	private Table table_showdaterange;
	private Button Checked_showoption;       //显示查看选 项
	private Button Checked_showdaterange;
	private Button Checked_allowdatewrite;
	private Button Checked_ybloading;
	private Button Checked_showrefresh;
	private CCombo cb_refreshinterval;
	private Spinner sp_quarter_start;
	private Spinner sp_quarter_end;
	private Spinner sp_year_start;
	private Spinner sp_year_end;
	
	
	private Button but_UP;
	private Button but_down;
	private Button but_Add;
	private Button but_Edit;
	private Button but_del;
	
	private Generalmethods general;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private String type = "New"; // 标志位 1=添加 2=编辑 3=复制
	
	private ViewDef selViewDef;
	

	
	public ViewGrid(Shell parentShell, DashboardPartDef dashboarddef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr, String type) {
		super(parentShell);
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.dashboardpartdef = dashboarddef;
		dashboarddef.set_EditMode(true);
		this.m_Def = m_Def;
		this.type = type;
		this.linkstr = linkstr;
		general=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, m_Def, linkstr);
		//网格
		gredpartdef=(GridPartDef) dashboardpartdef;
		//运行时选项
		
		multidef=(MultiViewGridPartDef) dashboardpartdef;
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 730);
		newShell.setLocation(300, 70);
		newShell.setText("新建	多视图网格");
		super.configureShell(newShell);
	}
	

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
//		container.setSize(700, 800);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 31);
		fd_lblNewLabel.left = new FormAttachment(0, 26);
		fd_lblNewLabel.right = new FormAttachment(0, 85);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("\u540D\u79F0(N):");
		
		Label lbla = new Label(container, SWT.NONE);
		lbla.setText("\u522B\u540D(A):");
		FormData fd_lbla = new FormData();
		fd_lbla.top = new FormAttachment(lblNewLabel, 21);
		fd_lbla.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lbla.setLayoutData(fd_lbla);
		
		txtName = new Text(container, SWT.BORDER);
		FormData fd_txtName = new FormData();
		fd_txtName.right = new FormAttachment(lblNewLabel, 406, SWT.RIGHT);
		fd_txtName.top = new FormAttachment(0, 28);
		fd_txtName.left = new FormAttachment(lblNewLabel, 6);
		txtName.setLayoutData(fd_txtName);
		
		txtAlias = new Text(container, SWT.BORDER);
		FormData fd_txtAlias = new FormData();
		fd_txtAlias.top = new FormAttachment(lbla, -3, SWT.TOP);
		fd_txtAlias.right = new FormAttachment(txtName, 0, SWT.RIGHT);
		fd_txtAlias.left = new FormAttachment(txtName, 0, SWT.LEFT);
		txtAlias.setLayoutData(fd_txtAlias);
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 643);
		fd_tabFolder.top = new FormAttachment(txtAlias, 21);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -40);
		tabFolder.setLayoutData(fd_tabFolder);
		
		
		
		general.tabFolder_1( tabFolder);
		general.tabFolder_2( tabFolder);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u67E5\u8BE2");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		
		Label lblNewLabel_6 = new Label(composite, SWT.NONE);
		lblNewLabel_6.setBounds(10, 10, 54, 18);
		lblNewLabel_6.setText("\u4E1A\u52A1\u5BF9\u8C61:");
		
		cb_BusObLink = new CCombo(composite, SWT.BORDER);
		cb_BusObLink.setVisibleItemCount(0);
		cb_BusObLink.setEditable(true);
		cb_BusObLink.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				if(e.button == 1){
					createObjectMenu();
					menu.setLocation(cb_BusObLink.toDisplay(0,0));
					menu.setVisible(true);
				}
				
			}
		});
		cb_BusObLink.setBounds(137, 7, 395, 20);
		Label lblNewLabel_8 = new Label(composite, SWT.NONE);
		lblNewLabel_8.setBounds(10, 45, 78, 18);
		lblNewLabel_8.setText("\u641C\u7D22\u7FA4\u7EC4(E):");
		
		txt_searchgroup = new Text(composite, SWT.BORDER);
		txt_searchgroup.setBounds(137, 45, 328, 25);
		
		Button but_browse = new Button(composite, SWT.NONE);
		but_browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SearchCenterForm searchCenterFrom = new SearchCenterForm("搜索  中心", true);
				searchCenterFrom.setShowAllBusName(true);
				searchCenterFrom.open();
				PlaceHolder ph = searchCenterFrom.getBackPlaceHolder();
				if(ph == null) return;
				IDefinition m_SelectedItem = (IDefinition) m_Library.GetDefinition(DefRequest.ById(ph.get_Scope(), ph.get_ScopeOwner(), ph.get_LinkedTo(),QueryGroupDef.get_ClassName(), ph.get_Id()));
				txt_searchgroup.setText(ph.get_Alias());
				txt_searchgroup.setData(m_SelectedItem);
			}
		});
		but_browse.setBounds(471, 45, 72, 22);
		but_browse.setText("\u6D4F\u89C8(B)...");
		
		Group group = new Group(composite, SWT.NONE);
		group.setText("");
		group.setBounds(10, 90, 576, 250);
		
		Label lblNewLabel_9 = new Label(group, SWT.NONE);
		lblNewLabel_9.setBounds(10, 30, 82, 18);
		lblNewLabel_9.setText("\u65E5\u671F\u65F6\u95F4\u5B57\u6BB5");
		
		cb_DateTimeField = new Combo(group, SWT.NONE);
		cb_DateTimeField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				if(cb_DateTimeField.getSelectionIndex()!=-1&&cb_DateTimeField.getData(cb_DateTimeField.getText())==null){
					
					BusinessObjectDef bus=(BusinessObjectDef) cb_BusObLink.getData();
					SimpleExpressSelector se=new SimpleExpressSelector(getShell(),bus.get_Name(),"",4,false);
					se.open();
					FieldDef field =se.getFieldDef();
					boolean flag=true;
					for(String str:cb_DateTimeField.getItems()){
						if(field.equals(cb_DateTimeField.getData(str))){
							flag=false;
						}
					}
					cb_DateTimeField.setText(field.get_Alias());
					if(flag){
						cb_DateTimeField.add(field.get_Alias());
						cb_DateTimeField.setData(field.get_Alias(),field.get_QualifiedName());
						cb_DateTimeField.remove("{更多值}");
						cb_DateTimeField.add("{更多值}");
						cb_DateTimeField.setData("{更多值}",null);
					}
					
				}
				
			}
		});
		cb_DateTimeField.setBounds(195, 27, 333, 20);
		cb_DateTimeField.setEnabled(false);
		
		Radio_timelength = new Button(group, SWT.RADIO);
		Radio_timelength.setSelection(true);
		Radio_timelength.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showControl(2);
			}
		});
		Radio_timelength.setBounds(10, 61, 104, 16);
		Radio_timelength.setText("\u4F7F\u7528\u65F6\u95F4\u957F\u5EA6");
		Radio_timelength.setEnabled(false);
		
		cb_timelength = new Combo(group, SWT.NONE);
		cb_timelength.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setTableDefaultDateRange((DateRange)cb_timelength.getData(cb_timelength.getText().trim()), true);
			}
		});
		cb_timelength.setBounds(195, 57, 333, 20);
		cb_timelength.setEnabled(false);
		
		Radio_timeRange = new Button(group, SWT.RADIO);
		Radio_timeRange.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showControl(3);
			}
		});
		Radio_timeRange.setText("\u4F7F\u7528\u65F6\u95F4\u8303\u56F4");
		Radio_timeRange.setBounds(10, 96, 104, 16);
		Radio_timeRange.setEnabled(false);
		
		Label lblNewLabel_10 = new Label(group, SWT.NONE);
		lblNewLabel_10.setBounds(195, 98, 63, 18);
		lblNewLabel_10.setText("\u5F00\u59CB\u65E5\u671F");
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setText("\u7ED3\u675F\u65E5\u671F");
		label_3.setBounds(195, 141, 63, 18);
		
		Radio_timequantity = new Button(group, SWT.RADIO);
		Radio_timequantity.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showControl(4);
			}
		});
		Radio_timequantity.setText("\u4F7F\u7528\u65F6\u95F4\u91CF");
		Radio_timequantity.setBounds(10, 181, 104, 16);
		Radio_timequantity.setEnabled(false);
		
		cb_last = new Combo(group, SWT.READ_ONLY);
		cb_last.setBounds(195, 177, 94, 20);
		cb_last.setEnabled(false);
		
		sp_number = new Spinner(group, SWT.BORDER);
		sp_number.setBounds(291, 176, 50, 21);
		sp_number.setEnabled(false);
		
		cb_dateortime = new Combo(group, SWT.READ_ONLY);
		cb_dateortime.setBounds(342, 177, 186, 20);
		cb_dateortime.setEnabled(false);
		
		Checked_querydate = new Button(group, SWT.CHECK);
		Checked_querydate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showControl(1);
			}
		});
		Checked_querydate.setText("\u5C06\u67E5\u8BE2\u7ED3\u679C\u9650\u5236\u5728\u4E00\u4E2A\u65E5\u671F\u8303\u56F4\u5185");
		Checked_querydate.setBounds(10, 0, 214, 16);
		
		cb_startdate = new CCombo(group, SWT.BORDER);
		cb_startdate.setEditable(false);
		cb_startdate.setEnabled(false);
		cb_startdate.setBounds(267, 96, 261, 21);
		cb_enddate = new CCombo(group, SWT.BORDER);
		cb_enddate.setEditable(false);
		cb_enddate.setEnabled(false);
		cb_enddate.setBounds(267, 138, 261, 21);
		cb_enddate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				DashboardDateDialog datedialog=new DashboardDateDialog(getShell(), null);
				if(datedialog.open()==Dialog.OK){
					java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
					ob.add(datedialog.getType());
					ob.add(datedialog.getStrdate());
					cb_enddate.setData(ob);
					cb_enddate.setText(datedialog.getStrname());
				}
				
			}
		});
		
		cb_startdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				DashboardDateDialog datedialog=new DashboardDateDialog(getShell(), null);
				if(datedialog.open()==Dialog.OK){
					java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
					ob.add(datedialog.getType());
					ob.add(datedialog.getStrdate());
					cb_startdate.setData(ob);
					cb_startdate.setText(datedialog.getStrname());
				}
			}
		});
		
		TabItem tabItem_1 = new TabItem(tabFolder, 0);
		tabItem_1.setText("\u7F51\u683C");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_1);
		
		Label lblNewLabel_11 = new Label(composite_1, SWT.NONE);
		lblNewLabel_11.setBounds(10, 10, 62, 18);
		lblNewLabel_11.setText("\u7F51\u683C(G):");
		
		cb_grid = new Combo(composite_1, SWT.READ_ONLY);
		cb_grid.setBounds(46, 38, 505, 20);
		
		Label label_4 = new Label(composite_1, SWT.NONE);
		label_4.setText("\u94BB\u53D6\u9009\u9879:");
		label_4.setBounds(10, 83, 62, 18);
		
		Radio_GoToRecord = new Button(composite_1, SWT.RADIO);
		Radio_GoToRecord.setSelection(true);
		Radio_GoToRecord.setBounds(46, 118, 155, 16);
		Radio_GoToRecord.setText("\u53CC\u51FB\u94BB\u53D6\u4E1A\u52A1\u5BF9\u8C61(B)");
		
		Radio_GoToParent = new Button(composite_1, SWT.RADIO);
		Radio_GoToParent.setText("\u53CC\u51FB\u94BB\u53D6\u4E1A\u52A1\u5BF9\u8C61\u7684\u7236\u5BF9\u8C61(P)");
		Radio_GoToParent.setBounds(46, 160, 208, 16);
		
		TabItem tabItem_2 = new TabItem(tabFolder, 0);
		tabItem_2.setText("\u8FD0\u884C\u65F6\u9009\u9879");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tabItem_2.setControl(composite_3);
		
		Group group_1 = new Group(composite_3, SWT.NONE);
		group_1.setText("");
		group_1.setBounds(10, 10, 615, 170);
		
	    Checked_showoption = new Button(group_1, SWT.CHECK);
		Checked_showoption.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean flag = Checked_showoption.getSelection();
				but_UP.setEnabled(flag);
				but_down.setEnabled(flag);
				but_Add.setEnabled(flag);
				but_Edit.setEnabled(flag);
				but_del.setEnabled(flag);
				table_showoption.setEnabled(flag);
			}
		});
		Checked_showoption.setBounds(10, 0, 104, 16);
		Checked_showoption.setText("\u663E\u793A\u67E5\u770B\u9009\u9879");
		
		but_UP = new Button(group_1, SWT.NONE);
		but_UP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(table_showoption.getSelectionIndex()==-1)return;
				int selectindex=table_showoption.getSelectionIndex();
				if(selectindex-1<0)return ;
				TableItem tabselect=table_showoption.getItem(selectindex);
				TableItem tab=table_showoption.getItem(selectindex-1);
				ViewDef viewdefselect= (ViewDef) tabselect.getData();
				ViewDef viewdef= (ViewDef) tab.getData();
				tabselect.setText(viewdef.get_Name());
				tabselect.setData(viewdef);
				tab.setText(viewdefselect.get_Name());
				tab.setData(viewdefselect);
				tab.setChecked(true);
				tabselect.setChecked(false);
				table_showoption.select(selectindex-1);
			}
		});
		but_UP.setEnabled(false);
		but_UP.setBounds(418, 25, 27, 22);
		but_UP.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.MoveUp16.png"),0x12,0x12));
		
		but_down = new Button(group_1, SWT.NONE);
		but_down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table_showoption.getSelectionIndex()==-1)return;
				int selectindex=table_showoption.getSelectionIndex();
				if(selectindex+1>table_showoption.getItemCount()-1)return ;
				TableItem tabselect=table_showoption.getItem(selectindex);
				TableItem tab=table_showoption.getItem(selectindex+1);
				ViewDef viewdefselect= (ViewDef) tabselect.getData();
				ViewDef viewdef= (ViewDef) tab.getData();
				tabselect.setText(viewdef.get_Name());
				tabselect.setData(viewdef);
				tab.setText(viewdefselect.get_Name());
				tab.setData(viewdefselect);
				tab.setChecked(true);
				tabselect.setChecked(false);
				table_showoption.select(selectindex+1);
			}
		});
		but_down.setEnabled(false);
		but_down.setBounds(418, 46, 27, 22);
		but_down.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.MoveDown16.png"),0x12,0x12));
		
		but_Add = new Button(group_1, SWT.NONE);
		but_Add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onViewDefAdd();
			}
		});
		but_Add.setEnabled(false);
		but_Add.setText("\u6DFB\u52A0");
		but_Add.setBounds(465, 25, 72, 22);
		
		but_Edit = new Button(group_1, SWT.NONE);
		but_Edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onViewDefEdit();
			}
		});
		but_Edit.setEnabled(false);
		but_Edit.setText("\u7F16\u8F91");
		but_Edit.setBounds(465, 46, 72, 22);
		
		but_del = new Button(group_1, SWT.NONE);
		but_del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(table_showoption.getSelectionIndex()==-1)return;
				table_showoption.remove(table_showoption.getSelectionIndex());
				
				
			}
		});
		but_del.setEnabled(false);
		but_del.setText("\u5220\u9664");
		but_del.setBounds(465, 67, 72, 22);
		
		CheckboxTableViewer checkboxTableViewer = CheckboxTableViewer.newCheckList(group_1, SWT.BORDER | SWT.FULL_SELECTION);
		table_showoption = checkboxTableViewer.getTable();
		table_showoption.setEnabled(false);
		table_showoption.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(table_showoption.getSelectionIndex()==-1)return;
				TableItem[] tables=table_showoption.getItems();
				int selectindex=table_showoption.getSelectionIndex();
				for(int i=0;i<tables.length;i++){
					TableItem tab=tables[i];
					if(selectindex==i){
						tab.setChecked(true);
					}else{
						tab.setChecked(false);
					}
				}
				
			}
		});
		table_showoption.setBounds(10, 22, 402, 138);
		
		Group group_2 = new Group(composite_3, SWT.NONE);
		group_2.setText("                                                             ");
		group_2.setBounds(10, 189, 615, 170);
		
		Checked_showdaterange = new Button(group_2, SWT.CHECK);
		Checked_showdaterange.setEnabled(false);
		Checked_showdaterange.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				Checked_allowdatewrite.setEnabled(Checked_showdaterange.getSelection());
				table_showdaterange.setEnabled(Checked_showdaterange.getSelection());
			}
		});
		Checked_showdaterange.setBounds(10, 0, 130, 16);
		Checked_showdaterange.setText("\u663E\u793A\u65E5\u671F\u8303\u56F4\u9009\u9879");
		
		Checked_allowdatewrite = new Button(group_2, SWT.CHECK);
		Checked_allowdatewrite.setEnabled(false);
		Checked_allowdatewrite.setBounds(219, 0, 115, 16);
		Checked_allowdatewrite.setText("\u5141\u8BB8\u65E5\u671F\u8F93\u5165");
		
		Label lblNewLabel_12 = new Label(group_2, SWT.NONE);
		lblNewLabel_12.setBounds(365, 22, 31, 16);
		lblNewLabel_12.setText("\u663E\u793A");
		
		sp_quarter_start = new Spinner(group_2, SWT.BORDER);
		sp_quarter_start.setMaximum(0);
		sp_quarter_start.setMinimum(-12);
		sp_quarter_start.setBounds(402, 19, 41, 21);
		
		Label label_5 = new Label(group_2, SWT.NONE);
		label_5.setText("\u5230");
		label_5.setBounds(448, 22, 15, 16);
		
		sp_quarter_end = new Spinner(group_2, SWT.BORDER);
		sp_quarter_end.setMaximum(12);
		sp_quarter_end.setBounds(466, 19, 41, 21);
		
		Label lblNewLabel_13 = new Label(group_2, SWT.NONE);
		lblNewLabel_13.setBounds(513, 22, 102, 16);
		lblNewLabel_13.setText("\u4ECE\u5F53\u524D\u8D22\u653F\u5B63\u5EA6\u3002");
		
		Label label_6 = new Label(group_2, SWT.NONE);
		label_6.setText("\u663E\u793A");
		label_6.setBounds(365, 53, 31, 16);
		
		sp_year_start = new Spinner(group_2, SWT.BORDER);
		sp_year_start.setMaximum(0);
		sp_year_start.setMinimum(-12);
		sp_year_start.setBounds(402, 48, 41, 21);
		
		Label label_7 = new Label(group_2, SWT.NONE);
		label_7.setText("\u5230");
		label_7.setBounds(448, 53, 15, 16);
		
		sp_year_end = new Spinner(group_2, SWT.BORDER);
		sp_year_end.setMaximum(12);
		sp_year_end.setBounds(466, 48, 41, 21);
		
		Label label_8 = new Label(group_2, SWT.NONE);
		label_8.setText("\u4ECE\u5F53\u524D\u8D22\u653F\u5E74\u5EA6\u3002");
		label_8.setBounds(513, 53, 102, 16);
		
		
		CheckboxTableViewer checkboxTableViewer2 = CheckboxTableViewer.newCheckList(group_2, SWT.BORDER | SWT.FULL_SELECTION);
		table_showdaterange = checkboxTableViewer2.getTable();
		table_showdaterange.setEnabled(false);
		table_showdaterange.setBounds(10, 22, 349, 138);
		
		Checked_ybloading = new Button(composite_3, SWT.CHECK);
		Checked_ybloading.setSelection(true);
		Checked_ybloading.setText("\u652F\u6301\u5F02\u6B65\u52A0\u8F7D");
		Checked_ybloading.setBounds(10, 374, 107, 16);
		
		Checked_showrefresh = new Button(composite_3, SWT.CHECK);
		Checked_showrefresh.setText("\u663E\u793A\u5237\u65B0\u6309\u94AE");
		Checked_showrefresh.setBounds(10, 398, 107, 16);
		
		Label label_9 = new Label(composite_3, SWT.NONE);
		label_9.setText("\u5237\u65B0\u95F4\u9694");
		label_9.setBounds(10, 432, 61, 16);
		
		cb_refreshinterval = new CCombo(composite_3, SWT.BORDER);
		cb_refreshinterval.setEditable(false);
		cb_refreshinterval.setBounds(150, 429, 151, 20);
		cb_refreshinterval.setText("\u65E0");
		
		
	
		//createObjectMenu();
		
		Fillcb_timelength();
		Filltimequantity();
		Fillcb_refreshinterval();
		load();
		return container;
	}
	
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	

	
	/**
	 *  第一次加载数据
	 */
	
	public void load(){
		placeHolderList = m_Library.GetPlaceHolderList(DefRequest.ByCategory(BusinessObjectDef.get_ClassName()));
		IEnumerator it1 = placeHolderList.GetEnumerator();
		while(it1.MoveNext()){
				PlaceHolder holder = (PlaceHolder)it1.get_Current();
				if (holder.HasFlag("AllowDerivation")){
					count++;
				}else if(holder.get_Name().indexOf(".") == -1){
					count++;
	            }
		}	
		
		Calendar date = Calendar.getInstance();
		System.out.println(date.get(Calendar.YEAR));
		
		String strdate=(date.get(Calendar.YEAR))+"-"+(date.get(Calendar.MONTH)+1)+"-"+(date.get(Calendar.DAY_OF_MONTH));
		if(dashboardpartdef == null || "New".equals(type)){
			cb_startdate.setText(strdate);
			java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
			ob.add(false);
			ob.add(date.toString());
			cb_startdate.setData(ob);
			
			cb_enddate.setText(strdate);
			java.util.ArrayList<Object> obs=new java.util.ArrayList<Object>();
			obs.add(false);
			obs.add(strdate);
			cb_enddate.setData(obs);
			return;
		}
		
		txtName.setText(dashboardpartdef.get_Name());
		txtAlias.setText(dashboardpartdef.get_Alias());
		
		if ("Edit".equals(type)) {
			txtName.setText(dashboardpartdef.get_Name());
			txtAlias.setText(dashboardpartdef.get_Alias());
		} else {
			txtName.setText(dashboardpartdef.get_Name() + "  的副本");
			txtAlias.setText(dashboardpartdef.get_Alias() + "  的副本");
		}

		
	
		
		if("GoToRecord".equals(gredpartdef.get_GridAction().toString())){
			Radio_GoToRecord.setSelection(true);
			Radio_GoToParent.setSelection(false);
		}else{
			Radio_GoToParent.setSelection(true);
			Radio_GoToRecord.setSelection(false);
		}
		
		//查询 
		SelectBusLink(gredpartdef.get_BusObName());
		m_collFieldDefHolders=new ArrayList();
		BusinessObjectDef bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), gredpartdef.get_BusObId(), "(Role)"));
		PopulateFieldHolderCollection(bus);
//		Fillcb_DateTimeField("Searching.Fields."+bus.get_Name());
		
		IEnumerator it = m_collFieldDefHolders.GetEnumerator();
		while(it.MoveNext())
		{
			FieldDef fieldef= (FieldDef) it.get_Current();
			 if(gredpartdef!=null&&gredpartdef.get_DefaultDateRangeDef().get_DateTimeField().equals(fieldef.get_QualifiedName())){
				 cb_DateTimeField.add(fieldef.get_Alias());
				 cb_DateTimeField.setData(fieldef.get_Alias(), fieldef.get_QualifiedName());
				 cb_DateTimeField.setText(fieldef.get_Alias());
			 }
		}
		cb_DateTimeField.add("{更多值}");
		cb_DateTimeField.setData("{更多值}",null);
		 
		
		cb_BusObLink.setText(bus.get_Alias());
		cb_BusObLink.setData(bus);
		
		
		Checked_querydate.setSelection(gredpartdef.get_DefaultDateRangeDef().get_ApplyDateRange());
		
		
		IDefinition m_SelectedItem = (IDefinition) m_Library.GetDefinition(DefRequest.ById(gredpartdef.get_QueryGroupScope(), gredpartdef.get_QueryGroupScopeOwner(), gredpartdef.get_QueryGroupLinkedTo(),QueryGroupDef.get_ClassName(), gredpartdef.get_QueryGroupId()));
		txt_searchgroup.setText(m_SelectedItem.get_Alias());
		txt_searchgroup.setData(m_SelectedItem);
		
		Radio_timelength.setSelection(gredpartdef.get_DefaultDateRangeDef().get_IsLengthOfTime());
		Radio_timeRange.setSelection(gredpartdef.get_DefaultDateRangeDef().get_IsSpecificDateRange());
		Radio_timequantity.setSelection(gredpartdef.get_DefaultDateRangeDef().get_IsAmountOfTime());
		if(Checked_querydate.getSelection())showControl(1);
		if(Radio_timelength.getSelection())cb_timelength.setText(LocalizeHelper.GetValue(Type.GetType(DateRange.class.getName()), XmlDateRangeCategory.ToString(gredpartdef.get_DefaultDateRangeDef().get_DateRange())));
		
		cb_last.select(gredpartdef.get_DefaultDateRangeDef().get_IsPast()?0:1);
		sp_number.setSelection(gredpartdef.get_DefaultDateRangeDef().get_AmountOfTimeValue());
		cb_dateortime.setText(LocalizeHelper.GetValue(Type.GetType(AmountOfTime.class.getName()), XmlAmountOfTimeCategory.ToString(gredpartdef.get_DefaultDateRangeDef().get_AmountOfTimeUnit())));
	
		if(StringUtils.IsEmpty(gredpartdef.get_DefaultDateRangeDef().get_StartValue())){
			cb_startdate.setText(strdate);
			java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
			ob.add(false);
			ob.add(strdate);
			cb_startdate.setData(ob);
			cb_startdate.setText(strdate);
			
		}else{
			if(gredpartdef.get_DefaultDateRangeDef().get_IsStartValueFunction()){
				 
				cb_startdate.setText(LocalizeHelper.GetValue(Type.GetType(FunctionCategory.class.getName()), gredpartdef.get_DefaultDateRangeDef().get_StartValue()));
				java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
				ob.add(true);
				ob.add(gredpartdef.get_DefaultDateRangeDef().get_StartValue());
				cb_startdate.setData(ob);
			}else{
				java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
				ob.add(false);
				ob.add(gredpartdef.get_DefaultDateRangeDef().get_StartValue());
				cb_startdate.setData(ob);
				cb_startdate.setText(gredpartdef.get_DefaultDateRangeDef().get_StartValue());
			}
		}
		
		if(StringUtils.IsEmpty(gredpartdef.get_DefaultDateRangeDef().get_EndValue())){
			cb_enddate.setText(strdate);
			java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
			ob.add(false);
			ob.add(strdate);
			cb_enddate.setData(ob);
			cb_enddate.setText(strdate);
			
		}else{
			if(gredpartdef.get_DefaultDateRangeDef().get_IsEndValueFunction()){
				 
				cb_enddate.setText(LocalizeHelper.GetValue(Type.GetType(FunctionCategory.class.getName()), gredpartdef.get_DefaultDateRangeDef().get_EndValue()));
				java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
				ob.add(true);
				ob.add(gredpartdef.get_DefaultDateRangeDef().get_EndValue());
				cb_enddate.setData(ob);
			}else{
				java.util.ArrayList<Object> ob=new java.util.ArrayList<Object>();
				ob.add(false);
				ob.add(gredpartdef.get_DefaultDateRangeDef().get_EndValue());
				cb_enddate.setData(ob);
				cb_enddate.setText(gredpartdef.get_DefaultDateRangeDef().get_EndValue());
			}
		}
		
		//运动时选项
		ViewDef[] viewoptions=multidef.get_ViewOptions();
		for(ViewDef viewdef:viewoptions){
			TableItem tableitem=new TableItem(table_showoption,SWT.NONE);
			tableitem.setChecked(viewdef.get_IsDefault());
			tableitem.setText(viewdef.get_Name());
			tableitem.setData(viewdef);
		}
		
		
		Checked_showoption.setSelection(multidef.get_ViewControlVisible());
		table_showoption.setEnabled(multidef.get_ViewControlVisible());
		but_UP.setEnabled(multidef.get_ViewControlVisible());
		but_down.setEnabled(multidef.get_ViewControlVisible());
		but_Add.setEnabled(multidef.get_ViewControlVisible());
		but_Edit.setEnabled(multidef.get_ViewControlVisible());
		but_del.setEnabled(multidef.get_ViewControlVisible());
		
		Checked_showdaterange.setSelection(multidef.get_DateRangeControlVisible());
		table_showdaterange.setEnabled(multidef.get_DateRangeControlVisible());
		Checked_allowdatewrite.setSelection(multidef.get_DefaultDateRangeDef().get_ApplyDateRange());	
		sp_quarter_start.setSelection(multidef.get_PrecedingFiscalQuarters());
		sp_quarter_end.setSelection(multidef.get_SucceedingFiscalQuarters());
		sp_year_start.setSelection(multidef.get_PrecedingFiscalYears());
		sp_year_end.setSelection(multidef.get_SucceedingFiscalYears());

		Checked_showrefresh.setSelection(dashboardpartdef.get_ShowRefreshButton());
		Checked_ybloading.setSelection(dashboardpartdef.get_SupportAsyncLoading());
		int tim=(int) dashboardpartdef.get_RefreshFrequencyTimeSpan().get_TotalMinutes();
		if(tim!=0)cb_refreshinterval.setText(tim+"分钟");
		
		
	}
	
	
	/**
	 * 创建 业务 菜单
	 */
	private void createObjectMenu(){
		menu = new Menu(cb_BusObLink);
		
		menuUp(0);
		FillBusLink(placeHolderList,0,1);
		menuDown(1);
	}
	
	
	
	/**
	 * 填充业务结象的数据
	 */
	public void FillBusLink(ICollection placeHolderList,int countup,int countdown){
		IEnumerator it = placeHolderList.GetEnumerator();
		
//		MenuItem menuUp = new MenuItem(menu, SWT.NONE);
//		menuUp.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_up.png"),0x12,0x12));
		int next=0;
		while(it.MoveNext()){
				final PlaceHolder holder = (PlaceHolder)it.get_Current();
				if (holder.HasFlag("AllowDerivation")){
					
					if(next>=countup*20&&next<=countdown*20){
						MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
						menuItem.setText(holder.get_Alias() + " " + "Group");
						Menu itemMenu = new Menu(menuItem);
						menuItem.setMenu(itemMenu);
						
						MenuItem menuItem5 = new MenuItem(itemMenu, SWT.NONE);
						menuItem5.setText(holder.get_Alias());
						menuItem5.setData(holder);
						menuItem5.addSelectionListener(new SelectionAdapter(){
							@Override
							public void widgetSelected(SelectionEvent e) {
								MenuItem source = (MenuItem)e.getSource();
								PlaceHolder pl=	(PlaceHolder) source.getData();
								BusinessObjectDef bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), pl.get_Id(), "(Role)"));
								cb_BusObLink.setText(source.getText());
								cb_BusObLink.setData(bus);
								SelectBusLink(pl.get_Name());
								m_collFieldDefHolders=new ArrayList();
								cb_DateTimeField.removeAll();
								PopulateFieldHolderCollection(bus);
								Fillcb_DateTimeField("Searching.Fields."+holder.get_Name());
							}
						});
						
						ICollection familyDefs = m_Library.GetGroupPlaceHolderList(holder.get_Name(), "(Base)", false);
						IEnumerator it2 = familyDefs.GetEnumerator();
						while(it2.MoveNext()){
							final PlaceHolder holder2 = (PlaceHolder)it2.get_Current();
							MenuItem menuItem2 = new MenuItem(itemMenu, SWT.NONE);
							menuItem2.setText(holder2.get_Alias());
							menuItem2.setData(holder2);
							menuItem2.addSelectionListener(new SelectionAdapter(){
								@Override
								public void widgetSelected(SelectionEvent e) {
									MenuItem source = (MenuItem)e.getSource();
									PlaceHolder pl=	(PlaceHolder) source.getData();
									BusinessObjectDef bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), pl.get_Id(), "(Role)"));
									cb_BusObLink.setText(source.getText());
									cb_BusObLink.setData(bus);
									SelectBusLink(pl.get_Name());
									m_collFieldDefHolders=new ArrayList();
									cb_DateTimeField.removeAll();
									PopulateFieldHolderCollection(bus);
									Fillcb_DateTimeField("Searching.Fields."+holder.get_Name());
								}
							});
						}
					}
					next++;
					
				}else if(holder.get_Name().indexOf(".") == -1){
					if(next>=countup*20&&next<=countdown*20){ 
						 MenuItem item4 = new MenuItem(menu, SWT.NONE);
			                item4.setText(holder.get_Alias());
			                
			                item4.setData(holder);
			                item4.addSelectionListener(new SelectionAdapter(){
								public void widgetSelected(SelectionEvent e) {
									MenuItem source = (MenuItem)e.getSource();
									PlaceHolder pl=	(PlaceHolder) source.getData();
									BusinessObjectDef bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), pl.get_Id(), "(Role)"));
									cb_BusObLink.setText(source.getText());
									cb_BusObLink.setData(bus);
									SelectBusLink(pl.get_Name());
									m_collFieldDefHolders=new ArrayList();
									cb_DateTimeField.removeAll();
									PopulateFieldHolderCollection(bus);
									Fillcb_DateTimeField("Searching.Fields."+holder.get_Name());
								}
							});	
					}
					next++;
	            }
			
		}	
	}
	
	/**
	 * 创建  上一条 菜单事件
	 */
	
	public void menuUp(int countup){
		
		menuUp = new MenuItem(menu, SWT.NONE);
		menuUp.setText("上一页");
		menuUp.setData(countup);
		if(countup<=0){
			menuUp.setEnabled(false);
		}else{
			menuUp.setEnabled(true);
		}
		
		
		
		menuUp.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int countup=(Integer) menuUp.getData();
				int countdown=(Integer) menuDown.getData();
				menuUp.setData(countup-1);
				menuDown.setData(countdown-1);
				menu.dispose();
				menu = new Menu(cb_BusObLink);
				menu.setLocation(cb_BusObLink.toDisplay(0,0));
				menu.setVisible(true);
			
				menuUp(countup-1);
				FillBusLink(placeHolderList,countup-1,countdown-1);
				menuDown(countdown-1);
				
			}
			
			
		});
	}

	/**
	 * 创建  下一条 菜单事件
	 */
	
	public void menuDown(int countdown){
		
		menuDown = new MenuItem(menu, SWT.NONE);
		menuDown.setText("下一页");
		menuDown.setData(countdown);
		if(countdown>count/20){
			menuDown.setEnabled(false);
		}else{
			menuDown.setEnabled(true);
		}
		
		
		menuDown.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int countup=(Integer) menuUp.getData();
				int countdown=(Integer) menuDown.getData();
				menu.dispose();
				menu = new Menu(cb_BusObLink);
				menu.setLocation(cb_BusObLink.toDisplay(0,0));
				menu.setVisible(true);
			
				menuUp(countup+1);
				FillBusLink(placeHolderList,countup+1,countdown+1);
				menuDown(countdown+1);
			}
			});
		
	}
	
	
	/**
	 * 选 择业务对象  查出网格 的下拉式数据
	 */
	
	public void SelectBusLink(String buslink){
		cb_grid.removeAll();
		
	//	String buslink=cb_BusObLink.getData(cb_BusObLink.getText()).toString();
		ICollection cb_gridList =m_api.get_LiveDefinitionLibrary().GetPlaceHolderList(DefRequest.ForList(GridDef.get_ClassName()));
		IEnumerator it = cb_gridList.GetEnumerator();
		while(it.MoveNext()){
				PlaceHolder holder = (PlaceHolder)it.get_Current();
				if(StringUtils.IsEmpty(buslink)||holder.get_LinkedTo().equals(buslink)){
					cb_grid.add(holder.get_Alias());
					cb_grid.setData(holder.get_Alias(),holder);
					if(gredpartdef!=null&&gredpartdef.get_GridDefName().equals(holder.get_Name())){
						cb_grid.setText(holder.get_Alias());
					}
				}
		}
		if(cb_grid.getItemCount()>0&&cb_grid.getSelectionIndex()==-1){
			cb_grid.select(0);
		}
	}
	
	/**
	 *  判断 控件是否显示  
	 *  @param type 1=所有的  2=使用时间长度  3=使用日期范围  4=使用时间量
	 */
	
	public void showControl(int type){
		
		if(1==type){
			cb_DateTimeField.setEnabled(Checked_querydate.getSelection());    //日期时间字段
			cb_timelength.setEnabled(Checked_querydate.getSelection());         //日期时间长度
			Radio_timelength.setEnabled(Checked_querydate.getSelection());      //使用日期长度
			Radio_timeRange.setEnabled(Checked_querydate.getSelection());       //使用日期范围
			Radio_timequantity.setEnabled(Checked_querydate.getSelection());    //使用日期量
			cb_startdate.setEnabled(Checked_querydate.getSelection());    //开始日期
			cb_enddate.setEnabled(Checked_querydate.getSelection());		//结束日期
			cb_last.setEnabled(Checked_querydate.getSelection());           
			sp_number.setEnabled(Checked_querydate.getSelection());
			cb_dateortime.setEnabled(Checked_querydate.getSelection());
			if(Checked_querydate.getSelection()){
				if(Radio_timelength.getSelection()){
					showControl(2);
				}
				if(Radio_timeRange.getSelection()){
					showControl(3);
				}
				if(Radio_timequantity.getSelection()){
					showControl(4);
				}
			}
			Checked_showdaterange.setEnabled(Checked_querydate.getSelection());
			
		}else if(2==type){
			cb_timelength.setEnabled(true);   
			setTableDefaultDateRange((DateRange)cb_timelength.getData(cb_timelength.getText().trim()), true);
			cb_startdate.setEnabled(false);  
			cb_enddate.setEnabled(false);		
			cb_last.setEnabled(false);           
			sp_number.setEnabled(false);
			cb_dateortime.setEnabled(false);
		}else if(3==type){
			cb_timelength.setEnabled(false);
			setTableDefaultDateRange((DateRange)cb_timelength.getData(cb_timelength.getText().trim()), false);
			cb_startdate.setEnabled(true);  
			cb_enddate.setEnabled(true);		
			cb_last.setEnabled(false);           
			sp_number.setEnabled(false);
			cb_dateortime.setEnabled(false);
		}else if(4==type){
			cb_timelength.setEnabled(false);
			setTableDefaultDateRange((DateRange)cb_timelength.getData(cb_timelength.getText().trim()), false);
			cb_startdate.setEnabled(false); 
			cb_enddate.setEnabled(false);		
			cb_last.setEnabled(true);           
			sp_number.setEnabled(true);
			cb_dateortime.setEnabled(true);
		}
	}
	
	
	/**
	 *  给使用时间长度 填充数据    和 表格日期范围 数据
	 */
	public void Fillcb_timelength(){
		
		for(DateRange daterange:DateRange.values()){
			
			if(!daterange.equals(DateRange.None)){
				String name=LocalizeHelper.GetValue(Type.GetType(DateRange.class.getName()), XmlDateRangeCategory.ToString(daterange));
				cb_timelength.add(name);
				cb_timelength.setData(name, daterange);
				
				if(gredpartdef!=null){
					if(daterange.equals(gredpartdef.get_DefaultDateRangeDef().get_DateRange())){
						cb_timelength.setText(name);
					}
				}
				
				ViewDef item = null;
				item = new ViewDef((MultiViewGridPartDef)dashboardpartdef);
                item.set_DateRange(daterange);
				
				TableItem tableitem=new TableItem(table_showdaterange,SWT.NONE);
				tableitem.setText(name);
				tableitem.setData(item);
				if(multidef!=null){
					for(ViewDef viewdef:multidef.get_DateRangeOptions()){
						if(daterange.equals(viewdef.get_DateRange())){
							tableitem.setChecked(true);
						}
						
					}
				}
					
			}
			
		}
		
		if(cb_timelength.getItemCount()>0 && cb_timelength.getData(cb_timelength.getText().trim()) == null){
			cb_timelength.select(0);
		}
	}
	
	/**
	 * 给使用时间量 填充数据
	 */
	
	public void Filltimequantity(){
		
		cb_last.add("最后一个");
		cb_last.setData("最后一个","Last");
		cb_last.add("下一页");
		cb_last.setData("下一页","Next");
		cb_last.select(0);
		
		for(AmountOfTime atime:AmountOfTime.values()){
			
			if(!atime.equals(AmountOfTime.None)){
				String name=LocalizeHelper.GetValue(Type.GetType(AmountOfTime.class.getName()), XmlAmountOfTimeCategory.ToString(atime));
				cb_dateortime.add(name);
				cb_dateortime.setData(name, atime);
			}
		}
		
		if(cb_dateortime.getItemCount()>0){
			cb_dateortime.select(0);
		}
	}
	
	/**
	 * 给日期时间字段填充数据 
	 */
	public void Fillcb_DateTimeField(String strBusobname){
		ICollection strbuss=m_api.get_SettingsService().get_Mru().GetItems(strBusobname);
		IEnumerator it = strbuss.GetEnumerator();
		while(it.MoveNext()){
				String str = (String) it.get_Current();
				 int num = FindMatchingFieldDefHolder(str, m_collFieldDefHolders, null);
				 if(num!=-1){
					 FieldDef fieldef= (FieldDef) m_collFieldDefHolders.get_Item(num);
					 cb_DateTimeField.add(fieldef.get_Alias());
					 cb_DateTimeField.setData(fieldef.get_Alias(),str);
					 if(gredpartdef!=null&&gredpartdef.get_DefaultDateRangeDef().get_DateTimeField().equals(fieldef.get_QualifiedName())){
						 cb_DateTimeField.setText(fieldef.get_Alias());
					 }
				 }
		}
		cb_DateTimeField.add("{更多值}");
		cb_DateTimeField.setData("{更多值}",null);
		
	}
	
	/**
	 * 取出时间数据
	 * @param targetBusObDef
	 */
	 private void PopulateFieldHolderCollection(BusinessObjectDef targetBusObDef ){
		 if(targetBusObDef!=null){
			 ICollection combinedGroupFieldDefs = null;
	         if (targetBusObDef.get_ParentOfGroup()){
	                 combinedGroupFieldDefs = m_Library.GetCombinedGroupFieldDefs(targetBusObDef.get_Name());
	         }else{
	        	  combinedGroupFieldDefs = targetBusObDef.get_FieldDefs();
	         }
	         
	         ICollection is3 = null;
             if (targetBusObDef.get_ParentOfGroup()){
            	   is3 = m_Library.GetGroupPlaceHolderList(targetBusObDef.get_Name(), "(Role)", false);
             }
             if(combinedGroupFieldDefs!=null){
            	 IEnumerator it = combinedGroupFieldDefs.GetEnumerator();
      			 while(it.MoveNext()){
      				FieldDef def=(FieldDef) it.get_Current();
      				if(!def.get_IsSearchable()){
      					continue;
      				}
      				
      				if(targetBusObDef.get_ParentOfGroup()){
      					
      					if(is3!=null){
      						IEnumerator it1 = is3.GetEnumerator();
      		      			while(it1.MoveNext()){
      		      				PlaceHolder holder=(PlaceHolder) it1.get_Current();
      		      				FieldDef field = this.m_Library.GetBusinessObjectDef(holder.get_Name()).GetField(def.get_Name());
	                            if ((field != null) && (field.get_ValidationRule()!= null)){
	                               def.SetCargo("ValidationHint", field);
	                               break;
	                            }
      		      			}
      					}
      				}
      				if (def.get_IsDateTime()){
      					m_collFieldDefHolders.Add(def);
      	            }	
      			 }
             }
		 }
     }
	
	 /**
	  * 查出历史数据
	  * @param strFieldNameWithoutPurposeOrLink
	  * @param collHolders
	  * @param lstIncludedFieldTypes
	  * @return
	  */
	 private int FindMatchingFieldDefHolder(String strFieldNameWithoutPurposeOrLink, IList collHolders, IList lstIncludedFieldTypes)
     {
         int num = 0;
         IEnumerator it = collHolders.GetEnumerator();
         while(it.MoveNext()){
        	 FieldDef fieldef=(FieldDef) it.get_Current(); 
        	 String strilink=Field.BuildBusObField(fieldef.get_BusObDef().get_Name(), fieldef.get_Name(), "","");
        	 if(fieldef!=null&&strFieldNameWithoutPurposeOrLink.equals(strilink)){
        		 return num;
        	 }
        	 num++;
         }
         
         return -1;
     }
	 
	 /**
	  *  填充  刷新时间间隔  
	  */
	 public void Fillcb_refreshinterval(){
		 int[] times=new int[]{ 1, 2, 3, 5, 10, 15, 20, 30, 60, 90, 120};
		 cb_refreshinterval.add("无");
		 cb_refreshinterval.setData("无", 0);
		 for(int i:times){
			 cb_refreshinterval.add(i+"分钟");
			 cb_refreshinterval.setData(i+"分钟", i);
		 }
	 }
	 
	 
	 private void onViewDefAdd(){
		View_RunTimeSelect view = new View_RunTimeSelect(getShell(), multidef, null);
		if(view.open() == 0){
			ViewDef viewDef = view.getViewDef();
			TableItem item = new TableItem(table_showoption, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
			item.setText(viewDef.get_Name()); 
			item.setData(viewDef);
		}
	 }
	 
	 private void onViewDefEdit(){
		 int index = table_showoption.getSelectionIndex();
		 if(index == -1) return;
		 TableItem item  = table_showoption.getSelection()[0];
		 ViewDef selViewDef = (ViewDef)item.getData();
		 
		 View_RunTimeSelect view = new View_RunTimeSelect(getShell(), multidef, selViewDef);
			if(view.open() == 0){
				ViewDef viewDef = view.getViewDef();
				item.setText(viewDef.get_Name()); 
				item.setData(viewDef);
			}
		 
	 }
	 
	 
	 private void setTableDefaultDateRange(DateRange dateRange, boolean flag)
	 {
		 for(TableItem item : table_showdaterange.getItems())
		 {
			 ViewDef def = (ViewDef)item.getData();
			 if(dateRange == def.get_DateRange())
			 {
				 if(flag)
				 {
					 item.setChecked(true);
					 multidef.get_DefaultDateRangeDef().set_DateRange(dateRange);
				 }else
				 {
					 item.setChecked(false);
					 multidef.get_DefaultDateRangeDef().set_DateRange(null);
				 }
				 
			 }
		 }
	 }
	 
	 
	 /**
	  *  保存  
	  */
	  protected void okPressed() {
			if(general.savavalidation(txtName, txtAlias, type)){
				
				
				if(StringUtils.IsEmpty(cb_BusObLink.getText())){
					
					MessageDialog.openInformation(getParentShell(), "提示",
							"请选择业务对象. ");
					cb_BusObLink.forceFocus();
					return;
				}
				
				if(StringUtils.IsEmpty(txt_searchgroup.getText())){
					MessageDialog.openInformation(getParentShell(), "提示",
							"请选择搜索群组. ");
					txt_searchgroup.forceFocus();
					return;
				}
				
				if(StringUtils.IsEmpty(cb_grid.getText())){
					MessageDialog.openInformation(getParentShell(), "提示",
							"请选择网络格. ");
					cb_grid.forceFocus();
					return;
				}
				
				if(Checked_querydate.getSelection() && cb_DateTimeField.getData(cb_DateTimeField.getText().trim()) == null)
				{
					MessageDialog.openInformation(getParentShell(), "提示",
							"请选择日期时间字段. ");
					cb_DateTimeField.forceFocus();
					return;
				}
				//查询 网络
				GridPartDef grpartdef=(GridPartDef) dashboardpartdef;
				
				BusinessObjectDef bus=(BusinessObjectDef) cb_BusObLink.getData();
				
				grpartdef.set_BusObId(bus.get_Id());
				grpartdef.set_BusObName(bus.get_Name());
				
				IDefinition definition=(IDefinition) txt_searchgroup.getData();
				grpartdef.set_QueryGroupId(definition.get_Id());
				grpartdef.set_QueryGroupLinkedTo(definition.get_LinkedTo());
				grpartdef.set_QueryGroupName(definition.get_Name());
				grpartdef.set_QueryGroupScope(definition.get_Scope());
				grpartdef.set_QueryGroupScopeOwner(definition.get_ScopeOwner());
			
				
				PlaceHolder holder=(PlaceHolder) cb_grid.getData(cb_grid.getText());
				grpartdef.set_GridDefId(holder.get_Id());
				grpartdef.set_GridDefName(holder.get_Name());
				
				grpartdef.get_DefaultDateRangeDef().set_ApplyDateRange(Checked_querydate.getSelection());
				grpartdef.get_DefaultDateRangeDef().set_IsLengthOfTime(Radio_timelength.getSelection());
				grpartdef.get_DefaultDateRangeDef().set_IsSpecificDateRange(Radio_timeRange.getSelection());
				grpartdef.get_DefaultDateRangeDef().set_IsAmountOfTime(Radio_timequantity.getSelection());
				if(!StringUtils.IsEmpty(cb_DateTimeField.getText().trim())){
					String str=cb_DateTimeField.getData(cb_DateTimeField.getText().trim()).toString();
					grpartdef.get_DefaultDateRangeDef().set_DateTimeField(str);
				}
				
				if(!StringUtils.IsEmpty(cb_timelength.getText())){
					DateRange daterange=(DateRange) cb_timelength.getData(cb_timelength.getText());
					grpartdef.get_DefaultDateRangeDef().set_DateRange(daterange);
				}
				
				
				if(cb_last.getSelectionIndex()==0){
					grpartdef.get_DefaultDateRangeDef().set_IsPast(true);
				}else{
					grpartdef.get_DefaultDateRangeDef().set_IsPast(false);
				}
				
				grpartdef.get_DefaultDateRangeDef().set_AmountOfTimeValue(sp_number.getSelection());
				
				AmountOfTime atime=(AmountOfTime) cb_dateortime.getData(cb_dateortime.getItem(cb_dateortime.getSelectionIndex()));
				grpartdef.get_DefaultDateRangeDef().set_AmountOfTimeUnit(atime);
				
				if(Radio_timeRange.getSelection()){
					java.util.ArrayList<Object> obstart=(java.util.ArrayList<Object>) cb_startdate.getData();
					grpartdef.get_DefaultDateRangeDef().set_IsStartValueFunction((Boolean) obstart.get(0));
					grpartdef.get_DefaultDateRangeDef().set_StartValue((String) obstart.get(1));
					
					java.util.ArrayList<Object> obend=(java.util.ArrayList<Object>) cb_enddate.getData();
					grpartdef.get_DefaultDateRangeDef().set_IsEndValueFunction((Boolean) obend.get(0));
					grpartdef.get_DefaultDateRangeDef().set_EndValue((String) obend.get(1));
				}
				
				
				
				if(Radio_GoToRecord.getSelection()){
					grpartdef.set_GridAction(GridAction.GoToRecord);
				}else{
					grpartdef.set_GridAction(GridAction.GoToParent);
				}
				
				
				//运行时选项
				multidef.ClearViewDefs();
				TableItem[] tableitems=table_showoption.getItems();
				for(int i=0;i<tableitems.length;i++){
					TableItem tableitem=tableitems[i];
					ViewDef def = (ViewDef)tableitem.getData();
					def.set_IsDefault(tableitem.getChecked());
					multidef.AddViewDef(def);
				}
				
				
				//查询 网络
				MultiViewGridPartDef multdef=(MultiViewGridPartDef) dashboardpartdef;
				
				multidef.set_ViewControlVisible(Checked_showoption.getSelection());
				multdef.ClearDateRangeDefs();
				multdef.set_DateRangeControlVisible(Checked_showdaterange.getSelection());
		//		multidef.get_DefaultDateRangeDef().set_ApplyDateRange(Checked_allowdatewrite.getSelection());
				multidef.set_PrecedingFiscalQuarters(sp_quarter_start.getSelection());
				multidef.set_SucceedingFiscalQuarters(sp_quarter_end.getSelection());
				multidef.set_PrecedingFiscalYears(sp_year_start.getSelection());
				multidef.set_SucceedingFiscalYears(sp_year_end.getSelection());
				
				TableItem[] tableitems1=table_showdaterange.getItems();
				for(int i=0;i<tableitems1.length;i++){
					TableItem tableitem=tableitems1[i];
					if(tableitem.getChecked()){
						multidef.AddDateRangeDef((ViewDef)tableitem.getData());
					}
				}
				
				dashboardpartdef.set_ShowRefreshButton(Checked_showrefresh.getSelection());
				dashboardpartdef.set_SupportAsyncLoading(Checked_ybloading.getSelection());
				if(!StringUtils.IsEmpty(cb_refreshinterval.getText())){
					int timeint=(Integer) cb_refreshinterval.getData(cb_refreshinterval.getText());
					dashboardpartdef.set_RefreshFrequency(timeint);
				}
				
//				if ("New".equals(type)) {
//					m_Library.UpdateDefinition(dashboardpartdef, true);
//				} else if ("Edit".equals(type)) {
//					m_Library.UpdateDefinition(dashboardpartdef, false);
//				} else if("Copy".equals(type)){
//					dashboardpartdef.set_Id(m_Def.get_Id());
//					m_Library.UpdateDefinition(dashboardpartdef, true);
//				}
				super.okPressed();
			}
			
	   }
		
	 
}
