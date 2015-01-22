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


import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;

import org.ow2.easywsdl.wsdl.api.Output;

public class OutputComparator extends AbsItfParamComparator<Output> {

	public OutputComparator(Output oldObject, Output newObject) {
		super(oldObject, newObject);
	}

	/* (non-Javadoc)
	 * @see nl.denhaag.tw.comparators.wsdl.AbsItfParamComparator#analyzeChangedObject(nl.denhaag.tw.comparators.result.ChangedResult)
	 */
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		result.setResponse(true);
		ChangedResult changedResult = super.analyzeChangedObject(result);
		if (changedResult.getCompatibleAndChildren().isAtLeast(Compatibility.NOTBREAKS)){
			if (isEqual(getOldObject().getMessageName(),getNewObject().getMessageName())){
				changedResult.init(Compatibility.BREAKS);
			}	
		}
		return changedResult;
	}

	@Override
	protected String getPrefix() {
		return "Output";
	}


}
