package com.citigroup.liquifi.autopilot.message;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SingleElementNodeMatcher extends NodeMatcher {

	@Override
	protected Node getValueNode(Node parentNode, String targetLeafName) {
		NodeList childNodes = parentNode.getChildNodes();
		for(int i=0;i<childNodes.getLength();i++){
			Node child = childNodes.item(i);
			if(targetLeafName.equals(child.getNodeName())){
				return child;
			}
		}
		return null;
	}

	@Override
	public Node createNode(String nodeName, String parentNode, String nodeValue, Document document) {
		Node newNode = document.createElement(nodeName);
		newNode.setTextContent(nodeValue);
		return newNode;
	}

	@Override
	protected String[] getTargetNodeTree(String nodeTree) {
		if(nodeTree.isEmpty()){
			return new String[]{};
		} else {
			return nodeTree.split("\\.");
		}
	}
}