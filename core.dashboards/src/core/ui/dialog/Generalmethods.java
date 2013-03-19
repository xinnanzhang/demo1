package core.ui.dialog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Type;
import system.Collections.ArrayList;
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
import Siteview.Api.PerspectiveDef;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;
import Siteview.Xml.Scope;
import Siteview.Xml.SecurityRight;
import Siteview.Xml.XmlSecurityRight;
import core.ui.DashBoardCenterForm;

public class Generalmethods {

	private Combo cbScope; // 范围下拉式
	private Combo cbCategory; // 类别下拉式
	private Combo cbOwner; // 负责人下拉式
	private Combo cbLinkedTo; // 关联下拉式
	private Text txtDesc;// 说明
	private Text text_4;
	private Button btnNewButton_2;
	private Menu popMenu;
	
	private Button button_CheckTitle;
	private Button Button_CheckBgColor;
	
	private TableCombo cb_1;
	private Button singleColBut;
	private Button shadeChange;
	private TableCombo cb_2;
	private TableCombo cb_3;
	private Label imageAddrLabel;
	private Label colorLb2;

	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口
	private PerspectiveDef perspectiveDef;
	private IList m_lstSupportedScopesWithOwners;
	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private DashBoardCenterForm bashboardcenter;
	// 类别
	private HashSet<String> Categoryset = new HashSet<String>();
	
	private Shell shell;
	
	public static boolean isClient = false;
	
	public Generalmethods(Shell shell,DashboardPartDef dashboarddef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr){
		this.shell=shell;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.dashboardpartdef = dashboarddef;
		this.m_Def = m_Def;
		this.linkstr = linkstr;
		this.m_lstSupportedScopesWithOwners = this.m_ScopeUtil
				.GetSupportedScopesWithOwners(XmlSecurityRight.ToRight("VAED"),
						true);
		if(m_Library == ConnectionBroker.get_SiteviewApi().get_LiveDefinitionLibrary())  isClient = true;
	}
	
	
	public Generalmethods(Shell shell,DashboardPartDef dashboarddef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr,Combo cbScope,Combo cbCategory,Combo cbOwner,Combo cbLinkedTo){
		this.shell=shell;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.dashboardpartdef = dashboarddef;
		this.m_Def = m_Def;
		this.linkstr = linkstr;
		this.m_lstSupportedScopesWithOwners = this.m_ScopeUtil
				.GetSupportedScopesWithOwners(XmlSecurityRight.ToRight("VAED"),
						true);
		this.cbLinkedTo=cbLinkedTo;
		this.cbOwner=cbOwner;
		this.cbScope=cbScope;
		this.cbCategory=cbCategory;
		if(m_Library == ConnectionBroker.get_SiteviewApi().get_LiveDefinitionLibrary())  isClient = true;
	}
	
	/**
	 * 创建  一般 文件选 项卡
	 */
	public void tabFolder_1(TabFolder tabFolder){

		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("\u4E00\u822C");
		Composite general = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(general);

		Label lblNewLabel_1 = new Label(general, SWT.NONE);
		lblNewLabel_1.setBounds(29, 23, 54, 21);
		lblNewLabel_1.setText("\u8BF4\u660E(D):");

		txtDesc = new Text(general, SWT.BORDER);
		txtDesc.setBounds(130, 20, 430, 43);
		
		Label lblNewLabel_2 = new Label(general, SWT.NONE);
		lblNewLabel_2.setBounds(29, 83, 54, 21);
		lblNewLabel_2.setText("\u8303\u56F4(S):");

		cbScope = new Combo(general, SWT.READ_ONLY);
		cbScope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbOwnerdate(cbScope.getData(cbScope.getItem(cbScope
						.getSelectionIndex())), isClient ? 0 : 1);
			}
		});
		cbScope.setBounds(130, 83, 177, 21);

		Label lblNewLabel_3 = new Label(general, SWT.NONE);
		lblNewLabel_3.setBounds(330, 83, 60, 21);
		lblNewLabel_3.setText("\u8D1F\u8D23\u4EBA(O):");

		cbOwner = new Combo(general, SWT.READ_ONLY);
		cbOwner.setEnabled(false);
		cbOwner.setBounds(400, 83, 177, 21);

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
				if (cbLinkedTo.getSelectionIndex() >= 0) {
					cbLinkedToSelected(cbLinkedTo.getItem(cbLinkedTo
							.getSelectionIndex()));
				}
			}
		});
		cbLinkedTo.setBounds(130, 183, 281, 21);
