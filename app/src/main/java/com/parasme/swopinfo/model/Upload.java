package com.parasme.swopinfo.model;

import java.io.Serializable;

/**
 * Created by :- Mukesh Kumawat on 23-Sep-16.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */
public class Upload implements Serializable {

    private int groupFileLinkId,groupId,fileId,ownerId,viewsCount,downloadsCount,companyId,statusInfoId,userId,score, userVote;
    private String realFileName,fileName,fileType,fileSize,timeDate,category,title,tags,description,videoURL,veryPdf, thumbURL,folderName,fileURL,uploadType, userFullName, commentText, userThumbURL, displayTime;
    private boolean broadcast,comments,ratings,embedding,downloads,profileRemoved,companyUploaded,includeExtension;

    public int getGroupFileLinkId() {
        return groupFileLinkId;
    }

    public void setGroupFileLinkId(int groupFileLinkId) {
        this.groupFileLinkId = groupFileLinkId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
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

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getStatusInfoId() {
        return statusInfoId;
    }

    public void setStatusInfoId(int statusInfoId) {
        this.statusInfoId = statusInfoId;
    }

    public String getRealFileName() {
        return realFileName;
    }

    public void setRealFileName(String realFileName) {
        this.realFileName = realFileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getVeryPdf() {
        return veryPdf;
    }

    public void setVeryPdf(String veryPdf) {
        this.veryPdf = veryPdf;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public boolean isComments() {
        return comments;
    }

    public void setComments(boolean comments) {
        this.comments = comments;
    }

    public boolean isRatings() {
        return ratings;
    }

    public void setRatings(boolean ratings) {
        this.ratings = ratings;
    }

    public boolean isEmbedding() {
        return embedding;
    }

    public void setEmbedding(boolean embedding) {
        this.embedding = embedding;
    }

    public boolean isDownloads() {
        return downloads;
    }

    public void setDownloads(boolean downloads) {
        this.downloads = downloads;
    }

    public boolean isProfileRemoved() {
        return profileRemoved;
    }

    public void setProfileRemoved(boolean profileRemoved) {
        this.profileRemoved = profileRemoved;
    }

    public boolean isCompanyUploaded() {
        return companyUploaded;
    }

    public void setCompanyUploaded(boolean companyUploaded) {
        this.companyUploaded = companyUploaded;
    }

    public boolean isIncludeExtension() {
        return includeExtension;
    }

    public void setIncludeExtension(boolean includeExtension) {
        this.includeExtension = includeExtension;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getUserThumbURL() {
        return userThumbURL;
    }

    public void setUserThumbURL(String userThumbURL) {
        this.userThumbURL = userThumbURL;
    }

    public String getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(String displayTime) {
        this.displayTime = displayTime;
    }

    public int getUserVote() {
        return userVote;
    }

    public void setUserVote(int userVote) {
        this.userVote = userVote;
    }
}