PLUGINJAR=/Library/Java/JavaVirtualMachines/jdk1.7.0_40.jdk/Contents/Home/jre/lib/plugin.jar
#PLUGINJAR=/usr/share/icedtea-web/plugin.jar

all:
	javac -classpath $(PLUGINJAR):. *.java

jar: circuit.jar

src:
	cd .. && zip -r circuit-src.zip src/Makefile src/*.java src/*.txt src/circuits/

run: all
	java Circuit

circuit.jar: all
	jar cfm circuit.jar Manifest.txt *.class *.txt circuits/

clean:
	rm -f *.class circuit.jar