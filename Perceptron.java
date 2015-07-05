package perceptron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Perceptron {
	public double[] learning(double[] X, double[] Y, double[] T) {
		double[] w = new double[3];
		Arrays.fill(w, 0.0);

		int misses;
		List<Integer> list;
		int predict;

		list = new ArrayList<Integer>();
		for(int i = 0;i < X.length; i++) {
			list.add(i);
		}

		while (true) {
			Collections.shuffle(list);

			misses = 0;

			for(int i : list) {
				predict = w[0] * X[i] + w[1] * Y[i] + w[2] >= 0 ? 1 : -1;

				if(T[i] != predict) {
					w[0] += X[i] * T[i];
					w[1] += Y[i] * T[i];
					w[2] += T[i];
					misses++;
				}
			}
			if(misses == 0) break;
		}
		return w;
	}
}
