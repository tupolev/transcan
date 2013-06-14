package com.facelift;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.util.*;

public class Xliff {

    private File file;
    private Document tree;
	private String prefix;

	public static final int DEST_FILE = 0;
	public static final int DEST_CONSOLE = 1;

	public Xliff() {

    }

    public Xliff(String file, String prefix) {
        setFile(new File(file));
	    setPrefix(prefix);
    }

	public Xliff(File file, String prefix) {
		setFile(file);
		setPrefix(prefix);
	}

    public Xliff parseFile(String xfile) {
        setFile(new File(xfile));
	    return this;
    }

    public Xliff parseFile(File xfile) {
        setFile(xfile);
	    return this;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setTree(Document tree) {
        this.tree = tree;
    }

    public Document getTree() {
        return tree;
    }

    public NodeList getTransUnitList() throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        NodeList result = (NodeList) xPath.evaluate("/xliff/file/body/trans-unit",
                getTree().getDocumentElement().getFirstChild(), XPathConstants.NODESET);
        return result;
    }

    public Node getTransUnitByID(Integer id) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        Node result = (Node) xPath.evaluate("/xliff/file/body/trans-unit[@id=" + id.toString() + "]",
                getTree().getDocumentElement().getFirstChild(), XPathConstants.NODE);
        return result;
    }

    public HashMap<String,String> getListFromTransUnit(Node transUnit) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        HashMap<String,String> list = new HashMap<String,String>();
        list.put("id",(String) xPath.evaluate("@id", transUnit.getFirstChild(), XPathConstants.STRING));
        list.put("source",(String) xPath.evaluate("source", transUnit.getFirstChild(), XPathConstants.STRING));
        list.put("target",(String) xPath.evaluate("target", transUnit.getFirstChild(), XPathConstants.STRING));
        return list;

    }

	public int findTransUnitIDByMessageKey(String messageKey) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		Node element = getTree().getDocumentElement().getFirstChild();
		NodeList result = (NodeList) xPath.evaluate("/xliff/file/body/trans-unit/source/text()",
				element, XPathConstants.NODESET);
		int found = -1;
		for(int index = 0, len = result.getLength(); ((found==-1) && (index<len)); index++) {
			if (result.item(index).getNodeValue().equals(messageKey)) {
				found = index;

			}
		}

		return found;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void insert(String messageKey, boolean updateOnExists) throws XPathExpressionException {
		int index = findTransUnitIDByMessageKey(messageKey);
		if (index < 0) {
			insertTransUnitNode(messageKey, messageKey);
		} else {
			if (updateOnExists) {
			}
		}
	}

	private void insertTransUnitNode(String messageKey, String translation) throws XPathExpressionException {
		// trans-unit elements
		Element transUnit = getTree().createElement("trans-unit");
		transUnit.setAttribute("id", String.valueOf(getMaxTransUnitID() + 1));
		// source element
		Element source = getTree().createElement("source");
		source.appendChild(getTree().createTextNode(messageKey));
		transUnit.appendChild(source);
		// target element
		Element target = getTree().createElement("target");
		target.appendChild(getTree().createTextNode(translation));
		transUnit.appendChild(target);
		getBodyElement(getTree()).appendChild(transUnit);
	}

	private int getMaxTransUnitID() throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		NodeList result = (NodeList) xPath.evaluate("/xliff/file/body/trans-unit", tree, XPathConstants.NODESET);

		List<Node> nodeArrayList = new ArrayList<Node>();
		for(int i=0, len = result.getLength(); i<len; i++)
			nodeArrayList.add(result.item(i));

		Collections.sort(nodeArrayList,new TransUnitNodeComparator());

		debugNodeList(nodeArrayList);
		int len = nodeArrayList.size();
		Element el;
		int max = 0;
		if (len >= 1) {
			el = (Element) nodeArrayList.get(len - 1);
			max = Integer.parseInt(el.getAttribute("id").toString());
		}
		return max;
	}
//
//	private void sortNodeArrayList(List<Node>) {
//		for()
//	}

	private void debugNodeList(List<Node> list) {
		for(int i=0, len = list.size(); i<len; i++) {
			Element e = (Element)list.get(i);
			System.out.println(
					e.getAttribute("id")
							+ " -> " +
							e.getElementsByTagName("source").item(0).getFirstChild().getNodeValue()
			);
		}

	}

	private Node getBodyElement(Document tree) throws XPathExpressionException {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = factory.newXPath();
		Node result = (Node) xPath.evaluate("/xliff/file/body",
				tree, XPathConstants.NODE);
		return result;
	}

	public void writeToConsole() throws TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(getTree());
		String fileAbsPath = getFile().getAbsolutePath();
		StreamResult sresult = new StreamResult(System.out);
		transformer.transform(source, sresult);
		System.out.println("File " + fileAbsPath + " " + "saved!");
	}

	public void write() throws TransformerException {
		write(Xliff.DEST_FILE);
	}

	public void write(int destination) throws TransformerException {
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(getTree());
		String fileAbsPath = getFile().getAbsolutePath();
//		fileAbsPath += (getFile().getAbsolutePath().endsWith(File.separator)) ? "" : File.separator;
//		fileAbsPath += prefix;
//		fileAbsPath += this.getOutputFileSuffix();
		StreamResult sresult = new StreamResult(System.out);
		if (destination != Xliff.DEST_CONSOLE && fileAbsPath.compareTo("") != 0) {
			sresult = new StreamResult(new File(fileAbsPath));
		}
		transformer.transform(source, sresult);
		System.out.println("File " + fileAbsPath + " " + "saved!");
	}

	public Xliff parse() throws ParserConfigurationException {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			setTree(docBuilder.parse(getFile()));
		} catch (Exception ex) {
			setTree(getEmptyTree());
		}
		getTree().getDocumentElement().normalize();
		System.out.println(getTree().getDocumentElement().getTagName().toString() + " tree loaded");
		return this;
	}

	public Document getEmptyTree() throws ParserConfigurationException {
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

		return doc;
	}
}
