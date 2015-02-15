package com.isosystem.smarthouse.data;

import com.isosystem.smarthouse.data.MenuTree.MenuScreenType;
import com.isosystem.smarthouse.data.MenuTree.NodeType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ����� "���� �������"
 */
public class MenuTreeNode implements Serializable, Cloneable {

	private static final long serialVersionUID = -5145972103127325087L;

	//�������� �������
	public String nodeTitle;
	
	//��� �������
	public NodeType nodeType;

	//��� ��������� ���� �������
	public MenuScreenType screenType;
	
	// �������� �� ���� ������
	public Boolean isRootNode;
	
	// ������������ ����
	public MenuTreeNode parentNode;
	
	//������ �������� �����
	public ArrayList<MenuTreeNode> childNodes;
	
	public Boolean isExpanded;

	//������ ���������� ����
	public HashMap<String, String> paramsMap;
	
	public MenuTreeNode(Boolean isParent) {
		this.nodeTitle = "";
		this.nodeType = NodeType.NODE_LEAF;
		this.isRootNode = isParent;
		this.parentNode = null;
		this.childNodes = new ArrayList<MenuTreeNode>();
		this.paramsMap = new HashMap<String, String>();
		this.isExpanded = false;
	}
	
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
	}
	
	public MenuTreeNode(Boolean rootnode, MenuTreeNode parentnode, String nodetitle,
			NodeType nodetype) {
		this(rootnode);
		this.parentNode = parentnode;
		this.nodeTitle = nodetitle;
		this.nodeType = nodetype;
	}	
}