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

import org.ow2.easywsdl.schema.impl.ComplexContentImpl;



public class ComplexContentComparator extends AbstractSchemaElementComparator<ComplexContentImpl> {


	private boolean baseType;
	public ComplexContentComparator(ComplexContentImpl oldObject, ComplexContentImpl newObject) {
		super(oldObject, newObject);
	}
	public ComplexContentComparator(ComplexContentImpl oldObject, ComplexContentImpl newObject, boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		/*
		 * check for mixed
		 */
		if (getOldObject().getModel().isMixed() != getNewObject().getModel().isMixed()) {
			result.getChildren().add(new ChangedResult(Compatibility.BREAKS, "Attribute mixed ", getOldObject().getModel().isMixed(), getNewObject().getModel().isMixed()));
		}	
		/*
		 * check for extension
		 */
		if (getOldObject().getExtension() != null|| getNewObject().getExtension()!= null){
			result.addChild(new ExtensionComparator(getOldObject().getExtension(), getNewObject().getExtension(), baseType).compare());
		}
		/*
		 * check for restriction
		 */
		if (getOldObject().getModel().getRestriction() != null|| getNewObject().getModel().getRestriction()!= null){
			result.addChild(new ComplexRestrictionTypeComparator(getOldObject(), getNewObject(), baseType).compare());
		}	
		if (result.getChildren().size() > 0){
				result.init(Compatibility.UNKNOWN,  "ComplexContent");
		}	
		return result;
	}




	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "ComplexContent");
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "ComplexContent");
		return result;
	}

}
