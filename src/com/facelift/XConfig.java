package com.facelift;


import com.sun.org.apache.xerces.internal.xs.StringList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.Regexp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class XConfig {
    protected Document doc;
    protected String _configFile = "config.xml";
    protected String outputFile;

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public List<String> getIncludedFileExtensions() {
        return includedFileExtensions;
    }

    public void setIncludedFileExtensions(List<String> includedFileExtensions) {
        this.includedFileExtensions = includedFileExtensions;
    }

    public List<String> getExcludedFileExtensions() {
        return excludedFileExtensions;
    }

    public void setExcludedFileExtensions(List<String> excludedFileExtensions) {
        this.excludedFileExtensions = excludedFileExtensions;
    }

    public List<Pattern> getInputPatterns() {
        return inputPatterns;
    }

    public void setInputPatterns(List<Pattern> inputPatterns) {
        this.inputPatterns = inputPatterns;
    }

    public List<Pattern> getOutputPatterns() {
        return outputPatterns;
    }

    public void setOutputPatterns(List<Pattern> outputPatterns) {
        this.outputPatterns = outputPatterns;
    }

    public List<String> getIncludedDirectories() {
        return includedDirectories;
    }

    public void setIncludedDirectories(List<String> includedDirectories) {
        this.includedDirectories = includedDirectories;
    }

    public List<String> getExcludedDirectories() {
        return excludedDirectories;
    }

    public void setExcludedDirectories(List<String> excludedDirectories) {
        this.excludedDirectories = excludedDirectories;
    }

    protected List<String> includedFileExtensions, excludedFileExtensions,includedDirectories,excludedDirectories;
    protected List<Pattern> inputPatterns,outputPatterns;

    public XConfig() throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new File(_configFile));

        doc.getDocumentElement().normalize();
        System.out.println(doc.getDocumentElement().getTagName().toString() + " tree loaded");


        try {
            String n = doc.getElementsByTagName("included-file-extensions").item(0).getFirstChild().getNodeValue();
            String[] list = n.split(",");
            setIncludedFileExtensions(Arrays.asList(list));
        } catch (Exception ex) {
            throw new Exception("Error loading included-file-extensions section from config.xml file." + ex.getMessage());
        }

        try {
            setExcludedFileExtensions(Arrays.asList(doc.getElementsByTagName("excluded-file-extensions").item(0).getFirstChild().getNodeValue().toString().split(",")));
        } catch (Exception ex) {
            throw new Exception("Error loading excluded-file-extensions section from config.xml file");
        }

        NodeList tags = doc.getElementsByTagName("input-pattern");
        if (tags.getLength() == 0) throw new Exception("No input patterns configured");
        setInputPatterns(new ArrayList<Pattern>());
        for(int i =0; i< tags.getLength();i++ ) {
            getInputPatterns().add(Pattern.compile(tags.item(i).getFirstChild().getNodeValue().toString()));
        }

        tags = doc.getElementsByTagName("output-pattern");
        setOutputPatterns(new ArrayList<Pattern>());
        for(int i =0; i< tags.getLength();i++ ) {
            getOutputPatterns().add(Pattern.compile(tags.item(i).getFirstChild().getNodeValue().toString()));
        }

        tags = doc.getElementsByTagName("included-directory");
        if (tags.getLength() == 0) throw new Exception("No included directories configured");
        setIncludedDirectories(new ArrayList<String>());
        for(int i =0; i< tags.getLength();i++ ) {
            getIncludedDirectories().add(tags.item(i).getFirstChild().getNodeValue().toString());
        }

        tags = doc.getElementsByTagName("excluded-directory");
        setExcludedDirectories(new ArrayList<String>());
        for(int i =0; i< tags.getLength();i++ ) {
            getExcludedDirectories().add(tags.item(i).getFirstChild().getNodeValue().toString());
        }
        tags = doc.getElementsByTagName("output-file");
        if (tags.getLength() == 0) throw new Exception("No output file configured");
        setOutputFile(tags.item(0).getFirstChild().getNodeValue().toString());
    }
}
