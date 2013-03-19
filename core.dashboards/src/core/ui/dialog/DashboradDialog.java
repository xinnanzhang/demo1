package core.ui.dialog;

import java.util.Collections;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.IList;
import Core.Dashboards.DashboardDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.TitleBarDef;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.PlaceHolder;
import Siteview.StringUtils;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.PerspectiveDef;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;
import Siteview.Xml.DashboardPartCategory;
import Siteview.Xml.Scope;
import Siteview.Xml.SecurityRight;
import Siteview.Xml.XmlDashboardPartCategory;
import Siteview.Xml.XmlScope;
import Siteview.Xml.XmlSecurityRight;
import core.dashboards.dialogs.Dashboradlayouts;
import core.ui.DashBoardCenterForm;
import core.ui.DashBoardPartCenterForm;


public class DashboradDialog extends Dialog {
	private Text txtName; // 名称
	private Text txtDesc;// 说明
	private Text txtText;
	private Combo cbScope; // 范围下拉式
	private Combo cbCategory; // 类别下拉式
	private Combo cbOwner; // 负责人下拉式
	private Combo cbScope_1; // 范围下拉式
	private Combo cbOwner_1; // 负责人下拉式
	private Combo cbLinkedTo; // 关联下拉式
	private Button Checked_Dh;
	private Button Checked_Cd;
	private Button Checked_SystemYs;
	private Button Checked_allrowcoulum;
	private Menu popMenu;
	private Button Radio_1;
	private Button Radio_2;
	private Button Radio_3;
	private Button Radio_4;
	private Button Checked_Date;

	private Label colorLb2;
	private Label color_2;
	private Label label_4;
	private Label label_2;
	private Label Image2;
	private Label imageAddrLabel;
	private List list;
	private Text txtAlias;
	private Spinner sp_1;
	private Spinner sp_2;
	private Spinner sp_3;

	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口
	private PerspectiveDef perspectiveDef;
	private IList m_lstSupportedScopesWithOwners;
	private DashboardDef dashboarddef;
	private String linkstr; // 下拉式 选 项
	private DashBoardCenterForm bashboardcenter;
	// 类别
	private HashSet<String> Categoryset = new HashSet<String>();
	private int type = 1; // 标志位 1=添加 2=编辑 3=复制
	private ICollection allPlaceHolders;
	private Composite composite;

	private int tubiao = 1;
	
	private Dashboradlayouts dashboradlayouts;
	private String layout="RAP";

	private TableCombo composite_Foreground_one;
	private TableCombo composite_Foreground_two;
	private TableCombo composite_Background_one;
	private TableCombo composite_Background_two;
	private TableCombo composite_Foreground;
	
	public boolean isClient = false;
	
