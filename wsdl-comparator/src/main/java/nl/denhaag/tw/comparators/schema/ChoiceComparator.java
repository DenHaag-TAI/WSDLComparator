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

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;

import org.ow2.easywsdl.schema.api.Choice;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.impl.ChoiceImpl;
import org.ow2.easywsdl.schema.impl.SequenceImpl;

public class ChoiceComparator extends AbstractSchemaElementComparator<Choice> {
	private boolean baseType;
	public ChoiceComparator(Choice oldObject, Choice newObject) {
		super(oldObject, newObject);
	}

	public ChoiceComparator(Choice oldObject, Choice newObject,  boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		ChoiceImpl oldChoiceImpl = (ChoiceImpl) getOldObject();
		ChoiceImpl newChoiceImpl = (ChoiceImpl) getNewObject();
		List<Object> oldChildren = copyList(((ChoiceImpl) getOldObject()).getModel().getParticle());
		List<Object> newChildren = copyList(((ChoiceImpl) getNewObject()).getModel().getParticle());
		while (oldChildren.size() > 0) {
			int oldIndex = 0;
			ObjectResult oldObjectResult = new ObjectResult(oldChoiceImpl, oldChildren.get(oldIndex), oldIndex);
			ObjectResult newObjectResult = getObjectResult(newChoiceImpl, newChildren, oldObjectResult);
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
			}

			oldChildren.remove(0);
		}
		while (newChildren.size() > 0) {
			int newIndex = 0;
			ObjectResult newObjectResult = new ObjectResult(newChoiceImpl, newChildren.get(newIndex), newIndex);
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
		if (result.getChildren().size() > 0) {
			result.init(Compatibility.UNKNOWN, "Choice");
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
		result.init(Compatibility.BREAKS, "Choice");
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Choice");
		return result;
	}


}
