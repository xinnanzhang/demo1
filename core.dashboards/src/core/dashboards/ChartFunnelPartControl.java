package core.dashboards;

import java.awt.Color;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.dom4j.Element;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.PieSectionEntity;

import siteview.windows.forms.ConvertUtil;
import system.Data.DataColumn;
import system.Data.DataRow;
import system.Data.DataTable;
import Core.Dashboards.ChartFunnelPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRange;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.ViewDef;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.ColorResolver;
import Siteview.IQueryResult;
import Siteview.SiteviewQuery;
import Siteview.Api.ISiteviewApi;
import Siteview.Xml.SecurityRight;
import core.dashboards.funnelChart.FusionChartsCreator;
import core.dashboards.funnelChart.XMLUtil;

public class ChartFunnelPartControl extends ChartPartControl {

	public ChartFunnelPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);
		
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);

		this.gridControl = new GridPartControl(super.getApi(), this.get_MainArea(), true);
		this.gridControl.set_Parent(get_Parent());
		gridControl.removeTitleBar();
	}

    private GridPartControl gridControl;
    private boolean m_bClickAgain;
    private ChartFunnelPartDef m_chartFunnelPartDef;
    private SiteviewQuery m_drillQuery;
    private boolean m_GridMode;
    private Browser m_ultraChart;
    private Integer m_parentColumn;
    private Integer m_parentRow;
    private int m_DrillDepth = 0;
	private int m_previousDepth = 0;
    private StackLayout stacklayout;
    
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef)
    {
        super.DefineFromDef(def, partRefDef);
        ChartFunnelPartDef dashboardPartDef = (ChartFunnelPartDef) super.get_DashboardPartDef();
        if (dashboardPartDef != null){
            this.m_chartFunnelPartDef = dashboardPartDef;
            super.set_DrilldownArrayList(dashboardPartDef.get_DrilldownArrayList());
            super.set_DateRangeControlVisible(dashboardPartDef.get_DateRangeControlVisible() && dashboardPartDef.get_DefaultDateRangeDef().get_ApplyDateRange());
            super.set_DrilldownGridDefID(dashboardPartDef.get_DrilldownGridDefID());
            super.set_DrilldownGridDef(super.getApi().get_Presentation().GetGridDefById(super.get_DrilldownGridDefID()));
            super.set_DrilldownGridOrderByFieldName(dashboardPartDef.get_DrilldownGridOrderByFieldName());
            super.set_DrilldownGridBehaior(dashboardPartDef.get_DrillDownGridAction());
            if (super.get_DateRangeControlVisible()){
                for (ViewDef def3 : dashboardPartDef.get_DateRangeOptions()){
                    if (def3.get_DateRange() != DateRange.None){
                        this.AddDateTimeRange(def3);
                    }
                }
                if (dashboardPartDef.get_AllowDateTimeEntry()){
                    super.AddCustomDateRange();
                }
                super.AddFiscalDateRangeIfAny(dashboardPartDef);
                super.SetDefaultDateRange(dashboardPartDef.get_DefaultDateRangeDef());
            }
            super.SetSelectionArea();
        }
    }
	
	@Override
	public void DataBind(Object dt)
    {
        super.DataBind(dt);
        if (this.m_GridMode)
        {
            this.gridControl.DataBind(dt);
            showPanel(this.gridControl);
        }
        else if (dt != null)
        {
            DataTable table = (DataTable) dt;
            if (this.get_DrillDepth() == 0)
            {
                super.set_ParentDataTable(table);
            }
            if (table.get_Rows().get_Count() == 0)
            {
                table = DataConverter.InsertEmptyRowToTable(DataConverter.ConvertColumnNameAndType(table, this.m_chartFunnelPartDef.get_GroupByDef(), this.m_chartFunnelPartDef.get_SubGroupByDef(), this.m_chartFunnelPartDef.get_EvaluateByDef()), false);
            }
            else
            {
                //this.m_ultraChart.FunnelChart.Labels.FormatString = 
                //this.m_chartFunnelPartDef.ConverttoFormatString(this.m_chartFunnelPartDef.get_FunnelLabelFormatString());
            }
            
            //得到文件位置
    		String str1= "";
    		try {
	    		URL url2 = Activator.getDefault().getBundle().getEntry("META-INF/MANIFEST.MF");
	    		//System.out.println("url2:"+url2);
	    		//System.out.println("FileLocator.toFileURL(url2):"+FileLocator.toFileURL(url2));
	    		
	    		str1 = FileLocator.toFileURL(url2).toString(); //截去一些前面6个无用的字符
	    		//System.out.println(str1);
	    		str1=str1.substring(6,str1.length());
	    		str1=str1.replaceAll("%20", " ");
	    		//System.out.println(str1);
	    		int num1 = str1.indexOf("META-INF/MANIFEST.MF");
	    		str1=str1.substring(0, num1);
	    		//System.out.println(str1);
    		} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //得到结果集
    		Color[] colors = createPaint();
    		
            XMLUtil xml = new XMLUtil();
    		Element graph = xml.addRoot("graph");
//    		xml.addAttribute(graph, "basefontsize", "12");
    		xml.addAttribute(graph, "decimalPrecision", "0");// 小数精确度，0为精确到个位
    		xml.addAttribute(graph, "showValues", "0");// 在报表上不显示数值
    		xml.addAttribute(graph, "slicingDistance","4");
    		xml.addAttribute(graph, "isSliced","1");
    		xml.addAttribute(graph, "is2D","1");
    		xml.addAttribute(graph, "showPlotBorder","1");
    		xml.addAttribute(graph, "plotBorderThickness","1");
    		String plotBorderColor_rgb = "";
    		if(m_chartFunnelPartDef.get_BackColor()!=null){
	    		Color plotBorderColor = ConvertUtil.toAwtColor(m_chartFunnelPartDef.get_BackColor());
	    		plotBorderColor_rgb = Integer.toHexString(plotBorderColor.getRGB());
	    		plotBorderColor_rgb = plotBorderColor_rgb.substring(2, plotBorderColor_rgb.length());
    		}else{
    			plotBorderColor_rgb = "333333";
    		}
    		xml.addAttribute(graph, "plotBorderColor",plotBorderColor_rgb);
    		xml.addAttribute(graph, "streamlinedData","0");
    		//caption='Sales distribution by Employee' subCaption='Jan 07 - Jul 07' 
    		int zf=0;
    		for(int i = 0; i < table.get_Rows().get_Count();i++){
            	DataRow dr = table.get_Rows().get_Item(i);
            	for(int j = 1; j < table.get_Columns().get_Count();j++){
            		DataColumn dc = table.get_Columns().get_Item(j);
            		String s1 = (String)dr.get_Item(0);
            		String s2 = dc.get_ColumnName();
            		double v = getDoubleValue(dr.get_Item(dc));
            		if(v>0){
            			Element set2 = xml.addNode(graph, "set");
	            		set2.addAttribute("name", s1);
	            		set2.addAttribute("label", s1);
	            		set2.addAttribute("value",String.valueOf(v));
	            		Color this_c = colors[zf % colors.length];
	            		String rgb = Integer.toHexString(this_c.getRGB());
	            		rgb = rgb.substring(2, rgb.length());
	            		set2.addAttribute("color", rgb);
	            		//增加单击事件
	            		//set2.addAttribute("link", "onClick()");
	            		zf++;
            		}
            	}
            }

    		//产生FunnelChart到Browser上面 没网络时,不影响RCP显示
    		String strChartCode = "";
    		if(isConnect("http://www.fusioncharts.com/").equalsIgnoreCase("ok")){
    			strChartCode = FusionChartsCreator.createChartHTML("http://www.fusioncharts.com/demos/gallery/charts/widgets/Funnel.swf", "",xml.getXML().replaceAll("\"", "'"),"funnel", 400, 320, false);
    		}else{
    			strChartCode=FusionChartsCreator.createChart(str1+"src/core/dashboards/funnelChart/FusionCharts.js",str1+"src/core/dashboards/funnelChart/Funnel.swf", "",xml.getXML().replaceAll("\"", "'"),"funnel", 400, 320, false, false);
    		}
    		//System.out.println(strChartCode);
    		this.m_ultraChart = new Browser(this.get_MainArea(),SWT.NONE);
            this.m_ultraChart.setText(strChartCode);
            
            showPanel(this.m_ultraChart);
        }
    }
	
	private static URL urlStr;
	private static HttpURLConnection connection;
	private static int state = -1;
	private static String succ;
	
	private synchronized String isConnect(String url) {
		int counts = 0;
		succ = "";
		if (url == null || url.length() <= 0) {
			return succ;
		}
		while (counts < 5) {
			try {
				urlStr = new URL(url);
				connection = (HttpURLConnection) urlStr.openConnection();
				state = connection.getResponseCode();
				if (state == 200) {
//					succ = connection.getURL().toString();
					succ = "ok";
				}
				break;
			}catch (Exception ex) {
				counts++;
				continue;
			}
		}
		return succ;
	}
	
	public void onClick(){
		//
	}
	@Override
	public Object GetData(ILoadingStatusSink sink)
    {
        DataTable dt = null;
        try{
            if (!super.getApi().get_SecurityService().HasBusObRight(super.get_ChartPartDef().get_BusObName(), SecurityRight.View))
            {
                super.ShowNoRightsAlert(super.get_ChartPartDef().get_BusObName());
                return null;
            }
            if (!super.CheckFieldsHaveViewRight())
            {
                return null;
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
                return this.GetChildTable(this.get_ParentRow(),this.get_ParentColumn(),super.get_ParentDataTable());
            }
            IQueryResult result = super.ExcuteStatisticRequest(super.get_ChartPartDef().get_Id(), super.get_ChartPartDef().get_Name(), super.get_ChartPartDef().get_Id(), super.get_CurrentRunTimeDateRange());
            if (sink != null)
            {
                sink.ChangeProgress(20, 100);
            }
            if (result.get_BusObDataTable() == null)
            {
                return dt;
            }
            dt = result.get_BusObDataTable();
            if (!super.get_ChartPartDef().get_GroupByDef().get_DateTimeField())
            {
                dt = DataConverter.RemoveDBNullRows(dt, 0);
            }
            if ((!super.get_ChartPartDef().get_GroupByDef().get_Duration() && super.get_ChartPartDef().get_GroupByDef().get_LimitQuery()) && (!super.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(super.get_ChartPartDef().get_GroupByDef().get_FieldName()) && !super.get_ChartPartDef().get_GroupByDef().get_OrderByFieldName().equals(super.get_ChartPartDef().get_BusObName() + "." + super.get_ChartPartDef().get_EvaluateByDef().get_FieldName())))
            {
                dt = DataConverter.RemoveOrderByColumn(dt, this.m_chartFunnelPartDef.get_GroupByDef(), this.m_chartFunnelPartDef.get_SubGroupByDef(), this.m_chartFunnelPartDef.get_EvaluateByDef());
            }
            if (sink != null)
            {
                sink.ChangeProgress(40, 100);
            }
            if (dt.get_Rows().get_Count() == 0)
            {
                return dt;
            }
            dt = DataConverter.ConvertColumnNameAndType(dt, this.m_chartFunnelPartDef.get_GroupByDef(), this.m_chartFunnelPartDef.get_SubGroupByDef(), this.m_chartFunnelPartDef.get_EvaluateByDef());
            if (sink != null)
            {
                sink.ChangeProgress(60, 100);
            }
            dt = DataConverter.ConvertIntervalNumberToProperName(dt, this.m_chartFunnelPartDef.get_GroupByDef(), this.m_chartFunnelPartDef.get_SubGroupByDef());
            if (sink != null)
            {
                sink.ChangeProgress(80, 100);
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
	
	private Color[] createPaint() {
		Color[] colors = new Color[10];
        colors[0] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor1())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[1] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor2())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[2] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor3())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[3] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor4())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[4] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor5())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[5] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor6())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[6] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor7())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[7] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor8())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[8] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor9())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        colors[9] = ConvertUtil.toAwtColor(ColorResolver.get_Resolver().ResolveColor(this.m_chartFunnelPartDef.get_CustomColor10())); //Color.red;//new GradientPaint(0f, 0f, Color.red, 0f, 0f, Color.white);
        return colors;
    }
	
	private void showPanel(Composite p) {
		// TODO Auto-generated method stub
		this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
	}

	public boolean get_ClickAgain() {
		return m_bClickAgain;
	}

	public void set_ClickAgain(boolean m_bClickAgain) {
		this.m_bClickAgain = m_bClickAgain;
	}

	public boolean get_GridMode() {
		return m_GridMode;
	}

	public void set_GridMode(boolean m_GridMode) {
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
	
	
}
