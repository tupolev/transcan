package com.facelift;

import javax.xml.xpath.XPathExpressionException;
import java.util.List;
import java.util.ListIterator;

public class XMerge {
    private XConfig config;
	private List<String> loadedStrings;
	private List<Xliff> masterFiles;
	private Xliff defaultMasterFile;

	private void setConfig(XConfig config) {
		this.config = config;
	}

	private XConfig getConfig() {
		return config;
	}

	private void setLoadedStrings(List<String> loadedStrings) {
		this.loadedStrings = loadedStrings;
	}

	private List<String> getLoadedStrings() {
		return loadedStrings;
	}

	private void setMasterFiles(List<Xliff> masterFiles) {
		this.masterFiles = masterFiles;
	}

	private List<Xliff> getMasterFiles() {
		return masterFiles;
	}

	public XMerge(XConfig config, List<Xliff> masterFiles, Xliff defaultMasterFile, List<String> loadedStrings) {
		setConfig(config);
		setLoadedStrings(loadedStrings);
		setMasterFiles(masterFiles);
		setDefaultMasterFile(defaultMasterFile);
	}

	private Xliff messageBelongsToList(String messageKey) {
		Xliff targetList = null;
		int pos = messageKey.indexOf(".");
		String prefix;
		if (pos >= 0)
			prefix = messageKey.substring(0,pos);
		else
			prefix = "other";
		boolean found = false;
		Xliff item = null;
		ListIterator<Xliff> it = getMasterFiles().listIterator();
		while(!found && it.hasNext()) {
			item = it.next();
			if (item.getPrefix().equals(prefix)) {
				found = true;
				break;
			}
		}
		return (found) ? item : getDefaultMasterFile();
	}

	public void processLoadedStrings() throws XPathExpressionException {
		for (String messageKey : getLoadedStrings()) {
			Xliff list = messageBelongsToList(messageKey);
			list.insert(messageKey, false);
		}
	}



	public void setDefaultMasterFile(Xliff defaultMasterFile) {
		this.defaultMasterFile = defaultMasterFile;
	}

	public Xliff getDefaultMasterFile() {
		return defaultMasterFile;
	}

	public List<Xliff> getUpdatedMasterLists() {
		return getMasterFiles();
	}
}
