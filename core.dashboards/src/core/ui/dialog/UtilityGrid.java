package core.ui.dialog;

import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.OverlayImageDescriptor;
import siteview.windows.forms.SwtImageConverter;
import system.Xml.XmlDocument;
import system.Xml.XmlElement;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.MultiQueryGridPartDef;
import Siteview.IDefinition;
import Siteview.XmlUtils;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import Siteview.Xml.GridAction;


public class UtilityGrid extends Dialog{
	
	
	private String type;	//操作类型----1.新建 2.编辑 3.复制
	private String linkTo;  //关联下拉式 选 项
	private HashSet<String> Categoryset = new HashSet<String>(); //保存全部类别的集合
//	private OutlookCalendarPartDef calendarDef;
	private DashboardPartDef dashboardPartDef;
	private MultiQueryGridPartDef multiQueryGridPartDef; 
	
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	private IDefinition m_Def;
	private Generalmethods general;
	

	private Text text_Name;
	private Text text_Alias;
	private Text text_XML;

	/**
	 * @wbp.parser.constructor
	 */
	protected UtilityGrid(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
	}

	public UtilityGrid(Shell parentShell, DashboardPartDef dashboardPartDef, ISiteviewApi m_Api, DefinitionLibrary m_Library, ScopeUtil m_ScopeUtil, IDefinition m_Def, String linkTo, String type) {
		super(parentShell);
		this.dashboardPartDef = dashboardPartDef;
		this.multiQueryGridPartDef = (MultiQueryGridPartDef)dashboardPartDef;
		this.m_Library = m_Library;
		this.m_ScopeUtil = m_ScopeUtil;
		this.m_Api = m_Api;
		this.linkTo = linkTo;
		this.type = type;
		this.m_Def = m_Def;
		general=new Generalmethods(getShell(),dashboardPartDef, m_Api, m_Library, m_ScopeUtil, m_Def, linkTo);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(700, 720);
		newShell.setLocation(300, 100);
		newShell.setText(type + "	多功能查询网格");
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
		
		text_Name = new Text(container, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.right = new FormAttachment(lblNewLabel, 406, SWT.RIGHT);
		fd_text.top = new FormAttachment(0, 28);
		fd_text.left = new FormAttachment(lblNewLabel, 6);
		text_Name.setLayoutData(fd_text);
		
		text_Alias = new Text(container, SWT.BORDER);
		FormData fd_text_1 = new FormData();
		fd_text_1.top = new FormAttachment(lbla, -3, SWT.TOP);
		fd_text_1.right = new FormAttachment(text_Name, 0, SWT.RIGHT);
		fd_text_1.left = new FormAttachment(text_Name, 0, SWT.LEFT);
		text_Alias.setLayoutData(fd_text_1);
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(text_Alias, 21);
		fd_tabFolder.right = new FormAttachment(lblNewLabel, 604);
		fd_tabFolder.left = new FormAttachment(lblNewLabel, 0, SWT.LEFT);
		fd_tabFolder.bottom = new FormAttachment(100, -40);
		tabFolder.setLayoutData(fd_tabFolder);
		
		
		general.tabFolder_1(tabFolder);
		general.tabFolder_2(tabFolder);
		
		
		TabItem tbtmXml = new TabItem(tabFolder, 0);
		tbtmXml.setText("XML\u5B9A\u4E49");
		
		Composite xmldefine = new Composite(tabFolder, SWT.NONE);
		tbtmXml.setControl(xmldefine);
		
		text_XML = new Text(xmldefine, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		text_XML.setBounds(10, 10, 576, 401);
		text_XML.setEditable(false);
		
		Button btnNewButton = new Button(xmldefine, SWT.NONE);
		btnNewButton.setEnabled(false);
		btnNewButton.setBounds(10, 417, 25, 25);
		btnNewButton.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.Cut.png"),0x12,0x12));
		
		Button btnNewButton_1 = new Button(xmldefine, SWT.NONE);
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.setBounds(35, 417, 25, 25);
		btnNewButton_1.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.Copy.png"),0x12,0x12));
		
		Button btnNewButton_2 = new Button(xmldefine, SWT.NONE);
		btnNewButton_2.setEnabled(false);
		btnNewButton_2.setBounds(60, 417, 25, 25);
		btnNewButton_2.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.Paste.png"),0x12,0x12));
		
		Button btnNewButton_3 = new Button(xmldefine, SWT.NONE);
		btnNewButton_3.setEnabled(false);
		btnNewButton_3.setBounds(95, 417, 25, 25);
		btnNewButton_3.setImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Siteview#Images.Icons.Common.Find.png"),0x12,0x12));
		
		Button btnNewButton_4 = new Button(xmldefine, SWT.NONE);
		btnNewButton_4.setEnabled(false);
		btnNewButton_4.setBounds(120, 417, 25, 25);
		btnNewButton_4.setImage(new OverlayImageDescriptor(ImageDescriptor.createFromImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Siteview#Images.Icons.Common.Find.png"),0x12,0x12)),ImageDescriptor.createFromImage(SwtImageConverter.ConvertToSwtImage(ImageResolver.get_Resolver().ResolveImage("[IMAGE]Core#Images.Icons.replace2.png"),0x9,0x9)) , new Point(0x12,0x12), OverlayImageDescriptor.BOTTOM_RIGHT).createImage());
		
		fillProperties();
		
		return container;
	}
	
	private void fillProperties(){
		
		multiQueryGridPartDef.set_DateRangeControlVisible(true);
        multiQueryGridPartDef.set_AllowDateTimeEntry(true);
        
		if(!"New".equals(type)){
			if ("Edit".equals(type)) {
				text_Name.setText(dashboardPartDef.get_Name());
				text_Alias.setText(dashboardPartDef.get_Alias());
				
			} else {
				text_Name.setText(dashboardPartDef.get_Name() + "  的副本");
				text_Alias.setText(dashboardPartDef.get_Alias() + "  的副本");
				
			}
			
		}else{
			multiQueryGridPartDef.set_GridAction(GridAction.Default);
//			text_XML.setText(getXmlString());
		}
		text_XML.setText(multiQueryGridPartDef.ToDetailXml());
		
	}
	
	
	
	
	private String getXmlString(){
		
		XmlDocument document = new XmlDocument();
		String detail = multiQueryGridPartDef.ToDetailXml();
		document.LoadXml(multiQueryGridPartDef.ToDetailXml());
		XmlElement elementAsElement = XmlUtils.GetElementAsElement(document.get_DocumentElement(), "MultiQueryGridPartDetails");
		if (elementAsElement == null)
        {
            return document.CreateElement("MultiQueryGridPartDetails").get_OuterXml();
        }
		String s = elementAsElement.get_OuterXml();
		
		
        return elementAsElement.get_OuterXml();
		
//		String xmls = "<MultiQueryGridPartDetails>" +
//				"<BusObDef ID=\"\" Name=\"\" />" +
//				"<GridDef ID=\"\" Name=\"\" />" +
//				"<DateRangeList AllowDateEntry=\"TRUE\" PrecedingFiscalQuarters=\"0\" SucceedingFiscalQuarters=\"0\"" +
//				" PrecedingFiscalYears=\"0\" SucceedingFiscalYears=\"0\" />" +
//				"<GroupBy Field=\"\" Caption=\"\" />" +
//				"<ColumnDataList />" +
//				"</MultiQueryGridPartDetails>";
//		return xmls;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "确定", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "取消", false);
	}

	@Override
	protected void okPressed() {
		
		if(general.savavalidation(text_Name, text_Alias, type)){
			
			multiQueryGridPartDef.FromDetailXml(null, text_XML.getText().trim());
			
//			if ("New".equals(type)) {
//				m_Library.UpdateDefinition(multiQueryGridPartDef, true);
//			} else if ("Edit".equals(type)) {
//				m_Library.UpdateDefinition(multiQueryGridPartDef, false);
//			} else if("Copy".equals(type)){
//				multiQueryGridPartDef.set_Id(m_Def.get_Id());
//				m_Library.UpdateDefinition(multiQueryGridPartDef, true);
//			}
			super.okPressed();
		}
		
	}
	
	
}
