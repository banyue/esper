/**************************************************************************************
 * Copyright (C) 2006-2015 EsperTech Inc. All rights reserved.                        *
 * http://www.espertech.com/esper                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.view.std;

import com.espertech.esper.epl.expression.core.ExprNode;
import com.espertech.esper.view.View;
import com.espertech.esper.view.ViewDataVisitableContainer;

public interface GroupByView extends View, ViewDataVisitableContainer
{
    public ExprNode[] getCriteriaExpressions();
}
