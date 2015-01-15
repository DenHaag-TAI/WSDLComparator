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

import org.ow2.easywsdl.wsdl.api.abstractItf.AbsItfBindingParam;
import org.ow2.easywsdl.wsdl.api.binding.wsdl11.soap.soap11.SOAP11Binding4Wsdl11;
import org.ow2.easywsdl.wsdl.api.binding.wsdl11.soap.soap11.SOAP11Header;
import org.ow2.easywsdl.wsdl.api.binding.wsdl11.soap.soap12.SOAP12Binding4Wsdl11;
import org.ow2.easywsdl.wsdl.api.binding.wsdl11.soap.soap12.SOAP12Header;

public abstract class AbsItfBindingParamComparator<T extends AbsItfBindingParam> extends AbstractSchemaElementComparator<T> {

	public AbsItfBindingParamComparator(T oldObject, T newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
	
		if (getOldObject().getSOAP11Binding4Wsdl11() != null && getNewObject().getSOAP11Binding4Wsdl11()!= null){
			SOAP11Binding4Wsdl11 oldObject = getOldObject().getSOAP11Binding4Wsdl11();
			SOAP11Binding4Wsdl11 newObject = getNewObject().getSOAP11Binding4Wsdl11();
			// TODO: implement more checks
			if (oldObject.getBody() != null && newObject.getBody() != null) {
				if (!oldObject.getBody().getUse().equals(newObject.getBody().getUse())){
					result.addChild(new ChangedResult(Compatibility.BREAKS, "Use changed", oldObject.getBody().getUse().toString(), newObject.getBody().getUse().toString()));				
				}
			}
			if (oldObject.getFault() != null && newObject.getFault() != null) {
				if (!oldObject.getFault().getUse().equals(newObject.getFault().getUse())){
					result.addChild(new ChangedResult(Compatibility.BREAKS, "Use changed", oldObject.getFault().getUse().toString(), newObject.getFault().getUse().toString()));				
				}
			}
			List<QName> oldHeaderQNames = new ArrayList<QName>();

			for (SOAP11Header oldHeader : oldObject.getHeaders()) {
				oldHeaderQNames.add(oldHeader.getMessage());
				result.addChild(new SOAP11HeaderComparator(oldHeader, getSOAP11Header(newObject.getHeaders(),oldHeader.getMessage())).compare());
			}
			for (SOAP11Header newHeader : newObject.getHeaders()) {
				if (!contains(oldHeaderQNames, newHeader.getMessage())) {
					result.addChild(new SOAP11HeaderComparator(null, newHeader).compare());
				}
			}
		}else if (getOldObject().getSOAP12Binding4Wsdl11() != null && getNewObject().getSOAP12Binding4Wsdl11()!= null){
			SOAP12Binding4Wsdl11 oldObject = getOldObject().getSOAP12Binding4Wsdl11();
			SOAP12Binding4Wsdl11 newObject = getNewObject().getSOAP12Binding4Wsdl11();
			// TODO: implement more checks
			if (oldObject.getBody() != null && newObject.getBody() != null) {
				if (!oldObject.getBody().getUse().equals(newObject.getBody().getUse())){
					result.addChild(new ChangedResult(Compatibility.BREAKS, "Use changed", oldObject.getBody().getUse().toString(), newObject.getBody().getUse().toString()));				
				}
			}
			if (oldObject.getFault() != null && newObject.getFault() != null) {
				if (!oldObject.getFault().getUse().equals(newObject.getFault().getUse())){
					result.addChild(new ChangedResult(Compatibility.BREAKS, "Use changed", oldObject.getFault().getUse().toString(), newObject.getFault().getUse().toString()));				
				}
			}	
			List<QName> oldHeaderQNames = new ArrayList<QName>();

			for (SOAP12Header oldHeader : oldObject.getHeaders()) {
				oldHeaderQNames.add(oldHeader.getMessage());
				result.addChild(new SOAP12HeaderComparator(oldHeader, getSOAP12Header(newObject.getHeaders(),oldHeader.getMessage())).compare());
			}
			for (SOAP12Header newHeader : newObject.getHeaders()) {
				if (!contains(oldHeaderQNames, newHeader.getMessage())) {
					result.addChild(new SOAP12HeaderComparator(null, newHeader).compare());
				}
			}		
		}else {
			result.addChild(new AddedResult(Compatibility.BREAKS, "different SOAP versions", null));
		}
		if (result.getChildren().size() > 0){
			result.init(Compatibility.UNKNOWN,  getPrefix());
		}		
		return result;
	}
	protected abstract String getPrefix();

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.BREAKS, getPrefix(), getNewObject().getName());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, getPrefix(), getOldObject().getName());
		return result;
	}
	
	protected SOAP11Header getSOAP11Header(List<SOAP11Header> soapHeaders, QName qname) {
		for (SOAP11Header soapHeader : soapHeaders) {
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				if (qname.equals(soapHeader.getMessage())) {
					return soapHeader;
				}
			} else {
				if (qname.getLocalPart().equals(soapHeader.getMessage().getLocalPart())) {
					return soapHeader;
				}
			}
		}
		return null;
	}
	
	protected SOAP12Header getSOAP12Header(List<SOAP12Header> soapHeaders, QName qname) {
		for (SOAP12Header soapHeader : soapHeaders) {
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				if (qname.equals(soapHeader.getMessage())) {
					return soapHeader;
				}
			} else {
				if (qname.getLocalPart().equals(soapHeader.getMessage().getLocalPart())) {
					return soapHeader;
				}
			}
		}
		return null;
	}
}
