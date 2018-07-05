package listener;

import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.BigGreetBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.MessageData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.message.data.News;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Created by HZ20314 on 2018/7/4.
 */
public class GreetListenerChange implements DataTreeChangeListener<News>{
    private static final Logger LOG = LoggerFactory.getLogger(GreetListenerChange.class);
    private static NotificationPublishService notificationPublishService;
    public  GreetListenerChange(){

    }

    public void setNotificationPublishService(NotificationPublishService notificationPublish) {
        notificationPublishService = notificationPublish;
    }
    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<News>> changes) {
        for (final DataTreeModification<News> change : changes) {
            final DataObjectModification<News> rootChange = change.getRootNode();
            switch (rootChange.getModificationType()) {
                case WRITE: {
                    LOG.info("Write - before : {} after : {}", rootChange.getDataBefore(), rootChange.getDataAfter());

                    try {
                        BigGreetBuilder bigGreetBuilder = new BigGreetBuilder();
                        bigGreetBuilder.setDesc(rootChange.getDataAfter().getName());
                        notificationPublishService.putNotification(bigGreetBuilder.build() );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case SUBTREE_MODIFIED: {
                    LOG.info("Write - before : {} after : {}", rootChange.getDataBefore(), rootChange.getDataAfter());

                    try {
                        notificationPublishService.putNotification(new BigGreetBuilder().setDesc(rootChange.getDataAfter().getName()).build() );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case DELETE:
                    LOG.debug("node {} deleted", rootChange.getIdentifier());
                    break;
            }
        }
    }
}

