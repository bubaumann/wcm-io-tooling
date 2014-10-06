/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.tooling.netbeans.sightly.completion.classLookup;

import io.wcm.tooling.netbeans.sightly.completion.classLookup.MemberLookupResult;
import io.wcm.tooling.netbeans.sightly.completion.BaseTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Test for MemberLookupResult
 */
public class MemberLookupResultTest extends BaseTest {

  /**
   * Tests the current hack for collection detection
   */
  @Test
  public void testCollectionDetection() {
    String test = "java.util.List<foobar>";
    assertEquals(test, "foobar", new MemberLookupResult(null, null, test).getReturnType());
    test = "java.util.Set<foobar>";
    assertEquals(test, "foobar", new MemberLookupResult(null, null, test).getReturnType());
    test = "java.util.Map<foobar>";
    assertEquals(test, "foobar", new MemberLookupResult(null, null, test).getReturnType());
    test = "java.util.Iterator<foobar>";
    assertEquals(test, "foobar", new MemberLookupResult(null, null, test).getReturnType());
    test = "java.lang.Enum<foobar>";
    assertEquals(test, "foobar", new MemberLookupResult(null, null, test).getReturnType());
    test = "java.util.Collection<foobar>";
    assertEquals(test, "foobar", new MemberLookupResult(null, null, test).getReturnType());
    test = "java.util.Map<foobar,ipsum>";
    assertEquals(test, "foobar", new MemberLookupResult(null, null, test).getReturnType());
    test = "java.util.Map";
    assertEquals(test, "java.util.Map", new MemberLookupResult(null, null, test).getReturnType());
    test = "SomeArray[]";
    assertEquals(test, "SomeArray", new MemberLookupResult(null, null, test).getReturnType());
  }

}
