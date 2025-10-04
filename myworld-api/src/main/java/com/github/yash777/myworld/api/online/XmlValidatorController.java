package com.github.yash777.myworld.api.online;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * ✅ XML Validator API
 * 
 * This controller provides endpoints and utility methods to:
 * 
 * <ul>
 * <li>✔ Validate raw XML input and return a single-line (minified) version</li>
 * <li>📦 Extract Maven-style dependencies: <code>groupId</code>, <code>artifactId</code>, <code>version</code>, <code>classifier</code></li>
 * <li>🧪 Support standalone execution from <code>main()</code> method for CLI testing</li>
 * </ul>
 * 
 * 🔍 Test your XML online:
 * 
 * <ul>
 * <li><a href="https://www.freeformatter.com/xml-validator-xsd.html">FreeFormatter XML Validator</a></li>
 * <li><a href="https://xmlgrid.net/">XML Grid Editor</a></li>
 * <li><a href="https://codebeautify.org/xmlvalidator">CodeBeautify XML Validator</a></li>
 * </ul>
 * 
 * 📌 Usage Modes:
 * 
 * <ul>
 * <li>✅ Spring Boot REST endpoint (via <code>@RestController</code>)</li>
 * <li>✅ Standalone Java app via <code>main()</code> method</li>
 * </ul>
 * 
 * @author 🔐 yashwanth
*/
@RestController
@RequestMapping("/xml")
public class XmlValidatorController {
	
	/**
	 * Validate XML and return single-line version or error message.
	 *
	 * @param xmlInput The XML content as string
	 * @return Compact XML string or error message
	 */
	@PostMapping("/validate")
	public String validateXml(
			@Parameter(description = "XML input", example = """
			<user>
				<name>Yash</name>
			</user>
			""")
			@RequestParam(required = true)
			String xmlInput
			) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));
			doc.getDocumentElement().normalize();
			
			// Minify XML (remove newlines, extra spaces)
			return xmlInput.replaceAll("\n+", " ").replaceAll("\\s+", " ").trim();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			return "❌ Invalid XML: " + e.getMessage();
		}
	}
	
	/**
	 * Standalone Java method for testing XML validation and Maven dependency parsing.
	 */
	public static void main(String[] args) {
		// Java 15+ required for text blocks
		String xmlInput = """
		<dependencies>
			<dependency>
				<groupId>org.hibernate.orm</groupId>
				<artifactId>hibernate-jcache</artifactId>
				<version>${hibernate.version}</version>
			</dependency>
			<dependency>
				<groupId>org.ehcache</groupId>
				<artifactId>ehcache</artifactId>
				<version>${ehcache3.version}</version>
				<classifier>jakarta</classifier>
			</dependency>
			<dependency>
				<groupId>org.springdoc</groupId>
				<artifactId>springdoc-openapi-ui</artifactId>
				<version>1.7.0</version>
			</dependency>
		</dependencies>
		""";

		
		XmlValidatorController controller = new XmlValidatorController();
		System.out.println("✅ Minified XML:\n" + controller.validateXml(xmlInput));
		
		System.out.println("\n📦 Extracted Maven Dependencies:");
		extractDependencies(xmlInput);
	}
	
	/**
	 * Parse Maven-style <dependency> entries and print structured output.
	 */
	public static void extractDependencies(String xmlInput) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(xmlInput.getBytes()));
			doc.getDocumentElement().normalize();
			
			NodeList dependencies = doc.getElementsByTagName("dependency");
			for (int i = 0; i < dependencies.getLength(); i++) {
				Element dep = (Element) dependencies.item(i);
				String groupId = dep.getElementsByTagName("groupId").item(0).getTextContent();
				String artifactId = dep.getElementsByTagName("artifactId").item(0).getTextContent();
				String version = dep.getElementsByTagName("version").item(0).getTextContent();
				NodeList classifierNode = dep.getElementsByTagName("classifier");
				String classifier = classifierNode.getLength() > 0 ? classifierNode.item(0).getTextContent() : "";
				System.out.println(groupId + " : " + artifactId + " : " + version + (classifier.isEmpty() ? "" : " : " + classifier));
			}
		} catch (Exception e) {
			System.err.println("❌ Error parsing dependencies: " + e.getMessage());
		}
	}
}