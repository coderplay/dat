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

import java.util.Comparator;
import java.util.Set;
import java.util.SortedMap;

import info.zhoumin.dat.analyzer.Analyzer;

/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
public class DoubleArrayTrie<K, V> extends AbstractDoubleArrayTrie<K, V> {

  private static final long serialVersionUID = 7428779721431400033L;
  
  
  DoubleArrayTrie(Analyzer<K> analyzer) {
    super(analyzer);
  }


  @Override
  public java.util.Map.Entry<K, V> select(K key) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public java.util.Map.Entry<K, V> select(K key,
      Cursor<? super K, ? super V> cursor) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public java.util.Map.Entry<K, V>
      traverse(Cursor<? super K, ? super V> cursor) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public SortedMap<K, V> prefixMap(K prefix) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Comparator<? super K> comparator() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public SortedMap<K, V> subMap(K fromKey, K toKey) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public SortedMap<K, V> headMap(K toKey) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public SortedMap<K, V> tailMap(K fromKey) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public K firstKey() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public K lastKey() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    // TODO Auto-generated method stub
    return null;
  }



}
