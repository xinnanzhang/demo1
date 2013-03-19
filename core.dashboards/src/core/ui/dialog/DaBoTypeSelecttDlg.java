package core.ui.dialog;


import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import Core.Dashboards.DashboardPartDef;
import Siteview.Api.DefinitionLibrary;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ScopeUtil;
import core.ui.wizard.DiagramWizard;

public class DaBoTypeSelecttDlg extends Dialog{
	
	private Button selectedButton ;
	private String type;	//��������----1.�½� 2.�༭ 3.����
	private String linkTo;  //��������ʽ ѡ ��
	private HashSet<String> Categoryset = new HashSet<String>(); //����ȫ�����ļ���
	private DashboardPartDef dashboardPartDef;
	private ScopeUtil m_ScopeUtil;
	private DefinitionLibrary m_Library;
	private ISiteviewApi m_Api;
	
	private String selectText="";
	
	/**
	 * @wbp.parser.constructor
	 */
	public DaBoTypeSelecttDlg(Shell parentShell, DashboardPartDef dashboardPartDef, ScopeUtil m_ScopeUtil, String linkTo, HashSet<String> Categoryset, String type) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.dashboardPartDef = dashboardPartDef;
		this.m_ScopeUtil = m_ScopeUtil;
		this.linkTo = linkTo;
		this.Categoryset = Categoryset;
		this.type = type;
		// TODO Auto-generated constructor stub
	}
	
	public DaBoTypeSelecttDlg(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.APPLICATION_MODAL);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void configureShell(Shell newShell) {
		newShell.setSize(300, 400);
		newShell.setLocation(500, 150);
		newShell.setText("�Ǳ��̲�������");
		super.configureShell(newShell);
	}
	

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(null);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setBounds(41, 21, 78, 16);
		lblNewLabel.setText("\u9009\u62E9\u90E8\u4EF6\u7C7B\u578B:");
		
		
		//Outlook����
		Button btnRadioButton = new Button(container, SWT.RADIO);
		btnRadioButton.setBounds(31, 54, 103, 16);
		selectedButton = btnRadioButton;
		btnRadioButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		btnRadioButton.setText("Outlook\u65E5\u5386");
		btnRadioButton.setSelection(true);
		btnRadioButton.setData("OutlookCalendar");
		
		//Web�����
		Button btnWeb = new Button(container, SWT.RADIO);
		btnWeb.setBounds(31, 86, 103, 16);
		btnWeb.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		btnWeb.setText("Web\u6D4F\u89C8\u5668");
		btnWeb.setData("WebBrowser");
		
		
		//���������
		Button button_1 = new Button(container, SWT.RADIO);
		button_1.setEnabled(false);
		button_1.setBounds(31, 118, 103, 16);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_1.setText("\u5BF9\u8C61\u6D4F\u89C8\u5668");
		button_1.setData("ObjectBrowser");
		
		//����ͼ����
		Button button_2 = new Button(container, SWT.RADIO);
		button_2.setBounds(31, 154, 103, 16);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_2.setData("MultiViewGrid");
		
		
		
		
		
		button_2.setText("\u591A\u89C6\u56FE\u7F51\u683C");
		
		//ͼ��
		Button button_3 = new Button(container, SWT.RADIO);
		button_3.setBounds(31, 190, 103, 16);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_3.setText("\u56FE\u8868");
		button_3.setData("Chart");
		
		//΢����Ŀ
		Button button_4 = new Button(container, SWT.RADIO);
		button_4.setBounds(31, 228, 103, 16);
		button_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_4.setText("ҵ�������");
		button_4.setData("BusinessObject");
		//button_4.setText("\u5FAE\u8F6F\u9879\u76EE");
		//button_4.setData("MSProject");
		
		//Outlook �ռ���
		Button btnOutlook = new Button(container, SWT.RADIO);
		btnOutlook.setBounds(140, 54, 110, 16);
		btnOutlook.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		btnOutlook.setText("Outlook\u6536\u4EF6\u7BB1");
		btnOutlook.setData("OutlookInbox");
		
		
		//�����ϵ����ͼ
		Button button_6 = new Button(container, SWT.RADIO);
