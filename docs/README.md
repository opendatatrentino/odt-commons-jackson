<p class="jedoc-to-strip">
WARNING: THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE <a href="http://opendatatrentino.github.io/tod-commons-jackson/" target="_blank">PROJECT WEBSITE</a>
</p>

### Maven

Tod Commons Jackson is available on Maven Central. To use it, put this in the dependencies section of your `pom.xml`:

```
    <dependency>
        <groupId>eu.trentorise.opendata</groupId>
        <artifactId>tod-commons-jackson</artifactId>
        <version>#{version}</version>
    </dependency>
```

In case updates are available, version numbers follows <a href="http://semver.org/" target="_blank">semantic versioning</a> rules.


### Using Jackson Module

Tod Commons Jackson allows serializing and deserializing `Dict` and `LocalizedString` in Jackson 2.x by installing the `TodCommonsModule` in a Jackson `ObjectMapper`. Also, some basic utility to work with Jackson is provided in the `Jacksonizer` class.


You can register `TodCommonsModule` in your own Jackson ObjectMapper:

```
    ObjectMapper om = new ObjectMapper();
    om.registerModule(new GuavaModule());
    om.registerModule(new TodCommonsModule());

    String json = om.writeValueAsString(LocalizedString.of(Locale.ITALIAN, "ciao"));
    LocalizedString reconstructedLocalizedString = om.readValue(json, LocalizedString.class);
```

Notice we have also registered the necessary Guava (for immutable collections) and Tod Commons modules (for `Dict` and `LocalizedString`).

To register everything in one command just write:

```
    ObjectMapper om = new ObjectMapper();
    TodCommonsModule.registerModulesInto(om);
```

#### Simple usage example

```
    ObjectMapper om = new ObjectMapper();
    TodCommonsModule.registerModulesInto(om);

    String json = om.writeValueAsString(LocalizedString.of(Locale.ITALIAN, "ciao"));
    LocalizedString reconstructedLocalizedString = om.readValue(json, LocalizedString.class);
```

### Logging

Tod Commons Jackson uses native Java logging system (JUL). If you also use JUL in your application and want to see Tod commons jackson logs, you can take inspiration from [tod-commons test logging properties](src/test/resources/tod.commons.logging.properties).  If you have an application which uses SLF4J logging system, you can route logging with <a href="http://mvnrepository.com/artifact/org.slf4j/jul-to-slf4j" target="_blank">JUL to SLF4J bridge</a>, just remember <a href="http://stackoverflow.com/questions/9117030/jul-to-slf4j-bridge" target="_blank"> to programmatically install it first. </a>
