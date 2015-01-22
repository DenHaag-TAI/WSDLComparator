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

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;

import org.ow2.easywsdl.schema.api.absItf.AbsItfImport;
import org.ow2.easywsdl.schema.api.absItf.AbsItfSchema;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.impl.SchemaImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Annotated;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.AttributeGroup;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.NamedAttributeGroup;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Schema;





public class AttributeGroupComparator extends AbstractOpenAttrsComparator<AttributeGroup> {

	private AbstractSchemaElementImpl oldParent;
	private AbstractSchemaElementImpl newParent;
	public AttributeGroupComparator(AttributeGroup oldObject, AttributeGroup newObject, AbstractSchemaElementImpl oldParent, AbstractSchemaElementImpl newParent) {
		super(oldObject, newObject);
		this.oldParent = oldParent;
		this.newParent = newParent;
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		compareAttributeGroups(result,getOldObject(), getNewObject());
		super.analyzeChangedObject(result);
		if (result.getChildren().size() > 0){
			if (getNewObject().getRef() != null){
				result.init(Compatibility.UNKNOWN, "AttributeGroup", getOldObject().getRef(), getNewObject().getRef());
			}else {
				result.init(Compatibility.UNKNOWN, "AttributeGroup", getOldObject().getName(), getNewObject().getName());
			}			
		}
		return result;
	}
	protected ChangedResult compareAttributeGroups(ChangedResult result, AttributeGroup oldAttributeGroup, AttributeGroup newAttributeGroup) {
		if (oldAttributeGroup.getRef() != null || newAttributeGroup.getRef() != null) {
			NamedAttributeGroup oldNamedGroup = getNamedAttributeGroup(oldAttributeGroup, oldParent);
			NamedAttributeGroup newNamedGroup = getNamedAttributeGroup(newAttributeGroup, newParent);
			compareAttributeGroups(result, oldNamedGroup, newNamedGroup);
		}else {
			AttributeUtil.compareAttributesAndAttributeGroups(result, oldAttributeGroup.getAttributeOrAttributeGroup(), newAttributeGroup.getAttributeOrAttributeGroup(), oldParent, newParent);
		}
		return result;
	}
	private NamedAttributeGroup getNamedAttributeGroup( AttributeGroup attributeGroup, AbstractSchemaElementImpl parent){
		AbsItfSchema schema = parent.getSchema();
		String namespaceURI = attributeGroup.getRef().getNamespaceURI();
		if (schema.getTargetNamespace().equals(namespaceURI)){
			return getNamedAttributeGroup(namespaceURI, schema);
		}else {
			List<AbsItfImport<AbsItfSchema>> list = schema.getImports(namespaceURI);
			for (AbsItfImport<AbsItfSchema> item: list){
				NamedAttributeGroup group =  getNamedAttributeGroup(namespaceURI, item.getSchema());
				if (group != null){
					return group;
				}
	
			}
		}
		return null;
	}
	private NamedAttributeGroup getNamedAttributeGroup(String namespaceURI, AbsItfSchema schema){
		SchemaImpl schemaImpl = (SchemaImpl) schema;
		Schema schemaModel = schemaImpl.getModel();
		for (Annotated annotated : schemaModel.getSimpleTypeOrComplexTypeOrGroup()){
			if (annotated instanceof NamedAttributeGroup){
				return (NamedAttributeGroup) annotated;
			}
			
		}
		return null;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		if (getNewObject().getRef() != null){
			result.init(Compatibility.BREAKS, "AttributeGroup", getNewObject().getRef());
		}else {
			result.init(Compatibility.BREAKS, "AttributeGroup", getNewObject().getName());
		}
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		if (getNewObject().getRef() != null){
			result.init(Compatibility.BREAKS, "AttributeGroup", getOldObject().getRef());
		}else {
			result.init(Compatibility.BREAKS, "AttributeGroup", getOldObject().getName());
		}		
		return result;
	}

}
