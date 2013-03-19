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
 *	日历类
 * @author zhangxinnan
 *
 */
public class OutlookCalendarPartControl extends DashboardPartControl {
	/** 显示对象 */
    private Display display = null;
    /**Clabel大小的宽度*/
    private static final int GRIDDATAHINT = 20;//星期的宽度
    
    /** 上一年 */
    private static final String topYear = "上一年";
    /** 下一年 */
    private static final String nextYear = "下一年";
    /** 上一月 */
    private static final String topMonth = "上一月";
    /** 下一月 */
    private static final String nextMonth = "下一月";
    /** 今天 */
    private static final String toDays = "今天";
    
    /** 星期日 —— 星期六 */
    private static final String[] weekdays = { "星期日", "星期一", "星期二", "星期三","星期四", "星期五", "星期六" };
    /**月份：1 —— 12 */
    private static final String[] months = { "1", "2", "3", "4","5", "6", "7", "8", "9", "10","11", "12",};
    
    /**年的下拉框*/
    Combo cbo_year;
    /**年下拉框显示到当前年的前后多少年*/
    private static final int aroundYear = 20;
    /**年份下拉框选项的数组*/
    private static final String[] years = new String[aroundYear*2+1];
    /**月的下拉框*/
    Combo cbo_month;
    
    /**显示日的Clable数组*/
    private final CLabel[] clbl_days = new CLabel[42]; // 表格数6 * 7
    private final Text[] textDays = new Text[42];//6 * 7 显示日期上添加 文本框
    
    /** 日历窗体的背景颜色*/
    private Color COLOR_SHELL_BACKGROUND = null;

    /** 星期X标签的背景颜色*/
    private Color COLOR_CLBLWEEKDAY_BACKGROUND = null;

    /** 白色（得到的为系统的颜色，不需要对其进行资源释放）*/
    private Color COLOR_SYSTEM_WHITE = null;

    /** 蓝色（得到的为系统的颜色，不需要对其进行资源释放）*/
    private Color COLOR_SYSTEM_BLUE = null;
    /** 记录现在已经有的颜色  上次选中的那一天*/
    private CLabel oldPitchOnDay;
    /** 记录现在新选择的颜色   本次选中的那一天 */
    private CLabel newPitchOnDay;
    /**选择的日期：yyyy-MM-dd*/ 
    private String selectedDate="";
    /** 格式化日期*/
    SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    
    /**今天的年月信息*/
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
        
		//设置布局方式    parent容器布局方式为GridLayout 布局方式设置为7列
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 7;
	    gridLayout.makeColumnsEqualWidth = true;
	    composite.setLayout(gridLayout);
	    composite.setBackground(COLOR_SHELL_BACKGROUND);
	
	    //年的下拉框
	    cbo_year = new Combo(composite, SWT.DROP_DOWN);
	    cbo_year.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    cbo_year.addSelectionListener(cboSelectionListener);
	    cbo_year.addKeyListener(cboKeyListener);
	    
