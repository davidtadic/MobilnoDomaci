package rs.fon.trecidomaci.database;

import android.provider.BaseColumns;

/**
 * Created by david on 19.12.2016..
 */

public class EmployeesContract {

    private static final String STRING_TYPE = " VARCHAR(20)";
    private static final String COMMA_SEP = " ,";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EmployeesEntry.TABLE_NAME + " (" +
                    EmployeesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    EmployeesEntry.COLUMN_2_NAME + STRING_TYPE + COMMA_SEP +
                    EmployeesEntry.COLUMN_3_NUMBER + STRING_TYPE + COMMA_SEP +
                    EmployeesEntry.COLUMN_4_EMAIL + STRING_TYPE + COMMA_SEP +
                    EmployeesEntry.COLUMN_5_POSITION + STRING_TYPE + COMMA_SEP +
                    EmployeesEntry.COLUMN_6_SALARY + STRING_TYPE+" )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EmployeesEntry.TABLE_NAME;

    private EmployeesContract() {

    }

    public static class EmployeesEntry implements BaseColumns {
        public static final String TABLE_NAME = "list_of_employees";
        public static final String COLUMN_2_NAME = "first_name";
        public static final String COLUMN_3_NUMBER = "last_name";
        public static final String COLUMN_4_EMAIL = "email";
        public static final String COLUMN_5_POSITION = "position";
        public static final String COLUMN_6_SALARY = "salary";


    }
}
