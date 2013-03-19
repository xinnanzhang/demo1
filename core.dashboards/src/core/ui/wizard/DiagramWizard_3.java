package core.ui.wizard;

import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;

import Siteview.ColorResolver;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.DashboardPartCategory;
import Siteview.Xml.XmlDashboardPartCategory;

import Core.Dashboards.DashboardPartDef;

import core.dashboards.ChartBarPartControl;
import core.dashboards.ChartPipelinePartControl.CustomBarRenderer1;

import siteview.windows.forms.ConvertUtil;
import system.ClrDouble;
import system.Convert;
import system.Type;
import system.Data.DataColumn;
import system.Data.DataRow;
import system.Data.DataTable;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DiagramWizard_3 extends WizardPage {

	private String type;	//操作类型----1.新建 2.编辑 3.复制
	private String linkTo;  //关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); //保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private static ISiteviewApi m_Api;
	
//	private static ChartPartDef chartPartDef;
	private DiagramWizard diagramWizard;
	private JFreeChart m_ultraChart;
	private static Label charImage; 
	private static DashboardPartCategory chartType;
	
	private Button radio_Bar;
	private Button radio_Column;
	private Button radio_Line;
	private Button radio_Pie;
	private Button radio_PieLine;
	private Button radio_GaugeLinear;
	private Button radio_Speedometer;
	
	private boolean toNextPage = false;
	
	public static String EidtChartType;
	
	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_3() {
		super("diagramWizard_3");
		setTitle("图表类型");
		setDescription("有 8 种类型的图表：条形图、柱形图、折线图、饼图和标准度量图。饼图和标准度量图不适用于基于时间的类别。");
	}
	
	public DiagramWizard_3(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_3(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
		this();
		diagramWizard = dw;
		this.dashboardPartDef = dashboardPartDef;
//		chartPartDef = (ChartPartDef)dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.Categoryset = Categoryset;
		this.type = type;
		EidtChartType = dashboardPartDef.get_CategoryAsString();
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		setControl(container);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(20, 30, 66, 17);
		lblNewLabel.setText("\u56FE\u8868\u7C7B\u578B\uFF1A");
		
		radio_Bar = new Button(container, SWT.RADIO);
		radio_Bar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageRaidoSelected(DashboardPartCategory.ChartBar);
			}
		});
		radio_Bar.setText("\u6761\u5F62\u56FE");
		radio_Bar.setBounds(20, 70, 70, 16);
		
		radio_Column = new Button(container, SWT.RADIO);
		radio_Column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageRaidoSelected(DashboardPartCategory.ChartColumn);
			}
		});
		radio_Column.setText("\u67F1\u5F62\u56FE");
		radio_Column.setBounds(20, 100, 70, 16);
		
		radio_Line = new Button(container, SWT.RADIO);
		radio_Line.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageRaidoSelected(DashboardPartCategory.ChartLine);
			}
		});
		radio_Line.setText("\u6298\u7EBF\u56FE");
		radio_Line.setBounds(20, 130, 70, 16);
		
		radio_Pie = new Button(container, SWT.RADIO);
		radio_Pie.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageRaidoSelected(DashboardPartCategory.ChartPie);
			}
		});
		radio_Pie.setText("\u997C\u56FE");
		radio_Pie.setBounds(20, 160, 70, 16);
		
		radio_PieLine = new Button(container, SWT.RADIO);
		radio_PieLine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageRaidoSelected(DashboardPartCategory.ChartPipeline);
			}
		});
		radio_PieLine.setText("\u7BA1\u9053\u56FE");
		radio_PieLine.setBounds(20, 190, 70, 16);
		
		Button button_5 = new Button(container, SWT.RADIO);
		button_5.setText("\u6F0F\u6597\u56FE");
		button_5.setBounds(20, 220, 70, 16);
		button_5.setEnabled(false);
		
		radio_GaugeLinear = new Button(container, SWT.RADIO);
		radio_GaugeLinear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageRaidoSelected(DashboardPartCategory.GaugeLinear);
			}
		});
		radio_GaugeLinear.setText("\u6E29\u5EA6\u8BA1");
		radio_GaugeLinear.setBounds(20, 250, 70, 16);
		
		radio_Speedometer = new Button(container, SWT.RADIO);
		radio_Speedometer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageRaidoSelected(DashboardPartCategory.Speedometer);
			}
		});
		radio_Speedometer.setText("\u901F\u5EA6\u8BA1");
		radio_Speedometer.setBounds(20, 280, 70, 16);
		
		charImage = new Label(container, SWT.BORDER);
 		charImage.setBounds(147, 29, 400, 300);
		
 		fillProperties();
 		charImage.setImage(initImage(getChartImage (getTable(), getChartType(dashboardPartDef.get_CategoryAsString()), null), 400, 300));
	}
	
	private void onImageRaidoSelected(DashboardPartCategory dashboardPartCategory)
	{
		if(chartType != dashboardPartCategory)
		{
			chartType = dashboardPartCategory;
			charImage.setImage(initImage(getChartImage (getTable(), chartType, null), 400, 300));
		}
	}
	
	
	public boolean canFlipToNextPage() {
		return true;
	}
	
	public IWizardPage getNextPage() {
		if(toNextPage){
			saveProperties();
			IWizardPage nextPage = diagramWizard.getPage("diagramWizard_4");
			if(nextPage != null){
				((DiagramWizard_4)nextPage).setDashboardPartDef(dashboardPartDef);
				return nextPage;
			}
			diagramWizard.addPage(new DiagramWizard_4(diagramWizard, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_4");
		}else{
			return this;
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible){
			toNextPage = true;
		}
		super.setVisible(visible);
	}
	
	private static Paint[] getPaints(){
		Paint[] colors = new Paint[10];
		String colorSetName = "Core.ChartColorTheme.BrowserSafeI";
		 colors[0] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color1")));
		 colors[1] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color2")));
		 colors[2] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color3")));
		 colors[3] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color4")));
		 colors[4] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color5")));
		 colors[5] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color6")));
		 colors[6] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color7")));
		 colors[7] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color8")));
		 colors[8] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color9")));
		 colors[9] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color10")));
		 return colors;
	}
	
	
	public static Color[] createPaint() {
		Color[] colors = new Color[10];
		String colorSetName = "Core.ChartColorTheme.BrowserSafeI";
        colors[0] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color1"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[1] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color2"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[2] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color3"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[3] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color4"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[4] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color5"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[5] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color6"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[6] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color7"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[7] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color8"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[8] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color9"))); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[9] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(m_Api.get_SettingsService().GetSettingAsString(colorSetName + ".Color10"))); 
        return colors;
    }
	
	
	/**
	 * 生成图表控件
	 * @param table	
	 * @param chartType
	 * @param chartPartDef
	 * @param paints
	 * @return
	 */
	public static JFreeChart getChartImage(DataTable table, DashboardPartCategory chartType, Paint[] paints){
		JFreeChart m_ultraChart = null;
		if (chartType == DashboardPartCategory.ChartPie){
			DefaultPieDataset dataset = new DefaultPieDataset();
			for (int i = 0; i < table.get_Rows().get_Count(); i++) {
				DataRow dr = table.get_Rows().get_Item(i);
				String s1 = (String) dr.get_Item(0);
				double v = getDoubleValue(dr.get_Item(1));
				dataset.setValue(s1, v);
			}
			m_ultraChart = ChartFactory.createPieChart("", dataset, true, true, false);
			
			PiePlot plot = (PiePlot) m_ultraChart.getPlot();
			plot.setBackgroundPaint(Color.white);//ConvertUtil.toAwtColor(system.Drawing.Color.FromName("White"))
    		plot.setLabelFont(new Font("simsun", Font.PLAIN, 14));
    		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}"));
    		plot.setStartAngle(5);

			Paint[] colors = paints == null ? getPaints() : paints;
			for (int i = 0; i < dataset.getItemCount(); i++) {
				plot.setSectionPaint(dataset.getKey(i), colors[i% colors.length]);
			}
			
			
		}else if(chartType == DashboardPartCategory.ChartBar){
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i = 0; i < table.get_Rows().get_Count();i++){
            	DataRow dr = table.get_Rows().get_Item(i);
            	for(int j = 1; j < table.get_Columns().get_Count();j++){
            		DataColumn dc = table.get_Columns().get_Item(j);
            		String s1 = (String)dr.get_Item(0);
            		String s2 = dc.get_ColumnName();
            		double v = getDoubleValue(dr.get_Item(dc));
            		dataset.addValue(v, s1, s2);
            	}
            }
			m_ultraChart = ChartFactory.createBarChart("", null, "", dataset, PlotOrientation.HORIZONTAL, true, true, false);
			CategoryPlot plot = (CategoryPlot) m_ultraChart.getPlot();
    		
    		plot.setBackgroundPaint(Color.white);
    		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    		rangeAxis.setLabelFont(new Font("simsun", Font.PLAIN, 14));
    		
    		CategoryAxis domainAxis = plot.getDomainAxis();
    		domainAxis.setLabelFont(new Font("simsun", Font.CENTER_BASELINE, 14));
    		domainAxis.setTickLabelFont(new Font("simsun", Font.PLAIN, 12));
    		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
//    		domainAxis.setVisible(true);//Y坐标 线与数值 显示
//    		domainAxis.setTickLabelsVisible(false);
//    		domainAxis.setUpperMargin(0.2);

//    		plot.getRangeAxis().setTickLabelsVisible(true); //X坐标数值显示
    		plot.getRangeAxis().setVerticalTickLabels(true);//true ----- X坐标数值 垂直向右显示；false 水平
//    		plot.getRangeAxis().setVisible(true); //X坐标 线与数值 显示
//    		plot.getRangeAxis().setTickMarkOutsideLength(5);
//    		plot.getRangeAxis().centerRange(0);//图表距离0 的距离
//    		plot.getRangeAxis().setAutoTickUnitSelection(true);
//    		plot.getRangeAxis().setFixedAutoRange(32);
//    		plot.getRangeAxis().setLowerBound(10);//X 原点值
//    		plot.getRangeAxis().setUpperBound(32);//X 终点值
//    		plot.getRangeAxis().setUpperMargin(2);//X 值间隔 2--->10 最小为5
//    		plot.getRangeAxis().getUpperBound();
    		Paint[] colors = paints == null ? getPaints() : paints;
    		BarRenderer renderer = new ChartBarPartControl.CustomBarRenderer(colors);
    		plot.setRenderer(renderer);
     		renderer.setDrawBarOutline(true);
     		renderer.setBarPainter(new StandardBarPainter());
     		
     		
		}else if(chartType == DashboardPartCategory.ChartColumn){
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i = 0; i < table.get_Rows().get_Count();i++){
            	DataRow dr = table.get_Rows().get_Item(i);
            	for(int j = 1; j < table.get_Columns().get_Count();j++){
            		DataColumn dc = table.get_Columns().get_Item(j);
            		String s1 = (String)dr.get_Item(0);
            		String s2 = dc.get_ColumnName();
            		double v = getDoubleValue(dr.get_Item(dc));
            		dataset.addValue(v, s1, s2);
            	}
            }
			m_ultraChart = ChartFactory.createBarChart("", null, "", dataset, PlotOrientation.VERTICAL, true, true, false);
            
    		m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
    		CategoryPlot plot = (CategoryPlot) m_ultraChart.getPlot();
    		plot.setBackgroundPaint(Color.white);

    		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    		rangeAxis.setLabelFont(new Font("simsun", Font.PLAIN, 14));
    		
    		CategoryAxis domainAxis = plot.getDomainAxis();
    		domainAxis.setLabelFont(new Font("simsun", Font.CENTER_BASELINE, 14));
    		domainAxis.setTickLabelFont(new Font("simsun", Font.PLAIN, 12));
    		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);//设置x轴columnKey方向,因横着显示不完全.设置成垂直
    		plot.setDomainAxis(domainAxis); 
    		
    		Paint[] colors = paints == null ? getPaints() : paints;
    		BarRenderer renderer = new ChartBarPartControl.CustomBarRenderer(colors);
            plot.setRenderer(renderer);
    		renderer.setDrawBarOutline(false);
    		renderer.setBarPainter(new StandardBarPainter());
    		
    		
		}else if(chartType == DashboardPartCategory.ChartLine){
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i = 0; i < table.get_Rows().get_Count();i++){
            	DataRow dr = table.get_Rows().get_Item(i);
            	for(int j = 1; j < table.get_Columns().get_Count();j++){
            		DataColumn dc = table.get_Columns().get_Item(j);
            		String s1 = (String)dr.get_Item(0);
            		String s2 = dc.get_ColumnName();
            		double v = getDoubleValue(dr.get_Item(dc));
            		dataset.addValue(v, s1, s2);
            	}
            }
			m_ultraChart = ChartFactory.createLineChart("", null, "", dataset, PlotOrientation.VERTICAL, true, true, false);
    		m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
    		
    		CategoryPlot plot = (CategoryPlot) m_ultraChart.getPlot();
    		plot.setBackgroundPaint(Color.white);
    		
    		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    		rangeAxis.setLabelFont(new Font("simsun", Font.PLAIN, 14));
    		
    		CategoryAxis domainAxis = plot.getDomainAxis();
    		domainAxis.setLabelFont(new Font("simsun", Font.CENTER_BASELINE, 14));
    		domainAxis.setTickLabelFont(new Font("simsun", Font.PLAIN, 12));
    		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);//设置x轴columnKey方向,因横着显示不完全.设置成垂直
    		plot.setDomainAxis(domainAxis); 

    		Paint[] colors = paints == null ? getPaints() : paints;
    	    LineAndShapeRenderer renderder = (LineAndShapeRenderer)plot.getRenderer();
    	    renderder.setBaseToolTipGenerator(new CategoryToolTipGenerator(){
				@Override
				public String generateToolTip(CategoryDataset db, int i, int j) {
					return db.getColumnKey(j) + " = " + db.getValue(i, j);
				}});
    	    renderder.setBaseShapesVisible(true);
    	    renderder.setDrawOutlines(true);
    	    renderder.setUseFillPaint(true);
    	    renderder.setBaseFillPaint(Color.white);	
    	    for(int i = 0 ; i<dataset.getRowCount(); i++){
    	    	renderder.setSeriesPaint(i, colors[i % colors.length]);
	    	    renderder.setSeriesStroke(i, new BasicStroke(3.0F));
	    	    renderder.setSeriesOutlineStroke(i, new BasicStroke(2.0F));
	    	    renderder.setSeriesShape(i, new Ellipse2D.Double(-5.0D, -5.0D, 10.0D, 10.0D));
    	    }
    	    
    	    
		}else if(chartType == DashboardPartCategory.ChartPipeline){
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
			for(int i = 0; i < table.get_Rows().get_Count();i++){
            	DataRow dr = table.get_Rows().get_Item(i);
            	for(int j = 1; j < table.get_Columns().get_Count();j++){
            		DataColumn dc = table.get_Columns().get_Item(j);
            		String s1 = (String)dr.get_Item(0);
            		String s2 = dc.get_ColumnName();
            		double v = getDoubleValue(dr.get_Item(dc));
            		dataset.addValue(v, s2, s1);
            	}
            }
			
			 m_ultraChart = ChartFactory.createStackedBarChart("", "", "", dataset, PlotOrientation.HORIZONTAL, true, true, false);
			/*----------设置消除字体的锯齿渲染（解决中文问题）--------------*/ 
			m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
			
			m_ultraChart.setBackgroundPaint(Color.white);
			m_ultraChart.getLegend().setItemFont(new Font("simsun", Font.CENTER_BASELINE, 12));
			m_ultraChart.getLegend().setPosition(RectangleEdge.RIGHT);
			
			CategoryPlot plot = (CategoryPlot)m_ultraChart.getPlot();
			plot.setBackgroundPaint(Color.white);
			
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			rangeAxis.setLabelFont(new Font("simsun", Font.PLAIN, 14));
			
			CategoryAxis domainAxis = plot.getDomainAxis();
			domainAxis.setLabelFont(new Font("simsun", Font.CENTER_BASELINE, 14));
			domainAxis.setTickLabelFont(new Font("simsun", Font.PLAIN, 12));
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);//设置x轴columnKey方向,因横着显示不完全.设置成垂直
			plot.setDomainAxis(domainAxis);
			
			Paint[] colors = getPaints();
			if(dataset.getRowCount()>1){
			//双列纵向柱型图时,图柱颜色特殊处理.
				CustomBarRenderer1 renderer1 = new CustomBarRenderer1();
				for(int i = 0; i < dataset.getRowCount(); i++){
					renderer1.setSeriesPaint(i, colors[i % colors.length]);
				}
				renderer1.setDrawBarOutline(true);
				renderer1.setBarPainter(new StandardBarPainter());
				plot.setRenderer(renderer1);
			}

			
		}else if(chartType == DashboardPartCategory.GaugeLinear){
//			double Maximum = 0.0;
        	DefaultValueDataset dataset = new DefaultValueDataset();
//        	Maximum = super.get_ChartPartDef().get_Maximum();
//            if (Convert.ToInt32(getTable().get_Rows().get_Item(0).get_Item(0)) > Convert.ToInt32(super.get_ChartPartDef().get_Maximum())){
//                Maximum = Convert.ToInt32(((DataTable)dt).get_Rows().get_Item(0).get_Item(0));
//            }
            dataset.setValue(Convert.ToInt32(getTable().get_Rows().get_Item(0).get_Item(1)));
            ThermometerPlot plot = new ThermometerPlot(dataset);
            m_ultraChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            /*----------设置消除字体的锯齿渲染（解决中文问题）--------------*/ 
            m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
            m_ultraChart.getLegend().setItemFont(new Font("simsun", Font.CENTER_BASELINE, 12));
            m_ultraChart.getLegend().setPosition(RectangleEdge.RIGHT);
            
            plot.setBackgroundPaint(Color.white);
            plot.setThermometerStroke(new BasicStroke(2.0f));
            plot.setThermometerPaint(Color.lightGray);
            
            plot.setOutlinePaint(null);
            plot.setGap(3);
            plot.setUnits(0);
            plot.setFollowDataInSubranges(true);
            //设置区间显示色
            Double low = ((system.ClrDouble)getTable().get_Rows().get_Item(0).get_Item(1)).getValue();
            Double mid = ((system.ClrDouble)getTable().get_Rows().get_Item(0).get_Item(2)).getValue();
            Double upp = ((system.ClrDouble)getTable().get_Rows().get_Item(0).get_Item(3)).getValue();
            
            plot.setRange(0.0, 100.0);
            plot.setSubrange(0, 0.0, low);
            plot.setSubrangePaint(0, Color.lightGray);
            plot.setSubrange(1, low, mid);
            plot.setSubrangePaint(1, Color.cyan);
            plot.setSubrange(2, mid, upp);
            plot.setSubrangePaint(2, Color.blue);
			
            
		}else if(chartType == DashboardPartCategory.Speedometer){
			
			DefaultValueDataset dataset = new DefaultValueDataset();
            dataset.setValue(Convert.ToInt32(getTable().get_Rows().get_Item(0).get_Item(1)));
            //整体样式
            DialPlot plot = new DialPlot(dataset);	//数据集
            m_ultraChart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            /*----------设置消除字体的锯齿渲染（解决中文问题）--------------*/ 
            m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
            //设置显示框架
            StandardDialFrame sdf = new StandardDialFrame();
            sdf.setBackgroundPaint(Color.gray);
            sdf.setForegroundPaint(Color.black);
            plot.setDialFrame(sdf);
            plot.setBackground(new DialBackground());
            
            //设置图区背景区域 无法出现渐变色
        	Color a_s = new Color(0, 128, 128);
        	Color a_e = new Color(122, 150, 233);
        	GradientPaint localGradientPaint = new GradientPaint(new Point(), a_s, new Point(), a_e);
            DialBackground localDialBackground = new DialBackground(localGradientPaint);
            localDialBackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
            plot.setBackground(localDialBackground);
            
            //仪表中心文字说明
            DialTextAnnotation localDialTextAnnotation = new DialTextAnnotation("");
            localDialTextAnnotation.setFont(new Font("Dialog", 1, 12));
            localDialTextAnnotation.setRadius(0.7D);
            plot.addLayer(localDialTextAnnotation);
            
            //表盘中心数值显示
            DialValueIndicator localDialValueIndicator = new DialValueIndicator(0);
            localDialValueIndicator.setFont(new Font("Dialog", 1, 12));		//中心数值字体
            localDialValueIndicator.setOutlinePaint(Color.white);			//中心数值外框颜色
            localDialValueIndicator.setPaint(Color.red);					//中心数值颜色
            localDialValueIndicator.setBackgroundPaint(Color.white);
            plot.addLayer(localDialValueIndicator);
            
            //设置区间显示色 起始区间
            StandardDialRange localStandardDialRange = new StandardDialRange(0, 22.0, Color.blue);
            StandardDialRange localStandardDialRange1 = new StandardDialRange(22.0, 60, Color.yellow);
            StandardDialRange localStandardDialRange2 = new StandardDialRange(60, 100, Color.red);
            plot.addLayer(localStandardDialRange);
            plot.addLayer(localStandardDialRange1);
            plot.addLayer(localStandardDialRange2);
                
            //表盘初始化
            StandardDialScale localStandardDialScale = new StandardDialScale(0.0, 100.0, -120.0D, -300.0D, 10.0D, 9);
            localStandardDialScale.setMajorTickIncrement(10); 					//数值显示间隔
            localStandardDialScale.setMinorTickCount(9);						//刻度数量
            localStandardDialScale.setTickRadius(0.88D);						//刻度表的偏移
            localStandardDialScale.setTickLabelOffset(0.15D);					//刻度值的偏移
            localStandardDialScale.setTickLabelFont(new Font("Dialog", 0, 12));	//刻度值字体
            localStandardDialScale.setMajorTickPaint(Color.black);				//刻度颜色
            localStandardDialScale.setTickLabelPaint(Color.black);				//刻度值颜色
            plot.addScale(0, localStandardDialScale);
            
            //中心指针区
//            plot.addPointer(new DialPointer.Pin());
            DialPointer.Pointer localPointer = new DialPointer.Pointer();
            DialCap localDialCap = new DialCap();
            localDialCap.setFillPaint(Color.black);
            localDialCap.setOutlinePaint(Color.black);
            localPointer.setOutlinePaint(Color.black);
            localPointer.setFillPaint(Color.black);
            plot.addPointer(localPointer);
            plot.setCap(localDialCap);
            
            
            plot.setBackgroundPaint(Color.white);
            
            
        }else if(chartType == DashboardPartCategory.ChartFunnel){
        	
//        	String str1= "";
//    		try {
//	    		URL url2 = Activator.getDefault().getBundle().getEntry("META-INF/MANIFEST.MF");
//	    		System.out.println("url2:"+url2);
//	    		System.out.println("FileLocator.toFileURL(url2):"+FileLocator.toFileURL(url2));
//	    		
//	    		str1 = FileLocator.toFileURL(url2).toString(); //截去一些前面6个无用的字符
//	    		System.out.println(str1);
//	    		str1=str1.substring(6,str1.length());
//	    		str1=str1.replaceAll("%20", " ");
//	    		System.out.println(str1);
//	    		int num1 = str1.indexOf("META-INF/MANIFEST.MF");
//	    		str1=str1.substring(0, num1);
//	    		System.out.println(str1);
//    		} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            //得到结果集
//    		Color[] colors = createPaint();
//    		
//            XMLUtil xml = new XMLUtil();
//    		Element graph = xml.addRoot("graph");
////    		xml.addAttribute(graph, "basefontsize", "12");
//    		xml.addAttribute(graph, "decimalPrecision", "0");// 小数精确度，0为精确到个位
//    		xml.addAttribute(graph, "showValues", "0");// 在报表上不显示数值
//    		xml.addAttribute(graph, "slicingDistance","4");
//    		xml.addAttribute(graph, "isSliced","1");
//    		xml.addAttribute(graph, "is2D","1");
//    		xml.addAttribute(graph, "showPlotBorder","1");
//    		xml.addAttribute(graph, "plotBorderThickness","1");
//    		Color plotBorderColor = Color.black;
//    		String plotBorderColor_rgb = Integer.toHexString(plotBorderColor.getRGB());
//    		plotBorderColor_rgb = plotBorderColor_rgb.substring(2, plotBorderColor_rgb.length());
//    		xml.addAttribute(graph, "plotBorderColor",plotBorderColor_rgb);
//    		xml.addAttribute(graph, "streamlinedData","0");
//    		//caption='Sales distribution by Employee' subCaption='Jan 07 - Jul 07' 
//    		int zf=0;
//    		for(int i = 0; i < table.get_Rows().get_Count();i++){
//            	DataRow dr = table.get_Rows().get_Item(i);
//            	for(int j = 1; j < table.get_Columns().get_Count();j++){
//            		DataColumn dc = table.get_Columns().get_Item(j);
//            		String s1 = (String)dr.get_Item(0);
//            		String s2 = dc.get_ColumnName();
//            		double v = getDoubleValue(dr.get_Item(dc));
//            		if(v>0){
//            			Element set2 = xml.addNode(graph, "set");
//	            		set2.addAttribute("name", s1);
//	            		set2.addAttribute("label", s1);
//	            		set2.addAttribute("value",String.valueOf(v));
//	            		Color this_c = colors[zf % colors.length];
//	            		String rgb = Integer.toHexString(this_c.getRGB());
//	            		rgb = rgb.substring(2, rgb.length());
//	            		set2.addAttribute("color", rgb);
//	            		//增加单击事件
//	            		//set2.addAttribute("link", "onClick()");
//	            		zf++;
//            		}
//            	}
//            }
//
//    		//产生FunnelChart到Browser上面 没网络时,不影响RCP显示
//    		String strChartCode = "";
//    		if(isConnect("http://www.fusioncharts.com/").equalsIgnoreCase("ok")){
//    			strChartCode = FusionChartsCreator.createChartHTML("http://www.fusioncharts.com/demos/gallery/charts/widgets/Funnel.swf", "",xml.getXML().replaceAll("\"", "'"),"funnel", 400, 320, false);
//    		}else{
//    			strChartCode=FusionChartsCreator.createChart(str1+"src/core/dashboards/funnelChart/FusionCharts.js",str1+"src/core/dashboards/funnelChart/Funnel.swf", "",xml.getXML().replaceAll("\"", "'"),"funnel", 400, 320, false, false);
//    		}
//    		System.out.println(strChartCode);
//    		Browser fun = new Browser(null, SWT.NONE);
//    		fun.setText(strChartCode);
        	
        }
