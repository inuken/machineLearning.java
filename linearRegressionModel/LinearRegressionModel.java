package linearRegressionModel;

import Jama.Matrix;

/**
 *
 * @author inukenta 線形回帰 多項式の基底関数
 */
public class LinearRegressionModel {
	//多項式の次元数
	private int equation;

	public LinearRegressionModel(int equation) {
		this.equation = equation;
	}

	private double[] getPHI(double x) {
		double[] phi = new double[equation + 1];
		for(int i = 0;i < phi.length;i++) {
			phi[i] = Math.pow(x, i);
		}
		return phi;
	}

	public double[] execution(double[] X, double[] t) {
		double[][] p = new double[X.length][equation + 1];
		for(int i = 0;i < X.length;i++) {
			p[i] = getPHI(X[i]);
		}
		Matrix phi = new Matrix(p);
		return returnValue((phi.transpose().times(phi)).inverse().times(phi.transpose().times(new Matrix(t,t.length))).getArray());
	}

	private double[] returnValue(double[][] value) {
		double[] v = new double[equation +  1];
		for(int i = 0;i< v.length;i++) {
			v[i] = value[i][0];
		}
		return v;
	}
}
