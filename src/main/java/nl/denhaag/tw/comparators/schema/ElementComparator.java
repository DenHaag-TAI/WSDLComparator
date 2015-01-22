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


import java.util.List;

import javax.xml.namespace.QName;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.InvalidResult;
import nl.denhaag.tw.comparators.result.RemovedResult;

import org.apache.commons.lang.StringUtils;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.api.absItf.AbsItfImport;
import org.ow2.easywsdl.schema.api.absItf.AbsItfSchema;
import org.ow2.easywsdl.schema.impl.ElementImpl;
import org.ow2.easywsdl.schema.impl.SchemaImpl;

public class ElementComparator extends AbstractSchemaElementComparator<Element> {

	private boolean baseType;
	public ElementComparator(Element oldObject, Element newObject) {
		super(oldObject, newObject);
	}	
	public ElementComparator(Element oldObject, Element newObject, boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		if (getOldObject().getMinOccurs() < getNewObject().getMinOccurs()) {
			result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Attribute minOccurs", getOldObject()
					.getMinOccurs(), getNewObject().getMinOccurs()));
		} else if (getOldObject().getMinOccurs() > getNewObject().getMinOccurs()) {
			result.getChildren().add(new ChangedResult(Compatibility.NOTBREAKS, "Attribute minOccurs", getOldObject()
					.getMinOccurs(), getNewObject().getMinOccurs()));
		}
		if (!getOldObject().getMaxOccurs().equals(getNewObject().getMaxOccurs())) {
			int oldMaxOccurs = -1;
			int newMaxOccurs = -1;
			if (StringUtils.isNumeric(getOldObject().getMaxOccurs())){
				oldMaxOccurs = Integer.parseInt(getOldObject().getMaxOccurs());
			}
			if (StringUtils.isNumeric(getNewObject().getMaxOccurs())){
				newMaxOccurs = Integer.parseInt(getNewObject().getMaxOccurs());
			}
			if (oldMaxOccurs == -1 && newMaxOccurs >= 0){
				result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Attribute maxOccurs",getOldObject()
						.getMaxOccurs(), getNewObject().getMaxOccurs()));				
			}else if (oldMaxOccurs >= 0 && newMaxOccurs == -1){
				result.getChildren().add(new ChangedResult(Compatibility.NOTBREAKS, "Attribute maxOccurs",  getOldObject()
						.getMaxOccurs(),getNewObject().getMaxOccurs()));				
			}else if (oldMaxOccurs > newMaxOccurs ){
				result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Attribute maxOccurs", getOldObject()
						.getMaxOccurs(), getNewObject().getMaxOccurs()));				
			}else if (oldMaxOccurs < newMaxOccurs ){
				result.getChildren().add(new ChangedResult(Compatibility.NOTBREAKS, "Attribute maxOccurs", getOldObject()
						.getMaxOccurs(), getNewObject().getMaxOccurs()));
				
			}
		}
		if (getOldObject().isNillable() != getNewObject().isNillable()) {
			result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Attribute nillable", getOldObject()
					.isNillable(), getNewObject().isNillable()));
		} 			
		/*
		 * TODO: add more comparators
		 */
		super.analyzeChangedObject(result);
		// strange hack
		getOldObject().getQName();
		getNewObject().getQName();
		//strange hack
		if (getOldObject().getType() != null ||  getNewObject().getType() != null){
			result.addChild(new TypeComparator(getOldObject().getType(), getNewObject().getType()).compare());
		}else if (getOldObject().getRef() != null && getNewObject().getRef() != null){
			Element oldElement = getGlobalElement( getOldObject());
			Element newElement = getGlobalElement( getNewObject());
			result.addChild(new TypeComparator(oldElement.getType(), newElement.getType()).compare());
			
		}else {
			if (getOldObject().getType() == null){
				result.addChild(new InvalidResult("Element(Old WSDL) has no type or ref", getOldObject().getQName()));
			}
			if (getNewObject().getType() == null){
				result.addChild(new InvalidResult("Element(New WSDL) has no type or ref", getNewObject().getQName()));
			}			
		}
		if (isEqual(getOldObject().getQName(),getNewObject().getQName())){
			if (result.getChildren().size() > 0){
				result.init(Compatibility.UNKNOWN,  "Element", getOldObject().getQName(), getNewObject().getQName());
			}			
		}else {
			result.init(Compatibility.BREAKS, "Element" , getOldObject().getQName(), getNewObject().getQName());
		}


		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		if (getNewObject().getMinOccurs() == 0) {
			if (baseType){
				result.init(Compatibility.BREAKS, "Optional element", getNewObject().getQName());
			}else {
				result.init(Compatibility.NOTBREAKS, "Optional element", getNewObject().getQName());
			}
		}
		else {
			result.init(Compatibility.BREAKS, "Element", getNewObject().getQName());
		}				
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Element ", getOldObject().getQName());
		return result;
	}

	private Element getGlobalElement(Element element){
		QName ref = element.getRef();
		if (ref != null){
			ElementImpl elementImpl = (ElementImpl) element;
			AbsItfSchema schema = elementImpl.getSchema();
			List<Element> globalElements = schema.findElementsInAllSchema(ref);
			for (Element globalElement: globalElements){
				if (ref.equals(globalElement.getQName())){
					return element;
				}
			}
		}
		return null;
	}
}
