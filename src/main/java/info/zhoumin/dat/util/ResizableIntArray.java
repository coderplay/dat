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
package info.zhoumin.dat.util;

import info.zhoumin.dat.paging.Page;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import sun.nio.ch.DirectBuffer;

/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
public final class ResizableIntArray {

  static final int DEFAULT_PAGE_SIZE_BITS = 12;
  static final int DEFAULT_PAGE_SIZE = 1 << DEFAULT_PAGE_SIZE_BITS;
  static final int DAFAULT_PAGE_SIZE_MASK = DEFAULT_PAGE_SIZE - 1;
 
  static final int INT_SIZE_BITS = 2; // 4 bytes

  private List<Page> pages;

  public ResizableIntArray() {
    ByteBuffer bb = ByteBuffer.allocateDirect(DEFAULT_PAGE_SIZE)
                              .order(ByteOrder.nativeOrder());
    Page p = new Page(bb, ((DirectBuffer) bb).address());
    pages = new ArrayList<Page>();
    pages.add(p);
  }

  /**
   * Returns the length of this array. 
   * @return the length of this array. 
   */
  public int length() {
    return pages.size() << DEFAULT_PAGE_SIZE_BITS;
  }

  /**
   */
  public void clear() {
  }

  /**
   * Returns the integer at the specified position in this array.
   *
   * @param index index of the integer to return
   * @return the integer at the specified position in this array.
   * @throws IndexOutOfBoundsException if the index is out of range
   *         (<tt>index &lt; 0 || index &gt;= length()</tt>)
   */
  public int get(int index) {
    int byteIndex = index << INT_SIZE_BITS;
    int pageIndex = (byteIndex >> DEFAULT_PAGE_SIZE_BITS);
    int offset = (byteIndex & DAFAULT_PAGE_SIZE_MASK);
    return pages.get(pageIndex).asByteBuffer().getInt(offset);
  }

  /**
   */
  public void set(int index, int value) {
    int byteIndex = index << INT_SIZE_BITS;
    int pageIndex = (byteIndex >> DEFAULT_PAGE_SIZE_BITS);
    if (pageIndex >= pages.size()) {
      for (int i = 0; i <= pageIndex - pages.size(); i++) {
        ByteBuffer bb =
            ByteBuffer.allocateDirect(DEFAULT_PAGE_SIZE)
                      .order(ByteOrder.nativeOrder());
        Page p = new Page(bb, ((DirectBuffer) bb).address());
        pages.add(p);
      }
    }
    
    int offset = (byteIndex & DAFAULT_PAGE_SIZE_MASK);
    pages.get(pageIndex).asByteBuffer().putInt(offset, value);
  }
  
}
