package com.DFM.StormFront.Exec;

import com.DFM.StormFront.Client.WebClient;
import com.DFM.StormFront.Model.Publisher;
import com.DFM.StormFront.Util.LogUtil;
import com.DFM.StormFront.Util.StringUtil;
import com.DFM.StormFront.Util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Mick on 4/21/2016.
 */
public class FeedLoadExec {
    public Publisher publisher;
    
    public FeedLoadExec(Publisher publisher){
        this.publisher = publisher;
    }
    
    public String Exec() throws Exception {
        //Read feed
        if(null == publisher.getUrl()) { return ""; }
        WebClient client = new WebClient(publisher);
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        String body = client.get();
        if ( ! XmlUtil.isWellFormed(body)){
            throw new Exception("XML was not wellformed from " + publisher.getUrl() + " XML: " + body);
        }
        InputSource xmlSource = StringUtil.toInputSource(body);
        Document doc = builder.parse(xmlSource);

        if (1 == 11) LogUtil.log("XML: " + XmlUtil.toString(doc));

        NodeList items = doc.getElementsByTagNameNS(publisher.getItemNamespace(), publisher.getItemElement());

        if (items.getLength() == 0) {
            items = doc.getElementsByTagName(publisher.getItemElement());
        }

        if (items.getLength() != 0) {
            if (1 == 11) LogUtil.log("found items");
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("root");
            doc.appendChild(root);
            if (publisher.getSortOrder().equals("reverse")) {
                for (int i = items.getLength() - 1; i >= 0; i--) {
                    if (1 == 11) LogUtil.log("item");
                    Node node = items.item(i);
                    Node copyNode = doc.importNode(node, true);
                    root.appendChild(copyNode);
                }
            } else {
                for (int i = 0; i < items.getLength(); i++) {
                    if (1 == 11) LogUtil.log("item");
                    Node node = items.item(i);
                    Node copyNode = doc.importNode(node, true);
                    root.appendChild(copyNode);
                }
            }
            return XmlUtil.toString(doc);
            //sStories= BasicUtil.removeUnicode(sStories);
        }

        return "";
    }
}
