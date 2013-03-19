package core.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

import system.Collections.ICollection;
import system.Collections.IEnumerator;

import Siteview.Api.FieldDef;


public class ShowTextChoose extends Dialog {

	private Combo combo_Text;
	private Combo combo_Tip;
	private ICollection arrFieldName;
	private String relationText;
	private String relationTip;
	
	
	protected ShowTextChoose(Shell parentShell, ICollection arrFieldName) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		this.arrFieldName = arrFieldName;
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(500, 150);
		newShell.setLocation(350, 350);
		newShell.setText("拓扑节点显示文字选择");
		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new FormLayout());
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.bottom = new FormAttachment(0, 38);
		fd_lblNewLabel.top = new FormAttachment(0, 20);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("\u663E\u793A\u6587\u5B57:");
		
		combo_Text = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		FormData fd_combo = new FormData();
		fd_combo.bottom = new FormAttachment(lblNewLabel, 0, SWT.BOTTOM);
		fd_combo.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_combo.left = new FormAttachment(lblNewLabel, 6);
		combo_Text.setLayoutData(fd_combo);
		
		Label lblTip = new Label(container, SWT.NONE);
		fd_combo.right = new FormAttachment(lblTip, -50);
		lblTip.setText("Tip\u6587\u5B57:");
		FormData fd_lblTip = new FormData();
		fd_lblTip.top = new FormAttachment(lblNewLabel, 0, SWT.TOP);
		lblTip.setLayoutData(fd_lblTip);
		
		combo_Tip = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		fd_lblTip.right = new FormAttachment(100, -176);
		FormData fd_combo_1 = new FormData();
		fd_combo_1.bottom = new FormAttachment(lblNewLabel, 0, SWT.BOTTOM);
		fd_combo_1.top = new FormAttachment(lblNewLabel, -3, SWT.TOP);
		fd_combo_1.left = new FormAttachment(lblTip, 6);
		fd_combo_1.right = new FormAttachment(100, -20);
		combo_Tip.setLayoutData(fd_combo_1);
//		container.setSize(700, 800);
		fillCombo();
		return container;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}
	
	private void fillCombo(){
		 IEnumerator it = arrFieldName.GetEnumerator();;
		    while(it.MoveNext()){
		    	FieldDef relDef = (FieldDef)it.get_Current();
		    	combo_Text.add(relDef.get_Alias());
		    	combo_Text.setData(relDef.get_Alias(), relDef.get_Name());
		    	combo_Tip.add(relDef.get_Alias());
		    	combo_Tip.setData(relDef.get_Alias(), relDef.get_Name());
		    }
	}
	
	
	@Override
	protected void okPressed() {
		if(combo_Text.getText().trim().length()<1||combo_Tip.getText().trim().length()<1){
			MessageDialog.openError(getShell(), "输入有误", "下拉框不能为空!");
		}else{
			relationText = (String)combo_Text.getData(combo_Text.getText());
			relationTip = (String)combo_Tip.getData(combo_Tip.getText());
			super.okPressed();
		}
	}
	
	
	public String getRelationText() {
		return relationText;
	}

	public String getRelationTip() {
		return relationTip;
	}
}
