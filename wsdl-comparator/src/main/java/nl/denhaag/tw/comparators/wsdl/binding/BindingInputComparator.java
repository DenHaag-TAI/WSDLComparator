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


import org.ow2.easywsdl.wsdl.api.BindingInput;

public class BindingInputComparator extends AbsItfBindingParamComparator<BindingInput> {

	public BindingInputComparator(BindingInput oldObject, BindingInput newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected String getPrefix() {
		return "Binding Input";
	}


}
