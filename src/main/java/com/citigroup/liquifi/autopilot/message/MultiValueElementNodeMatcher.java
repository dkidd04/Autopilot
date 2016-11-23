package com.citigroup.liquifi.autopilot.message;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class MultiValueElementNodeMatcher extends NodeMatcher {

	@Override
	protected String[] getTargetNodeTree(String nodeTree) {
		if(nodeTree.contains(".")){
			nodeTree = nodeTree.substring(0,nodeTree.lastIndexOf('.'));
			return nodeTree.split("\\.");
		} else {
			return new String[]{};
		}

	}

	@Override
	protected Node getValueNode(Node childNode, String targetLeafName) {
		Node valueNode = null;

		if(childNode.hasChildNodes() && containsTargetNode(childNode, targetLeafName)){
			valueNode = findValueNode(childNode);
		}
		return valueNode;
	}

	private Node findValueNode(Node childNode) {
		Node valueNode = null;
		NodeList keyValue = childNode.getChildNodes();
		for(int j=0;j<keyValue.getLength();j++){
			Node keyValueNode = keyValue.item(j);
			if(keyValueNode.getNodeName().equals("Value")){
				valueNode = keyValueNode;
			}
		}
		return valueNode;
	}

	private boolean containsTargetNode(Node childNode, String targetLeafName) {
		NodeList keyValue = childNode.getChildNodes();
		for(int j=0;j<keyValue.getLength();j++){
			Node keyValueNode = keyValue.item(j);
			if(keyValueNode.getNodeName().equals("Name") && targetLeafName.equals(keyValueNode.getTextContent())){
				return true;
			}
		}
		return false;
	}

	@Override
	public Node createNode(String childNodeName, String parentNodeName, String nodeValue, Document document) {
		Node childNode = document.createElement("Name");
		childNode.setTextContent(childNodeName);
		Node childValue = document.createElement("Value");
		childValue.setTextContent(nodeValue);
		Node parentNode = document.createElement(parentNodeName);
		parentNode.appendChild(childNode);
		parentNode.appendChild(childValue);
		return parentNode;
	}

}