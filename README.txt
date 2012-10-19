HOW TO USE

- Example configuration:

<plugin>
  <groupId>ro.isdc.wro4j</groupId>
  <artifactId>wro4j-maven-plugin</artifactId>
  <version>${wro4j.version}</version>
  <executions>
    <execution>
      <id>combine-all-js-and-css-code</id>
      <phase>compile</phase>
      <goals>
        <goal>run</goal>
      </goals>
      <configuration>
        <wroManagerFactory>no.bekk.wro4j.compass.CompassConfigurableWroManagerFactory</wroManagerFactory>
        <extraConfigFile>${basedir}/src/main/webapp/WEB-INF/wro.properties</extraConfigFile>
      </configuration>
    </execution>
  </executions>
  <dependencies>
    <dependency>
      <groupId>no.bekk.wro4j</groupId>
      <artifactId>wro4j-compass</artifactId>
      <version>0.1-SNAPSHOT</version>
    </dependency>
  </dependencies>
</plugin>
