package com.facelift;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Xliff {

    private File file;
    private Document tree;

    public Xliff() {

    }

    public Xliff(String file) {
        setFile(new File(file));
    }

    public Xliff(File file) {
        setFile(file);
    }

    public void parseFile(String xfile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        setTree(docBuilder.parse(new File(xfile)));
        getTree().getDocumentElement().normalize();
        System.out.println(getTree().getDocumentElement().getTagName().toString() + " tree loaded");
    }

    public void parseFile(File xfile) {
        setFile(xfile);
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
        NodeList result = (NodeList) xPath.evaluate("/trans-unit",
                getTree().getDocumentElement().getFirstChild(), XPathConstants.NODESET);
        return result;
    }

    public Node getTransUnitByID(Integer id) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        Node result = (Node) xPath.evaluate("/trans-unit/source/@id=" + id.toString(),
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
}
