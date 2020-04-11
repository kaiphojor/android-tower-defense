package com.vortexghost.plaintowerdefense.error_collect;

import android.provider.BaseColumns;

// 데이터 구성 체계를 지정하는 Contract class
public final class CrashReportContract {
    private CrashReportContract() {
    }

    /* Inner class that defines the table contents */
    private final String SQL_CREATE_TABLE = "create table if not exists "
            + CrashEntry.TABLE_NAME + "( "
            + CrashEntry.C_ID + " integer primary key autoincrement, "
            + CrashEntry.EMAIL + " text, "
            + CrashEntry.CRASH_TIME + " text, "
            + CrashEntry.CRASH_DESCRIPTION + " text, "
            + CrashEntry.STACK_TRACE + "text)";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + CrashEntry.TABLE_NAME;
    // 모든 activity에서 접근하기 위한
    public static class CrashEntry implements BaseColumns {
        public static final String TABLE_NAME = "crash_report";
        public static final String C_ID = "_id";
        public static final String EMAIL = "email";
        public static final String CRASH_TIME = "crash_time";
        public static final String CRASH_DESCRIPTION = "crash_description";
        public static final String STACK_TRACE = "stack_trace";
    }

}
