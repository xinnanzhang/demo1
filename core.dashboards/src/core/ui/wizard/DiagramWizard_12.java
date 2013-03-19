package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;

import system.Type;
import system.Drawing.KnownColor;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.PieLabelFormat;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;
import core.ui.dialog.OutlookCalendar;

public class DiagramWizard_12 extends WizardPage {

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
	
	private Text text_PieLabelFormat;
	private Combo combo_PieLabelFormat;
	private TableCombo combo_TooltipHighLightColor;
	private TableCombo combo_TooltipBackColor;
	
	private Label label_PieLabel;
	private Label label_ToolTip;
	
	
	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_12() {
		super("diagramWizard_12");
		setTitle("图表标签和工具提示选择");
		setDescription("设置图表控件外观的功能部件，包括标签和工具提示样式");
	}

	public DiagramWizard_12(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_12(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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

		Label label = new Label(container, SWT.NONE);
		label.setTouchEnabled(true);
		label.setText("\u63A7\u4EF6\u7684\u540D\u79F0");
		label.setForeground(new Color(null, 0, 0, 255));
		label.setBounds(10, 345, 562, 41);
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setBounds(10, 10, 562, 276);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("\u997C\u56FE\u6807\u7B7E");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(10, 28, 82, 16);
		lblNewLabel_1.setText("\u683C\u5F0F:");
		
		combo_PieLabelFormat = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		combo_PieLabelFormat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onCombo_PieFormatChanged();
			}
		});
		combo_PieLabelFormat.setBounds(10, 50, 166, 21);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setText("\u683C\u5F0F\u5B57\u7B26\u4E32:");
		label_2.setBounds(10, 82, 82, 16);
		
		text_PieLabelFormat = new Text(composite, SWT.BORDER);
		text_PieLabelFormat.setBounds(10, 104, 166, 21);
		
		label_PieLabel = new Label(composite, SWT.NONE);
		label_PieLabel.setBounds(243, 10, 301, 231);
		label_PieLabel.setText("New Label");
		
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("\u5DE5\u5177\u63D0\u793A");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_1);
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setText("\u7A81\u51FA\u663E\u793A\u989C\u8272:");
		label_1.setBounds(10, 28, 82, 16);
		
		combo_TooltipHighLightColor = new TableCombo(composite_1, SWT.BORDER | SWT.READ_ONLY);
		combo_TooltipHighLightColor.setBounds(10, 50, 166, 21);
		combo_TooltipHighLightColor.setEnabled(false);	//
		
		Label label_3 = new Label(composite_1, SWT.NONE);
		label_3.setText("\u80CC\u666F\u8272:");
		label_3.setBounds(10, 82, 82, 16);
		
		label_ToolTip = new Label(composite_1, SWT.NONE);
		label_ToolTip.setText("New Label");
		label_ToolTip.setBounds(243, 10, 301, 231);
		
		combo_TooltipBackColor = new TableCombo(composite_1, SWT.BORDER | SWT.READ_ONLY);
		combo_TooltipBackColor.setBounds(10, 104, 166, 21);
		combo_TooltipBackColor.setEnabled(false);	//

		initModelView();
		if("New".equals(type) || !chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)) fillOnNew();
		else fillProperties();
		showAllChart();
	}
	
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	

	@Override
	public IWizardPage getNextPage() {
		saveProperties();
		diagramWizard.addPage(new DiagramWizard_13(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
		return diagramWizard.getPage("diagramWizard_13");
	}
	
	private void initModelView(){
		combo_PieLabelFormat.setItems(new String[]{"无", "百分比", "数值", "项目标签", "标签和百分比"});
		String[] formatData = new String[]{"None", "PercentValue", "DataValue", "ItemLabel", "Label_and_Percent"};
		for(int i = 0; i < formatData.length; i++){
			combo_PieLabelFormat.setData(combo_PieLabelFormat.getItems()[i], formatData[i]);
		}
		
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + str);
//			combo_TooltipHighLightColor.add(colorName);
//			combo_TooltipBackColor.add(colorName);
//			combo_TooltipHighLightColor.setData(colorName, str);
//			combo_TooltipBackColor.setData(colorName, str);
//			if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
//				if (str.equals(chartPartDef.get_TooltipHighLightColor().get_Name()))
//					combo_TooltipHighLightColor.setText(colorName);
//				if (str.equals(chartPartDef.get_TooltipBackColor().get_Name()))
//					combo_TooltipBackColor.setText(colorName);
//			}
//		}
		
	}
	
	private void fillOnNew(){
		combo_PieLabelFormat.setText(combo_PieLabelFormat.getItem(1));
		text_PieLabelFormat.setText(GetFunnelLabelFormatString(chartPartDef.ConverttoFunnelChartLabelStyle(
				(String)combo_PieLabelFormat.getData(combo_PieLabelFormat.getText()))));
//		combo_TooltipHighLightColor.setText("黄色");
//		combo_TooltipBackColor.setText("白色");
		ColorComboInit.Init(combo_TooltipHighLightColor, system.Drawing.Color.get_Yellow().get_Name());
		ColorComboInit.Init(combo_TooltipBackColor, system.Drawing.Color.get_White().get_Name());
//		combo_TooltipHighLightColor.setBackground(OutlookCalendar.getBackgroudColor(combo_TooltipHighLightColor));
//		combo_TooltipBackColor.setBackground(OutlookCalendar.getBackgroudColor(combo_TooltipBackColor));
		//初始化 图表
				((PiePlot)labelChart.getPlot()).setLabelGenerator(GetFunnelLabelFormatInt(chartPartDef.ConverttoFunnelChartLabelStyle(
						(String)combo_PieLabelFormat.getData(combo_PieLabelFormat.getText()))));
	}
	
	private void fillProperties(){
		ColorComboInit.Init(combo_TooltipHighLightColor, chartPartDef.get_TooltipHighLightColor().get_Name());
		ColorComboInit.Init(combo_TooltipBackColor, chartPartDef.get_TooltipBackColor().get_Name());
		
		for(int i = 0; i < combo_PieLabelFormat.getItems().length; i++){
			if(combo_PieLabelFormat.getData(combo_PieLabelFormat.getItems()[i]).equals(chartPartDef.get_PieLabelFormat().toString()))
				combo_PieLabelFormat.setText(combo_PieLabelFormat.getItem(i));
		}
		text_PieLabelFormat.setText(chartPartDef.get_PieLabelFormatString());
		
		//初始化 图表
		((PiePlot)labelChart.getPlot()).setLabelGenerator(GetFunnelLabelFormatInt(chartPartDef.ConverttoFunnelChartLabelStyle(
				(String)combo_PieLabelFormat.getData(combo_PieLabelFormat.getText()))));
	}
	
	//显示所有图片
	private void showAllChart() {
		label_PieLabel.setImage(DiagramWizard_3.initImage(labelChart, 300, 230));
		label_ToolTip.setImage(DiagramWizard_3.initImage(labelChart, 300, 230));
	}
		 
	private String GetFunnelLabelFormatString(Core.Dashboards.FunnelLabelFormat format) {
		String str = "";
		switch (format) {
		case None:
			return "";

		case DataValue:
			return "(DATA_VALUE:#0.00)";

		case ItemLabel:
			return "(ITEM_LABEL)";

		case Label_and_Percent:
			return "(ITEM_LABEL) = (PERCENT_VALUE:#0.00)%";

		case PercentValue:
			return "(PERCENT_VALUE:#0)%";
		}
		return str;
	}
	 
	private StandardPieSectionLabelGenerator GetFunnelLabelFormatInt(Core.Dashboards.FunnelLabelFormat format) {
		switch (format) {
		case None:
			return null;

		case DataValue:
			return new StandardPieSectionLabelGenerator("{1}");

		case ItemLabel:
			return new StandardPieSectionLabelGenerator("{0}");

		case Label_and_Percent:
			return new StandardPieSectionLabelGenerator("{0} = {2}");

		case PercentValue:
			return new StandardPieSectionLabelGenerator("{2}");
		}
		return null;
	}
	 
	 private void onCombo_PieFormatChanged(){
		 text_PieLabelFormat.setText(GetFunnelLabelFormatString(chartPartDef.ConverttoFunnelChartLabelStyle(
					(String)combo_PieLabelFormat.getData(combo_PieLabelFormat.getText()))));
		 ((PiePlot)labelChart.getPlot()).setLabelGenerator(GetFunnelLabelFormatInt(chartPartDef.ConverttoFunnelChartLabelStyle(
					(String)combo_PieLabelFormat.getData(combo_PieLabelFormat.getText()))));
		 showAllChart();
	 }
	 
	 
	 
	//得到可保存的color 值
	 private system.Drawing.Color getColorFromString(TableCombo combo){
		 String colorName = (String)combo.getData(combo.getText());
		 colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		 return system.Drawing.Color.FromName(colorName);
	 }
		 
	 
	 
	 private void saveProperties(){
		 chartPartDef.set_PieLabelFormatString(text_PieLabelFormat.getText());
		 chartPartDef.set_PieLabelFormat(PieLabelFormat.valueOf((String)combo_PieLabelFormat.getData(combo_PieLabelFormat.getText())));
		 chartPartDef.set_TooltipHighLightColor(getColorFromString(combo_TooltipHighLightColor));
		 chartPartDef.set_TooltipBackColor(getColorFromString(combo_TooltipBackColor));
		 
		 
		 chartPartDef.set_AxisXItemFormat(Core.Dashboards.AxisItemLabelFormat.ItemLabel);
         chartPartDef.set_AxisXItemFormatString("(ITEM_LABEL)");
         chartPartDef.set_AxisXSeriesFormat(Core.Dashboards.AxisSeriesLabelFormat.SeriesLabel);
         chartPartDef.set_AxisXSeriesFormatString("(SERIES_LABEL)");
         chartPartDef.set_AxisXOrientation(Core.Dashboards.TextOrientation.Horizontal);
         chartPartDef.set_AxisXTickMarkStyle(Core.Dashboards.AxisTickStyle.Percentage);
         chartPartDef.set_AxisXTickMarkInterval(10);
         chartPartDef.set_AxisXTickMarkPercentage(20);
         chartPartDef.set_AxisXRangeType(Core.Dashboards.AxisRangeType.Custom);
         chartPartDef.set_AxisXRangeMax(0.0);
         chartPartDef.set_AxisXRangeMin(0.0);
         chartPartDef.set_AxisYItemFormat(Core.Dashboards.AxisItemLabelFormat.DataValue);
         chartPartDef.set_AxisYItemFormatString("(DATA_VALUE:0)");
         chartPartDef.set_AxisYSeriesFormat(Core.Dashboards.AxisSeriesLabelFormat.None);
         chartPartDef.set_AxisYSeriesFormatString("");
         chartPartDef.set_AxisYOrientation(Core.Dashboards.TextOrientation.Horizontal);
         chartPartDef.set_AxisYTickMarkStyle(Core.Dashboards.AxisTickStyle.Percentage);
         chartPartDef.set_AxisYTickMarkInterval(10);
         chartPartDef.set_AxisYTickMarkPercentage(20);
         chartPartDef.set_AxisYRangeType(Core.Dashboards.AxisRangeType.Custom);
         chartPartDef.set_AxisYRangeMax(0.0);
         chartPartDef.set_AxisYRangeMin(0.0);
         chartPartDef.set_ColumnIndex(1);
         chartPartDef.set_FunnelLabelFormat(Core.Dashboards.FunnelLabelFormat.None);
         chartPartDef.set_FunnelLabelFormatString("");
         chartPartDef.set_TooltipFormat(Core.Dashboards.TooltipStyle.Custom);
         chartPartDef.set_TooltipFormatString("(SERIES_LABEL) = (DATA_VALUE:0)");
		 
	 }
}
