package core.ui.wizard;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import system.Type;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.TimeInterval;
import Siteview.FieldCategory;
import Siteview.QueryFunctions;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.FunctionCategory;
import Siteview.Xml.LocalizeHelper;
import core.apploader.search.dialog.SimpleExpressSelector;
import core.apploader.search.pojo.FieldDefHolder;
import core.ui.dialog.DashboardDateDialog;


public class DiagramWizard_6 extends WizardPage {

	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private static DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private DiagramWizard diagramWizard;
	private CCombo combo_GroupByField;
	private Combo combo_EvaluateByField;
	private Combo combo_GroupByInterval;
	private Combo combo_GroupByDurationUnits;
	private Combo combo_EvaluateByDurationUnits;
	private Button radio_GroupByField;
	private Button radio_GroupByDuration;
	private CCombo combo_GroupByDurationStart;
	private CCombo combo_GroupByDurationEnd;
	
	private Button radio_EvaluateByField;
	private Button radio_EvaluateByDuration;
	private CCombo combo_EvaluateByDurationStart;
	private CCombo combo_EvaluateByDurationEnd;
	private Button check_IsIncludeOthers;
	private CCombo combo_EvaluateByQueryFunction;
	private CCombo combo_LimitOrderBy;
	private Text text_OtherName;
	private Button check_ShowOnly;
	private Spinner spinner_Number;
	private Combo combo_LimitDesc;

	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_6() {
		super("diagramWizard_6");
		setTitle("主分组选择");
		setDescription("提供定义图表首选分组方法和求值方法的功能部件。");
	}
	
