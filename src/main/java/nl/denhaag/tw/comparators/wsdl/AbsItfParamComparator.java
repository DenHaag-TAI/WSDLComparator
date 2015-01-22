package nl.denhaag.tw.comparators.wsdl;

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
import nl.denhaag.tw.comparators.schema.AbstractSchemaElementComparator;
import nl.denhaag.tw.comparators.schema.ElementComparator;

import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfParam;

public abstract class AbsItfParamComparator<T extends AbsItfParam> extends AbstractSchemaElementComparator<T> {

	public AbsItfParamComparator(T oldObject, T newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		if (isEqual(getOldObject().getMessageName(),getNewObject().getMessageName())){
			if (getOldObject().getName() != null && getNewObject().getName() != null
					&& !getOldObject().getName().equals(getNewObject().getName())) {
				result.addChild(new ChangedResult(Compatibility.BREAKS, getPrefix() + " attribute 'name' changed", getOldObject()
						.getMessageName() + "@" + getOldObject().getName(), getNewObject().getMessageName() + "@"
						+ getNewObject().getName()));
			} else if (getOldObject().getName() != null && getNewObject().getName() == null) {
				result.addChild(new RemovedResult(Compatibility.BREAKS, getPrefix() + " attribute 'name' ", getOldObject()
						.getMessageName() + "@" + getOldObject().getName()));
			} else if (getOldObject().getName() == null && getNewObject().getName() != null) {
				result.addChild(new AddedResult(Compatibility.BREAKS, getPrefix()+ " attribute 'name'", getNewObject()
						.getMessageName() + "@" + getNewObject().getName()));
			}			
			result.addChild(new ElementComparator(getOldObject().getElement(), getNewObject().getElement()).compare());	
			if (result.getChildren().size() > 0){
				result.init(Compatibility.UNKNOWN,  getPrefix() ,  getOldObject().getMessageName(), getNewObject().getMessageName());
			}				
		}else {
			result.init(Compatibility.BREAKS,getPrefix(), getOldObject().getMessageName(), getNewObject().getMessageName());
		}
		return result;
	}
	protected abstract String getPrefix();

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.BREAKS, getPrefix(), getNewObject().getMessageName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, getPrefix(), getOldObject().getMessageName());
		return result;
	}
}
