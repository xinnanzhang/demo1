package core.dashboards;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.text.AttributedString;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleEdge;

import siteview.windows.forms.ConvertUtil;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Data.DataColumn;
import system.Data.DataRow;
import system.Data.DataTable;
import Core.Dashboards.ChartLinePartDef;
import Core.Dashboards.ChartType;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRange;
import Core.Dashboards.DrilldownDef;
import Core.Dashboards.GroupByDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.TimeInterval;
import Core.Dashboards.ViewDef;
import Core.Dashboards.XmlTimeIntervalCategory;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.ColorResolver;
import Siteview.IQueryResult;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Xml.SecurityRight;

public class ChartLinePartControl extends ChartPartControl {

	public ChartLinePartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);
		
		this.gridControl = new GridPartControl(super.getApi(), this.get_MainArea(), true);
		this.gridControl.set_Parent(get_Parent());
		gridControl.removeTitleBar();
	}

    static class CustomBarRenderer extends BarRenderer {
        private int highlightRow;
		private int highlightColumn;
        /** The colors. */
        private Paint[] colors;
        /**
         * Creates a new renderer.
         *
         * @param colors  the colors.
         */
        public CustomBarRenderer(Paint[] colors) {
            this.colors = colors;
            highlightRow = -1;
			highlightColumn = -1;
        }
        /**
         * Returns the paint for an item.  Overrides the default behaviour 
         * inherited from AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint(int row, int column) {
            return this.colors[column % this.colors.length];
        }
		public void setHighlightedItem(int i, int j) {
			if (highlightRow == i && highlightColumn == j) {
				return;
			} else {
				highlightRow = i;
				highlightColumn = j;
				notifyListeners(new RendererChangeEvent(this));
				return;
			}
		}
		@Override
		public Paint getItemOutlinePaint(int i, int j) {
			if (i == highlightRow && j == highlightColumn)
				return Color.yellow;
			else
				return super.getItemOutlinePaint(i, j);
		}
		@Override
		public CategoryToolTipGenerator getToolTipGenerator(int row, int column) {
			CategoryToolTipGenerator ttg = new CategoryToolTipGenerator(){
				@Override
				public String generateToolTip(CategoryDataset db, int i, int j) {
					return db.getColumnKey(j) + " = " + db.getValue(i, j);
				}};
			return ttg;
			//return super.getToolTipGenerator(row, column);
		}
    }
	    
	//TODO 变量定义区
	private StackLayout stacklayout;
	private GridPartControl gridControl;
	private ChartPanel m_chartPanel;
	private JFreeChart m_ultraChart;
	private SiteviewQuery m_drillQuery;
    private Boolean m_GridMode = false;
    private Core.Dashboards.ChartLinePartDef m_chartLinePartDef;
    private Integer m_parentColumn;
    private Integer m_parentRow;
	private int m_DrillDepth = 0;
	private int m_previousDepth = 0;
	private boolean m_bClickAgain = false;
	private CustomBarRenderer renderer;
	
	private Comparable<?> previousSection = null;
	
	//TODO DefineFromDef
	@Override
	protected  void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
		super.DefineFromDef(def, partRefDef);
		if (super.get_DashboardPartDef()!= null && super.get_DashboardPartDef() instanceof ChartLinePartDef){
			ChartLinePartDef dashboardPartDef = (ChartLinePartDef) super.get_DashboardPartDef();
			this.m_chartLinePartDef = dashboardPartDef;
			super.set_DrilldownArrayList(dashboardPartDef.get_DrilldownArrayList());
			super.set_DrilldownGridDefID(dashboardPartDef.get_DrilldownGridDefID());
			super.set_DrilldownGridDef(super.getApi().get_Presentation().GetGridDef(dashboardPartDef.get_DrilldownGridDefID()));
			super.set_DrilldownGridBehaior(dashboardPartDef.get_DrillDownGridAction());
			super.set_DateRangeControlVisible(dashboardPartDef.get_DateRangeControlVisible() && dashboardPartDef.get_DefaultDateRangeDef().get_ApplyDateRange());
			super.set_DrilldownGridOrderByFieldName(dashboardPartDef.get_DrilldownGridOrderByFieldName());
			
			if(super.get_DateRangeControlVisible()){
                for(ViewDef def3:dashboardPartDef.get_DateRangeOptions()){
                    if(def3.get_DateRange() != DateRange.None){
                        this.AddDateTimeRange(def3);
                    }
                }
                if(dashboardPartDef.get_AllowDateTimeEntry()){
                    super.AddCustomDateRange();
                }
                super.AddFiscalDateRangeIfAny(dashboardPartDef);
                super.SetDefaultDateRange(dashboardPartDef.get_DefaultDateRangeDef());
            }
            super.SetSelectionArea();
		}
	}
	
	//TODO DataBind
	@Override
    public  void DataBind(java.lang.Object dt)
    {
        super.DataBind(dt);
        if(this.m_GridMode){
            this.gridControl.DataBind(dt);
            showPanel(this.gridControl);
        }
        else if(dt != null){
            DataTable table = (DataTable)dt;
            if(this.get_DrillDepth() == 0){
                super.set_ParentDataTable(table);
            }
            if(table.get_Rows().get_Count() == 0){
                table = DataConverter.InsertEmptyRowToTable(DataConverter.ConvertColumnNameAndType(table,this.m_chartLinePartDef.get_GroupByDef(),this.m_chartLinePartDef.get_SubGroupByDef(),this.m_chartLinePartDef.get_EvaluateByDef()), false);
            } else {
            	//
            }
            
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
            
			m_ultraChart = ChartFactory.createLineChart("", // chart title
    				null, // domain axis label
    				"", // range axis label
    				dataset, // data
    				PlotOrientation.VERTICAL, // orientation
    				m_chartLinePartDef.get_LegendVisible(),//false, // include legend
    				true, // tooltips?
    				false // URLs?
    				);

			/*----------设置消除字体的锯齿渲染（解决中文问题）--------------*/ 
    		m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
    		
            m_ultraChart.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage(m_chartLinePartDef.get_BackImageName())));
            m_ultraChart.setBackgroundPaint(ConvertUtil.toAwtColor(m_chartLinePartDef.get_BackColor()));
            if(m_chartLinePartDef.get_LegendVisible()){
                m_ultraChart.getLegend().setItemFont(new Font("simsun", Font.CENTER_BASELINE, 12));
                m_ultraChart.getLegend().setPosition(RectangleEdge.RIGHT);
            }
            // get a reference to the plot for further customisation...
    		CategoryPlot plot = (CategoryPlot) m_ultraChart.getPlot();
    		plot.setBackgroundPaint(ConvertUtil.toAwtColor(m_chartLinePartDef.get_BackColor()));
    		
    		// set the range axis to display integers only...
    		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    		rangeAxis.setLabelFont(new Font("simsun", Font.PLAIN, 14));
    		
    		CategoryAxis domainAxis = plot.getDomainAxis();
    		domainAxis.setLabelFont(new Font("simsun", Font.CENTER_BASELINE, 14));
    		domainAxis.setTickLabelFont(new Font("simsun", Font.PLAIN, 12));
    		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);//设置x轴columnKey方向,因横着显示不完全.设置成垂直
    		plot.setDomainAxis(domainAxis); 

    		Paint[] colors = createPaint();
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
    	    System.out.println(dataset.getRowCount());
    	    for(int i = 0 ; i<dataset.getRowCount(); i++){
    	    	renderder.setSeriesPaint(i, colors[i % colors.length]);
	    	    renderder.setSeriesStroke(i, new BasicStroke(3.0F));
	    	    renderder.setSeriesOutlineStroke(i, new BasicStroke(2.0F));
	    	    renderder.setSeriesShape(i, new Ellipse2D.Double(-5.0D, -5.0D, 10.0D, 10.0D));
    	    }
    	    
    		if (m_chartPanel!=null)
    			m_chartPanel.dispose();
    		    		
            m_chartPanel = new ChartPanel(this.get_MainArea(),SWT.NONE,m_ultraChart);
            m_chartPanel.addChartMouseListener(new ChartMouseListener(){
				@Override
				public void chartMouseClicked(ChartMouseEvent e) {
					if (e.getEntity() instanceof CategoryItemEntity){
						onChartDataClick(e);
					}
				}

				@Override
				public void chartMouseMoved(ChartMouseEvent e) {
					org.jfree.chart.entity.ChartEntity chartentity = e.getEntity();
//					if (!(chartentity instanceof CategoryItemEntity))
//					{
//						renderer.setHighlightedItem(-1, -1);
//						return;
//					} else
//					{
//						CategoryItemEntity categoryitementity = (CategoryItemEntity)chartentity;
//						CategoryDataset categorydataset = categoryitementity.getDataset();
//						renderer.setHighlightedItem(categorydataset.getRowIndex(categoryitementity.getRowKey()), categorydataset.getColumnIndex(categoryitementity.getColumnKey()));
//						return;
//					}
				}});
            showPanel(m_chartPanel);
        }

    }

	//TODO GetData
	@Override
	public Object GetData(ILoadingStatusSink sink){
        DataTable dt = null;
        try
        {
            if (!super.getApi().get_SecurityService().HasBusObRight(super.get_ChartPartDef().get_BusObName(), SecurityRight.View))
            {
                super.ShowNoRightsAlert(super.get_ChartPartDef().get_BusObName());
                return dt;
            }
            if (!super.CheckFieldsHaveViewRight())
            {
                return dt;
            }
            if (this.m_GridMode)
            {
                this.gridControl.set_DependMode(this.get_DependMode());
                this.gridControl.set_DependPartRef(this.get_DependPartRef());
                this.gridControl.set_DependBusObj(this.get_DependBusObj());
                return this.gridControl.GetData(sink);
            }
            int depth = this.get_DrillDepth();
            if (depth == 1)
            {
                return this.GetChildTable(this.get_ParentRow(), this.get_ParentColumn(), super.get_ParentDataTable());
            }
            IQueryResult result = super.ExcuteStatisticRequest(super.get_ChartPartDef().get_Id(), super.get_ChartPartDef().get_Name(), super.get_ChartPartDef().get_Id(), null);
            if (sink != null)
            {
                sink.ChangeProgress(10, 100);
            }
            if (result.get_BusObDataTable() == null)
            {
                return dt;
            }
            dt = result.get_BusObDataTable();
            if (super.get_ChartPartDef().get_GroupByDef().get_Duration())
            {
                dt = DataConverter.RemoveWrongRows(dt, 0);
            }
            else if (!super.get_ChartPartDef().get_GroupByDef().get_DateTimeField())
            {
                dt = DataConverter.RemoveDBNullRows(dt, 0);
            }
            if ((!super.get_ChartPartDef().get_GroupByDef().get_Duration() && super.get_ChartPartDef().get_GroupByDef().get_LimitQuery()) && (!super.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(super.get_ChartPartDef().get_GroupByDef().get_FieldName()) && !super.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(super.get_ChartPartDef().get_BusObName() + "." + super.get_ChartPartDef().get_EvaluateByDef().get_FieldName())))
            {
                dt = DataConverter.RemoveOrderByColumn(dt, this.m_chartLinePartDef.get_GroupByDef(), this.m_chartLinePartDef.get_SubGroupByDef(), this.m_chartLinePartDef.get_EvaluateByDef());
            }
            if (sink != null)
            {
                sink.ChangeProgress(20, 100);
            }
            if (dt.get_Rows().get_Count() == 0)
            {
                return dt;
            }
            dt = DataConverter.ConvertColumnNameAndType(dt, this.m_chartLinePartDef.get_GroupByDef(), this.m_chartLinePartDef.get_SubGroupByDef(), this.m_chartLinePartDef.get_EvaluateByDef());
            if (sink != null)
            {
                sink.ChangeProgress(30, 100);
            }
            dt = DataConverter.ConvertIntervalNumberToProperName(dt, this.m_chartLinePartDef.get_GroupByDef(), this.m_chartLinePartDef.get_SubGroupByDef());
            if (sink != null)
            {
                sink.ChangeProgress(40, 100);
            }
            if (super.get_ChartPartDef().get_SubGroupByDef().get_Apply())
            {
                if (!super.get_ChartPartDef().get_GroupByDef().get_Duration() && !super.get_ChartPartDef().get_SubGroupByDef().get_Duration())
                {
                    if ((!super.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && super.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()) && (super.get_ChartPartDef().get_SubGroupByDef().get_Interval() != TimeInterval.Yearly.toString()))
                    {
                        dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(super.get_ChartPartDef().get_SubGroupByDef().get_Interval()), super.get_ChartPartDef().get_GroupByDef());
                        if (sink != null)
                        {
                            sink.ChangeProgress(50, 100);
                        }
                    }
                    if ((super.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && !super.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()) && (super.get_ChartPartDef().get_GroupByDef().get_Interval() != TimeInterval.Yearly.toString()))
                    {
                        dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(super.get_ChartPartDef().get_GroupByDef().get_Interval()), super.get_ChartPartDef().get_GroupByDef());
                        if (sink != null)
                        {
                            sink.ChangeProgress(60, 100);
                        }
                    }
                    if ((super.get_ChartPartDef().get_GroupByDef().get_DateTimeField() && super.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField()) && ((super.get_ChartPartDef().get_GroupByDef().get_Interval() != TimeInterval.Yearly.toString()) || (super.get_ChartPartDef().get_SubGroupByDef().get_Interval() != TimeInterval.Yearly.toString())))
                    {
                        dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(super.get_ChartPartDef().get_GroupByDef().get_Interval()), XmlTimeIntervalCategory.ToCategory(super.get_ChartPartDef().get_SubGroupByDef().get_Interval()));
                        if (sink != null)
                        {
                            sink.ChangeProgress(70, 100);
                        }
                    }
                }
                dt = DataConverter.Convert2XRef(dt);
                if (sink != null)
                {
                    sink.ChangeProgress(80, 100);
                }
                if (super.get_ChartPartDef().get_SubGroupByDef().get_DateTimeField())
                {
                    dt = DataConverter.SortDateTimeColumns(dt, XmlTimeIntervalCategory.ToCategory(super.get_ChartPartDef().get_SubGroupByDef().get_Interval()));
                }
                if (super.get_ChartPartDef().get_GroupByDef().get_DateTimeField())
                {
                    dt = DataConverter.SortDateTimeRows(dt, XmlTimeIntervalCategory.ToCategory(super.get_ChartPartDef().get_GroupByDef().get_Interval()));
                }
                if (sink != null)
                {
                    sink.ChangeProgress(90, 100);
                }
                return dt;
            }
            if (!super.get_ChartPartDef().get_GroupByDef().get_Duration() && super.get_ChartPartDef().get_GroupByDef().get_DateTimeField())
            {
                dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(super.get_ChartPartDef().get_GroupByDef().get_Interval()), super.get_ChartPartDef().get_GroupByDef());
            }
            if (sink != null)
            {
                sink.ChangeProgress(70, 100);
            }
            dt = DataConverter.FlipDataTable(dt);
            if (sink != null)
            {
                sink.ChangeProgress(80, 100);
            }
            this.ResetAxisXExtent(dt);
            if (sink != null)
            {
                sink.ChangeProgress(90, 100);
            }
        }
        catch (system.Exception exception)
        {
            if (!super.get_TrapErrors())
            {
                throw exception;
            }
            super.OnAlertRaised(this, new AlertRaisedEventArgs(AlertType.Error, exception));
            return dt;
        }
        return dt;
    }
	
	private void Drill(int row, int column, ChartType chartType, DataTable dt) {
		++this.m_DrillDepth;
		createChart(dt,chartType);
	}

	private void createChart(DataTable table, ChartType chartType) {
		DrilldownDef def = (DrilldownDef)super.get_DrilldownArrayList().get_Item(0);
		final ChartType chartType_1 = chartType;
		if (chartType == ChartType.PieChart){
			//饼图
			DefaultPieDataset dataset = new DefaultPieDataset();
            for(int i = 0; i < table.get_Rows().get_Count();i++){
            	DataRow dr = table.get_Rows().get_Item(i);
            	String s1 = (String)dr.get_Item(0);
            	double v = getDoubleValue(dr.get_Item(1));
            	dataset.setValue(s1,v);
            }
            String str = "";
            BusinessObjectDef businessObjectDef = null;
            businessObjectDef = super.getApi().get_BusObDefinitions().GetBusinessObjectDef(super.get_BusObName());
            if(def.get_SubGroupByDef().get_FieldName().equals("")){
                if(businessObjectDef != null){
                    FieldDef def3 = businessObjectDef.GetField(def.get_GroupByDef().get_FieldName());
                    if(def3 != null){
                        str = businessObjectDef.get_Alias() + '.' + def3.get_Alias();
                    }
                }
            }
            m_ultraChart = ChartFactory.createPieChart(str,
    				dataset, // data
    				false,
    				true, // tooltips?
    				false // URLs?
    				);
            // get a reference to the plot for further customisation...
     		PiePlot plot = (PiePlot) m_ultraChart.getPlot();
     		plot.setBackgroundPaint(ConvertUtil.toAwtColor(m_chartLinePartDef.get_BackColor()));
     		
     		Paint[] colors = createPaint();
     		for(int i = 0; i < dataset.getItemCount(); i++){
     			plot.setSectionPaint(dataset.getKey(i), colors[i % colors.length]);
     		}
     		
     		final String title = str;
     		plot.setLabelGenerator(new PieSectionLabelGenerator(){
				@Override
				public AttributedString generateAttributedSectionLabel(
						PieDataset db, Comparable key) {
					// TODO Auto-generated method stub
					AttributedString as = new AttributedString( title + ":" + key + " = " + db.getValue(key) );
					return as;
				}
				@Override
				public String generateSectionLabel(PieDataset db,
						Comparable key) {
					// TODO Auto-generated method stub
					return  key + " = " + db.getValue(key);
				}});
		}else if(chartType == ChartType.ColumnChart){
			//柱图 add 2011-10-11 by zhangfan
            if(this.get_DrillDepth() == 0){
                super.set_ParentDataTable(table);
            }
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
            m_ultraChart = ChartFactory.createBarChart("", // chart title
    				null, // domain axis label
    				"", // range axis label
    				dataset, // data
    				PlotOrientation.VERTICAL, // orientation
    				m_chartLinePartDef.get_LegendVisible(),//false, // include legend
    				true, // tooltips?
    				false // URLs?
    				);
            // get a reference to the plot for further customisation...
    		CategoryPlot plot = (CategoryPlot) m_ultraChart.getPlot();
    		plot.setBackgroundPaint(ConvertUtil.toAwtColor(m_chartLinePartDef.get_BackColor()));

    		// set the range axis to display integers only...
    		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    		rangeAxis.setLabelFont(new Font("simsun", Font.PLAIN, 14));
    		
    		CategoryAxis domainAxis = plot.getDomainAxis();
    		domainAxis.setLabelFont(new Font("simsun", Font.CENTER_BASELINE, 14));
    		domainAxis.setTickLabelFont(new Font("simsun", Font.PLAIN, 12));
    		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);//设置x轴columnKey方向,因横着显示不完全.设置成垂直
    		
    		domainAxis.setUpperMargin(0.2); 
    		plot.setDomainAxis(domainAxis); 
    		
    		Paint[] colors = createPaint();
            renderer = new CustomBarRenderer(colors);
            plot.setRenderer(renderer);
            
            //BarRenderer renderer2 = (BarRenderer) plot.getRenderer();
    		renderer.setDrawBarOutline(true);
    		
    		// the SWTGraphics2D class doesn't handle GradientPaint well, so
    		// replace the gradient painter from the default theme with a
    		// standard painter...
    		renderer.setBarPainter(new StandardBarPainter());
    		
		}
		/*----------设置消除字体的锯齿渲染（解决中文问题）--------------*/ 
		m_ultraChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
		
		m_ultraChart.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage(m_chartLinePartDef.get_BackImageName())));
		m_ultraChart.setBackgroundPaint(ConvertUtil.toAwtColor(m_chartLinePartDef.get_BackColor()));
		 
		m_ultraChart.setBorderVisible(false);
		m_ultraChart.getTitle().setPosition(RectangleEdge.BOTTOM);
		m_ultraChart.getTitle().setFont(new Font("simsun", Font.PLAIN, 14));
         
		if (m_chartPanel!=null)
			m_chartPanel.dispose();
		    		
        m_chartPanel = new ChartPanel(this.get_MainArea(),SWT.NONE,m_ultraChart);
        m_chartPanel.addChartMouseListener(new ChartMouseListener(){
			@Override
			public void chartMouseClicked(ChartMouseEvent e) {
				if (e.getEntity() instanceof CategoryItemEntity){
					onChartDataClick(e);
				}else if (e.getEntity() instanceof PieSectionEntity){
					onChartDataClick(e);
				}
			}
			@Override
			public void chartMouseMoved(ChartMouseEvent e) {
				// TODO Auto-generated method stub
				org.jfree.chart.entity.ChartEntity chartentity = e.getEntity();
				if (chartType_1 == ChartType.PieChart){
					PiePlot plot = (PiePlot) e.getChart().getPlot();
					if (chartentity instanceof PieSectionEntity){
    					PieSectionEntity piesectionentity = (PieSectionEntity) e.getEntity();
    					if(piesectionentity.getSectionKey() != previousSection){
    						plot.setSectionOutlinePaint(piesectionentity.getSectionKey(), Color.yellow);
    						if(previousSection!=null){
    							plot.setSectionOutlinePaint(previousSection, Color.gray);
    						}
    						previousSection = piesectionentity.getSectionKey();
    					}
					}else{
						if(previousSection!=null){
							plot.setSectionOutlinePaint(previousSection, Color.gray);
						}
						previousSection = null;
					}
				}else if (chartType_1 == ChartType.BarChart){
					if (!(chartentity instanceof CategoryItemEntity))
					{
						renderer.setHighlightedItem(-1, -1);
						return;
					} else
					{
						CategoryItemEntity categoryitementity = (CategoryItemEntity)chartentity;
						CategoryDataset categorydataset = categoryitementity.getDataset();
						renderer.setHighlightedItem(categorydataset.getRowIndex(categoryitementity.getRowKey()), categorydataset.getColumnIndex(categoryitementity.getColumnKey()));
						return;
					}
				}else if (chartType_1 == ChartType.ColumnChart){
					if (!(chartentity instanceof CategoryItemEntity))
					{
						renderer.setHighlightedItem(-1, -1);
						return;
					} else
					{
						CategoryItemEntity categoryitementity = (CategoryItemEntity)chartentity;
						CategoryDataset categorydataset = categoryitementity.getDataset();
						renderer.setHighlightedItem(categorydataset.getRowIndex(categoryitementity.getRowKey()), categorydataset.getColumnIndex(categoryitementity.getColumnKey()));
						return;
					}
				}
			}});
        showPanel(m_chartPanel);
	}
	
	protected void onChartDataClick(ChartMouseEvent e) {
		
    	DataTable dt = null;
        SiteviewQuery query = null;
        
        int row = 0 ;
        int column = 0;
        
		int count = super.get_DrilldownArrayList().get_Count();
		int depth = this.get_DrillDepth();
		
		if (e.getEntity() instanceof CategoryItemEntity){
			String rowKey = (String) ((CategoryItemEntity)e.getEntity()).getRowKey();
			String colKey = (String) ((CategoryItemEntity)e.getEntity()).getColumnKey();
			CategoryDataset ds = e.getChart().getCategoryPlot().getDataset();
			row = e.getChart().getCategoryPlot().getDataset().getRowIndex(rowKey);
			column = ds.getColumnKeys().indexOf(colKey)+1;
		}else if (e.getEntity() instanceof PieSectionEntity){
			row = ((PieSectionEntity)e.getEntity()).getSectionIndex();
			column = 1;
		}
		
		super.set_CanDrillBack(true);
		
		if (count > 0){
			
			switch(depth){
			case 2:
				--m_DrillDepth;
				break;
			case 0:
				if (m_previousDepth == 0){
					if (super.get_SkipDrillDown()){
						createChart(super.get_ParentDataTable(),ChartType.BarChart);
						super.set_SkipDrillDown(false);
						return;
					}else
					{
						this.set_ParentColumn(column);
						this.set_ParentRow(row);
						dt = this.GetChildTable(row, column, super.get_ParentDataTable());
						ChartType chartType = ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_DrilldownChartType();

						Drill(row,column,chartType,dt);
						return;
					}
				}
			case 1:
				//if (m_previousDepth == 0){
					if (super.get_SkipDrillDown()){
						dt = this.GetChildTable(this.get_ParentRow(), this.get_ParentColumn(), super.get_ParentDataTable());
						super.set_SkipDrillDown(false);
						createChart(dt,null);
						return;
					}
					dt = this.GetChildTable(this.get_ParentRow(), this.get_ParentColumn(), super.get_ParentDataTable());
					query = this.GetSiteviewQuery(row, column, depth, dt);
					m_previousDepth = depth;
					if (super.get_DrilldownGridDef() != null){
						gridControl.set_GridDef(super.get_DrilldownGridDef());
					}else{
						super.GenerateDefaultGridDef(super.get_BusObName());
						gridControl.set_GridDef(super.get_DrilldownGridDef());
					}
					
					query.set_InfoToGet(QueryInfoToGet.RequestedFields);
                    query.set_RequestedFields(super.GetRequestedFields(this.gridControl.get_GridDef()));
                    this.gridControl.set_Query(query);
                    super.set_BaseQuery(query);
                    this.GoToDashboardGrid();

                    return ;
                
				//}
			}
			
			if (depth == 2){
				if(this.get_ClickAgain()){
                    this.set_ClickAgain(false);
                    
                    return ;
                }
                if(super.get_SkipDrillDown()){
                	
                	dt = this.GetChildTable(this.get_ParentRow(),this.get_ParentColumn(),super.get_ParentDataTable());
                	createChart(dt,null);
                    super.set_SkipDrillDown(false);

                    return ;
                }
                
                dt = this.GetChildTable(this.get_ParentRow(), this.get_ParentColumn(), super.get_ParentDataTable());
                
                query = this.GetSiteviewQuery(row, column, depth, dt);
                this.set_PreviousDepth(depth);
                if(super.get_DrilldownGridDef() != null){
                    this.gridControl.set_GridDef(super.get_DrilldownGridDef());
                }
                else{
                    super.GenerateDefaultGridDef(super.get_BusObName());
                    this.gridControl.set_GridDef(super.get_DrilldownGridDef());
                }
                query.set_InfoToGet(QueryInfoToGet.RequestedFields);
                query.set_RequestedFields(super.GetRequestedFields(this.gridControl.get_GridDef()));
                this.gridControl.set_Query(query);
                super.set_BaseQuery(query);
                this.GoToDashboardGrid();
                this.set_ClickAgain(true);

                return ;
            
			}
		}else{
			if(this.get_ClickAgain()){
                this.set_ClickAgain(false);

                return ;
            }
            if(super.get_SkipDrillDown()){
                super.set_SkipDrillDown(false);

                return ;
            }
            
            query = this.GetSiteviewQuery(row, column, depth, super.get_ParentDataTable());
            if(super.get_DrilldownGridDef() != null){
                this.gridControl.set_GridDef(super.get_DrilldownGridDef());
            }
            else{
                super.GenerateDefaultGridDef(super.get_BusObName());
                this.gridControl.set_GridDef(super.get_DrilldownGridDef());
            }
            query.set_InfoToGet(QueryInfoToGet.RequestedFields);
            query.set_RequestedFields(super.GetRequestedFields(this.gridControl.get_GridDef()));
            this.gridControl.set_Query(query);
            super.set_BaseQuery(query);
            this.GoToDashboardGrid();
            this.set_ClickAgain(true);
		}
		super.set_SkipDrillDown(false);
	}

	@Override
    protected  DataTable GetChildTable(Integer row, Integer column, DataTable dataSource)
    {
        DataTable dt = null;
        SiteviewQuery query = null;
        
        query = super.GetDrillDownCriteria(row, column, dataSource, super.get_CurrentRunTimeDateRange());
        try{
            IQueryResult result = super.ExcuteStatisticRequest(query, (DrilldownDef)super.get_DrilldownArrayList().get_Item(0));
            if(result.get_BusObDataTable() == null){

                return dt;
            }
            dt = result.get_BusObDataTable();
            if(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Duration()){
                
                dt = DataConverter.RemoveWrongRows(dt, 0);
            }
            if(dt.get_Rows().get_Count() == 0){
                
                dt = DataConverter.InsertEmptyRowToTable(dt, false);
            }
            
            dt = DataConverter.ConvertColumnNameAndType(dt, ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef(), ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef(), ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_EvaluateByDef());
            
            dt = DataConverter.ConvertIntervalNumberToProperName(dt, ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef(), ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef());
            if(!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Apply()){
                if((!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Duration() && ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField()) && ((!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval().equals(TimeInterval.Yearly.toString())))){
                    
                    dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval()), (GroupByDef)null);
                }
                if(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_DrilldownChartType() != Core.Dashboards.ChartType.PieChart){
                    
                    dt = DataConverter.FlipDataTable(dt);

                    if(dt.get_Columns().Contains(Res.get_Default().GetString("Chart.InfragisticNullColumn"))){
                        
                        dt = DataConverter.ChangeColumnCaption(dt);
                    }
                    if(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_DrilldownChartType() == Core.Dashboards.ChartType.BarChart){
                        this.ResetAxisXExtent(dt);
                    }
                }

                return dt;
            }
            if(!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Duration() && !((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Duration()){
                if((!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField() && ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_DateTimeField()) && ((!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval().equals(TimeInterval.Yearly.toString())))){
                    
                    dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval()), (GroupByDef)null);
                }
                if((((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField() && !((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_DateTimeField()) && ((!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval().equals(TimeInterval.Yearly.toString())))){
                    
                    dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval()), (GroupByDef)null);
                }
                if((((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField() && ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_DateTimeField()) && (((!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval().equals(TimeInterval.Yearly.toString()))) || ((!((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval().equals(TimeInterval.Yearly.toString()))))){
                    
                    dt = DataConverter.MergeIntervalColumns(dt, XmlTimeIntervalCategory.ToCategory(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval()), XmlTimeIntervalCategory.ToCategory(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval()));
                }
            }
            
            dt = DataConverter.Convert2XRef(dt);
            if(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_DateTimeField()){
                
                dt = DataConverter.SortDateTimeColumns(dt, XmlTimeIntervalCategory.ToCategory(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_SubGroupByDef().get_Interval()));
            }
            if(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_DateTimeField()){
                
                dt = DataConverter.SortDateTimeRows(dt, XmlTimeIntervalCategory.ToCategory(((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_GroupByDef().get_Interval()));
            }
        }
        catch(system.Exception e){
        	e.printStackTrace();
        }

        return dt;

    }
	
	private void ResetAxisXExtent(DataTable dt) {
		// TODO Auto-generated method stub
		
	}

	private void GoToDashboardGrid() {
        this.gridControl.set_BusObName(super.get_BusObName());
        this.gridControl.set_BusObDef(super.getApi().get_BusObDefinitions().GetBusinessObjectDef(this.gridControl.get_BusObName()));
        this.gridControl.set_GridId(super.get_DrilldownGridDefID());
        this.gridControl.setVisible(true);
        showPanel(gridControl);
        this.gridControl.set_LimitQuery(false);
        this.gridControl.set_OrderByFieldName(super.get_DrilldownGridOrderByFieldName());
        this.gridControl.set_DrillDownGridBehavior(super.get_DrilldownGridBehaior());
        this.m_GridMode = true;
        this.gridControl.set_PartLoader(super.get_PartLoader());
        this.DoDataRefresh();
		
	}
	
	@Override
	public void DrillBack(){
		if(super.get_CanDrillBack()){
			 if(this.m_GridMode){
	                this.m_GridMode = false;
	                showPanel(m_chartPanel);
	                if((this.get_PreviousDepth() > 0) && (this.get_DrillDepth() > 0)){
	                    if((this.get_PreviousDepth() == 2) && (this.get_DrillDepth() == 1)){
	                        this.set_PreviousDepth(this.get_PreviousDepth()-1);
	                    }
	                    DataTable dt = this.GetChildTable(this.get_ParentRow(),this.get_ParentColumn(),super.get_ParentDataTable());
	                    
	                    ChartType ctype = ((DrilldownDef)super.get_DrilldownArrayList().get_Item(0)).get_DrilldownChartType();
	                    createChart(dt,ctype);
	                    this.set_ClickAgain(false);
	                }
	                else{
	                    this.DataBind(super.get_ParentDataTable());
	                    super.set_CanDrillBack(false);
	                    this.set_ClickAgain(false);
	                }
	            }
	            else{
	                --m_DrillDepth;
	                if(this.get_PreviousDepth() > 0){
	                	this.set_PreviousDepth(this.get_PreviousDepth()-1);
	                }
	
	                this.DataBind(super.get_ParentDataTable());
	                super.set_SkipDrillDown(false);
	                super.set_CanDrillBack(false);
	            }
		}
	}

	private Paint[] createPaint() {
        Paint[] colors = new Paint[10];
        colors[0] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor1())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[1] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor2())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[2] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor3())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[3] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor4())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[4] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor5())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[5] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor6())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[6] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor7())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[7] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor8())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[8] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor9())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[9] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartLinePartDef.get_CustomColor10())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);

        return colors;
    }
	
    private  SiteviewQuery GetSiteviewQuery(Integer row, Integer column, Integer depth, DataTable dt)
    {
        ISiteviewApi api = super.getApi();
        SiteviewQuery query = null;
        if(depth == 0){
            
            query = super.GetDrillDownCriteria(row, column, super.get_ParentDataTable(), super.get_CurrentRunTimeDateRange());
            this.set_DrillQuery(query);
        }
        else{
            
            query = super.GetDrillDownCriteria(this.get_ParentRow(), this.get_ParentColumn(), super.get_ParentDataTable(), super.get_CurrentRunTimeDateRange());
            this.set_DrillQuery(query);
            if(this.get_DrillQuery() != null){
                
                query = super.GetDrillDownSiteviewQuery(this.get_DrillQuery(), row, column, dt);
            }
        }
        query.ToXml();

        return query;

    }
    
    @Override
	protected void ReQuery(ViewDef vViewDef, ViewDef vDateTimeDef){
        if (m_chartPanel.getVisible()){
            if (this.get_DrillDepth() == 0){
                super.set_ParentDataTable(null);
            }
            super.set_CurrentRunTimeDateRange(vDateTimeDef);
            this.DoDataRefresh();
        }else{
            SiteviewQuery query = super.get_BaseQuery().Clone();
            if (vDateTimeDef != null){
                if (vDateTimeDef.get_IsCustom()){
                    query = DateRangeCriteriaCreator.AddCustomDateRangeCriteria(query, this.m_chartLinePartDef.get_DefaultDateRangeDef().get_DateTimeField(), vDateTimeDef.get_StartDateTime(), vDateTimeDef.get_EndDateTime());
                }else if (vDateTimeDef.get_IsFiscalPeriod()){
                    query = DateRangeCriteriaCreator.AddFiscalDateRangeCriteria(super.getApi(), query, this.m_chartLinePartDef.get_DefaultDateRangeDef().get_DateTimeField(), vDateTimeDef.get_FiscalPeriod());
                }else{
                    query = DateRangeCriteriaCreator.AddDateRangeCriteria(super.getApi(), query, vDateTimeDef.get_DateRange(), this.m_chartLinePartDef.get_DefaultDateRangeDef().get_DateTimeField());
                }
            }
            this.gridControl.set_Query(query);
            this.DoDataRefresh();
        }
    }
    
	private void showPanel(ChartPanel zf) {
		// TODO Auto-generated method stub
		this.stacklayout.topControl = zf;
    	this.get_MainArea().layout();
	}

	private void showPanel(GridPartControl zf) {
		// TODO Auto-generated method stub
		this.stacklayout.topControl = zf;
    	this.get_MainArea().layout();
	}

	public JFreeChart get_Chart() {
		return m_ultraChart;
	}

	public void set_Chart(JFreeChart m_ultraChart) {
		this.m_ultraChart = m_ultraChart;
	}

	public SiteviewQuery get_DrillQuery() {
		return m_drillQuery;
	}

	public void set_DrillQuery(SiteviewQuery m_drillQuery) {
		this.m_drillQuery = m_drillQuery;
	}

	public Boolean get_GridMode() {
		return m_GridMode;
	}

	public void set_GridMode(Boolean m_GridMode) {
		this.m_GridMode = m_GridMode;
	}

	public Integer get_ParentColumn() {
		return m_parentColumn;
	}

	public void set_ParentColumn(Integer m_parentColumn) {
		this.m_parentColumn = m_parentColumn;
	}

	public Integer get_ParentRow() {
		return m_parentRow;
	}

	public void set_ParentRow(Integer m_parentRow) {
		this.m_parentRow = m_parentRow;
	}

	public int get_DrillDepth() {
		return m_DrillDepth;
	}

	public void set_DrillDepth(int m_DrillDepth) {
		this.m_DrillDepth = m_DrillDepth;
	}

	public int get_PreviousDepth() {
		return m_previousDepth;
	}

	public void set_PreviousDepth(int m_previousDepth) {
		this.m_previousDepth = m_previousDepth;
	}

	public boolean get_ClickAgain() {
		return m_bClickAgain;
	}

	public void set_ClickAgain(boolean m_bClickAgain) {
		this.m_bClickAgain = m_bClickAgain;
	}

}
