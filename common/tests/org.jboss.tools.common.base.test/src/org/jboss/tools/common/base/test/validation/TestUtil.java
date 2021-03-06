/*******************************************************************************
  * Copyright (c) 2010 - 2012 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.common.base.test.validation;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.wst.validation.ValidationFramework;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.FilterUtil;
import org.eclipse.wst.validation.internal.InternalValidatorManager;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.RegistryConstants;
import org.eclipse.wst.validation.internal.operations.EnabledValidatorsOperation;
import org.eclipse.wst.validation.internal.operations.ValidatorSubsetOperation;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.test.util.JobUtils;

public class TestUtil {

	/**
	 * Wait for validation to be completed.
	 * Usage:
	 *       ValidatorManager.setStatus("Any status but ValidatorManager.SLEEPING");
	 *       ... // do some work here which will make Eclipse build the project
	 *       TestUtil.waitForValidation(project);
	 * @throws CoreException
	 */
	public static void _waitForValidation(IProject project) throws CoreException{

		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		ValidationFramework.getDefault().suspendAllValidation(false);
		try {
			new EnabledValidatorsOperation(project,false).run(new NullProgressMonitor());
		} finally {
			ValidationFramework.getDefault().suspendAllValidation(true);
		}
	}

	public static void validate(IResource resource) throws CoreException {
		validate(resource.getProject(), new IResource[] {resource});
	}

	public static void validate(IProject project, IResource[] resources) throws CoreException {
		ValidationFramework.getDefault().suspendAllValidation(true);
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		ValidationFramework.getDefault().suspendAllValidation(false);
		try {
			new IncrementalValidatorOperation(project, resources).run(new NullProgressMonitor());
//			new EnabledIncrementalValidatorsOperation(project, resources).run(new NullProgressMonitor());
//			new ValidatorSubsetOperation(project,"java",resource,false).run(new NullProgressMonitor());
		} catch (OperationCanceledException e) {
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			TestCase.fail(e.getMessage());
		} finally {
			ValidationFramework.getDefault().suspendAllValidation(true);
		}
	}

	private static class IncrementalValidatorOperation extends ValidatorSubsetOperation {
		public IncrementalValidatorOperation(IProject project, Object[] changedResources) throws InvocationTargetException {
			super(project, shouldForce(changedResources), RegistryConstants.ATT_RULE_GROUP_DEFAULT, false);
			ProjectConfiguration prjp = ConfigurationManager.getManager().getProjectConfiguration(project);
			setEnabledValidators(InternalValidatorManager.wrapInSet(prjp.getEnabledIncrementalValidators(true)));
			setFileDeltas(FilterUtil.getFileDeltas(getEnabledValidators(), changedResources, false));
		}
	}

	public static boolean waitForValidation() throws CoreException{
		for (int i = 0; i < 50; i++) {
			if(ValidatorManager.getStatus().equals(ValidatorManager.SLEEPING)) {
				return true;
			}
			JobUtils.delay(100);
			JobUtils.waitForIdle();
		}
		return false;
	}
}