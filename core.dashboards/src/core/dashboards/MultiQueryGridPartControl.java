package core.dashboards;

import system.DateTime;
import system.Type;
import system.Collections.ArrayList;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.ComponentModel.Container;
import system.Data.DataColumn;
import system.Data.DataRow;
import system.Data.DataTable;
import system.Reflection.BindingFlags;
import system.Reflection.MethodInfo;
import system.Xml.XmlElement;
import Core.Dashboards.ColumnDataDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.DateRangeDef;
import Core.Dashboards.MultiQueryGridPartDef;
import Core.Dashboards.PartRefDef;
import Core.Dashboards.ViewDef;
import Siteview.AlertRaisedEventArgs;
import Siteview.AlertType;
import Siteview.DefRequest;
import Siteview.IQueryResult;
import Siteview.QueryInfoToGet;
import Siteview.QueryRequestField;
import Siteview.RelationshipQuery;
import Siteview.SiteviewQuery;
import Siteview.StringUtils;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.FieldDef;
import Siteview.Api.GridDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.RelationshipDef;
import Siteview.Xml.SecurityRight;

public class MultiQueryGridPartControl extends GridPartControl
{
	private ArrayList m_alColumnOrders;
	private boolean m_bAlertRaised;
	private boolean m_bIsDrillDown;
	private DateRangeDef m_CurrentDateRange;
	private ViewDef m_CurrentDateTimeDef;
	private BusinessObjectDef m_DrillDownBusObDef;
	private DateTime m_dtEndDateTime;
	private DataTable m_dtOriginalDataTable;
	private DateTime m_dtStartDateTime;
	private String m_ParentBusObId;
	private ColumnDataDef m_ParentColumnDataDef;

	public MultiQueryGridPartControl(ISiteviewApi iSiteviewApi, DashboardControl parent){
		super(iSiteviewApi, parent, false);
		this.m_alColumnOrders = new ArrayList();
		this.m_dtStartDateTime = DateTime.MinValue;
		this.m_dtEndDateTime = DateTime.MaxValue;
	}

//	@Override
//	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef){
//        super.DefineFromDef(def, partRefDef);
//        MultiQueryGridPartDef dashboardPartDef = (MultiQueryGridPartDef) super.get_DashboardPartDef();
//    }
	
	private void AddColumnsFromFunctionResult(DataTable dtFunctionResult, ColumnDataDef columnDataDef, DataTable dt){
		ICollection z_dfr = dtFunctionResult.get_Columns();
        IEnumerator it = z_dfr.GetEnumerator();
        while(it.MoveNext()){
        	DataColumn column = (DataColumn) it.get_Current();
        	if (!dt.get_Columns().Contains(column.get_Caption())){
				dt.get_Columns().Add(column.get_Caption());
			}
        }
	}

	protected boolean CheckFieldsHaveViewRight(){
		if (this.get_MultiQueryGridPartDef() != null){
			String[] strArray = QueryRequestField.Create(this.get_MultiQueryGridPartDef().get_GroupBy()).get_BusObFieldName().split("\\.");
			String strFieldName = (strArray.length == 1) ? strArray[0] : strArray[1];
			String strText = Res.get_Default().GetString("MultiQueryGridPartControl.GroupbyFieldNoViewRight");
			if (strArray.length == 1){
				if (!super.getApi().get_SecurityService().HasBusObFieldRight(super.get_BusObName(), strFieldName, SecurityRight.View)){
					strText = StringUtils.SetToken(strText, "FIELD", strFieldName);
					super.OnAlertRaised(this, new AlertRaisedEventArgs(strText, AlertType.Informational, null));
					return false;
				}
			}else if (strArray[0].equals(super.get_BusObName())){
				if (!super.getApi().get_SecurityService().HasBusObFieldRight(super.get_BusObName(), strFieldName, SecurityRight.View)){
					strText = StringUtils.SetToken(strText, "FIELD", strFieldName);
					super.OnAlertRaised(this, new AlertRaisedEventArgs(strText, AlertType.Informational, null));
					return false;
				}
			}else if (!super.getApi().get_SecurityService().HasBusObFieldRight(strArray[0], strFieldName, SecurityRight.View)){
				strText = StringUtils.SetToken(strText, "FIELD", strFieldName);
				super.OnAlertRaised(this, new AlertRaisedEventArgs(strText, AlertType.Informational, null));
				return false;
			}
		}
		return true;
	}

