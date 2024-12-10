// package edu.uw.bothell.css.dsl.mass.apps;
import edu.uw.bothell.css.dsl.MASS.*;
import edu.uw.bothell.css.dsl.MASS.logging.LogLevel;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;

public class FEAMass {
    public static void main(String[] args) {
        // Initialize MASS library
        MASS.setLoggingLevel(LogLevel.ERROR);
        MASS.init();

        // Define a grid size (e.g., 10x10 grid of nodes)
        int gridRows = 10;
        int gridCols = 10;
        int totalNodes = gridRows * gridCols;
               

        // Initialize places and agents
        Places grid = new Places(1, GridPlace.class.getName(), null, gridRows, gridCols);
        Agents elements = new Agents(2, ElementAgent.class.getName(), null, grid,totalNodes);

        // Assign one agent per quadrilateral element
        int[][] position = new int[gridRows][gridCols];
        for (int row = 0; row < gridRows - 1; row++) {
            for (int col = 0; col < gridCols - 1; col++) {
                position[row][0] = (col == 0) ? 1 : -1;//{row, col};
            }
        }

       //Move all agents across the X direction
        double[][] localStiffnessMatrices = LocalStiffness.calculateLocalStiffness();
        for (int col = 0; col < gridCols - 1; col++) {
            elements.callAll(GridPlace.init_, (Object[]) position);
             // Assemble the global stiffness matrix
            elements.callAll(GridPlace.ASSEMBLE_GLOBAL_MATRIX, localStiffnessMatrices);
            elements.callAll(elements.MIGRATE);
            elements.manageAll();
        }
           

        // Solve the system (global stiffness * displacement = force)
        elements.callAll(GridPlace.SOLVE_DISPLACEMENT, null);

        // Print results
        elements.callAll(GridPlace.PRINT_RESULTS, null);

        // Finalize MASS
        MASS.finish();
    }
}

