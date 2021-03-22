package cn.cb.btwatermeterpro.bean;

public class CommonBean {
    private int id;
    private String title;
    private int resId;
    private Class<?> cls;

    public CommonBean(int id, String title, int resId, Class<?> cls) {
        this.id = id;
        this.title = title;
        this.resId = resId;
        this.cls = cls;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public Class<?> getCls() {
        return cls;
    }

    public void setCls(Class<?> cls) {
        this.cls = cls;
    }
}
