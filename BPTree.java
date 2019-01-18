/**
 * Filename:   BPTree.java
 * Project:    Food Query
 * Authors:    Debra Deppeler, Josiah Fee, Aaron Kelly, Chris Willson, Chloe 
 * Chan
 *
 * Semester:   Fall 2018
 * Course:     CS400
 * Lecture:    001
 * 
 * Due Date:   Before 10pm on December 13, 2018
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Implementation of a B+ tree to allow efficient access to
 * many different indexes of a large data set. 
 * BPTree objects are created for each type of index
 * needed by the program.  BPTrees provide an efficient
 * range search as compared to other types of data structures
 * due to the ability to perform log_m N lookups and
 * linear in-order traversals of the data items.
 * 
 * @author sapan (sapan@cs.wisc.edu)
 *
 * @param <K> key - expect a string that is the type of id for each item
 * @param <V> value - expect a user-defined type that stores all data for a food item
 */
public class BPTree<K extends Comparable<K>, V> implements BPTreeADT<K, V> {

    // Root of the tree
    private Node root;

    // Branching factor is the number of children nodes 
    // for internal nodes of the tree
    private int branchingFactor;


    /**
     * Public constructor
     * 
     * @param branchingFactor 
     */
    public BPTree(int branchingFactor) {
        if (branchingFactor <= 2) {
            throw new IllegalArgumentException(
                            "Illegal branching factor: " + branchingFactor);
        }
        this.branchingFactor=branchingFactor;
        root=new LeafNode();
    }


    /*
     * (non-Javadoc)
     * @see BPTreeADT#insert(java.lang.Object, java.lang.Object)
     */
    @Override
    public void insert(K key, V value) {
        root.insert(key, value);
    }


