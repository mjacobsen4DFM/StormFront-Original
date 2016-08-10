package com.DFM.StormFront.Util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("ThrowFromFinallyBlock")
public class XmlUtil {
    public static boolean isWellFormed(String xml) {
        try {
            XMLReader parser = XMLReaderFactory.createXMLReader();
            parser.setContentHandler(new DefaultHandler());
            InputSource source = new InputSource(new ByteArrayInputStream(xml.getBytes()));
            parser.parse(source);
            return true;
        } catch (SAXException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static Document fromString(String xml) throws Exception {
        return fromInputStream(new ByteArrayInputStream(xml.getBytes()));
    }

    public static String fromJson(String json) throws Exception {
        JSONObject jsonObject;
        JSONParser jsonParser = new JSONParser();

        Object obj = jsonParser.parse(json);

        jsonObject = (JSONObject) obj;
        String xml = org.json.XML.toString(jsonObject);
        return xml;
    }

    public static Document fromInputStream(InputStream inputStream) throws Exception {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        javax.xml.parsers.DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        InputSource inputSource;
        try {
            inputSource = new InputSource(inputStream);
            inputSource.setEncoding("UTF-8");
            doc = builder.parse(inputSource);
        } catch (Exception e1) {
            try {
                inputStream.reset();
                inputSource = new InputSource(inputStream);
                inputSource.setEncoding("ISO-8859-1");
                doc = builder.parse(inputSource);
            } catch (Exception e2) {
                try {
                    inputStream.reset();
                    inputSource = new InputSource(inputStream);
                    inputSource.setEncoding("Windows-1252");
                    doc = builder.parse(inputSource);
                } catch (Exception e3) {
                    throw new Exception(ExceptionUtil.getFullStackTrace(e3));
                } finally {
                    inputStream.close();
                }
            }
        }
        return doc;
    }

    public static Document deserialize(String sXML) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(sXML)));
    }

    public static Document deserialize(Object object) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(object, doc);
        return doc;
    }

    public static String toString(Node node) throws Exception {
        Document document = node.getOwnerDocument();
        DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        serializer.getDomConfig().setParameter("xml-declaration", false);
        return serializer.writeToString(node);
    }

    public static String toString(Document doc) throws Exception {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer;
            transformer = tf.newTransformer();
            // below code to remove XML declaration
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString();
        } catch (TransformerException te) {
            throw new Exception(ExceptionUtil.getFullStackTrace(te));
        }
    }

    public static String transform(String xmlString, String stylesheetPathname) throws Exception {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source stylesheetSource = new StreamSource(new File(stylesheetPathname).getAbsoluteFile());
            Transformer transformer = factory.newTransformer(stylesheetSource);
            Source inputSource = new StreamSource(new StringReader(xmlString));
            Writer outputWriter = new StringWriter();
            Result outputResult = new StreamResult(outputWriter);
            transformer.transform(inputSource, outputResult);
            return outputWriter.toString();
        } catch (TransformerException te) {
            throw new Exception(ExceptionUtil.getFullStackTrace(te));
        }
    }

    public static String getNodeValue(String xml, String nodeName) throws Exception {
        Document doc = fromString(xml);
        return getFirstChildNodeValue(doc, nodeName);
    }

    public static String getAttributeValue(String xml, String nodeName, String attributeName) throws Exception {
        Document doc = fromString(xml);
        return getFirstChildNodeAttributeValue(doc, nodeName, attributeName);
    }

    public static String getFirstChildNodeAttributeValue(Element parent, String nodeName) {
        return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
    }

    public static String getFirstChildNodeValue(Document parent, String nodeName) {
        return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
    }

    public static String getFirstChildNodeAttributeValue(Document parent, String nodeName, String attributeName) {
        return parent.getElementsByTagName(nodeName).item(0).getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public static String getFirstValueFromXPath(Element parent, String xpath) throws Exception {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            return (String) xPath.compile(xpath).evaluate(parent, XPathConstants.STRING);
        } catch (XPathExpressionException xpee) {
            throw new Exception(ExceptionUtil.getFullStackTrace(xpee));
        }
    }

    public static String getFirstValueFromXPath(Document parent, String xpath) throws Exception {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            return (String) xPath.compile(xpath).evaluate(parent, XPathConstants.STRING);
        } catch (XPathExpressionException xpee) {
            throw new Exception(ExceptionUtil.getFullStackTrace(xpee));
        }
    }

    public static String getFirstValue(Element parent, String nodeName) {
        if(parent.getElementsByTagName(nodeName).getLength() > 0 ) {
            return parent.getElementsByTagName(nodeName).item(0).getFirstChild().getNodeValue();
        }
        else {
            return null;
        }
    }

    public static List<Element> getAllElementsByTagName(Element elem, String tagName) {
        List<Element> ret = new LinkedList<Element>();
        getAllElementsByTagName(elem, tagName, ret);
        return ret;
    }

    private static void getAllElementsByTagName(Element el, String tagName, List<Element> elementList) {

        if (tagName.equals(el.getTagName())) {
            elementList.add(el);
        }
        Element elem = getFirstElement(el);
        while (elem != null) {
            getAllElementsByTagName(elem, tagName, elementList);
            elem = getNextElement(elem);
        }
    }

    private static void getAllElementsByTagNameNS(Element el, String nameSpaceURI, String localName,
                                                   List<Element> elementList) {

        if (localName.equals(el.getLocalName()) && nameSpaceURI.contains(el.getNamespaceURI())) {
            elementList.add(el);
        }
        Element elem = getFirstElement(el);
        while (elem != null) {
            getAllElementsByTagNameNS(elem, nameSpaceURI, localName, elementList);
            elem = getNextElement(elem);
        }
    }

    public static Element getFirstElement(Node parent) {
        Node n = parent.getFirstChild();
        while (n != null && Node.ELEMENT_NODE != n.getNodeType()) {
            n = n.getNextSibling();
        }
        if (n == null) {
            return null;
        }
        return (Element) n;
    }

    public static Element getNextElement(Element el) {
        Node nd = el.getNextSibling();
        while (nd != null) {
            if (nd.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) nd;
            }
            nd = nd.getNextSibling();
        }
        return null;
    }

}

