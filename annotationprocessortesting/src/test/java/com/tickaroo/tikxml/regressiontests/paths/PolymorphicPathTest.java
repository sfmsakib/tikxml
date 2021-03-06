/*
 * Copyright (C) 2015 Hannes Dorfmann
 * Copyright (C) 2015 Tickaroo, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tickaroo.tikxml.regressiontests.paths;

import com.tickaroo.tikxml.TestUtils;
import com.tickaroo.tikxml.TikXml;
import com.tickaroo.tikxml.regressiontests.paths.element.Book;
import com.tickaroo.tikxml.regressiontests.paths.element.BookStore;
import com.tickaroo.tikxml.regressiontests.paths.element.Roman;
import com.tickaroo.tikxml.regressiontests.paths.element.ShortStory;
import java.io.IOException;
import java.text.ParseException;
import okio.Buffer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Skip some internal elements
 *
 * @author Hannes Dorfmann
 */
public class PolymorphicPathTest {

  @Test
  public void simple() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    Company company =
        xml.read(TestUtils.sourceForFile("regression/deep_polymprphic_paths.xml"), Company.class);

    Assert.assertEquals(company.persons.size(), 3);
    Boss boss = (Boss) company.persons.get(0);
    Employee employee = (Employee) company.persons.get(1);
    Person person = company.persons.get(2);

    Assert.assertEquals(boss.id, 1);
    Assert.assertEquals(boss.name, "Boss");
    Assert.assertEquals(employee.id, 2);
    Assert.assertEquals(employee.name, "Employee");
    Assert.assertEquals(person.id, 3);

    // Writing xml test

    Buffer buffer = new Buffer();
    xml.write(buffer, company);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><company><department><persons><boss><name>Boss</name><id>1</id></boss><employee><name>Employee</name><id>2</id></employee><person><id>3</id></person></persons></department></company>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Company company2 = xml.read(TestUtils.sourceFrom(xmlStr), Company.class);
    Assert.assertEquals(company, company2);
  }

  @Test
  public void simpleEmpty() throws IOException, ParseException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    Company company =
        xml.read(TestUtils.sourceForFile("regression/deep_polymorphic_paths_empty.xml"), Company.class);

    Assert.assertEquals(company.persons.size(), 0);

    Assert.assertEquals(company.bosses.size(), 1);
    Boss boss = (Boss) company.bosses.get(0);
    Assert.assertEquals(boss.id, 1);
    Assert.assertEquals(boss.name, "Boss");

    Assert.assertEquals(company.statisches.size(), 1);
    Company.TestStatisch statisch = (Company.TestStatisch) company.statisches.get(0);
    Assert.assertEquals(statisch.name, "Test");
    // Writing xml test

    Buffer buffer = new Buffer();
    xml.write(buffer, company);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><company><department><persons/></department><boss><name>Boss</name><id>1</id></boss><testStatisch><name>Test</name></testStatisch></company>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));

    Company company2 = xml.read(TestUtils.sourceFrom(xmlStr), Company.class);
    Assert.assertEquals(company, company2);
  }

  @Test
  public void polymorphicElement() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    BookStore bookStore = xml.read(TestUtils.sourceForFile("regression/bookstore.xml"), BookStore.class);
    Assert.assertNotNull(bookStore.book);
    Assert.assertEquals(bookStore.books.size(), 2);
    Book specialBook = bookStore.book;
    Book otherBook1 = bookStore.books.get(0);
    Book otherBook2 = bookStore.books.get(1);
    Assert.assertTrue(specialBook instanceof Roman);
    Assert.assertTrue(otherBook1 instanceof ShortStory);
    Assert.assertTrue(otherBook2 instanceof Roman);
    Assert.assertEquals(((Roman) specialBook).name, "Roman 1");
    Assert.assertEquals(((ShortStory) otherBook1).name, "ShortStory 1");
    Assert.assertEquals(((Roman) otherBook2).name, "Roman 2");

    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, bookStore);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bookStore><specialBook><roman name=\"Roman 1\"/></specialBook><otherBooks><shortStory name=\"ShortStory 1\"/><roman name=\"Roman 2\"/></otherBooks></bookStore>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));
    BookStore bookStore2 = xml.read(TestUtils.sourceFrom(xmlStr), BookStore.class);
    Assert.assertEquals(bookStore, bookStore2);
  }

  @Test
  public void polymorphicEmptyElement() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    BookStore bookStore = xml.read(TestUtils.sourceForFile("regression/bookstore_empty.xml"), BookStore.class);
    Assert.assertNotNull(bookStore.book);
    Assert.assertEquals(bookStore.books.size(), 0);
    Book specialBook = bookStore.book;
    Assert.assertTrue(specialBook instanceof Roman);

    Assert.assertEquals(((Roman) specialBook).name, "Roman 1");

    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, bookStore);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bookStore><specialBook><roman name=\"Roman 1\"/></specialBook><otherBooks/></bookStore>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));
    BookStore bookStore2 = xml.read(TestUtils.sourceFrom(xmlStr), BookStore.class);
    Assert.assertEquals(bookStore, bookStore2);
  }

  @Test
  public void polymorphicEmptySelfClosingElement() throws IOException {
    TikXml xml = new TikXml.Builder().exceptionOnUnreadXml(false).build();

    BookStore bookStore = xml.read(TestUtils.sourceForFile("regression/bookstore_empty_self_closing.xml"), BookStore.class);
    Assert.assertNotNull(bookStore.book);
    Assert.assertEquals(bookStore.books.size(), 0);
    Book specialBook = bookStore.book;
    Assert.assertTrue(specialBook instanceof Roman);

    Assert.assertEquals(((Roman) specialBook).name, "Roman 1");

    // Writing xml test
    Buffer buffer = new Buffer();
    xml.write(buffer, bookStore);

    String xmlStr =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bookStore><specialBook><roman name=\"Roman 1\"/></specialBook><otherBooks/></bookStore>";
    Assert.assertEquals(xmlStr, TestUtils.bufferToString(buffer));
    BookStore bookStore2 = xml.read(TestUtils.sourceFrom(xmlStr), BookStore.class);
    Assert.assertEquals(bookStore, bookStore2);
  }
}
