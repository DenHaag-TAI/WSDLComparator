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
import java.util.Map;

import javax.xml.namespace.QName;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.OpenAttrs;

public abstract class AbstractOpenAttrsComparator<T extends OpenAttrs> extends AbstractComparator<T> {
	protected final Logger logger = LogManager.getLogger(this.getClass());

	public AbstractOpenAttrsComparator(T oldObject, T newObject) {
		super(oldObject, newObject);
	}

	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		List<QName> oldOtherAttributesQNames = new ArrayList<QName>();
		for (Map.Entry<QName, String> oldOtherAttribute : getOldObject().getOtherAttributes().entrySet()) {
			oldOtherAttributesQNames.add(oldOtherAttribute.getKey());
			String newValue = getAttribute(getNewObject().getOtherAttributes(), oldOtherAttribute.getKey());
			if (newValue == null) {
				result.addChild(new RemovedResult(Compatibility.BREAKS, "Attribute removed", oldOtherAttribute
						.getValue()));
			} else if (!oldOtherAttribute.getValue().equals(newValue)) {
				result.addChild(new ChangedResult(Compatibility.BREAKS, "Attribute changed", oldOtherAttribute
						.getValue(), newValue));
			}
		}
		for (Map.Entry<QName, String> newOtherAttribute : getNewObject().getOtherAttributes().entrySet()) {
			if (!contains(oldOtherAttributesQNames, newOtherAttribute.getKey())) {
				result.addChild(new AddedResult(Compatibility.BREAKS, "Attribute added", newOtherAttribute.getValue()));
			}
		}
		return result;

	}




}
