package cn.cb.btwatermeterpro.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.cb.baselibrary.utils.LogHelper;

public class DbManager {
    private static DbManager instance;
    private static OpenHelper openHelper;
    private static SqlServer sqlServer;

    private DbManager() {
    }

    public static void createInstance(Context context, String name, int version) {
        instance = new DbManager();
        openHelper = new OpenHelper(context, name, null, version);
        sqlServer = new SqlServer(openHelper);
    }

    public static DbManager getInstance() {
        return instance;
    }

    public SqlServer getSqlServer() {
        return sqlServer;
    }


    public static class OpenHelper extends SQLiteOpenHelper {
        private final String TAG = getClass().getSimpleName();

        public OpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sqlReadRecord = "CREATE TABLE \"READ_RECORD\" (\n" +//抄表记录
                    "  \"ID\" INTEGER NOT NULL DEFAULT 0 PRIMARY KEY AUTOINCREMENT,\n" +//主键
                    "  \"METERADDRESS\" TEXT NOT NULL,\n" +//水表编号
                    "  \"USER_ID\" TEXT NOT NULL,\n" +//用户id
                    "  \"READ_DATE\" TEXT,\n" +//抄表日期
                    "  \"READ_TIME\" TEXT,\n" +//抄表时间
                    "  \"READ_NUMBER\" INTEGER DEFAULT 0,\n" +//抄表读数
                    "  \"FLOW\" INTEGER DEFAULT 0,\n" +//用量
                    "  \"READ_HEX\" TEXT\n" +//抄表报文
                    ")";
            LogHelper.i(TAG, "onCreate: " + sqlReadRecord);
            db.execSQL(sqlReadRecord);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            List<String> list = new ArrayList<>();
            switch (oldVersion) {
                case 1:
                    /*list.add("ALTER TABLE SCRIBE_RECORD ADD TEMP INTEGER DEFAULT 0");
                    list.add("ALTER TABLE SCRIBE_RECORD ADD ANGLE INTEGER DEFAULT 0");*/
            }
            for (String s : list) {
                LogHelper.i(TAG, "onUpgrade: " + s);
                db.execSQL(s);
            }
        }
    }
}


