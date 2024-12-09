// package edu.uw.bothell.css.dsl.mass.apps;
import edu.uw.bothell.css.dsl.MASS.*;


import java.util.Date;

import java.util.HashMap;
import java.util.Map;

public class FEAMass {
    public static void main(String[] args) {
        // Initialize MASS library
        MASS.init();

        // Define a grid size (e.g., 10x10 grid of nodes)
        int gridRows = 10;
        int gridCols = 10;
        int totalNodes = gridRows * gridCols;

        // Initialize places and agents
        Places grid = new Places(1, GridPlace.class.getName(), null, gridRows, gridCols);
        Agents elements = new Agents(2, ElementAgent.class.getName(), null);

        // Assign one agent per quadrilateral element
        for (int row = 0; row < gridRows - 1; row++) {
            for (int col = 0; col < gridCols - 1; col++) {
                int[] position = {row, col};
                elements.add(position);
            }
        }

        // Calculate local stiffness matrices and assemble global stiffness matrix
        elements.callAll(ElementAgent.COMPUTE_LOCAL_STIFFNESS, null);

        // Assemble the global stiffness matrix
        grid.callAll(GridPlace.ASSEMBLE_GLOBAL_MATRIX, null);

        // Solve the system (global stiffness * displacement = force)
        grid.callAll(GridPlace.SOLVE_DISPLACEMENT, null);

        // Print results
        grid.callAll(GridPlace.PRINT_RESULTS, null);

        // Finalize MASS
        MASS.finish();
    }
}

