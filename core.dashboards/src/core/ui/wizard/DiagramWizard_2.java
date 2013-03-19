package core.ui.wizard;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.IList;
import Core.Dashboards.DashboardPartDef;
import Siteview.DefRequest;
import Siteview.PlaceHolder;
import Siteview.StringUtils;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.Scope;
import Siteview.Xml.SecurityRight;
import Siteview.Xml.TimeUnit;
import Siteview.Xml.XmlSecurityRight;
import core.ui.dialog.WebBrowser;

public class DiagramWizard_2 extends WizardPage {
	private static boolean canValidate = false; //第一次进入页面 不验证，点击下一步时验证
	private String type;	//操作类型----1.新建 2.编辑 3.复制
	private String linkTo;  //关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); //保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;

	private DiagramWizard diagramWizard;
	private Text text_Name;
	private Text text_Alias;
	private Combo combo_Scope;
	private Combo combo_Owner;
	private Combo combo_Category;
	private Combo combo_LinkTo;
	private Button button_SupportAsync;
	private Button button_ShowRefresh;
	private Button button_Title;
	private Text text_Title;
	private Combo combo_RefreshInterval;
	private Text text_Description;
	
	private IList m_lstSupportedScopesWithOwners;
	
	public  DiagramWizard_2() {
		super("diagramWizard_2");
		setTitle("基本信息");
		setDescription("用于控件的一般数据。");
	}
	