	public DiagramWizard_6(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_6(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		lblNewLabel.setBounds(10, 10, 142, 18);
		lblNewLabel.setText("\u6307\u5B9A\u56FE\u8868\u4E0A\u7684\u8BB0\u5F55\u5206\u7EC4:");
		
		Group group = new Group(container, SWT.SHADOW_NONE);
		group.setBounds(10, 27, 113, 61);
		
		radio_GroupByField = new Button(group, SWT.RADIO);
		radio_GroupByField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onGroupRiadoSelected(true);
			}
		});
		radio_GroupByField.setText("\u4F7F\u7528\u5B57\u6BB5\u6570\u636E");
		radio_GroupByField.setBounds(0, 5, 103, 16);
		
		radio_GroupByDuration = new Button(group, SWT.RADIO);
		radio_GroupByDuration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onGroupRiadoSelected(false);
			}
		});
		radio_GroupByDuration.setText("\u4F7F\u7528\u5B57\u6BB5\u6570\u636E");
		radio_GroupByDuration.setBounds(0, 42, 103, 16);
		
		combo_GroupByField = new CCombo(container, SWT.BORDER);
		combo_GroupByField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("更多值...".equals(combo_GroupByField.getText())){
					SimpleExpressSelector simpleES = new SimpleExpressSelector(getShell(), new String[]{chartPartDef.get_BusObName()}, "", 1, true, true, !DiagramWizard_5.limitDateTime);
					if(simpleES.open() == 0){
						combo_GroupByField.setText(simpleES.getFieldDefHolder().getUp_fieldDef_alias());
						FieldDef field = simpleES.getFieldDefHolder().getFieldDef();
						combo_GroupByInterval.setEnabled(field.get_IsDateTime());
						String fieldName = field.get_QualifiedName();
						fieldName = fieldName.substring(0, fieldName.indexOf(".")) + "." + field.get_Name();
						combo_GroupByField.setData(simpleES.getFieldDefHolder().getUp_fieldDef_alias(), fieldName);
					}else{
						combo_GroupByField.setText("");
					}
						
				}
			}
		});
		combo_GroupByField.add("更多值...");
		combo_GroupByField.setBounds(129, 29, 142, 20);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("\u95F4\u9694");
		label_1.setBounds(315, 32, 45, 18);
		
		combo_GroupByInterval = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_GroupByInterval.setBounds(385, 30, 120, 20);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u5F00\u59CB");
		label.setBounds(134, 60, 35, 18);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setText("\u7ED3\u675F");
		label_2.setBounds(134, 90, 35, 16);
		
		Label label_3 = new Label(container, SWT.NONE);
		label_3.setText("\u5355\u5143");
		label_3.setBounds(315, 66, 45, 18);
		
		combo_GroupByDurationUnits = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_GroupByDurationUnits.setBounds(385, 64, 120, 20);
		
		Label label_4 = new Label(container, SWT.NONE);
		label_4.setText("\u6307\u5B9A\u56FE\u8868\u4E2D\u6570\u636E\u7684\u6C42\u503C\u65B9\u5F0F:");
		label_4.setBounds(10, 112, 174, 18);
		
		combo_EvaluateByQueryFunction = new CCombo(container, SWT.BORDER);
		combo_EvaluateByQueryFunction.setBounds(129, 130, 142, 20);
		
		combo_EvaluateByField = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_EvaluateByField.setBounds(315, 130, 190, 20);
		
		Label label_6 = new Label(container, SWT.NONE);
		label_6.setText("\u5F00\u59CB");
		label_6.setBounds(129, 160, 35, 18);
		
		Label label_7 = new Label(container, SWT.NONE);
		label_7.setText("\u7ED3\u675F");
		label_7.setBounds(129, 188, 35, 16);
		
		Label label_8 = new Label(container, SWT.NONE);
		label_8.setText("\u5355\u5143");
		label_8.setBounds(315, 169, 45, 18);
		
		combo_EvaluateByDurationUnits = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_EvaluateByDurationUnits.setBounds(385, 165, 120, 20);
		
		Label label_9 = new Label(container, SWT.NONE);
		label_9.setText("\u5E0C\u671B\u9650\u5236\u8FD9\u79CD\u5206\u7EC4\u7684\u6570\u636E\u5B50\u96C6:");
		label_9.setBounds(10, 220, 174, 18);
		
		check_ShowOnly = new Button(container, SWT.CHECK);
		check_ShowOnly.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onShowOnlySelected(check_ShowOnly.getSelection());
			}
		});
		check_ShowOnly.setBounds(10, 244, 65, 16);
		check_ShowOnly.setText("\u4EC5\u663E\u793A");
		
		combo_LimitDesc = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_LimitDesc.setBounds(81, 242, 58, 20);
		
		spinner_Number = new Spinner(container, SWT.BORDER);
		spinner_Number.setBounds(143, 242, 41, 20);
		spinner_Number.setMinimum(1);
		spinner_Number.setMaximum(100);
		
		Label label_10 = new Label(container, SWT.NONE);
		label_10.setText("\u8BB0\u5F55\u6392\u5217\u987A\u5E8F");
		label_10.setBounds(190, 244, 85, 18);
		
		combo_LimitOrderBy = new CCombo(container, SWT.BORDER);
		combo_LimitOrderBy.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("更多值...".equals(combo_LimitOrderBy.getText())){
					SimpleExpressSelector simpleES = new SimpleExpressSelector(getShell(), new String[]{chartPartDef.get_BusObName()}, "", 1, true, true, !DiagramWizard_5.limitDateTime);
					if(simpleES.open() == 0) {
						combo_LimitOrderBy.setText(simpleES.getFieldDefHolder().getUp_fieldDef_alias());
						FieldDef field = simpleES.getFieldDefHolder().getFieldDef();
						String fieldName = field.get_QualifiedName();
						fieldName = fieldName.substring(0, fieldName.indexOf(".")) + "." + field.get_Name();
						combo_LimitOrderBy.setData(simpleES.getFieldDefHolder().getUp_fieldDef_alias(), fieldName);
					}else{
						combo_LimitOrderBy.setText("");
					}
				}
			}
		});
		combo_LimitOrderBy.add("更多值...");
		combo_LimitOrderBy.setBounds(281, 242, 85, 20);
		
		check_IsIncludeOthers = new Button(container, SWT.CHECK);
		check_IsIncludeOthers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_OtherName.setEnabled(check_IsIncludeOthers.getSelection());
			}
		});
		check_IsIncludeOthers.setText("\u5305\u542B\u201C\u5176\u4ED6\u201D\u540D\u79F0");
		check_IsIncludeOthers.setBounds(10, 270, 129, 16);
		
		text_OtherName = new Text(container, SWT.BORDER);
		text_OtherName.setBounds(155, 269, 205, 20);
		
		Group group_1 = new Group(container, SWT.SHADOW_NONE);
		group_1.setBounds(10, 130, 113, 61);
		
		radio_EvaluateByField = new Button(group_1, SWT.RADIO);
		radio_EvaluateByField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onEvaluateRiadoSelected(true);
			}
		});
		radio_EvaluateByField.setText("\u4F7F\u7528\u5B57\u6BB5\u6570\u636E");
		radio_EvaluateByField.setBounds(0, 5, 103, 16);
		
		radio_EvaluateByDuration = new Button(group_1, SWT.RADIO);
		radio_EvaluateByDuration.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onEvaluateRiadoSelected(false);
			}
		});
		radio_EvaluateByDuration.setText("\u4F7F\u7528\u5B57\u6BB5\u6570\u636E");
		radio_EvaluateByDuration.setBounds(0, 42, 103, 16);
		
