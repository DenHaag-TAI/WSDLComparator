package nl.denhaag.tw.comparators.schema;

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


import java.util.List;

import java.util.Map;

import javax.xml.namespace.QName;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.CompareConfiguration;
import nl.denhaag.tw.comparators.result.CompareResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;
import nl.denhaag.tw.comparators.result.UnsupportedResult;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.ow2.easywsdl.schema.api.Attribute;

public abstract class AbstractComparator<T> {
	protected final Logger logger = LogManager.getLogger(this.getClass());
	private T oldObject;
	private T newObject;

	public AbstractComparator(T oldObject, T newObject) {
		super();
		this.oldObject = oldObject;
		this.newObject = newObject;
	}

	protected T getOldObject() {
		return oldObject;
	}

	protected T getNewObject() {
		return newObject;
	}

	public static boolean isEqual(QName oldQName, QName newQName, boolean nullValue) {
		if (CompareConfiguration.getInstance().isCheckNamespaces()) {
			if (oldQName != null) {
				return oldQName.equals(newQName);
			} else if (newQName != null) {
				return newQName.equals(oldQName);
			} else {
				return nullValue;
			}
		} else {
			if (oldQName != null && newQName != null) {
				return oldQName.getLocalPart().equals(newQName.getLocalPart());
			} else if (oldQName == null && newQName == null) {
				return nullValue;
			} else {
				return false;
			}
		}
	}

	protected static boolean isEqual(String value, String value1, boolean nullValue) {
		if (value != null) {
			return value.equals(value1);
		} else if (value1 != null) {
			return value1.equals(value);
		} else {
			return nullValue;
		}
	}

	public static boolean isEqual(QName oldQName, QName newQName) {
		return isEqual(oldQName, newQName, true);

	}

	protected static boolean isEqual(String value, String value1) {
		return isEqual(value, value1, true);
	}

	public final CompareResult compare() {
		CompareResult result = null;
		try {
		if (oldObject != null || newObject != null) {
			if (oldObject == null) {
				result = analyzeAddedObject(new AddedResult());
			} else if (newObject == null) {
				result = analyzeRemovedObject(new RemovedResult());
			} else {
				result = analyzeChangedObject(new ChangedResult());
			}
			if (result != null && Compatibility.NOCHANGE.equals(result.getCompatible())) {
				result = null;
			}
		} else {
			// result = new NothingResult(Compatibility.DOCUMENTATION,
			// "Nothing to compare");
		}
		}catch (Exception e){
			result = new UnsupportedResult(Compatibility.UNKNOWN, e.getMessage());
			logger.error(e.getMessage(),e);
		}
		return result;
	}

	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		return result;
	}

	protected AddedResult analyzeAddedObject(AddedResult result) {
		return result;
	}

	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		return result;
	}

	protected boolean contains(List<QName> qnames, QName qname) {
		if (qname != null) {
			for (QName current : qnames) {
				if (CompareConfiguration.getInstance().isCheckNamespaces()) {
					if (qname.equals(current)) {
						return true;
					}
				} else {
					if (qname.getLocalPart().equals(current.getLocalPart())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected boolean contains(List<String> names, String name) {
		for (String current : names) {
			if (name.equals(current)) {
				return true;
			}

		}

		return false;
	}

	protected String getAttribute(Map<QName, String> otherAttributes, QName qname) {
		if (CompareConfiguration.getInstance().isCheckNamespaces()) {
			return otherAttributes.get(qname);
		}
		for (Map.Entry<QName, String> oldOtherAttribute : otherAttributes.entrySet()) {
			QName current = oldOtherAttribute.getKey();
			if (qname.getLocalPart().equals(current.getLocalPart())) {
				return oldOtherAttribute.getValue();
			}

		}
		return null;
	}

	protected Attribute getAttribute(List<Attribute> attributes, Attribute containsAttribute) {
		for (Attribute attribute : attributes) {
			boolean equal = true;
			equal = equal && attribute.getName().equals(containsAttribute.getName());
			if (CompareConfiguration.getInstance().isCheckNamespaces()) {
				equal = equal && attribute.getNamespaceUri().equals(containsAttribute.getNamespaceUri());
			}
			if (equal) {
				return attribute;
			}
		}
		return null;
	}

}
