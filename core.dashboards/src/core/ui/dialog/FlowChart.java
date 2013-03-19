package core.ui.dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Collections.IList;
import Siteview.ActionDef;
import Siteview.AutoTaskDef;
import Siteview.CloneBusObActionDef;
import Siteview.CreateBusObActionDef;
import Siteview.CreateBusObFromCSVActionDef;
import Siteview.CreateChildBusObActionDef;
import Siteview.DedupeBusObActionDef;
import Siteview.DefRequest;
import Siteview.DeleteBusObActionDef;
import Siteview.FindBusObActionDef;
import Siteview.IDefinition;
import Siteview.LinkBusObActionDef;
import Siteview.LinkChildBusObActionDef;
import Siteview.ParseContentActionDef;
import Siteview.PlaceHolder;
import Siteview.RunForChildrenBusObsActionDef;
import Siteview.StringUtils;
import Siteview.UpdateBusObActionDef;
import Siteview.UpdatePrimaryCurrencyValuesActionDef;
import Siteview.Api.ActionElementDef;
import Siteview.Api.AutoTaskContext;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.BusinessProcessDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ElementDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.PerspectiveDef;
import Siteview.Api.ReferenceDef;
import Siteview.Api.RelationshipDef;
import Siteview.Api.TriggerDef;
import Siteview.Api.TriggerElementDef;
import Siteview.Windows.Forms.ConnectionBroker;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.ElementItems;
import Siteview.Xml.Scope;
import Siteview.Xml.XmlSecurityRight;

public class FlowChart {

	private static String m_strm_strBusinessProcessName="BusinessProcessDef";  // C#查询数据时的固定参数
	private ScopeUtil m_ScopeUtil;     // 暂时叫  查询范围的类
	private static String m_moduleSecurityName="Siteview.Security.AutoTasks";  // C#查询数据时的固定参数
	private PerspectiveDef perspectiveDef;  // 不懂
	private ISiteviewApi m_api;  //初始化接口
	private DefinitionLibrary m_Library ;
	private IDefinition m_Def;
	private IList m_lstSupportedScopesWithOwners;
	private java.util.List<PlaceHolder> buspdef=new java.util.ArrayList<PlaceHolder>();
	
	private StringBuffer strcontext=new StringBuffer(""); 
	
