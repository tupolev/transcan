package com.facelift;

public class Transcan {

    public static void main(String[] args) {
        try {
            XConfig config = new XConfig();
            //load files to a list
            FileScanner fscanner = new FileScanner(config);
            //start xscanner engine
            XScanner scanner = new XScanner();
            scanner.startEngine(fscanner.scanFileList(), config.getInputPatterns(), config.getOutputPatterns()).doMagic();
            //dump results to xliff
            Xliff xliff = new Xliff(config);
            xliff.loadStrings(scanner.getFilteredOutputList()).dumpToFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
