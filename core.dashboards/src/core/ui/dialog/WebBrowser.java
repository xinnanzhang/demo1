package core.ui.dialog;

import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

import Siteview.IDefinition;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.TimeUnit;

import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.WebBrowserPartDef;


public class WebBrowser extends Dialog {
	
	private String type;	//操作类型----1.新建 2.编辑 3.复制
	private String linkTo;  //关联下拉式 选 项
//	private OutlookCalendarPartDef calendarDef;
	private DashboardPartDef dashboardPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	private IDefinition m_Def;
	
	private Generalmethods general;
	
	private Text text_Name;
	private Text text_Alias;
	
	private Text text_URL;
	private Button button_SupportAsync;
	private Button Button_ShowRefresh;
	private Combo Combo_RefreshInterval;
	private WebBrowserPartDef webBrowserPartDef;
	
	

	protected WebBrowser(Shell parentShell) {
		super(parentShell);
	}
	
	public WebBrowser(Shell parentShell, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, String type, IDefinition m_Def) {
		super(parentShell);
		this.dashboardPartDef = dashboardPartDef;
		webBrowserPartDef = (WebBrowserPartDef)dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.type = type;
		this.m_Def = m_Def;
		general=new Generalmethods(getShell(),dashboardPartDef, m_Api, m_Library, m_ScopeUtil, m_Def, linkTo);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 700);
		newShell.setLocation(300, 100);
		newShell.setText(type+"	Web浏览器");
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
		
		text_Name = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblNewLabel, 406, SWT.RIGHT);
		fd_text.top = new FormAttachment(0, 28);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		text_Name.setLayoutData(fd_text);
		
		text_Alias = new Text(container, SWT.BORDER);
		FormData fd_text_1 = new FormData();
		fd_text_1.top = new FormAttachment(lbla, -3, SWT.TOP);
		fd_text_1.right = new FormAttachment(text_Name, 0, SWT.RIGHT);
		fd_text_1.left = new FormAttachment(text_Name, 0, SWT.LEFT);
		text_Alias.setLayoutData(fd_text_1);
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(text_Alias, 21);
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 604);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -40);
		tabFolder.setLayoutData(fd_tabFolder);
		
		general.tabFolder_1(tabFolder);
		general.tabFolder_2(tabFolder);
		
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u5C5E\u6027");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		
		Label lblNewLabel_6 = new Label(composite, SWT.NONE);
		lblNewLabel_6.setBounds(10, 27, 54, 18);
		lblNewLabel_6.setText("URL(U):");
		
		text_URL = new Text(composite, SWT.BORDER);
		text_URL.setBounds(81, 24, 392, 18);
		
		button_SupportAsync = new Button(composite, SWT.CHECK);
		button_SupportAsync.setBounds(10, 81, 118, 16);
		button_SupportAsync.setText("  \u652F\u6301\u5F02\u6B65\u52A0\u8F7D");
		
		Button_ShowRefresh = new Button(composite, SWT.CHECK);
		Button_ShowRefresh.setText("  \u663E\u793A\u5237\u65B0\u6309\u94AE");
		Button_ShowRefresh.setBounds(10, 126, 118, 16);
		
		Label lblNewLabel_8 = new Label(composite, SWT.NONE);
		lblNewLabel_8.setBounds(10, 165, 65, 18);
		lblNewLabel_8.setText("\u5237\u65B0\u95F4\u9694");
		
		Combo_RefreshInterval = new Combo(composite, SWT.READ_ONLY);
		Combo_RefreshInterval.setBounds(188, 159, 171, 20);
		initViewDate();
		
		fillProperties();
		return container;
	}
	
	protected void createViewModel(TabFolder tabFolder) {
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u5C5E\u6027");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		
		Label lblNewLabel_6 = new Label(composite, SWT.NONE);
		lblNewLabel_6.setBounds(10, 27, 54, 18);
		lblNewLabel_6.setText("URL(U):");
		
		text_URL = new Text(composite, SWT.BORDER);
		text_URL.setBounds(81, 24, 392, 18);
		
		button_SupportAsync = new Button(composite, SWT.CHECK);
		button_SupportAsync.setBounds(10, 81, 118, 16);
		button_SupportAsync.setText("  \u652F\u6301\u5F02\u6B65\u52A0\u8F7D");
		
		Button_ShowRefresh = new Button(composite, SWT.CHECK);
		Button_ShowRefresh.setText("  \u663E\u793A\u5237\u65B0\u6309\u94AE");
		Button_ShowRefresh.setBounds(10, 126, 118, 16);
		
		Label lblNewLabel_8 = new Label(composite, SWT.NONE);
		lblNewLabel_8.setBounds(10, 165, 65, 18);
		lblNewLabel_8.setText("\u5237\u65B0\u95F4\u9694");
		
		Combo_RefreshInterval = new Combo(composite, SWT.NONE);
		Combo_RefreshInterval.setBounds(188, 159, 171, 20);
		initViewDate();
	}
	
	
	private void initViewDate(){
		Combo_RefreshInterval.setItems(new String[]{"无", "1分钟", "2分钟", "3分钟", "5分钟", "10分钟", "15分钟", "20分钟", "30分钟", "60分钟", "90分钟", "120分钟"});
	}
	
	
	protected void fillProperties() {
		if(!"New".equals(type)){
			text_URL.setText(webBrowserPartDef.get_FilePath());
			button_SupportAsync.setSelection(webBrowserPartDef.get_SupportAsyncLoading());
			Button_ShowRefresh.setSelection(webBrowserPartDef.get_ShowRefreshButton());
			int minutes = (int)webBrowserPartDef.get_RefreshFrequencyTimeSpan().get_TotalMinutes();
			Combo_RefreshInterval.setText(minutes>=1 ? minutes+"分钟" : "无");
			
			if ("Edit".equals(type)) {
				text_Name.setText(dashboardPartDef.get_Name());
				text_Alias.setText(dashboardPartDef.get_Alias());
			} else {
				text_Name.setText(dashboardPartDef.get_Name() + "  的副本");
				text_Alias.setText(dashboardPartDef.get_Alias() + "  的副本");
			}
		}else{
			Combo_RefreshInterval.setText("无");
		}
	}
	
