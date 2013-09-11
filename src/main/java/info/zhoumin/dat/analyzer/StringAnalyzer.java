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
package info.zhoumin.dat.analyzer;

import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;


/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
public class StringAnalyzer extends AbstractAnalyzer<String> {

  public static final StringAnalyzer INSTANCE = new StringAnalyzer();
  
  @Override
  public boolean hasNext() {
    return (index + 1) < (value.length() << 1);
  }

  @Override
  public byte next() {
    ++index;
    char ch = value.charAt(index >> 1);
    return (byte) ((index & 1) == 0 ? (ch & 0xff) : (ch >> Byte.SIZE & 0xff));
  }

  @Override
  public ByteBuf rest() {
    int len = (value.length() << 1) - (index + 1);
    ByteBuf bb =
        PooledByteBufAllocator.DEFAULT.directBuffer(len).order(
            ByteOrder.nativeOrder());
    for (int i = len; i > 0; i--) {
      int idx = (value.length() << 1) - i;
      char ch = value.charAt(idx >> 1);
      byte b = (byte) ((idx & 1) == 0 ? (ch & 0xff) : (ch >> Byte.SIZE & 0xff));
      bb.writeByte(b);
    }
    return bb;
  }

}
