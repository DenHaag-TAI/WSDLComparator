package nl.denhaag.tw.comparators.result;

/*
 * #%L
 * wsdl-comparator
 * %%
 * Copyright (C) 2012 - 2014 Team Webservices (Gemeente Den Haag)
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


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

public class ComparisonStorage {
	private static final Logger LOGGER = Logger.getLogger(ComparisonStorage.class);
	private static ComparisonStorage instance;
	private Map<QName, Map<QName, ChangedResult>> typeComparison = new HashMap<QName, Map<QName, ChangedResult>>();
	private LinkedList<ComparisonStorage.Entry> stack = new LinkedList<ComparisonStorage.Entry>();
	private ComparisonStorage(){
		
	}
	public static ComparisonStorage getInstance(){
		if (instance == null){
			instance = new ComparisonStorage();
			
		}
		return instance;
	}

	public void clear(){
		typeComparison = new HashMap<QName, Map<QName, ChangedResult>>();
		stack = new LinkedList<ComparisonStorage.Entry>();
	}
	public void addTypeComparison(QName oldObject, QName newObject, ChangedResult changeResult){
		if (oldObject != null && newObject != null) {	
			Map<QName, ChangedResult> temp = typeComparison.get(oldObject);
			if (temp == null){
				temp = new HashMap<QName, ChangedResult>();
				typeComparison.put(oldObject, temp);
			}
			if (!temp.containsKey(newObject)){
				//LOGGER.info("Add comparison of " + oldObject + " " + newObject);
				temp.put(newObject, changeResult);
			}
		}
	}
	public ChangedResult getTypeComparison(QName oldObject, QName newObject){
		if (oldObject == null || newObject == null) {
			return null;
		}
		Map<QName, ChangedResult> temp = typeComparison.get(oldObject);
		if (temp == null){
			return null;
		}else {
			ChangedResult result = temp.get(newObject);
			if (result != null){
				//LOGGER.info("Reused comparison of " + oldObject + " " + newObject);
			}
			return result;
		}
	}
	
	public void push(QName oldObject, QName newObject){
		if (oldObject != null && newObject != null) {	
			stack.push(new Entry(oldObject, newObject));
		}
	}
	public void pop(QName oldObject, QName newObject){
		if (oldObject != null && newObject != null) {	
			stack.pop();
		}
	}
	public boolean isAlreadyInStack(QName oldObject, QName newObject){
		if (oldObject != null && newObject != null) {	
			for (Entry entry: stack){
				if (entry.oldObject.equals(oldObject) && entry.newObject.equals(newObject)){
					//LOGGER.info("isAlreadyInStack " + oldObject + " " + newObject);
					return true;
				}
			}
		}
		return false;
	}

	private static class Entry{
		private QName oldObject;
		private QName newObject;
		public Entry(QName oldObject, QName newObject){
			this.oldObject = oldObject;
			this.newObject = newObject;
		}
	}
}
