package com.facelift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileScanner {

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    protected List<String> fileList;
    protected String rootDir;
    protected List<String> fileExtensions;

    public String getRootDir() {
        return rootDir;
    }

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public List<String> getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(List<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }

    public FileScanner(String rootDir, String fileExtensions) {
        this.setRootDir(rootDir);
        this.setFileExtensions(Arrays.asList(fileExtensions.split(",")));
    }


    public List<String> scanFileList() {
        return null;
    }
}
