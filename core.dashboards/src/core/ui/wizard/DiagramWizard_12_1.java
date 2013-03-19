package core.ui.wizard;

import java.awt.Font;
import java.util.HashSet;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;

import system.Type;
import system.Drawing.KnownColor;
import Core.Dashboards.AxisItemLabelFormat;
import Core.Dashboards.AxisRangeType;
import Core.Dashboards.AxisSeriesLabelFormat;
import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.TextOrientation;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;
import Siteview.Xml.DashboardPartCategory;
import core.dashboards.DataConverter;
import core.ui.dialog.OutlookCalendar;


public class DiagramWizard_12_1 extends WizardPage {

	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private String chartType;

	private DiagramWizard diagramWizard;
	private static JFreeChart labelChart;
//	-------------------Tag |　标签------------------
	private CCombo combo_TagXFormat;
	private Text combo_TagXFormatS;
	private CCombo combo_TagXOrientation;
	private CCombo combo_TagYFormat;
	private Text combo_TagYFormatS;
	private CCombo combo_TagYOrientation;
//	-------------------Series　|　系列---------------------
	private CCombo combo_SeriesXFormat;
	private CCombo combo_SeriesYFormat;
//	-------------------Tick | 勾号---------------------
	private Button radio_TickXPercent;
	private Spinner spinner_TickXPercent;
	private Button radio_TickXDataInterval;
	private Spinner spinner_TickXDataInterval;
	private Button radio_TickYPercent;
	private Spinner spinner_TickYPercent;
	private Button radio_TickYDataInterval;
	private Spinner spinner_TickYDataInterval;
//	-------------------DataRange | 数据范围----------------------------------
	private AxisRangeType axisXRangeType;
	private AxisRangeType axisYRangeType;
	private Spinner spinner_DataRangXMin;
	private Spinner spinner_DataRangXMax;
	private Spinner spinner_DataRangYMin;
	private Spinner spinner_DataRangYMax;
//	-------------------ToolTip | 工具提示------------------------------
	private TableCombo combo_ToolTipHigh;
	private TableCombo combo_ToolTipBack;
//	-------------------图表-------------------------------
	private Label TagChart;
	private Label SeriesChart;
	private Label TickChart;
	private Label DataRangeChart;
	private Label ToolTipChart;
	
	
	
	
	private MouseAdapter menuListener; //浏览图片菜单 监听

	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_12_1() {
		super("diagramWizard_12_1");
		setTitle("图表标签和工具提示选择");
		setDescription("设置图表控件外观的功能部件，包括标签和工具提示样式");
	}

