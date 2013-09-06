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


/**
 * @author Min Zhou (coderplay AT gmail.com)
 */
public class IntegerAnalyzer extends AbstractAnalyzer<Integer> {
  public static final IntegerAnalyzer INSTANCE = new IntegerAnalyzer();

  private static final int BITS_STEP = 4;
  private static final char MASK = 1 << BITS_STEP - 1;
  private static final int STEPS = Integer.SIZE / BITS_STEP;

  @Override
  public int bitsStep() {
    return BITS_STEP;
  }

  @Override
  public boolean incrementToken() {
    return (index++ < STEPS);
  }

  @Override
  public char token() {
    return (char) ((value >> index) & MASK);
  }

}
