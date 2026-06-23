/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ibm.odm.javatime.utils.BomFileManager;
import com.ibm.odm.javatime.utils.ProjectHelper;

/**
 * Abstract base handler for adding Java Time support.
 * Provides common functionality for both Compatible and Strict modes.
 */
public abstract class AbstractAddJavaTimeHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = HandlerUtil.getCurrentStructuredSelection(event);
        
        if (selection.isEmpty()) {
            return null;
        }
        
        Object element = selection.getFirstElement();
        
        // Use common method to extract project and BOM folder
        ProjectHelper.ProjectBomInfo info = ProjectHelper.getProjectAndBomFromSelection(element);
        
        if (info == null) {
            showError(event, "Invalid Selection",
                "Could not locate project or BOM folder from selection.");
            return null;
        }
        
        IProject project = info.getProject();
        IFolder bomFolder = info.getBomFolder();
        
        if (!bomFolder.exists()) {
            showError(event, "BOM Folder Not Found",
                "Could not locate the BOM folder in the selected project.");
            return null;
        }
        
        try {
            // Check if BOM already exists (either compatible or strict)
            if (BomFileManager.checkBomExists(bomFolder, getBomBaseName()) ||
                BomFileManager.checkBomExists(bomFolder, getOtherModeBomName())) {
                MessageDialog.openInformation(
                    HandlerUtil.getActiveShell(event),
                    "Java Time Support Already Added",
                    "Java Time BOM files already exist in this project."
                );
                return null;
            }
            
            // Copy BOM files
            BomFileManager.copyBomFiles(bomFolder, getBomBaseName(), getMode());
            project.refreshLocal(IProject.DEPTH_INFINITE, null);
            
            // Show success message
            showSuccessMessage(event);
            
        } catch (Exception e) {
            showError(event, "Error", 
                "Failed to add Java Time support: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Show an error dialog to the user
     */
    protected void showError(ExecutionEvent event, String title, String message) {
        try {
            MessageDialog.openError(
                HandlerUtil.getActiveShell(event),
                title,
                message
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Show success message after BOM files are added
     */
    protected void showSuccessMessage(ExecutionEvent event) {
        try {
            MessageDialog.openInformation(
                HandlerUtil.getActiveShell(event),
                "Success",
                getSuccessMessage()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the BOM base name for this mode (e.g., "javatime-compatible" or "javatime-strict")
     */
    protected abstract String getBomBaseName();
    
    /**
     * Get the mode identifier (e.g., "compatible" or "strict")
     */
    protected abstract String getMode();
    
    /**
     * Get the BOM name of the other mode (for duplicate checking)
     */
    protected abstract String getOtherModeBomName();
    
    /**
     * Perform mode-specific validation before adding BOM files.
     * For example, Compatible mode might check if java.util.Date is NOT used,
     * while Strict mode might check if it IS used.
     * 
     * @param event the execution event
     * @param project the project
     * @param bomFolder the BOM folder
     * @return true if validation passed and action should continue, false to cancel
     */
    protected abstract boolean performModeSpecificValidation(ExecutionEvent event, IProject project, IFolder bomFolder);
    
    /**
     * Get the success message to display after BOM files are added
     */
    protected abstract String getSuccessMessage();
}

