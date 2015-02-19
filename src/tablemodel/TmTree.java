package tablemodel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import main.DialogBoxs;
import main.MyUtil;

public class TmTree implements TreeModel {
    DefaultTreeModel innerModel;
    DefaultMutableTreeNode rootNode = new MyDefaultMutableTreeNode("Матрица",0);

    public class MyDefaultMutableTreeNode extends DefaultMutableTreeNode{
        public int nodeID;

        private MyDefaultMutableTreeNode(String name, int nodeID) {
            this.nodeID = nodeID;
            this.setUserObject(name);
        }
    }
    public TmTree(ResultSet rs) {
        innerModel = new DefaultTreeModel(rootNode);
        int ind = 0;
        try {
            while (rs.next()) {
                MyDefaultMutableTreeNode newKeyNode = new MyDefaultMutableTreeNode(rs.getString("name"),rs.getInt("CatID"));
                innerModel.insertNodeInto(newKeyNode, rootNode, ind);
                ind++;
                if(rs.getInt("rgt")-rs.getInt("lft")-1 > 0) {
                    innerModel.insertNodeInto(new MyDefaultMutableTreeNode("уровень 1",-1),newKeyNode, 0);
                }
            }
        } catch (Exception ex) {
			MyUtil.errorToLog(this.getClass().getName(), ex);
            //DialogBoxs.viewError(ex);
        }
    }

    public void addNodes(ResultSet rs, MyDefaultMutableTreeNode node) {
        MutableTreeNode lastNode = (MutableTreeNode) node.getLastChild();
        int ind = 1;
        try {
            while (rs.next()) {
                MyDefaultMutableTreeNode newKeyNode = new MyDefaultMutableTreeNode(rs.getString("name"), rs.getInt("CatID"));
                innerModel.insertNodeInto(newKeyNode, node, ind);
                ind++;
                if (rs.getInt("rgt") - rs.getInt("lft") - 1 > 0) {
                    innerModel.insertNodeInto(new MyDefaultMutableTreeNode("уровень 2", -1), newKeyNode, 0);
                }
            }
        } catch (Exception ex) {
            DialogBoxs.viewError(ex);
        }
        innerModel.removeNodeFromParent(lastNode);
    }

    public Object getRoot() {
        return innerModel.getRoot();
    }

    public Object getChild(Object parm1, int parm2) {
        return innerModel.getChild(parm1, parm2);
    }

    public int getChildCount(Object parm1) {
        return innerModel.getChildCount(parm1);
    }

    public boolean isLeaf(Object parm1) {
        return innerModel.isLeaf(parm1);
    }

    public void valueForPathChanged(TreePath parm1, Object parm2) {
        innerModel.valueForPathChanged(parm1, parm2);
    }

    public int getIndexOfChild(Object parm1, Object parm2) {
        return innerModel.getIndexOfChild(parm1, parm2);
    }

    public void addTreeModelListener(TreeModelListener parm1) {
        innerModel.addTreeModelListener(parm1);
    }

    public void removeTreeModelListener(TreeModelListener parm1) {
        innerModel.removeTreeModelListener(parm1);
    }
}
