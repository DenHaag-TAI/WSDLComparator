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

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.InvalidResult;
import nl.denhaag.tw.comparators.result.RemovedResult;
import nl.denhaag.tw.comparators.schema.AbstractSchemaElementComparator;

import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;

public class ServiceComparator extends AbstractSchemaElementComparator<Service> {

	public ServiceComparator(Service oldObject, Service newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		/*
		 * compare operations
		 */
		List<Binding> oldBindings = new ArrayList<Binding>();
		for (Endpoint oldEndpoint : getOldObject().getEndpoints()) {
			if (oldEndpoint.getBinding() == null) {
				result.getChildren().add(new InvalidResult("No binding available for port", oldEndpoint.getName()));
			}else {
				oldBindings.add(oldEndpoint.getBinding());
				result.addChild(new EndpointComparator(oldEndpoint, getEndpoint(getNewObject().getEndpoints(), oldEndpoint))
					.compare());
			}
		}
		for (Endpoint newEndpoint : getNewObject().getEndpoints()) {
			if (newEndpoint.getBinding() == null) {
				result.getChildren().add(new InvalidResult("No binding available for port", newEndpoint.getName()));
			}else {
				if (!contains(oldBindings, newEndpoint.getBinding())) {
					result.addChild(new EndpointComparator(null, newEndpoint).compare());
				}
			}			

		}

		super.analyzeChangedObject(result);
		if (result.getChildren().size() > 0) {
			result.init(Compatibility.UNKNOWN, "Service", getOldObject().getQName(), getNewObject().getQName());
		}
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Service", getNewObject().getQName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Service", getOldObject().getQName());
		return result;
	}

	protected boolean contains(List<Binding> bindings, Binding binding) {
		for (Binding current : bindings) {
			try {
			if (current.getTypeOfBinding().equals(binding.getTypeOfBinding())
					&& current.getTransportProtocol().equals(binding.getTransportProtocol())) {
				return true;
			}
			}catch (Exception e) {
				logger.error(current.getQName() + " " + e.getMessage());
			}

		}
		return false;
	}

	protected Endpoint getEndpoint(List<Endpoint> endpoints, Endpoint endpoint) {
		for (Endpoint current : endpoints) {
			try {
				String name = endpoint.getName();
				String oldName = current.getName();
				if (name != null && oldName != null && name.equals(oldName)){
					return current;
				}
			} catch (Exception e) {
				logger.error(current.getBinding().getQName()  + " " + e.getMessage(),e);
			}
		}
		return null;
	}
}
