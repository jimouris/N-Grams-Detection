JFLAGS = -g
JC = javac
JVM = java -Xmx7168m
PACKAGE = NGram_detection
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	$(PACKAGE)/EditDistance.java \
	$(PACKAGE)/NGram.java \
	$(PACKAGE)/IndexNode.java \
	$(PACKAGE)/Main.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) $(PACKAGE)/*.class

MAIN = $(PACKAGE).Main

run:
	$(JVM) $(MAIN) -i terms2.dat -o out.dat
