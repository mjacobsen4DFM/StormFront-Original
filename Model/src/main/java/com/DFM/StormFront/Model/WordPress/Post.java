package com.DFM.StormFront.Model.WordPress;

import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class Post
{
	public Post()
	{
	}

	public Post fromJSON(String source, String sourceType) throws Exception {
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

/*	public static ArrayList<Post> ListFromJson(String json)
	{
		ArrayList<Post> tagList = new ArrayList<Post>();
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

	//public string date { get; set; }
	private String date_gmt;
	public final String getdate_gmt()
	{
		return date_gmt;
	}
	public final void setdate_gmt(String value)
	{
		date_gmt = value;
	}

	//public string modified { get; set; }
	private String modified_gmt;
	public final String getmodified_gmt()
	{
		return modified_gmt;
	}
	public final void setmodified_gmt(String value)
	{
		modified_gmt = value;
	}

	//public string slug { get; set; }
	private String type = "post";
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
	private String title;
	public final String gettitle()
	{
		return title;
	}
	public final void settitle(String value)
	{
		title = value;
	}
	private String status = "publish";
	public final String getstatus()
	{
		return status;
	}
	public final void setstatus(String value)
	{
		status = value;
	}
	private String content;
	public final String getcontent()
	{
		return content;
	}
	public final void setcontent(String value)
	{
		content = value;
	}
	private String excerpt;
	public final String getexcerpt()
	{
		return excerpt;
	}
	public final void setexcerpt(String value)
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
	private String comment_status = "closed";
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
	private boolean sticky = false;
	public final boolean getsticky()
	{
		return sticky;
	}
	public final void setsticky(boolean value)
	{
		sticky = value;
	}
	private String format = "standard";
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
}