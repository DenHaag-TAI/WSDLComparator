package nl.denhaag.tw.comparators.wsdl.binding;

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

import org.ow2.easywsdl.wsdl.api.BindingFault;
import org.ow2.easywsdl.wsdl.api.BindingOperation;

public class BindingOperationComparator extends AbstractSchemaElementComparator<BindingOperation> {

	public BindingOperationComparator(BindingOperation oldObject, BindingOperation newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		if (!getOldObject().getStyle().equals(getNewObject().getStyle())) {
			result.getChildren().add(
					new ChangedResult(Compatibility.BREAKS, "Binding style", getOldObject().getStyle().toString(),
							getNewObject().getStyle().toString()));
		}
		if (!getOldObject().getSoapAction().equals(getNewObject().getSoapAction())) {
			result.getChildren().add(
					new ChangedResult(Compatibility.NOTBREAKS, "Binding soapAction", getOldObject().getSoapAction(),
							getNewObject().getSoapAction()));
		}
		result.addChild(new BindingInputComparator(getOldObject().getInput(), getNewObject().getInput()).compare());
		result.addChild(new BindingOutputComparator(getOldObject().getOutput(), getNewObject().getOutput()).compare());
		/*
		 * compare faults
		 */
		List<String> oldFaultsNames = new ArrayList<String>();

		for (BindingFault oldFault : getOldObject().getFaults()) {
			oldFaultsNames.add(oldFault.getName());
			result.addChild(new BindingFaultComparator(oldFault, getFault(getNewObject().getFaults(),
					oldFault.getName())).compare());
		}
		for (BindingFault newFault : getNewObject().getFaults()) {
			if (!contains(oldFaultsNames, newFault.getName())) {
				result.addChild(new BindingFaultComparator(null, newFault).compare());
			}
		}

		super.analyzeChangedObject(result);
		if (result.getChildren().size() > 0) {
			result.init(Compatibility.UNKNOWN, "Binding Operation", getOldObject().getQName(), getNewObject()
					.getQName());
		}
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Binding Operation", getNewObject().getQName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Binding Operation", getOldObject().getQName());
		return result;
	}

	protected BindingFault getFault(List<BindingFault> faults, String name) {
		if (name != null) {
			for (BindingFault fault : faults) {
				if (name.equals(fault.getName())) {
					return fault;
				}
			}
		}
		return null;
	}
}
