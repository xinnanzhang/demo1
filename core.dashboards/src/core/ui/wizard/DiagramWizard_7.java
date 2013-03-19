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

import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.TimeInterval;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import core.apploader.search.dialog.SimpleExpressSelector;
import core.apploader.search.pojo.FieldDefHolder;



public class DiagramWizard_7 extends WizardPage {

	private DiagramWizard diagramWizard;
	
	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private Button radio_Yes;
	private Button radio_No;
	private Button check_Field;
	private CCombo combo_Field;
	private CCombo combo_Interval;
	
	private boolean isDateTime;
	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_7() {
		super("diagramWizard_7");
	}
	
	public DiagramWizard_7(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		lblNewLabel.setBounds(21, 21, 147, 18);
		lblNewLabel.setText("\u662F\u5426\u521B\u5EFA\u6570\u636E\u7684\u5B50\u5206\u7EC4?");
		
		radio_Yes = new Button(container, SWT.RADIO);
		radio_Yes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				check_Field.setEnabled(true);
				check_Field.setSelection(true);
				combo_Field.setEnabled(true);
				combo_Interval.setEnabled(true);
			}
		});
		radio_Yes.setBounds(293, 21, 46, 16);
		radio_Yes.setText(" \u662F");
		
		radio_No = new Button(container, SWT.RADIO);
		radio_No.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				check_Field.setEnabled(false);
				combo_Field.setEnabled(false);
				combo_Interval.setEnabled(false);
			}
		});
		radio_No.setText(" \u5426");
		radio_No.setBounds(394, 21, 46, 16);
		
		check_Field = new Button(container, SWT.CHECK);
		check_Field.setBounds(21, 76, 106, 16);
		check_Field.setText("\u4F7F\u7528\u5B57\u6BB5\u6570\u636E");
		
		combo_Field = new CCombo(container, SWT.BORDER);
		combo_Field.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("更多值...".equals(combo_Field.getText())) {
					SimpleExpressSelector simpleES = new SimpleExpressSelector(getShell(), new String[]{chartPartDef.get_BusObName()}, "", 1, true, true, !DiagramWizard_5.limitDateTime);
					if(simpleES.open() == 0) {
						combo_Field.setText(simpleES.getFieldDefHolder().getUp_fieldDef_alias());
						FieldDef field = simpleES.getFieldDefHolder().getFieldDef();
						isDateTime = field.get_IsDateTime();
						combo_Interval.setEnabled(isDateTime);
						String fieldName = field.get_QualifiedName();
						fieldName = fieldName.substring(0, fieldName.indexOf(".")) + "." + field.get_Name();
						combo_Field.setData(simpleES.getFieldDefHolder().getUp_fieldDef_alias(), fieldName);
					}
				}
			}
		});
		combo_Field.setBounds(239, 71, 308, 21);
		combo_Field.add("更多值...");
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u95F4\u9694");
		label.setBounds(130, 115, 38, 18);
		
		combo_Interval = new CCombo(container, SWT.BORDER);
		combo_Interval.setBounds(239, 110, 131, 21);
		
		initModelView();
		fillProperties();
	}
	
	private void fillProperties(){
		if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
			if (chartPartDef.get_SubGroupByDef().get_Apply()) {
				radio_Yes.setSelection(true);
				check_Field.setSelection(true);
				String subField = chartPartDef.get_SubGroupByDef().get_FieldName();
				if (subField.indexOf(".") == -1) subField = chartPartDef.get_BusObName() + "." + subField;
				
				if (!"".equals(chartPartDef.get_SubGroupByDef().get_FieldName())) {
					FieldDefHolder field = DiagramWizard_6.getFieldDefByString(subField, chartPartDef.get_BusObName(), 1);
					combo_Field.setText(field.getUp_fieldDef_alias());
					combo_Field.setData(field.getUp_fieldDef_alias(), subField);
					combo_Interval.setText(DiagramWizard_6.DateToString(TimeInterval.valueOf(chartPartDef.get_SubGroupByDef().get_Interval())));
				}
			} else {
				radio_No.setSelection(true);
				check_Field.setEnabled(false);
				combo_Field.setEnabled(false);
				combo_Interval.setEnabled(false);
			}
		}else{
			radio_No.setSelection(true);
			check_Field.setEnabled(false);
			combo_Field.setEnabled(false);
			combo_Interval.setEnabled(false);
		}
		
	}
	
	
	private void initModelView(){
		TimeInterval[] timeInterval = TimeInterval.values();
		 for(int i = 0 ; i < timeInterval.length ; i++){
			 if (timeInterval[i] != TimeInterval.None)
            {
				 combo_Interval.add(DiagramWizard_6.DateToString(timeInterval[i]));
				 combo_Interval.setData(DiagramWizard_6.DateToString(timeInterval[i]), timeInterval[i].name());
            }
		 }
		 combo_Interval.setText("每年");
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	private boolean validate(){
		if(radio_Yes.getSelection() && ("".equals(combo_Field.getText().trim()) || combo_Field.getData(combo_Field.getText()) ==null)){
			return false;
		}
		return true;
	}
	
	@Override
	public IWizardPage getNextPage() {
		if(!validate()){
			MessageDialog.openInformation(getShell(), "输入有误", "使用字段数据为空，或输入有误！");
			return this;
		}
		saveProperties();
		if(chartPartDef.get_DefaultDateRangeDef().get_ApplyDateRange()){
			diagramWizard.addPage(new DiagramWizard_8(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_8");
		}else{
			diagramWizard.addPage(new DiagramWizard_9(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_9");
		}
	}
	
	private void saveProperties(){
		if(radio_Yes.getSelection()){
			chartPartDef.get_SubGroupByDef().set_Apply(true);
		}else{
			chartPartDef.get_SubGroupByDef().set_Apply(false);
		}
		if(check_Field.getSelection()){
			chartPartDef.get_SubGroupByDef().set_FieldName((String)combo_Field.getData(combo_Field.getText()));
			chartPartDef.get_SubGroupByDef().set_Interval((String)combo_Interval.getData(combo_Interval.getText()));
			chartPartDef.get_SubGroupByDef().set_DateTimeField(isDateTime);
		}
	}
}
