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


import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;
import nl.denhaag.tw.comparators.schema.AbstractComparator;

import org.ow2.easywsdl.wsdl.api.binding.wsdl11.soap.soap11.SOAP11Header;
import org.ow2.easywsdl.wsdl.impl.wsdl11.binding.soap.soap11.SOAP11HeaderImpl;
import org.ow2.easywsdl.wsdl.org.xmlsoap.schemas.wsdl.soap.UseChoice;

public class SOAP11HeaderComparator extends AbstractComparator<SOAP11Header> {

	public SOAP11HeaderComparator(SOAP11Header oldObject, SOAP11Header newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		if (isEqual(getOldObject().getMessage(),getNewObject().getMessage())){
			/*
			 * part
			 */
			if (getOldObject().getPart() != null && getNewObject().getPart() != null
					&& !getOldObject().getPart().equals(getNewObject().getPart())) {
				result.addChild(new ChangedResult(Compatibility.NOTBREAKS, getPrefix() + " attribute 'part' changed", getOldObject()
						.getMessage() + "@" + getOldObject().getPart(), getNewObject().getMessage() + "@"
						+ getNewObject().getPart()));
			} else if (getOldObject().getPart() != null && getNewObject().getPart() == null) {
				result.addChild(new RemovedResult(Compatibility.NOTBREAKS, getPrefix() + " attribute 'part' ", getOldObject()
						.getMessage() + "@" + getOldObject().getPart()));
			} else if (getOldObject().getPart() == null && getNewObject().getPart() != null) {
				result.addChild(new AddedResult(Compatibility.NOTBREAKS, getPrefix()+ " attribute 'part'", getNewObject()
						.getMessage() + "@" + getNewObject().getPart()));
			}	
			/*
			 * use
			 */
			SOAP11HeaderImpl oldObject = (SOAP11HeaderImpl) getOldObject();
			SOAP11HeaderImpl newObject = (SOAP11HeaderImpl) getNewObject();
			UseChoice oldUse = oldObject.getModel().getUse();
			UseChoice newUse = newObject.getModel().getUse();
			if (oldUse != null && newUse!= null
					&& !oldUse.equals(newUse)) {
				result.addChild(new ChangedResult(Compatibility.BREAKS, getPrefix() + " attribute 'use' changed", getOldObject()
						.getMessage() + "@" + oldUse, getNewObject().getMessage() + "@"
						+ newUse));
			} else if (oldUse != null && newUse == null) {
				result.addChild(new RemovedResult(Compatibility.BREAKS, getPrefix() + " attribute 'use' ", getOldObject()
						.getMessage() + "@" + oldUse));
			} else if (oldUse == null && newUse != null) {
				result.addChild(new AddedResult(Compatibility.BREAKS, getPrefix()+ " attribute 'use'", getNewObject()
						.getMessage() + "@" + newUse));
			}
			
//			result.addChild(new ElementComparator(getOldObject().getElement(), getNewObject().getElement()).compare());	
			if (result.getChildren().size() > 0){
				result.init(Compatibility.UNKNOWN,  getPrefix() ,  getOldObject().getMessage(), getNewObject().getMessage());
			}				
		}else {
			result.init(Compatibility.BREAKS,getPrefix(), getOldObject().getMessage(), getNewObject().getMessage());
		}
		return result;
	}

	private String getPrefix(){
		return "SOAP 11 Header";
	}
	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.BREAKS, getPrefix(), getNewObject().getMessage());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, getPrefix(), getOldObject().getMessage());
		return result;
	}


}
