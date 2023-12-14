package org.example;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MatrixMultiplication {


    public static int[][] multiplyMatrices(int[][] matrixA, int[][] matrixB) {
        int numRowsA = matrixA.length;
        int numColsA = matrixA[0].length;
        int numRowsB = matrixB.length;
        int numColsB = matrixB[0].length;

        if (numColsA != numRowsB) {
            throw new IllegalArgumentException("Несовместимые матрицы для умножения");
        }

        int[][] result = new int[numRowsA][numColsB];

        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new MatrixMultiplicationTask(matrixA, matrixB, result, 0, numRowsA, 0, numColsB));

        return result;
    }

    public static class MatrixMultiplicationTask extends RecursiveTask<Void> {
        private static final int THRESHOLD = 2;

        private int[][] matrixA;
        private int[][] matrixB;
        private int[][] result;
        private int startRow;
        private int endRow;
        private int startCol;
        private int endCol;

        public MatrixMultiplicationTask(int[][] matrixA, int[][] matrixB, int[][] result,
                                        int startRow, int endRow, int startCol, int endCol) {
            this.matrixA = matrixA;
            this.matrixB = matrixB;
            this.result = result;
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
        }

        @Override
        protected Void compute() {
            int numRows = endRow - startRow;
            int numCols = endCol - startCol;

            if (numRows <= THRESHOLD || numCols <= THRESHOLD) {
                for (int i = startRow; i < endRow; i++) {
                    for (int j = startCol; j < endCol; j++) {
                        for (int k = 0; k < matrixB.length; k++) {
                            result[i][j] += matrixA[i][k] * matrixB[k][j];
                        }
                    }
                }
            } else {
                int midRow = (startRow + endRow) / 2;
                int midCol = (startCol + endCol) / 2;

                invokeAll(
                        new MatrixMultiplicationTask(matrixA, matrixB, result, startRow, midRow, startCol, midCol),
                        new MatrixMultiplicationTask(matrixA, matrixB, result, startRow, midRow, midCol, endCol),
                        new MatrixMultiplicationTask(matrixA, matrixB, result, midRow, endRow, startCol, midCol),
                        new MatrixMultiplicationTask(matrixA, matrixB, result, midRow, endRow, midCol, endCol)
                );
            }

            return null;
        }
    }
}
