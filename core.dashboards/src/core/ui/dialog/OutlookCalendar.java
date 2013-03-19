package core.ui.dialog;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.ImageResolver;
import system.Type;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.IList;
import system.Drawing.KnownColor;
import Core.Dashboards.DashboardPartDef;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.PlaceHolder;
import Siteview.StringUtils;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.SecurityRight;


public class OutlookCalendar extends Dialog{
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
	private Text text_Description;
	private Text text_title;
	
	private Menu popMenu;
	
	private CCombo Combo_ForeColor;
	private Button button_CheckTitle;
	private Button Button_SolidCheck;
	private Button Button_GradientCheck;
	private Button Button_CheckBgColor;
	private CCombo combo_Color1;
	private CCombo combo_Color2;
	
	private Label label_Color1;
	private Label label_Color2;
	private Label Label_ImageAddr;
	
	private CCombo combo_Scope;
	private CCombo combo_Owner;
	private CCombo combo_Category;
	private CCombo combo_LinkTo;

	
	public String getPartTypeName() {
		return "Outlook日历";
	}

	public OutlookCalendar(Shell parentShell) {
		super(parentShell);
	}
	
	public OutlookCalendar(Shell parentShell, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, String type, IDefinition m_Def) {
		super(parentShell);
		this.dashboardPartDef = dashboardPartDef;
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
		newShell.setText(type+"  Outlook日历");
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
		fillProperties();
		return container;
	}
	
	
	private MenuManager createPopMenu(){
		MenuManager menuManager=new MenuManager();
//		menuManager.setRemoveAllWhenShown(true);
		Action look = new Action("浏览(B)..."){
			@Override
			public void run() {
				createLookPage();
			}
		};
//		look.setImageDescriptor(new ImageDescriptor() {
//			
//			@Override
//			public ImageData getImageData() {
//				// TODO Auto-generated method stub
//				return SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_down.png"),0x12,0x12).getImageData();
//			}
//		});
		
		Action nul = new Action("无"){
			@Override
			public void run() {
				Label_ImageAddr.setText("无");
			}
		};
		menuManager.add(look);
		menuManager.add(nul);
		
		return menuManager;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	private void createLookPage() {
		ImageChooseDlg imageC = new ImageChooseDlg(this.getShell(), m_Library, m_Api, Label_ImageAddr.getData().toString());
		if(imageC.open() == 0) {
			Label_ImageAddr.setText(imageC.getImageAdr());
			Label_ImageAddr.setData(imageC.getImageAdr(), imageC.getImageAdrDate());
		}
	}
	
	
	@Override
	protected void okPressed() {
		
		if(general.savavalidation(text_Name, text_Alias, type)){
			
//			if ("New".equals(type)) {
//				m_Library.UpdateDefinition(dashboardPartDef, true);
//			} else if ("Edit".equals(type)) {
//				m_Library.UpdateDefinition(dashboardPartDef, false);
//			} else if("Copy".equals(type)){
//				dashboardPartDef.set_Id(m_Def.get_Id());
//				m_Library.UpdateDefinition(dashboardPartDef, true);
//			}
			super.okPressed();
		}
	}
	
	//颜色combo Date数据
	public static String getSiteviewColorForColor(String colorName){
		
		KnownColor kc = (KnownColor)KnownColor.Parse(Type.GetType(KnownColor.class.getName()), colorName);
		system.Drawing.Color c = system.Drawing.Color.FromKnownColor(kc.value__);
		
		if(c.get_IsSystemColor()) return "[SYSTEM]" + colorName;
		
		return "[NAMED]" + colorName;
	}
	
	//截取颜色名称
	public static String getColorForSiteview(String colorName){
		return colorName.substring(colorName.indexOf("]")+1, colorName.length());
	}
	
	//填充combo背景色
	public static Color getBackgroudColor(CCombo comb){
		if(comb.getSelectionIndex() != -1){
			system.Drawing.Color c = system.Drawing.Color.FromName(getColorForSiteview((String)comb.getData(comb.getItem(comb.getSelectionIndex()))));
			return new Color(null, c.get_R(), c.get_G(), c.get_B());
		}
		return null;
	}
	
	//解析图片名称     图片显示路径-->界面显示名称
	public static String getInitImageStr(String imagePath){
		String imageText = "";
		if(imagePath.contains("[IMAGE]")){
			String[] outFolder = new String[1];
			ICollection[] folerNames = new ICollection[1];
			String[] imageName = new String[1];
			ImageResolver.get_Resolver().ParseResourceImageName(imagePath, true, outFolder, folerNames, imageName);
			imageText = "System." + imageName[0];
		}else if(imagePath.contains("[DATABASE]")){
			imageText = "Database." + imagePath.substring("[DATABASE]".length());
		}else if("".equals(imagePath)){
			imageText = "无";
		}
		return imageText;
	}
	
	//解析图片名称    界面显示名称-->图片显示路径
	private String getSaveImageStr(String imagePath){
		String imageText = "";
		if(imagePath.contains("Database.")){
			imageText = "[DATABASE]"+imagePath.substring("Database.".length());
		}else if(imagePath.contains("System.")){
			imageText = (String)Label_ImageAddr.getData(imagePath);
		}
		return imageText;
	}
	
	//解析图片名称     界面显示名称-->图片名称  
	private String getSelectImageStr(String imagePath){
		String imageText = "";
		if(imagePath.contains("Database.")){
			imageText = imagePath.substring("Database.".length());
		}else if(imagePath.contains("System.")){
			imageText = imagePath.substring("System.".length());
		}
		return imageText;
	}
	
	protected void createViewModel(TabFolder tabFolder){}
	protected void fillProperties(){
		
		if(dashboardPartDef == null || "New".equals(type)){
			return;
		}
		
		if ("Edit".equals(type)) {
			text_Name.setText(dashboardPartDef.get_Name());
			text_Alias.setText(dashboardPartDef.get_Alias());
		} else {
			text_Name.setText(dashboardPartDef.get_Name() + "  的副本");
			text_Alias.setText(dashboardPartDef.get_Alias() + "  的副本");
		}
		
	}
	protected void saveModel(){}
	
	public DefinitionLibrary getM_Library() {
		return m_Library;
	}
}
