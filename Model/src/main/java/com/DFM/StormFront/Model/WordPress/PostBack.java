package com.DFM.StormFront.Model.WordPress;

import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class PostBack
{
	public PostBack()
	{
	}

	public PostBack fromJSON(String source, String sourceType) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		switch (sourceType) {
			case "file":
				return mapper.readValue(new File(source), this.getClass());
			case "url":
				return mapper.readValue(new URL(source), this.getClass());
			case "string":
				return mapper.readValue(source, this.getClass());
			default:
				throw new Exception("Unknown sourceType converting from JSON: " + sourceType);
		}
	}

	public final String toJSON()
	{
		return (JsonUtil.toJSON(this));
	}

	public String toXml() throws Exception {
		Document doc = XmlUtil.deserialize(this);
		return XmlUtil.toString(doc);
	}

/*	public static ArrayList<PostBack> ListFromJson(String json)
	{
		ArrayList<PostBack> tagList = new ArrayList<PostBack>();
		JArray jarray = JArray.Parse(json);

		for (JObject jobject : jarray.<JObject>Children())
		{
			String jsonElement = JsonConvert.SerializeObject(jobject);
			tagList.add(JsonConvert.<Tag>DeserializeObject(jsonElement));
		}
		return tagList;
	}*/


	private String id;
	public final String getid()
	{
		return id;
	}
	public final void setid(String value)
	{
		id = value;
	}
	private String date;
	public final String getdate()
	{
		return date;
	}
	public final void setdate(String value)
	{
		date = value;
	}
	private String date_gmt;
	public final String getdate_gmt()
	{
		return date_gmt;
	}
	public final void setdate_gmt(String value)
	{
		date_gmt = value;
	}
	private Guid guid = new Guid();
	public final Guid getguid()
	{
		return guid;
	}
	public final void setguid(Guid value)
	{
		guid = value;
	}
	private String modified;
	public final String getmodified()
	{
		return modified;
	}
	public final void setmodified(String value)
	{
		modified = value;
	}
	private String modified_gmt;
	public final String getmodified_gmt()
	{
		return modified_gmt;
	}
	public final void setmodified_gmt(String value)
	{
		modified_gmt = value;
	}
	private String slug;
	public final String getslug()
	{
		return slug;
	}
	public final void setslug(String value)
	{
		slug = value;
	}
	private String type;
	public final String gettype()
	{
		return type;
	}
	public final void settype(String value)
	{
		type = value;
	}
	private String link;
	public final String getlink()
	{
		return link;
	}
	public final void setlink(String value)
	{
		link = value;
	}
	private Title title;
	public final Title gettitle()
	{
		return title;
	}
	public final void settitle(Title value)
	{
		title = value;
	}
	private Content content;
	public final Content getcontent()
	{
		return content;
	}
	public final void setcontent(Content value)
	{
		content = value;
	}
	private Excerpt excerpt;
	public final Excerpt getexcerpt()
	{
		return excerpt;
	}
	public final void setexcerpt(Excerpt value)
	{
		excerpt = value;
	}
	private int author;
	public final int getauthor()
	{
		return author;
	}
	public final void setauthor(int value)
	{
		author = value;
	}
	private int featured_media;
	public final int getfeatured_media()
	{
		return featured_media;
	}
	public final void setfeatured_media(int value)
	{
		featured_media = value;
	}
	private String comment_status;
	public final String getcomment_status()
	{
		return comment_status;
	}
	public final void setcomment_status(String value)
	{
		comment_status = value;
	}
	private String ping_status;
	public final String getping_status()
	{
		return ping_status;
	}
	public final void setping_status(String value)
	{
		ping_status = value;
	}
	private String sticky;
	public final String getsticky()
	{
		return sticky;
	}
	public final void setsticky(String value)
	{
		sticky = value;
	}
	private String format;
	public final String getformat()
	{
		return format;
	}
	public final void setformat(String value)
	{
		format = value;
	}
	private ArrayList<Integer> categories;
	public final ArrayList<Integer> getcategories()
	{
		return categories;
	}
	public final void setcategories(ArrayList<Integer> value)
	{
		categories = value;
	}
	private ArrayList<Integer> tags;
	public final ArrayList<Integer> gettags()
	{
		return tags;
	}
	public final void settags(ArrayList<Integer> value)
	{
		tags = value;
	}
	private Links _links;
	public final Links get_links()
	{
		return _links;
	}
	public final void set_links(Links value)
	{
		_links = value;
	}
}