package core.ui.wizard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;

import system.Type;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Drawing.KnownColor;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.GaugeRegionDef;
import Siteview.QueryFunctions;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;
import core.apploader.search.dialog.SimpleExpressSelector;
import core.apploader.search.pojo.FieldDefHolder;
import core.ui.dialog.OutlookCalendar;

public class DiagramWizard_12_3 extends WizardPage {
	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private DiagramWizard diagramWizard;
	private JFreeChart labelChart;
	private Text text_Value1;
	private CCombo combo_Color1;
	
	private Label label_Chart;
	private boolean toNextPage = false;
	private Button radio_Value;
	private Button radio_Search;
	
	private Composite container;
	private final int STEP = 30;
	private ArrayList<ArrayList> regionRows = new ArrayList<ArrayList>();
	private int controlRow;
	private boolean ValueOrField;
	private List<FieldDefHolder> fieldDefList;
	
	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_12_3() {
		super("diagramWizard_12_3");
		setTitle("量表外观");
		setDescription("设置量表控件的功能部件，包括阈值范围，颜色，标题和标签说明。");
	}

	public DiagramWizard_12_3(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_12_3(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		labelChart = DiagramWizard_11.getLabelChart();
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		label_Chart = new Label(container, SWT.NONE);
		label_Chart.setBounds(407, 62, 265, 230);
		label_Chart.setText("New Label");
		
		radio_Value = new Button(container, SWT.RADIO);
		radio_Value.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onValueSelect();
			}
		});
		radio_Value.setBounds(30, 36, 40, 16);
		radio_Value.setText("\u503C");
		
		radio_Search = new Button(container, SWT.RADIO);
		radio_Search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSearchSelect();
			}
		});
		radio_Search.setText("\u67E5\u8BE2");
		radio_Search.setBounds(105, 36, 51, 16);
		
		
		fillProperties();
		label_Chart.setImage(DiagramWizard_3.initImage(DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(),DiagramWizard_3.getChartType(chartPartDef.get_CategoryAsString()),null), 265, 230));
