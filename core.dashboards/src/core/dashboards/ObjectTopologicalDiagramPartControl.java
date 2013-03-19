package core.dashboards;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ObjectTopologicalDiagramPartDef;
import Core.Dashboards.PartRefDef;
import Siteview.Api.ISiteviewApi;
import core.dashboards.gef.Topo;

public class ObjectTopologicalDiagramPartControl extends DashboardPartControl {
	private GridPartControl gridControl;
	private boolean m_GridMode;
	private Composite MainControl;
	private ScrolledComposite MainScs;
	private int z_space = 10;
	private StackLayout stacklayout;
	private ObjectTopologicalDiagramPartDef dashboardPartDef;

	public ObjectTopologicalDiagramPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi, parent, 0);

		stacklayout = new StackLayout();
		this.get_MainArea().setLayout(stacklayout);

		this.gridControl = new GridPartControl(super.getApi(),
				this.get_MainArea(), true);
		this.gridControl.set_Parent(get_Parent());
		gridControl.removeTitleBar();
		this.MainControl = new ScrolledComposite(this.get_MainArea(),
				SWT.V_SCROLL);
		this.MainControl.setLayout(new GridLayout(1, false));
		this.MainControl.setParent(get_Parent());
		

		// TODO Auto-generated constructor stub
	}

	private void showPanel(Composite p) {
		this.stacklayout.topControl = p;
		this.get_MainArea().layout();
	}

	@Override
	public void DataBind(Object dt) {
		// TODO Auto-generated method stub
		System.out.println("����ͼ");

		super.DataBind(dt);

		for (Control control : this.MainControl.getChildren()) {

			if (control instanceof Canvas) {
				Canvas canvas = (Canvas) control;
				
				//canvas=BasicBorders.createDiagram(MainControl);
				
				Topo topo=new Topo(dashboardPartDef,MainControl);
				canvas=topo.getCanvas();
				canvas.setLayoutData(new org.eclipse.swt.layout.GridData(
						org.eclipse.swt.layout.GridData.FILL_BOTH));

				canvas.setBounds(0, 0, 2600, 1600);
			}

		}
		this.MainScs.setContent(this.MainControl);
		this.MainScs.setExpandHorizontal(true);
		this.MainScs.setExpandVertical(true);
		Point p = this.MainControl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		MainScs.setMinSize(p.x, p.y);

		showPanel(this.MainScs);

	}

	
	@Override
	public Object GetData(ILoadingStatusSink sink) {
		dashboardPartDef = (ObjectTopologicalDiagramPartDef) super
				.get_DashboardPartDef();
		
		
		System.out.println(dashboardPartDef);
		
		return dashboardPartDef;
	}

	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef) {
		// TODO Auto-generated method stub
		boolean flag = false;
		super.DefineFromDef(def, partRefDef);

		MainScs = new ScrolledComposite(this.get_MainArea(), SWT.H_SCROLL
				| SWT.V_SCROLL);

		this.MainControl = new Composite(MainScs, SWT.None);
		// Link link1 = null;
		//
		// link1 = new Link(MainControl, SWT.NONE);
		// link1.setText("<a>dddddddddddddd</a>");
		// link1.setSize(getTextWidth(link1.getText(), 1000), 20);
		// link1.setLocation(10, z_space);
		// z_space = link1.getLocation().y + 25;

		Canvas canvas = new Canvas(MainControl, SWT.DOUBLE_BUFFERED);

		MainScs.setContent(this.MainControl);
		MainScs.setExpandHorizontal(true);
		MainScs.setExpandVertical(true);

		Point p = this.MainControl.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		MainScs.setMinSize(p.x, p.y);

		showPanel(this.MainScs);
	}

	public int getTextWidth(String strValue, int MaxLenth) {
		// ��� Ӣ������ʹ��Arial����ͨģʽ����С10����λ�ǰ��������еġ�һ�š��������š��������ķ��಻֧�֡���
		Font f = new Font("g����", Font.PLAIN, 10);
		// ȫ�� ���ģ�����MSGothic����ͨģʽ����СҲ��10.
		Font jpf = new Font("MSGothic", Font.PLAIN, 10);
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
		FontMetrics zffm = Toolkit.getDefaultToolkit().getFontMetrics(jpf);

		int totalWidth = 0;
		String strReturn = "";
		String tmpChar = "";
		int w_width = zffm.stringWidth("W");
		if (strValue != null) {
			int chr_width = 0;
			for (int i = 0; i < strValue.length(); i++) {
				// ȡ��ĳһ���ַ�
				tmpChar = strValue.substring(i, i + 1);
				// �ж��Ƿ���ȫ���ַ������ʹ��Ӣ�����壬ȫ��ʹ������������м�����
				if (tmpChar.getBytes().length == 1) {
					chr_width = fm.stringWidth(tmpChar);
				} else {
					chr_width = zffm.stringWidth(tmpChar);
				}
				// �ܿ�ȴ���ָ���Ŀ�Ⱥ󣬽�ȡ���ˡ�
				if ((totalWidth + chr_width) > w_width * MaxLenth) {
				}
				strReturn += tmpChar;
				totalWidth += chr_width;
			}
		}
		return totalWidth;
	}
}
