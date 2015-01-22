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

import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;

public class InterfaceTypeComparator extends AbstractSchemaElementComparator<InterfaceType> {

	public InterfaceTypeComparator(InterfaceType oldObject, InterfaceType newObject) {
		super(oldObject, newObject);
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		/*
		 * compare operations
		 */
		List<QName> oldOperationsQNames = new ArrayList<QName>();
		for (Operation oldOperation : getOldObject().getOperations()) {
			oldOperationsQNames.add(oldOperation.getQName());
			result.addChild(new OperationComparator(oldOperation, getOperation(getNewObject().getOperations(),oldOperation.getQName())).compare());
		}
		for (Operation newOperation : getNewObject().getOperations()) {
			if (!contains(oldOperationsQNames, newOperation.getQName())) {
				result.addChild(new OperationComparator(null, newOperation).compare());
			}
		}
		/*
		 * TODO: add more comparators
		 */
		super.analyzeChangedObject(result);
		if (result.getChildren().size() > 0){
			result.init(Compatibility.UNKNOWN,  "Interface/PortType", getOldObject().getQName(), getNewObject().getQName());
		}		
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Interface/PortType", getNewObject().getQName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS,  "Interface/PortType ",  getOldObject().getQName());
		return result;
	}
	protected Operation getOperation(List<Operation> operations, QName qname) {
		for (Operation operation : operations) {
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				if (qname.equals(operation.getQName())) {
					return operation;
				}
			} else {
				if (qname.getLocalPart().equals(operation.getQName().getLocalPart())) {
					return operation;
				}
			}
		}
		return null;
	}
}
