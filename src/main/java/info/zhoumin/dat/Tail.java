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

import java.util.ArrayList;
import java.util.Arrays;

import info.zhoumin.dat.util.ResizableIntArray;
import io.netty.buffer.ByteBuf;

/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
final class Tail {
  private static final int TAIL_START_BLOCKNO = 1;

  private int tailNum;
  private int firstFreeTail;
  private ResizableIntArray nextFreeTail;
  private ByteBuf[] tails;
  
  Tail() {
    this.tailNum = 0;
    this.nextFreeTail = new ResizableIntArray(0);
    this.firstFreeTail = 0;
    this.tails = new ByteBuf[0];
  }

  private void expand(int capacity) {
    if (capacity <= tails.length)
      return;

    nextFreeTail.capacity(capacity);
    tails = Arrays.copyOf(tails, capacity);
  }
  
  private int allocate() {
    int tailIndex;

    if (0 != firstFreeTail) {
      tailIndex = firstFreeTail;
      firstFreeTail = nextFreeTail.get(tailIndex);
    } else {
      tailIndex = tailNum;
      expand(++tailNum);
    }
    nextFreeTail.set(tailIndex, -1);
    return tailIndex;
  }

  /**
   *  Add a new suffix
   *
   * @param t      : the tail data
   * @param suffix : the new suffix
   *
   * @return the index of the newly added suffix.
   *
   * Add a new suffix entry to tail.
   */
  int addSuffix(ByteBuf suffix) {
    int newTail = allocate();
    tails[newTail] = suffix;
    return newTail + TAIL_START_BLOCKNO;
  }



}
