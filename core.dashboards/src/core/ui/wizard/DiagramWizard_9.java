package core.ui.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.CCombo;

import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Data.DataRow;
import system.Data.DataRowCollection;
import system.Data.DataTable;


import java.util.HashSet;

import Siteview.DefRequest;
import Siteview.PlaceHolder;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.DashboardPartCategory;
import Siteview.Xml.GridAction;

import Core.Dashboards.ChartPartDef;
import Core.Dashboards.ChartType;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DrilldownDef;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TableColumn;


public class DiagramWizard_9 extends WizardPage {

	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private DiagramWizard diagramWizard;
	
	private CCombo combo_ChartType;
	private CCombo combo_LinkTo;
	private Button radio_Chart;
	private Button radio_Grid;
	private Button radio_IsToBusOb;
	private Button radio_IsToParent;
	private Label label_Chart;
	private Table table;
	private Group group;
	private TableColumn tableColumn;
	private TableColumn tableColumn_1;
	private TableColumn tableColumn_2;
	private TableColumn tableColumn_3;
	
	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_9() {
		super("diagramWizard_9");
		setTitle("图表钻取");
		setDescription("提供定义钻取图表或网格的功能部件。");
	}
	
	public DiagramWizard_9(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_9(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
		this();
		diagramWizard = dw;
		this.dashboardPartDef = dashboardPartDef;
		this.chartPartDef = (ChartPartDef)dashboardPartDef;
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
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 251, 16);
		lblNewLabel.setText("\u8BF7\u9009\u62E9\u53EF\u7528\u7684\u7F51\u7EDC\u5B9A\u4E49\u548C\u5217\u4EE5\u5BF9\u94BB\u53D6\u8BB0\u5F55\u6392\u5E8F");
		
		combo_LinkTo = new CCombo(container, SWT.BORDER);
		combo_LinkTo.setBounds(10, 56, 215, 20);
		combo_LinkTo.setEditable(false);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u7F51\u683C\u5B9A\u4E49");
		label.setBounds(10, 34, 76, 16);
		
		Label lbln = new Label(container, SWT.NONE);
		lbln.setText("\u5355\u51FB\u56FE\u8868\u4E2D\u7684\u8BB0\u5F55\u65F6\uFF0C\u662F\u8981\u53D6\u7528\u94BB\u53D6\u7684\u56FE\u8868\n\u8FD8\u662F\u8F6C\u5230\u5BF9\u8C61\u7684\u7A97\u683C\u89C6\u56FE");
		lbln.setBounds(10, 87, 237, 32);
		
		radio_Chart = new Button(container, SWT.RADIO);
		radio_Chart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				combo_ChartType.setEnabled(true);
				label_Chart.setImage(DiagramWizard_3.initImage(DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(), 
						(DashboardPartCategory)combo_ChartType.getData(combo_ChartType.getText()), null), 400, 300));
				table.setVisible(false);
				label_Chart.setVisible(true);
				
			}
		});
		radio_Chart.setBounds(10, 125, 93, 16);
		radio_Chart.setText("\u56FE\u8868");
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("\u4F55\u79CD\u7C7B\u578B\uFF1F");
		label_1.setBounds(10, 147, 68, 16);
		
		combo_ChartType = new CCombo(container, SWT.BORDER);
		combo_ChartType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				label_Chart.setImage(DiagramWizard_3.initImage(DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(), 
						(DashboardPartCategory)combo_ChartType.getData(combo_ChartType.getText()), null), 400, 300));	//fill image
				label_Chart.setVisible(true);
			}
		});
		combo_ChartType.setBounds(84, 143, 141, 20);
		combo_ChartType.setEditable(false);
		
		radio_Grid = new Button(container, SWT.RADIO);
		radio_Grid.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				combo_ChartType.setEnabled(false);
				label_Chart.setVisible(false);
				table.setVisible(true);
			}
		});
		radio_Grid.setText("\u7F51\u683C");
		radio_Grid.setBounds(10, 169, 93, 16);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setText("\u94BB\u53D6\u9009\u9879:");
		label_2.setBounds(10, 201, 76, 16);
		
		group = new Group(container, SWT.NONE);
		group.setBounds(10, 214, 226, 72);
		
		radio_IsToBusOb = new Button(group, SWT.RADIO);
		radio_IsToBusOb.setText("\u53CC\u51FB\u94BB\u53D6\u4E1A\u52A1\u5BF9\u8C61(B)");
		radio_IsToBusOb.setBounds(0, 15, 141, 16);
		
		radio_IsToParent = new Button(group, SWT.RADIO);
		radio_IsToParent.setText("\u53CC\u51FB\u94BB\u53D6\u4E1A\u52A1\u5BF9\u8C61\u7684\u7236\u5BF9\u8C61(P)");
		radio_IsToParent.setBounds(0, 45, 215, 16);
		
		label_Chart = new Label(container, SWT.NONE);
		label_Chart.setBounds(253, 34, 400, 300);
		
		table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.VIRTUAL);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(283, 50, 278, 216);
		
		initModelView();
		fillProperties();
	}
	
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	
	@Override
	public IWizardPage getNextPage() {
		saveProperties();
		if(!radio_Chart.getSelection()){
			diagramWizard.addPage(new DiagramWizard_11(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_11");
		}else{
			diagramWizard.addPage(new DiagramWizard_10(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_10");
		}
	}
	
	private void initModelView(){
		combo_ChartType.setItems(new String[]{"条形图", "柱形图", "折线图", "饼图", "管道图"});
		combo_ChartType.setData("条形图", DashboardPartCategory.ChartBar);
		combo_ChartType.setData("柱形图", DashboardPartCategory.ChartColumn);
		combo_ChartType.setData("饼图", DashboardPartCategory.ChartPie);
		combo_ChartType.setData("折线图", DashboardPartCategory.ChartLine);
		combo_ChartType.setData("管道图", DashboardPartCategory.ChartPipeline);
		
		
		tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(70);
		tableColumn.setText("第0列");
		
		tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(70);
		tableColumn_1.setText("第1列");
		
		tableColumn_2 = new TableColumn(table, SWT.NONE);
		tableColumn_2.setWidth(70);
		tableColumn_2.setText("第2列");
		
		tableColumn_3 = new TableColumn(table, SWT.NONE);
		tableColumn_3.setWidth(70);
		tableColumn_3.setText("第3列");
		
		DataTable dataTable = DiagramWizard_3.getTable();
		
		DataRowCollection rows = dataTable.get_Rows();
		for(int i = 0 ; i < rows.get_Count() ; i++){
			DataRow row = rows.get_Item(i);
			TableItem tableItem = new TableItem(table, SWT.NONE);
			String[] s = new String[4];
			for(int j = 0 ; j < row.get_ItemArray().length; j++){
				s[j] = row.get_ItemArray()[j].toString();
			}
			tableItem.setText(s);
		}
	}
	
	private void fillProperties(){
//		combo_LinkTo.setText(m_Api.get_BusObDefinitions().GetBusinessObjectDef(chartPartDef.get_BusObName()).get_Alias());
		ICollection holders = this.m_Library.GetPlaceHolderList(DefRequest.ForList("GridDef"));
		IEnumerator it = holders.GetEnumerator();
		while(it.MoveNext()){
			//!"".equals(chartPartDef.get_BusObName()) || 
			if(((PlaceHolder)it.get_Current()).get_LinkedTo().equals(chartPartDef.get_BusObName())){
				combo_LinkTo.add(((PlaceHolder)it.get_Current()).get_Alias());
				combo_LinkTo.setData(((PlaceHolder)it.get_Current()).get_Alias(), (PlaceHolder)it.get_Current());
				if(chartPartDef.get_BusObName().equals(((PlaceHolder)it.get_Current()).get_Name()))
					combo_LinkTo.setText(((PlaceHolder)it.get_Current()).get_Alias());
			}
        }
		if("New".equals(type) || !chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
			radio_Grid.setSelection(true);
			radio_Grid.setSelection(true);
			combo_ChartType.setText("饼图");
			combo_ChartType.setEnabled(false);
			table.setVisible(true);
			label_Chart.setVisible(false);
			radio_IsToParent.setSelection(true);
			
		}else{
			if (!"".equals(chartPartDef.get_DrilldownGridDefID())){
	//			GridDef SelectedGridDef = (GridDef)m_Library.GetDefinition(DefRequest.ById("GridDef", chartPartDef.get_DrilldownGridDefID()));
				if(chartPartDef.get_DrilldownArrayList().get_Count() > 0){
					radio_Chart.setSelection(true);
					table.setVisible(false);
					combo_ChartType.setText(getChartName(((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_DrilldownChartType()));
					if(((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_TitleBottomVisible()){
						
					}
					combo_ChartType.setEnabled(true);
					label_Chart.setImage(DiagramWizard_3.initImage(DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(), 
							getChartCategory(((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_DrilldownChartType()), null), 400, 300));	//fill image
					label_Chart.setVisible(true);
				}else{
					radio_Grid.setSelection(true);
					combo_ChartType.setText("饼图");
					combo_ChartType.setEnabled(false);
					table.setVisible(true);
					label_Chart.setVisible(false);
				}
				if(chartPartDef.get_DrillDownGridAction() == GridAction.GoToParent) radio_IsToParent.setSelection(true);
				else radio_IsToBusOb.setSelection(true);
			}
		}
	}
	
	private String getChartName(ChartType chartType){
		switch(chartType){
		
			case PieChart:
				return "饼图";
				
			case ColumnChart:
				return "柱形图";
				
			case BarChart:
				return "条形图";
				
			case LineChart:
				return "折线图";
				
			case StackBarChart:
				
				return "管道图";
		}
		return null;
	}
	
	private DashboardPartCategory getChartCategory(ChartType chartType){
		switch(chartType){
		
		case PieChart:
			return DashboardPartCategory.ChartPie;
			
		case ColumnChart:
			return DashboardPartCategory.ChartColumn;
			
		case BarChart:
			return DashboardPartCategory.ChartBar;
			
		case LineChart:
			return DashboardPartCategory.ChartLine;
			
		case StackBarChart:
			return DashboardPartCategory.ChartPipeline;
		
	}
	return null;
}
	
	private ChartType getChartType(DashboardPartCategory category){
		switch(category){
		
			case ChartPie:
				return ChartType.PieChart;
				
			case ChartColumn:
				return ChartType.ColumnChart;
				
			case ChartBar:
				return ChartType.BarChart;
				
			case ChartLine:
				return ChartType.LineChart;
				
			case ChartPipeline:
				return ChartType.StackBarChart;
			
		}
		return null;
	}
	
	
	private void saveProperties(){
		chartPartDef.set_DrilldownGridDefID(((PlaceHolder)combo_LinkTo.getData(combo_LinkTo.getText())).get_Id());
		chartPartDef.set_DrilldownGridDefName(((PlaceHolder)combo_LinkTo.getData(combo_LinkTo.getText())).get_Name());
		chartPartDef.set_DrilldownGridOrderByFieldName("");
		chartPartDef.set_DrillDownGridAction(radio_IsToBusOb.getSelection() ? GridAction.GoToRecord : GridAction.GoToParent);
		if(radio_Chart.getSelection()){
			if (chartPartDef.get_DrilldownArrayList().get_Count() > 0){
				((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).set_GoToChart(true);
				((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).set_DrilldownChartType(getChartType((DashboardPartCategory)combo_ChartType.getData(combo_ChartType.getText())));
				((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).set_TitleBottomVisible(false);
				((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).set_TitleBottomText("");
            }
            else
            {
            	DrilldownDef drilldown = new DrilldownDef();
            	drilldown.set_GoToChart(true);
            	drilldown.set_DrilldownChartType(getChartType((DashboardPartCategory)combo_ChartType.getData(combo_ChartType.getText())));
            	drilldown.set_TitleBottomVisible(false);
            	drilldown.set_TitleBottomText("");
                chartPartDef.get_DrilldownArrayList().Clear();
                chartPartDef.get_DrilldownArrayList().Add(drilldown);
            }
		}else{
			chartPartDef.get_DrilldownArrayList().Clear();
		}
		
	}
}
