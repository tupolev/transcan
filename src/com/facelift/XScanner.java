package com.facelift;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XScanner {
    protected List<FocusedPattern> outputPatternList;
    protected List<String> fileList;
    protected List<String> outputList;
    protected List<FocusedPattern> inputPatternList;
	private String projectPrefix;

	private List<FocusedPattern> getInputPatternList() {
        return inputPatternList;
    }

    public void setInputPatternList(List<FocusedPattern> inputPatternList) {
        this.inputPatternList = inputPatternList;
    }

    private List<FocusedPattern> getOutputPatternList() {
        return outputPatternList;
    }

    public void setOutputPatternList(List<FocusedPattern> outputPatternList) {
        this.outputPatternList = outputPatternList;
    }

    public List<String> getFilteredOutputList() {
	    List<String> filteredList = new ArrayList<String>();
	    for(int i=0; i<getOutputList().size(); i++) {
            String item = getOutputList().get(i);
            if (item.startsWith(getProjectPrefix()+".")) {
	            filteredList.add(item);
	            FocusedPattern pattern = null;
	            Iterator<FocusedPattern> it = getOutputPatternList().iterator();
	            List<String> matches = new ArrayList<String>();
	            while (it.hasNext()) {
		            pattern = it.next();
		            matches = pattern.matchAndCapture(item);
		            filteredList.addAll(matches);
	            }
            }

        }
        return filteredList;
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

	public XScanner startEngine(List<String> fileList, List<FocusedPattern> inputPatternList,
	                            List<FocusedPattern> outputPatternList, String projectPrefix) throws Exception {
		if (inputPatternList.size() == 0) {
			throw new Exception();
		}
		this.setFileList(fileList);
		this.setInputPatternList(inputPatternList);
		this.setOutputPatternList(outputPatternList);
		this.setOutputList(new ArrayList<String>());
		this.setProjectPrefix(projectPrefix);
		return this;
	}

    private void loopThroughFiles() throws Exception {
        Iterator<String> fileIterator = getFileList().iterator();
        while (fileIterator.hasNext()) {
            try {
                String fileItem = fileIterator.next();
                String fileContent = new Scanner(new File(fileItem)).useDelimiter("\0").next();
                loopThroughPatterns(fileContent);
            } catch (NoSuchElementException e) {
                continue;
            }
        }
    }

    private void loopThroughPatterns(String fileContent) throws Exception {
        Iterator<FocusedPattern> patIterator = getInputPatternList().iterator();
        while (patIterator.hasNext()) {
            FocusedPattern pattern = patIterator.next();
            addMatchesListToOutputList(fileScan(fileContent, pattern));
        }
    }

    private List<String> fileScan(String fileContent, FocusedPattern pattern) throws Exception {
        return pattern.matchAndCapture(fileContent);
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

	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix;
	}

	public String getProjectPrefix() {
		return projectPrefix;
	}
}
