package core.dashboards;

import system.Convert;
import system.DateTime;
import system.DayOfWeek;
import system.Type;
import system.Collections.Hashtable;
import system.Collections.ICollection;
import system.Collections.IEnumerator;
import system.Data.DataColumn;
import system.Data.DataRow;
import system.Data.DataSet;
import system.Data.DataTable;
import system.Globalization.CultureInfo;
import Core.Dashboards.GroupByDef;
import Core.Dashboards.TimeInterval;
import Core.Dashboards.XmlTimeIntervalCategory;
import Siteview.StringUtils;
import Siteview.Xml.LocalizeHelper;

public class DataConverter {
	private String AverageAllValuesFunction = "AverageAllValuesFunction";

	private String AverageDistinctValuesFunction = "AverageDistinctValuesFunction";

	private String c_ReservedColumnName = "_R_e_S_e_R_v_E_d_";

	private String CountOfAllValuesFunction = "CountOfAllValuesFunction";

	private String CountOfDistinctValuesFunction = "CountOfDistinctValuesFunction";

	private String Daily = "Daily";

	private Integer GROUPBY_ONE_COLUMNS = 3;

	private Integer GROUPBY_TWO_COLUMNS = 4;

	private String Hourly = "Hourly";

	private String MaxValueFunction = "MaxValueFunction";

	private String MinValueFunction = "MinValueFunction";

	private String Month_Day_Year = "Month/Day/Year";

	private String Month_Day_Year1 = "Month/Day/Year1";

	private String Month_Day_Year2 = "Month/Day/Year2";

	private String Month_Year = "Month/Year";

	private String Month_Year1 = "Month/Year1";

	private String Month_Year2 = "Month/Year2";

	private String Monthly = "Monthly";

	private String NEW_STRING = "\n";

	private String Quarter_Year = "Quarter/Year";

	private String Quarter_Year1 = "Quarter/Year1";

	private String Quarter_Year2 = "Quarter/Year2";

	private String Quarterly = "Quarterly";

	private String SumOfAllValuesFunction = "SumOfAllValuesFunction";

	private String SumOfDistinctValuesFunction = "SumOfDistinctValuesFunction";

	private String Week_Month_Year = "Week/Month/Year";

	private String Week_Month_Year1 = "Week/Month/Year1";

	private String Week_Month_Year2 = "Week/Month/Year2";

	private String Weekly = "Weekly";

	private String Year = "Year";

	private String Year_Month_Day_Hour = "Year/Month/Day/Hour";

	private String Year_Month_Day_Hour1 = "Year/Month/Day/Hour1";

	private String Year_Month_Day_Hour2 = "Year/Month/Day/Hour2";

	private String Yearly = "Yearly";

	private static Integer CalculateFirstSunday(Integer year) {
		DateTime time = new DateTime();
		time.__Ctor__(year, 1, 1);
		if (time.get_DayOfWeek() == DayOfWeek.Sunday) {

			return 1;
		} else if (time.get_DayOfWeek() == DayOfWeek.Monday) {

			return 7;
		} else if (time.get_DayOfWeek() == DayOfWeek.Tuesday) {

			return 6;
		} else if (time.get_DayOfWeek() == DayOfWeek.Wednesday) {

			return 5;
		} else if (time.get_DayOfWeek() == DayOfWeek.Thursday) {

			return 4;
		} else if (time.get_DayOfWeek() == DayOfWeek.Friday) {

			return 3;
		} else if (time.get_DayOfWeek() == DayOfWeek.Saturday) {

			return 2;
		}

		return 0;

	}

	private static Integer CalculateFirstSundayofMonth(Integer year,
			Integer month) {
		DateTime time = new DateTime();
		time.__Ctor__(year, month, 1);
		if (time.get_DayOfWeek() == DayOfWeek.Sunday) {

			return 1;
		} else if (time.get_DayOfWeek() == DayOfWeek.Monday) {

			return 7;
		} else if (time.get_DayOfWeek() == DayOfWeek.Tuesday) {

			return 6;
		} else if (time.get_DayOfWeek() == DayOfWeek.Wednesday) {

			return 5;
		} else if (time.get_DayOfWeek() == DayOfWeek.Thursday) {

			return 4;
		} else if (time.get_DayOfWeek() == DayOfWeek.Friday) {

			return 3;
		} else if (time.get_DayOfWeek() == DayOfWeek.Saturday) {

			return 2;
		}

		return 1;

	}

	private static Integer CalculateFirstWeekofMonthInYear(Integer year,
			Integer month) {
		Integer dayOfYear = 0;
		Integer num2 = 0;
		Integer num3 = CalculateFirstSunday(year);
		if (month == 1) {

			return 1;
		}
		DateTime time = new DateTime();
		time.__Ctor__(year, month, 1);
		dayOfYear = time.get_DayOfYear();
		num2 = ((dayOfYear - num3) + 1) / 7;
		if ((((dayOfYear - num3) + 1) % 7) != 0) {
			num2++;
		}
		if (num3 != 1) {
			num2++;
		}

		return num2;

	}

