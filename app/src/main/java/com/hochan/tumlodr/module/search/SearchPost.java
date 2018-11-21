package com.hochan.tumlodr.module.search;

import com.google.gson.annotations.SerializedName;

/**
 * .
 * Created by hochan on 2018/6/23.
 */

public class SearchPost {

	/**
	 * id : 175080336219
	 * type : photo
	 * root_id : 175080336219
	 * tumblelog : mysharona1987
	 * tumblelog-key : pxSUYjqHe
	 * tumblelog-data : {"avatar_url":"https://78.media.tumblr.com/avatar_34d80906bc46_128.pnj","dashboard_url":"/blog/mysharona1987","url":"http://mysharona1987.tumblr.com","name":"mysharona1987","cname":"","description":"","description_sanitized":"","title":"Pretty things","likes":true,"share_following":false,"is_blogless_advertiser":false,"is_private":false,"is_group":false,"customizable":false,"following":false,"premium_partner":false,"can_receive_messages":true,"can_send_messages":true,"uuid":"mysharona1987.tumblr.com","can_be_followed":true,"has_default_header":true,"can_pixelate_avatar":false}
	 * tumblelog-parent-data : false
	 * tumblelog-root-data : false
	 * reblog_key : pHUSuY55
	 * is_reblog : false
	 * is_mine : false
	 * liked : false
	 * sponsored :
	 * premium_tracked : null
	 * is_recommended : false
	 * placement_id : null
	 * reblog_source : POST_CONTEXT_UNKOWN
	 * share_popover_data : {"tumblelog_name":"mysharona1987","embed_key":"0VxL1YKJJn2M0ztnu2gdIQ","embed_did":"da39a3ee5e6b4b0d3255bfef95601890afd80709","post_id":"175080336219","root_id":"175080336219","post_url":"http://mysharona1987.tumblr.com/post/175080336219","post_tiny_url":"https://tmblr.co/ZYVYWq2Z3cUjR","is_private":0,"has_user":false,"has_facebook":false,"twitter_username":"","permalink_label":"Permalink","show_reporting_links":false,"abuse_url":"https://www.tumblr.com/abuse","show_pinterest":true,"pinterest_share_window":{"url":"//www.pinterest.com/pin/create/button/?url=http://mysharona1987.tumblr.com/post/175080336219&description=&media=https%3A%2F%2F78.media.tumblr.com%2F56686fa1f44b30970008cf1beae11beb%2Ftumblr_pamu0qaZ9Y1shiv3ro1_500.png","name":"pinterest-share-dialog-175080336219","dimensions":"scrollbars=1,width=750,height=316"},"show_reddit":true,"show_flagging":true}
	 * recommendation_reason : null
	 * owner_appeal_nsfw : false
	 * post-id : 175080336219
	 * tumblelog-name : mysharona1987
	 * reblog-key : pHUSuY55
	 * direct-video :
	 * is-animated : false
	 * serve-id : b9441e78ade2cfd5a078f58518b7cec6
	 * is-pinned : false
	 * can_reply : false
	 * pt : O79s1Y4SsLlZS4nmsOoXIQZDqdnWH3jodLkxbtSMto05K9FxlscVHsxDDC227RfN5d6ye0esVazfhO6ut6LL2Kny4n/1u5MpWkKJLR4HqRrlC9p2bDGTeS6nUNwZwAascgSJ6vi3aiI50I3x6s6E3wz4hoVhohetqC3XNqj9dD9BluQC6PK+bPnH9AWjSrYdbHlHk+oOUBCpatc/wDKna/6dtoBpt2U1RPOYzcHhxKCEmB19MXVngNauTelG7ytswghvcFeFuHO2kMmBRnXPAhqb5GZOE2hgdBx6go17VMSxNhAkJP4afTD7YGoYmyXZaGQHLmSyA54mhH/OGGkoy061naEBN/irwiu+CnS7MFWBXuwlg6UKaOeXmh6JtXyLsjcfPh68MmMQ5vVKctO6JUxzXcS9Djc1p/HEf0/G/Qx41EPpbo0GMSLrmvvVaa0lfosrKOz332SpQSqT+RDZq6PDCuGnSWK7dawVLin1agu93Sg1satk4RdUqHmwUkxJb6bM2tRKpPOcyY673EstPDZv9n2L1Sr5JOnanvhLEJDYtdJXIC5TC5ue+bvaeOq/oCb8dNLK5SfsA9BD4uH2uNmyWeeZvxhfeIzzuO1AsUkH1DjnfeZnNPvbBFoweopKBHlCi8N5iMRr8KDO+rKwDwIxxMWfEIQMRqGw1K1+zg/gP5sVT5yin/iP61WO1hYBu2gCpeNXFyIIGIGwNoaZ2eSzDUH2lTLphdMo3jjYI6VfO5PsvhfQT0LpYLmi9mKkaCJXx4kXBFALrRJ575YxCw==
	 * log-index : 2
	 */

