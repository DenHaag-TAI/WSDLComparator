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

public class InvalidResult extends CompareResult {
	private String value;
	public InvalidResult(){
	}
	public InvalidResult(String prefix, String value){
		setCompatible(Compatibility.INVALID);
		setDescription(prefix);
		this.value = value;
	}
	public InvalidResult(String prefix, QName value){
		setCompatible(Compatibility.INVALID);
		setDescription(prefix);
		if (CompareConfiguration.getInstance().isDisplayNamespaces()){
			this.value = value.toString();
		}else {
			this.value = value.getLocalPart().toString();
		}
	}	
	public InvalidResult(String description, int value) {
		super(Compatibility.INVALID, description);
		this.value = value +"";
	}
	public void init(String prefix, String value) {
		setCompatible(Compatibility.INVALID);
		setDescription(prefix);
		this.value = value;
	}
	public void init(String prefix, QName value){
		setCompatible(Compatibility.INVALID);
		setDescription(prefix);
		if (CompareConfiguration.getInstance().isDisplayNamespaces()){
			this.value = value.toString();
		}else {
			this.value = value.getLocalPart().toString();
		}
	}
	@Override
	public String toString() {
		String result = "INVALID: " + getDescription();
		if (value != null) {
			result += " (" + value + ")";
		}
		return result;
	}
}
