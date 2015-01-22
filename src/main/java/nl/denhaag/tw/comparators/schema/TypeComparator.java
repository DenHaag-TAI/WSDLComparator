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


import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.ComparisonStorage;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.RemovedResult;

import org.ow2.easywsdl.schema.api.SimpleType;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.impl.ComplexTypeImpl;

public class TypeComparator extends AbstractTypeComparator<Type> {
	

	public TypeComparator(Type oldObject, Type newObject) {
		super(oldObject, newObject);
	}
	public TypeComparator(Type oldObject, Type newObject, boolean baseType) {
		super(oldObject, newObject, baseType);
	}
	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {

		if (getOldObject() instanceof ComplexTypeImpl && getNewObject() instanceof ComplexTypeImpl){
			if (ComparisonStorage.getInstance().isAlreadyInStack(getOldObject().getQName(), getNewObject().getQName())){
				return null;
			}
			ComparisonStorage.getInstance().push(getOldObject().getQName(), getNewObject().getQName());
			ChangedResult changedResult = ComparisonStorage.getInstance().getTypeComparison(getOldObject().getQName(), getNewObject().getQName());
			if (changedResult != null){
				return changedResult;		
			}
			new ComplexTypeComparator((ComplexTypeImpl) getOldObject(), (ComplexTypeImpl) getNewObject(), isBaseType()).analyzeChangedObject(result);
			ComparisonStorage.getInstance().addTypeComparison(getOldObject().getQName(), getNewObject().getQName(), result);
			ComparisonStorage.getInstance().pop(getOldObject().getQName(), getNewObject().getQName());
		}else if (getOldObject() instanceof SimpleType && getNewObject() instanceof SimpleType){
			new SimpleTypeComparator((SimpleType) getOldObject(), (SimpleType) getNewObject(), isBaseType()).analyzeChangedObject(result);
		}else {
			result.init(Compatibility.BREAKS, "Types are not equal", getOldObject().getClass().getSimpleName(), getNewObject().getClass().getSimpleName());
		}

		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		if (getNewObject() instanceof SimpleType){
			new SimpleTypeComparator(null, (SimpleType) getNewObject(), isBaseType()).analyzeAddedObject(result);
		}else if (getNewObject() instanceof ComplexTypeImpl){
			new ComplexTypeComparator(null, (ComplexTypeImpl) getNewObject(), isBaseType()).analyzeAddedObject(result);
		}		
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		if (getOldObject() instanceof SimpleType){
			new SimpleTypeComparator((SimpleType) getOldObject(), null, isBaseType()).analyzeRemovedObject(result);
		}if (getOldObject() instanceof ComplexTypeImpl){
			new ComplexTypeComparator((ComplexTypeImpl) getOldObject(), null, isBaseType()).analyzeRemovedObject(result);
		}
		return result;
	}

}
