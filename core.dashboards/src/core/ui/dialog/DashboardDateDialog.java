package core.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import system.Type;
import Siteview.StringUtils;
import Siteview.Api.FieldDef;
import Siteview.Xml.FunctionCategory;
import Siteview.Xml.LocalizeHelper;
import Siteview.Xml.XmlSystemFunctionCategory;
import core.apploader.search.dialog.SimpleExpressSelector;



public class DashboardDateDialog extends Dialog {
	
	private Combo cb_type;
	private Combo cb_date;
	private Combo cb_field;
	private DateTime dateTime;
	private Label label_TypeName;
	private boolean type;
	private String strdate;
	private String strname;
	private String busObName;
	
	public DashboardDateDialog(Shell parentShell, String busObName) {
		super(parentShell);
		this.busObName = busObName;
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL );
		// TODO Auto-generated constructor stub
	}

	

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("选择日期");
		newShell.setLayout(new FillLayout());
		// TODO Auto-generated method stub
		super.configureShell(newShell);
	}
	
	
	/**
	 * 初始化 窗口
	 */
	
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(267,299);
	}
	
	
	/**
	 *  按钮设置
	 */
	@Override
	protected void initializeBounds() {
		
		Button button = super.getButton(IDialogConstants.OK_ID);
		button.setText("确定");
		super.getButton(IDialogConstants.CANCEL_ID).setText("取消");
		
		super.initializeBounds();
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		// TODO Auto-generated method stub
		Composite subCom=(Composite)super.createDialogArea(parent);
		subCom.setLayout(null);
		
		final Group group= new Group(subCom, SWT.NONE);
		group.setBounds(10, 0, 237, 212);
		
		cb_type = new Combo(group, SWT.READ_ONLY);
		cb_type.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if("选择日期".equals(cb_type.getText())){
					
					dateTime.setVisible(true);
					cb_date.setVisible(false);
					cb_field.setVisible(false);
					label_TypeName.setText("选择日期:");
					
				}else if("选择日期函数".equals(cb_type.getText())){
					dateTime.setVisible(false);
					cb_date.setVisible(true);
					cb_date.setEnabled(true);
					cb_field.setVisible(false);
					label_TypeName.setText("选择日期函数:");
					
				}else if("选择字段".equals(cb_type.getText())){
					dateTime.setVisible(false);
					cb_date.setVisible(false);
					cb_field.setVisible(true);
					label_TypeName.setText("选择字段:");
					
				}
				
			}
		});
		cb_type.add("选择日期");
		cb_type.add("选择日期函数");
		if(busObName != null) cb_type.add("选择字段");
		cb_type.setBounds(10, 48, 197, 20);
		cb_type.select(0);
		
		cb_date = new Combo(group, SWT.READ_ONLY);
		cb_date.setBounds(10, 105, 197, 20);
		cb_date.setVisible(false);
		
		cb_field = new Combo(group, SWT.READ_ONLY);
		cb_field.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if("更多值...".equals(cb_field.getText())){
					SimpleExpressSelector simpleES = new SimpleExpressSelector(getShell(), new String[]{busObName}, "", 4, true, true, false);
					if(simpleES.open() == 0) {
//						cb_field.setText(simpleES.getFieldDefHolder().getUp_fieldDef_alias());
						FieldDef field = simpleES.getFieldDefHolder().getFieldDef();
						String fieldName = field.get_QualifiedName();
						fieldName = fieldName.substring(0, fieldName.indexOf(".")) + "." + field.get_Name();
//						cb_field.setData(simpleES.getFieldDefHolder().getUp_fieldDef_alias(), fieldName);
						if(cb_field.getData(fieldName) != null) cb_field.remove(fieldName);
						cb_field.add(fieldName, 0);
						cb_field.setData(fieldName, "");
						cb_field.select(0);
					}
				}
			}
		});
		cb_field.setBounds(10, 105, 197, 20);
		cb_field.setVisible(false);
		cb_field.add("更多值...");
		
		
		for(FunctionCategory daterange:FunctionCategory.values()){
			
			if(IsSupportedDateFunction(daterange.toString())){
				 String strB = LocalizeHelper.GetValue(Type.GetType(FunctionCategory.class.getName()), daterange.toString());
				 cb_date.add(strB);
				 cb_date.setData(strB, daterange.toString());
			}
			 
		}
		if(cb_date.getItemCount()>0)cb_date.select(0);
		
		
		dateTime = new DateTime(group, SWT.BORDER | SWT.DROP_DOWN|SWT.READ_ONLY);
		dateTime.setBounds(10, 105, 197, 20);
		
		Label lblNewLabel = new Label(group, SWT.NONE);
		lblNewLabel.setBounds(10, 24, 115, 18);
		lblNewLabel.setText("\u9009\u62E9\u65B9\u5F0F:");
		
		label_TypeName = new Label(group, SWT.NONE);
		label_TypeName.setBounds(10, 79, 120, 20);
		label_TypeName.setText("\u9009\u62E9\u65E5\u671F:");
		
	
		return subCom;
	}
	
	
	
	/**
	 * 点击保存事件
	 */
	
	@Override
	protected void okPressed() {
		
		
		if("选择日期".equals(cb_type.getText())){
			if(StringUtils.IsEmpty(cb_date.getText())){
				MessageDialog.openInformation(getParentShell(), "提示",
						"请选择日期值. ");
				return;
			}
			type=false;
			strdate=dateTime.getYear()+"-"+(dateTime.getMonth() < 9 ? "0"+(dateTime.getMonth()+1) : (dateTime.getMonth()+1))+"-"
						+(dateTime.getDay() < 10 ? "0" + dateTime.getDay() : dateTime.getDay());
			strname=dateTime.getYear()+"-"+(dateTime.getMonth() < 10 ? "0"+(dateTime.getMonth()+1) : (dateTime.getMonth()+1))+"-"
						+(dateTime.getDay() < 10 ? "0" + dateTime.getDay() : dateTime.getDay());
		}else if("选择日期函数".equals(cb_type.getText())){
			if(StringUtils.IsEmpty(dateTime.toString())){
				MessageDialog.openInformation(getParentShell(), "提示",
						"请选择日期函数值. ");
				return;
			}
			type=true;
			strdate=cb_date.getData(cb_date.getText()).toString();
			strname=cb_date.getText().toString();
		}else{
			if(StringUtils.IsEmpty(cb_field.getText().toString())){
				MessageDialog.openInformation(getParentShell(), "提示",
						"请选字段值. ");
				return;
			}
			strname = cb_field.getText();
			strdate = "";
		}
		
		super.okPressed();
	}



	public boolean getType() {
		return type;
	}


	public String getStrdate() {
		return strdate;
	}



	public String getStrname() {
		return strname;
	}


	   private boolean IsSupportedDateFunction(String strFunction)
       {
           switch (XmlSystemFunctionCategory.ToCategory(strFunction))
           {
               case CurrentLoginId:
                   return false;

               case CurrentSecurityGroup:
                   return false;

               case CurrentSecurityGroupId:
                   return false;

               case CurrentUserRecId:
                   return false;

               case CurrentUserName:
                   return false;

               case CurrentUserTeam:
                   return false;

               case CurrentUserType:
                   return false;

               case CurrentUserEmailAddress:
                   return false;

               case DefaultUserRecId:
                   return false;

               case DefaultUserName:
                   return false;

               case DefaultUserTeam:
                   return false;

               case DefaultUserType:
                   return false;
           }
           return true;
       }
	
	
}
