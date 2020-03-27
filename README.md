This project contains an argumentparser for command line arguments.

An instance of the [ArgumentParser](src/main/java/com/github/koettlitz/opt/ArgumentParser.java) class can parse command line arguments
into an [ArgumentModel](src/main/java/com/github/koettlitz/opt/ArgumentModel.java) object, which contains all the information from the given arguments.
To create an instance of an [ArgumentParser](src/main/java/com/github/koettlitz/opt/ArgumentParser.java), the
[ArgumentParserBuilder](src/main/java/com/github/koettlitz/opt/ArgumentParserBuilder.java) class provides
methods to specify what type of command line arguments the program expects:
```java 
    ArgumentParser parser = ArgumentParserBuilder.begin()
                                                 .addArgument("foo")
                                                 .buildArgument("bar")
                                                     .setMandatory(false)
                                                     .setDescription("description of bar")
                                                     .build()
                                                 .buildOption('b', "bar")
                                                     .setDescription("description of option bar")
                                                     .setExpectsValue(true)
                                                     .build()
                                                 .buildAndGet();
    
    ArgumentModel result = parser.parseArguments("-b", "bValue", "fooValue");
```