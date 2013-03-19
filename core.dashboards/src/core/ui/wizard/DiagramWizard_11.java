package core.ui.wizard;

import java.awt.Paint;

import java.util.HashSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabItem;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.ui.RectangleEdge;

import Siteview.ColorResolver;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Windows.Forms.Design.ColorComboInit;

import Core.Dashboards.ChartPartDef;
import Core.Dashboards.ChartType;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.LegendLocation;

import siteview.windows.forms.ConvertUtil;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Type;
import system.Drawing.KnownColor;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import core.ui.dialog.OutlookCalendar;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;

import com.lowagie.text.Rectangle;


public class DiagramWizard_11 extends WizardPage {

	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;

	private DiagramWizard diagramWizard;
	private static JFreeChart labelChart;
	
	private Text text_TitleTop;
	private Text text_TitleBottom;
	private Text text_TitleRight;
	private Text text_TitleLeft;
	private Button check_TitleTop;
	private Button check_TitleBottom;
	private Button check_TitleRight;
	private Button check_TitleLeft;
	
	private Button check_ShowLegend;
	private Combo combo_LegendLocation;
	private TableCombo combo_LegendColor;
	private Combo combo_ColorSet;
	
	private Button radio_ImageStyle;
	private Combo combo_ImageStyle;
	private Button radio_BackColor;
	private TableCombo combo_BackColor;
	private Label label_ImageSelector;
	private Menu popMenu;
	private Label label_ImageName;
	
	private Label label_TitleChart;
	private Label label_LegendChart;
	private Label label_BackgroundChart;
	private Label label_ColorModelChart;
	
	private MouseAdapter menuListener; //浏览图片菜单 监听
	
	private String[] m_customColor = new String[10];

	/**
	 * @wbp.parser.constructor
	 */
	public DiagramWizard_11() {
		super("diagramWizard_11");
		setTitle("标题，图例和背景选择");
		setDescription("图表控件可在右侧，左侧，顶部或底部同时显示4个不同的标题的1个图例");
	}