	public FlowChart(){
		this.m_api=ConnectionBroker.get_SiteviewApi();
		this.m_Library = ConnectionBroker.get_SiteviewApi().get_LiveDefinitionLibrary();
		this.m_ScopeUtil = new ScopeUtil(m_api, m_Library, m_strm_strBusinessProcessName, m_moduleSecurityName);
		this.m_ScopeUtil.set_CheckRights(true);
		this.m_lstSupportedScopesWithOwners = this.m_ScopeUtil.GetSupportedScopesWithOwners(XmlSecurityRight.ToRight("VAED"), true);
	
		load();
	}
	
	
	public void load(){
		DrawHead();
		QueryBusinessObjectDef();
		DrawOver();
	}
	
	
	/**
	 * 查出所有的  业务对象
	 */
	public void QueryBusinessObjectDef(){
		ICollection busLinks = m_Library.GetPlaceHolderList(DefRequest.ForList(BusinessObjectDef.get_ClassName()));
		if(busLinks==null)return;
		IEnumerator it = busLinks.GetEnumerator();
		boolean flag=true;
		while(it.MoveNext()){
			PlaceHolder ph = (PlaceHolder)it.get_Current();
			
			// flag="变更".equals(ph.get_Alias())||"问题".equals(ph.get_Alias())||"发布".equals(ph.get_Alias())||"库存".equals(ph.get_Alias())||"知识库".equals(ph.get_Alias())||"服务".equals(ph.get_Alias())||"员工".equals(ph.get_Alias());
		
//			flag="服务".equals(ph.get_Alias());
//			flag="事件".equals(ph.get_Alias())||"问题".equals(ph.get_Alias())||"服务".equals(ph.get_Alias());
//			flag="问题".equals(ph.get_Alias());
//			flag="事件".equals(ph.get_Alias());
//			flag="变更".equals(ph.get_Alias())||"发布".equals(ph.get_Alias());
//			flag="变更".equals(ph.get_Alias());
//			flag="发布".equals(ph.get_Alias());
//			flag=ph.get_Alias().indexOf("服务")!=-1;
//			flag="打印机配置".equals(ph.get_Alias())||"所有配置项".equals(ph.get_Alias())||"计算机配置".equals(ph.get_Alias())||"网络设备配置".equals(ph.get_Alias());
//			flag="知识库".equals(ph.get_Alias());
			
//			flag="事件".equals(ph.get_Alias())||"问题".equals(ph.get_Alias());
//			flag="变更".equals(ph.get_Alias())||"事件".equals(ph.get_Alias())||"问题".equals(ph.get_Alias())||ph.get_Alias().indexOf("知识")!=-1;
//			flag=ph.get_Alias().indexOf("库存")!=-1;
			
//			flag="计算机库存".equals(ph.get_Alias());
			
			
			System.out.println( ph.get_Alias() +"      "+ph.get_Name());
			if (flag){
		//		 System.out.println(ph.get_Alias());
			//	System.out.println("业务对象     "+ph.get_Alias()+"       " + ph.get_Id());
				Drawnode(482, 84,DrawWidth(ph.get_Alias(),30), 30, ph.get_Id(), ph.get_Alias(), "rectangle", "#FFCC00", "#000000");
			}
			//if(count>0)break;
		}
		
		IEnumerator it1 = busLinks.GetEnumerator();
		while(it1.MoveNext()){
			PlaceHolder ph = (PlaceHolder)it1.get_Current();
	//		flag="服务".equals(ph.get_Alias());
//			flag="事件".equals(ph.get_Alias());

//			flag="事件".equals(ph.get_Alias())||"问题".equals(ph.get_Alias())||"服务".equals(ph.get_Alias());
//			flag="问题".equals(ph.get_Alias());
//			flag="变更".equals(ph.get_Alias())||"发布".equals(ph.get_Alias());
			// flag="变更".equals(ph.get_Alias())||"问题".equals(ph.get_Alias())||"发布".equals(ph.get_Alias())||"库存".equals(ph.get_Alias())||"知识库".equals(ph.get_Alias())||"服务".equals(ph.get_Alias())||"员工".equals(ph.get_Alias());
//			flag="变更".equals(ph.get_Alias());
//			flag="发布".equals(ph.get_Alias());
//			flag=ph.get_Alias().indexOf("资产")!=-1;
//			flag="打印机配置".equals(ph.get_Alias())||"所有配置项".equals(ph.get_Alias())||"计算机配置".equals(ph.get_Alias())||"网络设备配置".equals(ph.get_Alias());
			
//			flag="知识库".equals(ph.get_Alias());
//			flag=ph.get_Alias().indexOf("服务")!=-1;
	//		flag="事件".equals(ph.get_Alias())||"问题".equals(ph.get_Alias());
//			flag="变更".equals(ph.get_Alias())||"事件".equals(ph.get_Alias())||"问题".equals(ph.get_Alias())||ph.get_Alias().indexOf("知识")!=-1;
//			flag="计算机库存".equals(ph.get_Alias());
			
			
			if (flag){
			//	System.out.println("业务对象     "+ph.get_Alias()+"       " + ph.get_Id());
				QueryBusinessProcessDef(ph.get_Id(),ph.get_Name());
			}
			//if(count>0)break;
		}
	}
	
	
	
	
	
	
	/**
	 * 根据一个业务对象 查出 所以有业务规则
	 */
	public void QueryBusinessProcessDef(String id,String bus_name){
		
		IEnumerator it =m_lstSupportedScopesWithOwners.GetEnumerator();
		while(it.MoveNext()){
			ScopeUtil.ScopeAndOwners owners = (ScopeUtil.ScopeAndOwners)it.get_Current();
			if (owners.get_Owners().get_Count()>0 && owners.get_Scope() != Scope.Global)
			{
				for(int i = 0; i < owners.get_Owners().get_Count(); i++){
					String str = (String)owners.get_Owners().get_Item(i);
					OrganizeListerItemsByCategory(buspdef,owners.get_Scope(),str,bus_name,false);
				}
			}else{
				OrganizeListerItemsByCategory(buspdef,owners.get_Scope(),"",bus_name,false);
			}
			
		}
		
		for(PlaceHolder ph:buspdef){
			//根据每个业务对象查出一个 业务规则  
			IDefinition defForEditing = m_Library.GetDefForEditing(DefRequest.ByHolder(ph));
			BusinessProcessDef busProcDef = (BusinessProcessDef) defForEditing;
			
			//通过一个业务规则 查出 触发器中心  和 快速操作 中心
			TriggerElementDef newDefForEditing = null;
            ActionElementDef defElementNext = null;
			if (busProcDef.get_ElementDef()==null){
				newDefForEditing = (TriggerElementDef) m_api.get_LiveDefinitionLibrary().GetNewDefForEditing(DefRequest.ByCategory(ElementDef.get_ClassName(),TriggerElementDef.get_SElementItem()));
				newDefForEditing.set_Name(ElementItems.ConvertConstantToDisplayText(TriggerElementDef.get_SElementItem()));
				busProcDef.set_ElementDef(newDefForEditing);
				defElementNext = (ActionElementDef) m_api.get_LiveDefinitionLibrary().GetNewDefForEditing(DefRequest.ByCategory(ElementDef.get_ClassName(),ActionElementDef.get_SElementItem()));
				defElementNext.set_Name(ElementItems.ConvertConstantToDisplayText(ActionElementDef.get_SElementItem()));
				newDefForEditing.AddPrimayElementDef(0,defElementNext);
			}else{
				if (busProcDef.get_ElementDef() instanceof TriggerElementDef){
					newDefForEditing = (TriggerElementDef) busProcDef.get_ElementDef();
					if (newDefForEditing.GetPrimaryNextElementDef(0) == null){
						defElementNext = (ActionElementDef) m_api.get_LiveDefinitionLibrary().GetNewDefForEditing(DefRequest.ByCategory(ElementDef.get_ClassName(),ActionElementDef.get_SElementItem()));
						defElementNext.set_Name(ElementItems.ConvertConstantToDisplayText(ActionElementDef.get_SElementItem()));
						newDefForEditing.AddPrimayElementDef(0,defElementNext);
					}else{
						defElementNext = (ActionElementDef) newDefForEditing.GetPrimaryNextElementDef(0);
					}
				}
			}
			
			//一个业务规则的所有触发中心
			if(newDefForEditing != null){
				ICollection triggers = newDefForEditing.get_TriggerReferenceDefs();
				IEnumerator ittriger = triggers.GetEnumerator();
				while(ittriger.MoveNext()){
					ReferenceDef defRef = (ReferenceDef)ittriger.get_Current();
					IDefinition def = m_api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(defRef.get_DefScope(), defRef.get_DefOwner(), defRef.get_DefLinkedTo(), defRef.get_DefClassName(), defRef.get_DefId()));
					if (def!=null){
						TriggerDef triggerDef = (TriggerDef)def;
						if (triggerDef.get_EventDef() != null){
							
					//		System.out.println("触发中心    "+id+"       "+ triggerDef.get_Id());
							
							Drawnode(482, 84, DrawWidth(triggerDef.get_Alias(),16), 30, triggerDef.get_Id(), triggerDef.get_Alias(), "parallelogram", "#00FFFF", "#000000");
							Drawedge(id,triggerDef.get_Id(),"","#000000","standard");
						
							//一个业务规则的所有快速操作中心
							if (defElementNext != null){
								ICollection actions = defElementNext.get_ActionReferenceDefs();
								IEnumerator itaction = actions.GetEnumerator();
								while(itaction.MoveNext()){
									ReferenceDef td = (ReferenceDef)itaction.get_Current();
									AutoTaskDef aiddef = (AutoTaskDef) m_api.get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(td.get_DefScope(), td.get_DefOwner(), td.get_DefLinkedTo(), td.get_DefClassName(), td.get_DefId()));
					//				System.out.println("快速操作中心   "+ triggerDef.get_Id()+"       "+ td.get_Id());
									if(aiddef==null)continue;
									Drawnode(482, 84, DrawWidth(aiddef.get_Alias(),12), 30, td.get_Id(), aiddef.get_Alias(), "ellipse", "#FF6600", "#000000");
									Drawedge( triggerDef.get_Id(),td.get_Id(),ph.get_Alias(),"#000000","standard");
									
									QueryAutoTaskDef(td.get_Id(),aiddef);
								}
							}
						}
					}	
				}
			}
			
