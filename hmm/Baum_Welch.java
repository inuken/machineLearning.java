package hmm;

/**
 *
 * @author inukenta Left to Right HiddenMarkovModelをbaum-welchアルゴリズムにより学習
 */

public class Baum_Welch {
	// 状態遷移確率
	private double[][] A = null;
	// シンボル出力確率
	private double[][] B = null;

	/**
	 *
	 * @param N
	 *            状態数:一番最後の状態を抜いた状態数
	 * @param K
	 *            シンボル数
	 */
	public void init(int N, int K) {
		A = new double[N][N + 1];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N + 1; j++) {
				A[i][j] = 1.0 / N + 1;
			}
		}

		B = new double[N][K];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < K; j++) {
				B[i][j] = 1.0 / K;
			}
		}
	}

	// forwardアルゴリズム
	private double[][] forward(int[] O) {
		double[][] alpha = new double[O.length + 1][A.length + 1];
		for (int t = 0; t < alpha.length; t++) {
			for (int j = 0; j < alpha[0].length; j++) {
				alpha[t][j] = 0.0;
			}
		}
		alpha[0][0] = 1.0;
		for (int t = 1; t < alpha.length; t++) {
			for (int j = 0; j < alpha[t].length; j++) {
				if (j < alpha[t].length - 1) {
					alpha[t][j] += alpha[t - 1][j] * A[j][j] * B[j][O[t - 1]];
				}
				if (j > 0) {
					alpha[t][j] += alpha[t - 1][j - 1] * A[j - 1][j]
							* B[j - 1][O[t - 1]];
				}
			}
		}
		return alpha;
	}

	// backwardアルゴリズム
	private double[][] backward(int[] O) {
		double[][] beta = new double[O.length + 1][A.length + 1];
		for (int t = 0; t < beta.length; t++) {
			for (int j = 0; j < beta[0].length; j++) {
				beta[t][j] = 0.0;
			}
		}
		beta[O.length][A.length] = 1.0;
		for (int t = beta.length - 2; t >= 0; t--) {
			for (int j = beta[t].length - 1; j >= 0; j--) {
				if (j < beta[t].length - 1) {
					beta[t][j] = beta[t + 1][j + 1] * A[j][j + 1] * B[j][O[t]]
							+ beta[t + 1][j] * A[j][j] * B[j][O[t]];
				}
			}
		}
		return beta;
	}

	// 期待値ステップ
	private double[][][] expectation(int[] O, double[][] alpha, double[][] beta) {
		double[][][] gamma = new double[O.length][A.length][A.length + 1];

		for (int t = 0; t < gamma.length; t++) {
			for (int i = 0; i < gamma[t].length; i++) {
				for (int j = 0; j < gamma[t][i].length; j++) {
					gamma[t][i][j] = (alpha[t][i] * A[i][j] * B[i][O[t]] * beta[t + 1][j])
							/ beta[0][0];
				}
			}
		}
		return gamma;
	}

	// 最大化ステップ
	private void maximization(int[] O, double[][][] gamma) {
		double a, b;

		// 状態遷移確率の再計算
		for (int i = 0; i < A.length; i++) {
			for (int j = 0; j < A[i].length; j++) {
				a = 0.0;
				b = 0.0;
				for (int t = 0; t < gamma.length; t++) {
					a += gamma[t][i][j];
					for (int _j = 0; _j < A[i].length; _j++) {
						b += gamma[t][i][_j];
					}
				}
				A[i][j] = a / b;
			}
		}

		// シンボル出力確率の再計算
		for (int j = 0; j < B.length; j++) {
			for (int o = 0; o < B[j].length; o++) {
				a = 0.0;
				b = 0.0;
				for (int t = 0; t < gamma.length; t++) {
					for (int k = 0; k < gamma[t][j].length; k++) {
						if (o == O[t])
							a += gamma[t][j][k];
						b += gamma[t][j][k];
					}
				}
				B[j][o] = a / b;
			}
		}
	}

	/**
	 *
	 * @param O
	 *            時系列データ
	 *
	 * @param max_step
	 *            最大学習回数
	 */
	public void learning(int[] O, int max_step) {
		double prev_likelihood = 0.0;
		int step = 0;
		double[][] alpha;
		double[][] beta;
		double[][][] gamma;

		while (true) {
			long start = System.currentTimeMillis();
			alpha = forward(O);
			beta = backward(O);

			if (!(prev_likelihood < beta[0][0]))
				break;
			if (max_step != 0 & step > max_step) {
				break;
			}

			prev_likelihood = beta[0][0];
			gamma = expectation(O, alpha, beta);
			maximization(O, gamma);
			step += 1;
			System.err.println("iteration:" + step + "\tend");
			System.out.println((System.currentTimeMillis() - start) + "ms");
		}
	}

	public double[][] getStateTransitionProbability() {
		return A;
	}

	public double[][] getSymbolOutputProbability() {
		return B;
	}

}
