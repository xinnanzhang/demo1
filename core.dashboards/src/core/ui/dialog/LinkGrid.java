package core.ui.dialog;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Convert;
import system.Type;
import system.Collections.IEnumerator;
import system.Drawing.KnownColor;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.LinkDef;
import Core.Dashboards.LinkListPartDef;
import Core.Dashboards.TitleBarDef;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.StringUtils;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;
import Siteview.Xml.TimeUnit;
import core.ui.DashBoardPartCenterForm;


public class LinkGrid extends Dialog {

	
	
	
	private Text txtName;
	private Text txtAlias;
	private Text txtDesc;// 说明
	private Table table;
	private Combo cbScope; // 范围下拉式
	private Combo cbCategory; // 类别下拉式
	private Combo cbOwner; // 负责人下拉式
	private Combo cbLinkedTo; // 关联下拉式
	private Text txtText;
	private TableCombo cb_1;
	private Button Radio_1;
	private Button Radio_2;
	private Button Radio_3;
	private Button Radio_4;
	private Button Checked_Date;
	private TableCombo cb_2;
	private TableCombo cb_3;
	private TableCombo cb_4;
	private TableCombo cb_5;
	private Button Checked_SystemYs;
	private Menu popMenu;
	private Label colorLb2;
	private Label color_2;
	private Label label_4;
	private Label label_2;
	private Label Image2;
	private Label imageAddrLabel;
	private int tubiao = 1;
	
	
	private Button but_UP;
	private Button but_Down;
	private Button but_Add;
	private Button but_Edit;
	private Button but_del;
	private Button Checked_ybloading;
	private Button Checked_showrefresh;
	private CCombo cb_refreshinterval;
	
	private Generalmethods generals;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private String type = "New"; // 标志位 1=添加 2=编辑 3=复制
	private Text txtPath;
	private LinkListPartDef linklistpartdef;
	
	public LinkGrid(Shell parentShell, DashboardPartDef dashboarddef, ISiteviewApi m_api,
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
		linklistpartdef=(LinkListPartDef) dashboardpartdef;
		
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(730, 700);
		newShell.setLocation(300, 70);
		newShell.setText(type + "	链接列表");
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
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 678);
		fd_tabFolder.top = new FormAttachment(txtAlias, 21);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -27);
		tabFolder.setLayoutData(fd_tabFolder);
		
