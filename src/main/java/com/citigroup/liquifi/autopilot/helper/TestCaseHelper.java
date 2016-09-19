package com.citigroup.liquifi.autopilot.helper;

import java.awt.Color;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.StyledDocument;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.citigroup.liquifi.AutoPilotAppl;
import com.citigroup.liquifi.autopilot.bootstrap.ApplicationContext;
import com.citigroup.liquifi.autopilot.controller.TestCaseController;
import com.citigroup.liquifi.autopilot.gui.AutoPilotApplView;
import com.citigroup.liquifi.autopilot.gui.InputStepPanel;
import com.citigroup.liquifi.autopilot.gui.SmartTagDialog;
import com.citigroup.liquifi.autopilot.gui.OutputStepPanel;
import com.citigroup.liquifi.autopilot.gui.StepLinkDialog;
import com.citigroup.liquifi.autopilot.gui.model.InputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.OutputStepTableModel;
import com.citigroup.liquifi.autopilot.gui.model.RepeatingGroupTableModel;
import com.citigroup.liquifi.autopilot.gui.model.TagTableModel;
import com.citigroup.liquifi.entities.LFCommonOverwriteTag;
import com.citigroup.liquifi.entities.LFOutputMsg;
import com.citigroup.liquifi.entities.LFOutputTag;
import com.citigroup.liquifi.entities.LFTag;
import com.citigroup.liquifi.entities.LFTemplate;
import com.citigroup.liquifi.entities.LFTestCase;
import com.citigroup.liquifi.entities.LFTestInputSteps;
import com.citigroup.liquifi.entities.Step;
import com.citigroup.liquifi.entities.Tag;
import com.citigroup.liquifi.util.AutoPilotConstants;
import com.citigroup.liquifi.util.DBUtil;
import com.citigroup.liquifi.util.Util;

public class TestCaseHelper {
	private InputStepTableModel inputStepTableModel = ApplicationContext.getInputStepTableModel(); // update with each testcase
	private TagTableModel<LFTag> inputTagTableModel = ApplicationContext.getInputTagTableModel(); // update with each input step selected
	private OutputStepTableModel outputStepTableModel = ApplicationContext.getOutputStepTableModel(); // update with each input step selected
	private TagTableModel<LFOutputTag> outputTagTableModel = ApplicationContext.getOutputTagTableModel(); // update with each output step selected
	private TagTableModel<LFTag> validationTagTableModel = ApplicationContext.getValidationTagTableModel(); // update with each output step selected
	private TagTableModel<LFTag> validationTemplateTableModel = ApplicationContext.getValidationTemplateTableModel(); // update with each output step selected
	private RepeatingGroupTableModel<Tag> repeatingGroupTagTableModel = ApplicationContext.getRepeatingGroupTagTableModel();
	private int inputRowPointer = -1;
	private int outputRowPointer = -1;
	private Map<String, LFTemplate> templateMap = DBUtil.getInstance().getTem().getAllTemplateMap();
	private Map<String, List<LFCommonOverwriteTag>> commonOverwriteTagMap = DBUtil.getInstance().getCom().getCommonOverwriteTagMap();
	private LFTestCase testcase = null;
	private HighlightPainter highlighPainter = new DefaultHighlightPainter(Color.YELLOW);
	private boolean testCaseChangesAreSaved = true;


	public void resetTestCaseChangesSavedStatus(){
		this.testCaseChangesAreSaved = true;
	}

	public void setTestCaseChangesSavedStatus(boolean status){
		this.testCaseChangesAreSaved = status;
	}

	public boolean isTestCaseChangesSaved(){
		return this.testCaseChangesAreSaved;
	}

	public void setTestcase(LFTestCase testcase) {
		this.testcase = testcase;

		// Set input step table model
		inputStepTableModel.setData(testcase.getInputStepList());
		if(inputStepTableModel.getRowCount() > 0) {
			setInputStepTableRowPointer(0);
		}
	}

	public LFTestCase getTestcase() {
		return testcase;
	}

	public void createDefaultSteps(){
		List<LFTestInputSteps> stepList = ApplicationContext.getConfig().getDefaultInputStep();
		for(int rowIndex = 0; rowIndex < stepList.size(); rowIndex++){
			LFTestInputSteps inputStep = stepList.get(rowIndex);
			inputStep.setTestID(testcase.getTestID());
			inputStepTableModel.addRow(rowIndex, stepList.get(rowIndex));
			createPlaceHolderTags(stepList.get(rowIndex));
		}
	}

