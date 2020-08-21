package com.github.caldav4j.model.request;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class ResourceRequest<T> {

    private T resource = null;
    private Charset charset = null;

    private Set<String> etags = new HashSet<>();
    private boolean ifMatch = false;
    private boolean ifNoneMatch = false;
    private boolean allEtags = false;

    public ResourceRequest() {}

    /**
     * Contructor to create a Calendar Based Request body, based on the parameters.
     *
     * <p>Note: If both, ifMatch and ifNoneMatch are set to true, then ifMatch will be selected.
     *
     * @param resource Resource body of the request to set
     * @param etags The set of eTags to match, that will be used in "if-none-match" or "if-match" if
     *     the ifMatch or ifNoneMatch properties are set. Note a quoted string should be provided.
     * @param ifMatch If true the "if-match" conditional header is set
     * @param ifNoneMatch If true the "if-none-match" conditional header
     * @param allEtags Enable all etags, instead of specific ones.
     * @param charset Charset to encode the calendar in. If not provided the JVM default charset is
     *     used.
     */
    public ResourceRequest(
            T resource,
            Charset charset,
            Set<String> etags,
            boolean ifMatch,
            boolean ifNoneMatch,
            boolean allEtags) {
        this.resource = resource;
        this.etags = etags;
        this.ifMatch = ifMatch;
        this.ifNoneMatch = ifNoneMatch;
        this.allEtags = allEtags;
        this.charset = charset;
    }

    public ResourceRequest(T resource) {
        this.resource = resource;
    }

    public ResourceRequest(T resource, Set<String> etags, boolean ifMatch, boolean ifNoneMatch) {
        this.resource = resource;
        this.etags = etags;
        this.ifMatch = ifMatch;
        this.ifNoneMatch = ifNoneMatch;
    }

    public ResourceRequest(T resource, boolean ifMatch, boolean ifNoneMatch, boolean allEtags) {
        this.resource = resource;
        this.ifMatch = ifMatch;
        this.ifNoneMatch = ifNoneMatch;
        this.allEtags = allEtags;
    }

    public T getResource() {
        return resource;
    }

    public void setResource(T resource) {
        this.resource = resource;
    }

    public Set<String> getEtags() {
        return etags;
    }

    public void setEtags(Set<String> etags) {
        this.etags = etags;
    }

    public void addEtag(String etag) {
        etags.add(etag);
    }

    public boolean isIfMatch() {
        return ifMatch;
    }

    public void setIfMatch(boolean ifMatch) {
        this.ifMatch = ifMatch;
    }

    public boolean isIfNoneMatch() {
        return ifNoneMatch;
    }

    public void setIfNoneMatch(boolean ifNoneMatch) {
        this.ifNoneMatch = ifNoneMatch;
    }

    public boolean isAllEtags() {
        return allEtags;
    }

    public void setAllEtags(boolean allEtags) {
        this.allEtags = allEtags;
    }

    public Charset getCharset() {
        if (charset == null) charset = Charset.defaultCharset();

        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
