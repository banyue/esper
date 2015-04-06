/**************************************************************************************
 * Copyright (C) 2006-2015 EsperTech Inc. All rights reserved.                        *
 * http://www.espertech.com/esper                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.example.qos_sla.monitor;

import com.espertech.esper.client.*;
import com.espertech.esper.example.qos_sla.eventbean.OperationMeasurement;

public class AverageLatencyMonitor
{
    private AverageLatencyMonitor()
    {
    }

    public static void start()
    {
        EPAdministrator admin = EPServiceProviderManager.getDefaultProvider().getEPAdministrator();

        EPStatement statView = admin.createEPL(
                "select * from " + OperationMeasurement.class.getName() +
                ".std:groupwin(customerId).std:groupwin(operationName)" +
                ".win:length(100).stat:uni(latency)");

        statView.addListener(new AverageLatencyListener(10000));
    }
}
