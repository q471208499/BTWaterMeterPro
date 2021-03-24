package cn.cb.btwatermeterpro.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cn.cb.baselibrary.utils.LogHelper;

public class SqlServer {
    private final String TAG = getClass().getSimpleName();
    private DbManager.OpenHelper openHelper;

    public SqlServer(DbManager.OpenHelper helper) {
        openHelper = helper;
    }

    public void getReadList() {
        try (SQLiteDatabase database = openHelper.getWritableDatabase()) {
            String sql = "SELECT * FROM READ_RECORD";
            LogHelper.i(TAG, "getReadList: " + sql);
            Cursor cursor = database.rawQuery(sql, null);
            while (cursor.moveToNext()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public void addBooks(List<BookResultBean> list) {
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        try {
            database.beginTransaction();
            String createTime = ABTimeUtils.getCurrentTimeInString(DEFAULT_DATE_FORMAT);
            for (BookResultBean bean : list) {
                String sql = "INSERT INTO BOOK(NAME, METERADDRESS, CREATE_TIME, ADDRESS) " +
                        "VALUES('" + bean.getBookName() + "', '" + bean.getMeterAddress() + "','"
                        + createTime + "','" + bean.getAddress() + "')";
                LogHelper.i(TAG, "addBooks: " + sql);
                database.execSQL(sql);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.endTransaction();
        database.close();
    }

    public boolean dropTable() {
        try (SQLiteDatabase database = sqlHelper.getWritableDatabase()) {
            String sql = "DROP TABLE BOOK";
            LogHelper.i(TAG, "dropTable: " + sql);
            database.execSQL(sql);
            database.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cleanBooks() {
        try (SQLiteDatabase database = sqlHelper.getWritableDatabase()) {
            String sql = "DELETE FROM BOOK";
            LogHelper.i(TAG, "cleanBooks: " + sql);
            database.execSQL(sql);
            database.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<BookListItemBean> getBooksBySelect() {
        List<BookListItemBean> list = getAllBooks();
        Set<String> selection = SPUtils.getInstance().getStringSet(SP_CHECKED_COMMUNITY_ITEMS);
        if (selection == null || selection.isEmpty()) return list;
        List<BookListItemBean> result = new ArrayList<>();
        for (String s : selection) {
            for (int i = 0; i < list.size(); i++) {
                if (s.equals(list.get(i).getBookName())) result.add(list.get(i));
            }
        }
        return result;
    }

    public List<BookListItemBean> getAllBooks() {
        List<BookListItemBean> list = new ArrayList<>();
        try (SQLiteDatabase database = sqlHelper.getWritableDatabase()) {
            String sql = "SELECT B.NAME, B.CREATE_TIME, COUNT(B.NAME) AS N, SR.STATUS\n" +
                    "FROM BOOK B\n" +
                    "LEFT JOIN SCRIBE_RECORD SR ON SR.METERADDRESS = B.METERADDRESS\n" +
                    "GROUP BY B.NAME, B.CREATE_TIME , SR.STATUS\n" +
                    "ORDER BY B.ID";
            LogHelper.i(TAG, "getBooks: " + sql);
            Cursor cursor = database.rawQuery(sql, null);
            String tempName = "";
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("NAME"));
                String createTime = cursor.getString(cursor.getColumnIndex("CREATE_TIME"));
                int n = cursor.getInt(cursor.getColumnIndex("N"));
                int status = cursor.getInt(cursor.getColumnIndex("STATUS"));
                if (tempName.isEmpty()) {
                    tempName = name;
                    BookListItemBean bean = new BookListItemBean();
                    bean.setBookCreateTime(createTime);
                    bean.setBookName(name);
                    bean.setBookId(null);
                    bean.setBookTotal(n);
                    list.add(bean);
                    if (status == 1) bean.setBookComplete(n);
                } else {
                    if (tempName.equals(name)) {
                        BookListItemBean bean = list.get(list.size() - 1);
                        if (status == 1) bean.setBookComplete(n);
                        int total = bean.getBookTotal() + n;
                        bean.setBookTotal(total);
                        list.set(list.size() - 1, bean);
                    } else {
                        tempName = name;
                        BookListItemBean bean = new BookListItemBean();
                        bean.setBookCreateTime(createTime);
                        bean.setBookName(name);
                        bean.setBookId(null);
                        bean.setBookTotal(n);
                        list.add(bean);
                        if (status == 1) bean.setBookComplete(n);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }*/
}
