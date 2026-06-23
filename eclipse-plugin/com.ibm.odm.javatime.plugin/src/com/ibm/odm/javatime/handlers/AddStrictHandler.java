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
 * Handler for adding Java Time support in Strict mode
 * (pure Java Time API without java.util.Date compatibility)
 */
public class AddStrictHandler extends AbstractAddJavaTimeHandler {

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
    protected boolean performModeSpecificValidation(ExecutionEvent event, IProject project, IFolder bomFolder) {
        
        try {
            // Check if java.util.Date is used in the project
            boolean usesDate = DateUsageDetector.detectDateUsage(project, bomFolder);
            
            if (usesDate) {
                // Warn user that Strict mode might cause issues
                boolean proceed = MessageDialog.openQuestion(
                    HandlerUtil.getActiveShell(event),
                    "Strict Mode Selected",
                    "This project appears to use java.util.Date.\n\n" +
                    "Strict mode does not provide compatibility with java.util.Date. " +
                    "Consider using Compatible mode instead.\n\n" +
                    "Do you want to proceed with Strict mode anyway?"
                );
                
                if (!proceed) {
                    return false;
                }
            }
            
            // Check if BOM already exists (either compatible or strict)
            if (BomFileManager.checkBomExists(bomFolder, Activator.BOM_STRICT_NAME) ||
                BomFileManager.checkBomExists(bomFolder, Activator.BOM_COMPATIBLE_NAME)) {
                MessageDialog.openInformation(
                    HandlerUtil.getActiveShell(event),
                    "Java Time Support Already Added",
                    "Java Time BOM files already exist in this project."
                );
                return false;
            }
            
            // Copy BOM files
            BomFileManager.copyBomFiles(bomFolder, Activator.BOM_STRICT_NAME, Activator.MODE_STRICT);
            project.refreshLocal(IProject.DEPTH_INFINITE, null);
            
            MessageDialog.openInformation(
                HandlerUtil.getActiveShell(event),
                "Success",
                "Java Time support (Strict mode) has been added successfully.\n\n" +
                "The following files were added to the BOM folder:\n" +
                BomFileManager.getBomFilesList(Activator.BOM_STRICT_NAME) + "\n" +
                "This mode provides pure Java Time API without java.util.Date compatibility."
            );
            
        } catch (Exception e) {
            showError(event, "Error", 
                "Failed to add Java Time support: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    

        @Override
    protected String getSuccessMessage() {
        return "Java Time support (Strict mode) has been added successfully.";
    }
}


