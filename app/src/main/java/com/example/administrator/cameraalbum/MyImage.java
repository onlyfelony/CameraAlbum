package com.example.administrator.cameraalbum;

public class MyImage {
    public static final int MYIMAGE = 0;
    public static final int ADDIM = 1;
    private String imagePath;
    private int type;
    private int addId;

    public MyImage(String imagePath, int type) {
        this.imagePath = imagePath;
        this.type = type;
    }

    public MyImage(int type, int addId) {
        this.type = type;
        this.addId = addId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAddId() {
        return addId;
    }

    public void setAddId(int addId) {
        this.addId = addId;
    }
}
