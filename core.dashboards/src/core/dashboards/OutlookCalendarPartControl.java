package core.dashboards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;

import Core.Dashboards.DashboardDef;
import Core.Dashboards.DashboardPartDef;
import Core.Dashboards.ImagePartDef;
import Core.Dashboards.PartRefDef;
import Siteview.Api.ISiteviewApi;
import Siteview.Windows.Forms.ImageHolder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.GridData;

import siteview.windows.forms.ImageResolver;
import siteview.windows.forms.SwtImageConverter;

/**
 *	������
 * @author zhangxinnan
 *
 */
public class OutlookCalendarPartControl extends DashboardPartControl {
	/** ��ʾ���� */
    private Display display = null;
    /**Clabel��С�Ŀ��*/
    private static final int GRIDDATAHINT = 20;//���ڵĿ��
    
    /** ��һ�� */
    private static final String topYear = "��һ��";
    /** ��һ�� */
    private static final String nextYear = "��һ��";
    /** ��һ�� */
    private static final String topMonth = "��һ��";
    /** ��һ�� */
    private static final String nextMonth = "��һ��";
    /** ���� */
    private static final String toDays = "����";
    
    /** ������ ���� ������ */
    private static final String[] weekdays = { "������", "����һ", "���ڶ�", "������","������", "������", "������" };
    /**�·ݣ�1 ���� 12 */
    private static final String[] months = { "1", "2", "3", "4","5", "6", "7", "8", "9", "10","11", "12",};
    
    /**���������*/
    Combo cbo_year;
    /**����������ʾ����ǰ���ǰ�������*/
    private static final int aroundYear = 20;
    /**���������ѡ�������*/
    private static final String[] years = new String[aroundYear*2+1];
    /**�µ�������*/
    Combo cbo_month;
    
    /**��ʾ�յ�Clable����*/
    private final CLabel[] clbl_days = new CLabel[42]; // �����6 * 7
    private final Text[] textDays = new Text[42];//6 * 7 ��ʾ��������� �ı���
    
    /** ��������ı�����ɫ*/
    private Color COLOR_SHELL_BACKGROUND = null;

    /** ����X��ǩ�ı�����ɫ*/
    private Color COLOR_CLBLWEEKDAY_BACKGROUND = null;

    /** ��ɫ���õ���Ϊϵͳ����ɫ������Ҫ���������Դ�ͷţ�*/
    private Color COLOR_SYSTEM_WHITE = null;

    /** ��ɫ���õ���Ϊϵͳ����ɫ������Ҫ���������Դ�ͷţ�*/
    private Color COLOR_SYSTEM_BLUE = null;
    /** ��¼�����Ѿ��е���ɫ  �ϴ�ѡ�е���һ��*/
    private CLabel oldPitchOnDay;
    /** ��¼������ѡ�����ɫ   ����ѡ�е���һ�� */
    private CLabel newPitchOnDay;
    /**ѡ������ڣ�yyyy-MM-dd*/ 
    private String selectedDate="";
    /** ��ʽ������*/
    SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy��MM��dd��");
    
    /**�����������Ϣ*/
    int nowYear,nowMonth;
	
    private StackLayout stacklayout;
    
	public OutlookCalendarPartControl(DashboardControl parent, int style) {
		super(parent, style);
		
	}
	
	private void showPanel(Composite p){
		// TODO Auto-generated method stub
    	this.stacklayout.topControl = p;
    	this.get_MainArea().layout();
    }
	