			//break;
			
		}
	}

	/**
	 * 根据一个快速操作 查出所有的子操作
	 */
	
	public void QueryAutoTaskDef(String id,AutoTaskDef taskdef){
		
		
	
		IList zyact=taskdef.get_ActionDefinitionObjects();
		if(zyact.get_Count()>0){
			for(int i=0;i<zyact.get_Count();i++){
				ActionDef ac =(ActionDef) zyact.get_Item(i);
				FXAutoTaskDef(id,taskdef,ac);
			}
		}
		
		IList cyact=taskdef.get_PreActionDefinitionObjects();
		if(cyact.get_Count()>0){
			for(int i=0;i<cyact.get_Count();i++){
				ActionDef ac =(ActionDef) cyact.get_Item(i);
				FXAutoTaskDef(id,taskdef,ac);
			}
		}
		IList zjact=taskdef.get_PostActionDefinitionObjects();
		if(zjact.get_Count()>0){
			for(int i=0;i<zjact.get_Count();i++){
				ActionDef ac =(ActionDef) zjact.get_Item(i);
				FXAutoTaskDef(id,taskdef,ac);
			}
		}
	}
	
	
	/**
	 * 分析每个子操用进行分析
	 */
	public void FXAutoTaskDef(String id,AutoTaskDef taskdef,ActionDef def){
		
		String Id="";
		String name="";
		
		String source=id;
		String target="";
		String edgename="";
		BusinessObjectDef bus=null;
		
		boolean flag=true;
		
		//克隆记录
		if(def instanceof CloneBusObActionDef){
			
			CloneBusObActionDef clonebusdef=(CloneBusObActionDef) def;
			
			target=FXAutoTaskDef(taskdef,clonebusdef.get_BusinessObjectToClone());
			Id=clonebusdef.get_Id();
			name=clonebusdef.get_Alias();
		}
		
		
		//重新计算基本币值
		if(def instanceof UpdatePrimaryCurrencyValuesActionDef){
			UpdatePrimaryCurrencyValuesActionDef updateprimarydef=(UpdatePrimaryCurrencyValuesActionDef) def;
			target=FXAutoTaskDef(taskdef,updateprimarydef.get_BusinessObjectName());
			Id=updateprimarydef.get_Id();
			name=updateprimarydef.get_Alias();
		}
		
		//删除记录
		if(def instanceof DeleteBusObActionDef){
			DeleteBusObActionDef deletebusdef=(DeleteBusObActionDef) def;
			String busname="";
			if(deletebusdef.get_IsDeleteConfirm()){
				busname=deletebusdef.BusinessObjectName;
			}else{
				busname=deletebusdef.get_ParentBusinessObjectName();
			}
			target=FXAutoTaskDef(taskdef,busname);
			Id=deletebusdef.get_Id();
			name=deletebusdef.get_Alias();
		}
		
		//创建记录
		if(def instanceof CreateBusObActionDef){
			
			CreateBusObActionDef createBusDef=(CreateBusObActionDef) def;
			bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ByName(BusinessObjectDef.get_ClassName(), createBusDef.get_BusinessObjectName()));
			if(bus!=null){
				target=bus.get_Id();
			}
			Id=createBusDef.get_Id();
			name=createBusDef.get_Alias();
		}
		
		//创建子记录
		if(def instanceof CreateChildBusObActionDef){
			
			//链接记录
			if(def instanceof LinkBusObActionDef){
				LinkBusObActionDef linkbusdef=(LinkBusObActionDef) def;
				target=FXAutoTaskDef(taskdef,linkbusdef.get_BusinessObjectName());
				Id=linkbusdef.get_Id();
				name=linkbusdef.get_Alias();
				
			//搜索并链接记录
			}else if(def instanceof LinkChildBusObActionDef){
				LinkChildBusObActionDef linkchildbusdef=(LinkChildBusObActionDef) def;
				//System.out.println(linkchildbusdef.get_Alias());
				
				AutoTaskContext autocontext=new AutoTaskContext(m_api,m_Library,taskdef,m_Library.GetBusinessObjectDef(taskdef.get_LinkedTo()));
				BusinessObjectDef businessObjectDef = autocontext.get_DefinitionLibrary().GetBusinessObjectDef(linkchildbusdef.get_ParentBusinessObjectName());
				
				 ICollection relationshipDefs = businessObjectDef.get_RelationshipDefs();
                 if (relationshipDefs != null)
                 {
                	 
                	 RelationshipDef relationship =null;
                	 if(linkchildbusdef!=null){
                		 relationship = autocontext.get_DefinitionLibrary().GetBusinessObjectDef(businessObjectDef.get_Name()).GetRelationship(linkchildbusdef.get_RelationshipName());
                		 if(relationship!=null){
                			 target= relationship.get_TargetId();
                		 }
                	 }
                	

                 }
//				target=FXAutoTaskDef(taskdef,linkchildbusdef.get_BusinessObjectName());
				Id=linkchildbusdef.get_Id();
				name=linkchildbusdef.get_Alias();
				
			//创建子记录
			}else{
				CreateChildBusObActionDef createchildbusdef=(CreateChildBusObActionDef) def;
				target=FXAutoTaskDef(taskdef,createchildbusdef.get_BusinessObjectName());
				Id=createchildbusdef.get_Id();
				name=createchildbusdef.get_Alias();
			}
		}
		
		
		//更新记录
		if(def instanceof UpdateBusObActionDef){
			UpdateBusObActionDef updatebusdef=(UpdateBusObActionDef) def;
			target=FXAutoTaskDef(taskdef,updatebusdef.get_BusinessObjectName());
			Id=updatebusdef.get_Id();
			name=updatebusdef.get_Alias();
		}
		
		//删除重复记录
		if(def instanceof DedupeBusObActionDef){
			DedupeBusObActionDef dedupebudef=(DedupeBusObActionDef) def;
			bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ByName(BusinessObjectDef.get_ClassName(), dedupebudef.get_BusinessObjectName()));
			if(bus!=null){
				target=bus.get_Id();
			}
			Id=dedupebudef.get_Id();
			name=dedupebudef.get_Alias();
		}
		
		//从 CSV 导入业务对象
		if(def instanceof CreateBusObFromCSVActionDef){
			CreateBusObFromCSVActionDef createCSVBusDef=(CreateBusObFromCSVActionDef) def;
			bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ByName(BusinessObjectDef.get_ClassName(), createCSVBusDef.get_BusinessObjectName()));
			
			if(bus!=null){
				target=bus.get_Id();
			}
			Id=createCSVBusDef.get_Id();
			name=createCSVBusDef.get_Alias();
		}
		//解析字段内容
		if(def instanceof ParseContentActionDef){
			
			ParseContentActionDef parsecontentdef=(ParseContentActionDef) def;
			bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ByName(BusinessObjectDef.get_ClassName(), parsecontentdef.get_BusinessObjectName()));
			if(bus!=null){
				target=bus.get_Id();
			}
			Id=parsecontentdef.get_Id();
			name=parsecontentdef.get_Alias();
		}
		
	
		
		
		//为子记录执行动作
		if(def instanceof RunForChildrenBusObsActionDef){
			RunForChildrenBusObsActionDef runforchilddef=(RunForChildrenBusObsActionDef) def;
			bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ByName(BusinessObjectDef.get_ClassName(), runforchilddef.get_ChildBusinessObjectName()));
			if(bus!=null){
				target=bus.get_Id();
			}
			Id=runforchilddef.get_Id();
			name=runforchilddef.get_Alias();
			
			IList list=runforchilddef.get_ActionDefinitionObjects();
			if(list.get_Count()>0){
				Drawnode(482, 84, DrawWidth(name,18), 30,Id, name, "octagon", "#CC99FF", "#000000");
				Drawedge( source,Id,edgename,"#000000","standard");
				Drawedge( Id,target,edgename,"#000000","standard");
			}
			IEnumerator it = list.GetEnumerator();
			while(it.MoveNext()){
				ActionDef adef=(ActionDef) it.get_Current();
				System.out.println(adef.get_Alias());
				FXAutoTaskDef(runforchilddef.get_Id(),taskdef,adef);
			}
			flag=false;
		}	
		//查找记录
		if(def instanceof FindBusObActionDef){
			FindBusObActionDef findbusdef=(FindBusObActionDef) def;
			bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ByName(BusinessObjectDef.get_ClassName(),findbusdef.get_SiteviewQuery().get_BusinessObjectName()));
			if(bus!=null){
				target=bus.get_Id();
			}
			Id=findbusdef.get_Id();
