package matrixFactorization;

import java.text.DecimalFormat;
import Jama.Matrix;

public class MatrixFactorization {
	//イテレーション
	private static int iteration = 1000;
	//特徴量数
	private static int topic = 4;

	public static void main(String[] args) {
		double[][] m = { { 5, 0, 6, 2, 2, 9, 0 },
				{ 7, 5, 0, 1, 0, 6, 0 },
				{ 1, 0, 1, 8, 1, 8, 1 },
				{ 4, 3, 0, 0, 0, 5, 0 },
				{ 2, 0, 4, 3, 6, 6, 0 } };

		Matrix matrix = new Matrix(m);
		Matrix[] rowcolBox = new  MatrixFactorization().factorize(matrix);

		Matrix rowMatrix = rowcolBox[0];
		Matrix colMatrix = rowcolBox[1];

		print(rowMatrix);
		print(colMatrix);
		print(matrix);
		print(rowMatrix.times(colMatrix));
	}

	//行列を出力する
	private static void print(Matrix matrix) {
		DecimalFormat df = new DecimalFormat("0.00");
		for(int i = 0;i < matrix.getRowDimension();i++) {
			for(int j = 0;j < matrix.getColumnDimension();j++) {
				System.out.print(df.format(matrix.get(i,j)) + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	//もとの行列との差を計算する
	private double difcost(Matrix matrix,Matrix rowcolMatrix) {
		double cost = 0;

		for(int i = 0;i < matrix.getRowDimension();i++) {
			for(int j = 0;j < matrix.getColumnDimension();j++) {
				cost += Math.pow(matrix.get(i, j)-rowcolMatrix.get(i,j), 2);
			}
		}
		return cost;
	}

	//行列を更新する
	private Matrix matrixUpdate(Matrix num,Matrix den,Matrix mat) {
	Matrix reslutMatrix = new Matrix(num.getRowDimension(), num.getColumnDimension());
		for(int i= 0;i < num.getRowDimension();i++) {
			for(int j = 0;j < num.getColumnDimension();j++) {
				reslutMatrix.set(i,j,mat.get(i, j) * num.get(i, j) / den.get(i, j));
			}
		}
		return reslutMatrix;
	}

	private Matrix[] factorize(Matrix matrix) {
		//行列をランダム値で初期化
		Matrix rowMatrix = Matrix.random(matrix.getRowDimension(),topic);
		Matrix colMatrix = Matrix.random(topic,matrix.getColumnDimension());

		//最大でitelationの数だけ操作を繰り返す
		for(int i = 0; i < iteration;i++) {
			Matrix rowcolMatrix = rowMatrix.times(colMatrix);

			//現在の差を計算
			double cost = difcost(matrix,rowcolMatrix);

			if(cost == 0)break;

			//行を更新
			colMatrix = matrixUpdate(rowMatrix.transpose().times(matrix), rowMatrix.transpose().times(rowMatrix).times(colMatrix), colMatrix);

			//列を更新
			rowMatrix = matrixUpdate(matrix.times(colMatrix.transpose()), rowMatrix.times(colMatrix).times(colMatrix.transpose()), rowMatrix);
		}
		Matrix[] rowcolBox = {rowMatrix, colMatrix};
		return rowcolBox;
	}
}
