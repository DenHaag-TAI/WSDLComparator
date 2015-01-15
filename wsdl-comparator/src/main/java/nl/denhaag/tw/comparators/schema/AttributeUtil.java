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

import nl.denhaag.tw.comparators.result.ChangedResult;

import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.impl.AttributeImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Annotated;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Attribute;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.AttributeGroup;

public class AttributeUtil {
	public static void compareAttributesAndAttributeGroups(ChangedResult result, List<Annotated> oldAttributeOrAttributeGroup, List<Annotated> newAttributeOrAttributeGroup, AbstractSchemaElementImpl oldObject, AbstractSchemaElementImpl newObject){
		for (Annotated oldAnnotated : oldAttributeOrAttributeGroup) {
			if (oldAnnotated instanceof Attribute) {
				Attribute oldAttribute = (Attribute) oldAnnotated;
				Attribute newAttribute = getAttributeFromAnnotatedList(newAttributeOrAttributeGroup, oldAttribute);
				AttributeImpl oldAttributeImpl = new AttributeImpl(oldAttribute, oldObject);
				AttributeImpl newAttributeImpl = null;
				if (newAttribute != null){
					newAttributeImpl = new AttributeImpl(newAttribute, newObject);
				}
				result.addChild(new AttributeComparator(oldAttributeImpl, newAttributeImpl).compare());
			}else if (oldAnnotated instanceof AttributeGroup){
				AttributeGroup oldAttributeGroup = (AttributeGroup) oldAnnotated;
				AttributeGroup newAttributeGroup = getAttributeGroupFromAnnotatedList(newAttributeOrAttributeGroup, oldAttributeGroup);
				result.addChild(new AttributeGroupComparator(oldAttributeGroup, newAttributeGroup, oldObject, newObject).compare());
			}
		}
		for (Annotated newAnnotated : newAttributeOrAttributeGroup) {
			if (newAnnotated instanceof Attribute) {
				Attribute newAttribute = (Attribute) newAnnotated;
				if (getAttributeFromAnnotatedList(oldAttributeOrAttributeGroup, newAttribute) == null) {
					AttributeImpl newAttributeImpl = new AttributeImpl(newAttribute, oldObject);
					result.addChild(new AttributeComparator(null, newAttributeImpl).compare());
				}
			}else if (newAnnotated instanceof AttributeGroup){
				AttributeGroup newAttributeGroup = (AttributeGroup) newAnnotated;
				if (getAttributeGroupFromAnnotatedList(oldAttributeOrAttributeGroup, newAttributeGroup) == null) {
					result.addChild(new AttributeGroupComparator(null, newAttributeGroup, oldObject, newObject).compare());
				}
			}
		}
	}
	private static Attribute getAttributeFromAnnotatedList(List<Annotated> attributes, Attribute containsAttribute) {
		for (Annotated annotated : attributes) {
			if (annotated instanceof Attribute) {
				Attribute attribute = (Attribute) annotated;
				if (AbstractComparator.isEqual(attribute.getName(),containsAttribute.getName(), false)){
					return attribute;
				}else if (AbstractComparator.isEqual(attribute.getRef(), containsAttribute.getRef(), false)){
					return attribute;
				}
			}
		}
		return null;
	}
	private static AttributeGroup getAttributeGroupFromAnnotatedList(List<Annotated> attributes, AttributeGroup containsAttributeGroup) {
		for (Annotated annotated : attributes) {
			if (annotated instanceof AttributeGroup) {
				AttributeGroup attribute = (AttributeGroup) annotated;
				if (AbstractComparator.isEqual(attribute.getName(),containsAttributeGroup.getName(), false)){
					return attribute;
				}else if (AbstractComparator.isEqual(attribute.getRef(), containsAttributeGroup.getRef(), false)){
					return attribute;
				}
			}
		}
		return null;
	}
	protected static boolean isEqual(String value, String value1) {
		if (value != null){
			return value.equals(value1);
		}else if (value1 != null){
			return value1.equals(value);
		}else {
			return false;
		}
	}

}
