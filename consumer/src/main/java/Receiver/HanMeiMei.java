
/*
 * Copyright (c) 2018 UTStarcom, Inc. and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package Receiver;

import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.BigGreet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.greeting.rev180702.GreetingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class HanMeiMei implements GreetingListener {
    Logger LOG = LoggerFactory.getLogger(HanMeiMei.class);

    @Override public void onBigGreet(BigGreet notification) {
        LOG.info("HanMeiMei:" + notification.getDesc() + "Feel hopeless ");
    }
}
