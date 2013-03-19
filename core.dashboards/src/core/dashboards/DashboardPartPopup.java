package core.dashboards;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import Core.Dashboards.DashboardDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.PartRefDef;
import Siteview.DefRequest;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageHolder;
import Siteview.Xml.Scope;

public class DashboardPartPopup extends Dialog {

	protected Object result;
	protected Shell shell;
	private ISiteviewApi m_Api;
    private DashboardPartDef m_PartDef;
    private PartRefDef m_PartRefDef;
    private DashboardControl m_parent;
    
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public DashboardPartPopup(Shell parent, int style) {
		super(parent, style);
		setText("Popup window");
	}
	public DashboardPartPopup(Shell parent, int style,ISiteviewApi api, DashboardControl parent1, PartRefDef partRefDef) {
		super(parent, style);
		setText("Popup window");
		
		this.m_Api = api;
		this.m_PartRefDef = partRefDef;
		this.m_parent = parent1;
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setSize(450, 300);
		shell.setText(getText());
		
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		this.m_PartDef = (DashboardPartDef) this.m_Api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(Scope.User, this.m_Api.get_SystemFunctions().get_CurrentLoginId(), DashboardPartDef.get_ClassName(), this.m_PartRefDef.get_Id(), true));
		if (this.m_PartDef == null){
			this.m_PartDef = (DashboardPartDef) this.m_Api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ByName(Scope.User, this.m_Api.get_SystemFunctions().get_CurrentLoginId(), DashboardPartDef.get_ClassName(), this.m_PartRefDef.get_Name(), true));
		}
		
		// 克隆DashboardControl作为新的弹出窗口的基控件
		DashboardDef ddd = (DashboardDef)m_parent.get_DashboardDef().CloneForEdit();
		ddd.ClearPartRefDefs();
		DashboardControl dc = DashboardControl.CreateFromDef(shell, m_Api, ddd);
		AddPartControl(dc,m_PartDef);
		
		dc.pack();
		dc.layout();
		this.set_Location(shell);
	}
	
	//得到要弹出的窗口内容
	private void AddPartControl(DashboardControl parent,DashboardPartDef partDef){
		DashboardPartControl dpc = null;
		if (partDef != null){
			try{
				// 克隆需弹出的PartRefDef更改显示位置
				PartRefDef mprd = (PartRefDef) m_PartRefDef.CloneForEdit();
				mprd.set_StartColumn(0);
				mprd.set_EndColumn(parent.get_DashboardDef().get_TableSizeDef().get_ColumnCount()-1);
				mprd.set_StartRow(0);
				mprd.set_EndRow(parent.get_DashboardDef().get_TableSizeDef().get_RowCount()-1);
				
				dpc = DashboardPartControl.CreateFromDef(this.m_Api, parent, partDef, mprd);
			}catch (Exception exception){
				//
			}
		}else{
			//String strError = StringUtils.SetToken(Res.get_Default().GetString("Dashboarddpc.PartDefNotFound"), "PARTNAME", this.m_PartRefDef.get_Alias());
		}
		if (dpc != null){
			if (this.m_PartDef != null){
				shell.setText(this.m_PartDef.get_Alias());
	        }else{
	        	shell.setText(this.m_PartRefDef.get_Alias());
	        }
			//设置图标
	        ImageHolder holder = ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.ComponentYellow.png");
	        if (holder !=null && (holder.get_HasIcon() || holder.get_HasImage())) {
	        	int width = 16;
            	int hight = 16;
            	shell.setImage(SwtImageConverter.ConvertToSwtImage(holder,width,hight));
	        }
			//设置大小
	        if(this.m_parent!=null){
	        	shell.setSize(m_parent.getSize());
	        }else{
	        	shell.setSize(700, 500);
	        }
	        //加载数据
	        try{
	        	dpc.LoadData();
	        }catch (Exception exception2){
	        	dpc.dispose();
	        }
	        //dpc.setParent(parent);
	        //dpc.set_Parent(parent);
	        //隐藏弹出按钮
	        dpc.get_TitleBar().get_PopupButton().setVisible(false);
	        //显示内容
	    	dpc.layout();
		}
	}
	
	//居中.
	private void set_Location(Control c){
		Dimension dem = Toolkit.getDefaultToolkit().getScreenSize();
		int sHeight=dem.height;
		int sWidth=dem.width;
		int fHeight=shell.getSize().y;
		int fWidth=shell.getSize().x;
		c.setLocation((sWidth-fWidth)/2, (sHeight-fHeight)/2-20);
	}
}