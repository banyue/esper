/*
 * *************************************************************************************
 *  Copyright (C) 2006-2015 EsperTech, Inc. All rights reserved.                       *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 * *************************************************************************************
 */

package com.espertech.esper.multithread.dispatchmodel;

import com.espertech.esper.dispatch.DispatchService;

import java.util.LinkedList;

public class UpdateDispatchViewOrderEnforcingModel implements UpdateDispatchViewModel
{
    private DispatchService dispatchService;
    private DispatchListener dispatchListener;

    private DispatchFuture currentFuture;
    private ThreadLocal<Boolean> isDispatchWaiting = new ThreadLocal<Boolean>() {
        protected synchronized Boolean initialValue() {
            return new Boolean(false);
        }
    };
    private ThreadLocal<LinkedList<int[]>> received = new ThreadLocal<LinkedList<int[]>>() {
        protected synchronized LinkedList<int[]> initialValue() {
            return new LinkedList<int[]>();
        }
    };

    public UpdateDispatchViewOrderEnforcingModel(DispatchService dispatchService, DispatchListener dispatchListener)
    {
        this.currentFuture = new DispatchFuture(); // use a completed future as a start
        this.dispatchService = dispatchService;
        this.dispatchListener = dispatchListener;
    }

    public void add(int[] payload)
    {
        received.get().add(payload);
        if (!isDispatchWaiting.get())
        {
            DispatchFuture nextFuture;
            synchronized(this)
            {
                nextFuture = new DispatchFuture(this, currentFuture);
                currentFuture.setLater(nextFuture);
                currentFuture = nextFuture;
            }
            dispatchService.addExternal(nextFuture);
            isDispatchWaiting.set(true);
        }
    }

    public void execute()
    {
        // flatten
        LinkedList<int[]> payloads = received.get();
        int[][] result = new int[payloads.size()][];

        int count = 0;
        for (int[] entry : payloads)
        {
            result[count++] = entry;
        }

        isDispatchWaiting.set(false);
        payloads.clear();
        dispatchListener.dispatched(result);
    }
}
