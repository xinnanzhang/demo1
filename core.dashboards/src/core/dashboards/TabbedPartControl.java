package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.TabbedPartDef;
import Siteview.DefRequest;
import Siteview.DefinitionObject;
import Siteview.IDisplaySettings;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageHolder;
import Siteview.Xml.DashboardPartCategory;
import Siteview.Xml.Scope;
import Siteview.Xml.XmlDashboardPartCategory;

public class TabbedPartControl extends DashboardPartControl {

	public TabbedPartControl(DashboardControl parent, int style) {
		super(parent, style);
		
		this.m_alPartDefs = new ArrayList();
        this.m_alPartRefDefs = new ArrayList();
        this.m_alInitializedParts = new ArrayList();
        new ArrayList();
	}

	public TabbedPartControl(ISiteviewApi iSiteviewApi, DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);
		
		this.m_alPartDefs = new ArrayList();
        this.m_alPartRefDefs = new ArrayList();
        this.m_alInitializedParts = new ArrayList();
        new ArrayList();
        
		stacklayout = new StackLayout();
		this.get_MainArea().setLayout(stacklayout);
	}

	private StackLayout stacklayout;
	private TabFolder tabGroup;
    private ArrayList m_alPartDefs;
    private ArrayList m_alPartRefDefs;
    private ArrayList m_alInitializedParts;
    private String m_strTabId;
    
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
        super.DefineFromDef(def, partRefDef);
        this.tabGroup = new TabFolder(this.get_MainArea(), SWT.TOP);
        if (def.get_TitleBarDef().get_Visible()){
        	//
        }
        TabbedPartDef def2 = (TabbedPartDef) def ;
        if (def2 != null){
        	ICollection z_def = def2.get_PartRefDefs();
        	IEnumerator it = z_def.GetEnumerator();
        	while(it.MoveNext()){
        		PartRefDef def3 = (PartRefDef) it.get_Current();
        		DashboardPartDef definition = (DashboardPartDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(Scope.User, super.getApi().get_SystemFunctions().get_CurrentLoginId(), "", DashboardPartDef.get_ClassName(), def3.get_Id(), "(Role)", true));
        		if (definition != null){
        			AddPart(definition,partRefDef);
        		}
        	}
        }
        //得到活动页签 只加载第一个页签.方便整个页面加速
        int count = this.tabGroup.getItemCount();
        if(count>0){
            //设置tab页内容
            this.getControl(this.tabGroup.getItem(0),0);
            this.m_alInitializedParts.Add(0);
        }
        
        //页签切换事件
        tabGroup.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					SelectedTabChanging();
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
        
        showPancel(tabGroup);
    }

	//增加页签项
	private void AddPart(DashboardPartDef partDef, PartRefDef partRefDef){
        this.m_alPartDefs.Add(partDef);
        this.m_alPartRefDefs.Add(partRefDef);
        if (partDef != null){
        	TabItem tabItem = new TabItem(tabGroup, SWT.NULL);
            tabItem.setText(partDef.get_Alias());
			tabItem.setData(partDef.get_Alias(), partDef);
			if(partDef.get_TitleBarDef().get_ImageName()!=""){
            	ImageHolder holder = ImageResolver.get_Resolver().ResolveImage(partDef.get_TitleBarDef().get_ImageName());
                if ((holder != null) && !holder.get_Empty()){
                	int width = holder.GetAsImage().get_Width();
                	int hight = holder.GetAsImage().get_Height();
                	tabItem.setImage(SwtImageConverter.ConvertToSwtImage(holder,width,hight));
                }
            }
        }
    }
	
	//设置tab页内容
	private void getControl(TabItem tab,int index){
		DashboardPartDef partDef =(DashboardPartDef) this.m_alPartDefs.get_Item(index);
		PartRefDef partRefDef = (PartRefDef) this.m_alPartRefDefs.get_Item(index);
		DashboardPartCategory category = XmlDashboardPartCategory.ToCategory(((DashboardPartDef) this.m_alPartDefs.get_Item(index)).get_CategoryAsString());
		switch(category){
			case ChartColumn:
				ChartColumnPartControl control_ChartColumn = new ChartColumnPartControl(super.getApi(),tabGroup);
				control_ChartColumn.DefineFromDef(partDef, partRefDef);
				control_ChartColumn.LoadData();
				control_ChartColumn.removeTitleBar();
				tab.setControl(control_ChartColumn);
				break;
			case ChartPie:
				ChartPiePartControl control_ChartPie = new ChartPiePartControl(super.getApi(),tabGroup);
				control_ChartPie.DefineFromDef(partDef, partRefDef);
				control_ChartPie.LoadData();
				control_ChartPie.removeTitleBar();
				tab.setControl(control_ChartPie);
				break;
			case MultiViewGrid:
				MultiViewGridPartControl control_MultiViewGrid = new MultiViewGridPartControl(super.getApi(),tabGroup,true);
				control_MultiViewGrid.DefineFromDef(partDef, partRefDef);
				control_MultiViewGrid.LoadData();
				tab.setControl(control_MultiViewGrid);
				break;
			default:
				//
				break;
		}
	}
	
	//页签变更时,加载内容
	private void SelectedTabChanging(){
		int index = tabGroup.getSelectionIndex();
		if (!this.m_alInitializedParts.Contains(index)){
			//加载内容 不重复加载
			getControl(this.tabGroup.getItem(index),index);
			this.m_alInitializedParts.Add(index);
		}
	}
	
	@Override
	public void DrillBack(){
		TabItem[] tab = this.tabGroup.getSelection();
		if(tab[0].getControl() instanceof ChartColumnPartControl){
			ChartColumnPartControl c = (ChartColumnPartControl) tab[0].getControl();
			c.DrillBack();
		}else if(tab[0].getControl() instanceof ChartPiePartControl){
			ChartPiePartControl c = (ChartPiePartControl) tab[0].getControl();
			c.DrillBack();
		}
    }
	
	private void showPancel(Composite p) {
		// TODO Auto-generated method stub
		this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
	}
	
	public String get_TabId() {
		if(m_strTabId!=null){
			return m_strTabId;
		}else{
			return (super.get_DashboardPartDef().get_Scope().toString() + "." + super.get_DashboardPartDef().get_ScopeOwner() + "." + super.get_Parent().get_DashboardPartDef().get_Name() + "." + super.get_DashboardPartDef().get_Name());
		}
	}

	public void set_TabId(String m_strTabId) {
		this.m_strTabId = m_strTabId;
	}
	
	public void Refresh() {
		if (tabGroup!=null && !tabGroup.isDisposed()){
			for (int i = 0 ; i < tabGroup.getItemCount();i++){
				if (tabGroup.getItem(i).getControl()!=null && (tabGroup.getItem(i).getControl() instanceof DashboardPartControl))
				((DashboardPartControl)tabGroup.getItem(i).getControl()).Refresh();
			}
		}
	}
}
