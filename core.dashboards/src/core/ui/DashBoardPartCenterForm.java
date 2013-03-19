package core.ui;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import siteview.windows.forms.IAdrCenter;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.IList;
import Core.Dashboards.DashboardPartDef;
import Core.ui.method.GetIeditorInput;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.IMru;
import Siteview.IViewOverrideInfo;
import Siteview.PlaceHolder;
import Siteview.SiteviewException;
import Siteview.StringUtils;
import Siteview.ViewOverrideInfo;
import Siteview.Api.BusinessObject;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.LicenseType;
import Siteview.Api.PerspectiveDef;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.Scope;
import Siteview.Xml.SecurityRight;
import Siteview.Xml.ViewBehavior;
import Siteview.Xml.XmlScope;
import Siteview.Xml.XmlSecurityRight;
import core.ui.dialog.BusinessObjectDlg;
import core.ui.dialog.CustomPartDlg;
import core.ui.dialog.DaBoTypeSelecttDlg;
import core.ui.dialog.ImageDlg;
import core.ui.dialog.LinkGrid;
import core.ui.dialog.MotionsPart;
import core.ui.dialog.ObjectBrowser;
import core.ui.dialog.ObjectRelationTopo;
import core.ui.dialog.OutlookCalendar;
import core.ui.dialog.OutlookInbox;
import core.ui.dialog.SoftItem;
import core.ui.dialog.UtilityGrid;
import core.ui.dialog.ViewGrid;
import core.ui.dialog.WebBrowser;
import core.ui.wizard.DiagramWizard;
public class DashBoardPartCenterForm extends ApplicationWindow implements IAdrCenter {

	private String m_strDefClassName = DashboardPartDef.get_ClassName(); // C#查询数据时的固定参数
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private String m_moduleSecurityName = "Core.Security.DashboardParts"; // C#查询数据时的固定参数
	private PerspectiveDef perspectiveDef; // 不懂
//	private DashBoardPartCenterForm quickcenter;// 主程序窗口
	private ISiteviewApi m_api; // 初始化接口
	private IList m_lstSupportedScopesWithOwners;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private Table tbBusObList;
	private Combo cboQuickOblink; // 快速操作下拉式
	private Combo cb; // 查找文本
	private String findtext; // 查找内容
	private TabFolder tabFolder; // 选项卡文件夹
	private TabItem tabRange;
	private TabItem tbCategoryy;
	private Table tbCategory;
	private Button btOK; // 保存按钮
	private ArrayList alldate = new ArrayList();; // 操作中心关联的所有的数据
	private SashForm sashForm;
	private DashboardPartDef dashboardPartDef;

	// 文件菜单
	private Action actAdd; // 新建
	private Action actEdit; // 编辑
	private Action actCopy; // 复制
	private Action actDelete; // 删除
	private Action actSearch;
	private Action qbigicon; // 大图标
	private Action qsmallicon; // 列表
	private Action qdetailed; // 详细列表
	private Action qdelqueryop; // 删除查询选 项
	private Action qref; // 刷新
	// 范围
	private Tree tree; // 范围 树结构
	private Map<String, ArrayList> htCatToListerItems = new HashMap<String, ArrayList>();
	// 类别
	private Map<String, ArrayList> httypeToListerItems = new HashMap<String, ArrayList>();
	// 查找
	private Tree findtree; // 范围 树结构
	private TabItem tabFind = null;
	private Map<String, ArrayList> findhtCatToListerItems = new HashMap<String, ArrayList>();
	// 右击菜单
	private Menu popMenu;
	private boolean showButtons = false; // 是否显示确定取消按钮
	private PlaceHolder selection;
	// 默认第一次查询
	private BusinessObject busob = null;

	
	private String bus_name="";
	private boolean choosesflag=false;
	private boolean serviceorchclient=false;
	private String title="仪表盘部件中心";
	private HashSet<String> Categoryset = new HashSet<String>(); //保存全部类别的集合
	
	private Button check_Override;
	
