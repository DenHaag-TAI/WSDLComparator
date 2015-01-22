package nl.denhaag.tw.comparators.schema;

/*
 * #%L
 * wsdl-comparator
 * %%
 * Copyright (C) 2012 - 2013 Team Webservices (Gemeente Den Haag)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;
import nl.denhaag.tw.comparators.schema.notsupported.AllComparator;
import nl.denhaag.tw.comparators.schema.notsupported.GroupComparator;

import org.ow2.easywsdl.schema.impl.ComplexContentImpl;
import org.ow2.easywsdl.schema.impl.ComplexTypeImpl;
import org.ow2.easywsdl.schema.impl.SimpleContentImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.ComplexType;

public class ComplexTypeComparator extends AbstractTypeComparator<ComplexTypeImpl> {

	public ComplexTypeComparator(ComplexTypeImpl oldObject, ComplexTypeImpl newObject) {
		super(oldObject, newObject);
	}
	public ComplexTypeComparator(ComplexTypeImpl oldObject, ComplexTypeImpl newObject, boolean baseType) {
		super(oldObject, newObject, baseType);
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		ComplexType oldObject = getOldObject().getModel();
		ComplexType newObject = getNewObject().getModel();
		/*
		 * attributes
		 */
		AttributeUtil.compareAttributesAndAttributeGroups(result, oldObject.getAttributeOrAttributeGroup(), newObject.getAttributeOrAttributeGroup(), getOldObject(), getNewObject());
		/*
		 * check for mixed
		 */
		
		if (getOldObject().getModel().isMixed() != getNewObject().getModel().isMixed()) {
			result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Attribute mixed ", getOldObject().getModel().isMixed(), getNewObject().getModel().isMixed()));
		}	
		/*
		 * check for abstract
		 */
		
		if (getOldObject().getModel().isAbstract() != getNewObject().getModel().isAbstract()) {
			result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Attribute abstract ", getOldObject().getModel().isMixed(), getNewObject().getModel().isMixed()));
		}			
		/*
		 * sequence
		 */
		if (getOldObject().hasSequence() || getNewObject().hasSequence()){
			result.addChild(new SequenceComparator(getOldObject().getSequence(), getNewObject().getSequence(), isBaseType()).compare());
		}
		/*
		 * choice
		 */
		if (getOldObject().hasChoice() || getNewObject().hasChoice()){
			result.addChild(new ChoiceComparator(getOldObject().getChoice(), getNewObject().getChoice()).compare());
		}
		/*
		 * ComplexContent
		 */
		if (getOldObject().hasComplexContent() || getNewObject().hasComplexContent()){
			result.addChild(new ComplexContentComparator((ComplexContentImpl)getOldObject().getComplexContent(), (ComplexContentImpl) getNewObject().getComplexContent(), isBaseType()).compare());
		}
		/*
		 * SimpleContent
		 */
		if (getOldObject().hasSimpleContent() || getNewObject().hasSimpleContent()){
			result.addChild(new SimpleContentComparator((SimpleContentImpl)getOldObject().getSimpleContent(), (SimpleContentImpl) getNewObject().getSimpleContent(), isBaseType()).compare());

		}
		/*
		 * All
		 */
		if (getOldObject().getAll() != null || getNewObject().getAll() != null ){
			result.addChild(new AllComparator(getOldObject().getAll(), getNewObject().getAll()).compare());
		}	
		/*
		 * Group
		 */
		if (getOldObject().getModel().getGroup() != null || getNewObject().getModel().getGroup() != null ){
			result.addChild(new GroupComparator(getOldObject().getModel().getGroup(), getNewObject().getModel().getGroup()).compare());
		}			
		if (result.getChildren().size() > 0){
			if (getNewObject().getQName() == null){
				result.init(Compatibility.UNKNOWN,  "Anonymous ComplexType");
			}else {
				result.init(Compatibility.UNKNOWN,  "ComplexType", getOldObject().getQName(), getNewObject().getQName());
			}
		}		
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		if (getNewObject().getQName() == null){
			result.init(Compatibility.BREAKS,  "Anonymous ComplexType");
		}else {
			result.init(Compatibility.BREAKS,  "ComplexType", getNewObject().getQName());
		}		
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		if (getOldObject().getQName() == null){
			result.init(Compatibility.BREAKS,  "Anonymous ComplexType");
		}else {
			result.init(Compatibility.BREAKS,  "ComplexType", getOldObject().getQName());
		}			
		return result;
	}

}
