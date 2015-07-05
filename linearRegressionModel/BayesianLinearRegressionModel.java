package linearRegressionModel;

import Jama.Matrix;

/**
 *
 * @author inukenta ベイジアン線形回帰 ガウス基底関数
 */
public class BayesianLinearRegressionModel {

	private int equation;

	private Matrix Sigma;
	private Matrix mu;

	private final double alpha = 0.1;
	private final double beta  =9.0;

	private double s;
	private double width;

	public BayesianLinearRegressionModel(int equation, double s, double width) {
		this.equation = equation;
		this.s = s;
		this.width = width;
	}

	private double[] getPHI(double x) {
		double[] phi = new double[equation];
		double c = 0.0;
		phi[0] = 1.0;
		for (int i = 1; i < phi.length; i++) {
				phi[i] = Math.exp((-Math.pow((x - c), 2)) / (2 * Math.pow(s, 2)));
				c += width;
		}
		return phi;
	}

	private Matrix makeIdentityMatrix(int num) {
		Matrix mat = new Matrix(num, num);
		for (int i = 0; i < mat.getRowDimension(); i++) {
			for (int j = 0; j < mat.getColumnDimension(); j++) {
				if (i == j)
					mat.set(i, j, alpha);
			}
		}
		return mat;
	}

	public void execution(double[] X, double[] t) {
		double[][] p = new double[X.length][equation];
		for (int i = 0; i < X.length; i++) {
			p[i] = getPHI(X[i]);
		}
		Matrix phi = new Matrix(p);
		Sigma = ((makeIdentityMatrix(equation)).plus(((phi.transpose()).times(beta)).times(phi))).inverse();
		mu = ((Sigma.times(beta)).times(phi.transpose())).times(new Matrix(t, t.length));
	}

	private double[] returnValue(double[][] value) {
		double[] v = new double[equation];
		for (int i = 0; i < v.length; i++) {
			v[i] = value[i][0];
		}
		return v;
	}

	public double[][] getSigma() {
		return Sigma.getArray();
	}

	public double[] getMu() {
		return returnValue(mu.getArray());
	}
}
