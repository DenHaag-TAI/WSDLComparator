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
import nl.denhaag.tw.comparators.schema.notsupported.GroupComparator;

import org.ow2.easywsdl.schema.api.Choice;
import org.ow2.easywsdl.schema.api.Sequence;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.impl.ComplexContentImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.ComplexRestrictionType;



public class ComplexRestrictionTypeComparator extends AbstractSchemaElementComparator<ComplexContentImpl> {


	private boolean baseType;
	public ComplexRestrictionTypeComparator(ComplexContentImpl oldObject, ComplexContentImpl newObject) {
		super(oldObject, newObject);
	}
	public ComplexRestrictionTypeComparator(ComplexContentImpl oldObject, ComplexContentImpl newObject, boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		ComplexRestrictionType oldComplexRestrictionType = getOldObject().getModel().getRestriction();
		ComplexRestrictionType newComplexRestrictionType = getNewObject().getModel().getRestriction();
		Type oldBaseType = (Type) getOldObject().getSchema().getType(oldComplexRestrictionType.getBase());
		Type newBaseType = (Type) getNewObject().getSchema().getType(newComplexRestrictionType.getBase());
		result.addChild(new TypeComparator(oldBaseType, newBaseType).compare());
		/*
		 * sequence
		 */
		Sequence oldSequence = SchemaUtil.convertToSequence (getOldObject(), oldComplexRestrictionType.getSequence());
		Sequence newSequence = SchemaUtil.convertToSequence (getNewObject(), newComplexRestrictionType.getSequence());
		if (oldSequence != null &&  newSequence != null){
			result.addChild(new SequenceComparator(oldSequence,newSequence).compare());
		}

		/*
		 * choice
		 */
		Choice oldChoice = SchemaUtil.convertToChoice(getOldObject(), oldComplexRestrictionType.getChoice());
		Choice newChoice = SchemaUtil.convertToChoice(getNewObject(), newComplexRestrictionType.getChoice());
		if (oldChoice != null || newChoice != null) {
			result.addChild(new ChoiceComparator(oldChoice, newChoice, baseType).compare());
		}
		/*
		 * attributes
		 */
		AttributeUtil.compareAttributesAndAttributeGroups(result,oldComplexRestrictionType.getAttributeOrAttributeGroup(), newComplexRestrictionType.getAttributeOrAttributeGroup(), getOldObject(),getNewObject());
		/*
		 * Group
		 */
		if (oldComplexRestrictionType.getGroup() != null || newComplexRestrictionType.getGroup() != null ){
			result.addChild(new GroupComparator(oldComplexRestrictionType.getGroup(), newComplexRestrictionType.getGroup()).compare());
		}
		if (result.getChildren().size() > 0){
				result.init(Compatibility.UNKNOWN,  "Restriction with base");
		}	
		return result;
	}


	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Restriction with base");
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Restriction with base");
		return result;
	}


}
