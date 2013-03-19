package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;

import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.LocalizeHelper;

import Core.Dashboards.DateRange;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ViewDef;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Spinner;

import system.Type;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class DiagramWizard_8 extends WizardPage {

	private DiagramWizard diagramWizard;
	
	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	private Table table;
	
	private Button radio_Yes;
	private Button radio_No;
	Spinner spinner_QuartersBefore;
	Spinner spinner_QuartersAfter;
	Spinner spinner_YearsBefore;
	Spinner spinner_YearsAfter;
	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_8() {
		super("diagramWizard_8");
	}
	
	public DiagramWizard_8(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		lblNewLabel.setBounds(21, 21, 313, 18);
		lblNewLabel.setText("\u662F\u5426\u5141\u8BB8\u67E5\u770B\u6B64\u56FE\u8868\u7684\u7528\u6237\u5728\u8FD0\u884C\u65F6\u9009\u62E9\u5176\u4ED6\u65E5\u671F\u8303\u56F4?");
		
		radio_Yes = new Button(container, SWT.RADIO);
		radio_Yes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.setEnabled(true);
			}
		});
		radio_Yes.setBounds(402, 19, 46, 16);
		radio_Yes.setText(" \u662F");
		
		radio_No = new Button(container, SWT.RADIO);
		radio_No.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table.setEnabled(false);
			}
		});
		radio_No.setText(" \u5426");
		radio_No.setBounds(476, 19, 46, 16);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u5982\u679C\u662F\uFF0C\u8BF7\u9009\u62E9\u8FD0\u884C\u65F6\u53EF\u7528\u7684\u9009\u9879\uFF1A");
		label.setBounds(21, 67, 203, 18);
		
		table = new Table(container, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setBounds(375, 67, 147, 136);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("\u663E\u793A");
		label_1.setBounds(21, 111, 30, 18);
		
		spinner_QuartersBefore = new Spinner(container, SWT.BORDER);
		spinner_QuartersBefore.setBounds(57, 108, 45, 21);
		spinner_QuartersBefore.setMaximum(0);
		spinner_QuartersBefore.setMinimum(-12);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setText("\u81F3");
		label_2.setBounds(106, 111, 22, 18);
		
		spinner_QuartersAfter = new Spinner(container, SWT.BORDER);
		spinner_QuartersAfter.setBounds(132, 108, 45, 21);
		spinner_QuartersAfter.setMinimum(0);
		spinner_QuartersAfter.setMaximum(12);
		
		Label label_3 = new Label(container, SWT.NONE);
		label_3.setText("\u5F53\u524D\u7684\u8D22\u653F\u5C97\u4F4D");
		label_3.setBounds(183, 111, 97, 18);
		
		Label label_4 = new Label(container, SWT.NONE);
		label_4.setText("\u663E\u793A");
		label_4.setBounds(21, 144, 30, 18);
		
		spinner_YearsBefore = new Spinner(container, SWT.BORDER);
		spinner_YearsBefore.setBounds(57, 141, 45, 21);
		spinner_YearsBefore.setMaximum(0);
		spinner_YearsBefore.setMinimum(-12);
		
		Label label_5 = new Label(container, SWT.NONE);
		label_5.setText("\u81F3");
		label_5.setBounds(106, 144, 22, 18);
		
		spinner_YearsAfter = new Spinner(container, SWT.BORDER);
		spinner_YearsAfter.setBounds(132, 141, 45, 21);
		spinner_YearsAfter.setMinimum(0);
		spinner_YearsAfter.setMaximum(12);
		
		Label label_6 = new Label(container, SWT.NONE);
		label_6.setText("\u4ECE\u76EE\u524D\u7684\u8D22\u653F\u5E74\u5EA6");
		label_6.setBounds(183, 144, 104, 18);
		
		fillProperties();
	}
	
	private void fillProperties(){
		
		int num = chartPartDef.get_DateRangeOptions().length;
		if(num > 1){
			radio_Yes.setSelection(true);
			table.setEnabled(true);
		}else{
			radio_No.setSelection(true);
			table.setEnabled(false);
		}
		for (int i = 0; i < DateRange.values().length; i++) {
			if (DateRange.values()[i] != DateRange.None) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setText(LocalizeHelper.GetValue(Type.GetType(DateRange.class.getName()), DateRange.values()[i].name()));
				tableItem.setData(DateRange.values()[i]);
				for(int j = 0; j < num; j++){
					if(DateRange.values()[i] == chartPartDef.get_DateRangeOptions()[j].get_DateRange()) tableItem.setChecked(true);
				}
			}
		}
		
		if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
			spinner_QuartersBefore.setSelection(chartPartDef.get_PrecedingFiscalQuarters());
			spinner_QuartersAfter.setSelection(chartPartDef.get_SucceedingFiscalQuarters());
			spinner_YearsBefore.setSelection(chartPartDef.get_PrecedingFiscalYears());
			spinner_YearsAfter.setSelection(chartPartDef.get_SucceedingFiscalYears());
		}
		
	}
	
	private  Boolean CheckTimeBased(ChartPartDef def) {
        Boolean flag = false;
        if(def.get_GroupByDef().get_Duration()) {
            flag = false;
        }else if(def.get_GroupByDef().get_DateTimeField()) {
            flag = false;
        }else {
            flag = false;
        }
        if(flag) return flag;
        if(def.get_SubGroupByDef().get_Duration()) return false;

        return def.get_SubGroupByDef().get_DateTimeField() && false;
    }
	
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	
	@Override
	public IWizardPage getNextPage() {
		saveProperties();
		diagramWizard.addPage(new DiagramWizard_9(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
		return diagramWizard.getPage("diagramWizard_9");
	}
	
	private void saveProperties(){
//		if(chartPartDef.get_DefaultDateRangeDef().get_ApplyDateRange() && radio_Yes.getSelection()){
		chartPartDef.ClearDateRangeDefs();
		if(table.getItems().length > 1){
			TableItem[] tableItems = table.getItems();
			for(int i = 0; i < tableItems.length; i++){
				if(tableItems[i].getChecked()) {
					ViewDef viewDef = new ViewDef(chartPartDef);
					viewDef.set_DateRange((DateRange)tableItems[i].getData());
					chartPartDef.AddDateRangeDef(viewDef);
				}
			}
		}else{
			ViewDef viewDef = new ViewDef(chartPartDef);
			viewDef.set_DateRange(DateRange.Today);
			chartPartDef.AddDateRangeDef(viewDef);
		}
	}
}
