package com.strandls.cca.util;

import java.util.ArrayList;
import java.util.List;

public class GeometryUtil {

	private GeometryUtil() {
	}

	public static List<Double> computeCentroid2D(List<List<Double>> coordinates) {
		List<Double> centroid = new ArrayList<>();
		Double x = 0.0;
		Double y = 0.0;
		boolean coordinateFound = false;
		Long n = 0L;
		for (List<Double> c : coordinates) {
			if (!c.isEmpty()) {
				coordinateFound = true;
				x += c.get(0);
				y += c.get(1);
				n++;
			}
		}
		if (!coordinateFound)
			return centroid;
		x /= n;
		y /= n;
		centroid.add(x);
		centroid.add(y);
		return centroid;
	}

	public static List<Double> computeCentroid3D(List<List<List<Double>>> coordinates) {
		List<Double> centroid = new ArrayList<>();
		Double x = 0.0;
		Double y = 0.0;
		boolean coordinateFound = false;
		Long n = 0L;
		for (List<List<Double>> coord : coordinates) {
			if (!coord.isEmpty()) {
				coordinateFound = true;
				List<Double> c = computeCentroid2D(coord);
				x += c.get(0);
				y += c.get(1);
				n++;
			}
		}
		if (!coordinateFound)
			return centroid;
		x /= n;
		y /= n;
		centroid.add(x);
		centroid.add(y);
		return centroid;
	}

	public static List<Double> computeCentroid4D(List<List<List<List<Double>>>> coordinates) {
		List<Double> centroid = new ArrayList<>();
		Double x = 0.0;
		Double y = 0.0;
		boolean coordinateFound = false;
		Long n = 0L;
		for (List<List<List<Double>>> coord : coordinates) {
			if (!coord.isEmpty()) {
				coordinateFound = true;
				List<Double> c = computeCentroid3D(coord);
				x += c.get(0);
				y += c.get(1);
				n++;
			}
		}
		if (!coordinateFound)
			return centroid;
		x /= n;
		y /= n;
		centroid.add(x);
		centroid.add(y);
		return centroid;
	}
}
