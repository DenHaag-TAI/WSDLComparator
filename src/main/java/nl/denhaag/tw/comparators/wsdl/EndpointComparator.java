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
import nl.denhaag.tw.comparators.wsdl.binding.BindingOperationComparator;

import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Endpoint;

public class EndpointComparator extends AbstractSchemaElementComparator<Endpoint> {

	public EndpointComparator(Endpoint oldObject, Endpoint newObject) {
		super(oldObject, newObject);
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		if (!getOldObject().getAddress().equals(getNewObject().getAddress())) {
			result.getChildren().add(new ChangedResult(Compatibility.NOTBREAKS, "Endpoint address", getOldObject().getAddress(), getNewObject().getAddress()));
		}		
		Binding oldBinding = getOldObject().getBinding();
		Binding newBinding = getNewObject().getBinding();
		/*
		 * compare operations
		 */
		List<QName> oldOperationsQNames = new ArrayList<QName>();
		for (BindingOperation oldOperation : oldBinding.getBindingOperations()) {
			oldOperationsQNames.add(oldOperation.getQName());
			result.addChild(new BindingOperationComparator(oldOperation, getOperation(newBinding.getBindingOperations(),oldOperation.getQName())).compare());
		}
		for (BindingOperation newOperation : newBinding.getBindingOperations()) {
			if (!contains(oldOperationsQNames, newOperation.getQName())) {
				result.addChild(new BindingOperationComparator(null, newOperation).compare());
			}
		}


		super.analyzeChangedObject(result);
		if (result.getChildren().size() > 0){
			result.init(Compatibility.UNKNOWN,  "Endpoint",getOldObject().getName(), getNewObject().getName());
		}		
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Endpoint", getNewObject().getName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS,  "Endpoint",  getOldObject().getName());
		return result;
	}
	protected BindingOperation getOperation(List<BindingOperation> operations, QName qname) {
		for (BindingOperation operation : operations) {
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
