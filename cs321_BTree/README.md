# B Tree Project

* Tyler Nicholls, Tim Wilson

* Class: cs321 
* Spring 2017
* Due 5/4/2017

## Overview

This project stores sequences of DNA bases in a B Tree to make it easier to
search the data base for sequences of interest. The gene bank create program 
stores the nodes of bases to disk, and the gene bank search program reads the
gene bank data from the disk and returns the number of times a sequence appeared 
in the original full sequence file.

## Layout of the B-Tree file on disk: 
At the beginning of the file (file offset 0) four parameters of the B Tree are stored: the
sequence length (an integer), degree (integer, symbolically t), number of nodes (integer), 
and the file offset of the root (long). The number of nodes is not really needed, but it 
provides a convenient check. Each of the nodes are stored sequentially as they are created 
after this initial metadata. Each node is organized starting with the number of keys (integer), 
the 2*t-1 keys themselves (long), the frequency each key occurs (integer), and the file 2*t
offsets of the child nodes (long). Each node is written such that there is space for all of 
the 2*t-1 keys to fit without needing to expand the node. The position of the root node is 
written as a file offset in the metadata at the completion of the B Tree. 



## Improvements in time using the cache
There were significant improvements in the speed at which the B tree was created when using
the cache. These tests were run with test3.gbk, with a degree of 4 and sequence length of 7.

#### GeneBankCreate:
 * with no cache:
 * with cache size 100: 11 seconds
 * with cache size 500: 57 seconds

#### GeneBankSearch:
 * with no cache:
 * with cache size 100:
 * with cache size 500:

### other relevant observations
 This was a hard project. We struggled. But we got it done.