	private DataTable CreateDataTable(){
		DataTable table = this.m_dtOriginalDataTable = new DataTable();
		table.get_Columns().Add(this.get_MultiQueryGridPartDef().get_GrouoByCaption());
		table.get_Columns().Add(super.get_BusObDef().get_IdField().get_QualifiedName());
		for (ColumnDataDef def : this.get_MultiQueryGridPartDef().get_ColumnDataDefs()){
			if (!def.get_IsFunction()){
				table.get_Columns().Add(def.get_Caption());
			}
		}
		return table;
	}

	private DataTable CreateEmptyDrillDownTable(BusinessObjectDef drillDownBusObDef){
		DataTable table = new DataTable();
		ICollection z_ddvod = drillDownBusObDef.get_FieldDefs();
        IEnumerator it = z_ddvod.GetEnumerator();
        while(it.MoveNext()){
        	FieldDef def = (FieldDef) it.get_Current();
        	table.get_Columns().Add(def.get_QualifiedName());
        }
		return table;
	}

	protected SiteviewQuery CreateSiteviewQuery(){
		RelationshipDef definition = null;
		XmlElement xeSearchCriteria = null;
		SiteviewQuery query = new SiteviewQuery();
		query.AddBusObQuery(super.get_BusObDef().get_Name(), QueryInfoToGet.RequestedFields, this.get_MultiQueryGridPartDef().get_GroupBy());
		query.AddOrderBy(this.get_MultiQueryGridPartDef().get_GroupBy());
		for (ColumnDataDef def2 : this.get_MultiQueryGridPartDef().get_Queries()){
			definition = (RelationshipDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(RelationshipDef.get_ClassName(), def2.get_RelationshipId()));
			if (definition != null){
				if (def2.get_Criteria() != null){
					xeSearchCriteria = query.get_CriteriaBuilder().ImportSearchCriteria(def2.get_Criteria());
				}
				query.AddRelationship(definition.get_Name(), xeSearchCriteria, QueryInfoToGet.Count);
			}
		}
		return query;
	}

	@Override
	public  void DataBind(Object dt){
		if (!this.m_bAlertRaised){
			super.ClearAlert();
			if (dt != null){
				if (this.m_bIsDrillDown){
					if (this.m_DrillDownBusObDef != null){
						GridDef definition = (GridDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ByName(GridDef.get_ClassName(), this.m_DrillDownBusObDef.get_Name()));
						super.set_GridId("");
						super.set_GridId(super.get_GridId() + "." + definition.get_Name());
						if (dt instanceof SiteviewQuery){
							super.set_BusObDef(this.m_DrillDownBusObDef);
							super.set_BusObName(this.m_DrillDownBusObDef.get_Name());
							super.get_Grid().LoadData((SiteviewQuery)dt, definition);
//							super.Grid.DisplayLayout.Override.HeaderClickAction = HeaderClickAction.ExternalSortMulti;
						}else if (dt instanceof DataTable){
//							super.get_Grid().Reset();
//							super.Grid.DataSource = (DataTable) dt;
							super.DataBind((DataTable) dt);
//							super.Grid.DataBind();
//							super.Grid.SetColumns(definition);
//							super.Grid.DisplayLayout.Override.HeaderClickAction = HeaderClickAction.SortMulti;
						}
//						super.Grid.DisplayLayout.Override.CellClickAction = CellClickAction.RowSelect;
					}
				}else{
					this.m_dtOriginalDataTable = (DataTable) dt;
					super.get_Grid().set_Id(super.get_GridId());
					this.m_alColumnOrders = this.GetColumnOrders();
//					for(int i = 0;i<this.m_dtOriginalDataTable.get_Rows().get_Count();i++){
//						DataRow row = (DataRow) this.m_dtOriginalDataTable.get_Rows().get_Item(i);
//						for(int j = 0;j<this.m_dtOriginalDataTable.get_Columns().get_Count();j++){
//							DataColumn column = (DataColumn) this.m_dtOriginalDataTable.get_Columns().get_Item(j);
//	                    	System.out.println(this.m_dtOriginalDataTable.get_Rows().get_Item(i).get_Item(column.get_Caption()));
//						}
//					}
					
					//绑定数据后.卡死
					super.get_Grid().DataBind((DataTable) dt);
					super.set_ParentDataTable(this.m_dtOriginalDataTable);
					this.SortColumnOrders();
				}
			}
		}
	}

