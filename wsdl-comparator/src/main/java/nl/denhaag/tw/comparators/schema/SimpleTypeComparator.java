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
import org.ow2.easywsdl.schema.impl.RestrictionImpl;

public class SimpleTypeComparator extends AbstractTypeComparator<SimpleType> {

	public SimpleTypeComparator(SimpleType oldObject, SimpleType newObject) {
		super(oldObject, newObject);
	}
	public SimpleTypeComparator(SimpleType oldObject, SimpleType newObject, boolean baseType) {
		super(oldObject, newObject, baseType);
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		//logger.info(getOldObject().getQName() + " "+  getNewObject().getQName());
		if (getOldObject().getQName() != null && getNewObject().getQName() != null && !getOldObject().getQName().equals(getNewObject().getQName())){
			
			result.init(Compatibility.BREAKS,  "SimpleType ", getOldObject().getQName(),  getNewObject().getQName());
		}else {
			result.addChild(new RestrictionComparator((RestrictionImpl) getOldObject().getRestriction(),(RestrictionImpl)  getNewObject().getRestriction()).compare());
			/*
			 * TODO: add more comparators
			 */
			super.analyzeChangedObject(result);
			if (result.getChildren().size() > 0){
				if (getNewObject().getQName() == null){
					result.init(Compatibility.UNKNOWN,  "Anonymous SimpleType");
				}else {
					result.init(Compatibility.UNKNOWN,  "SimpleType ", getOldObject().getQName(),  getNewObject().getQName());
				}
			}		
		}
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		if (getNewObject().getQName() == null){
			result.init(Compatibility.BREAKS,  "Anonymous SimpleType");
		}else {
			result.init(Compatibility.BREAKS, "SimpleType", getNewObject().getQName());
		}		

		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		if (getOldObject().getQName() == null){
			result.init(Compatibility.BREAKS,  "Anonymous SimpleType");
		}else {
			result.init(Compatibility.BREAKS, "SimpleType", getOldObject().getQName());
		}			
		return result;
	}
}
