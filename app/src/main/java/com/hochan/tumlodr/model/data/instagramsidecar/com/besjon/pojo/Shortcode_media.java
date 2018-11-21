/**
  * Copyright 2018 bejson.com 
  */
package com.hochan.tumlodr.model.data.instagramsidecar.com.besjon.pojo;
import java.util.List;

/**
 * Auto-generated: 2018-03-04 16:52:28
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Shortcode_media {

    private String __typename;
    private String id;
    private String shortcode;
    private String display_url;
    private boolean is_video;
    private Edge_sidecar_to_children edge_sidecar_to_children;
    private String video_url;
    public void set__typename(String __typename) {
         this.__typename = __typename;
     }
     public String get__typename() {
         return __typename;
     }

    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

    public void setShortcode(String shortcode) {
         this.shortcode = shortcode;
     }
     public String getShortcode() {
         return shortcode;
     }

    public void setDisplay_url(String display_url) {
         this.display_url = display_url;
     }
     public String getDisplay_url() {
         return display_url;
     }


     public Edge_sidecar_to_children getEdge_sidecar_to_children() {
         return edge_sidecar_to_children;
     }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }
}