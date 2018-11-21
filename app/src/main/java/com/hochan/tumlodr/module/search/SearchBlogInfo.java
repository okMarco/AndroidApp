package com.hochan.tumlodr.module.search;

/**
 * .
 * Created by hochan on 2018/6/23.
 */

public class SearchBlogInfo {

	/**
	 * avatar_url : https://78.media.tumblr.com/avatar_88eff7ce7c46_128.gif
	 * dashboard_url : /blog/lol
	 * url : http://lol.tumblr.com
	 * name : lol
	 * cname :
	 * description : You know,
	 it's just me.
	 * description_sanitized : You know,
	 it's just me.
	 * title : lol's soup
	 * likes : false
	 * share_following : false
	 * is_blogless_advertiser : false
	 * is_private : false
	 * is_group : false
	 * customizable : false
	 * following : false
	 * premium_partner : false
	 * can_receive_messages : true
	 * can_send_messages : true
	 * uuid : lol.tumblr.com
	 * can_be_followed : true
	 * has_default_header : true
	 * can_pixelate_avatar : false
	 * show_navigation : true
	 * global_theme_params : {"avatar_shape":"square","background_color":"#FAFAFA","body_font":"Helvetica Neue","header_image":"https://assets.tumblr.com/images/default_header/optica_pattern_02.png?_v=b976ee00195b1b7806c94ae285ca46a7","header_image_focused":"https://assets.tumblr.com/images/default_header/optica_pattern_02_640.png?_v=b976ee00195b1b7806c94ae285ca46a7","header_image_scaled":"http://assets.tumblr.com/images/default_header/optica_pattern_02.png?_v=b976ee00195b1b7806c94ae285ca46a7","header_stretch":true,"link_color":"#529ECC","show_avatar":true,"show_description":true,"show_header_image":true,"show_title":true,"title_color":"#444444","title_font":"Gibson","title_font_weight":"bold"}
	 * pt : cwQ9sHhbBelISdFfJt7iwGyAckTxYh3TohB8zFUjMJJnxElem84DL9yp9uSTYo2wSkEjDWhphKZxvrbn8DKzCf64cFBSA46f91/sJh3p+huYMIeJTSsfDwD/hiAX1aU6Jj0ek2Qax9b+E5Jp44LP6QVG4oAIqj2KzgYK7VKFwq2IGmDR913gshIzRWFrJyL0NSiOkL4zZ4ho4sUmC/I4owan1KQ7kuGThlopQOhnKzUbpNnr55RbPwE5p9T6QlKK+siQb6WMG7u9Z9Ja5D8Vt812fbI0/l72/srepMPqH5imDnzr0SV3nFCX7E/ASt3BYLdfMxTcspFbzBA7RcE7s3h0kq8NC6Tl0CogvjO7MU5neRgQhByVro77gyOiZPaBlrDbiM3kk1CbKI1Ri35avRacopOeOqYFygGP8g5mbYc=
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
	private boolean show_navigation;
	private GlobalThemeParamsBean global_theme_params;
	private String pt;

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

	public boolean isShow_navigation() {
		return show_navigation;
	}

	public void setShow_navigation(boolean show_navigation) {
		this.show_navigation = show_navigation;
	}

	public GlobalThemeParamsBean getGlobal_theme_params() {
		return global_theme_params;
	}

	public void setGlobal_theme_params(GlobalThemeParamsBean global_theme_params) {
		this.global_theme_params = global_theme_params;
	}

	public String getPt() {
		return pt;
	}

	public void setPt(String pt) {
		this.pt = pt;
	}

	public static class GlobalThemeParamsBean {
		/**
		 * avatar_shape : square
		 * background_color : #FAFAFA
		 * body_font : Helvetica Neue
		 * header_image : https://assets.tumblr.com/images/default_header/optica_pattern_02.png?_v=b976ee00195b1b7806c94ae285ca46a7
		 * header_image_focused : https://assets.tumblr.com/images/default_header/optica_pattern_02_640.png?_v=b976ee00195b1b7806c94ae285ca46a7
		 * header_image_scaled : http://assets.tumblr.com/images/default_header/optica_pattern_02.png?_v=b976ee00195b1b7806c94ae285ca46a7
		 * header_stretch : true
		 * link_color : #529ECC
		 * show_avatar : true
		 * show_description : true
		 * show_header_image : true
		 * show_title : true
		 * title_color : #444444
		 * title_font : Gibson
		 * title_font_weight : bold
		 */

		private String avatar_shape;
		private String background_color;
		private String body_font;
		private String header_image;
		private String header_image_focused;
		private String header_image_scaled;
		private boolean header_stretch;
		private String link_color;
		private boolean show_avatar;
		private boolean show_description;
		private boolean show_header_image;
		private boolean show_title;
		private String title_color;
		private String title_font;
		private String title_font_weight;

		public String getAvatar_shape() {
			return avatar_shape;
		}

		public void setAvatar_shape(String avatar_shape) {
			this.avatar_shape = avatar_shape;
		}

		public String getBackground_color() {
			return background_color;
		}

		public void setBackground_color(String background_color) {
			this.background_color = background_color;
		}

		public String getBody_font() {
			return body_font;
		}

		public void setBody_font(String body_font) {
			this.body_font = body_font;
		}

		public String getHeader_image() {
			return header_image;
		}

		public void setHeader_image(String header_image) {
			this.header_image = header_image;
		}

		public String getHeader_image_focused() {
			return header_image_focused;
		}

		public void setHeader_image_focused(String header_image_focused) {
			this.header_image_focused = header_image_focused;
		}

		public String getHeader_image_scaled() {
			return header_image_scaled;
		}

		public void setHeader_image_scaled(String header_image_scaled) {
			this.header_image_scaled = header_image_scaled;
		}

		public boolean isHeader_stretch() {
			return header_stretch;
		}

		public void setHeader_stretch(boolean header_stretch) {
			this.header_stretch = header_stretch;
		}

		public String getLink_color() {
			return link_color;
		}

		public void setLink_color(String link_color) {
			this.link_color = link_color;
		}

		public boolean isShow_avatar() {
			return show_avatar;
		}

		public void setShow_avatar(boolean show_avatar) {
			this.show_avatar = show_avatar;
		}

		public boolean isShow_description() {
			return show_description;
		}

		public void setShow_description(boolean show_description) {
			this.show_description = show_description;
		}

		public boolean isShow_header_image() {
			return show_header_image;
		}

		public void setShow_header_image(boolean show_header_image) {
			this.show_header_image = show_header_image;
		}

		public boolean isShow_title() {
			return show_title;
		}

		public void setShow_title(boolean show_title) {
			this.show_title = show_title;
		}

		public String getTitle_color() {
			return title_color;
		}

		public void setTitle_color(String title_color) {
			this.title_color = title_color;
		}

		public String getTitle_font() {
			return title_font;
		}

		public void setTitle_font(String title_font) {
			this.title_font = title_font;
		}

		public String getTitle_font_weight() {
			return title_font_weight;
		}

		public void setTitle_font_weight(String title_font_weight) {
			this.title_font_weight = title_font_weight;
		}
	}
}
