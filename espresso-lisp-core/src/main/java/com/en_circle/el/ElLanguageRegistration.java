package com.en_circle.el;

import com.oracle.truffle.api.TruffleFile.FileTypeDetector;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.TruffleLanguage.ContextPolicy;
import com.oracle.truffle.api.TruffleLanguage.Provider;
import com.oracle.truffle.api.TruffleLanguage.Registration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Registration(id = ElLanguage.ID, name = "Espresso Lisp", defaultMimeType = ElLanguage.MIME_TYPE,
        characterMimeTypes = ElLanguage.MIME_TYPE, contextPolicy = ContextPolicy.EXCLUSIVE,
        fileTypeDetectors = ElFileDetector.class)
public class ElLanguageRegistration implements Provider {

    @Override
    public String getLanguageClassName() {
        return ElLanguage.class.getName();
    }

    @Override
    public TruffleLanguage<?> create() {
        return new ElLanguage();
    }

    @Override
    public List<FileTypeDetector> createFileTypeDetectors() {
        return List.of(new ElFileDetector());
    }

    @Override
    public Collection<String> getServicesClassNames() {
        return Collections.emptySet();
    }
}
