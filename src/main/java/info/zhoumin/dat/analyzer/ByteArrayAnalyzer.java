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
public class ByteArrayAnalyzer extends AbstractAnalyzer<byte[]> {

  public static final ByteArrayAnalyzer INSTANCE = new ByteArrayAnalyzer();
  
  @Override
  public boolean hasNext() {
    return (index + 1) < value.length;
  }

  @Override
  public byte next() {
    return value[++index];
  }

  @Override
  public ByteBuf rest() {
    int len = value.length - (index + 1);
    ByteBuf bb =
        PooledByteBufAllocator.DEFAULT.directBuffer(len).order(
            ByteOrder.nativeOrder());
    for (int i = len; i > 0; i--) {
      bb.writeByte(value[value.length - i]);
    }
    return bb;
  }

}
