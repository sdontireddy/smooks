/*-
 * ========================LICENSE_START=================================
 * Smooks Core
 * %%
 * Copyright (C) 2020 Smooks
 * %%
 * Licensed under the terms of the Apache License Version 2.0, or
 * the GNU Lesser General Public License version 3.0 or later.
 * 
 * SPDX-License-Identifier: Apache-2.0 OR LGPL-3.0-or-later
 * 
 * ======================================================================
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ======================================================================
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
 * =========================LICENSE_END==================================
 */
package org.smooks.delivery.nested;

import org.junit.Test;
import static org.junit.Assert.*;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.smooks.Smooks;
import org.smooks.javabean.lifecycle.BeanContextLifecycleEvent;
import org.smooks.javabean.lifecycle.BeanContextLifecycleObserver;
import org.smooks.javabean.lifecycle.BeanLifecycle;
import org.smooks.payload.JavaResult;
import org.smooks.payload.StringResult;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class NestedExecutionVisitorTest {

	@Test
    public void test() throws IOException, SAXException {
        Smooks smooks = new Smooks(getClass().getResourceAsStream("config-01.xml"));
        StringResult result = new StringResult();
        JavaResult beans = new JavaResult();
        final List<String> orderItems = new ArrayList<String>();

        smooks.getApplicationContext().addBeanContextLifecycleObserver(new BeanContextLifecycleObserver() {
            public void onBeanLifecycleEvent(BeanContextLifecycleEvent event) {
                if(event.getLifecycle() == BeanLifecycle.REMOVE && event.getBeanId().getName().equals("orderItem")) {
                    orderItems.add((String) event.getBean());
                }
            }
        });

        smooks.filterSource(new StreamSource(getClass().getResourceAsStream("order-message.xml")), result, beans);

        XMLUnit.setIgnoreWhitespace(true);
        XMLAssert.assertXMLEqual(new InputStreamReader(getClass().getResourceAsStream("order-message.xml")), new StringReader(result.toString()));

        assertEquals("header", beans.getBean("header"));
        assertEquals("trailer", beans.getBean("trailer"));
        assertEquals(2, orderItems.size());
    }
}