//		button_6.setEnabled(false);
		button_6.setBounds(140, 86, 110, 16);
		button_6.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_6.setText("\u5BF9\u8C61\u5173\u7CFB\u62D3\u6251\u56FE");
		button_6.setData("ObjectTopologicalDiagram");
		
		
		//�๦�ܲ�ѯ����
		Button button_7 = new Button(container, SWT.RADIO);
		button_7.setBounds(140, 118, 110, 16);
		button_7.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_7.setText("\u591A\u529F\u80FD\u67E5\u8BE2\u7F51\u683C");
		button_7.setData("MultiQueryGrid");
		
		
		//�����б�
		Button button_8 = new Button(container, SWT.RADIO);
		button_8.setBounds(140, 154, 99, 16);
		button_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_8.setText("\u94FE\u63A5\u5217\u8868");
		button_8.setData("LinkList");
		
		//ͼ�� 
		Button button_9 = new Button(container, SWT.RADIO);
		button_9.setBounds(140, 190, 86, 16);
		button_9.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_9.setText("\u56FE\u50CF");
		button_9.setData("Image");
		
		//ѡ�������
		Button button_10 = new Button(container, SWT.RADIO);
		button_10.setBounds(140, 228, 105, 16);
		button_10.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_10.setText("\u9009\u9879\u5361\u5F0F\u90E8\u5206");
		button_10.setData("TabbedPart");
		
		
		//΢����Ŀ
		Button button_11 = new Button(container, SWT.RADIO);
		button_11.setBounds(31, 268, 103, 16);
		button_11.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedButton = (Button)e.getSource();
			}
		});
		button_11.setText("�Զ��岿��");
		button_11.setData("Custom");
		
		return container;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "ȷ��", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "ȡ��", false);
	}
	
	
	@Override
	protected void okPressed() {
		selectText= selectedButton.getData().toString();
		
		
//		if(selectedText.equals("Outlook����")){
//			newSelectedDlg = new OutlookCalendar(this.getShell(), dashboardPartDef, m_Library, m_ScopeUtil, m_Api, linkTo, Categoryset, type);
//		}else if(seleequals("���������")){
//			newSelectedDlg = new ObjectBrowser(this.getShell());
//		}else if(selectedText.equals("�๦�ܲ�ѯ����")){
//			newSelectedDlg = new UtilityGrid(this.getShell());
//		}else if(selectedText.equals("����ͼ����")){
//			newSelectedDlg = new ViewGrid(this.getShell());
//		}else if(selectedTexctedText.equals("Outlook�ռ���")){
//			newSelectedDlg = new OutlookInbox(this.getShell());
//		}else if(selectedText.equals("Web�����")){
//			newSelectedDlg = new WebBrowser(this.getShell());
//		}else if(selectedText.equals("�����ϵ����ͼ")){
//			newSelectedDlg = new ObjectRelationTopo(this.getShell());
//		}else if(selectedText.t.equals("�����б�")){
//			newSelectedDlg = new LinkGrid(this.getShell());
//		}else if(selectedText.equals("ͼ��")){
//			openWizard();
//		}else if(selectedText.equals("ͼ��")){
//			newSelectedDlg = new ImageDlg(this.getShell());
//		}else if(selectedText.equals("΢����Ŀ")){
//			newSelectedDlg = new SoftItem(this.getShell());
//		}else if(selectedText.equals("ѡ�ʽ����")){
//			newSelectedDlg = new MotionsPart(this.getShell());
//		}else{
//			//error
//		}
		super.okPressed();
	
	}
	
	private void openWizard(){
		DiagramWizard dw = new DiagramWizard();
		WizardDialog wd = new WizardDialog(getShell(),dw);
		wd.open();
	}

	public String getSelectText() {
		return selectText;
	}

	public void setSelectText(String selectText) {
		this.selectText = selectText;
	}
}
