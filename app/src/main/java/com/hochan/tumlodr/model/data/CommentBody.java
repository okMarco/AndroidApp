package com.hochan.tumlodr.model.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by hochan on 2017/9/10.
 */

public class CommentBody {

	private static final String TAG = "CommentBody";

	private String mBlog;
	private String mContent;
	private String mBlogUrl;

	public String getBlog() {
		return mBlog;
	}

	public void setmBlog(String mBlog) {
		this.mBlog = mBlog;
	}

	public String getContent() {
		return mContent;
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

	public String getmBlogUrl() {
		return mBlogUrl;
	}

	public void setmBlogUrl(String mBlogUrl) {
		this.mBlogUrl = mBlogUrl;
	}

	public static List<CommentBody> parseHtml(String html){
		List<CommentBody> commentBodyList = new ArrayList<>();
		Document doc = Jsoup.parse(html);
		Elements blockquotes = doc.select("blockquote");
		if (!blockquotes.isEmpty()) {
			for (Element blockquote : blockquotes) {
				CommentBody commentBody = findBlog(blockquote);
				commentBodyList.add(0, commentBody);
			}
		}else {
			Elements ps = doc.select("p");
			if (!ps.isEmpty()){
				CommentBody commentBody = new CommentBody();
				commentBody.setmContent(ps.get(0).html());
				commentBodyList.add(commentBody);
			}else {
				Elements as = doc.select("a");
				if (!as.isEmpty() && as.get(0).attr("class").equals("tumblr_blog")){
					CommentBody commentBody = new CommentBody();
					commentBody.setmBlog(as.get(0).html());
					commentBodyList.add(commentBody);
				}else {
					CommentBody commentBody = new CommentBody();
					commentBody.setmContent(html);
					commentBodyList.add(commentBody);
				}
			}
		}
		return commentBodyList;
	}

	public static CommentBody findBlog(Element blockquote){
		CommentBody commentBody = new CommentBody();
		Element parent = blockquote.parent();
		Elements brothers = parent.children();
		for (Element brother : brothers){
			if (brother.nodeName().equals("p")){
				Elements broChildren = brother.children();
				for (Element broChild : broChildren){
					if (broChild.nodeName().equals("a") && broChild.className().equals("tumblr_blog")){
						commentBody.setmBlog(broChild.ownText());
						commentBody.setmBlogUrl(broChild.attr("href"));
					}
				}
			}
		}
		Elements children = blockquote.children();
		Element lastChild = children.last();
		if (lastChild != null) {
			if (lastChild.nodeName().equals("p")) {
				commentBody.setmContent(lastChild.html());
			}
		}
		return commentBody;
	}
}
