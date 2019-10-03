# ll1pg

[![Build Status](https://api.cirrus-ci.com/github/paulzfm/ll1pg.svg)](https://cirrus-ci.com/github/paulzfm/ll1pg)

Yet another parser generation tool.
It can generate either a parser class or a LL prediction table in Java.
The input specification file must specify a LL(1) grammar.

This tool is now a part of the [decaf](https://github.com/decaf-lang/decaf) project for the undergraduate course 
_Principles of Compilation_ in Tsinghua University.

## Build

Prebuilt jars will be updated in the release page.

To build from source, simply type `./gradlew build`.
To assemble a standalone jar with all dependencies, type `./gradlew assembly` and you'll find it at `build/libs/`.

## Usage

```
java -jar ll1pg.jar [-parser | -table | -check] <spec file> <output dir>
```

where `<output dir>` is the destination folder for the generated parser, and `<spec file>` is the
path of the specification file.
The options mean:

- `-parser`: generate a Java parser
- `-table`: generate a LL predication table
- `-check`: simply check if a grammar is LL(1), or else warnings will be reported

See [here](https://github.com/paulzfm/ll1pg/wiki/1.-Specification-File) for the definition of specification file.
And [here](https://github.com/paulzfm/ll1pg/wiki/2.-Resolving-Conflicts) for how we resolve conflicts
even when your grammar is not LL(1).

We strongly recommend you to read our [wiki](https://github.com/paulzfm/ll1pg/wiki) first.

## Demo Projects

To integrate our tool with your project better, please take a closer look at our
demo project [arith](https://github.com/paulzfm/ll1pg/tree/master/demos/arith),
which implements a simple calculator.
Also, the [decaf](https://github.com/decaf-lang/decaf) project is a good practice.
