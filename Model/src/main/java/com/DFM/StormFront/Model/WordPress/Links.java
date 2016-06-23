package com.DFM.StormFront.Model.WordPress;

import java.util.ArrayList;

public class Links
{
	private ArrayList<Self> self;
	public final ArrayList<Self> getself()
	{
		return self;
	}
	public final void setself(ArrayList<Self> value)
	{
		self = value;
	}
	private ArrayList<com.DFM.StormFront.Model.WordPress.Collection> collection;
	public final ArrayList<com.DFM.StormFront.Model.WordPress.Collection> getcollection()
	{
		return collection;
	}
	public final void setcollection(ArrayList<com.DFM.StormFront.Model.WordPress.Collection> value)
	{
		collection = value;
	}
	private ArrayList<About> about;
	public final ArrayList<About> getabout()
	{
		return about;
	}
	public final void setabout(ArrayList<About> value)
	{
		about = value;
	}
	private ArrayList<Author> author;
	public final ArrayList<Author> getauthor()
	{
		return author;
	}
	public final void setauthor(ArrayList<Author> value)
	{
		author = value;
	}
	private ArrayList<Reply> replies;
	public final ArrayList<Reply> getreplies()
	{
		return replies;
	}
	public final void setreplies(ArrayList<Reply> value)
	{
		replies = value;
	}
	private ArrayList<VersionHistory> versionhistory;
	public final ArrayList<VersionHistory> getversionhistory()
	{
		return versionhistory;
	}
	public final void setversionhistory(ArrayList<VersionHistory> value)
	{
		versionhistory = value;
	}
	//public List<HttpsApiWOrgAttachment> HttpsApiWOrgAttachment { get; set; }
	//public List<HttpsApiWOrgTerm> HttpsApiWOrgTerm { get; set; }
	//public List<HttpsApiWOrgMeta> HttpsApiWOrgMeta { get; set; }
	//public List<HttpsApiWOrgFeaturedmedia> HttpsApiWOrgFeaturedmedia { get; set; }
}