//
//		Checked_Cd = new Button(general, SWT.CHECK);
//		Checked_Cd.setBounds(29, 246, 150, 16);
//		Checked_Cd.setText("\u4F5C\u4E3A\u83DC\u5355\u9879\u663E\u793A");
//
//		Checked_Dh = new Button(general, SWT.CHECK);
//		Checked_Dh.setBounds(29, 288, 150, 16);
//		Checked_Dh.setText("\u5728\u5BFC\u822A\u5668\u4E2D\u663E\u793A");
		
	}
	
	/**
	 * 创建  外观 文件选 项卡
	 */
	public void tabFolder_2(TabFolder tabFolder){
		TabItem tbtmNewItem_1 = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem_1.setText("\u5916\u89C2");
		
		Composite face = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(face);
		
		final Group grpt = new Group(face, SWT.NONE);
		grpt.setLocation(10, 10);
		grpt.setText("\u6807\u9898(T)");
//		FormData fd_Group = new FormData();
//		fd_Group.top = new FormAttachment(0 , 300);
//		grpt.setLayoutData(fd_Group);
		grpt.setSize(576, 319);
		
		Label lblNewLabel_4 = new Label(grpt, SWT.NONE);
		lblNewLabel_4.setBounds(10, 28, 54, 18);
		lblNewLabel_4.setText("\u6587\u672C:");
		
		text_4 = new Text(grpt, SWT.BORDER);
		text_4.setBounds(68, 25, 340, 18);
		
		button_CheckTitle = new Button(grpt, SWT.CHECK);
		button_CheckTitle.setSelection(true);
		button_CheckTitle.setBounds(437, 26, 93, 18);
		button_CheckTitle.setText("\u663E\u793A\u6807\u9898");
		
		Label lblNewLabel_5 = new Label(grpt, SWT.NONE);
		lblNewLabel_5.setBounds(10, 70, 33, 18);
		lblNewLabel_5.setText("\u56FE\u6807:");
		
		Button_CheckBgColor = new Button(grpt, SWT.CHECK);
		Button_CheckBgColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(Button_CheckBgColor.getSelection()){
					cb_1.setEnabled(false);
					singleColBut.setEnabled(false);
					shadeChange.setEnabled(false);
					cb_2.setEnabled(false);
					cb_3.setEnabled(false);
				}else{
					cb_1.setEnabled(true);
					singleColBut.setEnabled(true);
					shadeChange.setEnabled(true);
					cb_2.setEnabled(true);
					cb_3.setEnabled(true);
				}
			}
		});
		Button_CheckBgColor.setBounds(10, 112, 137, 18);
		Button_CheckBgColor.setText("\u4F7F\u7528\u4EEA\u8868\u677F\u6807\u9898\u80CC\u666F");
		
		Label label = new Label(grpt, SWT.NONE);
		label.setText("\u524D\u666F\u8272:");
		label.setBounds(233, 138, 54, 18);
		
		cb_1 = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		cb_1.setBounds(293, 135, 200, 21);
		
		
		Label label_1 = new Label(grpt, SWT.NONE);
		label_1.setText("\u80CC\u666F\u8272:");
		label_1.setBounds(10, 183, 54, 18);
		
		singleColBut = new Button(grpt, SWT.RADIO);
		singleColBut.setSelection(true);
		singleColBut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				colorLb2.setVisible(false);
				cb_3.setVisible(false);
			}
		});
		singleColBut.setText("\u5355\u8272");
		singleColBut.setBounds(92, 212, 93, 18);
		
		shadeChange = new Button(grpt, SWT.RADIO);
		shadeChange.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(shadeChange.getSelection()){
					colorLb2.setVisible(true);
					cb_3.setVisible(true);
				}
			}
		});
		shadeChange.setText("\u6E10\u53D8");
		shadeChange.setBounds(92, 258, 93, 18);
		
		Label label_2 = new Label(grpt, SWT.NONE);
		label_2.setText("\u989C\u82721:");
		label_2.setBounds(233, 212, 54, 18);
		
		cb_2 = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		cb_2.setBounds(293, 207, 200, 21);

		imageAddrLabel = new Label(grpt, SWT.NONE);
		imageAddrLabel.setBounds(92, 70, 329, 18);
		imageAddrLabel.setText(" (\u65E0)");
		imageAddrLabel.setData("");
		colorLb2 = new Label(grpt, SWT.NONE);
		colorLb2.setText("\u989C\u82722:");
		colorLb2.setBounds(233, 258, 54, 18);
		colorLb2.setVisible(false);
		
		cb_3 = new TableCombo(grpt, SWT.BORDER | SWT.READ_ONLY);
		cb_3.setBounds(293, 253, 200, 21);
		cb_3.setVisible(false);
		
		final Label lblNewLabel_7 = new Label(grpt, SWT.NONE);
		lblNewLabel_7.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
