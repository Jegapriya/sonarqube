/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.source.ws;

import org.junit.Before;
import org.junit.Test;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.source.SourceService;
import org.sonar.server.ws.WsTester;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ShowActionTest {

  SourceService sourceService = mock(SourceService.class);

  WsTester tester;

  @Before
  public void setUp() throws Exception {
    tester = new WsTester(new SourcesWs(new ShowAction(sourceService), mock(RawAction.class), new ScmAction(sourceService, mock(ScmWriter.class)), mock(IndexAction.class)));
  }

  @Test
  public void show_source() throws Exception {
    String componentKey = "src/Foo.java";
    when(sourceService.getLinesAsHtml(eq(componentKey), anyInt(), anyInt())).thenReturn(newArrayList(
      "/*",
      " * Header",
      " */",
      "",
      "public class <span class=\"sym-31 sym\">HelloWorld</span> {",
      "}"
    ));

    WsTester.TestRequest request = tester.newGetRequest("api/sources", "show").setParam("key", componentKey);
    request.execute().assertJson(getClass(), "show_source.json");
  }

  @Test
  public void fail_to_show_source_if_no_source_found() throws Exception {
    String componentKey = "src/Foo.java";
    when(sourceService.getLinesAsHtml(anyString(), anyInt(), anyInt())).thenReturn(null);

    try {
      WsTester.TestRequest request = tester.newGetRequest("api/sources", "show").setParam("key", componentKey);
      request.execute();
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(NotFoundException.class);
    }
  }

  @Test
  public void show_source_with_from_and_to_params() throws Exception {
    String fileKey = "src/Foo.java";
    when(sourceService.getLinesAsHtml(fileKey, 3, 5)).thenReturn(newArrayList(
      " */",
      "",
      "public class <span class=\"sym-31 sym\">HelloWorld</span> {"
    ));
    WsTester.TestRequest request = tester
      .newGetRequest("api/sources", "show")
      .setParam("key", fileKey)
      .setParam("from", "3")
      .setParam("to", "5");
    request.execute().assertJson(getClass(), "show_source_with_params_from_and_to.json");
  }

  @Test
  public void show_source_accept_from_less_than_one() throws Exception {
    String fileKey = "src/Foo.java";
    when(sourceService.getLinesAsHtml(fileKey, 1, 5)).thenReturn(newArrayList(
      " */",
      "",
      "public class <span class=\"sym-31 sym\">HelloWorld</span> {"
    ));
    WsTester.TestRequest request = tester
      .newGetRequest("api/sources", "show")
      .setParam("key", fileKey)
      .setParam("from", "0")
      .setParam("to", "5");
    request.execute();
    verify(sourceService).getLinesAsHtml(fileKey, 1, 5);
  }

}
