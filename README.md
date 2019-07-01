# Getting-Started Guide

## Requirements

- 64-bit Ubuntu operating system
- Java 7 (or higher) 
- Python 2.x



## Setup Instruction

### Doop Framework

#### Datalog engine

Running Doop Framework requires Datalog engine that computes new facts from initial facts and inference rules given by Doop framework. Please execute the following command in your terminal to make sure your system has one of them.

```
$ bloxbatch -help
```

If you need to install Datalog engine, please visit [this page](http://snf-705535.vm.okeanos.grnet.gr/agreement.html). The page provides a deb package of an academic version of LogicBlox v3 engine.

#### Doop configuration

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


