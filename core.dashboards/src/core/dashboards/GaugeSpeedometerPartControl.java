package core.dashboards;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Point;
import java.awt.RenderingHints;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.general.ValueDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;

import siteview.windows.forms.ConvertUtil;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Convert;
import system.Data.DataTable;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRange;
import Core.Dashboards.GaugeRegionDef;
import Core.Dashboards.GaugeSpeedometerPartDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.ViewDef;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.Api.ISiteviewApi;
import Siteview.Xml.SecurityRight;

public class GaugeSpeedometerPartControl extends ChartPartControl {

	public GaugeSpeedometerPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);
		
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);
	}

	private JFreeChart LinearGauge;
	private GaugeSpeedometerPartDef m_gaugeSpeedometerPartDef;
	private ChartPanel m_chartPanel;
	private StackLayout stacklayout;
	
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
        super.DefineFromDef(def, partRefDef);
        GaugeSpeedometerPartDef dashboardPartDef = (GaugeSpeedometerPartDef) super.get_DashboardPartDef();
        if (dashboardPartDef != null){
            this.m_gaugeSpeedometerPartDef = dashboardPartDef;
            super.set_DateRangeControlVisible(dashboardPartDef.get_DateRangeControlVisible() && dashboardPartDef.get_DefaultDateRangeDef().get_ApplyDateRange());
        }
        if (super.get_DateRangeControlVisible()){
            for (ViewDef def3 : dashboardPartDef.get_DateRangeOptions()){
                if (def3.get_DateRange() != DateRange.None){
                    this.AddDateTimeRange(def3);
                }
            }
            if (dashboardPartDef.get_AllowDateTimeEntry()){
                super.AddCustomDateRange();
            }
            super.SetDefaultDateRange(dashboardPartDef.get_DefaultDateRangeDef());
        }
    }
	
	@Override
	public void DataBind(Object dt){
        super.DataBind(dt);
        super.set_ParentDataTable((DataTable) dt);
        if (super.get_ParentDataTable() != null){
        	double min_num = -1111111;
            double max_num = 0.0; 
        	DefaultValueDataset dataset = new DefaultValueDataset();
            dataset.setValue(Convert.ToInt32(((DataTable)dt).get_Rows().get_Item(0).get_Item(0)));
            //整体样式
            DialPlot plot = new DialPlot(dataset);	//数据集
            this.LinearGauge = new JFreeChart("",
            		JFreeChart.DEFAULT_TITLE_FONT,
					plot,
					m_gaugeSpeedometerPartDef.get_LegendVisible());
            /*----------设置消除字体的锯齿渲染（解决中文问题）--------------*/ 
    		LinearGauge.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
            LinearGauge.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage(m_gaugeSpeedometerPartDef.get_BackImageName())));
            if(m_gaugeSpeedometerPartDef.get_LegendVisible()){
                LinearGauge.getLegend().setItemFont(new Font("simsun", Font.CENTER_BASELINE, 12));
                LinearGauge.getLegend().setPosition(RectangleEdge.RIGHT);
            }
            //设置显示框架
            StandardDialFrame sdf = new StandardDialFrame();
            sdf.setBackgroundPaint(Color.gray);
            sdf.setForegroundPaint(Color.black);
            plot.setDialFrame(sdf);
            
            plot.setBackground(new DialBackground());
            //设置图区背景区域 无法出现渐变色
        	system.Drawing.Color color_s = m_gaugeSpeedometerPartDef.get_ForeGradientStartColor();
        	Color a_s = new Color(color_s.get_R(),color_s.get_G(),color_s.get_B(),color_s.get_A());
        	system.Drawing.Color color_e = m_gaugeSpeedometerPartDef.get_ForeGradientEndColor();
        	Color a_e = new Color(color_e.get_R(),color_e.get_G(),color_e.get_B(),color_e.get_A());
        	GradientPaint localGradientPaint = new GradientPaint(new Point(), a_s, new Point(), a_e);
