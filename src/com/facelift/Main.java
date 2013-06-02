package com.facelift;

import sun.misc.Regexp;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args){
        if (args.length < 3) {
            System.out.println("Error: missing arguments");
            return;
        }
        String rootDir = (args.length > 2) ? args[1] : ".";
        String outFileName = (args.length > 2) ? args[3] : "file.xlf";

        try {
            //load file extensions
            //TODO load file extensions from config file
            String fileExtensions = "php,js,html";
            //create pattern list
            //TODO load patterns from config file
            ArrayList<Regexp> patternList = new ArrayList<Regexp>();
            patternList.add(0,new Regexp("(\\{\\{)(\\s*)(\\\"|\\')(.+)(\\\"|\\')(\\s*)(\\|)(\\s*)trans(\\s*)(\\}\\})"));
            patternList.add(1,new Regexp("_\\((\\'|\\\")(.+)(\\'|\\\")\\)"));
            //load files to a list
            FileScanner fscanner = new FileScanner(rootDir,fileExtensions);
            List<String> fileList = fscanner.scanFileList();
            //start xscanner engine
            XScanner scanner = new XScanner();
            List<String> outList = null;
            scanner.startEngine(fileList, patternList).doMagic();
            outList = scanner.getOutputList();
            //dump results to xliff
            Xliff xliff = new Xliff();
            xliff.loadStrings(outList).dumpToFile(outFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
