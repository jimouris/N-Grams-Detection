#N-Grams-Detection

An n-gram is a contiguous sequence of N items from a given sequence of text or speech. The items can be phonemes, syllables, letters, words or base pairs according to the application. An n-gram of size 1 is referred to as a unigram, size 2 is a bigram and size 3 is a trigram. In our approach, n-grams are words and are unigrams. More information about n-grams can be found [here.](https://en.wikipedia.org/wiki/N-gram)

For example, we will use n-gram like the figure below.

![screen shot 2016-12-24 at 20 24 00](https://cloud.githubusercontent.com/assets/11991105/21468421/3fa6e49c-ca18-11e6-8b7b-c27fdf47fd88.png)

**Basic Target** of N-Grams-Detection repository, is to **detect** specific list of n-grams in any text file.

##Pre-computations

Our program obtains all n-grams (with various N) in memory, by reading an n-gram file (see CLI arguments section below), and constructs two data-structures (HashMap). The first HashMap (occurrence-HashMap) counts the occurrences of every word in n-gram file and the second HashMap (index-HashMap) has the important words based on occurrence-HashMap as key and lists of all related n-grams as value.

##Search

After Pre-computation procedure, our program checks if text words exist in index-HashMap. If the searched word is in the index-HashMap, our program searches in the text backwards and forward to confirm if the related text matches with the specific n-gram. To speedup the search, we invented a "centralized text", namely one line of input file is a sub-file. This action will parallel our experiment using threads for every line of our input file. This input file is created by the converter.sh (shell script) which takes as arguments all the sub-files and creates our centralized file (ftexts.dat).



##Command line arguments
Our application accepts the following command line arguments:

```
usage: Main
  -i         N-grams-file
             (default argument: input.dat)
                
  -f         text-file
             (default argument: text_stream.dat)
                
  -o         output-file (matched N-grams) 
             (default argument: System.out)
```


##Running program with Maven

Change main's arguments in pom.xml file and run the following commands:

```
mvn clean install
MAVEN_OPTS="-Xmx10240m" mvn exec:java
```

# Evaluation

##Testbed

DNT3 TEAM runs several experiments to improve the algorithm (four approaches), these based on a physical machine with the following specs:

* Intel Core 4790k (4 cores / 8 threads @ 4GHz)
* 8 GB RAM (DDR3)
* 500 GB Solid State Drive
* OS: Ubuntu 16.04 (Xenial Xerus)

In the next figule, you may find the different kind of techniques and times of our approaches. In N-Grams-Detection master branch you will find only the final approach.

![screen shot 2016-12-24 at 22 14 50](https://cloud.githubusercontent.com/assets/11991105/21468653/6f0ec336-ca26-11e6-9550-8a37d5cee4cc.png)

