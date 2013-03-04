buildhealth
===========

Idea
----

A tool that runs after a build and gather metrics to determine if a build was successful or not. Currently we use CI servers to do that but, if you run your build locally, you don't receive any feedback. (A second problem is that all CI servers end up duplicating the code to process the build results).

So, I propose a tool that receive as inputs the results of the build (test results, code coverage files, etc), process them and return as result if the build is GOOD, BAD or SO SO. It should be easy to create plugins for this tool to process new kinds of output. Also, it would be good if the results can be used by other tools to be displayed in different formats. Lastly, it would be good if the tool could store the results from previous builds to tell if the quality has improved or not.


Interface
---------

I think the first interface should be a command line, something like
```
> buildhealth create
Created a new build (12)

> buildhealth add junit /path/to/xmls
Extracted 15 junit tests

> buildhealth add jacoco /path/to/xmls
Extracted jacoco coverage for 15 classes

> buildhealth set id = 1234
Build 12 now has id 1234

> buildhealth set coverage good = 70%
A good coverage is now >= 70%

> buildhealth set coverage warn = 30%
A so so coverage is now >= 30%

> buildhealth report
Your build is GOOD
   Unit tests: PASSED [33 tests]                         // PASSED written in green
   Coverage: 71% [improved, last one was 30%]            // 71% written in green, 30% written in red

> buildhealth report Coverage
Total coverage is 71% [improved, last one was 30%]       // 71% written in green, 30% written in red
   Java: 70% [70 covered lines, 30 missed one, 100 total]
   C++: 90% [90 covered lines, 10 missed one, 100 total]
```

Also, the cli should be based on a nice library that handles all this stuff and that could be used in other places (as an ant target, for example)


Implementation
--------------

Nothing is implemented yet, I'm just playing with some ideas, but I was thinking in implementing this in 2 stages: the first gather the data and put in a big table and the second you summarizes it. The advantage is that this format could group different results of different tools that have the same purpose.

The table would have as 1st column a number and in consecutive columns the specific information of what the number means, from most generic to most specific. So, for eg, for unit tests:
```
35,Unit test,java,junit,passed
1,Unit test,java,junit,failed
22,Unit test,c++,cpp unit,passed
3,Unit test,C#,NUnit,failed
```

So, you'd have plugins to add data to the table and plugins to summarize it.

Some other features that I think could be useful:
 - Store the data in a format that is good to store in SCM (eg git)
 - Store metadata about the build (where the data came from, etc)
 - Be able to add ids and parents to builds (to correctly determine if it improved or not)


Language
--------

I don't know which language is better to implement this. I'm between java (to reuse the lib in other places) and python (easier to call from command line?)
