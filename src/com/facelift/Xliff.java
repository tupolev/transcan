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

import java.util.Iterator;
import java.util.List;

public class Xliff {

    public List<String> getStringsList() {
        return stringsList;
    }

    public void setStringsList(List<String> stringsList) {
        this.stringsList = stringsList;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    protected List<String> stringsList;
    protected String outputFileName;

    public Xliff loadStrings(List<String> strings) {
        setStringsList(strings);
        return this;
    }
    public boolean dumpToFile(String fileName) {
        setOutputFileName(fileName);
        boolean result = true;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = null;
            docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element xliffElement = doc.createElement("xliff");
            //version="1.2" xmlns="urn:oasis:names:tc:xliff:document:1.2"
            xliffElement.setAttribute("version","1.2");
            xliffElement.setAttribute("xmlns","urn:oasis:names:tc:xliff:document:1.2");
            doc.appendChild(xliffElement);
            //source-language="en-DEV" datatype="plaintext" original="file.ext" target-language="en-DEV"
            Element fileElement = doc.createElement("file");
            fileElement.setAttribute("source-language","en-DEV");
            fileElement.setAttribute("target-language","en-DEV");
            fileElement.setAttribute("datatype","plaintext");
            fileElement.setAttribute("original","file.ext");
            xliffElement.appendChild(fileElement);

            Element bodyElement = doc.createElement("body");
            fileElement.appendChild(bodyElement);

            Iterator<String> itemsIterator = getStringsList().iterator();

            for (int i=1, siz = getStringsList().size(); (itemsIterator.hasNext() && i< siz); i++ ) {
                String item = itemsIterator.next();
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
            }
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult sresult = new StreamResult(System.out);
            if (getOutputFileName().compareTo("") != 0) {
                sresult = new StreamResult(new File(getOutputFileName()));
            }
            transformer.transform(source, sresult);
            System.out.println("File " + getOutputFileName() + " saved!");
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            result = false;
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
            result = false;
        }

        return result;
    }
}
