/**
  * Copyright 2018 bejson.com 
  */
package com.hochan.tumlodr.model.data.instagram.com.besjon.pojo;
import java.util.List;

/**
 * Auto-generated: 2018-04-05 9:39:19
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Node {

    private String __typename;
    private String id;
    private Edge_media_to_caption edge_media_to_caption;
    private String shortcode;
    private Edge_media_to_comment edge_media_to_comment;
    private boolean comments_disabled;
    private long taken_at_timestamp;
    private Dimensions dimensions;
    private String display_url;
    private Edge_liked_by edge_liked_by;
    private Edge_media_preview_like edge_media_preview_like;
    private String gating_info;
    private String media_preview;
    private Owner owner;
    private String thumbnail_src;
    private List<Thumbnail_resources> thumbnail_resources;
    private boolean is_video;
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

    public void setEdge_media_to_caption(Edge_media_to_caption edge_media_to_caption) {
         this.edge_media_to_caption = edge_media_to_caption;
     }
     public Edge_media_to_caption getEdge_media_to_caption() {
         return edge_media_to_caption;
     }

    public void setShortcode(String shortcode) {
         this.shortcode = shortcode;
     }
     public String getShortcode() {
         return shortcode;
     }

    public void setEdge_media_to_comment(Edge_media_to_comment edge_media_to_comment) {
         this.edge_media_to_comment = edge_media_to_comment;
     }
     public Edge_media_to_comment getEdge_media_to_comment() {
         return edge_media_to_comment;
     }

    public void setComments_disabled(boolean comments_disabled) {
         this.comments_disabled = comments_disabled;
     }
     public boolean getComments_disabled() {
         return comments_disabled;
     }

    public void setTaken_at_timestamp(long taken_at_timestamp) {
         this.taken_at_timestamp = taken_at_timestamp;
     }
     public long getTaken_at_timestamp() {
         return taken_at_timestamp;
     }

    public void setDimensions(Dimensions dimensions) {
         this.dimensions = dimensions;
     }
     public Dimensions getDimensions() {
         return dimensions;
     }

    public void setDisplay_url(String display_url) {
         this.display_url = display_url;
     }
     public String getDisplay_url() {
         return display_url;
     }

    public void setEdge_liked_by(Edge_liked_by edge_liked_by) {
         this.edge_liked_by = edge_liked_by;
     }
     public Edge_liked_by getEdge_liked_by() {
         return edge_liked_by;
     }

    public void setEdge_media_preview_like(Edge_media_preview_like edge_media_preview_like) {
         this.edge_media_preview_like = edge_media_preview_like;
     }
     public Edge_media_preview_like getEdge_media_preview_like() {
         return edge_media_preview_like;
     }

    public void setGating_info(String gating_info) {
         this.gating_info = gating_info;
     }
     public String getGating_info() {
         return gating_info;
     }

    public void setMedia_preview(String media_preview) {
         this.media_preview = media_preview;
     }
     public String getMedia_preview() {
         return media_preview;
     }

    public void setOwner(Owner owner) {
         this.owner = owner;
     }
     public Owner getOwner() {
         return owner;
     }

    public void setThumbnail_src(String thumbnail_src) {
         this.thumbnail_src = thumbnail_src;
     }
     public String getThumbnail_src() {
         return thumbnail_src;
     }

    public void setThumbnail_resources(List<Thumbnail_resources> thumbnail_resources) {
         this.thumbnail_resources = thumbnail_resources;
     }
     public List<Thumbnail_resources> getThumbnail_resources() {
         return thumbnail_resources;
     }

    public void setIs_video(boolean is_video) {
         this.is_video = is_video;
     }
     public boolean getIs_video() {
         return is_video;
     }

}