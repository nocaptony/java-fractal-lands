# java-fractal-lands
The "FractalLand3D" Java code is a program that generates a 3D fractal landscape using a mesh of textured squares. The landscape's bumpiness can be adjusted through a flatness value provided either as a command-line argument or with a default value. The user can navigate and explore this generated landscape using a set of movement controls similar to a first-person shooter game.

Here's a breakdown of the main components and functionalities of the code:

Imports and Libraries:

The program imports the necessary classes from the javax.swing and java.awt packages to create the graphical user interface (GUI) and manage window components.
FractalLand3D Class:

This class extends JFrame to create the main application window.
It holds constants for default and allowable ranges of the "flatness" parameter that affects the landscape's bumpiness.
The constructor takes a command-line argument (flatness value) and initializes the GUI components.
Constructor:

The constructor of the FractalLand3D class takes a single command-line argument (the flatness value) if provided.
It processes the command-line argument and either uses the provided value or the default value.
The WrapFractalLand3D class is instantiated, passing the determined flatness value to it.
processArgs Method:

This private method processes the command-line arguments and retrieves the flatness value.
If no arguments are provided, the default flatness value is used.
If more than one argument is provided, the usage information is displayed, and the program exits.
getFlatness Method:

This private method attempts to parse the input argument as a double value.
It validates the parsed value to ensure it falls within the acceptable range defined by MIN_FLAT and MAX_FLAT constants.
If the parsed value is not valid, the default flatness value is used.
main Method:

The entry point of the program.
Instantiates the FractalLand3D class to start the application.
Overall, this Java code defines a graphical application that generates a 3D fractal landscape with adjustable bumpiness. Users can navigate and explore the landscape using predefined movement controls. The flatness parameter affects the appearance of the landscape, with higher values yielding smoother landscapes and lower values producing rougher ones. The program provides command-line flexibility for specifying the flatness value or falls back to a default value if no arguments are given.

# Fractal Lands 3D
<img src="java3d.gif" alt="Alt Text">
