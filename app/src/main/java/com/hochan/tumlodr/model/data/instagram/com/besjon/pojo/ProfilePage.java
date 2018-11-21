/**
  * Copyright 2018 bejson.com 
  */
package com.hochan.tumlodr.model.data.instagram.com.besjon.pojo;

/**
 * Auto-generated: 2018-04-05 9:39:19
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ProfilePage {

    private String logging_page_id;
    private boolean show_suggested_profiles;
    private Graphql graphql;
    public void setLogging_page_id(String logging_page_id) {
         this.logging_page_id = logging_page_id;
     }
     public String getLogging_page_id() {
         return logging_page_id;
     }

    public void setShow_suggested_profiles(boolean show_suggested_profiles) {
         this.show_suggested_profiles = show_suggested_profiles;
     }
     public boolean getShow_suggested_profiles() {
         return show_suggested_profiles;
     }

    public void setGraphql(Graphql graphql) {
         this.graphql = graphql;
     }
     public Graphql getGraphql() {
         return graphql;
     }

}