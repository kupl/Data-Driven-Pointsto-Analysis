# Data-Driven Context-Sensitivity for Points-to Analysis

This implementation aims to reproduce the main results we reported in the paper "[Data-driven context-sensitivity points-to analysis](https://dl.acm.org/citation.cfm?doid=3152284.3133924)" submitted to OOPSLA 2017. Specifically, Table 3 and learned formulas in Appendix B will be reproduced.

## Getting-Started Guide

### Requirements

- 64-bit Ubuntu operating system
- Java 7 (or higher) 
- Python 2.x

### Setup Instruction

##### Datalog engine

Running Doop Framework requires Datalog engine that computes new facts from initial facts and inference rules given by Doop framework. Please execute the following command in your terminal to make sure your system has one of them.

```
$ bloxbatch -help
```

If you need to install Datalog engine, please visit [this page](http://snf-705535.vm.okeanos.grnet.gr/agreement.html). The page provides a deb package of an academic version of LogicBlox v3 engine.

##### Doop configuration

Running Doop framework requires an environment variable `$DOOP_HOME`. Please make sure this variable is set to the directory of "Data-Driven-Doop". Executing the following commands will do the job.

```
Data-Driven-Doop$ export $pwd > ~/.profile
Data-Driven-Doop$ source ~/.profile
```



### Data-Driven Points-to Analysis

Verifying installation is very easy. You can check the installation by running the following command:

```
Data-Driven-Pointsto-Analysis$ ./eval.py s2objH+Data eclipse
```

You will see the results as follows:
```
eclipse
==================================================================
Running selective-2-object-sensitive+heap+Data points-to analysis
analysis time                                              29.23s
polymorphic virtual call sites                              1,066
reachable methods                                           7,971
reachable casts that may fail                                 596
call graph edges                                          134,827
==================================================================
```

The results say that

- The program to be analyzed is eclipse
- This analysis analyzes program with selective-2-object-sensitive+heap+Data
- The main analysis took 24.34 seconds
- The results for each client (poly v-calls, reachable methods, may-fail casts, call-graph-edges)



## Implementation Details

In the implementation, you can find:

- Data-Driven-Doop: This folder contains a Doop framework, which was modified to support our data-driven technique. The modifications include 1) new Datalog rules to allow flexible context-depth handling and 2) additional analyses that are parameterized to context-depth-selection heuristics.
- features: This folder will store feature extraction results during the learning phase.
- eval.py: A Python script for reproducing Table 3. Data-driven context-sensitive analyses this script performs are pre-loaded with the learned heuristics. (Section 2.1)
- learn.py: A Python script for reproducing Table 1 and Appendix B. (Section 2.2)

### Data-Driven-Doop

