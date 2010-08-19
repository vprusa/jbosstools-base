/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.usage.reporting;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.usage.internal.JBossToolsUsageActivator;
import org.jboss.tools.usage.reporting.ReportingMessages;
import org.jboss.tools.usage.util.StatusUtils;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Andre Dieitsheim
 */
public class UsageReportDispatcher implements IStartup {

	public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable() {

			public void run() {
				UsageReportEnablementDialog dialog = new UsageReportEnablementDialog(ReportingMessages.UsageReport_DialogTitle,
						ReportingMessages.UsageReport_DialogMessage,
						ReportingMessages.UsageReport_Checkbox_Text,
						true,
						PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				if (UsageReportPreferences.isAskUser()) {
					if (dialog.open() == Window.OK) {
						UsageReportPreferences.setEnabled(dialog.isReportEnabled());
						UsageReportPreferences.setAskUser(false);
						flushPreferences();
					}
				}

				if (UsageReportPreferences.isEnabled()) {
					new UsageReport().report();
				}
			}

			private void flushPreferences() {
				try {
					UsageReportPreferences.flush();
				} catch (BackingStoreException e) {
					IStatus status = StatusUtils.getErrorStatus(JBossToolsUsageActivator.PLUGIN_ID,
							ReportingMessages.UsageReport_Error_SavePreferences, e);
					JBossToolsUsageActivator.getDefault().getLog().log(status);
				}
			}
		});
	}
}
