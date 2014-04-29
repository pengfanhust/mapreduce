package ssspcolormultiple;

import java.util.LinkedList;
import java.util.List;

import util.MpRdcConstants;
import util.StringArrayConvertor;

/**
 * represent a node in the graph.
 * it contains:
 * Adjacent node list.
 * A distance array, the ith element represent the 
 * current shortest distance to the ith source node.
 * A color array, the ith element represent the current color
 * correspond to the ith source node.
 * 
 * There is three color: white, gray, black.
 * white means the node havn't been visited yet.
 * gray means the node has been visited, but it's successor havn't been visited yet.
 * black means the node and it's successor has neen visited.
 * 
 * overall, white is the unknown node, gray is the frontier node and black is the
 * known node.
 * 
 * @author pengfan
 *
 */
public class NodeColorMultiple {
	private int[] distances;
	private List<Integer> adjacentList = new LinkedList<Integer>();
	private int[] colors;

	public NodeColorMultiple(int size) {
		distances = new int[size];
		colors = new int[size];
		for (int i = 0; i < size; i++) {
			distances[i] = MpRdcConstants.INFINITY;
			colors[i] = MpRdcConstants.WHITE;
		}
	}

	public NodeColorMultiple(String adjacentList, int[] distance, int[] color) {
		if (!adjacentList.equals("")) {
			String[] adjacentListStr = adjacentList
					.split(MpRdcConstants.ATTR_SEPRATOR);
			for (String id : adjacentListStr) {
				this.adjacentList.add(Integer.parseInt(id));
			}
		}
		this.distances = distance;
		this.colors = color;
	}

	public List<Integer> adjacentList() {
		return adjacentList;
	}

	public int[] distances() {
		return distances;
	}

	public void setDistance(int[] distances) {
		this.distances = distances;
	}

	public void setColor(int[] colors) {
		this.colors = colors;
	}

	public int[] getColors() {
		return colors;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Integer id : adjacentList) {
			sb.append(id.toString()).append(MpRdcConstants.ATTR_SEPRATOR);
		}
		if (sb.length() > 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(MpRdcConstants.FIELD_SEPRATOR)
				.append(StringArrayConvertor.intArrToString(distances))
				.append(MpRdcConstants.FIELD_SEPRATOR)
				.append(StringArrayConvertor.intArrToString(colors));

		return sb.toString();
	}

}
