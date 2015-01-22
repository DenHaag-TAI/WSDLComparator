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


import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.ow2.easywsdl.schema.api.Choice;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Sequence;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.ExplicitGroup;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.LocalElement;

public class ObjectResult {

	private Object object;
	private int index;
	private boolean isElement;
	private boolean isChoice;
	private boolean isSequence;
	private AbstractSchemaElementImpl parent;

	public ObjectResult(AbstractSchemaElementImpl parent, Object object, int index) {
		super();
		this.parent = parent;
		this.object = object;
		this.index = index;
		if (object instanceof JAXBElement) {
			JAXBElement jaxbElement = (JAXBElement) object;
			QName qname = jaxbElement.getName();
			isElement = SchemaUtil.XSD_ELEMENT.equals(qname);
			isSequence = SchemaUtil.XSD_CHOICE.equals(qname);
			isChoice = SchemaUtil.XSD_SEQUENCE.equals(qname);
		}
	}

	/**
	 * @return the object
	 */
	protected Object getObject() {
		return object;
	}

	protected Element getElement() {
		if (isElement) {
			JAXBElement jaxbElement = (JAXBElement) object;
			return SchemaUtil.convertToElement(parent, (LocalElement) jaxbElement.getValue());
		}
		return null;
	}

	protected Choice getChoice() {
		if (isChoice) {
			JAXBElement jaxbElement = (JAXBElement) object;
			return SchemaUtil.convertToChoice(parent, (ExplicitGroup) jaxbElement.getValue());
		}
		return null;
	}

	protected Sequence getSequence() {
		if (isChoice) {
			JAXBElement jaxbElement = (JAXBElement) object;
			return SchemaUtil.convertToSequence(parent, (ExplicitGroup) jaxbElement.getValue());
		}
		return null;
	}

	protected int getIndex() {
		return index;
	}

	/**
	 * @return the isElement
	 */
	protected boolean isElement() {
		return isElement;
	}

	/**
	 * @return the isChoice
	 */
	protected boolean isChoice() {
		return isChoice;
	}

	/**
	 * @return the isSequence
	 */
	protected boolean isSequence() {
		return isSequence;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof ObjectResult) {
			ObjectResult otherObjectResult = (ObjectResult) other;
			if (this.isElement && otherObjectResult.isElement()) {
				return AbstractComparator.isEqual(this.getElement().getQName(), otherObjectResult.getElement()
						.getQName());
			} else if (this.isChoice && otherObjectResult.isChoice()) {
				return true;
			} else if (this.isSequence && otherObjectResult.isSequence()) {
				return true;
			}
		}
		return false;
	}

}