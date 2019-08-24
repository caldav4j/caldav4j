package com.github.caldav4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;

import com.github.caldav4j.exceptions.CalDAV4JException;
import com.github.caldav4j.methods.CardDAVMethodFactory;
import com.github.caldav4j.model.ContactInfo;
import com.github.caldav4j.model.request.ContactQuery;

public class CardDAVCollection extends CalDAVCalendarCollectionBase<ContactInfo> {

    public CardDAVCollection(String uri) {
        super(new ResponseToContact());
        setCalendarCollectionRoot(uri);
        setMethodFactory(new CardDAVMethodFactory());
        this.prodId = CalDAVConstants.PROC_ID_DEFAULT;
    }
    
    public List<ContactInfo> queryAddressbook(HttpClient httpClient, ContactQuery query)
            throws CalDAV4JException {
        List <ContactInfo> list = new ArrayList<>();
        for (CalDAVResource<ContactInfo> cr: getCalDAVResources(httpClient, query)) {
            list.add(cr.getPayload());
        }

        return list;
    }

}