//			findbusdef.set_SiteviewQuery(value)
			name=findbusdef.get_Alias();
		}

		if(bus==null&&StringUtils.IsEmpty(target)){
			bus=(BusinessObjectDef) m_Library.GetDefinition(DefRequest.ByName(BusinessObjectDef.get_ClassName(), taskdef.get_LinkedTo()));
			target=bus.get_Id();
			Id=def.get_Id();
			name=def.get_Alias();
		}
		
		
		if(!StringUtils.IsEmpty(Id)&&flag){
			Drawnode(482, 84, DrawWidth(name,18), 30,Id, name, "octagon", "#CC99FF", "#000000");
			Drawedge( source,Id,edgename,"#000000","standard");
			Drawedge( Id,target,edgename,"#000000","standard");
		}
		
	}
	
		

		
	
	/**
	 * 分析子操用的选对的业务对象
	 */
	
	public String FXAutoTaskDef(AutoTaskDef taskdef,String businessobjectname){
		AutoTaskContext context=new AutoTaskContext(m_api,m_Library,taskdef,m_Library.GetBusinessObjectDef(taskdef.get_LinkedTo()));
		BusinessObjectDef businessObjectDef = context.get_DefinitionLibrary().GetBusinessObjectDef(businessobjectname);
		if(businessObjectDef==null)return"";
		return businessObjectDef.get_Id();
	}
	
	
	/**
	 * 根据一个业务对象 查出 所以有业务规则 2
	 * @param s Scope 
	 * @param Owner 
	 * @param linkTo 关联的业务对象的名字
	 * @param bFilter 是否过滤
	 */
	private void OrganizeListerItemsByCategory(java.util.List<PlaceHolder> listItems,int s,String Owner, String linkTo,boolean bFilter){
		String strPerspective = (this.perspectiveDef != null) ? this.perspectiveDef.get_Name() : "(Base)";
		listItems.clear();
		ArrayList obs=(ArrayList) m_Library.GetPlaceHolderList(DefRequest.ForList(s,Owner,linkTo,m_strm_strBusinessProcessName,strPerspective));
		
		if (obs!=null){
			for(int i = 0; i < obs.get_Count();i ++){
				PlaceHolder holder = (PlaceHolder)obs.get_Item(i);
				listItems.add(holder);
			}
		}
	}
	
	
	
	
	/**
	 * 判断 生成 字符长度大小 
	 *   
	 */
	public int DrawWidth(String name,int number){
		int width=name.length()*number;
		if(name.charAt(0)>0&&name.charAt(0)<127){
			width=name.length()*number/2;
		}
		return width;
	}
	
	/**
	 * 生成头部
	 */
	public void DrawHead(){
		strcontext.append("Creator	\"yFiles\" ");
		strcontext.append("\nVersion	\"2.8\" ");
		strcontext.append("\ngraph ");
		strcontext.append("\n[\n\thierarchic	1");
		strcontext.append("\n\tlabel	\"\"");
		strcontext.append("\n\tdirected	1 \n");
	}
	
	
	/**
	 * 生成结点
	 */
	public void Drawnode(int x,int y,int w,int h,String id,String name,String type,String color,String outline ){
		strcontext.append("\t node\n \t[\n");
		strcontext.append("\t\t id \""+id+"\"\n");
		strcontext.append("\t\t label \""+name+"\"\n");
		strcontext.append("\t\t graphics\n \t\t[ \n");
		strcontext.append("\t\t\t x "+x+"\n");
		strcontext.append("\t\t\t y "+y+"\n");
		strcontext.append("\t\t\t w "+w+"\n");
		strcontext.append("\t\t\t h "+h+"\n");
		strcontext.append("\t\t\t type \""+type+"\"\n");
		strcontext.append("\t\t\t fill \""+color+"\"\n");
		strcontext.append("\t\t\t outline \""+outline+"\"\n");
		strcontext.append("\t\t ] \n");
		strcontext.append("\t\t LabelGraphics\n \t\t [ \n");
		strcontext.append("\t\t\t text \""+name+"\"\n");
		strcontext.append("\t\t\t fontSize 12\n");
		strcontext.append("\t\t\t fontName \"Dialog\"\n");
		strcontext.append("\t\t\t anchor \"c\"\n");
		strcontext.append("\t\t ] \n");
		strcontext.append("\t ] \n");
	}
	
	
	
	
	
	/**
	 *  生成 边
	 */
	public void Drawedge(String source,String target,String name,String color,String targetArrow){
	//	name="";
		strcontext.append("\t edge\n \t[\n");
		strcontext.append("\t\t source \""+source+"\"\n");
		strcontext.append("\t\t target \""+target+"\"\n");
		strcontext.append("\t\t label \""+name+"\"\n");
		strcontext.append("\t\t graphics\n \t\t[ \n");
		strcontext.append("\t\t\t fill \""+color+"\"\n");
		strcontext.append("\t\t\t targetArrow \""+targetArrow+"\"\n");
		strcontext.append("\t\t ] \n");
		strcontext.append("\t\t LabelGraphics\n \t\t [ \n");
		strcontext.append("\t\t\t text \""+name+"\"\n");
		strcontext.append("\t\t\t fontSize 12\n");
		strcontext.append("\t\t\t fontName \"Dialog\"\n");
		strcontext.append("\t\t\t model \"six_pos\"\n");
		strcontext.append("\t\t\t position \"tail\"\n");
		strcontext.append("\t\t ] \n");
		strcontext.append("\t ] \n");
		
	}
	
	/**
	 * 生成结束
	 * @throws Exception 
	 */
	public void DrawOver() {
		strcontext.append("] \n");
	//	System.out.println();
		//System.out.println(strcontext.toString());
		
		File f= new File("C:\\Documents and Settings\\Administrator\\桌面\\test.gml") ;	
		OutputStream out = null ;	
		try {
			out = new FileOutputStream(f)  ;
			byte b[] = strcontext.toString().getBytes() ;		
			for(int i=0;i<b.length;i++){		
				out.write(b[i]) ;	
			}
			out.close() ;	
		} catch (Exception e) {
			e.printStackTrace();
		}	
			
	}

	
	
	public static void main(String[] agra){
		
		System.out.println(String.format("Unable to delete {0}: {1}", "Incident","对于Business Process Engine通知事件不能被发送.保存操作未完成.请参阅更多有关故障信息的InternalException属性."));
		
		String str="aa.dd.vv";
		String[] sp=str.split("\\.");
		System.out.println(sp);
		
		String SS="[字段: 变更.上次修改人]";
		
		System.out.println(SS.replaceAll("\\[字段: 变更.上次修改人\\]", ""));
	}
	
	
}