	public DiagramWizard_2(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, String type){
		this();
		diagramWizard = dw;
		this.dashboardPartDef = dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.type = type;
		this.m_lstSupportedScopesWithOwners = this.m_ScopeUtil
				.GetSupportedScopesWithOwners(XmlSecurityRight.ToRight("VAED"),
						true);
		
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_2(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}

	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		setControl(container);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(50, 10, 54, 18);
		lblNewLabel.setText("\u540D\u79F0\uFF1A");
		
		text_Name = new Text(container, SWT.BORDER);
		text_Name.setBounds(193, 7, 336, 18);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u522B\u540D\uFF1A");
		label.setBounds(50, 37, 54, 18);
		
		text_Alias = new Text(container, SWT.BORDER);
		text_Alias.setBounds(193, 31, 336, 18);
		
		Label lbls = new Label(container, SWT.NONE);
		lbls.setText("\u8303\u56F4(S)\uFF1A");
		lbls.setBounds(50, 63, 62, 18);
		
		combo_Scope = new Combo(container, SWT.READ_ONLY);
		combo_Scope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onScopeSelected();
			}
		});
		combo_Scope.setBounds(118, 55, 114, 20);
		
		Label lblo = new Label(container, SWT.NONE);
		lblo.setText("\u8D1F\u8D23\u4EBA(O)\uFF1A");
		lblo.setBounds(258, 63, 72, 18);
		
		combo_Owner = new Combo(container, SWT.READ_ONLY);
		combo_Owner.setEnabled(false);
		combo_Owner.setTouchEnabled(true);
		combo_Owner.setBounds(340, 55, 157, 20);
		
		Label lblc = new Label(container, SWT.NONE);
		lblc.setText("\u7C7B\u522B(C)\uFF1A");
		lblc.setBounds(50, 99, 62, 18);
		
		combo_Category = new Combo(container, SWT.NONE);
		combo_Category.setTouchEnabled(true);
		combo_Category.setBounds(193, 96, 320, 20);
		
		Label lbla = new Label(container, SWT.NONE);
		lbla.setText("\u5173\u8054(A)\uFF1A");
		lbla.setBounds(50, 131, 62, 18);
		
		combo_LinkTo = new Combo(container, SWT.READ_ONLY);
		combo_LinkTo.setTouchEnabled(true);
		combo_LinkTo.setBounds(193, 129, 320, 20);
		
		button_SupportAsync = new Button(container, SWT.CHECK);
		button_SupportAsync.setBounds(50, 162, 108, 16);
		button_SupportAsync.setText("\u652F\u6301\u5F02\u6B65\u52A0\u8F7D");
		
		button_ShowRefresh = new Button(container, SWT.CHECK);
		button_ShowRefresh.setText("\u663E\u793A\u5237\u65B0\u6309\u94AE");
		button_ShowRefresh.setBounds(50, 184, 108, 16);
		
		button_Title = new Button(container, SWT.CHECK);
		button_Title.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_Title.setEnabled(button_Title.getSelection());
			}
		});
		button_Title.setText("\u6807\u9898");
		button_Title.setBounds(50, 232, 54, 16);
		
		text_Title = new Text(container, SWT.BORDER);
		text_Title.setBounds(193, 231, 320, 18);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("\u8BF4\u660E\uFF1A");
		label_1.setBounds(50, 264, 54, 18);
		
		text_Description = new Text(container, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		text_Description.setBounds(193, 261, 320, 81);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setText("\u5237\u65B0\u65F6\u95F4");
		label_2.setBounds(50, 208, 54, 18);
		
		combo_RefreshInterval = new Combo(container, SWT.READ_ONLY);
		combo_RefreshInterval.setTouchEnabled(true);
		combo_RefreshInterval.setBounds(193, 205, 114, 20);
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setTouchEnabled(true);
		lblNewLabel_1.setBounds(10, 355, 562, 41);
		lblNewLabel_1.setText("\u63A7\u4EF6\u7684\u540D\u79F0");
		lblNewLabel_1.setForeground(new Color(null, 0, 0, 255));
		
		initViewDate();
		initUniversalDate();
		if(!"New".equals(type)) fillProperties();
		
	}
	
	private void onScopeSelected() {
		combo_Owner.removeAll();
		int ob = (Integer) combo_Scope.getData(combo_Scope.getText());
		IList list = m_ScopeUtil.GetScopeOwners(ob, true);
		if (list.get_Count() > 0) {
			for (int i = 0; i < list.get_Count(); i++) {
				String own = (String) list.get_Item(i);
				String item = m_ScopeUtil.ScopeOwnerToString(ob, own);
				combo_Owner.add(item);
				combo_Owner.setData(item, own);
			}
			combo_Owner.select(0);
		}
		if (combo_Owner.getItemCount() > 1) {
			combo_Owner.setEnabled(true);
		}
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	@Override
	public IWizardPage getNextPage() {
		if(!validate()) {
			MessageDialog.openInformation(getShell(), "仪表盘部件——图表", "图表名称输入为空或已存在");
			return this;
		}else{
			saveProperties();
			diagramWizard.addPage(new DiagramWizard_3(diagramWizard, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_3");
		}
	}
	
	
	
	public void getcboQuickOblink(String strBusLink) {
		IEnumerator it = m_lstSupportedScopesWithOwners.GetEnumerator();
		while (it.MoveNext()) {
			ScopeUtil.ScopeAndOwners owners = (ScopeUtil.ScopeAndOwners) it
					.get_Current();
			if (owners.get_Owners().get_Count() > 0
					&& owners.get_Scope() != Scope.Global) {
				for (int i = 0; i < owners.get_Owners().get_Count(); i++) {
					String str = (String) owners.get_Owners().get_Item(i);
					OrganizeListerItemsByCategory(owners.get_Scope(), str,
							strBusLink);
				}
			} else {
				OrganizeListerItemsByCategory(owners.get_Scope(), "",
						strBusLink);
			}

		}
	}
	
	private void OrganizeListerItemsByCategory(int s, String Owner,
			String linkTo) {
		String strPerspective = "(Base)";

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
	
	
	private void initUniversalDate(){
		ICollection supportedScopes =m_ScopeUtil.GetSupportedScopes(SecurityRight.Add);
		IEnumerator it = supportedScopes.GetEnumerator();
		while(it.MoveNext()){
			int scop_int = (Integer) it.get_Current();
			String item=m_ScopeUtil.ScopeToString(scop_int);
			combo_Scope.setData(item, scop_int);
			combo_Scope.add(item);
			if(dashboardPartDef.get_Scope() == scop_int){
				combo_Scope.setText(item);
			}
		}
//		--------------------------------------------------------------------------
		int ob=(Integer) dashboardPartDef.get_Scope();
		IList list=m_ScopeUtil.GetScopeOwners(ob, true);
		if(list.get_Count()>0){
			for(int i=0;i<list.get_Count();i++){
				String own=(String) list.get_Item(i);
				String item=m_ScopeUtil.ScopeOwnerToString(ob, own);
				combo_Owner.add(item);
				combo_Owner.setData(item, own);
			}
			combo_Owner.select(0);
		}
		if(combo_Owner.getItemCount()>1){
			combo_Owner.setEnabled(true);
		}

//		--------------------------------------------------------------------------
		ICollection busLinks = m_Library.GetPlaceHolderList(DefRequest.ForList(BusinessObjectDef.get_ClassName()));
		IEnumerator it1 = busLinks.GetEnumerator();
		combo_LinkTo.add(" （未关联）");
		combo_LinkTo.setData(" （未关联）","");
		if(dashboardPartDef.get_LinkedTo().equals("")) combo_LinkTo.setText(" （未关联）");
		while(it1.MoveNext()){
			PlaceHolder ph = (PlaceHolder)it1.get_Current();
			if (ph.HasFlag("Master")&& !ph.HasFlag("AllowDerivation")){
				combo_LinkTo.add(ph.get_Alias());
				combo_LinkTo.setData(ph.get_Alias(), ph.get_Name());
				if( ph.get_Name().equals(linkTo)){
					combo_LinkTo.setText(ph.get_Alias());
				}
			}
		}
		getcboQuickOblink((String)combo_LinkTo.getData(combo_LinkTo.getText()));
		
//		--------------------------------------------------------------------------
		Iterator<String> ite=Categoryset.iterator();
		while(ite.hasNext()){
			String str=(String) ite.next();
			if(!StringUtils.IsEmpty(str)){
				combo_Category.add(str);
			}
			if(dashboardPartDef.get_Folder().equals(str))
				combo_Category.setText(str);
		}
		
	}
	
	private void initViewDate(){
		combo_RefreshInterval.setItems(new String[]{"无", "1分钟", "2分钟", "3分钟", "5分钟", "10分钟", "15分钟", "20分钟", "30分钟", "60分钟", "90分钟", "120分钟"});
		combo_RefreshInterval.setText(combo_RefreshInterval.getItem(0));
		
		button_Title.setSelection(true);
	}
	
	private boolean validate() {
		if ("".equals(text_Name.getText()) || m_Api.get_LiveDefinitionLibrary().IsDuplicateName(DefRequest.ForDupeCheck(dashboardPartDef.get_InstanceClassName(),dashboardPartDef.get_Id(), (Integer) combo_Scope.getData(combo_Scope.getText()), combo_Owner.getText(), text_Name.getText().trim())))
			return false;
		return true;
    }
	
	
	public void fillProperties(){
		String copyfix = "";
		if("Copy".equals(type)){
			copyfix = " 的副本";
		}
		text_Name.setText(dashboardPartDef.get_Name() + copyfix);
		text_Alias.setText(dashboardPartDef.get_Alias() + copyfix);
		text_Title.setText(dashboardPartDef.get_TitleBarDef().get_Text());
		text_Description.setText(dashboardPartDef.get_Description());
		int minutes = (int)dashboardPartDef.get_RefreshFrequencyTimeSpan().get_TotalMinutes();
		combo_RefreshInterval.setText(minutes>=1 ? minutes+"分钟" : "无");
		
		button_Title.setSelection(dashboardPartDef.get_TitleBarDef().get_Visible());
		if(button_Title.getSelection()) {
			text_Title.setText(dashboardPartDef.get_TitleBarDef().get_Text()); 
		}else{
			text_Title.setEnabled(false);
		}
		button_ShowRefresh.setSelection(dashboardPartDef.get_ShowRefreshButton());
		button_SupportAsync.setSelection(dashboardPartDef.get_SupportAsyncLoading());
		
	}
	
	public void saveProperties(){
		dashboardPartDef.set_Name(text_Name.getText().trim());
		dashboardPartDef.set_Alias(text_Alias.getText().trim());
		dashboardPartDef.set_Scope((Integer)combo_Scope.getData(combo_Scope.getText()));
		dashboardPartDef.set_ScopeOwner((String)combo_Owner.getData(combo_Owner.getText()));
		dashboardPartDef.set_Folder(combo_Category.getText().trim());
		dashboardPartDef.set_LinkedTo((String)combo_LinkTo.getData(combo_LinkTo.getText()));
		dashboardPartDef.set_Description(text_Description.getText().trim());
		dashboardPartDef.set_RefreshFrequencyTimeUnit(TimeUnit.Minutes);
		dashboardPartDef.set_RefreshFrequency(WebBrowser.ComboIndexFromText(combo_RefreshInterval.getText()));
		dashboardPartDef.get_TitleBarDef().set_Visible(button_Title.getSelection());
		dashboardPartDef.get_TitleBarDef().set_Text(text_Title.getText().trim());
		dashboardPartDef.set_ShowRefreshButton(button_ShowRefresh.getSelection());
		dashboardPartDef.set_SupportAsyncLoading(button_SupportAsync.getSelection());
		
	}
}
