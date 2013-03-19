package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DrilldownDef;
import Core.Dashboards.TimeInterval;
import Siteview.FieldCategory;
import Siteview.QueryFunctions;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import core.apploader.search.dialog.SimpleExpressSelector;
import core.apploader.search.pojo.FieldDefHolder;


public class DiagramWizard_10 extends WizardPage {

	private DiagramWizard diagramWizard;
	
	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private CCombo combo_DrillEvaluateByField;
	private CCombo combo_DrillGroupByInterval;
	private CCombo combo_DrillQueryFunction;
	private CCombo combo_DrillBusObDef;
	private boolean isDateTime;
	
	private String DrillEvaluateByFieldName;

	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_10() {
		super("diagramWizard_10");
		setTitle("子图表--主群组选择");
		setDescription("提供定义钻取图表的分组方法和求值方法选择的功能部件。");
	}
	
	public DiagramWizard_10(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_10(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		lblNewLabel.setBounds(10, 10, 163, 16);
		lblNewLabel.setText("\u8BF7\u9009\u62E9\u4E0B\u94BB\u56FE\u8868\u7684\u5206\u7EC4\u65B9\u5F0F");
		
		combo_DrillEvaluateByField = new CCombo(container, SWT.BORDER);
		combo_DrillEvaluateByField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BusinessObjectDef businessObjectDef = m_Api.get_BusObDefinitions().GetBusinessObjectDef(chartPartDef.get_BusObName());
				String fieldName = combo_DrillEvaluateByField.getData(combo_DrillEvaluateByField.getText()).toString();
				DrillEvaluateByFieldName = fieldName;
				LoadEvaluateByQueryFunctionList(IsFieldNumeric(businessObjectDef.GetField(fieldName)));
			}
		});
		combo_DrillEvaluateByField.setBounds(170, 158, 283, 20);
		
		Button btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.setSelection(true);
		btnRadioButton.setBounds(10, 44, 103, 16);
		btnRadioButton.setText("\u4F7F\u7528\u5B57\u6BB5\u6570\u636E");
		
		combo_DrillBusObDef = new CCombo(container, SWT.BORDER);
		combo_DrillBusObDef.add("更多值...");
		combo_DrillBusObDef.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("更多值...".equals(combo_DrillBusObDef.getText())) {
					SimpleExpressSelector simpleES = new SimpleExpressSelector(getShell(), new String[]{chartPartDef.get_BusObName()}, "", 1, true, true, !DiagramWizard_5.limitDateTime);
					if(simpleES.open() == 0) {
						combo_DrillBusObDef.setText(simpleES.getFieldDefHolder().getUp_fieldDef_alias());
						FieldDef field = simpleES.getFieldDefHolder().getFieldDef();
						isDateTime = field.get_IsDateTime();
						combo_DrillGroupByInterval.setEnabled(isDateTime);
						String fieldName = field.get_QualifiedName();
						fieldName = fieldName.substring(0, fieldName.indexOf(".")) + "." + field.get_Name();
						combo_DrillBusObDef.setData(simpleES.getFieldDefHolder().getUp_fieldDef_alias(), fieldName);
					}else{
						combo_DrillBusObDef.setText("");
					}
				}
			}
		});
		combo_DrillBusObDef.setBounds(170, 40, 120, 20);
		
		combo_DrillGroupByInterval = new CCombo(container, SWT.BORDER);
		combo_DrillGroupByInterval.setBounds(390, 40, 120, 20);
		combo_DrillGroupByInterval.setEnabled(false);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u95F4\u9694");
		label.setBounds(324, 44, 35, 16);
		
		Label label_3 = new Label(container, SWT.NONE);
		label_3.setText("\u63A5\u7740\uFF0C\u8BF7\u9009\u62E9\u5728\u56FE\u8868\u4E2D\u8BC4\u4F30\u6570\u636E\u7684\u65B9\u5F0F");
		label_3.setBounds(10, 92, 225, 16);
		
		Button button = new Button(container, SWT.RADIO);
		button.setSelection(true);
		button.setText("\u4F7F\u7528\u5B57\u6BB5\u6570\u636E");
		button.setBounds(10, 128, 103, 16);
		
		combo_DrillQueryFunction = new CCombo(container, SWT.BORDER);
		combo_DrillQueryFunction.setBounds(170, 124, 120, 20);
		
		fillProperties();
	}
	
	@Override
	public IWizardPage getNextPage() {
		if("".equals(combo_DrillBusObDef.getText().trim()) || combo_DrillBusObDef.getData(combo_DrillBusObDef.getText()) == null){
			MessageDialog.openInformation(getShell(), "输入有误", "使用字段数据为空，或输入有误！");
			return this;
		}
		saveProperties();
		if("CHARTPIE".equals(chartPartDef.get_CategoryAsString())){
			diagramWizard.addPage(new DiagramWizard_11(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_11");
		}else{
			diagramWizard.addPage(new DiagramWizard_7_1(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_7_1");
		}
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	private boolean IsFieldNumeric(FieldDef field)
    {
        int fieldType = field.get_FieldType();
        if (fieldType <= FieldCategory.Number)
        {
            if ((fieldType != FieldCategory.Text) && (fieldType == FieldCategory.Number))
            {
                return true;
            }
        }
        else if (((fieldType == FieldCategory.Logical) || (fieldType == FieldCategory.DateTime)) || (fieldType == FieldCategory.Binary))
        {
        }
        return false;
    }
	
	private void LoadEvaluateByQueryFunctionList(boolean numeric)
    {
		combo_DrillQueryFunction.removeAll();
		combo_DrillQueryFunction.add("数量");
		combo_DrillQueryFunction.setData("数量", QueryFunctions.CountOfAllValuesFunction);
        if (numeric)
        {
            combo_DrillQueryFunction.add("平均");
            combo_DrillQueryFunction.add("最大");
            combo_DrillQueryFunction.add("最小");
            combo_DrillQueryFunction.add("总和");
            combo_DrillQueryFunction.setData("平均", QueryFunctions.AverageAllValuesFunction);
            combo_DrillQueryFunction.setData("最大", QueryFunctions.MaxValueFunction);
            combo_DrillQueryFunction.setData("最小", QueryFunctions.SumOfAllValuesFunction);
            combo_DrillQueryFunction.setData("总和", QueryFunctions.CountOfAllValuesFunction);
        }
        this.combo_DrillQueryFunction.setText(combo_DrillQueryFunction.getItem(0));
    }
	
	private void fillProperties(){
		
		BusinessObjectDef businessObjectDef = m_Api.get_BusObDefinitions().GetBusinessObjectDef(chartPartDef.get_BusObName());
		ICollection fieldDefs = businessObjectDef.get_FieldDefs();
		IEnumerator it = fieldDefs.GetEnumerator();
		while(it.MoveNext()){
			combo_DrillEvaluateByField.add(((FieldDef)it.get_Current()).get_Alias());
			combo_DrillEvaluateByField.setData(((FieldDef)it.get_Current()).get_Alias(), ((FieldDef)it.get_Current()).get_Name());
		}
		
		combo_DrillQueryFunction.add(DiagramWizard_6.ReflectQueryFunction(chartPartDef.get_EvaluateByDef().get_QueryFunction()));
		combo_DrillQueryFunction.setText(combo_DrillQueryFunction.getItem(0));
		combo_DrillQueryFunction.setData(chartPartDef.get_EvaluateByDef().get_QueryFunction());
		 
		 String busObName = ((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_FieldName();
		 if(busObName != null && !"".equals(busObName)){
			 FieldDefHolder fieldDef = DiagramWizard_6.getFieldDefByString(busObName, chartPartDef.get_BusObName(), 1);
			 if(fieldDef != null)
			 {
				 isDateTime = fieldDef.getFieldDef().get_IsDateTime();
				 combo_DrillGroupByInterval.setEnabled(isDateTime);
				 combo_DrillBusObDef.setText(fieldDef.getUp_fieldDef_alias());
				 combo_DrillBusObDef.setData(fieldDef.getUp_fieldDef_alias(), busObName);
			 }
		 }
		
		if(!"New".equals(type)){
			String fieldName = ((DrilldownDef) chartPartDef.get_DrilldownArrayList().get_Item(0)).get_EvaluateByDef().get_FieldName();
			IEnumerator it1 = fieldDefs.GetEnumerator();
			while(it1.MoveNext()){
				if(fieldName.equals(((FieldDef)it1.get_Current()).get_Alias()) || fieldName.equals(((FieldDef)it1.get_Current()).get_Name()))
					combo_DrillEvaluateByField.setText(((FieldDef)it1.get_Current()).get_Alias());
			}
			
			TimeInterval[] timeInterval = TimeInterval.values();
			for(int i = 0 ; i < timeInterval.length ; i++){
				if (timeInterval[i] != TimeInterval.None) {
					combo_DrillGroupByInterval.add(DiagramWizard_6.DateToString(timeInterval[i]));
					if(combo_DrillGroupByInterval.getItems().length == 1) combo_DrillGroupByInterval.setText(DiagramWizard_6.DateToString(timeInterval[i]));
				}
			}
		}else{
			combo_DrillEvaluateByField.setText(combo_DrillEvaluateByField.getItem(0));
			
			TimeInterval[] timeInterval = TimeInterval.values();
			for(int i = 0 ; i < timeInterval.length ; i++){
				if (timeInterval[i] != TimeInterval.None) {
					combo_DrillGroupByInterval.add(DiagramWizard_6.DateToString(timeInterval[i]));
				}
			}
			combo_DrillGroupByInterval.setText(combo_DrillGroupByInterval.getItem(0));
			combo_DrillGroupByInterval.setEnabled(false);
		}
		Object fieldName = combo_DrillEvaluateByField.getData(combo_DrillEvaluateByField.getText());
		
		if(fieldName != null){
			DrillEvaluateByFieldName = fieldName.toString();
			LoadEvaluateByQueryFunctionList(IsFieldNumeric(businessObjectDef.GetField(fieldName.toString())));
		}
			
	}
	
	private void saveProperties(){
		if (chartPartDef.get_DrilldownArrayList().get_Count() > 0){
            ((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().set_Apply(true);
        }else{
        	((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().set_Apply(false);
        }
        if (combo_DrillBusObDef.getEnabled()){
        	((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().set_FieldName((String)combo_DrillBusObDef.getData(combo_DrillBusObDef.getText()));
        	((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().set_DateTimeField(isDateTime);
        	((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().set_Interval((String)combo_DrillGroupByInterval.getData(combo_DrillGroupByInterval.getText()));
        }
        if (combo_DrillEvaluateByField.getEnabled()){
        	((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_EvaluateByDef().set_FieldName(DrillEvaluateByFieldName);
        	((DrilldownDef)chartPartDef.get_DrilldownArrayList().get_Item(0)).get_EvaluateByDef().set_QueryFunction((QueryFunctions)combo_DrillQueryFunction.getData());
        }
	}
}