	@Override
	protected void DefineFromDef(DashboardPartDef def, PartRefDef partRefDef) {
        super.DefineFromDef(def, partRefDef);
        Composite composite = new Composite(this.get_MainArea(), SWT.NONE);
        
	    display = composite.getDisplay();
	    COLOR_SHELL_BACKGROUND = new Color(display,219, 235, 250);
	    COLOR_CLBLWEEKDAY_BACKGROUND = new Color(display, 64, 128, 128);
	    COLOR_SYSTEM_WHITE = display.getSystemColor(SWT.COLOR_WHITE);
	    COLOR_SYSTEM_BLUE = display.getSystemColor(SWT.COLOR_BLUE);        
        
		//���ò��ַ�ʽ    parent�������ַ�ʽΪGridLayout ���ַ�ʽ����Ϊ7��
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 7;
	    gridLayout.makeColumnsEqualWidth = true;
	    composite.setLayout(gridLayout);
	    composite.setBackground(COLOR_SHELL_BACKGROUND);
	
	    //���������
	    cbo_year = new Combo(composite, SWT.DROP_DOWN);
	    cbo_year.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    cbo_year.addSelectionListener(cboSelectionListener);
	    cbo_year.addKeyListener(cboKeyListener);
	    
		//�µ�������
	    cbo_month = new Combo(composite, SWT.DROP_DOWN);
	    cbo_month.setItems(months);
	    cbo_month.select(Calendar.getInstance().get(Calendar.MONTH));//��õ�ǰ�²�����
	    cbo_month.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));//���ַ�ʽ
	    cbo_month.addSelectionListener(cboSelectionListener);
	    cbo_month.addKeyListener(cboKeyListener);
	    
	    //����
	    Button today = new Button(composite, SWT.CENTER);
	    GridData gd_clabel = new GridData(SWT.FILL,SWT.CENTER,true,false);
	    gd_clabel.heightHint = 21;
	    today.setLayoutData(gd_clabel);
	    today.setBackground(COLOR_SYSTEM_WHITE);
	    today.setText("����");
	    today.setToolTipText(toDays);
	    today.addSelectionListener(btnSelectionListener);
	    
	    // ��һ��
	    Button btn_topYear = new Button(composite, SWT.ARROW | SWT.UP);
	    	   btn_topYear.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_topYear.setToolTipText(topYear);
	    	   btn_topYear.addSelectionListener(btnSelectionListener);
	    
	    // ��һ��
	    Button btn_topMonth = new Button(composite, SWT.ARROW | SWT.LEFT);
	    	   btn_topMonth.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_topMonth.setToolTipText(topMonth);
	    	   btn_topMonth.addSelectionListener(btnSelectionListener);
	    
	    // ��һ��
	    Button btn_nextMonth = new Button(composite, SWT.ARROW | SWT.RIGHT);
	    	   btn_nextMonth.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_nextMonth.setToolTipText(nextMonth);
	    	   btn_nextMonth.addSelectionListener(btnSelectionListener);
	    
	    // ��һ��
	    Button btn_nextYear = new Button(composite, SWT.ARROW | SWT.DOWN);
	    	   btn_nextYear.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_nextYear.setToolTipText(nextYear);
	    	   btn_nextYear.addSelectionListener(btnSelectionListener);
	    
	    GridData gridData_1 = null;
	    // �� ������ ~������ �ı�ǩ��ʾ����
	    for (int i = 0; i < weekdays.length; i++) {
	        CLabel clbl_weekDay = new CLabel(composite, SWT.CENTER | SWT.SHADOW_OUT);
	        clbl_weekDay.setForeground(COLOR_SYSTEM_WHITE);
	        clbl_weekDay.setBackground(COLOR_CLBLWEEKDAY_BACKGROUND);
	        gridData_1 = new GridData(SWT.FILL,SWT.CENTER,true,false);
	        gridData_1.widthHint = GRIDDATAHINT;//���ڵĿ��Ϊ�����С
	        gridData_1.heightHint = GRIDDATAHINT;
	        clbl_weekDay.setLayoutData(gridData_1);
	        clbl_weekDay.setText(weekdays[i]);//������ѭ������
	    }
	    
	    // �����µ����� �� �ı�ǩ��ʾ����
	    for (int i = 0; i < clbl_days.length; i++) {
	    	clbl_days[i] = new CLabel(composite, SWT.FLAT | SWT.CENTER);
	        clbl_days[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
	        clbl_days[i].setCursor(display.getSystemCursor(SWT.CURSOR_HAND));//��ù��
	        clbl_days[i].addMouseListener(clblMouseListener);
	        textDays[i] = new Text(clbl_days[i], SWT.NONE);
	    }
	    displayClblDays(true);
                showPanel(composite);
    }
	
	public OutlookCalendarPartControl(ISiteviewApi iSiteviewApi,
			DashboardControl parent) {
		super(iSiteviewApi,parent,SWT.NONE);
		stacklayout = new StackLayout(); 
		this.get_MainArea().setLayout(stacklayout);
	}
	
	/**
     * �������������������ѡ��
     * @param middleYear �м����
     */
    private void setCboYearItems(int middleYear){
    	
        int selectIndex = aroundYear;
        if(middleYear < aroundYear){//ȷ�������ָ������
            selectIndex = middleYear;
            middleYear =  aroundYear;
        }
        int index = 0;
        for (int i = middleYear-aroundYear; i <= middleYear+aroundYear; i++) {
            years[index++] = ""+i;
        }
        cbo_year.setItems(years);//ѡ�Χ �����ǰ���ʮ��
        cbo_year.select(selectIndex);
    }
        
    /** 
     * �õ�ָ�����µ�����
     * 
     * @param year
     *            ��
     * @param month
     *            ��(1-12)
     * @return ָ�����µ��������磺year=2008,month=1 �ͷ��� 2008��1�µ�������31
     */ 
    private int getDayCountOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // ��Ϊ Calendar�е� month �� 0-11����monthҪ��ȥ1
        Calendar cal2 = (Calendar) cal.clone();
        cal2.add(Calendar.MONTH, 1);
        cal2.add(Calendar.DAY_OF_MONTH, -1);
        return cal2.get(Calendar.DAY_OF_MONTH);
    }       

    /**
     * �õ��������е������
     */
    private int[] getYearAndMonth(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR); //��
        int month = now.get(Calendar.MONTH)+1; //��
        //��¼�������µ���Ϣ
        nowYear = year;
        nowMonth = month;
        if("".equals(cbo_year.getText().trim()))
            cbo_year.setText(year+"");
        else{
            try {//�õ���
                year = Integer.parseInt(cbo_year.getText().trim());
            } catch (NumberFormatException e) {
                //�� �������е��ı�����һ��Int�����֣�����Ϊ��ǰ��
                cbo_year.setText(year+"");
            }
        }

        if("".equals(cbo_month.getText().trim()))
            cbo_month.setText(month+"");
        else{
            try {//�õ���
                month = Integer.parseInt(cbo_month.getText().trim());
                if(month<1){
                    month = 1;
                    cbo_month.setText("1");
                }else if(month>12){
                    month = 12;
                    cbo_month.setText("12");
                }
            } catch (NumberFormatException e) {
                cbo_month.setText(month+"");//�� �������е��ı�����һ��Int�����֣�����Ϊ��ǰ��
            }
        }
        return new int[]{year, month};
    }

    /** 
     * Ϊ���е� �� ��ǩ����������� 
     * @param reflushCboYearItems �Ƿ�ˢ�����������е�ѡ�� 
     */ 
    private void displayClblDays(boolean reflushCboYearItems){
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DATE); // ����
        int[] yearAndMonth = getYearAndMonth();
        int year = yearAndMonth[0]; //��
        int month = yearAndMonth[1]; //��
        calendar.set(year,month-1,1); //Calendar�е�monthΪ0-11�����ڴ˴�monthҪ��ȥ1
        int beginIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; //�õ�ָ���·ݵĵ�һ��Ϊ���ڼ���Calendar���󷵻ص�1-7��Ӧ������-���������ʼ�ȥ1
        int endIndex = beginIndex + getDayCountOfMonth(year, month) - 1;
        if(reflushCboYearItems)
            setCboYearItems(year);
        int day=1;
        for (int i = 0; i < clbl_days.length; i++) {
            if (i >= beginIndex && i <= endIndex) {//���Ϊ�����ڵ�    ���߲��ǿհ׵ĵط� ������ʾ���ڼ���ʾ��Ϣ
            	try {
	                calendar.setTime(chineseDateFormat.parse(""+year+"��"+month+"��"+day+"��"));//�����Զ�������
	                Lunar lunar = new Lunar(calendar);
	                clbl_days[i].setText(day + "");//������ʾ���� getSCalendar year + "-" + month+"-"+day
	                clbl_days[i].setToolTipText(year+"-"+month+"-"+day+"\n"+animalsYear(year)+"��\n"+lunar.toString());//��ʾ��Ϣ
	                if (day == currentDate){//���Ϊ���գ��ѱ�����ɫ����Ϊ��ɫ
	                    clbl_days[i].setBackground(COLOR_SYSTEM_BLUE);
	                    oldPitchOnDay = clbl_days[i];
	                    newPitchOnDay = clbl_days[i];
	                }else//��������ȫ����ɫ
	                    clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);
	                day++;
            	}catch (Exception e) {
				}
            } else {//������հ׵ط� ��������Ϣ����ʾ��Ϣ
               clbl_days[i].setText("");
               clbl_days[i].setToolTipText("");
               clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);
            }
        }
        int[] to = {year,month};
    }
    //����year��ֵ����Ф
    public String animalsYear(int year){
     final String[] Animals = new String[]{"��", "ţ", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��"};
           return Animals[(year - 4) % 12];
    }
    
    /**
     * �����¼�
     */
    private MouseListener clblMouseListener = new MouseAdapter() {
        @Override 
        public void mouseDown(MouseEvent e) {
            selectedDate = ((CLabel) e.widget).getToolTipText();
            if (!"".equals(selectedDate)){
            	oldPitchOnDay = newPitchOnDay;//�Ƚ��и�ֵ
            	newPitchOnDay = (CLabel) e.widget;//�ڻ��ѡ��� CLabel
            	if(oldPitchOnDay == newPitchOnDay){
            		newPitchOnDay.setBackground(COLOR_SYSTEM_BLUE);//���ѡ�е� CLabel �;ɵ���ɫһ���Ļ� ��ɫ����Ϊ ��ɫ
            	}else{
            		oldPitchOnDay.setBackground(COLOR_SYSTEM_WHITE);//�����һ���Ļ� �Ѿɵ���Ϊ��ɫ
            		newPitchOnDay.setBackground(COLOR_SYSTEM_BLUE);//���µ�����Ϊ ��ɫ
            	}
            }
        }
    };

    /**  ��ť�������Ҽ���*/
    private SelectionListener btnSelectionListener = new SelectionAdapter() {
        @Override
		public void widgetSelected(SelectionEvent e) {
            String tooptip = ((Button)e.widget).getToolTipText();
            int[] yearAndMonth = getYearAndMonth();
            int year = yearAndMonth[0];
            int month = yearAndMonth[1];
            boolean reflushCboyearItems = true;
            //����
            if (toDays.equals(tooptip)) {
				cbo_year.setText(nowYear+"");
				cbo_month.select(nowMonth-1);
			}
            //ǰһ��
            if(topYear.equals(tooptip))
                cbo_year.setText((year-1)+"" );
            //��һ��
            else if(nextYear.equals(tooptip))
                cbo_year.setText((year+1)+"" );
            //ǰһ��
            else if(topMonth.equals(tooptip)){
                if(month == 1){//���ѡ��һ�¼���ѡǰһ�µĻ�����ô��һ�ꡣ��������Ϊ12��(��Ϊ��0-11)11Ҳ����12��
                    setCboYearItems(year-1 );
                    cbo_month.select(11);
                }else{
                    cbo_month.select(month-2);
                    reflushCboyearItems = false;
                }
                
            }//��һ��
            else if(nextMonth.equals(tooptip)){
                if(month == 12){
                    setCboYearItems(year+1 );
                    cbo_month.select(0);
                }else{
                    cbo_month.select(month);
                    reflushCboyearItems =  false;
                }
            }else {
				reflushCboyearItems =  false;
			}
            displayClblDays(reflushCboyearItems);
        }
    };
    
    /**  
     * ���������ÿ�ζ���ˢ��
     */
    private SelectionListener cboSelectionListener = new SelectionAdapter() {
        @Override
		public void widgetSelected(SelectionEvent e) {
            boolean reflushCboyearItems = e.widget == cbo_year ? true : false;
            displayClblDays(reflushCboyearItems);
        }
    };
    
    /**
     * ���̼����¼�
     */
    private KeyListener cboKeyListener = new KeyAdapter(){
        @Override
        public void keyPressed(KeyEvent event) {
            //С���̵�Enter��Enter,��ʾ����
            if (event.keyCode == 16777296 || event.keyCode == 13) {
                boolean reflushCboyearItems = event.widget == cbo_year ? true : false;
                displayClblDays(reflushCboyearItems);
            }else{
                //ֻ����������
                if((event.keyCode<'0' || event.keyCode >'9') && !(event.keyCode == SWT.BS || event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN))
                    event.doit = false;
            }
        }
    };
	

	/**
	 * �ڲ���
	 * ����ũ������
	 * @author lenovo
	 *
	 */
	class Lunar {
	    private int year;
	    private int month;
	    private int day;
	    private boolean leap;
	    final String chineseNumber[] = {"��", "��", "��", "��", "��", "��", "��", "��", "��", "ʮ", "ʮһ", "��"};
	    final String chineseNumber1[] = {"һ", "��", "��", "��", "��", "��", "��", "��", "��", "ʮ", "ʮһ", "ʮ��"};
	    SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy��MM��dd��");
	    final long[] lunarInfo = new long[]
	    {0x04bd8, 0x04ae0, 0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
	     0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2, 0x095b0, 0x14977,
	     0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
	     0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7, 0x0c950,
	     0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557,
	     0x06ca0, 0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
	     0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0,
	     0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
	     0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46, 0x0ab60, 0x09570,
	     0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
	     0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0, 0x0cab5,
	     0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930,
	     0x07954, 0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
	     0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45,
	     0x0b5a0, 0x056d0, 0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0};
	//====== ����ũ�� y���������
	    final private int yearDays(int y) {
	        int i, sum = 348;
	        for (i = 0x8000; i > 0x8; i >>= 1) {
	            if ((lunarInfo[y - 1900] & i) != 0) sum += 1;
	        }
	        return (sum + leapDays(y));
	    }
	//====== ����ũ�� y�����µ�����
	    final private int leapDays(int y) {
	        if (leapMonth(y) != 0) {
	            if ((lunarInfo[y - 1900] & 0x10000) != 0)
	                return 30;
	            else
	                return 29;
	        } else
	            return 0;
	    }
	//====== ����ũ�� y�����ĸ��� 1-12 , û�򴫻� 0
	    final private int leapMonth(int y) {
	        return (int) (lunarInfo[y - 1900] & 0xf);
	    }
	//====== ����ũ�� y��m�µ�������
	    final private int monthDays(int y, int m) {
	        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
	            return 29;
	        else
	            return 30;
	    }
	//====== ����ũ�� y�����Ф
	    final public String animalsYear(int year) {
	        final String[] Animals = new String[]{"��", "ţ", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��"};
	        return Animals[(year - 4) % 12];
	    }
	//====== ���� ���յ�offset ���ظ�֧, 0=����
	    final private String cyclicalm(int num) {
	        final String[] Gan = new String[]{"��", "��", "��", "��", "��", "��", "��", "��", "��", "��"};
	        final String[] Zhi = new String[]{"��", "��", "��", "î", "��", "��", "��", "δ", "��", "��", "��", "��"};
	        return (Gan[num % 10] + Zhi[num % 12]);
	    }
	//====== ���� offset ���ظ�֧, 0=����
	    final public String cyclical(int year) {
	        int num = year - 1900 + 36;
	        return (cyclicalm(num));
	    }
	    /**
	     * ����y��m��d�ն�Ӧ��ũ��.
	     * yearCyl3:ũ������1864������� ?
	     * monCyl4:��1900��1��31������,������
	     * dayCyl5:��1900��1��31����������,�ټ�40 ?
	     *
	     * @param cal
	     * @return
	     */
	    public Lunar(Calendar cal) {
	        int yearCyl, monCyl, dayCyl;
	        int leapMonth = 0;
	        Date baseDate = null;
	        try {
	            baseDate = chineseDateFormat.parse("1900��1��31��");
	        } catch (ParseException e) {
	            e.printStackTrace(); //To change body of catch statement use Options | File Templates.
	        }
	//�����1900��1��31����������
	        int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);
	        dayCyl = offset + 40;
	        monCyl = 14;
	//��offset��ȥÿũ���������
	// ���㵱����ũ���ڼ���
	//i���ս����ũ�������
	//offset�ǵ���ĵڼ���
	        int iYear, daysOfYear = 0;
	        for (iYear = 1900; iYear < 2050 && offset > 0; iYear++) {
	            daysOfYear = yearDays(iYear);
	            offset -= daysOfYear;
	            monCyl += 12;
	        }
	        if (offset < 0) {
	            offset += daysOfYear;
	            iYear--;
	            monCyl -= 12;
	        }
	//ũ�����
	        year = iYear;
	        yearCyl = iYear - 1864;
	        leapMonth = leapMonth(iYear); //���ĸ���,1-12
	        leap = false;
	//�õ��������offset,�����ȥÿ�£�ũ��������������������Ǳ��µĵڼ���
	        int iMonth, daysOfMonth = 0;
	        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
	//����
	            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
	                --iMonth;
	                leap = true;
	                daysOfMonth = leapDays(year);
	            } else
	                daysOfMonth = monthDays(year, iMonth);
	            offset -= daysOfMonth;
	//�������
	            if (leap && iMonth == (leapMonth + 1)) leap = false;
	            if (!leap) monCyl++;
	        }
	//offsetΪ0ʱ�����Ҹղż�����·������£�ҪУ��
	        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
	            if (leap) {
	                leap = false;
	            } else {
	                leap = true;
	                --iMonth;
	                --monCyl;
	            }
	        }
	//offsetС��0ʱ��ҲҪУ��
	        if (offset < 0) {
	            offset += daysOfMonth;
	            --iMonth;
	            --monCyl;
	        }
	        month = iMonth;
	        day = offset + 1;
	    }
	    public String getChinaDayString(int day) {
	        String chineseTen[] = {"��", "ʮ", "إ", "ئ"};
	        int n = day % 10 == 0 ? 9 : day % 10 - 1;
	        if (day > 30)
	            return "";
	        if (day == 10)
	            return "��ʮ";
	        else
	            return chineseTen[day / 10] + chineseNumber1[n];
	    }
	    @Override
		public String toString() {
	        return (leap ? "��" : "") + chineseNumber[month - 1] + "��" + getChinaDayString(day);
	    }
	    public String getChinaWeekdayString(String weekday){
	     if(weekday.equals("Mon"))
	      return "һ";
	     if(weekday.equals("Tue"))
	      return "��";
	     if(weekday.equals("Wed"))
	      return "��";
	     if(weekday.equals("Thu"))
	      return "��";
	     if(weekday.equals("Fri"))
	      return "��";
	     if(weekday.equals("Sat"))
	      return "��";
	     if(weekday.equals("Sun"))
	      return "��";
	     else
	      return "";
	    }
	}
}
