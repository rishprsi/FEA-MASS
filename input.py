import sys

def generate_fem_input(grid_size, output_file, force_file, boundary_file):
    """
    Generate input files for FEM analysis with configurable grid size and force vector.

    Args:
        grid_size (int): Number of nodes per side (grid_size x grid_size grid).
        output_file (str): Path for the element connectivity file.
        force_file (str): Path for the force vector file.
        boundary_file (str): Path for the boundary conditions file.
    """
    # Open files for writing
    with open(output_file, "w") as element_file, \
         open(force_file, "w") as force_vector_file, \
         open(boundary_file, "w") as boundary_conditions_file:
        
        # Generate elements
        element_id = 0
        for r in range(grid_size - 1):  # Rows of elements
            for c in range(grid_size - 1):  # Columns of elements
                node1 = r * grid_size + c
                node2 = node1 + 1
                node3 = node1 + grid_size + 1
                node4 = node1 + grid_size
                # Write element connectivity
                element_file.write("{0} {1} {2} {3} {4}\n".format(element_id, node1, node2, node3, node4))
                element_id += 1

        # Generate force vector (Fx and Fy for each node in one line)
        force_vector = []
        for node in range(grid_size * grid_size):  # All nodes
            Fx = 100.0 if (node + 1) % grid_size == 0 else 0.0  # Example: Apply Fx=100 on the rightmost nodes
            Fy = 0.0  # Example: No vertical forces
            force_vector.append(Fx)
            force_vector.append(Fy)
        
        # Write the entire force vector in one line
        force_vector_file.write(" ".join(map(str, force_vector)) + "\n")

        # Generate boundary conditions (fixed nodes on the leftmost side)
        for node in range(grid_size):  # Leftmost column
            node_id = node * grid_size
            boundary_conditions_file.write("{0} 0.0 0.0\n".format(node_id))  # Fixed Ux=0, Uy=0

    print("Files generated:\n- Elements: {0}\n- Force vector: {1}\n- Boundary conditions: {2}".format(
        output_file, force_file, boundary_file))


if __name__ == "__main__":
    # Accept grid size as an argument from the command line
    if len(sys.argv) != 2:
        print("Usage: python generate_fem_input.py <grid_size>")
        sys.exit(1)
    
    try:
        grid_size = int(sys.argv[1])
        # print("Value for grid size is",grid_size)
    except ValueError:
        print("Error: Grid size must be an integer.")
        sys.exit(1)
    
    # File paths
    output_file = "input.txt"
    force_file = "force_vector.txt"
    boundary_file = "boundary_conditions.txt"

    # Generate the input files
    generate_fem_input(grid_size, output_file, force_file, boundary_file)
