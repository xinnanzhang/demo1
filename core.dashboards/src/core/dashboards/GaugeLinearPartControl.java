package core.dashboards;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ThermometerPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.ui.RectangleEdge;

import siteview.windows.forms.ConvertUtil;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Convert;
import system.Data.DataTable;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRange;
import Core.Dashboards.GaugeLinearPartDef;
import Core.Dashboards.GaugeRegionDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.ViewDef;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.Api.ISiteviewApi;
import Siteview.Xml.SecurityRight;

public class GaugeLinearPartControl extends ChartPartControl {

	public GaugeLinearPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);
		
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);
	}

	private JFreeChart LinearGauge;
	private GaugeLinearPartDef m_GaugeLinearPartDef;
	private ChartPanel m_chartPanel;
	private StackLayout stacklayout;
	
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
        super.DefineFromDef(def, partRefDef);
        GaugeLinearPartDef dashboardPartDef = (GaugeLinearPartDef) super.get_DashboardPartDef();
        if (dashboardPartDef != null){
        	this.m_GaugeLinearPartDef = dashboardPartDef;
            super.set_DateRangeControlVisible(dashboardPartDef.get_DateRangeControlVisible() && dashboardPartDef.get_DefaultDateRangeDef().get_ApplyDateRange());
            if (super.get_DateRangeControlVisible())
            {
                for (ViewDef def3 : dashboardPartDef.get_DateRangeOptions()){
                    if (def3.get_DateRange() != DateRange.None){
                        this.AddDateTimeRange(def3);
                    }
                }
                super.SetDefaultDateRange(dashboardPartDef.get_DefaultDateRangeDef());
            }
            super.SetSelectionArea();
        }
    }
	
	@Override
	public void DataBind(Object dt){
        super.DataBind(dt);
        super.set_ParentDataTable((DataTable) dt);
        if (super.get_ParentDataTable() != null){
        	double Maximum = 0.0;
        	DefaultValueDataset dataset = new DefaultValueDataset();
        	Maximum = super.get_ChartPartDef().get_Maximum();
            if (Convert.ToInt32(super.get_ParentDataTable().get_Rows().get_Item(0).get_Item(0)) > Convert.ToInt32(super.get_ChartPartDef().get_Maximum())){
                Maximum = Convert.ToInt32(((DataTable)dt).get_Rows().get_Item(0).get_Item(0));
            }
            dataset.setValue(Convert.ToInt32(((DataTable)dt).get_Rows().get_Item(0).get_Item(0)));
            
            ThermometerPlot plot = new ThermometerPlot(dataset);
            
            this.LinearGauge = new JFreeChart("",
            								JFreeChart.DEFAULT_TITLE_FONT,
            								plot,
            								m_GaugeLinearPartDef.get_LegendVisible());
            /*----------设置消除字体的锯齿渲染（解决中文问题）--------------*/ 
    		LinearGauge.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF); 
            LinearGauge.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage(m_GaugeLinearPartDef.get_BackImageName())));
            if(m_GaugeLinearPartDef.get_LegendVisible()){
                LinearGauge.getLegend().setItemFont(new Font("simsun", Font.CENTER_BASELINE, 12));
                LinearGauge.getLegend().setPosition(RectangleEdge.RIGHT);
            }
            
            TextTitle subtitle = new TextTitle(m_GaugeLinearPartDef.get_Alias(), new Font("simsun", Font.CENTER_BASELINE, 12));
            LinearGauge.addSubtitle(subtitle);

            plot.setBackgroundPaint(LinearGauge.getBackgroundPaint());
            plot.setThermometerStroke(new BasicStroke(2.0f));
            plot.setThermometerPaint(Color.lightGray);
            
            plot.setOutlinePaint(null);
            plot.setGap(3);
            plot.setUnits(0);
            plot.setFollowDataInSubranges(true);
            //设置区间显示色
            plot.setRange(m_GaugeLinearPartDef.get_Minimum(), Maximum);
            plot.setSubrange(0, m_GaugeLinearPartDef.get_Minimum(), m_GaugeLinearPartDef.get_LowerThreshold());
            plot.setSubrangePaint(0, ConvertUtil.toAwtColor(m_GaugeLinearPartDef.get_LowerThresholdColor()));
            plot.setSubrange(1, m_GaugeLinearPartDef.get_LowerThreshold(), m_GaugeLinearPartDef.get_UpperThreshold());
            plot.setSubrangePaint(1, ConvertUtil.toAwtColor(m_GaugeLinearPartDef.get_MiddleThresholdColor()));
            plot.setSubrange(2, m_GaugeLinearPartDef.get_UpperThreshold(), Maximum);
            plot.setSubrangePaint(2, ConvertUtil.toAwtColor(m_GaugeLinearPartDef.get_UpperThresholdColor()));
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
