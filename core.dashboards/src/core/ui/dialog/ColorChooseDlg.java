package core.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import Siteview.StringUtils;

import system.Type;
import system.Collections.ArrayList;
import system.Drawing.KnownColor;

public class ColorChooseDlg extends Dialog {
	
	private String colorvalue="";
	private system.Drawing.Color color=null;
	private Label label=null;
	
	private Label colorLabel;
	private CLabel textLabel;
	private Button colorButton;
	private String selectValue;
	private String colorName_ZH;
	private ColorChooseDlg chooseDlg;
	
	private Composite composites;
	private Shell shell;
	
	/**
	 * @wbp.parser.constructor
	 */
	
	
	public ColorChooseDlg(Shell parentShell, Composite composite, String selectValue) {
		super(parentShell);
//		setShellStyle(SWT.MAX | SWT.APPLICATION_MODAL);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		
		this.composites=composite;
//		this.selectValue = "";
//		if(selectValue !=null && !"".equals(selectValue)){
//			colorName_ZH = Siteview.Windows.Forms.Res.get_Default() .GetString("SiteviewColorEditor" + "." + selectValue);
//		}else { 
//			colorName_ZH = "请选择颜色...";
//		}
		this.shell=parentShell;
		colorLabel = new Label(composite, SWT.NONE);
		colorLabel.setBounds(0, 0, 30, 18);
		colorLabel.setText("");
		
		textLabel = new CLabel(composite, SWT.CENTER);
		textLabel.setBounds(31, 0, 145, 17);
		
		colorButton = new Button(composite, SWT.FLAT );
		colorButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				chooseDlg.open();
				super.widgetSelected(e);
			}
		});
		colorButton.setBounds(177, 0, 20, 16);
//		initValue();
		chooseDlg = this;
		
		
	}
	
	public void setColor(String selectValue){
		this.selectValue = "";
		if(selectValue !=null && !"".equals(selectValue)){
			this.selectValue=getColorForSiteview(selectValue);
			colorName_ZH = Siteview.Windows.Forms.Res.get_Default() .GetString("SiteviewColorEditor" + "." + this.selectValue);
		}else { 
			colorName_ZH = "请选择颜色...";
		}
		initValue();
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText("颜色选择器");
		newShell.setLocation(0, 0);
		super.configureShell(newShell);
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(342, 360);
	}
	
	@Override
	protected Point getInitialLocation(Point initialSize) {
		// TODO Auto-generated method stub
		Point po = composites.getShell().getLocation();
		Point pp = composites.getLocation();
		return new Point(pp.x + po.x, pp.y + po.y + 70);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "选择", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout =new GridLayout(14, false);
		container.setLayout(gridLayout);
		gridLayout.verticalSpacing=8;
		gridLayout.horizontalSpacing=8;
		
		String[] s = KnownColor.GetNames(Type.GetType(KnownColor.class
				.getName()));
		for (String str : s) {
			
			final Composite cc=new Composite(container,SWT.NONE);
			final Label btnNewButton = new Label(cc, SWT.BORDER );
			
			btnNewButton.setBounds(0,0,15,15);
			String colorName = Siteview.Windows.Forms.Res.get_Default()
					.GetString("SiteviewColorEditor" + "." + str);
			KnownColor kc = (KnownColor) KnownColor.Parse(
					Type.GetType(KnownColor.class.getName()),getColorForSiteview(str));
			system.Drawing.Color c = system.Drawing.Color
					.FromKnownColor(kc.value__);
			btnNewButton.setBackground(new Color(null, c.get_R(), c.get_G(), c.get_B()));
//			System.out.println(colorName);
			btnNewButton.setToolTipText(colorName);
			ArrayList list=new ArrayList();
			list.Add(str);
			list.Add(c);
			btnNewButton.setData(list);
			btnNewButton.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseUp(MouseEvent arg0) {
					
					
				}
				
				@Override
				public void mouseDown(MouseEvent arg0) {
					if(label!=null && !label.isDisposed()){
						label.setBounds(0,0, 15, 15);
					}
					label=btnNewButton;
					btnNewButton.setBounds(0,0, 2000, 2000);
					ArrayList list=(ArrayList) btnNewButton.getData();
					colorvalue=(String) list.get_Item(0);
					color=(system.Drawing.Color) list.get_Item(1);
					GC gc=new GC(container);
					gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
					gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE));
//					System.out.println(cc.toDisplay(arg0.x, arg0.y).x+"-----------:"+arg0.x+"--"+arg0.y);
					gc.drawRectangle(cc.toControl(arg0.x, arg0.y).x,arg0.y, 16, 16);
//					System.out.println(btnNewButton.getLocation());
//					System.out.println(btnNewButton.getBounds().x+"--"+btnNewButton.getBounds().y);
					gc.dispose();
					
				}
				
				@Override
				public void mouseDoubleClick(MouseEvent arg0) {
					ArrayList list=(ArrayList) btnNewButton.getData();
					selectValue=(String) list.get_Item(0);
					color=(system.Drawing.Color) list.get_Item(1);
					okPressed();
				}
			});
		}
		return container;
	}
	
	public static String getColorForSiteview(String colorName) {
		return colorName.substring(colorName.indexOf("]") + 1,
				colorName.length());
	}
	
	private Color getColorFormString(String colorname){
		KnownColor kc = (KnownColor) KnownColor.Parse(Type.GetType(KnownColor.class.getName()),
				getColorForSiteview(colorname));
		system.Drawing.Color c = system.Drawing.Color.FromKnownColor(kc.value__);
		return new Color(null, c.get_R(), c.get_G(), c.get_B());
	}
	
	public String getSiteviewColorForColor(String colorName) {
		if(StringUtils.IsEmpty(colorName))return "";
		KnownColor kc = (KnownColor) KnownColor.Parse(Type.GetType(KnownColor.class.getName()), colorName);
		system.Drawing.Color c = system.Drawing.Color.FromKnownColor(kc.value__);
		if (c.get_IsSystemColor())	return "[SYSTEM]" + colorName;
		return "[NAMED]" + colorName;
	}
	
	@Override
	protected void okPressed() {
		if(color==null){
			MessageDialog.openInformation(getParentShell(), "提示", "请选择颜色!");
			return;
		}
		colorLabel.setBackground(new Color(null, color.get_R(), color.get_G(), color.get_B()));
		colorName_ZH = Siteview.Windows.Forms.Res.get_Default() .GetString("SiteviewColorEditor" + "." + colorvalue);
		textLabel.setText(colorName_ZH);
		super.okPressed();
	}
	
	public void initValue(){
		if(selectValue == null || "".equals(selectValue)){
			colorLabel.setBackground(new Color(null, 255, 255, 255));
		}else{
			colorLabel.setBackground(getColorFormString(selectValue));
		}
		textLabel.setText(colorName_ZH);
	}


	public String getColorvalue() {
//		return colorvalue;
		return getSiteviewColorForColor(this.selectValue);
	}


	public system.Drawing.Color getColor() {
		return color;
	}
	
	public Button getColorButton(){
		return colorButton;
	}


	public Shell getShell() {
		return shell;
	}
	
	
	
}