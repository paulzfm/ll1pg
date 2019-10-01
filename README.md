# ll1pg

A generator that automatically generates a parser class written in Java which
satisfies the context-free grammar (CFG) given as a specification file. The parse engine is
based on LL(1) parsing technique, from top to bottom.

LL1-Parser-Gen will become a part of the decaf project for the undergraduate course _Principles of
Compilation_, Tsinghua University.

### Build

See release page for prebuilt jars.

If you want to build from source, simply type `gradle build`.

### Usage

```
java -jar ll1pg.jar [-parser | -table | -check] <spec file> <output dir>
```

where `<output dir>` is the destination folder for the generated parser, and `<spec file>` is the
path of the specification file. 
The options mean:

- `-parser`: generate a Java parser
- `-table`: generate a LL predication table
- `-check`: simply check if a grammar is LL(1), or else warnings will be reported

See [here](https://github.com/paulzfm/LL1-Parser-Gen/wiki/1.-Specification-File) for the definition of specification file.
And [here](https://github.com/paulzfm/ll1pg/wiki/2.-Resolving-Conflicts) for how we resolve conflicts
even when your grammar is not LL(1). 

We strongly recommend you to read our [wiki](https://github.com/paulzfm/LL1-Parser-Gen/wiki) first.

### Demo Projects

To integrate our tool with your project better, please take a closer look at our
demo project [arith](https://github.com/paulzfm/LL1-Parser-Gen/tree/master/demos/arith), which
implements a simple calculator, and [decaf](https://github.com/decaf-lang/decaf), a compiler for education.
