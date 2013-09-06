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

import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
abstract class AbstractDoubleArrayTrie<K, V> extends AbstractTrie<K, V> {
  
  /** Maximum trie index value */
  private static final int TRIE_INDEX_MAX = 0x7fffffff;

  private static final int DA_SIGNATURE = 0xDAFCDAFC;

  private static final int DA_POOL_BEGIN = 3;

  protected final ResizableIntArray base;

  protected final ResizableIntArray check;
  
  protected boolean isDirty;
  

  
  AbstractDoubleArrayTrie(Analyzer<? super K> keyAnalyzer) {
    super(keyAnalyzer);
    this.base = new ResizableIntArray();
    this.check = new ResizableIntArray();
    
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
    
    this.isDirty = true;
  }

  int getRoot() {
   return 2; 
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
  private int walkDoubleArray(int s, int c) {
    int next = base.get(s) + c;
    if (check.get(next) == s) {
      return next;
    }
    return -1;
  }
  
  private boolean inBranch(int sep, char[] suffix, V data) {
    
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
    if(bs > 0) {
      next = bs + c;
      /* if already there, do not actually insert */
      if (check.get(next) == s)
        return next;
      
      /* if (base + c) > TRIE_INDEX_MAX which means 'next' is overflow,
       * or cell [next] is not free, relocate to a free slot
       */
      if (bs > TRIE_INDEX_MAX - c || !checkFreeCell(next)) {

      }
    }
  }

  @Override
  public V put(K key, V value) {
    if (key == null) {
      throw new NullPointerException("Key cannot be null");
    }
    
    int s = getRoot(); 
    keyAnalyzer.setValue(key);
    while(keyAnalyzer.incrementToken()) {
      int c = (int) keyAnalyzer.token() + 1;
      if((s = walkDoubleArray(s, c)) < 0) {
        
      }
    }
    return null;
  }

}
