package linearRegressionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LogisticRegressionModel {

	private double lr;

	private int iteration;

	public LogisticRegressionModel(int iteration, double lr) {
		this.lr = lr;
		this.iteration = iteration;
	}

	private double sigmoid(double v) {
		return  1.0 / (1 + Math.exp(-v));
	}

	public double[] learning(double[] X,double[] Y, double[] T) {
		double[] w = new double[3];
		Arrays.fill(w, 0.0);

		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0;i < T.length;i++) {
			list.add(i);
		}

		double prediction;

		for(int i = 0; i < this.iteration;i++) {
			Collections.shuffle(list);

			for(int num : list) {
				prediction = sigmoid(w[0] * X[num] + w[1] * Y[num] + w[2]);

				w[0] -= this.lr * (prediction - T[num]) * X[num];
				w[1] -= this.lr * (prediction - T[num]) * Y[num];
				w[2] -= this.lr * (prediction - T[num]) * 1;
			}
			lr *= 0.9;
		}
		return w;
	}
}
