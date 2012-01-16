/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jsr299.tck.tests.context.session.event;

import static org.jboss.jsr299.tck.TestGroups.INTEGRATION;
import static org.testng.Assert.assertTrue;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.jsr299.tck.AbstractJSR299Test;
import org.jboss.jsr299.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * <p>
 * This test was originally part of Weld test suite.
 * <p>
 * 
 * @author Jozef Hartinger
 * @author Martin Kouba
 */
@Test(groups = INTEGRATION)
@SpecVersion(spec = "cdi", version = "20091101")
public class SessionScopeEventTest extends AbstractJSR299Test {

    @ArquillianResource(Servlet.class)
    private URL contextPath;

    @Deployment(testable = false)
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(SessionScopeEventTest.class).build();
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = "6.7.2", id = "da"), @SpecAssertion(section = "6.7.2", id = "db"),
            @SpecAssertion(section = "6.7.2", id = "cd") })
    public void test() throws Exception {
        WebClient client = new WebClient();

        TextPage page = client.getPage(contextPath);
        assertTrue(page.getContent().contains("Initialized sessions:1")); // the current session
        assertTrue(page.getContent().contains("Destroyed sessions:0")); // not destroyed yet

        // nothing should change
        page = client.getPage(contextPath);
        assertTrue(page.getContent().contains("Initialized sessions:1"));
        assertTrue(page.getContent().contains("Destroyed sessions:0"));

        // invalidate the session
        page = client.getPage(contextPath + "/invalidate");
        assertTrue(page.getContent().contains("Initialized sessions:1"));
        // the context is destroyed after the response is sent
        // verify in the next request
        assertTrue(page.getContent().contains("Destroyed sessions:0"));

        page = client.getPage(contextPath);
        // new session context was initialized
        assertTrue(page.getContent().contains("Initialized sessions:2"));
        // the previous one was destroyed
        assertTrue(page.getContent().contains("Destroyed sessions:1"));
    }
}