	public void addOutputStepTag() {
		LFOutputTag tag = new LFOutputTag();
		tag.setActionSequence(inputRowPointer+1);
		tag.setOutputMsgID(outputRowPointer+1);
		tag.setTestID(testcase.getTestID());

		outputTagTableModel.add(tag);
	}


	public void addOutputStepTag(LFOutputTag tag) {
		// TODO Auto-generated method stub
		tag.setActionSequence(inputRowPointer+1);
		tag.setOutputMsgID(outputRowPointer+1);
		tag.setTestID(testcase.getTestID());

		outputTagTableModel.add(tag);
	}

	public void updateRepeatingGroupTag(Tag overwrittenTag, String groupNum, boolean isInputStep) {

		if(isInputStep){
			boolean tagOverwritten = false;
			String newTagId = new StringBuilder(overwrittenTag.getTagID()).append("[").append(groupNum).append("]").toString();
			String newTagValue = overwrittenTag.getTagValue();
			for(LFTag t : inputTagTableModel.getData()){
				if(t.getTagID().endsWith(newTagId)){
					tagOverwritten = true;
					t.setTagValue(newTagValue);
					inputTagTableModel.fireTableDataChanged();
				}
			}
			if(!tagOverwritten){
				LFTag newTag = new LFTag();
				newTag.setTestID(testcase.getTestID());
				newTag.setActionSequence(inputRowPointer+1);
				newTag.setTagID(newTagId);
				newTag.setTagValue(newTagValue);
				inputTagTableModel.add(newTag);
			}
		}else{
			boolean tagOverwritten = false;
			String newTagId = new StringBuilder(overwrittenTag.getTagID()).append("[").append(groupNum).append("]").toString();
			String newTagValue = overwrittenTag.getTagValue();
			for(LFOutputTag t : outputTagTableModel.getData()){
				if(t.getTagID().endsWith(newTagId)){
					tagOverwritten = true;
					t.setTagValue(newTagValue);
					inputTagTableModel.fireTableDataChanged();
				}
			}
			if(!tagOverwritten){
				LFOutputTag newTag = new LFOutputTag();
				newTag.setOutputMsgID(outputRowPointer + 1);
				newTag.setTestID(testcase.getTestID());
				newTag.setActionSequence(inputRowPointer+1);
				newTag.setTagID(newTagId);
				newTag.setTagValue(newTagValue);
				outputTagTableModel.add(newTag);
			}
		}
		setTestCaseChangesSavedStatus(false);

	}

	public void removeOutputStepTag(int step){
		if (step != -1){
			outputTagTableModel.removeRow(step);

			// Refresh msg panel
			refreshInputMessageBox();
			refreshOutputMessageBox();
		}
	}

	public void addValidationTag(LFTag tag) {
		tag.setTestID(testcase.getTestID());
		//		tag.setActionSequence(inputRowPointer+1);
		validationTagTableModel.add(tag);
	}

	public void addNewValidationTemplateTag(LFTag tag){
		tag.setTestID(testcase.getTestID());
		//		tag.setActionSequence(inputRowPointer+1);
		validationTemplateTableModel.add(tag);
	}

	public void populateRepeatingGroupTag(LFTag tag){
		this.repeatingGroupTagTableModel.add(tag);
	}

	public void addInputStepTag() {
		LFTag tag = new LFTag();
		tag.setTestID(testcase.getTestID());
		tag.setActionSequence(inputRowPointer+1);

		inputTagTableModel.add(tag);
	}



	public void addInputStepTag(LFTag tag) {
		tag.setTestID(testcase.getTestID());
		tag.setActionSequence(inputRowPointer+1);
		inputTagTableModel.add(tag);
	}

	public void removeInputStepTag(int step) {
		if (step != -1){
			inputTagTableModel.removeRow(step);

			// Refresh msg panel
			refreshInputMessageBox();
			refreshOutputMessageBox();
		}
	}

	public void removeValidationTemplateTag(int step){
		if(step != -1){
			validationTemplateTableModel.removeRow(step);
		}
	}


	public void removeInputStep(int step) {
		if (step != -1){
			clearStepLink(step, -1);
			refractor(step, -1, false);

			// Clear data structures
			for(LFOutputMsg outputStep : inputStepTableModel.getRow(step).getOutputStepList()) {
				outputStep.getOutputTagList().clear();
			}
			inputStepTableModel.getRow(step).getOutputStepList().clear();
			inputStepTableModel.getRow(step).getInputTagsValueList().clear();

			// Finally remove from input step table
			inputStepTableModel.removeRow(step);
		}
	}

