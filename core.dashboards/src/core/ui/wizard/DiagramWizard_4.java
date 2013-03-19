package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;

import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Siteview.DefRequest;
import Siteview.PlaceHolder;
import Siteview.QueryGroupDef;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import core.search.form.SearchCenterForm;

public class DiagramWizard_4 extends WizardPage {

	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;

	private DiagramWizard diagramWizard;
	private JFreeChart m_ultraChart;
	private Text text;
	
	private CCombo combo;
	
	private boolean toNextPage = false;
	
	private Menu menu;
	private MenuItem menuUp;  //上一条
	private MenuItem menuDown;  //下一条
	private int count=0;  
	private ICollection placeHolderList;  //业务的 菜单数据
	
	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_4() {
		super("diagramWizard_4");
		setTitle("业务对象和记录选择");
		setDescription("首先选择业务对象。然后选择返回所有记录，或选择使用现有搜索群组从选择的业务对象返回一组记录。");
	}

	public DiagramWizard_4(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
		this();
		diagramWizard = dw;
		this.dashboardPartDef = dashboardPartDef;
//		this.chartPartDef = (ChartPartDef)dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.Categoryset = Categoryset;
		this.type = type;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		Label label = new Label(container, SWT.NONE);
		label.setTouchEnabled(true);
		label.setText("\u63A7\u4EF6\u7684\u540D\u79F0");
		label.setForeground(new Color(null, 0, 0, 255));
		label.setBounds(10, 345, 562, 41);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(25, 21, 70, 18);
		lblNewLabel.setText("\u4E1A\u52A1\u5BF9\u8C61:");
		
		combo = new CCombo(container, SWT.BORDER);
		combo.setVisibleItemCount(0);
		combo.setEditable(false);
		combo.setBounds(25, 42, 182, 21);
		combo.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				if(e.button == 1){
					createMenu();
					menu.setLocation(combo.toDisplay(0,0));
					menu.setVisible(true);
				}
			}
		});
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("\u641C\u7D22\u7FA4\u7EC4:");
		label_1.setBounds(25, 86, 70, 18);
		
		text = new Text(container, SWT.BORDER);
		text.setBounds(25, 110, 365, 35);
		text.setEditable(false);
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if("".equals(combo.getText().trim())) return;
				SearchCenterForm searchCenterFrom = new SearchCenterForm("搜索  中心", true, ((BusinessObjectDef)combo.getData()).get_Name());
				searchCenterFrom.setShowAllBusName(true);
				searchCenterFrom.open();
				PlaceHolder ph = searchCenterFrom.getBackPlaceHolder();
				if(ph == null) return;
				QueryGroupDef deQueryGroupDef = (QueryGroupDef)m_Library.GetDefinition(DefRequest.ByHolder(ph));
				text.setText(deQueryGroupDef.get_Alias());
				text.setData(deQueryGroupDef);
				
			}
		});
		btnNewButton.setBounds(396, 108, 72, 22);
		btnNewButton.setText("\u6D4F\u89C8");

	}
	
	private void fillProperties(){
		
		placeHolderList = m_Library.GetPlaceHolderList(DefRequest.ByCategory(BusinessObjectDef.get_ClassName()));
		IEnumerator it1 = placeHolderList.GetEnumerator();
		while(it1.MoveNext()){
				PlaceHolder holder = (PlaceHolder)it1.get_Current();
				if (holder.HasFlag("AllowDerivation")){
					count++;
				}else if(holder.get_Name().indexOf(".") == -1){
					count++;
	            }
		}	
		
		if (!"New".equals(type)) {
			QueryGroupDef definition = (QueryGroupDef) m_Library.GetDefinition(DefRequest.ById(chartPartDef.get_QueryGroupScope(), chartPartDef.get_QueryGroupScopeOwner(), chartPartDef.get_QueryGroupLinkedTo(), QueryGroupDef.get_ClassName(), chartPartDef.get_QueryGroupId()));
			if (definition != null) {
				text.setText(definition.get_Alias());
				text.setData(definition);
			}

			BusinessObjectDef busObDef = m_Api.get_BusObDefinitions().GetBusinessObjectDef(chartPartDef.get_BusObName());
			if(busObDef != null){
				combo.setText(busObDef.get_Alias());
				combo.setData(busObDef);
			}
		}
	}

	private void createMenu(){
//		menu = new Menu(combo);
//		
//		ICollection placeHolderList = m_Library.GetPlaceHolderList(DefRequest.ByCategory(BusinessObjectDef.get_ClassName()));
//		IEnumerator it = placeHolderList.GetEnumerator();
//		
//		while(it.MoveNext()){
//			PlaceHolder holder = (PlaceHolder)it.get_Current();
//			if (holder.HasFlag("AllowDerivation")){
//				MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
//				menuItem.setText(holder.get_Alias() + " " + "Group");
//				Menu itemMenu = new Menu(menuItem);
//				menuItem.setMenu(itemMenu);
//				
//				MenuItem menuItem1 = new MenuItem(itemMenu, SWT.NONE);
//				menuItem1.setText(holder.get_Alias());
//				menuItem1.setData(holder.get_Name());
//				addMenuListener(menuItem1);
//				
//				ICollection familyDefs = m_Library.GetGroupPlaceHolderList(holder.get_Name(), "(Base)", false);
//				IEnumerator it2 = familyDefs.GetEnumerator();
//				while(it2.MoveNext()){
//					PlaceHolder holder2 = (PlaceHolder)it2.get_Current();
//					MenuItem menuItem2 = new MenuItem(itemMenu, SWT.NONE);
//					menuItem2.setText(holder2.get_Alias());
//					menuItem2.setData(holder2.get_Name());
//					addMenuListener(menuItem2);
//				}
//			}else if(holder.get_Name().indexOf(".") == -1){
//                MenuItem item4 = new MenuItem(menu, SWT.NONE);
//                item4.setText(holder.get_Alias());
//                item4.setData(holder.get_Name());
//                addMenuListener(item4);
//            }
//		}
		
		menu = new Menu(combo);
		
		menuUp(0);
		FillBusLink(placeHolderList,0,1);
		menuDown(1);
	}
	
	public void menuUp(int countup){
		
		menuUp = new MenuItem(menu, SWT.NONE);
		menuUp.setText("上一页");
		menuUp.setData(countup);
		if(countup<=0){
			menuUp.setEnabled(false);
		}else{
			menuUp.setEnabled(true);
		}
		
		
		
		menuUp.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int countup=(Integer) menuUp.getData();
				int countdown=(Integer) menuDown.getData();
				menuUp.setData(countup-1);
				menuDown.setData(countdown-1);
				menu.dispose();
				menu = new Menu(combo);
				menu.setLocation(combo.toDisplay(0,0));
				menu.setVisible(true);
			
				menuUp(countup-1);
				FillBusLink(placeHolderList,countup-1,countdown-1);
				menuDown(countdown-1);
				
			}
			
			
		});
	}
	
	
	public void menuDown(int countdown){
		
		menuDown = new MenuItem(menu, SWT.NONE);
		menuDown.setText("下一页");
		menuDown.setData(countdown);
		if(countdown>count/20){
			menuDown.setEnabled(false);
		}else{
			menuDown.setEnabled(true);
		}
		
		
		menuDown.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				int countup=(Integer) menuUp.getData();
				int countdown=(Integer) menuDown.getData();
				menu.dispose();
				menu = new Menu(combo);
				menu.setLocation(combo.toDisplay(0,0));
				menu.setVisible(true);
			
				menuUp(countup+1);
				FillBusLink(placeHolderList,countup+1,countdown+1);
				menuDown(countdown+1);
			}
			});
		
	}

	
	//为menu添加监听
	private void addMenuListener(MenuItem item){
		item.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				MenuItem source = (MenuItem)e.getSource();
				combo.setText(source.getText());
				BusinessObjectDef busObDef = m_Api.get_BusObDefinitions().GetBusinessObjectDef((String)source.getData());
				combo.setData(busObDef);
			}
		});
	}
	
	
	public void FillBusLink(ICollection placeHolderList,int countup,int countdown){
		IEnumerator it = placeHolderList.GetEnumerator();
		
//		MenuItem menuUp = new MenuItem(menu, SWT.NONE);
//		menuUp.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_up.png"),0x12,0x12));
		int next=0;
		while(it.MoveNext()){
				final PlaceHolder holder = (PlaceHolder)it.get_Current();
				if (holder.HasFlag("AllowDerivation")){
					
					if(next>=countup*20&&next<=countdown*20){
						MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
						menuItem.setText(holder.get_Alias() + " " + "Group");
						Menu itemMenu = new Menu(menuItem);
						menuItem.setMenu(itemMenu);
						
						MenuItem menuItem5 = new MenuItem(itemMenu, SWT.NONE);
						menuItem5.setText(holder.get_Alias());
						menuItem5.setData(holder);
						menuItem5.addSelectionListener(new SelectionAdapter(){
							@Override
							public void widgetSelected(SelectionEvent e) {
								MenuItem source = (MenuItem)e.getSource();
								PlaceHolder pl=	(PlaceHolder) source.getData();
								BusinessObjectDef bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), pl.get_Id(), "(Role)"));
								combo.setText(source.getText());
								combo.setData(bus);
							}
						});
						
						ICollection familyDefs = m_Library.GetGroupPlaceHolderList(holder.get_Name(), "(Base)", false);
						IEnumerator it2 = familyDefs.GetEnumerator();
						while(it2.MoveNext()){
							final PlaceHolder holder2 = (PlaceHolder)it2.get_Current();
							MenuItem menuItem2 = new MenuItem(itemMenu, SWT.NONE);
							menuItem2.setText(holder2.get_Alias());
							menuItem2.setData(holder2);
							menuItem2.addSelectionListener(new SelectionAdapter(){
								@Override
								public void widgetSelected(SelectionEvent e) {
									MenuItem source = (MenuItem)e.getSource();
									PlaceHolder pl=	(PlaceHolder) source.getData();
									BusinessObjectDef bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), pl.get_Id(), "(Role)"));
									combo.setText(source.getText());
									combo.setData(bus);
								}
							});
						}
					}
					next++;
					
				}else if(holder.get_Name().indexOf(".") == -1){
					if(next>=countup*20&&next<=countdown*20){ 
						 MenuItem item4 = new MenuItem(menu, SWT.NONE);
			                item4.setText(holder.get_Alias());
			                
			                item4.setData(holder);
			                item4.addSelectionListener(new SelectionAdapter(){
								public void widgetSelected(SelectionEvent e) {
									MenuItem source = (MenuItem)e.getSource();
									PlaceHolder pl=	(PlaceHolder) source.getData();
									BusinessObjectDef bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), pl.get_Id(), "(Role)"));
									combo.setText(source.getText());
									combo.setData(bus);
								}
							});	
					}
					next++;
	            }
			
		}	
	}
	
	
	@Override
	public void setVisible(boolean visible) {
		if(visible){
			GetChartPartDefinition();
			this.chartPartDef = (ChartPartDef)dashboardPartDef;
			fillProperties();
		}
		super.setVisible(visible);
	}
	
	public boolean canFlipToNextPage() {
		return true;
	}
	
	public IWizardPage getNextPage() 
	{
		if(!validate())
		{
			MessageDialog.openInformation(getShell(), "输入有误", "业务对象和搜索群组不能为空！");
			return this;
		}
		saveProperties();
		diagramWizard.addPage(new DiagramWizard_5(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
		return diagramWizard.getPage("diagramWizard_5");
	}
	
	
	private boolean validate()
	{
		if(!"".equals(combo.getText().trim()) && !"".equals(text.getText().trim()) 
				&& combo.getData() != null && text.getData() != null) 
		{
			return true;	
		}
		return false;
	}
	
	private void saveProperties()
	{
		QueryGroupDef definition = (QueryGroupDef)text.getData();
		if(definition != null)
		{
			chartPartDef.set_QueryGroupId(definition.get_Id());
			chartPartDef.set_QueryGroupName(definition.get_Name());
			chartPartDef.set_QueryGroupLinkedTo(definition.get_LinkedTo());
			chartPartDef.set_QueryGroupScope(definition.get_Scope());
			chartPartDef.set_QueryGroupScopeOwner(definition.get_ScopeOwner());
		}
		Object busObj = combo.getData();
		if(busObj != null)
		{
			chartPartDef.set_BusObName(((BusinessObjectDef)busObj).get_Name());
			chartPartDef.set_BusObID(((BusinessObjectDef)busObj).get_Id());
			chartPartDef.ClearDateRangeDefs();
		}
		
	}
	
	private void GetChartPartDefinition(){
		if("New".equals(type) || !DiagramWizard_3.EidtChartType.equals(dashboardPartDef.get_CategoryAsString())){
			ChartPartDef dashboardPartDef_New = null;
			dashboardPartDef_New = (ChartPartDef) m_Library.GetNewDefForEditing(DefRequest.ByCategory("DashboardPartDef", DiagramWizard_3.getChartType(dashboardPartDef.get_CategoryAsString()).name()));
			
			dashboardPartDef_New.set_Id(dashboardPartDef.get_Id());
			dashboardPartDef_New.set_Name(dashboardPartDef.get_Name());
			dashboardPartDef_New.set_Alias(dashboardPartDef.get_Alias());
			dashboardPartDef_New.set_Scope(dashboardPartDef.get_Scope());
			dashboardPartDef_New.set_ScopeOwner(dashboardPartDef.get_ScopeOwner());
			dashboardPartDef_New.set_Folder(dashboardPartDef.get_Folder());
			dashboardPartDef_New.set_LinkedTo(dashboardPartDef.get_LinkedTo());
			dashboardPartDef_New.set_Description(dashboardPartDef.get_Description());
			dashboardPartDef_New.set_RefreshFrequencyTimeUnit(dashboardPartDef.get_RefreshFrequencyTimeUnit());
			dashboardPartDef_New.set_RefreshFrequency(dashboardPartDef.get_RefreshFrequency());
			dashboardPartDef_New.get_TitleBarDef().set_Text(dashboardPartDef.get_TitleBarDef().get_Text());
			dashboardPartDef_New.get_TitleBarDef().set_Visible(dashboardPartDef.get_TitleBarDef().get_Visible());
			dashboardPartDef_New.set_ShowRefreshButton(dashboardPartDef.get_ShowRefreshButton());
			dashboardPartDef_New.set_SupportAsyncLoading(dashboardPartDef.get_SupportAsyncLoading());
			dashboardPartDef_New.set_TimeBased(dashboardPartDef.get_TimeBased());
			dashboardPartDef_New.set_ViewControlVisible(dashboardPartDef.get_ViewControlVisible());
			dashboardPartDef_New.set_ViewControlType(dashboardPartDef.get_ViewControlType());
			
			if(!"New".equals(type)){
				ChartPartDef chartPartDef = (ChartPartDef)dashboardPartDef;
				
				dashboardPartDef_New.set_QueryGroupId(chartPartDef.get_QueryGroupId());
				dashboardPartDef_New.set_QueryGroupName(chartPartDef.get_QueryGroupName());
				dashboardPartDef_New.set_QueryGroupLinkedTo(chartPartDef.get_QueryGroupLinkedTo());
				dashboardPartDef_New.set_QueryGroupScope(chartPartDef.get_QueryGroupScope());
				dashboardPartDef_New.set_QueryGroupScopeOwner(chartPartDef.get_QueryGroupScopeOwner());
				dashboardPartDef_New.set_BusObName(chartPartDef.get_BusObName());
				
				dashboardPartDef_New.get_DefaultDateRangeDef().set_ApplyDateRange(chartPartDef.get_DefaultDateRangeDef().get_ApplyDateRange());
				dashboardPartDef_New.get_DefaultDateRangeDef().set_IsLengthOfTime(chartPartDef.get_DefaultDateRangeDef().get_IsLengthOfTime());
				dashboardPartDef_New.get_DefaultDateRangeDef().set_DateTimeField(chartPartDef.get_DefaultDateRangeDef().get_DateTimeField());
				dashboardPartDef_New.get_DefaultDateRangeDef().set_DateRange(chartPartDef.get_DefaultDateRangeDef().get_DateRange());

			}
			
			dashboardPartDef = dashboardPartDef_New;
		}
	}

	
	public void setDashboardPartDef(DashboardPartDef dashboardPartDef) {
		this.dashboardPartDef = dashboardPartDef;
	}
}
