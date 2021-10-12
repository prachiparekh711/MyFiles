package myfiles.adfree.filemanager.esfilexplorer.managefileslocally.Model;

import java.io.Serializable;

public class BaseModel implements Serializable{
    private long id;
    private String did;
    private String name,folderName;
    private String path;
    private long size;   //byte
    private String bucketId;  //Directory ID
    private String bucketName;  //Directory Name
    private String date,recentDate;
    boolean isDirectory = false;

    public String getRecentDate() {
        return recentDate;
    }

    public void setRecentDate(String recentDate) {
        this.recentDate = recentDate;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getDid(){
        return did;
    }

    public void setDid(String did){
        this.did = did;
    }

    public String getFolderName(){
        return folderName;
    }

    public void setFolderName(String folderName){
        this.folderName = folderName;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public long getSize(){
        return size;
    }

    public void setSize(long size){
        this.size = size;
    }

    public String getBucketId(){
        return bucketId;
    }

    public void setBucketId(String bucketId){
        this.bucketId = bucketId;
    }

    public String getBucketName(){
        return bucketName;
    }

    public void setBucketName(String bucketName){
        this.bucketName = bucketName;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }


}


