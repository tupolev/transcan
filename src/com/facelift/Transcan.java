package com.facelift;

import java.util.List;

public class Transcan {

    public static void main(String[] args) throws Exception{
//        try {
            XConfig config = new XConfig();
            XliffEngine xliffEngine = new XliffEngine(config);
	        List<Xliff> masterFiles = xliffEngine.loadMasterFiles().getMasterFilesList();
            //load files to a list
            FileScanner fscanner = new FileScanner(config);
            //start xscanner engine
            XScanner scanner = new XScanner();
            scanner.startEngine(fscanner.scanFileList(), config.getInputPatterns(), config.getOutputPatterns()).doMagic();
	        List<String> loadedStrings = scanner.getFilteredOutputList();
	        XMerge merger = new XMerge(config, masterFiles, xliffEngine.getDefaultMasterFile(), loadedStrings);
			merger.processLoadedStrings();
	        //dump results to xliff
	        for(Xliff item : merger.getUpdatedMasterLists()) {
		        item.write(Xliff.DEST_FILE);
	        }
	        if (config.getSaveDefaultMasterList()) {
		        merger.getDefaultMasterFile().write(Xliff.DEST_FILE);
	        } else {
		        merger.getDefaultMasterFile().write(Xliff.DEST_CONSOLE);
	        }
//		} catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
