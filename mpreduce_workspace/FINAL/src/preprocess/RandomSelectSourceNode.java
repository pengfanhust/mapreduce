package preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import util.MpRdcConstants;

/**
 * Random select specify number of nodes as the source node.
 * Then we will using a job to calculate the sssp for all these
 * random selected node.
 * The input file is the graph adjacentlist file.
 * We only select the node outdegree > 0.
 * Because if the source node outdegree == 0, the sssp for this node
 * is always infinity, its meaningless.
 * @author pengfan
 *
 */
public class RandomSelectSourceNode {
	private int max;
	private ArrayList<Integer> nodes;

	public RandomSelectSourceNode(String graphAdjacentListFile)
			throws IOException {
		nodes = new ArrayList<Integer>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(graphAdjacentListFile));
			String line = null;
			while ((line = br.readLine()) != null) {
				nodes.add(Integer.parseInt(line
						.split(MpRdcConstants.FIELD_SEPRATOR)[0]));
			}
			max = nodes.size();
		} catch (IOException e) {
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void random(int requiredNumber, String outputFile)
			throws IOException {
		Random randomGenerator = new Random();
		HashSet<Integer> result = new HashSet<Integer>();
		while (true) {
			int i = nodes.get(randomGenerator.nextInt(max));
			if (!result.contains(i)) {
				result.add(i);
				if (result.size() == requiredNumber)
					break;
			}
		}
		output(result, outputFile);
	}

	private void output(HashSet<Integer> result, String outputFile)
			throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outputFile));
			for (Integer i : result) {
				bw.write(i.toString());
				bw.newLine();
			}
			bw.flush();
		} catch (IOException e) {
			throw e;

		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	static public void main(String[] args) {
		try {
			int requiredNum = Integer.parseInt(args[0]);
			RandomSelectSourceNode r = new RandomSelectSourceNode(args[1]);
			r.random(requiredNum, args[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