	public void clearStepLink(int inputStepNum, int outputStepNum){

		if(outputStepNum == -1){ //removing input step
			LFTestInputSteps inputStepToRemove = inputStepTableModel.getData().get(inputStepNum);
			// clear all child step ref of to be removed input step
			for(String refStepStr : inputStepToRemove.getChildrenStepsArray()){
				int[] stepArr = parseStep(refStepStr);
				int childInputStepNum = stepArr[0]-1;
				int childOutputStepNum = stepArr[1]-1;
				LFOutputMsg childStep = inputStepTableModel.getData().get(childInputStepNum).getOutputStepList().get(childOutputStepNum);
				for (Iterator<LFOutputTag> it = childStep.getOverrideTags().iterator(); it.hasNext(); ){
					LFOutputTag outputTag = it.next();
					if(outputTag.getTagID().startsWith("@APVAR") && outputTag.getTagValue().equals("@IP[" + inputStepToRemove.getActionSequence() + "]")){
						childStep.setParentPlaceHolder(null);
						childStep.setParentRefStep(null);
						//remove ref tag
						it.remove();
						break;
					}
				}
			}

			// clear all child step ref of to be removed output step
			for(LFOutputMsg outputStep : inputStepToRemove.getOutputStepList()){
				for(String refStepStr2 : outputStep.getChildrenStepsArray()){
					int[] stepArr2 = parseStep(refStepStr2);
					int childInputStepNum2 = stepArr2[0]-1;
					int childOutputStepNum2 = stepArr2[1]-1;
					LFOutputMsg childStep2 = inputStepTableModel.getData().get(childInputStepNum2).getOutputStepList().get(childOutputStepNum2);
					for (Iterator<LFOutputTag> it2 = childStep2.getOverrideTags().iterator(); it2.hasNext(); ){
						LFOutputTag outputTag = it2.next();
						if(outputTag.getTagID().startsWith("@APVAR") && outputTag.getTagValue().equals("@OP[" + outputStep.getActionSequence() + "][" + outputStep.getOutputMsgID() + "]")){
							childStep2.setParentPlaceHolder(null);
							childStep2.setParentRefStep(null);
							//remove ref tag
							it2.remove();
							break;
						}
					}
				}
			}
		}else{ //removing output step
			LFOutputMsg outputStepToRemove = inputStepTableModel.getData().get(inputStepNum).getOutputStepList().get(outputStepNum);
			for(String refStepStr2 : outputStepToRemove.getChildrenStepsArray()){
				int[] stepArr2 = parseStep(refStepStr2);
				int childInputStepNum2 = stepArr2[0]-1;
				int childOutputStepNum2 = stepArr2[1]-1;
				LFOutputMsg childStep2 = inputStepTableModel.getData().get(childInputStepNum2).getOutputStepList().get(childOutputStepNum2);
				for (Iterator<LFOutputTag> it2 = childStep2.getOverrideTags().iterator(); it2.hasNext(); ){
					LFOutputTag outputTag = it2.next();
					if(outputTag.getTagID().startsWith("@APVAR") && outputTag.getTagValue().equals("@OP[" + outputStepToRemove.getActionSequence() + "][" + outputStepToRemove.getOutputMsgID() + "]")){
						childStep2.setParentPlaceHolder(null);
						childStep2.setParentRefStep(null);
						//remove ref tag
						it2.remove();
						break;
					}
				}
			}
		}

	}

	public int[] parseStep(String refChildStepStr){
		String step = refChildStepStr;
		step = step.replace("[", "");
		String[] tokens = step.split("]");
		int[] stepArr = new int[tokens.length];
		for(int i = 0; i < tokens.length; i++){
			stepArr[i] = Integer.parseInt(tokens[i]);
		}
		return stepArr;
	}


	public void removeOutputStep(int step) {
		if(step != -1) {
			clearStepLink(inputRowPointer, step);
			refractor(inputRowPointer, step, false);

			// Clear data structure
			outputStepTableModel.getRow(step).getOutputTagList().clear();

			outputStepTableModel.removeRow(step);
		}

	}



