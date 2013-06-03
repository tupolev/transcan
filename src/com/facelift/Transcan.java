package com.facelift;


import java.util.ArrayList;
import java.util.regex.Pattern;

public class Transcan {

    public static void main(String[] args){
        if (args.length < 2) {
            System.out.println("Error: missing arguments");
            return;
        }
        String rootDir = (args.length > 1) ? args[0] : ".";
        String outFileName = (args.length > 1) ? args[1] : "file.xlf";

        try {
            //load file extensions
            //TODO load file extensions from config file
            String fileExtensions = "php,js,html";
            //create pattern list
            //TODO load patterns from config file
            ArrayList<Pattern> inputPatternList , outputFilterPatternList;
            inputPatternList = new ArrayList<Pattern>();
            outputFilterPatternList = new ArrayList<Pattern>();
            inputPatternList.add(0, Pattern.compile("(\\{\\{)(\\s*)(\\\"|\\')(.+)(\\\"|\\')(\\s*)(\\|)(\\s*)trans(\\s*)(\\}\\})"));
            inputPatternList.add(1, Pattern.compile("_\\((\\'|\\\")(.+)(\\'|\\\")\\)"));
            outputFilterPatternList.add(0, Pattern.compile("\\'((\\w|\\.){3,})\\'"));
            //load files to a list
            FileScanner fscanner = new FileScanner(rootDir,fileExtensions);
            //start xscanner engine
            XScanner scanner = new XScanner();
            scanner.startEngine(fscanner.scanFileList(), inputPatternList, outputFilterPatternList).doMagic();
            //dump results to xliff
            Xliff xliff = new Xliff();
            xliff.loadStrings(scanner.getFilteredOutputList()).dumpToFile(outFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
