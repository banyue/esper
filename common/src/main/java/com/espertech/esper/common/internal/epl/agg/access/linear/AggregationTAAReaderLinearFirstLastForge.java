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
package com.espertech.esper.common.internal.epl.agg.access.linear;

import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.context.aifactory.core.SAIFFInitializeSymbol;
import com.espertech.esper.common.internal.epl.agg.core.AggregationTableAccessAggReaderForge;
import com.espertech.esper.common.internal.epl.expression.core.ExprNode;
import com.espertech.esper.common.internal.epl.expression.core.ExprNodeUtilityCodegen;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;

public class AggregationTAAReaderLinearFirstLastForge implements AggregationTableAccessAggReaderForge {
    private final Class underlyingType;
    private final AggregationAccessorLinearType accessType;
    private final ExprNode optionalEvaluator;

    public AggregationTAAReaderLinearFirstLastForge(Class underlyingType, AggregationAccessorLinearType accessType, ExprNode optionalEvaluator) {
        this.underlyingType = underlyingType;
        this.accessType = accessType;
        this.optionalEvaluator = optionalEvaluator;
    }

    public Class getResultType() {
        return underlyingType;
    }

    public CodegenExpression codegenCreateReader(CodegenMethodScope parent, SAIFFInitializeSymbol symbols, CodegenClassScope classScope) {
        CodegenMethod method = parent.makeChild(AggregationTAAReaderLinearFirstLast.class, this.getClass(), classScope);
        method.getBlock()
                .declareVar(AggregationTAAReaderLinearFirstLast.class, "strat", newInstance(AggregationTAAReaderLinearFirstLast.class))
                .exprDotMethod(ref("strat"), "setAccessType", constant(accessType))
                .exprDotMethod(ref("strat"), "setOptionalEvaluator", optionalEvaluator == null ? constantNull() : ExprNodeUtilityCodegen.codegenEvaluator(optionalEvaluator.getForge(), method, this.getClass(), classScope))
                .methodReturn(ref("strat"));
        return localMethod(method);
    }
}
