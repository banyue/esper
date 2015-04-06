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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.subquery.ExprSubselectStrategy;

import java.util.Collection;

public class AIRegistrySubselectSingle implements AIRegistrySubselect, ExprSubselectStrategy {

    private ExprSubselectStrategy strategy;

    public AIRegistrySubselectSingle() {
    }

    public void assignService(int num, ExprSubselectStrategy subselectStrategy) {
        this.strategy = subselectStrategy;
    }

    public void deassignService(int num) {
        this.strategy = null;
    }

    public Collection<EventBean> evaluateMatching(EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        return strategy.evaluateMatching(eventsPerStream, exprEvaluatorContext);
    }

    public int getAgentInstanceCount() {
        return strategy == null ? 0 : 1;
    }
}
