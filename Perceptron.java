package perceptron;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Perceptron {

	private int N = 100;

	private double[] X = new double[N];

	private double[] Y = new double[N];

	private int[] T = new int[N];

	// 分離平面を 5x + 3y = 1に設定
	private double h(double x, double y) {
		return 5 * x + 3 * y - 1;
	}

	private void dataOccurence() {
		Random ran = new Random();
		for (int i = 0; i < N; i++) {
			X[i] = ran.nextGaussian();
			Y[i] = ran.nextGaussian();
			T[i] = h(X[i], Y[i]) > 0 ? 1 : -1;
		}
	}

	private double[] learning() {
		double[] w = new double[3];
		List<Integer> list;
		int misses,predict;
		double x,y,t;

		while (true) {
			list = new ArrayList<Integer>();

			for (int i = 0; i < N; i++) {
				list.add(i);
			}
			Collections.shuffle(list);

			misses = 0;
			for (int i : list) {
				x = X[i];
				y = Y[i];
				t = T[i];

				predict = w[0] * x + w[1] * y + w[2] > 0 ? 1 : -1;

				if (predict != t) {
					w[0] += x * t;
					w[1] += y * t;
					w[2] += t;
					misses += 1;
				}
			}
			if(misses == 0) break;
		}
		return w;
	}

	public static void main(String[] args) {
		Perceptron p = new Perceptron();
		p.dataOccurence();
		double[] w = p.learning();

		DecimalFormat df = new DecimalFormat("0.00");
		System.out.println(df.format(w[0]) + " x + " + df.format(w[1]) + " y + " + df.format(w[2]));
	}
}
