<p class="jedoc-to-strip">
WARNING: THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE <a href="http://opendatatrentino.github.io/odt-commons-jackson/" target="_blank">PROJECT WEBSITE</a>
</p>

This release allows serializing and deserializing Dict and LocalizedString in Jackson 2.x by installing the OdtCommonsModule in a Jackson ObjectMapper. Also, some basic utility to work with Jackson is provided in the Jacksonizer class.

### Maven

Odt Commons Jackson is available on Maven Central. To use it, put this in the dependencies section of your _pom.xml_:

```
    <dependency>
        <groupId>eu.trentorise.opendata</groupId>
        <artifactId>odt-commons-jackson</artifactId>
        <version>#{version}</version>
    </dependency>
```

In case updates are available, version numbers follows <a href="http://semver.org/" target="_blank">semantic versioning</a> rules.


### Using Jackson Module

You can register `OdtCommonsModule` in your own Jackson ObjectMapper:

```
    ObjectMapper om = new ObjectMapper();
    om.registerModule(new GuavaModule());
    om.registerModule(new OdtCommonsModule());

    String json = om.writeValueAsString(LocalizedString.of(Locale.ITALIAN, "ciao"));
    LocalizedString reconstructedLocalizedString = om.readValue(json, LocalizedString.class);
```

Notice we have also registered the necessary Guava (for immutable collections) and Odt Commons modules (for `Dict` and `LocalizedString`).

To register everything in one command just write:

```
    ObjectMapper om = new ObjectMapper();
    OdtCommonsModule.registerModulesInto(om);
```

#### Simple usage example

```
    ObjectMapper om = new ObjectMapper();
    OdtCommonsModule.registerModulesInto(om);

    String json = om.writeValueAsString(LocalizedString.of(Locale.ITALIAN, "ciao"));
    LocalizedString reconstructedLocalizedString = om.readValue(json, LocalizedString.class);
```

### Logging

Odt Commons Jackson uses native Java logging system (JUL). If you also use JUL in your application and want to see Odt commons jackson logs, you can take inspiration from [odt-commons test logging properties](src/test/resources/odt.commons.logging.properties).  If you have an application which uses SLF4J logging system, you can route logging with <a href="http://mvnrepository.com/artifact/org.slf4j/jul-to-slf4j" target="_blank">JUL to SLF4J bridge</a>, just remember <a href="http://stackoverflow.com/questions/9117030/jul-to-slf4j-bridge" target="_blank"> to programmatically install it first. </a>
