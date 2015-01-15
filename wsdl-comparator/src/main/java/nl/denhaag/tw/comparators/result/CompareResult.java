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


import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

public class CompareResult {

	private static final String CSS_DOCUMENTATION = "documentation";
	private static final String CSS_NOBREAKS_BELOW = "nobreaks_below";
	private static final String CSS_NOBREAKS = "nobreaks";
	private static final String CSS_BREAKS_BELOW = "breaks_below";
	private static final String CSS_BREAKS = "breaks";
	private Compatibility compatible = Compatibility.NOCHANGE;
	private boolean isResponse = false;
	private String description;
	private List<CompareResult> children = new ArrayList<CompareResult>();
	public CompareResult(){
		
	}
	
	public CompareResult(Compatibility compatible, String description) {
		super();
		this.compatible = compatible;
		this.description = description;
	}
	public void init(Compatibility compatibility, String description){
		setCompatible(compatibility);
		setDescription(description);
	}
	public void setResponse(boolean response){
		this.isResponse = response;
	}
	public void init(Compatibility compatibility){
		setCompatible(compatibility);
	}
	public String getDescription() {
		return description;
	}
	protected void setCompatible(Compatibility compatible) {
		this.compatible = compatible;
	}

	protected void setDescription(String description) {
		this.description = description;
	}


	protected void setChildren(List<CompareResult> children) {
		this.children = children;
	}

	public List<CompareResult> getChildren() {
		return children;
	}
	public void updateCompatibility(boolean isResponse){
		this.isResponse = isResponse;
		if (isResponse){
			if (Compatibility.NOTBREAKS.equals(compatible)){
				compatible = Compatibility.BREAKS;
			}
			for (CompareResult child: children){
				child.updateCompatibility(isResponse);
			}
		}
	}
	public void addChild(CompareResult compareResult) {
		if (compareResult != null){
			compareResult.updateCompatibility(isResponse);
			children.add(compareResult);
			if (Compatibility.NOCHANGE.equals(compatible)){
				compatible = Compatibility.UNKNOWN;
			}
		}
	}
	public Compatibility getCompatible() {
		return compatible;
	}
	public Compatibility getCompatibleAndChildren(){
		Compatibility result = compatible;
		for (int i=0; i < children.size() && !result.isHighest(); i++){
			Compatibility child = children.get(i).getCompatibleAndChildren();
			if (child.isHigher(result)){
				result = child;
			}
		}
		return result;
	}
	@Override
	public String toString() {
		String result =  description;
		return result;
	}
	public Font getFont(){
		String cssClass = getCssClass();
		if (CSS_BREAKS.equals(cssClass)){
			return new Font(Font.SANS_SERIF, Font.BOLD, 12);
		}else if (CSS_BREAKS_BELOW.equals(cssClass)){
			return new Font(Font.SANS_SERIF, Font.PLAIN, 11);
		}else if (CSS_NOBREAKS.equals(cssClass)){
			return new Font(Font.SANS_SERIF, Font.BOLD, 11);
		}else if (CSS_NOBREAKS_BELOW.equals(cssClass)){
			return new Font(Font.SANS_SERIF, Font.ITALIC, 10);
		}else {
			return new Font(Font.SANS_SERIF,  Font.PLAIN, 11);
		}

	}
	public Color getColor(){
		String cssClass = getCssClass();
		if (CSS_BREAKS.equals(cssClass)){
			return Color.RED;
		}else if (CSS_BREAKS_BELOW.equals(cssClass)){
			return Color.BLACK;
		}else if (CSS_NOBREAKS.equals(cssClass)){
			return Color.BLACK;
		}else if (CSS_NOBREAKS_BELOW.equals(cssClass)){
			return Color.GRAY;
		}else {
			return Color.GRAY;
		}
		
	}
	
	public String getCssClass(){
        if (getCompatible().isAtLeast(Compatibility.BREAKS)){
        	return CSS_BREAKS;
        }else if (Compatibility.NOTBREAKS.equals(getCompatible())){
        	Compatibility inheritedCompatibility = getCompatibleAndChildren();
        	if (inheritedCompatibility.isAtLeast(Compatibility.BREAKS)){
        		return CSS_BREAKS_BELOW;
        	}else {
        		return CSS_NOBREAKS;
        	}
        }else {
        	Compatibility inheritedCompatibility = getCompatibleAndChildren();
        	if (inheritedCompatibility.isAtLeast(Compatibility.BREAKS)){
        		return CSS_BREAKS_BELOW;
        	}else if (Compatibility.DOCUMENTATION.equals(inheritedCompatibility)){
        		return CSS_DOCUMENTATION;
        	}else {
        		return CSS_NOBREAKS_BELOW;
        	}

        }	
	}
}
