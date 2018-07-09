/*
 * Copyright (c) 2018 UTStarcom, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package com.utstar.test.impl.datastore;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Future;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;

import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;

import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.GreetingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.MessageData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.SmallGreetInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.SmallGreetOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.SmallGreetOutputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.message.data.News;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.message.data.NewsBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Created by HZ20314 on 2018/7/2.
 */
public class GreetingIml implements GreetingService {
    private static final Logger LOG = LoggerFactory.getLogger(GreetingIml.class);
    private final DataBroker dataBroker;

    public GreetingIml(final DataBroker databroker) {
        this.dataBroker = databroker;
    }

    @Override public Future<RpcResult<SmallGreetOutput>> smallGreet(SmallGreetInput input) {
        SettableFuture<RpcResult<SmallGreetOutput>> futureResult = SettableFuture.create();
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        News news = createDbGreeting(input);
        InstanceIdentifier<News> ii = InstanceIdentifier.create(MessageData.class).child(News.class);
        tx.put(LogicalDatastoreType.CONFIGURATION, ii, news);
        CheckedFuture<Void, TransactionCommitFailedException> future = tx.submit();
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void avoid) {
                LOG.info("Succeed to write config/DS");
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("Failed to write config/DS ", throwable);
            }
        });

        SmallGreetOutputBuilder outputBuilder = new SmallGreetOutputBuilder();
        outputBuilder.setResult(input.getName()).build();
        futureResult.set(RpcResultBuilder.success(outputBuilder.build()).build());
        return futureResult;
    }

    private News createDbGreeting(SmallGreetInput input) {
        NewsBuilder newsBuilder = new NewsBuilder();
        newsBuilder.setName(input.getName());

        return newsBuilder.build();
    }
}
