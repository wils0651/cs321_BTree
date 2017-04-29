# B Tree Project
* Tyler Nicholls
* Elijah Hill
* Tim Wilson


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
- [x] Search: rebuild BTree? no
- [ ] Search seems to have an error
- [x] BTree: file offset for root is wrong
- [ ] Cache:
- [ ] debugDump
