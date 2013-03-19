package core.ui.dialog;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.IList;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.TabbedPartDef;
import Siteview.DefRequest;
import Siteview.IDefinition;
import Siteview.PlaceHolder;
import Siteview.StringUtils;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageChooseDlg;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.DashboardPartCategory;
import Siteview.Xml.Scope;
import Siteview.Xml.SecurityRight;
import Siteview.Xml.XmlDashboardPartCategory;
import core.ui.DashBoardPartCenterForm;

public class MotionsPart extends Dialog {

	private Text txtName;
	private Text txtAlias;
	private Combo cbScope_1; // 范围下拉式
	private Combo cbOwner_1; // 负责人下拉式
	private List list_range;
	private List list_addrange;
	private Generalmethods general;
	private DefinitionLibrary m_Library;
	private IDefinition m_Def;
	private ScopeUtil m_ScopeUtil; // 暂时叫 查询范围的类
	private ISiteviewApi m_api; // 初始化接口

	private DashboardPartDef dashboardpartdef;
	private String linkstr; // 下拉式 选 项
	private String type = "New"; // 标志位 1=添加 2=编辑 3=复制
	private ICollection allPlaceHolders;
	private TabFolder tabFolder;
	private TabbedPartDef tabbedpartdef;
	
	public MotionsPart(Shell parentShell, DashboardPartDef dashboarddef, ISiteviewApi m_api,
			DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil,
			IDefinition m_Def, String linkstr, String type) {
		super(parentShell);
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_api = m_api;
		this.dashboardpartdef = dashboarddef;
		this.m_Def = m_Def;
		this.type = type;
		this.linkstr = linkstr;
		general=new Generalmethods(getShell(),dashboardpartdef, m_api, m_Library, m_ScopeUtil, m_Def, linkstr);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 750);
		newShell.setLocation(300, 70);
		newShell.setText(type + "	选项卡式部分");
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
		
		txtName = new Text(container, SWT.BORDER);
		FormData fd_txtName = new FormData();
		fd_txtName.right = new FormAttachment(lblNewLabel, 406, SWT.RIGHT);
		fd_txtName.top = new FormAttachment(0, 28);
		fd_txtName.left = new FormAttachment(lblNewLabel, 6);
		txtName.setLayoutData(fd_txtName);
		
		txtAlias = new Text(container, SWT.BORDER);
		FormData fd_txtAlias = new FormData();
		fd_txtAlias.top = new FormAttachment(lbla, -3, SWT.TOP);
		fd_txtAlias.right = new FormAttachment(txtName, 0, SWT.RIGHT);
		fd_txtAlias.left = new FormAttachment(txtName, 0, SWT.LEFT);
		txtAlias.setLayoutData(fd_txtAlias);
		
		tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TabItem[] selected=tabFolder.getSelection();
				if(selected.length>0){
					
					if("属性".equals(selected[0].getText())){
						FillScope_1() ;
						cbOwnerdate(cbScope_1.getData(cbScope_1.getItem(cbScope_1
								.getSelectionIndex())), Generalmethods.isClient ? 0 : 2);
						int scope = (Integer) cbScope_1.getData(cbScope_1
								.getItem(cbScope_1.getSelectionIndex()));
						String scopeowner = (String) cbOwner_1.getData(cbOwner_1
								.getItem(cbOwner_1.getSelectionIndex()));
						FillList(scope, scopeowner);
					}
				}
			}
		});
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(txtAlias, 21);
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 604);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -50);
		tabFolder.setLayoutData(fd_tabFolder);
		
		general.tabFolder_1( tabFolder);
		general.tabFolder_2( tabFolder);
		
		TabItem tabItem = new TabItem(tabFolder, 0);
		tabItem.setText("\u5C5E\u6027");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tabItem.setControl(composite);
		
		Group group = new Group(composite, SWT.NONE);
		group.setText("\u4EEA\u8868\u677F\u90E8\u4EF6");
		group.setBounds(10, 10, 203, 447);
		
		Label lbls = new Label(group, SWT.NONE);
		lbls.setBounds(10, 23, 54, 17);
		lbls.setText("\u8303\u56F4(S):");
		
		Label lblo = new Label(group, SWT.NONE);
		lblo.setText("\u8D1F\u8D23\u4EBA(O):");
		lblo.setBounds(10, 49, 66, 17);
		
		cbScope_1 = new Combo(group, SWT.READ_ONLY);
		cbScope_1.setBounds(82, 20, 111, 20);
		cbScope_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cbOwnerdate(cbScope_1.getData(cbScope_1.getItem(cbScope_1
						.getSelectionIndex())), 2);
				int scope = (Integer) cbScope_1.getData(cbScope_1
						.getItem(cbScope_1.getSelectionIndex()));
				String scopeowner = (String) cbOwner_1.getData(cbOwner_1
						.getItem(cbOwner_1.getSelectionIndex()));
				FillList(scope, scopeowner);
			}
		});
		
		cbOwner_1= new Combo(group, SWT.READ_ONLY);
		cbOwner_1.setEnabled(false);
		cbOwner_1.setBounds(82, 46, 111, 20);
		
		Link link = new Link(group, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Dialog dbsd = new DaBoTypeSelecttDlg(getShell());
				dbsd.open();
			}
		});
		link.setBounds(10, 418, 111, 22);
		link.setText("<a>\u6D4F\u89C8\u90E8\u4EF6(A)...</a>");
		
		list_range = new List(group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		list_range.setBounds(10, 72, 183, 340);
		
		Button btnNewButton = new Button(composite, SWT.BORDER);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(list_range.getSelectionIndex()!=-1){
					boolean flag=true;
					for (int i = 0; i < list_addrange.getItemCount(); i++){
						  if(list_range.getItem(list_range.getSelectionIndex()).equals(list_addrange.getItem(i))){
							  flag=false;
						  }
				    }
					if(flag){
						if(tabbedpartdef!=null){
							PartRefDef item=(PartRefDef) list_range.getData(list_range.getItem(list_range.getSelectionIndex()));
							list_addrange.add(item.get_Alias());
							list_addrange.setData(item.get_Alias(),new PartHolder(item));
							tabbedpartdef.AddPartRefDef(item);
						}
					}
				}
			}
		});
		btnNewButton.setBounds(219, 133, 21, 28);
		btnNewButton.setText("");
		btnNewButton.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.Arrow_Right_Blue.png"),0x12,0x12));
		
		ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		toolBar.setBounds(253, 10, 105, 22);
		
		ToolItem tar_del = new ToolItem(toolBar, SWT.NONE);
		tar_del.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(list_addrange.getSelectionIndex()==-1)return;
				PartHolder holder = (PartHolder) list_addrange.getData(list_addrange.getItem(list_addrange.getSelectionIndex()));
		        PartRefDef partRefDef = tabbedpartdef.GetPartRefDef(holder.getM_strId());
		        tabbedpartdef.RemovePartRefDef(partRefDef);
		        list_addrange.remove(list_addrange.getSelectionIndex());
			}
		});
		tar_del.setText("\u5220\u9664");
		
		ToolItem tar_up = new ToolItem(toolBar, SWT.NONE);
		tar_up.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				
				if(list_addrange.getSelectionIndex()==-1)return;
				int selectindex=list_addrange.getSelectionIndex();
				if(selectindex-1<0)return ;
				PartHolder listselect=(PartHolder) list_addrange.getData(list_addrange.getItem(selectindex));
				PartHolder list=(PartHolder) list_addrange.getData(list_addrange.getItem(selectindex-1));
				list_addrange.setItem(selectindex, list.getM_strName());
				list_addrange.setItem(selectindex-1, listselect.getM_strName());
				list_addrange.setSelection(selectindex-1);
				
			}
		});
		tar_up.setText("\u4E0A\u79FB");
		
		ToolItem tar_down = new ToolItem(toolBar, SWT.NONE);
		tar_down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(list_addrange.getSelectionIndex()==-1)return;
				int selectindex=list_addrange.getSelectionIndex();
				if(selectindex+1>list_addrange.getItemCount()-1)return ;
				PartHolder listselect=(PartHolder) list_addrange.getData(list_addrange.getItem(selectindex));
				PartHolder list=(PartHolder) list_addrange.getData(list_addrange.getItem(selectindex+1));
				list_addrange.setItem(selectindex, list.getM_strName());
				list_addrange.setItem(selectindex+1, listselect.getM_strName());
				list_addrange.setSelection(selectindex+1);
			}
		});
		tar_down.setText("\u4E0B\u79FB");
		
		list_addrange = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		list_addrange.setBounds(246, 38, 340, 419);
		load();
		datemove();
		return container;
	}
	
	/**
	 * 第一次加载数据
	 */
	public void load(){
		FillScope_1();
		allPlaceHolders = m_Library.GetAllPlaceHolders(
				DashboardPartDef.get_ClassName(), "(Base)");
		
		cbOwnerdate(cbScope_1.getData(cbScope_1.getItem(cbScope_1
				.getSelectionIndex())), 2);
		int scope = (Integer) cbScope_1.getData(cbScope_1
				.getItem(cbScope_1.getSelectionIndex()));
		String scopeowner = (String) cbOwner_1.getData(cbOwner_1
				.getItem(cbOwner_1.getSelectionIndex()));
		FillList(scope, scopeowner);
		tabbedpartdef=(TabbedPartDef) dashboardpartdef;
		if(dashboardpartdef == null || "New".equals(type)){
			return;
		}
		
		txtName.setText(dashboardpartdef.get_Name());
		txtAlias.setText(dashboardpartdef.get_Alias());
		
		if ("Edit".equals(type)) {
			txtName.setText(dashboardpartdef.get_Name());
			txtAlias.setText(dashboardpartdef.get_Alias());
		} else {
			txtName.setText(dashboardpartdef.get_Name() + "  的副本");
			txtAlias.setText(dashboardpartdef.get_Alias() + "  的副本");
		}

		IEnumerator it = tabbedpartdef.get_PartRefDefs().GetEnumerator();
		
		ArrayList<PartRefDef> lis=new ArrayList<PartRefDef>();
		
		while (it.MoveNext()) {
			PartRefDef item=(PartRefDef) it.get_Current();
			item.set_EditMode(true);
			if(IsValidPartRefDef(item)){
				if(item.get_Name().equals(item.get_Alias())){
					DashboardPartDef definition = (DashboardPartDef) m_api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(Scope.User, m_api.get_SystemFunctions().get_CurrentLoginId(), DashboardPartDef.get_ClassName(), item.get_Id(), true));
					if(definition!=null){
						item.set_Alias(definition.get_Alias());
					}
				}
				item.set_Alias((StringUtils.IsEmpty(item.get_Alias())) ? item.get_Name() : item.get_Alias());
				list_addrange.add(item.get_Alias());
				list_addrange.setData(item.get_Alias(),new PartHolder(item));
			}else{
				lis.add(item);
			}
		}
		
		for(PartRefDef partr:lis){
			tabbedpartdef.RemovePartRefDef(partr);
		}
	}
	
	
	 private boolean IsValidPartRefDef(PartRefDef def)
     {
         DashboardPartDef definition = (DashboardPartDef) m_api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(Scope.User, m_api.get_SystemFunctions().get_CurrentLoginId(), DashboardPartDef.get_ClassName(), def.get_Id(), true)) ;
         if ((definition == null))
         {
             definition = (DashboardPartDef) m_Library.GetDefinition(DefRequest.ById(def.get_Scope(), def.get_ScopeOwner(), def.get_LinkedTo(), DashboardPartDef.get_ClassName(), def.get_Id()));
         }
         return (definition != null);
     }
	
	  /**
     *  数据拖动事件
     */
    
    public void datemove(){
    	DragSource   ds=new   DragSource(list_range,DND.DROP_MOVE);
		ds.setTransfer(new   Transfer[]   {TextTransfer.getInstance()});
		ds.addDragListener(new   DragSourceAdapter(){
			
			public   void   dragSetData(DragSourceEvent   event){ 
				event.data=list_range.getItem(list_range.getSelectionIndex()); 
				} 
		});
		
		DropTarget   dr=new   DropTarget(list_addrange,DND.DROP_MOVE);
		dr.setTransfer(new   Transfer[]   {TextTransfer.getInstance()}); 

		dr.addDropListener(new   DropTargetAdapter(){
			
			public void drop(DropTargetEvent   event){ 
			
				if(list_range.getSelectionIndex()!=-1){
					
					boolean flag=true;
					for (int i = 0; i < list_addrange.getItemCount(); i++){
						  if(list_range.getItem(list_range.getSelectionIndex()).equals(list_addrange.getItem(i))){
							  flag=false;
						  }
				    }
					if(flag){
						if(tabbedpartdef!=null){
							PartRefDef item=(PartRefDef) list_range.getData(list_range.getItem(list_range.getSelectionIndex()));
							list_addrange.add(item.get_Alias());
							list_addrange.setData(item.get_Alias(),new PartHolder(item));
							tabbedpartdef.AddPartRefDef(item);
						}
							
							
					}
					
				}
				
			} 
		});
    }
   
	
	/**
	 * 给布局中 的 范围 填 充数据
	 */
	public void FillScope_1() {
		cbScope_1.removeAll();
		int scope = (Integer) general.getCbScope().getData(general.getCbScope().getItem(general.getCbScope()
				.getSelectionIndex()));
		ICollection supportedScopes = m_ScopeUtil.GetSupportedScopes(
				SecurityRight.View, scope);
		if (supportedScopes.get_Count() > 0) {
			IEnumerator it = supportedScopes.GetEnumerator();
			while (it.MoveNext()) {
				Integer sc = (Integer) it.get_Current();
				String item = m_ScopeUtil.ScopeToString(sc);
				cbScope_1.setData(item, sc);
				cbScope_1.add(item);
			}
			cbScope_1.setText(general.getCbScope().getText());
			if (cbScope_1.getItemCount() > 1) {
				cbScope_1.setEnabled(true);
			} else {
				cbScope_1.setEnabled(false);
			}
		}
		cbOwnerdate(cbScope_1.getData(cbScope_1.getItem(cbScope_1
				.getSelectionIndex())), 2);
	}
	/**
	 * 负责人 加载数据
	 */
	private void cbOwnerdate(Object object, int type) {
		if (type == 0) {
			cbOwner_1.removeAll();
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, true);
			if (list.get_Count() > 0) {
				for (int i = 0; i < list.get_Count(); i++) {
					String own = (String) list.get_Item(i);
					String item = m_ScopeUtil.ScopeOwnerToString(ob, own);
					cbOwner_1.add(item);
					cbOwner_1.setData(item, own);
				}
				cbOwner_1.select(0);
			}
			if (cbOwner_1.getItemCount() > 1) {
				cbOwner_1.setEnabled(true);
			}
		}else if (type == 2) {
			cbOwner_1.removeAll();
			int ob = (Integer) object;
			IList list = m_ScopeUtil.GetScopeOwners(ob, false);
			if (list.get_Count() > 0) {
				for (int i = 0; i < list.get_Count(); i++) {
					String own = (String) list.get_Item(i);
					String item = m_ScopeUtil.ScopeOwnerToString(ob, own);
					cbOwner_1.add(item);
					cbOwner_1.setData(item, own);
				}
				cbOwner_1.select(0);
			}
			if (cbOwner_1.getItemCount() > 1) {
				cbOwner_1.setEnabled(true);
			}
		}

	}
	/**
	 * 填充布局 中的 LIST数据
	 */
	public void FillList(int scope, String ScopeOwner) {
		if (allPlaceHolders == null)
			return;
		list_range.removeAll();
		IEnumerator Placs = allPlaceHolders.GetEnumerator();
		PartRefDef item;
		DashboardPartDef def = null;
		while (Placs.MoveNext()) {
			PlaceHolder holder = (PlaceHolder) Placs.get_Current();
			item = (PartRefDef) DashboardPartDef
					.DeserializeCreateNewForEditing(XmlDashboardPartCategory
							.ToString(DashboardPartCategory.PartRef));
			def = (DashboardPartDef) m_Library.GetDefinition(DefRequest.ById(
					holder.get_Scope(), holder.get_ScopeOwner(),
					holder.get_LinkedTo(), DashboardPartDef.get_ClassName(),
					holder.get_Id()));
			if (((def != null) && (holder.get_Scope() == scope))
					&& holder.get_ScopeOwner().toUpperCase()
							.equals(ScopeOwner.toUpperCase())) {
				item.CopyContentsForEdit(def);
				item.set_CategoryAsString("PartRef");
				item.set_PartCategory(XmlDashboardPartCategory.ToCategory(def.get_CategoryAsString()));
				item.set_ShowPartType(false);
				list_range.add(holder.get_Alias());
				list_range.setData(holder.get_Alias(), item);
			}
		}
	}

	
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	private void createLookPage() {
		ImageChooseDlg imageC = new ImageChooseDlg(this.getShell());
		imageC.open();
	}
	
	
	@Override
	protected void okPressed() {
		if(general.savavalidation(txtName, txtAlias, type)){
			
			TabbedPartDef tablledpartdef=(TabbedPartDef) dashboardpartdef;
			
			for (int i = 0; i < list_addrange.getItemCount(); i++){
				
			   PartHolder holder = (PartHolder) list_addrange.getData(list_addrange.getItem(i));
	           PartRefDef partRefDef = tablledpartdef.GetPartRefDef(holder.getM_strId());
	           tablledpartdef.RemovePartRefDef(partRefDef);
	           tablledpartdef.AddPartRefDef(partRefDef);
	        }
			
//			if ("New".equals(type)) {
//				m_Library.UpdateDefinition(dashboardpartdef, true);
//			} else if ("Edit".equals(type)) {
//				m_Library.UpdateDefinition(dashboardpartdef, false);
//			} else if("Copy".equals(type)){
//				dashboardpartdef.set_Id(m_Def.get_Id());
//				m_Library.UpdateDefinition(dashboardpartdef, true);
//			}
			super.okPressed();
		}
	}
}


class PartHolder{
	
	private String m_strId;
    private String m_strName;

    public PartHolder(PartRefDef def)
    {
        this.m_strId = def.get_Id();
        this.m_strName = def.get_Alias();
    }

	public String getM_strId() {
		return m_strId;
	}

	public void setM_strId(String m_strId) {
		this.m_strId = m_strId;
	}

	public String getM_strName() {
		return m_strName;
	}

	public void setM_strName(String m_strName) {
		this.m_strName = m_strName;
	}
	
    
}


