package core.ui.dialog;

import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;

import Core.Dashboards.DashboardPartDef;
import Siteview.IDefinition;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;


public class OutlookInbox extends Dialog {

	private String type;	//��������----1.�½� 2.�༭ 3.����
	private String linkTo;  //��������ʽ ѡ ��
//	private OutlookCalendarPartDef calendarDef;
	private DashboardPartDef dashboardPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	private IDefinition m_Def;
	
	private Generalmethods general;
	
	private Text text_Name;
	private Text text_Alias;
	private Text text_Description;
	private Text text_title;
	
	
	protected OutlookInbox(Shell parentShell){
		super(parentShell);
	}
	
	public OutlookInbox(Shell parentShell, DashboardPartDef dashboardPartDef, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, ISiteviewApi m_Api, String linkTo, String type, IDefinition m_Def) {
		super(parentShell);
		this.dashboardPartDef = dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.type = type;
		this.m_Def = m_Def;
		general=new Generalmethods(getShell(),dashboardPartDef, m_Api, m_Library, m_ScopeUtil, m_Def, linkTo);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 700);
		newShell.setLocation(300, 100);
		newShell.setText(type+"	 Outlook�ռ���");
		super.configureShell(newShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
//		container.setSize(700, 800);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 31);
		fd_lblNewLabel.left = new FormAttachment(0, 26);
		fd_lblNewLabel.right = new FormAttachment(0, 85);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("\u540D\u79F0(N):");
		
		Label lbla = new Label(container, SWT.NONE);
		lbla.setText("\u522B\u540D(A):");
		FormData fd_lbla = new FormData();
		fd_lbla.top = new FormAttachment(lblNewLabel, 21);
		fd_lbla.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		lbla.setLayoutData(fd_lbla);
		
		text_Name = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblNewLabel, 406, SWT.RIGHT);
		fd_text.top = new FormAttachment(0, 28);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		text_Name.setLayoutData(fd_text);
		
		text_Alias = new Text(container, SWT.BORDER);
		FormData fd_text_1 = new FormData();
		fd_text_1.top = new FormAttachment(lbla, -3, SWT.TOP);
		fd_text_1.right = new FormAttachment(text_Name, 0, SWT.RIGHT);
		fd_text_1.left = new FormAttachment(text_Name, 0, SWT.LEFT);
		text_Alias.setLayoutData(fd_text_1);
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(text_Alias, 21);
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 604);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -40);
		tabFolder.setLayoutData(fd_tabFolder);
		
		general.tabFolder_1(tabFolder);
		general.tabFolder_2(tabFolder);
		fillProperties();
		return container;
	}
	
	private void fillProperties() {
		if(dashboardPartDef == null || "New".equals(type)){
			return;
		}
		
		if ("Edit".equals(type)) {
			text_Name.setText(dashboardPartDef.get_Name());
			text_Alias.setText(dashboardPartDef.get_Alias());
		} else {
			text_Name.setText(dashboardPartDef.get_Name() + "  �ĸ���");
			text_Alias.setText(dashboardPartDef.get_Alias() + "  �ĸ���");
		}
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
	}
	
	@Override
	protected void okPressed() {
		
		if(general.savavalidation(text_Name, text_Alias, type)){
			
//			if ("New".equals(type)) {
//				m_Library.UpdateDefinition(dashboardPartDef, true);
//			} else if ("Edit".equals(type)) {
//				m_Library.UpdateDefinition(dashboardPartDef, false);
//			} else if("Copy".equals(type)){
//				dashboardPartDef.set_Id(m_Def.get_Id());
//				m_Library.UpdateDefinition(dashboardPartDef, true);
//			}
			super.okPressed();
		}
		
	}
	
}
