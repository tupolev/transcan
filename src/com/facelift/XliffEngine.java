package com.facelift;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class XliffEngine {

	private String projectName, projectPrefix, projectPrefixShort;
	private List<String> stringsList, outputPrefixes;
//    private String outputFileName, outputDirectory,
	private String defaultOutputPrefix;
//	private boolean outputMultipleFiles;
	private String outputFileSuffix;
	private ArrayList<String> processedItems;
    private String masterFileDirectory;
    private String masterFileSuffix;
    private List<Xliff> masterFilesList;
	private Xliff defaultMasterFile;
	private String defaultMasterFilename;
	private boolean saveDefaultMasterList;

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

	private List<String> getStringsList() {
        return stringsList;
    }

    private void setStringsList(List<String> stringsList) {
        this.stringsList = stringsList;
    }

	private List<String> getOutputPrefixes() {
		return outputPrefixes;
	}

	private void setOutputPrefixes(List<String> outputPrefixes) {
		this.outputPrefixes = outputPrefixes;
	}

	private String getDefaultOutputPrefix() {
		return defaultOutputPrefix;
	}

	private void setDefaultOutputPrefix(String defaultOutputPrefix) {
		this.defaultOutputPrefix = defaultOutputPrefix;
	}

    public ArrayList<String> getProcessedItems() {
        return processedItems;
    }

    public void setProcessedItems(ArrayList<String> processedItems) {
        this.processedItems = processedItems;
    }

    public void setMasterFileDirectory(String masterFileDirectory) {
        this.masterFileDirectory = masterFileDirectory;
    }

    public String getMasterFileDirectory() {
        return masterFileDirectory;
    }

    public void setMasterFileSuffix(String masterFileSuffix) {
        this.masterFileSuffix = masterFileSuffix;
    }

    public String getMasterFileSuffix() {
        return masterFileSuffix;
    }

    public XliffEngine(XConfig config) {
	    setMasterFilesList(new ArrayList<Xliff>());
        setDefaultOutputPrefix(config.getDefaultOutputPrefix());
        setOutputPrefixes(config.getOutputPrefixes());
        setMasterFileDirectory(config.getMasterFileDirectory());
        setMasterFileSuffix(config.getMasterFileSuffix());
	    setSaveDefaultMasterList(config.getSaveDefaultMasterList());
		setDefaultMasterFilename(config.getDefaultMasterListFilename());
    }

	public XliffEngine loadStrings(List<String> strings) {
        setStringsList(strings);
        return this;
    }

	private void processForPrefix(String prefix, ListIterator<String> itemsIterator) throws TransformerException,
			ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		docBuilder = docFactory.newDocumentBuilder();
		// root elements
		Document doc = docBuilder.newDocument();
		Element xliffElement = doc.createElement("xliff");
		//version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2"
		xliffElement.setAttribute("version", "1.2");
		xliffElement.setAttribute("xmlns", "urn:oasis:names:tc:xliff:document:1.2");
		doc.appendChild(xliffElement);
		//source-language="en-DEV" datatype="plaintext" original="file.ext" target-language="en-DEV"
		Element fileElement = doc.createElement("file");
		fileElement.setAttribute("source-language", "en-DEV");
		fileElement.setAttribute("target-language", "en-DEV");
		fileElement.setAttribute("datatype", "plaintext");
		fileElement.setAttribute("original", prefix);
		xliffElement.appendChild(fileElement);

		Element bodyElement = doc.createElement("body");
		fileElement.appendChild(bodyElement);

		for (int i = 1, siz = getStringsList().size(); (itemsIterator.hasNext() && i < siz); i++) {
			String item = itemsIterator.next();
			if (item.startsWith(prefix + ".") || (prefix.compareTo(this.getDefaultOutputPrefix()) == 0) && !this
					.getProcessedItems().contains(item)) {
				// trans-unit elements
				Element transUnit = doc.createElement("trans-unit");
				transUnit.setAttribute("id", String.valueOf(i));
				// source element
				Element source = doc.createElement("source");
				source.appendChild(doc.createTextNode(item));
				transUnit.appendChild(source);
				// target element
				Element target = doc.createElement("target");
				target.appendChild(doc.createTextNode(item));
				transUnit.appendChild(target);
				bodyElement.appendChild(transUnit);
				this.getProcessedItems().add(item);
			}
		}
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);
		String fileAbsPath = this.getMasterFileDirectory();
		fileAbsPath += (this.getMasterFileDirectory().endsWith(File.separator)) ? "" : File.separator;
		fileAbsPath += prefix;
		fileAbsPath += this.getMasterFileDirectory();
		StreamResult sresult = new StreamResult(System.out);
		if (fileAbsPath.compareTo("") != 0) {
			sresult = new StreamResult(new File(fileAbsPath));
		}
		transformer.transform(source, sresult);
		System.out.println("File " + fileAbsPath + " " + "saved!");
	}

	public XliffEngine loadMasterFiles() throws ParserConfigurationException, SAXException, IOException {
		String projectPrefix = getProjectPrefix();
		List<String> prefixes = getOutputPrefixes();
		String suffix = getMasterFileSuffix();
		String dir = getMasterFileDirectory();
		if (new File(dir).isDirectory()) {
			File masterFile;
			for (String prefix : prefixes) {
				masterFile = new File(dir.concat(File.separator).concat(prefix).concat(suffix));
				if (masterFile.getName().startsWith(getDefaultOutputPrefix() + ".")) {
					loadDefaultMasterFile(
							(getSaveDefaultMasterList())
								? new File(dir.concat(File.separator).concat(getDefaultMasterFilename()))
								: masterFile
					);
				} else {
					loadMasterFile(masterFile);
				}
			}
		} else {
			System.out.println("ERROR: Master directory " + dir + " does not exist.");
		}
		return this;
	}

	private void loadDefaultMasterFile(File masterFile) throws IOException, SAXException, ParserConfigurationException {
		String[] prefix = masterFile.getName().split("\\.");
		Xliff file = new Xliff(masterFile, prefix[0]);
		this.setDefaultMasterFile(file.parse());
	}

	private void loadMasterFile(File masterFile) throws IOException, SAXException, ParserConfigurationException {
	    String[] prefix = masterFile.getName().split("\\.");
		Xliff file = new Xliff(masterFile, prefix[0]);
	    this.getMasterFilesList().add(file.parse());
    }

    public List<Xliff> getMasterFilesList() {
        return masterFilesList;
    }

    public void setMasterFilesList(List<Xliff> masterFilesList) {
        this.masterFilesList = masterFilesList;
    }

	public void setDefaultMasterFile(Xliff defaultMasterFile) {
		this.defaultMasterFile = defaultMasterFile;
	}

	public Xliff getDefaultMasterFile() {
		return defaultMasterFile;
	}

	public String getDefaultMasterFilename() {
		return defaultMasterFilename;
	}

	public void setDefaultMasterFilename(String defaultMasterFilename) {
		this.defaultMasterFilename = defaultMasterFilename;
	}

	public boolean getSaveDefaultMasterList() {
		return saveDefaultMasterList;
	}

	public void setSaveDefaultMasterList(boolean saveDefaultMasterList) {
		this.saveDefaultMasterList = saveDefaultMasterList;
	}
}
