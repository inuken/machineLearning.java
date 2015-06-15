package svd;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

public class SVDplusplus{

	// 列行列
	private double[][] U;
	// 行行列
	private double[][] I;
	// ユーザバイアス
	private double[] bu;
	// アイテムバイアス
	private double[] bi;
	// 評価平均
	private double mu;
	private double[] rR_u;
	//implicit feedback
	private double[][] y;
	//map<ユーザ、評価したアイテムID群>
	private Map<Integer,Set<Integer>> estimateMap;
	// 分解する行列
	private Map<Integer, Map<Integer, Double>> matrixMap;
	// 列行列の列数
	int nu;
	// 行行列の行数
	int ni;
	// 次元数
	int K;
	// 学習率
	final double lr = 1.0e-3;
	// 正則化項
	final double lambda = 1.0e-5;
	final double lambda2 = 1.0e-3;

	private SVDplusplus(Map<Integer, Map<Integer, Double>> map, int nu,
			int ni, int component) {
		this.matrixMap = map;
		this.nu = nu;
		this.ni = ni;
		this.K = component;
	}

	private void init() {
		Random r = new Random();
		int count = 0;

		U = new double[nu][K];
		I = new double[ni][K];
		bu = new double[nu];
		bi = new double[ni];
		rR_u = new double[nu];
		y = new double[ni][K];
		estimateMap = new HashMap<Integer,Set<Integer>>();

		for (int i = 0; i < nu; i++) {
			for (int j = 0; j < K; j++) {
				U[i][j] = r.nextDouble();
			}
		}

		for (int i = 0; i < ni; i++) {
			for (int j = 0; j < K; j++) {
				I[i][j] = r.nextDouble();
			}
		}

		for (Entry<Integer, Map<Integer, Double>> outer : matrixMap.entrySet()) {
			for (Entry<Integer, Double> inner : outer.getValue().entrySet()) {
				mu += inner.getValue();
				count++;
			}
		}
		mu /= count;

		Set<Integer> set;
		for(Entry<Integer, Map<Integer,Double>> outer : matrixMap.entrySet()) {
			count = 0;
			set = new HashSet<Integer>();
			for(Entry<Integer, Double> inner : outer.getValue().entrySet()) {
				if(inner.getValue() != 0) {
					count++;
					set.add(inner.getKey());
				}
			}
			rR_u[outer.getKey()] = 1.0 / Math.sqrt(count);
			estimateMap.put(outer.getKey(), set);
		}
	}

	private void learn() {
		for (Entry<Integer, Map<Integer, Double>> outer : matrixMap.entrySet()) {
			for (Entry<Integer, Double> inner : outer.getValue().entrySet()) {

				double e = estimateError(outer.getKey(), inner.getKey(),
						inner.getValue());

				bu[outer.getKey()] += lr * (e - lambda * bu[outer.getKey()]);
				bi[inner.getKey()] += lr * (e - lambda * bi[inner.getKey()]);

				for (int k = 0; k < K; k++) {
					double oldu = U[outer.getKey()][k];
					double userContext = 0;

					U[outer.getKey()][k] += lr * (e * I[inner.getKey()][k] - lambda2 * U[outer.getKey()][k]);

					for(int itemNo : estimateMap.get(outer.getKey())) {
						userContext += y[itemNo][k];
						y[itemNo][k] += lr * (e * I[inner.getKey()][k] * rR_u[outer.getKey()] - lambda2 * y[itemNo][k]);
					}
					userContext = oldu + userContext * rR_u[outer.getKey()];
					I[inner.getKey()][k] += lr * (e * userContext - lambda2 * I[inner.getKey()][k]);
				}
			}
		}
	}

	private double estimateError(int outerKey, int innerKey, double rate) {
		double score = mu + bu[outerKey] + bi[innerKey];
		for (int k = 0; k < K; k++) {
			double userScore = U[outerKey][k];
			double sum = 0;
			for(int itemNo : estimateMap.get(outerKey)) {
				sum += y[itemNo][k];
			}
			userScore += sum * rR_u[outerKey];
			score += userScore * I[innerKey][k];
		}
		return rate - score;
	}

	private void print() {
		double[][] computeMatrix = new double[nu][ni];

		for (int i = 0; i < nu; i++) {
			for (int j = 0; j < ni; j++) {
				computeMatrix[i][j] = mu;
			}
		}

		for (int i = 0; i < nu; i++) {
			for (int j = 0; j < ni; j++) {
				computeMatrix[i][j] += bu[i] + bi[j];
				for (int k = 0; k < K; k++) {
					double userScore = U[i][k];
					double sum = 0;
					for(int itemNo : estimateMap.get(i)) {
						sum += y[itemNo][k];
					}
					userScore += sum * rR_u[i];
					computeMatrix[i][j] += userScore * I[j][k];
				}
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		for (int i = 0; i < nu; i++) {
			for (int j = 0; j < ni; j++) {
				System.out.print(df.format(computeMatrix[i][j]) + ",");
			}
			System.out.println();
		}
	}

	public void iterator(int iteration) {
		init();
		for (int i = 1; i <= iteration; i++)
			learn();
		print();
	}

	public double[][] getU() {
		return U;
	}

	public double[][] getI() {
		return I;
	}

	public static void main(String[] args) {
		double[][] mat = { { 5, 0, 6, 2, 2, 9, 0 }, { 7, 5, 0, 1, 0, 6, 0 },
				{ 4, 3, 0, 0, 0, 5, 0 }, { 1, 0, 1, 8, 1, 8, 1 },
				{ 2, 0, 4, 3, 6, 6, 0 } };

		Map<Integer, Map<Integer, Double>> map = new HashMap<Integer, Map<Integer, Double>>();
		Map<Integer, Double> innerMap;
		for (int i = 0; i < mat.length; i++) {
			innerMap = new HashMap<Integer, Double>();
			for (int j = 0; j < mat[0].length; j++) {
				innerMap.put(j, mat[i][j]);
			}
			map.put(i, innerMap);
		}

		SVDplusplus svdpp = new SVDplusplus(map, 5, 7, 4);
		svdpp.iterator(10000);
	}
}
