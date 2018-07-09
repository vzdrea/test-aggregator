/*
 * Copyright (c) 2018 UTStarcom, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.utstar.test.impl.demo;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.MessageData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.message.data.News;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TestProvider {
    private static final Logger LOG = LoggerFactory.getLogger(TestProvider.class);
    private final DataBroker dataBroker;
    private ListenerRegistration<?> dataTreeChangeListenerRegistration;
    private DataTreeChangeListener<News> listener;

    public TestProvider(DataBroker dataBroker, DataTreeChangeListener<News> listener) {
        this.dataBroker = dataBroker;
        this.listener = listener;

    }

    public void init() {
        LOG.info("Greet Session Initiated");
        InstanceIdentifier<News> ii = InstanceIdentifier.create(MessageData.class).child(News.class);
        dataTreeChangeListenerRegistration = dataBroker
            .registerDataTreeChangeListener(new DataTreeIdentifier(LogicalDatastoreType.CONFIGURATION, ii), listener);
    }

    public void close() {
        dataTreeChangeListenerRegistration.close();
        LOG.info("Greet Closed");
    }

}