	public static DataTable ChangeColumnCaption(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			if (dt.get_Columns()
					.get_Item(i)
					.get_Caption()
					.equals(Res.get_Default().GetString(
							"Chart.InfragisticNullColumn"))) {
				column = new DataColumn(Res.get_Default().GetString(
						"Chart.NullGroupByFieldValue"), dt.get_Columns()
						.get_Item(i).get_DataType());
			} else {
				column = new DataColumn(dt.get_Columns().get_Item(i)
						.get_Caption(), dt.get_Columns().get_Item(i)
						.get_DataType());
			}
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable ChangeYearColumnName(DataTable dt,
			GroupByDef groupByDef) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			if (dt.get_Columns().get_Item(i).get_Caption()
					.equals(TimeInterval.Yearly.toString())) {
				column = new DataColumn("Year", dt.get_Columns().get_Item(i)
						.get_DataType());
			} else {
				column = new DataColumn(dt.get_Columns().get_Item(i)
						.get_Caption(), dt.get_Columns().get_Item(i)
						.get_DataType());
			}
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (!groupByDef.get_LimitQuery()) {
				for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) == groupByDef
							.get_NumberOfRecordToReturn())) {
				row2.set_Item(0, groupByDef.get_OthersName());
				row2.set_Item(1, row.get_ItemArray()[1]);
				row2.set_Item(2, row.get_ItemArray()[2]);
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) > groupByDef
							.get_NumberOfRecordToReturn())) {
				row2 = table.get_Rows().get_Item(
						groupByDef.get_NumberOfRecordToReturn());
				row2.set_Item(
						1,
						Convert.ToInt32(row2.get_Item(1))
								+ Convert.ToInt32(row.get_ItemArray()[1]));
			} else {
				row2.set_Item(0, Convert.ToString(row.get_ItemArray()[0]));
				row2.set_Item(1, row.get_ItemArray()[1]);
				row2.set_Item(2, row.get_ItemArray()[2]);
			}
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()) {
					if (dt.get_Rows().IndexOf(row) <= groupByDef
							.get_NumberOfRecordToReturn()) {
						table.get_Rows().Add(row2);
					}
				} else if (dt.get_Rows().IndexOf(row) < groupByDef
						.get_NumberOfRecordToReturn()) {
					table.get_Rows().Add(row2);
				}
				continue;
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	public static DataTable Convert2XRef(DataTable st) {
		DataTable table = new DataTable(st.toString());
		DataColumn column = new DataColumn(st.get_Columns().get_Item(0)
				.get_ColumnName(), Type.GetType("System.String"));
		column.set_DefaultValue("");
		column.set_Caption(st.get_Columns().get_Item(0).get_ColumnName());
		table.get_Columns().Add(column);
		Hashtable hashtable = new Hashtable();
		Hashtable hashtable2 = new Hashtable();
		IEnumerator it1 = st.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			String key = (String) row.get_ItemArray()[0];
			String str2 = (String) row.get_ItemArray()[1];
			Integer num = DbTyp2Int(row.get_ItemArray()[2]);

			if (hashtable2.Contains(key)) {

				if (((Hashtable) hashtable2.get_Item(key)).Contains(str2)) {
					((Hashtable) hashtable2.get_Item(key)).set_Item(str2, num);
				} else {
					((Hashtable) hashtable2.get_Item(key)).Add(str2, num);

					if (!hashtable.Contains(str2)) {
						hashtable.Add(str2, str2);
					}
				}
				continue;
			}
			Hashtable hashtable3 = new Hashtable();
			hashtable3.Add(str2, num);
			hashtable2.Add(key, hashtable3);

			if (!hashtable.Contains(str2)) {
				hashtable.Add(str2, str2);
			}
		}
		String current = "";
		IEnumerator it2 = ((ICollection) hashtable.get_Keys()).GetEnumerator();
		while (it2.MoveNext()) {
			String str4 = (String) it2.get_Current();
			column = new DataColumn(str4, Type.GetType("System.Int32"));
			column.set_DefaultValue("0");
			column.set_Caption(str4);
			table.get_Columns().Add(column);
		}
		IEnumerator enumerator = ((ICollection) hashtable2.get_Keys())
				.GetEnumerator();

		while (enumerator.MoveNext()) {
			current = (String) enumerator.get_Current();
			Hashtable hashtable4 = (Hashtable) hashtable2.get_Item(current);
			DataRow row2 = table.NewRow();
			row2.set_Item(st.get_Columns().get_Item(0).get_ColumnName(),
					current);
			IEnumerator it3 = ((ICollection) hashtable4.get_Keys())
					.GetEnumerator();
			while (it3.MoveNext()) {
				String str5 = (String) it3.get_Current();
				//row2.set_Item(str5, Convert.ToInt32(hashtable4.get_Item(str5)));
				system.ClrInt32 val = new system.ClrInt32();
				val.__Ctor__((Integer) hashtable4.get_Item(str5));
				row2.set_Item(str5, val);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable ConvertBackToYearAndMonthAndDayAndHourName(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = new DataColumn(dt.get_Columns().get_Item(i)
					.get_Caption(), Type.GetType("System.String"));
			table.get_Columns().Add(column);
		}
		for (Integer j = 4; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column2 = new DataColumn(dt.get_Columns().get_Item(j)
					.get_Caption(), dt.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column2);
		}
		for (Integer k = 0; k < dt.get_Rows().get_Count(); k++) {
			DataRow row = table.NewRow();
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			String str2 = dt.get_Rows().get_Item(k).get_ItemArray()[0]
					.toString();
			String str3 = SetMonthName(dt.get_Rows().get_Item(k)
					.get_ItemArray()[1].toString());
			String str4 = dt.get_Rows().get_Item(k).get_ItemArray()[2]
					.toString();
			Integer hour = Convert.ToInt32(GetHourNumber(dt.get_Rows()
					.get_Item(k).get_ItemArray()[3].toString()));
			DateTime dt1 = new DateTime();
			dt1.__Ctor__(1, 1, 1, hour, 0, 0);
			String str5 = dt1.ToString(GetHourFormat());
			row.set_Item(0, String.format("%s%s%s%s%s %s", str2, dateSeparator,
					str3, dateSeparator, str4, str5));
			for (Integer m = 4; m < dt.get_Columns().get_Count(); m++) {
				row.set_Item(m - 3,
						dt.get_Rows().get_Item(k).get_ItemArray()[m]);
			}
			table.get_Rows().Add(row);
		}

		return table;

	}
	
	private static int DbTyp2Int(Object obj){
		if (obj instanceof system.ClrInt32){
			return ((system.ClrInt32)obj).getValue();
		}else if (obj instanceof Integer){
			return (Integer)obj;
		}
		return 0;
	}

	private static DataTable ConvertBackToYearAndMonthAndDayName(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = new DataColumn(dt.get_Columns().get_Item(i)
					.get_Caption(), Type.GetType("System.String"));
			table.get_Columns().Add(column);
		}
		for (Integer j = 3; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column2 = new DataColumn(dt.get_Columns().get_Item(j)
					.get_Caption(), dt.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column2);
		}
		for (Integer k = 0; k < dt.get_Rows().get_Count(); k++) {
			DataRow row = table.NewRow();
			Integer year = Convert.ToInt32(dt.get_Rows().get_Item(k)
					.get_ItemArray()[0]);
			Integer month = Convert.ToInt32(dt.get_Rows().get_Item(k)
					.get_ItemArray()[1]);
			Integer day = Convert.ToInt32(dt.get_Rows().get_Item(k)
					.get_ItemArray()[2]);
			DateTime dt1 = new DateTime();
			dt1.__Ctor__(year, month, day);
			row.set_Item(0, dt1.ToShortDateString());
			for (Integer m = 3; m < dt.get_Columns().get_Count(); m++) {
				row.set_Item(m - 2,
						dt.get_Rows().get_Item(k).get_ItemArray()[m]);
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	private static DataTable ConvertBackToYearAndMonthAndWeekName(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = new DataColumn(dt.get_Columns().get_Item(i)
					.get_Caption(), Type.GetType("System.String"));
			table.get_Columns().Add(column);
		}
		for (Integer j = 3; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column2 = new DataColumn(dt.get_Columns().get_Item(j)
					.get_Caption(), dt.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column2);
		}
		for (Integer k = 0; k < dt.get_Rows().get_Count(); k++) {
			DataRow row = table.NewRow();
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			String str2 = SetWeekName(dt.get_Rows().get_Item(k).get_ItemArray()[2]
					.toString());
			String str3 = SetMonthName(dt.get_Rows().get_Item(k)
					.get_ItemArray()[1].toString());
			String str4 = dt.get_Rows().get_Item(k).get_ItemArray()[0]
					.toString();
			row.set_Item(
					0,
					String.format("%s%s%s%s%s", 
							str2, dateSeparator, str3, dateSeparator, str4 ));
			for (Integer m = 3; m < dt.get_Columns().get_Count(); m++) {
				row.set_Item(m - 2,
						dt.get_Rows().get_Item(k).get_ItemArray()[m]);
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	private static DataTable ConvertBackToYearAndMonthName(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = new DataColumn(dt.get_Columns().get_Item(i)
					.get_Caption(), Type.GetType("System.String"));
			table.get_Columns().Add(column);
		}
		for (Integer j = 2; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column2 = new DataColumn(dt.get_Columns().get_Item(j)
					.get_Caption(), dt.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column2);
		}
		for (Integer k = 0; k < dt.get_Rows().get_Count(); k++) {
			DataRow row = table.NewRow();
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			String str2 = SetMonthName(dt.get_Rows().get_Item(k)
					.get_ItemArray()[1].toString());
			String str3 = dt.get_Rows().get_Item(k).get_ItemArray()[0]
					.toString();
			row.set_Item(0,
					String.format("{0}{1}{2}", str2, dateSeparator, str3));
			for (Integer m = 2; m < dt.get_Columns().get_Count(); m++) {
				row.set_Item(m - 1,
						dt.get_Rows().get_Item(k).get_ItemArray()[m]);
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	private static DataTable ConvertBackToYearAndQuarterName(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = new DataColumn(dt.get_Columns().get_Item(i)
					.get_Caption(), Type.GetType("System.String"));
			table.get_Columns().Add(column);
		}
		for (Integer j = 2; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column2 = new DataColumn(dt.get_Columns().get_Item(j)
					.get_Caption(), dt.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column2);
		}
		for (Integer k = 0; k < dt.get_Rows().get_Count(); k++) {
			DataRow row = table.NewRow();
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			String str2 = SetQuarterName(dt.get_Rows().get_Item(k)
					.get_ItemArray()[1].toString());
			String str3 = dt.get_Rows().get_Item(k).get_ItemArray()[0]
					.toString();
			row.set_Item(0,
					String.format("{0}{1}{2}", str2, dateSeparator, str3));
			for (Integer m = 2; m < dt.get_Columns().get_Count(); m++) {
				row.set_Item(m - 1,
						dt.get_Rows().get_Item(k).get_ItemArray()[m]);
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	public static DataTable ConvertColumnNameAndType(DataTable dt,
			GroupByDef groupByDef, GroupByDef subGroupByDef,
			GroupByDef evaluateByDef) {
		if (!subGroupByDef.get_Apply()) {

			dt = ConvertColumnNameAndTypeForOneGroupBy(dt, groupByDef,
					subGroupByDef, evaluateByDef);

			return dt;
		}
		if (groupByDef.get_DateTimeField() && subGroupByDef.get_DateTimeField()) {

			dt = ConvertColumnNameAndTypeForTwoInTwoGroupBy(dt, groupByDef,
					subGroupByDef, evaluateByDef);

			return dt;
		}
		if (!groupByDef.get_DateTimeField()
				&& !subGroupByDef.get_DateTimeField()) {

			dt = ConvertColumnNameAndTypeForNoneInTwoGroupBy(dt, groupByDef,
					subGroupByDef, evaluateByDef);

			return dt;
		}
		if (groupByDef.get_DateTimeField()
				&& !subGroupByDef.get_DateTimeField()) {

			dt = SwitchGroupByColumns(dt, groupByDef, subGroupByDef,
					evaluateByDef);

			dt = ConvertColumnNameAndTypeForOneInTwoGroupBy(dt, groupByDef,
					subGroupByDef, evaluateByDef);

			return dt;
		}

		dt = ConvertColumnNameAndTypeForOneInTwoGroupBy(dt, groupByDef,
				subGroupByDef, evaluateByDef);

		return dt;

	}

	private static DataTable ConvertColumnNameAndTypeForNoneInTwoGroupBy(
			DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef,
			GroupByDef evaluateByDef) {
		DataTable table = new DataTable();
		DataRow row = null;
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			if (i == 0) {
				column = new DataColumn(groupByDef.get_FieldName(),
						Type.GetType("System.String"));
				// NOTICE: break ignore!!!
			} else if (i == 1) {
				column = new DataColumn(subGroupByDef.get_FieldName(),
						Type.GetType("System.String"));
				// NOTICE: break ignore!!!
			} else if (i == 2) {
				column = new DataColumn(SetQueryFunctionColumn(evaluateByDef
						.get_QueryFunction().toString()),
						Type.GetType("System.Int32"));
				// NOTICE: break ignore!!!
			} else {
				column = new DataColumn(dt.get_Columns().get_Item(i)
						.get_Caption(), dt.get_Columns().get_Item(i)
						.get_DataType());
				// NOTICE: break ignore!!!
			}
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row2 = (DataRow) it1.get_Current();

			row = table.NewRow();
			row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
			row.set_Item(1, Convert.ToString(row2.get_ItemArray()[1]));
			row.set_Item(2, row2.get_ItemArray()[2]);
			row.set_Item(3, row2.get_ItemArray()[3]);
			table.get_Rows().Add(row);
		}

		return table;

	}

	private static DataTable ConvertColumnNameAndTypeForOneGroupBy(
			DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef,
			GroupByDef evaluateByDef) {
		DataTable table = new DataTable();
		DataRow row = null;
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			if (!subGroupByDef.get_Apply()) {
				if (!groupByDef.get_Duration()) {
					if (groupByDef.get_DateTimeField()) {
						if ((groupByDef.get_Interval()
								.equals(XmlTimeIntervalCategory
										.ToString(TimeInterval.Monthly)))
								|| (groupByDef.get_Interval()
										.equals(XmlTimeIntervalCategory
												.ToString(TimeInterval.Quarterly)))) {
							if (i == 0) {
								column = new DataColumn(
										TimeInterval.Yearly.toString(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 1) {
								column = new DataColumn(
										groupByDef.get_Interval(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 2) {
								column = new DataColumn(
										SetQueryFunctionColumn(evaluateByDef
												.get_QueryFunction().toString()),
										Type.GetType("System.Int32"));
								// goto Label_043A;
							} else
								column = new DataColumn(dt.get_Columns()
										.get_Item(i).get_Caption(), dt
										.get_Columns().get_Item(i)
										.get_DataType());
						} else if ((groupByDef.get_Interval()
								.equals(XmlTimeIntervalCategory
										.ToString(TimeInterval.Weekly)))
								|| (groupByDef.get_Interval()
										.equals(XmlTimeIntervalCategory
												.ToString(TimeInterval.Daily)))) {
							if (i == 0) {
								column = new DataColumn(
										TimeInterval.Yearly.toString(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 1) {
								column = new DataColumn(
										TimeInterval.Monthly.toString(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 2) {
								column = new DataColumn(
										groupByDef.get_Interval(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 3) {
								column = new DataColumn(
										SetQueryFunctionColumn(evaluateByDef
												.get_QueryFunction().toString()),
										Type.GetType("System.Int32"));
								// goto Label_043A;
							} else
								column = new DataColumn(dt.get_Columns()
										.get_Item(i).get_Caption(), dt
										.get_Columns().get_Item(i)
										.get_DataType());
						} else if (groupByDef.get_Interval().equals(
								XmlTimeIntervalCategory
										.ToString(TimeInterval.Hourly))) {
							if (i == 0) {
								column = new DataColumn(
										TimeInterval.Yearly.toString(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 1) {
								column = new DataColumn(
										TimeInterval.Monthly.toString(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 2) {
								column = new DataColumn(
										TimeInterval.Daily.toString(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 3) {
								column = new DataColumn(
										groupByDef.get_Interval(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 4) {
								column = new DataColumn(
										SetQueryFunctionColumn(evaluateByDef
												.get_QueryFunction().toString()),
										Type.GetType("System.Int32"));
								// goto Label_043A;
							} else
								column = new DataColumn(dt.get_Columns()
										.get_Item(i).get_Caption(), dt
										.get_Columns().get_Item(i)
										.get_DataType());
						} else {
							if (i == 0) {
								column = new DataColumn(
										groupByDef.get_Interval(),
										Type.GetType("System.String"));
								// goto Label_043A;
							} else if (i == 1) {
								column = new DataColumn(
										SetQueryFunctionColumn(evaluateByDef
												.get_QueryFunction().toString()),
										Type.GetType("System.Int32"));
								// goto Label_043A;
							}
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
						}
					} else {
						if (i == 0) {
							column = new DataColumn(groupByDef.get_FieldName(),
									Type.GetType("System.String"));

						} else if (i == 1) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));

						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					}
				} else {
					if (i == 0) {
						column = new DataColumn(groupByDef.get_DurationUnit(),
								Type.GetType("System.String"));
					} else if (i == 1) {
						column = new DataColumn(
								SetQueryFunctionColumn(evaluateByDef
										.get_QueryFunction().toString()),
								Type.GetType("System.Int32"));

					} else
						column = new DataColumn(dt.get_Columns().get_Item(i)
								.get_Caption(), dt.get_Columns().get_Item(i)
								.get_DataType());
				}
			}
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row2 = (DataRow) it1.get_Current();
			if (!groupByDef.get_Duration()) {
				if (groupByDef.get_DateTimeField()) {
					if ((groupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Monthly)))
							|| (groupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Quarterly)))) {

						row = table.NewRow();
						row.set_Item(0,
								Convert.ToString(row2.get_ItemArray()[0]));
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2, row2.get_ItemArray()[2]);
						row.set_Item(3, row2.get_ItemArray()[3]);
					} else if ((groupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Weekly)))
							|| (groupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Daily)))) {

						row = table.NewRow();
						row.set_Item(0,
								Convert.ToString(row2.get_ItemArray()[0]));
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2,
								Convert.ToString(row2.get_ItemArray()[2]));
						row.set_Item(3, row2.get_ItemArray()[3]);
						row.set_Item(4, row2.get_ItemArray()[4]);
					} else if (groupByDef.get_Interval().equals(
							XmlTimeIntervalCategory
									.ToString(TimeInterval.Hourly))) {

						row = table.NewRow();
						row.set_Item(0,
								Convert.ToString(row2.get_ItemArray()[0]));
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2,
								Convert.ToString(row2.get_ItemArray()[2]));
						row.set_Item(3,
								Convert.ToString(row2.get_ItemArray()[3]));
						row.set_Item(4, row2.get_ItemArray()[4]);
						row.set_Item(5, row2.get_ItemArray()[5]);
					} else {

						row = table.NewRow();
						row.set_Item(0,
								Convert.ToString(row2.get_ItemArray()[0]));
						row.set_Item(1, row2.get_ItemArray()[1]);
						row.set_Item(2, row2.get_ItemArray()[2]);
					}
					table.get_Rows().Add(row);
				} else {

					row = table.NewRow();
					if (row2.get_ItemArray().length > 2) {
						if (groupByDef.get_LimitQuery()) {
							if (groupByDef.get_IncludeOthers()
									&& (dt.get_Rows().IndexOf(row2) == groupByDef
											.get_NumberOfRecordToReturn())) {
								row.set_Item(0, groupByDef.get_OthersName());
								row.set_Item(1, row2.get_ItemArray()[1]);
								row.set_Item(2, row2.get_ItemArray()[2]);
							} else if (groupByDef.get_IncludeOthers()
									&& (dt.get_Rows().IndexOf(row2) > groupByDef
											.get_NumberOfRecordToReturn())) {
								row = table
										.get_Rows()
										.get_Item(
												groupByDef
														.get_NumberOfRecordToReturn());
								row.set_Item(
										0,
										Convert.ToString(row.get_Item(0)
												+ ","
												+ Convert.ToString(row2
														.get_ItemArray()[0])));
								row.set_Item(
										1,
										Convert.ToInt32(row.get_Item(1))
												+ Convert.ToInt32(row2
														.get_ItemArray()[1]));
							} else {
								row.set_Item(0, row2.get_ItemArray()[0]);
								row.set_Item(1, row2.get_ItemArray()[1]);
								row.set_Item(2, row2.get_ItemArray()[2]);
							}
						} else {
							row.set_Item(0, row2.get_ItemArray()[0]);
							row.set_Item(1, row2.get_ItemArray()[1]);
							row.set_Item(2, row2.get_ItemArray()[2]);
						}
						if (groupByDef.get_LimitQuery()) {
							if (groupByDef.get_IncludeOthers()) {
								if (dt.get_Rows().IndexOf(row2) <= groupByDef
										.get_NumberOfRecordToReturn()) {
									table.get_Rows().Add(row);
								}
							} else if (dt.get_Rows().IndexOf(row2) < groupByDef
									.get_NumberOfRecordToReturn()) {
								table.get_Rows().Add(row);
							}
						} else {
							table.get_Rows().Add(row);
						}
					}
				}
				continue;
			}

			row = table.NewRow();
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()
						&& (dt.get_Rows().IndexOf(row2) == groupByDef
								.get_NumberOfRecordToReturn())) {
					row.set_Item(0, groupByDef.get_OthersName());
					row.set_Item(1, row2.get_ItemArray()[1]);
					row.set_Item(2, row2.get_ItemArray()[2]);
				} else if (groupByDef.get_IncludeOthers()
						&& (dt.get_Rows().IndexOf(row2) > groupByDef
								.get_NumberOfRecordToReturn())) {
					row = table.get_Rows().get_Item(
							groupByDef.get_NumberOfRecordToReturn());
					row.set_Item(0, Convert.ToString(row.get_Item(0) + ","
							+ Convert.ToString(row2.get_ItemArray()[0])));
					row.set_Item(
							1,
							Convert.ToInt32(row.get_Item(1))
									+ Convert.ToInt32(row2.get_ItemArray()[1]));
				} else {
					row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
					row.set_Item(1, row2.get_ItemArray()[1]);
					row.set_Item(2, row2.get_ItemArray()[2]);
				}
			} else {
				row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
				row.set_Item(1, row2.get_ItemArray()[1]);
				row.set_Item(2, row2.get_ItemArray()[2]);
			}
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()) {
					if (dt.get_Rows().IndexOf(row2) <= groupByDef
							.get_NumberOfRecordToReturn()) {
						table.get_Rows().Add(row);
					}
				} else if (dt.get_Rows().IndexOf(row2) < groupByDef
						.get_NumberOfRecordToReturn()) {
					table.get_Rows().Add(row);
				}
				continue;
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	public static DataTable ConvertColumnNameAndTypeForOneInTwoGroupBy(
			DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef,
			GroupByDef evaluateByDef) {
		DataTable table = new DataTable();
		DataRow row = null;
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			if (!groupByDef.get_DateTimeField()
					|| !subGroupByDef.get_DateTimeField()) {
				if (subGroupByDef.get_DateTimeField()) {
					if ((subGroupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Monthly)))
							|| (subGroupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Quarterly)))) {
						if (i == 1) {
							column = new DataColumn(
									TimeInterval.Yearly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 2) {
							column = new DataColumn(
									subGroupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 3) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					} else if ((subGroupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Weekly)))
							|| (subGroupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Daily)))) {
						if (i == 1) {
							column = new DataColumn(
									TimeInterval.Yearly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 2) {
							column = new DataColumn(
									TimeInterval.Monthly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 3) {
							column = new DataColumn(
									subGroupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 4) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					} else if (subGroupByDef.get_Interval().equals(
							XmlTimeIntervalCategory
									.ToString(TimeInterval.Hourly))) {
						if (i == 1) {
							column = new DataColumn(
									TimeInterval.Yearly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 2) {
							column = new DataColumn(
									TimeInterval.Monthly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 3) {
							column = new DataColumn(
									TimeInterval.Daily.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 4) {
							column = new DataColumn(
									subGroupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 5) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					} else {
						if (i == 1) {
							column = new DataColumn(
									subGroupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 2) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					}
				} else if (groupByDef.get_DateTimeField()) {
					if ((groupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Monthly)))
							|| (groupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Quarterly)))) {
						if (i == 0) {
							column = new DataColumn(
									TimeInterval.Yearly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 1) {
							column = new DataColumn(groupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 3) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					} else if ((groupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Weekly)))
							|| (groupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Daily)))) {
						if (i == 0) {
							column = new DataColumn(
									TimeInterval.Yearly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 1) {
							column = new DataColumn(
									TimeInterval.Monthly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 2) {
							column = new DataColumn(groupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 4) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						}
						column = new DataColumn(dt.get_Columns().get_Item(i)
								.get_Caption(), dt.get_Columns().get_Item(i)
								.get_DataType());
					} else if (groupByDef.get_Interval().equals(
							XmlTimeIntervalCategory
									.ToString(TimeInterval.Hourly))) {
						if (i == 0) {
							column = new DataColumn(
									TimeInterval.Yearly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 1) {
							column = new DataColumn(
									TimeInterval.Monthly.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 2) {
							column = new DataColumn(
									TimeInterval.Daily.toString(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 3) {
							column = new DataColumn(groupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 5) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					} else {
						if (i == 0) {
							column = new DataColumn(groupByDef.get_Interval(),
									Type.GetType("System.String"));
							// goto Label_0678;
						} else if (i == 2) {
							column = new DataColumn(
									SetQueryFunctionColumn(evaluateByDef
											.get_QueryFunction().toString()),
									Type.GetType("System.Int32"));
							// goto Label_0678;
						} else
							column = new DataColumn(dt.get_Columns()
									.get_Item(i).get_Caption(), dt
									.get_Columns().get_Item(i).get_DataType());
					}
				}
			}
			Label_0678: table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row2 = (DataRow) it1.get_Current();
			if (subGroupByDef.get_DateTimeField()) {
				if (subGroupByDef.get_DateTimeField()) {
					if ((subGroupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Monthly)))
							|| (subGroupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Quarterly)))) {

						row = table.NewRow();
						row.set_Item(0, row2.get_ItemArray()[0]);
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2,
								Convert.ToString(row2.get_ItemArray()[2]));
						row.set_Item(3, row2.get_ItemArray()[3]);
						row.set_Item(4, row2.get_ItemArray()[4]);
					} else if ((subGroupByDef.get_Interval()
							.equals(XmlTimeIntervalCategory
									.ToString(TimeInterval.Weekly)))
							|| (subGroupByDef.get_Interval()
									.equals(XmlTimeIntervalCategory
											.ToString(TimeInterval.Daily)))) {

						row = table.NewRow();
						row.set_Item(0, row2.get_ItemArray()[0]);
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2,
								Convert.ToString(row2.get_ItemArray()[2]));
						row.set_Item(3,
								Convert.ToString(row2.get_ItemArray()[3]));
						row.set_Item(4, row2.get_ItemArray()[4]);
						row.set_Item(5, row2.get_ItemArray()[5]);
					} else if (subGroupByDef.get_Interval().equals(
							XmlTimeIntervalCategory
									.ToString(TimeInterval.Hourly))) {

						row = table.NewRow();
						row.set_Item(0, row2.get_ItemArray()[0]);
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2,
								Convert.ToString(row2.get_ItemArray()[2]));
						row.set_Item(3,
								Convert.ToString(row2.get_ItemArray()[3]));
						row.set_Item(4,
								Convert.ToString(row2.get_ItemArray()[4]));
						row.set_Item(5, row2.get_ItemArray()[5]);
						row.set_Item(6, row2.get_ItemArray()[6]);
					} else {

						row = table.NewRow();
						row.set_Item(0, row2.get_ItemArray()[0]);
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2, row2.get_ItemArray()[2]);
						row.set_Item(3, row2.get_ItemArray()[3]);
					}
				}
			} else if (groupByDef.get_DateTimeField()) {
				if ((groupByDef.get_Interval().equals(XmlTimeIntervalCategory
						.ToString(TimeInterval.Monthly)))
						|| (groupByDef.get_Interval()
								.equals(XmlTimeIntervalCategory
										.ToString(TimeInterval.Quarterly)))) {

					row = table.NewRow();
					row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
					row.set_Item(1, Convert.ToString(row2.get_ItemArray()[1]));
					row.set_Item(2, row2.get_ItemArray()[2]);
					row.set_Item(3, row2.get_ItemArray()[3]);
					row.set_Item(4, row2.get_ItemArray()[4]);
				} else if ((groupByDef.get_Interval()
						.equals(XmlTimeIntervalCategory
								.ToString(TimeInterval.Weekly)))
						|| (groupByDef.get_Interval()
								.equals(XmlTimeIntervalCategory
										.ToString(TimeInterval.Daily)))) {

					row = table.NewRow();
					row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
					row.set_Item(1, Convert.ToString(row2.get_ItemArray()[1]));
					row.set_Item(2, Convert.ToString(row2.get_ItemArray()[2]));
					row.set_Item(3, row2.get_ItemArray()[3]);
					row.set_Item(4, row2.get_ItemArray()[4]);
					row.set_Item(5, row2.get_ItemArray()[5]);
				} else if (groupByDef.get_Interval().equals(
						XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))) {

					row = table.NewRow();
					row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
					row.set_Item(1, Convert.ToString(row2.get_ItemArray()[1]));
					row.set_Item(2, Convert.ToString(row2.get_ItemArray()[2]));
					row.set_Item(3, Convert.ToString(row2.get_ItemArray()[3]));
					row.set_Item(4, row2.get_ItemArray()[4]);
					row.set_Item(5, row2.get_ItemArray()[5]);
					row.set_Item(6, row2.get_ItemArray()[6]);
				} else {

					row = table.NewRow();
					row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
					row.set_Item(1, row2.get_ItemArray()[1]);
					row.set_Item(2, row2.get_ItemArray()[2]);
					row.set_Item(3, row2.get_ItemArray()[3]);
				}
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	public static DataTable ConvertColumnNameAndTypeForTwoInTwoGroupBy(
			DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef,
			GroupByDef evaluateByDef) {
		DataTable table = new DataTable();
		DataRow row = null;
		Integer index = 0;
		Integer num2 = 0;
		DataColumn column = null;
		String interval = groupByDef.get_Interval();
		if (interval != null) {
			if (!(interval.equals("Yearly"))) {
				if ((interval.equals("Quarterly"))
						|| (interval.equals("Monthly"))) {
					column = new DataColumn(TimeInterval.Yearly.toString()
							+ "1", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(groupByDef.get_Interval() + "1",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					index = 2;
				} else if ((interval.equals("Weekly"))
						|| (interval.equals("Daily"))) {
					column = new DataColumn(TimeInterval.Yearly.toString()
							+ "1", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(TimeInterval.Monthly.toString()
							+ "1", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(groupByDef.get_Interval() + "1",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					index = 3;
				} else if (interval.equals("Hourly")) {
					column = new DataColumn(TimeInterval.Yearly.toString()
							+ "1", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(TimeInterval.Monthly.toString()
							+ "1", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(
							TimeInterval.Daily.toString() + "1",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(groupByDef.get_Interval() + "1",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					index = 4;
				}
			} else {
				column = new DataColumn(TimeInterval.Yearly.toString() + "1",
						Type.GetType("System.String"));
				table.get_Columns().Add(column);
				index = 1;
			}
		}
		String str2 = subGroupByDef.get_Interval();
		if (str2 != null) {
			if (!(str2.equals("Yearly"))) {
				if ((str2.equals("Quarterly")) || (str2.equals("Monthly"))) {
					column = new DataColumn(TimeInterval.Yearly.toString()
							+ "2", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(subGroupByDef.get_Interval() + "2",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					num2 = 2;
				} else if ((str2.equals("Weekly")) || (str2.equals("Daily"))) {
					column = new DataColumn(TimeInterval.Yearly.toString()
							+ "2", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(TimeInterval.Monthly.toString()
							+ "2", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(subGroupByDef.get_Interval() + "2",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					num2 = 3;
				} else if (str2.equals("Hourly")) {
					column = new DataColumn(TimeInterval.Yearly.toString()
							+ "2", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(TimeInterval.Monthly.toString()
							+ "2", Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(
							TimeInterval.Daily.toString() + "2",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					column = new DataColumn(subGroupByDef.get_Interval() + "2",
							Type.GetType("System.String"));
					table.get_Columns().Add(column);
					num2 = 4;
				}
			} else {
				column = new DataColumn(TimeInterval.Yearly.toString() + "2",
						Type.GetType("System.String"));
				table.get_Columns().Add(column);
				num2 = 1;
			}
		}
		Integer num3 = index + num2;
		for (Integer i = num3; i < dt.get_Columns().get_Count(); i++) {
			if (i == num3) {
				column = new DataColumn(SetQueryFunctionColumn(evaluateByDef
						.get_QueryFunction().toString()),
						Type.GetType("System.Int32"));
			} else {
				column = new DataColumn(dt.get_Columns().get_Item(i)
						.get_Caption(), dt.get_Columns().get_Item(i)
						.get_DataType());
			}
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row2 = (DataRow) it1.get_Current();
			Integer num5 = null;
			String str4 = null;

			row = table.NewRow();
			String str3 = groupByDef.get_Interval();
			if (str3 != null) {
				if (!(str3.equals("Yearly"))) {
					if ((str3.equals("Quarterly")) || (str3.equals("Monthly"))) {
						// goto Label_0632;
						row.set_Item(0,
								Convert.ToString(row2.get_ItemArray()[0]));
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
					}
					if ((str3.equals("Weekly")) || (str3.equals("Daily"))) {
						// goto Label_0661;
						row.set_Item(0,
								Convert.ToString(row2.get_ItemArray()[0]));
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2,
								Convert.ToString(row2.get_ItemArray()[2]));
					}
					if (str3.equals("Hourly")) {
						// goto Label_06A2;
						row.set_Item(0,
								Convert.ToString(row2.get_ItemArray()[0]));
						row.set_Item(1,
								Convert.ToString(row2.get_ItemArray()[1]));
						row.set_Item(2,
								Convert.ToString(row2.get_ItemArray()[2]));
						row.set_Item(3,
								Convert.ToString(row2.get_ItemArray()[3]));
					}
				} else {
					row.set_Item(0, Convert.ToString(row2.get_ItemArray()[0]));
				}
			}
			if ((str4 = subGroupByDef.get_Interval()) != null) {
				if (!(str4.equals("Yearly"))) {
					if ((str4.equals("Quarterly")) || (str4.equals("Monthly"))) {
						// goto Label_077A;
						row.set_Item(index,
								Convert.ToString(row2.get_ItemArray()[index]));
						row.set_Item(index + 1, Convert.ToString(row2
								.get_ItemArray()[index + 1]));
					}
					if ((str4.equals("Weekly")) || (str4.equals("Daily"))) {
						// goto Label_07AD;
						row.set_Item(index,
								Convert.ToString(row2.get_ItemArray()[index]));
						row.set_Item(index + 1, Convert.ToString(row2
								.get_ItemArray()[index + 1]));
						row.set_Item(index + 2, Convert.ToString(row2
								.get_ItemArray()[index + 2]));
					}
					if (str4.equals("Hourly")) {
						// goto Label_07F6;
						row.set_Item(index,
								Convert.ToString(row2.get_ItemArray()[index]));
						row.set_Item(index + 1, Convert.ToString(row2
								.get_ItemArray()[index + 1]));
						row.set_Item(index + 2, Convert.ToString(row2
								.get_ItemArray()[index + 2]));
						row.set_Item(index + 3, Convert.ToString(row2
								.get_ItemArray()[index + 3]));
					}
				} else {
					row.set_Item(index,
							Convert.ToString(row2.get_ItemArray()[index]));
				}
			}
			num5 = num3;
			while (num5 < dt.get_Columns().get_Count()) {
				if (num5 == num3) {
					row.set_Item(num3, row2.get_ItemArray()[num3]);
				} else {
					row.set_Item(num5, row2.get_ItemArray()[num5]);
				}
				num5++;
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	private static DataTable ConvertDayFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Days")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Days));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertDayFromNumberToName(DataTable dt,
			GroupByDef groupByDef, GroupByDef subGroupByDef) {
		Boolean flag = false;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Daily")) {
				flag = true;
				break;
			}
		}
		if (flag) {

			dt = ConvertDayofYearToDayofMonth(dt, groupByDef, subGroupByDef);
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					row2.set_Item(k, row.get_ItemArray()[k]);
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertDayFromNumberToNameFor2DateTimeFields(
			DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef) {
		Boolean flag = false;
		Boolean flag2 = false;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Daily1")) {
				flag = true;
			}
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Daily2")) {
				flag2 = true;
			}
		}
		if (flag || flag2) {

			dt = ConvertDayofYearToDayofMonth(dt, groupByDef, subGroupByDef);
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					row2.set_Item(k, row.get_ItemArray()[k]);
				}
				table.get_Rows().Add(row2);
			}
		}
		if (!flag && !flag2) {

			return dt;
		}

		return table;

	}

	private static  DataTable ConvertDayofYearToDayofMonth(DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef)
    {
        Integer year = 0;
        Integer month = 0;
        Integer dayofYear = 0;
        Integer num4 = 0;
        Integer index = 0;
        Integer num6 = 0;
        DataTable table = new DataTable();
        for(Integer i = 0;i < dt.get_Columns().get_Count();i++){
            DataColumn column = null;
            column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(), dt.get_Columns().get_Item(i).get_DataType());
            table.get_Columns().Add(column);
        }
        IEnumerator it1 = dt.get_Rows().GetEnumerator();
        while(it1.MoveNext()){
            DataRow row = (DataRow)it1.get_Current();
            Integer num8 = null;
            String str2 = null;
            DataRow row2 = table.NewRow();

            if(subGroupByDef.get_FieldName().equals("")){
                
                year = Convert.ToInt32(row.get_ItemArray()[0]);
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[1].toString()));
                
                dayofYear = Convert.ToInt32(row.get_ItemArray()[2]);
                
                num4 = GenerateDayOfMonth(year, month, dayofYear);
                row2.set_Item(0,row.get_ItemArray()[0]);
                row2.set_Item(1,row.get_ItemArray()[1]);
                row2.set_Item(2,num4.toString());
                row2.set_Item(3,row.get_ItemArray()[3]);
                row2.set_Item(4,row.get_ItemArray()[4]);
                table.get_Rows().Add(row2);
                continue;
            }
            if(groupByDef.get_DateTimeField() && !subGroupByDef.get_DateTimeField()){
                
                year = Convert.ToInt32(row.get_ItemArray()[0]);
                
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[1].toString()));
                
                dayofYear = Convert.ToInt32(row.get_ItemArray()[2]);
                
                num4 = GenerateDayOfMonth(year, month, dayofYear);
                row2.set_Item(0,row.get_ItemArray()[0]);
                row2.set_Item(1,row.get_ItemArray()[1]);
                row2.set_Item(2,num4.toString());
                row2.set_Item(3,row.get_ItemArray()[3]);
                row2.set_Item(4,row.get_ItemArray()[4]);
                row2.set_Item(5,row.get_ItemArray()[5]);
            }
            if(!groupByDef.get_DateTimeField() && subGroupByDef.get_DateTimeField()){
                
                year = Convert.ToInt32(row.get_ItemArray()[1]);
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[2].toString()));
                
                dayofYear = Convert.ToInt32(row.get_ItemArray()[3]);
                
                num4 = GenerateDayOfMonth(year, month, dayofYear);
                row2.set_Item(0,row.get_ItemArray()[0]);
                row2.set_Item(1,row.get_ItemArray()[1]);
                row2.set_Item(2,row.get_ItemArray()[2]);
                row2.set_Item(3,num4.toString());
                row2.set_Item(4,row.get_ItemArray()[4]);
                row2.set_Item(5,row.get_ItemArray()[5]);
            }
            if(!groupByDef.get_DateTimeField() || !subGroupByDef.get_DateTimeField()){
            	table.get_Rows().Add(row2);
            	continue;
            }
            String interval = groupByDef.get_Interval();
            if(interval != null){
                if(!(interval.equals("Yearly"))){
                    if((interval.equals("Quarterly")) || (interval.equals("Monthly"))){

                        row2.set_Item(0,row.get_ItemArray()[0]);
                        row2.set_Item(1,row.get_ItemArray()[1]);
                        index = 2;
                    }
                    
                    if(interval.equals("Weekly")){
                    	row2.set_Item(0,row.get_ItemArray()[0]);
                        row2.set_Item(1,row.get_ItemArray()[1]);
                        row2.set_Item(2,row.get_ItemArray()[2]);
                        index = 3;
                    }
                    
                    if(interval.equals("Daily")){
                    	year = Convert.ToInt32(row.get_ItemArray()[0]);
                        
                        month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[1].toString()));
                        
                        dayofYear = Convert.ToInt32(row.get_ItemArray()[2]);
                        
                        num4 = GenerateDayOfMonth(year, month, dayofYear);
                        row2.set_Item(0,row.get_ItemArray()[0]);
                        row2.set_Item(1,row.get_ItemArray()[1]);
                        row2.set_Item(2,num4.toString());
                        index = 3;
                        
                        
                    }
                    
                    if(interval.equals("Hourly")){
                    	year = Convert.ToInt32(row.get_ItemArray()[0]);
                        
                        month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[1].toString()));
                        
                        dayofYear = Convert.ToInt32(row.get_ItemArray()[2]);
                        
                        num4 = GenerateDayOfMonth(year, month, dayofYear);
                        row2.set_Item(0,row.get_ItemArray()[0]);
                        row2.set_Item(1,row.get_ItemArray()[1]);
                        row2.set_Item(2,num4.toString());
                        row2.set_Item(3,row.get_ItemArray()[3]);
                        index = 4;
                        
                    }
                }
                else{
                    row2.set_Item(0,row.get_ItemArray()[0]);
                    index = 1;
                }
            }
            
            if((str2 = subGroupByDef.get_Interval()) != null){
                if(!(str2.equals("Yearly"))){
                    if((str2.equals("Quarterly")) || (str2.equals("Monthly"))){
                    	row2.set_Item(index,row.get_ItemArray()[index]);
                        row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
                        num6 = 2;
                    }
                    if(str2.equals("Weekly")){
                    	row2.set_Item(index,row.get_ItemArray()[index]);
                        row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
                        row2.set_Item(index + 2,row.get_ItemArray()[index + 2]);
                        num6 = 3;
                    }
                    if(str2.equals("Daily")){
                    	year = Convert.ToInt32(row.get_ItemArray()[index]);
                        
                        month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[index + 1].toString()));
                        
                        dayofYear = Convert.ToInt32(row.get_ItemArray()[index + 2]);
                        
                        num4 = GenerateDayOfMonth(year, month, dayofYear);
                        row2.set_Item(index,row.get_ItemArray()[index]);
                        row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
                        row2.set_Item(index + 2,num4.toString());
                        num6 = 3;
                    }
                    if(str2.equals("Hourly")){
                    	 year = Convert.ToInt32(row.get_ItemArray()[index]);
                         
                         month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[index + 1].toString()));
                         
                         dayofYear = Convert.ToInt32(row.get_ItemArray()[index + 2]);
                         
                         num4 = GenerateDayOfMonth(year, month, dayofYear);
                         row2.set_Item(index,row.get_ItemArray()[index]);
                         row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
                         row2.set_Item(index + 2,num4.toString());
                         row2.set_Item(index + 3,row.get_ItemArray()[index + 3]);
                         num6 = 4;
                    }
                }
                else{
                    row2.set_Item(index,row.get_ItemArray()[index]);
                    num6 = 1;
                }
            }
            
            num8 = index + num6;
            for(            Integer j = num8;j < dt.get_Columns().get_Count();j++){
                row2.set_Item(num8,row.get_ItemArray()[num8]);
            }

            table.get_Rows().Add(row2);
            
        }

        return table;

    }

	private static DataTable ConvertHourFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Hours")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Hours));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertHourFromNumberToName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Hourly")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(k, SetHourName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertHourFromNumberToNameFor2DateTimeFields(
			DataTable dt) {
		Boolean flag = false;
		Boolean flag2 = false;
		Integer num = -1;
		Integer num2 = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Hourly1")) {
				flag = true;
				num = i;
			}
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Hourly2")) {
				flag2 = true;
				num2 = i;
			}
		}
		if (flag || flag2) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if ((k == num) && flag) {
						row2.set_Item(k, SetHourName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else if ((k == num2) && flag2) {
						row2.set_Item(k, SetHourName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (!flag && !flag2) {

			return dt;
		}

		return table;

	}

	public static DataTable ConvertIntervalNumberToProperName(DataTable dt,
			GroupByDef groupByDef, GroupByDef subGroupByDef) {
		if (groupByDef.get_DateTimeField() && subGroupByDef.get_DateTimeField()) {

			return ConvertHourFromNumberToNameFor2DateTimeFields(ConvertDayFromNumberToNameFor2DateTimeFields(
					ConvertWeekFromNumberToNameFor2DateTimeFields(
							ConvertQuarterFromNumberToNameFor2DateTimeFields(ConvertMonthFromNumberToNameFor2DateTimeFields(dt)),
							groupByDef, subGroupByDef), groupByDef,
					subGroupByDef));
		}

		return ConvertSecondFromNumberToDurationName(ConvertMinuteFromNumberToDurationName(ConvertHourFromNumberToDurationName(ConvertDayFromNumberToDurationName(ConvertWeekFromNumberToDurationName(ConvertMonthFromNumberToDurationName(ConvertQuarterFromNumberToDurationName(ConvertYearFromNumberToDurationName(ConvertHourFromNumberToName(ConvertDayFromNumberToName(
				ConvertWeekFromNumberToName(
						ConvertQuarterFromNumberToName(ConvertMonthFromNumberToName(dt)),
						groupByDef, subGroupByDef), groupByDef, subGroupByDef))))))))));

	}

	private static DataTable ConvertMinuteFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Minutes")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Minutes));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertMonthFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Months")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Months));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertMonthFromNumberToName(DataTable dt) {
		Boolean flag = false;
		Boolean flag2 = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Monthly")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				flag2 = false;
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {

						if (Convert.ToString(row.get_ItemArray()[k]).equals("")) {
							flag2 = true;
						} else {
							row2.set_Item(k, SetMonthName(Convert.ToString(row
									.get_ItemArray()[k])));
						}
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				if (!flag2) {
					table.get_Rows().Add(row2);
				}
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertMonthFromNumberToNameFor2DateTimeFields(
			DataTable dt) {
		Boolean flag = false;
		Boolean flag2 = false;
		Integer num = -1;
		Integer num2 = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Monthly1")) {
				flag = true;
				num = i;
			}
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Monthly2")) {
				flag2 = true;
				num2 = i;
			}
		}
		if (flag || flag2) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if ((k == num) && flag) {
						row2.set_Item(k, SetMonthName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else if ((k == num2) && flag2) {
						row2.set_Item(k, SetMonthName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (!flag && !flag2) {

			return dt;
		}

		return table;

	}

	private static DataTable ConvertQuarterFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Quarters")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Quarters));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertQuarterFromNumberToName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Quarterly")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(k, SetQuarterName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertQuarterFromNumberToNameFor2DateTimeFields(
			DataTable dt) {
		Boolean flag = false;
		Boolean flag2 = false;
		Integer num = -1;
		Integer num2 = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Quarterly1")) {
				flag = true;
				num = i;
			}
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Quarterly2")) {
				flag2 = true;
				num2 = i;
			}
		}
		if (flag || flag2) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if ((k == num) && flag) {
						row2.set_Item(k, SetQuarterName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else if ((k == num2) && flag2) {
						row2.set_Item(k, SetQuarterName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (!flag && !flag2) {

			return dt;
		}

		return table;

	}

	private static DataTable ConvertSecondFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Seconds")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Seconds));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertWeekFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Weeks")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Weeks));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertWeekFromNumberToName(DataTable dt,
			GroupByDef groupByDef, GroupByDef subGroupByDef) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Weekly")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {

			dt = ConvertWeekofYearToWeekofMonth(dt, groupByDef, subGroupByDef);
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if ((k == num) && flag) {
						row2.set_Item(k, SetWeekName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	private static DataTable ConvertWeekFromNumberToNameFor2DateTimeFields(
			DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef) {
		Boolean flag = false;
		Boolean flag2 = false;
		Integer num = -1;
		Integer num2 = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Weekly1")) {
				flag = true;
				num = i;
			}
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Weekly2")) {
				flag2 = true;
				num2 = i;
			}
		}
		if (flag || flag2) {

			dt = ConvertWeekofYearToWeekofMonth(dt, groupByDef, subGroupByDef);
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if ((k == num) && flag) {
						row2.set_Item(k, SetWeekName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else if ((k == num2) && flag2) {
						row2.set_Item(k, SetWeekName(Convert.ToString(row
								.get_ItemArray()[k])));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (!flag && !flag2) {

			return dt;
		}

		return table;

	}

	private static  DataTable ConvertWeekofYearToWeekofMonth(DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef)
    {
        Integer year = 0;
        Integer month = 0;
        Integer week = 0;
        Integer num4 = 0;
        Integer index = 0;
        Integer num6 = 0;
        
        DataTable table = new DataTable();
        for(        Integer i = 0;i < dt.get_Columns().get_Count();i++){
            DataColumn column = null;
            column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(), dt.get_Columns().get_Item(i).get_DataType());
            table.get_Columns().Add(column);
        }
        
        IEnumerator it1 = dt.get_Rows().GetEnumerator();
        
        while(it1.MoveNext()){
            DataRow row = (DataRow)it1.get_Current();
            Integer num8 = null;
            String str2 = null;
            DataRow row2 = table.NewRow();

            if(subGroupByDef.get_FieldName().equals("")){
                
                year = Convert.ToInt32(row.get_ItemArray()[0]);
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[1].toString()));
                
                week = Convert.ToInt32(row.get_ItemArray()[2]);
                if(year != 0){
                    
                    num4 = GenerateWeekOfMonth(year, month, week);
                }
                else{
                    num4 = 0;
                }
                row2.set_Item(0,row.get_ItemArray()[0]);
                row2.set_Item(1,row.get_ItemArray()[1]);
                row2.set_Item(2,num4.toString());
                row2.set_Item(3,row.get_ItemArray()[3]);
                row2.set_Item(4,row.get_ItemArray()[4]);
 
            }else {
	            if(groupByDef.get_DateTimeField() && !subGroupByDef.get_DateTimeField()){
	                
	                year = Convert.ToInt32(row.get_ItemArray()[0]);
	                
	                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[1].toString()));
	                
	                week = Convert.ToInt32(row.get_ItemArray()[2]);
	                if(year != 0){
	                    
	                    num4 = GenerateWeekOfMonth(year, month, week);
	                }
	                else{
	                    num4 = 0;
	                }
	                row2.set_Item(0,row.get_ItemArray()[0]);
	                row2.set_Item(1,row.get_ItemArray()[1]);
	                row2.set_Item(2,num4.toString());
	                row2.set_Item(3,row.get_ItemArray()[3]);
	                row2.set_Item(4,row.get_ItemArray()[4]);
	                row2.set_Item(5,row.get_ItemArray()[5]);
	            }
	            if(!groupByDef.get_DateTimeField() && subGroupByDef.get_DateTimeField()){
	                
	                year = Convert.ToInt32(row.get_ItemArray()[1]);

	                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[2].toString()));
	                
	                week = Convert.ToInt32(row.get_ItemArray()[3]);
	                if(year != 0){
	                    
	                    num4 = GenerateWeekOfMonth(year, month, week);
	                }
	                else{
	                    num4 = 0;
	                }
	                row2.set_Item(0,row.get_ItemArray()[0]);
	                row2.set_Item(1,row.get_ItemArray()[1]);
	                row2.set_Item(2,row.get_ItemArray()[2]);
	                row2.set_Item(3,num4.toString());
	                row2.set_Item(4,row.get_ItemArray()[4]);
	                row2.set_Item(5,row.get_ItemArray()[5]);
	            }
	            if(!groupByDef.get_DateTimeField() || !subGroupByDef.get_DateTimeField()){
	            	
	            }else{
		            
		            
		            String interval = groupByDef.get_Interval();
		            if(interval != null){
		                if(!(interval.equals("Yearly"))){
		                    if((interval.equals("Quarterly")) || (interval.equals("Monthly"))){
		                    	row2.set_Item(0,row.get_ItemArray()[0]);
		                        row2.set_Item(1,row.get_ItemArray()[1]);
		                        index = 2;
		                    }
		                    if(interval.equals("Weekly")){
		                    	year = Convert.ToInt32(row.get_ItemArray()[0]);
		                        
		                        month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[1].toString()));
		                        
		                        week = Convert.ToInt32(row.get_ItemArray()[2]);
		                        if(year != 0){
		                            
		                            num4 = GenerateWeekOfMonth(year, month, week);
		                        }
		                        else{
		                            num4 = 0;
		                        }
		                        row2.set_Item(0,row.get_ItemArray()[0]);
		                        row2.set_Item(1,row.get_ItemArray()[1]);
		                        row2.set_Item(2,num4.toString());
		                        index = 3;
		                    }
		                    if(interval.equals("Daily")){
		                    	row2.set_Item(0,row.get_ItemArray()[0]);
		                        row2.set_Item(1,row.get_ItemArray()[1]);
		                        row2.set_Item(2,row.get_ItemArray()[2]);
		                        index = 3;
		                    }
		                    if(interval.equals("Hourly")){
		                    	row2.set_Item(0,row.get_ItemArray()[0]);
		                        row2.set_Item(1,row.get_ItemArray()[1]);
		                        row2.set_Item(2,row.get_ItemArray()[2]);
		                        row2.set_Item(3,row.get_ItemArray()[3]);
		                        index = 4;
		                    }
		                }
		                else{
		                    row2.set_Item(0,row.get_ItemArray()[0]);
		                    index = 1;
		                }
		            }
		             
					if((str2 = subGroupByDef.get_Interval()) != null){
		                if(!(str2.equals("Yearly"))){
		                    if((str2.equals("Quarterly")) || (str2.equals("Monthly"))){
		                    	row2.set_Item(index,row.get_ItemArray()[index]);
		                        row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
		                        num6 = 2;
		                    }
		                    if(str2.equals("Weekly")){
		                    	year = Convert.ToInt32(row.get_ItemArray()[index]);
		                        
		                        month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,row.get_ItemArray()[index + 1].toString()));
		                        
		                        week = Convert.ToInt32(row.get_ItemArray()[index + 2]);
		                        if(year != 0){
		                            
		                            num4 = GenerateWeekOfMonth(year, month, week);
		                        }
		                        else{
		                            num4 = 0;
		                        }
		                        row2.set_Item(index,row.get_ItemArray()[index]);
		                        row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
		                        row2.set_Item(index + 2,num4.toString());
		                        num6 = 3;
		                    }
		                    if(str2.equals("Daily")){
		                    	row2.set_Item(index,row.get_ItemArray()[index]);
		                        row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
		                        row2.set_Item(index + 2,row.get_ItemArray()[index + 2]);
		                        num6 = 3;
		                    }
		                    if(str2.equals("Hourly")){
		                    	row2.set_Item(index,row.get_ItemArray()[index]);
		                        row2.set_Item(index + 1,row.get_ItemArray()[index + 1]);
		                        row2.set_Item(index + 2,row.get_ItemArray()[index + 2]);
		                        row2.set_Item(index + 3,row.get_ItemArray()[index + 3]);
		                        num6 = 4;
		                    }
		                }
		                else{
		                    row2.set_Item(index,row.get_ItemArray()[index]);
		                    num6 = 1;
		                }
		            }
		    
					num8 = index + num6;
		            for(Integer j = num8;j < dt.get_Columns().get_Count();j++){
		                row2.set_Item(num8,row.get_ItemArray()[num8]);
		            }
		        }
            }         
			table.get_Rows().Add(row2);
        }

        return table;

    }

	private static DataTable ConvertYearFromNumberToDurationName(DataTable dt) {
		Boolean flag = false;
		Integer num = -1;
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (dt.get_Columns().get_Item(i).get_Caption().equals("Years")) {
				flag = true;
				num = i;
				break;
			}
		}
		if (flag) {
			for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
				DataColumn column = null;
				column = new DataColumn(dt.get_Columns().get_Item(j)
						.get_Caption(), dt.get_Columns().get_Item(j)
						.get_DataType());
				table.get_Columns().Add(column);
			}
			IEnumerator it1 = dt.get_Rows().GetEnumerator();
			while (it1.MoveNext()) {
				DataRow row = (DataRow) it1.get_Current();
				DataRow row2 = table.NewRow();
				for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
					if (k == num) {
						row2.set_Item(
								k,
								GetLocalizedDurationValue(Convert.ToInt32(
										row.get_ItemArray()[k],
										CultureInfo.get_InvariantCulture()),
										DurationUnit.Years));
					} else {
						row2.set_Item(k, row.get_ItemArray()[k]);
					}
				}
				table.get_Rows().Add(row2);
			}
		}
		if (flag) {

			return table;
		}

		return dt;

	}

	public static String DataTable2String(DataTable dt) {
		system.Text.StringBuilder builder = new system.Text.StringBuilder(
				dt.get_TableName());
		builder.Append("\n");
		IEnumerator it1 = dt.get_Columns().GetEnumerator();
		while (it1.MoveNext()) {
			DataColumn column = (DataColumn) it1.get_Current();
			builder.Append(column.get_ColumnName());
			builder.Append("\t");
		}
		builder.Append("\n");
		IEnumerator it2 = dt.get_Rows().GetEnumerator();
		while (it2.MoveNext()) {
			DataRow row = (DataRow) it2.get_Current();
			IEnumerator it3 = dt.get_Columns().GetEnumerator();
			while (it3.MoveNext()) {
				DataColumn column2 = (DataColumn) it3.get_Current();
				builder.Append(row.get_Item(column2));
				builder.Append("\t");
			}
			builder.Append("\n");
		}

		return builder.toString();

	}

	public static DataTable FlipDataTable(DataTable dt) {
		DataTable table = new DataTable();
		DataColumn column = new DataColumn(dt.get_Columns().get_Item(0)
				.toString());
		table.get_Columns().Add(column);
		for (Integer i = 0; i < dt.get_Rows().get_Count(); i++) {
			String columnName = Convert.ToString(dt.get_Rows().get_Item(i)
					.get_ItemArray()[0]);
			if ((columnName == null) || (columnName.length() == 0)) {
				columnName = "_R_e_S_e_R_v_E_d_" + Convert.ToString(i);
			}
			column = new DataColumn(columnName, Type.GetType(Integer.class
					.getName()));
			table.get_Columns().Add(column);
		}
		DataRow row = null;
		for (Integer j = 1; j < (dt.get_Columns().get_Count() - 1); j++) {

			row = table.NewRow();
			row.set_Item(0, dt.get_Columns().get_Item(j).toString());
			for (Integer k = 1; k <= dt.get_Rows().get_Count(); k++) {
				row.set_Item(k, dt.get_Rows().get_Item(k - 1).get_Item(j));
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	public static DataTable FlipDataTableWith2GroupBy(DataTable dt) {
		DataTable table = new DataTable();
		DataColumn column = new DataColumn(dt.get_Columns().get_Item(0)
				.toString());
		table.get_Columns().Add(column);
		for (Integer i = 0; i < dt.get_Rows().get_Count(); i++) {
			String columnName = Convert.ToString(dt.get_Rows().get_Item(i)
					.get_ItemArray()[0]);
			if ((columnName == null) || (columnName.length() == 0)) {
				columnName = "_R_e_S_e_R_v_E_d_" + Convert.ToString(i);
			}
			column = new DataColumn(columnName, Type.GetType(Integer.class
					.getName()));
			table.get_Columns().Add(column);
		}
		DataRow row = null;
		for (Integer j = 1; j < dt.get_Columns().get_Count(); j++) {

			row = table.NewRow();
			row.set_Item(0, dt.get_Columns().get_Item(j).toString());
			for (Integer k = 1; k <= dt.get_Rows().get_Count(); k++) {
				row.set_Item(k, dt.get_Rows().get_Item(k - 1).get_Item(j));
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	private static Integer GenerateDayOfMonth(Integer year, Integer month,
			Integer dayofYear) {
		Integer dayOfYear = 0;
		DateTime time = new DateTime();
		time.__Ctor__(year, month, 1);
		dayOfYear = time.get_DayOfYear();

		return ((dayofYear - dayOfYear) + 1);

	}

	private static Integer GenerateWeekOfMonth(Integer year, Integer month,
			Integer week) {
		Integer num2 = CalculateFirstWeekofMonthInYear(year, month);

		return ((week - num2) + 1);

	}

	public static DateTime GetEndDateTimeFor2GroupByDrillDown(String paraName,
			String paraValue) {
		String[] strArray = null;
		String str2 = null;
		Integer year = 0;
		Integer month = 0;
		Integer startMonth = 0;
		Integer endMonth = 0;
		Integer day = 0;
		Integer hour = 0;
		String ch = CultureInfo.get_CurrentCulture()
				.get_DateTimeFormat().get_DateSeparator();
		if (((str2 = paraName) != null) && ((!str2.equals("Yearly")))) {
			if (!(str2.equals("Quarterly"))) {
				if (str2.equals("Monthly")) {
					if (paraValue.indexOf(ch) != -1) {

						strArray = paraValue.split(ch);

						year = Convert.ToInt32(strArray[1]);

						month = XmlMonthIntervalCategory
								.ToMonthNumber((MonthInterval) LocalizeHelper
										.Parse(MonthInterval.class, strArray[0]
												.toString()));

						day = DateTime.DaysInMonth(year, month);
						hour = 0x17;
					}
				} else if (str2.equals("Weekly")) {
					if (paraValue.indexOf(ch) != -1) {

						strArray = paraValue.split(ch);

						year = Convert.ToInt32(strArray[2]);

						month = XmlMonthIntervalCategory
								.ToMonthNumber((MonthInterval) LocalizeHelper
										.Parse(MonthInterval.class, strArray[1]
												.toString()));

						day = GetEndDayofWeek(
								CalculateFirstSundayofMonth(year, month),
								XmlWeekIntervalCategory
										.ToWeekNumber((WeekInterval) LocalizeHelper.Parse(
												WeekInterval.class,
												strArray[0].toString())), year,
								month);
						hour = 0x17;
					}
				} else if (str2.equals("Daily")) {
					DateTime time = Convert.ToDateTime(paraValue);
					year = time.get_Year();
					month = time.get_Month();
					day = time.get_Day();
					hour = 0x17;
				} else if ((str2.equals("Hourly"))
						&& (paraValue.indexOf(ch) != -1)) {

					strArray = paraValue.split(ch );

					year = Convert.ToInt32(strArray[0]);

					month = Convert.ToInt32(strArray[1]);
					String[] strArray2 = strArray[2].split(" ");

					day = Convert.ToInt32(strArray2[0]);
					String num = strArray2[1].toString();
					if ((num.toUpperCase().indexOf("A") == -1)
							&& (num.toUpperCase().indexOf("P") == -1)) {

						num = SetHourName(num);
					} else if (num.startsWith("0")) {

						num = num.substring(1);
					}

					hour = XmlHourIntervalCategory
							.ToHourNumber((HourInterval) LocalizeHelper.Parse(
									HourInterval.class,
									num));
				}
			} else if (paraValue.indexOf(ch) != -1) {

				strArray = paraValue.split(ch );

				year = Convert.ToInt32(strArray[1]);
				Integer[] __startMonth_0 = new Integer[1];
				Integer[] __endMonth_1 = new Integer[1];
				XmlQuarterIntervalCategory
						.GetStartEndMonthOfQuarter(
								XmlQuarterIntervalCategory
										.ToQuarterNumber((QuarterInterval) LocalizeHelper.Parse(
												Type.GetType(QuarterInterval.class
														.getName()),
												strArray[0].toString())),
								__startMonth_0, __endMonth_1);
				startMonth = __startMonth_0[0];
				endMonth = __endMonth_1[0];
				month = endMonth;

				day = DateTime.DaysInMonth(year, month);
				hour = 0x17;
			}
		}
		
		DateTime dt = new DateTime();
		dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);

		return dt;

	}

	public static  DateTime GetEndDateTimeForDrillDown(String paraName, String paraValue)
    {
        String[] strArray = null;
        String str = null;
        Integer year= 0;
        Integer month = 0;
        Integer startMonth = 0;
        Integer endMonth = 0;
        Integer day = 0;
        Integer hour = 0;
        String ch = CultureInfo.get_CurrentCulture().get_DateTimeFormat().get_DateSeparator();
        if(paraName.equals("Yearly") || paraName.equals("Year")){
            
            year = Convert.ToInt32(paraValue);
            month = 12;
            
            day = DateTime.DaysInMonth(year, month);
            hour = 0x17;
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
            return dt;
        }
        else if(paraName.equals("Quarter/Year") || paraName.equals("Quarter/Year1")){
            if(paraValue.indexOf(ch) != -1){
                
                strArray = paraValue.split(ch);
                
                year = Convert.ToInt32(strArray[1]);
                Integer[] __startMonth_0 = new Integer[1];
                Integer[] __endMonth_1 = new Integer[1];
                XmlQuarterIntervalCategory.GetStartEndMonthOfQuarter(XmlQuarterIntervalCategory.ToQuarterNumber((QuarterInterval)LocalizeHelper.Parse(Type.GetType(QuarterInterval.class.getName()),strArray[0].toString())),__startMonth_0,__endMonth_1);
                startMonth = __startMonth_0[0];
                endMonth = __endMonth_1[0];
                month = endMonth;
                
                day = DateTime.DaysInMonth(year, month);
                hour = 0x17;
            }
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
            return dt;
        }
        else if(paraName.equals("Month/Year") || paraName.equals("Month/Year1")){
            if(paraValue.indexOf(ch) != -1){
                
                strArray = paraValue.split(ch);
                
                year = Convert.ToInt32(strArray[1]);
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,strArray[0].toString()));
                
                day = DateTime.DaysInMonth(year, month);
                hour = 0x17;
            }
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
            return dt;
        }
        else if(paraName.equals("Week/Month/Year") || paraName.equals("Week/Month/Year1")){
            if(paraValue.indexOf(ch) != -1){
                
                strArray = paraValue.split(ch);
                
                year = Convert.ToInt32(strArray[2]);
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,strArray[1].toString()));
                
                day = GetEndDayofWeek(CalculateFirstSundayofMonth(year, month), XmlWeekIntervalCategory.ToWeekNumber((WeekInterval) LocalizeHelper.Parse(WeekInterval.class,strArray[0].toString())), year, month);
                hour = 0x17;
            }
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
            return dt;
        }
        else if(paraName.equals("Month/Day/Year") || paraName.equals("Month/Day/Year1")){
            DateTime time = Convert.ToDateTime(paraValue);
            year = time.get_Year();
            month = time.get_Month();
            day = time.get_Day();
            hour = 0x17;
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
            return dt;

        }
        else if(paraName == "Year/Month/Day/Hour" || paraName == "Year/Month/Day/Hour1"){
            if(paraValue.indexOf(ch) == -1){
            	DateTime dt = new DateTime();
                dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
                return dt;
            }
            
            strArray = paraValue.split(ch);
            
            year = Convert.ToInt32(strArray[0]);
            
            month = Convert.ToInt32(strArray[1]);
            String[] strArray2 = strArray[2].split(" ");
            
            day = Convert.ToInt32(strArray2[0]);
            
            str = strArray2[1].toString();
            if((str.toUpperCase().indexOf("A") != -1) || (str.toUpperCase().indexOf("P") != -1)){

                if(str.startsWith("0")){
                    
                    str = str.substring(1);
                }
            }else{
            
            str = SetHourName(str);
            }
        }
        else {
        	DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
            return dt;
        }
        
        hour = XmlHourIntervalCategory.ToHourNumber((HourInterval)LocalizeHelper.Parse(HourInterval.class,str));

	
        DateTime dt = new DateTime();
        dt.__Ctor__(year, month, day, hour, 0x3b, 0x3b);
        return dt;

    }

	private static Integer GetEndDayofWeek(Integer firstSunday,
			Integer weekNumber, Integer year, Integer month) {
		Integer num = DateTime.DaysInMonth(year, month);
		if (weekNumber == 1) {

			return ((firstSunday == 1) ? 7 : (firstSunday - 1));
		} else if (weekNumber == 2) {

			return ((firstSunday == 1) ? 14 : (firstSunday + 6));
		} else if (weekNumber == 3) {

			return ((firstSunday == 1) ? 0x15 : (firstSunday + 13));
		} else if (weekNumber == 4) {

			return ((firstSunday == 1) ? 0x1c : (firstSunday + 20));
		} else if (weekNumber == 5) {

			return ((firstSunday == 1) ? num
					: (((firstSunday + 0x1b) > num) ? num
							: (firstSunday + 0x1b)));
		} else if (weekNumber == 6) {

			return num;
		}

		return 1;

	}

	private static String GetHourFormat() {
		system.Text.StringBuilder builder = new system.Text.StringBuilder(" ");
		String str = "";
		Integer num = 0;
		String longTimePattern = CultureInfo.get_CurrentCulture()
				.get_DateTimeFormat().get_LongTimePattern();
		for (Integer i = 0; i < longTimePattern.length(); i++) {
			char ch = longTimePattern.charAt(i);
			if ("h".equalsIgnoreCase(String.valueOf(ch))) {
				builder.Append(longTimePattern.charAt(i));
				num++;
			}
			if (num == 2) {
				break;
			}
		}
		if (builder.get_Chars(1) == 'h') {

			str = builder.toString().toLowerCase();
		} else {

			str = builder.toString().toUpperCase();
		}
		builder.Remove(0, builder.get_Length());
		num = 0;
		for (Integer j = 0; j < longTimePattern.length(); j++) {
			char ch2 = longTimePattern.charAt(j);
			if ("t".equalsIgnoreCase(String.valueOf(ch2))) {
				builder.Append(longTimePattern.charAt(j));
				num++;
			}
			if (num == 2) {
				break;
			}
		}

		return (str + builder.toString());

	}

	private static String GetHourNumber(String strHourName) {
		Integer num = 0;
		String strA = strHourName.substring(strHourName.length() - 2, 2);
		String str2 = "";
		if (clr.System.StringStaticWrapper.Compare(strA, "AM", true) == 0) {

			return strHourName.substring(0, strHourName.length() - 2);
		}
		if (clr.System.StringStaticWrapper.Compare(strA, "PM", true) != 0) {

			return str2;
		}
		num = Convert
				.ToInt32(strHourName.substring(0, strHourName.length() - 2)) + 12;
		if (num == 0x18) {
			num = 0;
		}

		return num.toString();

	}

	private Integer GetIndex(DataTable dt, String columnName) {

		if (dt.get_Columns().Contains(columnName)) {

			return dt.get_Columns().IndexOf(columnName);
		}

		return -1;

	}

	private static String GetLocalizedDurationValue(Integer nDuration,
			int durationUnit) {
		String strText = "";
		if (durationUnit == DurationUnit.Seconds) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationSecond")
					: Res.get_Default().GetString(
							"DataConverter.DurationSeconds");
			// NOTICE: break ignore!!!
		} else if (durationUnit == DurationUnit.Minutes) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationMinute")
					: Res.get_Default().GetString(
							"DataConverter.DurationMinutes");
			// NOTICE: break ignore!!!
		} else if (durationUnit == DurationUnit.Hours) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationHour")
					: Res.get_Default().GetString(
							"DataConverter.DurationHours");
			// NOTICE: break ignore!!!
		} else if (durationUnit == DurationUnit.Days) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationDay")
					: Res.get_Default().GetString(
							"DataConverter.DurationDays");
			// NOTICE: break ignore!!!
		} else if (durationUnit == DurationUnit.Weeks) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationWeek")
					: Res.get_Default().GetString(
							"DataConverter.DurationWeeks");
			// NOTICE: break ignore!!!
		} else if (durationUnit == DurationUnit.Months) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationMonth")
					: Res.get_Default().GetString(
							"DataConverter.DurationMonths");
			// NOTICE: break ignore!!!
		} else if (durationUnit == DurationUnit.Quarters) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationQuarter")
					: Res.get_Default().GetString(
							"DataConverter.DurationQuarters");
			// NOTICE: break ignore!!!
		} else if (durationUnit == DurationUnit.Years) {
			strText = (nDuration == 1) ? Res.get_Default()
					.GetString("DataConverter.DurationYear")
					: Res.get_Default().GetString(
							"DataConverter.DurationYears");
			// NOTICE: break ignore!!!
		}
		if ((!"".equals(strText))) {

			strText = StringUtils.SetToken(strText, "DURATION", nDuration);
		}

		return strText;

	}

	public static  DataTable GetSampleData()
    {
        String[][] strArray = new String[][]{new String[]{ "A", "G1", "5" },new String[]{ "P", "G1", "10" },
        									new String[]{"A", "G2", "15"},new String[]{"C", "G2", "90"},new String[]{"P", "G2", "20"}};
        DataTable table = new DataTable("Sample");
        DataColumn column = new DataColumn("Status", Type.GetType("System.String"));
        column.set_DefaultValue("");
        column.set_Caption("Status");
        table.get_Columns().Add(column);
        DataColumn column2 = new DataColumn("Group", Type.GetType("System.String"));
        column2.set_DefaultValue("");
        column2.set_Caption("Group");
        table.get_Columns().Add(column2);
        DataColumn column3 = new DataColumn("Value", Type.GetType("System.Int32"));
        column3.set_DefaultValue(0);
        column3.set_Caption("Value");
        table.get_Columns().Add(column3);
        table.NewRow();
        Integer length = strArray[0].length;
        for(Integer i = 0;i < length;i++){
            Integer num = -1;
            DataRow row = table.NewRow();
            IEnumerator it1 = table.get_Columns().GetEnumerator();
            while(it1.MoveNext()){
                DataColumn column4 = (DataColumn)it1.get_Current();
                num++;
                if(column4.get_DataType() == Type.GetType(String.class.getName())){
                    row.set_Item(column4,strArray[i][num]);
                }
                else if(column4.get_DataType() == Type.GetType(Integer.class.getName())){
                    row.set_Item(column4,Convert.ToInt32(strArray[i][num]));
                }
            }
            table.get_Rows().Add(row);
        }

        return table;

    }

	public static DateTime GetStartDateTimeFor2GroupByDrillDown(
			String paraName, String paraValue) {
		String[] strArray = null;
		String str2 = null;
		Integer year = 0;
		Integer month = 0;
		Integer startMonth = 0;
		Integer endMonth = 0;
		Integer day = 0;
		Integer hour = 0;
		String ch = CultureInfo.get_CurrentCulture()
				.get_DateTimeFormat().get_DateSeparator();
		if (((str2 = paraName) != null) && ((!str2.equals("Yearly")))) {
			if (!(str2.equals("Quarterly"))) {
				if (str2.equals("Monthly")) {
					if (paraValue.indexOf(ch) != -1) {

						strArray = paraValue.split(ch );

						year = Convert.ToInt32(strArray[1]);

						month = XmlMonthIntervalCategory
								.ToMonthNumber((MonthInterval) LocalizeHelper
										.Parse(MonthInterval.class, strArray[0]
												.toString()));
						day = 1;
						hour = 0;
					}
				} else if (str2.equals("Weekly")) {
					if (paraValue.indexOf(ch) != -1) {

						strArray = paraValue.split(ch );

						year = Convert.ToInt32(strArray[2]);

						month = XmlMonthIntervalCategory
								.ToMonthNumber((MonthInterval) LocalizeHelper
										.Parse(MonthInterval.class, strArray[1]
												.toString()));

						day = GetStartDayofWeek(
								CalculateFirstSundayofMonth(year, month),
								XmlWeekIntervalCategory
										.ToWeekNumber((WeekInterval) LocalizeHelper.Parse(
												WeekInterval.class,	strArray[0].toString())));
						hour = 0;
					}
				} else if (str2.equals("Daily")) {
					DateTime time = Convert.ToDateTime(paraValue);
					year = time.get_Year();
					month = time.get_Month();
					day = time.get_Day();
					hour = 0;
				} else if ((str2.equals("Hourly"))
						&& (paraValue.indexOf(ch) != -1)) {

					strArray = paraValue.split(ch );

					year = Convert.ToInt32(strArray[0]);

					month = Convert.ToInt32(strArray[1]);
					String[] strArray2 = strArray[2].split(" ");

					day = Convert.ToInt32(strArray2[0]);
					String num = strArray2[1].toString();
					if ((num.toUpperCase().indexOf("A") == -1)
							&& (num.toUpperCase().indexOf("P") == -1)) {

						num = SetHourName(num);
					} else if (num.startsWith("0")) {

						num = num.substring(1);
					}

					hour = XmlHourIntervalCategory
							.ToHourNumber((HourInterval) LocalizeHelper.Parse(
									HourInterval.class,
									num));
				}
			} else if (paraValue.indexOf(ch) != -1) {

				strArray = paraValue.split(ch );

				year = Convert.ToInt32(strArray[1]);
				Integer[] __startMonth_0 = new Integer[1];
				Integer[] __endMonth_1 = new Integer[1];
				XmlQuarterIntervalCategory
						.GetStartEndMonthOfQuarter(
								XmlQuarterIntervalCategory
										.ToQuarterNumber((QuarterInterval) LocalizeHelper.Parse(
												QuarterInterval.class,
												strArray[0].toString())),
								__startMonth_0, __endMonth_1);
				startMonth = __startMonth_0[0];
				endMonth = __endMonth_1[0];
				month = startMonth;
				day = 1;
				hour = 0;
			}
		}

		DateTime dt =new DateTime();
		dt.__Ctor__(year, month, day, hour, 0, 0);
		return dt;

	}

	public static  DateTime GetStartDateTimeForDrillDown(String paraName, String paraValue)
    {
        String[] strArray = null;
        String str = null;
        Integer year = 0;
        Integer quarter = 0;
        Integer month = 0;
        Integer startMonth = 0;
        Integer endMonth = 0;
        Integer day = 0;
        Integer hour = 0;
        String ch = CultureInfo.get_CurrentCulture().get_DateTimeFormat().get_DateSeparator();
        if(paraName.equals("Yearly") || paraName.equals("Year")){
            
            year = Convert.ToInt32(paraValue);
            month = 1;
            day = 1;
            hour = 0;
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0, 0);
            return dt;
        }
        else if(paraName.equals( "Quarter/Year") || paraName.equals("Quarter/Year1")){
            if(paraValue.indexOf(ch) != -1){
                
                strArray = paraValue.split(ch);
                
                quarter = XmlQuarterIntervalCategory.ToQuarterNumber((QuarterInterval)LocalizeHelper.Parse(QuarterInterval.class,strArray[0].toString()));
                
                year = Convert.ToInt32(strArray[1]);
                Integer[] __startMonth_0 = new Integer[1];
                Integer[] __endMonth_1 = new Integer[1];
                XmlQuarterIntervalCategory.GetStartEndMonthOfQuarter(quarter,__startMonth_0,__endMonth_1);
                startMonth = __startMonth_0[0];
                endMonth = __endMonth_1[0];
                month = startMonth;
                day = 1;
                hour = 0;
            }
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0, 0);
            return dt;
        }
        else if(paraName.equals("Month/Year") || paraName.equals("Month/Year1")){
            if(paraValue.indexOf(ch) != -1){
                
                strArray = paraValue.split(ch);
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,strArray[0].toString()));
                
                year = Convert.ToInt32(strArray[1]);
                day = 1;
                hour = 0;
            }
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0, 0);
            return dt;
        }
        else if(paraName == "Week/Month/Year" || paraName == "Week/Month/Year1"){
            if(paraValue.indexOf(ch) != -1){
                
                strArray = paraValue.split(ch);
                
                year = Convert.ToInt32(strArray[2]);
                
                month = XmlMonthIntervalCategory.ToMonthNumber((MonthInterval)LocalizeHelper.Parse(MonthInterval.class,strArray[1].toString()));
                
                day = GetStartDayofWeek(CalculateFirstSundayofMonth(year, month), XmlWeekIntervalCategory.ToWeekNumber((WeekInterval)LocalizeHelper.Parse(WeekInterval.class,strArray[0].toString())));
                hour = 0;
            }
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0, 0);
            return dt;
        }
        else if(paraName.equals("Month/Day/Year") || paraName.equals("Month/Day/Year1")){
            DateTime time = Convert.ToDateTime(paraValue);
            year = time.get_Year();
            month = time.get_Month();
            day = time.get_Day();
            hour = 0;
            DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0, 0);
            return dt;
        }
        else if(paraName.equals( "Year/Month/Day/Hour" )|| paraName.equals("Year/Month/Day/Hour1")){
            if(paraValue.indexOf(ch) == -1){
            	DateTime dt = new DateTime();
                dt.__Ctor__(year, month, day, hour, 0, 0);
                return dt;
            }
            
            strArray = paraValue.split(ch);
            
            year = Convert.ToInt32(strArray[0]);
            
            month = Convert.ToInt32(strArray[1]);
            String[] strArray2 = strArray[2].split(" ");
            
            day = Convert.ToInt32(strArray2[0]);
            
            str = strArray2[1].toString();
            if((str.toUpperCase().indexOf("A") != -1) || (str.toUpperCase().indexOf("P") != -1)){

                if(str.startsWith("0")){
                    
                    str = str.substring(1);
                }
               
            }else
            	str = SetHourName(str);
        
        }
        else {
        	DateTime dt = new DateTime();
            dt.__Ctor__(year, month, day, hour, 0, 0);
            return dt;
        }
        
        hour = XmlHourIntervalCategory.ToHourNumber((HourInterval)LocalizeHelper.Parse(HourInterval.class,str));

		DateTime dt = new DateTime();
        dt.__Ctor__(year, month, day, hour, 0, 0);
        return dt;

    }

	private static Integer GetStartDayofWeek(Integer firstSunday,
			Integer weekNumber) {
		if (weekNumber == 1) {

			return 1;
		} else if (weekNumber == 2) {

			return firstSunday;
		} else if (weekNumber == 3) {

			return (firstSunday + 7);
		} else if (weekNumber == 4) {

			return (firstSunday + 14);
		} else if (weekNumber == 5) {

			return (firstSunday + 0x15);
		} else if (weekNumber == 6) {

			return (firstSunday + 0x1c);
		}

		return 1;

	}

	public static DataTable InsertEmptyRowToTable(DataTable dt,
			Boolean lineChart) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					dt.get_Columns().get_Item(i).get_DataType());
			table.get_Columns().Add(column);
		}
		DataRow row = table.NewRow();
		for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
			if (dt.get_Columns().get_Item(j).get_DataType() == Type
					.GetType("System.String")) {
				if (lineChart) {
					row.set_Item(j, "");
				} else {
					row.set_Item(j, Res.get_Default()
							.GetString("DataConverter.NoRecord"));
				}
			} else if ((dt.get_Columns().get_Item(j).get_DataType() == Type
					.GetType("System.Int32"))
					|| (dt.get_Columns().get_Item(j).get_DataType() == Type
							.GetType("System.Decimal"))) {
				system.ClrInt32 zero = new system.ClrInt32();
				zero.__Ctor__(0);
				row.set_Item(j, zero);
			} else if (dt.get_Columns().get_Item(j).get_DataType() == Type
					.GetType("System.DateTime")) {
				row.set_Item(j, DateTime.get_Today());
			} else {
				row.set_Item(j, "");
			}
		}
		table.get_Rows().Add(row);
		if (lineChart) {
			DataRow row2 = table.NewRow();
			for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	public static Boolean IsReservedColumnName(DataColumn clm) {

		return clm.get_ColumnName().startsWith("_R_e_S_e_R_v_E_d_");

	}

	public static double MaxTotalValueFromDataTable(DataTable dt) {
		double num = 0.0;
		IEnumerator it1 = dt.get_Columns().GetEnumerator();
		while (it1.MoveNext()) {
			DataColumn column = (DataColumn) it1.get_Current();
			if ((column.get_DataType() != Type.GetType(String.class.getName()))
					&& (column.get_DataType() != Type.GetType(DateTime.class
							.getName()))) {
				Integer ordinal = column.get_Ordinal();
				double num3 = 0.0;
				IEnumerator it2 = dt.get_Rows().GetEnumerator();
				while (it2.MoveNext()) {
					DataRow row = (DataRow) it2.get_Current();
					try {

						num3 += Convert.ToDouble(row.get_ItemArray()[ordinal]);
						continue;
					} catch (java.lang.Exception e) {
						continue;
					}
				}
				if (num3 > num) {
					num = num3;
				}
			}
		}

		return num;

	}

	public static double MaxValueFromDataTable(DataTable dt) {
		double num = 0.0;
		IEnumerator it1 = dt.get_Columns().GetEnumerator();
		while (it1.MoveNext()) {
			DataColumn column = (DataColumn) it1.get_Current();
			if ((column.get_DataType() != Type.GetType(String.class.getName()))
					&& (column.get_DataType() != Type.GetType(DateTime.class
							.getName()))) {
				Integer ordinal = column.get_Ordinal();
				IEnumerator it2 = dt.get_Rows().GetEnumerator();
				while (it2.MoveNext()) {
					DataRow row = (DataRow) it2.get_Current();
					try {
						double num3 = Convert
								.ToDouble(row.get_ItemArray()[ordinal]);
						if (num3 > num) {
							num = num3;
						}
						continue;
					} catch (java.lang.Exception e) {
						continue;
					}
				}
				continue;
			}
		}

		return num;

	}

	public static DataTable MergeIntervalColumns(DataTable dt,
			TimeInterval interval, GroupByDef groupByDef) {
		if (interval == TimeInterval.Yearly) {

			return ChangeYearColumnName(dt, groupByDef);
		} else if (interval == TimeInterval.Quarterly) {

			return MergeYearAndQuarterColumns(dt, groupByDef);
		} else if (interval == TimeInterval.Monthly) {

			return MergeYearAndMonthColumns(dt, groupByDef);
		} else if (interval == TimeInterval.Weekly) {

			return MergeYearAndMonthAndWeekColumns(dt, groupByDef);
		} else if (interval == TimeInterval.Daily) {

			return MergeYearAndMonthAndDayColumns(dt, groupByDef);
		} else if (interval == TimeInterval.Hourly) {

			return MergeYearAndMonthAndDayAndHourColumns(dt, groupByDef);
		}

		return null;

	}

	public static DataTable MergeIntervalColumns(DataTable dt,
			TimeInterval groupByInterval, TimeInterval subGroupByInterval) {
		DataTable table = null;
		if (groupByInterval == TimeInterval.Quarterly) {

			table = MergeYearAndQuarterColumnsForGroupByIn2DateTimeFields(dt);
			// NOTICE: break ignore!!!
		} else if (groupByInterval == TimeInterval.Monthly) {

			table = MergeYearAndMonthColumnsForGroupByIn2DateTimeFields(dt);
			// NOTICE: break ignore!!!
		} else if (groupByInterval == TimeInterval.Weekly) {

			table = MergeYearAndMonthAndWeekColumnsForGroupByIn2DateTimeFields(dt);
			// NOTICE: break ignore!!!
		} else if (groupByInterval == TimeInterval.Daily) {

			table = MergeYearAndMonthAndDayColumnsForGroupByIn2DateTimeFields(dt);
			// NOTICE: break ignore!!!
		} else if (groupByInterval == TimeInterval.Hourly) {

			table = MergeYearAndMonthAndDayAndHourColumnsForGroupByIn2DateTimeFields(dt);
			// NOTICE: break ignore!!!
		}
		if (subGroupByInterval == TimeInterval.Quarterly) {

			return MergeYearAndQuarterColumnsForSubGroupByIn2DateTimeFields(table);
		} else if (subGroupByInterval == TimeInterval.Monthly) {

			return MergeYearAndMonthColumnsForSubGroupByIn2DateTimeFields(table);
		} else if (subGroupByInterval == TimeInterval.Weekly) {

			return MergeYearAndMonthAndWeekColumnsForSubGroupByIn2DateTimeFields(table);
		} else if (subGroupByInterval == TimeInterval.Daily) {

			return MergeYearAndMonthAndDayColumnsForSubGroupByIn2DateTimeFields(table);
		} else if (subGroupByInterval == TimeInterval.Hourly) {

			return MergeYearAndMonthAndDayAndHourColumnsForSubGroupByIn2DateTimeFields(table);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndDayAndHourColumns(
			DataTable dt, GroupByDef groupByDef) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((((!dt.get_Columns().get_Item(i).get_Caption()
					.equals(TimeInterval.Monthly.toString()))) && ((!dt
					.get_Columns().get_Item(i).get_Caption()
					.equals(TimeInterval.Daily.toString()))))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals(TimeInterval.Hourly.toString())))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals(TimeInterval.Yearly.toString())) {
					column = new DataColumn("Year/Month/Day/Hour", dt
							.get_Columns().get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns()
				.IndexOf(TimeInterval.Yearly.toString());
		Integer num3 = dt.get_Columns()
				.IndexOf(TimeInterval.Monthly.toString());
		Integer num4 = dt.get_Columns().IndexOf(TimeInterval.Daily.toString());
		Integer num5 = dt.get_Columns().IndexOf(TimeInterval.Hourly.toString());
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (!groupByDef.get_LimitQuery()) {
				for (Integer j = 0; j < index; j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
				String dateSeparator = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				Integer num7 = XmlMonthIntervalCategory
						.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
								MonthInterval.class,
								row.get_ItemArray()[num3].toString()));
				Integer hour = Convert.ToInt32(GetHourNumber(row
						.get_ItemArray()[num5].toString()));
				DateTime time = new DateTime();
				time.__Ctor__(1, 1, 1, hour, 0, 0);
				String str2 = time.ToString(GetHourFormat()).trim();
				row2.set_Item(index, String.format(
						"%s%s%s%s%s %s",
						row.get_ItemArray()[index],
								dateSeparator, num7, dateSeparator,
								row.get_ItemArray()[num4], str2 ));
				for (Integer k = num5 + 1; k < dt.get_Columns().get_Count(); k++) {
					row2.set_Item(k - 3, row.get_ItemArray()[k]);
				}
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) == groupByDef
							.get_NumberOfRecordToReturn())) {
				row2.set_Item(index, groupByDef.get_OthersName());
				row2.set_Item(1, row.get_ItemArray()[4]);
				row2.set_Item(2, row.get_ItemArray()[5]);
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) > groupByDef
							.get_NumberOfRecordToReturn())) {
				row2 = table.get_Rows().get_Item(
						groupByDef.get_NumberOfRecordToReturn());
				row2.set_Item(
						1,
						Convert.ToInt32(row2.get_Item(1))
								+ Convert.ToInt32(row.get_ItemArray()[4]));
			} else {
				for (Integer m = 0; m < index; m++) {
					row2.set_Item(m, row.get_ItemArray()[m]);
				}
				String str3 = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				Integer num11 = XmlMonthIntervalCategory
						.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
								MonthInterval.class,
								row.get_ItemArray()[num3].toString()));
				Integer num12 = Convert.ToInt32(GetHourNumber(row
						.get_ItemArray()[num5].toString()));
				DateTime time2 = new DateTime();
				time2.__Ctor__(1, 1, 1, num12, 0, 0);
				String str4 = time2.ToString(GetHourFormat()).trim();
				row2.set_Item(index, String.format("%s%s%d%s%s %s",
						 row.get_ItemArray()[index],
								str3, num11, str3, row.get_ItemArray()[num4],
								str4 ));
				for (Integer n = num5 + 1; n < dt.get_Columns().get_Count(); n++) {
					row2.set_Item(n - 3, row.get_ItemArray()[n]);
				}
			}
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()) {
					if (dt.get_Rows().IndexOf(row) <= groupByDef
							.get_NumberOfRecordToReturn()) {
						table.get_Rows().Add(row2);
					}
				} else if (dt.get_Rows().IndexOf(row) < groupByDef
						.get_NumberOfRecordToReturn()) {
					table.get_Rows().Add(row2);
				}
				continue;
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndDayAndHourColumnsForGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "1")))) && ((!dt
					.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Daily.toString() + "1")))))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals((TimeInterval.Hourly.toString() + "1"))))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "1"))) {
					column = new DataColumn("Year/Month/Day/Hour1", dt
							.get_Columns().get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "1");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "1");
		Integer num4 = dt.get_Columns().IndexOf(
				TimeInterval.Daily.toString() + "1");
		Integer num5 = dt.get_Columns().IndexOf(
				TimeInterval.Hourly.toString() + "1");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			Integer num7 = XmlMonthIntervalCategory
					.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
							MonthInterval.class,
							row.get_ItemArray()[num3].toString()));
			Integer hour = Convert
					.ToInt32(GetHourNumber(row.get_ItemArray()[num5].toString()));
			DateTime time = new DateTime();
			time.__Ctor__(1, 1, 1, hour, 0, 0);
			String str2 = time.ToString(GetHourFormat()).trim();
			row2.set_Item(index, String.format(
					"%s%s%d%s%s %s",
					row.get_ItemArray()[index],
							dateSeparator, num7, dateSeparator,
							row.get_ItemArray()[num4], str2 ));
			for (Integer k = num5 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 3, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndDayAndHourColumnsForSubGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "2")))) && ((!dt
					.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Daily.toString() + "2")))))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals((TimeInterval.Hourly.toString() + "2"))))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "2"))) {
					column = new DataColumn("Year/Month/Day/Hour2", dt
							.get_Columns().get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "2");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "2");
		Integer num4 = dt.get_Columns().IndexOf(
				TimeInterval.Daily.toString() + "2");
		Integer num5 = dt.get_Columns().IndexOf(
				TimeInterval.Hourly.toString() + "2");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			Integer num7 = XmlMonthIntervalCategory
					.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
							MonthInterval.class,
							row.get_ItemArray()[num3].toString()));
			Integer hour = Convert
					.ToInt32(GetHourNumber(row.get_ItemArray()[num5].toString()));
			DateTime time = new DateTime();
			time.__Ctor__(1, 1, 1, hour, 0, 0);
			String str2 = time.ToString(GetHourFormat()).trim();
			row2.set_Item(index, String.format(
					"%s%s%d%s%s %s",
					row.get_ItemArray()[index],
							dateSeparator, num7, dateSeparator,
							row.get_ItemArray()[num4], str2));
			for (Integer k = num5 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 3, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndDayColumns(DataTable dt,
			GroupByDef groupByDef) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (((!dt.get_Columns().get_Item(i).get_Caption()
					.equals(TimeInterval.Monthly.toString())))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals(TimeInterval.Daily.toString())))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals(TimeInterval.Yearly.toString())) {
					column = new DataColumn("Month/Day/Year", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns()
				.IndexOf(TimeInterval.Yearly.toString());
		Integer num3 = dt.get_Columns()
				.IndexOf(TimeInterval.Monthly.toString());
		Integer num4 = dt.get_Columns().IndexOf(TimeInterval.Daily.toString());
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (!groupByDef.get_LimitQuery()) {
				for (Integer j = 0; j < index; j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
				Integer year = Convert.ToInt32(row.get_ItemArray()[index]);
				Integer month = XmlMonthIntervalCategory
						.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
								MonthInterval.class,
								row.get_ItemArray()[num3].toString()));
				Integer day = Convert.ToInt32(row.get_ItemArray()[num4]);
				DateTime dt2 = new DateTime();
				dt2.__Ctor__(year, month, day);
				row2.set_Item(index, dt2.ToShortDateString());
				for (Integer k = num4 + 1; k < dt.get_Columns().get_Count(); k++) {
					row2.set_Item(k - 2, row.get_ItemArray()[k]);
				}
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) == groupByDef
							.get_NumberOfRecordToReturn())) {
				row2.set_Item(index, groupByDef.get_OthersName());
				row2.set_Item(1, row.get_ItemArray()[3]);
				row2.set_Item(2, row.get_ItemArray()[4]);
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) > groupByDef
							.get_NumberOfRecordToReturn())) {
				row2 = table.get_Rows().get_Item(
						groupByDef.get_NumberOfRecordToReturn());
				row2.set_Item(
						1,
						Convert.ToInt32(row2.get_Item(1))
								+ Convert.ToInt32(row.get_ItemArray()[3]));
			} else {
				for (Integer m = 0; m < index; m++) {
					row2.set_Item(m, row.get_ItemArray()[m]);
				}
				Integer num11 = Convert.ToInt32(row.get_ItemArray()[index]);
				Integer num12 = XmlMonthIntervalCategory
						.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
								MonthInterval.class,
								row.get_ItemArray()[num3].toString()));
				Integer num13 = Convert.ToInt32(row.get_ItemArray()[num4]);
				DateTime dt2 = new DateTime();
				dt2.__Ctor__(num11, num12, num13);
				row2.set_Item(index, dt2.ToShortDateString());
				for (Integer n = num4 + 1; n < dt.get_Columns().get_Count(); n++) {
					row2.set_Item(n - 2, row.get_ItemArray()[n]);
				}
			}
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()) {
					if (dt.get_Rows().IndexOf(row) <= groupByDef
							.get_NumberOfRecordToReturn()) {
						table.get_Rows().Add(row2);
					}
				} else if (dt.get_Rows().IndexOf(row) < groupByDef
						.get_NumberOfRecordToReturn()) {
					table.get_Rows().Add(row2);
				}
				continue;
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndDayColumnsForGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "1"))))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals((TimeInterval.Daily.toString() + "1"))))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "1"))) {
					column = new DataColumn("Month/Day/Year1", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "1");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "1");
		Integer num4 = dt.get_Columns().IndexOf(
				TimeInterval.Daily.toString() + "1");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			Integer year = Convert.ToInt32(row.get_ItemArray()[index]);
			Integer month = XmlMonthIntervalCategory
					.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
							MonthInterval.class,
							row.get_ItemArray()[num3].toString()));
			Integer day = Convert.ToInt32(row.get_ItemArray()[num4]);
			DateTime dt2 = new DateTime();
			dt2.__Ctor__(year, month, day);
			row2.set_Item(index, dt2.ToShortDateString());
			for (Integer k = num4 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 2, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndDayColumnsForSubGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "2"))))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals((TimeInterval.Daily.toString() + "2"))))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "2"))) {
					column = new DataColumn("Month/Day/Year2", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "2");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "2");
		Integer num4 = dt.get_Columns().IndexOf(
				TimeInterval.Daily.toString() + "2");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			Integer year = Convert.ToInt32(row.get_ItemArray()[index]);
			Integer month = XmlMonthIntervalCategory
					.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
							MonthInterval.class,
							row.get_ItemArray()[num3].toString()));
			Integer day = Convert.ToInt32(row.get_ItemArray()[num4]);
			DateTime dt2 = new DateTime();
			dt2.__Ctor__(year, month, day);
			row2.set_Item(index, dt2.ToShortDateString());
			for (Integer k = num4 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 2, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndWeekColumns(DataTable dt,
			GroupByDef groupByDef) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (((!dt.get_Columns().get_Item(i).get_Caption()
					.equals(TimeInterval.Monthly.toString())))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals(TimeInterval.Weekly.toString())))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals(TimeInterval.Yearly.toString())) {
					column = new DataColumn("Week/Month/Year", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns()
				.IndexOf(TimeInterval.Yearly.toString());
		Integer num3 = dt.get_Columns()
				.IndexOf(TimeInterval.Monthly.toString());
		Integer num4 = dt.get_Columns().IndexOf(TimeInterval.Weekly.toString());
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (!groupByDef.get_LimitQuery()) {
				for (Integer j = 0; j < index; j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
				String dateSeparator = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				row2.set_Item(index, String.format("%s%s%s%s%s",
						row.get_ItemArray()[num4],
								dateSeparator, row.get_ItemArray()[num3],
								dateSeparator, row.get_ItemArray()[index]));
				for (Integer k = num4 + 1; k < dt.get_Columns().get_Count(); k++) {
					row2.set_Item(k - 2, row.get_ItemArray()[k]);
				}
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) == groupByDef
							.get_NumberOfRecordToReturn())) {
				row2.set_Item(index, groupByDef.get_OthersName());
				row2.set_Item(1, row.get_ItemArray()[3]);
				row2.set_Item(2, row.get_ItemArray()[4]);
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) > groupByDef
							.get_NumberOfRecordToReturn())) {
				row2 = table.get_Rows().get_Item(
						groupByDef.get_NumberOfRecordToReturn());
				row2.set_Item(
						1,
						Convert.ToInt32(row2.get_Item(1))
								+ Convert.ToInt32(row.get_ItemArray()[3]));
			} else {
				for (Integer m = 0; m < index; m++) {
					row2.set_Item(m, row.get_ItemArray()[m]);
				}
				String str2 = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				row2.set_Item(index, String.format(
						"{0}{1}{2}{3}{4}",
						row.get_ItemArray()[num4],
								str2, row.get_ItemArray()[num3], str2,
								row.get_ItemArray()[index] ));
				for (Integer n = num4 + 1; n < dt.get_Columns().get_Count(); n++) {
					row2.set_Item(n - 2, row.get_ItemArray()[n]);
				}
			}
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()) {
					if (dt.get_Rows().IndexOf(row) <= groupByDef
							.get_NumberOfRecordToReturn()) {
						table.get_Rows().Add(row2);
					}
				} else if (dt.get_Rows().IndexOf(row) < groupByDef
						.get_NumberOfRecordToReturn()) {
					table.get_Rows().Add(row2);
				}
				continue;
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndWeekColumnsForGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "1"))))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals((TimeInterval.Weekly.toString() + "1"))))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "1"))) {
					column = new DataColumn("Week/Month/Year1", dt
							.get_Columns().get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "1");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "1");
		Integer num4 = dt.get_Columns().IndexOf(
				TimeInterval.Weekly.toString() + "1");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			row2.set_Item(index, String.format("%s%s%s%s%s",
					row.get_ItemArray()[num4],
							dateSeparator, row.get_ItemArray()[num3],
							dateSeparator, row.get_ItemArray()[index]));
			for (Integer k = num4 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 2, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthAndWeekColumnsForSubGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if (((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "2"))))
					&& ((!dt.get_Columns().get_Item(i).get_Caption()
							.equals((TimeInterval.Weekly.toString() + "2"))))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "2"))) {
					column = new DataColumn("Week/Month/Year2", dt
							.get_Columns().get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "2");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "2");
		Integer num4 = dt.get_Columns().IndexOf(
				TimeInterval.Weekly.toString() + "2");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			row2.set_Item(index, String.format("%s%s%s%s%s",
					row.get_ItemArray()[num4],
							dateSeparator, row.get_ItemArray()[num3],
							dateSeparator, row.get_ItemArray()[index] ));
			for (Integer k = num4 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 2, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthColumns(DataTable dt,
			GroupByDef groupByDef) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((!dt.get_Columns().get_Item(i).get_Caption()
					.equals(TimeInterval.Monthly.toString()))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals(TimeInterval.Yearly.toString())) {
					column = new DataColumn("Month/Year", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns()
				.IndexOf(TimeInterval.Yearly.toString());
		Integer num3 = dt.get_Columns()
				.IndexOf(TimeInterval.Monthly.toString());
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (!groupByDef.get_LimitQuery()) {
				for (Integer j = 0; j < index; j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
				String dateSeparator = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				row2.set_Item(index, String.format("%s%s%s",
						row.get_ItemArray()[num3], dateSeparator,
						row.get_ItemArray()[index]));
				for (Integer k = num3 + 1; k < dt.get_Columns().get_Count(); k++) {
					row2.set_Item(k - 1, row.get_ItemArray()[k]);
				}
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) == groupByDef
							.get_NumberOfRecordToReturn())) {
				row2.set_Item(index, groupByDef.get_OthersName());
				row2.set_Item(1, row.get_ItemArray()[2]);
				row2.set_Item(2, row.get_ItemArray()[3]);
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) > groupByDef
							.get_NumberOfRecordToReturn())) {
				row2 = table.get_Rows().get_Item(
						groupByDef.get_NumberOfRecordToReturn());
				row2.set_Item(
						1,
						Convert.ToInt32(row2.get_Item(1))
								+ Convert.ToInt32(row.get_ItemArray()[2]));
			} else {
				for (Integer m = 0; m < index; m++) {
					row2.set_Item(m, row.get_ItemArray()[m]);
				}
				String str2 = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				row2.set_Item(index, String.format("%s%s%s",
						row.get_ItemArray()[num3], str2,
						row.get_ItemArray()[index]));
				for (Integer n = num3 + 1; n < dt.get_Columns().get_Count(); n++) {
					row2.set_Item(n - 1, row.get_ItemArray()[n]);
				}
			}
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()) {
					if (dt.get_Rows().IndexOf(row) <= groupByDef
							.get_NumberOfRecordToReturn()) {
						table.get_Rows().Add(row2);
					}
				} else if (dt.get_Rows().IndexOf(row) < groupByDef
						.get_NumberOfRecordToReturn()) {
					table.get_Rows().Add(row2);
				}
				continue;
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthColumnsForGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "1")))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "1"))) {
					column = new DataColumn("Month/Year1", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "1");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "1");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			row2.set_Item(index, String.format("%s%s%s",
					row.get_ItemArray()[num3], dateSeparator,
					row.get_ItemArray()[index]));
			for (Integer k = num3 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 1, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndMonthColumnsForSubGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Monthly.toString() + "2")))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "2"))) {
					column = new DataColumn("Month/Year2", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "2");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Monthly.toString() + "2");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			row2.set_Item(index, String.format("%s%s%s",
					row.get_ItemArray()[num3], dateSeparator,
					row.get_ItemArray()[index]));
			for (Integer k = num3 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 1, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndQuarterColumns(DataTable dt,
			GroupByDef groupByDef) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((!dt.get_Columns().get_Item(i).get_Caption()
					.equals(TimeInterval.Quarterly.toString()))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals(TimeInterval.Yearly.toString())) {
					column = new DataColumn("Quarter/Year", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns()
				.IndexOf(TimeInterval.Yearly.toString());
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Quarterly.toString());
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (!groupByDef.get_LimitQuery()) {
				for (Integer j = 0; j < index; j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
				String dateSeparator = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				row2.set_Item(index, String.format("%s%s%s",
						row.get_ItemArray()[num3], dateSeparator,
						row.get_ItemArray()[index]));
				for (Integer k = num3 + 1; k < dt.get_Columns().get_Count(); k++) {
					row2.set_Item(k - 1, row.get_ItemArray()[k]);
				}
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) == groupByDef
							.get_NumberOfRecordToReturn())) {
				row2.set_Item(index, groupByDef.get_OthersName());
				row2.set_Item(1, row.get_ItemArray()[2]);
				row2.set_Item(2, row.get_ItemArray()[3]);
			} else if (groupByDef.get_IncludeOthers()
					&& (dt.get_Rows().IndexOf(row) > groupByDef
							.get_NumberOfRecordToReturn())) {
				row2 = table.get_Rows().get_Item(
						groupByDef.get_NumberOfRecordToReturn());
				row2.set_Item(
						1,
						Convert.ToInt32(row2.get_Item(1))
								+ Convert.ToInt32(row.get_ItemArray()[2]));
			} else {
				for (Integer m = 0; m < index; m++) {
					row2.set_Item(m, row.get_ItemArray()[m]);
				}
				String str2 = CultureInfo.get_CurrentCulture()
						.get_DateTimeFormat().get_DateSeparator();
				row2.set_Item(index, String.format("%s%s%s",
						row.get_ItemArray()[num3], str2,
						row.get_ItemArray()[index]));
				for (Integer n = num3 + 1; n < dt.get_Columns().get_Count(); n++) {
					row2.set_Item(n - 1, row.get_ItemArray()[n]);
				}
			}
			if (groupByDef.get_LimitQuery()) {
				if (groupByDef.get_IncludeOthers()) {
					if (dt.get_Rows().IndexOf(row) <= groupByDef
							.get_NumberOfRecordToReturn()) {
						table.get_Rows().Add(row2);
					}
				} else if (dt.get_Rows().IndexOf(row) < groupByDef
						.get_NumberOfRecordToReturn()) {
					table.get_Rows().Add(row2);
				}
				continue;
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndQuarterColumnsForGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Quarterly.toString() + "1")))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "1"))) {
					column = new DataColumn("Quarter/Year1", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "1");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Quarterly.toString() + "1");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			row2.set_Item(index, String.format("%s%s%s",
					row.get_ItemArray()[num3], dateSeparator,
					row.get_ItemArray()[index]));
			for (Integer k = num3 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 1, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable MergeYearAndQuarterColumnsForSubGroupByIn2DateTimeFields(
			DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			if ((!dt.get_Columns().get_Item(i).get_Caption()
					.equals((TimeInterval.Quarterly.toString() + "2")))) {
				DataColumn column = null;
				if (dt.get_Columns().get_Item(i).get_Caption()
						.equals((TimeInterval.Yearly.toString() + "2"))) {
					column = new DataColumn("Quarter/Year2", dt.get_Columns()
							.get_Item(i).get_DataType());
				} else {
					column = new DataColumn(dt.get_Columns().get_Item(i)
							.get_Caption(), dt.get_Columns().get_Item(i)
							.get_DataType());
				}
				table.get_Columns().Add(column);
			}
		}
		Integer index = dt.get_Columns().IndexOf(
				TimeInterval.Yearly.toString() + "2");
		Integer num3 = dt.get_Columns().IndexOf(
				TimeInterval.Quarterly.toString() + "2");
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			for (Integer j = 0; j < index; j++) {
				row2.set_Item(j, row.get_ItemArray()[j]);
			}
			String dateSeparator = CultureInfo.get_CurrentCulture()
					.get_DateTimeFormat().get_DateSeparator();
			row2.set_Item(index, String.format("%s%s%s",
					row.get_ItemArray()[num3], dateSeparator,
					row.get_ItemArray()[index]));
			for (Integer k = num3 + 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k - 1, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return table;

	}

	public static double MinValueFromDataTable(DataTable dt) {
		double num = 0.0;
		IEnumerator it1 = dt.get_Columns().GetEnumerator();
		while (it1.MoveNext()) {
			DataColumn column = (DataColumn) it1.get_Current();
			if ((column.get_DataType() != Type.GetType(String.class.getName()))
					&& (column.get_DataType() != Type.GetType(DateTime.class
							.getName()))) {
				Integer ordinal = column.get_Ordinal();
				IEnumerator it2 = dt.get_Rows().GetEnumerator();
				while (it2.MoveNext()) {
					DataRow row = (DataRow) it2.get_Current();
					try {
						double num3 = Convert
								.ToDouble(row.get_ItemArray()[ordinal]);
						if (num3 < num) {
							num = num3;
						}
						continue;
					} catch (java.lang.Exception e) {
						continue;
					}
				}
				continue;
			}
		}

		return num;

	}

	public static DataTable RemoveDBNullRows(DataTable dt, Integer columnIndex) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					dt.get_Columns().get_Item(i).get_DataType());
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (row.get_ItemArray()[columnIndex] instanceof system.DBNull)
				continue;
			if (row.get_ItemArray()[columnIndex] instanceof String && row.get_ItemArray()[columnIndex].equals(""))
				continue;
			
