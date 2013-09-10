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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.nio.ByteOrder;

/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
public final class ResizableIntArray {
 
  static final int INT_SIZE = 4; // 4 bytes
  static final int INT_SIZE_BITS = 2; // 4 bytes

  private ByteBuf buffer;

  public ResizableIntArray() {
    buffer =
        PooledByteBufAllocator.DEFAULT.directBuffer().order(
            ByteOrder.nativeOrder());
  }

  public ResizableIntArray(int initialCapacity) {
    buffer =
        PooledByteBufAllocator.DEFAULT.directBuffer(
            initialCapacity << INT_SIZE_BITS).order(ByteOrder.nativeOrder());
  }

  public void capacity(int capacity) {
    buffer.capacity(capacity << INT_SIZE_BITS);
  }
  
  public int capacity() {
    /* In netty4, ByteBuf will be cleared after enlarge it
     * By changing the writer index, this bug would be coped*/
    buffer.writerIndex(buffer.capacity());
    return buffer.capacity() >>  INT_SIZE_BITS;
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
    return buffer.getInt(index << INT_SIZE_BITS);
  }

  /**
   */
  public void set(int index, int value) {
    int address = index << INT_SIZE_BITS;
    buffer.setInt(address, value);
  }
  
}