	private String id;
	private String type;
	private String root_id;
	private String tumblelog;
	@SerializedName("tumblelog-key")
	private String tumblelogkey;
	@SerializedName("tumblelog-data")
	private TumblelogdataBean tumblelogdata;
	@SerializedName("tumblelog-parent-data")
	private boolean tumblelogparentdata;
	@SerializedName("tumblelog-root-data")
	private boolean tumblelogrootdata;
	private String reblog_key;
	private boolean is_reblog;
	private boolean is_mine;
	private boolean liked;
	private String sponsored;
	private Object premium_tracked;
	private boolean is_recommended;
	private Object placement_id;
	private String reblog_source;
	private SharePopoverDataBean share_popover_data;
	private Object recommendation_reason;
	private boolean owner_appeal_nsfw;
	@SerializedName("post-id")
	private String postid;
	@SerializedName("tumblelog-name")
	private String tumblelogname;
	@SerializedName("reblog-key")
	private String reblogkey;
	@SerializedName("direct-video")
	private String directvideo;
	@SerializedName("is-animated")
	private boolean isanimated;
	@SerializedName("serve-id")
	private String serveid;
	@SerializedName("is-pinned")
	private boolean ispinned;
	private boolean can_reply;
	private String pt;
	@SerializedName("log-index")
	private String logindex;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRoot_id() {
		return root_id;
	}

	public void setRoot_id(String root_id) {
		this.root_id = root_id;
	}

	public String getTumblelog() {
		return tumblelog;
	}

	public void setTumblelog(String tumblelog) {
		this.tumblelog = tumblelog;
	}

	public String getTumblelogkey() {
		return tumblelogkey;
	}

	public void setTumblelogkey(String tumblelogkey) {
		this.tumblelogkey = tumblelogkey;
	}

	public TumblelogdataBean getTumblelogdata() {
		return tumblelogdata;
	}

	public void setTumblelogdata(TumblelogdataBean tumblelogdata) {
		this.tumblelogdata = tumblelogdata;
	}

	public boolean isTumblelogparentdata() {
		return tumblelogparentdata;
	}

	public void setTumblelogparentdata(boolean tumblelogparentdata) {
		this.tumblelogparentdata = tumblelogparentdata;
	}

	public boolean isTumblelogrootdata() {
		return tumblelogrootdata;
	}

	public void setTumblelogrootdata(boolean tumblelogrootdata) {
		this.tumblelogrootdata = tumblelogrootdata;
	}

	public String getReblog_key() {
		return reblog_key;
	}

	public void setReblog_key(String reblog_key) {
		this.reblog_key = reblog_key;
	}

	public boolean isIs_reblog() {
		return is_reblog;
	}

	public void setIs_reblog(boolean is_reblog) {
		this.is_reblog = is_reblog;
	}

	public boolean isIs_mine() {
		return is_mine;
	}