	public void setInputStepTableRowPointer(int pointer) {
		inputRowPointer = pointer;

		if(pointer == -1){
			// If pointer is -1 then remove input tags and message box
			inputTagTableModel.setData(new ArrayList<LFTag>());
			ApplicationContext.getTestcasePanel().getTextArea(1).setText("");

			// ... and remove output steps, output tags and message box
			outputStepTableModel.setData(new ArrayList<LFOutputMsg>());
			outputTagTableModel.setData(new ArrayList<LFOutputTag>());
			ApplicationContext.getTestcasePanel().getTextArea(2).setText("");
			outputRowPointer = -1;
		} else if(pointer > -1) {			
			//ApplicationContext.getTestcasePanel().getInputStepTable().getSelectionModel().setSelectionInterval(pointer, pointer);
			inputTagTableModel.setData(inputStepTableModel.getRow(pointer).getInputTagsValueList());
			refreshInputMessageBox();

			// Output Steps
			outputStepTableModel.setData(inputStepTableModel.getRow(inputRowPointer).getOutputStepList());
			if(outputStepTableModel.getRowCount() > 0) {
				setOutputStepTableRowPointer(0);
			}
		}
	}

	public void setOutputStepTableRowPointer(int pointer) {
		outputRowPointer = pointer;

		if(pointer == -1){
			// If pointer is -1 then remove output tags and message box
			outputTagTableModel.setData(new ArrayList<LFOutputTag>());
			ApplicationContext.getTestcasePanel().getTextArea(2).setText("");
		} else if(pointer > -1) {
			ApplicationContext.getTestcasePanel().getOutputStepTable().getSelectionModel().setSelectionInterval(pointer, pointer);
			outputTagTableModel.setData(outputStepTableModel.getRow(outputRowPointer).getOutputTagList());
			refreshOutputMessageBox();
		}
	}

	private void refreshInputMessageBox() {
		StyledDocument doc = ApplicationContext.getTestcasePanel().getTextArea(1).getStyledDocument();
		if(inputRowPointer > -1) {
			format(inputStepTableModel.getRow(inputRowPointer), doc);
		}
	}

	private void refreshOutputMessageBox() {
		StyledDocument doc = ApplicationContext.getTestcasePanel().getTextArea(2).getStyledDocument();
		if(outputRowPointer > -1) {
			format(outputStepTableModel.getRow(outputRowPointer), doc);
		}
	}

	private Pattern varPattern = Pattern.compile("(@APVAR.*?)\\.");

	private void createPlaceHolderTags(Step<? extends Tag> step){
		if(step.getMsgType().equalsIgnoreCase("FixMsg") || step.getMsgType().equals("XML")) {
			try {
				LFTemplate template = (step.getTemplate() != null) ? DBUtil.getInstance().getTem().getAllTemplateMap().get(step.getTemplate()) : null;
				String msg = (template != null) ? DBUtil.getInstance().getTem().getAllTemplateMap().get(step.getTemplate()).getMsgTemplate() : step.getMessage();

				ArrayList<String> completeList = new ArrayList<String>();


				// Tags from step common overwrite tags
				if(step.getCommonTags() != null) {
					List<LFCommonOverwriteTag> tags = commonOverwriteTagMap.get(step.getCommonTags());

					for(Tag tag : tags) {
						Matcher m = varPattern.matcher(tag.getTagValue());
						while (m.find()) {
							String strPlaceholderpattern = m.group(1);
							if(!completeList.contains(strPlaceholderpattern)) {
								completeList.add(strPlaceholderpattern);
							}
						}
					}
				}


				// Tags from template common overwrite tags
				if(template != null && template.getCommonOverwriteTagListName() != null && !template.getCommonOverwriteTagListName().equals(AutoPilotConstants.ComboBoxEmptyItem)) {
					List<LFCommonOverwriteTag> tags = commonOverwriteTagMap.get(template.getCommonOverwriteTagListName());

					for(Tag tag : tags) {
						Matcher m = varPattern.matcher(tag.getTagValue());
						while (m.find()) {
							String strPlaceholderpattern = m.group(1);
							if(!completeList.contains(strPlaceholderpattern)) {
								completeList.add(strPlaceholderpattern);
							}
						}
					}
				}

				// Tags from template
				Matcher m = varPattern.matcher(msg);

				while (m.find()) {
					String strPlaceholderpattern = m.group(1);
					if(!completeList.contains(strPlaceholderpattern)) {
						completeList.add(strPlaceholderpattern);
					}
				}

				for(String token : completeList){
					if (step instanceof LFTestInputSteps) {
						LFTag tag = new LFTag(token, "");
						tag.setActionSequence(step.getActionSequence());
						tag.setTestID(testcase.getTestID());
						if (!inputStepTableModel.getRow(inputRowPointer).getInputTagsValueList().contains(tag)) {
							inputStepTableModel.getRow(inputRowPointer).getInputTagsValueList().add(tag);
						}
					} else if (step instanceof LFOutputMsg) {
						LFOutputTag tag = new LFOutputTag(token, "");
						tag.setOutputMsgID(outputRowPointer + 1);
						tag.setActionSequence(step.getActionSequence());
						tag.setTestID(testcase.getTestID());
						if (!outputStepTableModel.getRow(outputRowPointer).getOutputTagList().contains(tag)) {
							outputStepTableModel.getRow(outputRowPointer).getOutputTagList().add(tag);
						}
					}
				}
			} catch(Exception e) {
				System.out.println("Error occured when trying to extract input placholder tags: ");
				e.printStackTrace();
			}
		}

		inputStepTableModel.fireTableDataChanged();
		outputStepTableModel.fireTableDataChanged();
	}

