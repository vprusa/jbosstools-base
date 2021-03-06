/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.common.model.ui.action;

import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;

public class XModelObjectActionItem {
	protected XActionItem item;
	protected XModelObject object;
	protected XModelObject[] targets;
	protected Object environment;
	protected Shell shell;

	public XModelObjectActionItem(XActionItem item, XModelObject object, XModelObject[] targets, Object environment) {
		this.item = item;
		this.object = object;
		this.targets = targets;
		this.environment = environment;
	}
	
	public void setShell(Shell shell) {
		this.shell = shell;
	}
	
}
