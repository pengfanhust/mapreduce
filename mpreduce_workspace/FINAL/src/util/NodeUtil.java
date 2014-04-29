package util;

import ssspcolormultiple.NodeColorMultiple;

public class NodeUtil {
	public static boolean isNodeInfinity(NodeColorMultiple n)
	{
		for (int i : n.distances()) {
			if (i != MpRdcConstants.INFINITY)
				return false;
		}
		return true;
	}
	
	public static boolean isNodeBlack(NodeColorMultiple n)
	{
		for (int i : n.getColors()) {
			if (i != MpRdcConstants.BLACK)
				return false;
		}
		return true;
	}
	
	public static boolean isNodeGray(NodeColorMultiple n)
	{
		for (int i : n.getColors()) {
			if (i != MpRdcConstants.GRAY)
				return false;
		}
		return true;
	}
}
