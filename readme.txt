README.TXT

Lukas Saul, Feb. 19, 2006

This file contains information about java programs constructed to analyze IBEX-LO TOF data.  These programs were mostly written in 2006 and 2007, with additions through 2012.  Please contact me directly with any further questions via github.  



Basic Instructions:

The main class to run is IbexPhaGui.  This program is a graphical user interface that prompts the user for the name of the input file (which must be an ASCII output from the GSE containing PHA data) and the name of an output file, into which the filtered data will be placed.  This program does not produce graphical plots, but only data for import into another graphical program such as Excel or Kaleidagraph.  

There are several options displayed on the IbexPhaGui screen, which should be self explanatory.  There are three kinds of data output which can be created, all of which will be output to the same file (if their checkboxes are selected).

1) The program can create a filtered TOF histogram (according to filtered parameters entered in the text fields)

2) The program can create filtered TOF scatter data, i.e. a single line for each event that passes the filter showing the four times of flights.  

3) The program can create data histogrammed by MCP voltage, giving rates of all times of flight as well as rates of golden events (as determined by checksum criteria entered in the min. and max. checksum fields).

The other classes which can be run are the _rates classes.  These classes have no GUI and so to change the source file and output file the code must be edited by hand.





Source Files:

GSE_ep.java

	This file contains the code that parses and process PHA data output from the GSE.  This ASCII output is created from the data export function of the GSE.  This program is called by IbexPhaGui.

GSE_rates.java

	This file contains code that parse and process rates data output from the GSE.

GSE_rates_mcp.java

	This file contains code that parses rates data from the GSE and creates histograms in MCP voltage, for use in making MCP curves.

GSE_rates_pac.java

	This file contains code that parses rates data from the GSE and creates histograms in PAC voltage, for use in making PAC scan plots.

GSE_rates_pitch.java
	
	This file contains code that parses rates data from the GSE and creates histograms in table pitch, for use in processing automated pitch scans.

GSE_rates_roll.java

	This file contains code that parses rates data from the GSE and creates histograms in table roll, for use in processing automated roll scans.
	
Histogram.java

	This file is used by many other routines to create a histogram and add data to the appropriate bin.

IbexPhaGui.java

	This is a graphical user interface for processing PHA data from output for from the data export function of the GSE.  Further instructions are available from within the program itself.  To run, enter at command prompt:   
	java IbexPhaGui
	
MCPhaData.java
	
	This is used to process MCP scan from PHA data.  

file.java
	
	A general file utility used to create and read from files more easily.


