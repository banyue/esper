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

package com.espertech.esper.epl.enummethod.eval;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.agg.aggregator.AggregatorAvgBigDecimal;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.event.arr.ObjectArrayEventBean;
import com.espertech.esper.event.arr.ObjectArrayEventType;

import java.math.MathContext;
import java.util.Collection;

public class EnumEvalAverageBigDecimalScalarLambda extends EnumEvalBase implements EnumEval {

    private final ObjectArrayEventType resultEventType;
    private final MathContext optionalMathContext;

    public EnumEvalAverageBigDecimalScalarLambda(ExprEvaluator innerExpression, int streamCountIncoming, ObjectArrayEventType resultEventType, MathContext optionalMathContext) {
        super(innerExpression, streamCountIncoming);
        this.resultEventType = resultEventType;
        this.optionalMathContext = optionalMathContext;
    }

    public Object evaluateEnumMethod(EventBean[] eventsLambda, Collection target, boolean isNewData, ExprEvaluatorContext context) {

        AggregatorAvgBigDecimal agg = new AggregatorAvgBigDecimal(optionalMathContext);
        ObjectArrayEventBean resultEvent = new ObjectArrayEventBean(new Object[1], resultEventType);

        Collection<Object> values = (Collection<Object>) target;
        for (Object next : values) {

            resultEvent.getProperties()[0] = next;
            eventsLambda[streamNumLambda] = resultEvent;

            Number num = (Number) innerExpression.evaluate(eventsLambda, isNewData, context);
            if (num == null) {
                continue;
            }
            agg.enter(num);
        }

        return agg.getValue();
    }
}
