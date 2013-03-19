package core.dashboards;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import core.search.form.dialog.SearchParamDialog;

import siteview.windows.forms.ConvertUtil;
import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.ArrayList;
import system.Collections.Hashtable;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.LinkDef;
import Core.Dashboards.LinkListPartDef;
import Core.Dashboards.PartRefDef;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.ColorResolver;
import Siteview.DefRequest;
import Siteview.IQueryResult;
import Siteview.PlaceHolder;
import Siteview.QueryGroupDef;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.SiteviewQueryOrderByField;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.ColumnDef;
import Siteview.Api.FieldDef;
import Siteview.Api.GridDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageHolder;
import Siteview.Xml.SecurityRight;

public class LinkListPartControl extends DashboardPartControl {

	public LinkListPartControl(DashboardControl parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	public LinkListPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, SWT.NONE);

		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);

		this.gridControl = new GridPartControl(super.getApi(), this.get_MainArea(), true);
		this.gridControl.set_Parent(get_Parent());
		gridControl.removeTitleBar();
		
		this.MainControl = new ScrolledComposite(this.get_MainArea(),SWT.V_SCROLL);
		this.MainControl.setLayout(new GridLayout(1, false));
		this.MainControl.setParent(get_Parent());
	}
	
    private GridPartControl gridControl;
    private boolean m_GridMode;
    private Composite MainControl;
    private ScrolledComposite MainScs;
    private int z_space = 10;
    private StackLayout stacklayout;
    
    @Override
    protected  void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
    	boolean flag = false;
        super.DefineFromDef(def, partRefDef);
        
        MainScs = new ScrolledComposite(this.get_MainArea(),SWT.H_SCROLL | SWT.V_SCROLL);
        
        this.MainControl = new Composite(MainScs,SWT.None);
        
        LinkListPartDef dashboardPartDef = (LinkListPartDef) super.get_DashboardPartDef();
