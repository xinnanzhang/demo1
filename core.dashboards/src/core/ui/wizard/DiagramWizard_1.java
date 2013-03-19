package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;
import Siteview.PlaceHolder;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.FieldDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.Enumerator;
import system.Collections.ICollection;
import system.Collections.IEnumerator;

public class DiagramWizard_1 extends WizardPage {

	private DiagramWizard diagramWizard;
	private String type;	//操作类型----1.新建 2.编辑 3.复制
	private String linkTo;  //关联下拉式 选 项
	private DashboardPartDef dashboardPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_1() {
		super("diagramWizard_1");
		setTitle("欢迎使用图表向导。");
	}
	
	public DiagramWizard_1(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}
	
	public DiagramWizard_1(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, String type){
		this();
		diagramWizard = dw;
		setTitle("基本信息");
		setDescription("用于控件的一般数据。");
		this.dashboardPartDef = dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.type = type;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		setControl(container);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(136, 10, 119, 16);
		lblNewLabel.setText("\u672C\u5411\u5BFC\u5141\u8BB8\u60A8\uFF1A");
		
		Label label = new Label(container, SWT.NONE);
		label.setText("* \u4ECE\u56FE\u8868\u7C7B\u578B\u5217\u8868\u4E2D\u9009\u62E9");
		label.setBounds(146, 32, 165, 16);
		
		Label label_1 = new Label(container, SWT.NONE);
		label_1.setText("* \u6311\u9009\u56FE\u8868\u4E2D\u5C06\u663E\u793A\u7684\u6570\u636E");
		label_1.setBounds(146, 64, 165, 16);
		
		Label label_2 = new Label(container, SWT.NONE);
		label_2.setText("* \u9009\u62E9\uFF08\u5982\u679C\u8FD0\u884C\u65F6\u7B5B\u9009\u53EF\u7528\uFF09");
		label_2.setBounds(146, 98, 191, 16);
		
		Label label_3 = new Label(container, SWT.NONE);
		label_3.setText("* \u914D\u7F6E\u56FE\u8868\u5916\u89C2");
		label_3.setBounds(146, 131, 119, 16);
		
		Label label_4 = new Label(container, SWT.NONE);
		label_4.setText("* \u4FDD\u5B58\u56FE\u8868");
		label_4.setBounds(146, 166, 119, 16);
		
		Label label_5 = new Label(container, SWT.NONE);
		label_5.setText("\u8BF7\u5355\u51FB\u201C\u4E0B\u4E00\u6B65\u201D\u5F00\u59CB...");
		label_5.setBounds(146, 204, 119, 16);
		
		Label WelcomeImage = new Label(container, SWT.NONE);
		WelcomeImage.setBounds(0, 0, 130, 220);
		WelcomeImage.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]siteview#ModuleImages.QuoteWatermark.png")));
		
	}
	@Override
	public boolean canFlipToNextPage() {
		return true;
	}
	
	@Override
	public IWizardPage getNextPage() {
//		diagramWizard.addPage(new DiagramWizard_2(diagramWizard, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
		return diagramWizard.getPage("diagramWizard_2");
	}
}
