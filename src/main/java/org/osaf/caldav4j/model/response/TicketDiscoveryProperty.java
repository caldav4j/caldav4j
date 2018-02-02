/*
 * Copyright 2006 Open Source Applications Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osaf.caldav4j.model.response;

import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses a MultiStatus or MultiStatus Response into TicketResponses.
 *
 * @author EdBindl
 * @deprecated All Ticket related classes are now deprecated. Since, 0.9
 */
public class TicketDiscoveryProperty {

	private static final Logger log = LoggerFactory.getLogger(TicketDiscoveryProperty.class);

	List<TicketResponse> tickets = null;

	public static final String ELEMENT_CALENDAR_DATA = "ticketdiscovery";

	/**
	 * Create a Property from the responses
	 *
	 * @param responses Responses to build from.
	 */
	public TicketDiscoveryProperty(MultiStatus responses) {
		if(responses != null)
			try {
				parseTickets(responses.toXml(DomUtil.createDocument()));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
	}

	/**
	 * Create a Property from the responses
	 * @param response Responses to build from.
	 */
	public TicketDiscoveryProperty(MultiStatusResponse response) {
		if(response != null)
			try {
				parseTickets(response.toXml(DomUtil.createDocument()));
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
	}

	public List<TicketResponse> getTickets() {
		if (tickets == null) {
			return null;
		}
		return tickets;
	}
	
	public List<String> getTicketIDs(){
		if (tickets == null) {
			return null;
		}
		List<String> ticketIDs = new ArrayList<String>();
		
		int length = tickets.size();

		for (int r = 0; r < length; r++) {
			ticketIDs.add(tickets.get(r).getID());
		}
		return ticketIDs;
	}

	/**
	 * Handles the reponse and Stores each ticketinfo as a TicketResponse
	 * Object, in a List of TicketResponses
	 * @param element Xml Element containing Tickets
	 */
	public void parseTickets(Element element) {
		tickets = new ArrayList<TicketResponse>();

        if (element != null) {
            NodeList list = element.getElementsByTagNameNS(
                    CalDAVConstants.NS_XYTHOS,
                    CalDAVConstants.ELEM_TICKETINFO);
            int length = list.getLength();

            for (int r = 0; r < length; r++) {
                Element temp = (Element) list.item(r);
                tickets.add(XMLUtils.createTicketResponseFromDOM(temp));
            }
        }
    }

//	/**
//	 * From a <D:ticketdiscovery>...</D:ticketdiscovery> property, return the ID
//	 * @param property
//	 * @return
//     */
//	public static List<String> getTicketIDs(DavProperty property){
//        List<String> ticketIds = new ArrayList<String>();
//		if(property.getName().getName().equals(CalDAVConstants.ELEM_TICKETDISCOVERY)){
//			DavPropertySet set = PropProperty.getChildrenfromValue(property);
//			if(set != null) {
//                DavPropertyName id = DavPropertyName.create(CalDAVConstants.ELEM_ID);
//                for(DavProperty ticketinfo: set) {
//                    if(ticketinfo.getName().getName().equals(CalDAVConstants.ELEM_TICKETINFO)) {
//                        DavPropertySet ticketinfochildren = PropProperty.getChildrenfromValue(ticketinfo);
//                        ticketIds.add(ticketinfochildren.get(id).getValue().toString());
//                    }
//                }
//			}
//		}
//
//		return ticketIds;
//	}
//
//	public static List<String> getTicketIDs(MultiStatusResponse response){
//		return getTicketIDs(response.getProperties(CaldavStatus.SC_OK).get(CalDAVConstants.DNAME_TICKETDISCOVERY));
//	}
//
//	public static List<String> getTicketIDs(MultiStatusResponse[] responses){
//		List<String> ticketIDs = new ArrayList<String>();
//		for (MultiStatusResponse response : responses)
//		 	ticketIDs.addAll(getTicketIDs(response));
//		return  ticketIDs;
//	}
}
