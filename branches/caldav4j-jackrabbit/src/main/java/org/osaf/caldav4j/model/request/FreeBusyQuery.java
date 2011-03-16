package org.osaf.caldav4j.model.request;

import java.util.Collection;
import java.util.Map;

import org.osaf.caldav4j.exceptions.UnimplementedException;
import org.osaf.caldav4j.xml.OutputsDOM;
import org.osaf.caldav4j.xml.OutputsDOMBase;

public class FreeBusyQuery extends OutputsDOMBase {

	public FreeBusyQuery(String nsQualCaldav) {
		//TODO 
		throw new UnimplementedException();
	}

	public void setTimeRange(TimeRange timeRange) {
		// TODO Auto-generated method stub
		throw new UnimplementedException();

	}

	public void validate() {
		// TODO Auto-generated method stub
		throw new UnimplementedException();

	}

	@Override
	protected String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getNamespaceQualifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getNamespaceURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Collection<? extends OutputsDOM> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, String> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTextContent() {
		// TODO Auto-generated method stub
		return null;
	}

}
