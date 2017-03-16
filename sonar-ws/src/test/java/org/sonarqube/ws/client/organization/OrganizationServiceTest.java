/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonarqube.ws.client.organization;

import org.junit.Rule;
import org.junit.Test;
import org.sonarqube.ws.client.ServiceTester;
import org.sonarqube.ws.client.WsConnector;

import static org.mockito.Mockito.mock;

public class OrganizationServiceTest {

  @Rule
  public ServiceTester<OrganizationService> serviceTester = new ServiceTester<>(new OrganizationService(mock(WsConnector.class)));

  private OrganizationService underTest = serviceTester.getInstanceUnderTest();

  @Test
  public void add_member() {
    underTest.addMember("O1", "login-1");

    serviceTester.assertThat(serviceTester.getPostRequest())
      .hasPath("add_member")
      .hasParam("organization", "O1")
      .hasParam("login", "login-1")
      .andNoOtherParam();
  }

  @Test
  public void remove_member() {
    underTest.removeMember("O1", "login-1");

    serviceTester.assertThat(serviceTester.getPostRequest())
      .hasPath("remove_member")
      .hasParam("organization", "O1")
      .hasParam("login", "login-1")
      .andNoOtherParam();
  }
}