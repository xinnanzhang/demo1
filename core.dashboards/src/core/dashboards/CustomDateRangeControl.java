package core.dashboards;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;

import siteview.windows.forms.ImageHelper;
import system.EventHandler;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;



public class CustomDateRangeControl extends Composite{
	private Composite components;

    private Button m_btnGo;

    private DateTime m_dtpEnd;

    private DateTime m_dtpStart;

    private Label m_lblEndDateRange;

    private Label m_lblStartDateRange;

    /* TODO: Event Declare */
    public  ArrayList<EventHandler> GoButtonClicked =new ArrayList<EventHandler>();
    
    
    public CustomDateRangeControl(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new FillLayout(SWT.HORIZONTAL));
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(5, false));
		
		m_lblStartDateRange = new Label(composite, SWT.NONE);
		String str_start = Res.get_Default().GetString("m_lblStartDateRange.Text");
		m_lblStartDateRange.setText("\u5F00\u59CB\u65F6\u95F4:");
		
		m_dtpStart = new DateTime(composite, SWT.BORDER|SWT.DROP_DOWN);
		GridData gd_m_dtpStart = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_m_dtpStart.widthHint = 108;
		m_dtpStart.setLayoutData(gd_m_dtpStart);
		
		m_lblEndDateRange = new Label(composite, SWT.NONE);
		String str_end = Res.get_Default().GetString("m_lblEndDateRange.Text");
		m_lblEndDateRange.setText("\u7ED3\u675F\u65F6\u95F4:");
		
		m_dtpEnd = new DateTime(composite, SWT.BORDER|SWT.DROP_DOWN);
		GridData gd_m_dtpEnd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_m_dtpEnd.widthHint = 108;
		m_dtpEnd.setLayoutData(gd_m_dtpEnd);
		
		m_btnGo = new Button(composite, SWT.NONE);
		m_btnGo.setImage(ImageHelper.LoadImage(Activator.PLUGIN_ID, "img/Find.png"));
		// TODO Auto-generated constructor stub
	}
    
    public system.DateTime get_EndDateTime(){

    	system.DateTime dt = new system.DateTime();
    	dt.__Ctor__(m_dtpEnd.getYear(), m_dtpEnd.getMonth()+1, m_dtpEnd.getDay(), m_dtpEnd.getHours(), m_dtpEnd.getMinutes(), m_dtpEnd.getSeconds());
        return dt;
    }
    public void set_EndDateTime(system.DateTime value){
        this.m_dtpEnd.setDate(value.get_Year(), value.get_Month()-1, value.get_Day());
        this.m_dtpEnd.setTime(value.get_Hour(), value.get_Minute(), value.get_Second());
     }

    public system.DateTime get_StartDateTime(){
    	system.DateTime dt = new system.DateTime();
    	dt.__Ctor__(m_dtpStart.getYear(), m_dtpStart.getMonth()+1, m_dtpStart.getDay(), m_dtpStart.getHours(), m_dtpStart.getMinutes(), m_dtpStart.getSeconds());
    	
        return dt;
    }
    public void set_StartDateTime(system.DateTime value){
        this.m_dtpStart.setDate(value.get_Year(), value.get_Month()-1, value.get_Day());
        this.m_dtpStart.setTime(value.get_Hour(), value.get_Minute(), value.get_Second());
    }

	public Button get_btnGo() {
		return m_btnGo;
	}

	public DateTime get_dtpEnd() {
		return m_dtpEnd;
	}

	public DateTime get_dtpStart() {
		return m_dtpStart;
	}

	public Label get_EndDateRange() {
		return m_lblEndDateRange;
	}

	public Label get_StartDateRange() {
		return m_lblStartDateRange;
	}

	public void set_btnGo(Button m_btnGo) {
		this.m_btnGo = m_btnGo;
	}

	public void set_dtpEnd(DateTime m_dtpEnd) {
		this.m_dtpEnd = m_dtpEnd;
	}

	public void set_dtpStart(DateTime m_dtpStart) {
		this.m_dtpStart = m_dtpStart;
	}

	public void set_EndDateRange(Label m_lblEndDateRange) {
		this.m_lblEndDateRange = m_lblEndDateRange;
	}

	public void set_StartDateRange(Label m_lblStartDateRange) {
		this.m_lblStartDateRange = m_lblStartDateRange;
	}
    
    
}