package com.github.caldav4j;

import java.util.List;

import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;

import com.github.caldav4j.model.ContactInfo;
import com.github.caldav4j.model.response.CalendarDataProperty;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.Telephone;

public class ResponseToContact implements ResponseToResource<ContactInfo> {

    @Override
    public CalDAVResource<ContactInfo> toResource(MultiStatusResponse response) {
        ContactInfo contact = new ContactInfo();

        DavPropertyName name = DavPropertyName.create(CalDAVConstants.CARDDAV_ADDRESS_DATA, CalDAVConstants.NAMESPACE_CARDDAV);
        DavProperty<String> prop = (DavProperty<String>) response.getProperties(200).get(name);
        
        VCard vcard = Ezvcard.parse(prop.getValue()).first();
        
        contact.setGivenName(vcard.getStructuredName().getGiven());
        contact.setFamilyName(vcard.getStructuredName().getFamily());
        List<Telephone> numbers = vcard.getTelephoneNumbers();
        if(numbers != null && !numbers.isEmpty()) {
            contact.setMobilePhone(numbers.get(0).getText());
        }
        
        ResourceMetadata resourceMetadata = new ResourceMetadata();
        resourceMetadata.setETag(CalendarDataProperty.getEtagfromResponse(response));
        resourceMetadata.setHref(response.getHref());

        return new CalDAVResource<>(contact, resourceMetadata);
    }

}
