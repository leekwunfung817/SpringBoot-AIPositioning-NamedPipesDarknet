package com.dnn.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DarknetOCRString {
	public static final double MAX_LINE_DISTANCE_PROPORTION = 0.5;

	public static String toOcrString(DarknetResultSet resultSet) {
		ArrayList<DarknetResult> results = resultSet.getResultset();

		// line , line content
		LinkedList<LinkedList<DarknetResult>> textSource = new LinkedList<LinkedList<DarknetResult>>();
		while (true) {
			LinkedList<DarknetResult> line = popHighestLine(results);
			if (line == null)
				break;
			textSource.addLast(line);
		}

		StringBuilder strBuilder = new StringBuilder();
		for (LinkedList<DarknetResult> line : textSource) {
			for (DarknetResult character : line) {
				strBuilder.append(character.getName());
			}
		}
		return strBuilder.toString();
	}

	private static LinkedList<DarknetResult> popHighestLine(ArrayList<DarknetResult> results) {
		if (results.size() == 0) {
			return null;
		}
		// DarknetResult list
		LinkedList<DarknetResult> list = new LinkedList<DarknetResult>();

		// get highest character
		DarknetResult highest = popHighest(results);
		list.addFirst(highest);

		// construct highest line
		while (true) {
			DarknetResult cloestest = popSameLineClosetest(highest, results);
			if (cloestest == null) {
				break;
			}
			if (isRightDirection(cloestest, list.getFirst())) {
				list.addFirst(cloestest);
			} else {
				list.addLast(cloestest);
			}
		}

		return list;
	}

	private static boolean isRightDirection(DarknetResult r1, DarknetResult r2) {
		return r1.getCenterX() <= r2.getCenterX();
	}

	private static DarknetResult popHighest(ArrayList<DarknetResult> results) {
		HashMap<Double, DarknetResult> distanceMap = new HashMap<Double, DarknetResult>();
		for (DarknetResult character : results) {
			distanceMap.put(character.getCenterY(), character);
		}
		double min = Collections.min(distanceMap.keySet());
		DarknetResult result = distanceMap.get(min);
		results.remove(result);
		return result;
	}

	private static DarknetResult popLowest(ArrayList<DarknetResult> results) {
		HashMap<Double, DarknetResult> distanceMap = new HashMap<Double, DarknetResult>();
		for (DarknetResult character : results) {
			distanceMap.put(character.getCenterY(), character);
		}
		double max = Collections.max(distanceMap.keySet());
		DarknetResult result = distanceMap.get(max);
		results.remove(result);
		return result;
	}

	private static DarknetResult popSameLineClosetest(DarknetResult r, ArrayList<DarknetResult> results) {
		HashMap<Double, DarknetResult> distanceMap = new HashMap<Double, DarknetResult>();
		for (DarknetResult character : results) {
			if (isSameLine(r, character)) {
				double distance = getDistance(r, character);
				distanceMap.put(distance, character);
			}
		}
		double min = Collections.min(distanceMap.keySet());
		DarknetResult result = distanceMap.get(min);
		results.remove(result);
		return result;
	}

	private static boolean isSameLine(DarknetResult r1, DarknetResult r2) {
		double highDif = r1.getCenterY() - r2.getCenterY();
		highDif = highDif < 0 ? highDif * -1 : highDif;
		return highDif > r1.getHeight() * MAX_LINE_DISTANCE_PROPORTION;
	}

	private static double getDistance(DarknetResult r1, DarknetResult r2) {
		return getDistance(r1.getCenterX(), r2.getCenterX(), r1.getCenterY(), r2.getCenterY());
	}

	private static double getDistance(double x1, double y1, double x2, double y2) {
		double ac = Math.abs(y2 - y1);
		double cb = Math.abs(x2 - x1);
		return Math.hypot(ac, cb);
	}
}
