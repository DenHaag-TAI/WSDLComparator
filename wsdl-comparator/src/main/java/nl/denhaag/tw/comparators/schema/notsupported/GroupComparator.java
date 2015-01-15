package nl.denhaag.tw.comparators.schema.notsupported;

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


import java.util.List;

import javax.xml.namespace.QName;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.CompareConfiguration;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;
import nl.denhaag.tw.comparators.schema.AbstractComparator;

import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Group;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.GroupRef;

public class GroupComparator extends AbstractComparator<GroupRef> {
	private boolean baseType;
	public GroupComparator(GroupRef oldObject, GroupRef newObject) {
		super(oldObject, newObject);
	}

	public GroupComparator(GroupRef oldObject, GroupRef newObject,  boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		result.init(Compatibility.UNSUPPORTED, "Group is not supported");
		

		return result;
	}

	protected Element getElement(List<Element> elements, QName qname) {
		for (Element element : elements) {
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				if (qname.equals(element.getQName())) {
					return element;
				}
			} else {
				if (qname.getLocalPart().equals(element.getQName().getLocalPart())) {
					return element;
				}
			}
		}
		return null;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.BREAKS, "Group");
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Group");
		return result;
	}


}
