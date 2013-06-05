package com.facelift;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class XConfig {
    private Document doc;
    private String _configFile = "config.xml";
    private String outputFile;
	private List<String> outputPrefixes;
	private String defaultOutputPrefix;
	private String outputDirectory;
	private boolean outputMultiFile;
	private List<String> includedFileExtensions, excludedFileExtensions,includedDirectories,excludedDirectories;
	private List<FocusedPattern> inputPatterns,outputPatterns;
	private String outputFileSuffix;

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

    public List<FocusedPattern> getInputPatterns() {
        return inputPatterns;
    }

    public void setInputPatterns(List<FocusedPattern> inputPatterns) {
        this.inputPatterns = inputPatterns;
    }

    public List<FocusedPattern> getOutputPatterns() {
        return outputPatterns;
    }

    public void setOutputPatterns(List<FocusedPattern> outputPatterns) {
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

	public List<String> getOutputPrefixes() {
		return outputPrefixes;
	}

	public void setOutputPrefixes(ArrayList<String> outputPrefixes) {
		this.outputPrefixes = outputPrefixes;
	}

	public void setDefaultOutputPrefix(String defaultOutputPrefix) {
		this.defaultOutputPrefix = defaultOutputPrefix;
	}

	public String getDefaultOutputPrefix() {
		return defaultOutputPrefix;
	}

	public void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputMultiFile(boolean outputMultiFile) {
		this.outputMultiFile = outputMultiFile;
	}

	public boolean isOutputMultiFile() {
		return outputMultiFile;
	}

	public String getOutputFileSuffix() {
		return outputFileSuffix;
	}

	public void setOutputFileSuffix(String outputFileSuffix) {
		this.outputFileSuffix = outputFileSuffix;
	}

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
		setInputPatterns(new ArrayList<FocusedPattern>());
		for (int i = 0; i < tags.getLength(); i++) {
			Element pattern = (Element)tags.item(i);
			String cg = pattern.getAttribute("capture-group");
			FocusedPattern fp = new FocusedPattern(
					Integer.valueOf(cg),
					Pattern.compile(tags.item(i).getFirstChild().getNodeValue().toString())
			);
			getInputPatterns().add(fp);
		}

		tags = doc.getElementsByTagName("output-pattern");
		setOutputPatterns(new ArrayList<FocusedPattern>());
		for (int i = 0; i < tags.getLength(); i++) {
			FocusedPattern fp = new FocusedPattern(
					Integer.getInteger(
							((Element)tags.item(i)).getAttribute("capture-group")
					),
					Pattern.compile(tags.item(i).getFirstChild().getNodeValue().toString())
			);
			getOutputPatterns().add(fp);
		}

		tags = doc.getElementsByTagName("included-directory");
		if (tags.getLength() == 0) throw new Exception("No included directories configured");
		setIncludedDirectories(new ArrayList<String>());
		for (int i = 0; i < tags.getLength(); i++) {
			getIncludedDirectories().add(tags.item(i).getFirstChild().getNodeValue().toString());
		}

		tags = doc.getElementsByTagName("excluded-directory");
		setExcludedDirectories(new ArrayList<String>());
		for (int i = 0; i < tags.getLength(); i++) {
			getExcludedDirectories().add(tags.item(i).getFirstChild().getNodeValue().toString());
		}

		tags = doc.getElementsByTagName("output-multiple-files");
		if (tags.getLength() == 0) throw new Exception("No output multi file property configured");
		setOutputMultiFile(Boolean.getBoolean(tags.item(0).getFirstChild().getNodeValue()));

		tags = doc.getElementsByTagName("output-single-file");
		if (tags.getLength() == 0) throw new Exception("No output single file property configured");
		setOutputFile(tags.item(0).getFirstChild().getNodeValue());

		tags = doc.getElementsByTagName("output-directory");
		if (tags.getLength() == 0) throw new Exception("No output directory configured");
		setOutputDirectory(tags.item(0).getFirstChild().getNodeValue());

		tags = doc.getElementsByTagName("output-file-suffix");
		if (tags.getLength() == 0) throw new Exception("No output file suffix configured");
		setOutputFileSuffix(tags.item(0).getFirstChild().getNodeValue());

		tags = doc.getElementsByTagName("output-prefix");
		setOutputPrefixes(new ArrayList<String>());
		for (int i = 0; i < tags.getLength(); i++) {
			getOutputPrefixes().add(tags.item(i).getFirstChild().getNodeValue().toString());
			if (tags.item(i).hasAttributes() && ((Element)tags.item(i)).getAttribute("default").compareTo("true") == 0
					) {
				setDefaultOutputPrefix(tags.item(i).getFirstChild().getNodeValue().toString());
			}
		}
	}
}
