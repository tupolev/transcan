package com.facelift;

import java.util.List;

public class Xliff {

    protected List<String> stringsList;
    protected String outputFileName;

    public Xliff loadStrings(List<String> strings) {
        return this;
    }
    public boolean dumpToFile(String fileName) {
        return true;
    }
}
