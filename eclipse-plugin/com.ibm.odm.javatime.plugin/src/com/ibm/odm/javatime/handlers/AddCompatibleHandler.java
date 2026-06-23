/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ibm.odm.javatime.Activator;
import com.ibm.odm.javatime.utils.BomFileManager;
import com.ibm.odm.javatime.utils.DateUsageDetector;

/**
 * Handler for adding Java Time support in Compatible mode
 * (includes java.util.Date compatibility)
 */
public class AddCompatibleHandler extends AbstractAddJavaTimeHandler {

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
    protected boolean performModeSpecificValidation(ExecutionEvent event, IProject project, IFolder bomFolder) {
        try {
            // Check if java.util.Date is used in the project
            boolean usesDate = DateUsageDetector.detectDateUsage(project, bomFolder);
            
            if (!usesDate) {
                // Warn user that Compatible mode might not be necessary
                boolean proceed = MessageDialog.openQuestion(
                    HandlerUtil.getActiveShell(event),
                    "Compatible Mode Selected",
                    "This project does not appear to use java.util.Date.\n\n" +
                    "Compatible mode is intended for projects that already use java.util.Date. " +
                    "Consider using Strict mode instead.\n\n" +
                    "Do you want to proceed with Compatible mode anyway?"
                );
                
                return proceed;
            }
            
            return true;
        } catch (Exception e) {
            showError(event, "Validation Error", 
                "Failed to validate project: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected String getSuccessMessage() {
        return "Java Time support (Compatible mode) has been added successfully.";
    }
}


