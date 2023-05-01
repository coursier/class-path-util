# class-path-util

Utility library to parse class path inputs, and return the corresponding files. It
handles simple globs (`/dir/*`, `/dir/*.jar`) and Java properties (`${spark.home}/jars/*`).

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
ClassPathUtil.classPath("/foo/${thing}/*:/a/b.jar") // Seq[java.nio.file.Path]
```

## Details

### Separator

Just like the format that `java -cp` accepts, paths are split
by an OS-dependent separator: `;` on Windows, `:` on Linux / macOS
(and other Unix systems). *class-path-util* uses the separator
returned by `java.io.File.pathSeparator`.

### Globs

`*` and `*.jar` are accepted at the end of a path. Case doesn't matter
(`*.JAR` works too, for example). Both `*` and `*.jar` are equivalent,
and make class-path-util add all the JARs (files ending in `.jar`,
comparison done in a case-insensitive way) of the underlying directory
to the class path.

## Version policy

The API of this library is minimal (one singleton with 2 methods).

It intends to respect [semantic versioning](https://semver.org), even
with `0.x` versions (major version `0` subjected to the same
constraints as `1`, `2`, etc.).
