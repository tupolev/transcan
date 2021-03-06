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
//    private String _configFile = "config_project1.xml";
	private String projectName, projectPrefix, projectPrefixShort;
	private List<String> outputPrefixes;
	private String defaultOutputPrefix;
	private List<String> includedFileExtensions, excludedFileExtensions,includedDirectories,excludedDirectories;
	private List<FocusedPattern> inputPatterns,outputPatterns;
    private String masterFileDirectory;
    private String masterFileSuffix;
	private boolean saveDefaultMasterList;
	private String defaultMasterListFilename;

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectPrefix() {
		return projectPrefix;
	}

	public void setProjectPrefix(String projectPrefix) {
		this.projectPrefix = projectPrefix;
	}

	public String getProjectPrefixShort() {
		return projectPrefixShort;
	}

	public void setProjectPrefixShort(String projectPrefixShort) {
		this.projectPrefixShort = projectPrefixShort;
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

    public String getMasterFileDirectory() {
        return masterFileDirectory;
    }

    public void setMasterFileDirectory(String masterFileDirectory) {
        this.masterFileDirectory = masterFileDirectory;
    }

    public String getMasterFileSuffix() {
        return masterFileSuffix;
    }

    public void setMasterFileSuffix(String masterFileSuffix) {
        this.masterFileSuffix = masterFileSuffix;
    }


	public boolean getSaveDefaultMasterList() {
		return saveDefaultMasterList;
	}

	public void setSaveDefaultMasterList(boolean saveDefaultMasterList) {
		this.saveDefaultMasterList = saveDefaultMasterList;
	}

	public void setDefaultMasterListFilename(String defaultMasterListFilename) {
		this.defaultMasterListFilename = defaultMasterListFilename;
	}

	public String getDefaultMasterListFilename() {
		return defaultMasterListFilename;
	}

	public XConfig(String configFile) throws Exception {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc;
		if (configFile.isEmpty()) {
			throw new Exception("Error loading project: No config file specified.");
		} else {
			doc = docBuilder.parse(new File(configFile));
		}
		doc.getDocumentElement().normalize();
		//System.out.println(doc.getDocumentElement().getTagName().toString() + " tree loaded");

		NodeList project;
		try {
			project = doc.getElementsByTagName("project-name");
			if (project.getLength() == 0) throw new Exception("No project name found");
			setProjectName(project.item(0).getFirstChild().getNodeValue());
			project = doc.getElementsByTagName("project-prefix");
			if (project.getLength() == 0) throw new Exception("No project prefix found");
			setProjectPrefix(project.item(0).getFirstChild().getNodeValue());
			project = doc.getElementsByTagName("master-file-suffix");
			if (project.getLength() == 0) throw new Exception("No project short prefix");
			setProjectPrefixShort(project.item(0).getFirstChild().getNodeValue());
		} catch (Exception ex) {
			throw new Exception("Error loading project details from config file." + ex.getMessage());
		}


		try {
			String n = doc.getElementsByTagName("included-file-extensions").item(0).getFirstChild().getNodeValue();
			String[] list = n.split(",");
			setIncludedFileExtensions(Arrays.asList(list));
		} catch (Exception ex) {
			throw new Exception("Error loading included-file-extensions section from config file." + ex.getMessage());
		}

		try {
			setExcludedFileExtensions(Arrays.asList(doc.getElementsByTagName("excluded-file-extensions").item(0).getFirstChild().getNodeValue().toString().split(",")));
		} catch (Exception ex) {
			throw new Exception("Error loading excluded-file-extensions section from config file");
		}

		NodeList tags = doc.getElementsByTagName("input-pattern");
		if (tags.getLength() == 0) throw new Exception("No input patterns configured");
		setInputPatterns(new ArrayList<FocusedPattern>());
		for (int i = 0; i < tags.getLength(); i++) {
			Element pattern = (Element) tags.item(i);
			String cg = pattern.getAttribute("capture-group");
			//here prepend the project prefix to the pattern text
			FocusedPattern fp = new FocusedPattern(
					Integer.valueOf(cg),
					Pattern.compile(tags.item(i).getFirstChild().getNodeValue().toString()));
			getInputPatterns().add(fp);
		}

//		tags = doc.getElementsByTagName("output-pattern");
//		setOutputPatterns(new ArrayList<FocusedPattern>());
//		for (int i = 0; i < tags.getLength(); i++) {
//			FocusedPattern fp = new FocusedPattern(
//					Integer.getInteger(
//							((Element) tags.item(i)).getAttribute("capture-group")
//					),
//					Pattern.compile(
//						getProjectPrefix() + "." + tags.item(i).getFirstChild().getNodeValue().toString()
//					)
//			);
//			getOutputPatterns().add(fp);
//		}

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

		tags = doc.getElementsByTagName("output-prefix");
		setOutputPrefixes(new ArrayList<String>());
		for (int i = 0; i < tags.getLength(); i++) {
			getOutputPrefixes().add(tags.item(i).getFirstChild().getNodeValue().toString());
			if (tags.item(i).hasAttributes() && ((Element) tags.item(i)).getAttribute("default").compareTo("true") == 0) {
				setDefaultOutputPrefix(tags.item(i).getFirstChild().getNodeValue().toString());
				if (((Element) tags.item(i)).hasAttribute("savetofile")
						&& ((Element) tags.item(i)).getAttribute("savetofile").compareTo("") != 0) {
					setSaveDefaultMasterList(true);
					setDefaultMasterListFilename(((Element) tags.item(i)).getAttribute("savetofile").toString());
				}
			}
		}
		setOutputPatterns(new ArrayList<FocusedPattern>());
		for (int i = 0; i < tags.getLength(); i++) {
			FocusedPattern fp = new FocusedPattern(
					Integer.getInteger(
							((Element) tags.item(i)).getAttribute("capture-group")
					),
					Pattern.compile(
							getProjectPrefix() + "\\." + tags.item(i).getFirstChild().getNodeValue().toString()
					)
			);
			getOutputPatterns().add(fp);
		}

		tags = doc.getElementsByTagName("master-file-directory");
		if (tags.getLength() == 0) throw new Exception("No master file directory configured");
		setMasterFileDirectory(tags.item(0).getFirstChild().getNodeValue());

		tags = doc.getElementsByTagName("master-file-suffix");
		if (tags.getLength() == 0) throw new Exception("No master file suffix configured");
		setMasterFileSuffix(tags.item(0).getFirstChild().getNodeValue());
	}
}
