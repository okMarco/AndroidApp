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
public class Edge_saved_media {

    private int count;
    private Page_info page_info;
    private List<String> edges;
    public void setCount(int count) {
         this.count = count;
     }
     public int getCount() {
         return count;
     }

    public void setPage_info(Page_info page_info) {
         this.page_info = page_info;
     }
     public Page_info getPage_info() {
         return page_info;
     }

    public void setEdges(List<String> edges) {
         this.edges = edges;
     }
     public List<String> getEdges() {
         return edges;
     }

}