/*
 * Copyright (c) 2018 UTStarcom, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.utstar.test.impl.listener;

import java.util.Collection;
import javax.annotation.Nonnull;

import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.BigGreetBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.message.data.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by HZ20314 on 2018/7/4.
 */
public class GreetListenerChange implements DataTreeChangeListener<News> {
    private static final Logger LOG = LoggerFactory.getLogger(GreetListenerChange.class);
    private NotificationPublishService notificationPublishService;

    public GreetListenerChange(NotificationPublishService notificationPublishService) {
        this.notificationPublishService = notificationPublishService;
    }


    @Override public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<News>> changes) {
        for (final DataTreeModification<News> change : changes) {
            final DataObjectModification<News> rootChange = change.getRootNode();
            switch (rootChange.getModificationType()) {
                case WRITE: {
                    LOG.info("Write - before : {} after : {}", rootChange.getDataBefore(), rootChange.getDataAfter());

                    try {
                        BigGreetBuilder bigGreetBuilder = new BigGreetBuilder();
                        bigGreetBuilder.setDesc(rootChange.getDataAfter().getName());
                        notificationPublishService.putNotification(bigGreetBuilder.build());
                    } catch (InterruptedException e) {
                        LOG.error("Fail to listener data change", e);
                    }
                    break;
                }
                case SUBTREE_MODIFIED: {
                    LOG.info("Write - before : {} after : {}", rootChange.getDataBefore(), rootChange.getDataAfter());

                    try {
                        notificationPublishService.putNotification(
                            new BigGreetBuilder().setDesc(rootChange.getDataAfter().getName()).build());
                    } catch (InterruptedException e) {
                        LOG.error("Fail to listener data change", e);
                    }
                    break;
                }
                case DELETE: {
                    LOG.info("node {} deleted", rootChange.getIdentifier());
                    break;
                }
                default: {
                    LOG.info("listener data change");
                }
            }
        }
    }
}

