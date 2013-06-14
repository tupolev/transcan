package com.facelift;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Comparator;

public class TransUnitNodeComparator implements Comparator<Node> {
	@Override
	public int compare(Node o1, Node o2) {
		Integer n1 = Integer.parseInt(((Element)o1).getAttribute("id"));
		Integer n2 = Integer.parseInt(((Element)o2).getAttribute("id"));
		return n1.compareTo(n2);
	}
}
