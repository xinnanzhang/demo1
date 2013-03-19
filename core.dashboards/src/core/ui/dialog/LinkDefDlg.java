package core.ui.dialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.LinkDef;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.PlaceHolder;
import Siteview.QueryGroupDef;
import Siteview.StringUtils;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.GridDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import core.search.form.SearchCenterForm;



public class LinkDefDlg extends Dialog {
	
	private Text txttext;
	private Menu popMenu;
	private Label imageAddrLabel;
	private Generalmethods general;
	private DefinitionLibrary m_Library;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private LinkDef linkDef;
	private Text txt_Url;
	private Text txt_searchgroup;
	private Button Radio_URL;
	private Button Radio_query;
	private Button but_b;
	private CCombo cb_griddef;
	private ICollection cb_gridList;
	private Label labgriddef ;
	
	public LinkDefDlg(Shell parentShell, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,LinkDef linkDef) {
		super(parentShell);
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.linkDef=linkDef;
		linkDef.set_EditMode(true);
		general=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, null, linkstr);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL );
		// TODO Auto-generated constructor stub
	}

	

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("链接");
		newShell.setLayout(new FillLayout());
		// TODO Auto-generated method stub
		super.configureShell(newShell);
	}
	
	
	/**
	 * 初始化 窗口
	 */
	
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(386,335);
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite subCom=(Composite)super.createDialogArea(parent);
		subCom.setLayout(null);
		
		Label lblNewLabel = new Label(subCom, SWT.NONE);
		lblNewLabel.setBounds(10, 21, 46, 17);
		lblNewLabel.setText("\u6587\u672C:");
		
		txttext = new Text(subCom, SWT.BORDER);
		txttext.setBounds(63, 18, 301, 18);
		
		Label label = new Label(subCom, SWT.NONE);
		label.setText("\u56FE\u50CF(I):");
		label.setBounds(10, 54, 54, 18);
		
		final Label lblNewLabel_1 = new Label(subCom, SWT.NONE);
		lblNewLabel_1.setBounds(70, 54, 23, 18);
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				
				if(e.button == 1){
					popMenu = createPopMenu().createContextMenu(lblNewLabel_1);
					popMenu.setLocation(lblNewLabel_1.toDisplay(e.x,e.y));
					popMenu.setVisible(true);
				}
			}
		});
		lblNewLabel_1.setText("");
		lblNewLabel_1.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_down.png"),0x12,0x12));
		
		imageAddrLabel = new Label(subCom, SWT.NONE);
		imageAddrLabel.setText(" (\u65E0)");
		imageAddrLabel.setBounds(99, 54, 265, 18);
		imageAddrLabel.setData("");
		Radio_URL = new Button(subCom, SWT.RADIO);
		Radio_URL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				cb_griddef.setVisible(false);
				labgriddef.setVisible(false);
				txt_searchgroup.setVisible(false);
				but_b.setVisible(false);
				txt_Url.setVisible(true);
				
			}
		});
		Radio_URL.setSelection(true);
		Radio_URL.setBounds(10, 78, 54, 16);
		Radio_URL.setText("URL");
		
		Radio_query = new Button(subCom, SWT.RADIO);
		Radio_query.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		
		Radio_query.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				cb_griddef.setVisible(true);
				labgriddef.setVisible(true);
				txt_searchgroup.setVisible(true);
				but_b.setVisible(true);
				txt_Url.setVisible(false);
				
			}
		});
		
		Radio_query.setBounds(80, 78, 75, 16);
		Radio_query.setText("\u67E5\u8BE2");
		
		Group group = new Group(subCom, SWT.NONE);
		group.setBounds(10, 100, 355, 147);
		
		txt_Url = new Text(group, SWT.BORDER);
		txt_Url.setBounds(10, 37, 335, 18);
		
		txt_searchgroup = new Text(group, SWT.BORDER | SWT.READ_ONLY);
		txt_searchgroup.setBounds(10, 37, 230, 18);
		txt_searchgroup.setVisible(false);
		
		but_b = new Button(group, SWT.NONE);
		but_b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SearchCenterForm searchCenterFrom = new SearchCenterForm("搜索  中心", true);
				searchCenterFrom.open();
				PlaceHolder ph = searchCenterFrom.getBackPlaceHolder();
				IDefinition m_SelectedItem = (IDefinition) m_Library.GetDefinition(DefRequest.ById(ph.get_Scope(), ph.get_ScopeOwner(), ph.get_LinkedTo(),QueryGroupDef.get_ClassName(), ph.get_Id()));
				txt_searchgroup.setText(ph.get_Alias());
				txt_searchgroup.setData(m_SelectedItem);
				SelectBusLink(m_SelectedItem.get_LinkedTo());
			}
		});
		but_b.setBounds(258, 34, 87, 22);
		but_b.setText("\u6D4F\u89C8(B)...");
		but_b.setVisible(false);
		
		
		labgriddef = new Label(group, SWT.NONE);
		labgriddef.setBounds(10, 70, 61, 18);
		labgriddef.setText("\u7F51\u683C\u5B9A\u4E49:");
		labgriddef.setVisible(false);
		
		
		cb_griddef = new CCombo(group, SWT.BORDER);
		cb_griddef.setEditable(false);
		cb_griddef.setBounds(10, 90, 335, 21);
		cb_griddef.setVisible(false);
		
		load();
		return subCom;
	}
	
	
	/**
	 * 第一次加载数据
	 */
	public void load(){
		cb_gridList =m_api.get_LiveDefinitionLibrary().GetPlaceHolderList(DefRequest.ForList(GridDef.get_ClassName()));
		if(!StringUtils.IsEmpty(linkDef.get_Alias())){
			txttext.setText(linkDef.get_Alias());
			imageAddrLabel.setText(ImageChooseDlg.getInitImageStr(linkDef.get_ImageName()));
			imageAddrLabel.setData(linkDef.get_ImageName());
			txt_Url.setText(linkDef.get_FilePath());
			
			if(linkDef.get_IsQuery()){
				Radio_URL.setSelection(false);
				Radio_query.setSelection(true);
				IDefinition m_SelectedItem = (IDefinition) m_Library.GetDefinition(DefRequest.ById(linkDef.get_QueryGroupScope(), linkDef.get_QueryGroupScopeOwner(), linkDef.get_QueryGroupLinkedTo(),QueryGroupDef.get_ClassName(), linkDef.get_QueryGroupId()));
				txt_searchgroup.setText(m_SelectedItem.get_Alias());
				txt_searchgroup.setData(m_SelectedItem);
				SelectBusLink(m_SelectedItem.get_LinkedTo());
				cb_griddef.setVisible(true);
				labgriddef.setVisible(true);
				txt_searchgroup.setVisible(true);
				but_b.setVisible(true);
				txt_Url.setVisible(false);
				
			}else{
				Radio_URL.setSelection(true);
				Radio_query.setSelection(false);
				cb_griddef.setVisible(false);
				labgriddef.setVisible(false);
				txt_searchgroup.setVisible(false);
				but_b.setVisible(false);
				txt_Url.setVisible(true);
			}
		}else{
			SelectBusLink("");
		}
	}
	
	
	/**
	 * 选 择业务对象  查出网格 的下拉式数据
	 */
	
	public void SelectBusLink(String buslink){
		cb_griddef.removeAll();
		
	//	String buslink=cb_BusObLink.getData(cb_BusObLink.getText()).toString();
		IEnumerator it = cb_gridList.GetEnumerator();
		while(it.MoveNext()){
				PlaceHolder holder = (PlaceHolder)it.get_Current();
				if(StringUtils.IsEmpty(buslink) || holder.get_LinkedTo().equals(buslink)){
					cb_griddef.add(holder.get_Alias());
					cb_griddef.setData(holder.get_Alias(),holder);
					if(linkDef!=null&&linkDef.get_GridDefName().equals(holder.get_Name())){
						cb_griddef.setText(holder.get_Alias());
					}
				}
		}
		if(cb_griddef.getItemCount()>0&&cb_griddef.getSelectionIndex()==-1){
			cb_griddef.select(0);
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

	/**
	 *  按钮设置
	 */
	@Override
	protected void initializeBounds() {
		
		Button button = super.getButton(IDialogConstants.OK_ID);
		button.setText("确定");
		super.getButton(IDialogConstants.CANCEL_ID).setText("取消");
		
		super.initializeBounds();
	}
	/**
	 * 点击保存事件
	 */
	
	@Override
	protected void okPressed() {
		
		if(StringUtils.IsEmpty(txttext.getText())){
			
			MessageDialog.openInformation(getParentShell(), "提示",
					"请输入文字. ");
			txttext.forceFocus();
			return;
		}
	
		if(Radio_URL.getSelection()&&StringUtils.IsEmpty(txt_Url.getText())){
			MessageDialog.openInformation(getParentShell(), "提示",
					"请输入URL或文件路径. ");
			txt_Url.forceFocus();
			return;
		}
	
		if(Radio_query.getSelection()&&StringUtils.IsEmpty(txt_searchgroup.getText())){
			MessageDialog.openInformation(getParentShell(), "提示",
					"请选择搜索群组. ");
			txt_searchgroup.forceFocus();
			return;
		}
		if(Radio_query.getSelection()&&StringUtils.IsEmpty(cb_griddef.getText())){
			MessageDialog.openInformation(getParentShell(), "提示",
					"请选择网格定义. ");
			cb_griddef.forceFocus();
			return;
		}
		
		linkDef.set_Name(txttext.getText());
		linkDef.set_Alias(txttext.getText());
		linkDef.set_ImageName(imageAddrLabel.getData().toString());
		if(Radio_URL.getSelection()){
			linkDef.set_IsQuery(false);
			linkDef.set_IsFile(txt_Url.getText().trim().toUpperCase().startsWith("HTTP://"));
			linkDef.set_FilePath(txt_Url.getText().trim());
		}else{
			linkDef.set_IsQuery(true);
			IDefinition definition=(IDefinition) txt_searchgroup.getData();
			linkDef.set_QueryGroupId(definition.get_Id());
			linkDef.set_QueryGroupLinkedTo(definition.get_LinkedTo());
			linkDef.set_QueryGroupName(definition.get_Name());
			linkDef.set_QueryGroupScope(definition.get_Scope());
			linkDef.set_QueryGroupScopeOwner(definition.get_ScopeOwner());
			PlaceHolder holder=(PlaceHolder) cb_griddef.getData(cb_griddef.getText());
			linkDef.set_GridDefID(holder.get_Id());
			linkDef.set_GridDefName(holder.get_Name());
		}
		
		super.okPressed();
	}



	public LinkDef getLinkDef() {
		return linkDef;
	}
	
	
	
	
}
