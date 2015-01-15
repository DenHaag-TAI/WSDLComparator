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


import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.All;
import org.ow2.easywsdl.schema.api.Choice;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Sequence;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.impl.AllImpl;
import org.ow2.easywsdl.schema.impl.ChoiceImpl;
import org.ow2.easywsdl.schema.impl.ElementImpl;
import org.ow2.easywsdl.schema.impl.SequenceImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.ExplicitGroup;

public class SchemaUtil {
	public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
	public static final QName XSD_ELEMENT = new QName(XSD_NAMESPACE, "element");
	public static final QName XSD_CHOICE = new QName(XSD_NAMESPACE, "choice");
	public static final QName XSD_SEQUENCE = new QName(XSD_NAMESPACE, "sequence");
	@SuppressWarnings("rawtypes")
	public static Sequence convertToSequence(AbstractSchemaElementImpl parent, ExplicitGroup sequenceModel){
		if (sequenceModel == null){
			return null;
		}else {
			return new SequenceImpl(sequenceModel, parent);
		}
	}
	@SuppressWarnings("rawtypes")
	public static Choice convertToChoice(AbstractSchemaElementImpl parent, ExplicitGroup choiceModel){
		if (choiceModel == null){
			return null;
		}else {
			return new ChoiceImpl(choiceModel, parent);
		}
	}
	@SuppressWarnings("rawtypes")
	public static All convertToAll(AbstractSchemaElementImpl parent, org.ow2.easywsdl.schema.org.w3._2001.xmlschema.All allModel){
		if (allModel == null){
			return null;
		}else {
			return new AllImpl(allModel, parent);
		}
	}
	@SuppressWarnings("rawtypes")
	public static Element convertToElement(AbstractSchemaElementImpl parent, org.ow2.easywsdl.schema.org.w3._2001.xmlschema.LocalElement localElement){
		if (localElement == null){
			return null;
		}else {
			return (Element) new ElementImpl(localElement, parent);
		}
	}
}
