package core.dashboards;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import core.busobmaint.BusObMaintUC;
import core.busobmaint.BusObMaintView;

import system.Convert;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Xml.XmlElement;

import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.DefRequest;
import Siteview.IVirtualKeyList;
import Siteview.Operators;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.Field;
import Siteview.Api.ISiteviewApi;
import Siteview.Xml.GridAction;
import Siteview.Xml.Scope;

import Core.Dashboards.DashboardDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.HeaderDef;
import Core.Dashboards.PartRefDef;

public class DashboardControl extends DashboardPartControl {

	private GoToBusObEventHandler GoToBusOb;
	
	private List<DashboardPartControl> parts = new Vector<DashboardPartControl>(); 
	
	
	public DashboardControl(Composite parent, int style) {
		super(parent, style);
		this.get_MainArea().setLayout(null);
		this.get_MainArea().addControlListener(new ControlListener(){

			@Override
			public void controlMoved(ControlEvent e) {
				//System.out.println("Dashboard moved");
				
			}

			@Override
			public void controlResized(ControlEvent e) {
				//System.out.println("Dashboard resize");
				ResizeControls(true,true);
			}});
		
		GoToBusOb = new GoToBusObEventHandler(){

			@Override
			public void handler(IVirtualKeyList iVirtualKeyList,
					String strBusObName, String strRecId,
					GridAction gridAction, String strGridActionDetail) {
				if (gridAction == GridAction.GoToParent)
	            {
					ShowParentBusOb(strBusObName, strRecId, strGridActionDetail);
	            }
	            else
	            {
	                try {
						ShowBusOb(iVirtualKeyList, strBusObName, strRecId, strGridActionDetail);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	                //base.ProcessCommand("AutoTask.CustomizeList", null);
	            }
				
			}};
	}
	
	public DashboardControl(Composite parent, int style, ISiteviewApi iSiteviewApi) {
		super(iSiteviewApi,parent, style);
		
		this.get_MainArea().setLayout(null);
		this.get_MainArea().addControlListener(new ControlListener(){

			@Override
			public void controlMoved(ControlEvent e) {
				//System.out.println("Dashboard moved");
				
			}

			@Override
			public void controlResized(ControlEvent e) {
				//System.out.println("Dashboard resize");
				ResizeControls(true,true);
			}});
		
		GoToBusOb = new GoToBusObEventHandler(){

			@Override
			public void handler(IVirtualKeyList iVirtualKeyList,
					String strBusObName, String strRecId,
					GridAction gridAction, String strGridActionDetail) {
				if (gridAction == GridAction.GoToParent)
	            {
					//ShowParentBusOb(strBusObName, strRecId, strGridActionDetail);
					try {
						ShowBusOb(iVirtualKeyList, strBusObName, strRecId, strGridActionDetail);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            else
	            {
	                try {
						ShowBusOb(iVirtualKeyList, strBusObName, strRecId, strGridActionDetail);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

	                //base.ProcessCommand("AutoTask.CustomizeList", null);
	            }
				
			}};
	}
	

    public static DashboardControl CreateFromDef(Composite parent,ISiteviewApi iSiteviewApi, Core.Dashboards.DashboardDef def)
    {	
        DashboardControl control = new DashboardControl(parent,SWT.NONE,iSiteviewApi);
        if (control != null)
        {
            control.DefineFromDef(def, null);
        }
        return control;
    }
    
    public  void ShowBusOb(IVirtualKeyList keyList, String strName, String strId, String strParentLinkField) throws Exception
    {
        if(keyList != null){
        	BusObMaintView.open(getApi(), strName, keyList);
        }
        else{
            String strField = "";
            SiteviewQuery query = null;
            Siteview.Api.BusinessObject busOb = null;

            if(!strName.equals("")){
                strField = getApi().get_BusObDefinitions().GetBusinessObjectDef(strName).get_IdField().get_Name();
                
                query = this.BuildSiteviewQuery(strName, strField, strId);
            }
            if(query != null){
                
                busOb = getApi().get_BusObService().GetBusinessObject(query);
            }
            if(busOb == null){
                throw(new Exception("没找到业务对象：" + strName));
            }
            if(busOb.get_Definition().get_MasterObject()){
                this.ShowSingleBusOb(busOb);
            }
            else{
                this.ShowParentBusOb(strName,strId,strParentLinkField);
            }
        }

    }
    
    
 

	public void ShowParentBusOb(String strName, String strId, String strParentLinkField)
    {
        String strField = "";
        SiteviewQuery query = null;
        if (!strName.equals(""))
        {
            strField = getApi().get_BusObDefinitions().GetBusinessObjectDef(strName).get_IdField().get_Name();
            query = this.BuildSiteviewQuery(strName, strField, strId);
        }
        if (query != null)
        {
            Siteview.Api.BusinessObject businessObject = getApi().get_BusObService().GetBusinessObject(query);
            if (businessObject != null)
            {
                String str2 = "";
                String str3 = "";
                strParentLinkField = !(strParentLinkField ==null || strParentLinkField.equals("")) ? strParentLinkField : "ParentLink";
                Field field = businessObject.GetField(strParentLinkField);
                if (field != null)
                {
                    Field subFieldByPurpose = field.GetSubFieldByPurpose("LinkCategory");
                    if (subFieldByPurpose != null)
                    {
                        str2 = subFieldByPurpose.GetString();
                    }
                    Field field3 = field.GetSubFieldByPurpose("LinkID");
                    if (field3 != null)
                    {
                        str3 = field3.GetString();
                    }
                }
                if ((!str2.equals("")) && (!str3.equals("")))
                {
                    try
                    {
                        this.ShowSingleBusOb(str2, str3);
                    }
                    catch (Exception exception)
                    {
                        String msg = "没有找到业务对象：" + strName;
                        ErrorDialog.openError(getShell(), "错误。", msg, Status.OK_STATUS);
                    }
                }
            }
        }
    }
    
    
    
    
    private void ShowSingleBusOb(String name, String strValue) {
    	Siteview.Api.BusinessObject busOb;
    	if((!name.equals(""))){
            if((!strValue.equals(""))){
                BusinessObjectDef businessObjectDef = getApi().get_BusObDefinitions().GetBusinessObjectDef(name);
                SiteviewQuery query = this.BuildSiteviewQuery(name, businessObjectDef.get_IdField().get_StorageName(), strValue);
                
                busOb = getApi().get_BusObService().GetBusinessObject(query);
            }
            else{
                
                busOb = getApi().get_BusObService().Create(name);
            }
            ShowSingleBusOb( busOb);
        }
		
	}
    
    private void ShowSingleBusOb(BusinessObject busOb) {
    	BusObMaintView.open(getApi(), busOb);
 		
 	}

	private  SiteviewQuery BuildSiteviewQuery(String strType, String strField, String strValue)
    {
        SiteviewQuery query = new SiteviewQuery();
        query.AddBusObQuery(strType,QueryInfoToGet.All);
        String str = String.format("%s.%s", strType, strField);
        XmlElement element = query.get_CriteriaBuilder().FieldAndValueExpression(str, Operators.Equals, strValue);
        query.set_BusObSearchCriteria(element);

        return query;

    }

    private void ResizeControls(boolean bChangeHeight, boolean bChangeWidth)
    {
    	super.get_MainArea().setLayout(null);//new GridLayout(this.get_DashboardDef().get_TableSizeDef().get_ColumnCount(),true));
    	
    	for(Control c :super.get_MainArea().getChildren()){
    		if (c instanceof DashboardPartControl){
    			((DashboardPartControl) c).ResizeControl(bChangeHeight, bChangeWidth);
    		}
    	}
    }
    
    @Override
    protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef)
    {
        if (def != null)
        {
            super.DefineFromDef(def, partRefDef);
            if(this.get_DashboardDef() != null)
            {
                DashboardPartDef partDef = null;
                ICollection partDefs = this.get_DashboardDef().get_PartRefDefs();
                List<PartRefDef> listPrd = new Vector<PartRefDef>();
                IEnumerator it = partDefs.GetEnumerator();
                while(it.MoveNext()){
                	PartRefDef def3 = (PartRefDef) it.get_Current();
                	listPrd.add(def3);
                }
                Collections.sort(listPrd,new Comparator<PartRefDef>(){

					@Override
					public int compare(PartRefDef o1, PartRefDef o2) {
						if (o1.get_StartRow()>o2.get_StartRow())
							return 1;
						else if (o1.get_StartRow() == o2.get_StartRow()){
							if (o1.get_StartColumn()>o2.get_StartColumn())
								return 1;
							else
								return -1;
						}else
							return -1;
					}});
                
                for(PartRefDef def3: listPrd){
                	partDef = (DashboardPartDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(Scope.User, super.getApi().get_SystemFunctions().get_CurrentLoginId(), DashboardPartDef.get_ClassName(), def3.get_Id(), true));
                	if (partDef == null)
                    {
                		partDef = (DashboardPartDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ByName(Scope.User, super.getApi().get_SystemFunctions().get_CurrentLoginId(), DashboardPartDef.get_ClassName(), def3.get_Name(), true));
                    }
                	this.AddPartControl(partDef, def3);
                }
                
                
                ResizeControls(true,true);
                
//                this.m_TitledControlHolder = new Siteview.Windows.Forms.TitledControlHolder();
//                this.m_TitledControlHolder.Title = this.DashboardDef.TitleBarDef.Text;
//                this.m_TitledControlHolder.ShowDate = this.DashboardDef.TitleBarDef.ShowDate;
//                ImageHolder holder = ImageResolver.Resolver.ResolveImage(this.DashboardDef.TitleBarDef.ImageName);
//                if (!holder.Empty)
//                {
//                    this.m_TitledControlHolder.Image = holder.Image;
//                }
//                if (this.DashboardDef.TitleBarDef.UseParentColor)
//                {
//                    ColorGallery colorGallery = ColorGallery.GetColorGallery();
//                    this.m_TitledControlHolder.TitleForeColor = colorGallery.GetColor("TitleDisplayControl.TitleText");
//                    this.m_TitledControlHolder.DateForeColor = colorGallery.GetColor("TitleDisplayControl.TitleText");
//                    this.m_TitledControlHolder.StartColor = colorGallery.GetColor("TitleDisplayControl.BackColor1");
//                    this.m_TitledControlHolder.EndColor = colorGallery.GetColor("TitleDisplayControl.BackColor2");
//                }
//                else
//                {
//                    this.m_TitledControlHolder.TitleForeColor = ColorResolver.Resolver.ResolveColor(this.DashboardDef.TitleBarDef.TextForeColor);
//                    this.m_TitledControlHolder.DateForeColor = ColorResolver.Resolver.ResolveColor(this.DashboardDef.TitleBarDef.TextForeColor);
//                    this.m_TitledControlHolder.LinearGradientMode = this.DashboardDef.TitleBarDef.BackgroundDef.IsLeftToRightGradient ? LinearGradientMode.Horizontal : LinearGradientMode.Vertical;
//                    this.m_TitledControlHolder.IsSolidColor = this.DashboardDef.TitleBarDef.BackgroundDef.IsSolidColor;
//                    this.m_TitledControlHolder.StartColor = ColorResolver.Resolver.ResolveColor(this.DashboardDef.TitleBarDef.BackgroundDef.FirstColor);
//                    this.m_TitledControlHolder.EndColor = ColorResolver.Resolver.ResolveColor(this.DashboardDef.TitleBarDef.BackgroundDef.SecondColor);
//                }
            }
        }
    }

	private void AddPartControl(DashboardPartDef partDef, PartRefDef partRefDef) {
		if (partDef != null){
			DashboardPartControl control = DashboardPartControl.CreateFromDef(super.getApi(), this, partDef, partRefDef);
			//control.setFocus();
			if (control != null)
				parts.add(control);
		}
		
	}
	
    public Rectangle GetPartRectangleFromIndex(int nStartRow, int nEndRow, int nStartColumn, int nEndColumn)
    {
        int x = this.GetColumnPositionFromIndex(nStartColumn) + this.get_DashboardDef().get_TableSizeDef().get_SeparatorThickness();
        int y = this.GetRowPositionFromIndex(nStartRow) + this.get_DashboardDef().get_TableSizeDef().get_SeparatorThickness();
        int width = this.GetCellSize(nStartColumn, nEndColumn, false) - this.get_DashboardDef().get_TableSizeDef().get_SeparatorThickness();
        return new Rectangle(x, y, width, this.GetCellSize(nStartRow, nEndRow, true) - this.get_DashboardDef().get_TableSizeDef().get_SeparatorThickness());
    }
    
    private int GetCellSize(int nStartIndex, int nEndIndex, boolean bRow)
    {
        int num = 0;
        ArrayList list = bRow ? this.GetHeaderPositions(true) : this.GetHeaderPositions(false);
        if (((nStartIndex != -1) && (nEndIndex != -1)) && ((nEndIndex + 1) <= list.get_Count()))
        {
            num = ((Integer) list.get_Item(nEndIndex + 1)) - ((Integer) list.get_Item(nStartIndex));
        }
        return num;
    }
    
    private int GetRowPositionFromIndex(int nIndex)
    {
        int num = 0;
        ArrayList headerPositions = this.GetHeaderPositions(true);
        if ((nIndex != -1) && (nIndex <= (headerPositions.get_Count() - 1)))
        {
            num = (Integer) headerPositions.get_Item(nIndex);
        }
        return num;
    }
    
    private int GetColumnPositionFromIndex(int nIndex)
    {
        int num = 0;
        ArrayList headerPositions = this.GetHeaderPositions(false);
        if ((nIndex != -1) && (nIndex <= (headerPositions.get_Count() - 1)))
        {
            num = (Integer) headerPositions.get_Item(nIndex);
        }
        return num;
    }
    
    private ArrayList GetHeaderPositions(boolean bRow)
    {
//        if (!this.get_DashboardDef().get_TableSizeDef().get_IsEqualCellSize())
//        {
//            return this.GetNonEqualCellPositions(bRow);
//        }
        return this.GetEqualCellPositions(bRow);
    }
    
    private ArrayList GetEqualCellPositions(boolean bRow)
    {
        ArrayList list = new ArrayList();
        int num = bRow ? this.get_DashboardDef().get_TableSizeDef().get_RowCount() : this.get_DashboardDef().get_TableSizeDef().get_ColumnCount();
        int totalAreaSize = this.GetTotalAreaSize(bRow);
        int num3 = 0;
        if ((totalAreaSize != 0) && (num != 0))
        {
            num3 = totalAreaSize / num;
        }
        int num4 = 0;
        list.Add(num4);
        for (int i = 0; i <= (num - 1); i++)
        {
            num4 += num3;
            list.Add(num4);
        }
        return list;
    }
    
    private ArrayList GetNonEqualCellPositions(boolean bRow)
    {
        ArrayList list = new ArrayList();
        ArrayList list2 = (ArrayList) (bRow ? (this.get_DashboardDef().get_TableSizeDef().get_RowList()) : (this.get_DashboardDef().get_TableSizeDef().get_ColumnList()));
        int totalAreaSize = this.GetTotalAreaSize(bRow);
        int num2 = 0;
        list.Add(num2);
        HeaderDef def = null;
        for (int i = 0; i <= (list2.get_Count() - 1); i++)
        {
            def = (HeaderDef) list2.get_Item(i);
            if (def.get_IsPercentage())
            {
                num2 += Convert.ToInt32(Math.ceil((double) (totalAreaSize * def.get_Size())));
            }
            else
            {
                num2 += Convert.ToInt32(def.get_Size());
            }
            list.Add(num2);
        }
        return list;
    }
    
    private int GetTotalAreaSize(boolean bRow)
    {
        int num = bRow ? super.get_MainArea().getBounds().height: super.get_MainArea().getBounds().width;
        if (bRow)
        {
            return (num - ((this.get_DashboardDef().get_TableSizeDef().get_RowList().get_Count() + 1) 
            		* this.get_DashboardDef().get_TableSizeDef().get_SeparatorThickness()));
        }
        return (num - ((this.get_DashboardDef().get_TableSizeDef().get_ColumnList().get_Count() + 1) 
        		* this.get_DashboardDef().get_TableSizeDef().get_SeparatorThickness()));
    }

	public DashboardDef get_DashboardDef() {
		if (super.get_DashboardPartDef() instanceof DashboardDef)
			return (DashboardDef) super.get_DashboardPartDef();
		else
			return null;
	}
	
	
	@Override
	protected void SetTitleBar(){
		super.removeTitleBar();
	}
	
    public  void OnGoToBusOb(IVirtualKeyList iVirtualKeyList, String strBusObName, String strRecId, GridAction gridAction, String strGridActionDetail)
    {
        try{
            if(this.GoToBusOb != null){
                this.GoToBusOb.handler(iVirtualKeyList, strBusObName, strRecId, gridAction, strGridActionDetail);
            }
        }
        catch(system.Exception exception){
        	System.err.println(exception.getMessage());
            super.OnAlertRaised(this,new AlertRaisedEventArgs(AlertType.Error, exception));
        }

    }

	public void Refresh() {
		
		for(DashboardPartControl c:parts){
			c.Refresh();
		}
	}
	
}
