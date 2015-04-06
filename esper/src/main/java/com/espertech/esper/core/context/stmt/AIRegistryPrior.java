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

package com.espertech.esper.core.context.stmt;

import com.espertech.esper.epl.expression.prior.ExprPriorEvalStrategy;

public interface AIRegistryPrior extends ExprPriorEvalStrategy {
    public void assignService(int num, ExprPriorEvalStrategy value);
    public void deassignService(int num);
    public int getAgentInstanceCount();
}
