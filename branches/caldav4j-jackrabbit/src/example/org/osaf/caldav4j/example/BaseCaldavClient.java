package org.osaf.caldav4j.example;

import java.io.InputStream;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Properties;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.osaf.caldav4j.credential.CaldavCredential;
import org.osaf.caldav4j.methods.CalDAV4JMethodFactory;
import org.osaf.caldav4j.methods.HttpClient;
import org.osaf.caldav4j.methods.MkCalendarMethod;
import org.osaf.caldav4j.methods.PutMethod;




	/**
	 * Base class for CalDAV4j connection,
	 * .TODO use SSL?
	 * FIXME it shouldn't be possibile to change serverhost/port:
	 *  	these should be changed at the same time re-creating the hostConfig
	 */
	public class BaseCaldavClient extends HttpClient {
	    protected HostConfiguration hostConfig = new HostConfiguration();
	    protected CaldavCredential caldavCredential = new CaldavCredential();
	    private String serverHost = caldavCredential.host;
	    private int serverPort = caldavCredential.port;
	    private String serverProtocol = caldavCredential.protocol;
	    private String serverWebDavRoot = caldavCredential.home;
	    private String serverUserName = caldavCredential.user;
	    private String serverPassword = caldavCredential.password;
	    
	    public final String COLLECTION_PATH = caldavCredential.home + caldavCredential.collection;
	    public CalDAV4JMethodFactory methodFactory = new CalDAV4JMethodFactory();

	    public BaseCaldavClient() {
	    	super();
	    	try {
	    		setCredentials(serverUserName, serverPassword);
	    		hostConfig.setHost(this.serverHost, this.serverPort, this.serverProtocol);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		System.out.println("can't create BaseCaldavClient");
	    	}
	    }
	    /**
	     * connect with credentials
	     * @param serverHost
	     * @param serverPort
	     * @param serverProtocol
	     * @param serverWebDavRoot
	     * @param serverUserName
	     * @param serverPassword
	     */
	    public BaseCaldavClient(String serverHost, String serverPort, 
	    						String serverProtocol, String serverWebDavRoot,
	    						String serverUserName, String serverPassword) {
	    	super();

	    	
	    	this.serverHost = serverHost;
	    	this.serverPort = Integer.parseInt(serverPort);
	    	this.serverProtocol = serverProtocol;
	    	this.serverWebDavRoot = serverWebDavRoot;
	    	this.serverUserName = serverUserName;
	    	this.serverPassword = serverPassword;
	    	
	    	try {
	    		setCredentials(serverUserName, serverPassword);
	    		hostConfig.setHost(this.serverHost, this.serverPort, this.serverProtocol);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    		System.out.println("can't create BaseCaldavClient");
	    	}
	    }
	    
	    /**
	     * TODO this method misses the property file, needs to be implemented
	     * connect without credentials using  properties set 
	     * @param props
	     */
	    public BaseCaldavClient(Properties props) {
	    	this(props.get("CALDAV_SERVER_HOST").toString(),
	    			props.get("CALDAV_SERVER_PORT").toString(),
	    			props.get("CALDAV_SERVER_PROTOCOL").toString(),
	    			props.get("CALDAV_SERVER_WEBDAV_ROOT").toString());
	    }
	    
	    /**
	     * connect without credentials
	     * @param host
	     * @param port
	     * @param protocol
	     * @param webDavRoot
	     */
	    public BaseCaldavClient(String host, String port, 
				String protocol, String webDavRoot) {
	    	this(host, port, protocol, webDavRoot, null, null);
	    }
	    
	    
	    public String getCalDavServerHost() {
	        return serverHost;
	    }
	    
	    public void setCalDavServerHost(String serverHost) {
	        this.serverHost = serverHost;
	    }
	    
	    public int getCalDavServerPort() {
	        return serverPort;
	    }
	    
	    public void setCalDavServerPort(int serverPort){
	        this.serverPort = serverPort;
	    }
	    
	    public String getCalDavSeverProtocol() {
	        return serverProtocol;
	    }
	    
	    public void setCalDavSeverProtocol(String serverProtocol) {
	        this.serverProtocol = serverProtocol;
	    }
	    
	    
	    public String getCalDavSeverWebDAVRoot() {
	        return serverWebDavRoot;
	    }
	    
	    public void setCalDavSeverWebDAVRoot(String serverWebDavRoot) {
	        this.serverWebDavRoot = serverWebDavRoot;
	    }
	    
	    public String getCalDavSeverUsername() {
	        return serverUserName;
	    }
	    
	    public void setCalDavSeverUsername(String serverUserName) {
	        this.serverUserName = serverUserName;
	    }
	    
	    public String getCalDavSeverPassword() {
	        return serverPassword;
	    }
	    
	    public void setCalDavSeverPassword(String serverPassword) {
	        this.serverPassword = serverPassword;
	    }
	    
	    /**
	     * set credentials
	     * @return
	     */
	    protected void setCredentials(String user, String pass) {

	    	Credentials credentials = new UsernamePasswordCredentials(user, pass);
	        
	        getParams().setAuthenticationPreemptive(true);
	        getState().setCredentials(AuthScope.ANY,credentials);
	    }
	    

	    
	    protected Calendar getCalendarResource(String resourceName) {
	        Calendar cal;

	        InputStream stream = this.getClass().getClassLoader()
	                .getResourceAsStream(resourceName);
	        CalendarBuilder cb = new CalendarBuilder();
	        
	        try {
	            cal = cb.build(stream);
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	        
	        return cal;
	    }    
	    
	    protected void put(String resourceFileName, String path) {
	        PutMethod put = methodFactory.createPutMethod();
	        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resourceFileName);
	        
	       // put.setRequestBody(stream);  deprecated
	        put.setRequestEntity(new InputStreamRequestEntity(stream));
	        
	        put.setPath(path);
	        try {
	            executeMethod(hostConfig, put);
	        } catch (Exception e){
	            throw new RuntimeException(e);
	        }
	    }
	    
	    protected void del(String path) {
	        DeleteMethod delete = new DeleteMethod();
	        delete.setPath(path);
	        try {
	        	executeMethod(hostConfig, delete);
	        } catch (Exception e){
	            throw new RuntimeException(e);
	        }
	    }
	    
	    protected void mkdir(String path) {
	        MkCalendarMethod mk = new MkCalendarMethod(COLLECTION_PATH);
	        mk.setPath(path);
	        try {
	        	executeMethod(hostConfig, mk);
	        } catch (Exception e){
	            throw new RuntimeException(e);
	        }
	    }
	    
	    // ************* test *****************
		public static void main(String[] args) throws SocketException, URISyntaxException {
			
			BaseCaldavClient cli = new BaseCaldavClient();
			
	}
}
