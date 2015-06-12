package mf;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class MatrixFactorization_SGD {

	// 列行列
	private double[][] U;
	// 行行列
	private double[][] V;
	// 分解する行列
	private Map<Integer,Map<Integer,Double>> mapMatrix;
	// 列行列の列数
	int nu;
	// 行行列の行数
	int nv;
	// 次元数
	int K;
	// 学習率
	final double lr = 1.0e-3;
	// 正則化項
	final double lambda = 1.0e-5;

	private MatrixFactorization_SGD(Map<Integer, Map<Integer, Double>> map,
			int nu, int nv, int component) {
		this.mapMatrix = map;
		this.nu = nu;
		this.nv = nv;
		this.K = component;
	}

	private void init() {
		Random r = new Random();

		U = new double[nu][K];
		for (int i = 0; i < nu; i++) {
			for (int j = 0; j < K; j++) {
				U[i][j] = r.nextDouble();
			}
		}

		V = new double[nv][K];
		for (int i = 0; i < nv; i++) {
			for (int j = 0; j < K; j++) {
				V[i][j] = r.nextDouble();
			}
		}
	}

	private void updateSGD() {
		for(Entry<Integer,Map<Integer,Double>> outer : mapMatrix.entrySet()) {
			for(Entry<Integer,Double> inner : outer.getValue().entrySet()) {
				double sum = 0.0;
				for(int k = 0;k < K;k++) {
					sum += U[outer.getKey()][k] * V[inner.getKey()][k];
				}
				double e = inner.getValue() - sum;
				for(int k = 0;k < K;k++) {
					double oldu = U[outer.getKey()][k];
					U[outer.getKey()][k] += lr * (e * V[inner.getKey()][k] - lambda * V[outer.getKey()][k]);
					V[inner.getKey()][k] += lr * (e * oldu - lambda * V[inner.getKey()][k]);
					if(Double.isNaN(U[outer.getKey()][k]) || Double.isNaN(V[inner.getKey()][k])) {
						System.err.println("Nan error");
						System.exit(0);
					}
				}
			}
		}
	}

	public void iterator(int iteration) {
		init();
		for (int i = 1; i <= iteration; i++)
			updateSGD();
	}

	public double[][] getU() {
		return U;
	}

	public double[][] getV() {
		return V;
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

		MatrixFactorization_SGD mf_sgd = new MatrixFactorization_SGD(map, 5, 7, 4);
		mf_sgd.iterator(10000);
	}
}