	private DataTable DrillDownFunction(String strBusObId, ColumnDataDef columnDataDef){
		ArrayList list = new ArrayList();
		Type type = Type.GetType(super.getClass().toString());
		//Type type = super.GetType();
		if (columnDataDef.get_FunctionDef().get_Function().equalsIgnoreCase("GetServiceAvailablityTable")){
//			type = Type.GetType(ServiceAvailabilityCalculation.getClass().toString());
			//type = typeof(ServiceAvailabilityCalculation);
		}else if (columnDataDef.get_FunctionDef().get_Function().equalsIgnoreCase("GetBreachedSLA")){
//			type = Type.GetType(BreachedSLAQuery.getClass().toString());
			//type = typeof(BreachedSLAQuery);
		}
		this.m_DrillDownBusObDef = (BusinessObjectDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), columnDataDef.get_FunctionDef().get_DrillDownBusObId(), "(Role)"));
		list.Add(super.getApi());
		list.Add(this.m_dtStartDateTime);
		list.Add(this.m_dtEndDateTime);
		list.Add(strBusObId);
		return (DataTable) this.ExcuteFunctionMethod("", null, columnDataDef.get_FunctionDef().get_DrillDownFunction(), (Object[]) list.ToArray());
	}

	private SiteviewQuery DrillDownQuery(String strBusObId, ColumnDataDef columnDataDef){
		SiteviewQuery query = null;
		RelationshipDef definition = (RelationshipDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(RelationshipDef.get_ClassName(), columnDataDef.get_RelationshipId()));
		this.m_DrillDownBusObDef = (BusinessObjectDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(BusinessObjectDef.get_ClassName(), definition.get_TargetId(), "(Role)"));
		if (definition == null){
			return query;
		}
		query = new SiteviewQuery();
		query.AddBusObQuery(super.get_BusObDef().get_Name(), QueryInfoToGet.IdAndName);
		query.set_BusObSearchCriteria(query.get_CriteriaBuilder().FieldAndValueExpression(super.get_BusObDef().get_IdField().get_QualifiedName(), Siteview.Operators.Equals, strBusObId));
		XmlElement criteria = columnDataDef.get_Criteria();
		if (this.m_CurrentDateRange != null){
			this.m_CurrentDateRange.set_DateTimeField(columnDataDef.get_DateRangeField());
			XmlElement element2 = null;
			if (this.m_CurrentDateTimeDef == null){
				element2 = DateRangeCriteriaCreator.AppendDefaultDateRangeCriteria(super.getApi(), query, this.m_CurrentDateRange, columnDataDef.get_Criteria());
			}else if (this.m_CurrentDateTimeDef.get_IsCustom()){
				element2 = DateRangeCriteriaCreator.GetCustomCriteria(query, this.m_CurrentDateRange.get_DateTimeField(), this.m_CurrentDateTimeDef.get_StartDateTime(), this.m_CurrentDateTimeDef.get_EndDateTime());
			}else if (this.m_CurrentDateTimeDef.get_IsFiscalPeriod()){
				element2 = DateRangeCriteriaCreator.GetDateRangeCriteria(super.getApi(), query, this.m_CurrentDateTimeDef.get_FiscalPeriod(), this.m_CurrentDateRange.get_DateTimeField());
			}else{
				element2 = DateRangeCriteriaCreator.GetDateRangeCriteria(super.getApi(), query, this.m_CurrentDateTimeDef.get_DateRange(), this.m_CurrentDateRange.get_DateTimeField());
			}
			if (criteria != null){
				XmlElement element3 = query.get_CriteriaBuilder().ImportSearchCriteria(criteria);
				criteria = query.get_CriteriaBuilder().AndExpressions(element2, element3);
			}else{
				criteria = element2;
			}
		}
		query.AddRelationship(definition.get_Name(), criteria, QueryInfoToGet.All);
		return SiteviewQuery.GetPageQueryForRelationship(query, strBusObId, query.get_BusinessObjectName(), definition.get_Name(), definition.get_TargetBusinessObjectDef().get_Name(), criteria, QueryInfoToGet.All, null);
	}

	private Object ExcuteFunctionMethod(String strModule, Type type, String strMethod, Object[] parameters){
		Object obj2 = null;
		try{
			if (type != null){
				MethodInfo method = type.GetMethod(strMethod, BindingFlags.Public | BindingFlags.Static);
				if (method == null){
					return obj2;
				}
				obj2 = method.Invoke(null, parameters);
			}
		}catch (Exception e){
			//
		}
		return obj2;
	}

	private void FillDataFromFunctions(DataTable dt){
		int num = 0;
		DataTable dtFunctionResult = null;
		ArrayList list = new ArrayList(8);
		Type type = Type.GetType(super.getClass().toString());
		//Type type = super.GetType();
		for (ColumnDataDef def : this.get_MultiQueryGridPartDef().get_Functions()){
			list.Clear();
			if (def.get_FunctionDef().get_Function().equalsIgnoreCase("GetServiceAvailablityTable")){
				list.Add(super.getApi());
				list.Add(this.m_dtStartDateTime);
				list.Add(this.m_dtEndDateTime);
				list.Add(def.get_FunctionDef().GetParameterValue("strAvailabilityColumn"));
				list.Add(def.get_FunctionDef().GetParameterValue("strNumOfOutageColumn"));
				list.Add(def.get_FunctionDef().GetParameterValue("strGroupBy"));
				list.Add(def.get_FunctionDef().GetParameterValue("strScheduledCategory"));
				list.Add(def.get_FunctionDef().GetParameterValue("strUnscheduledCategory"));
				//type = Type.GetType(ServiceAvailabilityCalculation.getClass().toString());
				//type = typeof(ServiceAvailabilityCalculation);
			}else if (def.get_FunctionDef().get_Function().equalsIgnoreCase("GetBreachedSLA")){
				list.Add(super.getApi());
				list.Add(this.m_dtStartDateTime);
				list.Add(this.m_dtEndDateTime);
				if (def.get_FunctionDef().get_ColumnCaptions().length > 0){
					list.Add(def.get_FunctionDef().get_ColumnCaptions()[0]);
				}else{
					list.Add(def.get_FunctionDef().GetParameterValue("strColumnCaption"));
				}
				list.Add(def.get_FunctionDef().GetParameterValue("strGroupBy"));
				//type = Type.GetType(BreachedSLAQuery.getClass().toString());
				//type = typeof(BreachedSLAQuery);
			}
			dtFunctionResult = (DataTable) this.ExcuteFunctionMethod("", null, def.get_FunctionDef().get_Function(), (Object[]) list.ToArray());
			if (dtFunctionResult != null){
				this.AddColumnsFromFunctionResult(dtFunctionResult, def, dt);
				num = 0;
				ICollection z_dfr = dtFunctionResult.get_Rows();
                IEnumerator it = z_dfr.GetEnumerator();
                while(it.MoveNext()){
                	DataRow row = (DataRow) it.get_Current();
                	ICollection z_dfc = dtFunctionResult.get_Columns();
                    IEnumerator it1 = z_dfc.GetEnumerator();
                    while(it1.MoveNext()){
                    	DataColumn column = (DataColumn) it1.get_Current();
                    	dt.get_Rows().get_Item(num).set_Item(column.get_Caption(),row.get_Item(column.get_Caption()).toString());
                    }
                    num++;
                }	
			}
		}
	}

	private void FillDataFromQuery(IQueryResult queryResult, DataTable res){
		String strOwnerBusObId = "";
		DataRow row = null;
		DataTable relationshipDataTable = null;
		RelationshipDef definition = null;
		ICollection qr_bodt = queryResult.get_BusObDataTable().get_Rows();
        IEnumerator it = qr_bodt.GetEnumerator();
        while(it.MoveNext()){
        	DataRow row2 = (DataRow) it.get_Current();
        	row = res.NewRow();
			strOwnerBusObId = (String) row2.get_Item(super.get_BusObDef().get_IdField().get_QualifiedName());
			row.set_Item(super.get_BusObDef().get_IdField().get_QualifiedName(),(String) row2.get_Item(super.get_BusObDef().get_IdField().get_QualifiedName()));
			row.set_Item(this.get_MultiQueryGridPartDef().get_GrouoByCaption(),(String) row2.get_Item(this.get_MultiQueryGridPartDef().get_GroupBy()));
			for (ColumnDataDef def2 : this.get_MultiQueryGridPartDef().get_Queries()){
				definition = (RelationshipDef) super.getApi().get_LiveDefinitionLibrary().GetDefinition(DefRequest.ById(RelationshipDef.get_ClassName(), def2.get_RelationshipId()));
				if (definition != null){
					relationshipDataTable = queryResult.GetRelationshipDataTable(strOwnerBusObId, definition.get_Name());
					row.set_Item(def2.get_Caption(),((relationshipDataTable != null) && (relationshipDataTable.get_Rows().get_Count() > 0)) ? relationshipDataTable.get_Rows().get_Item(0).get_Item("Count") : 0);
				}
			}
			res.get_Rows().Add(row);
        }
	}

	private ColumnDataDef GetColumnDataDefByCaption(String strCaption){
		ColumnDataDef def = null;
		for (ColumnDataDef def2 : this.get_MultiQueryGridPartDef().get_ColumnDataDefs()){
			if (def2.get_IsFunction()){
				for (String str : def2.get_FunctionDef().get_ColumnCaptions()){
					if (str.equalsIgnoreCase(strCaption)){
						def = def2;
						break;
					}
				}
			}else if (def2.get_Caption().equalsIgnoreCase(strCaption)){
				return def2;
			}
		}
		return def;
	}

	private ColumnDataDef GetColumnDataDefByRelationship(String strRelationship){
		for (ColumnDataDef def2 : this.get_MultiQueryGridPartDef().get_Queries()){
			if (def2.get_RelationshipName().equalsIgnoreCase(strRelationship)){
				return def2;
			}
		}
		return null;
	}

	private ArrayList GetColumnOrders(){
		ArrayList list = new ArrayList();
		list.Add(this.get_MultiQueryGridPartDef().get_GrouoByCaption());
		for (ColumnDataDef def : this.get_MultiQueryGridPartDef().get_ColumnDataDefs()){
			if (def.get_IsFunction()){
				for (String str : def.get_FunctionDef().get_ColumnCaptions()){
					list.Add(str);
				}
			}else{
				list.Add(def.get_Caption());
			}
		}
		return list;
	}

	@Override
	public Object GetData(ILoadingStatusSink sink){
		Object obj2 = null;
		try{
			super.set_FirstTimeToLoad(false);
			if (!super.getApi().get_SecurityService().HasBusObRight(super.get_BusObDef().get_Name(), SecurityRight.View)){
				super.get_Grid().set_Visible(false);
				super.ShowNoRightsAlert(super.get_BusObDef().get_Name());
				this.m_bAlertRaised = true;
				return null;
			}
			if (!this.CheckFieldsHaveViewRight()){
				super.get_Grid().set_Visible(false);
				this.m_bAlertRaised = true;
				return null;
			}
			if (this.m_bIsDrillDown){
				if (this.m_ParentColumnDataDef == null){
					return obj2;
				}
				if (this.m_ParentColumnDataDef.get_IsFunction()){
					return this.DrillDownFunction(this.m_ParentBusObId, this.m_ParentColumnDataDef);
				}
				return this.DrillDownQuery(this.m_ParentBusObId, this.m_ParentColumnDataDef);
			}
			if (super.get_Query() == null){
				return obj2;
			}
			IQueryResult queryResult = super.getApi().get_BusObService().get_SimpleQueryResolver().ResolveQuery(super.get_Query());
			if (sink != null){
				sink.ChangeProgress(0x19, 100);
			}
			DataTable res = this.CreateDataTable();
			if (sink != null){
				sink.ChangeProgress(50, 100);
			}
			this.FillDataFromQuery(queryResult, res);
			if (sink != null){
				sink.ChangeProgress(0x4b, 100);
			}
			this.FillDataFromFunctions(res);
			if (sink != null){
				sink.ChangeProgress(90, 100);
			}
			obj2 = res;
		}catch (system.Exception exception){
			if (!super.get_TrapErrors()){
				throw exception;
			}
			super.get_Grid().set_Visible(false);
			this.m_bAlertRaised = true;
			super.OnAlertRaised(this, new AlertRaisedEventArgs(AlertType.Error, exception));
			return obj2;
		}
		return obj2;
	}

