# B Tree Project

### Authors
* Tyler Nicholls
* Tim Wilson

### Class: cs321 
### Spring 2017
### Due 5/4/2017

## Overview

This project stores sequences of DNA bases in a B Tree to make it easier to
search the data base for sequences of interest. The gene bank create program 
stores the nodes of bases to disk, and the gene bank search program reads the
gene bank data from the disk and returns the number of times a sequence appeared 
in the original full sequence file.

Needed in README:
describes the layout of the B-Tree file on disk as well as any other relevant observations
Report the improvement in time using a cache of size 100 and 500 in your README file.

### Files / Classes / ? we will need:
* GeneBankCreateBTree
* GeneBankSearch
* BTree
* BTreeNode
* TreeObject
* Parser
* StringToKey
* DiskRead/Write
* BTreeNodeInsert
* BTreeFind
* Cache

### TODO:
- [x] Parser: ignore numbers in listing of bases
- [x] Parser: check for another ORIGIN after //
- [x] Parser: stop parsing at end of listing of bases 
- [x] Parser: not reading all sequences... going by twos, removing the last base
- [x] BTree node: write to disk - design file format
- [x] Create: metadata file
- [x] Search: search algorithm
- [x] Search: rebuild BTree?
- [x] Search seems to have an error for some sequence lengths
- [x] BTree: file offset for root is wrong
- [x] BTree: error with file offsets for root's children
- [x] Cache: need to implement
- [x] debugDump - need to implement
- [x] test4.gbk error - this test file has no DNA sequences
- [ ] default node size - update this calculation, test it works


