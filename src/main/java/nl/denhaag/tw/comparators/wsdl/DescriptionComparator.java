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

import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Service;

public class DescriptionComparator extends AbstractSchemaElementComparator<Description> {

	public DescriptionComparator(Description oldObject, Description newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		if (CompareConfiguration.getInstance().isCheckNamespaces() && !getOldObject().getTargetNamespace().equals(getNewObject().getTargetNamespace())) {
			result.init(Compatibility.BREAKS, "Target namespace", getOldObject().getTargetNamespace(), getNewObject()
					.getTargetNamespace());
		} else {
			List<QName> oldInterfaceTypeQNames = new ArrayList<QName>();
			for (InterfaceType oldInterfaceType : getOldObject().getInterfaces()) {
				oldInterfaceTypeQNames.add(oldInterfaceType.getQName());
				result.addChild(new InterfaceTypeComparator(oldInterfaceType, getInterface(getNewObject()
						.getInterfaces(), oldInterfaceType.getQName())).compare());
			}
			for (InterfaceType newInterfaceType : getNewObject().getInterfaces()) {
				if (!contains(oldInterfaceTypeQNames, newInterfaceType.getQName())) {
					result.addChild(new InterfaceTypeComparator(null, newInterfaceType)
							.compare());
				}
			}
			List<QName> oldServiceQNames = new ArrayList<QName>();
			for (Service oldService : getOldObject().getServices()) {
				oldServiceQNames.add(oldService.getQName());
				result.addChild(new ServiceComparator(oldService, getService(getNewObject()
						.getServices(), oldService.getQName())).compare());
			}
			for (Service service : getNewObject().getServices()) {
				if (!contains(oldServiceQNames, service.getQName())) {
					result.addChild(new ServiceComparator(null, service)
							.compare());
				}
			}			
			//getNewObject().getBindings().get(0).get
			super.analyzeChangedObject(result);

			if (result.getChildren().size() > 0) {
				result.init(Compatibility.UNKNOWN, "Definitions ");
			}

		}

		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Definitions");
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Definitions");
		return result;
	}

	protected Service getService(List<Service> services, QName qname) {
		for (Service service : services) {
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				if (qname.equals(service.getQName())) {
					return service;
				}
			} else {
				if (qname.getLocalPart().equals(service.getQName().getLocalPart())) {
					return service;
				}
			}
		}
		return null;
	}
	protected InterfaceType getInterface(List<InterfaceType> types, QName qname) {
		for (InterfaceType type : types) {
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				if (qname.equals(type.getQName())) {
					return type;
				}
			} else {
				if (qname.getLocalPart().equals(type.getQName().getLocalPart())) {
					return type;
				}
			}
		}
		return null;
	}
}
