package org.osaf.caldav4j.methods;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.List;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.webdav.client.methods.AclMethod;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.security.AclProperty;
import org.apache.jackrabbit.webdav.security.Principal;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.security.AclProperty.Ace;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.junit.Test;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.exceptions.CalDAV4JException;
import org.osaf.caldav4j.model.request.CalendarDescription;
import org.osaf.caldav4j.model.request.DisplayName;
import org.osaf.caldav4j.model.request.PropProperty;
import org.osaf.caldav4j.model.util.PropertyFactory;
import org.osaf.caldav4j.util.XMLUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PropFindTest extends BaseTestCase {


	public PropFindTest() {
		super();
	}
	private static final Log log = LogFactory.getLog(PropFindTest.class);


	public void setUp() throws Exception {
		super.setUp();
		mkcalendar(COLLECTION_PATH);
	}

	public void tearDown() throws Exception {
		super.tearDown();
//		del(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
		del(COLLECTION_PATH);
	}

	@SuppressWarnings("unchecked")
	public void donttestGetAcl() throws Exception {
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		PropFindMethod propfind = new PropFindMethod(caldavCredential.home);
		propfind.setPath(caldavCredential.home);

		PropProperty propFindTag = PropertyFactory.createProperty(PropertyFactory.PROPFIND);
		PropProperty aclTag = PropertyFactory.createProperty(PropertyFactory.ACL);
		PropProperty propTag = new PropProperty(CalDAVConstants.NS_DAV,"D","prop");
		propTag.addChild(aclTag);
//		propTag.addChild(new DisplayName());
//		propTag.addChild(new CalendarDescription());
		propFindTag.addChild(propTag);
		propfind.setPropFindRequest(propFindTag);
		propfind.setDepth(0);
//		try {
//			http.executeMethod(hostConfig,propfind);
//			
//			Enumeration<Property> myEnum = propfind.getResponseProperties(caldavCredential.home);
//			/*
//			 * response
//			 *   href
//			 *   propstat
//			 *      prop
//			 *         acl
//			 *            ace
//			 *               principal
//			 *                  property
//			 *                     owner
//			 *               grant
//			 *                  privilege
//			 *               inherited
//			 *                  href
//			 *            ace
//			 *            ,,,      
//			 */
//			while (myEnum.hasMoreElements()) {
//				AclProperty prop = (AclProperty) myEnum.nextElement();
//				NodeList nl = prop.getElement().getElementsByTagName("ace");
//				log.info(prop.getPropertyAsString());
//				Ace[] aces = (Ace[]) prop.getAces();
//				log.info(aces[0]);
//
//				log.info("There are aces # "+ nl.getLength() );
//				for (int j=0; j<nl.getLength(); j++) {
//					log.info("ace number "+ j);
//					Element o = (Element) nl.item(j);
//					// log.info("O:" +o.getNodeName() + o.getNodeValue() + o.getTextContent());
//
//					NodeList nl1 = o.getElementsByTagName("grant");
//					for ( int l=0; l<nl1.getLength(); l++) {
//						Element o1 = (Element) nl1.item(l);
//						log.info("O:" +o1.getTagName() );
//						if (o1.getNodeValue() == null) {
//							parseNode(o1);
//						}
//					}
//					 nl1 = o.getElementsByTagName("principal");
//						for ( int l=0; l<nl1.getLength(); l++) {
//							Element o1 = (Element) nl1.item(l);
//							log.info("O:" +o1.getTagName() );
//							if (o1.getNodeValue() == null) {
//								parseNode(o1);
//							}
//						}
//				} // aces
//
//				for (int k=0; k<prop.getAces().length; k++) {
//					Ace ace = null;
//					ace = (Ace) prop.getAces()[k];
//					log.info("ace:" + prop.getElement().getChildNodes());
//					log.info("inherited by: " + ace.getInheritedFrom() + ";" +
//							"principal is: " + ace.getPrincipal() + ";" +
//									"localname (if principal==property) e':" + ace.getProperty().getLocalName()+ ";" );
//					Enumeration<Privilege> privs = ace.enumeratePrivileges();
//					while (privs.hasMoreElements()) {
//						Privilege priv = privs.nextElement();
//						log.info("further elements: " +"ns:" + priv.getNamespace() +":"+ priv.getName() + 
//								"; "+ priv.getParameter());
//					}
//					ace.addPrivilege(new Privilege(CalDAVConstants.NS_DAV,"spada","read"));
//				}
//				log.info(prop);
//			}
//			Ace test = new Ace("<property><owner/></property>");
//			test.addPrivilege(new Privilege(CalDAVConstants.NS_DAV,"grant","read"));
//			log.info(test);
//
//		} catch (HttpException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
	public void donttestGetAcl_1() throws IOException {
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		PropFindMethod propfind = new PropFindMethod(caldavCredential.home);
		propfind.setPath(caldavCredential.home);

		PropProperty propFindTag = new PropProperty(CalDAVConstants.NS_DAV,"D","propfind");
		PropProperty aclTag = new PropProperty(CalDAVConstants.NS_DAV,"D","acl");
		PropProperty propTag = new PropProperty(CalDAVConstants.NS_DAV,"D","prop");
		propTag.addChild(aclTag);
		propTag.addChild(new DisplayName());
		propTag.addChild(new CalendarDescription());
		propFindTag.addChild(propTag);
		propfind.setPropFindRequest(propFindTag);
		propfind.setDepth(0);
		try {
			http.executeMethod(hostConfig,propfind);
			//Hashtable<String, CalDAVResponse> hashme = propfind.getResponseHashtable();

			//Enumeration<Property> myEnum = propfind.getResponseProperties(caldavCredential.home);
			//while (myEnum.hasMoreElements()) {
	      for (DavProperty<?> prop : propfind.getResponseTable()){
   
				log.info("new Property element");
				//BaseProperty e =  (BaseProperty) myEnum.nextElement();
				log.info(prop.getName());
				//AclProperty prop = (AclProperty) e;
				log.info(prop.getValue());
				List<Ace> aces =  propfind.getAces(caldavCredential.home);
				log.info("There are aces # "+ aces.size() );

				for (Ace ace:aces) {
					printAce(ace);
				}
				log.info(prop);
			}
//			Ace test = new Ace("property");
//			test.setProperty(new PropertyName(CalDAVConstants.NS_DAV, "owner"));
//			test.addPrivilege(new Privilege(CalDAVConstants.NS_DAV,"grant","read"));
			Ace test = AclProperty.createGrantAce(
			      Principal.getSelfPrincipal(), new Privilege[]{Privilege.PRIVILEGE_READ}, true, true, null);
			printAce(test);


		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CalDAV4JException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

	}
	
	/**
	 * TODO this test will work only on bedework which has a set of permission set
	 * @throws CalDAV4JException 
	 * @throws IOException 
	 */
	@Test
	public void testNewPropfind() throws CalDAV4JException, IOException {
		log.info("New Propfind");
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		PropFindMethod propfind = new PropFindMethod(COLLECTION_PATH);
		propfind.setPath(COLLECTION_PATH);

		PropProperty propFindTag = new PropProperty(CalDAVConstants.NS_DAV,"D","propfind");
		PropProperty aclTag = new PropProperty(CalDAVConstants.NS_DAV,"D","acl");
		PropProperty propTag = new PropProperty(CalDAVConstants.NS_DAV,"D","prop");
		propTag.addChild(aclTag);
		propTag.addChild(new DisplayName());
		propTag.addChild(new CalendarDescription());
		propFindTag.addChild(propTag);
		propfind.setPropFindRequest(propFindTag);
		//propfind.setDepth(0);
		try {
			http.executeMethod(hostConfig,propfind);
			
			// check that Calendar-description and DisplayName matches
			log.info("DisplayName: " + 
			      propfind.getDisplayName(COLLECTION_PATH));
			//assertEquals(caldavCredential.collection.replaceAll("/$", ""), propfind.getDisplayName(COLLECTION_PATH).replaceAll("/$", ""));			

			log.info("CalendarDescription: " +  
			      propfind.getCalendarDescription(COLLECTION_PATH));
			//assertEquals(CALENDAR_DESCRIPTION, propfind.getCalendarDescription(COLLECTION_PATH));

			// check that ACLs matches         
			List<Ace> aces =  propfind.getAces(COLLECTION_PATH);
			log.info("There are aces # "+ aces.size() );
			//AclProperty.createFromXml(acl.getName().);
			for (Object obj: aces){
			  // if(!(obj instanceof Ace)) continue;
			   try {
			      Ace ace = (Ace)obj;
			      String href = ace.getInheritedHref();
               //TODO assertEquals("/user",href);

               Principal principal = ace.getPrincipal();
               	//pdav.isOwner();
             //TODO assertEquals("property", ace.getPrincipal());
               	//assertEquals("owner", ace.getProperty().getLocalName());
               	//Privilege p = (Privilege) aces[k].enumeratePrivileges().nextElement();
               	//assertEquals("all", p.getName());

             //TODO assertEquals(CalDAVConstants.DAV_PRINCIPAL_AUTHENTICATED, ace.getPrincipal());
               	//p = (Privilege) ace.enumeratePrivileges().nextElement();
               	//assertTrue( p.getName().contains(CalDAVConstants.CALDAV_PRIVILEGE_READ_FREE_BUSY));

               printAce(ace);
            } catch (Exception e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
			}

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
      }

	}
	
	@Test
	public void testAclMethod() {
		log.info("New Propfind");
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();
		Privilege[] privs =new Privilege[]{
		      Privilege.PRIVILEGE_WRITE,
		      Privilege.PRIVILEGE_READ,
		      Privilege.getPrivilege("schedule-query-freebusy", SecurityConstants.NAMESPACE)
		      };
		Ace ace = AclProperty.createGrantAce(Principal.getSelfPrincipal(),
             privs, false, false, null);


		try {
		   AclMethod method = new AclMethod(COLLECTION_PATH,new AclProperty(new Ace[]{ace}));

			http.executeMethod(hostConfig,method);
			
			
			// verify output
			PropFindMethod propfind = new PropFindMethod(COLLECTION_PATH);
			propfind.setPath(COLLECTION_PATH);
			PropProperty propFindTag = new PropProperty(CalDAVConstants.NS_DAV,"D","propfind");
			PropProperty aclTag = new PropProperty(CalDAVConstants.NS_DAV,"D","acl");
			PropProperty propTag = new PropProperty(CalDAVConstants.NS_DAV,"D","prop");
			propTag.addChild(aclTag);
			propTag.addChild(new DisplayName());
			propTag.addChild(new CalendarDescription());
			propFindTag.addChild(propTag);
			propfind.setPropFindRequest(propFindTag);
			//propfind.setDepth(0);
			http.executeMethod(hostConfig,propfind);

			log.info("post setacl returns: "+ propfind.getResponseBodyAsString());
			// TODO check returned ACIS
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}

	@SuppressWarnings("unchecked")
	private void printAce(Ace ace) {
		PropProperty principal =	new PropProperty(CalDAVConstants.NS_DAV, CalDAVConstants.NS_QUAL_DAV, "property");
		principal.addChild(new PropProperty(ace.getInheritedHref(), CalDAVConstants.NS_QUAL_DAV,"principal"));
		String stringFormattedAci = String.format("ACE:" + 
				" principal: %s ", "property".equals(ace.getPrincipal()==null) ? 
				      XMLUtils.prettyPrint(principal)  : 
				         ((ace.getPrincipal().getPropertyName()==null) ?"":ace.getPrincipal().getPropertyName().getName()) +
						" ereditata da: " + ace.getInheritedHref() + ";" );
		log.info( stringFormattedAci );

		log.info("privileges are" );
		for(Privilege priv:ace.getPrivileges()) {					
			log.info(String.format("<privilege><%s %s/></privilege>",  priv.getNamespace().getURI(), priv.getName() ));
		}

	}
	private void parseNode(Element e) {
		try {
			log.info("node is:" + e.getClass().getName());			
			NodeList nl = e.getChildNodes();
			for (int i=0; i< nl.getLength(); i++) {
				Element el =  DomUtil.getFirstChildElement(nl.item(i) );//, "DAV:", "privilege");
				log.info("child is:" + el.getClass().getName());
			//	log.info("parseNode:" + el.getNodeName() + el.getNodeType() + el.getNodeValue()+el.getTextContent());
			}
		} catch (Exception ex) {
			log.warn("Error while parsing node: "+ e);
			log.warn("Error while parsing node: "+ ex);
		}
	}
}
