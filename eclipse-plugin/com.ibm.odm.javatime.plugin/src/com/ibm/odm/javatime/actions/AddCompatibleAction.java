/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.actions;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import com.ibm.odm.javatime.Activator;
import com.ibm.odm.javatime.utils.BomFileManager;

/**
 * Action for adding Java Time support in Compatible mode
 * (includes java.util.Date compatibility)
 */
public class AddCompatibleAction extends AbstractAddJavaTimeAction {

    @Override
    protected String getBomBaseName() {
        return Activator.BOM_COMPATIBLE_NAME;
    }

    @Override
    protected String getMode() {
        return Activator.MODE_COMPATIBLE;
    }

    @Override
    protected String getOtherModeBomName() {
        return Activator.BOM_STRICT_NAME;
    }

    @Override
    protected boolean performModeSpecificValidation(IProject project, IFolder bomFolder) {
        // Compatible mode doesn't require specific validation
        // It's designed to work with or without java.util.Date
        return true;
    }

    @Override
    protected String getSuccessMessage() {
        return "Java Time support (Compatible mode) has been added successfully.\n\n" ;
    }
}