	/**
	 * Create the application window,
	 * 
	 * @wbp.parser.constructor
	 */
	public DashBoardPartCenterForm(boolean flag, Object strcontext) {
		super(null);
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.RESIZE
				| SWT.APPLICATION_MODAL);
//		quickcenter = this;
		this.choosesflag = flag;
		Initializeddate();
	}

	/**
	 *  param String m_strDefClassName 查询数据时的固定参数 
	 *  param String m_moduleSecurityName 查询数据时的固定参数 
	 *  param String title 标题
	 *  param String bus_name 默认的业务对象
	 *  param boolean true= 有选择按钮 false=没有确定按钮
	 *  param boolean true= 服务端  false=客户端
	 */
	public DashBoardPartCenterForm(String m_strDefClassName,String m_moduleSecurityName,String title,String bus_name, PerspectiveDef perspectiveDef, DefinitionLibrary m_Library, boolean choosesflag,boolean serviceorchclient){
		super(null);
		this.setShellStyle(SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.RESIZE| SWT.APPLICATION_MODAL);
//		this.quickcenter=this;
		this.bus_name=bus_name;
		this.m_moduleSecurityName=m_moduleSecurityName;
		this.choosesflag=choosesflag;
		this.serviceorchclient=serviceorchclient;
		this.m_strDefClassName=m_strDefClassName;
		this.title=title;
		this.perspectiveDef = perspectiveDef;
		this.m_Library = m_Library;
		Initializeddate();
	}
	
	/**
	 * 初始化一些基本数据
	 * 
	 * @return
	 */
	public void Initializeddate() {
		
		createActions();
		addMenuBar();
		addCoolBar(SWT.FLAT);
		addStatusLine();
		this.m_api = ConnectionBroker.get_SiteviewApi();
		if(this.m_Library == null)	this.m_Library = ConnectionBroker.get_SiteviewApi().get_LiveDefinitionLibrary();
		this.m_ScopeUtil = new ScopeUtil(m_api, m_Library, m_strDefClassName,m_moduleSecurityName);
		this.m_ScopeUtil.set_CheckRights(true);
		this.m_lstSupportedScopesWithOwners = this.m_ScopeUtil.GetSupportedScopesWithOwners(XmlSecurityRight.ToRight("VAED"),!serviceorchclient);
//		this.quickcenter = this;
	
		// 加载首页的值
		Object[] object = GetIeditorInput.getIeditorInput(m_api);
		this.busob = (BusinessObject) object[1];
		if(!serviceorchclient)	this.perspectiveDef=m_Library.GetPerspectiveDef(m_api.get_RoleManager().PerspectiveFromRole());
		
	}

	/**
	 * Configure the shell.
	 * 
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		newShell.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				openclosecoolbar(false);
			}
		});
		super.configureShell(newShell);
		newShell.setMaximized(!choosesflag);
		newShell.setSize(new Point(800, 600));
		newShell.setLocation(240, 80);
		newShell.setText(title);
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		Composite fcontainer = new Composite(container, SWT.NONE);
		fcontainer.setLayout(new FormLayout());
		sashForm = new SashForm(fcontainer, SWT.NONE);
		FormData fd_sash = new FormData();
		fd_sash.left = new FormAttachment(0, 0);
		fd_sash.right = new FormAttachment(100, 0);
		fd_sash.top = new FormAttachment(0, 0);
		if (choosesflag)
			fd_sash.bottom = new FormAttachment(100, -40);
		else
			fd_sash.bottom = new FormAttachment(100, 0);
		sashForm.setLayoutData(fd_sash);
		if (choosesflag) {
			Composite cBottom = new Composite(fcontainer, SWT.NONE);
			cBottom.setLayout(new FormLayout());
			FormData fd_bottom = new FormData();
			fd_bottom.left = new FormAttachment(0, 0);
			fd_bottom.right = new FormAttachment(100, 0);
			fd_bottom.top = new FormAttachment(sashForm, 0);
			fd_bottom.bottom = new FormAttachment(100, -5);
			cBottom.setLayoutData(fd_bottom);
			btOK = new Button(cBottom, SWT.NONE);
			btOK.setText("确定");
			btOK.setEnabled(false);
			FormData fd_btok = new FormData();
			fd_btok.left = new FormAttachment(100, -200);
			fd_btok.right = new FormAttachment(100, -120);
			fd_btok.top = new FormAttachment(0, 10);
			fd_btok.bottom = new FormAttachment(0, 30);
			btOK.setLayoutData(fd_btok);
			btOK.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (tbBusObList.getSelectionIndex() == -1)
						return;
					TableItem ti = tbBusObList.getSelection()[0];
					if (tbBusObList.getSelectionCount() < 1) {
						if (!(ti.getData() instanceof PlaceHolder)) {
							MessageDialog.openInformation(getShell(), "提示",
									"请选择仪表盘部件中心。");
							return;
						}
					} else {
						selection = (PlaceHolder) ti.getData();
					}
					setReturnCode(OK);
					close();
				}
			});

			Button btCancel = new Button(cBottom, SWT.NONE);
			btCancel.setText("取消");
			FormData fd_btcancel = new FormData();
			fd_btcancel.left = new FormAttachment(100, -100);
			fd_btcancel.right = new FormAttachment(100, -20);
			fd_btcancel.top = new FormAttachment(0, 10);
			fd_btcancel.bottom = new FormAttachment(0, 30);
			btCancel.setLayoutData(fd_btcancel);
			btCancel.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setReturnCode(CANCEL);
					close();
				}
			});
		}
		tabFolder = new TabFolder(sashForm, SWT.NONE);
		tabFolder.setBounds(0, 0, parent.getSize().x / 3,
				parent.getSize().y - 45);
		tabRange = new TabItem(tabFolder, SWT.NONE);
		tabRange.setText("范围");
		tabRange.setImage(SwtImageConverter.ConvertToSwtImage(
				ImageResolver.get_Resolver().ResolveImage(
						"[IMAGE]Siteview#Images.Icons.Common.Global.png"),
				0x12, 0x12));
		tbCategoryy = new TabItem(tabFolder, SWT.NONE);
		tbCategoryy.setText("类别");
		tbCategoryy
				.setImage(SwtImageConverter.ConvertToSwtImage(
						ImageResolver.get_Resolver().ResolveImage(
								"[IMAGE]Core#Images.Icons.FolderClosed.png"),
						0x12, 0x12));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (htCatToListerItems.isEmpty())
					return;
				TabItem[] selected = tabFolder.getSelection();
				if (selected.length > 0) {
					if ("类别".equals(selected[0].getText())) {
						TableItem[] se = tbCategory.getSelection();
						if (se.length > 0) {
							addlisttabledate(se[0].getData() + "");
						} else {
							addlisttabledate("");
						}
					} else if ("查找".equals(selected[0].getText())) {
						TreeItem[] findfreeselected = findtree.getSelection();
						if (findfreeselected.length > 0) {
							findtree.showItem(findfreeselected[0]);
							addtabledate(findfreeselected[0].getData()
									.toString(), "SHOWFIND");
						} else {
							addtabledate(Scope.Global+"", "SHOWFIND");
						}

					} else {

						TreeItem[] findfreeselected = tree.getSelection();
						if (findfreeselected.length > 0) {
							tree.showItem(findfreeselected[0]);
							addtabledate(findfreeselected[0].getData()
									.toString(), "1");
						} else {
							addtabledate(Scope.Global+"", "SHOWRANGE");
						}

					}
				}
			}
		});

		tbBusObList = new Table(sashForm, SWT.BORDER | SWT.FULL_SELECTION);
		tbBusObList.setHeaderVisible(true);
		tbBusObList.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {

				//openclosecoolbar(false);

			}
		});
		tbBusObList.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Object ob = method();
				if(check_Override != null)
				{
					if(tbBusObList.getSelection() != null && tbBusObList.getSelection().length > 0)
					{
						check_Override.setEnabled(true);
						check_Override.setSelection(IsOverriddenObject(ob));
					}else
					{
						check_Override.setEnabled(false);
						check_Override.setSelection(false);
					}
				}
				if(serviceorchclient && perspectiveDef != null && check_Override != null)
				{
					actAdd.setEnabled(false);
					actEdit.setEnabled(check_Override.getSelection());
					actCopy.setEnabled(false);
					actDelete.setEnabled(false);
				}else
				{
					if (ob instanceof PlaceHolder) {
						if (choosesflag) {
							btOK.setEnabled(true);
						}
						openclosecoolbar(true);
					} else {
						openclosecoolbar(false);
					}
				}
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

				Object ob = method();
				if (!(ob instanceof PlaceHolder)) {
					String str = (String) ob;
					TabItem[] selected = tabFolder.getSelection();
					if (selected.length > 0) {

						if ("查找".equals(selected[0].getText())) {

							TreeItem[] rangetree = findtree.getItems();
							for (int i = 0; i < rangetree.length; i++) {

								TreeItem[] ran = rangetree[i].getItems();

								for (int j = 0; j < ran.length; j++) {
									TreeItem item = ran[j];
									if (item.getData().toString().equals(str)) {
										findtree.setSelection(item);
										findtree.showItem(item);
									}
								}

							}
							addtabledate(str, "SHOWFIND");
						} else {

							TreeItem[] rangetree = tree.getItems();
							for (int i = 0; i < rangetree.length; i++) {

								TreeItem[] ran = rangetree[i].getItems();

								for (int j = 0; j < ran.length; j++) {
									TreeItem item = ran[j];
									if (item.getData().toString().equals(str)) {
										tree.setSelection(item);
										tree.showItem(item);
									}
								}

							}

							addtabledate(str, "SHOWRANGE");
						}
					}
				} else {
					if (showButtons) {
						selection = (PlaceHolder) ob;
						setReturnCode(OK);
						close();
					}

				}
			}

			public Object method() {
				TableItem[] selected = tbBusObList.getSelection();
				Object ob = null;
				if (selected.length > 0) {
					ob = selected[0].getData();
				}
				return ob;
			}
		});

		TableColumn tc1 = new TableColumn(tbBusObList, SWT.NONE);
		tc1.setText("名字");
		tc1.setWidth(260);
		TableColumn tc2 = new TableColumn(tbBusObList, SWT.NONE);
		tc2.setText("描述");
		tc2.setWidth(300);
		TableColumn tc3 = new TableColumn(tbBusObList, SWT.NONE);
		tc3.setText("类别");
		tc3.setWidth(200);

		final MenuManager pm = new MenuManager();
		pm.setRemoveAllWhenShown(true);

		popMenu = pm.createContextMenu(tbBusObList);
		tbBusObList.setMenu(popMenu);
		sashForm.setWeights(new int[] { 147, 661 });
		pm.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				manager.add(actAdd);
				manager.add(actEdit);
				manager.add(actCopy);
				manager.add(actDelete);

			}
		});

		tbBusObList.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {

			}
			@Override
			public void mouseDown(MouseEvent e) {
				
			}
			@Override
			public void mouseUp(MouseEvent e) {
				if (e.button == 3) {
//					openclosecoolbar(tbBusObList.getSelectionCount() > 0);
					popMenu.setLocation(tbBusObList.toDisplay(e.x, e.y));
					popMenu.setVisible(true);
				}

			}
		});
		firstloaddate();
		return container;
	}

	/**
	 * 第一次默认加载 数据 方法
	 */
	public void firstloaddate() {
		loadObject();
		getcboQuickOblink(bus_name);
		addtreedate();
		addlistdate();
		addtabledate(Scope.Global+"", "SHOWRANGE");
	}

	/**
	 * 加载快速操作中心的下拉式
	 */
	protected void loadObject() {
		if (busob != null && !StringUtils.IsEmpty(busob.get_Name())) 
		{
			bus_name = busob.get_Name();
		} else if(StringUtils.IsEmpty(bus_name)){
			bus_name = "";
		}
		String selectname="";
		cboQuickOblink.add("未关联");
		cboQuickOblink.setData("未关联", "");
		List<String> bobs = new Vector<String>();
		ICollection placeHolderList = m_Library.GetPlaceHolderList(DefRequest
				.ForList(BusinessObjectDef.get_ClassName()));
		if (placeHolderList != null)
		{
			IEnumerator it = placeHolderList.GetEnumerator();
			while (it.MoveNext()) {
				PlaceHolder holder = (PlaceHolder) it.get_Current();
				if ((holder.HasFlag("AllowDerivation") || 
						(!m_api.get_SecurityService().HasBusObRight(holder.get_Name(), SecurityRight.View)) &&
						m_api.get_LicenseType() != LicenseType.Administrator))
				{
					continue;
				}else
				{
					if (holder.HasFlag("Master"))
					{
						bobs.add(holder.get_Alias());
						cboQuickOblink.setData(holder.get_Alias(), holder.get_Name());
						if (!StringUtils.IsEmpty(bus_name)
								&& holder.get_Name().equals(bus_name)) {
							selectname=holder.get_Alias();
						}
					}
				}
			}

			Collections.sort(bobs,new Comparator<String>(){
				Collator collator = Collator.getInstance(java.util.Locale.CHINA); 
				public int compare(String o1,String o2) {
					return collator.getCollationKey(o1).compareTo(collator.getCollationKey(o2));  
				}
			});
			for(int i=0;i<bobs.size();i++){
				cboQuickOblink.add(bobs.get(i));
			}
			bobs.clear();
			cboQuickOblink.setText(selectname);
			if(StringUtils.IsEmpty(cboQuickOblink.getText())&&cboQuickOblink.getItemCount()>0)cboQuickOblink.select(0);
		}
	}

	/**
	 * 下拉式点击事件 先初始化清理一些数据
	 */
	public void cboQuickOblinkSelected() {

		htCatToListerItems.clear();
		alldate.Clear();
		httypeToListerItems.clear();
		getcboQuickOblink(cboQuickOblink.getData(cboQuickOblink.getText())
				.toString());
		addtreedate();
		addlistdate();
		addtabledate(Scope.Global+"", "SHOWRANGE");
	}

	/**
	 * 根据选择下拉式 装载关联的范围和类型
	 */

	public void getcboQuickOblink(String strBusLink) {
		if(serviceorchclient){
			IEnumerator it = m_lstSupportedScopesWithOwners.GetEnumerator();
			while (it.MoveNext()) {
				ScopeUtil.ScopeAndOwners owners = (ScopeUtil.ScopeAndOwners) it
						.get_Current();
				if (owners.get_Owners().get_Count() > 0
						&& owners.get_Scope() != Scope.Global) {
					for (int i = 0; i < owners.get_Owners().get_Count(); i++) {
						String str = (String) owners.get_Owners().get_Item(i);
						OrganizeListerItemsByCategoryService(htCatToListerItems,
								owners.get_Scope(), str, strBusLink, false);
					}
				} else {
					OrganizeListerItemsByCategoryService(htCatToListerItems,
							owners.get_Scope(), "", strBusLink, false);
				}
			}
		}else {
			IEnumerator it = m_lstSupportedScopesWithOwners.GetEnumerator();
			while (it.MoveNext()) {
				ScopeUtil.ScopeAndOwners owners = (ScopeUtil.ScopeAndOwners) it
						.get_Current();
				if (owners.get_Owners().get_Count() > 0
						&& owners.get_Scope() != Scope.Global) {
					for (int i = 0; i < owners.get_Owners().get_Count(); i++) {
						String str = (String) owners.get_Owners().get_Item(i);
						OrganizeListerItemsByCategory(htCatToListerItems,
								owners.get_Scope(), str, strBusLink, false);
					}
				} else {
					OrganizeListerItemsByCategory(htCatToListerItems,
							owners.get_Scope(), "", strBusLink, false);
				}

			}
		}
		
	}

	/**
	 * 对取出来的数据进行分类 保存到 MAP 集合中
	 * 
	 * @param s
	 * @param Scope
	 * @param Owner
	 * @param linkTo
	 *            关联的业务对象的名字
	 * @param bFilter
	 *            是否过滤
	 */
	private void OrganizeListerItemsByCategory(
			Map<String, ArrayList> listItems, int s, String Owner,
			String linkTo, boolean bFilter) {
		String strPerspective = (this.perspectiveDef != null) ? this.perspectiveDef
				.get_Name() : "(Base)";

		ArrayList obs = (ArrayList) m_Library.GetPlaceHolderList(DefRequest
				.ForList(s, Owner, linkTo, m_strDefClassName, strPerspective));

		
		if (obs != null) {
			alldate.Add(obs);
			for (int i = 0; i < obs.get_Count(); i++) {
				PlaceHolder holder = (PlaceHolder) obs.get_Item(i);

				if (!listItems.containsKey(holder.get_Scope() + "~"
						+ holder.get_Folder())) {
					listItems.put(
							holder.get_Scope() + "~" + holder.get_Folder(),
							new ArrayList());
				}
				listItems.get(holder.get_Scope() + "~" + holder.get_Folder())
						.Add(holder);

				if (!httypeToListerItems.containsKey(holder.get_Folder())) {
					httypeToListerItems.put(holder.get_Folder(),
							new ArrayList());
				}
				httypeToListerItems.get(holder.get_Folder()).Add(holder);
			}
		}else{
			
		}
	}
	
	/**
	 * 服务端
	 * @param listItems
	 * @param s
	 * @param Owner
	 * @param linkTo
	 * @param bFilter
	 */
	private void OrganizeListerItemsByCategoryService(
			Map<String, ArrayList> listItems, int s, String Owner,
			String linkTo, boolean bFilter) {
		String strPerspective = (this.perspectiveDef != null) ? this.perspectiveDef
				.get_Name() : "(Base)";

		ArrayList obs = (ArrayList) m_Library.GetPlaceHolderList(DefRequest
				.ForList(s, Owner, linkTo, m_strDefClassName, strPerspective));

		
	    if (obs != null&&obs.get_Count()>0) {
	    	alldate.Add(obs);
			for (int i = 0; i < obs.get_Count(); i++) {
				PlaceHolder holder = (PlaceHolder) obs.get_Item(i);
				if (!listItems.containsKey(holder.get_Scope() + "~"+ Owner+"~"+holder.get_Folder())) {
					listItems.put(holder.get_Scope() + "~"+ Owner+"~"+holder.get_Folder(),new ArrayList());
				}
				listItems.get(holder.get_Scope() + "~"+ Owner+"~"+holder.get_Folder()).Add(holder);
				if (!httypeToListerItems.containsKey(holder.get_Folder())) {
					httypeToListerItems.put(holder.get_Folder(),
							new ArrayList());
				}
				httypeToListerItems.get(holder.get_Folder()).Add(holder);
			}
		}else{
			if (!listItems.containsKey(s+"~"+Owner)) {
				listItems.put(s+"~"+Owner,new ArrayList());
			}
		}
	}
	

	/**
	 * 动态的创建 范围 文件夹 和 树结构
	 */

	public void addtreedate() {

		tree = new Tree(tabFolder, SWT.SINGLE);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selected = tree.getSelection();
				if (selected.length > 0) {
					addtabledate((selected[0].getData() + ""), "SHOWRANGE");
				}
			}
		});
		IEnumerator it = m_lstSupportedScopesWithOwners.GetEnumerator();
		String nodetext = "";
		Image images =null;
		while (it.MoveNext()) {
			ScopeUtil.ScopeAndOwners owners = (ScopeUtil.ScopeAndOwners) it.get_Current();
			nodetext = m_ScopeUtil.ScopeToString(owners.get_Scope());
			if (!StringUtils.IsEmpty(nodetext)) {
				switch (owners.get_Scope()) {
				case Scope.User:
					images=SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Siteview#Images.Icons.Common.User.png",0x12, 0x12));
					break;
				case Scope.Role:
					images=SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Siteview#Images.Icons.Common.Role.png",0x12, 0x12));
					break;
				case Scope.Global:
					images=SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Siteview#Images.Icons.Common.Global.png",0x12, 0x12));
					break;
				}

				TreeItem treeItem = new TreeItem(tree, SWT.SINGLE);
				treeItem.setImage(images);
				treeItem.setText(nodetext);
				treeItem.setData(owners.get_Scope() + "");
				if(serviceorchclient){
					for (String str : htCatToListerItems.keySet()) {
						String[] strss=str.split("~");
						if(strss.length>1&&(owners.get_Scope()+"").equals(strss[0])){
							TreeItem treei=null;
							if(!StringUtils.IsEmpty(strss[1])){
								TreeItem[] items = treeItem.getItems();
								for(TreeItem i : items)
								{
									if(strss[1].equals(i.getText())) {
										treei = i;
										break;
									}
								}
								if(treei == null)
								{
									treei = new TreeItem(treeItem,SWT.NONE);
									treei.setImage(images);
									treei.setText(strss[1]);
									treei.setData(strss[0] + "~" + strss[1] + "~");
								}
							}
							if(strss.length>2){
								ArrayList obs = htCatToListerItems.get(str);
								HashSet<String> set = new HashSet<String>();
								for (int i = 0; i < obs.get_Count(); i++) {
									PlaceHolder ph = (PlaceHolder) obs.get_Item(i);
									if (owners.get_Scope() == ph.get_Scope()) {
										if (!StringUtils.IsEmpty(ph.get_Folder())) {
											if (!set.contains(ph.get_Folder())) {
												TreeItem node =null;
												if(treei==null){
													node = new TreeItem(treeItem,SWT.NONE);
												}else {
													 node = new TreeItem(treei,SWT.NONE);
												}
												node.setImage(SwtImageConverter
														.ConvertToSwtImage(ImageResolver
																.get_Resolver()
																.ResolveImage(
																		"[IMAGE]Core#Images.Icons.FolderClosed.png",
																		0x12, 0x12)));
												node.setText(ph.get_Folder());
												node.setData(str);
											}
											set.add(ph.get_Folder());
										}
									}
								}
							}
						}
					}
				}else{
					HashSet<String> set = new HashSet<String>();
					for (String str : htCatToListerItems.keySet()) {
						ArrayList obs = htCatToListerItems.get(str);
						for (int i = 0; i < obs.get_Count(); i++) {
							PlaceHolder ph = (PlaceHolder) obs.get_Item(i);

							if (owners.get_Scope() == ph.get_Scope()) {

								if (!StringUtils.IsEmpty(ph.get_Folder())) {
									if (!set.contains(ph.get_Folder())) {
										TreeItem node = new TreeItem(treeItem,
												SWT.NONE);
										node.setImage(SwtImageConverter
												.ConvertToSwtImage(ImageResolver
														.get_Resolver()
														.ResolveImage(
																"[IMAGE]Core#Images.Icons.FolderClosed.png",
																0x12, 0x12)));
										node.setText(ph.get_Folder());
										node.setData(str);
									}
									set.add(ph.get_Folder());
								}
							}
						}
					}
				}
				
			}

		}
		tabRange.setControl(tree);
	}

	/**
	 * 动态加载查找树结构数据
	 */

	public void addfindtreedate() {

		findtree = new Tree(tabFolder, SWT.SINGLE);
		findtree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				TreeItem[] selected = findtree.getSelection();
				if (selected.length > 0) {
					addtabledate((selected[0].getData() + ""), "2");
				}

			}
		});

		IEnumerator it = m_lstSupportedScopesWithOwners.GetEnumerator();
		String nodetext = "";
		String images = "";
		while (it.MoveNext()) {

			ScopeUtil.ScopeAndOwners owners = (ScopeUtil.ScopeAndOwners) it
					.get_Current();

			nodetext = m_ScopeUtil.ScopeToString(owners.get_Scope());

			if (!StringUtils.IsEmpty(nodetext)) {
				switch (owners.get_Scope()) {
				case 1:
					images = "[IMAGE]Siteview#Images.Icons.Common.User.png";
					break;
				case 16:
					images = "[IMAGE]Siteview#Images.Icons.Common.Global.png";

				}

				TreeItem treeItem = new TreeItem(findtree, SWT.NONE);
				treeItem.setImage(SwtImageConverter.ConvertToSwtImage(
						ImageResolver.get_Resolver().ResolveImage(images),
						0x12, 0x12));
				treeItem.setText(nodetext);
				treeItem.setData(owners.get_Scope());
				HashSet<String> set = new HashSet<String>();
				for (String str : findhtCatToListerItems.keySet()) {
					ArrayList obs = findhtCatToListerItems.get(str);
					for (int i = 0; i < obs.get_Count(); i++) {
						PlaceHolder ph = (PlaceHolder) obs.get_Item(i);

						if (owners.get_Scope() == ph.get_Scope()) {

							if (!StringUtils.IsEmpty(ph.get_Folder())) {

								if (!set.contains(ph.get_Folder())) {
									TreeItem node = new TreeItem(treeItem,
											SWT.NONE);
									node.setImage(SwtImageConverter
											.ConvertToSwtImage(
													ImageResolver
															.get_Resolver()
															.ResolveImage(
																	"[IMAGE]Core#Images.Icons.FolderClosed.png"),
													0x12, 0x12));
									node.setText(ph.get_Folder());
									node.setData(owners.get_Scope() + "~"
											+ ph.get_Folder());
								}
								set.add(ph.get_Folder());
							}
						}
					}

				}
			}

		}
		tabFind.setControl(findtree);
	}

	/**
	 * 查找数据过滤
	 */
	public void Filterfinddate() {
		if (alldate != null) {
			findhtCatToListerItems.clear();
			for (int j = 0; j < alldate.get_Count(); j++) {
				ArrayList obs = (ArrayList) alldate.get_Item(j);
				for (int i = 0; i < obs.get_Count(); i++) {
					PlaceHolder holder = (PlaceHolder) obs.get_Item(i);
					if ((holder.get_Name().toUpperCase().indexOf(findtext) >= 0)
							|| (holder.get_Alias().toUpperCase()
									.indexOf(findtext) >= 0)
							|| (holder.get_Description().toUpperCase()
									.indexOf(findtext) >= 0)
							|| (holder.get_Folder().toUpperCase()
									.indexOf(findtext)) >= 0) {
						if (!findhtCatToListerItems.containsKey(holder
								.get_Scope() + "~" + holder.get_Folder())) {
							findhtCatToListerItems.put(holder.get_Scope() + "~"
									+ holder.get_Folder(), new ArrayList());
						}
						findhtCatToListerItems.get(
								holder.get_Scope() + "~" + holder.get_Folder())
								.Add(holder);
					}
				}
			}
		}
	}

	/**
	 * 
	 * 加载类别的数据
	 * 
	 */
	public void addlistdate() {
		tbCategory = new Table(tabFolder, SWT.BORDER | SWT.FULL_SELECTION);
		tbCategory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selected = tbCategory.getSelection();
				if (selected.length > 0) {
					addlisttabledate((String) selected[0].getData());
				}
			}
		});
		tbCategoryy.setControl(tbCategory);
		if (httypeToListerItems.isEmpty())
			return;
		tbCategory.removeAll();
		for (String str : httypeToListerItems.keySet()) {
			TableItem ti = new TableItem(tbCategory, SWT.NONE);
			ti.setImage(SwtImageConverter.ConvertToSwtImage(
					ImageResolver.get_Resolver().ResolveImage(
							"[IMAGE]Core#Images.Icons.FolderClosed.png"), 0x12,
					0x12));
			if (str.equals("")) {
				ti.setText("（无类别）");
				ti.setData(str);
			} else
				ti.setText(str);
			ti.setData(str);
		}
	}

	/**
	 * 点击类别 加载table里显示的详细信息
	 */
	public void addlisttabledate(String sel) {
		tbBusObList.removeAll();
		ArrayList obs = httypeToListerItems.get(sel);
		if (obs != null && obs.get_Count() > 0) {
			for (int i = 0; i < obs.get_Count(); i++) {
				PlaceHolder ph = (PlaceHolder) obs.get_Item(i);
				TableItem ti = new TableItem(tbBusObList, SWT.NONE);
				ti.setImage(SwtImageConverter
						.ConvertToSwtImage(
								ImageResolver
										.get_Resolver()
										.ResolveImage(getImageUrl(ph.get_Category())),
								0x12, 0x12));
				ti.setText(new String[] {
						ph.get_Alias(),
						ph.get_Description(),
						ph.get_Perspective().equals("(Base)") ? "(一般)" : ph
								.get_Perspective() });
				ti.setData(ph);
			}
		}

	}

	/**
	 * 点击树时加载table里显示的详细信息
	 * 
	 * @param type
	 *            =SHOWRANGE 显示范围和类别的数据 =SHOWFIND 显示查找的数据
	 */

	public void addtabledate(String sel, String type) {
		tbBusObList.removeAll();
		HashMap<String, ArrayList> dateItems = null;

		if ("SHOWRANGE".equals(type)) {
			dateItems = (HashMap<String, ArrayList>) htCatToListerItems;
		} else {
			dateItems = (HashMap<String, ArrayList>) findhtCatToListerItems;
		}
		if(serviceorchclient){
			String[] stip = sel.split("~");
			if(stip!=null&&stip.length==1&&stip[0].equals(Scope.Global+"")){
				for (String str : dateItems.keySet()) {
					String[] strsplit = str.split("~");
					if (stip[0].equals(strsplit[0])) {
						ArrayList obs = dateItems.get(str);
						for (int i = 0; i < obs.get_Count(); i++) {
							PlaceHolder ph = (PlaceHolder) obs.get_Item(i);
							if (StringUtils.IsEmpty(ph.get_Folder())) {
								TableItem ti = new TableItem(tbBusObList, SWT.NONE);
								ti.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage(getImageUrl(ph.get_Category())),0x12, 0x12));
								ti.setText(new String[] {ph.get_Alias(),ph.get_Description(),ph.get_Perspective().equals("(Base)") ? "(一般)": ph.get_Perspective() });
								ti.setData(ph);
								if(perspectiveDef != null && !"(Base)".equals(this.perspectiveDef.get_Name())
										&& "(Base)".equals(ph.get_Perspective())) ti.setForeground(new Color(null, 172, 168, 153));
							}
						}
						if(strsplit!=null&&strsplit.length>1&&(!StringUtils.IsEmpty(strsplit[2]))){
								TableItem t2 = new TableItem(tbBusObList, SWT.NONE);
								t2.setImage(SwtImageConverter.ConvertToSwtImage(
										ImageResolver.get_Resolver().ResolveImage(
												"[IMAGE]Core#Images.Icons.FolderClosed.png"),
										0x12, 0x12));
								t2.setText(new String[] { strsplit[2], "", "" });
								t2.setData(str);
						}
					}
				}
				
				
			}else if (stip != null && stip.length == 3&&!StringUtils.IsEmpty(stip[2])) {
				for (String str : dateItems.keySet()) {
					if (sel.equals(str)) {
						ArrayList obs = dateItems.get(str);
						for (int i = 0; i < obs.get_Count(); i++) {
							PlaceHolder ph = (PlaceHolder) obs.get_Item(i);
							TableItem ti = new TableItem(tbBusObList, SWT.NONE);
							ti.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage(getImageUrl(ph.get_Category())),0x12, 0x12));
							ti.setText(new String[] {ph.get_Alias(),ph.get_Description(),ph.get_Perspective().equals("(Base)") ? "(一般)": ph.get_Perspective() });
							ti.setData(ph);
							if(perspectiveDef != null && !"(Base)".equals(this.perspectiveDef.get_Name())
									&& "(Base)".equals(ph.get_Perspective())) ti.setForeground(new Color(null, 172, 168, 153));
//							if(!StringUtils.IsEmpty(stip[2])){
//								String[] strsplit = str.split("~");
//								if (stip[0].equals(strsplit[0]) && strsplit != null
//										&& strsplit.length > 1&&(!StringUtils.IsEmpty(ph.get_Folder()))) {
//									TableItem t2 = new TableItem(tbBusObList, SWT.NONE);
//									t2.setImage(SwtImageConverter.ConvertToSwtImage(
//											ImageResolver.get_Resolver().ResolveImage(
//													"[IMAGE]Core#Images.Icons.BPCategory.png"),
//											0x12, 0x12));
//									t2.setText(new String[] { strsplit[2], "", "" });
//									t2.setData(str);
//								}
//							}
						}
					}
				}
			}else if(stip != null && stip.length ==2&&!StringUtils.IsEmpty(stip[1])){
				for (String str : dateItems.keySet()) {
					String[] strsplit = str.split("~");
					if (strsplit != null&& strsplit.length >=2&&stip[0].equals(strsplit[0])&&stip[1].equals(strsplit[1])) {
						if(strsplit.length > 2 && !StringUtils.IsEmpty(strsplit[2])){
							TableItem t2 = new TableItem(tbBusObList, SWT.NONE);
							t2.setImage(SwtImageConverter.ConvertToSwtImage(
									ImageResolver.get_Resolver().ResolveImage(
											"[IMAGE]Core#Images.Icons.FolderClosed.png"),
									0x12, 0x12));
							t2.setText(new String[] { strsplit[2], "", "" });
							t2.setData(str);
						}else {
							ArrayList obs = dateItems.get(str);
							for (int i = 0; i < obs.get_Count(); i++) {
								PlaceHolder ph = (PlaceHolder) obs.get_Item(i);
								TableItem ti = new TableItem(tbBusObList, SWT.NONE);
								ti.setImage(SwtImageConverter
										.ConvertToSwtImage(
												ImageResolver
														.get_Resolver()
														.ResolveImage(getImageUrl(ph.get_Category())),
												0x12, 0x12));
								ti.setText(new String[] {
										ph.get_Alias(),
										ph.get_Description(),
										ph.get_Perspective().equals("(Base)") ? "(一般)"
												: ph.get_Perspective() });
								ti.setData(ph);
								if(perspectiveDef != null && !"(Base)".equals(this.perspectiveDef.get_Name())
										&& "(Base)".equals(ph.get_Perspective())) ti.setForeground(new Color(null, 172, 168, 153));
							}
						}
					}
				}
			}
		}else {
			for (String str : dateItems.keySet()) {
				String[] stip = sel.split("~");
				if (stip != null && stip.length > 1) {
					if (sel.equals(str)) {
						ArrayList obs = dateItems.get(str);
						for (int i = 0; i < obs.get_Count(); i++) {
							PlaceHolder ph = (PlaceHolder) obs.get_Item(i);
							TableItem ti = new TableItem(tbBusObList, SWT.NONE);
							ti.setImage(SwtImageConverter
									.ConvertToSwtImage(
											ImageResolver
													.get_Resolver()
													.ResolveImage(getImageUrl(ph.get_Category())),
											0x12, 0x12));
							ti.setText(new String[] {
									ph.get_Alias(),
									ph.get_Description(),
									ph.get_Perspective().equals("(Base)") ? "(一般)"
											: ph.get_Perspective() });
							ti.setData(ph);
						}
					}
				} else {
					String[] strsplit = str.split("~");
					if (stip[0].equals(strsplit[0]) && strsplit != null
							&& strsplit.length > 1) {
						TableItem t2 = new TableItem(tbBusObList, SWT.NONE);
						t2.setImage(SwtImageConverter.ConvertToSwtImage(
								ImageResolver.get_Resolver().ResolveImage(
										"[IMAGE]Core#Images.Icons.FolderClosed.png"),
								0x12, 0x12));
						t2.setText(new String[] { strsplit[1], "", "" });
						t2.setData(str);
					}

					if (stip[0].equals(strsplit[0])) {
						ArrayList obs = dateItems.get(str);
						for (int i = 0; i < obs.get_Count(); i++) {
							PlaceHolder ph = (PlaceHolder) obs.get_Item(i);
							if (StringUtils.IsEmpty(ph.get_Folder())) {
								TableItem ti = new TableItem(tbBusObList, SWT.NONE);
								ti.setImage(SwtImageConverter
										.ConvertToSwtImage(
												ImageResolver
														.get_Resolver()
														.ResolveImage(getImageUrl(ph.get_Category())),
												0x12, 0x12));
								ti.setText(new String[] {
										ph.get_Alias(),
										ph.get_Description(),
										ph.get_Perspective().equals("(Base)") ? "(一般)"
												: ph.get_Perspective() });
								ti.setData(ph);
							}
						}
					}
				}
			}
		}
		
	}

	/**
	 * Create the menu manager.
	 * 
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {

		MenuManager menuBar = new MenuManager();// 创建得菜单栏对象
		MenuManager OpMenu = new MenuManager("操作(A)"); // 操作菜单项
		MenuManager ViewMenu = new MenuManager("视图(v)"); // 视图菜单项

		menuBar.add(OpMenu);
		menuBar.add(ViewMenu);
		
		OpMenu.add(actAdd);
		OpMenu.add(actEdit);
		OpMenu.add(actCopy);
		OpMenu.add(actDelete);

		ViewMenu.add(qbigicon);
		ViewMenu.add(qsmallicon);
		ViewMenu.add(qdetailed);
		ViewMenu.add(new Separator());
		qdelqueryop.setEnabled(false);
		ViewMenu.add(qdelqueryop);
		ViewMenu.add(new Separator());
		ViewMenu.add(qref);

		return menuBar;
	}

	/**
	 * Create the coolbar manager.
	 * 
	 * @return the coolbar manager
	 */
	protected CoolBarManager createCoolBarManager(int style) {
		CoolBarManager coolBarManager = new CoolBarManager(style);
		IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBarManager.add(new ToolBarContributionItem(toolbar, "main"));

		toolbar.add(actAdd);
		toolbar.add(actEdit);
		toolbar.add(actCopy);
		toolbar.add(actDelete);
		toolbar.add(new Separator());
		ControlContribution cc = new ControlContribution("") {
			@Override
			protected Control createControl(Composite parent) {
				Composite container = new Composite(parent, SWT.NONE);
				Label searchLabel = new Label(container, SWT.NONE);
				searchLabel.setSize(30, 20);
				searchLabel.setLocation(5, 5);
				searchLabel.setText("查找:");
				cb = new Combo(container, SWT.NONE);
				cb.setSize(150, 20);
				cb.setLocation(35, 2);
				return container;
			}
		};
		toolbar.add(cc);
		toolbar.add(actSearch);
		IToolBarManager toolbar2 = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		coolBarManager.add(new ToolBarContributionItem(toolbar2, "linkObject"));
		toolbar2.add(new ControlContribution("") {

			@Override
			protected Control createControl(Composite parent) {
				Composite container = new Composite(parent, SWT.NONE);
				Label lblLinkTxt = new Label(container, SWT.NONE);
				lblLinkTxt.setSize(225, 20);
				lblLinkTxt.setLocation(5, 5);
				lblLinkTxt.setText("查找与以下对象关联的   仪表盘部件:");

				cboQuickOblink = new Combo(container, SWT.READ_ONLY);
				cboQuickOblink.setSize(180, 20);
				cboQuickOblink.setLocation(230, 0);
				cboQuickOblink.setVisibleItemCount(20);
				cboQuickOblink.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						cboQuickOblinkSelected();
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});

				return container;
			}
		});
		if(serviceorchclient && perspectiveDef != null)
		{
			IToolBarManager toolbar3 = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
			coolBarManager.add(new ToolBarContributionItem(toolbar3, "replace"));
			toolbar3.add(new ControlContribution(""){

				@Override
				protected Control createControl(Composite parent) {
					Composite container = new Composite(parent,SWT.NONE);
					
					check_Override = new Button(container, SWT.CHECK);
					check_Override.setSize(225,20);
					check_Override.setLocation(5,2);
					check_Override.addSelectionListener(new SelectionAdapter(){
						@Override
						public void widgetSelected(SelectionEvent e) {
							TableItem[] items = tbBusObList.getSelection();
							if(items != null && items.length > 0)
							{
								TableItem item = tbBusObList.getSelection()[0];
								Object o = item.getData();
								Override_Clicked(o);
							}
						}
					});
					check_Override.setText("  取代当前透视中的定义。");
					check_Override.setEnabled(false);
					return container;
				}});
		}
		
		return coolBarManager;
	}

	/**
	 * 创建Action
	 */

	private void createActions() {
			
		actAdd = new Action("新建(&N)"){
			@Override
			public void run() {
				TreeItem[] selected=tree.getSelection();
				
				int scope_int=16;
				String folderName="";
				if(selected.length>0){
					String[] stip=(selected[0].getData()+"").split("~");
					if(stip.length>0){
						scope_int=Integer.parseInt(stip[0]);
						if(!serviceorchclient && stip.length>1){
							folderName=stip[1];
						}else if(serviceorchclient && stip.length > 2)
						{
							folderName=stip[2];
						}
					} 
				}
				if (!HasAddRight(scope_int))
				{
					MessageDialog.openInformation(getShell(), "权限不足", "您没有权限进行此操作！");
					return;
				}
			
				DaBoTypeSelecttDlg dbsd = new DaBoTypeSelecttDlg(getShell());
				if(dbsd.open()==Dialog.OK){
					System.out.println();
					String partType = dbsd.getSelectText();
//					if(dbsd.getSelectText().equals("Chart")) partType = "ChartPie";
					dashboardPartDef = (DashboardPartDef)m_Library.GetNewDefForEditing(DefRequest.ByCategory(scope_int, "", "DashboardPartDef", partType));
					m_Def=dashboardPartDef;
					m_Def.set_Folder(folderName);
					dashboardPartDef = getPartType(dashboardPartDef.get_CategoryAsString(), getShell(), dashboardPartDef, m_Library, m_ScopeUtil, m_api, cboQuickOblink.getData(cboQuickOblink.getItem(cboQuickOblink.getSelectionIndex())).toString(), "New", m_Def);
					if(dashboardPartDef != null){
						m_Library.UpdateDefinition(dashboardPartDef, true);
						Refresh();
					}
					
				}

			}
		};
		
		actAdd.setImageDescriptor(new ImageDescriptor(){
			@Override
			public ImageData getImageData() {
				return SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[Image]Core#Images.Icons.NewAdd.png"),0x12,0x12).getImageData();
			}});
		actAdd.setAccelerator(SWT.CTRL + 'N');
		
		
		actEdit = new Action("编辑(&E)"){
			@Override
			public void run() {
				if (tbBusObList.getSelection().length < 1) return;
				TableItem ti = tbBusObList.getSelection()[0];
				PlaceHolder ph = (PlaceHolder)ti.getData();
				IDefinition defForEditing = m_Library.GetDefForEditing(DefRequest.ByHolder(ph));
				
				if ((defForEditing != null) && HasEditRight(defForEditing.get_Scope())){
					dashboardPartDef=(DashboardPartDef) defForEditing;
					dashboardPartDef = getPartType(dashboardPartDef.get_CategoryAsString(), getShell(), dashboardPartDef, m_Library, m_ScopeUtil, m_api, ph.get_LinkedTo(), "Edit",null);
					
					if(dashboardPartDef != null){
						if(serviceorchclient)
						{
							SynchronizeCoreProperties(dashboardPartDef);
							ReplaceDefinitionInformationInRunMru(dashboardPartDef);
						}
						m_Library.UpdateDefinition(dashboardPartDef, false);
						Refresh();
					}
				}else
				{
					//没有权限
					MessageDialog.openInformation(getShell(), "权限不足", "您没有权限进行此操作！");
				}
			}
		};
		actEdit.setAccelerator(SWT.CTRL + 'E');
		
		actEdit.setImageDescriptor(new ImageDescriptor(){
			@Override
			public ImageData getImageData() {
				return SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[Image]Core#Images.Icons.Edit.png"),0x12,0x12).getImageData();
			}});
		
		actCopy = new Action("复制(&F)"){
			@Override
			public void run() {
				if (tbBusObList.getSelection().length < 1) return;
				
//				TreeItem[] selected=tree.getSelection();
//				int phtype=16;
//				if(selected.length>0){
//					String[] stip=(selected[0].getData()+"").split("~");
//					if(stip.length>0){
//						phtype=Integer.parseInt(stip[0]);
//					}
//				}
				
				TableItem ti = tbBusObList.getSelection()[0];
				PlaceHolder ph = (PlaceHolder)ti.getData();
				IDefinition defForEditing = m_Library.GetDefForEditing(DefRequest.ByHolder(ph));
				if ((defForEditing != null) && HasAddRight(defForEditing.get_Scope())){
					dashboardPartDef=(DashboardPartDef) defForEditing;
					DashboardPartDef newdashboardPartDef = (DashboardPartDef)m_Library.GetNewDefForEditing(DefRequest.ByCategory(4, "", DashboardPartDef.get_ClassName(),dashboardPartDef.get_CategoryAsString()));
					m_Def=newdashboardPartDef;
					dashboardPartDef = getPartType(dashboardPartDef.get_CategoryAsString(), getShell(), dashboardPartDef, m_Library, m_ScopeUtil, m_api, ph.get_LinkedTo(), "Copy",m_Def);
					if(dashboardPartDef != null)
					{
						dashboardPartDef.set_Id(m_Def.get_Id());
						m_Library.UpdateDefinition(dashboardPartDef, true);
						Refresh();
					}
				}else
				{
					//没有权限
					MessageDialog.openInformation(getShell(), "权限不足", "您没有权限进行此操作！");
				}
			}
		};
		actCopy.setAccelerator(SWT.CTRL + 'F');
		
		actCopy.setImageDescriptor(new ImageDescriptor(){
			@Override
			public ImageData getImageData() {
				return SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[Image]Core#Images.Icons.Copy.png"),0x12,0x12).getImageData();
			}});
		

		actDelete = new Action("删除(&D)") {
			@Override
			public void run() {

				if (tbBusObList.getSelection().length < 1)
					return;
				tbBusObList.setCursor(new Cursor(null,SWT.CURSOR_WAIT));
				TableItem ti = tbBusObList.getSelection()[0];
				PlaceHolder ph = (PlaceHolder) ti.getData();
				
				if(!MessageDialog.openQuestion(getShell(), "SiteView应用程序", "是否删除 ' "+ph.get_Alias()+" '")){
					tbBusObList.setCursor(new Cursor(null,SWT.CURSOR_ARROW));
					return ;
				}
				IDefinition def = m_Library.GetDefForEditing(DefRequest
						.ByHolder(ph));
				if ((def != null) && HasDeleteRight(def.get_Scope())) {
//					RemoveDefinitionInformationFromRunMru(def);
					PerspectiveDef defForEditing = null;
					IViewOverrideInfo voi = null;
					if (StringUtils.IsEmpty(def.get_Perspective())
							|| "(Base)".equals(def.get_Perspective())) {
						IDefinition definition = null;
						ICollection list = m_Library
								.GetPlaceHolderList(DefRequest
										.ForList(Siteview.Api.PerspectiveDef
												.get_ClassName()));
						IEnumerator it = list.GetEnumerator();
						while (it.MoveNext()) {
							PlaceHolder holder = (PlaceHolder) it.get_Current();
							defForEditing = (PerspectiveDef) m_Library
									.GetDefForEditing(DefRequest.ById(
											holder.get_DefClassName(),
											holder.get_Id()));
							if (defForEditing != null) {
								voi = defForEditing.GetViewForDefinitionById(
										def.get_InstanceClassName(),
										def.get_Id());
								if ((voi != null)
										&& !"(Base)".equals(voi
												.get_Perspective())) {
									definition = m_Library
											.GetDefinition(DefRequest.ById(
													def.get_Scope(),
													def.get_ScopeOwner(),
													def.get_LinkedTo(),
													def.get_InstanceClassName(),
													def.get_Id(),
													defForEditing.get_Name(),
													false));
									if (definition != null) {
										definition = definition.CloneForEdit();
										m_Library
												.MarkDefinitionForDeletion(definition);
										// }
										defForEditing.RemoveOverride(voi);
										m_Library
												.UpdateDefinition(defForEditing);
									}
								}
							}
						}
					} else {
						defForEditing = (PerspectiveDef) m_Library
								.GetDefForEditing(DefRequest.ByName(
										Siteview.Api.PerspectiveDef
												.get_ClassName(), def
												.get_Perspective()));
						if (defForEditing != null) {
							voi = defForEditing.GetViewForDefinitionById(
									def.get_InstanceClassName(), def.get_Id());
							defForEditing.RemoveOverride(voi);
							m_Library.UpdateDefinition(defForEditing);
						}
					}
					m_Library.MarkDefinitionForDeletion(def);
					// 刷新
					Refresh();
				}else
				{
					//没有权限
					MessageDialog.openInformation(getShell(), "权限不足", "您没有权限进行此操作！");
				}
				tbBusObList.setCursor(new Cursor(null,SWT.CURSOR_ARROW));
			}
		};
		actDelete.setAccelerator(SWT.CTRL + 'D');
		actDelete.setImageDescriptor(new ImageDescriptor() {
			@Override
			public ImageData getImageData() {
				return SwtImageConverter.ConvertToSwtImage(
						ImageResolver.get_Resolver().ResolveImage(
								"[Image]Core#Images.Icons.Delete.png", 0x12,
								0x12)).getImageData();
			}
		});

		actSearch = new Action("搜索") {
			@Override
			public void run() {
				findtext = cb.getText().toUpperCase();
				if (StringUtils.IsEmpty(findtext))
					return;
				int count = tabFolder.getItemCount();
				if (count < 3) {
					tabFind = new TabItem(tabFolder, SWT.NONE);
					tabFolder.setSelection(tabFind);
					tabFind.setText("查找");
					tabFind.setImage(SwtImageConverter.ConvertToSwtImage(
							ImageResolver.get_Resolver().ResolveImage(
									"[Image]Core#Images.Icons.Search.png"),
							0x10, 0x10));
					Filterfinddate();
					addfindtreedate();
					addtabledate(Scope.Global+"", "SHOWFIND");
				} else {
					findtree.removeAll();
					Filterfinddate();
					addfindtreedate();
					addtabledate(Scope.Global+"", "SHOWFIND");
				}
			}
		};
		actSearch.setImageDescriptor(new ImageDescriptor() {
			@Override
			public ImageData getImageData() {
				return SwtImageConverter
						.ConvertToSwtImage(
								ImageResolver
										.get_Resolver()
										.ResolveImage(
												"[Image]Siteview#Images.Icons.Common.Find.png"))
						.getImageData();
			}
		});

//		qrun = new Action("运行(&R)") {
//			@Override
//			public void run() {
//				if (tbBusObList.getSelection().length < 1)
//					return;
//				TableItem ti = tbBusObList.getSelection()[0];
//				PlaceHolder ph = (PlaceHolder) ti.getData();
//				AutoTaskDef def = (AutoTaskDef) m_Library
//						.GetDefForEditing(DefRequest.ByHolder(ph));
//				if(RunAutoTasks.RunAtuotasts(def, m_Library, m_api, busob)){
//					getShell().close();
//				}
//	//			CommHistoryDataLoad.loadHistoryNav(busob);
//			}
//		};
//		qrun.setAccelerator(SWT.CTRL + 'R');
//		qrungroup = new Action("为群组运行(&G)") {
//			@Override
//			public void run() {
//				
//				if (tbBusObList.getSelection().length < 1)
//					return;
//				TableItem ti = tbBusObList.getSelection()[0];
//				PlaceHolder ph = (PlaceHolder) ti.getData();
//				AutoTaskDef def = (AutoTaskDef) m_Library
//						.GetDefForEditing(DefRequest.ByHolder(ph));
//				if(RunAutoTasks.RunForGroupAtuotasts(def, m_Library, m_api, busob, m_virtualKeyList)){
//					getShell().close();
//				}
//		//		CommHistoryDataLoad.loadHistoryNav(busob);
//				
//			}
//		};

		qbigicon = new Action("大图标(&I)") {
			@Override
			public void run() {

			}
		};

		qbigicon.setAccelerator(SWT.CTRL + 'I');
		qsmallicon = new Action("列表(&L)") {
			@Override
			public void run() {

			}
		};
		qsmallicon.setAccelerator(SWT.CTRL + 'L');
		qdetailed = new Action("详细列表(&Y)") {
			@Override
			public void run() {

			}
		};
		qdetailed.setAccelerator(SWT.CTRL + 'Y');
		qdelqueryop = new Action("删除查询选项(&T)") {
			@Override
			public void run() {

			}
		};
		qdelqueryop.setAccelerator(SWT.CTRL + 'T');
		qref = new Action("刷新            F5") {
			@Override
			public void run() {
				Refresh();
			}
		};
		actEdit.setEnabled(false);
		actCopy.setEnabled(false);
		actDelete.setEnabled(false);
	}

	/**
	 * 
	 * ACTION 打开关闭
	 */

	protected void openclosecoolbar(boolean flag) {
		actEdit.setEnabled(flag);
		actCopy.setEnabled(flag);
		actDelete.setEnabled(flag);
	}


	protected void RemoveDefinitionInformationFromRunMru(IDefinition def) {
		if (def != null) {
			IMru mru = m_api.get_SettingsService().get_Mru();
			if (mru != null) {
				String strMruId = (def.get_LinkedTo() != "") ? "DashboardPartDef.Run."
						+ def.get_LinkedTo()
						: "DashboardPartDef.Run";

				mru.RemoveItem(
						strMruId,
						XmlScope.CategoryToXmlCategory(def.get_Scope()) + ","
								+ def.get_ScopeOwner() + ","
								+ def.get_LinkedTo() + "," + def.get_Folder()
								+ "," + def.get_Id() + "," + def.get_Name()
								+ "," + def.get_Alias() + ","
								+ def.get_Perspective());
				mru.Flush(strMruId);
			}
		}
	}

	/**
	 * 刷新
	 */

	public void Refresh() {

		m_api=ConnectionBroker.get_SiteviewApi();
		htCatToListerItems.clear();
		alldate.Clear();
		httypeToListerItems.clear();
		tbBusObList.removeAll();
		getcboQuickOblink(cboQuickOblink.getData(
				cboQuickOblink.getText().trim()).toString());

		TabItem[] selected = tabFolder.getSelection();
		if (selected.length > 0) {

			if ("类别".equals(selected[0].getText())) {
				TableItem[] se = tbCategory.getSelection();
				if (se.length > 0) {
					addlistdate();
					addlisttabledate((String) se[0].getData());

				} else {
					addlistdate();
					addlisttabledate("");
				}

			} else if ("查找".equals(selected[0].getText())) {
				TreeItem[] findfreeselected = findtree.getSelection();
				if (findfreeselected.length > 0) {
					addfindtreedate();
					findtree.select(findfreeselected[0]);

					TreeItem[] rangetree = findtree.getItems();
					for (int i = 0; i < rangetree.length; i++) {

						TreeItem[] ran = rangetree[i].getItems();

						for (int j = 0; j < ran.length; j++) {
							TreeItem item = ran[j];
							if (item.getData().equals(
									findfreeselected[0].getData())) {
								findtree.setSelection(item);
								findtree.showItem(item);
							}
						}

					}
					addtabledate((String) findfreeselected[0].getData(), "SHOWFIND");
				} else {
					addfindtreedate();
					addtabledate(Scope.Global+"", "SHOWFIND");
				}
			} else {
				TreeItem[] freeselected = tree.getSelection();
				if (freeselected.length > 0) {
					addtreedate();

					TreeItem[] rangetree = tree.getItems();
					for (int i = 0; i < rangetree.length; i++) {

						TreeItem[] ran = rangetree[i].getItems();

						for (int j = 0; j < ran.length; j++) {
							TreeItem item = ran[j];
							if (item.getData()
									.equals(freeselected[0].getData())) {
								tree.setSelection(item);
								tree.showItem(item);
							}
						}

					}
					addtabledate((String) freeselected[0].getData(), "SHOWRANGE");
				} else {
					addtreedate();
					addtabledate(Scope.Global+"", "SHOWRANGE");
				}
			}
		}

	}

	public DashboardPartDef getSelection() {
		IDefinition defForEditing = m_Library.GetDefForEditing(DefRequest
				.ByHolder(selection));
		if ((defForEditing != null) && HasEditRight(defForEditing.get_Scope())) {
			dashboardPartDef = (DashboardPartDef) defForEditing;
			return dashboardPartDef;
		}
		return null;
	}

	@Override
	public int open() {
		this.setBlockOnOpen(true);
		return super.open();
	}

	@Override
	public String get_DefinitionClassName() {
		return m_strDefClassName;
	}

	@Override
	public boolean ShowDialog() {
		return this.open() == Dialog.OK;
	}

	@Override
	public PlaceHolder get_SelectedObject() {
		return selection;
	}

	@Override
	public void set_ShowOnlyGlobal(boolean bShowOnlyGlobal) {
		
	}

	@Override
	public void set_LinkedTo(String linkTo) {

	}

	@Override
	public void setShowButtons(boolean showButtons) {

	}
	
	
	/*
	 * 权限组-编辑
	 */
	private boolean HasEditRight(Integer s) {
		if (s != Scope.Unknown){
			return m_api.get_SecurityService().HasModuleItemRight(m_moduleSecurityName, XmlScope.CategoryToXmlCategory(s),SecurityRight.Edit);
		}
		return false;
	}
	
	/*
	 * 权限组-添加
	 */
	private boolean HasAddRight(Integer s) {
		if (s != Scope.Unknown){
			return m_api.get_SecurityService().HasModuleItemRight(m_moduleSecurityName, XmlScope.CategoryToXmlCategory(s),SecurityRight.Add);
		}
		return false;
	}
	
	/*
	 * 权限组-删除
	 */
	private boolean HasDeleteRight(Integer s) {
		if (s != Scope.Unknown){
			return m_api.get_SecurityService().HasModuleItemRight(m_moduleSecurityName, XmlScope.CategoryToXmlCategory(s),SecurityRight.Delete);
		}
		return false;
	}
	
	
	
	private String getImageUrl(String imageCategory) {
		if ("IMAGE".equals(imageCategory)) 
			return "[IMAGE]Siteview#Images.Icons.Common.Image.png";
		
		else if ("LINKLIST".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Link_list.png";
		
	    else if ("MSPROJECT".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.MSProject.png";
		
	    else if ("MULTIQUERYGRID".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.MultiQueryGrid.png";
		
	    else if ("MULTIVIEWGRID".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.MultiViewGrid.png";
		
	    else if ("OUTLOOKCALENDAR".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Outlook_Calendar.png";
		
	    else if ("OUTLOOKINBOX".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Outlook_Inbox.png";
		
	    else if ("TABBEDPART".equals(imageCategory) || "TabbedPart".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Tabbed.png";
		
	    else if ("WEBBROWSER".equals(imageCategory) || "WebBrowser".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Web_Browser.png";
		
	    else if ("CHARTBAR".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Bar_Chart.png";
		
	    else if ("CHARTCOLUMN".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.ColumnChart.png";
		
	    else if ("CHARTPIPELINE".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.DotChart.png";
		
	    else if ("GAUGELINEAR".equals(imageCategory) || "GaugeLinear".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Thermometer.png";
		
	    else if ("CHARTLINE".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Line_Chart.png";
		
	    else if ("CHARTPIE".equals(imageCategory))
			return "[IMAGE]Siteview#Images.Icons.Common.Pie_Chart.png";
		
	    else if ("OBJECTTOPOLOGICALDIAGRAM".equals(imageCategory))
	    	return "[IMAGE]Core#Images.Icons.ComponentYellow.png";
		
	    else if ("SPEEDOMETER".equals(imageCategory))
	    	return "[IMAGE]Siteview#Images.Icons.Common.Speedometer.png";
		
	    else if ("BUSINESSOBJECT".equals(imageCategory))
	    	return "[IMAGE]Core#Images.Icons.BusinessObject16.png";
		
		return "";
	}

	
	
	public static DashboardPartDef getPartType(String partType, Shell parentShell, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, String type,IDefinition m_Def){
		
		int state = 1;
		
		if ("OUTLOOKCALENDAR".equals(partType)){
			state = new OutlookCalendar(parentShell, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, type, m_Def).open();
		
		}else if ("OUTLOOKINBOX".equals(partType)){
			state = new OutlookInbox(parentShell, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, type, m_Def).open();
		
		}else if ("WEBBROWSER".equals(partType)){
			state = new WebBrowser(parentShell, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, type, m_Def).open();
		
		}else if("MULTIQUERYGRID".equals(dashboardPartDef.get_CategoryAsString())){
	    	UtilityGrid newSelectedDlg = new UtilityGrid(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil, m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    
	    }else if("MULTIVIEWGRID".equals(dashboardPartDef.get_CategoryAsString())){
	    	ViewGrid newSelectedDlg = new ViewGrid(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil, m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    //图像
	    }else if("IMAGE".equals(dashboardPartDef.get_CategoryAsString())){
	    	ImageDlg newSelectedDlg = new ImageDlg(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    //选项卡部分
	    }else if("TABBEDPART".equals(dashboardPartDef.get_CategoryAsString())){
	    	MotionsPart newSelectedDlg = new MotionsPart(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    //微软项目
	    }else if("MSPROJECT".equals(dashboardPartDef.get_CategoryAsString())){
	    	SoftItem newSelectedDlg = new SoftItem(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    //链接列表
	    }else if("LINKLIST".equals(dashboardPartDef.get_CategoryAsString())){
	    	LinkGrid newSelectedDlg = new LinkGrid(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    }else if("CHARTPIE".equals(dashboardPartDef.get_CategoryAsString()) || "CHARTBAR".equals(dashboardPartDef.get_CategoryAsString()) ||
	    		"NONE".equals(dashboardPartDef.get_CategoryAsString()) || "CHARTCOLUMN".equals(dashboardPartDef.get_CategoryAsString()) ||
	    		"CHARTLINE".equals(dashboardPartDef.get_CategoryAsString()) || "CHARTPIPELINE".equals(dashboardPartDef.get_CategoryAsString()) ||
	    		"GAUGELINEAR".equals(dashboardPartDef.get_CategoryAsString()) || "SPEEDOMETER".equals(dashboardPartDef.get_CategoryAsString())){
	    	System.out.println(dashboardPartDef);
	    	DiagramWizard dw = new DiagramWizard(dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, type, m_Def);
	    	
	    	state = new WizardDialog(parentShell,dw).open();
	    	if(state == 0) dashboardPartDef = dw.getDashboardPartDef();
	    //对象关系拓扑图
	    }else if("OBJECTTOPOLOGICALDIAGRAM".equals(dashboardPartDef.get_CategoryAsString())){
	    	ObjectRelationTopo newSelectedDlg = new ObjectRelationTopo(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    
	    //对象浏览器ObjectBrowser
	    }else if("OBJECTBROWSER".equals(dashboardPartDef.get_CategoryAsString())){
	    	ObjectBrowser newSelectedDlg = new ObjectBrowser(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    //业务对象定义
	    }else if("BUSINESSOBJECT".equals(dashboardPartDef.get_CategoryAsString())){
	    	BusinessObjectDlg newSelectedDlg = new BusinessObjectDlg(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    
	    }//自定义部件
	    else if("CUSTOM".equals(dashboardPartDef.get_CategoryAsString())){
	    	CustomPartDlg newSelectedDlg = new CustomPartDlg(parentShell, dashboardPartDef,m_Api, m_Library, m_ScopeUtil,m_Def, linkTo, type);
	    	state = newSelectedDlg.open();
	    }
		if(state == 0) return dashboardPartDef;
		
		return null;
	}
	
	public boolean IsOverriddenObject(Object o)
    {
		boolean flag = false;
        IDefinition definition = null;
        if (o != null)
        {
            if (o instanceof PlaceHolder)
            {
                definition = m_Library.GetDefinition(DefRequest.ByHolder((PlaceHolder)o));
            }
            else if (o instanceof IDefinition)
            {
                definition = (IDefinition) o;
            }
            if (((definition != null) && (!"".equals(definition.get_Perspective()))) && (!"(Base)".equals(definition.get_Perspective())))
            {
                flag = true;
            }
        }
        return flag;
    }
	
	private void Override_Clicked(Object sender)
    {
		boolean isOverride = check_Override.getSelection();
        if (isOverride)
        {
        	try
        	{
        		Display.getCurrent().getActiveShell().setCursor(new Cursor(Display.getDefault(),SWT.CURSOR_WAIT));
        		Object ob = OverrideObject(sender);
            	if(ob == null) check_Override.setSelection(false);
            	else	SetTargetListItemColor((PlaceHolder)ob, true);
        	}
        	finally
        	{
        		Display.getCurrent().getActiveShell().setCursor(new Cursor(Display.getDefault(),SWT.CURSOR_ARROW));
        	}
        }
        else
        {
        	try
        	{
        		Display.getCurrent().getActiveShell().setCursor(new Cursor(Display.getDefault(),SWT.CURSOR_WAIT));
        		SetTargetListItemColor((PlaceHolder)UnOverrideObject(sender), false);
        	}
        	finally
        	{
        		Display.getCurrent().getActiveShell().setCursor(new Cursor(Display.getDefault(),SWT.CURSOR_ARROW));
        	}
            
        }
        
//        this.UpdateMenusAndToolbar();
//        this.RefreshTargetView();
    }
	
	public Object OverrideObject(Object o)
    {
        PlaceHolder holder = null;
        if (this.perspectiveDef == null)
        {
            return holder;
        }
        IDefinition definition = null;
        if (o instanceof PlaceHolder)
        {
            definition = m_Library.GetDefinition(DefRequest.ByHolder((PlaceHolder)o));
        }
        else if (o instanceof IDefinition)
        {
            definition = (IDefinition) o;
        }
        if (definition == null)
        {
            return holder;
        }
        IDefinition def = definition.CloneForEdit();
        def.set_Perspective(this.perspectiveDef.get_Name());
        if (this.HasAddRight(def.get_Scope()))
        {
            if(def != null) this.m_Library.UpdateDefinition(def, true);
        }
//        this.m_strSelectedId = def.get_Id();
        IViewOverrideInfo voi = new ViewOverrideInfo(perspectiveDef.get_Name(), def.get_Name(), def.get_Id(), def.get_InstanceClassName(), ViewBehavior.CustomVersion, "", "", "");
        this.perspectiveDef.AddOverride(voi);
        this.m_Library.UpdateDefinition(perspectiveDef);
        
//        if (((definition != null) && !"".equals(definition.get_Perspective()) && !"(Base)".equals(definition.get_Perspective())))
//        {
//        	SetTargetListItemColor(true);
//        }
        
        return new PlaceHolder(def);
    }

	
//	private String getScopeString(int scope)
//	{
//		String scopeString = "";
//		switch(scope)
//		{
//		case Scope.Global:
//			scopeString = "Global";
//			break;
//		case Scope.Role:
//			scopeString = "Role";
//			break;
//		case Scope.User:
//			scopeString = "User";
//		}
//		
//		return scopeString;
//	}
	
	 public Object UnOverrideObject(Object o)
     {
         PlaceHolder holder = null;
         if (this.perspectiveDef != null)
         {
             IDefinition def = null;
             if (o instanceof PlaceHolder)
             {
                 PlaceHolder ph = (PlaceHolder)o;
                 def = m_Library.GetDefinition(DefRequest.ByHolder(ph));
             }
             else if (o instanceof IDefinition)
             {
                 def = (IDefinition) o;
             }
             if (def != null)
             {
                 IViewOverrideInfo viewForDefinition = perspectiveDef.GetViewForDefinition(def.get_InstanceClassName(), def.get_Name());
                 IDefinition definition2 = m_Library.GetDefinition(DefRequest.ById(def.get_Scope(), def.get_ScopeOwner(), "", "DashboardPartDef", def.get_Id(), "(Base)", false));
//                 this.m_strSelectedId = definition2.Id;
                 if(definition2 != null)
                 {
                	 this.DeleteDefinition(def);
                     perspectiveDef.RemoveOverride(viewForDefinition);
                     m_Library.UpdateDefinition(perspectiveDef);
                     holder = new PlaceHolder(definition2);
                 }else
                 { 
                	 return null;
                 }
             }
             
//             if (((def != null) && !"".equals(def.get_Perspective()) && !"(Base)".equals(def.get_Perspective())))
//             {
//             	SetTargetListItemColor(false);
//             }
         }
         
         return holder;
     }
	 
	 protected boolean DeleteDefinition(IDefinition def)
     {
		 boolean flag = false;
         Siteview.Api.PerspectiveDef defForEditing = null;
         IViewOverrideInfo voi = null;
         if (def != null)
         {
             this.RemoveDefinitionInformationFromRunMru(def);
             try
             {
                 if ("".equals(def.get_Perspective()) || "(Base)".equals(def.get_Perspective()))
                 {
                     IDefinition definition = null;
                     IEnumerator it = this.m_Library.GetPlaceHolderList(DefRequest.ForList(Siteview.Api.PerspectiveDef.get_ClassName())).GetEnumerator();
                     
                     while(it.MoveNext())
                     {
                    	 PlaceHolder holder = (PlaceHolder)it.get_Current();
                         defForEditing = (PerspectiveDef)this.m_Library.GetDefForEditing(DefRequest.ById(holder.get_DefClassName(), holder.get_Id()));
                         if (defForEditing != null)
                         {
                             voi = defForEditing.GetViewForDefinitionById(def.get_InstanceClassName(), def.get_Id());
                             if ((voi != null) && !"(Base)".equals(voi.get_Perspective()))
                             {
                                 definition = this.m_Library.GetDefinition(DefRequest.ById(def.get_Scope(), def.get_ScopeOwner(), def.get_LinkedTo(), def.get_InstanceClassName(), def.get_Id(), defForEditing.get_Name(), false));
                                 if (definition != null)
                                 {
                                     definition = definition.CloneForEdit();
                                     this.m_Library.MarkDefinitionForDeletion(definition);
                                 }
                                 defForEditing.RemoveOverride(voi);
                                 this.m_Library.UpdateDefinition(defForEditing);
                             }
                         }
                     }
                 }
                 else
                 {
                     defForEditing = (PerspectiveDef)m_Library.GetDefForEditing(DefRequest.ByName(Siteview.Api.PerspectiveDef.get_ClassName(), def.get_Perspective()));
                     if (defForEditing != null)
                     {
                         voi = defForEditing.GetViewForDefinitionById(def.get_InstanceClassName(), def.get_Id());
                         if (voi != null)
                         {
                             defForEditing.RemoveOverride(voi);
                             this.m_Library.UpdateDefinition(defForEditing);
                         }
                     }
                 }
                 this.m_Library.MarkDefinitionForDeletion(def);
                 flag = true;
             }
             catch (SiteviewException e)
             {
             }
         }
         return flag;
     }
	 
	 
	private void SetTargetListItemColor(PlaceHolder holder, boolean flag) {
		if(holder == null) return;
		TableItem item = tbBusObList.getSelection()[0];
		if(flag)
		{
			item.setForeground(new Color(null, 0, 0, 0));
			item.setText(2, perspectiveDef.get_Name());
		}else
		{
			item.setForeground(new Color(null, 172, 168, 153));
			item.setText(2, "(一般)");
		}
		item.setData(holder);
	}
	
	private void SynchronizeCoreProperties(IDefinition def)
    {
        IDefinition defOriginal = this.m_Library.GetDefinition(DefRequest.ById(def.get_OriginalScope(), def.get_OriginalScopeOwner(), "", "DashboardPartDef", def.get_Id(), def.get_Perspective(), false));
        if (defOriginal != null)
        {
            boolean flag = !"(Base)".equals(def.get_Perspective());
            if (!def.get_Name().equals(defOriginal.get_Name()) || def.get_Scope() != defOriginal.get_Scope() || !def.get_ScopeOwner().equals(defOriginal.get_ScopeOwner()) || !def.get_LinkedTo().equals(defOriginal.get_LinkedTo()) || !def.get_Folder().equals(defOriginal.get_Folder()))
            {
                PerspectiveDef defForEditing = null;
                IEnumerator it = m_Library.GetPlaceHolderList(DefRequest.ForList(PerspectiveDef.get_ClassName())).GetEnumerator();
                while (it.MoveNext())
                {
                	PlaceHolder holder = (PlaceHolder)it.get_Current();
                    if (!holder.get_Name().equals(def.get_Perspective()))
                    {
                        defForEditing = (PerspectiveDef)this.m_Library.GetDefForEditing(DefRequest.ById(holder.get_DefClassName(), holder.get_Id()));
                        if (defForEditing != null)
                        {
                            IViewOverrideInfo viewForDefinitionById = defForEditing.GetViewForDefinitionById(def.get_InstanceClassName(), def.get_Id());
                            if ((viewForDefinitionById != null) && !("(Base)".equals(viewForDefinitionById.get_Perspective())))
                            {
                                this.UpdateCoreProperties(defOriginal, def, defForEditing.get_Name());
                            }
                        }
                    }
                }
                if (flag)
                {
                    this.UpdateCoreProperties(defOriginal, def, "(Base)");
                }
            }
        }
    }
	
	private void UpdateCoreProperties(IDefinition defOriginal, IDefinition defChanged, String strPerspective)
    {
        IDefinition def = null;
        def = this.m_Library.GetDefForEditing(DefRequest.ById(defOriginal.get_Scope(), defOriginal.get_ScopeOwner(), defOriginal.get_LinkedTo(), defOriginal.get_InstanceClassName(), defOriginal.get_Id(), strPerspective, false));
        if (def != null)
        {
            def.set_Name(defChanged.get_Name());
            def.set_Scope(defChanged.get_Scope());
            def.set_ScopeOwner(defChanged.get_ScopeOwner());
            def.set_LinkedTo(defChanged.get_LinkedTo());
            def.set_Folder(defChanged.get_Folder());
            m_Library.UpdateDefinition(def);
        }
    }
	
	
	protected void ReplaceDefinitionInformationInRunMru(IDefinition def)
    {
        if (def != null)
        {
            IMru mru = m_api.get_SettingsService().get_Mru();
            if (mru != null)
            {
                String strMruId = "".equals(def.get_LinkedTo()) ? "DashboardPartDef.Run" : "DashboardPartDef.Run" + "." + def.get_LinkedTo();
                ICollection items = mru.GetItems(strMruId);
                if (items != null)
                {
                    ArrayList list = new ArrayList(items);
                    String str2 = XmlScope.CategoryToXmlCategory(def.get_OriginalScope()) + "," + def.get_OriginalScopeOwner() + "," + def.get_LinkedTo() + "," +  def.get_Folder()+ "," + def.get_Id() + "," +  def.get_OriginalName() + "," + def.get_Alias() + "," + def.get_Perspective();
                    if(list.get_Count() > 0)
                    {
                    	int index = list.IndexOf(str2);
                        if (index != -1)
                        {
                            String str3 =  XmlScope.CategoryToXmlCategory(def.get_Scope()) + "," + def.get_ScopeOwner() + "," + def.get_LinkedTo() + "," + def.get_Folder() + "," + def.get_Id() + "," + def.get_Name() + "," + def.get_Alias() + "," + def.get_Perspective(); 
                            list.RemoveAt(index);
                            list.Insert(index, str3);
                            mru.ClearItems(strMruId);
                            for (int i = list.get_Count() - 1; i >= 0; i--)
                            {
                                mru.AddItem(strMruId, (String)list.get_Item(i));
                            }
                            mru.Flush(strMruId);
                        }
                    }
                }
            }
        }
    }
	
}
