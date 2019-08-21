package com.github.caldav4j.model.request;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;

import com.github.caldav4j.CalDAVConstants;
import com.github.caldav4j.xml.OutputsDOMBase;

public class ContactQuery extends OutputsDOMBase implements CalDAVReportRequest {

    private static final String ADDRESS_DATA = CalDAVConstants.CARDDAV_ADDRESS_DATA;
    private static final String GETETAG = "getetag";
    private static final String ELEMENT_NAME = "addressbook-query";

    @Override
    protected String getElementName() {
        return ELEMENT_NAME;
    }

    @Override
    protected Namespace getNamespace() {
        return CalDAVConstants.NAMESPACE_CARDDAV;
    }

    @Override
    protected Collection<? extends XmlSerializable> getChildren() {
        DavPropertyNameSet set = new DavPropertyNameSet();
        set.add(GETETAG, CalDAVConstants.NAMESPACE_WEBDAV);
        set.add(ADDRESS_DATA, CalDAVConstants.NAMESPACE_CARDDAV);
        return Arrays.asList(set);
    }

    @Override
    protected Map<String, String> getAttributes() {
        // No attributes
        return null;
    }

    @Override
    protected String getTextContent() {
        // Not text content
        return null;
    }


}
