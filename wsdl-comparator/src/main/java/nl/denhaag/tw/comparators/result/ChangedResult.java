package nl.denhaag.tw.comparators.result;

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


import javax.xml.namespace.QName;

public class ChangedResult extends CompareResult {
	private String oldValue;
	private String newValue;
	
	public ChangedResult(){
		
	}
	public ChangedResult(Compatibility compatible, String description, int oldValue, int newValue) {
		super(compatible, description);
		this.oldValue = oldValue +"";
		this.newValue = newValue +"";
	}
	public ChangedResult(Compatibility compatible, String description, String oldValue, String newValue) {
		super(compatible, description);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public ChangedResult(Compatibility compatible, String description, Boolean oldValue, Boolean newValue) {
		super(compatible, description);
		this.oldValue = oldValue +"";
		this.newValue = newValue +"";
	}
	public ChangedResult(Compatibility compatibility, String description, QName oldValue, QName newValue){
		super(compatibility, description);
		if (CompareConfiguration.getInstance().isDisplayNamespaces()){
			this.oldValue = oldValue + "";
			this.newValue = newValue + "";
		}else {
			if (oldValue != null)
				this.oldValue = oldValue.getLocalPart().toString();
			if (newValue != null)
			this.newValue = newValue.getLocalPart().toString();
		}		
	}
	public void init(Compatibility compatibility, String prefix, String oldValue, String newValue){
		setCompatible(compatibility);
		setDescription(prefix);
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public void init(Compatibility compatibility, String prefix, QName oldValue, QName newValue){
		setCompatible(compatibility);
		setDescription(prefix);
		if (CompareConfiguration.getInstance().isDisplayNamespaces()){
			this.oldValue = oldValue + "";
			this.newValue = newValue + "";
		}else {
			if (oldValue != null)
				this.oldValue = oldValue.getLocalPart().toString();
			if (newValue != null)
			this.newValue = newValue.getLocalPart().toString();
		}		
	}
	@Override
	public String toString() {
		String result = "";
		if (!Compatibility.UNKNOWN.equals(this.getCompatible())){
			result +="CHANGED: ";
		}
		result += getDescription();
		if (oldValue != null || newValue != null){
			if (oldValue != null && newValue != null){
				if (oldValue.equals(newValue)){
					result += " (" + oldValue + ")";
				}else {
					result += " (" + oldValue + "->"+ newValue + ")";
				}
			}else {
				result += " (" + oldValue + "->"+ newValue + ")";
			}
			
		}
		return result;
	}
}
