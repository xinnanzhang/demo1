package core.dashboards.gef;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.graph.DirectedGraph;
import org.eclipse.draw2d.graph.DirectedGraphLayout;
import org.eclipse.draw2d.graph.Edge;
import org.eclipse.draw2d.graph.EdgeList;
import org.eclipse.draw2d.graph.Node;
import org.eclipse.draw2d.graph.NodeList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import system.Collections.ICollection;
import system.Collections.IEnumerator;
import Core.Dashboards.ObjectTopologicalDiagramPartDef;
import Siteview.LegalUtils;
import Siteview.QueryInfoToGet;
import Siteview.SiteviewQuery;
import Siteview.Api.BusinessObject;
import Siteview.Api.BusinessObjectCollection;
import Siteview.Api.BusinessObjectDef;
import Siteview.Api.Field;
import Siteview.Api.ISiteviewApi;
import Siteview.Api.Relationship;
import Siteview.Windows.Forms.ConnectionBroker;
import core.busobmaint.BusObMaintView;
import core.busobmaint.BusObNewInput;

public class Topo {

	private ObjectTopologicalDiagramPartDef objectTopo;
	private Composite composite;
	private String name = "";
	private String id = "";
	private ISiteviewApi m_api;
	private String[] reship;
	private String[] shownames;
	private String[] topshownames;
	private String bustoptext = "";
	private String bustiptext = "";
	private Figure root;
	private NodeList nodes = new NodeList();
	private EdgeList edges = new EdgeList();
	private Canvas canvas;
	private BusinessObjectDef busobdef; // 业务对象定义集
	private ICollection icbusob ;

	public Topo(ObjectTopologicalDiagramPartDef objectTopo, Composite composite) {
		this.objectTopo = objectTopo;
		this.composite = composite;
		m_api = ConnectionBroker.get_SiteviewApi();
		getDate();
	}

	public void getDate() {
		if (objectTopo == null)
			return;
		this.root = new Figure();
		this.root.setFont(composite.getFont());
		this.root.setLayoutManager(new XYLayout());
		this.name = objectTopo.get_BusObName();
		this.id = objectTopo.get_BusObId();
		this.bustoptext = objectTopo.get_MainBusObTopText();
		this.bustiptext = objectTopo.get_MainBusObTipText();
		String tempstr = this.objectTopo.get_TopoRelationShipListString();
		String[] templist = tempstr.split("&");
		if (templist != null && templist.length > 0) {
			reship = new String[templist.length];
			shownames= new String[templist.length];
			topshownames= new String[templist.length];
			for (int i = 0; i < templist.length; i++) {
				String[] res = templist[i].split("\\|");
				if (res != null && res.length > 3) {
					reship[i] = res[3];
					shownames[i] = res[1];
					topshownames[i] = res[2];
				}
			}
		}
		Anaylise();
		DirectedGraph graph=new DirectedGraph();
		graph.edges=edges;
		graph.nodes=nodes;
		new DirectedGraphLayout().visit(graph);
		
		
		
		
		for(int i=0;i<nodes.size();i++){
			Node node=nodes.getNode(i);
			System.out.println("X:"+node.x+"   Y:"+node.y+"   W:"+node.width+"   H:"+node.height);
		}
		
		DrawTopo();
		canvas = new Canvas(composite, SWT.DOUBLE_BUFFERED);
		canvas.setBackground(ColorConstants.white);
		LightweightSystem lws = new LightweightSystem(canvas);
		lws.setContents(root);
		canvas.setBounds(0, 0, 2600, 1600);
		
	}

	public void Anaylise() {
		busobdef = m_api.get_LiveDefinitionLibrary().GetBusinessObjectDef(name);

		if (busobdef != null) {

			// 画 业务对象定义节点
			Node source = AddNode(busobdef.get_Id());

			SiteviewQuery siteviewQuery = new SiteviewQuery();
			siteviewQuery.AddBusObQuery(name, QueryInfoToGet.All);
			icbusob = m_api.get_BusObService()
					.get_SimpleQueryResolver()
					.ResolveQueryToBusObList(siteviewQuery);
			if (icbusob != null) {
				IEnumerator iebusob = icbusob.GetEnumerator();
				while (iebusob.MoveNext()) {
					BusinessObject busob = (BusinessObject) iebusob
							.get_Current();
					Node target = AddNode(busob.get_Id());
					AddEdge(busobdef.get_Id() + "~" + busob.get_Id(), source,
							target);
					
					for (int i = 0; i < reship.length; i++) {
						
						if(reship[i]!=null&&!"".equals(reship[i])){
							Relationship relationship = busob
									.GetRelationship(reship[i]);
							if (relationship != null) {
								BusinessObjectCollection busobcoll = relationship
										.get_BusinessObjects();
								if (busobcoll != null) {
									for (int j = 0; j < busobcoll.get_Count(); j++) {
										BusinessObject busobject = (BusinessObject) busobcoll
												.get_Item(j);
										if (busobject != null) {
											Node target2 = AddNode(busobject.get_Id());
											AddEdge(busob.get_Id() + "~"
													+ busobject.get_Id(), target,
													target2);
										}

									}
								}
							}
						}
						
					}

				}
			}
		}

	}

	
	public void DrawTopo(){
		
		//画总节点
		int[] x_y=getX_Y(busobdef.get_Id());
		IFigure Source	=Draw_Rectangle(null, x_y[0], x_y[1], x_y[2], x_y[3], busobdef);
		
		//画二级节点
		if (icbusob != null) {
			IEnumerator iebusob = icbusob.GetEnumerator();
			while (iebusob.MoveNext()) {
				BusinessObject busob = (BusinessObject) iebusob
						.get_Current();
				x_y=getX_Y(busob.get_Id());
				IFigure Source2=Draw_RectangleFigure(Source,  x_y[0], x_y[1], x_y[2], x_y[3], busob,bustoptext,bustiptext);
				for (int i = 0; i < reship.length; i++) {
					if(reship[i]!=null&&!"".equals(reship[i])){
						Relationship relationship = busob
								.GetRelationship(reship[i]);
						if (relationship != null) {
							BusinessObjectCollection busobcoll = relationship
									.get_BusinessObjects();
							if (busobcoll != null) {
								for (int j = 0; j < busobcoll.get_Count(); j++) {
									BusinessObject busobject = (BusinessObject) busobcoll
											.get_Item(j);
									if (busobject != null) {
										x_y=getX_Y(busobject.get_Id());
										Draw_Ellipse(Source2,  x_y[0], x_y[1], x_y[2], x_y[3], busobject,shownames[i],topshownames[i]);
									}

								}
							}
						}
					}
				}

			}
		}
		
	}
	
	
	// 增加节点
	public Node AddNode(String name) {
		Node node = getSelectNode(name);
		if (node == null) {
			node = new Node(name);
			node.width = 200;
			nodes.add(node);
		}
		return node;
	}