	class TagStyle {
		String tagID = "";
		String tagValue = "";
		String style = "";

		@Override public boolean equals(Object arg0) {
			boolean result = false;
			if (arg0 instanceof TagStyle) {
				TagStyle other = (TagStyle) arg0;
				result = (other.tagID.equals(tagID));
			}
			return result;
		}
	}


	public void format(Step<? extends Tag> step, StyledDocument doc){
		String msg;
		String type;
		List<? extends Tag> commonTagsStep = null;
		List<? extends Tag> commonTagsTempl = null;

		// Get template msg and common template tags
		if(step.getTemplate() != null) {
			LFTemplate template = templateMap.get(step.getTemplate());
			if(template == null) {
				return;
			}
			msg = template.getMsgTemplate();
			type = template.getMsgType();

			// Template common tags
			if(template.getCommonOverwriteTagListName() != null && !template.getCommonOverwriteTagListName().equals(AutoPilotConstants.ComboBoxEmptyItem)) {
				commonTagsTempl = commonOverwriteTagMap.get(template.getCommonOverwriteTagListName());
			}
			// Otherwise get msg from the step
		} else {
			msg = step.getMessage();
			// Since output steps don't record msg type, make the assumption it is FIX
			if(step instanceof LFOutputMsg) {
				type = AutoPilotConstants.MSG_TYPE_FIXMSG;
			} else {
				type = step.getMsgType();
			}
		}

		// Step common tags
		if(step.getCommonTags() != null) {
			commonTagsStep = commonOverwriteTagMap.get(step.getCommonTags());
		}

		if (type != null) {
			if (type.equals(AutoPilotConstants.MSG_TYPE_FIXMSG)) {
				ArrayList<TagStyle> styles = new ArrayList<TagStyle>();
				msg = msg.replace("^A", AutoPilotConstants.FIX_SEPERATOR);
				msg = msg.replace("\n", "");
				String[] splits = msg.split(AutoPilotConstants.FIX_SEPERATOR);

				// Set everything to regular style
				for (int i = 0; i < splits.length; i++) {
					if (!splits[i].equals("")) {
						String[] bits = splits[i].split("=");
						TagStyle temp = new TagStyle();

						if (bits.length > 0) {
							temp.tagID = bits[0];
							temp.tagValue = bits.length < 2 ? "EMPTY" : bits[1];
							temp.style = "regular";
							styles.add(temp);
						}
					}
				}

				overwriteTags(styles, step.getTags(), "overwritten_tag");
				overwriteTags(styles, commonTagsStep, "common_tag_step");
				overwriteTags(styles, commonTagsTempl, "common_tag_tmpl");

				try {
					doc.remove(0, doc.getLength());
					for (TagStyle style : styles) {
						doc.insertString(doc.getLength(), style.tagID + "=" + style.tagValue + "\n", doc.getStyle(style.style));
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}

				highlight((step instanceof LFTestInputSteps));
			} else if (type.equals(AutoPilotConstants.MSG_TYPE_XML) || type.equals(AutoPilotConstants.MSG_TYPE_CONFIG)) {
				if (type.equals(AutoPilotConstants.MSG_TYPE_CONFIG)) {
					int beginIndex = msg.indexOf("=<?") + 1;
					int endIndex = msg.lastIndexOf(">") + 1;
					msg = msg.substring(beginIndex, endIndex);
				}

				try {
					Transformer transformer = TransformerFactory.newInstance().newTransformer();
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					if(TestCaseController.OMIT_XML_DECLARATION){
						transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
					}

					StreamResult result = new StreamResult(new StringWriter());
					StreamSource source = new StreamSource(new StringReader(msg));

					transformer.transform(source, result);

					msg = result.getWriter().toString();

					doc.remove(0, doc.getLength());
					doc.insertString(doc.getLength(), msg, doc.getStyle("regular"));
				} catch (TransformerException e) {
					e.printStackTrace();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			} else {
				try {
					doc.remove(0, doc.getLength());
					doc.insertString(doc.getLength(), msg, doc.getStyle("regular"));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void highlight(boolean isInput) {
		try {	
			JTextField field;
			JTextPane msg;

			if(isInput) {
				msg = ApplicationContext.getTestcasePanel().getTextArea(1);
				field = ApplicationContext.getTestcasePanel().getHighlightField(1);
			} else {
				msg = ApplicationContext.getTestcasePanel().getTextArea(2);
				field = ApplicationContext.getTestcasePanel().getHighlightField(2);
			}


			// Remove current highlights
			Highlighter hilite =  msg.getHighlighter();
			hilite.removeAllHighlights();

			String[] highlights = field.getText().split(";");
			String[] message = msg.getStyledDocument().getText(0, msg.getStyledDocument().getLength()).split("\n");

			for(String highlight : highlights) {
				for(String token : message) {
					if(token.startsWith(highlight+"=")) {

						int startPos = msg.getStyledDocument().getText(0, msg.getStyledDocument().getLength()).indexOf(token);
						int endPos = startPos + token.length();
						hilite.addHighlight(startPos, endPos, highlighPainter);

					}
				}
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void overwriteTags(List<TagStyle> styles, List<? extends Tag> tags, String styleName) {
		if(tags != null) {
			for(Tag over : tags) {
				TagStyle overwrite = new TagStyle();
				overwrite.tagID = over.getTagID();
				overwrite.tagValue = "[" + over.getTagValue() + "]";

				if("@UNSET".equals(over.getTagValue())) {
					overwrite.style = styleName+"_unset";
				} else {
					overwrite.style = styleName;
				}

				// If tag is already in list, then replace it
				if(styles.contains(overwrite)) {
					int index = styles.indexOf(overwrite);
					TagStyle temp = styles.get(index);
					if(temp.style.equals("regular")) {
						overwrite.tagValue = temp.tagValue + " " + overwrite.tagValue;
						styles.remove(index);
						styles.add(index, overwrite);
					}
				} else {
					styles.add(overwrite);
				}
			}
		}
	}

	public void updateInputStep(LFTestInputSteps inputStep) {
		inputStepTableModel.updateRow(inputStep.getActionSequence()-1, inputStep);
		setInputStepTableRowPointer(inputStep.getActionSequence()-1);
		createPlaceHolderTags(inputStep);
	}

	public void updateOutputStep(LFOutputMsg outputStep) {
		outputStepTableModel.updateRow(outputStep.getOutputMsgID()-1, outputStep);
		setOutputStepTableRowPointer(outputStep.getOutputMsgID()-1);
		createPlaceHolderTags(outputStep);
	}

	public void editOutputStep(int row) {
		OutputStepPanel outputStepPanel = ApplicationContext.getOutputStepPanel();
		outputStepPanel.populateForm(outputStepTableModel.getRow(row), false);
		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showOutputStepPanel();
	}

	public void editInputStep(int row) {
		InputStepPanel inputStepPanel = ApplicationContext.getInputStepPanel();
		inputStepPanel.populateForm(inputStepTableModel.getRow(row), false);
		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showInputStepPanel();
	}	

	public void addInputStep(int row) {
		if(row == -1) {
			// Add row to the end of the input step table
			row = inputStepTableModel.getRowCount();
		}

		// Create new input step
		LFTestInputSteps inputStep = new LFTestInputSteps();
		inputStep.setTestID(testcase.getTestID());
		inputStep.setActionSequence(row+1);

		inputStepTableModel.insertRow(inputStep);

		refractor(row, -1, true);

		InputStepPanel inputStepPanel = ApplicationContext.getInputStepPanel();
		inputStepPanel.populateForm(inputStep, true);
		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showInputStepPanel();
	}

	public void addInputStepSmartTag(int selectedInputStepIdx){
		//		SmartTagPanel smartTagPanel = ApplicationContext.getSmartTagPanel();
		//		
		//		LFTestInputSteps inputStep = inputStepTableModel.getRow(selectedInputStepIdx);
		//		smartTagPanel.populateForm(inputStep, true);
		//		
		//		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showSmartTagPanel();

		JFrame mainFrame = AutoPilotAppl.getApplication().getMainFrame();
		SmartTagDialog smartTagDialog = new SmartTagDialog (mainFrame, false);
		LFTestInputSteps inputStep = inputStepTableModel.getRow(selectedInputStepIdx);
		smartTagDialog.populateForm(inputStep);
		smartTagDialog.setLocationRelativeTo(mainFrame);
		AutoPilotAppl.getApplication().show(smartTagDialog);

		//		SmartTagPanel smartTagPanel = ApplicationContext.getSmartTagPanel();
		//		LFTestInputSteps inputStep = inputStepTableModel.getRow(selectedInputStepIdx);
		//		smartTagPanel.populateForm(inputStep, true);
		//		new SmartTagView();
	}

	public void addOutputputStepSmartTag(int selectedInputStepIdx, int selectedOutputStepIdx) {

		//		SmartTagPanel smartTagPanel = ApplicationContext.getSmartTagPanel();
		//
		//		//LFTestInputSteps inputStep = inputStepTableModel.getRow(selectedInputStepIdx);
		//		LFOutputMsg outputStep = outputStepTableModel.getRow(selectedOutputStepIdx);
		//		
		//		smartTagPanel.populateForm(outputStep, false);
		//
		//		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showSmartTagPanel();

		JFrame mainFrame = AutoPilotAppl.getApplication().getMainFrame();
		SmartTagDialog smartTagDialog = new SmartTagDialog (mainFrame, false);
		LFOutputMsg outputStep = outputStepTableModel.getRow(selectedOutputStepIdx);
		smartTagDialog.populateForm(outputStep);
		smartTagDialog.setLocationRelativeTo(mainFrame);
		AutoPilotAppl.getApplication().show(smartTagDialog);

	}

	public void addStepLink(int inputStep, int outputStep){

		JFrame mainFrame = AutoPilotAppl.getApplication().getMainFrame();
		StepLinkDialog stepLinkDialog = new StepLinkDialog (mainFrame, false, inputStep, outputStep);
		stepLinkDialog.populateForm(testcase, false);
		stepLinkDialog.setLocationRelativeTo(mainFrame);
		AutoPilotAppl.getApplication().show(stepLinkDialog);


	}

	public void addOutputStep(int inputRow, int row) {
		if(row == -1) {
			// Add row to the end of the output step table
			row = outputStepTableModel.getRowCount();
		}

		LFOutputMsg outputStep = new LFOutputMsg ();
		outputStep.setActionSequence(inputRow+1);
		outputStep.setOutputMsgID(row+1);
		outputStep.setTestID(testcase.getTestID());

		outputStepTableModel.insertRow(outputStep);

		refractor(inputRow, row, true);

		OutputStepPanel outputStepPanel = ApplicationContext.getOutputStepPanel();
		outputStepPanel.populateForm(outputStep, true);
		((AutoPilotApplView)AutoPilotAppl.getApplication().getMainView()).showOutputStepPanel();
	}

	public InputStepTableModel getInputStepModel() {
		return inputStepTableModel;
	}

	public TagTableModel<LFTag> getInputTagModel() {
		return inputTagTableModel;
	}

	public OutputStepTableModel getOutputStepModel() {
		return outputStepTableModel;
	}

	public TagTableModel<LFOutputTag> getOutputTagModel() {
		return outputTagTableModel;
	}

	public TagTableModel<LFTag> getValidationTagModel(){
		return validationTagTableModel;
	}

	public TagTableModel<LFTag> getValidationTemplateTableModel(){
		return validationTemplateTableModel;
	}

	public RepeatingGroupTableModel<Tag> getRepeatingGroupTagTableModel(){
		return repeatingGroupTagTableModel;
	}

	public LFTestCase getTestcaseClone() {
		LFTestCase testcaseClone = testcase.clone(Util.getTestIDSequencer());
		//LFTestCase testcaseClone = (LFTestCase) testcase.clone();
		return testcaseClone;
	}

	// If a step is added/removed then @IP[x] and @OP[x][y] might have to be
	// increase/decreased
	private void refractor(int inputRow, int outputRow, boolean added) {
		List<LFTestInputSteps> inputs = inputStepTableModel.getData();

		// If an output step was changed then first check the output steps above that one and refractor OP tags
		if(outputRow > -1) {
			List<LFOutputMsg> outputs = inputs.get(inputRow).getOutputStepList();

			for(int outPointer = outputRow; outPointer < outputs.size(); outPointer++) {
				List<LFOutputTag> outputTags = outputs.get(outPointer).getOutputTagList();

				for(LFOutputTag tag : outputTags) {
					// Increase the number as stored in array 0..., whilst user looks at them from 1...
					refractorTag(tag, inputRow+1, outputRow+1, added);
				}
			}
		}

		// look at each input and output stage above inputRow and refractor IP and OP tags
		for(int inPointer = inputRow+1; inPointer < inputs.size(); inPointer++) {
			List<LFTag> inputTags = inputs.get(inPointer).getInputTagsValueList();

			for(LFTag tag : inputTags) {
				// Increase the number as stored in array 0..., whilst user looks at them from 1...
				refractorTag(tag, inputRow+1, outputRow==-1?-1:outputRow+1, added);
			}

			List<LFOutputMsg> outputs = inputs.get(inPointer).getOutputStepList();

			for(LFOutputMsg output : outputs) {
				List<LFOutputTag> outputTags = output.getOutputTagList();

				for(LFOutputTag tag : outputTags) {
					// Increase the number as stored in array 0..., whilst user looks at them from 1...
					refractorTag(tag, inputRow+1, outputRow==-1?-1:outputRow+1, added);
				}
			}
		}
	}

	private void refractorTag(Tag tag, int inputRow, int outputRow, boolean added) {
		// IP[y]
		// OP[y][z]

		// deals with IP[y]
		if (outputRow < 0 && tag.getTagValue().startsWith(AutoPilotConstants.PLACEHOLDER_INPUT)) {
			int indexStart = tag.getTagValue().indexOf(AutoPilotConstants.SEPERATOR_ARRAY_START);
			int indexEnd = tag.getTagValue().indexOf(AutoPilotConstants.SEPERATOR_ARRAY_END);
			int inputStep = Integer.parseInt(tag.getTagValue().substring(indexStart+1, indexEnd));

			// add input row at position x => if y >= x then y++
			// remove input row a position x => if y >= x then y-- (y == x becomes invalid)
			if(inputStep >= inputRow) {
				if(added) {
					inputStep++;
				} else {
					if(inputStep > inputRow) {
						inputStep--;
					} else {
						inputStep = -1;
					}
				}
				tag.setTagValue("@IP["+inputStep+"]"+tag.getTagValue().substring(indexEnd+1));
			}
			// deals with OP[y][z]
		} else if(tag.getTagValue().startsWith(AutoPilotConstants.PLACEHOLDER_OUTPUT)) {
			int indexStart = tag.getTagValue().indexOf(AutoPilotConstants.SEPERATOR_ARRAY_START);
			int indexEnd = tag.getTagValue().indexOf(AutoPilotConstants.SEPERATOR_ARRAY_END);
			int inputStep = Integer.parseInt(tag.getTagValue().substring(indexStart+1, indexEnd));

			indexStart = tag.getTagValue().indexOf(AutoPilotConstants.SEPERATOR_ARRAY_START, indexEnd+1);
			indexEnd = tag.getTagValue().indexOf(AutoPilotConstants.SEPERATOR_ARRAY_END, indexEnd+1);
			int outputStep = Integer.parseInt(tag.getTagValue().substring(indexStart+1, indexEnd));

			if(inputStep >= inputRow) {
				// only when y changes
				if(outputRow < 0) {
					if(added) {
						inputStep++;
					} else {
						if(inputStep >= inputRow) {
							inputStep--;
						} else {
							inputStep = -1;
						}
					}
				}

				// only when z changes
				if(outputRow > -1) {
					if(inputStep == inputRow) {
						if(added) {
							if(outputStep >= outputRow) {
								outputStep++;
							}
						} else {
							if(outputStep >= outputRow) {
								outputStep--;
								if(outputStep == 0) {
									outputStep = -1;
								}
							}
						}
					}
				}

				tag.setTagValue("@OP["+inputStep+"]["+outputStep+"]"+tag.getTagValue().substring(indexEnd+1));
			}
		}
	}




}