//        Color color = ColorResolver.get_Resolver().ResolveColor(def.get_BackgroundDef().get_FirstColor());
//        if (color != Color.get_Transparent()){
//            this.MainControl.setBackground(color);
//        }
        Link link1 = null;
    	ICollection links = dashboardPartDef.get_LinkDefs();
        IEnumerator it = links.GetEnumerator();
        while(it.MoveNext()){
        	LinkDef def3 = (LinkDef) it.get_Current();
            if (def3.get_IsQuery()){
                flag = this.HasBusObRight(def3);
            }else{
                flag = true;
            }
            int z_left = 10;
            if (flag){
            	if ("".compareTo(def3.get_ImageName()) != 0){
	            	try{
	                    ImageHolder ih = ImageResolver.get_Resolver().ResolveImage(def3.get_ImageName());
	                    Label img = new Label(MainControl,SWT.NONE);
	                    img.setLocation(10, z_space);
	                    if (ih !=null && (ih.get_HasIcon() || ih.get_HasImage())){
	                    	img.setImage(SwtImageConverter.ConvertToSwtImage(ih,ih.GetAsImage().get_Width(),ih.GetAsImage().get_Height()));
	                    	z_left += ih.GetAsImage().get_Width();
	                    }
	                    img.pack();
	                    
	                }catch(Exception e){
	                	e.printStackTrace();
	                }
            	}
                link1 = new Link(MainControl,SWT.NONE);
                link1.setText("<a>"+def3.get_Alias()+"</a>");
                link1.setData(def3);
                link1.setToolTipText(def3.get_Alias());
                link1.setSize(getTextWidth(link1.getText(),1000),20);
                link1.setLocation(z_left, z_space);
                z_space = link1.getLocation().y + 25;
                link1.addSelectionListener(new SelectionAdapter() {
            	   	@Override
            	   	public void widgetSelected(SelectionEvent e) {
            	   		LinkClicked(e);
            	   	}});
            }
        }

        MainScs.setContent(this.MainControl);
        MainScs.setExpandHorizontal(true);
        MainScs.setExpandVertical(true);
        
        Point p = this.MainControl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        MainScs.setMinSize(p.x, p.y);
        
        showPanel(this.MainScs);
    }
    
    @Override
    public void DataBind(Object dt){
        super.DataBind(dt);
        if (this.m_GridMode){
            this.gridControl.DataBind(dt);
        }else if (dt != null){
            Hashtable hashtable = (Hashtable) dt;
            for (Control control : this.MainControl.getChildren()){
            	if(control.getData() instanceof LinkDef){
            		LinkDef tag = (LinkDef) control.getData();
                    if (tag.get_IsQuery()){
                        Object obj2 = hashtable.get_Item(tag);
                        if (obj2 != null){
                            int num = (Integer) obj2;
                            Link link = (Link) control;
                            if (link != null){
                            	link.setText("<a>"+tag.get_Alias() + " (" + num + ")</a>");
                            	link.setToolTipText(tag.get_Alias());
                            	link.setSize(getTextWidth(link.getText(),1000),20);
                            }
                        }
                    }
            	}
            }
            this.MainScs.setContent(this.MainControl);
            this.MainScs.setExpandHorizontal(true);
            this.MainScs.setExpandVertical(true);
            Point p = this.MainControl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            MainScs.setMinSize(p.x, p.y);

            showPanel(this.MainScs);
        }
    }
    
	@Override
    public Object GetData(ILoadingStatusSink sink){
        Hashtable hashtable = null;
        try{
            if (this.m_GridMode){
                return this.gridControl.GetData(sink);
            }
            LinkListPartDef dashboardPartDef = (LinkListPartDef) super.get_DashboardPartDef();
            int count = dashboardPartDef.get_LinkDefs().get_Count();
            hashtable = new Hashtable(dashboardPartDef.get_LinkDefs().get_Count());
            int currentValue = 0;
            ICollection def = dashboardPartDef.get_LinkDefs();
            IEnumerator it = def.GetEnumerator();
            while(it.MoveNext()){
            	LinkDef def2 = (LinkDef) it.get_Current();
            	if (sink != null){
                    sink.ChangeProgress(currentValue, count);
                }
                if (def2.get_IsQuery() && this.HasBusObRight(def2)){
                    SiteviewQuery siteviewQuery = this.GetSiteviewQuery(def2);
                    SiteviewQuery query = new SiteviewQuery();
                    query.AddBusObQuery(siteviewQuery.get_BusinessObjectName(), QueryInfoToGet.Count);
                    query.set_BusObSearchCriteria(siteviewQuery.get_BusObSearchCriteria());
                    IQueryResult result = super.getApi().get_BusObService().get_SimpleQueryResolver().ResolveQuery(query);
                    hashtable.Add(def2, result.get_Found());
                }
                currentValue++;
            }
            return hashtable;
        }catch (system.Exception exception){
            if (!super.get_TrapErrors()){
                throw exception;
            }
            super.OnAlertRaised(this, new AlertRaisedEventArgs(AlertType.Error, exception));
            return hashtable;
        }
	}
	
	private void LinkClicked(SelectionEvent e){
        super.set_CanDrillBack(true);
        Control c = (Control)e.getSource();
        LinkDef tag = (LinkDef) c.getData();
        if (tag != null){
            if (tag.get_IsQuery()){
                try{
                	QueryGroupDef queryGroupDef = (QueryGroupDef) super.get_Parent().getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(tag.get_QueryGroupScope(), tag.get_QueryGroupScopeOwner(), tag.get_QueryGroupLinkedTo(), QueryGroupDef.get_ClassName(), tag.get_QueryGroupId()));
                    SiteviewQuery siteviewQuery = this.GetSiteviewQuery(tag);
                    GridDef gridDefById = super.getApi().get_Presentation().GetGridDefById(tag.get_GridDefID());
                    siteviewQuery = siteviewQuery.Clone();
                    if (!siteviewQuery.get_HaveSearchParameters()){
                        if (gridDefById != null){
                            this.gridControl.set_GridDef(gridDefById);
                        }else{
                            gridDefById = this.GenerateDefaultGridDef(tag.get_QueryGroupLinkedTo());
                            this.gridControl.set_GridDef(gridDefById);
                        }
                        siteviewQuery.set_InfoToGet(QueryInfoToGet.RequestedFields);
                        siteviewQuery.set_RequestedFields((ArrayList) this.GetRequestedFields(this.gridControl.get_GridDef(), tag));
                        siteviewQuery = this.AddQueryOrder(siteviewQuery, tag.get_GridOrderByFieldName(), tag);
                        this.gridControl.set_Query(siteviewQuery);
                        this.GoToDashboardGrid(tag);
                    }else{
                    	SearchParamDialog searchParamDialog = new SearchParamDialog(getShell(),queryGroupDef);
						int result = searchParamDialog.open();
						if(result != Dialog.OK){
							gridControl.setCursor(new Cursor(null,SWT.CURSOR_ARROW));
							return;
						}else{
							siteviewQuery = queryGroupDef.get_SiteviewQuery();
							if (gridDefById != null){
	                            this.gridControl.set_GridDef(gridDefById);
	                        }else{
	                            gridDefById = this.GenerateDefaultGridDef(tag.get_QueryGroupLinkedTo());
	                            this.gridControl.set_GridDef(gridDefById);
	                        }
	                        siteviewQuery.set_InfoToGet(QueryInfoToGet.RequestedFields);
	                        siteviewQuery.set_RequestedFields((ArrayList) this.GetRequestedFields(this.gridControl.get_GridDef(), tag));
	                        siteviewQuery = this.AddQueryOrder(siteviewQuery, tag.get_GridOrderByFieldName(), tag);
	                        this.gridControl.set_Query(siteviewQuery);
	                        this.GoToDashboardGrid(tag);
						}
                    }
                }catch (Exception exception){
                    //ErrorDialog.ShowErrorDialog(exception);
                }
            }
        }
    }
	
	@Override
	public void DrillBack(){
        if (super.get_CanDrillBack() && this.m_GridMode){
            showPanel(this.MainScs);
        }
    }
	
	private SiteviewQuery AddQueryOrder(SiteviewQuery query, String strOrderByFieldName, LinkDef linkDef){
        if (query != null){
            String[] astrField = null;
            if ("".compareTo(strOrderByFieldName) != 0){
                if (this.OrderFieldAlreadyIncluded(query, strOrderByFieldName)){
                    astrField = null;
                }else{
                    astrField = new String[] { strOrderByFieldName };
                }
            }else{
                GridDef definition = (GridDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(GridDef.get_ClassName(), linkDef.get_GridDefID()));
                if (((definition != null) && (definition.get_ColumnDefs() != null)) && (definition.get_ColumnDefs().get_Count() > 0)){
                    IEnumerator enumerator = definition.get_ColumnDefs().GetEnumerator();
                    enumerator.MoveNext();
                    ColumnDef current = (ColumnDef) enumerator.get_Current();
                    String[] strArray2 = current.get_QualifiedName().split("\\.");
                    int length = strArray2.length;
                    if (length > 1){
                        astrField = new String[] { linkDef.get_QueryGroupLinkedTo() + "." + strArray2[length - 1] };
                    }
                }
            }
            if (astrField != null){
                query.AddOrderBy(astrField);
            }
        }
        return query;
    }
	
	private boolean OrderFieldAlreadyIncluded(SiteviewQuery query, String strOrderByFieldName){
        new ArrayList();
        if (query.get_OrderByList() != null){
        	ICollection z_field = query.get_OrderByList();
            IEnumerator it = z_field.GetEnumerator();
            while(it.MoveNext()){
            	SiteviewQueryOrderByField field = (SiteviewQueryOrderByField) it.get_Current();
            	if (field.get_Name().equals(strOrderByFieldName)){
                    return true;
                }
            }
        }
        return false;
    }
	
	private GridDef GenerateDefaultGridDef(String BusObName){
        GridDef gridDefById = null;
        ICollection z_holder = super.getApi().get_LiveDefinitionLibrary().GetPlaceHolderList(DefRequest.ForList(GridDef.get_ClassName()));
        IEnumerator it = z_holder.GetEnumerator();
        while(it.MoveNext()){
        	PlaceHolder holder = (PlaceHolder)it.get_Current();
        	if (holder.get_LinkedTo().compareTo(BusObName) == 0){
                gridDefById = super.getApi().get_Presentation().GetGridDefById(holder.get_Id());
            }
        }
        return gridDefById;
    }
	
	private ICollection GetRequestedFields(GridDef gridDef, LinkDef linkDef){
        BusinessObjectDef businessObjectDef = super.getApi().get_BusObDefinitions().GetBusinessObjectDef(linkDef.get_QueryGroupLinkedTo());
        ArrayList list = new ArrayList();
        boolean flag = false;
        if (businessObjectDef.get_ChildOfGroup() && !BusinessObjectDef.IsDerivedClass(gridDef.get_BaseObject())){
        	ICollection z_gdf = gridDef.get_ColumnDefs();
            IEnumerator it = z_gdf.GetEnumerator();
            while(it.MoveNext()){
            	ColumnDef def2 = (ColumnDef) it.get_Current();
            	ICollection z_bodf = businessObjectDef.get_FieldDefs();
                IEnumerator it1 = z_bodf.GetEnumerator();
                while(it1.MoveNext()){
                	FieldDef def3 = (FieldDef) it1.get_Current();
                	if (def3.get_BaseQualifiedName().compareTo(def2.get_QualifiedName()) == 0){
                        list.Add(def3.get_QualifiedName());
                        if (def3.get_QualifiedName().compareTo(businessObjectDef.get_IdField().get_QualifiedName()) == 0){
                            flag = true;
                        }
                        break;
                    }
                }
            }
            if (!flag){
                list.Add(businessObjectDef.get_IdField().get_QualifiedName());
            }
//            FieldDef def4 = null;
//            if (super.CheckGridRowColor(businessObjectDef, def4) && (def4 != null)){
//                list.Add(def4.get_QualifiedName());
//            }
            return list;
        }
        list.AddRange(gridDef.get_ColumnNames());
//        FieldDef fDef = null;
//        if (super.CheckGridRowColor(businessObjectDef, fDef) && (fDef != null)){
//            list.Add(fDef.get_QualifiedName());
//        }
        return list;
    }
	
    private SiteviewQuery GetSiteviewQuery(LinkDef linkDef){
        SiteviewQuery siteviewQuery = null;
        if ("".compareTo(linkDef.get_XmlQuery()) != 0){
            return new SiteviewQuery(linkDef.get_XmlQuery());
        }
        QueryGroupDef definition = (QueryGroupDef) super.get_Parent().getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(linkDef.get_QueryGroupScope(), linkDef.get_QueryGroupScopeOwner(), linkDef.get_QueryGroupLinkedTo(), QueryGroupDef.get_ClassName(), linkDef.get_QueryGroupId()));
        if (definition != null){
            siteviewQuery = definition.get_SiteviewQuery();
        }
        return siteviewQuery;
    }
    
    private boolean HasBusObRight(LinkDef linkDef){
        boolean flag = false;
        SiteviewQuery siteviewQuery = this.GetSiteviewQuery(linkDef);
        if (siteviewQuery != null){
            flag = super.getApi().get_SecurityService().HasBusObRight(siteviewQuery.get_BusinessObjectName(), SecurityRight.View);
        }
        return flag;
    }
    
    private void GoToDashboardGrid(LinkDef def) {
        this.gridControl.set_BusObName(def.get_QueryGroupLinkedTo());
        this.gridControl.set_BusObDef(super.getApi().get_BusObDefinitions().GetBusinessObjectDef(this.gridControl.get_BusObName()));
        this.gridControl.set_GridId(this.gridControl.get_GridDef().get_Id());
        this.gridControl.setVisible(true);
        showPanel(gridControl);
        this.gridControl.set_LimitQuery(false);
        this.gridControl.set_OrderByFieldName(def.get_GridOrderByFieldName());
        this.gridControl.set_PartLoader(super.get_PartLoader());
        this.m_GridMode = true;
        this.gridControl.set_PartLoader(super.get_PartLoader());
        this.DoDataRefresh();
	}
    
    private void showPanel(Composite p) {
		// TODO Auto-generated method stub
    	this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
	}
    
    public int getTextWidth(String strValue, int MaxLenth){
    	// 半角 英文字体使用Arial，普通模式，大小10（单位是磅，中文中的“一号”，“二号”等这样的分类不支持。）
        Font f = new Font("g宋体", Font.PLAIN, 10);
        // 全角 中文，字体MSGothic，普通模式，大小也是10.
        Font jpf = new Font("MSGothic", Font.PLAIN, 10);
        FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
        FontMetrics zffm = Toolkit.getDefaultToolkit().getFontMetrics(jpf);
        
        int totalWidth = 0;
        String strReturn = "";
        String tmpChar = "";
        int w_width = zffm.stringWidth("W");
        if (strValue != null){
            int chr_width = 0;
            for (int i = 0; i < strValue.length(); i++){
                // 取得某一个字符
                tmpChar = strValue.substring(i, i + 1);
                // 判断是否是全角字符，半角使用英文字体，全角使用日文字体进行计算宽度
                if (tmpChar.getBytes().length == 1){
                    chr_width = fm.stringWidth(tmpChar);
                }else{
                    chr_width = zffm.stringWidth(tmpChar);
                }
                // 总宽度大于指定的宽度后，截取终了。
                if ((totalWidth + chr_width) > w_width * MaxLenth){
                    break;
                }
                strReturn += tmpChar;
                totalWidth += chr_width;
            }
        }
        return totalWidth;
    }
}
