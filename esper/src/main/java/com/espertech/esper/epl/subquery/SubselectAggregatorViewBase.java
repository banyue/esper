/**************************************************************************************
 * Copyright (C) 2006-2015 EsperTech Inc. All rights reserved.                        *
 * http://www.espertech.com/esper                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.epl.subquery;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.MultiKeyUntyped;
import com.espertech.esper.epl.agg.service.AggregationService;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.view.ViewSupport;

import java.util.Iterator;

/**
 * View handling the insert and remove stream generated by a subselect
 * for application to aggregation state.
 */
public abstract class SubselectAggregatorViewBase extends ViewSupport
{
    protected final AggregationService aggregationService;
    protected final ExprEvaluator optionalFilterExpr;
    protected final ExprEvaluatorContext exprEvaluatorContext;
    protected final ExprEvaluator[] groupKeys;
    protected final EventBean[] eventsPerStream = new EventBean[1];

    public SubselectAggregatorViewBase(AggregationService aggregationService, ExprEvaluator optionalFilterExpr, ExprEvaluatorContext exprEvaluatorContext, ExprEvaluator[] groupKeys) {
        this.aggregationService = aggregationService;
        this.optionalFilterExpr = optionalFilterExpr;
        this.exprEvaluatorContext = exprEvaluatorContext;
        this.groupKeys = groupKeys;
    }

    public EventType getEventType() {
        return this.getParent().getEventType();
    }

    public Iterator<EventBean> iterator() {
        return this.getParent().iterator();
    }

    protected boolean filter(boolean isNewData)
    {
        Boolean result = (Boolean) optionalFilterExpr.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (result == null) {
            return false;
        }
        return result;
    }

    protected Object generateGroupKey(boolean isNewData) {
        if (groupKeys.length == 1) {
            return groupKeys[0].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }
        Object[] keys = new Object[groupKeys.length];
        for (int i = 0; i < groupKeys.length; i++) {
            keys[i] = groupKeys[i].evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        }
        return new MultiKeyUntyped(keys);
    }
}