	public DiagramWizard_11(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_11(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
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
		initShowMenuListener();//初始化图片浏览监听
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
		tabFolder.setBounds(10, 10, 562, 286);
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("\u6807\u9898");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite);
		
		check_TitleTop = new Button(composite, SWT.CHECK);
		check_TitleTop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_TitleTop.setEnabled(check_TitleTop.getSelection());
				onTitleChecked(check_TitleTop, text_TitleTop, RectangleEdge.TOP, 1, check_TitleTop.getSelection());
			}
		});
		check_TitleTop.setBounds(10, 10, 56, 16);
		check_TitleTop.setText("\u9876\u90E8");
		
		text_TitleTop = new Text(composite, SWT.BORDER);
		text_TitleTop.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				onTitleTextModify(text_TitleTop, RectangleEdge.TOP, 1);
			}
		});
		text_TitleTop.setBounds(10, 32, 164, 18);
		
		check_TitleBottom = new Button(composite, SWT.CHECK);
		check_TitleBottom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_TitleBottom.setEnabled(check_TitleBottom.getSelection());
				onTitleChecked(check_TitleBottom, text_TitleBottom, RectangleEdge.BOTTOM, 2, check_TitleBottom.getSelection());
			}
		});
		check_TitleBottom.setText("\u5E95\u90E8");
		check_TitleBottom.setBounds(10, 56, 56, 16);
		
		text_TitleBottom = new Text(composite, SWT.BORDER);
		text_TitleBottom.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				onTitleTextModify(text_TitleBottom, RectangleEdge.BOTTOM, 2);
			}
		});
		text_TitleBottom.setBounds(10, 78, 164, 18);
		
		check_TitleLeft = new Button(composite, SWT.CHECK);
		check_TitleLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_TitleLeft.setEnabled(check_TitleLeft.getSelection());
				onTitleChecked(check_TitleLeft, text_TitleLeft, RectangleEdge.LEFT, 3, check_TitleLeft.getSelection());
			}
		});
		check_TitleLeft.setText("\u5DE6\u4FA7");
		check_TitleLeft.setBounds(10, 102, 56, 16);
		
		text_TitleLeft = new Text(composite, SWT.BORDER);
		text_TitleLeft.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				onTitleTextModify(text_TitleLeft, RectangleEdge.LEFT, 3);
			}
		});
		text_TitleLeft.setBounds(10, 123, 164, 18);
		
		check_TitleRight = new Button(composite, SWT.CHECK);
		check_TitleRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text_TitleRight.setEnabled(check_TitleRight.getSelection());
				onTitleChecked(check_TitleRight, text_TitleRight, RectangleEdge.RIGHT, 4, check_TitleRight.getSelection());
			}
		});
		check_TitleRight.setText("\u53F3\u4FA7");
		check_TitleRight.setBounds(10, 147, 56, 16);
		
		text_TitleRight = new Text(composite, SWT.BORDER);
		text_TitleRight.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				onTitleTextModify(text_TitleRight, RectangleEdge.RIGHT, 4);
			}
		});
		text_TitleRight.setBounds(10, 169, 164, 18);
		
		label_TitleChart = new Label(composite, SWT.NONE);
		label_TitleChart.setBounds(243, 10, 301, 231);
		
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("\u56FE\u4F8B");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite_1);
		
		check_ShowLegend = new Button(composite_1, SWT.CHECK);
		check_ShowLegend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onShowLegendChecked(check_ShowLegend.getSelection());
			}
		});
		check_ShowLegend.setBounds(10, 10, 93, 16);
		check_ShowLegend.setText("\u663E\u793A\u56FE\u4F8B");
		
		Label lblNewLabel = new Label(composite_1, SWT.NONE);
		lblNewLabel.setBounds(10, 42, 54, 16);
		lblNewLabel.setText("\u4F4D\u7F6E:");
		
		combo_LegendLocation = new Combo(composite_1, SWT.BORDER | SWT.READ_ONLY);
		combo_LegendLocation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onLegendLocationSelected();
			}
		});
		combo_LegendLocation.setBounds(82, 37, 108, 21);
		
		Label label_1 = new Label(composite_1, SWT.NONE);
		label_1.setText("\u989C\u8272:");
		label_1.setBounds(10, 77, 54, 16);
		
		combo_LegendColor = new TableCombo(composite_1, SWT.BORDER | SWT.READ_ONLY);
		combo_LegendColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onLegendColorSelected();
			}
		});
		combo_LegendColor.setBounds(10, 99, 200, 21);
		
		label_LegendChart = new Label(composite_1, SWT.NONE);
		label_LegendChart.setText("New Label");
		label_LegendChart.setBounds(243, 10, 301, 231);
		
		TabItem tabItem_1 = new TabItem(tabFolder, 0);
		tabItem_1.setText("\u989C\u8272\u6A21\u578B");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tabItem_1.setControl(composite_2);
		
		Label lblNewLabel_1 = new Label(composite_2, SWT.NONE);
		lblNewLabel_1.setBounds(21, 26, 111, 16);
		lblNewLabel_1.setText("\u8BF7\u9009\u62E9\u989C\u8272\u65B9\u6848:");
		
		combo_ColorSet = new Combo(composite_2, SWT.BORDER | SWT.READ_ONLY);
		combo_ColorSet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onColorSetSelected();
			}
		});
		combo_ColorSet.setBounds(21, 58, 162, 21);
		
		label_ColorModelChart = new Label(composite_2, SWT.NONE);
		label_ColorModelChart.setText("New Label");
		label_ColorModelChart.setBounds(243, 10, 301, 231);
		
		TabItem tabItem_2 = new TabItem(tabFolder, 0);
		tabItem_2.setText("\u80CC\u666F");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tabItem_2.setControl(composite_3);
		
		radio_ImageStyle = new Button(composite_3, SWT.RADIO);
		radio_ImageStyle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onImageStyleSelected();
			}
		});
		radio_ImageStyle.setBounds(10, 10, 50, 16);
		radio_ImageStyle.setText("\u56FE\u50CF");
		
		label_ImageSelector = new Label(composite_3, SWT.NONE);
