package core.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import system.StringSplitOptions;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ObjectTopologicalDiagramPartDef;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.PlaceHolder;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.RelationshipDef;
import Siteview.Windows.Forms.ScopeUtil;
import core.ui.DashBoardPartCenterForm;


public class ObjectRelationTopo extends Dialog{

	private ObjectTopologicalDiagramPartDef ObjectTopologicalDiagramPartDef;
	private Button button_CoreOfObject;
	private CCombo combo_SelectObject;
	private Combo cb_MainObTopText;
	private Combo cb_MainObTipText;
	private Table clb_Relationship;
	private Table lb_TopoRelationship;
	
	private ArrayList m_TopoRelationshipList = new ArrayList();
	private ArrayList m_RelationshipTempList = new ArrayList();
	private BusinessObjectDef m_SelectedObDef;
	
	
	private Menu menu;
	private MenuItem menuUp;  //上一条
	private MenuItem menuDown;  //下一条
	private int count=0;  
	private ICollection placeHolderList;  //业务的 菜单数据
	
//	
//	/**
//	 * @wbp.parser.constructor
//	 */
//	protected ObjectRelationTopo(Shell parentShell) {
//		super(parentShell);
//	}
//	
//	public ObjectRelationTopo(Shell parentShell, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
//		super(parentShell, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type);
//		this.objectTopoPartDef = (ObjectTopologicalDiagramPartDef)dashboardPartDef;
//		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
//	}
//	
//	
//	
//	@Override
//	protected void configureShell(Shell newShell) {
//		super.configureShell(newShell);
//	}
//
//	@Override
//	protected Control createDialogArea(Composite parent) {
////		-----------------------------测试代码-------------------------------------
////		final TableItem tableitem=new TableItem(table,SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
////		tableitem.setText("test1");
////		tableitem.setData(false);
////		TableItem tableItem = new TableItem(table, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
////		tableItem.setText("test2");
////		tableItem.setData(false);
////		
////		Table list = new Table(group, SWT.BORDER);
////		list.setBackground(new Color(null, 135, 206, 250));
////		list.setBounds(10, 249, 536, 163);
////		
////		TableItem relation1 = new TableItem(list, SWT.NONE);
////		relation1.setText("relation1");
////		--------------------------------------------------------------------
//		return super.createDialogArea(parent);
//	}
//	
//	

//
//	@Override
//	protected void createViewModel(TabFolder tabFolder) {
//		
//		
////		createObjectMenu();
//	}
//	
//	@Override
//	public String getPartTypeName() {
//		return "对象关系拓扑图";
//	}
//	
//	@Override
//	protected void fillProperties() {
//		String tempstr = this.objectTopoPartDef.get_TopoRelationShipListString();
//        String[] templist = tempstr.split("&", StringSplitOptions.RemoveEmptyEntries);
//        
//        BusinessObjectDef topoObjects = (BusinessObjectDef)getM_Library().GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), objectTopoPartDef.get_BusObId(), "(Role)"));
//        combo_SelectObject.setText(topoObjects.get_Alias());
//        combo_SelectObject.setData(topoObjects.get_Alias(), topoObjects);
//        IEnumerator it = topoObjects.get_FieldDefs().GetEnumerator();
//        while(it.MoveNext()){
//        	FieldDef field = (FieldDef)it.get_Current();
//        	combo_ShowText.add(field.get_Alias());
//        	combo_ShowText.setData(field.get_Alias(), field.get_Name());
//        	if(objectTopoPartDef.get_MainBusObTopText().equals(field.get_Name())) combo_ShowText.setText(field.get_Alias());
//        	combo_TipText.add(field.get_Alias());
//        	combo_TipText.setData(field.get_Alias(), field.get_Name());
//        	if(objectTopoPartDef.get_MainBusObTipText().equals(field.get_Name())) combo_TipText.setText(field.get_Alias());
//        }
//        
//        IEnumerator it_Select = topoObjects.get_RelationshipDefs().GetEnumerator();
//        while(it_Select.MoveNext()){
//        	RelationshipDef relDef = (RelationshipDef)it_Select.get_Current();
//        	TableItem tableitem=new TableItem(table_SelectTopo,SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
//    		tableitem.setText(relDef.get_Alias());
//    		tableitem.setData(relDef);
//        }
//		
//        
//        
//	}
//	
//	private void createObjectMenu(){
//		menu = new Menu(combo_SelectObject);
//		
//		
//		ICollection placeHolderList = getM_Library().GetPlaceHolderList(DefRequest.ByCategory(BusinessObjectDef.get_ClassName()));
//		IEnumerator it = placeHolderList.GetEnumerator();
//		
////		MenuItem menuUp = new MenuItem(menu, SWT.NONE);
////		menuUp.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_up.png"),0x12,0x12));
//		
//		while(it.MoveNext()){
//			PlaceHolder holder = (PlaceHolder)it.get_Current();
//			if (holder.HasFlag("AllowDerivation")){
//				MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
//				menuItem.setText(holder.get_Alias() + " " + "Group");
//				Menu itemMenu = new Menu(menuItem);
//				menuItem.setMenu(itemMenu);
//				
//				ICollection familyDefs = getM_Library().GetGroupPlaceHolderList(holder.get_Name(), "(Base)", false);
//				IEnumerator it2 = familyDefs.GetEnumerator();
//				while(it2.MoveNext()){
//					PlaceHolder holder2 = (PlaceHolder)it2.get_Current();
//					MenuItem menuItem2 = new MenuItem(itemMenu, SWT.NONE);
//					menuItem2.setText(holder2.get_Alias());
//					menuItem2.setData(holder2.get_Alias(), holder2.get_Id());
//					menuItem2.addSelectionListener(new SelectionAdapter(){
//						@Override
//						public void widgetSelected(SelectionEvent e) {
//							MenuItem source = (MenuItem)e.getSource();
//							combo_SelectObject.setText(source.getText());
//							combo_SelectObject.setData(source.getText(), source.getData(source.getText()));
//							fillSelectObject((String)source.getData(source.getText()));
//						}
//					});
//				}
//			}else if(holder.get_Name().indexOf(".") == -1){
//                MenuItem item4 = new MenuItem(menu, SWT.NONE);
//                item4.setText(holder.get_Alias());
//                item4.setData(holder.get_Alias(), holder.get_Id());
//                item4.addSelectionListener(new SelectionAdapter(){
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						MenuItem source = (MenuItem)e.getSource();
//						combo_SelectObject.setText(source.getText());
//						combo_SelectObject.setData(source.getText(), source.getData(source.getText()));
//						fillSelectObject((String)source.getData(source.getText()));
//					}
//				});
//            }
//		}
//		
//	}
//	
//	private void fillSelectObject(String id){
//		table_SelectTopo.removeAll();
//		table_ShowTopo.removeAll();
//		
//		BusinessObjectDef topoObjects = (BusinessObjectDef)getM_Library().GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), id, "(Role)"));
//		 IEnumerator it_Select = topoObjects.get_RelationshipDefs().GetEnumerator();
//	        while(it_Select.MoveNext()){
//	        	RelationshipDef relDef = (RelationshipDef)it_Select.get_Current();
//	        	TableItem tableitem=new TableItem(table_SelectTopo,SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
//	    		tableitem.setText(relDef.get_Alias());
//	    		tableitem.setData(relDef);
//	        }
//	}
//	
//	
	