		//月的下拉框
	    cbo_month = new Combo(composite, SWT.DROP_DOWN);
	    cbo_month.setItems(months);
	    cbo_month.select(Calendar.getInstance().get(Calendar.MONTH));//获得当前月并设置
	    cbo_month.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));//布局方式
	    cbo_month.addSelectionListener(cboSelectionListener);
	    cbo_month.addKeyListener(cboKeyListener);
	    
	    //今天
	    Button today = new Button(composite, SWT.CENTER);
	    GridData gd_clabel = new GridData(SWT.FILL,SWT.CENTER,true,false);
	    gd_clabel.heightHint = 21;
	    today.setLayoutData(gd_clabel);
	    today.setBackground(COLOR_SYSTEM_WHITE);
	    today.setText("今天");
	    today.setToolTipText(toDays);
	    today.addSelectionListener(btnSelectionListener);
	    
	    // 上一年
	    Button btn_topYear = new Button(composite, SWT.ARROW | SWT.UP);
	    	   btn_topYear.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_topYear.setToolTipText(topYear);
	    	   btn_topYear.addSelectionListener(btnSelectionListener);
	    
	    // 上一月
	    Button btn_topMonth = new Button(composite, SWT.ARROW | SWT.LEFT);
	    	   btn_topMonth.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_topMonth.setToolTipText(topMonth);
	    	   btn_topMonth.addSelectionListener(btnSelectionListener);
	    
	    // 下一月
	    Button btn_nextMonth = new Button(composite, SWT.ARROW | SWT.RIGHT);
	    	   btn_nextMonth.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_nextMonth.setToolTipText(nextMonth);
	    	   btn_nextMonth.addSelectionListener(btnSelectionListener);
	    
	    // 下一年
	    Button btn_nextYear = new Button(composite, SWT.ARROW | SWT.DOWN);
	    	   btn_nextYear.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false));
	    	   btn_nextYear.setToolTipText(nextYear);
	    	   btn_nextYear.addSelectionListener(btnSelectionListener);
	    
	    GridData gridData_1 = null;
	    // 将 星期日 ~星期六 的标签显示出来
	    for (int i = 0; i < weekdays.length; i++) {
	        CLabel clbl_weekDay = new CLabel(composite, SWT.CENTER | SWT.SHADOW_OUT);
	        clbl_weekDay.setForeground(COLOR_SYSTEM_WHITE);
	        clbl_weekDay.setBackground(COLOR_CLBLWEEKDAY_BACKGROUND);
	        gridData_1 = new GridData(SWT.FILL,SWT.CENTER,true,false);
	        gridData_1.widthHint = GRIDDATAHINT;//星期的宽度为网格大小
	        gridData_1.heightHint = GRIDDATAHINT;
	        clbl_weekDay.setLayoutData(gridData_1);
	        clbl_weekDay.setText(weekdays[i]);//把星期循环出来
	    }
	    
	    // 将当月的所有 日 的标签显示出来
	    for (int i = 0; i < clbl_days.length; i++) {
	    	clbl_days[i] = new CLabel(composite, SWT.FLAT | SWT.CENTER);
	        clbl_days[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
	        clbl_days[i].setCursor(display.getSystemCursor(SWT.CURSOR_HAND));//获得光标
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
     * 给年的下拉框设置设置选项
     * @param middleYear 中间年份
     */
    private void setCboYearItems(int middleYear){
    	
        int selectIndex = aroundYear;
        if(middleYear < aroundYear){//确保不出现负的年份
            selectIndex = middleYear;
            middleYear =  aroundYear;
        }
        int index = 0;
        for (int i = middleYear-aroundYear; i <= middleYear+aroundYear; i++) {
            years[index++] = ""+i;
        }
        cbo_year.setItems(years);//选项范围 当年的前后二十年
        cbo_year.select(selectIndex);
    }
        
    /** 
     * 得到指定年月的天数
     * 
     * @param year
     *            年
     * @param month
     *            月(1-12)
     * @return 指定年月的天数，如：year=2008,month=1 就返回 2008年1月的天数：31
     */ 
    private int getDayCountOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // 因为 Calendar中的 month 是 0-11，故month要减去1
        Calendar cal2 = (Calendar) cal.clone();
        cal2.add(Calendar.MONTH, 1);
        cal2.add(Calendar.DAY_OF_MONTH, -1);
        return cal2.get(Calendar.DAY_OF_MONTH);
    }       

    /**
     * 得到下拉框中的年和月
     */
    private int[] getYearAndMonth(){
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR); //年
        int month = now.get(Calendar.MONTH)+1; //月
        //记录今天年月的信息
        nowYear = year;
        nowMonth = month;
        if("".equals(cbo_year.getText().trim()))
            cbo_year.setText(year+"");
        else{
            try {//得到年
                year = Integer.parseInt(cbo_year.getText().trim());
            } catch (NumberFormatException e) {
                //年 下拉框中的文本不是一个Int型数字，则设为当前年
                cbo_year.setText(year+"");
            }
        }

        if("".equals(cbo_month.getText().trim()))
            cbo_month.setText(month+"");
        else{
            try {//得到月
                month = Integer.parseInt(cbo_month.getText().trim());
                if(month<1){
                    month = 1;
                    cbo_month.setText("1");
                }else if(month>12){
                    month = 12;
                    cbo_month.setText("12");
                }
            } catch (NumberFormatException e) {
                cbo_month.setText(month+"");//月 下拉框中的文本不是一个Int型数字，则设为当前月
            }
        }
        return new int[]{year, month};
    }

    /** 
     * 为所有的 日 标签设置相关属性 
     * @param reflushCboYearItems 是否刷新年下拉框中的选项 
     */ 
    private void displayClblDays(boolean reflushCboYearItems){
        Calendar calendar = Calendar.getInstance();
        int currentDate = calendar.get(Calendar.DATE); // 当日
        int[] yearAndMonth = getYearAndMonth();
        int year = yearAndMonth[0]; //年
        int month = yearAndMonth[1]; //月
        calendar.set(year,month-1,1); //Calendar中的month为0-11，故在此处month要减去1
        int beginIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; //得到指定月份的第一天为星期几，Calendar对象返回的1-7对应星期日-星期六，故减去1
        int endIndex = beginIndex + getDayCountOfMonth(year, month) - 1;
        if(reflushCboYearItems)
            setCboYearItems(year);
        int day=1;
        for (int i = 0; i < clbl_days.length; i++) {
            if (i >= beginIndex && i <= endIndex) {//如果为有日期的    或者不是空白的地方 设置显示日期及提示信息
            	try {
	                calendar.setTime(chineseDateFormat.parse(""+year+"年"+month+"月"+day+"日"));//加载自定义日期
	                Lunar lunar = new Lunar(calendar);
	                clbl_days[i].setText(day + "");//设置显示日期 getSCalendar year + "-" + month+"-"+day
	                clbl_days[i].setToolTipText(year+"-"+month+"-"+day+"\n"+animalsYear(year)+"年\n"+lunar.toString());//提示信息
	                if (day == currentDate){//如果为当日，把背景颜色设置为蓝色
	                    clbl_days[i].setBackground(COLOR_SYSTEM_BLUE);
	                    oldPitchOnDay = clbl_days[i];
	                    newPitchOnDay = clbl_days[i];
	                }else//否则其他全部白色
	                    clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);
	                day++;
            	}catch (Exception e) {
				}
            } else {//否则其空白地方 不设置信息及提示信息
               clbl_days[i].setText("");
               clbl_days[i].setToolTipText("");
               clbl_days[i].setBackground(COLOR_SYSTEM_WHITE);
            }
        }
        int[] to = {year,month};
    }
    //根据year的值求生肖
    public String animalsYear(int year){
     final String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
           return Animals[(year - 4) % 12];
    }
    
    /**
     * 监听事件
     */
    private MouseListener clblMouseListener = new MouseAdapter() {
        @Override 
        public void mouseDown(MouseEvent e) {
            selectedDate = ((CLabel) e.widget).getToolTipText();
            if (!"".equals(selectedDate)){
            	oldPitchOnDay = newPitchOnDay;//先进行赋值
            	newPitchOnDay = (CLabel) e.widget;//在获得选择的 CLabel
            	if(oldPitchOnDay == newPitchOnDay){
            		newPitchOnDay.setBackground(COLOR_SYSTEM_BLUE);//如果选中的 CLabel 和旧的颜色一样的话 颜色设置为 蓝色
            	}else{
            		oldPitchOnDay.setBackground(COLOR_SYSTEM_WHITE);//如果不一样的话 把旧的设为白色
            		newPitchOnDay.setBackground(COLOR_SYSTEM_BLUE);//把新的设置为 蓝色
            	}
            }
        }
    };

    /**  按钮上下左右监听*/
    private SelectionListener btnSelectionListener = new SelectionAdapter() {
        @Override
		public void widgetSelected(SelectionEvent e) {
            String tooptip = ((Button)e.widget).getToolTipText();
            int[] yearAndMonth = getYearAndMonth();
            int year = yearAndMonth[0];
            int month = yearAndMonth[1];
            boolean reflushCboyearItems = true;
            //今天
            if (toDays.equals(tooptip)) {
				cbo_year.setText(nowYear+"");
				cbo_month.select(nowMonth-1);
			}
            //前一年
            if(topYear.equals(tooptip))
                cbo_year.setText((year-1)+"" );
            //后一年
            else if(nextYear.equals(tooptip))
                cbo_year.setText((year+1)+"" );
            //前一月
            else if(topMonth.equals(tooptip)){
                if(month == 1){//如果选择一月继续选前一月的话。那么退一年。并设置月为12月(因为是0-11)11也就是12了
                    setCboYearItems(year-1 );
                    cbo_month.select(11);
                }else{
                    cbo_month.select(month-2);
                    reflushCboyearItems = false;
                }
                
            }//下一月
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
     * 如果下拉框每次都有刷新
     */
    private SelectionListener cboSelectionListener = new SelectionAdapter() {
        @Override
		public void widgetSelected(SelectionEvent e) {
            boolean reflushCboyearItems = e.widget == cbo_year ? true : false;
            displayClblDays(reflushCboyearItems);
        }
    };
    
    /**
     * 键盘监听事件
     */
    private KeyListener cboKeyListener = new KeyAdapter(){
        @Override
        public void keyPressed(KeyEvent event) {
            //小键盘的Enter或Enter,显示日历
            if (event.keyCode == 16777296 || event.keyCode == 13) {
                boolean reflushCboyearItems = event.widget == cbo_year ? true : false;
                displayClblDays(reflushCboyearItems);
            }else{
                //只能输入数字
                if((event.keyCode<'0' || event.keyCode >'9') && !(event.keyCode == SWT.BS || event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN))
                    event.doit = false;
            }
        }
    };
	

	/**
	 * 内部类
	 * 调用农历方法
	 * @author lenovo
	 *
	 */
	class Lunar {
	    private int year;
	    private int month;
	    private int day;
	    private boolean leap;
	    final String chineseNumber[] = {"正", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "腊"};
	    final String chineseNumber1[] = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
	    SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
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
	//====== 传回农历 y年的总天数
	    final private int yearDays(int y) {
	        int i, sum = 348;
	        for (i = 0x8000; i > 0x8; i >>= 1) {
	            if ((lunarInfo[y - 1900] & i) != 0) sum += 1;
	        }
	        return (sum + leapDays(y));
	    }
	//====== 传回农历 y年闰月的天数
	    final private int leapDays(int y) {
	        if (leapMonth(y) != 0) {
	            if ((lunarInfo[y - 1900] & 0x10000) != 0)
	                return 30;
	            else
	                return 29;
	        } else
	            return 0;
	    }
	//====== 传回农历 y年闰哪个月 1-12 , 没闰传回 0
	    final private int leapMonth(int y) {
	        return (int) (lunarInfo[y - 1900] & 0xf);
	    }
	//====== 传回农历 y年m月的总天数
	    final private int monthDays(int y, int m) {
	        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
	            return 29;
	        else
	            return 30;
	    }
	//====== 传回农历 y年的生肖
	    final public String animalsYear(int year) {
	        final String[] Animals = new String[]{"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
	        return Animals[(year - 4) % 12];
	    }
	//====== 传入 月日的offset 传回干支, 0=甲子
	    final private String cyclicalm(int num) {
	        final String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
	        final String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
	        return (Gan[num % 10] + Zhi[num % 12]);
	    }
	//====== 传入 offset 传回干支, 0=甲子
	    final public String cyclical(int year) {
	        int num = year - 1900 + 36;
	        return (cyclicalm(num));
	    }
	    /**
	     * 传出y年m月d日对应的农历.
	     * yearCyl3:农历年与1864的相差数 ?
	     * monCyl4:从1900年1月31日以来,闰月数
	     * dayCyl5:与1900年1月31日相差的天数,再加40 ?
	     *
	     * @param cal
	     * @return
	     */
	    public Lunar(Calendar cal) {
	        int yearCyl, monCyl, dayCyl;
	        int leapMonth = 0;
	        Date baseDate = null;
	        try {
	            baseDate = chineseDateFormat.parse("1900年1月31日");
	        } catch (ParseException e) {
	            e.printStackTrace(); //To change body of catch statement use Options | File Templates.
	        }
	//求出和1900年1月31日相差的天数
	        int offset = (int) ((cal.getTime().getTime() - baseDate.getTime()) / 86400000L);
	        dayCyl = offset + 40;
	        monCyl = 14;
	//用offset减去每农历年的天数
	// 计算当天是农历第几天
	//i最终结果是农历的年份
	//offset是当年的第几天
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
	//农历年份
	        year = iYear;
	        yearCyl = iYear - 1864;
	        leapMonth = leapMonth(iYear); //闰哪个月,1-12
	        leap = false;
	//用当年的天数offset,逐个减去每月（农历）的天数，求出当天是本月的第几天
	        int iMonth, daysOfMonth = 0;
	        for (iMonth = 1; iMonth < 13 && offset > 0; iMonth++) {
	//闰月
	            if (leapMonth > 0 && iMonth == (leapMonth + 1) && !leap) {
	                --iMonth;
	                leap = true;
	                daysOfMonth = leapDays(year);
	            } else
	                daysOfMonth = monthDays(year, iMonth);
	            offset -= daysOfMonth;
	//解除闰月
	            if (leap && iMonth == (leapMonth + 1)) leap = false;
	            if (!leap) monCyl++;
	        }
	//offset为0时，并且刚才计算的月份是闰月，要校正
	        if (offset == 0 && leapMonth > 0 && iMonth == leapMonth + 1) {
	            if (leap) {
	                leap = false;
	            } else {
	                leap = true;
	                --iMonth;
	                --monCyl;
	            }
	        }
	//offset小于0时，也要校正
	        if (offset < 0) {
	            offset += daysOfMonth;
	            --iMonth;
	            --monCyl;
	        }
	        month = iMonth;
	        day = offset + 1;
	    }
	    public String getChinaDayString(int day) {
	        String chineseTen[] = {"初", "十", "廿", "卅"};
	        int n = day % 10 == 0 ? 9 : day % 10 - 1;
	        if (day > 30)
	            return "";
	        if (day == 10)
	            return "初十";
	        else
	            return chineseTen[day / 10] + chineseNumber1[n];
	    }
	    @Override
		public String toString() {
	        return (leap ? "闰" : "") + chineseNumber[month - 1] + "月" + getChinaDayString(day);
	    }
	    public String getChinaWeekdayString(String weekday){
	     if(weekday.equals("Mon"))
	      return "一";
	     if(weekday.equals("Tue"))
	      return "二";
	     if(weekday.equals("Wed"))
	      return "三";
	     if(weekday.equals("Thu"))
	      return "四";
	     if(weekday.equals("Fri"))
	      return "五";
	     if(weekday.equals("Sat"))
	      return "六";
	     if(weekday.equals("Sun"))
	      return "日";
	     else
	      return "";
	    }
	}
}
