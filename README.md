# VennMaker

License: MIT License

Contact: contact@mynetworkmap.com

Web: http://www.vennmaker.com

**Feedback is very welcome (bugs, issues, suggestions, stories, questions).**

**Volunteers are welcome, too.**

**Donations are welcome.**


## Building and running

### Requirements

* JDK 8 (the sources use Java 7 syntax and `java.util.Base64`; newer JDKs have not been tested)
* Apache Maven 3
* A graphical desktop — VennMaker is a Swing application

On Debian/Ubuntu:

```bash
sudo apt install openjdk-8-jdk maven
```

### Build

```bash
mvn compile
```

### Run

```bash
mvn dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -cp "target/classes:$(cat target/classpath.txt)" gui.VennMaker
```

The classpath file only has to be regenerated when the dependencies in `pom.xml`
change; after a code change, `mvn compile` followed by the `java -cp ...` line is
enough.

### Notes

* **Start VennMaker from the project root.** It resolves `./icons` relative to the
  working directory and aborts with a "missing folders" dialog otherwise.
* **`mvn exec:java` does not work.** `FileOperations.getAbsolutePath()` locates the
  installation directory via `URLClassLoader.getSystemResource("gui/VennMaker.class")`.
  The exec plugin loads the application in its own class loader, so that call returns
  `null` and startup fails with a `NullPointerException`.
* The optional plugins in `module/` are loaded at startup. If one of them cannot be
  loaded, a `ClassNotFoundException` is printed to the console; the application itself
  still starts.
