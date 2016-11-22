package com.citigroup.liquifi.autopilot.message;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

abstract class NodeMatcher {

	protected abstract Node getValueNode(Node node, String targetLeafName);

	public void addNodeToTree(Node node, String nodeTree, Node targetLeaf, Document document){
		String[] targetNodeTree = getTargetNodeTree(nodeTree);
		addNodeToTreeRecursive(node, targetNodeTree, targetLeaf, document, 0);
	}

	protected abstract String[] getTargetNodeTree(String nodeTree);

	public abstract Node createNode(String nodeName, String parentNode, String nodeValue, Document document);

	protected void addNodeToTreeRecursive(Node node, String[] targetNodeTree, Node targetLeaf, Document document, int currentDepth){
		if(currentDepth == targetNodeTree.length){
			node.appendChild(targetLeaf);
			return;
		}

		String targetNodeName = targetNodeTree[currentDepth];
		NodeList childNodes = node.getChildNodes();
		Node targetNode = null;
		for(int i=0; i<childNodes.getLength();i++){
			Node childNode = childNodes.item(i);
			if(childNode.getNodeName().equals(targetNodeName)){
				targetNode = childNode;
				break;
			}
		}

		if(targetNode==null){
			targetNode = document.createElement(targetNodeName);
			node.appendChild(targetNode);
		}
		addNodeToTreeRecursive(targetNode, targetNodeTree, targetLeaf, document, ++currentDepth);
	}

	public Node matchNode(Node node, String[] targetNodeTree, String targetLeafName) {
		return getMatch(node, 0, targetNodeTree, targetLeafName);
	}

	protected Node getMatch(Node node, int currentDepth, String[] targetNodeTree, String targetLeafName){
		if(currentDepth > targetNodeTree.length){
			return null;
		} else if (currentDepth == targetNodeTree.length){
			Node valueNode = getValueNode(node, targetLeafName);
			return valueNode;
		}

		String targetNodeName = targetNodeTree[currentDepth];
		NodeList childNodes = node.getChildNodes();
		for(int i=0; i<childNodes.getLength();i++){
			Node childNode = childNodes.item(i);
			if(childNode.getNodeName().equals(targetNodeName)){
				Node valueNode = getMatch(childNode, currentDepth+1, targetNodeTree, targetLeafName);
				if(valueNode!=null){
					return valueNode;
				}
			}
		}
		return null;
	}

}