    /*
     * (non-Javadoc)
     * @see BPTreeADT#rangeSearch(java.lang.Object, java.lang.String)
     */
    @Override
    public List<V> rangeSearch(K key, String comparator) {
        if (!comparator.contentEquals(">=") && 
                        !comparator.contentEquals("==") && 
                        !comparator.contentEquals("<=") )
            return new ArrayList<V>();
        return root.rangeSearch(key, comparator);
    }


    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        Queue<List<Node>> queue = new LinkedList<List<Node>>();
        queue.add(Arrays.asList(root));
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<List<Node>>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                sb.append('{');
                Iterator<Node> it = nodes.iterator();
                while (it.hasNext()) {
                    Node node = it.next();
                    sb.append(node.toString());
                    if (it.hasNext())
                        sb.append(", ");
                    if (node instanceof BPTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
                sb.append('}');
                if (!queue.isEmpty())
                    sb.append(", ");
                else {
                    sb.append('\n');
                }
            }
            queue = nextQueue;
        }
        return sb.toString();
    }



    /**
     * This abstract class represents any type of node in the tree
     * This class is a super class of the LeafNode and InternalNode types.
     * 
     * @author sapan
     */
    private abstract class Node {

        // List of keys
        List<K> keys;

        /**
         * Package constructor
         */
        Node() {
            keys= new ArrayList<K>();
        }

        /**
         * Inserts key and value in the appropriate leaf node 
         * and balances the tree if required by splitting
         *  
         * @param key
         * @param value
         */
        abstract void insert(K key, V value);

        /**
         * Gets the first leaf key of the tree
         * 
         * @return key
         */
        abstract K getFirstLeafKey();

        /**
         * Gets the new sibling created after splitting the node
         * 
         * @return Node
         */
        abstract Node split();

        /*
         * (non-Javadoc)
         * @see BPTree#rangeSearch(java.lang.Object, java.lang.String)
         */
        abstract List<V> rangeSearch(K key, String comparator);

        /**
         * 
         * @return boolean
         */
        abstract boolean isOverflow();

        public String toString() {
            return keys.toString();
        }

    } // End of abstract class Node

    /**
     * This class represents an internal node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations
     * required for internal (non-leaf) nodes.
     * 
     * @author sapan
     */
    private class InternalNode extends Node {

        // List of children nodes
        List<Node> children;

        /**
         * Package constructor
         */
        InternalNode() {
            super();
            this.children = new ArrayList<Node>();
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            K key = this.children.get(0).getFirstLeafKey();
            return key;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return (keys.size()-1)>branchingFactor;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(java.lang.Comparable, java.lang.Object)
         */
        void insert(K key, V value) {
            Node child = getChild(key);
            child.insert(key,value);

            if(child.isOverflow()) {
                Node newChild= child.split();
                // where key of newChild belongs in the parent key list
                K keyToPromote = newChild.getFirstLeafKey();
                int indexOfPromotedKey = Collections.binarySearch(keys, keyToPromote);
                if (indexOfPromotedKey <0) {
                    indexOfPromotedKey=-indexOfPromotedKey-1;
                }
                keys.add(indexOfPromotedKey, keyToPromote);
                // the right child of a key always has the index of the key+1, and the new child of a split will always be a right child
                children.add(children.indexOf(child)+1, newChild); 
            }

            if(root.isOverflow()) {
                Node sibling = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(sibling.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(sibling);
                root = newRoot;

//                InternalNode newRoot = new InternalNode();
//                Node rightHalfOfRoot= root.split();
//                Node leftHalfOfRoot=root;
//                newRoot.children.add(leftHalfOfRoot);
//                newRoot.children.add(rightHalfOfRoot);
//                newRoot.keys.add(rightHalfOfRoot.getFirstLeafKey());
//                root=newRoot;       
            }
        }

        private BPTree<K, V>.Node getChild(K key) {
            int indexOfChildKey = Collections.binarySearch(keys, key);   

            if (indexOfChildKey <0) {
                // if <0 child is to the left of the parent key
                indexOfChildKey=-indexOfChildKey-1;
            }
            else {
                indexOfChildKey++;
            }
            return children.get(indexOfChildKey);
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#split()
         */
        Node split() {
            InternalNode rightHalf = new InternalNode();
            int IndexOfSplit= keys.size()/2+1;
            int end = keys.size();
            rightHalf.keys.addAll(this.keys.subList(IndexOfSplit,end));
            rightHalf.children.addAll(this.children.subList(IndexOfSplit, end+1));
            children.subList(IndexOfSplit, end+1).clear();
            keys.subList(IndexOfSplit-1,end).clear();       
            return rightHalf;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(java.lang.Comparable, java.lang.String)
         */
        List<V> rangeSearch(K key, String comparator) {
            return getChild(key).rangeSearch(key, comparator);
        }

    } // End of class InternalNode


    /**
     * This class represents a leaf node of the tree.
     * This class is a concrete sub class of the abstract Node class
     * and provides implementation of the operations that
     * required for leaf nodes.
     * 
     * @author sapan
     */
    private class LeafNode extends Node {

        // List of values
        List<V> values;

        // Reference to the next leaf node
        LeafNode next;

        // Reference to the previous leaf node
        LeafNode previous;

        /**
         * Package constructor
         */
        LeafNode() {
            super();
            values = new ArrayList<>();
            // TODO : Complete
        }


        /**
         * (non-Javadoc)
         * @see BPTree.Node#getFirstLeafKey()
         */
        K getFirstLeafKey() {
            return keys.get(0);
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#isOverflow()
         */
        boolean isOverflow() {
            return (keys.size()-1)>branchingFactor;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#insert(Comparable, Object)
         */
        void insert(K key, V value) {
            int whereToInsert = Collections.binarySearch(keys, key);
            if (whereToInsert >=0) {
                values.add(whereToInsert, value);
                keys.add(whereToInsert, key);
            }else {
                whereToInsert=-whereToInsert-1;
                values.add(whereToInsert, value);
                keys.add(whereToInsert, key);
            }
            // The following is a special Case: this will only happen if the root is currently a leaf node
            // although inserting into a leaf node could cause overflow to propagate up the the root,
            // this type of overflow is checked in the internal node class
            if(root.isOverflow()) {
                InternalNode newRoot = new InternalNode();
                Node rightHalfOfRoot= root.split();
                // root was truncated to its left half when split() was called on it
                Node leftHalfOfRoot=root;
                newRoot.children.add(leftHalfOfRoot);
                newRoot.children.add(rightHalfOfRoot);
                newRoot.keys.add(rightHalfOfRoot.getFirstLeafKey());
                root=newRoot;              
            }
        }

        /**
         * Calling split on a leaf node divides it in half, leave the larger half on the left if the 
         * number keys is odd, and links the linked the nodes together appropriately
         * @see BPTree.Node#split()
         */
        Node split() {
            LeafNode rightHalf = new LeafNode();
            int IndexOfSplit= keys.size()/2+1;
            int end=keys.size();
            rightHalf.keys.addAll(this.keys.subList(IndexOfSplit, end));
            rightHalf.values.addAll(this.values.subList(IndexOfSplit, end));
            this.keys.subList(IndexOfSplit, end).clear();
            this.values.subList(IndexOfSplit, end).clear();
            if(this.next!=null) {
                rightHalf.next=this.next;
                rightHalf.next.previous=rightHalf;
            }
            rightHalf.previous=this;
            this.next=rightHalf;
            return rightHalf;
        }

        /**
         * (non-Javadoc)
         * @see BPTree.Node#rangeSearch(Comparable, String)
         */
        List<V> rangeSearch(K key, String comparator) {
            List<V> valsInRange=new ArrayList<>();
            LeafNode nodeOfIntF=new LeafNode();
            LeafNode nodeOfIntL=new LeafNode();
            int keyLocF=0;
            int keyLocL=0;
            if (comparator.contentEquals("<=")){
                nodeOfIntL=getLastNode(key,this);
                keyLocL=getLastIndex(key,nodeOfIntL.keys);
                valsInRange.addAll(nodeOfIntL.values.subList(0, keyLocL+1));
                while(nodeOfIntL.previous!=null) {
                    nodeOfIntL=nodeOfIntL.previous;
                    valsInRange.addAll(nodeOfIntL.values);
                }
            }
            if (comparator.contentEquals(">=")){
                nodeOfIntF=getFirstNode(key,this);
                keyLocF=getFirstIndex(key,nodeOfIntF.keys);
                valsInRange.addAll(this.values.subList(keyLocF, keys.size()));
                while(nodeOfIntF.next!=null) {
                    nodeOfIntF=nodeOfIntF.next;
                    valsInRange.addAll(nodeOfIntF.values);
                }
            }
            if (comparator.contentEquals("==")){
                nodeOfIntF=getFirstNode(key,this);
                keyLocF=getFirstIndex(key,nodeOfIntF.keys);
                nodeOfIntL=getLastNode(key,this);
                keyLocL=getLastIndex(key,nodeOfIntL.keys);
                if(!nodeOfIntF.equals(nodeOfIntL)) {
                    valsInRange.addAll(this.values.subList(keyLocF, keys.size()));
                }
                if(nodeOfIntF.next!=null&&nodeOfIntL!=null) {
                    while(!nodeOfIntF.next.equals(nodeOfIntL)) {
                        nodeOfIntF=nodeOfIntF.next;
                        valsInRange.addAll(nodeOfIntF.values);
                    }
                }
                valsInRange.addAll(nodeOfIntL.values.subList(0, keyLocL+1));
            }
            return valsInRange;
        }


        private BPTree<K, V>.LeafNode getLastNode(K key, BPTree<K, V>.LeafNode leafNode) {
            if(leafNode.next!=null) {
                if(leafNode.next.keys.contains(key)) {
                    leafNode=getLastNode(key,leafNode.next);             
                }
            }
            return leafNode;
        }

        private BPTree<K, V>.LeafNode getFirstNode(K key, BPTree<K, V>.LeafNode leafNode) {
            if(leafNode.previous!=null) {
                if(leafNode.previous.keys.contains(key)) {
                    leafNode=getFirstNode(key,leafNode.previous);             
                }
            }
            return leafNode;
        }


        private int getFirstIndex(K key, List<K> keys) {
            int keyLoc = Collections.binarySearch(keys, key);
            if(keyLoc-1>0) {
                while(keys.get(keyLoc-1).equals(key)) {
                    keyLoc--;
                    if(keyLoc==0) {
                        break;
                    }
                }
            }
            return keyLoc;
        }

        private int getLastIndex(K key, List<K> keys) {
            int keyLoc = Collections.binarySearch(keys, key);
            if (keyLoc <0) {
                keyLoc=-keyLoc-1; 
            }        
            if(keyLoc+1<keys.size()) {
                while(keys.get(keyLoc+1).equals(key)) {
                    keyLoc++;
                    if(keyLoc+1==keys.size()) {
                        break;
                    }
                }
            }
            return keyLoc;
        }

    } // End of class LeafNode


    /**
     * Contains a basic test scenario for a BPTree instance.
     * It shows a simple example of the use of this class
     * and its related types.
     * 
     * @param args
     */
    public static void main(String[] args) {


        // create empty BPTree with branching factor of 3
        BPTree<Double, Double> bpTree = new BPTree<>(3);

        // create a pseudo random number generator
        Random rnd1 = new Random();

        // some value to add to the BPTree
        Double[] dd = {0.0d, 0.5d, 0.2d, 0.8d};

        // build an ArrayList of those value and add to BPTree also
        // allows for comparing the contents of the ArrayList 
        // against the contents and functionality of the BPTree
        // does not ensure BPTree is implemented correctly
        // just that it functions as a data structure with
        // insert, rangeSearch, and toString() working.
        List<Double> list = new ArrayList<>();
        for (int i = 0; i < 400; i++) {
            Double j = dd[rnd1.nextInt(4)];
            list.add(j);
            bpTree.insert(j, j);
            // for error checking, see it children are being assigned properly after internal node restructuring
            System.out.println("\n\nTree structure:\n" + bpTree.toString());
        }
        List<Double> filteredValues = bpTree.rangeSearch(0.2d, "==");
        System.out.println("Filtered values: " + filteredValues.toString());
    }

} // End of class BPTree