//		m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
		m_ultraChart.setBackgroundPaint(Color.white);
        m_ultraChart.setBorderVisible(false);
		
		return m_ultraChart;
	}
	
	/**
	 * 由图表控件生成图片
	 * @param m_ultraChart 
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image initImage(JFreeChart m_ultraChart, int width, int height){
		Image image = null;
		ChartRenderingInfo chartReInfo = new ChartRenderingInfo();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayInputStream bis = null;
		try {
			ChartUtilities.writeChartAsPNG(bos, m_ultraChart, width, height, chartReInfo);
			image = new Image(Display.getCurrent(), bis = new ByteArrayInputStream(bos.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		charImage.setToolTipText("Chart");
//		charImage.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
		return image;
	}
	
	public static DataTable getTable(){
		
		ClrDouble c1 = new ClrDouble();
		c1.setValue(22.0);
		ClrDouble c2 = new ClrDouble();
		c2.setValue(30.2);
		ClrDouble c3 = new ClrDouble();
		c3.setValue(25.4);
		ClrDouble c4 = new ClrDouble();
		c4.setValue(32.2);
		ClrDouble c5 = new ClrDouble();
		c5.setValue(14.8);
		ClrDouble c6 = new ClrDouble();
		c6.setValue(19.5);
		ClrDouble c7 = new ClrDouble();
		c7.setValue(25.0);
		ClrDouble c8 = new ClrDouble();
		c8.setValue(19.6);
		ClrDouble c9 = new ClrDouble();
		c9.setValue(20.3);
		
		Object[] objArray = new Object[] { new Object[] { "Row 0", c1, c2, c3 }, new Object[] { "Row 1", c4, c5, c6 }, new Object[] { "Row 2", c7, c8, c9 } };
		DataTable table = new DataTable("DragonFlow Solutions");
        DataColumn column = null;
        column = new DataColumn("Sample", Type.GetType(String.class.getName()));
        table.get_Columns().Add(column);
        column = new DataColumn("C1", Type.GetType(Double.class.getName()));
        table.get_Columns().Add(column);
        column = new DataColumn("C2", Type.GetType(Double.class.getName()));
        table.get_Columns().Add(column);
        column = new DataColumn("C3", Type.GetType(Double.class.getName()));
        table.get_Columns().Add(column);
        for (int i = 0; i < objArray.length; i++)
        {
            DataRow row = table.NewRow();
            row.set_ItemArray((Object[])objArray[i]);
            table.get_Rows().Add(row);
        }
		return table;
	}
	
	public static double getDoubleValue(Object obj){
    	if (obj instanceof system.ClrInt32){
    		return ((system.ClrInt32)obj).getValue();
    	}else if (obj instanceof system.ClrInt16){
    		return ((system.ClrInt16)obj).getValue();
    	}else if (obj instanceof system.ClrDouble){
    		return ((system.ClrDouble)obj).getValue();
    	}else if (obj instanceof system.ClrUInt16){
    		return ((system.ClrUInt16)obj).getValue();
    	}else if (obj instanceof system.ClrUInt32){
    		return ((system.ClrUInt32)obj).getValue();
    	}else if (obj instanceof system.ClrInt64){
    		return ((system.ClrInt64)obj).getValue();
    	}else if (obj instanceof system.ClrUInt64){
    		return ((system.ClrUInt64)obj).getValue();
    	}else if (obj instanceof system.ClrSingle){
    		return ((system.ClrSingle)obj).getValue();
    	}
    	return 0;
    }
	
	private void fillProperties(){
		chartType = getChartType(dashboardPartDef.get_CategoryAsString());
		String chart = dashboardPartDef.get_CategoryAsString();
		if("CHARTBAR".equals(chart)){
			radio_Bar.setSelection(true);
		}else if("CHARTCOLUMN".equals(chart)){
			radio_Column.setSelection(true);
		}else if("CHARTPIE".equals(chart) || "NONE".equals(chart)){
			radio_Pie.setSelection(true);
//			chartType = "CHARTPIE";
		}else if("CHARTLINE".equals(chart)){
			radio_Line.setSelection(true);
		}else if("CHARTPIPELINE".equals(chart)){
			radio_PieLine.setSelection(true);
		}else if("GAUGELINEAR".equals(chart)){
			radio_GaugeLinear.setSelection(true);
		}else if("SPEEDOMETER".equals(chart)){
			radio_Speedometer.setSelection(true);
		}
	}
	
	private void saveProperties(){
//		GetChartPartDefinition();
		dashboardPartDef.set_CategoryAsString(XmlDashboardPartCategory.ToString(chartType).toUpperCase());
	}
	
	public static DashboardPartCategory getChartType(String typeName){
		DashboardPartCategory chartType = null;
		if("CHARTBAR".equals(typeName))
			chartType = DashboardPartCategory.ChartBar;
		else if("CHARTCOLUMN".equals(typeName))
			chartType = DashboardPartCategory.ChartColumn;
		else if("CHARTPIE".equals(typeName) || "NONE".equals(typeName))
			chartType = DashboardPartCategory.ChartPie;
		else if("CHARTLINE".equals(typeName))
			chartType = DashboardPartCategory.ChartLine;
		else if("CHARTPIPELINE".equals(typeName))
			chartType = DashboardPartCategory.ChartPipeline;
		else if("GAUGELINEAR".equals(typeName))
			chartType = DashboardPartCategory.GaugeLinear;
		else if("SPEEDOMETER".equals(typeName))
			chartType = DashboardPartCategory.Speedometer;
//		else if("CHARTCOLUMN".equals(typeName))
//			chartType = ChartType.ColumnChart;
		return chartType;
	}
	
	public static DashboardPartCategory getChartType() {
		return chartType;
	}
}
