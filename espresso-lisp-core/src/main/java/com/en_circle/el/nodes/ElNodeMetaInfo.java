package com.en_circle.el.nodes;


import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

public class ElNodeMetaInfo {

    public static ElNodeMetaInfo empty() {
        return new ElNodeMetaInfo();
    }

    public static ElNodeMetaInfo atPlace(Source source, int lineno, int charno) {
        return atCallable(source, source.getName(), lineno, charno);
    }

    public static ElNodeMetaInfo atCallable(Source source, String bindPlace, int lineno, int charno) {
        ElNodeMetaInfo metaInfo = new ElNodeMetaInfo();
        metaInfo.source = source;
        metaInfo.sourceName = source.getName();
        metaInfo.callPlace = bindPlace;
        metaInfo.lineno = lineno;
        metaInfo.charno = charno;
        return metaInfo;
    }

    public static ElNodeMetaInfo nativeMetaInfo(String nativeFunctionName) {
        ElNodeMetaInfo metaInfo = new ElNodeMetaInfo();
        metaInfo.sourceName = "native";
        metaInfo.callPlace = nativeFunctionName;
        return metaInfo;
    }

    private String callPlace;
    private String sourceName;
    private Source source;
    private SourceSection sourceSection;
    private int lineno;
    private int charno;

    private ElNodeMetaInfo() {

    }

    public String getCallPlace() {
        return callPlace;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getLineno() {
        return lineno;
    }

    public int getCharno() {
        return charno;
    }

    public ElNodeMetaInfo copy() {
        ElNodeMetaInfo metaInfo = new ElNodeMetaInfo();
        metaInfo.callPlace = callPlace;
        metaInfo.sourceName = sourceName;
        metaInfo.charno = charno;
        metaInfo.lineno = lineno;
        metaInfo.source = source;
        metaInfo.sourceSection = sourceSection;
        return metaInfo;
    }

    public SourceSection getSourceSection() {
        if (sourceSection == null) {
            if (source == null) {
                return null;
            }
            sourceSection = source.createSection(lineno + 1);
        }
        return sourceSection;
    }
}
