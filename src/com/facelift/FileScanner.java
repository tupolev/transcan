package com.facelift;

import java.io.File;
import java.util.*;

public class FileScanner {

    protected List<String> outFileList;
    protected String rootDir;
    protected List<String> fileExtensions;

    private List<String> getOutFileList() {
        return outFileList;
    }

    private String getRootDir() {
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
        this.outFileList = new ArrayList<String>();
        this.setRootDir(rootDir);
        this.setFileExtensions(Arrays.asList(fileExtensions.split(",")));
    }

    public List<String> scanFileList() {
        String nam = getRootDir();
        File aFile = new File(nam);
        Process("", aFile);
        return getOutFileList();
    }

    public void Process(String spcs, File aFile) {
        if(aFile.isFile()) {
            List<String> extensions = getFileExtensions();
            Iterator it = extensions.iterator();
            while (it.hasNext()) {
                String obj = (String)it.next();
                if (aFile.getName().endsWith(obj)) {
                    getOutFileList().add(getOutFileList().size(),aFile.getAbsolutePath());
                }
            }
        } else if (aFile.isDirectory()) {
            File[] listOfFiles = aFile.listFiles();
            if(listOfFiles!=null) {
                for (int i = 0; i < listOfFiles.length; i++)
                    Process(spcs + " ", listOfFiles[i]);
            }
        }
    }
}




