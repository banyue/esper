/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.regressionlib.support.multithread;

import com.espertech.esper.common.internal.support.SupportBean;
import com.espertech.esper.regressionlib.support.bean.SupportMarketDataBean;
import com.espertech.esper.runtime.client.EPRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public class StmtInsertIntoCallable implements Callable {
    private final EPRuntime runtime;
    private final int numRepeats;
    private final String threadKey;

    public StmtInsertIntoCallable(String threadKey, EPRuntime runtime, int numRepeats) {
        this.runtime = runtime;
        this.numRepeats = numRepeats;
        this.threadKey = threadKey;
    }

    public Object call() throws Exception {
        try {
            for (int loop = 0; loop < numRepeats; loop++) {
                SupportBean eventOne = new SupportBean();
                eventOne.setTheString("E1_" + threadKey);
                runtime.getEventService().sendEventBean(eventOne, eventOne.getClass().getSimpleName());

                SupportMarketDataBean eventTwo = new SupportMarketDataBean("E2_" + threadKey, 0d, null, null);
                runtime.getEventService().sendEventBean(eventTwo, eventTwo.getClass().getSimpleName());
            }
        } catch (Exception ex) {
            log.error("Error in thread " + Thread.currentThread().getId(), ex);
            return false;
        }
        return true;
    }

    private static final Logger log = LoggerFactory.getLogger(StmtInsertIntoCallable.class);
}
