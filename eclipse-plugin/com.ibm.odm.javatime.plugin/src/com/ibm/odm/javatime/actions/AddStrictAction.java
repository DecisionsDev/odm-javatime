/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.actions;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;

import com.ibm.odm.javatime.Activator;
import com.ibm.odm.javatime.utils.BomFileManager;
import com.ibm.odm.javatime.utils.DateUsageDetector;

/**
 * Action for adding Java Time support in Strict mode
 * (pure Java Time without java.util.Date compatibility)
 */
public class AddStrictAction extends AbstractAddJavaTimeAction {

    @Override
    protected String getBomBaseName() {
        return Activator.BOM_STRICT_NAME;
    }

    @Override
    protected String getMode() {
        return Activator.MODE_STRICT;
    }

    @Override
    protected String getOtherModeBomName() {
        return Activator.BOM_COMPATIBLE_NAME;
    }

    @Override
    protected boolean performModeSpecificValidation(IProject project, IFolder bomFolder) {
        try {
            // Check if java.util.Date is used in the project
            boolean usesDate = DateUsageDetector.detectDateUsage(project, bomFolder);
            
            if (usesDate) {
                // Warn user that Strict mode might cause issues
                boolean proceed = MessageDialog.openQuestion(
                    targetPart.getSite().getShell(),
                    "Strict Mode Selected",
                    "This project appears to use java.util.Date.\n\n" +
                    "Strict mode does NOT provide compatibility with java.util.Date. " +
                    "This may cause compilation errors in existing rules.\n\n" +
                    "Consider using Compatible mode instead.\n\n" +
                    "Do you want to proceed with Strict mode anyway?"
                );
                
                return proceed;
            }
            
            return true;
        } catch (Exception e) {
            showError("Validation Error", 
                "Failed to validate project: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected String getSuccessMessage() {
        return "Java Time support (Strict mode) has been added successfully.";
    }
}


