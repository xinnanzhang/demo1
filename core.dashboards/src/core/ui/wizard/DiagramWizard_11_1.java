package core.ui.wizard;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.util.HashSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.StandardGradientPaintTransformer;

import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;

import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DialStyle;
import Core.Dashboards.GradientStyle;

import siteview.windows.forms.ConvertUtil;
import system.Type;
import system.Drawing.KnownColor;
//import system.Drawing.Color;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import core.ui.dialog.OutlookCalendar;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.graphics.Image;

import org.eclipse.swt.widgets.Group;


public class DiagramWizard_11_1 extends WizardPage {

	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private String[] m_customColor = new String[10];

	private DiagramWizard diagramWizard;
	private static JFreeChart labelChart;
	
	private Text text_TitleTop;
	private Text text_TitleBottom;
	private Button check_TitleTop;
	private Button check_TitleBottom;
	
	private Label label_TitleChart;
	private Label label_BackChart;
	private Label label_ForeChart;
	
	
	private CCombo combo_GradStyle;
	private CCombo combo_MeterStyles;
	private TableCombo combo_NeedleColor;
	private TableCombo combo_BackColor;
	private TableCombo combo_SimpleColor;
	private TableCombo combo_GradStartColor;
	private TableCombo combo_GradEndColor;
	
	private Button radio_SimpleColor;
	private Button radio_GradColor;
	
	private DialPlot plot;
	private DialPointer.Pointer pointer;
	private DialBackground dialBack;

	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_11_1() {
		super("diagramWizard_11_1");
		setTitle("标题和指针的颜色选择");
		setDescription("标题，背景颜色，形式可以在这里改变。");
	}

	public DiagramWizard_11_1(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_11_1(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		labelChart = DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(), DiagramWizard_3.getChartType(chartPartDef.get_CategoryAsString()), null);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);

