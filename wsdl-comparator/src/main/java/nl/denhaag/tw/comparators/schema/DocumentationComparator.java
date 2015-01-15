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

import org.ow2.easywsdl.schema.api.Documentation;

public class DocumentationComparator extends AbstractSchemaElementComparator<Documentation> {

	public DocumentationComparator(Documentation oldObject, Documentation newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		if (!getOldObject().getContent().equals(getNewObject().getContent())) {
			result.init(Compatibility.DOCUMENTATION, "Documentation", getOldObject().getContent(), getNewObject()
					.getContent());
		}
		return null;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.DOCUMENTATION, "Documentation", getNewObject().getContent());
		return null;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.DOCUMENTATION, "Documentation", getOldObject().getContent());
		return null;
	}

}
