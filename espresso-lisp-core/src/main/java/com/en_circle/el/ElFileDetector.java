package com.en_circle.el;

import com.oracle.truffle.api.TruffleFile;
import com.oracle.truffle.api.TruffleFile.FileTypeDetector;

import java.io.IOException;
import java.nio.charset.Charset;

public class ElFileDetector implements FileTypeDetector {

    public String findMimeType(TruffleFile file) throws IOException {
        return null;
    }

    public Charset findEncoding(TruffleFile file) throws IOException {
        return null;
    }

}
