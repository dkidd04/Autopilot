package com.citigroup.liquifi.autopilot.message;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.citigroup.liquifi.autopilot.util.Command;
import com.citigroup.liquifi.autopilot.util.Command.CommandName;
import com.citigroup.liquifi.entities.Tag;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class AdminXMLFactory implements XMLFactory {
	@Override
	public String overWriteTags(String msgStr, Set<Tag> specTags) {
		AdminXMLMessage msg = new AdminXMLMessage(msgStr);
		msg.overWriteTags(specTags);
		return msg.toString();
	}
	
	public String extractCommands(String msgStr, List<Command> commands) throws Exception {
		Document document = loadXML(msgStr);
		boolean deleteNode = false;
		Element adminRequest = document.getDocumentElement();

		for (int i = 0; i < adminRequest.getChildNodes().getLength(); i++) {
			Node requestObjectNode = adminRequest.getChildNodes().item(i);

			if (requestObjectNode.getChildNodes().getLength() > 1) {
				String requestName = requestObjectNode.getNodeName();

				if (requestName.equals("AutoPilot")) {
					deleteNode = true;
					for (int j = 0; j < requestObjectNode.getChildNodes().getLength(); j++) {
						if (requestObjectNode.getChildNodes().item(j).getNodeName().equals("property")) {
							Node property = requestObjectNode.getChildNodes().item(j);
							Node name = property.getAttributes().item(0);
							Node value = property.getAttributes().item(1);
	
							commands.add(new Command(CommandName.getEnum(name.getNodeValue()), value.getNodeValue()));
							break;
						}
					}
				}
			}

			if (deleteNode) {
				adminRequest.removeChild(requestObjectNode);
				deleteNode = false;
				i--;
			}
		}

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		transformer.transform(source, result);

		return stringWriter.getBuffer().toString();
	}

	public String overwriteAdminOrderRequest2XMLMessage(String msgStr, Set<Tag> specTags) throws Exception {
		Document document = loadXML(msgStr);
		Element adminRequest = document.getDocumentElement();

		for (int i = 0; i < adminRequest.getChildNodes().getLength(); i++) {
			Node requestObjectNode = adminRequest.getChildNodes().item(i);

			// code below design to parse blocks, for example:
			// <Symbol>P</Symbol>
			// Symbol = "C", becomes:
			// <Symbol>C</Symbol>
			if (requestObjectNode.getChildNodes().getLength() == 1) {
				String requestName = requestObjectNode.getNodeName();
				
				for (Tag tag : specTags) {
					if(requestName.equals(tag.getTagID())) {
						requestObjectNode.getChildNodes().item(0).setNodeValue(tag.getTagValue());
					}
				}
			}
			
			// code below designed to parse "properties" within blocks, for example:
			// <BookAttribute><property name="value" value=""/></BoolkAttribute>
			// BookAttribute.value = "Hello World", becomes:
			// <BookAttribute><property name="value" value="Hello World"/></BookAttribute>
			else if (requestObjectNode.getChildNodes().getLength() > 1) {
				String requestName = requestObjectNode.getNodeName();

				for (int j = 0; j < requestObjectNode.getChildNodes().getLength(); j++) {
					if (requestObjectNode.getChildNodes().item(j).getNodeName().equals("property")) {
						Node property = requestObjectNode.getChildNodes().item(j);
						for (int k = 0; k < property.getAttributes().getLength(); k = k + 2) {
							Node name = property.getAttributes().item(k);
							Node value = property.getAttributes().item(k + 1);

							String methodName = name.getNodeValue();

							for (Tag tag : specTags) {
								String[] split = tag.getTagID().split("\\.");
								if (requestName.equals(split[0])) {
									if (methodName.equals(split[1])) {
										value.setNodeValue(tag.getTagValue());
									}
								}
							}
						}
					}
				}
			}
		}

		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		transformer.transform(source, result);

		return stringWriter.getBuffer().toString();

	}

	private static synchronized Document loadXML(Object xml) {
		Document document = null;
		try {
			DOMParser parser = new DOMParser();
			if (xml instanceof String) {
				ByteArrayInputStream bs = new ByteArrayInputStream(((String) xml).getBytes());
				InputSource source = new InputSource(bs);
				parser.parse(source);
			}
			document = parser.getDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

}
