package core.ui.wizard;

import java.util.HashSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;

import Core.Dashboards.ChartPartDef;
import Core.Dashboards.DashboardPartDef;

public class DiagramWizard_13 extends WizardPage {

	private DiagramWizard diagramWizard;
	private String type; // 操作类型----1.新建 2.编辑 3.复制
	private String linkTo; // 关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); // 保存全部类别的集合
	private DashboardPartDef dashboardPartDef;
	private ChartPartDef chartPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	/**
	 * @wbp.parser.constructor
	 */
	public  DiagramWizard_13() {
		super("diagramWizard_13");
	}
	
	public DiagramWizard_13(DiagramWizard dw) {
		this();
		diagramWizard = dw;
	}

	public DiagramWizard_13(DiagramWizard dw, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, HashSet<String> Categoryset, String type) {
		this();
		diagramWizard = dw;
		this.dashboardPartDef = dashboardPartDef;
		this.chartPartDef = (ChartPartDef) dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.Categoryset = Categoryset;
		this.type = type;
	}
	
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		setControl(container);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(106, 10, 316, 16);
		lblNewLabel.setText("\u56FE\u8868\u6458\u8981\uFF1A\u606D\u559C\uFF01\u60A8\u5DF2\u6210\u529F\u521B\u5EFA\u4E86\u4EEA\u8868\u7248\u56FE\u8868\u3002");
		
	}
	
	public DashboardPartDef getDashBoardPartDef(){
		return dashboardPartDef;
	}

	@Override
	public IWizardPage getNextPage() {
		// TODO Auto-generated method stub
		return null;
	}
}
