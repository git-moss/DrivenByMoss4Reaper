// Written by Jürgen Moßgraber - mossgrabers.de
// (c) 2017-2019
// Licensed under LGPLv3 - http://www.gnu.org/licenses/lgpl-3.0.txt

package de.mossgrabers.reaper.framework;

import java.util.ArrayList;
import java.util.List;


/**
 * A tree and its nodes.
 *
 * @author J&uuml;rgen Mo&szlig;graber
 *
 * @param <T> The type of the trees content
 */
public class TreeNode<T>
{
    final T                 data;
    TreeNode<T>             parent;
    final List<TreeNode<T>> children = new ArrayList<> ();


    /**
     * Constructor for the root note.
     */
    public TreeNode ()
    {
        this (null);
    }


    /**
     * Constructor for a node in the tree.
     *
     * @param data The data to store in the node
     */
    private TreeNode (final T data)
    {
        this.data = data;
    }


    /**
     * Add a child node to the node.
     *
     * @param child The child content to add
     * @return The created node
     */
    public TreeNode<T> addChild (final T child)
    {
        final TreeNode<T> childNode = new TreeNode<> (child);
        childNode.parent = this;
        this.children.add (childNode);
        return childNode;
    }


    /**
     * Get the parent of the node.
     *
     * @return The parent or null if it is the root node
     */
    public TreeNode<T> getParent ()
    {
        return this.parent;
    }


    /**
     * Get the children of the node.
     *
     * @return The children
     */
    public List<TreeNode<T>> getChildren ()
    {
        return this.children;
    }


    /**
     * Get the data which is stored in the node.
     *
     * @return The data
     */
    public T getData ()
    {
        return this.data;
    }
}