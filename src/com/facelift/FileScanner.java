package com.facelift;

import java.io.File;
import java.util.*;

public class FileScanner {

    protected List<String> outFileList;

    public XConfig getConfig() {
        return config;
    }

    protected XConfig config;

    private List<String> getOutFileList() {
        return outFileList;
    }

    public FileScanner(XConfig config) {
        this.setConfig(config);
        this.outFileList = new ArrayList<String>();
    }

    private void setConfig(XConfig config) {
        this.config = config;
    }

    private boolean directoryIsExcluded(String dir) {
        return getConfig().getExcludedDirectories().contains(dir);
    }

    private boolean extensionIsExcluded(String ext) {
        return getConfig().getExcludedFileExtensions().contains(ext);
    }

    public List<String> scanFileList() {
        for (String goodDir : getConfig().getIncludedDirectories()) {
            if (!directoryIsExcluded(goodDir)) {
                String nam = goodDir;
                File aFile = new File(nam);
                Process("", aFile);
            }
        }
        return getOutFileList();
    }

    public void Process(String spcs, File aFile) {
        if(aFile.isFile()) {
            List<String> extensions = getConfig().getIncludedFileExtensions();
            Iterator it = extensions.iterator();
            while (it.hasNext()) {
                String obj = (String)it.next();
                if (!extensionIsExcluded(obj) && aFile.getName().endsWith(obj)) {
                    getOutFileList().add(getOutFileList().size(),aFile.getAbsolutePath());
                }
            }
        } else if (aFile.isDirectory() && !directoryIsExcluded(aFile.getAbsolutePath())) {
            File[] listOfFiles = aFile.listFiles();
            if(listOfFiles!=null) {
                for (int i = 0; i < listOfFiles.length; i++)
                    Process(spcs + " ", listOfFiles[i]);
            }
        }
    }
}




