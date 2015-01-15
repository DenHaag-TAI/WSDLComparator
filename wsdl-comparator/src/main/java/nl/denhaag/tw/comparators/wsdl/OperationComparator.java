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


import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.CompareConfiguration;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;
import nl.denhaag.tw.comparators.schema.AbstractSchemaElementComparator;

import org.ow2.easywsdl.wsdl.api.Fault;
import org.ow2.easywsdl.wsdl.api.Operation;

public class OperationComparator extends AbstractSchemaElementComparator<Operation> {

	public OperationComparator(Operation oldObject, Operation newObject) {
		super(oldObject, newObject);
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		result.addChild(new InputComparator(getOldObject().getInput(), getNewObject().getInput()).compare());
		result.addChild(new OutputComparator(getOldObject().getOutput(), getNewObject().getOutput()).compare());
	/*
		 * compare faults
		 */
		List<QName> oldFaultsQNames = new ArrayList<QName>();

		for (Fault oldFault : getOldObject().getFaults()) {
			oldFaultsQNames.add(oldFault.getMessageName());
			result.addChild(new FaultComparator(oldFault, getFault(getNewObject().getFaults(),oldFault.getMessageName())).compare());
		}
		for (Fault newFault : getNewObject().getFaults()) {
			if (!contains(oldFaultsQNames, newFault.getMessageName())) {
				result.addChild(new FaultComparator(null, newFault).compare());
			}
		}
		/*
		 * TODO: add more comparators
		 */
		super.analyzeChangedObject(result);
		if (result.getChildren().size() > 0){
			result.init(Compatibility.UNKNOWN,  "Operation", getOldObject().getQName(), getNewObject().getQName());
		}		
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS,  "Operation", getNewObject().getQName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Operation", getOldObject().getQName());
		return result;
	}
	protected Fault getFault(List<Fault> faults, QName qname) {
		for (Fault fault : faults) {
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				if (qname.equals(fault.getMessageName())) {
					return fault;
				}
			} else {
				if (qname.getLocalPart().equals(fault.getMessageName().getLocalPart())) {
					return fault;
				}
			}
		}
		return null;
	}
}
