package com.github.caldav4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;

import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CalDAV4JMethodFactory;
import com.github.caldav4j.model.ContactInfo;
import com.github.caldav4j.model.request.ContactQuery;

public class CardDAVCollection extends CalDAVCollection {

    public CardDAVCollection(String uri) {
        super(new ResponseToContact());
        setCalendarCollectionRoot(uri);
        setMethodFactory(new CalDAV4JMethodFactory());
        this.prodId = CalDAVConstants.PROC_ID_DEFAULT;
    }
    
    public List<ContactInfo> queryAddressbook(HttpClient httpClient, ContactQuery query)
            throws CalDAV4JException {
        List <ContactInfo> list = new ArrayList<>();
        for (CalDAVResource<ContactInfo> cr: getCalDAVResources(httpClient, query)) {
            list.add(cr.getContact());
        }

        return list;
    }

}
