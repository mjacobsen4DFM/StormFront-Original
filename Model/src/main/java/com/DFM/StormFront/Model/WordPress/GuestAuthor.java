package com.DFM.StormFront.Model.WordPress;

import com.DFM.StormFront.Util.JsonUtil;
import com.DFM.StormFront.Util.XmlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Document;

import java.io.File;
import java.net.URL;

public class GuestAuthor
{
	public GuestAuthor()
	{
	}

	public GuestAuthor fromJSON(String source, String sourceType) throws Exception {
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

/*	public static ArrayList<GuestAuthor> ListFromJson(String json)
	{
		ArrayList<GuestAuthor> tagList = new ArrayList<GuestAuthor>();
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
	private String display_name;
	public final String getdisplay_name()
	{
		return display_name;
	}
	public final void setdisplay_name(String value)
	{
		display_name = value;
	}
	private String first_name;
	public final String getfirst_name()
	{
		return first_name;
	}
	public final void setfirst_name(String value)
	{
		first_name = value;
	}
	private String last_name;
	public final String getlast_name()
	{
		return last_name;
	}
	public final void setlast_name(String value)
	{
		last_name = value;
	}
	private String user_login;
	public final String getuser_login()
	{
		return user_login;
	}
	public final void setuser_login(String value)
	{
		user_login = value;
	}
	private String user_email;
	public final String getuser_email()
	{
		return user_email;
	}
	public final void setuser_email(String value)
	{
		user_email = value;
	}
	private String linked_account;
	public final String getlinked_account()
	{
		return linked_account;
	}
	public final void setlinked_account(String value)
	{
		linked_account = value;
	}
	private String website;
	public final String getwebsite()
	{
		return website;
	}
	public final void setwebsite(String value)
	{
		website = value;
	}
	private String aim;
	public final String getaim()
	{
		return aim;
	}
	public final void setaim(String value)
	{
		aim = value;
	}
	private String yahooim;
	public final String getyahooim()
	{
		return yahooim;
	}
	public final void setyahooim(String value)
	{
		yahooim = value;
	}
	private String jabber;
	public final String getjabber()
	{
		return jabber;
	}
	public final void setjabber(String value)
	{
		jabber = value;
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