	private Text txtName;
	private Text txtAlias;

	private Generalmethods general;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

//	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private String type = "New"; // 标志位 1=添加 2=编辑 3=复制
	
	public ObjectRelationTopo(Shell parentShell, DashboardPartDef dashboardpartdef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr, String type) {
		super(parentShell);
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.ObjectTopologicalDiagramPartDef = (ObjectTopologicalDiagramPartDef)dashboardpartdef;
		this.m_Def = m_Def;
		this.type = type;
		this.linkstr = linkstr;
		general=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, m_Def, linkstr);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 700);
		newShell.setLocation(300, 100);
		newShell.setText(type + "  对象关系拓扑图");
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
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 621);
		fd_tabFolder.top = new FormAttachment(txtAlias, 21);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -40);
		tabFolder.setLayoutData(fd_tabFolder);
		
		general.tabFolder_1(tabFolder);
		general.tabFolder_2(tabFolder);
		
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u62D3\u6251\u5BF9\u8C61\u9009\u62E9");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		
		button_CoreOfObject = new Button(composite, SWT.RADIO);
		button_CoreOfObject.setSelection(true);
		button_CoreOfObject.setBounds(20, 20, 179, 18);
		button_CoreOfObject.setText("\u4EE5\u4E1A\u52A1\u5BF9\u8C61\u4E3A\u6838\u5FC3\u8FDB\u884C\u62D3\u6251");
		
		Group group = new Group(composite, SWT.NONE);
		group.setText("\u62D3\u6251\u5BF9\u8C61\u9009\u62E9");
		group.setBounds(20, 44, 566, 390);
		
		Label lblNewLabel_6 = new Label(group, SWT.NONE);
		lblNewLabel_6.setBounds(20, 20, 135, 18);
		lblNewLabel_6.setText("\u9009\u62E9\u8981\u62D3\u6251\u7684\u4E1A\u52A1\u5BF9\u8C61:");
		
		combo_SelectObject = new CCombo(group, SWT.NONE|SWT.BORDER);
		combo_SelectObject.setBounds(180, 17, 353, 20);
		combo_SelectObject.setEditable(false);
		combo_SelectObject.setVisibleItemCount(0);
		combo_SelectObject.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				if(e.button == 1){
					createObjectMenu();
					menu.setLocation(combo_SelectObject.toDisplay(0,0));
					menu.setVisible(true);
				}
			}
		});
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setText("\u5BF9\u8C61\u8282\u70B9\u663E\u793A\u6587\u5B57:");
		label_3.setBounds(10, 54, 111, 18);
		
		cb_MainObTopText = new Combo(group, SWT.READ_ONLY | SWT.BORDER);
		cb_MainObTopText.setBounds(127, 51, 135, 20);
		
		Label lbltip = new Label(group, SWT.NONE);
		lbltip.setText("\u5BF9\u8C61\u8282\u70B9Tip\u6587\u5B57:");
		lbltip.setBounds(295, 54, 111, 18);
		
		cb_MainObTipText = new Combo(group, SWT.READ_ONLY | SWT.BORDER);
		cb_MainObTipText.setBounds(412, 52, 135, 20);
		
		Label label_4 = new Label(group, SWT.NONE);
		label_4.setText("\u9009\u62E9\u8981\u62D3\u6251\u7684\u5173\u7CFB:");
		label_4.setBounds(10, 84, 111, 18);
		