	private ScopeUtil part_ScopeUtil;
	
		
	public DashboradDialog(DashBoardCenterForm quickcenter,
			Shell parentShell, DashboardDef dashboarddef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr, int type) {
		super(parentShell);
		this.bashboardcenter = quickcenter;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.dashboarddef = dashboarddef;
		this.m_Def = m_Def;
		this.type = type;
		this.linkstr = linkstr;
		this.m_lstSupportedScopesWithOwners = this.m_ScopeUtil
				.GetSupportedScopesWithOwners(XmlSecurityRight.ToRight("VAED"), true);
		if(m_Library == ConnectionBroker.get_SiteviewApi().get_LiveDefinitionLibrary())  isClient = true;
		
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(760, 660);
		newShell.setLocation(300, 70);
		newShell.setText("新建	仪表板");
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		// container.setSize(700, 800);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(26, 31, 59, 18);
		lblNewLabel.setText("\u540D\u79F0(N):");

		Label lbla = new Label(container, SWT.NONE);
		lbla.setBounds(26, 64, 48, 18);
		lbla.setText("\u522B\u540D(A):");

		txtName = new Text(container, SWT.BORDER);
		txtName.setBounds(91, 28, 400, 18);

		txtAlias = new Text(container, SWT.BORDER);
		txtAlias.setBounds(91, 61, 400, 18);

		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setBounds(26, 98, 703, 476);

		TabItem tabItem_1 = new TabItem(tabFolder, SWT.NONE);
		tabItem_1.setText("\u4E00\u822C");

		Composite general = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(general);

		Label lblNewLabel_1 = new Label(general, SWT.NONE);
		lblNewLabel_1.setBounds(29, 23, 54, 21);
		lblNewLabel_1.setText("\u8BF4\u660E(D):");

		txtDesc = new Text(general, SWT.BORDER);
		txtDesc.setBounds(130, 20, 530, 43);

		Label lblNewLabel_2 = new Label(general, SWT.NONE);
		lblNewLabel_2.setBounds(29, 83, 54, 21);
		lblNewLabel_2.setText("\u8303\u56F4(S):");

		cbScope = new Combo(general, SWT.READ_ONLY);
		cbScope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbOwnerdate(cbScope.getData(cbScope.getItem(cbScope
						.getSelectionIndex())), 1);
				FillScope_1();
				cbOwnerdate(cbScope_1.getData(cbScope_1.getItem(cbScope_1
						.getSelectionIndex())), 2);
				int scope = (Integer) cbScope_1.getData(cbScope_1
						.getItem(cbScope_1.getSelectionIndex()));
				String scopeowner = (String) cbOwner_1.getData(cbOwner_1
						.getItem(cbOwner_1.getSelectionIndex()));
				FillList(scope, scopeowner);
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
					cbLinkedToSelected(cbLinkedTo.getItem(cbLinkedTo
							.getSelectionIndex()));
				}
			}
		});
		
		
		cbLinkedTo.setBounds(130, 183, 281, 21);

		Checked_Cd = new Button(general, SWT.CHECK);
		Checked_Cd.setBounds(29, 246, 150, 16);
		Checked_Cd.setText("\u4F5C\u4E3A\u83DC\u5355\u9879\u663E\u793A");

		Checked_Dh = new Button(general, SWT.CHECK);
		Checked_Dh.setBounds(29, 288, 150, 16);
		Checked_Dh.setText("\u5728\u5BFC\u822A\u5668\u4E2D\u663E\u793A");

		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("\u5916\u89C2");

		Composite face = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(face);

		final Group grpt = new Group(face, SWT.NONE);
		grpt.setLocation(10, 10);
		grpt.setText("\u6807\u9898(T)");
		grpt.setSize(675, 261);

		Label lblNewLabel_4 = new Label(grpt, SWT.NONE);
		lblNewLabel_4.setBounds(31, 28, 54, 18);
		lblNewLabel_4.setText("\u6587\u672C:");

		txtText = new Text(grpt, SWT.BORDER);
		txtText.setBounds(114, 25, 364, 18);

		Checked_Date = new Button(grpt, SWT.CHECK);
		Checked_Date.setSelection(true);
		Checked_Date.setBounds(514, 25, 93, 18);
		Checked_Date.setText("\u663E\u793A\u65E5\u671F");

		Label lblNewLabel_5 = new Label(grpt, SWT.NONE);
		lblNewLabel_5.setBounds(31, 70, 33, 18);
		lblNewLabel_5.setText("\u56FE\u6807:");

		Checked_SystemYs = new Button(grpt, SWT.CHECK);
		Checked_SystemYs.setSelection(true);
		Checked_SystemYs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Checked_SystemYs.getSelection()) {
					composite_Foreground.setEnabled(false);
					Radio_1.setEnabled(false);
					Radio_2.setEnabled(false);
					composite_Foreground_one.setEnabled(false);
					composite_Foreground_two.setEnabled(false);
					label_2.setEnabled(false);
					colorLb2.setEnabled(false);
				} else {
					composite_Foreground.setEnabled(true);
					Radio_1.setEnabled(true);
					Radio_2.setEnabled(true);
					composite_Foreground_one.setEnabled(true);
					composite_Foreground_two.setEnabled(true);
					label_2.setEnabled(true);
					colorLb2.setEnabled(true);
					if (Radio_2.getSelection()) {
						composite_Foreground_two.setVisible(true);
						label_2.setVisible(true);
					}
				}
			}
		});
		Checked_SystemYs.setBounds(30, 106, 137, 18);
		Checked_SystemYs.setText("\u4F7F\u7528\u7CFB\u7EDF\u989C\u8272");

		Label label = new Label(grpt, SWT.NONE);
		label.setText("\u524D\u666F\u8272:");
		label.setBounds(233, 138, 54, 18);

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
				composite_Foreground_two.setVisible(false);
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
					composite_Foreground_two.setVisible(true);
				}
			}
		});
		Radio_2.setText("\u6E10\u53D8");
		Radio_2.setBounds(91, 220, 93, 18);

		label_2 = new Label(grpt, SWT.NONE);
		label_2.setEnabled(false);
		label_2.setText("\u989C\u82721:");
		label_2.setBounds(212, 185, 54, 18);
		imageAddrLabel = new Label(grpt, SWT.NONE);
		imageAddrLabel.setBounds(149, 70, 329, 18);
		imageAddrLabel.setText(" (\u65E0)");
		imageAddrLabel.setData("");
		colorLb2 = new Label(grpt, SWT.NONE);
		colorLb2.setEnabled(false);
		colorLb2.setText("\u989C\u82722:");
		colorLb2.setBounds(212, 223, 54, 17);
		colorLb2.setVisible(false);

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
		
		composite_Foreground = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		composite_Foreground.setBounds(293, 135, 200, 20);
		
		
		composite_Foreground_one = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		composite_Foreground_one.setBounds(293, 183, 200, 20);
		
		composite_Foreground_two = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		composite_Foreground_two.setBounds(293, 220, 200, 20);
		
		
		Group group = new Group(face, SWT.NONE);
		group.setText("\u80CC\u666F");
		group.setBounds(10, 277, 675, 162);

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
				composite_Background_two.setVisible(false);
			}
		});
		Radio_4 = new Button(group, SWT.RADIO);
		Radio_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				color_2.setVisible(true);
				composite_Background_two.setVisible(true);
			}
		});
		Radio_4.setText("\u6E10\u53D8");
		Radio_4.setBounds(89, 83, 93, 18);

		label_4 = new Label(group, SWT.NONE);
		label_4.setText("\u989C\u82721:");
		label_4.setBounds(212, 51, 54, 18);
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
		color_2 = new Label(group, SWT.NONE);
		color_2.setVisible(false);
		color_2.setText("\u989C\u82722:");
		color_2.setBounds(212, 84, 54, 17);
		
		composite_Background_one = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		composite_Background_one.setBounds(292, 46, 200, 20);
		
		composite_Background_two = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		composite_Background_two.setBounds(292, 81, 200, 20);
		

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u5E03\u5C40");

		Composite buju = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(buju);
		buju.setLayout(new FormLayout());

		Group group_1 = new Group(buju, SWT.NONE);
		group_1.setLayout(null);
		FormData fd_group_1 = new FormData();
		fd_group_1.top = new FormAttachment(0, 10);
		fd_group_1.left = new FormAttachment(0, 10);
		group_1.setLayoutData(fd_group_1);
		group_1.setText("\u4EEA\u8868\u677F\u5E03\u5C40");
		
		

		Label lblNewLabel_8 = new Label(group_1, SWT.NONE);
		lblNewLabel_8.setBounds(20, 342, 54, 20);
		lblNewLabel_8.setText("\u884C\u6570:");

		Label lblNewLabel_9 = new Label(group_1, SWT.NONE);
		lblNewLabel_9.setBounds(228, 342, 74, 20);
		lblNewLabel_9.setText("\u5206\u9694\u7EBF\u7C97\u7EC6:");

		Label lblNewLabel_10 = new Label(group_1, SWT.NONE);
		lblNewLabel_10.setBounds(20, 372, 54, 22);
		lblNewLabel_10.setText("\u5217\u6570:");

		Checked_allrowcoulum = new Button(group_1, SWT.CHECK);
		Checked_allrowcoulum.setEnabled(false);
		Checked_allrowcoulum.setSelection(true);
		Checked_allrowcoulum.setBounds(20, 400, 312, 21);
		Checked_allrowcoulum
				.setText("\u6240\u6709\u5217\u548C\u6240\u6709\u884C\u90FD\u6709\u76F8\u540C\u7684\u5927\u5C0F");

		sp_1 = new Spinner(group_1, SWT.BORDER);
		sp_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dashboradlayouts.setX(sp_1.getSelection());
				dashboradlayouts.setY(sp_2.getSelection());
				dashboradlayouts.Fillxt();
				dashboradlayouts.FillTable();
			}
		});
		sp_1.setBounds(110, 339, 70, 21);
		sp_1.setMaximum(5);
		sp_1.setMinimum(1);
		sp_1.setSelection(3);

		sp_3 = new Spinner(group_1, SWT.BORDER);
		sp_3.setBounds(308, 339, 70, 21);
		sp_3.setMaximum(10);
		sp_3.setSelection(5);

		sp_2 = new Spinner(group_1, SWT.BORDER);
		sp_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dashboradlayouts.setX(sp_1.getSelection());
				dashboradlayouts.setY(sp_2.getSelection());
				dashboradlayouts.Fillxt();
				dashboradlayouts.FillTable();
			}
		});
		sp_2.setBounds(110, 369, 70, 21);
		sp_2.setMaximum(5);
		sp_2.setMinimum(1);
		sp_2.setSelection(3);

		Group group_2 = new Group(buju, SWT.NONE);
		fd_group_1.bottom = new FormAttachment(group_2, 0, SWT.BOTTOM);
		fd_group_1.right = new FormAttachment(group_2, -6);

		composite = new Composite(group_1, SWT.BORDER);
		composite.setBounds(10, 48, 400, 280);
		
		dashboradlayouts=new Dashboradlayouts(composite, m_api, m_Library, m_ScopeUtil);
		layout=Dashboradlayouts.layout;
		
		Label lblNewLabel_6 = new Label(group_1, SWT.WRAP);
		lblNewLabel_6.setForeground(new Color(null, 255, 0, 0));
		lblNewLabel_6.setBounds(10, 22, 390, 17);
		if("RCP".equals(layout)){
			lblNewLabel_6
			.setText("将部件从列表中拖到布局部分, 双击部件可改变部件的大小和位置.");
		}else{
			
			lblNewLabel_6
			.setText("温馨提示:此Web版本只提供查看功能,需要设置布局请使用客户端版本.");
		}
		
		
		FormData fd_group_2 = new FormData();
		fd_group_2.bottom = new FormAttachment(0, 441);
		fd_group_2.right = new FormAttachment(0, 685);
		fd_group_2.top = new FormAttachment(0, 10);
		fd_group_2.left = new FormAttachment(0, 435);

		group_2.setLayoutData(fd_group_2);
		group_2.setText("\u4EEA\u8868\u677F\u90E8\u4EF6");

		Label lblNewLabel_11 = new Label(group_2, SWT.NONE);
		lblNewLabel_11.setBounds(22, 30, 54, 20);
		lblNewLabel_11.setText("\u8303\u56F4:");

		Label lblNewLabel_12 = new Label(group_2, SWT.NONE);
		lblNewLabel_12.setBounds(22, 59, 54, 20);
		lblNewLabel_12.setText("\u8D1F\u8D23\u4EBA:");

		list = new List(group_2, SWT.BORDER | SWT.V_SCROLL);
		list.setBounds(10, 88, 230, 298);

		list.setData("dfd");
		Link link = new Link(group_2, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createDashboardPart();
			}
		});
		link.setBounds(10, 402, 136, 15);
		link.setText("<a>\u6DFB\u52A0\u90E8\u4EF6...</a>");

		cbScope_1 = new Combo(group_2, SWT.READ_ONLY);
		cbScope_1.setEnabled(false);
		cbScope_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbOwnerdate(cbScope_1.getData(cbScope_1.getItem(cbScope_1
						.getSelectionIndex())), 2);
				int scope = (Integer) cbScope_1.getData(cbScope_1
						.getItem(cbScope_1.getSelectionIndex()));
				String scopeowner = (String) cbOwner_1.getData(cbOwner_1
						.getItem(cbOwner_1.getSelectionIndex()));
				FillList(scope, scopeowner);
			}
		});
		cbScope_1.setBounds(95, 27, 145, 20);

		cbOwner_1 = new Combo(group_2, SWT.READ_ONLY);
		cbOwner_1.setEnabled(false);
		cbOwner_1.setBounds(95, 56, 145, 20);
	
		
		
		if("RCP".equals(layout)){
			dashboradlayouts.datemove(list, getShell());
		}else{
			sp_1.setEnabled(false);
			sp_2.setEnabled(false);
			sp_3.setEnabled(false);
			Checked_allrowcoulum.setEnabled(false);
		}
		loaddate();
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
				if (tubiao == 1) {
					imageAddrLabel.setText("(无)");
					imageAddrLabel.setData("");
				} else {
					Image2.setText("无");
					imageAddrLabel.setData("");
				}

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



	/**
	 * 范围 类别,关联 下拉式 数据加载
	 */
	private void loaddate() {

		boolean useParentColor = false;
		boolean isSolidColor_fore = true;
		boolean isSolidColor_back = true;
		
		
		// 范围数据加载
		ICollection supportedScopes = m_ScopeUtil.GetSupportedScopes(SecurityRight.Add);
		IEnumerator it = supportedScopes.GetEnumerator();
		while (it.MoveNext()) {
			int ph = (Integer) it.get_Current();
			String item = m_ScopeUtil.ScopeToString(ph);
			cbScope.setData(item, ph);
			cbScope.add(item);
			if (dashboarddef.get_Scope() == ph) {
				cbScope.setText(item);
			}
		}
		FillScope_1();
		cbOwnerdate(
				cbScope.getData(cbScope.getItem(cbScope.getSelectionIndex())),
				0);

		// 关联数据加载
		ICollection busLinks = m_Library.GetPlaceHolderList(DefRequest
				.ForList(BusinessObjectDef.get_ClassName()));
		IEnumerator it1 = busLinks.GetEnumerator();
		Vector<String> bobs = new Vector<String>();
		cbLinkedTo.add(" 未关联");
		cbLinkedTo.setData(" 未关联", "");
		while (it1.MoveNext()) {
			PlaceHolder ph = (PlaceHolder) it1.get_Current();

			if (ph.HasFlag("Master") && !ph.HasFlag("AllowDerivation")) {
				cbLinkedTo.add(ph.get_Alias());
				cbLinkedTo.setData(ph.get_Alias(), ph.get_Name());
				if (ph.get_Name().equals(linkstr)) {
					cbLinkedTo.setText(ph.get_Alias());
					getcboQuickOblink(ph.get_Alias());
				}
			}

		}

		if ("".equals(cbLinkedTo.getText())) {
			cbLinkedTo.select(0);
		}

		// 类别数据加载

		Iterator ite = Categoryset.iterator();
		while (ite.hasNext()) {
			String str = (String) ite.next();
			if (!StringUtils.IsEmpty(str)) {
				cbCategory.add(str);
				cbCategory.setData(str, str);
			}
		}

		allPlaceHolders = m_Library.GetAllPlaceHolders(
				DashboardPartDef.get_ClassName(), "(Base)");

		int scope = (Integer) cbScope.getData(cbScope.getItem(cbScope
				.getSelectionIndex()));
		String scopeowner = (String) cbOwner.getData(cbOwner.getItem(cbOwner
				.getSelectionIndex()));
		FillList(scope, scopeowner);

		if (dashboarddef != null && type != 1) {

			if (2 == type) {
				txtName.setText(dashboarddef.get_Name());
				txtAlias.setText(dashboarddef.get_Alias());
			} else {
				txtName.setText(dashboarddef.get_Name() + "  的副本");
				txtAlias.setText(dashboarddef.get_Alias() + "  的副本");
			}

			cbCategory.setText(dashboarddef.get_Folder());
			txtDesc.setText(dashboarddef.get_Description());
			Checked_Cd.setSelection(dashboarddef.get_ShowOnMenu());
			Checked_Dh.setSelection(dashboarddef.get_ShowInNavigator());

			// 外观
			txtText.setText(dashboarddef.get_TitleBarDef().get_Text());
			if (!StringUtils.IsEmpty(dashboarddef.get_TitleBarDef()
					.get_ImageName())) {
				imageAddrLabel.setText(ImageChooseDlg.getInitImageStr(dashboarddef.get_TitleBarDef().get_ImageName()));
				imageAddrLabel.setData(dashboarddef.get_TitleBarDef()
						.get_ImageName());
			}
			Checked_Date.setSelection(dashboarddef.get_TitleBarDef()
					.get_ShowDate());
			Checked_SystemYs.setSelection(dashboarddef.get_TitleBarDef()
					.get_UseParentColor());

			if (!StringUtils.IsEmpty(dashboarddef.get_BackgroundDef()
					.get_ImageName())) {
				Image2.setText(ImageChooseDlg.getInitImageStr(dashboarddef.get_BackgroundDef().get_ImageName()));
				Image2.setData(dashboarddef.get_BackgroundDef().get_ImageName());
			}

			
			useParentColor = !dashboarddef.get_TitleBarDef().get_UseParentColor();
			isSolidColor_fore = dashboarddef.get_TitleBarDef().get_BackgroundDef().get_IsSolidColor();
			isSolidColor_back = dashboarddef.get_BackgroundDef().get_IsSolidColor();
			
			
//			if (dashboarddef.get_TitleBarDef().get_BackgroundDef()
//					.get_IsSolidColor()) {
//				Radio_1.setSelection(true);
//				Radio_2.setSelection(false);
//				if (!dashboarddef.get_TitleBarDef().get_UseParentColor()) {
//					composite_Foreground_one.setEnabled(true);
//					composite_Foreground_two.setVisible(false);
//				}
//				composite_Foreground_two.setVisible(false);
//			}
//			if (dashboarddef.get_TitleBarDef().get_BackgroundDef()
//					.get_IsLeftToRightGradient()) {
//				if (!dashboarddef.get_TitleBarDef().get_UseParentColor()) {
//					colorLb2.setEnabled(true);
//					composite_Foreground_two.setEnabled(true);
//				}
//				Radio_1.setSelection(false);
//				Radio_2.setSelection(true);
//				colorLb2.setVisible(true);
//				composite_Foreground_two.setVisible(true);
//			}
			

//			if (dashboarddef.get_BackgroundDef().get_IsSolidColor()) {
//				Radio_3.setSelection(true);
//				Radio_4.setSelection(false);
//				composite_Background_one.setEnabled(true);
//				composite_Background_two.setVisible(false);
//			}

//			if (dashboarddef.get_BackgroundDef().get_IsLeftToRightGradient()) {
//				Radio_3.setSelection(false);
//				Radio_4.setSelection(true);
//				composite_Background_two.setEnabled(true);
//				color_2.setEnabled(true);
//				composite_Background_two.setVisible(true);
//				color_2.setVisible(true);
//			}

			// 布局数据填充
			sp_1.setSelection(dashboarddef.get_TableSizeDef().get_RowCount());
			dashboradlayouts.setX(dashboarddef.get_TableSizeDef().get_RowCount());
			dashboradlayouts.setY(dashboarddef.get_TableSizeDef().get_ColumnCount());
			sp_2.setSelection(dashboarddef.get_TableSizeDef().get_ColumnCount());
			sp_3.setSelection(dashboarddef.get_TableSizeDef()
					.get_SeparatorThickness());
			Checked_allrowcoulum.setSelection(dashboarddef.get_TableSizeDef()
					.get_IsEqualCellSize());

			
			// 得到表格里的数据
			ICollection partrefdefs = dashboarddef.get_PartRefDefs();
			IEnumerator it2 = partrefdefs.GetEnumerator();
			while (it2.MoveNext()) {
				PartRefDef partrefdef = (PartRefDef) it2.get_Current();
				final CLabel nowlabel = new CLabel(composite, SWT.BORDER | SWT.CENTER
						| SWT.SHADOW_NONE | SWT.WRAP);
				
				PartRefDef partref=(PartRefDef) partrefdef.CloneForEdit();
				
				DashboardPartDef definition = null;
//				if(partref.get_Name().equals(partref.get_Alias())){
					definition = (DashboardPartDef) m_api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(Scope.User, m_api.get_SystemFunctions().get_CurrentLoginId(), DashboardPartDef.get_ClassName(), partref.get_Id(), true));
					if(definition == null)
					{
						definition = (DashboardPartDef) m_api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ByName(partref.get_Scope(), partref.get_ScopeOwner(), DashboardPartDef.get_ClassName(), partref.get_Name()));
						if(definition!=null){
							nowlabel.setData("DashboardPart", definition);
						}
					}else
					{
						partref.set_Alias(definition.get_Alias());
						nowlabel.setData("DashboardPart", definition);
					}
//				}
				
				nowlabel.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseUp(MouseEvent e) {
						if(e.button == 3){
							if(isClient)
							{
								popMenu = createEditPopMenu(nowlabel, getShell()).createContextMenu(nowlabel);
								popMenu.setLocation(nowlabel.toDisplay(e.x,e.y));
								popMenu.setVisible(true);
							}
						}
					}
				});
				
				partref.set_Alias((StringUtils.IsEmpty(partref.get_Alias())) ? partref.get_Name() : partref.get_Alias());
				nowlabel.setText(partrefdef.get_Alias());
				nowlabel.setBackground(new Color[] {
						new Color(null, 0, 0, 255),
						new Color(null, 202, 225, 245) }, new int[] { 100 },
						false);
				java.util.ArrayList<Object> obs = new java.util.ArrayList<Object>();
				obs.add(partrefdef.get_StartColumn());
				obs.add(partrefdef.get_EndColumn());
				obs.add(partrefdef.get_StartRow());
				obs.add(partrefdef.get_EndRow());
				obs.add(partref);
				dashboradlayouts.getHashmap().put(nowlabel, obs);
				if("RCP".equals(layout)){
					dashboradlayouts.moveLabel(nowlabel);
					dashboradlayouts.sizeLabel(nowlabel,getShell());
				}
			}

		}
		
		composite_Foreground.setEnabled(useParentColor);
		composite_Foreground_one.setEnabled(useParentColor);
		composite_Foreground_two.setVisible(!isSolidColor_fore);
		colorLb2.setVisible(!isSolidColor_fore);
		composite_Foreground_two.setEnabled(useParentColor);
		Radio_1.setEnabled(useParentColor);
		Radio_2.setEnabled(useParentColor);
		Radio_1.setSelection(isSolidColor_fore);
		Radio_2.setSelection(!isSolidColor_fore);
		label_2.setEnabled(useParentColor);
		colorLb2.setEnabled(useParentColor);
		
		
		Radio_3.setSelection(isSolidColor_back);
		Radio_4.setSelection(!isSolidColor_back);
		color_2.setVisible(!isSolidColor_back);
		composite_Background_two.setVisible(!isSolidColor_back);
		
		
		dashboradlayouts.Fillxt();
		dashboradlayouts.FillTable();

	}
	
	private MenuManager createEditPopMenu(final CLabel label, final Shell shell) {
		MenuManager menuManager = new MenuManager();
		// menuManager.setRemoveAllWhenShown(true);
		Action editor = new Action("编辑...") {
			@Override
			public void run() {
				editDashBoardPart(label, shell);
			}
		};

		menuManager.add(editor);

		return menuManager;
	}
	
	
	private void editDashBoardPart(CLabel label, Shell shell)
	{
		DashboardPartDef definition = (DashboardPartDef)label.getData("DashboardPart");
		if(definition == null)
		{
			MessageDialog.openInformation(shell, "仪表盘", "此仪表盘部件不存在，或已删除！");
			return;
		}
		if (HasEditRight(definition.get_Scope()))
		{
			if(part_ScopeUtil == null)
			{
				part_ScopeUtil = new ScopeUtil(m_api, m_Library, DashboardPartDef.get_ClassName(), "Core.Security.DashboardParts");
			}
			definition = DashBoardPartCenterForm.getPartType(definition.get_CategoryAsString(), shell, definition, m_Library, part_ScopeUtil, m_api, definition.get_LinkedTo(), "Edit",null);
			
			if(definition != null){
				m_Library.UpdateDefinition(definition, false);
//				Refresh();
				label.setData("DashboardPart", definition);
				PartRefDef partref = (PartRefDef)dashboradlayouts.getHashmap().get(label).get(4);
				
			}
		}else
		{
			//没有权限
			MessageDialog.openInformation(shell, "权限不足", "您没有权限进行此操作！");
		}
	}
	
	
	private boolean HasEditRight(Integer s) {
		if (s != Scope.Unknown){
			return m_api.get_SecurityService().HasModuleItemRight("Core.Security.DashboardParts", XmlScope.CategoryToXmlCategory(s),SecurityRight.Edit);
		}
		return false;
	}
	

	/**
	 * 负责人 加载数据
	 */
	private void cbOwnerdate(Object object, int type) {
		if (type == 0) {
			cbOwner.removeAll();
			cbOwner_1.removeAll();
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, isClient);
			if (list.get_Count() > 0) {
				for (int i = 0; i < list.get_Count(); i++) {
					String own = (String) list.get_Item(i);
					String item = m_ScopeUtil.ScopeOwnerToString(ob, own);
					cbOwner.add(item);
					cbOwner.setData(item, own);
					cbOwner_1.add(item);
					cbOwner_1.setData(item, own);
				}
				cbOwner.select(0);
				cbOwner_1.select(0);
			}
			cbOwner.setEnabled(cbOwner.getItemCount() > 1);
			cbOwner_1.setEnabled(cbOwner.getItemCount() > 1);
			
		} else if (type == 1) {
			cbOwner.removeAll();
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, isClient);
			if (list.get_Count() > 0) {
				for (int i = 0; i < list.get_Count(); i++) {
					String own = (String) list.get_Item(i);
					String item = m_ScopeUtil.ScopeOwnerToString(ob, own);
					cbOwner.add(item);
					cbOwner.setData(item, own);
				}
				cbOwner.select(0);
			}
			cbOwner.setEnabled(cbOwner.getItemCount() > 1);
			
		} else if (type == 2) {
			cbOwner_1.removeAll();
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, isClient);
			if (list.get_Count() > 0) {
				for (int i = 0; i < list.get_Count(); i++) {
					String own = (String) list.get_Item(i);
					String item = m_ScopeUtil.ScopeOwnerToString(ob, own);
					cbOwner_1.add(item);
					cbOwner_1.setData(item, own);
				}
				cbOwner_1.select(0);
			}
			cbOwner_1.setEnabled(cbOwner_1.getItemCount() > 1);
			
		}
	}

	/**
	 * 下拉式点击事件 先初始化清理一些数据
	 */
	protected void cbLinkedToSelected(String str) {
		Categoryset.clear();
		cbCategory.removeAll();
		getcboQuickOblink(str);
		// 类别数据加载

		Iterator<String> ite = Categoryset.iterator();
		while (ite.hasNext()) {
			String str1 = (String) ite.next();
			if (!StringUtils.IsEmpty(str1)) {
				cbCategory.add(str1);
				cbCategory.setData(str1, str1);
			}
		}
	}

	/**
	 * 根据选择下拉式 装载关联的范围和类型
	 */

	public void getcboQuickOblink(String str1) {
		if (StringUtils.IsEmpty(str1))
			return;
		String strBusLink = (String) cbLinkedTo.getData(str1);
		IEnumerator it = m_lstSupportedScopesWithOwners.GetEnumerator();
		while (it.MoveNext()) {
			ScopeUtil.ScopeAndOwners owners = (ScopeUtil.ScopeAndOwners) it
					.get_Current();
			if (owners.get_Owners().get_Count() > 0
					&& owners.get_Scope() != Scope.Global) {
				for (int i = 0; i < owners.get_Owners().get_Count(); i++) {
					String str = (String) owners.get_Owners().get_Item(i);
					OrganizeListerItemsByCategory(owners.get_Scope(), str,
							strBusLink, false);
				}
			} else {
				OrganizeListerItemsByCategory(owners.get_Scope(), "",
						strBusLink, false);
			}

		}
	}

	/**
	 * 对取出来的数据进行分类 保存到 set 集合中
	 * 
	 * @param s
	 *            Scope
	 * @param Owner
	 * @param linkTo
	 *            关联的业务对象的名字
	 * @param bFilter
	 *            是否过滤
	 */
	private void OrganizeListerItemsByCategory(int s, String Owner,
			String linkTo, boolean bFilter) {
		String strPerspective = (this.perspectiveDef != null) ? this.perspectiveDef
				.get_Name() : "(Base)";

		ArrayList obs = (ArrayList) m_Library.GetPlaceHolderList(DefRequest
				.ForList(s, Owner, linkTo, DashboardDef.get_ClassName(),
						strPerspective));
		if (obs != null) {
			for (int i = 0; i < obs.get_Count(); i++) {
				PlaceHolder holder = (PlaceHolder) obs.get_Item(i);
				if (!Categoryset.contains(holder.get_Folder())) {
					Categoryset.add(holder.get_Folder());
				}
			}
		}
	}

	/**
	 * 给布局中 的 范围 填 充数据
	 */
	public void FillScope_1() {
		cbScope_1.removeAll();
		int scope = (Integer) cbScope.getData(cbScope.getItem(cbScope
				.getSelectionIndex()));
		ICollection supportedScopes = m_ScopeUtil.GetSupportedScopes(
				SecurityRight.View, scope);
		if (supportedScopes.get_Count() > 0) {
			IEnumerator it = supportedScopes.GetEnumerator();
			while (it.MoveNext()) {
				Integer sc = (Integer) it.get_Current();
				String item = m_ScopeUtil.ScopeToString(sc);
				cbScope_1.setData(item, sc);
				cbScope_1.add(item);
			}
			cbScope_1.setText(cbScope.getText());
			if (cbScope_1.getItemCount() > 1) {
				cbScope_1.setEnabled(true);
			} else {
				cbScope_1.setEnabled(false);
			}
		}
	}

	/**
	 * 填充布局 中的 LIST数据
	 */
	public void FillList(int scope, String ScopeOwner) {
		if (allPlaceHolders == null)
			return;
		list.removeAll();
		IEnumerator Placs = allPlaceHolders.GetEnumerator();
		PartRefDef item;
		DashboardPartDef def = null;
		int count = 0;
		
		java.util.List<PlaceHolder> bobs = new java.util.ArrayList<PlaceHolder>();
		
		while (Placs.MoveNext()) {
			PlaceHolder holder = (PlaceHolder) Placs.get_Current();
			bobs.add(holder);
//			item = (PartRefDef) DashboardPartDef
//					.DeserializeCreateNewForEditing(XmlDashboardPartCategory
//							.ToString(DashboardPartCategory.PartRef));
//			def = (DashboardPartDef) m_Library.GetDefinition(DefRequest.ById(
//					holder.get_Scope(), holder.get_ScopeOwner(),
//					holder.get_LinkedTo(), DashboardPartDef.get_ClassName(),
//					holder.get_Id()));
//			if (((def != null) && (holder.get_Scope() == scope))
//					&& holder.get_ScopeOwner().toUpperCase()
//							.equals(ScopeOwner.toUpperCase())) {
//				item.CopyContentsForEdit(def);
//				item.set_CategoryAsString("PartRef");
//				item.set_PartCategory(XmlDashboardPartCategory.ToCategory(def.get_CategoryAsString()));
//				item.set_ShowPartType(false);
//				list.add(holder.get_Alias());
//				list.setData(holder.get_Alias(), item);
//			}
		}
		
		java.util.Collections.sort(bobs,new Comparator(){
			@Override
			public int compare(Object o1, Object o2) {
				String s1=((PlaceHolder)o1).get_Alias();
				return 0;
			}
		});
		
		Collections.sort(bobs,new Comparator(){
			 @Override
			 public int compare(Object o1, Object o2) {
				 String s1=((PlaceHolder)o1).get_Alias();
				 String s2=((PlaceHolder)o2).get_Alias();
			 return s1.compareToIgnoreCase(s2);
			 }});
		
		for(PlaceHolder ph:bobs){
			item = (PartRefDef) DashboardPartDef
			.DeserializeCreateNewForEditing(XmlDashboardPartCategory
					.ToString(DashboardPartCategory.PartRef));
			def = (DashboardPartDef) m_Library.GetDefinition(DefRequest.ById(
					ph.get_Scope(), ph.get_ScopeOwner(),
					ph.get_LinkedTo(), DashboardPartDef.get_ClassName(),
					ph.get_Id()));
			if (((def != null) && (ph.get_Scope() == scope))
					&& ph.get_ScopeOwner().toUpperCase()
							.equals(ScopeOwner.toUpperCase())) {
				item.CopyContentsForEdit(def);
				item.set_CategoryAsString("PartRef");
				item.set_PartCategory(XmlDashboardPartCategory.ToCategory(def.get_CategoryAsString()));
				item.set_ShowPartType(false);
				list.add(ph.get_Alias());
				list.setData(ph.get_Alias(), item);
			}
		}
	}

	/**
	 * 取 下拉式的 背景颜色
	 */
	private void initColorCombo() {
		
		ColorComboInit.Init(composite_Foreground_one, dashboarddef.get_TitleBarDef().get_BackgroundDef().get_FirstColor());
		ColorComboInit.Init(composite_Foreground_two, dashboarddef.get_TitleBarDef().get_BackgroundDef().get_SecondColor());
		ColorComboInit.Init(composite_Background_one, dashboarddef.get_BackgroundDef().get_FirstColor());
		ColorComboInit.Init(composite_Background_two, dashboarddef.get_BackgroundDef().get_SecondColor());
		ColorComboInit.Init(composite_Foreground, dashboarddef.get_TitleBarDef().get_TextForeColor());
		
	}


	/**
	 * 点击保存 的处理的事件
	 */

	protected void okPressed() {

		String name = txtName.getText().trim();

		// 添加

		if (StringUtils.IsEmpty(name)) {
			MessageDialog.openInformation(getParentShell(), "提示",
					"请为此  快速操作  输入名称。");
			return;
		} else if (2 == type) {
			if (!name.equals(dashboarddef.get_Name())) {
				if (m_Library.IsDuplicateName(DefRequest.ForDupeCheck(
						dashboarddef.get_InstanceClassName(),
						dashboarddef.get_Id(), dashboarddef.get_Scope(),
						dashboarddef.get_ScopeOwner(), name))) {
					MessageDialog.openInformation(getParentShell(), "提示",
							"找到相同名称,请为此  快速操作  输入其它名称. ");
					txtName.forceFocus();
					return;
				}
			}
		} else {
			if (m_Library.IsDuplicateName(DefRequest.ForDupeCheck(
					m_Def.get_InstanceClassName(), m_Def.get_Id(),
					m_Def.get_Scope(), m_Def.get_ScopeOwner(), name))) {
				MessageDialog.openInformation(getParentShell(), "提示",
						"找到相同名称,请为此  快速操作  输入其它名称. ");
				txtName.forceFocus();
				return;
			}
		}
		dashboarddef.set_Alias(txtAlias.getText().trim());
		dashboarddef.set_Name(txtName.getText().trim());
		dashboarddef.set_Description(txtDesc.getText().trim());
		dashboarddef.set_Scope((Integer) cbScope.getData(cbScope
				.getItem(cbScope.getSelectionIndex())));
		dashboarddef.set_ScopeOwner((String)cbOwner.getData(cbOwner.getText()));

		if (!StringUtils.IsEmpty(cbCategory.getText())) {
			dashboarddef.set_Folder(cbCategory.getText());
		} else {
			dashboarddef.set_Folder("");
		}
		dashboarddef.set_LinkedTo((String) cbLinkedTo.getData(cbLinkedTo
				.getItem(cbLinkedTo.getSelectionIndex())));

		dashboarddef.set_ShowOnMenu(Checked_Cd.getSelection());
		dashboarddef.set_ShowInNavigator(Checked_Dh.getSelection());

		// 保存外观数据
		TitleBarDef titlebardef = dashboarddef.get_TitleBarDef();
		titlebardef.set_Text(txtText.getText());
		titlebardef.set_ImageName(imageAddrLabel.getData().toString());
		titlebardef.set_ShowDate(Checked_Date.getSelection());
		titlebardef.set_UseParentColor(Checked_SystemYs.getSelection());
		titlebardef.set_TextForeColor((String)composite_Foreground.getData(composite_Foreground.getText()));
		titlebardef.get_BackgroundDef()
				.set_IsSolidColor(Radio_1.getSelection());
		titlebardef.get_BackgroundDef().set_IsLeftToRightGradient(
				Radio_2.getSelection());
		titlebardef.get_BackgroundDef().set_FirstColor((String)composite_Foreground_one.getData(composite_Foreground_one.getText()));
		titlebardef.get_BackgroundDef().set_SecondColor((String)composite_Foreground_two.getData(composite_Foreground_two.getText()));
		dashboarddef.get_BackgroundDef().set_FirstColor((String)composite_Background_one.getData(composite_Background_one.getText()));
		dashboarddef.get_BackgroundDef().set_SecondColor((String)composite_Background_two.getData(composite_Background_two.getText()));

		dashboarddef.get_BackgroundDef().set_IsSolidColor(
				Radio_3.getSelection());
		dashboarddef.get_BackgroundDef().set_IsLeftToRightGradient(
				Radio_4.getSelection());
		dashboarddef.get_BackgroundDef().set_ImageName(
				Image2.getData().toString());


		// 保存存局
		dashboarddef.get_TableSizeDef().set_RowCount(
				Integer.parseInt(sp_1.getText()));
		dashboarddef.get_TableSizeDef().set_ColumnCount(
				Integer.parseInt(sp_2.getText()));
		dashboarddef.get_TableSizeDef().set_SeparatorThickness(
				Integer.parseInt(sp_3.getText()));
		dashboarddef.get_TableSizeDef().set_IsEqualCellSize(
				Checked_allrowcoulum.getSelection());

		if("RCP".equals(layout)){
			dashboarddef.ClearPartRefDefs();
			for (CLabel lab : dashboradlayouts.getHashmap().keySet()) {
				java.util.ArrayList<Object> obs = dashboradlayouts.getHashmap().get(lab);
				int Y_start = Integer.valueOf(obs.get(2).toString());
				int Y_end = Integer.valueOf(obs.get(3).toString());
				int X_start = Integer.valueOf(obs.get(0).toString());
				int X_end = Integer.valueOf(obs.get(1).toString());
				
				PartRefDef partrefdef=(PartRefDef) obs.get(4);
				
//				partrefdef.set_Id("");
				partrefdef.set_ShowPartType(false);
				partrefdef.set_StartColumn(X_start);
				partrefdef.set_EndColumn(X_end);
				partrefdef.set_StartRow(Y_start);
				partrefdef.set_EndRow(Y_end);
				
				dashboarddef.AddPartRefDef(partrefdef);
			}
		}
		
//		if (1 == type) {
//			m_Library.UpdateDefinition(dashboarddef, true);
//		} else if (2 == type) {
//			m_Library.UpdateDefinition(dashboarddef, false);
//		} else {
//			dashboarddef.set_Id(m_Def.get_Id());
//			m_Library.UpdateDefinition(dashboarddef, true);
//		}

		super.okPressed();
//		if (bashboardcenter != null) {
//			bashboardcenter.Refresh();
//		}

	}
	
	private void createDashboardPart()
	{
		DaBoTypeSelecttDlg dbsd = new DaBoTypeSelecttDlg(getShell());
		if(dbsd.open()==Dialog.OK){
			String partType = dbsd.getSelectText();
			DashboardPartDef dashboardPartDef = (DashboardPartDef)m_Library.GetNewDefForEditing(DefRequest.ByCategory(dashboarddef.get_Scope(), "", "DashboardPartDef", partType));
			m_Def=dashboardPartDef;
			m_Def.set_Folder(dashboarddef.get_Folder());
			dashboardPartDef = DashBoardPartCenterForm.getPartType(dashboardPartDef.get_CategoryAsString(), getShell(), dashboardPartDef, m_Library, m_ScopeUtil, m_api, dashboarddef.get_LinkedTo(), "New", m_Def);
			if(dashboardPartDef != null){
				m_Library.UpdateDefinition(dashboardPartDef, true);
				refreshList(dashboardPartDef);
			}
		}
	}
	
	
	private void refreshList(DashboardPartDef dashboardPartDef)
	{
		if((Integer)cbScope_1.getData(cbScope_1.getText()) == dashboardPartDef.get_Scope()
			&& dashboardPartDef.get_ScopeOwner().equals((String)cbOwner_1.getData(cbOwner_1.getText())))
		{
			allPlaceHolders = m_Library.GetAllPlaceHolders(DashboardPartDef.get_ClassName(), "(Base)");
			FillList(dashboardPartDef.get_Scope(), dashboardPartDef.get_ScopeOwner());
			list.setSelection(list.indexOf(dashboardPartDef.get_Alias()));
			list.setTopIndex(list.indexOf(dashboardPartDef.get_Alias()));
		}
	}
	
}


