package com.github.yash777.commons.objectmapper;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import org.xml.sax.SAXException;

/**
 * Java program that reads an XML string, formats it into a single-line string, 
 * and extracts Maven dependencies into a structured format
 * 
 * @author ymerugu
 *
 */
public class XMLParser {

    public static void main(String[] args) throws Exception {
    	// XML input as a text block (multiline string)  - use java compiler >= 15
        String xmlInput = """
        		<dependencies>
        		
 <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-sleuth</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-zipkin</artifactId>
		</dependency>


		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
		
		</dependencies>
                """;

        try {
            // Convert string to XML Document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlInput.getBytes()));
            doc.getDocumentElement().normalize();
            
            // Convert XML to Single Line :: https://tools.knowledgewalls.com/online-multiline-to-single-line-converter
            String singleLineXML = xmlInput.replaceAll("\n+", " ").replaceAll("\s+", " ").trim();
            System.out.println("Single Line Output:");
            System.out.println(singleLineXML);
            
            //parseXML(xmlInput);
            
            // Extract dependencies in Maven format
            NodeList dependencies = doc.getElementsByTagName("dependency");
            System.out.println("\nMaven Format Output:");
            for (int i = 0; i < dependencies.getLength(); i++) {
            	Element dep = (Element) dependencies.item(i);

                String groupId = getTagValue(dep, "groupId");
                String artifactId = getTagValue(dep, "artifactId");
                String version = getTagValue(dep, "version");
                String classifier = getTagValue(dep, "classifier");

                StringBuilder output = new StringBuilder();
                output.append(groupId).append(" : ").append(artifactId);

                if (version != null && !version.isEmpty()) {
                    output.append(" : ").append(version);
                }

                if (classifier != null && !classifier.isEmpty()) {
                    output.append(" : ").append(classifier);
                }

                System.out.println(output.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList != null && nodeList.getLength() > 0 && nodeList.item(0) != null) {
            return nodeList.item(0).getTextContent().trim();
        }
        return null;
    }
    public static void parseXML(String xmlString) throws Exception {
        // Create an input stream from the XML string
        InputStream is = new ByteArrayInputStream(xmlString.getBytes());

        // Initialize the document builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);

        // Normalize the XML structure
        document.getDocumentElement().normalize();

        // Get all the dependency elements
        NodeList nodeList = document.getElementsByTagName("dependency");

        // Loop through the NodeList and process each dependency
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;

                // Extract groupId, artifactId, version, and classifier (if present)
                String groupId = element.getElementsByTagName("groupId").item(0).getTextContent();
                String artifactId = element.getElementsByTagName("artifactId").item(0).getTextContent();
                String version = element.getElementsByTagName("version").item(0).getTextContent();
                NodeList classifierNodes = element.getElementsByTagName("classifier");
                String classifier = classifierNodes.getLength() > 0 ? classifierNodes.item(0).getTextContent() : "";

                // Output the formatted result
                if (!classifier.isEmpty()) {
                    System.out.println(groupId + " : " + artifactId + " : " + version + " : " + classifier);
                } else {
                    System.out.println(groupId + " : " + artifactId + " : " + version);
                }
            }
        }
    }
}