	public void setIs_mine(boolean is_mine) {
		this.is_mine = is_mine;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	public String getSponsored() {
		return sponsored;
	}

	public void setSponsored(String sponsored) {
		this.sponsored = sponsored;
	}

	public Object getPremium_tracked() {
		return premium_tracked;
	}

	public void setPremium_tracked(Object premium_tracked) {
		this.premium_tracked = premium_tracked;
	}

	public boolean isIs_recommended() {
		return is_recommended;
	}

	public void setIs_recommended(boolean is_recommended) {
		this.is_recommended = is_recommended;
	}

	public Object getPlacement_id() {
		return placement_id;
	}

	public void setPlacement_id(Object placement_id) {
		this.placement_id = placement_id;
	}

	public String getReblog_source() {
		return reblog_source;
	}

	public void setReblog_source(String reblog_source) {
		this.reblog_source = reblog_source;
	}

	public SharePopoverDataBean getShare_popover_data() {
		return share_popover_data;
	}

	public void setShare_popover_data(SharePopoverDataBean share_popover_data) {
		this.share_popover_data = share_popover_data;
	}

	public Object getRecommendation_reason() {
		return recommendation_reason;
	}

	public void setRecommendation_reason(Object recommendation_reason) {
		this.recommendation_reason = recommendation_reason;
	}

	public boolean isOwner_appeal_nsfw() {
		return owner_appeal_nsfw;
	}

	public void setOwner_appeal_nsfw(boolean owner_appeal_nsfw) {
		this.owner_appeal_nsfw = owner_appeal_nsfw;
	}

	public String getPostid() {
		return postid;
	}

	public void setPostid(String postid) {
		this.postid = postid;
	}

	public String getTumblelogname() {
		return tumblelogname;
	}

	public void setTumblelogname(String tumblelogname) {
		this.tumblelogname = tumblelogname;
	}

	public String getReblogkey() {
		return reblogkey;
	}

	public void setReblogkey(String reblogkey) {
		this.reblogkey = reblogkey;
	}

	public String getDirectvideo() {
		return directvideo;
	}

	public void setDirectvideo(String directvideo) {
		this.directvideo = directvideo;
	}

	public boolean isIsanimated() {
		return isanimated;
	}

	public void setIsanimated(boolean isanimated) {
		this.isanimated = isanimated;
	}

	public String getServeid() {
		return serveid;
	}

	public void setServeid(String serveid) {
		this.serveid = serveid;
	}

	public boolean isIspinned() {
		return ispinned;
	}

	public void setIspinned(boolean ispinned) {
		this.ispinned = ispinned;
	}

	public boolean isCan_reply() {
		return can_reply;
	}

	public void setCan_reply(boolean can_reply) {
		this.can_reply = can_reply;
	}

	public String getPt() {
		return pt;
	}

	public void setPt(String pt) {
		this.pt = pt;
	}

	public String getLogindex() {
		return logindex;
	}

	public void setLogindex(String logindex) {
		this.logindex = logindex;
	}

	public static class TumblelogdataBean {
		/**
		 * avatar_url : https://78.media.tumblr.com/avatar_34d80906bc46_128.pnj
		 * dashboard_url : /blog/mysharona1987
		 * url : http://mysharona1987.tumblr.com
		 * name : mysharona1987
		 * cname :
		 * description :
		 * description_sanitized :
		 * title : Pretty things
		 * likes : true
		 * share_following : false
		 * is_blogless_advertiser : false
		 * is_private : false
		 * is_group : false
		 * customizable : false
		 * following : false
		 * premium_partner : false
		 * can_receive_messages : true
		 * can_send_messages : true
		 * uuid : mysharona1987.tumblr.com
		 * can_be_followed : true
		 * has_default_header : true
		 * can_pixelate_avatar : false
		 */

		private String avatar_url;
		private String dashboard_url;
		private String url;
		private String name;
		private String cname;
		private String description;
		private String description_sanitized;
		private String title;
		private boolean likes;
		private boolean share_following;
		private boolean is_blogless_advertiser;
		private boolean is_private;
		private boolean is_group;
		private boolean customizable;
		private boolean following;
		private boolean premium_partner;
		private boolean can_receive_messages;
		private boolean can_send_messages;
		private String uuid;
		private boolean can_be_followed;
		private boolean has_default_header;
		private boolean can_pixelate_avatar;

		public String getAvatar_url() {
			return avatar_url;
		}

		public void setAvatar_url(String avatar_url) {
			this.avatar_url = avatar_url;
		}

		public String getDashboard_url() {
			return dashboard_url;
		}

		public void setDashboard_url(String dashboard_url) {
			this.dashboard_url = dashboard_url;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCname() {
			return cname;
		}

		public void setCname(String cname) {
			this.cname = cname;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getDescription_sanitized() {
			return description_sanitized;
		}

		public void setDescription_sanitized(String description_sanitized) {
			this.description_sanitized = description_sanitized;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public boolean isLikes() {
			return likes;
		}

		public void setLikes(boolean likes) {
			this.likes = likes;
		}

		public boolean isShare_following() {
			return share_following;
		}

		public void setShare_following(boolean share_following) {
			this.share_following = share_following;
		}

		public boolean isIs_blogless_advertiser() {
			return is_blogless_advertiser;
		}

		public void setIs_blogless_advertiser(boolean is_blogless_advertiser) {
			this.is_blogless_advertiser = is_blogless_advertiser;
		}

		public boolean isIs_private() {
			return is_private;
		}

		public void setIs_private(boolean is_private) {
			this.is_private = is_private;
		}

		public boolean isIs_group() {
			return is_group;
		}

		public void setIs_group(boolean is_group) {
			this.is_group = is_group;
		}

		public boolean isCustomizable() {
			return customizable;
		}

		public void setCustomizable(boolean customizable) {
			this.customizable = customizable;
		}

		public boolean isFollowing() {
			return following;
		}

		public void setFollowing(boolean following) {
			this.following = following;
		}

		public boolean isPremium_partner() {
			return premium_partner;
		}

		public void setPremium_partner(boolean premium_partner) {
			this.premium_partner = premium_partner;
		}

		public boolean isCan_receive_messages() {
			return can_receive_messages;
		}

		public void setCan_receive_messages(boolean can_receive_messages) {
			this.can_receive_messages = can_receive_messages;
		}

		public boolean isCan_send_messages() {
			return can_send_messages;
		}

		public void setCan_send_messages(boolean can_send_messages) {
			this.can_send_messages = can_send_messages;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public boolean isCan_be_followed() {
			return can_be_followed;
		}

		public void setCan_be_followed(boolean can_be_followed) {
			this.can_be_followed = can_be_followed;
		}

		public boolean isHas_default_header() {
			return has_default_header;
		}

		public void setHas_default_header(boolean has_default_header) {
			this.has_default_header = has_default_header;
		}

		public boolean isCan_pixelate_avatar() {
			return can_pixelate_avatar;
		}

		public void setCan_pixelate_avatar(boolean can_pixelate_avatar) {
			this.can_pixelate_avatar = can_pixelate_avatar;
		}
	}

	public static class SharePopoverDataBean {
		/**
		 * tumblelog_name : mysharona1987
		 * embed_key : 0VxL1YKJJn2M0ztnu2gdIQ
		 * embed_did : da39a3ee5e6b4b0d3255bfef95601890afd80709
		 * post_id : 175080336219
		 * root_id : 175080336219
		 * post_url : http://mysharona1987.tumblr.com/post/175080336219
		 * post_tiny_url : https://tmblr.co/ZYVYWq2Z3cUjR
		 * is_private : 0
		 * has_user : false
		 * has_facebook : false
		 * twitter_username :
		 * permalink_label : Permalink
		 * show_reporting_links : false
		 * abuse_url : https://www.tumblr.com/abuse
		 * show_pinterest : true
		 * pinterest_share_window : {"url":"//www.pinterest.com/pin/create/button/?url=http://mysharona1987.tumblr.com/post/175080336219&description=&media=https%3A%2F%2F78.media.tumblr.com%2F56686fa1f44b30970008cf1beae11beb%2Ftumblr_pamu0qaZ9Y1shiv3ro1_500.png","name":"pinterest-share-dialog-175080336219","dimensions":"scrollbars=1,width=750,height=316"}
		 * show_reddit : true
		 * show_flagging : true
		 */

		private String tumblelog_name;
		private String embed_key;
		private String embed_did;
		private String post_id;
		private String root_id;
		private String post_url;
		private String post_tiny_url;
		private int is_private;
		private boolean has_user;
		private boolean has_facebook;
		private String twitter_username;
		private String permalink_label;
		private boolean show_reporting_links;
		private String abuse_url;
		private boolean show_pinterest;
		private PinterestShareWindowBean pinterest_share_window;
		private boolean show_reddit;
		private boolean show_flagging;

		public String getTumblelog_name() {
			return tumblelog_name;
		}

		public void setTumblelog_name(String tumblelog_name) {
			this.tumblelog_name = tumblelog_name;
		}

		public String getEmbed_key() {
			return embed_key;
		}

		public void setEmbed_key(String embed_key) {
			this.embed_key = embed_key;
		}

		public String getEmbed_did() {
			return embed_did;
		}

		public void setEmbed_did(String embed_did) {
			this.embed_did = embed_did;
		}

		public String getPost_id() {
			return post_id;
		}

		public void setPost_id(String post_id) {
			this.post_id = post_id;
		}

		public String getRoot_id() {
			return root_id;
		}

		public void setRoot_id(String root_id) {
			this.root_id = root_id;
		}

		public String getPost_url() {
			return post_url;
		}

		public void setPost_url(String post_url) {
			this.post_url = post_url;
		}

		public String getPost_tiny_url() {
			return post_tiny_url;
		}

		public void setPost_tiny_url(String post_tiny_url) {
			this.post_tiny_url = post_tiny_url;
		}

		public int getIs_private() {
			return is_private;
		}

		public void setIs_private(int is_private) {
			this.is_private = is_private;
		}

		public boolean isHas_user() {
			return has_user;
		}

		public void setHas_user(boolean has_user) {
			this.has_user = has_user;
		}

		public boolean isHas_facebook() {
			return has_facebook;
		}

		public void setHas_facebook(boolean has_facebook) {
			this.has_facebook = has_facebook;
		}

		public String getTwitter_username() {
			return twitter_username;
		}

		public void setTwitter_username(String twitter_username) {
			this.twitter_username = twitter_username;
		}

		public String getPermalink_label() {
			return permalink_label;
		}

		public void setPermalink_label(String permalink_label) {
			this.permalink_label = permalink_label;
		}

		public boolean isShow_reporting_links() {
			return show_reporting_links;
		}

		public void setShow_reporting_links(boolean show_reporting_links) {
			this.show_reporting_links = show_reporting_links;
		}

		public String getAbuse_url() {
			return abuse_url;
		}

		public void setAbuse_url(String abuse_url) {
			this.abuse_url = abuse_url;
		}

		public boolean isShow_pinterest() {
			return show_pinterest;
		}

		public void setShow_pinterest(boolean show_pinterest) {
			this.show_pinterest = show_pinterest;
		}

		public PinterestShareWindowBean getPinterest_share_window() {
			return pinterest_share_window;
		}

		public void setPinterest_share_window(PinterestShareWindowBean pinterest_share_window) {
			this.pinterest_share_window = pinterest_share_window;
		}

		public boolean isShow_reddit() {
			return show_reddit;
		}

		public void setShow_reddit(boolean show_reddit) {
			this.show_reddit = show_reddit;
		}

		public boolean isShow_flagging() {
			return show_flagging;
		}

		public void setShow_flagging(boolean show_flagging) {
			this.show_flagging = show_flagging;
		}

		public static class PinterestShareWindowBean {
			/**
			 * url : //www.pinterest.com/pin/create/button/?url=http://mysharona1987.tumblr.com/post/175080336219&description=&media=https%3A%2F%2F78.media.tumblr.com%2F56686fa1f44b30970008cf1beae11beb%2Ftumblr_pamu0qaZ9Y1shiv3ro1_500.png
			 * name : pinterest-share-dialog-175080336219
			 * dimensions : scrollbars=1,width=750,height=316
			 */

			private String url;
			private String name;
			private String dimensions;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getDimensions() {
				return dimensions;
			}

			public void setDimensions(String dimensions) {
				this.dimensions = dimensions;
			}
		}
	}
}
