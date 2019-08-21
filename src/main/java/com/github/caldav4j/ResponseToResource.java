package com.github.caldav4j;

import java.io.Serializable;

import org.apache.jackrabbit.webdav.MultiStatusResponse;

public interface ResponseToResource<T extends Serializable> {

    CalDAVResource<T> toResource(MultiStatusResponse response);

}
