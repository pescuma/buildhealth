buildhealth
===========

[Download buildhealth-0.1.jar](http://dl.bintray.com/pescuma/buildhealth/buildhealth-0.1.jar?direct)

[![Build Status](https://travis-ci.org/pescuma/buildhealth.png?branch=master)](https://travis-ci.org/pescuma/buildhealth) (you can see the buildhealth results of buldhealth at the end of the console logs) 

Idea
----

A tool that runs after a build and gather metrics to determine if a build was successful or not. Currently we use CI servers to do that but if you run your build locally you don't receive any feedback. (A second problem is that all CI servers end up duplicating the code to process the build results, but this is a problem too big to think about now).

So, this is a tool that receive as inputs the results of the build (test results, code coverage files, etc), process them and output if the build is GOOD, PROBLEMATIC or SO SO. 

Some goals for this project: it should be easy to create plugins for this tool to process new kinds of output. Also, it would be good if the results can be used by other tools (to be displayed in different formats). Lastly, it would be good if the tool could store the results from previous builds to tell if the quality has improved or not.


Downloading
-----------

From the [Releases tab](https://github.com/pescuma/buildhealth/releases) you can download a jar that contains both the cli and ant tasks or the cli as an exe for Windows. To call the cli from linux you need to call `java -jar buildhealth-0.X.jar`


Interface
---------

You can use this through two interfaces: the command line and inside an `ant` build script.

Let's first see some examples

### Command line interface (cli)

The way it works is: you first create a build, then you add data to it and finally you call report to see the status of your build. At this point you can also call the notify command to receive nofications with the report.


##### Creates a new build
This must be done before adding data
```
> buildhealth new
```

##### Add junit tests
```
> buildhealth add junit /path/to/xmls
File processed: /path/to/xmls/TEST-test1.xml
File processed: /path/to/xmls/TEST-test2.xml
File processed: /path/to/xmls/TEST-test2.xml
```

##### Add coverage
```
> buildhealth add jacoco /path/th/xmls
File processed: /path/th/xmls/coverage.xml
```

##### Compute tasks
Tasks are `TODO`/`FIXME`/`HACK`/`XXX` comments on your code
```
> buildhealth compute tasks /path/to/sources
File computed: /home/pescuma/.buildhealth/computed/tasks.csv
File processed: /home/pescuma/.buildhealth/computed/tasks.csv
```

##### List available configurations
```
> buildhealth config list
Coverage:
    coverage good = <no limit> [Minimun coverage for a Good build]
    coverage warn = <no limit> [Minimun coverage for a So So build]
    coverage <type> good = <no limit> [Minimun coverage for a Good build]
    coverage <type> warn = <no limit> [Minimun coverage for a So So build]
    coverage maintype = instruction,line [Which coverage type will represent the global coverage (can have more than one, separated by ',', with the most import ant first)]

Disk usage:
    diskUsage reportWithTags = true [If file tags should be used in the full report tree]

Performance:
    performace runsPerS good = <no limit> [Minimun runs per second for a Good build]
    performace runsPerS warn = <no limit> [Minimun runs per second for a Good build]
    performace ms good = <no limit> [Maximun run time (ms) for a Good build]
    performace ms warn = <no limit> [Maximun run time (ms) for a Good build]
    performace report = <ms if both available, else what is available> [How to show the agregated results (runsPerS or ms)]

Static analysis:
    staticanalysis good = <no limit> [Maximun munber of violations for a Good build]
    staticanalysis warn = <no limit> [Maximun munber of violations for a So So build]
    staticanalysis <language> good = <no limit> [Maximun munber of violations for a Good build]
    staticanalysis <language> warn = <no limit> [Maximun munber of violations for a So So build]
    staticanalysis <language> <framework> good = <no limit> [Maximun munber of violations for a Good build]
    staticanalysis <language> <framework> warn = <no limit> [Maximun munber of violations for a So So build]

Growl notifier:
    notification growl enabled = true [Growl server name]
    notification growl server = localhost [Growl server name]
    notification growl port = 23053 [Growl server port]
```


##### Set config parameters
```
> buildhealth config set coverage good = 70
```

##### Get report
```
> buildhealth report
Your build is GOOD
    Unit tests: PASSED [109 tests, 109 passed (1.4 s)]
    Coverage: 73% [class: 50%, method: 56%, line: 67%, branch: 57%, instruction: 73%, complexity: 52%]
    Static analysis: 103 [FindBugs: 17, PMD: 60, Tasks: 26]
    Lines of code: 9.1k [2.0k blank, 6.9k code, 194 comment, in 164 files]
    Disk usage: 10.5 MiB
```

##### Get detailed report on one item
```
> buildhealth report "Unit tests"
Unit tests: PASSED [109 tests, 109 passed (1.4 s)]
    Java: PASSED [109 tests, 109 passed (1.4 s)]
...
```

##### Notify
```
> buildhealth notify
Sent growl notification
```

##### Start web server to see the report as an HTML page
```
> buildhealth webserver
Web server started at http://localhost:8190
Available urls:
  http://localhost:8190/            => Browse the reports as a web site
  http://localhost:8190/report.json => Report in JSON format
  http://localhost:8190/report.xml  => Report in XML format

Hit Enter to stop...
```

### `ant` task

There is one `ant` task that does everything. In this case you don't need to call `new` or `report`, using the task means that the new is the first thing and the report is the last one.

```xml
<taskdef resource="org/pescuma/buildhealth/ant/antlib.xml" classpath="${build.dir}/buildhealth-${version}.jar" />

<buildhealth home="${reports.dir}/builhealth" notify="true">
   <config key="coverage good" value="70" />
	<add-junit>
		<fileset dir="${build.dir}">
			<include name="**/reports/junit/*" />
		</fileset>
	</add-junit>
	<add-jacoco dir="${jacoco.dir}" />
	<compute-tasks>
		<fileset dir="${source.dir}">
			<include name="**/*.java" />
		</fileset>
	</compute-tasks>
</buildhealth>
```

### `add` x `compute`

If you noticied, in the previous examples, some commands start with _add_ and others start with _compute_. The difference is what the command will do.

 - `add` commands only read results processed by other tools (internally they are called extractors). Most of the commands are like this.
 - `compute` commands execute some task (for example parse the sources to find tasks), outputting and intermediary file with results, and then add that data to the build results (internally they call an extractor after doing its job).


## Existing commands:

 - `add cloc`: adds lines of code information from [CLOC](http://cloc.sourceforge.net/)
 - `add diskusage`: adds disk usage (reads from file system)
 - `add aunit`: adds unit test results from [Aunit](http://libre.adacore.com/tools/aunit/)
 - `add boosttest`: adds unit test results from [Boost Test](http://www.boost.org/doc/libs/1_53_0/libs/test/doc/html/index.html)
 - `add cpptest`: adds unit test results from [CppTest](http://cpptest.sourceforge.net/)
 - `add cppunit`: adds unit test results from [CppUnit](http://sourceforge.net/projects/cppunit/)
 - `add fpcunit`: adds unit test results from [fpcunit](http://wiki.freepascal.org/fpcunit)
 - `add junit`: adds unit test results from [JUnit](http://junit.org/)
 - `add mnmlstcUnittest`: adds unit test results from [MNMLSTC Unittest](https://github.com/mnmlstc/unittest)
 - `add mstest`: adds unit test results from [MSTest](http://msdn.microsoft.com/en-us/library/ms182486.aspx)
 - `add nunit`: adds unit test results from [NUnit](http://www.nunit.org/)
 - `add phpunit`: adds unit test results from [PHPUnit](https://github.com/sebastianbergmann/phpunit/)
 - `add tusar`: adds unit test results from _Thales Unified Software Analysis Report_
 - `add emma`: adds coverage information from [EMMA](http://emma.sourceforge.net/)
 - `add jacoco`: adds coverage information from [JaCoCo](http://www.eclemma.org/jacoco/)
 - `add dotCover`: adds coverage information from [dotCover](http://www.jetbrains.com/dotcover/)
 - `add vtest-coverage`: adds coverage information from a [vtest](http://www.jetbrains.com/dotcover/) XML file. Information on how to convert the .coverage to an XML can be found [here](http://reportgenerator.codeplex.com/wikipage?title=Visual%20Studio%20Coverage%20Tools&ANCHOR#vstestconsoleexe)
 - `add pmd`: adds static analysis results from [PMD](http://pmd.sourceforge.net/)
 - `add cpd`: adds static analysis results from [CPD](http://pmd.sourceforge.net/snapshot/cpd-usage.html)
 - `add findbugs`: adds static analysis results from [FindBugs](http://findbugs.sourceforge.net/)
 - `add gendarme`: adds static analysis results from [Gendarme](http://www.mono-project.com/Gendarme)
 - `add fxcop`: adds static analysis results from [FxCop](http://msdn.microsoft.com/en-us/library/bb429476%28v=vs.80%29.aspx)
 - `add stylecop`: adds static analysis results from [StyleCop](https://stylecop.codeplex.com/)
 - `add japex`: adds performance test results from [Japex](http://japex.java.net/)
 - `add be`: adds tasks from [BugsEverywhere](http://bugseverywhere.org/) (a call to `be show -xml`)
 - `add tfs-wi`: adds tasks from [TFS work items](http://msdn.microsoft.com/en-us/library/ms181268%28v=vs.90%29.aspx) (a call to `tfpt query /collection:uri /format:xml`)
 - `add github-issues`: adds tasks from [GitHub issues](https://github.com/)
 - `compute loc`: compute and add lines of code using [CLOC](http://cloc.sourceforge.net/). This requires `perl` in your path if you are on Linux or MacOS.
 - `compute tasks`: compute and add tasks from the source code
 
The ant tasks have the same name, with an - in the midde. For eg. `add pmd` command is equivalent to the ant task `add-pmd`.

Implementation
--------------

This is implemented in 2 stages: the first gather the data and put in a big table (extractors) and the second summarizes it (analysers). The advantage is that this format could group different results of different tools that have the same purpose.

The table have as 1st column a number and in consecutive columns the specific information of what the number means, from most generic to most specific. So, for eg, for unit tests:
```
35,Unit test,Java,JUnit,passed
1,Unit test,Java,JUnit,failed
22,Unit test,C++,CppUnit,passed
3,Unit test,C#,NUnit,failed
```

So, you'd have plugins to add data to the table and plugins to summarize it.

To store the data, you can specify a folder or by default it uses a folder in your home called `.buildhealth`.

Some other features that I think could be useful (but aren't implemented yet):
 - Store the data in a format that is good to store in VCS (eg git)
 - Store metadata about the build (where the data came from, etc)
 - Add option to have multiple build data, so we can report the diffs
 - Be able to add ids and parents to builds (to correctly determine if it improved or not)
 - A jenkins plugin to show colected data


Thanks
------

 - Kohsuke Kawaguchi for creating [Jenkins](http://jenkins-ci.org/), from which I copied a lot of ideas and code to parse JUnit reports
 - Gregory Boissinot who created the [xUnit Jenkins plugin](https://wiki.jenkins-ci.org/display/JENKINS/xUnit+Plugin) and a library to parse most unit test results handled
 - Ulli Hafner who created the [Task Scanner Jenkins plugin](https://wiki.jenkins-ci.org/display/JENKINS/Task+Scanner+Plugin), from which I copied the code to parse tasks
 - All the library developers for all the libs I'm using.


License
-------

(The MIT License)

Copyright (c) 2013 Ricardo Pescuma Domenecci

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