//	@Override
//	public void m_Grid_DoubleClick(Object sender, EventArgs e){
//		if (this.m_bIsDrillDown){
//			if (!(super.get_Grid().DataSource is DataTable)){
//				super.m_Grid_DoubleClick(sender, e);
//			}else if ((this.m_DrillDownBusObDef != null) && (super.Grid.ActiveRow != null)){
//				IVirtualBusObKeyList iVirtualKeyList = super.getApi().get_BusObService().CreateVirtualKeyListByDataTable((DataTable) super.Grid.DataSource, this.m_DrillDownBusObDef.get_IdField().get_QualifiedName());
//				super.Parent.OnGoToBusOb(iVirtualKeyList, super.Grid.ActiveRow.Cells["BusObName"].Value as String, super.Grid.ActiveRow.Cells[this.m_DrillDownBusObDef.get_IdField().get_QualifiedName()].Value as String, GridAction.Default, "");
//			}
//		}else if (super.Grid.ActiveCell != null){
//			//if (String.Compare(this.get_MultiQueryGridPartDef().get_GrouoByCaption(), super.Grid.ActiveCell.Column.Header.get_Caption(), true) == 0){
//				SiteviewQuery q = super.get_Query();
//				q.RemoveRelationshipQueries();
//				VirtualKeyList list2 = new VirtualKeyList(super.getApi(), q);
//				list2.CurrentPosition = super.Grid.ActiveCell.Row.ListIndex;
//				super.Parent.OnGoToBusOb(list2, super.get_BusObDef().get_Name(), super.Grid.ActiveCell.Row.Cells[super.get_BusObDef().get_IdField().get_QualifiedName()].Value as String, GridAction.Default, "");
//			//}else{
//			//    this.m_ParentBusObId = super.Grid.ActiveCell.Row.Cells[super.get_BusObDef().get_IdField().get_QualifiedName()].Value as String;
//			//    this.m_ParentColumnDataDef = this.GetColumnDataDefByCaption(super.Grid.ActiveCell.Column.Header.get_Caption());
//			//    this.m_bIsDrillDown = true;
//			//    this.DoDataRefresh();
//			//}
//		}
//	}

	@Override
	protected void PrepareQuery(){
		ColumnDataDef columnDataDefByRelationship = null;
		super.set_Query(this.CreateSiteviewQuery());
		if (this.get_MultiQueryGridPartDef().get_DefaultDateRangeDef().get_ApplyDateRange()){
			this.m_CurrentDateRange = this.get_MultiQueryGridPartDef().get_DefaultDateRangeDef().CloneWithOutOwner();
			DateTime[] z = new DateTime[1],f = new DateTime[1];
			DateRangeCriteriaCreator.GetStartEndDateTimeForDateRangeDef(super.getApi(), this.m_CurrentDateRange, z, f);
			this.m_dtStartDateTime = z[0];
			this.m_dtEndDateTime = f[0];
			if (super.get_Query().get_RelationshipQueries() != null){
				ICollection z_rsq = super.get_Query().get_RelationshipQueries();
				IEnumerator it = z_rsq.GetEnumerator();
				while(it.MoveNext()){
					RelationshipQuery query = (RelationshipQuery) it.get_Current();
					columnDataDefByRelationship = this.GetColumnDataDefByRelationship(query.get_Name());
					if ((columnDataDefByRelationship != null) && ("" != columnDataDefByRelationship.get_DateRangeField())){
						this.m_CurrentDateRange.set_DateTimeField(columnDataDefByRelationship.get_DateRangeField());
						query.set_SearchCriteria(DateRangeCriteriaCreator.AppendDefaultDateRangeCriteria(super.getApi(), super.get_Query(), this.m_CurrentDateRange, query.get_SearchCriteria()));
					}
				}
			}
		}
	}

	@Override
	protected void ReQuery(ViewDef vViewDef, ViewDef vDateTimeDef){
		if (!super.get_FirstTimeToLoad() && (vDateTimeDef != null)){
			super.set_BusObName(this.get_MultiQueryGridPartDef().get_BusObName());
			super.set_BusObDef(super.getApi().get_BusObDefinitions().GetBusinessObjectDef(super.get_BusObName()));
			this.m_CurrentDateTimeDef = vDateTimeDef;
			ColumnDataDef columnDataDefByRelationship = null;
			SiteviewQuery query = this.CreateSiteviewQuery();
			if (vDateTimeDef != null){
				this.m_dtStartDateTime = vDateTimeDef.get_StartDateTime();
				this.m_dtEndDateTime = vDateTimeDef.get_EndDateTime();
				XmlElement element = null;
				if (query.get_RelationshipQueries() != null){
					ICollection q_rsq = query.get_RelationshipQueries();
					IEnumerator it = q_rsq.GetEnumerator();
					while(it.MoveNext()){
						RelationshipQuery query2 = (RelationshipQuery) it.get_Current();
						columnDataDefByRelationship = this.GetColumnDataDefByRelationship(query2.get_Name());
						if ((columnDataDefByRelationship != null) && ("" != columnDataDefByRelationship.get_DateRangeField())){
							if (vDateTimeDef.get_IsCustom()){
								element = DateRangeCriteriaCreator.GetCustomCriteria(query, columnDataDefByRelationship.get_DateRangeField(), vDateTimeDef.get_StartDateTime(), vDateTimeDef.get_EndDateTime());
							}else if (vDateTimeDef.get_IsFiscalPeriod()){
								element = DateRangeCriteriaCreator.GetDateRangeCriteria(super.getApi(), query, vDateTimeDef.get_FiscalPeriod(), columnDataDefByRelationship.get_DateRangeField());
							}else{
								element = DateRangeCriteriaCreator.GetDateRangeCriteria(super.getApi(), query, vDateTimeDef.get_DateRange(), columnDataDefByRelationship.get_DateRangeField());
							}
							if (query2.get_SearchCriteria() != null){
								XmlElement element2 = query.get_CriteriaBuilder().ImportSearchCriteria(query2.get_SearchCriteria());
								query2.set_SearchCriteria(query.get_CriteriaBuilder().AndExpressions(element, element2));
							}else{
								query2.set_SearchCriteria(element);
							}
						}
					}
				}
			}
			super.set_Query(query);
			this.m_bIsDrillDown = false;
			this.DoDataRefresh();
		}
	}

	private void SetDataTable(){
		if (super.get_Query() != null){
			IQueryResult queryResult = super.getApi().get_BusObService().get_SimpleQueryResolver().ResolveQuery(super.get_Query());
			this.m_alColumnOrders = this.GetColumnOrders();
			DataTable res = this.CreateDataTable();
			this.FillDataFromQuery(queryResult, res);
			this.FillDataFromFunctions(res);
			if (res != null){
				this.m_dtOriginalDataTable = res;
				//super.Grid.DataSource = this.m_dtOriginalDataTable;
				super.DataBind(this.m_dtOriginalDataTable);
				//super.Grid.DataBind();
				super.set_ParentDataTable(this.m_dtOriginalDataTable);
				this.SortColumnOrders();
			}
		}
	}

	private void ShowAlert(){
		super.ShowNoRightsAlert(super.get_BusObDef().get_Name());
	}

	private void SortColumnOrders(){
//		int num = 0;
//		super.Grid.DisplayLayout.Bands[0].get_Columns()[super.get_BusObDef().get_IdField().get_QualifiedName()].Hidden = true;
//		for (String str : this.m_alColumnOrders){
//			if (super.Grid.DisplayLayout.Bands[0].get_Columns().Contains(str)){
//				super.Grid.DisplayLayout.Bands[0].get_Columns()[str].Header.VisiblePosition = num;
//				num++;
//			}
//		}
//		super.Grid.Refresh();
	}

	public MultiQueryGridPartDef get_MultiQueryGridPartDef(){
		return (MultiQueryGridPartDef) super.get_DashboardPartDef();
	}
}


