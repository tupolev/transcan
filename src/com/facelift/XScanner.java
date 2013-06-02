package com.facelift;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XScanner {
    protected List<Pattern> patternList;
    protected List<String> fileList;
    protected List<String> outputList;


    public List<Pattern> getPatternList() {
        return patternList;
    }

    public void setPatternList(List<Pattern> patternList) {
        this.patternList = patternList;
    }


    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<String> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<String> outputList) {
        this.outputList = outputList;
    }

    public XScanner addToOutputList(String item) {
        if (!outputList.contains(item)) {
            outputList.add(item);
        }
        return this;
    }

    public XScanner() {

    }

    public XScanner startEngine(List<String> fileList, List<Pattern> patternList) throws Exception {
        if (patternList.size() == 0) {
            throw new Exception();
        }
        this.setFileList(fileList);
        this.setPatternList(patternList);
        this.setOutputList(new ArrayList<String>());
        return this;
    }

    private void loopThroughFiles() throws Exception {
        Iterator<String> fileIterator = getFileList().iterator();
        while (fileIterator.hasNext()) {
            String fileItem = fileIterator.next();
            String fileContent = new Scanner(new File(fileItem)).useDelimiter("\\Z").next();
            loopThroughPatterns(fileContent);
        }
    }

    private void loopThroughPatterns(String fileContent) throws Exception {
        Iterator<Pattern> patIterator = getPatternList().iterator();
        while (patIterator.hasNext()) {
            Pattern pattern = patIterator.next();
            addMatchesListToOutputList(fileScan(fileContent, pattern));
        }
    }

    private List<String> fileScan(String fileContent, Pattern pattern) throws Exception {
        List<String> allMatches = new ArrayList<String>();
        Matcher m = pattern.matcher(fileContent);
        while (m.find()) {
            if (!allMatches.contains(m.group())) {
                allMatches.add(m.group());
            }
        }
        return allMatches;
    }

    private void addMatchesListToOutputList(List<String> matchesList) throws Exception {
        Iterator<String> it = matchesList.iterator();
        while (it.hasNext()) {
            addToOutputList(it.next());
        }
    }

    public void doMagic() throws Exception {
        loopThroughFiles();
    }
}
