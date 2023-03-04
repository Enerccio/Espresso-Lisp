package com.en_circle.el.runtime;

import com.en_circle.el.nodes.ElNodeMetaInfo;

public interface ElHasSourceInfo {

    ElNodeMetaInfo getMetaInfo();
    void setMetaInfo(ElNodeMetaInfo metaInfo);

    static ElNodeMetaInfo get(Object o) {
        if (o instanceof ElHasSourceInfo hasSourceInfo) {
            if (hasSourceInfo.getMetaInfo() == null) {
                return ElNodeMetaInfo.empty();
            } else {
                return hasSourceInfo.getMetaInfo();
            }
        }
        return ElNodeMetaInfo.empty();
    }

    static Object withSource(Object sourceElement, ElNodeMetaInfo toMetaInfo) {
        if (sourceElement instanceof ElHasSourceInfo withSourceInfo)
            withSourceInfo.setMetaInfo(toMetaInfo);
        return sourceElement;
    }


}
