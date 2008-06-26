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
package org.jboss.tools.common.model.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.commands.ICommandImageService;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.osgi.framework.Bundle;

public class ModelImages {
	private static ModelImages instance;
	
	static {
		try {
			instance = new ModelImages(ModelPlugin.getDefault().getBundle().getEntry("/"));
		} catch (Exception e) {
			// do nothing
			ModelPlugin.getPluginLog().logError(e);
		}
	}

	public static Image getImage(String key) {
		return instance.createImageDescriptor(key).createImage();
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		return instance.createImageDescriptor(key);
	}
	private URL baseUrl;

	protected ModelImages(URL url) {
		baseUrl = url;
	}

	public ImageDescriptor createImageDescriptor(String key) {
		if(key.startsWith("command:")) {
			key = key.substring("command:".length());
			IWorkbench w = ModelPlugin.getDefault().getWorkbench();
			ICommandImageService s = (ICommandImageService)w.getService(ICommandImageService.class);
			return s.getImageDescriptor(key);
		}
		URL url = baseUrl;
		if(key.startsWith("plugin:")) {
			key = key.substring("plugin:".length());
			int i = key.indexOf(":");
			String plugin = key.substring(0, i);
			key = key.substring(i + 1);
			Bundle bundle = Platform.getBundle(plugin);
			if(bundle != null) {
				url = bundle.getEntry("/");
			}
		}
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(url, key));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}		
	}

	private URL makeIconFileURL(URL url, String name) throws MalformedURLException {
		if (name == null) throw new MalformedURLException("Image name cannot be null.");
		return new URL(url, name);
	}	

}
