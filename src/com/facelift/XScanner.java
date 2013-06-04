package com.facelift;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XScanner {
    protected List<Pattern> outputPatternList;
    protected List<String> fileList;
    protected List<String> outputList;
    protected List<Pattern> inputPatternList;

    private List<Pattern> getInputPatternList() {
        return inputPatternList;
    }

    public void setInputPatternList(List<Pattern> inputPatternList) {
        this.inputPatternList = inputPatternList;
    }

    private List<Pattern> getOutputPatternList() {
        return outputPatternList;
    }

    public void setOutputPatternList(List<Pattern> outputPatternList) {
        this.outputPatternList = outputPatternList;
    }

    public List<String> getFilteredOutputList() {
        for(int i=0; i<getOutputList().size(); i++) {
            String item = getOutputList().get(i);
            Pattern pattern = null;
            Iterator<Pattern> it = getOutputPatternList().iterator();
            while (it.hasNext()) {
                pattern = it.next();
                Matcher m = pattern.matcher(item);
                //System.out.println(m.pattern().toString());
                if (m.find()) {
                    item = m.group();
                    //System.out.println(m.group());
                }
            }
            item = item.replace("'","").replace('"',' ').replace(" ","");
            getOutputList().set(i, item);
        }
        return getOutputList();
    }

    private List<String> getFileList() {
        return fileList;
    }

    private void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<String> getOutputList() {
        return outputList;
    }

    public void setOutputList(List<String> outputList) {
        this.outputList = outputList;
    }

    private XScanner addToOutputList(String item) {
        if (!outputList.contains(item)) {
            outputList.add(item);
        }
        return this;
    }

    public XScanner startEngine(List<String> fileList, List<Pattern> inputPatternList, List<Pattern> outputPatternList) throws Exception {
        if (inputPatternList.size() == 0) {
            throw new Exception();
        }
        this.setFileList(fileList);
        this.setInputPatternList(inputPatternList);
        this.setOutputPatternList(outputPatternList);
        this.setOutputList(new ArrayList<String>());
        return this;
    }

    private void loopThroughFiles() throws Exception {
        Iterator<String> fileIterator = getFileList().iterator();
        while (fileIterator.hasNext()) {
            try {
                String fileItem = fileIterator.next();
                System.out.println("Scanning file: " + fileItem);
                String fileContent = new Scanner(new File(fileItem)).useDelimiter("\0").next();
                loopThroughPatterns(fileContent);
            } catch (NoSuchElementException e) {
                continue;
            }
        }
    }

    private void loopThroughPatterns(String fileContent) throws Exception {
        Iterator<Pattern> patIterator = getInputPatternList().iterator();
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
