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


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;

import org.ow2.easywsdl.schema.impl.RestrictionImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Facet;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.NoFixedFacet;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.NumFacet;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Pattern;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.Restriction;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.TotalDigits;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.WhiteSpace;

public class RestrictionComparator extends AbstractSchemaElementComparator<RestrictionImpl> {

	public RestrictionComparator(RestrictionImpl oldObject, RestrictionImpl newObject) {
		super(oldObject, newObject);
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		if (!getOldObject().getBase().equals(getNewObject().getBase())) {
			result.init(Compatibility.BREAKS, "Restriction ", getOldObject().getBase(), getNewObject().getBase());

		} else {
			Restriction oldRestriction = getOldObject().getModel();
			Restriction newRestriction = getNewObject().getModel();
			RestrictionContent oldRestrictionContent = fillRestrictions(oldRestriction);
			RestrictionContent newRestrictionContent = fillRestrictions(newRestriction);
			analyzeFacets(result, "Pattern ", oldRestrictionContent.patterns, newRestrictionContent.patterns);
			analyzeFacets(result, "Lengths ", oldRestrictionContent.numberFacets, newRestrictionContent.numberFacets);
			analyzeFacets(result, "Others ", oldRestrictionContent.others, newRestrictionContent.others);
			analyzeFacets(result, "Whitespaces ", oldRestrictionContent.whitespaces, newRestrictionContent.whitespaces);

			if (oldRestrictionContent.totalDigits != null && newRestrictionContent.totalDigits != null){
				if (oldRestrictionContent.totalDigits < newRestrictionContent.totalDigits){
					result.addChild(new ChangedResult(Compatibility.NOTBREAKS, "TotalDigits ", oldRestrictionContent.totalDigits, newRestrictionContent.totalDigits));
				}else if (oldRestrictionContent.totalDigits > newRestrictionContent.totalDigits){
					result.addChild(new ChangedResult(Compatibility.BREAKS, "TotalDigits ", oldRestrictionContent.totalDigits, newRestrictionContent.totalDigits));
				}
			}else if (oldRestrictionContent.totalDigits != null && newRestrictionContent.totalDigits == null){
				result.addChild(new RemovedResult(Compatibility.BREAKS, "TotalDigits ", oldRestrictionContent.totalDigits));
			}else if (oldRestrictionContent.totalDigits == null && newRestrictionContent.totalDigits != null){
				result.addChild(new AddedResult(Compatibility.BREAKS, "TotalDigits ", newRestrictionContent.totalDigits));
			}
			for (String enumValue : oldRestrictionContent.enumerations) {
				if (newRestrictionContent.enumerations.contains(enumValue)) {
					newRestrictionContent.enumerations.remove(enumValue);
				} else {
					result.addChild(new RemovedResult(Compatibility.BREAKS, "Enumeration ", enumValue));
				}
			}
			for (String enumValue : newRestrictionContent.enumerations) {
				result.addChild(new AddedResult(Compatibility.NOTBREAKS, "Enumeration ", enumValue));
			}			

			if (result.getChildren().size() > 0) {
				result.init(Compatibility.UNKNOWN, "Restriction");
			}
		}

		return result;
	}

	private void analyzeFacets(ChangedResult result, String description, List<String> oldList, List<String> newList) {
		for (String oldPattern : oldList) {
			if (newList.contains(oldPattern)) {
				newList.remove(oldPattern);
			} else {
				result.addChild(new RemovedResult(Compatibility.BREAKS, description, oldPattern));
			}
		}
		for (String newPattern : newList) {
			result.addChild(new AddedResult(Compatibility.BREAKS, description, newPattern));
		}
	}

	private RestrictionContent fillRestrictions(Restriction restriction) {
		RestrictionContent result = new RestrictionContent();
		for (Object objectFacet : restriction.getFacets()) {
			if (objectFacet instanceof Pattern) {
				result.patterns.add(((Pattern) objectFacet).getValue());
			} else if (objectFacet instanceof TotalDigits) {
				result.totalDigits = Integer.parseInt(((TotalDigits) objectFacet).getValue());
			}else if (objectFacet instanceof WhiteSpace) {
				result.whitespaces.add(((WhiteSpace) objectFacet).getValue());
			} else if (objectFacet instanceof JAXBElement<?>) {
				JAXBElement<?> element = (JAXBElement<?>) objectFacet;
				if (element.getValue() instanceof NumFacet) {
					result.numberFacets.add(((NumFacet) element.getValue()).getValue());
				} else if (element.getValue() instanceof NoFixedFacet) {
					result.enumerations.add(((NoFixedFacet) element.getValue()).getValue());
				} else if (element.getValue() instanceof Facet) {
					result.others.add(((Facet) element.getValue()).getValue());
				}else if (element.getValue() instanceof WhiteSpace) {
					result.whitespaces.add(((WhiteSpace) element.getValue()).getValue());
				} else {
					logger.error("NOT SUPPORTED: " + objectFacet);
				}
			} else {
				logger.error("NOT SUPPORTED: " + objectFacet);
			}
		}
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Restriction ", getNewObject().getBase());
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Restriction ", getOldObject().getBase());
		return result;
	}
	private class RestrictionContent {
		List<String> patterns = new ArrayList<String>();
		Integer totalDigits = null;
		List<String> numberFacets = new ArrayList<String>();
		List<String> enumerations = new ArrayList<String>();
		List<String> others = new ArrayList<String>();
		List<String> whitespaces = new ArrayList<String>();
	}
}
