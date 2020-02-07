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


import java.util.Enumeration;


import javax.swing.tree.TreeNode;

import nl.denhaag.tw.comparators.result.CompareResult;


public class CompareResultTreeNode implements TreeNode {
	private CompareResult current;
	private CompareResultTreeNode parent;
	public CompareResultTreeNode(CompareResult root, CompareResultTreeNode parent){
		this.current = root;
		this.parent = parent;
	}
//	@Override
	public Enumeration children() {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public boolean getAllowsChildren() {
		// TODO Auto-generated method stub
		return !current.getChildren().isEmpty();
	}

//	@Override
	public TreeNode getChildAt(int childIndex) {
		return new CompareResultTreeNode(current.getChildren().get(childIndex), this);
	}

//	@Override
	public int getChildCount() {
		return current.getChildren().size();
	}

//	@Override
	public int getIndex(TreeNode node) {
		return 0;
	}

//	@Override
	public TreeNode getParent() {
		return parent;
	}

//	@Override
	public boolean isLeaf() {
		return current.getChildren().size() == 0;
	}
	public CompareResult getResult(){
		return current;
	}
	@Override
	public String toString() {
		return current.toString();
	}
	
}
