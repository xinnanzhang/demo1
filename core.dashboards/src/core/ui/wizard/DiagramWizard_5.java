package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.CCombo;

import system.Type;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.LocalizeHelper;

import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRange;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class DiagramWizard_5 extends WizardPage {

	private DiagramWizard diagramWizard;
	
	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	public static boolean limitDateTime;
	
	private Button radio_Yes;
	private Button radio_No;
	private Combo combo_DateField;
	private Combo combo_LengthOfTime;
	private Button radio_LengthOfTime;
	
	private BusinessObjectDef busObDef;
	
	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_5() {
		super("diagramWizard_5");
		setTitle("默认日期范围选择");
		setDescription("选择可选的默认时间范围。");
	}
	
	public DiagramWizard_5(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 253, 18);
		lblNewLabel.setText("\u662F\u5426\u8981\u7528\u65E5\u671F\u8303\u56F4\u8FDB\u4E00\u6B65\u9650\u5236\u7ED3\u679C\u6570\u91CF?");
		
		radio_Yes = new Button(container, SWT.RADIO);
		radio_Yes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				radio_LengthOfTime.setEnabled(true);
				combo_LengthOfTime.setEnabled(radio_LengthOfTime.getSelection());
				combo_DateField.setEnabled(true);
			}
		});
		radio_Yes.setBounds(20, 34, 45, 16);
		radio_Yes.setText("\u662F");
		
		
		radio_No = new Button(container, SWT.RADIO);
		radio_No.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				radio_LengthOfTime.setEnabled(false);
				combo_LengthOfTime.setEnabled(false);
				combo_DateField.setEnabled(false);
			}
		});
		radio_No.setText("\u5426");
		radio_No.setBounds(128, 34, 45, 16);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u50A8\u5B58\u65E5\u671F\u7684\u5B57\u6BB5?");
		label.setBounds(20, 72, 128, 18);
		
		radio_LengthOfTime = new Button(container, SWT.CHECK);
		radio_LengthOfTime.setBounds(20, 96, 118, 16);
		radio_LengthOfTime.setText("\u4F7F\u7528\u65F6\u95F4\u957F\u5EA6");
		
		combo_DateField = new Combo(container, SWT.READ_ONLY);
		combo_DateField.setBounds(241, 63, 209, 21);
		
		combo_LengthOfTime = new Combo(container, SWT.READ_ONLY);
		combo_LengthOfTime.setBounds(241, 91, 209, 21);
		
//		initDateField();
//		fillProperties();
		
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible){
			this.chartPartDef = (ChartPartDef)dashboardPartDef;
			initDateField();
			fillProperties();
		}else{
			saveProperties();
		}
		super.setVisible(visible);
	}
	
	private void fillProperties(){
		boolean flag = chartPartDef.get_DefaultDateRangeDef().get_ApplyDateRange();
		radio_No.setSelection(!flag);
		radio_Yes.setSelection(flag);
		combo_DateField.setEnabled(flag);
		radio_LengthOfTime.setEnabled(flag);
		boolean isOfTime = chartPartDef.get_DefaultDateRangeDef().get_IsLengthOfTime();
		radio_LengthOfTime.setSelection(isOfTime);
		combo_LengthOfTime.setEnabled(isOfTime&&flag);
        if(flag){
        	combo_DateField.setText(m_Library.GetBusinessObjectFieldDef(chartPartDef.get_DefaultDateRangeDef().get_DateTimeField()).get_Alias());
        }
	}
	 
	private void initDateField(){
		ICollection combinedGroupFieldDefs = null;
		
		busObDef = m_Api.get_BusObDefinitions().GetBusinessObjectDef(chartPartDef.get_BusObName());
		if (busObDef.get_ParentOfGroup())
            combinedGroupFieldDefs = this.m_Library.GetCombinedGroupFieldDefs(busObDef.get_Name());
        else
            combinedGroupFieldDefs = busObDef.get_FieldDefs();
		
		IEnumerator it = combinedGroupFieldDefs.GetEnumerator();
		while(it.MoveNext()){
			FieldDef def = (FieldDef)it.get_Current();
			if (def.get_IsDateTime()){
				combo_DateField.add(def.get_Alias());
				combo_DateField.setData(def.get_Alias(), chartPartDef.get_BusObName() + "." + def.get_Name());
			}
		}
		
		for (int i = 0; i < DateRange.values().length; i++) {
			if (DateRange.values()[i] != DateRange.None) {
				combo_LengthOfTime.add(LocalizeHelper.GetValue(Type.GetType(DateRange.class.getName()), DateRange.values()[i].name()));
				combo_LengthOfTime.setData(LocalizeHelper.GetValue(Type.GetType(DateRange.class.getName()), DateRange.values()[i].name()), DateRange.values()[i]);
				if(chartPartDef.get_DefaultDateRangeDef().get_IsLengthOfTime() && 
						DateRange.values()[i] == chartPartDef.get_DefaultDateRangeDef().get_DateRange())
					combo_LengthOfTime.setText(LocalizeHelper.GetValue(Type.GetType(DateRange.class.getName()), DateRange.values()[i].name()));
			}
		}
		combo_LengthOfTime.setText(combo_LengthOfTime.getItem(0));
		combo_DateField.setText(combo_DateField.getItem(0));
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	@Override
	public IWizardPage getNextPage() {
		if(combo_DateField.getEnabled() && (combo_DateField.getData(combo_DateField.getText()) == null || "".equals(combo_DateField.getText().trim()))){
			MessageDialog.openInformation(getShell(), "输入有误", "储存日期的字段 为空，或输入有误！");
			return this;
		}
		saveProperties();
		diagramWizard.addPage(new DiagramWizard_6(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
		return diagramWizard.getPage("diagramWizard_6");
	}
	
	private void saveProperties(){
		chartPartDef.get_DefaultDateRangeDef().set_ApplyDateRange(radio_Yes.getSelection());
		if(radio_Yes.getSelection()){
			limitDateTime = true;
			chartPartDef.get_DefaultDateRangeDef().set_IsLengthOfTime(radio_LengthOfTime.getSelection());
			if(radio_LengthOfTime.getSelection()){
				chartPartDef.get_DefaultDateRangeDef().set_DateTimeField((String)combo_DateField.getData(combo_DateField.getText()));
				if(combo_LengthOfTime.getData(combo_LengthOfTime.getText()) == null){
					chartPartDef.get_DefaultDateRangeDef().set_DateRange((DateRange)combo_LengthOfTime.getData(combo_LengthOfTime.getItem(0)));
				}else{
					chartPartDef.get_DefaultDateRangeDef().set_DateRange((DateRange)combo_LengthOfTime.getData(combo_LengthOfTime.getText()));
				}
			}
		}else{
			limitDateTime = false;
		}
	}
}
