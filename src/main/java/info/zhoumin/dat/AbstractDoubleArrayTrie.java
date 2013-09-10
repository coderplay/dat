/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.zhoumin.dat;

import info.zhoumin.dat.analyzer.Analyzer;
import info.zhoumin.dat.util.ResizableIntArray;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
abstract class AbstractDoubleArrayTrie<K, V> extends AbstractTrie<K, V> {
  private static final long serialVersionUID = -6999526694361413605L;

  /** Maximum trie index value */
  private static final int TRIE_INDEX_MAX = 0x7fffffff;

  private static final int DA_SIGNATURE = 0xDAFCDAFC;
  
  private static final int TRIE_INDEX_ERROR = 0;
  
  private static final int FREE_LIST_BEGIN = 1;

  private static final int DOUBLE_ARRAY_ROOT = 2;
  
  private static final int DA_POOL_BEGIN = 3;


  protected ResizableIntArray base;

  protected ResizableIntArray check;

  protected Tail tail;
  
  protected boolean isDirty;

  
  AbstractDoubleArrayTrie(Analyzer<? super K> keyAnalyzer) {
    super(keyAnalyzer);
    this.base = new ResizableIntArray(3);
    this.check = new ResizableIntArray(3);

    /* DA Header:
     * - Cell 0: SIGNATURE, number of cells
     * - Cell 1: free circular-list pointers
     * - Cell 2: root node
     * - Cell 3: DA pool begin
     */
    base.set(0, DA_SIGNATURE);
    check.set(0, DA_POOL_BEGIN);
    base.set(1, -1);
    check.set(1, -1);
    base.set(2, DA_POOL_BEGIN);
    check.set(2, 0);
    
    this.tail = new Tail();
 
    this.isDirty = true;
  }
  
  
  private int getCheck(int s) {
    return (s < check.capacity()) ? check.get(s) : TRIE_INDEX_ERROR;
  }

  private boolean daIsSeperate() {
    return false;
  }
  
  
  /**
   * Walk the double-array trie from state s, using input character c.
   * If there exists an edge from @a *s with arc labeled @a c, this function
   * returns TRUE and @a *s is updated to the new state. Otherwise, it returns
   * FALSE and @a *s is left unchanged.
   *
   * @param s current state
   * @param c the input character
   *
   * @return boolean indicating success
   */
  private int getNext(int s, int c) {
    int next = base.get(s) + c;
    if (next < check.capacity() && check.get(next) == s) {
      return next;
    }
    return -1;
  }
  
  private boolean inBranch(int sepNode, int c, ByteBuf suffix, V data) {
    int newDA = insertBranch (sepNode, c);

    int newTail = tail.addSuffix(suffix);
//    tail_set_data (trie->tail, new_tail, data);
    base.set(newDA, -newTail);
    return true;
  }
  
  /**
   * @brief Insert a branch from trie node
   *
   * @param d : the double-array structure
   * @param s : the state to add branch to
   * @param c : the character for the branch label
   *
   * @return the index of the new node
   *
   * Insert a new arc labelled with character @a c from the trie node 
   * represented by index @a s in double-array structure @a d.
   * Note that it assumes that no such arc exists before inserting.
   */
  private int insertBranch(int s, int c) {
    int bs = base.get(s);
    int next;
    if (bs > 0) {
      next = bs + c;
      /* if already there, do not actually insert */
      if (next < check.capacity() && check.get(next) == s)
        return next;

      /*
       * if (base + c) > TRIE_INDEX_MAX which means 'next' is overflow, or cell
       * [next] is not free, relocate to a free slot
       */
      if (bs > TRIE_INDEX_MAX - c || !checkFreeCell(next)) {
        List<Integer> symbols = outputSymbols(s);
        int insertIndex = Collections.binarySearch(symbols, c);
        if (insertIndex < 0)
          symbols.add(-insertIndex, c);
        int newBase = findFreeBase(symbols);

        if (TRIE_INDEX_ERROR == newBase)
          return TRIE_INDEX_ERROR;

        relocateBase(s, newBase);
        next = newBase + c;
      }
    } else {
      List<Integer> symbols = new ArrayList<Integer>();
      symbols.add(c);
      int newBase = findFreeBase(symbols);

      if (TRIE_INDEX_ERROR == newBase)
        return TRIE_INDEX_ERROR;
      
      base.set(s, newBase);
      next = newBase + c;
    }
    allocateCell(next);
    check.set(next, s);
    return next;
  }
  
