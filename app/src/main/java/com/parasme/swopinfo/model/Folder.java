package com.parasme.swopinfo.model;

/**
 * Created by :- Mukesh Kumawat on 23-Sep-16.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */
public class Folder {

    private String folderName,numOfFiles;
    private int userId;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getNumOfFiles() {
        return numOfFiles;
    }

    public void setNumOfFiles(String numOfFiles) {
        this.numOfFiles = numOfFiles;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
