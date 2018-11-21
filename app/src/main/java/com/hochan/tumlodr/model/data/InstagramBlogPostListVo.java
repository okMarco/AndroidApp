package com.hochan.tumlodr.model.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * .
 * Created by hochan on 2018/2/10.
 */

public class InstagramBlogPostListVo {

	public static final String POST_TYPE_VIDEO = "GraphVideo";
	public static final String POST_TYPE_PHOTO = "GraphImage";
	public static final String POST_TYPE_SIDECAR = "GraphSidecar";

	public Data data;
	public String status;

	public static class Data{
		public User user;
	}

	public static class User{
		public EdgeOwnerToTimelineMedia edge_owner_to_timeline_media;
	}

	public static class EdgeOwnerToTimelineMedia {
		public int count;
		public PageInfo page_info;
		public List<Edge> edges;
	}

	public static class PageInfo{
		public boolean has_next_page;
		public String end_cursor;
	}

	public static class Edge{
		public Node node;
	}

	public static class Node{
		public String id;
		public String __typename;
		public String display_url;
		public String shortcode;
	}
}
