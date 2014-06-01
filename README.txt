GIT
Directory structure of tag:
FinalSubmission/								root directory of the tag
	.classpath									contains classpath information for java project
	.project									Eclipse IDE project settings and preferences
	.settings/
		org.eclipse.jdt.core.prefs
	Manifest.txt								Manifest file containing information used when building the compiled project executable jar file.
												Contains location of required libraries and main entry point for program.
	ValueAtRisk.jar								Compiled jar file which can be run by double-clicking or through command line
	commons-math3-3.2-javadoc.jar				javadoc for Apache Commons library for mathematical statistical operations
	commons-math3-3.2-sources.jar				source code for Apache Commons library for mathematical statistical operations
	commons-math3-3.2.jar						compiled jar file for Apache Commons library used for mathematical statistical operations in project.
	DecemberReviewProgramInstructions.txt		instructions for using the program from the InterimSubmission
	Documents/										documentation associated with project e.g. class diagrams, report, user manual
		
	program/									source code folder for Value at Risk project.
		/com/nm/var/gui/							package containing user interface related classes
		/com/nm/var/gui/icons						package containing icons used as classpath resources within the program						
		/com/nm/var/src/							package containing other source code for VaR computation
	test/										JUnit test case folder for Value at Risk project.
		/com/nm/var/test							package containing unit tests
	testing/									contains files used for testing the program, and also recently updated historical stock price data
	
		