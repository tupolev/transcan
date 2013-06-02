package com.facelift;

import sun.misc.Regexp;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XScanner {
    protected List<Regexp> patternList;
    protected List<String> fileList;
    protected List<String> outputList;


    public List<Regexp> getPatternList() {
        return patternList;
    }

    public void setPatternList(List<Regexp> patternList) {
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

    public XScanner() {

    }

    public XScanner startEngine(List<String> fileList, List<Regexp> patternList) throws Exception{
        if (patternList.size() == 0) {
            throw new Exception();
        }
        this.setFileList(fileList);
        this.setPatternList(patternList);
        return this;
    }

    private void loopThroughFiles() throws Exception {
        Iterator<String> fileIterator = getFileList().iterator();
        while (fileIterator.hasNext()) {
            String fileContent = new Scanner(new File(fileIterator.next())).useDelimiter("\\Z").next();
            loopThroughPatterns(fileContent);
        }
    }

    private void loopThroughPatterns(String fileContent) throws Exception {
        Iterator<Regexp> patIterator = getPatternList().iterator();
        while (patIterator.hasNext()) {
            addMatchesListToOutputList(fileScan(fileContent, patIterator.next()));
        }
    }

    private List<String> fileScan(String fileContent, Regexp pattern) throws Exception {
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile(pattern.toString())
                .matcher(fileContent);
        while (m.find()) {
            allMatches.add(m.group());
        }
        return allMatches;
    }

    private boolean addMatchesListToOutputList(List<String> matchesList) throws Exception {
        return getOutputList().addAll(matchesList);
    }

    public void doMagic() throws Exception {
        loopThroughFiles();
    }
}
