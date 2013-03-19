package core.ui.dialog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.CustomDashboardPartBase;
import Core.Dashboards.CustomPartDef;
import Core.Dashboards.DashboardPartDef;
import Siteview.IDefinition;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.MsgBox;
import Siteview.Windows.Forms.ScopeUtil;

public class CustomPartDlg extends Dialog{

	private Text txtName;
	private Text txtAlias;

	private Generalmethods general;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

	private CustomPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private String type = "New"; // 标志位 1=添加 2=编辑 3=复制

	private Combo cboExtension;
	
	private ArrayList<String> exBundles;
	private ArrayList<String> exTensions;
	private Composite customUI;
	private CustomDashboardPartBase cusPart;
	
	public CustomPartDlg(Shell parentShell, DashboardPartDef dashboarddef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr, String type) {
		super(parentShell);
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.dashboardpartdef = (CustomPartDef) dashboarddef;
		this.m_Def = m_Def;
		this.type = type;
		this.linkstr = linkstr;
		general=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, m_Def, linkstr);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);

	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 700);
		newShell.setLocation(300, 100);
		newShell.setText("新建	自定义部件");
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
		tabItem.setText("\u5C5E\u6027");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		Label lblExt = new Label(composite,SWT.NONE);
		
		composite.setLayout(new FormLayout());
		
		lblExt.setText("部件扩展点：");
		lblExt.pack();
		FormData fd = new FormData();
		fd.left = new FormAttachment(0, 14);
		fd.right = new FormAttachment(0, 115);
		fd.top = new FormAttachment(0, 24);
		fd.bottom = new FormAttachment(0, 45);
		lblExt.setLayoutData(fd);
		
		cboExtension = new Combo(composite, SWT.READ_ONLY);
		fd = new FormData();
		fd.left = new FormAttachment(0, 120);
		fd.right = new FormAttachment(0, 460);
		fd.top = new FormAttachment(0, 20);
		fd.bottom = new FormAttachment(0, 40);
		cboExtension.setLayoutData(fd);
		
		customUI = new Composite(composite, SWT.BORDER);
		fd = new FormData();
		fd.left = new FormAttachment(0, 10);
		fd.right = new FormAttachment(100, -10);
		fd.top = new FormAttachment(0, 60);
		fd.bottom = new FormAttachment(100, -10);
		customUI.setLayoutData(fd);
		customUI.setLayout(new FillLayout());
		
		cboExtension.addSelectionListener(new SelectionAdapter(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				onSelectExtension();
			}});
		
		load();
		return container;
	}
	
	protected void onSelectExtension() {
		if (cboExtension.getSelectionIndex() < 0) return;
		for(Control c:customUI.getChildren()){
			c.dispose();
		}
		String bundleName = exBundles.get(cboExtension.getSelectionIndex());
		String className = exTensions.get(cboExtension.getSelectionIndex());
		cusPart = this.loadCustomClass(bundleName, className);
		if (cusPart!=null){
			Map<String, String> params = new HashMap<String,String>();
			for(int i = 0; i < dashboardpartdef.get_ParamTypes().get_Count(); i++){
				params.put(dashboardpartdef.get_ParamTypes().get_Item(i).toString(), dashboardpartdef.get_ParamVals().get_Item(i).toString());
			}
			try{
				cusPart.creatConfigUI(params);
			}catch(Exception e){

				Label l = new Label(this.customUI,SWT.WRAP);
				StringBuffer sb = new StringBuffer();
				for(StackTraceElement se: e.getStackTrace()){
					sb.append(se.toString());
				}

				l.setText("创建配置界面失败" + e +"\n" + sb);
				l.setForeground(new Color(Display.getDefault(),255,0,0));
				l.pack();
			}
			customUI.layout();
		}
		
	}

	/**
	 *  第一次加载数据
	 */
	
	public void load(){
		
		if ("New".equals(type)){
			txtName.setText(dashboardpartdef.get_Name());
			txtAlias.setText(dashboardpartdef.get_Alias());
		}
		else	if ("Edit".equals(type)) {
			txtName.setText(dashboardpartdef.get_Name());
			txtAlias.setText(dashboardpartdef.get_Alias());
		} else {
			txtName.setText(dashboardpartdef.get_Name() + "  的副本");
			txtAlias.setText(dashboardpartdef.get_Alias() + "  的副本");
		}

		//搜索扩展点
		exTensions = new ArrayList<String>();
		exBundles = new ArrayList<String>();
		IExtensionPoint extPoint = Platform.getExtensionRegistry()
					.getExtensionPoint("Siteview.Forms.Common.DashboardPartExtension");
			
			for (IExtension ext : extPoint.getExtensions()) {
				for (IConfigurationElement c : ext.getConfigurationElements()) {
					String className = "";
					if (c.getName().equals("CustomPart")) {
						className = c.getAttribute("Class");
						exTensions.add(className);
						exBundles.add(c.getContributor().getName());
						cboExtension.add(c.getAttribute("name"));
					}
				}
			}
		cboExtension.setText(dashboardpartdef.getExtensionName());
		onSelectExtension();
		
	}
	
	
	private CustomDashboardPartBase loadCustomClass(String bundleName,String className) {
		try {
			Class<?> cls = Platform.getBundle(bundleName).loadClass(className);
			CustomDashboardPartBase ext = (CustomDashboardPartBase) cls.getConstructor(Composite.class).newInstance(customUI);
			return ext;
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		return null;
	}

	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	@Override
	protected void okPressed() {
		if(general.savavalidation(txtName, txtAlias, type)){
		
			if (cboExtension.getSelectionIndex() < 0 ){
				MsgBox.ShowWarning("请选择 部件扩展点！");
			}
			dashboardpartdef.setExtensionName(cboExtension.getText());
			dashboardpartdef.setBundleId(exBundles.get(cboExtension.getSelectionIndex()));
			dashboardpartdef.get_ParamTypes().Clear();
			dashboardpartdef.get_ParamVals().Clear();
			
			if (cusPart!=null && cusPart.hasCustomUI()){
				try{
					Map<String,String> params = cusPart.getConfig();
					for(String key: params.keySet()){
						dashboardpartdef.get_ParamTypes().Add(key);
						dashboardpartdef.get_ParamVals().Add(params.get(key));
					}
				}catch(Exception e){
					MsgBox.ShowError("错误", "保存参数错误："+e.getMessage());
				}
			}
				
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
	}
}
