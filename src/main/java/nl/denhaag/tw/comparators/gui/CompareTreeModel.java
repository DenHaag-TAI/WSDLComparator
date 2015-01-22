package nl.denhaag.tw.comparators.gui;

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


import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import nl.denhaag.tw.comparators.result.CompareResult;

public class CompareTreeModel implements TreeModel {

	private CompareResult root;
	public CompareTreeModel(CompareResult root){
		this.root = root;
	}
	@Override
	public void addTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getChild(Object arg0, int arg1) {
		CompareResult compareResult = (CompareResult)arg0;
		return compareResult.getChildren().get(arg1);
	}

	@Override
	public int getChildCount(Object arg0) {
		CompareResult compareResult = (CompareResult)arg0;
		return compareResult.getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object arg0) {
		CompareResult compareResult = (CompareResult)arg0;
		return compareResult.getChildren().isEmpty();
	}

	@Override
	public void removeTreeModelListener(TreeModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueForPathChanged(TreePath arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}