//		final CheckboxTableViewer checkboxTableViewer = CheckboxTableViewer.newCheckList(group, SWT.BORDER | SWT.CHECK);
		
		clb_Relationship = new Table(group, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
//		clb_Relationship.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				if(clb_Relationship.getSelection().length > 0)
//				{
//					TableItem tab = clb_Relationship.getSelection()[0];
//					RelationshipDef relDef = (RelationshipDef)tab.getData();
//					if((Boolean)tab.getData("ischecked") != tab.getChecked())
//					{
////							clb_Relationship_ItemCheck(relDef, tab);
//					}
//				}
//			}
//		});
		
		clb_Relationship.addListener(SWT.MouseDown, new Listener(){
			@Override
			public void handleEvent(Event event) {
				TableItem tab = clb_Relationship.getItem(new Point(event.x, event.y));
				if(tab != null)
				{
					clb_Relationship.setSelection(tab);
					if(clb_Relationship.getSelection().length > 0)
					{
						if((Boolean)tab.getData("ischecked") != tab.getChecked())
						{
							RelationshipDef relDef = (RelationshipDef)tab.getData();
							clb_Relationship_ItemCheck(relDef, tab);
						}
					}
				}
			}
		});
		clb_Relationship.setBounds(10, 108, 536, 127);
		
		lb_TopoRelationship = new Table(group, SWT.BORDER);
		lb_TopoRelationship.setBackground(new Color(null, 135, 206, 250));
		lb_TopoRelationship.setBounds(10, 249, 536, 130);
		
		createObjectMenu();
		load();
		return container;
	}
	
	/**
	 *  第一次加载数据
	 */
	
	public void load(){
		
		if(ObjectTopologicalDiagramPartDef == null || "New".equals(type)){
			return;
		}
		
		txtName.setText(ObjectTopologicalDiagramPartDef.get_Name());
		txtAlias.setText(ObjectTopologicalDiagramPartDef.get_Alias());
		
		if ("Edit".equals(type)) {
			txtName.setText(ObjectTopologicalDiagramPartDef.get_Name());
			txtAlias.setText(ObjectTopologicalDiagramPartDef.get_Alias());
		} else {
			txtName.setText(ObjectTopologicalDiagramPartDef.get_Name() + "  的副本");
			txtAlias.setText(ObjectTopologicalDiagramPartDef.get_Alias() + "  的副本");
		}

		fillProperties();
		
	}
	
	
	protected void fillProperties() {
        
        String tempstr = this.ObjectTopologicalDiagramPartDef.get_TopoRelationShipListString();
        String[] templist = tempstr.split("&");
        this.m_TopoRelationshipList.AddRange(templist);
        if (this.m_TopoRelationshipList != null && this.m_TopoRelationshipList.get_Count() > 0)
        {
        	BusinessObjectDef topoObjects = (BusinessObjectDef)m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), this.ObjectTopologicalDiagramPartDef.get_BusObId(), "(Role)"));
            combo_SelectObject.setText(topoObjects.get_Alias());
        	this.m_SelectGroupOrNonGroupBusObCtrl_BusObNameChanged(topoObjects);
            String mainTopText = this.ObjectTopologicalDiagramPartDef.get_MainBusObTopText();
            String mainTipText = this.ObjectTopologicalDiagramPartDef.get_MainBusObTipText();
            for (int mainTextCount = 0; mainTextCount < this.cb_MainObTipText.getItemCount(); mainTextCount++)
            {
                String compareString = (String)cb_MainObTopText.getData(cb_MainObTopText.getItem(mainTextCount));
                if (mainTopText.equals(compareString))
                {
                    cb_MainObTopText.select(mainTextCount);
                }
                if (mainTipText.equals(compareString))
                {
                    cb_MainObTipText.select(mainTextCount);
                }
            }
            this.m_RelationshipTempList.AddRange(templist);
            ArrayList arrExistRelationship = new ArrayList();
            for (int k = 0; k < this.m_TopoRelationshipList.get_Count(); k++)
            {
                arrExistRelationship.Add(this.m_TopoRelationshipList.get_Item(k).toString().split("\\|", StringSplitOptions.None)[0]);
            }
            IEnumerator it = m_SelectedObDef.get_RelationshipDefs().GetEnumerator();
            int i = 0;
            while (it.MoveNext())
            {
                RelationshipDef relDef = (RelationshipDef)it.get_Current();
                for (int j = 0; j < arrExistRelationship.get_Count(); j++)
                {
                    if (relDef.get_TargetName().equals(arrExistRelationship.get_Item(j).toString()))
                    {
                        clb_Relationship.getItem(i).setChecked(true);
                        clb_Relationship.getItem(i).setData("ischecked", true);
                        String[] temparr = this.m_TopoRelationshipList.get_Item(j).toString().split("\\|", StringSplitOptions.None);
                        TableItem tableitem=new TableItem(lb_TopoRelationship,SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION );
                	    tableitem.setText("拓扑关系：" + temparr[0] + "    拓扑显示字段：" + temparr[1] + "    拓扑Tip字段：" + temparr[2]);
//                    	tableitem.setData(relationDate);
                    }
                }
                i++;
            }
        }
