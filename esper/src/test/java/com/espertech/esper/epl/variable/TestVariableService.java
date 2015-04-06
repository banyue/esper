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

package com.espertech.esper.epl.variable;

import com.espertech.esper.schedule.SchedulingServiceImpl;
import com.espertech.esper.support.event.SupportEventAdapterService;
import com.espertech.esper.timer.TimeSourceServiceImpl;
import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestVariableService extends TestCase
{
    private VariableService service;
    
    public void setUp()
    {
        service = new VariableServiceImpl(10000, new SchedulingServiceImpl(new TimeSourceServiceImpl()), SupportEventAdapterService.getService(), null);
    }

    public void testPerfSetVersion()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++)
        {
            service.setLocalVersion();
        }
        long end = System.currentTimeMillis();
        long delta = (end - start);
        assertTrue("delta=" + delta, delta < 100);
    }

    public void testMultithreadedZero() throws Exception
    {
        tryMT(4, 5000, 8);
    }

    public void testMultithreadedOne() throws Exception
    {
        tryMT(2, 10000, 4);
    }

    // Start N threads
    // each thread performs X loops
    // each loop gets a unique number Y from a shared object and performs setVersion in the synchronized block
    // then the thread performs reads, write and read of shared variables, writing the number Y
    // ==> the reads should not see any higher number (unless watemarks reached)
    // ==> reads should produce the exact same result unless setVersion called
    private void tryMT(int numThreads, int numLoops, int numVariables) throws Exception
    {
        VariableVersionCoord coord = new VariableVersionCoord(service);

        // create variables
        String[] variables = new String[numVariables];
        for (int i = 0; i < numVariables; i++)
        {
            char c = 'A';
            c+=i;
            variables[i] = Character.toString(c);
            service.createNewVariable(null, variables[i], Integer.class.getName(), false, false, false, 0, null);
            service.allocateVariableState(variables[i], 0, null);
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        VariableServiceCallable[] callables = new VariableServiceCallable[numThreads];
        Future future[] = new Future[numThreads];
        for (int i = 0; i < numThreads; i++)
        {
            callables[i] = new VariableServiceCallable(variables, service, coord, numLoops);
            future[i] = threadPool.submit(callables[i]);
        }

        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.SECONDS);

        for (int i = 0; i < numThreads; i++)
        {
            assertTrue((Boolean) future[i].get());
        }

        //System.out.println(service.toString());
        // Verify results per thread
        for (int i = 0; i < callables.length; i++)
        {
            int[][] result = callables[i].getResults();
            int[] marks = callables[i].getMarks();
        }
    }

    public void testReadWrite() throws Exception
    {
        assertNull(service.getReader("a", 0));

        service.createNewVariable(null, "a", Long.class.getName(), false, false, false, 100L, null);
        service.allocateVariableState("a", 0, null);
        VariableReader reader = service.getReader("a", 0);
        assertEquals(Long.class, reader.getVariableMetaData().getType());
        assertEquals(100L, reader.getValue());

        service.write(reader.getVariableMetaData().getVariableNumber(), 0, 101L);
        service.commit();
        assertEquals(100L, reader.getValue());
        service.setLocalVersion();
        assertEquals(101L, reader.getValue());        

        service.write(reader.getVariableMetaData().getVariableNumber(), 0, 102L);
        service.commit();
        assertEquals(101L, reader.getValue());
        service.setLocalVersion();
        assertEquals(102L, reader.getValue());        
    }

    public void testRollover() throws Exception
    {
        service = new VariableServiceImpl(VariableServiceImpl.ROLLOVER_READER_BOUNDARY - 100, 10000, new SchedulingServiceImpl(new TimeSourceServiceImpl()), SupportEventAdapterService.getService(), null);
        String[] variables = "a,b,c,d".split(",");

        VariableReader readers[] = new VariableReader[variables.length];
        for (int i = 0; i < variables.length; i++)
        {
            service.createNewVariable(null, variables[i], Long.class.getName(), false, false, false, 100L, null);
            service.allocateVariableState(variables[i], 0, null);
            readers[i] = service.getReader(variables[i], 0);
        }

        for (int i = 0; i < 1000; i++)
        {
            for (int j = 0; j < variables.length; j++)
            {
                service.write(readers[j].getVariableMetaData().getVariableNumber(), 0, 100L + i);
                service.commit();
            }
            readCompare(variables, 100L + i);
        }
    }

    private void readCompare(String[] variables, Object value)
    {
        service.setLocalVersion();
        for (int i = 0; i < variables.length; i++)
        {
            assertEquals(value, service.getReader(variables[i], 0).getValue());
        }
    }

    public void testInvalid() throws Exception
    {
        service.createNewVariable(null, "a", Long.class.getName(), false, false, false, null, null);
        service.allocateVariableState("a", 0, null);
        assertNull(service.getReader("dummy", 0));

        try
        {
            service.createNewVariable(null, "a", Long.class.getName(), false, false, false, null, null);
            fail();
        }
        catch (VariableExistsException e)
        {
            assertEquals("Variable by name 'a' has already been created", e.getMessage());
        }
    }
}
