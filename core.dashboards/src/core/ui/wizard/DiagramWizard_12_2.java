package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;

import system.Convert;
import system.Type;
import system.Drawing.KnownColor;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;
import core.ui.dialog.OutlookCalendar;

public class DiagramWizard_12_2 extends WizardPage {
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
	private Text text_Title;
	private Text text_Tag;
	private Text text_Min;
	private Text text_Max;
	private Text text_Low1;
	private Text text_Low2;
	private Text text_Low3;
	private Text text_Limit1;
	private Text text_Limit2;
	private Text text_Limit3;
	private Text text_Value1;
	private Text text_Value2;
	private Text text_Value3;
	
	private Button check_Title;
	private Button check_Tag;
	private Button check_PassValue;
	private TableCombo combo_Color1;
	private TableCombo combo_Color2;
	private TableCombo combo_Color3;
	
	private Label label_Chart;
	private boolean toNextPage = false;
	
	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_12_2() {
		super("diagramWizard_12_2");
		setTitle("线性仪表地区");
		setDescription("线性测量区域限制，可以在这里定义。");
	}

	public DiagramWizard_12_2(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_12_2(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		
		check_Title = new Button(container, SWT.CHECK);
		check_Title.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_Title.setEnabled(check_Title.getSelection());
			}
		});
		check_Title.setBounds(10, 30, 50, 16);
		check_Title.setText("\u6807\u9898");
		
		check_Tag = new Button(container, SWT.CHECK);
		check_Tag.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_Tag.setEnabled(check_Tag.getSelection());
			}
		});
		check_Tag.setText("\u6807\u7B7E");
		check_Tag.setBounds(217, 30, 50, 16);
		
		text_Title = new Text(container, SWT.BORDER);
		text_Title.setEnabled(false);
		text_Title.setBounds(66, 30, 145, 18);
		
		text_Tag = new Text(container, SWT.BORDER);
		text_Tag.setEnabled(false);
		text_Tag.setBounds(273, 30, 145, 18);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(45, 73, 44, 16);
		lblNewLabel.setText("\u6700\u5C0F\u503C");
		
		text_Min = new Text(container, SWT.BORDER);
		text_Min.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				text_Low1.setText(text_Min.getText().trim());
			}
		});
		text_Min.setBounds(95, 70, 60, 18);
		
		Label label = new Label(container, SWT.NONE);
		label.setText("\u6700\u5927\u503C");
		label.setBounds(183, 73, 44, 16);
		
		text_Max = new Text(container, SWT.BORDER);
		text_Max.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				text_Limit3.setText(text_Max.getText().trim());
			}
		});
		text_Max.setBounds(233, 70, 60, 18);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("\u8F83\u4F4E\u7684\u503C");
		label_1.setBounds(10, 116, 60, 16);
		
		text_Low1 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Low1.setEnabled(false);
		text_Low1.setBounds(10, 151, 70, 18);
		
		text_Low2 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Low2.setEnabled(false);
		text_Low2.setBounds(10, 176, 70, 18);
		
		text_Low3 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Low3.setEnabled(false);
		text_Low3.setBounds(10, 200, 70, 18);
		
		text_Limit1 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Limit1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				text_Low2.setText(text_Limit1.getText().trim());
			}
		});
		text_Limit1.setBounds(86, 151, 70, 18);
		
		text_Limit2 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Limit2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				text_Low3.setText(text_Limit2.getText().trim());
			}
		});
		text_Limit2.setBounds(86, 176, 70, 18);
		
		text_Limit3 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Limit3.setEnabled(false);
		text_Limit3.setBounds(86, 200, 70, 18);
		
		combo_Color1 = new TableCombo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_Color1.setBounds(162, 149, 88, 21);
		
		combo_Color2 = new TableCombo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_Color2.setBounds(162, 174, 88, 21);
		
		combo_Color3 = new TableCombo(container, SWT.BORDER | SWT.READ_ONLY);
		combo_Color3.setBounds(162, 199, 88, 21);
		
		text_Value1 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Value1.setEnabled(false);
		text_Value1.setBounds(256, 151, 70, 18);
		
		text_Value2 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Value2.setEnabled(false);
		text_Value2.setBounds(256, 176, 70, 18);
		
		text_Value3 = new Text(container, SWT.BORDER | SWT.CENTER);
		text_Value3.setEnabled(false);
		text_Value3.setBounds(256, 200, 70, 18);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setText("\u4E0A\u9650\u503C");
		label_2.setBounds(90, 116, 50, 16);
		
		Label label_3 = new Label(container, SWT.NONE);
		label_3.setText("\u989C\u8272");
		label_3.setBounds(177, 116, 50, 16);
		
		check_PassValue = new Button(container, SWT.CHECK);
		check_PassValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_Value1.setEnabled(check_PassValue.getSelection());
				text_Value2.setEnabled(check_PassValue.getSelection());
				text_Value3.setEnabled(check_PassValue.getSelection());
			}
		});
		check_PassValue.setText("  \u4F20\u503C");
		check_PassValue.setBounds(256, 116, 50, 16);
		
		label_Chart = new Label(container, SWT.NONE);
		label_Chart.setBounds(332, 73, 258, 270);
		label_Chart.setText("New Label");
		
		initModelView();
		if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
			fillPorperties();
		}
		label_Chart.setImage(DiagramWizard_3.initImage(DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(),DiagramWizard_3.getChartType(chartPartDef.get_CategoryAsString()),null), 258, 270));
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible){
			toNextPage = true;
		}
		super.setVisible(visible);
	}
	
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	
	
	@Override
	public IWizardPage getNextPage() {
		if(toNextPage){
			if(validate()){	
				saveProperties();
				diagramWizard.addPage(new DiagramWizard_13(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
				return diagramWizard.getPage("diagramWizard_13");
			}else 
			{
				MessageDialog.openInformation(getShell(), "输入有误", "输入的内容不正确！");
				return this;
			}
		}else{
			return this;
		}
	}
	
	private boolean validate(){
		if("".equals(text_Limit1.getText()) || "".equals(text_Limit2.getText()) || "".equals(text_Limit3.getText()))
			return false;
		if(Integer.valueOf(text_Min.getText()) < Integer.valueOf(text_Limit1.getText()) && 
				Integer.valueOf(text_Limit1.getText()) < Integer.valueOf(text_Limit2.getText()) &&
				Integer.valueOf(text_Limit2.getText()) < Integer.valueOf(text_Max.getText()))
			return true;
		
		return false; 
	}
	
	private void initModelView(){
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + str);
//			combo_Color1.add(colorName);
//			combo_Color2.add(colorName);
//			combo_Color3.add(colorName);
//			combo_Color1.setData(colorName, str);
//			combo_Color2.setData(colorName, str);
//			combo_Color3.setData(colorName, str);
//			if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
//				if (str.equals(chartPartDef.get_LowerThresholdColor().name))
//					combo_Color1.setText(colorName);
//				if (str.equals(chartPartDef.get_MiddleThresholdColor().name))
//					combo_Color2.setText(colorName);
//				if (str.equals(chartPartDef.get_UpperThresholdColor().name))
//					combo_Color3.setText(colorName);
//			}
//		}
//		if("".equals(combo_Color1.getText()))
//			combo_Color1.setText("选择颜色...");
//		else
//			combo_Color1.setBackground(OutlookCalendar.getBackgroudColor(combo_Color1));
//		if("".equals(combo_Color2.getText()))
//			combo_Color2.setText("选择颜色...");
//		else
//			combo_Color2.setBackground(OutlookCalendar.getBackgroudColor(combo_Color2));
//		if("".equals(combo_Color3.getText()))
//			combo_Color3.setText("选择颜色...");
//		else
//			combo_Color3.setBackground(OutlookCalendar.getBackgroudColor(combo_Color3));
		
		if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType))
		{
			ColorComboInit.Init(combo_Color1, chartPartDef.get_LowerThresholdColor().get_Name());
			ColorComboInit.Init(combo_Color2, chartPartDef.get_MiddleThresholdColor().get_Name());
			ColorComboInit.Init(combo_Color3, chartPartDef.get_UpperThresholdColor().get_Name());
		}else
		{
			ColorComboInit.Init(combo_Color1, "");
			ColorComboInit.Init(combo_Color2, "");
			ColorComboInit.Init(combo_Color3, "");
		}
		
		
		text_Min.setText("0");
		text_Max.setText("100");
		text_Low1.setText("0");
		text_Limit3.setText("100");
	}
	
	private void fillPorperties(){
		if(chartPartDef.get_GaugeTitleVisible()){
            check_Title.setSelection(true);
            text_Title.setText(chartPartDef.get_GaugeTitle());
        }else{
        	check_Title.setSelection(false);
        	text_Title.setText("");
        }
        if(chartPartDef.get_GaugeLabelVisible()){
            check_Tag.setSelection(true);
            text_Tag.setText(chartPartDef.get_GaugeLabel());
        }else{
        	check_Tag.setSelection(false);
            text_Tag.setText("");
        }
        if(chartPartDef.get_GaugeLegendVisible()){
            check_PassValue.setSelection(true);
            text_Value1.setText(chartPartDef.get_GaugeLowLegend());
            text_Value2.setText(chartPartDef.get_GaugeMiddleLegend());
            text_Value3.setText(chartPartDef.get_GaugeHighLegend());
            text_Value1.setEnabled(true);
            text_Value2.setEnabled(true);
            text_Value3.setEnabled(true);
        }else{
        	check_PassValue.setSelection(false);
        	text_Value1.setText("");
            text_Value2.setText("");
            text_Value3.setText("");
            text_Value1.setEnabled(false);
            text_Value2.setEnabled(false);
            text_Value3.setEnabled(false);
        }
        text_Low1.setText(Convert.ToInt32(chartPartDef.get_Minimum()) + "");
        text_Limit3.setText(Convert.ToInt32(chartPartDef.get_Maximum()) + "");
        text_Limit1.setText(chartPartDef.get_LowerThreshold().toString());
        text_Limit2.setText(chartPartDef.get_UpperThreshold().toString());
		
       
	}
	
	
	private system.Drawing.Color getColorFromString(TableCombo combo){
		 String colorName = (String)combo.getData(combo.getText());
		 colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		 return system.Drawing.Color.FromName(colorName);
	}
	
	private void saveProperties(){
		chartPartDef.set_GaugeTitle(text_Title.getText().trim());
        chartPartDef.set_GaugeTitleVisible(check_Title.getSelection());
        chartPartDef.set_GaugeLabel(text_Tag.getText().trim());
        chartPartDef.set_GaugeLabelVisible(check_Tag.getSelection());
        chartPartDef.set_GaugeLegendVisible(check_PassValue.getSelection());
        chartPartDef.set_GaugeLowLegend(text_Value1.getText().trim());
        chartPartDef.set_GaugeMiddleLegend(text_Value2.getText().trim());
        chartPartDef.set_GaugeHighLegend(text_Value3.getText().trim());

        if(chartPartDef.get_GaugeLowLegend().equals("")){
            chartPartDef.set_GaugeLowLegend(core.dashboards.Res.get_Default().GetString("Gauge.Low"));
        }
        if(chartPartDef.get_GaugeMiddleLegend().equals("")){
            chartPartDef.set_GaugeMiddleLegend(core.dashboards.Res.get_Default().GetString("Gauge.Middle"));
        }
        if(chartPartDef.get_GaugeHighLegend().equals("")){
            chartPartDef.set_GaugeHighLegend(core.dashboards.Res.get_Default().GetString("Gauge.High"));
        }
        chartPartDef.set_LowerThreshold(Integer.valueOf(text_Limit1.getText()));
        chartPartDef.set_UpperThreshold(Integer.valueOf(text_Limit2.getText()));
        chartPartDef.set_Minimum(Integer.valueOf(text_Low1.getText()));
        chartPartDef.set_Maximum(Integer.valueOf(text_Limit3.getText()));
        chartPartDef.set_LowerThresholdColor(getColorFromString(combo_Color1));
        chartPartDef.set_MiddleThresholdColor(getColorFromString(combo_Color2));
        chartPartDef.set_UpperThresholdColor(getColorFromString(combo_Color3));
        chartPartDef.set_RefreshFrequency(100);
		
	}
	
}