	public DiagramWizard_12_1(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_12_1(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		this.labelChart = DiagramWizard_11.getLabelChart();
		this.chartType = chartPartDef.get_CategoryAsString();
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
		tabFolder.setBounds(10, 10, 562, 316);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("\u6807\u7B7E");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		
		Group grpx = new Group(composite, SWT.NONE);
		grpx.setText("\u8F74-X");
		grpx.setBounds(10, 10, 210, 135);
		
		Label lblNewLabel = new Label(grpx, SWT.NONE);
		lblNewLabel.setBounds(10, 15, 54, 16);
		lblNewLabel.setText("\u683C\u5F0F:");
		
		combo_TagXFormat = new CCombo(grpx, SWT.BORDER);
		combo_TagXFormat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTagFormatComboChanged(true);
			}
		});
		combo_TagXFormat.setBounds(10, 31, 190, 21);
		
		Label label_5 = new Label(grpx, SWT.NONE);
		label_5.setText("\u683C\u5F0F\u5B57\u7B26\u4E32:");
		label_5.setBounds(10, 55, 75, 16);
		
		combo_TagXFormatS = new Text(grpx, SWT.BORDER);
		combo_TagXFormatS.setBounds(10, 71, 190, 21);
		
		Label label_1 = new Label(grpx, SWT.NONE);
		label_1.setText("\u65B9\u5411:");
		label_1.setBounds(10, 95, 54, 16);
		
		combo_TagXOrientation = new CCombo(grpx, SWT.BORDER);
		combo_TagXOrientation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTagOrientationComboChanged(true);
			}
		});
		combo_TagXOrientation.setBounds(10, 111, 190, 21);
		
		TagChart = new Label(composite, SWT.NONE);
		TagChart.setBounds(234, 10, 310, 271);
		TagChart.setText("New Label");
		
		Group grpy_2 = new Group(composite, SWT.NONE);
		grpy_2.setText("\u8F74-Y");
		grpy_2.setBounds(10, 151, 210, 135);
		
		Label label_6 = new Label(grpy_2, SWT.NONE);
		label_6.setText("\u683C\u5F0F:");
		label_6.setBounds(10, 15, 54, 16);
		
		combo_TagYFormat = new CCombo(grpy_2, SWT.BORDER);
		combo_TagYFormat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTagFormatComboChanged(false);
			}
		});
		combo_TagYFormat.setBounds(10, 31, 190, 21);
		
		Label label_7 = new Label(grpy_2, SWT.NONE);
		label_7.setText("\u683C\u5F0F\u5B57\u7B26\u4E32:");
		label_7.setBounds(10, 55, 75, 16);
		
		combo_TagYFormatS = new Text(grpy_2, SWT.BORDER);
		combo_TagYFormatS.setBounds(10, 71, 190, 21);
		
		Label label_8 = new Label(grpy_2, SWT.NONE);
		label_8.setText("\u65B9\u5411:");
		label_8.setBounds(10, 95, 54, 16);
		
		combo_TagYOrientation = new CCombo(grpy_2, SWT.BORDER);
		combo_TagYOrientation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTagOrientationComboChanged(false);
			}
		});
		combo_TagYOrientation.setBounds(10, 111, 190, 21);
		
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("\u7CFB\u5217");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_1);
		
		Group group = new Group(composite_1, SWT.NONE);
		group.setText("\u8F74-X");
		group.setBounds(10, 10, 210, 80);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("\u683C\u5F0F:");
		label_2.setBounds(10, 25, 54, 16);
		
		combo_SeriesXFormat = new CCombo(group, SWT.BORDER);
		combo_SeriesXFormat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSeriesFormatChanged(true);
			}
		});
		combo_SeriesXFormat.setBounds(10, 48, 190, 21);
		
		Group group_1 = new Group(composite_1, SWT.NONE);
		group_1.setText("\u8F74-Y");
		group_1.setBounds(10, 100, 210, 80);
		
		Label label_3 = new Label(group_1, SWT.NONE);
		label_3.setText("\u683C\u5F0F:");
		label_3.setBounds(10, 25, 54, 16);
		
		combo_SeriesYFormat = new CCombo(group_1, SWT.BORDER);
		combo_SeriesYFormat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSeriesFormatChanged(false);
			}
		});
		combo_SeriesYFormat.setBounds(10, 48, 190, 21);
		
		SeriesChart = new Label(composite_1, SWT.NONE);
		SeriesChart.setText("New Label");
		SeriesChart.setBounds(224, 10, 320, 220);
		
		TabItem tabItem_1 = new TabItem(tabFolder, 0);
		tabItem_1.setText("\u52FE\u53F7");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_2);
		
		Group group_3 = new Group(composite_2, SWT.NONE);
		group_3.setText("\u8F74-X");
		group_3.setBounds(10, 10, 210, 90);
		
		radio_TickXPercent = new Button(group_3, SWT.RADIO);
		radio_TickXPercent.setBounds(10, 24, 80, 16);
		radio_TickXPercent.setText("\u767E\u5206\u6BD4");
		
		spinner_TickXPercent = new Spinner(group_3, SWT.BORDER);
		spinner_TickXPercent.setBounds(120, 21, 80, 21);
		spinner_TickXPercent.setMaximum(100);
		spinner_TickXPercent.setMinimum(1);
		
		radio_TickXDataInterval = new Button(group_3, SWT.RADIO);
		radio_TickXDataInterval.setText("\u6570\u636E\u95F4\u9694");
		radio_TickXDataInterval.setBounds(10, 61, 80, 16);
		
		spinner_TickXDataInterval = new Spinner(group_3, SWT.BORDER);
		spinner_TickXDataInterval.setBounds(120, 58, 80, 21);
		spinner_TickXDataInterval.setMaximum(100);
		spinner_TickXDataInterval.setMinimum(1);
		
		TickChart = new Label(composite_2, SWT.NONE);
		TickChart.setBounds(226, 10, 320, 220);
		
		Group grpy = new Group(composite_2, SWT.NONE);
		grpy.setText("\u8F74-Y");
		grpy.setBounds(10, 114, 210, 90);
		
		radio_TickYPercent = new Button(grpy, SWT.RADIO);
		radio_TickYPercent.setText("\u767E\u5206\u6BD4");
		radio_TickYPercent.setBounds(10, 24, 80, 16);
		
		spinner_TickYPercent = new Spinner(grpy, SWT.BORDER);
		spinner_TickYPercent.setBounds(120, 21, 80, 21);
		spinner_TickYPercent.setMaximum(100);
		spinner_TickYPercent.setMinimum(1);
		
		radio_TickYDataInterval = new Button(grpy, SWT.RADIO);
		radio_TickYDataInterval.setText("\u6570\u636E\u95F4\u9694");
		radio_TickYDataInterval.setBounds(10, 61, 80, 16);
		
		spinner_TickYDataInterval = new Spinner(grpy, SWT.BORDER);
		spinner_TickYDataInterval.setBounds(120, 58, 80, 21);
		spinner_TickYDataInterval.setMaximum(100);
		spinner_TickYDataInterval.setMinimum(1);
		
		TabItem tabItem_2 = new TabItem(tabFolder, 0);
		tabItem_2.setText("\u6570\u636E\u8303\u56F4");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tabItem_2.setControl(composite_3);
		
		Group group_4 = new Group(composite_3, SWT.NONE);
		group_4.setText("\u8F74-X");
		group_4.setBounds(10, 10, 210, 90);
		
		spinner_DataRangXMin = new Spinner(group_4, SWT.BORDER);
		spinner_DataRangXMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onDataRangSpinnerChanged(true, true);
			}
		});
		spinner_DataRangXMin.setBounds(100, 21, 100, 21);
		
		spinner_DataRangXMax = new Spinner(group_4, SWT.BORDER);
		spinner_DataRangXMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onDataRangSpinnerChanged(true, false);
			}
		});
		spinner_DataRangXMax.setBounds(100, 58, 100, 21);
		
		Label lblNewLabel_2 = new Label(group_4, SWT.NONE);
		lblNewLabel_2.setBounds(10, 24, 54, 16);
		lblNewLabel_2.setText("\u6700\u5C0F\uFF1A");
		
		Label label_10 = new Label(group_4, SWT.NONE);
		label_10.setText("\u6700\u5927\uFF1A");
		label_10.setBounds(10, 63, 54, 16);
		
		Group grpy_1 = new Group(composite_3, SWT.NONE);
		grpy_1.setText("\u8F74-Y");
		grpy_1.setBounds(10, 124, 210, 90);
		
		spinner_DataRangYMin = new Spinner(grpy_1, SWT.BORDER);
		spinner_DataRangYMin.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onDataRangSpinnerChanged(false, true);
			}
		});
		spinner_DataRangYMin.setBounds(100, 21, 100, 21);
		
		spinner_DataRangYMax = new Spinner(grpy_1, SWT.BORDER);
		spinner_DataRangYMax.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onDataRangSpinnerChanged(false, false);
			}
		});
		spinner_DataRangYMax.setBounds(100, 58, 100, 21);
		
		Label label_12 = new Label(grpy_1, SWT.NONE);
		label_12.setText("\u6700\u5C0F\uFF1A");
		label_12.setBounds(10, 24, 54, 16);
		
		Label label_13 = new Label(grpy_1, SWT.NONE);
		label_13.setText("\u6700\u5927\uFF1A");
		label_13.setBounds(10, 63, 54, 16);
		
		DataRangeChart = new Label(composite_3, SWT.NONE);
		DataRangeChart.setText("New Label");
		DataRangeChart.setBounds(224, 10, 320, 220);
		
		TabItem tabItem_3 = new TabItem(tabFolder, 0);
		tabItem_3.setText("\u5DE5\u5177\u63D0\u793A");
		
		Composite composite_4 = new Composite(tabFolder, SWT.NONE);
		tabItem_3.setControl(composite_4);
		
		Label lblNewLabel_3 = new Label(composite_4, SWT.NONE);
		lblNewLabel_3.setBounds(10, 28, 100, 16);
		lblNewLabel_3.setText("\u7A81\u51FA\u663E\u793A\u989C\u8272\uFF1A");
		
		combo_ToolTipHigh = new TableCombo(composite_4, SWT.BORDER | SWT.READ_ONLY);
		combo_ToolTipHigh.setBounds(10, 50, 171, 21);
		
		Label label_14 = new Label(composite_4, SWT.NONE);
		label_14.setText("\u80CC\u666F\u8272\uFF1A");
		label_14.setBounds(10, 84, 100, 16);
		
		combo_ToolTipBack = new TableCombo(composite_4, SWT.BORDER | SWT.READ_ONLY);
		combo_ToolTipBack.setBounds(10, 106, 171, 21);
		
		ToolTipChart = new Label(composite_4, SWT.NONE);
		ToolTipChart.setText("New Label");
		ToolTipChart.setBounds(224, 10, 320, 220);
		
		initModelView();
		if("New".equals(type) || !chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)) fillOnNew();
		else	fillProperties();
		initChart();
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
		LoadTagStyleList();
		LoadTagOrientationList();
		LoadSeriesStyleList();
	}
	
	private void loadTickSpinner(){
		if ("CHARTCOLUMN".equals(chartType) || "CHARTLINE".equals(chartType)){
			spinner_TickXPercent.setSelection(20);
            spinner_TickXDataInterval.setSelection(10);
            spinner_TickYPercent.setSelection(20);
            spinner_TickYDataInterval.setSelection(10);
        }
		if ("CHARTBAR".equals(chartType) || "CHARTPIPELINE".equals(chartType)){
        	spinner_TickXPercent.setSelection(20);
            spinner_TickXDataInterval.setSelection(20);
            spinner_TickYPercent.setSelection(20);
            spinner_TickYDataInterval.setSelection(10);
        }
	}
	
	
	private void fillOnNew(){
		combo_TagXFormatS.setText(getTagFormatString((AxisItemLabelFormat)combo_TagXFormat.getData(combo_TagXFormat.getText())));
		combo_TagYFormatS.setText(getTagFormatString((AxisItemLabelFormat)combo_TagYFormat.getData(combo_TagYFormat.getText())));
		radio_TickXPercent.setSelection(true);
		radio_TickYPercent.setSelection(true);
		spinner_TickXDataInterval.setEnabled(false);
		spinner_TickYDataInterval.setEnabled(false);
		loadTickSpinner();
		SetDataRangeValue();
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + str);
//			combo_ToolTipHigh.add(colorName);
//			combo_ToolTipBack.add(colorName);
//			combo_ToolTipHigh.setData(colorName, str);
//			combo_ToolTipBack.setData(colorName, str);
//		}
//		combo_ToolTipHigh.setText("黄色");
//		combo_ToolTipBack.setText("白色");
//		combo_ToolTipHigh.setBackground(OutlookCalendar.getBackgroudColor(combo_ToolTipHigh));
//		combo_ToolTipBack.setBackground(OutlookCalendar.getBackgroudColor(combo_ToolTipBack));
		
		ColorComboInit.Init(combo_ToolTipHigh, system.Drawing.Color.get_Yellow().get_Name());
		ColorComboInit.Init(combo_ToolTipBack, system.Drawing.Color.get_White().get_Name());
	}
	
	private void fillProperties(){
		
		ColorComboInit.Init(combo_ToolTipHigh, chartPartDef.get_TooltipHighLightColor().get_Name());
		ColorComboInit.Init(combo_ToolTipBack, chartPartDef.get_TooltipBackColor().get_Name());
//		-----------------------Tag | 标签   ----------------------------------
		combo_TagXFormat.setText(getTagStyleName(chartPartDef.get_AxisXItemFormat()));
		combo_TagXFormatS.setText(chartPartDef.get_AxisXItemFormatString());
		combo_TagXOrientation.setText(getTagOrientation(chartPartDef.get_AxisXOrientation()));
		
		combo_TagYFormat.setText(getTagStyleName(chartPartDef.get_AxisYItemFormat()));
		combo_TagYFormatS.setText(chartPartDef.get_AxisYItemFormatString());
		combo_TagYOrientation.setText(getTagOrientation(chartPartDef.get_AxisYOrientation()));
//		-----------------------Series | 系列---------------------------------------
		combo_SeriesXFormat.setText(getSeriesFormat(chartPartDef.get_AxisXSeriesFormat()));
		combo_SeriesYFormat.setText(getSeriesFormat(chartPartDef.get_AxisYSeriesFormat()));
//		-----------------------Tick | 勾号-------------------------------------
		if(Core.Dashboards.AxisTickStyle.Percentage == chartPartDef.get_AxisXTickMarkStyle()){
			radio_TickXPercent.setSelection(true);
			spinner_TickXDataInterval.setEnabled(false);
		}else if(Core.Dashboards.AxisTickStyle.DataInterval == chartPartDef.get_AxisXTickMarkStyle()){
			radio_TickXDataInterval.setSelection(true);
			spinner_TickXPercent.setEnabled(false);
		}else{
			radio_TickXPercent.setSelection(false);
			radio_TickXDataInterval.setSelection(false);
			spinner_TickXPercent.setEnabled(false);
			spinner_TickXDataInterval.setEnabled(false);
		}
		if(Core.Dashboards.AxisTickStyle.Percentage == chartPartDef.get_AxisYTickMarkStyle()){
			radio_TickYPercent.setSelection(true);
			spinner_TickYDataInterval.setEnabled(false);
		}else if(Core.Dashboards.AxisTickStyle.DataInterval == chartPartDef.get_AxisYTickMarkStyle()){
			radio_TickYDataInterval.setSelection(true);
			spinner_TickYPercent.setEnabled(false);
		}else{
			radio_TickYPercent.setSelection(false);
			radio_TickYPercent.setSelection(false);
			spinner_TickYPercent.setEnabled(false);
			spinner_TickYDataInterval.setEnabled(false);
		}
		spinner_TickXPercent.setSelection((int)chartPartDef.get_AxisXTickMarkPercentage());
		spinner_TickXDataInterval.setSelection((int)chartPartDef.get_AxisXTickMarkInterval());
		spinner_TickYPercent.setSelection((int)chartPartDef.get_AxisYTickMarkPercentage());
		spinner_TickYDataInterval.setSelection((int)chartPartDef.get_AxisYTickMarkInterval());
//		----------------------DataRange | 数据范围----------------------------------------
		SetDataRangeValue();	
//		----------------------Color | 颜色------------------------------------------------
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + str);
//			combo_ToolTipHigh.add(colorName);
//			combo_ToolTipBack.add(colorName);
//			combo_ToolTipHigh.setData(colorName, str);
//			combo_ToolTipBack.setData(colorName, str);
//			if (str.equals(chartPartDef.get_TooltipHighLightColor().get_Name()))
//				combo_ToolTipHigh.setText(colorName);
//			if (str.equals(chartPartDef.get_TooltipBackColor().get_Name()))
//				combo_ToolTipBack.setText(colorName);
//		}
//		combo_ToolTipHigh.setBackground(OutlookCalendar.getBackgroudColor(combo_ToolTipHigh));
//		combo_ToolTipBack.setBackground(OutlookCalendar.getBackgroudColor(combo_ToolTipBack));		
		
	}
	
	private void initChart(){
		CategoryPlot plot = (CategoryPlot) labelChart.getPlot();
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setVisible(true);
		rangeAxis.setLabelFont(new Font("simsun", Font.LAYOUT_LEFT_TO_RIGHT, 14));
		showAllChart();
//		CategoryAxis domainAxis = plot.getDomainAxis();
//		domainAxis.setVisible(true);
//		domainAxis.set
	}
	
	private void SetDataRangeValue(){
		if (DashboardPartCategory.ChartColumn.toString().toUpperCase().equals(chartType) || 
				DashboardPartCategory.ChartLine.toString().toUpperCase().equals(chartType)){
            axisXRangeType = Core.Dashboards.AxisRangeType.Automatic;
            axisYRangeType = Core.Dashboards.AxisRangeType.Custom;
			spinner_DataRangXMax.setSelection(0);
			spinner_DataRangXMin.setSelection(0);
            spinner_DataRangYMax.setSelection((int)DataConverter.MaxValueFromDataTable(DiagramWizard_3.getTable()));
            spinner_DataRangYMin.setSelection((int)DataConverter.MinValueFromDataTable(DiagramWizard_3.getTable()));
            spinner_DataRangXMax.setEnabled(false);
            spinner_DataRangXMin.setEnabled(false);
            spinner_DataRangYMax.setEnabled(true);
            spinner_DataRangYMin.setEnabled(true);
        }else if (DashboardPartCategory.ChartBar.toString().toUpperCase().equals(chartType)){
            axisXRangeType = Core.Dashboards.AxisRangeType.Custom;
            axisYRangeType = Core.Dashboards.AxisRangeType.Automatic;
        	spinner_DataRangXMax.setSelection((int)DataConverter.MaxValueFromDataTable(DiagramWizard_3.getTable()));
        	spinner_DataRangXMin.setSelection((int)DataConverter.MinValueFromDataTable(DiagramWizard_3.getTable()));
        	spinner_DataRangYMax.setSelection(0);
			spinner_DataRangYMin.setSelection(0);
			spinner_DataRangXMax.setEnabled(true);
            spinner_DataRangXMin.setEnabled(true);
            spinner_DataRangYMax.setEnabled(false);
            spinner_DataRangYMin.setEnabled(false);
        }else if (DashboardPartCategory.ChartPipeline.toString().toUpperCase().equals(chartType)){
            axisXRangeType = Core.Dashboards.AxisRangeType.Custom;
            axisYRangeType = Core.Dashboards.AxisRangeType.Automatic;
        	spinner_DataRangXMax.setSelection((int)DataConverter.MaxTotalValueFromDataTable(DiagramWizard_3.getTable()));
        	spinner_DataRangXMin.setSelection((int)DataConverter.MinValueFromDataTable(DiagramWizard_3.getTable()));
        	spinner_DataRangYMax.setSelection(0);
			spinner_DataRangYMin.setSelection(0);
			spinner_DataRangXMax.setEnabled(true);
            spinner_DataRangXMin.setEnabled(true);
            spinner_DataRangYMax.setEnabled(false);
            spinner_DataRangYMin.setEnabled(false);
        }
	}
	
	public static JFreeChart getLabelChart() {
		return labelChart;
	}
	
	//得到可保存的color 值
	 private system.Drawing.Color getColorFromString(TableCombo combo){
		 String colorName = (String)combo.getData(combo.getText());
		 colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		 return system.Drawing.Color.FromName(colorName);
	 }
		 
	 
	private void saveProperties() {
		chartPartDef.set_AxisXItemFormat((AxisItemLabelFormat)combo_TagXFormat.getData(combo_TagXFormat.getText()));
		chartPartDef.set_AxisXItemFormatString(combo_TagXFormatS.getText().trim());
		if("".equals(combo_TagXOrientation.getText())){
			chartPartDef.set_AxisXOrientation((TextOrientation)combo_TagXOrientation.getData(combo_TagXOrientation.getItem(0)));
		}else{
			chartPartDef.set_AxisXOrientation((TextOrientation)combo_TagXOrientation.getData(combo_TagXOrientation.getText()));
		}
		
		chartPartDef.set_AxisYItemFormat((AxisItemLabelFormat)combo_TagYFormat.getData(combo_TagYFormat.getText()));
		chartPartDef.set_AxisYItemFormatString(combo_TagYFormatS.getText().trim());
		if("".equals(combo_TagYOrientation.getText())){
			chartPartDef.set_AxisYOrientation((TextOrientation)combo_TagYOrientation.getData(combo_TagYOrientation.getItem(0)));
		}else{
			chartPartDef.set_AxisYOrientation((TextOrientation)combo_TagYOrientation.getData(combo_TagYOrientation.getText()));
		}
//		-----------------------Series | 系列---------------------------------------
		chartPartDef.set_AxisXSeriesFormat((AxisSeriesLabelFormat)combo_SeriesXFormat.getData(combo_SeriesXFormat.getText()));
		chartPartDef.set_AxisYSeriesFormat((AxisSeriesLabelFormat)combo_SeriesYFormat.getData(combo_SeriesYFormat.getText()));
//		-----------------------Date-----------------------------
		chartPartDef.set_AxisXRangeType(axisXRangeType);
		chartPartDef.set_AxisYRangeType(axisYRangeType);
//		-----------------------Tick | 勾号-------------------------------------
		chartPartDef.set_AxisXTickMarkStyle(radio_TickXPercent.getSelection() ? Core.Dashboards.AxisTickStyle.Percentage : Core.Dashboards.AxisTickStyle.DataInterval);
		chartPartDef.set_AxisYTickMarkStyle(radio_TickYPercent.getSelection() ? Core.Dashboards.AxisTickStyle.Percentage : Core.Dashboards.AxisTickStyle.DataInterval);
		
		chartPartDef.set_AxisXTickMarkPercentage(spinner_TickXPercent.getSelection());
		chartPartDef.set_AxisXTickMarkInterval(spinner_TickXDataInterval.getSelection());
		chartPartDef.set_AxisYTickMarkPercentage(spinner_TickYPercent.getSelection());
		chartPartDef.set_AxisYTickMarkInterval(spinner_TickYDataInterval.getSelection());
//		-------------------------color-----------------------------
		chartPartDef.set_TooltipHighLightColor(getColorFromString(combo_ToolTipHigh));
		chartPartDef.set_TooltipBackColor(getColorFromString(combo_ToolTipBack));
		
		
//        chartPartDef.set_AxisXSeriesFormatString("(SERIES_LABEL)");
//        chartPartDef.set_AxisXRangeMax(0.0);
//        chartPartDef.set_AxisXRangeMin(0.0);
//        chartPartDef.set_AxisYSeriesFormatString("");
//        chartPartDef.set_AxisYRangeMax(0.0);
//        chartPartDef.set_AxisYRangeMin(0.0);
//        chartPartDef.set_ColumnIndex(1);
//        chartPartDef.set_FunnelLabelFormat(Core.Dashboards.FunnelLabelFormat.None);
//        chartPartDef.set_FunnelLabelFormatString("");
//        chartPartDef.set_TooltipFormat(Core.Dashboards.TooltipStyle.Custom);
//        chartPartDef.set_TooltipFormatString("(SERIES_LABEL) = (DATA_VALUE:0)");
		
	}
	 
	private void LoadTagStyleList(){
		String none = core.dashboards.Res.get_Default().GetString("Chart.NoneLabelStyle");
		String item = core.dashboards.Res.get_Default().GetString("Chart.ItemLabelLabelStyle");
		String data = core.dashboards.Res.get_Default().GetString("Chart.DataValueLabelStyle");
		String labelAndData = core.dashboards.Res.get_Default().GetString("Chart.LabelAndDataLabelStyle");
		
		if(DashboardPartCategory.ChartBar.toString().toUpperCase().equals(chartType) || 
				DashboardPartCategory.ChartPipeline.toString().toUpperCase().equals(chartType)){
			combo_TagXFormat.removeAll();
			combo_TagXFormat.add(none);
			combo_TagXFormat.setData(none, AxisItemLabelFormat.None);
			combo_TagXFormat.add(data);
			combo_TagXFormat.setData(data, AxisItemLabelFormat.DataValue);
			combo_TagXFormat.setText(data);
			
			combo_TagYFormat.removeAll();
			combo_TagYFormat.add(none);
			combo_TagYFormat.setData(none, AxisItemLabelFormat.None);
			combo_TagYFormat.add(item);
			combo_TagYFormat.setData(item, AxisItemLabelFormat.ItemLabel);
			combo_TagYFormat.setText(none);
			
	     }else if(DashboardPartCategory.ChartColumn.toString().toUpperCase().equals(chartType) || 
	    		 DashboardPartCategory.ChartLine.toString().toUpperCase().equals(chartType)){
	    	combo_TagXFormat.removeAll();
			combo_TagXFormat.add(none);
			combo_TagXFormat.setData(none, AxisItemLabelFormat.None);
			combo_TagXFormat.add(item);
			combo_TagXFormat.setData(item, AxisItemLabelFormat.ItemLabel);
			combo_TagXFormat.add(data);
			combo_TagXFormat.setData(data, AxisItemLabelFormat.DataValue);
			combo_TagXFormat.add(labelAndData);
			combo_TagXFormat.setData(labelAndData, AxisItemLabelFormat.Label_and_Data);
			combo_TagXFormat.setText(item);
			
			combo_TagYFormat.removeAll();
			combo_TagYFormat.add(none);
			combo_TagYFormat.setData(none, AxisItemLabelFormat.None);
			combo_TagYFormat.add(data);
			combo_TagYFormat.setData(data, AxisItemLabelFormat.DataValue);
			combo_TagYFormat.setText(data);
			
	     }else{
	    	combo_TagXFormat.removeAll();
			combo_TagXFormat.add(none);
			combo_TagXFormat.setData(none, AxisItemLabelFormat.None);
			combo_TagXFormat.add(item);
			combo_TagXFormat.setData(item, AxisItemLabelFormat.ItemLabel);
			combo_TagXFormat.add(data);
			combo_TagXFormat.setData(data, AxisItemLabelFormat.DataValue);
			combo_TagXFormat.add(labelAndData);
			combo_TagXFormat.setData(labelAndData, AxisItemLabelFormat.Label_and_Data);
			combo_TagXFormat.setText(item);
			
			combo_TagYFormat.removeAll();
			combo_TagYFormat.add(none);
			combo_TagYFormat.setData(none, AxisItemLabelFormat.None);
			combo_TagYFormat.add(item);
			combo_TagYFormat.setData(item, AxisItemLabelFormat.ItemLabel);
			combo_TagYFormat.add(data);
			combo_TagYFormat.setData(data, AxisItemLabelFormat.DataValue);
			combo_TagYFormat.add(labelAndData);
			combo_TagYFormat.setData(labelAndData, AxisItemLabelFormat.Label_and_Data);
			combo_TagYFormat.setText(data);
	     }
	 }
	 
	 private void LoadTagOrientationList(){
		 String horiz = core.dashboards.Res.get_Default().GetString("Chart.HorizontalLabel");
		 String vertLeft = core.dashboards.Res.get_Default().GetString("Chart.VerticalLeftFacingLabel");
		 String vertRight = core.dashboards.Res.get_Default().GetString("Chart.VerticalRightFacingLabel");
		 
		 combo_TagXOrientation.removeAll();
		 combo_TagXOrientation.add(horiz);
		 combo_TagXOrientation.setData(horiz, TextOrientation.Horizontal);
		 combo_TagXOrientation.add(vertLeft);
		 combo_TagXOrientation.setData(vertLeft, TextOrientation.VerticalLeftFacing);
		 combo_TagXOrientation.add(vertRight);
		 combo_TagXOrientation.setData(vertRight, TextOrientation.VerticalRightFacing);
		 
		 combo_TagYOrientation.removeAll();
		 combo_TagYOrientation.add(horiz);
		 combo_TagYOrientation.setData(horiz, TextOrientation.Horizontal);
		 combo_TagYOrientation.add(vertLeft);
		 combo_TagYOrientation.setData(vertLeft, TextOrientation.VerticalLeftFacing);
		 combo_TagYOrientation.add(vertRight);
		 combo_TagYOrientation.setData(vertRight, TextOrientation.VerticalRightFacing);
		 
		 combo_TagXOrientation.setText(vertLeft);
     }
	 
	 private void LoadSeriesStyleList(){
		 String none = core.dashboards.Res.get_Default().GetString("Chart.NoneLabelStyle");
		 String series = core.dashboards.Res.get_Default().GetString("Chart.SeriesLabel");
		 combo_SeriesXFormat.removeAll();
		 combo_SeriesXFormat.add(none);
		 combo_SeriesXFormat.setData(none, AxisSeriesLabelFormat.None);
		 combo_SeriesXFormat.add(series);
		 combo_SeriesXFormat.setData(series, AxisSeriesLabelFormat.SeriesLabel);
		 combo_SeriesYFormat.removeAll();
		 combo_SeriesYFormat.add(none);
		 combo_SeriesYFormat.setData(none, AxisSeriesLabelFormat.None);
		 combo_SeriesYFormat.add(series);
		 combo_SeriesYFormat.setData(series, AxisSeriesLabelFormat.SeriesLabel);
         if (DashboardPartCategory.ChartBar.toString().toUpperCase().equals(chartType) || 
        		 DashboardPartCategory.ChartPipeline.toString().toUpperCase().equals(chartType)){
        	 combo_SeriesXFormat.setText(none);
        	 combo_SeriesYFormat.setText(series);
         }else{
        	 combo_SeriesXFormat.setText(series);
        	 combo_SeriesYFormat.setText(none);
         }
     }
	 
	private String getTagStyleName(AxisItemLabelFormat tagStyle) {
		switch (tagStyle) {
		case None:
			return "无";
			
		case ItemLabel:
			return "项目标签";
			
		case DataValue:
			return "数值";
			
		case Label_and_Data:
			return "标签和数据";
		}
		return "";
	}
	
	private String getTagOrientation(TextOrientation orientation){
		switch (orientation) {
		case Horizontal:
			return "水平";
			
		case VerticalLeftFacing:
			return "垂直向左";
			
		case VerticalRightFacing:
			return "垂直向右";
		}
		return "";
	 }
	 
	private String getSeriesFormat(AxisSeriesLabelFormat series) {
		switch (series) {
		case None:
			return "无";
			
		case SeriesLabel:
			return "序列标签";
		}
		return "";
	}
	
	private String getTagFormatString(AxisItemLabelFormat format){
		String str = "";
        switch (format)
        {
            case DataValue:
                return core.dashboards.Res.get_Default().GetString("Chart.DataValueFormatString");

            case ItemLabel:
                return core.dashboards.Res.get_Default().GetString("Chart.ItemLabelFormatString");

            case Label_and_Data:
                return core.dashboards.Res.get_Default().GetString("Chart.LabelDataFormatString");

            case None:
                return "";
        }
        return str;
    }
	
	private void onTagFormatComboChanged(boolean XorY){
		CategoryPlot plot = (CategoryPlot) labelChart.getPlot();
		if(XorY){
			AxisItemLabelFormat axisFormat = (AxisItemLabelFormat)combo_TagXFormat.getData(combo_TagXFormat.getText());
			combo_TagXFormatS.setText(getTagFormatString(axisFormat));
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setTickLabelsVisible(!(AxisItemLabelFormat.None == axisFormat));
		}else{
			combo_TagYFormatS.setText(getTagFormatString((AxisItemLabelFormat)combo_TagYFormat.getData(combo_TagYFormat.getText())));
		}
		showAllChart();
	}
	
	private void onTagOrientationComboChanged(boolean XorY){
		CategoryPlot plot = (CategoryPlot) labelChart.getPlot();
		if(XorY){
			TextOrientation tagOrientation = (TextOrientation) combo_TagXOrientation.getData(combo_TagXOrientation.getText());
			if (DashboardPartCategory.ChartBar.toString().toUpperCase().equals(chartType)
					|| DashboardPartCategory.ChartPipeline.toString().toUpperCase().equals(chartType)) {
				if(TextOrientation.Horizontal == tagOrientation) {
					plot.getRangeAxis().setVerticalTickLabels(false);
				}else if (TextOrientation.VerticalRightFacing == tagOrientation) {
					plot.getRangeAxis().setVerticalTickLabels(true);
				}
			}else if(DashboardPartCategory.ChartColumn.toString().toUpperCase().equals(chartType) || 
		    		 DashboardPartCategory.ChartLine.toString().toUpperCase().equals(chartType)){
				if(TextOrientation.Horizontal == tagOrientation) {
					plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
				}else if (TextOrientation.VerticalRightFacing == tagOrientation) {
					plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
				}else{
					plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
				}
			}
		}else{
			TextOrientation tagOrientation = (TextOrientation) combo_TagYOrientation.getData(combo_TagYOrientation.getText());
			if (DashboardPartCategory.ChartBar.toString().toUpperCase().equals(chartType)
					|| DashboardPartCategory.ChartPipeline.toString().toUpperCase().equals(chartType)) {
				if(TextOrientation.Horizontal == tagOrientation) {
					plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.STANDARD);
				}else if (TextOrientation.VerticalRightFacing == tagOrientation) {
					plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
				}else{
					plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
				}
			}else if(DashboardPartCategory.ChartColumn.toString().toUpperCase().equals(chartType) || 
		    		 DashboardPartCategory.ChartLine.toString().toUpperCase().equals(chartType)){
				if(TextOrientation.Horizontal == tagOrientation) {
					plot.getRangeAxis().setVerticalTickLabels(false);
				}else if (TextOrientation.VerticalRightFacing == tagOrientation) {
					plot.getRangeAxis().setVerticalTickLabels(true);
				}
			}
		}
		showAllChart();
	}
	
	//XorY(true--->X) MaxOrMin(true--->Min)
	private void onDataRangSpinnerChanged(boolean XorY, boolean MaxOrMin){
		CategoryPlot plot = (CategoryPlot) labelChart.getPlot();
		if(XorY){
			if (DashboardPartCategory.ChartBar.toString().toUpperCase().equals(chartType)
					|| DashboardPartCategory.ChartPipeline.toString().toUpperCase().equals(chartType)) {
				if (MaxOrMin) {
					plot.getRangeAxis().setLowerBound(spinner_DataRangXMin.getSelection());
				} else {
					plot.getRangeAxis().setUpperBound(spinner_DataRangXMax.getSelection());
				}
			}
		}else{
			if(DashboardPartCategory.ChartColumn.toString().toUpperCase().equals(chartType) || 
		    		 DashboardPartCategory.ChartLine.toString().toUpperCase().equals(chartType)){
				if (MaxOrMin) {
					plot.getRangeAxis().setLowerBound(spinner_DataRangYMin.getSelection());
				} else {
					plot.getRangeAxis().setUpperBound(spinner_DataRangYMax.getSelection());
				}
			}
		}
		showAllChart();
	}
	
	private void onSeriesFormatChanged(boolean XorY){
		CategoryPlot plot = (CategoryPlot) labelChart.getPlot();
		if(XorY){
			if(DashboardPartCategory.ChartColumn.toString().toUpperCase().equals(chartType) || 
		    		 DashboardPartCategory.ChartLine.toString().toUpperCase().equals(chartType)){
				plot.getDomainAxis().setTickLabelsVisible(false);
			}
		}else{
			if (DashboardPartCategory.ChartBar.toString().toUpperCase().equals(chartType)
					|| DashboardPartCategory.ChartPipeline.toString().toUpperCase().equals(chartType)) {
				plot.getDomainAxis().setTickLabelsVisible(false);
			}
		}
		showAllChart();
	}
	
	private void showAllChart(){
		Image iamge = DiagramWizard_3.initImage(labelChart, 300, 230);
		TagChart.setImage(iamge);
		SeriesChart.setImage(iamge);
		TickChart.setImage(iamge);
		DataRangeChart.setImage(iamge);
		ToolTipChart.setImage(iamge);
	}
	
}
