/**************************************************************************************
 * Copyright (C) 2006-2015 EsperTech Inc. All rights reserved.                        *
 * http://www.espertech.com/esper                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.pattern.guard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class for applications to extend to implement pattern guard objects.
 */
public abstract class GuardFactorySupport implements GuardFactory
{
    private static Log log = LogFactory.getLog(GuardFactorySupport.class);
}