//		general.tabFolder_1( tabFolder);
//		general.tabFolder_2( tabFolder);
		
		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("\u4E00\u822C");

		Composite general = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(general);

		Label lblNewLabel_1 = new Label(general, SWT.NONE);
		lblNewLabel_1.setBounds(29, 23, 54, 21);
		lblNewLabel_1.setText("\u8BF4\u660E(D):");

		txtDesc = new Text(general, SWT.BORDER | SWT.MULTI);
		txtDesc.setBounds(130, 20, 530, 43);

		Label lblNewLabel_2 = new Label(general, SWT.NONE);
		lblNewLabel_2.setBounds(29, 83, 54, 21);
		lblNewLabel_2.setText("\u8303\u56F4(S):");

		cbScope = new Combo(general, SWT.READ_ONLY);
		cbScope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				generals.cbOwnerdate(cbScope.getData(cbScope.getItem(cbScope
						.getSelectionIndex())), Generalmethods.isClient ? 0 : 1);
			}
		});
		cbScope.setBounds(130, 83, 177, 21);

		Label lblNewLabel_3 = new Label(general, SWT.NONE);
		lblNewLabel_3.setBounds(370, 83, 60, 21);
		lblNewLabel_3.setText("\u8D1F\u8D23\u4EBA(O):");

		cbOwner = new Combo(general, SWT.READ_ONLY);
		cbOwner.setEnabled(false);
		cbOwner.setBounds(465, 83, 195, 21);

		Label lblc = new Label(general, SWT.NONE);
		lblc.setText("\u7C7B\u522B(C):");
		lblc.setBounds(29, 138, 54, 21);

		Label lbla_1 = new Label(general, SWT.NONE);
		lbla_1.setText("\u5173\u8054(A):");
		lbla_1.setBounds(29, 183, 54, 21);

		cbCategory = new Combo(general, SWT.BORDER);
		cbCategory.setBounds(130, 138, 341, 21);
		
		cbLinkedTo = new Combo(general, SWT.READ_ONLY);
		cbLinkedTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cbLinkedTo.getSelectionIndex() > 0) {
					
					generals.cbLinkedToSelected(cbLinkedTo.getItem(cbLinkedTo
							.getSelectionIndex()));
					
				}
			}
		});
		
		
		cbLinkedTo.setBounds(130, 183, 281, 21);

		
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("\u5916\u89C2");

		Composite face = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(face);

		final Group grpt = new Group(face, SWT.NONE);
		grpt.setLocation(10, 10);
		grpt.setText("\u6807\u9898(T)");
		grpt.setSize(576, 261);

		Label lblNewLabel_4 = new Label(grpt, SWT.NONE);
		lblNewLabel_4.setBounds(31, 28, 54, 18);
		lblNewLabel_4.setText("\u6587\u672C:");

		txtText = new Text(grpt, SWT.BORDER);
		txtText.setBounds(114, 25, 334, 18);

		Checked_Date = new Button(grpt, SWT.CHECK);
		Checked_Date.setSelection(true);
		Checked_Date.setBounds(460, 25, 93, 18);
		Checked_Date.setText("\u663E\u793A\u6807\u9898");

		Label lblNewLabel_5 = new Label(grpt, SWT.NONE);
		lblNewLabel_5.setBounds(31, 70, 33, 18);
		lblNewLabel_5.setText("\u56FE\u6807:");

		
		Checked_SystemYs = new Button(grpt, SWT.CHECK);
		Checked_SystemYs.setSelection(true);
		Checked_SystemYs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Checked_SystemYs.getSelection()) {
					cb_1.setEnabled(false);
					Radio_1.setEnabled(false);
					Radio_2.setEnabled(false);
					cb_2.setEnabled(false);
					cb_3.setEnabled(false);
					label_2.setEnabled(false);
					colorLb2.setEnabled(false);
				} else {
					cb_1.setEnabled(true);
					Radio_1.setEnabled(true);
					Radio_2.setEnabled(true);
					cb_2.setEnabled(true);
					cb_3.setEnabled(true);
					label_2.setEnabled(true);
					colorLb2.setEnabled(true);
					if (Radio_2.getSelection()) {
						cb_3.setVisible(true);
						label_2.setVisible(true);
					}
				}
			}
		});
		Checked_SystemYs.setBounds(30, 106, 205, 18);
		Checked_SystemYs.setText("\u4F7F\u7528\u4EEA\u8868\u677F\u6807\u9898\u80CC\u666F\u989C\u8272");

		Label label = new Label(grpt, SWT.NONE);
		label.setText("\u524D\u666F\u8272:");
		label.setBounds(233, 138, 54, 18);

		cb_1 = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		cb_1.setEnabled(false);
		cb_1.setBounds(293, 135, 220, 21);

		Label label_1 = new Label(grpt, SWT.NONE);
		label_1.setText("\u80CC\u666F\u8272:");
		label_1.setBounds(31, 160, 54, 18);

		Radio_1 = new Button(grpt, SWT.RADIO);
		Radio_1.setEnabled(false);
		Radio_1.setSelection(true);
		Radio_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorLb2.setVisible(false);
				cb_3.setVisible(false);
			}
		});
		Radio_1.setText("\u5355\u8272");
		Radio_1.setBounds(91, 185, 93, 18);

		Radio_2 = new Button(grpt, SWT.RADIO);
		Radio_2.setEnabled(false);
		Radio_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Radio_2.getSelection()) {
					colorLb2.setVisible(true);
					cb_3.setVisible(true);
				}
			}
		});
		Radio_2.setText("\u6E10\u53D8");
		Radio_2.setBounds(91, 220, 93, 18);

		label_2 = new Label(grpt, SWT.NONE);
		label_2.setEnabled(false);
		label_2.setText("\u989C\u82721:");
		label_2.setBounds(212, 185, 54, 18);

		cb_2 = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		cb_2.setEnabled(false);
		cb_2.setBounds(293, 182, 220, 21);

		imageAddrLabel = new Label(grpt, SWT.NONE);
		imageAddrLabel.setBounds(149, 70, 329, 18);
		imageAddrLabel.setText(" (\u65E0)");
		imageAddrLabel.setData("");
		colorLb2 = new Label(grpt, SWT.NONE);
		colorLb2.setEnabled(false);
		colorLb2.setText("\u989C\u82722:");
		colorLb2.setBounds(212, 223, 54, 17);
		colorLb2.setVisible(false);

		cb_3 = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		cb_3.setEnabled(false);
		cb_3.setBounds(293, 220, 220, 21);
		cb_3.setVisible(false);

		final Label lblNewLabel_7 = new Label(grpt, SWT.NONE);
		lblNewLabel_7.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// System.out.println(MouseButtons.Left); 左键为1
				// System.out.println(MouseButtons.Right);右键为2
				if (e.button == 1) {
					tubiao = 1;
					popMenu = createPopMenu().createContextMenu(lblNewLabel_7);
					// lblNewLabel_7.setMenu(popMenu); 绑定菜单，点击右键自动触发
					popMenu.setLocation(lblNewLabel_7.toDisplay(e.x, e.y));
					popMenu.setVisible(true);
				}
			}
		});
		lblNewLabel_7.setBounds(114, 70, 17, 18);
		lblNewLabel_7.setText("");
		lblNewLabel_7.setImage(SwtImageConverter.ConvertToSwtImage(
				ImageResolver.get_Resolver().ResolveImage(
						"[IMAGE]Core#Images.Icons.navigate_down.png"), 0x12,
				0x12));
		
		Group group = new Group(face, SWT.NONE);
		group.setText("\u80CC\u666F");
		group.setBounds(10, 277, 576, 151);

		Label label_3 = new Label(group, SWT.NONE);
		label_3.setText("\u989C\u8272:");
		label_3.setBounds(29, 23, 54, 18);

		Radio_3 = new Button(group, SWT.RADIO);
		Radio_3.setText("\u5355\u8272");
		Radio_3.setSelection(true);
		Radio_3.setBounds(89, 48, 93, 18);
		Radio_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				color_2.setVisible(false);
				cb_5.setVisible(false);
			}
		});
		Radio_4 = new Button(group, SWT.RADIO);
		Radio_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				color_2.setVisible(true);
				cb_5.setVisible(true);
			}
		});
		Radio_4.setText("\u6E10\u53D8");
		Radio_4.setBounds(89, 83, 93, 18);

		label_4 = new Label(group, SWT.NONE);
		label_4.setText("\u989C\u82721:");
		label_4.setBounds(212, 51, 54, 18);

		cb_4 = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		cb_4.setBounds(291, 48, 220, 21);
		
		Label label_5 = new Label(group, SWT.NONE);
		label_5.setText("\u56FE\u50CF:");
		label_5.setBounds(29, 116, 33, 18);

		Image2 = new Label(group, SWT.NONE);
		Image2.setText(" (\u65E0)");
		Image2.setBounds(158, 116, 353, 18);
		Image2.setData("");

		final Label lblNewLabel_71 = new Label(group, SWT.NONE);
		lblNewLabel_71.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// System.out.println(MouseButtons.Left); 左键为1
				// System.out.println(MouseButtons.Right);右键为2
				if (e.button == 1) {
					tubiao = 2;
					popMenu = createPopMenu().createContextMenu(lblNewLabel_71);
					// lblNewLabel_7.setMenu(popMenu); 绑定菜单，点击右键自动触发
					popMenu.setLocation(lblNewLabel_71.toDisplay(e.x, e.y));
					popMenu.setVisible(true);
				}
			}
		});
		lblNewLabel_71.setBounds(119, 116, 33, 18);
		lblNewLabel_71.setText("");
		lblNewLabel_71.setImage(SwtImageConverter.ConvertToSwtImage(
				ImageResolver.get_Resolver().ResolveImage(
						"[IMAGE]Core#Images.Icons.navigate_down.png"), 0x12,
				0x12));

		cb_5 = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		cb_5.setVisible(false);
		cb_5.setBounds(291, 83, 220, 20);

		color_2 = new Label(group, SWT.NONE);
		color_2.setVisible(false);
		color_2.setText("\u989C\u82722:");
		color_2.setBounds(212, 84, 54, 17);
		
		
		
		TabItem tabItem_2 = new TabItem(tabFolder, 0);
		tabItem_2.setText("\u5C5E\u6027");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tabItem_2.setControl(composite_3);
		
		
		table = new Table(composite_3, SWT.BORDER | SWT.FULL_SELECTION);
		table.setBounds(10, 25, 450, 309);
		
		but_UP = new Button(composite_3, SWT.NONE);
		but_UP.setBounds(466, 25, 27, 22);
		but_UP.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.MoveUp16.png"),0x12,0x12));
		but_UP.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(table.getSelectionIndex()==-1)return;
				int selectindex=table.getSelectionIndex();
				if(selectindex-1<0)return ;
				TableItem tabselect=table.getItem(selectindex);
				TableItem tab=table.getItem(selectindex-1);
				LinkDef viewdefselect= (LinkDef) tabselect.getData();
				LinkDef viewdef= (LinkDef) tab.getData();
				tabselect.setText(viewdef.get_Alias());
				tabselect.setData(viewdef);
				tab.setText(viewdefselect.get_Alias());
				tab.setData(viewdefselect);
				tab.setChecked(true);
				tabselect.setChecked(false);
				table.select(selectindex-1);
			}
		});
		
		but_Down = new Button(composite_3, SWT.NONE);
		but_Down.setBounds(466, 46, 27, 22);
		but_Down.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.MoveDown16.png"),0x12,0x12));
		but_Down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table.getSelectionIndex()==-1)return;
				int selectindex=table.getSelectionIndex();
				if(selectindex+1>table.getItemCount()-1)return ;
				TableItem tabselect=table.getItem(selectindex);
				TableItem tab=table.getItem(selectindex+1);
				LinkDef viewdefselect= (LinkDef) tabselect.getData();
				LinkDef viewdef= (LinkDef) tab.getData();
				tabselect.setText(viewdef.get_Alias());
				tabselect.setData(viewdef);
				tab.setText(viewdefselect.get_Alias());
				tab.setData(viewdefselect);
				tab.setChecked(true);
				tabselect.setChecked(false);
				table.select(selectindex+1);
			}
		});
		
		
		but_Add = new Button(composite_3, SWT.NONE);
		but_Add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LinkDefDlg linkdefdlg=new LinkDefDlg(getShell(), m_api, m_Library, m_ScopeUtil, new LinkDef());
				if(linkdefdlg.open()==linkdefdlg.OK){
					TableItem tableIitem=new TableItem(table,SWT.NONE);
			    	tableIitem.setText(linkdefdlg.getLinkDef().get_Alias());
			    	tableIitem.setData(linkdefdlg.getLinkDef());
				}
			}
		});
		but_Add.setText("\u6DFB\u52A0");
		but_Add.setBounds(514, 25, 72, 22);
		
		but_Edit = new Button(composite_3, SWT.NONE);
		but_Edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table.getSelectionIndex()==-1)return;
				TableItem tabselect=table.getItem(table.getSelectionIndex());
				LinkDef viewdefselect= (LinkDef) tabselect.getData();
				LinkDefDlg linkdefdlg=new LinkDefDlg(getShell(), m_api, m_Library, m_ScopeUtil, viewdefselect);
				if(linkdefdlg.open()==linkdefdlg.OK){
					System.out.println();
					tabselect.setText(linkdefdlg.getLinkDef().get_Alias());
					tabselect.setData(linkdefdlg.getLinkDef());
				}
			}
		});
		but_Edit.setText("\u7F16\u8F91");
		but_Edit.setBounds(514, 46, 72, 22);
		
		but_del = new Button(composite_3, SWT.NONE);
		but_del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(table.getSelectionIndex()==-1)return;
				table.remove(table.getSelectionIndex());
				
				
			}
		});
		but_del.setText("\u5220\u9664");
		but_del.setBounds(514, 67, 72, 22);
		
		Checked_ybloading = new Button(composite_3, SWT.CHECK);
		Checked_ybloading.setText("\u652F\u6301\u5F02\u6B65\u52A0\u8F7D");
		Checked_ybloading.setBounds(10, 348, 107, 16);
		
		Checked_showrefresh = new Button(composite_3, SWT.CHECK);
		Checked_showrefresh.setText("\u663E\u793A\u5237\u65B0\u6309\u94AE");
		Checked_showrefresh.setBounds(10, 370, 107, 16);
		
		Label label_9 = new Label(composite_3, SWT.NONE);
		label_9.setText("\u5237\u65B0\u95F4\u9694");
		label_9.setBounds(10, 402, 61, 16);
		
		cb_refreshinterval = new CCombo(composite_3, SWT.BORDER);
		cb_refreshinterval.setEditable(false);
		cb_refreshinterval.setBounds(135, 399, 151, 20);
		cb_refreshinterval.setText("\u65E0");
		
		Fillcb_refreshinterval();
		generals=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, m_Def, linkstr, cbScope,cbCategory,cbOwner,cbLinkedTo);
		generals.loadYb();
		load();
		initColorCombo();
		return container;
	}
	
	private MenuManager createPopMenu() {
		MenuManager menuManager = new MenuManager();
		// menuManager.setRemoveAllWhenShown(true);
		Action look = new Action("浏览(B)...") {
			@Override
			public void run() {
				String str = "";
				if (tubiao == 1) {
					str = imageAddrLabel.getData().toString();
				} else {
					str = Image2.getData().toString();
				}
				ImageChooseDlg imageC = new ImageChooseDlg(getShell(),
						m_Library, m_api, str);
				if (imageC.open() == 0) {

					if (tubiao == 1) {
						imageAddrLabel.setText(imageC.getImageAdr());
						imageAddrLabel.setData(imageC.getImageAdrDate());
					} else {
						Image2.setText(imageC.getImageAdr());
						Image2.setData(imageC.getImageAdrDate());
					}

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
	/**
	 *  第一次加载数据
	 */
	
	public void load(){
		
		if(dashboardpartdef == null || "New".equals(type)){
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
		
		Checked_ybloading.setSelection(dashboardpartdef.get_SupportAsyncLoading());
		Checked_showrefresh.setSelection(dashboardpartdef.get_ShowRefreshButton());
		int tim=(int) dashboardpartdef.get_RefreshFrequencyTimeSpan().get_TotalMinutes();
		if(tim!=0)cb_refreshinterval.setText(tim+"分钟");
		
		IEnumerator it = linklistpartdef.get_LinkDefs().GetEnumerator();
	    while(it.MoveNext()){
	    	LinkDef def=(LinkDef) it.get_Current();
	    	TableItem tableIitem=new TableItem(table,SWT.NONE);
	    	tableIitem.setText(def.get_Alias());
	    	tableIitem.setData(def);
	    }
	    
	    cbCategory.setText(dashboardpartdef.get_Folder());
		txtDesc.setText(dashboardpartdef.get_Description());

		// 外观
		txtText.setText(dashboardpartdef.get_TitleBarDef().get_Text());
		if (!StringUtils.IsEmpty(dashboardpartdef.get_TitleBarDef()
				.get_ImageName())) {
			imageAddrLabel.setText(ImageChooseDlg.getInitImageStr(dashboardpartdef.get_TitleBarDef()
					.get_ImageName()));
			imageAddrLabel.setData(dashboardpartdef.get_TitleBarDef()
					.get_ImageName());
		}
		Checked_Date.setSelection(dashboardpartdef.get_TitleBarDef()
				.get_ShowDate());
		Checked_SystemYs.setSelection(dashboardpartdef.get_TitleBarDef()
				.get_UseParentColor());
//
//		cb_1.setText(dashboardpartdef.get_TitleBarDef().get_TextForeColor());
//		cb_2.setText(dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
//				.get_FirstColor());
//		cb_3.setText(dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
//				.get_SecondColor());
//		cb_4.setText(dashboardpartdef.get_BackgroundDef().get_FirstColor());
//		cb_5.setText(dashboardpartdef.get_BackgroundDef().get_SecondColor());

		if (!StringUtils.IsEmpty(dashboardpartdef.get_BackgroundDef()
				.get_ImageName())) {
			Image2.setText(ImageChooseDlg.getInitImageStr(dashboardpartdef.get_BackgroundDef().get_ImageName()));
			Image2.setData(dashboardpartdef.get_BackgroundDef().get_ImageName());
		}

		if (!dashboardpartdef.get_TitleBarDef().get_UseParentColor()) {
			cb_1.setEnabled(true);
			cb_2.setEnabled(true);
			cb_3.setEnabled(true);
			Radio_1.setEnabled(true);
			Radio_2.setEnabled(true);
			label_2.setEnabled(true);
			colorLb2.setEnabled(true);
		}
		if (dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
				.get_IsSolidColor()) {
			Radio_1.setSelection(true);
			Radio_2.setSelection(false);
			if (!dashboardpartdef.get_TitleBarDef().get_UseParentColor()) {
				cb_2.setEnabled(true);
			}
		}
		if (dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
				.get_IsLeftToRightGradient()) {
			if (!dashboardpartdef.get_TitleBarDef().get_UseParentColor()) {
				colorLb2.setEnabled(true);
				cb_3.setEnabled(true);
			}
			Radio_1.setSelection(false);
			Radio_2.setSelection(true);
			colorLb2.setVisible(true);
			cb_3.setVisible(true);
		}

		if (dashboardpartdef.get_BackgroundDef().get_IsSolidColor()) {
			Radio_3.setSelection(true);
			Radio_4.setSelection(false);
			cb_4.setEnabled(true);
		}

		if (dashboardpartdef.get_BackgroundDef().get_IsLeftToRightGradient()) {
			Radio_3.setSelection(false);
			Radio_4.setSelection(true);
			cb_5.setEnabled(true);
			color_2.setEnabled(true);
			cb_5.setVisible(true);
			color_2.setVisible(true);
		}
	    
	}
	
	/**
	 * 取 下拉式的 背景颜色
	 */
	private void initColorCombo() {

		String foreColor = dashboardpartdef.get_TitleBarDef().get_TextForeColor();
		String color1 = dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
				.get_FirstColor();
		String color2 = dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
				.get_SecondColor();
		String color3 = dashboardpartdef.get_BackgroundDef().get_FirstColor();
		String color4 = dashboardpartdef.get_BackgroundDef().get_SecondColor();
		
		
		ColorComboInit.Init(cb_1, foreColor);
		ColorComboInit.Init(cb_2, color1);
		ColorComboInit.Init(cb_3, color2);
		ColorComboInit.Init(cb_4, color3);
		ColorComboInit.Init(cb_5, color4);
		
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
	 
	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	@Override
	protected void okPressed() {
		String name = txtName.getText().trim();

		// 添加

		if (StringUtils.IsEmpty(name)) {
			MessageDialog.openInformation(getParentShell(), "提示",
					"请为此  仪表盘部件  输入名称。");
			return;
		} else if ("Edit".equals(type)) {
			if (!name.equals(dashboardpartdef.get_Name())) {
				if (m_Library.IsDuplicateName(DefRequest.ForDupeCheck(
						dashboardpartdef.get_InstanceClassName(),
						dashboardpartdef.get_Id(), dashboardpartdef.get_Scope(),
						dashboardpartdef.get_ScopeOwner(), name))) {
					MessageDialog.openInformation(getParentShell(), "提示",
							"找到相同名称,请为此  仪表盘部件  输入其它名称. ");
					txtName.forceFocus();
					return;
				}
			}
		} else {
			if (m_Library.IsDuplicateName(DefRequest.ForDupeCheck(
					m_Def.get_InstanceClassName(), m_Def.get_Id(),
					m_Def.get_Scope(), m_Def.get_ScopeOwner(), name))) {
				MessageDialog.openInformation(getParentShell(), "提示",
						"找到相同名称,请为此  仪表盘部件  输入其它名称. ");
				txtName.forceFocus();
				return;
			}
		}
		dashboardpartdef.set_Alias(txtAlias.getText().trim());
		dashboardpartdef.set_Name(txtName.getText().trim());
		dashboardpartdef.set_Description(txtDesc.getText().trim());
		dashboardpartdef.set_Scope((Integer) cbScope.getData(cbScope
				.getItem(cbScope.getSelectionIndex())));
		dashboardpartdef.set_ScopeOwner((String) cbOwner.getData(cbOwner
				.getItem(cbOwner.getSelectionIndex())));
		System.out.println(dashboardpartdef.get_ScopeOwner() + "----" + (String)cbOwner.getData(cbOwner
				.getItem(cbOwner.getSelectionIndex())));

		if (!StringUtils.IsEmpty(cbCategory.getText())) {
			dashboardpartdef.set_Folder(cbCategory.getText());
		} else {
			dashboardpartdef.set_Folder("");
		}
		dashboardpartdef.set_LinkedTo((String) cbLinkedTo.getData(cbLinkedTo
				.getItem(cbLinkedTo.getSelectionIndex())));


		// 保存外观数据
		TitleBarDef titlebardef = dashboardpartdef.get_TitleBarDef();
		titlebardef.set_Text(txtText.getText());
		titlebardef.set_ImageName(imageAddrLabel.getData().toString());
		titlebardef.set_ShowDate(Checked_Date.getSelection());
		titlebardef.set_UseParentColor(Checked_SystemYs.getSelection());
		titlebardef.set_TextForeColor(cb_1.getData(cb_1.getText()).toString());
		titlebardef.get_BackgroundDef()
				.set_IsSolidColor(Radio_1.getSelection());
		titlebardef.get_BackgroundDef().set_IsLeftToRightGradient(
				Radio_2.getSelection());
		titlebardef.get_BackgroundDef()
				.set_FirstColor(
						cb_2.getData(cb_2.getText())
								.toString());
		titlebardef.get_BackgroundDef()
				.set_SecondColor(
						cb_3.getData(cb_3.getText())
								.toString());

		dashboardpartdef.get_BackgroundDef().set_IsSolidColor(
				Radio_3.getSelection());
		dashboardpartdef.get_BackgroundDef().set_IsLeftToRightGradient(
				Radio_4.getSelection());
		dashboardpartdef.get_BackgroundDef()
				.set_FirstColor(
						cb_4.getData(cb_4.getText())
								.toString());
		dashboardpartdef.get_BackgroundDef()
				.set_SecondColor(
						cb_5.getData(cb_5.getText())
								.toString());
		dashboardpartdef.get_BackgroundDef().set_ImageName(
				Image2.getData().toString());
			
			dashboardpartdef.set_ShowRefreshButton(Checked_showrefresh.getSelection());
			dashboardpartdef.set_SupportAsyncLoading(Checked_ybloading.getSelection());
			if(!StringUtils.IsEmpty(cb_refreshinterval.getText())){
				int timeint=(Integer) cb_refreshinterval.getData(cb_refreshinterval.getText());
				dashboardpartdef.set_RefreshFrequency(timeint);
			}
		
			linklistpartdef.ClearLinkDefs();
			for(int i=0;i<table.getItemCount();i++){
				linklistpartdef.AddLinkDef((LinkDef)table.getItem(i).getData());
			}
			
			dashboardpartdef.set_SupportAsyncLoading(true);
            Convert.ToInt32(dashboardpartdef.get_RefreshFrequencyTimeSpan().get_TotalMinutes());
            dashboardpartdef.set_RefreshFrequencyTimeUnit(TimeUnit.Minutes);
            dashboardpartdef.set_RefreshFrequency(1);
            dashboardpartdef.set_ShowRefreshButton(false);
			
			
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
