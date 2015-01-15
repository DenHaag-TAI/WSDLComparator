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


import javax.xml.namespace.QName;

import nl.denhaag.tw.comparators.result.AddedResult;
import nl.denhaag.tw.comparators.result.ChangedResult;
import nl.denhaag.tw.comparators.result.Compatibility;
import nl.denhaag.tw.comparators.result.NothingResult;
import nl.denhaag.tw.comparators.result.RemovedResult;
import nl.denhaag.tw.comparators.schema.notsupported.AllComparator;
import nl.denhaag.tw.comparators.schema.notsupported.GroupComparator;

import org.ow2.easywsdl.schema.api.All;
import org.ow2.easywsdl.schema.api.Choice;
import org.ow2.easywsdl.schema.api.Extension;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.schema.api.absItf.AbsItfSchema;
import org.ow2.easywsdl.schema.api.abstractElmt.AbstractSchemaElementImpl;
import org.ow2.easywsdl.schema.impl.ChoiceImpl;
import org.ow2.easywsdl.schema.impl.ExtensionImpl;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.ExplicitGroup;
import org.ow2.easywsdl.schema.org.w3._2001.xmlschema.ExtensionType;

public class ExtensionComparator extends AbstractSchemaElementComparator<Extension> {
	private boolean baseType;

	public ExtensionComparator(Extension oldObject, Extension newObject) {
		super(oldObject, newObject);
	}

	public ExtensionComparator(Extension oldObject, Extension newObject, boolean baseType) {
		super(oldObject, newObject);
		this.baseType = baseType;
	}

	@Override
	protected ChangedResult analyzeChangedObject(ChangedResult result) {
		super.analyzeChangedObject(result);
		Type oldBaseType = findBaseType(getOldObject());
		Type newBaseType = findBaseType(getNewObject());
		ExtensionType oldObject = ((ExtensionImpl) getOldObject()).getModel();
		ExtensionType newObject = ((ExtensionImpl) getNewObject()).getModel();
		if (oldBaseType != null || newBaseType != null){
			result.addChild(new TypeComparator(oldBaseType, newBaseType, true).compare());
		}else {
			result.addChild(new NothingResult(Compatibility.UNSUPPORTED, "Extension has no base type"));
		}

		/*
		 * sequence
		 */
		if (getOldObject().getSequence() != null || getNewObject().getSequence() != null) {
			result.addChild(new SequenceComparator(getOldObject().getSequence(), getNewObject().getSequence(), baseType)
					.compare());

		}
		/*
		 * choice
		 */

		if (oldObject.getChoice() != null || newObject.getChoice() != null) {
			Choice oldChoice = new ChoiceImpl(oldObject.getChoice(), (ExtensionImpl) getOldObject());
			Choice newChoice = new ChoiceImpl(newObject.getChoice(), (ExtensionImpl) getNewObject());
			result.addChild(new ChoiceComparator(oldChoice, newChoice, baseType).compare());

		}
		if (oldObject.getGroup() != null || newObject.getGroup() != null) {
			result.addChild(new GroupComparator(oldObject.getGroup(), newObject.getGroup(), baseType).compare());
		}
		if (oldObject.getAll() != null || newObject.getAll() != null) {
			All oldAll = SchemaUtil.convertToAll((ExtensionImpl) getOldObject(), oldObject.getAll());
			All newAll = SchemaUtil.convertToAll((ExtensionImpl) getNewObject(), newObject.getAll());
			result.addChild(new AllComparator(oldAll, newAll, baseType).compare());
		}

		/*
		 * attributes
		 */

		AttributeUtil.compareAttributesAndAttributeGroups(result, oldObject.getAttributeOrAttributeGroup(), newObject.getAttributeOrAttributeGroup(), (ExtensionImpl) getOldObject(),(ExtensionImpl) getNewObject());

		if (result.getChildren().size() > 0) {
			result.init(Compatibility.UNKNOWN, "Extension with base", getBaseName(getOldObject()),
					getBaseName(getNewObject()));
		}
		return result;
	}

	@Override
	protected AddedResult analyzeAddedObject(AddedResult result) {
		super.analyzeAddedObject(result);
		result.init(Compatibility.NOTBREAKS, "Extension with base", getBaseName(getNewObject()));
		return result;
	}

	@Override
	protected RemovedResult analyzeRemovedObject(RemovedResult result) {
		super.analyzeRemovedObject(result);
		result.init(Compatibility.BREAKS, "Extension with base", getBaseName(getOldObject()));
		return result;
	}

	protected Choice getChoice(ExplicitGroup model, AbstractSchemaElementImpl parent) {
		if (model != null) {
			return new ChoiceImpl(model, parent);
		}
		return null;
	}

	protected QName getBaseName(Extension extension) {
		ExtensionImpl extensionImpl= (ExtensionImpl) extension;
		ExtensionType extensionType = extensionImpl.getModel();
		return  extensionType.getBase();
	}
	public static Type findBaseType(Extension extension){
		ExtensionImpl extensionImpl= (ExtensionImpl) extension;
		ExtensionType extensionType = extensionImpl.getModel();
		AbsItfSchema schema = extensionImpl.getSchema();
		QName qname = extensionType.getBase();
		Type result = (Type) schema.getType(qname);
		return result;
	}
}
