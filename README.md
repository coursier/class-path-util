# class-path-util

Utility library to parse class path inputs, and return the corresponding files. It
handles simple blobs (`/dir/*`, `/dir/*.jar`) and Java properties (`${spark.home}/jars/*`).

## Usage

Add it as a dependency:
```scala
//> using lib "io.get-coursier::class-path-util:0.1.1"
```

Import it:
```scala
import coursier.cputil.ClassPathUtil
```

Use it like
```scala
ClassPathUtil.classPath("/foo/${thing}/*;/a/b.jar") // Seq[java.nio.file.Path]
```
