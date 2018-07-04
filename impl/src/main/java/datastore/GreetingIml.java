package datastore;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.opendaylight.controller.md.sal.binding.api.*;

import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;

import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.message.data.News;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.message.data.NewsBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * Created by HZ20314 on 2018/7/2.
 */
public class GreetingIml implements GreetingService{
    private static final Logger LOG = LoggerFactory.getLogger(GreetingIml.class);
    private final DataBroker dataBroker;

    public GreetingIml(final DataBroker databroker){

        this.dataBroker=databroker;
    }


    public void init() {
        LOG.info(" Session Initiated");

    }


    public void close() {
        LOG.info("Greet  Closed");

    }

    @Override
    public Future<RpcResult<SmallGreetOutput>> smallGreet(SmallGreetInput input) {
        SettableFuture<RpcResult <SmallGreetOutput>> futureResult = SettableFuture.create();

        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        News news = createDbGreeting(input);
        InstanceIdentifier<News> ii = InstanceIdentifier.create(MessageData.class).child(News.class);
        tx.put(LogicalDatastoreType.CONFIGURATION,ii,news);
        SmallGreetOutputBuilder outputBuilder = new SmallGreetOutputBuilder();
        outputBuilder.setResult(input.getName()).build();

        CheckedFuture<Void, TransactionCommitFailedException> future = tx.submit();
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // silent is OK.
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        futureResult.set(RpcResultBuilder.success(
                outputBuilder.build()).build());
        return  futureResult;
    }

    private News createDbGreeting(SmallGreetInput input) {
        NewsBuilder newsBuilder = new NewsBuilder();
        newsBuilder.setName(input.getName());

      return  newsBuilder.build();
    }
}