  private void relocateBase(int s, int newBase) {

    int old_base = base.get(s);
    List<Integer> symbols = outputSymbols(s);

    for (int sym : symbols) {

      int oldNext = old_base + sym;
      int newNext = newBase + sym;
      int oldNextBase = base.get(oldNext);

      /* allocate new next node and copy BASE value */
      allocateCell(newNext);
      check.set(newNext, s);
      base.set(newNext, oldNextBase);

      /*
       * old_next node is now moved to new_next so, all cells belonging to
       * old_next must be given to new_next
       */
      /* preventing the case of TAIL pointer */
      if (oldNextBase > 0) {

        int max = Math.min(Byte.MAX_VALUE + 1, TRIE_INDEX_MAX - oldNextBase);
        for (int c = 1; c < max; c++) {
          if (check.get(oldNextBase + c) == oldNext)
            check.set(oldNextBase + c, newNext);
        }
      }

      /* free old_next node */
      freeCell(oldNext);
    }

    /* finally, make BASE[s] point to new_base */
    base.set(s, newBase);
  }
  
  private List<Integer> outputSymbols(int s) {
    List<Integer> syms = new ArrayList<Integer>();

    int bs = base.get(s);
    int max = Math.min(Byte.MAX_VALUE + 1, TRIE_INDEX_MAX - bs);
    for (int c = 1; c < max; c++) {
      if (check.get(bs + c) == s)
        syms.add(c);
    }

    return syms;
  }

  private void freeCell(int cell) {
    /* find insertion point */
    int i = -check.get(FREE_LIST_BEGIN);
    while (i != FREE_LIST_BEGIN && i < cell)
      i = -check.get(i);

    int prev = -base.get(i);

    /* insert cell before i */
    check.set(cell, -i);
    base.set(cell, -prev);
    check.set(prev, -cell);
    base.set(i, -cell);
  }

  private int findFreeBase(List<Integer> symbols) {
    /* find first free cell that is beyond the first symbol */
    int firstSym = symbols.get(0);
    int s = -check.get(FREE_LIST_BEGIN);
    while (s != FREE_LIST_BEGIN && s < (firstSym + DA_POOL_BEGIN)) {
      s = -check.get(s);
    }
    if (s == FREE_LIST_BEGIN) {
      for (s = firstSym + DA_POOL_BEGIN;; ++s) {
        if (!extendDoubleArray(s))
          return TRIE_INDEX_ERROR;
        if (check.get(s) < 0)
          break;
      }
    }

    /* search for next free cell that fits the symbols set */
    while (!fitSymbols(s - firstSym, symbols)) {
      /* extend pool before getting exhausted */
      if (-check.get(s) == FREE_LIST_BEGIN) {
        if (!extendDoubleArray(base.capacity()))
          return TRIE_INDEX_ERROR;
      }

      s = -check.get(s);
    }

    return s - firstSym;
  }
  
  private boolean fitSymbols(int base, List<Integer> symbols) {
    for (int sym : symbols) {
      /*
       * if (base + sym) > TRIE_INDEX_MAX which means it's overflow, 
       * or cell[base + sym] is not free, the symbol is not fit.
       */
      if (base > TRIE_INDEX_MAX - sym || !checkFreeCell(base + sym))
        return false;
    }
    return true;
  }

  private boolean checkFreeCell(int s) {
    return extendDoubleArray(s) && check.get(s) < 0;
  }

  private boolean extendDoubleArray(int toIndex) {
    if (toIndex <= 0 || TRIE_INDEX_MAX <= toIndex)
      return false;

    if (toIndex < base.capacity())
      return true;

    int newBegin = base.capacity();
    base.capacity(toIndex + 1);
    check.capacity(toIndex + 1);

    /* initialize new free list */
    for (int i = newBegin; i < toIndex; i++) {
      check.set(i, -(i + 1));
      base.set(i + 1, -i);
    }

    /* merge the new circular list to the old */
    int freeTail = -base.get(FREE_LIST_BEGIN);
    check.set(freeTail, -newBegin);
    base.set(newBegin, -freeTail);
    check.set(toIndex, -FREE_LIST_BEGIN);
    base.set(FREE_LIST_BEGIN, -toIndex);

    return true;
  }

  private void allocateCell(int cell) {
    int prev = -base.get(cell);
    int next = -check.get(cell);

    /* remove the cell from free list */
    check.set(prev, -next);
    base.set(next, -prev);
  }


  @Override
  public V put(K key, V value) {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }

    int s = DOUBLE_ARRAY_ROOT;
    keyAnalyzer.setValue(key);
    while (keyAnalyzer.hasNext()) {
      int c = (int) keyAnalyzer.next() + 1;
      int next = getNext(s, c);
      if (next < 0) {
        inBranch(s, c, keyAnalyzer.rest(), value);
        return value;
      } else {
        s = next;
      }
    }
    return null;
  }

  @Override
  public V get(Object key) {
//    if (key == null) {
//      throw new NullPointerException("Key cannot be null");
//    }
//    
//    int s = getRoot(); 
//    keyAnalyzer.setValue(key);
//    while(keyAnalyzer.hasNext()) {
//      int c = (int) keyAnalyzer.next() + 1;
//      if((s = walkDoubleArray(s, c)) < 0) {
//        
//      }
//    }
    return null;
  }

}
