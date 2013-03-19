package core.ui.wizard;


import java.util.HashSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import Core.Dashboards.DashboardPartDef;
import Siteview.IDefinition;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;

public class DiagramWizard extends Wizard {
	
	private String type;	//操作类型----1.新建 2.编辑 3.复制
	private String linkTo;  //关联下拉式 选 项
	private DashboardPartDef dashboardPartDef;
	private DashboardPartDef dashboardPartDef_Clone;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	private IDefinition m_Def;
	
	public DiagramWizard(){
		setWindowTitle("欢迎使用图表向导");
	}
	
	public DiagramWizard(DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, String type, IDefinition m_Def) {
		setWindowTitle("欢迎使用图表向导");
		this.dashboardPartDef = dashboardPartDef;
		if("New".equals(type)) this.dashboardPartDef_Clone = dashboardPartDef;
		else	this.dashboardPartDef_Clone = (DashboardPartDef)dashboardPartDef.CloneForEdit();
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.type = type;
		this.m_Def = m_Def;
	}
	
	
	
	public void addPages() {
		this.addPage(new DiagramWizard_1(this, dashboardPartDef_Clone, m_Library, m_ScopeUtil, m_Api, linkTo, type));
		this.addPage(new DiagramWizard_2(this, dashboardPartDef_Clone, m_Library, m_ScopeUtil, m_Api, linkTo, type));
//		this.addPage(new DiagramWizard_3(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_4(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_5(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_6(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_7(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_8(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_9(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_10(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_11(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_12(this, dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type));
//		this.addPage(new DiagramWizard_13(this));
	}

	
	@Override
	public IWizardPage getStartingPage() {
		return this.getPage("diagramWizard_1");
	}
	
	@Override
	public boolean performFinish() {
		DiagramWizard_13 finishPage = (DiagramWizard_13)this.getPage("diagramWizard_13");
//		dashboardPartDef_Clone = finishPage.getDashBoardPartDef();
		dashboardPartDef = finishPage.getDashBoardPartDef();
//		if("New".equals(type)) {
//			m_Library.UpdateDefinition(dashboardPartDef, true);
//		}else if("Copy".equals(type)){
//			dashboardPartDef.set_Id(m_Def.get_Id());
//			m_Library.UpdateDefinition(dashboardPartDef, true);
//		}else {
//			m_Library.UpdateDefinition(dashboardPartDef, false);
//		}
		return true;
	}
	
	public boolean canFinish() {
		if (this.getContainer().getCurrentPage() == this.getPage("diagramWizard_13")){
			return true;
		}else
			return false;
	}
	
	public DashboardPartDef getDashboardPartDef() {
		return dashboardPartDef;
	}
	
}