		Label label = new Label(container, SWT.NONE);
		label.setTouchEnabled(true);
		label.setText("\u63A7\u4EF6\u7684\u540D\u79F0");
//		label.setForeground(new Color(null, 0, 0, 255));
		label.setBounds(10, 345, 562, 41);
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setBounds(10, 10, 562, 329);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("\u6807\u9898");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		
		check_TitleTop = new Button(composite, SWT.CHECK);
		check_TitleTop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTitleChecked(check_TitleTop, text_TitleTop, 1, check_TitleTop.getSelection());
			}
		});
		check_TitleTop.setBounds(10, 10, 56, 16);
		check_TitleTop.setText("\u4E0A\u8FB9");
		
		text_TitleTop = new Text(composite, SWT.BORDER);
		text_TitleTop.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				onTitleTextModify(text_TitleTop, 1);
			}
		});
		text_TitleTop.setBounds(10, 32, 164, 18);
		
		check_TitleBottom = new Button(composite, SWT.CHECK);
		check_TitleBottom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTitleChecked(check_TitleBottom, text_TitleBottom, 2, check_TitleBottom.getSelection());
			}
		});
		check_TitleBottom.setText("\u5E95\u90E8");
		check_TitleBottom.setBounds(10, 56, 56, 16);
		
		text_TitleBottom = new Text(composite, SWT.BORDER);
		text_TitleBottom.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				onTitleTextModify(text_TitleBottom, 2);
			}
		});
		text_TitleBottom.setBounds(10, 78, 164, 18);
		
		label_TitleChart = new Label(composite, SWT.NONE);
		label_TitleChart.setBounds(243, 10, 301, 231);
		
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("\u80CC\u666F");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_1);
		
		label_BackChart = new Label(composite_1, SWT.NONE);
		label_BackChart.setText("New Label");
		label_BackChart.setBounds(243, 10, 301, 231);
		
		Group group = new Group(composite_1, SWT.NONE);
		group.setText("\u80CC\u666F");
		group.setBounds(10, 10, 209, 79);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 32, 30, 16);
		lblNewLabel.setText("\u989C\u8272");
		
		combo_BackColor = new TableCombo(group, SWT.BORDER | SWT.READ_ONLY);
		combo_BackColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onBackColorChanged();
			}
		});
		combo_BackColor.setBounds(52, 27, 147, 21);
		
		Group group_1 = new Group(composite_1, SWT.NONE);
		group_1.setText("\u9488\u989C\u8272");
		group_1.setBounds(10, 95, 209, 79);
		
		Label label_1 = new Label(group_1, SWT.NONE);
		label_1.setText("\u989C\u8272");
		label_1.setBounds(10, 32, 30, 16);
		
		combo_NeedleColor = new TableCombo(group_1, SWT.BORDER | SWT.READ_ONLY);
		combo_NeedleColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onNeedleColorChanged();
			}
		});
		combo_NeedleColor.setBounds(52, 27, 147, 21);
		
		TabItem tabItem_1 = new TabItem(tabFolder, 0);
		tabItem_1.setText("\u524D\u666F");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_2);
		
		label_ForeChart = new Label(composite_2, SWT.NONE);
		label_ForeChart.setText("New Label");
		label_ForeChart.setBounds(243, 10, 301, 231);
		
		Group group_2 = new Group(composite_2, SWT.NONE);
		group_2.setText("\u524D\u666F");
		group_2.setBounds(10, 10, 227, 200);
		
		Label label_2 = new Label(group_2, SWT.NONE);
		label_2.setText("\u5F00\u59CB\u989C\u8272");
		label_2.setBounds(10, 53, 54, 16);
		
		combo_GradStartColor = new TableCombo(group_2, SWT.BORDER | SWT.READ_ONLY);
		combo_GradStartColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onGradColorChanged();
			}
		});
		combo_GradStartColor.setBounds(70, 48, 147, 21);
		
		combo_GradEndColor = new TableCombo(group_2, SWT.BORDER | SWT.READ_ONLY);
		combo_GradEndColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onGradColorChanged();
			}
		});
		combo_GradEndColor.setBounds(70, 77, 147, 21);
		
		radio_GradColor = new Button(group_2, SWT.RADIO);
		radio_GradColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onGradColorChecked();
			}
		});
		radio_GradColor.setBounds(10, 21, 45, 16);
		radio_GradColor.setText("\u68AF\u5EA6");
		
		Label label_3 = new Label(group_2, SWT.NONE);
		label_3.setText("\u7ED3\u675F\u989C\u8272");
		label_3.setBounds(10, 82, 54, 16);
		
		Label label_4 = new Label(group_2, SWT.NONE);
		label_4.setText("\u7C7B\u578B");
		label_4.setBounds(10, 107, 54, 16);
		
		radio_SimpleColor = new Button(group_2, SWT.RADIO);
		radio_SimpleColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSimpleStyleChecked();
			}
		});
		radio_SimpleColor.setText("\u7B80\u5355");
		radio_SimpleColor.setBounds(10, 134, 45, 16);
		
		combo_GradStyle = new CCombo(group_2, SWT.BORDER);
		combo_GradStyle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onGradStyleChanged();
			}
		});
		combo_GradStyle.setBounds(70, 104, 147, 21);
		
		combo_SimpleColor = new TableCombo(group_2, SWT.BORDER | SWT.READ_ONLY);
		combo_SimpleColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onSimpleColorChanged();
			}
		});
		combo_SimpleColor.setBounds(70, 156, 147, 21);
		
		Label label_5 = new Label(group_2, SWT.NONE);
		label_5.setText("\u989C\u8272");
		label_5.setBounds(10, 161, 54, 16);
		
		Group group_3 = new Group(composite_2, SWT.NONE);
		group_3.setText("\u6837\u5F0F\u8868");
		group_3.setBounds(10, 216, 227, 79);
		
		Label label_6 = new Label(group_3, SWT.NONE);
		label_6.setText("\u6837\u5F0F");
		label_6.setBounds(10, 32, 30, 16);
		
		combo_MeterStyles = new CCombo(group_3, SWT.BORDER);
		combo_MeterStyles.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onMeterStyleChanged();
			}
		});
		combo_MeterStyles.setBounds(70, 27, 147, 21);

		initColorCombo();
		fillPorperties();
	}
	
	private void initColorCombo(){
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + str);
//			combo_NeedleColor.add(colorName);
//			combo_BackColor.add(colorName);
//			combo_SimpleColor.add(colorName);
//			combo_GradStartColor.add(colorName);
//			combo_GradEndColor.add(colorName);
//			
//			combo_NeedleColor.setData(colorName, str);
//			combo_BackColor.setData(colorName, str);
//			combo_SimpleColor.setData(colorName, str);
//			combo_GradStartColor.setData(colorName, str);
//			combo_GradEndColor.setData(colorName, str);
		
		if(!"New".equals(type))
		{
			ColorComboInit.Init(combo_NeedleColor, chartPartDef.get_NeedleColor().get_Name());
			ColorComboInit.Init(combo_BackColor, chartPartDef.get_BackColor().get_Name());
			ColorComboInit.Init(combo_SimpleColor, chartPartDef.get_ForeColor().get_Name());
			ColorComboInit.Init(combo_GradStartColor, chartPartDef.get_ForeGradientStartColor().get_Name());
			ColorComboInit.Init(combo_GradEndColor, chartPartDef.get_ForeGradientEndColor().get_Name());
		}else
		{
			ColorComboInit.Init(combo_NeedleColor, system.Drawing.Color.get_SteelBlue().get_Name());
			ColorComboInit.Init(combo_BackColor, system.Drawing.Color.get_White().get_Name());
			ColorComboInit.Init(combo_SimpleColor, system.Drawing.Color.get_LightSteelBlue().get_Name());
			ColorComboInit.Init(combo_GradStartColor, system.Drawing.Color.get_White().get_Name());
			ColorComboInit.Init(combo_GradEndColor, system.Drawing.Color.get_Black().get_Name());
		}
		
		
			
