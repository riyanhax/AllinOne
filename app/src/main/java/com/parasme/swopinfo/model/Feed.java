package com.parasme.swopinfo.model;

import android.graphics.Bitmap;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by :- Mukesh Kumawat on 23-Sep-16.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */
public class Feed {

    private int userId, fileId, fileUserId, companyId, viewsCount, downloadsCount, statusInfoId, voteStatus;
    private String type, itemDate, comment, userFullName, fileName, fileTitle, thumbFileName, folderName, fileType, videoUrl, companyName,
            companyThumbFileName, userThumb, thumbURL, itemTimeAgo, fileTime, previewTitle, previewPageURL, previewDescription, previewThumbURL;
    private boolean isMenuExpanded, isErrorSet, isPreviewLoaded, userPicLoaded;
    private EditText commentEditText;
    private ArrayList<Comment> commentArrayList = new ArrayList<>();
    private ArrayList<Upload> uploadArrayList = new ArrayList<>();
    private Bitmap userBitmap;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getFileUserId() {
        return fileUserId;
    }

    public void setFileUserId(int fileUserId) {
        this.fileUserId = fileUserId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getDownloadsCount() {
        return downloadsCount;
    }

    public void setDownloadsCount(int downloadsCount) {
        this.downloadsCount = downloadsCount;
    }

    public int getStatusInfoId() {
        return statusInfoId;
    }

    public void setStatusInfoId(int statusInfoId) {
        this.statusInfoId = statusInfoId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getItemDate() {
        return itemDate;
    }

    public void setItemDate(String itemDate) {
        this.itemDate = itemDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getThumbFileName() {
        return thumbFileName;
    }

    public void setThumbFileName(String thumbFileName) {
        this.thumbFileName = thumbFileName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCompanyThumbFileName() {
        return companyThumbFileName;
    }

    public void setCompanyThumbFileName(String companyThumbFileName) {
        this.companyThumbFileName = companyThumbFileName;
    }

    public String getUserThumb() {
        return userThumb;
    }

    public void setUserThumb(String userThumb) {
        this.userThumb = userThumb;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public boolean isMenuExpanded() {
        return isMenuExpanded;
    }

    public void setMenuExpanded(boolean menuExpanded) {
        isMenuExpanded = menuExpanded;
    }

    public int getVoteStatus() {
        return voteStatus;
    }

    public void setVoteStatus(int voteStatus) {
        this.voteStatus = voteStatus;
    }

    public boolean isErrorSet() {
        return isErrorSet;
    }

    public void setErrorSet(boolean errorSet) {
        isErrorSet = errorSet;
    }

    public EditText getCommentEditText() {
        return commentEditText;
    }

    public void setCommentEditText(EditText commentEditText) {
        this.commentEditText = commentEditText;
    }

    public ArrayList<Comment> getCommentArrayList() {
        return commentArrayList;
    }

    public void setCommentArrayList(ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
    }

    public String getItemTimeAgo() {
        return itemTimeAgo;
    }

    public void setItemTimeAgo(String itemTimeAgo) {
        this.itemTimeAgo = itemTimeAgo;
    }

    public String getFileTime() {
        return fileTime;
    }

    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ArrayList<Upload> getUploadArrayList() {
        return uploadArrayList;
    }

    public void setUploadArrayList(ArrayList<Upload> uploadArrayList) {
        this.uploadArrayList = uploadArrayList;
    }

    public boolean isPreviewLoaded() {
        return isPreviewLoaded;
    }

    public void setPreviewLoaded(boolean previewLoaded) {
        isPreviewLoaded = previewLoaded;
    }

    public String getPreviewTitle() {
        return previewTitle;
    }

    public void setPreviewTitle(String previewTitle) {
        this.previewTitle = previewTitle;
    }

    public String getPreviewPageURL() {
        return previewPageURL;
    }

    public void setPreviewPageURL(String previewPageURL) {
        this.previewPageURL = previewPageURL;
    }

    public String getPreviewDescription() {
        return previewDescription;
    }

    public void setPreviewDescription(String previewDescription) {
        this.previewDescription = previewDescription;
    }

    public String getPreviewThumbURL() {
        return previewThumbURL;
    }

    public void setPreviewThumbURL(String previewThumbURL) {
        this.previewThumbURL = previewThumbURL;
    }

    public boolean isUserPicLoaded() {
        return userPicLoaded;
    }

    public void setUserPicLoaded(boolean userPicLoaded) {
        this.userPicLoaded = userPicLoaded;
    }

    public void setUserBitmap(Bitmap bitmap) {
        this.userBitmap = bitmap;
    }

    public Bitmap getUserBitmap() {
        return this.userBitmap;
    }
}