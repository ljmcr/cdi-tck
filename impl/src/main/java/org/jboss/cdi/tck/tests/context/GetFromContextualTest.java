/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.cdi.tck.tests.context;

import static org.jboss.cdi.tck.cdi.Sections.CONTEXT;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.cdi.tck.AbstractTest;
import org.jboss.cdi.tck.shrinkwrap.WebArchiveBuilder;
import org.jboss.cdi.tck.util.MockCreationalContext;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.test.audit.annotations.SpecAssertion;
import org.jboss.test.audit.annotations.SpecAssertions;
import org.jboss.test.audit.annotations.SpecVersion;
import org.testng.annotations.Test;

@SpecVersion(spec = "cdi", version = "2.0-EDR1")
public class GetFromContextualTest extends AbstractTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return new WebArchiveBuilder().withTestClassPackage(GetFromContextualTest.class).build();
    }

    @Test
    @SpecAssertions({ @SpecAssertion(section = CONTEXT, id = "o") })
    public void testGetMayNotCreateNewInstanceUnlessCreationalContextGiven() {
        Contextual<MySessionBean> mySessionBean = getBeans(MySessionBean.class).iterator().next();
        assert getCurrentManager().getContext(SessionScoped.class).get(mySessionBean) == null;

        Contextual<MyApplicationBean> myApplicationBean = getBeans(MyApplicationBean.class).iterator().next();
        assert getCurrentManager().getContext(ApplicationScoped.class).get(myApplicationBean) == null;

        // Now try same operation with a CreationalContext
        CreationalContext<MySessionBean> myCreationalContext = new MockCreationalContext<MySessionBean>();
        assert getCurrentManager().getContext(SessionScoped.class).get(mySessionBean, myCreationalContext) != null;

        CreationalContext<MyApplicationBean> myOtherCreationalContext = new MockCreationalContext<MyApplicationBean>();
        assert getCurrentManager().getContext(ApplicationScoped.class).get(myApplicationBean, myOtherCreationalContext) != null;
    }

}