//		label_ImageSelector.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseUp(MouseEvent e) {
//				if(e.button == 1){
//					popMenu = createPopMenu().createContextMenu(label_ImageSelector);
//					popMenu.setLocation(label_ImageSelector.toDisplay(e.x,e.y)); //定位
//					popMenu.setVisible(true);
//				}
//			}
//		});
		label_ImageSelector.setBounds(61, 8, 17, 18);
		label_ImageSelector.setText("");
		label_ImageSelector.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.navigate_down.png"),0x12,0x12));
		
		label_ImageName = new Label(composite_3, SWT.NONE);
		label_ImageName.setLocation(117, 12);
		label_ImageName.setSize(237, 16);
		label_ImageName.setText("无");
		
		Label lblNewLabel_2 = new Label(composite_3, SWT.NONE);
		lblNewLabel_2.setBounds(10, 40, 54, 16);
		lblNewLabel_2.setText("\u6837\u5F0F");
		
		combo_ImageStyle = new Combo(composite_3, SWT.BORDER | SWT.READ_ONLY);
		combo_ImageStyle.setBounds(82, 35, 102, 21);
		
		radio_BackColor = new Button(composite_3, SWT.RADIO);
		radio_BackColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				combo_ImageStyle.setEnabled(false);
				combo_BackColor.setEnabled(true);
				label_ImageSelector.removeMouseListener(menuListener);
				labelChart.setBackgroundImage(null);
				onBackColorSelected();
			}
		});
		
		radio_BackColor.setText("\u989C\u8272");
		radio_BackColor.setBounds(10, 69, 54, 16);

		combo_BackColor = new TableCombo(composite_3, SWT.BORDER | SWT.READ_ONLY);
		combo_BackColor.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onBackColorSelected();
			}
		});
		combo_BackColor.setBounds(10, 98, 200, 21);
		
		label_BackgroundChart = new Label(composite_3, SWT.NONE);
		label_BackgroundChart.setText("New Label");
		label_BackgroundChart.setBounds(254, 31, 300, 230);

		
		initCombos();
		if("New".equals(type) || !chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType))	fillOnNew();
		else	fillProperties();
		
		initLabelChart();
		showAllChart();
	}
	
	
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	

	@Override
	public IWizardPage getNextPage() {
		saveProperties();
		if(!"CHARTPIE".equals(chartPartDef.get_CategoryAsString())){
			diagramWizard.addPage(new DiagramWizard_12_1(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
			return diagramWizard.getPage("diagramWizard_12_1");
		}
		diagramWizard.addPage(new DiagramWizard_12(diagramWizard, chartPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
		return diagramWizard.getPage("diagramWizard_12");
	}
	
	private void initCombos(){
		
		//---legend颜色 下拉框---
//		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class.getName()));
//		for (String str : s) {
//			String colorName = Siteview.Windows.Forms.Res.get_Default().GetString("SiteviewColorEditor" + "." + str);
//			combo_LegendColor.add(colorName);
//			combo_BackColor.add(colorName);
//			combo_LegendColor.setData(colorName, str);
//			combo_BackColor.setData(colorName, str);
//			if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType)){
//				if (str.equals(chartPartDef.get_LegendColor().get_Name()))
//					combo_LegendColor.setText(colorName);
//				if (str.equals(chartPartDef.get_BackColor().get_Name()))
//					combo_BackColor.setText(colorName);
//			}
//		}
//		combo_LegendColor.setBackground(OutlookCalendar.getBackgroudColor(combo_LegendColor));
//		combo_BackColor.setBackground(OutlookCalendar.getBackgroudColor(combo_BackColor));
		if(!"New".equals(type) && chartPartDef.get_CategoryAsString().equals(DiagramWizard_3.EidtChartType))
		{
			ColorComboInit.Init(combo_BackColor, chartPartDef.get_BackColor().get_Name());
			ColorComboInit.Init(combo_LegendColor, chartPartDef.get_LegendColor().get_Name());
		}
		
		//----------legend location ----------------
				
		combo_LegendLocation.setItems(new String[]{"右侧", "左侧", "顶部", "底部"});	
		combo_LegendLocation.setData("右侧", LegendLocation.Right);
		combo_LegendLocation.setData("左侧", LegendLocation.Left);
		combo_LegendLocation.setData("顶部", LegendLocation.Top);
		combo_LegendLocation.setData("底部", LegendLocation.Bottom);
		
		//----------------color set-------------------------
		combo_ColorSet.setItems(new String[]{"默认", "强调", "亘古的大自然", "新型艺术", "秋季", "浏览器安全一", "浏览器安全二", "冷色调",
				"文化", "深色调", "折衷", "时尚", "快速检索储存器", "历史组合", "热情的大自然", "主", "混合组合", "首选", "进化的大自然",
				"安静的色调", "丰富的色调", "大滨菊", "城市色调"});
		
		String[] colorSetList = new String[]{"Default", "Accent", "AgedNaturals", "ArtNouveau", "Autumn", "BrowserSafeI",
				"BrowserSafeII", "CoolHues", "Culture", "DarkenTones", "EclecticMix", "Fashion", "FRS", "HistoricalCombinations", "IntenseNaturals",
				"Masters", "MixedCombinations", "Primary", "ProgressiveNaturals", "QuietTones", "RichHues", "Shasta", "UrbanHues"};
		for(int i = 0; i < combo_ColorSet.getItems().length; i++){
			combo_ColorSet.setData(combo_ColorSet.getItems()[i], colorSetList[i]);
		}
		//------------------imageStyle-------------------------
		combo_ImageStyle.setItems(new String[]{"扩展", "平铺", "居中"});
		
	}
	
	private void fillOnNew(){
		check_TitleTop.setSelection(true);
		check_TitleBottom.setSelection(true);
		check_TitleLeft.setSelection(true);
		
		String str = "";
        BusinessObjectDef businessObjectDef = null;
        businessObjectDef = m_Api.get_BusObDefinitions().GetBusinessObjectDef(chartPartDef.get_BusObName());
        if(businessObjectDef != null){
        	FieldDef def3 = businessObjectDef.GetField(chartPartDef.get_GroupByDef().get_FieldName());
            if(def3 != null){
            	str = businessObjectDef.get_Alias() + '.' + def3.get_Alias();
            }
        }
        text_TitleBottom.setText(str);
        text_TitleLeft.setText(chartPartDef.get_EvaluateByDef().get_FieldName());
        text_TitleRight.setEnabled(false);
//     	--------------------------------------
        check_ShowLegend.setSelection(true);
        combo_LegendLocation.setText(core.dashboards.Res.get_Default().GetString("Chart.LegendLocationRight"));
//        combo_LegendColor.setText("白烟色");
		ColorComboInit.Init(combo_LegendColor, system.Drawing.Color.get_WhiteSmoke().get_Name());
//     	-----------------------------------------
        combo_ColorSet.setText(combo_ColorSet.getItem(0));
//      ------------------------------
        radio_BackColor.setSelection(true);
//        combo_BackColor.setText("选择颜色...");
        ColorComboInit.Init(combo_BackColor, "");
	}
	
	private void fillProperties(){
		check_TitleTop.setSelection(chartPartDef.get_TitleTopVisible());
		check_TitleBottom.setSelection(chartPartDef.get_TitleBottomVisible());
		check_TitleRight.setSelection(chartPartDef.get_TitleRightVisible());
		check_TitleLeft.setSelection(chartPartDef.get_TitleLeftVisible());
		
		text_TitleTop.setEnabled(check_TitleTop.getSelection());
		text_TitleBottom.setEnabled(check_TitleBottom.getSelection());
		text_TitleRight.setEnabled(check_TitleRight.getSelection());
		text_TitleLeft.setEnabled(check_TitleLeft.getSelection());
		
		text_TitleTop.setText(chartPartDef.get_TitleTop());
		text_TitleBottom.setText(chartPartDef.get_TitleBottom());
		text_TitleRight.setText(chartPartDef.get_TitleRight());
		text_TitleLeft.setText(chartPartDef.get_TitleLeft());
		
		check_ShowLegend.setSelection(chartPartDef.get_LegendVisible());
		combo_LegendLocation.setEnabled(check_ShowLegend.getSelection());
		combo_LegendColor.setEnabled(check_ShowLegend.getSelection());
		combo_LegendLocation.setText(getLegendLocationStr(chartPartDef.get_LegendLocation()));
		
		String defColorSet = chartPartDef.get_ColorSetName().replace(" ", "");				//去掉空格
		for(int i = 0; i < combo_ColorSet.getItems().length; i++){
			if(combo_ColorSet.getData(combo_ColorSet.getItems()[i]).equals(defColorSet) ||
					combo_ColorSet.getItems()[i].equals(chartPartDef.get_ColorSetName()))
				combo_ColorSet.setText(combo_ColorSet.getItems()[i]);
		}
//		combo_LegendColor.setText(chartPartDef.get_LegendColor().get_Name());
//		combo_ImageStyle.setText(chartPartDef.get_BackImageStyle().name());
		if(chartPartDef.get_BackImageName() == null){
			radio_BackColor.setSelection(true);
			combo_ImageStyle.setEnabled(false);
		}else{
			label_ImageSelector.addMouseListener(menuListener);//添加监听
			radio_ImageStyle.setSelection(true);
			combo_BackColor.setEnabled(false);
			label_ImageName.setText(ImageChooseDlg.getInitImageStr(chartPartDef.get_BackImageName()));
			label_ImageName.setData(label_ImageName.getText(), chartPartDef.get_BackImageName());
		}
		
		
		//-----------------------填充图片------------------------
//		labelChart = DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(), ChartType.PieChart, chartPartDef, SetCustomColorFromTheme("Core.ChartColorTheme." + (String)combo_ColorSet.getData(combo_ColorSet.getText())));
//		//-----------设置title---- 1=Top ; 2=bottom ; 3=left ; 4=right ---------
////		jfreeChart.clearSubtitles();
//		if(check_TitleTop.getSelection() && !"".equals(text_TitleTop.getText().trim())) {
//			TextTitle title = new TextTitle();
//			title.setPosition(RectangleEdge.TOP);
//			title.setText(text_TitleTop.getText().trim());
//			labelChart.addSubtitle(1, title);
//		}
//		if(check_TitleBottom.getSelection() && !"".equals(text_TitleBottom.getText().trim())) {
//			TextTitle title = new TextTitle();
//			title.setPosition(RectangleEdge.BOTTOM);
//			title.setText(text_TitleBottom.getText().trim());
//			labelChart.addSubtitle(2, title);
//		}
//		if(check_TitleLeft.getSelection() && !"".equals(text_TitleLeft.getText().trim())) {
//			TextTitle title = new TextTitle();
//			title.setPosition(RectangleEdge.LEFT);
//			title.setText(text_TitleLeft.getText().trim());
//			labelChart.addSubtitle(3, title);
//		}
//		if(check_TitleRight.getSelection() && !"".equals(text_TitleRight.getText().trim())) {
//			TextTitle title = new TextTitle();
//			title.setPosition(RectangleEdge.RIGHT);
//			title.setText(text_TitleRight.getText().trim());
//			labelChart.addSubtitle(4, title);
//		}
//		//----------设置Legend（图例）----------
//		
//		LegendTitle legend = labelChart.getLegend();
//		legend.setPosition((RectangleEdge)combo_LegendLocation.getData(combo_LegendLocation.getText()));
//		legend.setBackgroundPaint(ConvertUtil.toAwtColor(chartPartDef.get_LegendColor()));
//		
//		//-------------background---------------
//		labelChart.setBackgroundPaint(ConvertUtil.toAwtColor(chartPartDef.get_BackColor()));
//		labelChart.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage(chartPartDef.get_BackImageName())));
	}
	
	private MenuManager createPopMenu(){
		MenuManager menuManager=new MenuManager();
		Action look = new Action("浏览(B)..."){
			@Override
			public void run() {
				ImageChooseDlg imageC = new ImageChooseDlg(getShell(), m_Library, m_Api, label_ImageName.getData().toString());
				if(imageC.open() == 0) {
					label_ImageName.setText(imageC.getImageAdr());
					label_ImageName.setData(imageC.getImageAdr(), imageC.getImageAdrDate());
					labelChart.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage(imageC.getImageAdrDate())));
					showAllChart();
				}
			}
		};
		
		Action nul = new Action("无"){
			@Override
			public void run() {
				label_ImageName.setText("无");
				labelChart.setBackgroundImage(null);
				showAllChart();
			}
		};
		menuManager.add(look);
		menuManager.add(nul);
		
		return menuManager;
	}
	//图片路径解析
	private String getSelectImageStr(String imagePath){
		String imageText = "";
		if(imagePath.contains("Database.")){
			imageText = imagePath.substring("Database.".length());
		}else if(imagePath.contains("System.")){
			imageText = imagePath.substring("System.".length());
		}
		return imageText;
	}
	
	private String getLegendLocationStr(LegendLocation location){
		String str = "";
		switch(location){
		case Top:
			return str = "顶部";
		case Bottom:
			return str = "底部";
		case Left:
			return str = "左侧";
		case Right:
			return str = "右侧";
		}
		return str;
	}
	
	private RectangleEdge getRectangleForLocation(LegendLocation location){
		switch(location){
		case Top:
			return RectangleEdge.TOP;
		case Bottom:
			return RectangleEdge.BOTTOM;
		case Left:
			return RectangleEdge.LEFT;
		case Right:
			return RectangleEdge.RIGHT;
		}
		return null;
	}
	
	 private Paint[] SetCustomColorFromTheme(String colorSetName){
		 Paint[] colors = new Paint[10];
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
	 
	 private void onTitleChecked(Button titleChecked, Text titleText, RectangleEdge positon, int type, boolean flag){
		 TextTitle title = (TextTitle)labelChart.getSubtitle(type);
		 if(flag){
			 title.setText(titleText.getText().trim());
		 }else{
			 title.setText("");
		 }
		 showAllChart();
	 }
	 
	 private void onTitleTextModify(Text titleText, RectangleEdge positon, int type){
		 if(labelChart.getSubtitleCount() > 1){
			 TextTitle title = (TextTitle)labelChart.getSubtitle(type);
			 title.setText(titleText.getText().trim());
			 showAllChart();
		 }
	 }
	 
	 //显示4张图片
	 private void showAllChart(){
		 Image chartIamge = DiagramWizard_3.initImage(labelChart, 300, 230);
		 label_TitleChart.setImage(chartIamge);
		 label_LegendChart.setImage(chartIamge);
		 label_BackgroundChart.setImage(chartIamge);
		 label_ColorModelChart.setImage(chartIamge);
	 }
	 
	 private void onShowLegendChecked(boolean flag){
		 combo_LegendLocation.setEnabled(flag);
		 combo_LegendColor.setEnabled(flag);
		 labelChart.getLegend().setVisible(flag);
		 showAllChart();
	 }
	 
	 private void onLegendLocationSelected(){
		 labelChart.getLegend().setPosition(getRectangleForLocation((LegendLocation)combo_LegendLocation.getData(combo_LegendLocation.getText())));
		 showAllChart();
	 }
	 
	 private void onLegendColorSelected(){
		 labelChart.getLegend().setBackgroundPaint(ConvertUtil.toAwtColor(getColorFromString(combo_LegendColor)));
		 showAllChart();
	 }
	 
	//得到可保存的color 值
	 private system.Drawing.Color getColorFromString(TableCombo combo){
		 String colorName = (String)combo.getData(combo.getText());
		 colorName = colorName.substring(colorName.indexOf("]") + 1, colorName.length());
		 return system.Drawing.Color.FromName(colorName);
	 }
	 
	 private void onColorSetSelected(){
		 initLabelChart();
		 showAllChart();
	 }
	 
	private void initLabelChart() {
		labelChart = DiagramWizard_3.getChartImage(DiagramWizard_3.getTable(), DiagramWizard_3.getChartType(chartPartDef.get_CategoryAsString()),
				SetCustomColorFromTheme("Core.ChartColorTheme." + (String) combo_ColorSet.getData(combo_ColorSet.getText())));
		TextTitle title1 = new TextTitle();
		TextTitle title2 = new TextTitle();
		TextTitle title3 = new TextTitle();
		TextTitle title4 = new TextTitle();
		
		title1.setPosition(RectangleEdge.TOP);
		title2.setPosition(RectangleEdge.BOTTOM);
		title3.setPosition(RectangleEdge.LEFT);
		title4.setPosition(RectangleEdge.RIGHT);
		
		title1.setText("");
		title2.setText("");
		title3.setText("");
		title4.setText("");
		
		if (check_TitleTop.getSelection() && !"".equals(text_TitleTop.getText().trim())) 
			title1.setText(text_TitleTop.getText().trim());
		if (check_TitleBottom.getSelection() && !"".equals(text_TitleBottom.getText().trim())) 
			title2.setText(text_TitleBottom.getText().trim());
		if (check_TitleLeft.getSelection() && !"".equals(text_TitleLeft.getText().trim())) 
			title3.setText(text_TitleLeft.getText().trim());
		if (check_TitleRight.getSelection() && !"".equals(text_TitleRight.getText().trim())) 
			title4.setText(text_TitleRight.getText().trim());
		
		labelChart.addSubtitle(1, title1);
		labelChart.addSubtitle(2, title2);
		labelChart.addSubtitle(3, title3);
		labelChart.addSubtitle(4, title4);
		
		LegendTitle legend = labelChart.getLegend();
		if(check_ShowLegend.getSelection()){
			legend.setPosition(getRectangleForLocation((LegendLocation)combo_LegendLocation.getData(combo_LegendLocation.getText())));
			legend.setBackgroundPaint(ConvertUtil.toAwtColor(getColorFromString(combo_LegendColor)));
		}else{
			legend.setVisible(false);
		}
		
		if(radio_ImageStyle.getSelection()){
			combo_ImageStyle.setEnabled(true);
			if (!"".equals((String) label_ImageName.getData(label_ImageName.getText())))
				labelChart.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage((String) label_ImageName.getData(label_ImageName.getText()))));
			else
				labelChart.setBackgroundImage(null);
			
			labelChart.setBackgroundPaint(null);
			labelChart.getPlot().setBackgroundPaint(null);
			
		}else{
			combo_ImageStyle.setEnabled(false);
			labelChart.setBackgroundPaint(ConvertUtil.toAwtColor(getColorFromString(combo_BackColor)));
			labelChart.getPlot().setBackgroundPaint(ConvertUtil.toAwtColor(getColorFromString(combo_BackColor)));
			labelChart.setBackgroundImage(null);
			
		}
	 }
	 
	 private void onBackColorSelected(){
		 labelChart.setBackgroundPaint(ConvertUtil.toAwtColor(getColorFromString(combo_BackColor)));
		 labelChart.getPlot().setBackgroundPaint(ConvertUtil.toAwtColor(getColorFromString(combo_BackColor)));
		 showAllChart();
	 }
	 
	 private void onImageStyleSelected(){
		label_ImageSelector.addMouseListener(menuListener);
		combo_ImageStyle.setEnabled(true);
		combo_BackColor.setEnabled(false);
		labelChart.setBackgroundPaint(null);//外围 背景色
		labelChart.getPlot().setBackgroundPaint(null);//内部背景色
		if(!"".equals((String)label_ImageName.getData(label_ImageName.getText())))
			labelChart.setBackgroundImage(SwtImageConverter.ConvertToAwtImage(ImageResolver.get_Resolver().ResolveImage((String)label_ImageName.getData(label_ImageName.getText()))));
		else{
			labelChart.setBackgroundImage(null);
		}
		showAllChart();
	 }
	 
	 private void initShowMenuListener(){
		 menuListener = new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					if(e.button == 1){
						popMenu = createPopMenu().createContextMenu(label_ImageSelector);
						popMenu.setLocation(label_ImageSelector.toDisplay(e.x,e.y)); //定位
						popMenu.setVisible(true);
					}
				}
			};
	 }
	 
	 public static JFreeChart getLabelChart(){
		 return labelChart;
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
	 
	 
	 private void saveProperties(){
		 chartPartDef.set_TitleTopVisible(check_TitleTop.getSelection());
		 chartPartDef.set_TitleBottomVisible(check_TitleBottom.getSelection());
		 chartPartDef.set_TitleLeftVisible(check_TitleLeft.getSelection());
		 chartPartDef.set_TitleRightVisible(check_TitleRight.getSelection());
		 
		 if(check_TitleTop.getSelection() && !"".equals(text_TitleTop.getText().trim()))
			 chartPartDef.set_TitleTop(text_TitleTop.getText().trim());
		 if(check_TitleBottom.getSelection() && !"".equals(text_TitleBottom.getText().trim())) 
			 chartPartDef.set_TitleBottom(text_TitleBottom.getText().trim());
		 if(check_TitleLeft.getSelection() && !"".equals(text_TitleLeft.getText().trim())) 
			 chartPartDef.set_TitleLeft(text_TitleLeft.getText().trim());
		 if(check_TitleRight.getSelection() && !"".equals(text_TitleRight.getText().trim())) 
			 chartPartDef.set_TitleRight(text_TitleRight.getText().trim());
		 
		 chartPartDef.set_LegendVisible(check_ShowLegend.getSelection());
		 chartPartDef.set_LegendLocation((LegendLocation)combo_LegendLocation.getData(combo_LegendLocation.getText()));
		 chartPartDef.set_LegendColor(getColorFromString(combo_LegendColor));
		 
		 chartPartDef.set_ColorSetName((String) combo_ColorSet.getData(combo_ColorSet.getText()));
		 
		 getCustomColorFromTheme(chartPartDef.get_ColorSetName());
		 chartPartDef.set_CustomColor1(m_customColor[0]);
		 chartPartDef.set_CustomColor2(m_customColor[1]);
		 chartPartDef.set_CustomColor3(m_customColor[2]);
		 chartPartDef.set_CustomColor4(m_customColor[3]);
		 chartPartDef.set_CustomColor5(m_customColor[4]);
		 chartPartDef.set_CustomColor6(m_customColor[5]);
		 chartPartDef.set_CustomColor7(m_customColor[6]);
		 chartPartDef.set_CustomColor8(m_customColor[7]);
		 chartPartDef.set_CustomColor9(m_customColor[8]);
		 chartPartDef.set_CustomColor10(m_customColor[9]);
		 
		 
		 if(radio_ImageStyle.getSelection()){
			 chartPartDef.set_BackImageName((String)label_ImageName.getData(label_ImageName.getText()));
		 }else{
			 chartPartDef.set_BackImageName(null);
		 }
		 if(combo_BackColor.getData(combo_BackColor.getText()) != null){
			 chartPartDef.set_BackColor(getColorFromString(combo_BackColor));
		 }else{
			 chartPartDef.set_BackColor(system.Drawing.Color.get_White());
		 }
		 
		 
		 chartPartDef.set_LegendSpanPercentage(25);
		 chartPartDef.set_LegendMarginTop(5);
		 chartPartDef.set_LegendMarginLeft(5);
		 chartPartDef.set_LegendMarginBottom(5);
		 chartPartDef.set_LegendMarginRight(5);
		 chartPartDef.set_Display3DStyle(false);
         chartPartDef.set_XAxisRotation(144);
         chartPartDef.set_YAxisRotation(12);
         chartPartDef.set_ZAxisRotation(0);
         chartPartDef.set_ColorModelAlpha(150);
         chartPartDef.set_ColorModelStyle(Core.Dashboards.ColorModels.CustomLinear);
	 }
}
