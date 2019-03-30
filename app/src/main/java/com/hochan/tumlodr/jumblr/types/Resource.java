package com.hochan.tumlodr.jumblr.types;


import com.hochan.tumlodr.jumblr.JumblrClient;

/**
 * A resource that can have a client
 * @author jc
 */
public class Resource {

    protected JumblrClient client;

    /**
     * Set the JumblrClient used for relative calls from this resource
     * @param client the client to use
     */
    public void setClient(JumblrClient client) {
        this.client = client;
    }

    /**
     * Get the client for a particular resource
     * @return the client
     */
    public JumblrClient getClient() {
        return this.client;
    }

}
