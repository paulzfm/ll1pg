# LL1-Parser-Gen

LL(1) Parser Generator that automatically generate a parser class written in Java which
satisfies the context-free grammar (CFG) written as a specification file. The parse engine is
based on LL(1) parsing technique, from top to bottom.

LL1-Parser-Gen will become a part of the decaf project for the undergraduate course Principles of
Compilation, Tsinghua University.

### Build

First you should have [sbt](http://www.scala-sbt.org) 0.13 correctly installed. To compile source
code, in project root directory, simply type

```
sbt compile
```

To run tests, type

```
sbt test
```

To pack a standalone jar file, type

```
sbt assembly
```

and you will find the target jar at `target/scala-2.12/LL1-Parser-Gen-assembly-1.0.jar`.

### Usage

After preparing your
[specification file](https://github.com/paulzfm/LL1-Parser-Gen/wiki/1.-Specification-File),
type

```
java -jar pg.jar [-strict] <spec file> <output file>
```

to generate the target parser `<output file>` from your `<spec file>`. Open the option `-strict` to 
run in [strict mode](https://github.com/paulzfm/LL1-Parser-Gen/wiki/2.-Strict-Mode).

We strongly recommend you to read our [wiki](https://github.com/paulzfm/LL1-Parser-Gen/wiki) before
using the tool.

### Demo Projects

To integrate our tool with your project better, please take a closer look at our
demo projects [arith](https://github.com/paulzfm/LL1-Parser-Gen/tree/master/demos/arith), which
implements a simple calculator, and
[decaf](https://github.com/paulzfm/LL1-Parser-Gen/tree/master/demos/decaf), a Java-like language parser.

To build these projects, under the demo project root directory, type `ant` to build (make sure you have
installed the `ant` tool). The target jar file will be generated at `result/` folder.