//	private String ComboIndexFromMinutes(int freq)
//    {
//        if (freq >= 120)	return "120分钟";
//        if (freq >= 90)		return "90分钟";
//        if (freq >= 60)		return "60分钟";
//        if (freq >= 30)		return "30分钟";
//        if (freq >= 15)		return "20分钟";
//        if (freq >= 20)		return "15分钟";
//        if (freq >= 10)		return "10分钟";
//        if (freq >= 5)		return "5分钟";
//        if (freq >= 3)		return "3分钟";
//        if (freq >= 2)		return "2分钟";
//        if (freq >= 1)		return "1分钟";
//        return "无";
//    }
	
	public static int ComboIndexFromText(String text)
    {
        if ("120分钟".equals(text))		return 120;
        if ("90分钟".equals(text))		return 90;
        if ("60分钟".equals(text))		return 60;
        if ("30分钟".equals(text))		return 30;
        if ("20分钟".equals(text))		return 20;
        if ("15分钟".equals(text))		return 15;
        if ("10分钟".equals(text))		return 10;
        if ("5分钟".equals(text))		return 5;
        if ("3分钟".equals(text))		return 3;
        if ("2分钟".equals(text))		return 2;
        if ("1分钟".equals(text))		return 1;
        return 0;
    }
	
	
	@Override
	protected void okPressed() {
		
		if(general.savavalidation(text_Name, text_Alias, type)){
			
			saveModel();
			
//			if ("New".equals(type)) {
//				m_Library.UpdateDefinition(webBrowserPartDef, true);
//			} else if ("Edit".equals(type)) {
//				m_Library.UpdateDefinition(webBrowserPartDef, false);
//			} else if("Copy".equals(type)){
//				webBrowserPartDef.set_Id(m_Def.get_Id());
//				m_Library.UpdateDefinition(webBrowserPartDef, true);
//			}
			
			super.okPressed();
		}
		
	}
	
	
	protected void saveModel() {
		webBrowserPartDef.set_FilePath(text_URL.getText().contains("http://")? text_URL.getText(): "http://"+text_URL.getText());
		webBrowserPartDef.set_SupportAsyncLoading(button_SupportAsync.getSelection());
		webBrowserPartDef.set_ShowRefreshButton(Button_ShowRefresh.getSelection());
		webBrowserPartDef.set_RefreshFrequencyTimeUnit(TimeUnit.Minutes);
		webBrowserPartDef.set_RefreshFrequency(ComboIndexFromText(Combo_RefreshInterval.getText()));
	}
}
