/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.actions;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.ibm.odm.javatime.utils.BomFileManager;
import com.ibm.odm.javatime.utils.ProjectHelper;

/**
 * Abstract base class for adding Java Time support actions.
 * Provides common functionality for both Compatible and Strict modes.
 */
public abstract class AbstractAddJavaTimeAction implements IObjectActionDelegate {
    
    protected IWorkbenchPart targetPart;
    protected ISelection selection;

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
    }

    @Override
    public void run(IAction action) {
        if (!(selection instanceof IStructuredSelection)) {
            return;
        }
        
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        if (structuredSelection.isEmpty()) {
            return;
        }
        
        Object element = structuredSelection.getFirstElement();
        
        // Use common method to extract project and BOM folder
        ProjectHelper.ProjectBomInfo info = ProjectHelper.getProjectAndBomFromSelection(element);
        
        if (info == null) {
            showError("Invalid Selection",
                "Could not locate project or BOM folder from selection.");
            return;
        }
        
        IProject project = info.getProject();
        IFolder bomFolder = info.getBomFolder();
        
        if (!bomFolder.exists()) {
            showError("BOM Folder Not Found",
                "Could not locate the BOM folder in the selected project.");
            return;
        }
        
        try {
            // Perform mode-specific validation (e.g., check for java.util.Date usage)
            if (!performModeSpecificValidation(project, bomFolder)) {
                return; // User cancelled or validation failed
            }
            
            // Check if BOM already exists (either compatible or strict)
            if (BomFileManager.checkBomExists(bomFolder, getBomBaseName()) ||
                BomFileManager.checkBomExists(bomFolder, getOtherModeBomName())) {
                MessageDialog.openInformation(
                    targetPart.getSite().getShell(),
                    "Java Time Support Already Added",
                    "Java Time BOM files already exist in this project."
                );
                return;
            }
            
            // Copy BOM files
            BomFileManager.copyBomFiles(bomFolder, getBomBaseName(), getMode());
            
            // Optional: Add runtime JAR to xom-libraries and execution model
            // Uncomment the following line if you need the runtime JAR deployed:
            // BomFileManager.addRuntimeJarToProject(project);
            
            project.refreshLocal(IProject.DEPTH_INFINITE, null);
            
            // Show success message
            showSuccessMessage();
            
        } catch (Exception e) {
            showError("Error",
                "Failed to add Java Time support: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }
    
    /**
     * Show an error dialog to the user
     */
    protected void showError(String title, String message) {
        if (targetPart != null) {
            MessageDialog.openError(
                targetPart.getSite().getShell(),
                title,
                message
            );
        }
    }
    
    /**
     * Show success message after BOM files are added
     */
    protected void showSuccessMessage() {
        MessageDialog.openInformation(
            targetPart.getSite().getShell(),
            "Success",
            getSuccessMessage()
        );
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
     * @param project the project
     * @param bomFolder the BOM folder
     * @return true if validation passed and action should continue, false to cancel
     */
    protected abstract boolean performModeSpecificValidation(IProject project, IFolder bomFolder);
    
    /**
     * Get the success message to display after BOM files are added
     */
    protected abstract String getSuccessMessage();
}

