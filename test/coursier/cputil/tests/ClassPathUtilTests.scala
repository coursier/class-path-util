//> using lib "org.scalameta::munit:0.7.29"
//> using lib "com.eed3si9n.expecty::expecty:0.16.0"
//> using lib "com.lihaoyi::os-lib:0.9.1"

package coursier.cputil.tests

import com.eed3si9n.expecty.Expecty.expect
import coursier.cputil.ClassPathUtil

import java.io.File

class ClassPathUtilTests extends munit.FunSuite {

  test("simple") {
    val dep = Seq("com.chuusai::shapeless:2.4.0-M1", "--scala", "2.12.17")
    val input = os.proc("cs", "fetch", "--classpath", dep)
      .call()
      .out.trim()
    val files = os.proc("cs", "fetch", dep)
      .call()
      .out.lines()
      .filter(_.nonEmpty)
      .map(os.Path(_, os.pwd))
      .toVector
    assert(input.nonEmpty)
    assert(files.nonEmpty)

    val res = ClassPathUtil.classPath(input, _ => None).map(os.Path(_, os.pwd))

    expect(files == res)
  }

  val testGlobs = Seq("*", "*.jar", "*.Jar")

  test("star") {
    val initialCp = os.proc("cs", "fetch", "org.scala-lang:scala3-compiler_3:3.1.3")
      .call()
      .out.lines()
      .filter(_.nonEmpty)
      .map(os.Path(_, os.pwd))
    val tmpDir = os.temp.dir(prefix = "class-path-util-tests")
    for (f <- initialCp)
      os.copy.into(f, tmpDir)

    val sep = File.separator

    val expected = Seq(
      "compiler-interface-1.3.5.jar",
      "jline-reader-3.19.0.jar",
      "jline-terminal-3.19.0.jar",
      "jline-terminal-jna-3.19.0.jar",
      "jna-5.3.1.jar",
      "protobuf-java-3.7.0.jar",
      "scala-asm-9.2.0-scala-1.jar",
      "scala-library-2.13.8.jar",
      "scala3-compiler_3-3.1.3.jar",
      "scala3-interfaces-3.1.3.jar",
      "scala3-library_3-3.1.3.jar",
      "tasty-core_3-3.1.3.jar",
      "util-interface-1.3.0.jar"
    )

    for (glob <- testGlobs) {
      val res = ClassPathUtil.classPath(s"$tmpDir$sep$glob", _ => None).map(os.Path(_, os.pwd))
      expect(res.map(_.last) == expected)
    }
  }

  test("star missing dir") {
    val tmpDir = os.temp.dir(prefix = "class-path-util-tests")

    val sep = File.separator

    for (glob <- testGlobs) {
      val res =
        ClassPathUtil.classPath(s"${tmpDir / "foo"}$sep$glob", _ => None).map(os.Path(_, os.pwd))
      expect(res.isEmpty)
    }
  }

  test("property") {
    val initialCp = os.proc("cs", "fetch", "org.scala-lang:scala3-compiler_3:3.1.3")
      .call()
      .out.lines()
      .filter(_.nonEmpty)
      .map(os.Path(_, os.pwd))
    val tmpDir = os.temp.dir(prefix = "class-path-util-tests")
    for (f <- initialCp)
      os.copy.into(f, tmpDir)

    val sep   = File.separator
    val props = Map("test.tmp.dir" -> tmpDir.toString)

    val expected = Seq(
      "compiler-interface-1.3.5.jar",
      "jline-reader-3.19.0.jar",
      "jline-terminal-3.19.0.jar",
      "jline-terminal-jna-3.19.0.jar",
      "jna-5.3.1.jar",
      "protobuf-java-3.7.0.jar",
      "scala-asm-9.2.0-scala-1.jar",
      "scala-library-2.13.8.jar",
      "scala3-compiler_3-3.1.3.jar",
      "scala3-interfaces-3.1.3.jar",
      "scala3-library_3-3.1.3.jar",
      "tasty-core_3-3.1.3.jar",
      "util-interface-1.3.0.jar"
    )

    for (glob <- testGlobs) {
      val res = ClassPathUtil.classPath(s"$${test.tmp.dir}$sep$glob", props.get)
        .map(os.Path(_, os.pwd))
      expect(res.map(_.last) == expected)
    }
  }

  test("substitute properties before splitting") {
    val initialCp = os.proc("cs", "fetch", "org.scala-lang:scala3-compiler_3:3.1.3")
      .call()
      .out.lines()
      .filter(_.nonEmpty)
      .map(os.Path(_, os.pwd))

    val props = Map("test.cp" -> initialCp.map(_.toString).mkString(File.pathSeparator))

    val res = ClassPathUtil.classPath("${test.cp}", props.get)
      .map(os.Path(_, os.pwd))
    expect(res == initialCp)
  }

}