We implemented our approach on top of a variant of Doop framework, which is introduced along with the paper "[Introspective analysis: context-sensitivity, across the board](https://dl.acm.org/citation.cfm?id=2594320)" by Smaragdakis et al. You can download the original copy of introspective version Doop framework from [here](https://yanniss.github.io/).

Here are a list of important files we changed:

##### 1. logic/context-sensitive.logic

It has basic analysis rules that are shared by all analysis instances. In the original rules, analyzing a method invocation statement results one calling context. Following example is for static invocations:

```
MergeStaticMacro(?callerCtx, ?invocation, ?calleeCtx),
CallGraphEdge(?callerCtx, ?invocation, ?calleeCtx, ?tomethod) <-
  StaticMethodInvocationSkolemOpt(?callerCtx, ?invocation, ?tomethod).
```

In contrary, our implementation takes a invocation fact and assigns context depth from zero to two with respect to our context-sensitivity heuristic $\mathcal{H}$, which is expressed using `Select2` and `Select1`:

```
LMergeStaticMacro2(?callerCtx, ?invocation, ?calleeCtx),
CallGraphEdge(?callerCtx, ?invocation, ?calleeCtx, ?tomethod) <-
  Select2(?tomethod),
  StaticMethodInvocationSkolemOpt(?callerCtx, ?invocation, ?tomethod).

LMergeStaticMacro1(?callerCtx, ?invocation, ?calleeCtx),
CallGraphEdge(?callerCtx, ?invocation, ?calleeCtx, ?tomethod) <-
  !Select2(?tomethod),
  Select1(?tomethod),
  StaticMethodInvocationSkolemOpt(?callerCtx, ?invocation, ?tomethod).

LMergeStaticMacro0(?callerCtx, ?invocation, ?calleeCtx),
CallGraphEdge(?callerCtx, ?invocation, ?calleeCtx, ?tomethod) <-
  !Select2(?tomethod),
  !Select1(?tomethod),
  StaticMethodInvocationSkolemOpt(?callerCtx, ?invocation, ?tomethod).
```

We did the same thing for virtual and special method invocations. You can easily find them by searching predicates `Select1` or `Select2`.

##### 2. logic/select.logic

This file contains definitions of `Select` and `Select2` predicates. The learning script `learn.py` changes the definitions during the learning phase.

##### 3. logic/{012-object-sensitive+heap, s-012-object-sensitive+heap, 012-type-sensitive+heap}

These folders contains modified versions of three context-sensitive analyses; object, selective-hybrid-object, and type sensitivities. Each analysis has `macros.logic` , which defines `MergeMacro` rules for producing `calleeCtx` fact. Similar to the case of `context-sensitive.logic`, we expanded the original rule into three in order to handle multiple context-depths in one place. For example, `012-object-sensitive+heap/macros.logic` has three macros for virtual method invocations as follows:

```c
//2-object-sensitive
#define LMergeMacro2(callerCtx, invocation, hctx, heap, calleeCtx) \
  Context(calleeCtx), \
  ContextFromRealContext[RealHContextFromHContext[hctx], heap] = calleeCtx
//1-object-sensitive
#define LMergeMacro1(callerCtx, invocation, hctx, heap, calleeCtx) \
  Context(calleeCtx), \
  ContextFromRealContext[InitialHeapValue[], heap] = calleeCtx
//context-insensitive
#define LMergeMacro0(callerCtx, invocation, hctx, heap, calleeCtx) \
  Context(calleeCtx), \
  ContextFromRealContext[InitialHeapValue[], InitialHeapValue[]] = calleeCtx
```

We did the same thing for static method invocations.

##### 4. logic/features.logic

This file contains definitions of atomic features. The learning script `learn.py` changes the contents if necessary.

### eval.py

This script is for reproducing Table 3 in the paper. Usage is `./eval.py ANALYSIS BENCHMARK`. It returns points-to analysis report for a given target analysis (`ANALYSIS`) and benchmark (`BENCHMARK`).

`ANALYSIS` can be a category name or an abbreviation of specific analysis. The script supports four categories --- `data`, `intro`, `normal`, and `insens` --- and each category (except for `insens`) means multiple context-sensitivities as follows:

- `data`
  - `s2objH+Data`: Data-driven selective hybrid 2-object-sensitive analysis with a context-sensitive heap
  - `2objH+Data`: Data-driven 2-object-sensitive analysis with a context-sensitive heap
  - `2-typeH+Data`: Data-driven 2-type-sensitive analysis with a context-sensitive heap
- `intro`
  - `introA-s2objH`, `introB-s2objH`: Introspective versions of selective hybrid 2-object-sensitive analysis with a context-sensitive heap
  - `introA-2objH`, `introB-2objH`:  Introspective versions of 2-object-sensitive analysis with a context-sensitive heap
  - `introA-typeH`, `introB-typeH`: Introspective versions of 2-type-sensitive analysis with a context-sensitive heap
- `normal`
  - `s2objH`: Unmodified selective hybrid 2-object-sensitive analysis with a context-sensitive heap
  - `2objH`: Unmodified 2-object-sensitive analysis with a context-sensitive heap
  - `2typeH`: Unmodified 2-type-sensitive analysis with a context-sensitive heap
- `insens`: context-insensitive analysis

For benchmarks, the script supports six large benchmarks in DaCapo suite: `eclipse`, `chart`, `bloat`, `xalan`, `jython`, and `hsqldb`. Also, you can use `all` keyword to run all those benchmarks at once.

### learn.py

This script is for learning Boolean formulas shown in Appendix B in the paper. Usage is simply `./learn.py ANALYSIS`. The argument `ANALYSIS` can be one of three context-sensitivities: `sobj`, `obj`, or `type`. Executing this script mainly does two things: 1) atomic feature extraction and 2) Boolean formula learning. Please note that this procedure fully runs our learning algorithm over the entire training set, so it takes about 2 days in total.

#### 1. Algorithm 2

Functions `learn` and `refine` implement Algorithm 2 in the paper. The `learn` function takes current formulas (i.e., $f_1$ when learning $f_2$) and first evaluates performance and precision of atomic features w.r.t the current formulas (lines 545 ~ 553). This function also obtains upper and lower bounds of precision w.r.t the current formulas. The lower bound will be used to remove useless atomic features and accelerate the learning procedure (lines 557 ~ 560). The upper bound is used to set the precision criteria $\gamma$ (lines 562 ~ 594). Finally, the `learn` function calls `refine` to learn Boolean formulas (line 600).

The `refine` function first makes the initial formula $f$ and worklist $W$ using atomic features (line 374 ~ 376). Than, the function iterates main refinements until the worklist is emptied (line 380 ~ 505). The main refinement loop repeatedly chooses a clause to refine (line 435) and conjoins it with the chosen atomic feature (line 439). Please note that Algorithm 2 always checks satisfiability of the refined formula's precision against $\gamma$ (function `need_further_refinement`) but ours skips the checking as many as possible to accelerate the learning procedure. For example, if the chosen atomic feature can prove more queries than the clause to conjoin, we can safely conclude that the refined formula's precision must be unchanged without calling points-to analysis. 

#### 2. Using learned formulas

When the learning completes, the learned heuristic can be found in the `select.logic` file located in `Data-Driven-Doop/logic`. Analyzing benchmark with the learned heuristic can be done by executing following commands:

```
Data-Driven-Doop$ ./run -jre1.6 ANALYSIS jars/dacapo/BENCHMARK.jar
```

You should replace `ANALYSIS` with the full name of one you used in learning phase:

- `sobj`: `s-012-object-sensitive+heap`
- `obj`: `012-object-sensitive+heap`
- `type`: `012-type-sensitive+heap`

For available benchmarks, you can check `Data-Driven-Doop/jars/dacapo` directory.


## VirtualBox Image

We've archived a ready-to-run version of our implementation in ACM Digital Library ([Link](https://dl.acm.org/citation.cfm?doid=3152284.3133924)). In the page, you can find the download link "Auxiliary Archive" under the "Source Materials" section. Please note that the size of the VirtualBox image is about 7.2 GB and importing it to your machine may require additional 50GB. 
