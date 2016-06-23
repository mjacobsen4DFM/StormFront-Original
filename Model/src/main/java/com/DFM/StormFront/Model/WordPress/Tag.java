package com.DFM.StormFront.Model.WordPress;

import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;

import java.io.File;
import java.net.URL;

public class Tag
{
	public Tag()
	{
	}

	public Tag(String json)
	{
		try {
			this.fromJSON(json, "string");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Tag fromJSON(String source, String sourceType) throws Exception {
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

/*	public static ArrayList<Tag> ListFromJson(String json)
	{
		ArrayList<Tag> tagList = new ArrayList<Tag>();
		JArray jarray = JArray.Parse(json);

		for (JObject jobject : jarray.<JObject>Children())
		{
			String jsonElement = JsonConvert.SerializeObject(jobject);
			tagList.add(JsonConvert.<Tag>DeserializeObject(jsonElement));
		}
		return tagList;
	}*/

	private int id;
	public final int getid()
	{
		return id;
	}
	public final void setid(int value)
	{
		id = value;
	}
	private int count;
	public final int getcount()
	{
		return count;
	}
	public final void setcount(int value)
	{
		count = value;
	}
	private String description;
	public final String getdescription()
	{
		return description;
	}
	public final void setdescription(String value)
	{
		description = value;
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
	private String name;
	public final String getname()
	{
		return name;
	}
	public final void setname(String value)
	{
		name = value;
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
	private String taxonomy;
	public final String gettaxonomy()
	{
		return taxonomy;
	}
	public final void settaxonomy(String value)
	{
		taxonomy = value;
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