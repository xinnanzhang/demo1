package core.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorPart;

import Core.Dashboards.DashboardDef;
import Siteview.Api.ISiteviewApi;
import core.dashboards.DashboardControl;

public class DashboardPanel extends EditorPart {

	public static final String ID = "core.editors.DashboardPanel"; //$NON-NLS-1$
	
	private ISiteviewApi siteviewApi = null;
	private DashboardDef dashboardDef = null;
	private DashboardControl control = null;
	
	public DashboardPanel() {
	}

	/**
	 * Create contents of the editor part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		//container.setLayout(new FillLayout());
		container.setLayout(null);
		
//		Composite child = new Composite(container, SWT.NONE);
//		
//		child.setBackground(new Color(Display.getCurrent(), 255, 123, 125));
//		
//		Button bt = new Button(container,SWT.NONE);
//		bt.setBounds(0, 0, 500, 200);
		
		if (dashboardDef != null){
			
			//dashboardDef.get_TableSizeDef().set_IsEqualCellSize(true);
			
			this.setPartName(dashboardDef.get_TitleBarDef().get_Text().equals("")?dashboardDef.get_Alias():dashboardDef.get_TitleBarDef().get_Text());
			control = DashboardControl.CreateFromDef(container, siteviewApi, dashboardDef);
			//control.setBackground(new Color(Display.getCurrent(), 255, 0, 0));
			control.setLocation(0, 0);
		}
		
		container.addControlListener(new ControlListener(){

			@Override
			public void controlMoved(ControlEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void controlResized(ControlEvent e) {
				Composite c = (Composite) e.getSource();
				if (control != null)
					control.setSize(c.getSize());
				
			}});
		
		//System.out.println("create part:" + this.getTitle());
//		this.addPartPropertyListener(new IPropertyChangeListener(){
//
//			@Override
//			public void propertyChange(PropertyChangeEvent event) {
//				if (event.getProperty().equals("Refresh")){
//					Refresh();
//				}
//				
//			}});

	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Do the Save operation
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}
	
	public void Refresh(){
		if (control!=null){
			control.Refresh();
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setInput(input);
        this.setSite(site);
        
        Action refreshAction = new Action() {
		    public void run() {
		       Refresh();
		    }
		};
		
		this.getEditorSite().getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
		
        if (input instanceof DashboardInput){
        	DashboardInput dinput = (DashboardInput) input;
        	dashboardDef = dinput.getDashboardDef();
        	siteviewApi = dinput.getApi();
        }
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	public static void open(final ISiteviewApi api,final DashboardDef def){
		BusyIndicator.showWhile(Display.getDefault(), new Runnable(){

			@Override
			public void run() {
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new DashboardInput(api,def), DashboardPanel.ID);
						//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(DashboardView.ID);
					} catch (PartInitException err) {
						// TODO Auto-generated catch block
						err.printStackTrace();
					}
				}
				
			});
	}


}