//				System.out.println(MouseButtons.Left); 左键为1
//				System.out.println(MouseButtons.Right);右键为2
				if(e.button == 1){
					popMenu = createPopMenu().createContextMenu(lblNewLabel_7);
//					lblNewLabel_7.setMenu(popMenu);   绑定菜单，点击右键自动触发
					popMenu.setLocation(lblNewLabel_7.toDisplay(e.x,e.y));
					popMenu.setVisible(true);
				}
			}
		});
		lblNewLabel_7.setBounds(68, 70, 17, 18);
		lblNewLabel_7.setText("");
		lblNewLabel_7.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_down.png"),0x12,0x12));
		initColorCombo();
		loaddate();
	}
	
	
	/**
	 * 创建  外般  文件选 项卡
	 */
	
	
	

	/**
	 *  数据加载  一般 选项卡
	 */
	public void loadYb() {

		// 范围数据加载
		ICollection supportedScopes = m_ScopeUtil.GetSupportedScopes(SecurityRight.Add);
		IEnumerator it = supportedScopes.GetEnumerator();
		while (it.MoveNext()) {
			int ph = (Integer) it.get_Current();
			String item = m_ScopeUtil.ScopeToString(ph);
			cbScope.setData(item, ph);
			cbScope.add(item);
			if (dashboardpartdef.get_Scope() == ph) {
				cbScope.setText(item);
			}
		}
		cbOwnerdate(
				cbScope.getData(cbScope.getItem(cbScope.getSelectionIndex())),
				isClient ? 0 : 1);

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
	}
	
	
	/**
	 * 范围 类别,关联 下拉式 数据加载
	 */
	public void loaddate() {

		// 范围数据加载
		ICollection supportedScopes = m_ScopeUtil.GetSupportedScopes(SecurityRight.Add);
		IEnumerator it = supportedScopes.GetEnumerator();
		while (it.MoveNext()) {
			int ph = (Integer) it.get_Current();
			String item = m_ScopeUtil.ScopeToString(ph);
			cbScope.setData(item, ph);
			cbScope.add(item);
			if (dashboardpartdef.get_Scope() == ph) {
				cbScope.setText(item);
			}
		}
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
		getcboQuickOblink(cbLinkedTo.getText());
		Iterator<String> ite = Categoryset.iterator();
		while (ite.hasNext()) {
			String str = (String) ite.next();
			if (!StringUtils.IsEmpty(str)) {
				cbCategory.add(str);
				cbCategory.setData(str, str);
			}
		}


		if (dashboardpartdef != null) {
			cbCategory.setText(dashboardpartdef.get_Folder());
			txtDesc.setText(dashboardpartdef.get_Description());
//			Checked_Cd.setSelection(dashboardpartdef.get_ShowOnMenu());
//			Checked_Dh.setSelection(dashboardpartdef.get_ShowInNavigator());
//			
			text_4.setText(dashboardpartdef.get_TitleBarDef().get_Text());
			if (!StringUtils.IsEmpty(dashboardpartdef.get_TitleBarDef()
					.get_ImageName())) {
				imageAddrLabel.setText(ImageChooseDlg.getInitImageStr(dashboardpartdef.get_TitleBarDef()
						.get_ImageName()));
				imageAddrLabel.setData(dashboardpartdef.get_TitleBarDef()
						.get_ImageName());
			}
			button_CheckTitle.setSelection(dashboardpartdef.get_TitleBarDef().get_Visible());
			Button_CheckBgColor.setSelection(dashboardpartdef.get_TitleBarDef().get_UseParentColor());

			
			boolean flag = !dashboardpartdef.get_TitleBarDef().get_UseParentColor();
			cb_1.setEnabled(flag);
			cb_2.setEnabled(flag);
			cb_3.setEnabled(flag);
			singleColBut.setEnabled(flag);
			shadeChange.setEnabled(flag);
//			colorLb2.setEnabled(flag);
			
			if (dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
					.get_IsSolidColor()) {
				singleColBut.setSelection(true);
				shadeChange.setSelection(false);
				if (!dashboardpartdef.get_TitleBarDef().get_UseParentColor()) {
					cb_2.setEnabled(true);
				}
			}
			if (dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
					.get_IsLeftToRightGradient()) {
				if (!dashboardpartdef.get_TitleBarDef().get_UseParentColor()) {
//					colorLb2.setEnabled(true);
					cb_3.setEnabled(true);
				}
				singleColBut.setSelection(false);
				shadeChange.setSelection(true);
				colorLb2.setVisible(true);
				cb_3.setVisible(true);
			}
		}
	}

	/**
	 * 负责人 加载数据
	 */
	public void cbOwnerdate(Object object, int type) {
		if (type == 0) {
			cbOwner.removeAll();
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, true);
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
			
		} else if (type == 1) {
			cbOwner.removeAll();
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, false);
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
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, true);
			if (list.get_Count() > 0) {
				for (int i = 0; i < list.get_Count(); i++) {
					String own = (String) list.get_Item(i);
					String item = m_ScopeUtil.ScopeOwnerToString(ob, own);
				}
			}
		}

	}

	/**
	 * 下拉式点击事件 先初始化清理一些数据
	 */
	public void cbLinkedToSelected(String str) {
		Categoryset.clear();
		cbCategory.removeAll();
		getcboQuickOblink(str);
		// 类别数据加载

		Iterator ite = Categoryset.iterator();
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
				.ForList(s, Owner, linkTo, DashboardPartDef.get_ClassName(),
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
	
	
	private MenuManager createPopMenu() {
		MenuManager menuManager = new MenuManager();
		// menuManager.setRemoveAllWhenShown(true);
		Action look = new Action("浏览(B)...") {
			@Override
			public void run() {
				String str = "";
				str = imageAddrLabel.getData().toString();
				ImageChooseDlg imageC = new ImageChooseDlg(shell,
					m_Library, m_api, str);
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

	//解析图片名称     图片显示路径-->界面显示名称
	public String getInitImageStr(String imagePath){
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
	// 解析图片名称 界面显示名称-->图片名称
	public String getSelectImageStr(String imagePath) {
		String imageText = "";
		if (imagePath.contains("Database.")) {
			imageText = imagePath.substring("Database.".length());
		} else if (imagePath.contains("System.")) {
			imageText = imagePath.substring("System.".length());
		}
		return imageText;
	}
	/**
	 * 选择颜色后 给下拉式 填充背景颜色
	 */
	public void FillCombo(String colorname, CCombo combo) {
		//combo.setText(colorname);
		KnownColor kc = (KnownColor) KnownColor.Parse(
				Type.GetType(KnownColor.class.getName()),
				getColorForSiteview(colorname));
		system.Drawing.Color c = system.Drawing.Color
				.FromKnownColor(kc.value__);
		combo.setBackground(new Color(null, c.get_R(), c.get_G(), c.get_B()));
		if(c.get_R() < 25 && c.get_G() < 25 && c.get_B() < 25)
			combo.setForeground(new Color(null, 255, 255, 255));
		else
			combo.setForeground(new Color(null, 0, 0, 0));
		
	}


	public String getColorForSiteview(String colorName) {
		return colorName.substring(colorName.indexOf("]") + 1,
				colorName.length());
	}

	
	public String getSiteviewColorForColor(String colorName) {

		KnownColor kc = (KnownColor) KnownColor.Parse(
				Type.GetType(KnownColor.class.getName()), colorName);
		system.Drawing.Color c = system.Drawing.Color
				.FromKnownColor(kc.value__);

		if (c.get_IsSystemColor())
			return "[SYSTEM]" + colorName;

		return "[NAMED]" + colorName;
	}

	


	/**
	 * 取 下拉式的 背景颜色
	 */
	private void initColorCombo() {

//		String foreColor = dashboardpartdef.get_TitleBarDef().get_TextForeColor();
//		String color1 = dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
//				.get_FirstColor();
//		String color2 = dashboardpartdef.get_TitleBarDef().get_BackgroundDef()
//				.get_SecondColor();
//
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class
//				.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default()
//					.GetString("SiteviewColorEditor" + "." + str);
////			cb_1.add(colorName);
//			cb_2.add(colorName);
//			cb_3.add(colorName);
//			cb_1.setData(colorName, getSiteviewColorForColor(str));
//			cb_2.setData(colorName, getSiteviewColorForColor(str));
//			cb_3.setData(colorName, getSiteviewColorForColor(str));
////			if (getSiteviewColorForColor(str).equals(foreColor)) {
////				cb_1.setText(colorName);
////				FillCombo(str, cb_1);
////			}
//			if (getSiteviewColorForColor(str).equals(color1)) {
//				cb_2.setText(colorName);
//				FillCombo(str, cb_2);
//			}
//			if (getSiteviewColorForColor(str).equals(color2)) {
//				cb_3.setText(colorName);
//				FillCombo(str, cb_3);
//			}
//		}
////		if ("".equals(cb_1.getText())) {
////			cb_1.setText("控制文本");
////			FillCombo("ControlText", cb_1);
////		}
//		if ("".equals(cb_2.getText())) {
//			cb_2.setText("透明色");
//			FillCombo("Transparent", cb_2);
//		}
//		if ("".equals(cb_3.getText())) {
//			cb_3.setText("透明色");
//			FillCombo("Transparent", cb_3);
//		}
		
		ColorComboInit.Init(cb_1, dashboardpartdef.get_TitleBarDef().get_TextForeColor());
		ColorComboInit.Init(cb_2, dashboardpartdef.get_TitleBarDef().get_BackgroundDef().get_FirstColor());
		ColorComboInit.Init(cb_3, dashboardpartdef.get_TitleBarDef().get_BackgroundDef().get_SecondColor());

	}
	
	
	public boolean  savavalidation(Text txtName,Text txtAlias,String type) {
		
		//添加
		
		String name = txtName.getText().trim();
		
		if(StringUtils.IsEmpty(name)){
			MessageDialog.openInformation(shell, "提示", "请为此  仪表盘部件  输入名称。");
			txtName.forceFocus();
			return false;
		}else if ("Edit".equals(type)) {
			if (!name.equals(dashboardpartdef.get_Name())) {
				if (m_Library.IsDuplicateName(DefRequest.ForDupeCheck(
						dashboardpartdef.get_InstanceClassName(),
						dashboardpartdef.get_Id(), dashboardpartdef.get_Scope(),
						dashboardpartdef.get_ScopeOwner(), name))) {
					MessageDialog.openInformation(shell, "提示",
							"找到相同名称,请为此  仪表盘部件  输入其它名称. ");
					txtName.forceFocus();
					return false;
				}
			}
		} else {
			if (m_Library.IsDuplicateName(DefRequest.ForDupeCheck(
					dashboardpartdef.get_InstanceClassName(), dashboardpartdef.get_Id(),
					dashboardpartdef.get_Scope(), dashboardpartdef.get_ScopeOwner(), name))) {
				MessageDialog.openInformation(shell, "提示",
						"找到相同名称,请为此  仪表盘部件  输入其它名称. ");
				txtName.forceFocus();
				return false;
			}
		}
		dashboardpartdef.set_Alias(txtAlias.getText().trim());
		dashboardpartdef.set_Name(name);
		dashboardpartdef.set_Description(txtDesc.getText().trim());
		dashboardpartdef.set_Scope((Integer)cbScope.getData(cbScope.getText()));
		dashboardpartdef.set_ScopeOwner((String)cbOwner.getData(cbOwner.getText()));
		dashboardpartdef.set_Folder(cbCategory.getText().trim());
		dashboardpartdef.set_LinkedTo((String)cbLinkedTo.getData(cbLinkedTo.getText()));
		dashboardpartdef.get_TitleBarDef().set_Text(text_4.getText().trim());
		dashboardpartdef.get_TitleBarDef().set_Visible(button_CheckTitle.getSelection());
		dashboardpartdef.get_TitleBarDef().set_ImageName(imageAddrLabel.getData().toString());
		dashboardpartdef.get_TitleBarDef().set_UseParentColor(Button_CheckBgColor.getSelection());
		dashboardpartdef.get_TitleBarDef().set_TextForeColor((String)cb_1.getData(cb_1.getText()));
		dashboardpartdef.get_TitleBarDef().get_BackgroundDef().set_FirstColor((String)cb_2.getData(cb_2.getText()));
		dashboardpartdef.get_TitleBarDef().get_BackgroundDef().set_SecondColor((String)cb_3.getData(cb_3.getText()));
		dashboardpartdef.get_TitleBarDef().get_BackgroundDef().set_IsSolidColor(singleColBut.getSelection());
		dashboardpartdef.get_TitleBarDef().get_BackgroundDef().set_IsLeftToRightGradient(shadeChange.getSelection());
		return true;
	}


	public Combo getCbScope() {
		return cbScope;
	}


	public Combo getCbOwner() {
		return cbOwner;
	}


	
	
}
