package com.isosystem.smarthouse.data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ����� "������ ����". �������� ������ ���� � ������ ���� ���������������� ������
 */
public class MenuTree implements Serializable {

	private static final long serialVersionUID = -2468431332446345270L;
	
	// �������� �������
	public MenuTreeNode rootNode;
	
	// ��������� �������
	public MenuTreeNode tempNode;
	// ��������� ������������ �������
	public MenuTreeNode tempParentNode;
	
	// ��������� ������� ��� �������� �� ���� ��������� ��������
	public MenuTreeNode mPageReturnNode;
	
	// ������ ��� ���� "����"
	private ArrayList<String> sTreeMenuNodes;
	private ArrayList<MenuTreeNode> nTreeMenuNodes;
		
	// ������ ���
	private ArrayList<String> sTreeNodes;
	private ArrayList<MenuTreeNode> nTreeNodes;
	
	private ArrayList<MenuTreeNode> mSettingsTree;
	
	// �����������
	public MenuTree() {
		mPageReturnNode = null;
		rootNode = new MenuTreeNode(true,null,"������� ����",NodeType.NODE_MENU);		
	}
		
	public void ClearMenu() {
		mPageReturnNode = null;
		rootNode = new MenuTreeNode(true,null,"������� ����",NodeType.NODE_MENU);
	}
	
	public ArrayList<MenuTreeNode> getNodesForSettingsTree() {
		mSettingsTree = new ArrayList<MenuTreeNode>();
		traverseTree(rootNode);
		return mSettingsTree;
	}

	private void traverseTree(MenuTreeNode node) {
		if (node == null) return;
		
		mSettingsTree.add(node);
		ArrayList<MenuTreeNode> children = node.childNodes;
		
		if (node.isExpanded) {
			for (int i=0;i < children.size();i++) {
				traverseTree(children.get(i));
			}
		}
	
	}
	
	/** ���������� ������ ����� ���� "����" */
	public ArrayList<String> getMenuNodes() {
		sTreeMenuNodes = new ArrayList<String>();
		nTreeMenuNodes = new ArrayList<MenuTreeNode>();
		TraverseTreeForMenuNodes("",rootNode);
		return sTreeMenuNodes;
	}

	/** ���������� ������ ����� ���� "����" ��� ������ ��������������.
	 *  �� ������ ����������� ������������� ���� � ��������� 
	 */
	public ArrayList<String> getMenuNodes(MenuTreeNode checkNode) {
		sTreeMenuNodes = new ArrayList<String>();
		nTreeMenuNodes = new ArrayList<MenuTreeNode>();	
		TraverseTreeForMenuNodes("",rootNode,checkNode);
		return sTreeMenuNodes;
	}
	
	/** ����� ������ ��� ����� ���� "����"
	 */
	private void TraverseTreeForMenuNodes(String bcrumb, MenuTreeNode node) {
		
		if (node == null) return;
		
		sTreeMenuNodes.add(bcrumb + node.nodeTitle);
		nTreeMenuNodes.add(node);
		
		ArrayList<MenuTreeNode> children = node.childNodes;
		
		for (int i=0;i < children.size();i++) {
			
			if (children.get(i).nodeType == NodeType.NODE_MENU) {
				TraverseTreeForMenuNodes(bcrumb+node.nodeTitle+"\\",children.get(i));
			}
		}
	}
	
	/** ����� ������ ��� ����� ���� "����" ��� ������ ��������������
	 *  ����������� ������� ������������� ����
	 */
	private void TraverseTreeForMenuNodes(String bcrumb, MenuTreeNode node, MenuTreeNode checkNode) {
		if (node == null || node.equals(checkNode)) return;
		
		sTreeMenuNodes.add(bcrumb + node.nodeTitle);
		nTreeMenuNodes.add(node);
		
		ArrayList<MenuTreeNode> children = node.childNodes;
		
		for (int i=0;i < children.size();i++) {
			
			if (children.get(i).nodeType == NodeType.NODE_MENU) {
				TraverseTreeForMenuNodes(bcrumb+node.nodeTitle+"\\",children.get(i),checkNode);
			}
		}
	}
	
	public ArrayList<String> getNodes() {
		sTreeNodes = new ArrayList<String>();
		nTreeNodes = new ArrayList<MenuTreeNode>();
		TraverseTreeForNodes("",rootNode);
		return sTreeNodes;
	}
	
	public void DeleteNode (MenuTreeNode node) {		
		SearchForNodeToDelete(this.rootNode,node);
	}

	public void SearchForNodeToDelete (MenuTreeNode curNode,MenuTreeNode delNode) {
		if (curNode == null) return;
		if (curNode.equals(delNode)) {
			curNode = null;
			return;
		}
		ArrayList<MenuTreeNode> children = curNode.childNodes;
		
		for (int i=0;i < children.size();i++) {
			SearchForNodeToDelete(children.get(i), delNode);
		}
	}
	
	private void TraverseTreeForNodes(String bcrumb, MenuTreeNode node) {
		
		if (node == null) return;
		
		sTreeNodes.add(bcrumb + node.nodeTitle);
		nTreeNodes.add(node);
		ArrayList<MenuTreeNode> children = node.childNodes;
		
		for (int i=0;i < children.size();i++) {
			TraverseTreeForNodes(bcrumb+node.nodeTitle+"\\",children.get(i));
		}
	}
	
	public MenuTreeNode GetNodeForSpinnerPosition(int position) {
		return nTreeMenuNodes.get(position);
	}
	
	public int GetSpinnerPositionForNode (MenuTreeNode node) {
		int position = nTreeMenuNodes.indexOf(node);
		return position;
	}
	
	public MenuTreeNode GetNodeForListViewPosition(int position) {
		return nTreeNodes.get(position);
	}
	
	// ��� ����:
	// ����
	// �������� �����
	// 
	public enum NodeType {
		NODE_MENU,
		NODE_LEAF
	}
	
	public enum MenuScreenType {
		SetIntValue("������ ���� ��������", 0),
		SetPasswordValue("���� ������", 1),
		SetBooleanValue("������������ ���/����", 2),
		SendMessage("������� ���������", 3);

		private String stringValue;
		private int intValue;

		private MenuScreenType(String toString, int value) {
			stringValue = toString;
			intValue = value;
		}

		@Override
		public String toString() {
			return stringValue;
		}

		public int toInt() {
			return intValue;
		}
	}
}