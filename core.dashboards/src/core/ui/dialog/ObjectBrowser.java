package core.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import siteview.windows.forms.ImageHelper;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ObjectBrowserPartDef;
import Core.Presentation.TreeBrowser.TreeDef;
import Core.ui.QuickCenterForm;
import Siteview.AutoTaskDef;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.PlaceHolder;
import Siteview.StringUtils;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import core.ui.DashBoardPartCenterForm;

public class ObjectBrowser extends Dialog{

	private Text txtName;
	private Text txtAlias;
	
	private Menu popMenu;
	private Table table_object;
	private Text text_2;
	private Table table_edit;
	private Text txtAutotasts;
	private Combo cb_object;
	private Button But_b;
	private Button Radio_allobject;
	private Button Radio_objectlist;
	private Button Radio_object;
	private Button Checked_allowobjectrefreh;

	private Generalmethods general;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private String type = "New"; // 标志位 1=添加 2=编辑 3=复制
	private ObjectBrowserPartDef objectbrowerdef;
	
	public ObjectBrowser(Shell parentShell, DashboardPartDef dashboarddef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr, String type) {
		super(parentShell);
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.dashboardpartdef = dashboarddef;
		this.m_Def = m_Def;
		this.type = type;
		this.linkstr = linkstr;
		
		this.objectbrowerdef=(ObjectBrowserPartDef) dashboarddef;
		general=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, m_Def, linkstr);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
	}
	

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 750);
		newShell.setLocation(300, 100);
		newShell.setText("新建	对象浏览器");
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
		fd_tabFolder.top = new FormAttachment(txtAlias, 21);
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 604);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -40);
		tabFolder.setLayoutData(fd_tabFolder);
		
		general.tabFolder_1( tabFolder);
		general.tabFolder_2( tabFolder);
		
		
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("\u5BF9\u8C61\u6D4F\u89C8\u5668");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		
		Group group = new Group(composite, SWT.SHADOW_NONE);
		group.setText("\u9009\u62E9\u5BF9\u8C61\u6D4F\u89C8\u5668\u5C06\u663E\u793A\u5728\u4EEA\u8868\u677F:");
		group.setBounds(10, 10, 576, 470);
		
		Radio_allobject = new Button(group, SWT.RADIO);
		Radio_allobject.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cb_object.setEnabled(false);
				table_object.setEnabled(false);
			}
		});
		Radio_allobject.setBounds(10, 20, 111, 16);
		Radio_allobject.setText("\u6240\u6709\u5BF9\u8C61\u6D4F\u89C8\u5668:");
		
		Radio_object = new Button(group, SWT.RADIO);
		Radio_object.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cb_object.setEnabled(true);
				table_object.setEnabled(false);
			}
		});
		Radio_object.setText("\u8FD9\u5BF9\u8C61\u6D4F\u89C8\u5668:");
		Radio_object.setBounds(10, 52, 111, 16);
		
		Radio_objectlist = new Button(group, SWT.RADIO);
		Radio_objectlist.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cb_object.setEnabled(false);
				table_object.setEnabled(true);
			}
		});
		Radio_objectlist.setText("\u8FD9\u5BF9\u8C61\u6D4F\u89C8\u5668\u5217\u8868:");
		Radio_objectlist.setBounds(10, 84, 123, 16);
		
		cb_object = new Combo(group, SWT.READ_ONLY);
		cb_object.setEnabled(false);
		cb_object.setBounds(197, 51, 274, 20);
		
		table_object = new Table(group, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table_object.setEnabled(false);
		table_object.setBounds(10, 106, 556, 132);
		
		ToolBar toolBar = new ToolBar(group, SWT.BORDER);
		toolBar.setBounds(10, 254, 556, 27);
		
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.First.png"),0x12,0x12));
		tltmNewItem.setToolTipText("首条");
		
		ToolItem tltmNewItem_2 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_2.setImage(ImageHelper.getImage("[IMAGE]Core#Images.Icons.Previous.png",0x12,0x12));
		
		
		ToolItem tltmNewItem_4 = new ToolItem(toolBar, SWT.SEPARATOR);
		tltmNewItem_4.setWidth(50);
		
		text_2 = new Text(toolBar, SWT.BORDER);
		tltmNewItem_4.setControl(text_2);
		
		final ToolItem tltmNewItem_5 = new ToolItem(toolBar, SWT.SEPARATOR);
		tltmNewItem_5.setWidth(35);
		
		Label lblNewLabel_6 = new Label(toolBar, SWT.SHADOW_NONE | SWT.CENTER);
		tltmNewItem_5.setControl(lblNewLabel_6);
		lblNewLabel_6.setText("/ 11");
		
		
		ToolItem tltmNewItem_7 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_7.setImage(ImageHelper.getImage("[IMAGE]Core#Images.Icons.Next.png",0x12,0x12));
		
		
		ToolItem tltmNewItem_6 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_6.setImage(ImageHelper.getImage("[IMAGE]Core#Images.Icons.Last.png",0x12,0x12));
		
	
		
		ToolItem tltmNewItem_9 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_9.setImage(ImageHelper.getImage("[IMAGE]Core#Images.Icons.NewAdd.png",0x12,0x12));
		
		ToolItem tltmNewItem_10 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_10.setImage(ImageHelper.getImage("[IMAGE]Core#Images.Icons.Delete.png",0x12,0x12));
		
		ToolItem tltmNewItem_11 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_11.setImage(ImageHelper.getImage("[IMAGE]siteview#Images.Icons.Common.SaveDisk.png",0x12,0x12));
		
		table_edit = new Table(group, SWT.BORDER | SWT.HIDE_SELECTION | SWT.VIRTUAL);
		table_edit.setLinesVisible(true);
		table_edit.setBounds(10, 287, 556, 100);
		table_edit.setHeaderVisible(true);
		
		TableColumn tblclmnNewColumn = new TableColumn(table_edit, SWT.NONE);
		tblclmnNewColumn.setResizable(false);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("\u7F16\u53F7");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table_edit, SWT.NONE);
		tblclmnNewColumn_1.setWidth(100);
		tblclmnNewColumn_1.setText("\u663E\u793A\u6D4F\u89C8");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table_edit, SWT.NONE);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("\u5141\u8BB8\u81EA\u52A8\u5237\u65B0");
		
		TableColumn tblclmnNewColumn_3 = new TableColumn(table_edit, SWT.NONE);
		tblclmnNewColumn_3.setWidth(100);
		tblclmnNewColumn_3.setText("\u81EA\u52A8\u5237\u65B0\u4EFB\u52A1");
		
		TableColumn tblclmnNewColumn_4 = new TableColumn(table_edit, SWT.NONE);
		tblclmnNewColumn_4.setWidth(100);
		tblclmnNewColumn_4.setText("\u6D4F\u89C8\u5668\u663E\u793A");
		
		TableItem tableItem_2 = new TableItem(table_edit, SWT.NONE);
		tableItem_2.setText("New TableItem");
		
		Checked_allowobjectrefreh = new Button(group, SWT.CHECK);
		Checked_allowobjectrefreh.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				txtAutotasts.setEnabled(Checked_allowobjectrefreh.getSelection());
				But_b.setEnabled(Checked_allowobjectrefreh.getSelection());
			}
		});
		Checked_allowobjectrefreh.setBounds(10, 393, 209, 16);
		Checked_allowobjectrefreh.setText("\u5141\u8BB8\u76EE\u524D\u7684\u5BF9\u8C61\u6D4F\u89C8\u5668\u7684\u52A8\u6001\u5237\u65B0");
		
		txtAutotasts = new Text(group, SWT.BORDER);
		txtAutotasts.setEditable(false);
		txtAutotasts.setBounds(10, 415, 308, 36);
		txtAutotasts.setEnabled(false);
		
		But_b = new Button(group, SWT.NONE);
		But_b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				QuickCenterForm frm = new QuickCenterForm(true,"Change");
				if (frm.open() == QuickCenterForm.OK){
					AutoTaskDef ta = frm.getSelection();
					txtAutotasts.setText(ta.get_Alias());
					txtAutotasts.setData(ta);
				}
			}
		});
		But_b.setBounds(333, 415, 72, 26);
		But_b.setText("\u6D4F\u89C8(B)...");
		But_b.setEnabled(false);
		Fillcb_object();
		load();
		return container;
	}
	
	/**
	 * 第一次加载数据
	 */
	public void load(){
		
		if(dashboardpartdef == null || "New".equals(type)){
			Radio_allobject.setSelection(true);
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
		
		Radio_allobject.setSelection(objectbrowerdef.get_ObjectBrowserIDs()!=null&&objectbrowerdef.get_ObjectBrowserIDs().get_Count()==0);
		if(Radio_allobject.getSelection()){
			cb_object.setEnabled(false);
			table_object.setEnabled(false);
		}
		Radio_object.setSelection(objectbrowerdef.get_ObjectBrowserIDs()!=null&&objectbrowerdef.get_ObjectBrowserIDs().get_Count()==1);
		if(Radio_object.getSelection()){
			cb_object.setEnabled(false);
			table_object.setEnabled(true);
		}else{
			Radio_objectlist.setSelection(objectbrowerdef.get_ObjectBrowserIDs()!=null&&objectbrowerdef.get_ObjectBrowserIDs().get_Count()>1);
			cb_object.setEnabled(true);
			table_object.setEnabled(false);
		}
		Checked_allowobjectrefreh.setSelection(objectbrowerdef.get_AllowDynamicRefresh());
		if (objectbrowerdef.get_AllowDynamicRefresh() && !StringUtils.IsEmpty(objectbrowerdef.get_QuickActionID())){
    	  AutoTaskDef definition = (AutoTaskDef) m_api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(objectbrowerdef.get_QuickActionScope(), objectbrowerdef.get_QuickActionScopeOwner(), objectbrowerdef.get_QuickActionLinkedTo(), AutoTaskDef.get_ClassName(), objectbrowerdef.get_QuickActionID()));
    	  txtAutotasts.setText(definition.get_Alias());
    	  txtAutotasts.setData(definition);
    	  But_b.setEnabled(true);
		}
		
	}
	
	/**
	 * 填充 对象浏览器和数据  下拉式的数据  
	 */
	public void Fillcb_object(){
		
		ICollection cb_gridList =m_api.get_LiveDefinitionLibrary().GetPlaceHolderList(DefRequest.ByCategory(TreeDef.get_ClassName()));
		IEnumerator it = cb_gridList.GetEnumerator();
		boolean flag=objectbrowerdef.get_ObjectBrowserIDs().get_Count()==1;
		while(it.MoveNext()){
			PlaceHolder holder = (PlaceHolder)it.get_Current();
			
			cb_object.add(holder.get_Alias());
			cb_object.setData(holder.get_Alias(),holder);
			TableItem tableitem=new TableItem(table_object,SWT.NONE); 
			tableitem.setText(holder.get_Alias());
			tableitem.setData(holder);
			if(objectbrowerdef.get_ObjectBrowserIDs().Contains(holder.get_Id())){
				tableitem.setChecked(true);
				if(flag)cb_object.setText(holder.get_Alias());
			}
		}
		
		if(cb_object.getSelectionIndex()==-1){
			cb_object.add("请选择一个对象浏览器");
			cb_object.setText("请选择一个对象浏览器");
			cb_object.setData("请选择一个对象浏览器", "");
		}
	}
	
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	
	/**
	 * 点击保存 的处理的事件
	 */
	protected void okPressed() {
		
		if(general.savavalidation(txtName, txtAlias, type)){
			ArrayList list = new ArrayList();
			if(Radio_object.getSelection()){
				
				if(StringUtils.IsEmpty(cb_object.getData(cb_object.getText()).toString())){
					MessageDialog.openInformation(getParentShell(), "提示",
							"请选择一个对象浏览器. ");
					cb_object.forceFocus();
					return;
				}
				else{
					 PlaceHolder dataValue = (PlaceHolder) cb_object.getData(cb_object.getText());
		             list.Add(dataValue.get_Id());
				}
			}
			if(Radio_objectlist.getSelection()){
				
				for(int i=0;i<table_object.getItemCount();i++){
					TableItem tableitem=table_object.getItems()[i];
					if(tableitem.getChecked()){
						PlaceHolder dataValue=(PlaceHolder) tableitem.getData();
						list.Add(dataValue.get_Id());
					}
				}
				if(list.get_Count()<1){
					MessageDialog.openInformation(getParentShell(), "提示",
							"请选择至少一个对象浏览器. ");
					table_object.forceFocus();
					return;
				}
			}
			
			if(Checked_allowobjectrefreh.getSelection()){
				
				if(StringUtils.IsEmpty(txtAutotasts.getText())){
					MessageDialog.openInformation(getParentShell(), "提示",
							"请选择一个快速操作. ");
					txtAutotasts.forceFocus();
					return;
				}else{
					AutoTaskDef autotaskdef=(AutoTaskDef) txtAutotasts.getData();
					objectbrowerdef.set_QuickActionID(autotaskdef.get_Id());
					objectbrowerdef.set_QuickActionScope(autotaskdef.get_Scope());
					objectbrowerdef.set_QuickActionScopeOwner(autotaskdef.get_ScopeOwner());
					objectbrowerdef.set_QuickActionName(autotaskdef.get_Name());
					objectbrowerdef.set_QuickActionLinkedTo(autotaskdef.get_LinkedTo());
					objectbrowerdef.set_AllowDynamicRefresh(true);
				}
				
			}
			objectbrowerdef.set_ObjectBrowserIDs(list);
//			if ("New".equals(type)) {
//				m_Library.UpdateDefinition(dashboardpartdef, true);
//			} else if ("Edit".equals(type)) {
//				m_Library.UpdateDefinition(dashboardpartdef, false);
//			} else if("Copy".equals(type)){
//				dashboardpartdef.set_Id(m_Def.get_Id());
//				m_Library.UpdateDefinition(dashboardpartdef, true);
//			}
			super.okPressed();
		}
		super.okPressed();
	}
	
	
}