//        	GradientPaint localGradientPaint = new GradientPaint(new Point(), new Color(color_s.get_R(),color_s.get_G(),color_s.get_B(),color_s.get_A()), new Point(), new Color(color_e.get_R(),color_e.get_G(),color_e.get_B(),color_e.get_A()));
//        	GradientPaint localGradientPaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
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
            localDialValueIndicator.setBackgroundPaint(ConvertUtil.toAwtColor(m_gaugeSpeedometerPartDef.get_ForeGradientStartColor()));
            plot.addLayer(localDialValueIndicator);
            
            max_num = Convert.ToInt32(((DataTable)dt).get_Rows().get_Item(0).get_Item(0));
            //设置区间显示色 起始区间
            for(GaugeRegionDef ra : m_gaugeSpeedometerPartDef.get_GaugeRegionOptions()){
            	if(min_num == -1111111){
            		min_num = ra.get_RegionStartValue();
            	}
            	StandardDialRange localStandardDialRange = new StandardDialRange(ra.get_RegionStartValue(), ra.get_RegionValue(), ConvertUtil.toAwtColor(ra.get_RegionColor()));
            	if(ra.get_RegionStartValue() != ra.get_RegionValue()){
            		max_num = ra.get_RegionValue();
            	}else{
            		max_num += 5 ;
            		localStandardDialRange = new StandardDialRange(min_num, max_num, ConvertUtil.toAwtColor(ra.get_RegionColor()));
            	}
                localStandardDialRange.setInnerRadius(0.52D);
                localStandardDialRange.setOuterRadius(0.55D);
                plot.addLayer(localStandardDialRange);
            }
            //表盘初始化
            StandardDialScale localStandardDialScale = new StandardDialScale(min_num, max_num, -120.0D, -300.0D, 10.0D, 9);
            localStandardDialScale.setMajorTickIncrement(10); 					//数值显示间隔
            localStandardDialScale.setMinorTickCount(9);						//刻度数量
            localStandardDialScale.setTickRadius(0.88D);						//刻度表的偏移
            localStandardDialScale.setTickLabelOffset(0.15D);					//刻度值的偏移
            localStandardDialScale.setTickLabelFont(new Font("Dialog", 0, 12));	//刻度值字体
            localStandardDialScale.setMajorTickPaint(Color.black);				//刻度颜色
            localStandardDialScale.setTickLabelPaint(Color.black);				//刻度值颜色
            plot.addScale(0, localStandardDialScale);
            //中心指针区
            plot.addPointer(new DialPointer.Pin());
            DialPointer.Pointer localPointer = new DialPointer.Pointer();
            DialCap localDialCap = new DialCap();
            if(ConvertUtil.toAwtColor(m_gaugeSpeedometerPartDef.get_NeedleColor()).getRGB()==0){
            	localPointer.setFillPaint(Color.black);
            	localDialCap.setFillPaint(Color.black);
            }else{
            	localPointer.setFillPaint(ConvertUtil.toAwtColor(m_gaugeSpeedometerPartDef.get_NeedleColor()));
            	localPointer.setOutlinePaint(ConvertUtil.toAwtColor(m_gaugeSpeedometerPartDef.get_NeedleColor()));
            	localDialCap.setFillPaint(ConvertUtil.toAwtColor(m_gaugeSpeedometerPartDef.get_NeedleColor()));
            	localDialCap.setOutlinePaint(ConvertUtil.toAwtColor(m_gaugeSpeedometerPartDef.get_NeedleColor()));
            }
            localPointer.setOutlinePaint(Color.black);
            localDialCap.setOutlinePaint(Color.black);
            plot.addPointer(localPointer);
            plot.setCap(localDialCap);
            
            plot.setBackgroundPaint(LinearGauge.getBackgroundPaint());
        }
        
        if (m_chartPanel!=null)
			m_chartPanel.dispose();
        
        m_chartPanel = new ChartPanel(this.get_MainArea(),SWT.NONE,LinearGauge);
        
        showPanel(m_chartPanel);
    }
	
	@Override
	public Object GetData(ILoadingStatusSink sink){
        DataTable busObDataTable = null;
        try{
            if (!super.getApi().get_SecurityService().HasBusObRight(super.get_ChartPartDef().get_BusObName(), SecurityRight.View)){
                super.ShowNoRightsAlert(super.get_ChartPartDef().get_BusObName());
                return busObDataTable;
            }
            busObDataTable = super.ExcuteStatisticRequest(super.get_ChartPartDef().get_Id(), super.get_ChartPartDef().get_Name(), super.get_ChartPartDef().get_Id(), super.get_CurrentRunTimeDateRange()).get_BusObDataTable();
        }catch (system.Exception exception){
            if (!super.get_TrapErrors()){
                throw exception;
            }
            super.OnAlertRaised(this, new AlertRaisedEventArgs(AlertType.Error, exception));
            return busObDataTable;
        }
        return busObDataTable;
    }

	@Override
	protected void ReQuery(ViewDef vViewDef, ViewDef vDateTimeDef){
        super.set_ParentDataTable(null);
        super.set_CurrentRunTimeDateRange(vDateTimeDef);
        this.LoadData();
    }
	
	private void showPanel(Composite p) {
		// TODO Auto-generated method stub
		this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
	}
	
}
