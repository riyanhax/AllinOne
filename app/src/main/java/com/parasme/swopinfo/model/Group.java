package com.parasme.swopinfo.model;

/**
 * Created by :- Mukesh Kumawat on 23-Sep-16.
 * Designation :- Android Senior Developer
 * Organization :- Parasme Software And Technology
 * Email :- mukeshkmtskr@gmail.com
 * Mobile :- +917737556190
 */
public class Group {

    private int groupLinkId,groupId,ownerUserId,totalFiles,totalMembers;
    private String groupName,timeDate,ownerUserName,ownerFullName,ownerEmail,extraInfo1,extraInfo2,businessCity,companyIndustry,type, groupAccessCode;
    private boolean groupLogo, requireAccessCode, readOnlyGroup;

    public int getGroupLinkId() {
        return groupLinkId;
    }

    public void setGroupLinkId(int groupLinkId) {
        this.groupLinkId = groupLinkId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public boolean isGroupLogo() {
        return groupLogo;
    }

    public void setGroupLogo(boolean groupLogo) {
        this.groupLogo = groupLogo;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public void setTotalFiles(int totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public String getExtraInfo1() {
        return extraInfo1;
    }

    public void setExtraInfo1(String extraInfo1) {
        this.extraInfo1 = extraInfo1;
    }

    public String getExtraInfo2() {
        return extraInfo2;
    }

    public void setExtraInfo2(String extraInfo2) {
        this.extraInfo2 = extraInfo2;
    }

    public String getBusinessCity() {
        return businessCity;
    }

    public void setBusinessCity(String businessCity) {
        this.businessCity = businessCity;
    }

    public String getCompanyIndustry() {
        return companyIndustry;
    }

    public void setCompanyIndustry(String companyIndustry) {
        this.companyIndustry = companyIndustry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupAccessCode() {
        return groupAccessCode;
    }

    public void setGroupAccessCode(String groupAccessCode) {
        this.groupAccessCode = groupAccessCode;
    }

    public boolean isRequireAccessCode() {
        return requireAccessCode;
    }

    public void setRequireAccessCode(boolean requireAccessCode) {
        this.requireAccessCode = requireAccessCode;
    }

    public boolean isReadOnlyGroup() {
        return readOnlyGroup;
    }

    public void setReadOnlyGroup(boolean readOnlyGroup) {
        this.readOnlyGroup = readOnlyGroup;
    }
}