//			if (!(row.get_ItemArray()[columnIndex] instanceof system.DBNull)
//					&& !(row.get_ItemArray()[columnIndex] instanceof String
//							) || (!((String) row
//							.get_ItemArray()[columnIndex]).equals(" "))){
				for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
				table.get_Rows().Add(row2);
//			}
		}

		return table;

	}

	public static DataTable RemoveOrderByColumn(DataTable dt,
			GroupByDef groupByDef, GroupByDef subGroupByDef,
			GroupByDef evaluateByDef) {
		if (!subGroupByDef.get_Apply()) {

			dt = RemoveOrderByColumnForOneGroupBy(dt, groupByDef,
					subGroupByDef, evaluateByDef);
		}

		return dt;

	}

	private static  DataTable RemoveOrderByColumnForOneGroupBy(DataTable dt, GroupByDef groupByDef, GroupByDef subGroupByDef, GroupByDef evaluateByDef)
    {
        DataTable table = new DataTable();
        DataRow row = null;
        for(Integer i = 0;i < dt.get_Columns().get_Count();i++){
            DataColumn column = null;
            if(!subGroupByDef.get_Apply() && !groupByDef.get_Duration()){
                if(groupByDef.get_DateTimeField()){
                    if((groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
                        if(i == 0){
                            continue;
                        }
                        else if(i == 1){
                            column = new DataColumn(TimeInterval.Yearly.toString(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 2){
                            column = new DataColumn(groupByDef.get_Interval(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 3){
                            column = new DataColumn(SetQueryFunctionColumn(evaluateByDef.get_QueryFunction().toString()), Type.GetType("System.Int32"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(), dt.get_Columns().get_Item(i).get_DataType());
                    }
                    else if((groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
                        if(i == 0){
                            continue;
                        }
                        else if(i == 1){
                            column = new DataColumn(TimeInterval.Yearly.toString(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 2){
                            column = new DataColumn(TimeInterval.Monthly.toString(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 3){
                            column = new DataColumn(groupByDef.get_Interval(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 4){
                            column = new DataColumn(SetQueryFunctionColumn(evaluateByDef.get_QueryFunction().toString()), Type.GetType("System.Int32"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(), dt.get_Columns().get_Item(i).get_DataType());
                    }
                    else if(groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
                        if(i == 0){
                            continue;
                        }
                        else if(i == 1){
                            column = new DataColumn(TimeInterval.Yearly.toString(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 2){
                            column = new DataColumn(TimeInterval.Monthly.toString(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 3){
                            column = new DataColumn(TimeInterval.Daily.toString(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 4){
                            column = new DataColumn(groupByDef.get_Interval(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 5){
                            column = new DataColumn(SetQueryFunctionColumn(evaluateByDef.get_QueryFunction().toString()), Type.GetType("System.Int32"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(), dt.get_Columns().get_Item(i).get_DataType());
                    }
                    else{
                        if(i == 0){
                            continue;
                        }
                        else if(i == 1){
                            column = new DataColumn(groupByDef.get_Interval(), Type.GetType("System.String"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        else if(i == 2){
                            column = new DataColumn(SetQueryFunctionColumn(evaluateByDef.get_QueryFunction().toString()), Type.GetType("System.Int32"));
                            table.get_Columns().Add(column);
                            continue;
                        }
                        column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(), dt.get_Columns().get_Item(i).get_DataType());
                    }
                }
                else{
                    if(i == 0){
                        column = new DataColumn(groupByDef.get_FieldName(), Type.GetType("System.String"));
                        table.get_Columns().Add(column);
                        continue;
                    }
                    else if(i == 1){
                        continue;
                    }
                    else if(i == 2){
                        column = new DataColumn(SetQueryFunctionColumn(evaluateByDef.get_QueryFunction().toString()), Type.GetType("System.Int32"));
                        table.get_Columns().Add(column);
                        continue;
                    }
                    column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(), dt.get_Columns().get_Item(i).get_DataType());
                }
            }
Label_03E1:            
			table.get_Columns().Add(column);
        }
        IEnumerator it1 = dt.get_Rows().GetEnumerator();
        while(it1.MoveNext()){
            DataRow row2 = (DataRow)it1.get_Current();
            if(groupByDef.get_Duration()){
                continue;
            }
            if(groupByDef.get_DateTimeField()){
                
                row = table.NewRow();
                if((groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Monthly))) || (groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Quarterly)))){
                    if(row2.get_ItemArray().length > 4){
                        row.set_Item(0,row2.get_ItemArray()[1]);
                        row.set_Item(1,row2.get_ItemArray()[2]);
                        row.set_Item(2,row2.get_ItemArray()[3]);
                        row.set_Item(3,row2.get_ItemArray()[4]);
                        table.get_Rows().Add(row);
                    }
                }
                else if((groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Weekly))) || (groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Daily)))){
                    if(row2.get_ItemArray().length > 5){
                        row.set_Item(0,row2.get_ItemArray()[1]);
                        row.set_Item(1,row2.get_ItemArray()[2]);
                        row.set_Item(2,row2.get_ItemArray()[3]);
                        row.set_Item(3,row2.get_ItemArray()[4]);
                        row.set_Item(4,row2.get_ItemArray()[5]);
                        table.get_Rows().Add(row);
                    }
                }
                else if(groupByDef.get_Interval().equals(XmlTimeIntervalCategory.ToString(TimeInterval.Hourly))){
                    if(row2.get_ItemArray().length > 6){
                        row.set_Item(0,row2.get_ItemArray()[1]);
                        row.set_Item(1,row2.get_ItemArray()[2]);
                        row.set_Item(2,row2.get_ItemArray()[3]);
                        row.set_Item(3,row2.get_ItemArray()[4]);
                        row.set_Item(4,row2.get_ItemArray()[5]);
                        row.set_Item(5,row2.get_ItemArray()[6]);
                        table.get_Rows().Add(row);
                    }
                }
                else if(row2.get_ItemArray().length > 3){
                    row.set_Item(0,row2.get_ItemArray()[1]);
                    row.set_Item(1,row2.get_ItemArray()[2]);
                    row.set_Item(2,row2.get_ItemArray()[3]);
                    table.get_Rows().Add(row);
                }
                continue;
            }
            
            row = table.NewRow();
            if(row2.get_ItemArray().length > 3){
                row.set_Item(0,row2.get_ItemArray()[0]);
                row.set_Item(1,row2.get_ItemArray()[2]);
                row.set_Item(2,row2.get_ItemArray()[3]);
                table.get_Rows().Add(row);
            }
        }

        return table;

    }

	public static DataTable RemoveWrongRows(DataTable dt, Integer columnIndex) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					dt.get_Columns().get_Item(i).get_DataType());
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			if (!(row.get_ItemArray()[columnIndex] instanceof system.DBNull)
					&& (Convert.ToInt32(row.get_ItemArray()[columnIndex]) >= 0)) {
				for (Integer j = 0; j < dt.get_Columns().get_Count(); j++) {
					row2.set_Item(j, row.get_ItemArray()[j]);
				}
				table.get_Rows().Add(row2);
			}
		}

		return table;

	}

	public static DataSet RotateDataSet(DataSet myDataSet) {
		DataSet set = new DataSet();
		IEnumerator it1 = myDataSet.get_Tables().GetEnumerator();
		while (it1.MoveNext()) {
			DataTable table = (DataTable) it1.get_Current();
			DataTable table2 = new DataTable();
			for (Integer i = 0; i <= table.get_Rows().get_Count(); i++) {
				table2.get_Columns().Add(Convert.ToString(i));
			}
			for (Integer j = 0; j < table.get_Columns().get_Count(); j++) {
				DataRow row = table2.NewRow();
				row.set_Item(0, table.get_Columns().get_Item(j).toString());
				for (Integer k = 1; k <= table.get_Rows().get_Count(); k++) {
					row.set_Item(k, table.get_Rows().get_Item(k - 1)
							.get_Item(j));
				}
				table2.get_Rows().Add(row);
			}
			set.get_Tables().Add(table2);
		}

		return set;

	}

	private Integer SetCellData(DataRow dr, Integer weekIndex) {
		if (weekIndex != -1) {

			return Convert.ToInt32(dr.get_ItemArray()[weekIndex]);
		}

		return 0;

	}

	private static String SetHourName(String num) {
		String str = "";
		if (Convert.ToInt32(num) == 0) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.TwelveAM));
		} else if (Convert.ToInt32(num) == 1) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.OneAM));
		} else if (Convert.ToInt32(num) == 2) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.TwoAM));
		} else if (Convert.ToInt32(num) == 3) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.ThreeAM));
		} else if (Convert.ToInt32(num) == 4) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.FourAM));
		} else if (Convert.ToInt32(num) == 5) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.FiveAM));
		} else if (Convert.ToInt32(num) == 6) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.SixAM));
		} else if (Convert.ToInt32(num) == 7) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.SevenAM));
		} else if (Convert.ToInt32(num) == 8) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.EightAM));
		} else if (Convert.ToInt32(num) == 9) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.NineAM));
		} else if (Convert.ToInt32(num) == 10) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.TenAM));
		} else if (Convert.ToInt32(num) == 11) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.ElevenAM));
		} else if (Convert.ToInt32(num) == 12) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.TwelvePM));
		} else if (Convert.ToInt32(num) == 13) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.OnePM));
		} else if (Convert.ToInt32(num) == 14) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.TwoPM));
		} else if (Convert.ToInt32(num) == 15) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.ThreePM));
		} else if (Convert.ToInt32(num) == 0x10) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.FourPM));
		} else if (Convert.ToInt32(num) == 0x11) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.FivePM));
		} else if (Convert.ToInt32(num) == 0x12) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.SixPM));
		} else if (Convert.ToInt32(num) == 0x13) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.SevenPM));
		} else if (Convert.ToInt32(num) == 20) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.EightPM));
		} else if (Convert.ToInt32(num) == 0x15) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.NinePM));
		} else if (Convert.ToInt32(num) == 0x16) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.TenPM));
		} else if (Convert.ToInt32(num) == 0x17) {

			return Res.get_Default().GetString(
					XmlHourIntervalCategory.ToString(HourInterval.ElevenPM));
		}

		return str;

	}

	private static String SetMonthName(String num) {
		String str = "";
		if (Convert.ToInt32(num) == 1) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Jan));
		} else if (Convert.ToInt32(num) == 2) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Feb));
		} else if (Convert.ToInt32(num) == 3) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Mar));
		} else if (Convert.ToInt32(num) == 4) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Apr));
		} else if (Convert.ToInt32(num) == 5) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.May));
		} else if (Convert.ToInt32(num) == 6) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Jun));
		} else if (Convert.ToInt32(num) == 7) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Jul));
		} else if (Convert.ToInt32(num) == 8) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Aug));
		} else if (Convert.ToInt32(num) == 9) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Sep));
		} else if (Convert.ToInt32(num) == 10) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Oct));
		} else if (Convert.ToInt32(num) == 11) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Nov));
		} else if (Convert.ToInt32(num) == 12) {

			return Res.get_Default().GetString(
					XmlMonthIntervalCategory.ToString(MonthInterval.Dec));
		}

		return str;

	}

	private static String SetQuarterName(String num) {
		String str = "";
		if (Convert.ToInt32(num) == 1) {

			return Res.get_Default().GetString(
					XmlQuarterIntervalCategory.ToString(QuarterInterval.Q1));
		} else if (Convert.ToInt32(num) == 2) {

			return Res.get_Default().GetString(
					XmlQuarterIntervalCategory.ToString(QuarterInterval.Q2));
		} else if (Convert.ToInt32(num) == 3) {

			return Res.get_Default().GetString(
					XmlQuarterIntervalCategory.ToString(QuarterInterval.Q3));
		} else if (Convert.ToInt32(num) == 4) {

			return Res.get_Default().GetString(
					XmlQuarterIntervalCategory.ToString(QuarterInterval.Q4));
		}

		return str;

	}

	private static String SetQueryFunctionColumn(String queryFunction) {
		String str = "";
		if (queryFunction == "AverageAllValuesFunction"
				|| queryFunction == "AverageDistinctValuesFunction") {

			return Res.get_Default().GetString(
					"Chart.EvaluateByFieldAverage");
		} else if (queryFunction == "MaxValueFunction") {

			return Res.get_Default().GetString(
					"Chart.EvaluateByFieldMax");
		} else if (queryFunction == "MinValueFunction") {

			return Res.get_Default().GetString(
					"Chart.EvaluateByFieldMin");
		} else if (queryFunction == "SumOfAllValuesFunction"
				|| queryFunction == "SumOfDistinctValuesFunction") {

			return Res.get_Default().GetString(
					"Chart.EvaluateByFieldSum");
		} else if (queryFunction == "CountOfAllValuesFunction"
				|| queryFunction == "CountOfDistinctValuesFunction") {

			return Res.get_Default().GetString(
					"Chart.EvaluateByFieldCount");
		}

		return str;

	}

	private static String SetWeekName(String num) {
		if (Convert.ToInt32(num) == 1) {

			return LocalizeHelper.GetValue(
					WeekInterval.class,
					XmlWeekIntervalCategory.ToString(WeekInterval.W1));
		} else if (Convert.ToInt32(num) == 2) {

			return LocalizeHelper.GetValue(
					WeekInterval.class,
					XmlWeekIntervalCategory.ToString(WeekInterval.W2));
		} else if (Convert.ToInt32(num) == 3) {

			String str = LocalizeHelper.GetValue(
					WeekInterval.class,
					XmlWeekIntervalCategory.ToString(WeekInterval.W3));
			//return Res.get_Default().GetString("WeekInterval." + XmlWeekIntervalCategory.ToString(WeekInterval.W3));
			return str;
		} else if (Convert.ToInt32(num) == 4) {

			String str = LocalizeHelper.GetValue(
					WeekInterval.class,
					XmlWeekIntervalCategory.ToString(WeekInterval.W4));
			//return Res.get_Default().GetString("WeekInterval." + XmlWeekIntervalCategory.ToString(WeekInterval.W4));
			return str;
		} else if (Convert.ToInt32(num) == 5) {

			return LocalizeHelper.GetValue(
					WeekInterval.class,
					XmlWeekIntervalCategory.ToString(WeekInterval.W5));
		}

		return LocalizeHelper.GetValue(
				WeekInterval.class,
				XmlWeekIntervalCategory.ToString(WeekInterval.W6));

	}

	public static DataTable SortDateTimeColumns(DataTable dt,
			TimeInterval interval) {

		dt = FlipDataTableWith2GroupBy(dt);
		if (interval == TimeInterval.Yearly) {

			dt = SortRowLabels(dt, 1);
			// NOTICE: break ignore!!!
		} else if (interval == TimeInterval.Quarterly) {

			dt = SortYearAndQuarterRowLabels(dt);
			// NOTICE: break ignore!!!
		} else if (interval == TimeInterval.Monthly) {

			dt = SortYearAndMonthRowLabels(dt);
			// NOTICE: break ignore!!!
		} else if (interval == TimeInterval.Weekly) {

			dt = SortYearAndMonthAndWeekRowLabels(dt);
			// NOTICE: break ignore!!!
		} else if (interval == TimeInterval.Daily) {

			dt = SortYearAndMonthAndDayRowLabels(dt);
			// NOTICE: break ignore!!!
		} else if (interval == TimeInterval.Hourly) {

			dt = SortYearAndMonthAndDayAndHourRowLabels(dt);
			// NOTICE: break ignore!!!
		}

		dt = FlipDataTableWith2GroupBy(dt);

		return dt;

	}

	public static DataTable SortDateTimeRows(DataTable dt, TimeInterval interval) {
		if (interval == TimeInterval.Yearly) {

			dt = SortRowLabels(dt, 1);

			return dt;
		} else if (interval == TimeInterval.Quarterly) {

			dt = SortYearAndQuarterRowLabels(dt);

			return dt;
		} else if (interval == TimeInterval.Monthly) {

			dt = SortYearAndMonthRowLabels(dt);

			return dt;
		} else if (interval == TimeInterval.Weekly) {

			dt = SortYearAndMonthAndWeekRowLabels(dt);

			return dt;
		} else if (interval == TimeInterval.Daily) {

			dt = SortYearAndMonthAndDayRowLabels(dt);

			return dt;
		} else if (interval == TimeInterval.Hourly) {

			dt = SortYearAndMonthAndDayAndHourRowLabels(dt);

			return dt;
		}

		return dt;

	}

	private static DataTable SortRowLabels(DataTable dt, Integer sortColumnCount) {
		DataRow[] rowArray = null;
		if (sortColumnCount == 1) {

			rowArray = dt.Select("", dt.get_Columns().get_Item(0) + " ASC");
			// NOTICE: break ignore!!!
		} else if (sortColumnCount == 2) {

			rowArray = dt.Select("", clr.System.StringStaticWrapper
					.Concat(new java.lang.Object[] {
							dt.get_Columns().get_Item(0), " ASC , ",
							dt.get_Columns().get_Item(1), " ASC" }));
			// NOTICE: break ignore!!!
		} else if (sortColumnCount == 3) {

			rowArray = dt.Select("", clr.System.StringStaticWrapper
					.Concat(new java.lang.Object[] {
							dt.get_Columns().get_Item(0), " ASC , ",
							dt.get_Columns().get_Item(1), " ASC , ",
							dt.get_Columns().get_Item(2), " ASC" }));
			// NOTICE: break ignore!!!
		} else if (sortColumnCount == 4) {

			rowArray = dt.Select("", clr.System.StringStaticWrapper
					.Concat(new java.lang.Object[] {
							dt.get_Columns().get_Item(0), " ASC , ",
							dt.get_Columns().get_Item(1), " ASC , ",
							dt.get_Columns().get_Item(2), " ASC , ",
							dt.get_Columns().get_Item(3), " ASC" }));
			// NOTICE: break ignore!!!
		}
		DataTable table = new DataTable();
		for (Integer i = 0; i < dt.get_Columns().get_Count(); i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					dt.get_Columns().get_Item(i).get_DataType());
			table.get_Columns().Add(column);
		}
		for (Integer j = 0; j < dt.get_Rows().get_Count(); j++) {
			DataRow row = table.NewRow();
			for (Integer k = 0; k < dt.get_Columns().get_Count(); k++) {
				row.set_Item(k, rowArray[j].get_ItemArray()[k]);
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

	private DataTable SortWeekColumns(DataTable dt) {
		DataTable table = new DataTable();
		Integer index = this.GetIndex(dt, "1");
		Integer weekIndex = this.GetIndex(dt, "2");
		Integer num3 = this.GetIndex(dt, "3");
		Integer num4 = this.GetIndex(dt, "4");
		Integer num5 = this.GetIndex(dt, "5");
		Integer num6 = this.GetIndex(dt, "6");
		DataColumn column = null;
		column = new DataColumn(dt.get_Columns().get_Item(0).get_Caption(), dt
				.get_Columns().get_Item(0).get_DataType());
		table.get_Columns().Add(column);
		for (Integer i = 1; i < 7; i++) {
			column = null;
			column = new DataColumn(StringUtils.SetToken(Res.get_Default().GetString("DataConverter.WeekNumber"),
					"NUM", i), Type.GetType("System.Int32"));
			table.get_Columns().Add(column);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			row2.set_Item(0, row.get_ItemArray()[0]);
			row2.set_Item(1, this.SetCellData(row, index));
			row2.set_Item(2, this.SetCellData(row, weekIndex));
			row2.set_Item(3, this.SetCellData(row, num3));
			row2.set_Item(4, this.SetCellData(row, num4));
			row2.set_Item(5, this.SetCellData(row, num5));
			row2.set_Item(6, this.SetCellData(row, num6));
			table.get_Rows().Add(row2);
		}

		return table;

	}

	private static DataTable SortYearAndMonthAndDayAndHourRowLabels(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					Type.GetType("System.Int32"));
			table.get_Columns().Add(column);
		}
		DataColumn column2 = null;
		column2 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column2);
		DataColumn column3 = null;
		column3 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column3);
		DataColumn column4 = null;
		column4 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column4);
		for (Integer j = 1; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column5 = null;
			column5 = new DataColumn(
					dt.get_Columns().get_Item(j).get_Caption(), dt
							.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column5);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			String[] strArray = row.get_ItemArray()[0].toString().split("/");
			row2.set_Item(0, Convert.ToInt32(strArray[0]));
			row2.set_Item(1, Convert.ToInt32(strArray[1]));
			row2.set_Item(2, Convert.ToInt32(strArray[2]));
			row2.set_Item(3, XmlHourIntervalCategory
					.ToHourNumber((HourInterval) LocalizeHelper.Parse(
							HourInterval.class,
							strArray[3].toString())));
			for (Integer k = 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k + 3, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return ConvertBackToYearAndMonthAndDayAndHourName(SortRowLabels(table,
				4));

	}

	private static DataTable SortYearAndMonthAndDayRowLabels(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					Type.GetType("System.Int32"));
			table.get_Columns().Add(column);
		}
		DataColumn column2 = null;
		column2 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column2);
		DataColumn column3 = null;
		column3 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column3);
		for (Integer j = 1; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column4 = null;
			column4 = new DataColumn(
					dt.get_Columns().get_Item(j).get_Caption(), dt
							.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column4);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			String[] strArray = row.get_ItemArray()[0].toString().split("/");
			row2.set_Item(0, Convert.ToInt32(strArray[2]));
			row2.set_Item(1, Convert.ToInt32(strArray[0]));
			row2.set_Item(2, Convert.ToInt32(strArray[1]));
			for (Integer k = 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k + 2, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return ConvertBackToYearAndMonthAndDayName(SortRowLabels(table, 3));

	}

	private static DataTable SortYearAndMonthAndWeekRowLabels(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					Type.GetType("System.Int32"));
			table.get_Columns().Add(column);
		}
		DataColumn column2 = null;
		column2 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column2);
		DataColumn column3 = null;
		column3 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column3);
		for (Integer j = 1; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column4 = null;
			column4 = new DataColumn(
					dt.get_Columns().get_Item(j).get_Caption(), dt
							.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column4);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			String[] strArray = row.get_ItemArray()[0].toString().split("/");
			system.ClrInt32 val = new system.ClrInt32();
			val.__Ctor__(Convert.ToInt32(strArray[2]));
			row2.set_Item(0, val);
			
			system.ClrInt32 val2 = new system.ClrInt32();
			val2.__Ctor__(XmlMonthIntervalCategory
					.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
							MonthInterval.class,
							strArray[1].toString())));
			row2.set_Item(1, val2 );
			
			system.ClrInt32 val3 = new system.ClrInt32();
			val3.__Ctor__(XmlWeekIntervalCategory
					.ToWeekNumber((WeekInterval) LocalizeHelper.Parse(
							WeekInterval.class,
							strArray[0].toString())));
			
			row2.set_Item(2, val3);
			for (Integer k = 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k + 2, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return ConvertBackToYearAndMonthAndWeekName(SortRowLabels(table, 3));

	}

	private static DataTable SortYearAndMonthRowLabels(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					Type.GetType("System.Int32"));
			table.get_Columns().Add(column);
		}
		DataColumn column2 = null;
		column2 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column2);
		for (Integer j = 1; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column3 = null;
			column3 = new DataColumn(
					dt.get_Columns().get_Item(j).get_Caption(), dt
							.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column3);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			String[] strArray = row.get_ItemArray()[0].toString().split("/");
			row2.set_Item(0, Convert.ToInt32(strArray[1]));
			row2.set_Item(1, XmlMonthIntervalCategory
					.ToMonthNumber((MonthInterval) LocalizeHelper.Parse(
							MonthInterval.class,
							strArray[0].toString())));
			for (Integer k = 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k + 1, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return ConvertBackToYearAndMonthName(SortRowLabels(table, 2));

	}

	private static DataTable SortYearAndQuarterRowLabels(DataTable dt) {
		DataTable table = new DataTable();
		for (Integer i = 0; i < 1; i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					Type.GetType("System.Int32"));
			table.get_Columns().Add(column);
		}
		DataColumn column2 = null;
		column2 = new DataColumn("", Type.GetType("System.Int32"));
		table.get_Columns().Add(column2);
		for (Integer j = 1; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column3 = null;
			column3 = new DataColumn(
					dt.get_Columns().get_Item(j).get_Caption(), dt
							.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column3);
		}
		IEnumerator it1 = dt.get_Rows().GetEnumerator();
		while (it1.MoveNext()) {
			DataRow row = (DataRow) it1.get_Current();
			DataRow row2 = table.NewRow();
			String[] strArray = row.get_ItemArray()[0].toString().split("/");
			row2.set_Item(0, Convert.ToInt32(strArray[1]));
			row2.set_Item(1, XmlQuarterIntervalCategory
					.ToQuarterNumber((QuarterInterval) LocalizeHelper.Parse(
							MonthInterval.class,
							strArray[0].toString())));
			for (Integer k = 1; k < dt.get_Columns().get_Count(); k++) {
				row2.set_Item(k + 1, row.get_ItemArray()[k]);
			}
			table.get_Rows().Add(row2);
		}

		return ConvertBackToYearAndQuarterName(SortRowLabels(table, 2));

	}

	private static DataTable SwitchGroupByColumns(DataTable dt,
			GroupByDef groupByDef, GroupByDef subGroupByDef,
			GroupByDef evaluateByDef) {
		String interval = groupByDef.get_Interval();
		if (interval != null) {
			if (!(interval.equals("Yearly"))) {
				if ((interval.equals("Quarterly"))
						|| (interval.equals("Monthly"))) {

					dt = SwitchTableColumns(dt, 2);

					return dt;
				}
				if ((interval.equals("Weekly")) || (interval.equals("Daily"))) {

					dt = SwitchTableColumns(dt, 3);

					return dt;
				}
				if (interval.equals("Hourly")) {

					dt = SwitchTableColumns(dt, 4);
				}

				return dt;
			}

			dt = SwitchTableColumns(dt, 1);
		}

		return dt;

	}

	private static DataTable SwitchTableColumns(DataTable dt, Integer skipCount) {
		DataTable table = new DataTable();
		for (Integer i = 1; i <= skipCount; i++) {
			DataColumn column = null;
			column = new DataColumn(dt.get_Columns().get_Item(i).get_Caption(),
					dt.get_Columns().get_Item(i).get_DataType());
			table.get_Columns().Add(column);
		}
		DataColumn column2 = null;
		column2 = new DataColumn(dt.get_Columns().get_Item(0).get_Caption(), dt
				.get_Columns().get_Item(0).get_DataType());
		table.get_Columns().Add(column2);
		for (Integer j = skipCount + 1; j < dt.get_Columns().get_Count(); j++) {
			DataColumn column3 = null;
			column3 = new DataColumn(
					dt.get_Columns().get_Item(j).get_Caption(), dt
							.get_Columns().get_Item(j).get_DataType());
			table.get_Columns().Add(column3);
		}
		for (Integer k = 0; k < dt.get_Rows().get_Count(); k++) {
			DataRow row = table.NewRow();
			for (Integer m = 0; m < skipCount; m++) {
				row.set_Item(m,
						dt.get_Rows().get_Item(k).get_ItemArray()[m + 1]);
			}
			row.set_Item(skipCount,
					dt.get_Rows().get_Item(k).get_ItemArray()[0]);
			for (Integer n = skipCount + 1; n < dt.get_Columns().get_Count(); n++) {
				row.set_Item(n, dt.get_Rows().get_Item(k).get_ItemArray()[n]);
			}
			table.get_Rows().Add(row);
		}

		return table;

	}

}