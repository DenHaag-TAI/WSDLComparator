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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;

import org.ow2.easywsdl.schema.api.Choice;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Sequence;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.impl.SequenceImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.ExplicitGroup;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.LocalElement;

public class SequenceComparator extends AbstractSchemaElementComparator<Sequence> {
	private boolean baseType;

	public SequenceComparator(Sequence oldObject, Sequence newObject) {
		super(oldObject, newObject);
	}

	public SequenceComparator(Sequence oldObject, Sequence newObject, boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		SequenceImpl oldSequenceImpl = (SequenceImpl) getOldObject();
		SequenceImpl newSequenceImpl = (SequenceImpl) getNewObject();
		List<Object> oldChildren = copyList(((SequenceImpl) getOldObject()).getModel().getParticle());
		List<Object> newChildren = copyList(((SequenceImpl) getNewObject()).getModel().getParticle());
		boolean orderChanged = false;
		while (oldChildren.size() > 0) {
			int oldIndex = 0;
			ObjectResult oldObjectResult = new ObjectResult(oldSequenceImpl, oldChildren.get(oldIndex), oldIndex);
			ObjectResult newObjectResult = getObjectResult(newSequenceImpl, newChildren, oldObjectResult);
			if (newObjectResult == null) {
				if (oldObjectResult.isElement()) {
					result.addChild(new ElementComparator(oldObjectResult.getElement(), null, baseType).compare());
				} else if (oldObjectResult.isChoice()) {
					result.addChild(new ChoiceComparator(oldObjectResult.getChoice(), null, baseType).compare());
				}else if (oldObjectResult.isSequence()) {
					result.addChild(new SequenceComparator(oldObjectResult.getSequence(), null,
							baseType).compare());
				}
			} else {
				if (oldObjectResult.isElement()) {
					result.addChild(new ElementComparator(oldObjectResult.getElement(), newObjectResult.getElement(),
							baseType).compare());
				} else if (oldObjectResult.isChoice()) {
					result.addChild(new ChoiceComparator(oldObjectResult.getChoice(), newObjectResult.getChoice(),
							baseType).compare());
				}else if (oldObjectResult.isSequence()) {
					result.addChild(new SequenceComparator(oldObjectResult.getSequence(), newObjectResult.getSequence(),
							baseType).compare());
				}
				if (newObjectResult.getIndex() != oldIndex) {
					orderChanged = true;
				}
			}
			oldChildren.remove(0);
		}
		while (newChildren.size() > 0) {
			int newIndex = 0;
			ObjectResult newObjectResult = new ObjectResult(newSequenceImpl, newChildren.get(newIndex), newIndex);
			if (newObjectResult.isElement()) {
				result.addChild(new ElementComparator(null, newObjectResult.getElement(), baseType).compare());
			} else if (newObjectResult.isChoice()) {
				result.addChild(new ChoiceComparator(null, newObjectResult.getChoice(), baseType).compare());
			}else if (newObjectResult.isSequence()) {
				result.addChild(new SequenceComparator(null, newObjectResult.getSequence(), baseType).compare());
			}
			newChildren.remove(0);
		}
		/*
		 * TODO: add more comparators
		 */
		super.analyzeChangedObject(result);
		if (orderChanged) {
			result.init(Compatibility.BREAKS, "Sequence order changed");
		} else if (result.getChildren().size() > 0) {
			result.init(Compatibility.UNKNOWN, "Sequence");
		}
		return result;
	}

	private ObjectResult getObjectResult(AbstractSchemaElementImpl parent, List<Object> objects, ObjectResult otherObjectResult) {
		for (int index = 0; index < objects.size(); index++) {
			ObjectResult tempObjectResult = new ObjectResult(parent, objects.get(index), index);
			if (otherObjectResult.equals(tempObjectResult)) {
				objects.remove(index);
				return tempObjectResult;
			}
		}
		return null;
	}

	private List<Object> copyList(List<Object> org){
		List<Object> result = new ArrayList<Object>();
		for (Object object: org){
			result.add(object);
		}
		return result;
	}
	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.BREAKS, "Sequence");
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Sequence");
		return result;
	}

	private class ObjectResult {
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
				isSequence = SchemaUtil.XSD_SEQUENCE.equals(qname);
				isChoice = SchemaUtil.XSD_CHOICE.equals(qname);
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
			if (isSequence) {
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
					return isEqual(this.getElement().getQName(), otherObjectResult.getElement().getQName());
				} else if (this.isChoice && otherObjectResult.isChoice()) {
					return true;
				}else if (this.isSequence && otherObjectResult.isSequence()) {
					return true;
				}
			}
			return false;
		}

	}


}