//			if(!"New".equals(type)){
//				if (str.equals(chartPartDef.get_LegendColor().get_Name()))
//					combo_NeedleColor.setText(colorName);
//				if (str.equals(chartPartDef.get_BackColor().get_Name()))
//					combo_BackColor.setText(colorName);
//			}
//		}
		
	}
	
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	

	@Override
	public IWizardPage getNextPage() {
		saveProperties();
		diagramWizard.addPage(new DiagramWizard_12_3(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
		return diagramWizard.getPage("diagramWizard_12_3");
	}
	
	private void fillPorperties() {
		LoadStyleList();
		// this.InitializeGaugeChart();
		if ("New".equals(type) || !chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)) {
			SetDefaultTitles();
			radio_SimpleColor.setSelection(true);
			combo_MeterStyles.setText(combo_MeterStyles.getItem(0));
//			combo_NeedleColor.setText(getColorString(system.Drawing.Color.get_SteelBlue()));
//			combo_BackColor.setText(getColorString(system.Drawing.Color.get_White()));
//			combo_SimpleColor.setText(getColorString(system.Drawing.Color.get_LightSteelBlue()));
//			combo_GradStartColor.setText(getColorString(system.Drawing.Color.get_White()));
//			combo_GradEndColor.setText(getColorString(system.Drawing.Color.get_Black()));
			
		} else {
			radio_SimpleColor.setSelection(chartPartDef.get_SimpleForeground());
			radio_GradColor.setSelection(chartPartDef.get_GradientForeground());
			check_TitleTop.setSelection(chartPartDef.get_TitleTopVisible());
			check_TitleBottom.setSelection(chartPartDef.get_TitleBottomVisible());
			text_TitleTop.setText(chartPartDef.get_TitleTop());
			text_TitleBottom.setText(chartPartDef.get_TitleBottom());
//			combo_BackColor.setText(getColorString(chartPartDef.get_BackColor()));
			combo_MeterStyles.setText(EntoCh(chartPartDef.get_DialStyle().toString()));
//			combo_GradStartColor.setText(getColorString(chartPartDef.get_ForeGradientStartColor()));
//			combo_GradEndColor.setText(getColorString(chartPartDef.get_ForeGradientEndColor()));
			combo_GradStyle.setText(EntoCh(chartPartDef.get_ForeGradientType().toString()));
//			combo_SimpleColor.setText(getColorString(chartPartDef.get_ForeColor()));
//			combo_NeedleColor.setText(getColorString(chartPartDef.get_NeedleColor()));
			
		}
		boolean flag = radio_SimpleColor.getSelection();
		combo_SimpleColor.setEnabled(flag);
		combo_GradStartColor.setEnabled(!flag);
		combo_GradEndColor.setEnabled(!flag);
		combo_GradStyle.setEnabled(!flag);
		
//		combo_NeedleColor.setBackground(OutlookCalendar.getBackgroudColor(combo_NeedleColor));
//		combo_BackColor.setBackground(OutlookCalendar.getBackgroudColor(combo_BackColor));
//		combo_SimpleColor.setBackground(OutlookCalendar.getBackgroudColor(combo_SimpleColor));
//		combo_GradStartColor.setBackground(OutlookCalendar.getBackgroudColor(combo_GradStartColor));
//		combo_GradEndColor.setBackground(OutlookCalendar.getBackgroudColor(combo_GradEndColor));
		
		initLabelChart();
		showAllChart();
	}
	
	private String getColorString(system.Drawing.Color color){
		return Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + color.name);
	}
	
	private system.Drawing.Color getColorFromCombo(TableCombo combo){
		String colorName = (String) combo.getData(combo.getText());
		colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		return system.Drawing.Color.FromName(colorName);
	}
	
	private void LoadStyleList() {

		combo_GradStyle.removeAll();
		combo_MeterStyles.removeAll();
		
		for (GradientStyle style : Core.Dashboards.GradientStyle.values()) {
			String styleName = EntoCh(style.name());
			combo_GradStyle.add(styleName);
			combo_GradStyle.setData(styleName, style);
		}
		combo_GradStyle.setData("", GradientStyle.None);
		
		for (DialStyle style2 : Core.Dashboards.DialStyle.values()) {
			String styleName2 = EntoCh(style2.name());
			combo_MeterStyles.add(styleName2);
			combo_MeterStyles.setData(styleName2, style2.name());
		}

	}
	
	private void SetDefaultTitles() {
		String str = "";
		String fieldName = "";
		if ("New".equals(type)) {
			check_TitleTop.setSelection(true);
			text_TitleTop.setText(chartPartDef.get_TitleBarDef().get_Text());

			if (chartPartDef.get_SubGroupByDef().get_FieldName().equals("")) {
				str = chartPartDef.get_GroupByDef().get_Alias();
			} else {
				str = String.format(core.dashboards.Res.get_Default().GetString("Chart.DefaultTitleStringWith2GroupBy"), 
						chartPartDef.get_GroupByDef().get_Alias(), chartPartDef.get_SubGroupByDef().get_Alias());
			}
			check_TitleBottom.setSelection(true);
			text_TitleBottom.setText(str);
			fieldName = chartPartDef.get_EvaluateByDef().get_FieldName();
			check_TitleTop.setSelection(true);
			text_TitleTop.setText(fieldName);
		}

	}
	 
	private String EntoCh(String en) {
		String ch = "";
		if (en == "BackwardDiagonal") {
			ch = "向后对角线";
			
		} else if (en == "Circular") {
			ch = "圆形";
			
		} else if (en == "Default") {
			ch = "默认";
			
		} else if (en == "Elliptical") {
			ch = "椭圆形";
			
		} else if (en == "ForwardDiagonal") {
			ch = "向前对角线";
			
		} else if (en == "Horizontal") {
			ch = "水平";
			
		} else if (en == "HorizontalBump") {
			ch = "水平凸起型";
			
		} else if (en == "None") {
			ch = "无";
			
		} else if (en == "Raise") {
			ch = "上升";
			
		} else if (en == "Rectangular") {
			ch = "矩形";
			
		} else if (en == "Vertical") {
			ch = "垂直";
			
		} else if (en == "VerticalBump") {
			ch = "垂直凸起";
			
		} else if (en == "Full") {
			ch = "全部";
			
		} else if (en == "Half") {
			ch = "一半";
			
		} else if (en == "Quarter") {
			ch = "四分之一";
			
		}

		return ch;

	}
	
	private void initLabelChart() {
		labelChart = DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(), DiagramWizard_3.getChartType(chartPartDef.get_CategoryAsString()), null);
		TextTitle title1 = new TextTitle();
		TextTitle title2 = new TextTitle();
		
		title1.setPosition(RectangleEdge.TOP);
		title2.setPosition(RectangleEdge.BOTTOM);
		
		title1.setText("");
		title2.setText("");
		
		if (check_TitleTop.getSelection() && !"".equals(text_TitleTop.getText().trim())) 
			title1.setText(text_TitleTop.getText().trim());
		if (check_TitleBottom.getSelection() && !"".equals(text_TitleBottom.getText().trim())) 
			title2.setText(text_TitleBottom.getText().trim());
		
		labelChart.addSubtitle(1, title1);
		labelChart.addSubtitle(2, title2);
		
		labelChart.setBackgroundPaint(getPaintFormCombo(combo_BackColor));
		
		//设置指针颜色
		plot = (DialPlot)labelChart.getPlot();
		pointer = (DialPointer.Pointer)plot.getPointerForDataset(0);
		pointer.setOutlinePaint(Color.black);
		pointer.setFillPaint(getPaintFormCombo(combo_NeedleColor));
		
		//设置表盘式样与颜色
		dialBack = (DialBackground)plot.getBackground();
		
		if(radio_SimpleColor.getSelection()){
			dialBack.setPaint(getPaintFormCombo(combo_SimpleColor))	;
		
		}else{
			GradientPaint localGradientPaint = new GradientPaint(new Point(), getPaintColorToGrad(combo_GradStartColor), new Point(), getPaintColorToGrad(combo_GradEndColor));
			dialBack.setPaint(localGradientPaint);
			
			GradientPaintTransformType paintType = getGradientType((GradientStyle) combo_GradStyle.getData(combo_GradStyle.getText()));
			if(paintType == null) 
				dialBack.setGradientPaintTransformer(new StandardGradientPaintTransformer());
			else 
				dialBack.setGradientPaintTransformer(new StandardGradientPaintTransformer(paintType));
		}
		
	 }
	
	private Paint getPaintFormCombo(TableCombo combo){
		String colorName = (String) combo.getData(combo.getText());
		colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		return ConvertUtil.toAwtColor(system.Drawing.Color.FromName(colorName));
	}
	
	private Color getPaintColorToGrad(TableCombo combo){
		String colorName = (String) combo.getData(combo.getText());
		colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		system.Drawing.Color colorStart = system.Drawing.Color.FromName(colorName);
		return new Color(colorStart.get_R(), colorStart.get_G(), colorStart.get_B());
	}
	
	private GradientPaintTransformType getGradientType(GradientStyle style){
		
		switch(style){
		
		case Horizontal:
			return GradientPaintTransformType.CENTER_VERTICAL;
		case VerticalBump:
			return GradientPaintTransformType.CENTER_VERTICAL;
		case HorizontalBump:
			return GradientPaintTransformType.CENTER_HORIZONTAL;
		case None:
			return null;
		}
		return GradientPaintTransformType.VERTICAL;
	}
	
	private void onTitleChecked(Button titleChecked, Text titleText, int type, boolean flag) {
		titleText.setEnabled(flag);
		TextTitle title = (TextTitle) labelChart.getSubtitle(type);
		if (flag) {
			title.setText(titleText.getText().trim());
		} else {
			title.setText("");
		}
		showAllChart();
	}
	 
	private void onTitleTextModify(Text titleText, int type){
		 if(labelChart.getSubtitleCount() > 1){
			 TextTitle title = (TextTitle)labelChart.getSubtitle(type);
			 title.setText(titleText.getText().trim());
			 showAllChart();
		 }
	 }
	 
	private void onBackColorChanged() {
		labelChart.setBackgroundPaint(getPaintFormCombo(combo_BackColor));
		showAllChart();
	}
	 
	private void onNeedleColorChanged() {
		pointer.setFillPaint(getPaintFormCombo(combo_NeedleColor));
		showAllChart();
	}

	private void onGradColorChecked() {
		combo_SimpleColor.setEnabled(false);
		combo_GradStartColor.setEnabled(true);
		combo_GradEndColor.setEnabled(true);
		combo_GradStyle.setEnabled(true);

		onGradColorChanged();
		onGradStyleChanged();
		showAllChart();
	}
	
	private void onGradColorChanged(){
		GradientPaint localGradientPaint = new GradientPaint(new Point(), getPaintColorToGrad(combo_GradStartColor), new Point(), getPaintColorToGrad(combo_GradEndColor));
		dialBack.setPaint(localGradientPaint);
		showAllChart();
	}
	
	private void onGradStyleChanged() {
		GradientPaintTransformType paintType = getGradientType((GradientStyle) combo_GradStyle.getData(combo_GradStyle.getText()));
		if(paintType == null) 
			dialBack.setGradientPaintTransformer(new StandardGradientPaintTransformer());
		else 
			dialBack.setGradientPaintTransformer(new StandardGradientPaintTransformer(paintType));
		showAllChart();
	}

	private void onSimpleColorChanged() {
		dialBack.setPaint(getPaintFormCombo(combo_SimpleColor));
		showAllChart();
	}

	private void onSimpleStyleChecked() {
		combo_SimpleColor.setEnabled(true);
		combo_GradStartColor.setEnabled(false);
		combo_GradEndColor.setEnabled(false);
		combo_GradStyle.setEnabled(false);
		dialBack.setPaint(getPaintFormCombo(combo_NeedleColor));
		showAllChart();
	}
	
	private void onMeterStyleChanged(){
	}
	
	private GradientStyle getStyleByCombo(CCombo combo){
		GradientStyle styleName = (GradientStyle)combo.getData(combo.getText());
		
		if("".equals(combo.getText())){
			return GradientStyle.BackwardDiagonal;
		}else{
			return styleName;
		}
		
	}
	 
	
	private void showAllChart(){
		Image chartIamge = DiagramWizard_3.initImage(labelChart, 300, 230);
		label_TitleChart.setImage(chartIamge);
		label_ForeChart.setImage(chartIamge);
		label_BackChart.setImage(chartIamge);
	}
	
	public static JFreeChart getLabelChart() {
		return labelChart;
	}

	private void saveProperties() {
		chartPartDef.set_TitleTopVisible(check_TitleTop.getSelection());
        chartPartDef.set_TitleBottomVisible(check_TitleBottom.getSelection());
        chartPartDef.set_TitleLeftVisible(false);
        chartPartDef.set_TitleRightVisible(false);
        chartPartDef.set_TitleTop(text_TitleTop.getText().trim());
        chartPartDef.set_TitleBottom(text_TitleBottom.getText().trim());
        chartPartDef.set_TitleLeft("");
        chartPartDef.set_TitleRight("");
        chartPartDef.set_NeedleColor(getColorFromCombo(combo_NeedleColor));
        chartPartDef.set_DialStyle(DialStyle.valueOf((String)combo_MeterStyles.getData(combo_MeterStyles.getText())));
        chartPartDef.set_BackColor(getColorFromCombo(combo_BackColor));
        chartPartDef.set_SimpleForeground(radio_SimpleColor.getSelection());
        chartPartDef.set_GradientForeground(radio_GradColor.getSelection());
        chartPartDef.set_ForeColor(getColorFromCombo(combo_SimpleColor));
        chartPartDef.set_ForeGradientStartColor(getColorFromCombo(combo_GradStartColor));
        chartPartDef.set_ForeGradientEndColor(getColorFromCombo(combo_GradEndColor));
        chartPartDef.set_ForeGradientType(getStyleByCombo(combo_GradStyle));
        
//        getCustomColorFromTheme(chartPartDef.get_ColorSetName());
//		chartPartDef.set_CustomColor1(m_customColor[0]);
//		chartPartDef.set_CustomColor2(m_customColor[1]);
//		chartPartDef.set_CustomColor3(m_customColor[2]);
//		chartPartDef.set_CustomColor4(m_customColor[3]);
//		chartPartDef.set_CustomColor5(m_customColor[4]);
//		chartPartDef.set_CustomColor6(m_customColor[5]);
//		chartPartDef.set_CustomColor7(m_customColor[6]);
//		chartPartDef.set_CustomColor8(m_customColor[7]);
//		chartPartDef.set_CustomColor9(m_customColor[8]);
//		chartPartDef.set_CustomColor10(m_customColor[9]);
        
        
	}
	
	private void getCustomColorFromTheme(String colorSetName)
    {
        String str = "Core.ChartColorTheme." + colorSetName;
        this.m_customColor[0] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color1");
        this.m_customColor[1] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color2");
        this.m_customColor[2] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color3");
        this.m_customColor[3] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color4");
        this.m_customColor[4] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color5");
        this.m_customColor[5] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color6");
        this.m_customColor[6] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color7");
        this.m_customColor[7] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color8");
        this.m_customColor[8] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color9");
        this.m_customColor[9] = m_Api.get_SettingsService().GetSettingAsString(str + ".Color10");
    }
}
