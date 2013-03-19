package core.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import Core.Dashboards.MultiViewGridPartDef;
import Core.Dashboards.ViewDef;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import core.apploader.search.comm.CommEnum.ExpressType;
import core.apploader.search.composite.AbstractExpress;

public class View_RunTimeSelect extends Dialog{
	
	private Text text_Name;
	private Text text_Alias;
	
	private AbstractExpress express;
	private ViewDef viewDef;

	private MultiViewGridPartDef multidef;
	private ViewDef selViewDef;
	private SiteviewQuery siteviewquery;
//	private String type;
	

	protected View_RunTimeSelect(Shell parentShell, MultiViewGridPartDef multidef, ViewDef selViewDef) {
		super(parentShell);
		this.multidef = multidef;
		this.selViewDef = selViewDef;
		
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 400);
		newShell.setLocation(300, 100);
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 20);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("\u6587\u672C(T):");
		
		text_Name = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblNewLabel, 171, SWT.RIGHT);
		fd_text.top = new FormAttachment(0, 17);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		text_Name.setLayoutData(fd_text);
		
		Label lblAlias = new Label(container, SWT.NONE);
		lblAlias.setText("Alias:");
		FormData fd_lblAlias = new FormData();
		fd_lblAlias.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
		fd_lblAlias.left = new FormAttachment(text_Name, 20);
		lblAlias.setLayoutData(fd_lblAlias);
		
		text_Alias = new Text(container, SWT.BORDER);
		FormData fd_text_1 = new FormData();
		fd_text_1.top = new FormAttachment(0, 17);
		fd_text_1.left = new FormAttachment(lblAlias, 6);
		fd_text_1.right = new FormAttachment(100, -124);
		text_Alias.setLayoutData(fd_text_1);
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		FormData fd_composite = new FormData();
		fd_composite.bottom = new FormAttachment(text_Name, 256, SWT.BOTTOM);
		fd_composite.top = new FormAttachment(text_Name, 16);
		fd_composite.left = new FormAttachment(0, 10);
		fd_composite.right = new FormAttachment(0, 684);
		composite.setLayoutData(fd_composite);
		
		fillProperties();
		express = new AbstractExpress(composite, ExpressType.SIMPLEEXPRESS, siteviewquery);
		
		return container;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}
	
	private void fillProperties(){
		if(selViewDef != null && selViewDef.get_Criteria() != null){
			
			siteviewquery = new SiteviewQuery();
			if("".equals(selViewDef.get_LinkedTo())) siteviewquery.AddBusObQuery(multidef.get_BusObName(), QueryInfoToGet.All);
			else siteviewquery.AddBusObQuery(selViewDef.get_LinkedTo(), QueryInfoToGet.All);
			if(!"".equals(selViewDef.get_XmlQuery())){
				siteviewquery.FromXml(selViewDef.get_XmlQuery());
//				siteviewquery.set_BusinessObjectName(selViewDef.get_LinkedTo());
			}
			siteviewquery.set_BusObSearchCriteria(selViewDef.get_Criteria());
			
			text_Name.setText(selViewDef.get_Name());
			text_Alias.setText(selViewDef.get_Alias());
		}
	}
	
	
	private void saveProperties(){
		viewDef = new ViewDef(multidef);
		if(express.getXmlElementByTree() == null){
			viewDef.set_XmlQuery("");
			
		}else{
			SiteviewQuery siteviewquery = new SiteviewQuery();
			String busObName=express.busObMap.get(express.busObIncludeHeadList.get(0)); 
			siteviewquery.AddBusObQuery(busObName, QueryInfoToGet.All);
			siteviewquery.set_BusObSearchCriteria(express.getXmlElementByTree());
			viewDef.set_LinkedTo(busObName);
			viewDef.set_XmlQuery(siteviewquery.ToXml());	
			if(siteviewquery.get_BusObSearchCriteria() != null)
				viewDef.set_Criteria(siteviewquery.get_BusObSearchCriteria());
		}
		
		viewDef.set_Name(text_Name.getText().trim());
		viewDef.set_Alias(text_Alias.getText().trim());
		
	}
	
	@Override
	protected void okPressed() {
		if(!validate()) return; 
		saveProperties();
		super.okPressed();
	}
	
	private boolean validate() {
		if("".equals(text_Name.getText()))
		{
			MessageDialog.openError(getShell(), "输入有误!", "文本名称不能为空！");
			return false;
		}
		return true;
	}


	public ViewDef getViewDef() {
		return viewDef;
	}
	
}
