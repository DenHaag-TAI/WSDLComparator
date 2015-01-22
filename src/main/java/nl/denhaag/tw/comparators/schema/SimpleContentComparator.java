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

import org.ow2.easywsdl.schema.impl.SimpleContentImpl;



public class SimpleContentComparator extends AbstractSchemaElementComparator<SimpleContentImpl> {


	private boolean baseType;
	public SimpleContentComparator(SimpleContentImpl oldObject, SimpleContentImpl newObject) {
		super(oldObject, newObject);
	}
	public SimpleContentComparator(SimpleContentImpl oldObject, SimpleContentImpl newObject, boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		boolean supported  = false;
		/*
		 * check for extension
		 */
		if (getOldObject().getExtension() != null|| getNewObject().getExtension()!= null){
			result.addChild(new ExtensionComparator(getOldObject().getExtension(), getNewObject().getExtension(), baseType).compare());
			supported = true;
		}
		if (!supported) {
			result.init(Compatibility.UNKNOWN,  "SimpleContent with unsupported structure");
		}
		if (result.getChildren().size() > 0){
				result.init(Compatibility.UNKNOWN,  "SimpleContent");
		}	
		return result;
	}




	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "SimpleContent");
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "SimpleContent");
		return result;
	}

}
