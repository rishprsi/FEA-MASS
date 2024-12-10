import java.util.*;


public class LocalStiffness{


   public static double[][] calculateLocalStiffness() {
           double E = 7.0e10;
           double nu = 0.3;
           double thickness = 1.0;
           double length = 1.0;
           double crossSectionArea = 1.0;


       int dof = 8; // 4 nodes, 2 degrees of freedom per node (Ux, Uy)
       double[][] stiffness = new double[dof][dof];


       // Constitutive matrix for plane stress (simplified)
       double[][] D = {
               { 1, nu, 0 },
               { nu, 1, 0 },
               { 0, 0, (1 - nu) / 2 }
       };
       double factor = E / (1 - nu * nu);
       for (int i = 0; i < 3; i++) {
           for (int j = 0; j < 3; j++) {
               D[i][j] *= factor;
           }
       }


       // Integration points and weights for 2x2 Gaussian quadrature
       double[] gaussPoints = { -1 / Math.sqrt(3), 1 / Math.sqrt(3) };
       double[] gaussWeights = { 1, 1 };


       // Loop over Gauss points
       for (int i = 0; i < 2; i++) {
           for (int j = 0; j < 2; j++) {
               double xi = gaussPoints[i];
               double eta = gaussPoints[j];
               double weight = gaussWeights[i] * gaussWeights[j];


               // Calculate shape function derivatives
               double[][] dN = calculateShapeFunctionDerivatives(xi, eta, length);
               double[][] B = calculateBMatrix(dN);


               // Stiffness matrix contribution (B^T * D * B)
               double[][] BT = transposeMatrix(B);
               double[][] BTDB = multiplyMatrices(BT, multiplyMatrices(D, B));


               // Scale by thickness and Gauss weight
               for (int k = 0; k < dof; k++) {
                   for (int l = 0; l < dof; l++) {
                       stiffness[k][l] += thickness * weight * BTDB[k][l];
                   }
               }
           }
       }


       return stiffness;
   }


   /**
    * Calculate the shape function derivatives at a given Gauss point.
    */
   private static double[][] calculateShapeFunctionDerivatives(double xi, double eta, double length) {
       double[][] dN = new double[2][4];


       // Derivatives of bilinear shape functions
       dN[0][0] = -(1 - eta) / 4;
       dN[0][1] = (1 - eta) / 4;
       dN[0][2] = (1 + eta) / 4;
       dN[0][3] = -(1 + eta) / 4;


       dN[1][0] = -(1 - xi) / 4;
       dN[1][1] = -(1 + xi) / 4;
       dN[1][2] = (1 + xi) / 4;
       dN[1][3] = (1 - xi) / 4;


       // Scale by element dimensions
       for (int i = 0; i < 4; i++) {
           dN[0][i] /= length;
           dN[1][i] /= length;
       }


       return dN;
   }


   /**
    * Calculate the B matrix from shape function derivatives.
    */
   private static double[][] calculateBMatrix(double[][] dN) {
       double[][] B = new double[3][8];


       for (int i = 0; i < 4; i++) {
           B[0][2 * i] = dN[0][i]; // dN/dx
           B[1][2 * i + 1] = dN[1][i]; // dN/dy
           B[2][2 * i] = dN[1][i]; // dN/dy
           B[2][2 * i + 1] = dN[0][i]; // dN/dx
       }


       return B;
   }


   /**
    * Transpose a matrix.
    */
   private static double[][] transposeMatrix(double[][] matrix) {
       int rows = matrix.length;
       int cols = matrix[0].length;
       double[][] transposed = new double[cols][rows];
       for (int i = 0; i < rows; i++) {
           for (int j = 0; j < cols; j++) {
               transposed[j][i] = matrix[i][j];
           }
       }
       return transposed;
   }


   /**
    * Multiply two matrices.
    */
   private static double[][] multiplyMatrices(double[][] A, double[][] B) {
       int rowsA = A.length;
       int colsA = A[0].length;
       int colsB = B[0].length;
       double[][] result = new double[rowsA][colsB];
       for (int i = 0; i < rowsA; i++) {
           for (int j = 0; j < colsB; j++) {
               for (int k = 0; k < colsA; k++) {
                   result[i][j] += A[i][k] * B[k][j];
               }
           }
       }
       return result;
   }
}