//		ToolBar toolBar = new ToolBar(container, SWT.HORIZONTAL);
//		toolBar.setBounds(175, 55, 120, 20);
//		toolBar.setLayoutDeferred(true);
////		toolBar.setLayout(new BorLayout());
//		combo_GroupByDurationStart = new ToolItem(toolBar, SWT.DROP_DOWN);
//		combo_GroupByDurationStart.setWidth(120);
//		combo_GroupByDurationStart.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				DashboardDateDialog date = new DashboardDateDialog(getShell(), chartPartDef.get_BusObName());
//				if(date.open() == 0) {
//					combo_GroupByDurationStart.setText(date.getStrname());
//					combo_GroupByDurationStart.setData(date.getStrdate());
//				}
//			}
//		});
//		toolBar.pack();
		combo_GroupByDurationStart = new CCombo(container, SWT.BORDER);
		combo_GroupByDurationStart.setBounds(175, 57, 120, 20);
		combo_GroupByDurationStart.setEditable(false);
		combo_GroupByDurationStart.setVisibleItemCount(0);
		
		combo_GroupByDurationEnd = new CCombo(container, SWT.BORDER);
		combo_GroupByDurationEnd.setBounds(175, 85, 120, 20);
		combo_GroupByDurationEnd.setEditable(false);
		combo_GroupByDurationEnd.setVisibleItemCount(0);
		
		combo_EvaluateByDurationStart = new CCombo(container, SWT.BORDER);
		combo_EvaluateByDurationStart.setBounds(175, 156, 120, 20);
		combo_EvaluateByDurationStart.setEditable(false);
		combo_EvaluateByDurationStart.setVisibleItemCount(0);
		
		combo_EvaluateByDurationEnd = new CCombo(container, SWT.BORDER);
		combo_EvaluateByDurationEnd.setBounds(175, 186, 120, 20);
		combo_EvaluateByDurationEnd.setEditable(false);
		combo_EvaluateByDurationEnd.setVisibleItemCount(0);
		
		
		
		initModelView();
		if("New".equals(type) || !chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
			fillOnNew();
		}else{
			fillProperties();
		}
	}
	
	private boolean validate(){
		boolean flag = true;
		if(!"GAUGELINEAR".equals(chartPartDef.get_CategoryAsString())){
			if(radio_GroupByField.getEnabled()){
				if(radio_GroupByField.getSelection()){
					if(combo_GroupByField.getData(combo_GroupByField.getText().trim()) == null) return false;
				}else{
					if("".equals(combo_GroupByDurationStart.getText().trim()) || "".equals(combo_GroupByDurationEnd.getText().trim())) return false;
				}
			}
					
			if(!radio_EvaluateByField.getSelection()){
				if("".equals(combo_EvaluateByDurationStart.getText().trim()) || "".equals(combo_EvaluateByDurationEnd.getText().trim())) return false;
			}
		}
		
		return flag;
	}
	
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	
	@Override
	public IWizardPage getNextPage() {
		if(!validate()){
			MessageDialog.openInformation(getShell(), "输入有误", "数据字段为空，或输入有误！");
			return this;
		}
		saveProperties();
		
		if("CHARTPIE".equals(chartPartDef.get_CategoryAsString())){
			
			if(chartPartDef.get_DefaultDateRangeDef().get_ApplyDateRange()){
				diagramWizard.addPage(new DiagramWizard_8(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
				return diagramWizard.getPage("diagramWizard_8");
				
			}else{
				diagramWizard.addPage(new DiagramWizard_9(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
				return diagramWizard.getPage("diagramWizard_9");
				
			}
		}else if("GAUGELINEAR".equals(chartPartDef.get_CategoryAsString())){
			diagramWizard.addPage(new DiagramWizard_12_2(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_12_2");
			
		}else if("SPEEDOMETER".equals(chartPartDef.get_CategoryAsString())){
			diagramWizard.addPage(new DiagramWizard_11_1(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_11_1");
		
		}else{
			diagramWizard.addPage(new DiagramWizard_7(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_7");
			
		}
	}
	
	private void initModelView(){
		BusinessObjectDef businessObjectDef = m_Api.get_BusObDefinitions().GetBusinessObjectDef(chartPartDef.get_BusObName());
		 ICollection fieldDefs = businessObjectDef.get_FieldDefs();
		 IEnumerator it = fieldDefs.GetEnumerator();
		 
		 while(it.MoveNext()){
			 FieldDef field = (FieldDef)it.get_Current();
			 combo_EvaluateByField.add(field.get_Alias());
			 combo_EvaluateByField.setData(field.get_Alias(), field.get_Name());
		 }
		
		 TimeInterval[] timeInterval = TimeInterval.values();
		 
		 for(int i = 0 ; i < timeInterval.length ; i++){
			 if (timeInterval[i] != TimeInterval.None)
             {
				 combo_GroupByInterval.add(DateToString(timeInterval[i]));
				 combo_GroupByInterval.setData(DateToString(timeInterval[i]), timeInterval[i].name());
             }
		 }
		 combo_GroupByInterval.setText("每年");
		 
		 Map<String, String> durations = core.dashboards.Res.get_Default().GetCategoryStrings("DurationUnit");
		 Set<String> duraSet = durations.keySet();
		 
		 for(String duration : duraSet){
			 combo_GroupByDurationUnits.add(durations.get(duration));
			 combo_EvaluateByDurationUnits.add(durations.get(duration));
			 combo_GroupByDurationUnits.setData(durations.get(duration), duration);
			 combo_EvaluateByDurationUnits.setData(durations.get(duration), duration);
			 combo_GroupByDurationUnits.setText("秒");
			 combo_EvaluateByDurationUnits.setText("秒");
		 }
		 
		 combo_LimitDesc.setItems(new String[]{"上限", "下限"});
		 combo_LimitDesc.setData("上限", false);
		 combo_LimitDesc.setData("下限", true);
		 combo_LimitDesc.setText(combo_LimitDesc.getItem(0));
		 
		 addDateMouseListener(combo_GroupByDurationStart);
		 addDateMouseListener(combo_GroupByDurationEnd);
		 addDateMouseListener(combo_EvaluateByDurationStart);
		 addDateMouseListener(combo_EvaluateByDurationEnd);
	}
	
	public static String DateToString(TimeInterval timeInterval) {
		switch (timeInterval) {
		case Yearly:
			return "每年";
		case Quarterly:
			return "每季";
		case Monthly:
			return "每月";
		case Weekly:
			return "每周";
		case Daily:
			return "每天";
		case Hourly:
			return "每小时";
		}
		return "无";
    }
	
	public static String ReflectQueryFunction(QueryFunctions queryFunctions)
    {
		String str = "";
        switch (queryFunctions)
        {
            case AverageAllValuesFunction:
            case AverageDistinctValuesFunction:
                return str = "平均";

            case MaxValueFunction:
            	return str = "最大";

            case MinValueFunction:
            	return str = "最小";

            case SumOfAllValuesFunction:
            case SumOfDistinctValuesFunction:
            	return str = "合计";

            case CountOfAllValuesFunction:
            case CountOfDistinctValuesFunction:
            	return str = "数量";
        }
		return str;
    }
	
	private void onGroupRiadoSelected(boolean flag){
		combo_GroupByField.setEnabled(flag);
		combo_GroupByInterval.setEnabled(flag);
		combo_GroupByDurationStart.setEnabled(!flag);
//		if(combo_GroupByDurationStart.getEnabled())
//			addDateMouseListener(combo_GroupByDurationStart);
		combo_GroupByDurationEnd.setEnabled(!flag);
//		if(combo_GroupByDurationEnd.getEnabled())
//			addDateMouseListener(combo_GroupByDurationEnd);
		combo_GroupByDurationUnits.setEnabled(!flag);
	}
	
	private void onEvaluateRiadoSelected(boolean flag){
		combo_EvaluateByDurationStart.setEnabled(!flag);
//		if(combo_EvaluateByDurationStart.getEnabled()) 
//			addDateMouseListener(combo_EvaluateByDurationStart);
		combo_EvaluateByDurationEnd.setEnabled(!flag);
//		if(combo_EvaluateByDurationEnd.getEnabled()) 
//			addDateMouseListener(combo_EvaluateByDurationEnd);
		combo_EvaluateByDurationUnits.setEnabled(!flag);
		combo_EvaluateByField.setVisible(flag);
		LoadEvaluateByQueryFunctionList(!flag, combo_EvaluateByQueryFunction);
	}
	
	private void onShowOnlySelected(boolean flag){
		combo_LimitDesc.setEnabled(flag);
		spinner_Number.setEnabled(flag);
		combo_LimitOrderBy.setEnabled(flag);
	}
	
	private void addDateMouseListener(final CCombo dateCombo){
		
		dateCombo.addListener(SWT.MouseUp, new Listener(){

			@Override
			public void handleEvent(Event arg0) {
				dateCombo.setVisible(false);
				DashboardDateDialog date = new DashboardDateDialog(getShell(), chartPartDef.get_BusObName());
				if(date.open() == 0) {
					dateCombo.setText(date.getStrname());
					dateCombo.setData(date.getStrdate());
				}
				dateCombo.setVisible(true);
			}
			
			
		});
//			
//			dateCombo.addMouseListener(new MouseAdapter() {
//				@Override
//				public void mouseUp(MouseEvent e) {
//					
//					DashboardDateDialog date = new DashboardDateDialog(getShell(), chartPartDef.get_BusObName());
//					if(date.open() == 0) {
//						dateCombo.setText(date.getStrname());
//						dateCombo.setData(date.getStrdate());
//					}
//				}
//			});
	}
	
	private void fillOnNew(){
		if("GAUGELINEAR".equals(chartPartDef.get_CategoryAsString()) || "SPEEDOMETER".equals(chartPartDef.get_CategoryAsString())){
			onGaugeLinear();
		}else{
			radio_GroupByField.setSelection(true);
			onGroupRiadoSelected(true);
			combo_GroupByInterval.setEnabled(false);
			
			radio_EvaluateByField.setSelection(true);
			onEvaluateRiadoSelected(true);
			
			onShowOnlySelected(false);
			text_OtherName.setEnabled(false);
		}
		combo_EvaluateByField.setText(combo_EvaluateByField.getItem(0));
		FieldDef field = m_Library.GetBusinessObjectFieldDef(chartPartDef.get_BusObName() + "." + combo_EvaluateByField.getData(combo_EvaluateByField.getText()));
		LoadEvaluateByQueryFunctionList(IsFieldNumeric(field), combo_EvaluateByQueryFunction);
	}
	
	private void onGaugeLinear(){
		radio_EvaluateByField.setSelection(true);
		
		combo_GroupByField.setEnabled(false);
		combo_GroupByInterval.setEnabled(false);
		combo_GroupByDurationUnits.setEnabled(false);
		combo_EvaluateByDurationUnits.setEnabled(false);
		radio_GroupByField.setEnabled(false);
		radio_GroupByDuration.setEnabled(false);
		combo_GroupByDurationStart.setEnabled(false);
		combo_GroupByDurationEnd.setEnabled(false);
		
		radio_EvaluateByDuration.setEnabled(false);
		combo_EvaluateByDurationStart.setEnabled(false);
		combo_EvaluateByDurationEnd.setEnabled(false);
		check_IsIncludeOthers.setEnabled(false);
		combo_LimitOrderBy.setEnabled(false);
		text_OtherName.setEnabled(false);
		check_ShowOnly.setEnabled(false);
		spinner_Number.setEnabled(false);
		combo_LimitDesc.setEnabled(false);
	}
	
	public static void LoadEvaluateByQueryFunctionList(boolean numeric, CCombo combo){
		
		String count = core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldCount");
		String average = core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldAverage");
		String max = core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldMax");
		String min = core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldMin");
		String sum = core.dashboards.Res.get_Default().GetString("Chart.EvaluateByFieldSum");
		
        if (numeric){
        	combo.removeAll();
            combo.add(average);
            combo.setData(average, QueryFunctions.AverageAllValuesFunction);
            combo.add(max);
            combo.setData(max, QueryFunctions.MaxValueFunction);
            combo.add(min);
            combo.setData(min, QueryFunctions.MinValueFunction);
            combo.add(sum);
            combo.setData(sum, QueryFunctions.SumOfAllValuesFunction);
            
        }else{
        	combo.removeAll();
        	combo.add(count);
        	combo.setData(count, QueryFunctions.CountOfAllValuesFunction);
        	
        }
        
        combo.setText(combo.getItem(0));
        
    }
	
	 public static boolean IsFieldNumeric(FieldDef field){
         int fieldType = field.get_FieldType();
         if (fieldType <= FieldCategory.Number){
             if ((fieldType != FieldCategory.Text) && (fieldType == FieldCategory.Number)) return true;
         }
         return false;
     }
	 
	
	private void fillProperties(){
		if("GAUGELINEAR".equals(chartPartDef.get_CategoryAsString()) || "SPEEDOMETER".equals(chartPartDef.get_CategoryAsString())){
			
			onGaugeLinear();
			combo_EvaluateByQueryFunction.setText(ReflectQueryFunction(chartPartDef.get_EvaluateByDef().get_QueryFunction()));
			FieldDef field = m_Library.GetBusinessObjectFieldDef(chartPartDef.get_BusObName() + "." + chartPartDef.get_EvaluateByDef().get_FieldName());
			LoadEvaluateByQueryFunctionList(IsFieldNumeric(field), combo_EvaluateByQueryFunction);
			combo_EvaluateByField.setText(field.get_Alias());
			combo_EvaluateByField.setData(field.get_Alias(), field.get_Name());//保存没有上级对象前缀的字段名
			
		}else{
			
			if (!"".equals(chartPartDef.get_GroupByDef().get_FieldName()) && chartPartDef.get_GroupByDef().get_FieldName() != null) {
				radio_GroupByField.setSelection(true);
				onGroupRiadoSelected(true);
				FieldDefHolder fieldByG = getFieldDefByString(chartPartDef.get_GroupByDef().get_FieldName(), chartPartDef.get_BusObName(), 1);//保存含有上级对象前缀的字段名
				if(fieldByG != null)
				{
					combo_GroupByField.setText(fieldByG.getUp_fieldDef_alias());
					combo_GroupByField.setData(fieldByG.getUp_fieldDef_alias(), chartPartDef.get_GroupByDef().get_FieldName());
				}
				
				if(chartPartDef.get_GroupByDef().get_Interval() != null)
					combo_GroupByInterval.setText(DateToString(TimeInterval.valueOf(chartPartDef.get_GroupByDef().get_Interval())));

			} else {
				radio_GroupByDuration.setSelection(true);
				onGroupRiadoSelected(false);
//				-------------------判断 是三种 情况 的哪一种--------数字日期，中文时间段，字段------------
				String groupStart = chartPartDef.get_GroupByDef().get_DurationStartValue();
				
				if (chartPartDef.get_GroupByDef().get_DurationStartFunction()) {//中文时间段
					combo_GroupByDurationStart.setText(LocalizeHelper.GetValue(Type.GetType(FunctionCategory.class.getName()), groupStart));
					combo_GroupByDurationStart.setData(chartPartDef.get_GroupByDef().get_DurationStartValue());
				}else if(groupStart.indexOf(".") != -1) {
					combo_GroupByDurationStart.setText(groupStart);
				}else{// 数字 日期
					combo_GroupByDurationStart.setText(groupStart.substring(0, groupStart.indexOf("T")));
				}
					
				String groupEnd = chartPartDef.get_GroupByDef().get_DurationEndValue();
				
				if (chartPartDef.get_GroupByDef().get_DurationEndFunction()) {
					combo_GroupByDurationEnd.setText(LocalizeHelper.GetValue(Type.GetType(FunctionCategory.class.getName()), groupEnd));
					combo_GroupByDurationEnd.setData(groupEnd);
				}else if(groupEnd.indexOf(".") != -1){
					combo_GroupByDurationEnd.setText(groupEnd);
				}else{
					combo_GroupByDurationEnd.setText(groupEnd.substring(0, groupEnd.indexOf("T")));
				}
				
				String groupUnit = chartPartDef.get_GroupByDef().get_DurationUnit();
				
				if(groupUnit.indexOf(".") == -1)
					groupUnit = "DurationUnit." + groupUnit;
				
				combo_GroupByDurationUnits.setText(core.dashboards.Res.get_Default().GetCategoryStrings("DurationUnit").get(groupUnit));
			}
				 
			if (!"".equals(chartPartDef.get_EvaluateByDef().get_FieldName()) && chartPartDef.get_EvaluateByDef().get_FieldName() != null) {
				
				radio_EvaluateByField.setSelection(true);
				onEvaluateRiadoSelected(true);
				FieldDef field = m_Library.GetBusinessObjectFieldDef(chartPartDef.get_BusObName() + "." + "aa");//chartPartDef.get_EvaluateByDef().get_FieldName());
				if(field != null)
				{
					combo_EvaluateByField.setText(field.get_Alias());
					combo_EvaluateByField.setData(field.get_Alias(), field.get_Name());//保存没有上级对象前缀的字段名
				}
				
			} else {
				
				radio_EvaluateByDuration.setSelection(true);
				onEvaluateRiadoSelected(false);
				String evaluateStart = chartPartDef.get_EvaluateByDef().get_DurationStartValue();
				
				if (chartPartDef.get_EvaluateByDef().get_DurationStartFunction()) {
					combo_EvaluateByDurationStart.setText(LocalizeHelper.GetValue(Type.GetType(FunctionCategory.class.getName()), evaluateStart));
					combo_EvaluateByDurationStart.setData(evaluateStart);
				}else if(evaluateStart.indexOf(".") != -1) {
					combo_EvaluateByDurationStart.setText(evaluateStart);
				}else{
					combo_EvaluateByDurationStart.setText(evaluateStart.substring(0, evaluateStart.indexOf("T")));
				}
					
				String evaluateEnd = chartPartDef.get_EvaluateByDef().get_DurationEndValue();
				
				if (chartPartDef.get_EvaluateByDef().get_DurationEndFunction()) {
					combo_EvaluateByDurationEnd.setText(LocalizeHelper.GetValue(Type.GetType(FunctionCategory.class.getName()), evaluateEnd));
					combo_EvaluateByDurationEnd.setData(evaluateEnd);
				}else if(evaluateEnd.indexOf(".") != -1){
					combo_EvaluateByDurationEnd.setText(evaluateEnd);
				}else{
					combo_EvaluateByDurationEnd.setText(evaluateEnd.substring(0, evaluateEnd.indexOf("T")));
				}
				
				String evaluateUnit = chartPartDef.get_EvaluateByDef().get_DurationUnit();
				
				if(evaluateUnit.indexOf(".") == -1)
					evaluateUnit = "DurationUnit." + evaluateUnit;
				
				combo_EvaluateByDurationUnits.setText(core.dashboards.Res.get_Default().GetCategoryStrings("DurationUnit").get(evaluateUnit));
			}
			
			combo_EvaluateByQueryFunction.setText(ReflectQueryFunction(chartPartDef.get_EvaluateByDef().get_QueryFunction()));
			check_ShowOnly.setSelection(chartPartDef.get_GroupByDef().get_LimitQuery());	//
			
			if(check_ShowOnly.getSelection()){
				combo_LimitDesc.setText(chartPartDef.get_GroupByDef().get_Descending() ? "下限" : "上限");
				spinner_Number.setSelection(chartPartDef.get_GroupByDef().get_NumberOfRecordToReturn());
				
				if(chartPartDef.get_GroupByDef().get_OrderByFieldName() != null && !"".equals(chartPartDef.get_GroupByDef().get_OrderByFieldName())){
					FieldDefHolder field = getFieldDefByString(chartPartDef.get_GroupByDef().get_OrderByFieldName(), chartPartDef.get_BusObName(), 1);//保存含有上级对象前缀的字段名
					if(field != null)
					{
						combo_LimitOrderBy.setText(field.getUp_fieldDef_alias());
						combo_LimitOrderBy.setData(field.getUp_fieldDef_alias(), chartPartDef.get_GroupByDef().get_OrderByFieldName());
					}
				}
				
			}else{
				onShowOnlySelected(false);
			}
			check_IsIncludeOthers.setSelection(chartPartDef.get_GroupByDef().get_IncludeOthers());
			
			if(check_IsIncludeOthers.getSelection()){
				text_OtherName.setText(chartPartDef.get_GroupByDef().get_OthersName());
			}else{
				text_OtherName.setEnabled(false);
			}
		}
	}
	
	private void saveProperties(){
		chartPartDef.get_GroupByDef().set_IsGroupBy(true);
		chartPartDef.get_EvaluateByDef().set_IsEvaluateBy(true);
		chartPartDef.get_SubGroupByDef().set_IsSubGroupBy(true);
		
		if("GAUGELINEAR".equals(chartPartDef.get_CategoryAsString()) || "SPEEDOMETER".equals(chartPartDef.get_CategoryAsString())){
			
			chartPartDef.get_GroupByDef().set_Apply(false);
			chartPartDef.get_EvaluateByDef().set_Duration(false);
			chartPartDef.get_EvaluateByDef().set_FieldName((String)combo_EvaluateByField.getData(combo_EvaluateByField.getText()));
			chartPartDef.get_EvaluateByDef().set_QueryFunction((QueryFunctions)combo_EvaluateByQueryFunction.getData(combo_EvaluateByQueryFunction.getText()));
		
		}else{
			
			chartPartDef.get_GroupByDef().set_Apply(true);
			
			if(radio_GroupByField.getSelection()){
				chartPartDef.get_GroupByDef().set_FieldName((String)combo_GroupByField.getData(combo_GroupByField.getText()));
				chartPartDef.get_GroupByDef().set_Interval((String)combo_GroupByInterval.getData(combo_GroupByInterval.getText()));
				
			}else{
				chartPartDef.get_GroupByDef().set_FieldName("");
				chartPartDef.get_GroupByDef().set_DurationStartFunction(false);
				
				if(combo_GroupByDurationStart.getText().indexOf(".") != -1){	//包含“.”属于字段
					chartPartDef.get_GroupByDef().set_DurationStartValue(combo_GroupByDurationStart.getText());
				}else if(combo_GroupByDurationStart.getText().indexOf("-") != -1){	//包含“-”属于日期
					chartPartDef.get_GroupByDef().set_DurationStartValue(combo_GroupByDurationStart.getText() + "T00:00:00");
				}else{	//属于中文时间段
					chartPartDef.get_GroupByDef().set_DurationStartFunction(true);
					chartPartDef.get_GroupByDef().set_DurationStartValue((String)combo_GroupByDurationStart.getData());
				}
				
				chartPartDef.get_GroupByDef().set_DurationEndFunction(false);
				
				if(combo_GroupByDurationEnd.getText().indexOf(".") != -1){	//包含“.”属于字段
					chartPartDef.get_GroupByDef().set_DurationEndValue(combo_GroupByDurationEnd.getText());
				}else if(combo_GroupByDurationEnd.getText().indexOf("-") != -1){	//包含“-”属于日期
					chartPartDef.get_GroupByDef().set_DurationEndValue(combo_GroupByDurationEnd.getText() + "T00:00:00");
				}else{	//属于中文时间段
					chartPartDef.get_GroupByDef().set_DurationEndFunction(true);
					chartPartDef.get_GroupByDef().set_DurationEndValue((String)combo_GroupByDurationEnd.getData());
				}
				
				chartPartDef.get_GroupByDef().set_DurationUnit((String)combo_GroupByDurationUnits.getData(combo_GroupByDurationUnits.getText()));
				
			}
			
			if(radio_EvaluateByField.getSelection()){
				if("".equals(combo_EvaluateByField.getText().trim())) combo_EvaluateByField.select(0);
				chartPartDef.get_EvaluateByDef().set_FieldName((String)combo_EvaluateByField.getData(combo_EvaluateByField.getText()));
				
			}else{
				chartPartDef.get_EvaluateByDef().set_FieldName("");//设为空  fillproperties时凭此判断radio_EvaluateByField 是否为true
				chartPartDef.get_EvaluateByDef().set_DurationStartFunction(false);
				
				if(combo_EvaluateByDurationStart.getText().indexOf(".") != -1){	//包含“.”属于字段
					chartPartDef.get_EvaluateByDef().set_DurationStartValue(combo_EvaluateByDurationStart.getText());
				}else if(combo_EvaluateByDurationStart.getText().indexOf("-") != -1){	//包含“-”属于日期
					chartPartDef.get_EvaluateByDef().set_DurationStartValue(combo_EvaluateByDurationStart.getText() + "T00:00:00");
				}else{	//属于中文时间段
					chartPartDef.get_EvaluateByDef().set_DurationStartFunction(true);
					chartPartDef.get_EvaluateByDef().set_DurationStartValue((String)combo_EvaluateByDurationStart.getData());
				}
				
				chartPartDef.get_EvaluateByDef().set_DurationEndFunction(false);
				
				if(combo_EvaluateByDurationEnd.getText().indexOf(".") != -1){	//包含“.”属于字段
					chartPartDef.get_EvaluateByDef().set_DurationEndValue(combo_EvaluateByDurationEnd.getText());
				}else if(combo_EvaluateByDurationEnd.getText().indexOf("-") != -1){	//包含“-”属于日期
					chartPartDef.get_EvaluateByDef().set_DurationEndValue(combo_EvaluateByDurationEnd.getText() + "T00:00:00");
				}else{	//属于中文时间段
					chartPartDef.get_EvaluateByDef().set_DurationEndFunction(true);
					chartPartDef.get_EvaluateByDef().set_DurationEndValue((String)combo_EvaluateByDurationEnd.getData());
				}
				
				chartPartDef.get_EvaluateByDef().set_DurationUnit((String)combo_EvaluateByDurationUnits.getData(combo_EvaluateByDurationUnits.getText()));

			}
			
			chartPartDef.get_EvaluateByDef().set_QueryFunction((QueryFunctions)combo_EvaluateByQueryFunction.getData(combo_EvaluateByQueryFunction.getText()));
			chartPartDef.get_GroupByDef().set_LimitQuery(check_ShowOnly.getSelection());
			
			if(check_ShowOnly.getSelection()){
				chartPartDef.get_GroupByDef().set_Descending((Boolean)combo_LimitDesc.getData(combo_LimitDesc.getText()));
				chartPartDef.get_GroupByDef().set_NumberOfRecordToReturn(spinner_Number.getSelection());
				if(combo_LimitOrderBy.getEnabled() && "".equals(combo_LimitOrderBy.getText().trim())){ 
					combo_LimitOrderBy.setText(combo_EvaluateByField.getItem(0));  //记录标识
					combo_LimitOrderBy.setData(combo_EvaluateByField.getItem(0), combo_EvaluateByField.getData(combo_EvaluateByField.getItem(0)));
				}
				chartPartDef.get_GroupByDef().set_OrderByFieldName((String)combo_LimitOrderBy.getData(combo_LimitOrderBy.getText()));
			}
			
			chartPartDef.get_GroupByDef().set_IncludeOthers(check_IsIncludeOthers.getSelection());
			
			if(check_IsIncludeOthers.getSelection()){
				chartPartDef.get_GroupByDef().set_OthersName(text_OtherName.getText().trim());
			}
		}
		
		chartPartDef.get_SubGroupByDef().set_Apply(false);
	}
	
	public static FieldDefHolder getFieldDefByString(String fieldName , String busObName, int filterType){
		String[] names = fieldName.split("\\.");
 		if(names.length > 2){
			fieldName = names[0] + "." + names[names.length - 1];
		}
		List<FieldDefHolder> fields = SimpleExpressSelector.createFieldDefList(busObName, filterType, true, true, false);
		for(FieldDefHolder fieldholder : fields){
			String fieldName1 = fieldholder.getFieldDef().get_QualifiedName();//+ "." + fieldholder.getFieldDef().get_Name();
			fieldName1 = fieldName1.substring(0, fieldName1.indexOf(".")) + "." + fieldholder.getFieldDef().get_Name();
			if(fieldName1.equals(fieldName)){
				return fieldholder;
			}
		}
		return null;
	}
}
