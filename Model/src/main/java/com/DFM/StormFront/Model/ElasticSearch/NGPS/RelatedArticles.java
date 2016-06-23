package com.DFM.StormFront.Model.ElasticSearch.NGPS;

import java.util.List;

/**
 * Created by Mick on 5/3/2016.
 */
public class RelatedArticles
{
    public List<RelatedArticle> relatedArticle;

    @Override
    public String toString()
    {
        return "RelatedArticles [relatedArticle = "+relatedArticle+"]";
    }
}
