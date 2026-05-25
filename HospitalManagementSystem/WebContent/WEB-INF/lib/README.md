# WEB-INF/lib

Place the following JAR file here before deploying to Tomcat:

- `sqlite-jdbc-3.27.2.jar`

## Download

You can download the JAR from Maven Central:

https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.27.2/sqlite-jdbc-3.27.2.jar

Or use the helper script from the project root:

```bash
./download-deps.sh
```

The sandbox where this project was scaffolded has no public internet access, so the JAR could not be bundled in the repo. Once placed here, Tomcat will pick it up automatically at startup.
