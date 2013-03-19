package core.ui.dialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ImagePartDef;
import Siteview.IDefinition;
import Siteview.StringUtils;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import core.ui.DashBoardPartCenterForm;

public class ImageDlg extends Dialog{

	private Text txtName;
	private Text txtAlias;
	
	private Menu popMenu;
	private Label imageAddrLabel;

	private Generalmethods general;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private String type = "New"; // 标志位 1=添加 2=编辑 3=复制
	
	public ImageDlg(Shell parentShell, DashboardPartDef dashboarddef, ISiteviewApi m_api,
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
		general=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, m_Def, linkstr);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 700);
		newShell.setLocation(300, 100);
		newShell.setText("新建	图像");
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
		
		Label lblNewLabel_6 = new Label(composite, SWT.NONE);
		lblNewLabel_6.setBounds(25, 23, 54, 18);
		lblNewLabel_6.setText("\u56FE\u50CF(I):");
		
		final Label lblNewLabel_8 = new Label(composite, SWT.NONE);
		lblNewLabel_8.setBounds(85, 23, 21, 18);
		lblNewLabel_8.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				if(e.button == 1){
					popMenu = createPopMenu().createContextMenu(lblNewLabel_8);
					popMenu.setLocation(lblNewLabel_8.toDisplay(e.x,e.y));
					popMenu.setVisible(true);
				}
			}
		});
		lblNewLabel_8.setText("");
		lblNewLabel_8.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_down.png"),0x12,0x12));
		
		imageAddrLabel = new Label(composite, SWT.NONE);
		imageAddrLabel.setBounds(112, 23, 410, 18);
		imageAddrLabel.setText(" (\u65E0)");
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FlowChart fc=new FlowChart();
			}
		});
		btnNewButton.setBounds(78, 126, 72, 22);
		btnNewButton.setText("\u6D4B\u8BD5");
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				LogMonitorDialog d = new LogMonitorDialog(getShell());
//				if (d.open() == IDialogConstants.OK_ID) {
//					LogMonitorSet[] lms = d.getDialogData();
//					for (int i = 0; i < lms.length; i++) {
//						LogMonitorSet o = lms[i];
//						System.out.println(o.getSelection() + "__" + o.getName() + "__"
//								+ LogHelper.getLevelShowString(o.getLogLevel()));
//					}
//				}
			}
		});
		btnNewButton_1.setBounds(78, 193, 72, 22);
		btnNewButton_1.setText("New Button");
		
		load();
		return container;
	}
	
	/**
	 *  第一次加载数据
	 */
	
	public void load(){
		
		if(dashboardpartdef == null || "New".equals(type)){
			imageAddrLabel.setData("");
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

		
		ImagePartDef imagepartdef=(ImagePartDef) dashboardpartdef;
	
		if (!StringUtils.IsEmpty(imagepartdef.get_ImageName())) {
				imageAddrLabel.setText(ImageChooseDlg.getInitImageStr(imagepartdef.get_ImageName()));
				imageAddrLabel.setData(imagepartdef.get_ImageName());
		}
		
	}
	
	private MenuManager createPopMenu() {
		MenuManager menuManager = new MenuManager();
		// menuManager.setRemoveAllWhenShown(true);
		Action look = new Action("浏览(B)...") {
			@Override
			public void run() {
				String str = "";
				str = imageAddrLabel.getData().toString();
				ImageChooseDlg imageC = new ImageChooseDlg(getShell(),m_Library, m_api, str);
				if (imageC.open() == 0) {
					imageAddrLabel.setText(imageC.getImageAdr());
					imageAddrLabel.setData(imageC.getImageAdrDate());

				}
			}
		};

		Action nul = new Action("无") {
			@Override
			public void run() {
				imageAddrLabel.setText("(无)");
				imageAddrLabel.setData("");

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

	@Override
	protected void okPressed() {
		if(general.savavalidation(txtName, txtAlias, type)){
			
			ImagePartDef imagepartdef=(ImagePartDef) dashboardpartdef;
			imagepartdef.set_ImageName(imageAddrLabel.getData() == null ? "" : imageAddrLabel.getData().toString());
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
