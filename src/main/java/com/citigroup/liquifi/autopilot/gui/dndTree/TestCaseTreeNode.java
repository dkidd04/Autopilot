package com.citigroup.liquifi.autopilot.gui.dndTree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.citigroup.liquifi.entities.LFCategory;
import com.citigroup.liquifi.entities.LFLabel;

public class TestCaseTreeNode extends DefaultMutableTreeNode{
	
	public enum NodeType {ROOT, ALL_CASES_DIR, CATEGORY, LABEL_FOLDER, LABEL, LABEL_IN_CATEGORY, RELEASE_NUM_DIR, RELEASE_NUM_NODE, JIRA_NUM_DIR, JIRA_NUM_NODE};
	
	private NodeType type;
	
	private int casesCount;
	
	private String nodeName;
	
	private LFCategory category;
	
	private LFLabel label;
	


	public TestCaseTreeNode(String nodeName, NodeType type){
		
		super(nodeName);
		this.type = type;
		this.nodeName = nodeName;
	}
	
	public TestCaseTreeNode(LFCategory category, LFLabel label){
		super(label.getLabel());
		this.type = NodeType.LABEL_IN_CATEGORY;
		this.nodeName = label.getLabel();
		this.label = label;
		this.category = category;
	}
	
	public TestCaseTreeNode(LFCategory category){
		super(category.getCategory());
		this.type = NodeType.CATEGORY;
		this.nodeName = category.getCategory();
		this.category = category;
	}
	
	public TestCaseTreeNode(LFLabel label){
		super(label.getLabel());
		this.type = NodeType.LABEL;
		this.nodeName = label.getLabel();
		this.label = label;
	}
	
	public void setName(String name){
		this.nodeName = name;
	}
	
	public LFLabel getLabel() {
		return label;
	}

	public void setLabel(LFLabel label) {
		this.label = label;
	}

	public NodeType getType(){
		
		return this.type;
		
	}
	
	public void setChildCount(int count){
		this.casesCount = count;
	}
	
	public int getTestCount(){
		return this.getTestCount();
	}
	
	public String getName(){
		return super.toString();
	}
	
	public void incrementCaseCount(){
		this.casesCount++;
	}
	
	public void decrementCaseCount(){
		if(this.casesCount > 0){
			this.casesCount--;
		}
	}
	
	public LFCategory getCategory() {
		return category;
	}

	public void setCategory(LFCategory category) {
		this.category = category;
	}
	
	@Override
	public String toString(){
		if(this.type.equals(NodeType.ALL_CASES_DIR) || this.type.equals(NodeType.LABEL) || this.type.equals(NodeType.CATEGORY)){
			return nodeName + " (" + this.casesCount + ")";
		}
		
		return nodeName;
	}
	
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof TestCaseTreeNode)){
			return false;
		}
		TestCaseTreeNode otherNode = (TestCaseTreeNode) obj;
		return (this.type.equals(otherNode.getType()) && this.nodeName.equals(otherNode.nodeName));
	}
	
	@Override
	public int hashCode(){
		return this.nodeName.hashCode() + this.type.hashCode();
	}

}
