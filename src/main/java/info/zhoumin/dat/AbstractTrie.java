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

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;

/**
 * This class provides some basic {@link Trie} functionality and 
 * utility methods for actual {@link Trie} implementations.
 */
abstract class AbstractTrie<K, V> extends AbstractMap<K, V> 
    implements Serializable, Trie<K, V> {
  
  private static final long serialVersionUID = -6358111100045408883L;
  
  /**
   * The {@link Analyzer} that's being used to build the 
   * PATRICIA {@link Trie}
   */
  protected final Analyzer<? super K> keyAnalyzer;

  /** 
   * Constructs a new {@link Trie} using the given {@link Analyzer} 
   */
  public AbstractTrie(Analyzer<? super K> keyAnalyzer) {
    this.keyAnalyzer = Tries.notNull(keyAnalyzer, "keyAnalyzer");
  }
  
  /**
   * Returns the {@link Analyzer} that constructed the {@link Trie}.
   */
  public Analyzer<? super K> getKeyAnalyzer() {
    return keyAnalyzer;
  }
  
  @Override
  public K selectKey(K key) {
    Map.Entry<K, V> entry = select(key);
    return entry != null ? entry.getKey() : null;
  }
  
  @Override
  public V selectValue(K key) {
    Map.Entry<K, V> entry = select(key);
    return entry != null ? entry.getValue() : null;
  }
    
  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    buffer.append("Trie[").append(size()).append("]={\n");
    for (Map.Entry<K, V> entry : entrySet()) {
      buffer.append("  ").append(entry).append("\n");
    }
    buffer.append("}\n");
    return buffer.toString();
  }
  
//  /**
//   * An utility method for calling {@link Analyzer#compare(Object, Object)}
//   */
//  final boolean compareKeys(K key, K other) {
//    if (key == null) {
//      return (other == null);
//    } else if (other == null) {
//      return (key == null);
//    }
//    
//    return keyAnalyzer.compare(key, other) == 0;
//  }
  
  /**
   * A basic implementation of {@link Entry}
   */
  abstract static class BasicEntry<K, V> implements Map.Entry<K, V>, Serializable {
    
    private static final long serialVersionUID = -944364551314110330L;

    protected K key;
    
    protected V value;
    
    private transient int hashCode = 0;
    
    public BasicEntry(K key) {
      this.key = key;
    }
    
    public BasicEntry(K key, V value) {
      this.key = key;
      this.value = value;
    }
    
    /**
     * Replaces the current key and value with the provided
     * key &amp; value
     */
    public V setKeyValue(K key, V value) {
      this.key = key;
      this.hashCode = 0;
      return setValue(value);
    }
    
    @Override
    public K getKey() {
      return key;
    }
    
    @Override
    public V getValue() {
      return value;
    }
    
    @Override
    public V setValue(V value) {
      V previous = this.value;
      this.value = value;
      return previous;
    }
    
    @Override
    public int hashCode() {

      if(hashCode == 0) {
        hashCode = (key != null ? key.hashCode() : 0);
      }

      return hashCode;
    }
    
    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      } else if (!(o instanceof Map.Entry<?, ?>)) {
        return false;
      }
      
      Map.Entry<?, ?> other = (Map.Entry<?, ?>)o;
      if (Tries.areEqual(key, other.getKey()) 
          && Tries.areEqual(value, other.getValue())) {
        return true;
      }
      return false;
    }
    
    @Override
    public String toString() {
      return key + "=" + value;
    }
  }
}