	// 增加边
	public void AddEdge(String name, Node source, Node target) {
		Edge edge = new Edge(name, source, target);
		edges.add(edge);
	}

	public Node getSelectNode(String id) {
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.getNode(i);
			if (id.equals(node.data.toString())) {
				return node;
			}
		}
		return null;
	}

	public int[] getX_Y(String Id) {
		int[] x_y = new int[] { 100, 100, 200, 35 };
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.getNode(i);
			if (Id.equals(node.data.toString())) {
				x_y[0] = node.x;
				x_y[1] = node.y;
				x_y[3] = node.height;
				x_y[2] = node.width;
			}
		}
		return x_y;
	}

	// 画总节点
	public IFigure Draw_Rectangle(IFigure Source, int x, int y, int w, int h,
			final BusinessObjectDef busdef) {

		Label label = new Label();
		label.setText(busdef.get_Alias());
		label.setTextAlignment(2);
		label.setOpaque(true);
		label.setBackgroundColor(ColorConstants.orange);
		label.setToolTip(new Label(busdef.get_Alias()));
		// label.setIcon(CreateImages(IImageKeys.Server_topo));
		root.add(label, new Rectangle(new Point(x, y), new Dimension(w, h)));
		new FigureMover(label);
		if (Source != null) {
			Draw_Line(Source, label);
		}
		return label;
	}

	// 画二级节点
	public IFigure Draw_RectangleFigure(IFigure Source, int x, int y, int w,
			int h, final BusinessObject bus,String top ,String tip) {

		Field field = bus.GetField(top);
		String showtop="未找到";
		if (field != null){
			showtop=field.get_Value().toString();
		}

		Label label = new Label();
		label.setText(showtop);
		label.setTextAlignment(2);
		field=null;
		field = bus.GetField(tip);
		String showtip="未找到";
		if (field != null){
			showtip=field.get_Value().toString();
		}
		label.setOpaque(true);
		label.setToolTip(new Label(showtip));
		label.setBackgroundColor(ColorConstants.green);
		// label.setIcon(CreateImages(IImageKeys.VirtualMachine_topo));
		root.add(label, new Rectangle(new Point(x, y), new Dimension(w, h)));
		new FigureMover(label);

		if (Source != null) {
			Draw_Line(Source, label);
		}
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent me) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent me) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				openBusObMaintView(bus);

			}
		});
		return label;
	}

	// 画三级节点
	public void Draw_Ellipse(IFigure Source, int x, int y, int w, int h,
			final BusinessObject bus,String top,String tip) {
		Field field = bus.GetField(top);
		String showtop="未找到";
		if (field != null){
			showtop=field.get_Value().toString();
		}
		Label label = new Label();
		label.setText(showtop);
		label.setTextAlignment(2);
		field=null;
		field = bus.GetField(tip);
		String showtip="未找到";
		if (field != null){
			showtip=field.get_Value().toString();
		}
		label.setToolTip(new Label(showtip));
		label.setBackgroundColor(ColorConstants.titleGradient);
		label.setOpaque(true);
		// label.setIcon(CreateImages(IImageKeys.Oracle_topo));
		root.add(label, new Rectangle(new Point(x, y), new Dimension(w, h)));
		new FigureMover(label);

		if (Source != null) {
			Draw_Line(Source, label);
		}
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent me) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent me) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				openBusObMaintView(bus);

			}
		});

	}

	// 画线条
	public void Draw_Line(IFigure Source, IFigure Targer) {
		PolylineConnection conn = new PolylineConnection();
		conn.setForegroundColor(ColorConstants.black);
		conn.setSourceAnchor(new EllipseAnchor(Source));
		conn.setTargetAnchor(new EllipseAnchor(Targer));
		// Set the target decoration
		Label label = new Label("");
		// label.setToolTip(new Label(linename));
		label.setForegroundColor(ColorConstants.red);
		conn.add(label, new ConnectionLocator(conn, ConnectionLocator.MIDDLE));
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setBackgroundColor(ColorConstants.blue);
		conn.setTargetDecoration(decoration);
		root.add(conn);
	}

	// 打开业务对象
	public void openBusObMaintView(Object ob) {
		if (ob instanceof BusinessObject) {
			try {
				PlatformUI
						.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.openEditor(
								new BusObNewInput(
										ConnectionBroker.get_SiteviewApi(), "",
										(BusinessObject) ob), BusObMaintView.ID);
			} catch (PartInitException e) {
				MessageDialog.openError(null,
						LegalUtils.get_MessageBoxCaption(), e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public Canvas getCanvas() {
		return canvas;
	}

}
