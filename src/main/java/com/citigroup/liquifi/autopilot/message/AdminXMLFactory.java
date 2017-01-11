package com.citigroup.liquifi.autopilot.message;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.citigroup.liquifi.autopilot.controller.TestCaseController;
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

		return toString(document);
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

		return toString(document);
	}

	private String toString(Document document)
			throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();

		DOMSource source = new DOMSource(document);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		if(TestCaseController.OMIT_XML_DECLARATION){
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		}
		transformer.transform(source, result);

		return stringWriter.getBuffer().toString();
	}

	private synchronized Document loadXML(Object xml) {
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

	@Override
	public String getField(String msg, String strTagID) {
		String sanitisedMessage = sanitise(msg);
		Node firstMatchingNode = null;
		try {
			Document document = parseXMLMessage(sanitisedMessage);
			Element root = document.getDocumentElement();
			firstMatchingNode = getFieldFromRoot(strTagID, root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return firstMatchingNode!=null ? firstMatchingNode.getTextContent() : null;
	}

	private Node getFieldFromRoot(String strTagID, Element xmlMessage) {
		Node firstMatchingNode;
		String targetleafName = leaf(strTagID);
		String tree = parent(strTagID);
		NodeMatcher nodeFinder = null;
		if("listValue".equals(targetleafName)){
			targetleafName = leaf(tree);
			tree = parent(tree);
			nodeFinder = new MultiValueElementNodeMatcher();
		} else {
			nodeFinder = new SingleElementNodeMatcher();
		}
		String[] split = tree.length()>0?tree.split("\\.") : new String[]{};
		firstMatchingNode = nodeFinder.matchNode(xmlMessage, split, targetleafName);
		return firstMatchingNode;
	}

	private String sanitise(String msg) {
		return msg.replaceAll(String.valueOf('\001'), "^A").replaceAll(String.valueOf('\005'), "^E").replaceAll(String.valueOf('\006'), "^F");
	}

	@Override
	public String overWriteTagsGeneric(String inputMsg, Set<Tag> overwriteTags) {
		Document document = parseXMLMessage(inputMsg);
		Element root = document.getDocumentElement();
		for(Tag tag : overwriteTags){
			String targetNode = tag.getTagID();
			String targetValue = tag.getTagValue();
			overWriteTagGeneric(document, root, targetNode, targetValue);
		}
		return toXMLString(document);
	}
	
	private void overWriteTagGeneric(Document document, Element root, String targetNode, String targetValue) {
		String targetleafName = leaf(targetNode);
		String tree = parent(targetNode);
		NodeMatcher nodeFinder = null;
		if("listValue".equals(targetleafName)){
			targetleafName = leaf(tree);
			tree = parent(tree);
			nodeFinder = new MultiValueElementNodeMatcher();
		} else {
			nodeFinder = new SingleElementNodeMatcher();
		}
		String[] split = tree.length()>0?tree.split("\\.") : new String[]{};
		Node matchingNode = nodeFinder.matchNode(root, split, targetleafName);
		if(matchingNode!=null){
			System.out.println("changing value");
			matchingNode.setTextContent(targetValue);
		} else {
			System.out.println("creating new node");
			String parentNodeName = leaf(tree);
			Node node = nodeFinder.createNode(targetleafName, parentNodeName, targetValue, document);
			nodeFinder.addNodeToTree(root, tree, node, document);
		}
	}

	private String toXMLString(Document document) {
		String output = null;
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(writer));
			output = writer.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	private Document parseXMLMessage(String inputMsg) {
		String sanitisedMessage = sanitise(inputMsg);
		return loadXML(sanitisedMessage);
	}

	private String parent(String target) {
		if(target.contains(".")){
			return target.substring(0, target.lastIndexOf('.'));
		} else return "";
	}

	private String leaf(String target) {
		return target.substring(target.lastIndexOf('.')+1);
	}

}