//		initValueModel(container, 1);
	}
	
	//true-->值；false-->查找
	private void createLabelModel(boolean flag){
		if(flag){
			Label label_StartValue = new Label(container, SWT.NONE);
			label_StartValue.setText("\u5F00\u59CB\u503C");
			label_StartValue.setBounds(10, 73, 60, 16);
			
			Label label_EndValue = new Label(container, SWT.NONE);
			label_EndValue.setText("\u7ED3\u675F\u503C");
			label_EndValue.setBounds(76, 73, 50, 16);
			
			Label label_AreaColor = new Label(container, SWT.NONE);
			label_AreaColor.setText("\u533A\u57DF\u989C\u8272");
			label_AreaColor.setBounds(149, 73, 60, 16);
			
			Label label_AreaName = new Label(container, SWT.NONE);
			label_AreaName.setText("\u533A\u57DF\u540D\u79F0");
			label_AreaName.setBounds(236, 73, 60, 16);
			
		}else{
			Label label_field = new Label(container, SWT.NONE);
			label_field.setText("\u5F00\u59CB\u503C");
			label_field.setBounds(10, 73, 60, 16);
			
			Label label_Sum = new Label(container, SWT.NONE);
			label_Sum.setText("\u7ED3\u675F\u503C");
			label_Sum.setBounds(106, 73, 50, 16);
			
			Label label_AreaColor = new Label(container, SWT.NONE);
			label_AreaColor.setText("\u533A\u57DF\u989C\u8272");
			label_AreaColor.setBounds(181, 73, 60, 16);
			
			Label label_AreaName = new Label(container, SWT.NONE);
			label_AreaName.setText("\u533A\u57DF\u540D\u79F0");
			label_AreaName.setBounds(272, 73, 60, 16);
			
		}
	}
	
	private void createValueModel(GaugeRegionDef def, final int count){
		controlRow = count;
		final ArrayList<Control> rows = new ArrayList<Control>();
		
		Text text_StartValue = new Text(container, SWT.BORDER | SWT.CENTER);
		text_StartValue.setEnabled(false);
		text_StartValue.setBounds(5, 95 + STEP*count, 60, 20);
		
		
		final Text text_EndValue = new Text(container, SWT.BORDER | SWT.CENTER);
		text_EndValue.setBounds(68, 95 + STEP*count, 60, 20);
		
		
		final TableCombo combo_AreaColor = new TableCombo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_AreaColor.setBounds(132, 95 + STEP*count, 90, 21);
		
		
		final Text text_AreaName = new Text(container, SWT.BORDER | SWT.CENTER);
		text_AreaName.setBounds(223, 95 + STEP*count, 85, 20);
		
		
		Button btnAdd = new Button(container, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(validateValue(text_EndValue, combo_AreaColor, text_AreaName))	createValueModel(null, controlRow + 1);
			}
		});
		btnAdd.setText("add");
		btnAdd.setBounds(310, 92 + STEP*count, 25, 25);
		
		
		Button btnDel = new Button(container, SWT.NONE);
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delelteRow(rows);
				((Text)regionRows.get(controlRow).get(1)).setEnabled(true);
			}
		});
		btnDel.setText("del");
		btnDel.setBounds(336, 92 + STEP*count, 25, 25);
		
		
		String color = "";
		
		if(def != null){
			text_StartValue.setText(def.get_RegionStartValue().toString());
			text_EndValue.setText(def.get_RegionValue().toString());
			color = def.get_RegionColor().get_Name();
			text_AreaName.setText(def.get_RegionName());
			
		}else{
			if(count == 0){
				text_StartValue.setText(0 + "");
			}else{
				Text startNum = (Text) regionRows.get(count - 1).get(1);
				text_StartValue.setText(startNum.getText());
			}
		}
		initColorCombo(combo_AreaColor, color);
		
		
		rows.add(text_StartValue);
		rows.add(text_EndValue);
		rows.add(combo_AreaColor);
		rows.add(text_AreaName);
		rows.add(btnAdd);
		rows.add(btnDel);
		regionRows.add(count, rows);
		
		
		if(count > 0){
			Button add = (Button) regionRows.get(count - 1).get(4);
//			if(count == 1) ((Button) regionRows.get(0).get(4)).setVisible(true);
			add.setVisible(false);
			Button del = (Button) regionRows.get(count - 1).get(5);
			del.setVisible(true);
			Text endValue = (Text) regionRows.get(count -1).get(1);
			endValue.setEnabled(false);
		}
		if(count >6){
			Button add = (Button) regionRows.get(count).get(4);
			add.setVisible(false);
		}
		if(count <1){
			Button del = (Button) regionRows.get(count).get(5);
			del.setVisible(false);
		}
	}
	
	private boolean validateRows(){
		ArrayList<Control> list = regionRows.get(regionRows.size() - 1);
		if(radio_Value.getSelection()){
			return validateValue((Text)list.get(1), (TableCombo)list.get(2), (Text)list.get(3));
		}else{
			return validateSearch((CCombo)list.get(0), (CCombo)list.get(1), (TableCombo)list.get(2), (Text)list.get(3));
		}
	}
	
	private boolean validateValue(Text endValue, TableCombo color, Text name){
		
		if(!"".equals(endValue.getText().trim()) && color.getData(color.getText()) != null && 
				!"".equals(name.getText().trim()))  return true;
		return false;
	}
	
	private boolean validateSearch(CCombo field, CCombo query, TableCombo color, Text name){
		
		if(!"".equals(field.getText()) && !"".equals(query.getText()) && color.getData(color.getText()) != null && !"".equals(name.getText())){
			return true;
		}
		return false;
	}
	
	
	private void delelteRow(ArrayList<Control> rows){
		ArrayList<Control> list =  rows;
		int now = regionRows.indexOf(rows);
		for(int i = 0; i < list.size(); i++){
			Control con = (Control) list.get(i);
			con.dispose();
		}
		regionRows.remove(rows);
		controlRow --;
		
		for(int i = now; i < regionRows.size(); i++){
			ArrayList<Control> current = regionRows.get(i);
			
			for(int j = 0; j < current.size(); j++){
				Control control = (Control)current.get(j);
				org.eclipse.swt.graphics.Rectangle  re = control.getBounds();
				re.y = re.y - STEP;
				control.setBounds(re);
				
			}
		}
		if(controlRow < 7) ((Button)regionRows.get(controlRow).get(4)).setVisible(true);
		if(controlRow < 1) ((Button)regionRows.get(controlRow).get(5)).setVisible(false);
	}
	
	private void onValueSelect(){
		if(!ValueOrField){
			for(int i = 0; i < regionRows.size(); i++){
				for(int j = 0; j< regionRows.get(i).size(); j++){
					Control con = (Control) regionRows.get(i).get(j);
					con.dispose();
				}
			}
			regionRows.clear();
			createValueModel(null, 0);
			ValueOrField = true;
		}
	}
	
	private void onSearchSelect(){
		if(ValueOrField){
			for(int i = 0; i < regionRows.size(); i++){
				for(int j = 0; j< regionRows.get(i).size(); j++){
					Control con = (Control) regionRows.get(i).get(j);
					con.dispose();
				}   
			}
			regionRows.clear();
			createSearchModel(null, 0);
			ValueOrField = false;
		}
	}
	
	private void createSearchModel(GaugeRegionDef def, final int count){
		controlRow = count;
		final ArrayList<Control> rows = new ArrayList<Control>();
		
		final CCombo combo_Sum = new CCombo(container, SWT.BORDER);
		combo_Sum.setBounds(103, 95 + STEP*count, 60, 20);
		
		
		final CCombo combo_Field = new CCombo(container, SWT.BORDER);
		combo_Field.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("更多值...".equals(combo_Field.getText())){
					SimpleExpressSelector simpleES = new SimpleExpressSelector(getShell(), new String[]{chartPartDef.get_BusObName()}, "", 1, false, false, !DiagramWizard_5.limitDateTime);
					if(simpleES.open() == 0){
						combo_Field.setText(simpleES.getFieldDefHolder().getUp_fieldDef_alias());
						FieldDef field = simpleES.getFieldDefHolder().getFieldDef();
						DiagramWizard_6.LoadEvaluateByQueryFunctionList(DiagramWizard_6.IsFieldNumeric(field), combo_Sum);
						combo_Field.setData(simpleES.getFieldDefHolder().getUp_fieldDef_alias(), field.get_Name());
					}
				}
			}
		});
		combo_Field.setBounds(10, 95 + STEP*count, 90, 20);
		
		
		final TableCombo combo_AreaColor = new TableCombo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_AreaColor.setBounds(166, 95 + STEP*count, 90, 21);
		
		
		final Text text_AreaName = new Text(container, SWT.BORDER | SWT.CENTER);
		text_AreaName.setBounds(260, 95 + STEP*count, 85, 20);
		
		
		Button button_Add = new Button(container, SWT.NONE);
		button_Add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(validateSearch(combo_Field, combo_Sum, combo_AreaColor, text_AreaName))
					createSearchModel(null, controlRow + 1);
			}
		});
		button_Add.setBounds(350, 92 + STEP*count, 25, 25);
		button_Add.setText("add");
		
		
		
		Button button_Del = new Button(container, SWT.NONE);
		button_Del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				delelteRow(rows);
			}
		});
		button_Del.setBounds(376, 92 + STEP*count, 25, 25);
		button_Del.setText("del");
		
		
		String color = "";
		FieldDef field = fieldDefList.get(0).getFieldDef();
		
		
		combo_Field.add(field.get_Alias());
		combo_Field.setData(field.get_Alias(), field.get_Name());
		combo_Field.add("更多值...");
		
		if(def != null){
			field = getFieldDefByList(def.get_FieldName());
			combo_Field.setText(field.get_Alias());
			combo_Field.setData(field.get_Alias(), field.get_Name());
			
			DiagramWizard_6.LoadEvaluateByQueryFunctionList(DiagramWizard_6.IsFieldNumeric(field), combo_Sum);
			combo_Sum.setText(ReflectQueryFunction(def.get_QueryFunction()));
			color = def.get_RegionColor().name;
			text_AreaName.setText(def.get_RegionName());
			
		}else{
			DiagramWizard_6.LoadEvaluateByQueryFunctionList(DiagramWizard_6.IsFieldNumeric(fieldDefList.get(0).getFieldDef()), combo_Sum);
			combo_Field.setText(combo_Field.getItem(0));
		}
		initColorCombo(combo_AreaColor, color);
		
		rows.add(combo_Field);
		rows.add(combo_Sum);
		rows.add(combo_AreaColor);
		rows.add(text_AreaName);
		rows.add(button_Add);
		rows.add(button_Del);
		regionRows.add(count, rows);
		
		if(count > 0){
			Button add = (Button) regionRows.get(count - 1).get(4);
			add.setVisible(false);
//			if(count == 1) ((Button) regionRows.get(0).get(4)).setVisible(true);
			Button del = (Button) regionRows.get(count - 1).get(5);
			del.setVisible(true);
		}
		if(count >6){
			button_Add.setVisible(false);
		}
		if(count <1){
			button_Del.setVisible(false);
		}
	}
	
	private FieldDef getFieldDefByList(String fieldName) {
		FieldDef def = null;
		for (FieldDefHolder fieldHolder : fieldDefList) {
			if (fieldHolder.getFieldDef().get_Name().equals(fieldName)) {
				return def = fieldHolder.getFieldDef();
			}
		}
		return def;
	}
	
	private void initColorCombo(TableCombo combo, String color){
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + str);
//			combo.add(colorName);
//			combo.setData(colorName, str);
//			if(color != null && str.equals(color))	combo.setText(colorName);
//		}
//		if("".equals(combo.getText()))	combo.setText("请选择颜色...");
		ColorComboInit.Init(combo, color);
	}
	
	
	
	private List<FieldDefHolder> getFieldsByOb(String busName){
		DefinitionLibrary m_Library=ConnectionBroker.get_SiteviewApi().get_LiveDefinitionLibrary();
		List<FieldDefHolder> fieldDefHolderList=new ArrayList<FieldDefHolder>();
		BusinessObjectDef businessObjectDef =m_Library.GetBusinessObjectDef(busName);
		
		Set<String> repeatValidata=new HashSet<String>();
		
		ICollection combinedGroupFieldDefs=null;
		
		if(businessObjectDef.get_ParentOfGroup()){
			combinedGroupFieldDefs=m_Library.GetCombinedGroupFieldDefs(businessObjectDef.get_Name());
		}
		else{
			combinedGroupFieldDefs=businessObjectDef.get_FieldDefs();
		}
		
		for(IEnumerator enume=combinedGroupFieldDefs.GetEnumerator();enume.MoveNext();){
			FieldDef def=(FieldDef)enume.get_Current();
			if (!def.get_IsSearchable()){
				continue;
			}
			if(!repeatValidata.contains(def.get_Alias())){
				fieldDefHolderList.add(new FieldDefHolder(def,businessObjectDef.get_Alias(), null, def.get_Alias(), 1, null));
				repeatValidata.add(def.get_Alias());
			}
		}
		return fieldDefHolderList;
	}
	
	 
	 private  String ReflectQueryFunction(QueryFunctions qf) {
		 	String str = "";
	        if(qf == QueryFunctions.AverageAllValuesFunction || qf == QueryFunctions.AverageDistinctValuesFunction){
	            return core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldAverage");
	        }
	        else if(qf == QueryFunctions.MaxValueFunction){
	            return core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldMax");
	        }
	        else if(qf == QueryFunctions.MinValueFunction){
	            return core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldMin");
	        }
	        else if(qf == QueryFunctions.SumOfAllValuesFunction || qf == QueryFunctions.SumOfDistinctValuesFunction){
	            return core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldSum");
	        }
	        else if(qf == QueryFunctions.CountOfAllValuesFunction || qf == QueryFunctions.CountOfDistinctValuesFunction){
	            return core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldCount");
	        }
	        return str;
	 }
	
	 private void LoadQuery(boolean flag){
	        GaugeRegionDef[] gaugeRegionOptions = null;
	        gaugeRegionOptions = chartPartDef.get_GaugeRegionOptions();
	        
            if (flag){
            	int count = 0;
            	 for(GaugeRegionDef def:gaugeRegionOptions){
            		 createValueModel(def, count);
 	                count++;
 	            }
            }else{
            	int count = 0;
           	 for(GaugeRegionDef def:gaugeRegionOptions){
           		 createSearchModel(def, count);
	                count++;
	            }
            	
            }
	 }
	 
	 
	 @Override
		public boolean canFlipToNextPage() {
			return true;
		}
	
	
	@Override
	public IWizardPage getNextPage() {
		if(validateRows()){
			saveProperties();
			diagramWizard.addPage(new DiagramWizard_13(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_13");
		}else{
			MessageDialog.openInformation(getShell(), "输入提示！", "请正确填写数据！");
			return this;
		}
	}
	
	private void fillProperties(){
		fieldDefList = getFieldsByOb(chartPartDef.get_BusObName());
		GaugeRegionDef[] gaugeRegionOptions = chartPartDef.get_GaugeRegionOptions();
		if("New".equals(type) || gaugeRegionOptions.length < 1){
			radio_Value.setSelection(true);
			ValueOrField = true;
			createLabelModel(true);
			createValueModel(null, 0);
        
		}else{
        	boolean flag = chartPartDef.get_RegionByQuery();
        	ValueOrField = !flag;
            radio_Search.setSelection(flag);
            radio_Value.setSelection(!flag);
            createLabelModel(!flag);
            LoadQuery(!flag);
            
        }
	}
	
	
	private void BuildRegions(){
		
        for(int i = 0; i< regionRows.size(); i++){
        	GaugeRegionDef regionDef = new GaugeRegionDef();
        	
        	if(radio_Value.getSelection()){
                regionDef.set_FieldName("");
                regionDef.set_QueryFunction(QueryFunctions.CountOfAllValuesFunction);
                regionDef.set_RegionStartValue(Integer.valueOf(((Text)regionRows.get(i).get(0)).getText().trim()));
                regionDef.set_RegionValue(Integer.valueOf(((Text)regionRows.get(i).get(1)).getText().trim()));
            
        	}else{
        		CCombo field = (CCombo)regionRows.get(i).get(0);
                regionDef.set_FieldName((String)field.getData(field.getText()));
                CCombo query = (CCombo)regionRows.get(i).get(1);
                regionDef.set_QueryFunction((QueryFunctions)query.getData(query.getText()));
                regionDef.set_RegionStartValue(0);
                regionDef.set_RegionValue(0);
        		
        	}
        	regionDef.set_RegionName(((Text)regionRows.get(i).get(3)).getText().trim());
        	TableCombo color = (TableCombo)regionRows.get(i).get(2);
        	regionDef.set_RegionColor(getColor(color));
        		
        	chartPartDef.AddGaugeRegionDef(regionDef);
        }

    }
	
	private system.Drawing.Color getColor(TableCombo combo){
		 String colorName = (String)combo.getData(combo.getText());
		 colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		 return system.Drawing.Color.FromName(colorName);
	}
	
	
	private void saveProperties(){
		  chartPartDef.set_GaugeTitle("");
          chartPartDef.set_GaugeTitleVisible(true);
          chartPartDef.set_GaugeLabel("");
          chartPartDef.set_GaugeLabelVisible(true);
          chartPartDef.set_GaugeLegendVisible(true);
          chartPartDef.set_RefreshFrequency(100);
          chartPartDef.set_RegionByQuery(radio_Search.getSelection());
          chartPartDef.ClearGaugeRegionDefs();
          BuildRegions();
	}
}
