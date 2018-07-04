package org.utstar.jyh;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.*;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by HZ20314 on 2018/7/2.
 */
public class GreetingIml implements GreetingService {
    private static final Logger LOG = LoggerFactory.getLogger(GreetingIml.class);
    private final DataBroker dataBroker;
    public GreetingIml(final DataBroker databroker){
        this.dataBroker=databroker;
    }


    public void init() {
        LOG.info("It's begin");
        ReadWriteTransaction txr = dataBroker.newReadWriteTransaction();
        InstanceIdentifier <MessageData> root = InstanceIdentifier.
                create(MessageData.class);
        try {
            CheckedFuture <Optional<MessageData>, ReadFailedException> future =
                    txr.read(LogicalDatastoreType.CONFIGURATION, root);
            Optional <MessageData> opData = future.get();
            if (!opData.isPresent()) {
                MessageDataBuilder mbuilder = new MessageDataBuilder();
                txr.put(LogicalDatastoreType.CONFIGURATION, root, mbuilder.build());
                txr.submit().checkedGet();
            }
        } catch (InterruptedException e) {
            LOG.warn(" error", e);
        } catch (ExecutionException e) {
            LOG.warn(" error", e);
        } catch (TransactionCommitFailedException e) {
            LOG.warn("  error", e);
        }

    }


    public void close() {
        LOG.info("Closed");

    }

    @Override
    public Future<RpcResult<SmallGreetOutput>> smallGreet(SmallGreetInput input) {
        SettableFuture<RpcResult <SmallGreetOutput>> futureResult = SettableFuture.create();
        WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
        InstanceIdentifier<MessageData> id= InstanceIdentifier.create(MessageData.class);
        MessageData message = new MessageDataBuilder().setWeather("Rain").setLocation("Beijing").build();
        tx.put(LogicalDatastoreType.CONFIGURATION,id,message);
        MessageData msd = createDbGreeting(input);
        tx.put(LogicalDatastoreType.CONFIGURATION,id,msd);
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
        return  futureResult;
    }

    private MessageData createDbGreeting(SmallGreetInput input) {
      MessageDataBuilder builder = new MessageDataBuilder();
      builder.setLocation(input.getLocation());
      builder.setWeather(input.getWeather());
      return  builder.build();
    }


}
