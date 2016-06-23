package com.DFM.StormFront.Model.WordPress;

import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;

import java.io.File;
import java.net.URL;

public class TagBack
{
	public TagBack()
	{
	}

	public TagBack fromJSON(String source, String sourceType) throws Exception {
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

/*	public static ArrayList<TagBack> ListFromJson(String json)
	{
		ArrayList<TagBack> tagList = new ArrayList<TagBack>();
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

	private String count;
	public final String getcount()
	{
		return count;
	}
	public final void setcount(String value)
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

	private String name;
	public final String getname()
	{
		return name;
	}
	public final void setname(String value)
	{
		name = value;
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

	private Links _links;
	public final Links get_links()
	{
		return _links;
	}
	public final void set_links(Links value)
	{
		_links = value;
	}

	private String parent;
	public final String getparent()
	{
		return parent;
	}
	public final void setparent(String value)
	{
		parent = value;
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
}