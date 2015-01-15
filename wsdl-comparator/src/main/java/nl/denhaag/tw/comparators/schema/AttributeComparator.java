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

import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.absItf.AbsItfAttribute.Use;
import org.ow2.easywsdl.schema.impl.AttributeImpl;
import org.ow2.easywsdl.schema.impl.SimpleTypeImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Attribute;





public class AttributeComparator extends AbstractSchemaElementComparator<AttributeImpl> {

	public AttributeComparator(AttributeImpl oldObject, AttributeImpl newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		compareAttributes(result, getOldObject(), getNewObject());
		super.analyzeChangedObject(result);
		if (result.getChildren().size() > 0){
			result.init(Compatibility.UNKNOWN,  "Attribute " + getNewObject().getName() );
		}
		return result;
	}

	protected ChangedResult compareAttributes(ChangedResult result, AttributeImpl oldAttribute, AttributeImpl newAttribute) {
		Attribute oldObject = oldAttribute.getModel();
		Attribute newObject = newAttribute.getModel();		
		if (!isEqual(oldAttribute.getUse(), newAttribute.getUse())) {
			if (Use.REQUIRED.equals(oldAttribute.getUse()) && Use.OPTIONAL.equals(newAttribute.getUse())){
				result.getChildren().add(new ChangedResult(Compatibility.NOTBREAKS, "Use", oldAttribute
						.getUse() +"",newAttribute.getUse()+""));				
			}else {
				result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Use", oldAttribute
					.getUse()+"", newAttribute.getUse()+""));
			}
		}
		
		if (!isEqual(oldObject.getFixed(), newObject.getFixed())) {
			result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Fixed", oldObject
					.getFixed(), newObject.getFixed()));
		}
		if (!isEqual(oldObject.getDefault(), newObject.getDefault())) {
			result.getChildren().add(new ChangedResult(Compatibility.NOTBREAKS, "Default", oldObject
					.getDefault(), newObject.getDefault()));
		}	
		if (oldObject.getSimpleType() != null || newObject.getSimpleType() != null) {
			SimpleType oldSimpleType = null;
			SimpleType newSimpleType = null;
			if (oldObject.getSimpleType() != null){
				oldSimpleType = new SimpleTypeImpl(oldObject.getSimpleType(), getOldObject());
			}
			if (newObject.getSimpleType() != null){
				newSimpleType = new SimpleTypeImpl(newObject.getSimpleType(), getNewObject());
			}
			result.addChild(new SimpleTypeComparator(oldSimpleType, newSimpleType).compare());
		}
		if (oldObject.getRef() != null && newObject.getRef() != null) {
			AttributeImpl oldAttr = (AttributeImpl) getOldObject().getSchema().getAttribute(oldObject.getRef());
			AttributeImpl newAttr = (AttributeImpl) getNewObject().getSchema().getAttribute(newObject.getRef());
			compareAttributes(result, oldAttr, newAttr);
		}	
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.BREAKS, "Attribute", getNewObject().getName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Attribute", getOldObject().getName());
		return result;
	}

	protected boolean isEqual(Use value, Use value1) {
		if (value != null){
			return value.equals(value1);
		}else if (value1 != null){
			return value1.equals(value);
		}else {
			return true;
		}
	}
}
