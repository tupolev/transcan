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

import com.sun.javafx.geom.Vec3d;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class XliffEngine {

    private List<String> stringsList, outputPrefixes;
    private String outputFileName, outputDirectory, defaultOutputPrefix;
	private boolean outputMultipleFiles;
	private String outputFileSuffix;
	private ArrayList<String> processedItems;
    private String masterFileDirectory;
    private String masterFileSuffix;
    private List<Xliff> masterFilesList;

    private List<String> getStringsList() {
        return stringsList;
    }

    private void setStringsList(List<String> stringsList) {
        this.stringsList = stringsList;
    }

    private String getOutputFileName() {
        return outputFileName;
    }

    private void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
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

	private String getOutputDirectory() {
		return outputDirectory;
	}

	private void setOutputDirectory(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	private boolean isOutputMultipleFiles() {
		return outputMultipleFiles;
	}

	private void setOutputMultipleFiles(boolean outputMultipleFiles) {
		this.outputMultipleFiles = outputMultipleFiles;
	}

	public String getOutputFileSuffix() {
		return outputFileSuffix;
	}

	public void setOutputFileSuffix(String outputFileSuffix) {
		this.outputFileSuffix = outputFileSuffix;
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
        setOutputMultipleFiles(config.isOutputMultiFile());
        setOutputDirectory(config.getOutputDirectory());
        setDefaultOutputPrefix(config.getDefaultOutputPrefix());
        setOutputPrefixes(config.getOutputPrefixes());
        setOutputFileSuffix(config.getOutputFileSuffix());
        setMasterFileDirectory(config.getMasterFileDirectory());
        setMasterFileSuffix(config.getMasterFileSuffix());
    }

	public XliffEngine loadStrings(List<String> strings) {
        setStringsList(strings);
        return this;
    }

	public boolean dumpToFile() {
		this.setProcessedItems(new ArrayList<String>());
		boolean result = true;
		try {
			for (String prefix : this.getOutputPrefixes()) {
				processForPrefix(prefix, getStringsList().listIterator(0));
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
			result = false;
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
			result = false;
		}

		return result;
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
		fileElement.setAttribute("original", "file.ext");
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
		String fileAbsPath = this.getOutputDirectory();
		fileAbsPath += (this.getOutputDirectory().endsWith(File.separator)) ? "" : File.separator;
		fileAbsPath += prefix;
		fileAbsPath += this.getOutputFileSuffix();
		StreamResult sresult = new StreamResult(System.out);
		if (fileAbsPath.compareTo("") != 0) {
			sresult = new StreamResult(new File(fileAbsPath));
		}
		transformer.transform(source, sresult);
		System.out.println("File " + fileAbsPath + " " + "saved!");
	}

    public void loadMasterFiles() {
        File masterDir = new File(getMasterFileDirectory());
        if (masterDir.isDirectory()) {
            File[] listOfFiles = masterDir.listFiles();
            if(listOfFiles!=null) {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].getAbsolutePath().endsWith(getMasterFileSuffix())) {
                        loadMasterFile(listOfFiles[i]);
                    }
                }
            }
        }
    }

    private void loadMasterFile(File masterFile) {
        this.getMasterFilesList().add(new Xliff(masterFile));
    }

    public List<Xliff> getMasterFilesList() {
        return masterFilesList;
    }

    public void setMasterFilesList(List<Xliff> masterFilesList) {
        this.masterFilesList = masterFilesList;
    }
}
