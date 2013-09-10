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

import junit.framework.TestCase;

import org.junit.Test;

/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
public class ResizableIntArrayTest {
  @Test
  public void getterSetter() {
//    ByteBuf bb =
//        PooledByteBufAllocator.DEFAULT.directBuffer().order(
//            ByteOrder.nativeOrder());
//    
//    System.out.println(bb.memoryAddress());
    
    ResizableIntArray ints = new ResizableIntArray();
    TestCase.assertEquals(64, ints.capacity());

    ints.set(0, Integer.MAX_VALUE);
    TestCase.assertEquals(Integer.MAX_VALUE, ints.get(0));
    ints.capacity(4096);
    TestCase.assertEquals(Integer.MAX_VALUE, ints.get(0));

    ints.set(4095, 333);
    TestCase.assertEquals(333, ints.get(4095));
    TestCase.assertEquals(Integer.MAX_VALUE, ints.get(0));

    ints.set(8, 111);
    TestCase.assertEquals(111, ints.get(8));
    TestCase.assertEquals(Integer.MAX_VALUE, ints.get(0));

    ints.capacity(256);
    TestCase.assertEquals(Integer.MAX_VALUE, ints.get(0));
    
    
  }
}
