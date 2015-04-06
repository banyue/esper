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

package com.espertech.esper.epl.expression;

import com.espertech.esper.epl.expression.core.ExprNodeUtility;
import com.espertech.esper.epl.expression.core.ExprValidationException;
import com.espertech.esper.epl.expression.ops.ExprOrNode;
import com.espertech.esper.epl.expression.ops.ExprRelationalOpNode;
import com.espertech.esper.epl.expression.ops.ExprRelationalOpNodeImpl;
import com.espertech.esper.support.epl.SupportExprNode;
import com.espertech.esper.support.epl.SupportExprNodeUtil;
import com.espertech.esper.type.RelationalOpEnum;
import junit.framework.TestCase;

public class TestExprRelationalOpNode extends TestCase
{
    private ExprRelationalOpNode opNode;

    public void setUp()
    {
        opNode = new ExprRelationalOpNodeImpl(RelationalOpEnum.GE);
    }

    public void testGetType() throws Exception
    {
        opNode.addChildNode(new SupportExprNode(Long.class));
        opNode.addChildNode(new SupportExprNode(int.class));
        assertEquals(Boolean.class, opNode.getType());
    }

    public void testValidate() throws Exception
    {
        // Test success
        opNode.addChildNode(new SupportExprNode(String.class));
        opNode.addChildNode(new SupportExprNode(String.class));
        opNode.validate(ExprValidationContextFactory.makeEmpty());

        opNode.setChildNodes(new SupportExprNode(String.class));

        // Test too few nodes under this node
        try
        {
            opNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (IllegalStateException ex)
        {
            // Expected
        }

        // Test mismatch type
        opNode.addChildNode(new SupportExprNode(Integer.class));
        try
        {
            opNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }

        // Test type cannot be compared
        opNode.setChildNodes(new SupportExprNode(Boolean.class));
        opNode.addChildNode(new SupportExprNode(Boolean.class));

        try
        {
            opNode.validate(ExprValidationContextFactory.makeEmpty());
            fail();
        }
        catch (ExprValidationException ex)
        {
            // Expected
        }
    }

    public void testEvaluate() throws Exception
    {
        SupportExprNode childOne = new SupportExprNode("d");
        SupportExprNode childTwo = new SupportExprNode("c");
        opNode.addChildNode(childOne);
        opNode.addChildNode(childTwo);
        opNode.validate(ExprValidationContextFactory.makeEmpty());       // Type initialization

        assertEquals(true, opNode.evaluate(null, false, null));

        childOne.setValue("c");
        assertEquals(true, opNode.evaluate(null, false, null));

        childOne.setValue("b");
        assertEquals(false, opNode.evaluate(null, false, null));

        opNode = makeNode(null, Integer.class, 2, Integer.class);
        assertEquals(null, opNode.evaluate(null, false, null));
        opNode = makeNode(1, Integer.class, null, Integer.class);
        assertEquals(null, opNode.evaluate(null, false, null));
        opNode = makeNode(null, Integer.class, null, Integer.class);
        assertEquals(null, opNode.evaluate(null, false, null));
    }

    public void testToExpressionString() throws Exception
    {
        opNode.addChildNode(new SupportExprNode(10));
        opNode.addChildNode(new SupportExprNode(5));
        assertEquals("10>=5", ExprNodeUtility.toExpressionStringMinPrecedenceSafe(opNode));
    }

    private ExprRelationalOpNode makeNode(Object valueLeft, Class typeLeft, Object valueRight, Class typeRight) throws Exception
    {
        ExprRelationalOpNode relOpNode = new ExprRelationalOpNodeImpl(RelationalOpEnum.GE);
        relOpNode.addChildNode(new SupportExprNode(valueLeft, typeLeft));
        relOpNode.addChildNode(new SupportExprNode(valueRight, typeRight));
        SupportExprNodeUtil.validate(relOpNode);
        return relOpNode;
    }

    public void testEqualsNode() throws Exception
    {
        assertTrue(opNode.equalsNode(opNode));
        assertFalse(opNode.equalsNode(new ExprRelationalOpNodeImpl(RelationalOpEnum.LE)));
        assertFalse(opNode.equalsNode(new ExprOrNode()));
    }
}