//            else
//            {
//                this.m_BLoading = false;
//            }
        
	}
	

	private void createObjectMenu(){
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
		menu = new Menu(combo_SelectObject);
		menuUp(0);
		FillBusLink(placeHolderList,0,1);
		menuDown(1);
	}
	
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
				menu = new Menu(combo_SelectObject);
				menu.setLocation(combo_SelectObject.toDisplay(0,0));
				menu.setVisible(true);
			
				menuUp(countup-1);
				FillBusLink(placeHolderList,countup-1,countdown-1);
				menuDown(countdown-1);
			}
		});
	}
	
	
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
				menu = new Menu(combo_SelectObject);
				menu.setLocation(combo_SelectObject.toDisplay(0,0));
				menu.setVisible(true);
			
				menuUp(countup+1);
				FillBusLink(placeHolderList,countup+1,countdown+1);
				menuDown(countdown+1);
			}
			});
	}
	
	
	public void FillBusLink(ICollection placeHolderList,int countup,int countdown){
		IEnumerator it = placeHolderList.GetEnumerator();
		
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
								combo_SelectObject.setText(source.getText());
								combo_SelectObject.setData(bus);
								m_SelectGroupOrNonGroupBusObCtrl_BusObNameChanged(bus);
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
									combo_SelectObject.setText(source.getText());
									combo_SelectObject.setData(bus);
									m_SelectGroupOrNonGroupBusObCtrl_BusObNameChanged(bus);
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
									combo_SelectObject.setText(source.getText());
									combo_SelectObject.setData(bus);
									m_SelectGroupOrNonGroupBusObCtrl_BusObNameChanged(bus);
								}
							});	
					}
					next++;
	            }
			
		}	
	}
	


	private void m_SelectGroupOrNonGroupBusObCtrl_BusObNameChanged(BusinessObjectDef topoObjects){
		cb_MainObTopText.removeAll();
		cb_MainObTipText.removeAll();
		clb_Relationship.removeAll();
        lb_TopoRelationship.removeAll();
        m_RelationshipTempList.Clear();
		
        m_SelectedObDef = topoObjects;
        
		IEnumerator it_Text = topoObjects.get_FieldDefs().GetEnumerator();
		while(it_Text.MoveNext()){
			FieldDef field = (FieldDef)it_Text.get_Current();
			cb_MainObTopText.add(field.get_Alias());
			cb_MainObTipText.add(field.get_Alias());
			cb_MainObTopText.setData(field.get_Alias(), field.get_Name());
			cb_MainObTipText.setData(field.get_Alias(), field.get_Name());
		}
		
		IEnumerator it_Select = topoObjects.get_RelationshipDefs().GetEnumerator();
	    while(it_Select.MoveNext()){
	    	RelationshipDef relDef = (RelationshipDef)it_Select.get_Current();
	        TableItem tableitem=new TableItem(clb_Relationship,SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
	    	tableitem.setText(relDef.get_Alias());
	    	tableitem.setData(relDef);
	    	tableitem.setData("ischecked", false);
	    }
	}

	
	
	private void clb_Relationship_ItemCheck(RelationshipDef relDef, TableItem item)
    {
        if (this.m_SelectedObDef != null)
        {
            String targetBusinessObjectName = relDef.get_TargetName();
            if (!item.getChecked())
            {
                for (int i = 0; i < this.lb_TopoRelationship.getItemCount(); i++)
                {
                    if (this.lb_TopoRelationship.getItem(i).getText().indexOf(targetBusinessObjectName) > -1)
                    {
                        this.lb_TopoRelationship.remove(i);
                        this.m_RelationshipTempList.RemoveAt(i);
                    }
                }
                item.setData("ischecked", false);
            }
            else
            {
                ICollection arrFieldDefs = m_Library.GetBusinessObjectDef(targetBusinessObjectName).get_FieldDefs();
                ShowTextChoose showTextChoose = new ShowTextChoose(this.getShell(), arrFieldDefs);
        		if(showTextChoose.open() == 0 && arrFieldDefs.get_Count() > 0){
        			TableItem fieldItem=new TableItem(lb_TopoRelationship,SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION );
                    fieldItem.setText("拓扑关系：" + targetBusinessObjectName + "    拓扑显示字段：" + showTextChoose.getRelationText() + "    拓扑Tip字段：" + showTextChoose.getRelationTip());
                    m_RelationshipTempList.Add(targetBusinessObjectName + "|" + showTextChoose.getRelationText() + "|" + showTextChoose.getRelationTip() + "|" + relDef.get_Name() );
                
                    item.setData("ischecked", true);
        		}else
                {
                    item.setChecked(false);
                    item.setData("ischecked", false);
                }
            }
        }
    }

	
	
	
//	private void deleteRelation(String relationObName){
//		TableItem[] items = lb_TopoRelationship.getItems();
//		int count = 0;
//		for(TableItem item : items){
//			if(item.getText().substring(item.getText().indexOf("拓扑显示字段:")).contains(relationObName)){
//				lb_TopoRelationship.remove(count);
//				return;
//			}
//			count ++;
//		}
//		
//		
//		
//	}
	
	
	public boolean saveProperties()
    {
        if (cb_MainObTopText.getData(cb_MainObTopText.getText()) == null || cb_MainObTipText.getData(cb_MainObTopText.getText()) == null)
        {
            MessageDialog.openInformation(getShell(), "对象关系拓扑图", "业务对象节点显示文字和Tip文字选择不能为空!");
            return false;
        }
        String tempstr = "";
        this.m_TopoRelationshipList.Clear();
        this.m_TopoRelationshipList.AddRange(this.m_RelationshipTempList);
        for (int i = 0; i < this.m_TopoRelationshipList.get_Count(); i++)
        {
            tempstr += this.m_TopoRelationshipList.get_Item(i) + "&";
        }
        if (tempstr.length() > 0)
        {
            tempstr = tempstr.substring(0, tempstr.length() - 1);
        }
        this.ObjectTopologicalDiagramPartDef.set_TopoRelationShipListString(tempstr);
        this.ObjectTopologicalDiagramPartDef.set_BusObId(m_SelectedObDef.get_Id());
        this.ObjectTopologicalDiagramPartDef.set_WillBeTopoBusObjectName(m_SelectedObDef.get_Alias());
        this.ObjectTopologicalDiagramPartDef.set_BusObName(m_SelectedObDef.get_Name());
        this.ObjectTopologicalDiagramPartDef.set_MainBusObTopText((String)cb_MainObTopText.getData(cb_MainObTopText.getText()));
        this.ObjectTopologicalDiagramPartDef.set_MainBusObTipText((String)cb_MainObTipText.getData(cb_MainObTipText.getText()));
		
        
        return true;
    }
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	@Override
	protected void okPressed() {
		if(general.savavalidation(txtName, txtAlias, type)){
			if(saveProperties())
			{
				super.okPressed();
			}
//			if ("New".equals(type)) {
//				m_Library.UpdateDefinition(dashboardpartdef, true);
//			} else if ("Edit".equals(type)) {
//				m_Library.UpdateDefinition(dashboardpartdef, false);
//			} else if("Copy".equals(type)){
//				dashboardpartdef.set_Id(m_Def.get_Id());
//				m_Library.UpdateDefinition(dashboardpartdef, true);
//			}
			
//			if (bashboardpartcenter != null) {
//				bashboardpartcenter.Refresh();
//			}
		}
	}
}
