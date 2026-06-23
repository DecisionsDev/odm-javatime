/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.utils;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.common.util.EList;

import ilog.rules.studio.model.base.IlrModelFolder;
import ilog.rules.studio.model.base.IlrRuleProject;
import ilog.rules.studio.model.bom.IlrBOMFolder;
import ilog.rules.studio.model.resource.IlrResourceElement;
import ilog.rules.studio.ui.explorer.nodes.IlrRuleProjectNode;

/**
 * Utility class for ODM project operations
 */
public class ProjectHelper {
    
    
    /**
     * Result class to hold project and BOM folder information
     */
    public static class ProjectBomInfo {
        private final IProject project;
        private final IFolder bomFolder;
        
        public ProjectBomInfo(IProject project, IFolder bomFolder) {
            this.project = project;
            this.bomFolder = bomFolder;
        }
        
        public IProject getProject() {
            return project;
        }
        
        public IFolder getBomFolder() {
            return bomFolder;
        }
    }
    
    /**
     * Extract project and BOM folder from a selection element.
     * Handles various selection types including IlrRuleProjectNode, IProject, IFolder, and BOM folders.
     *
     * @param element the selected element
     * @return ProjectBomInfo containing project and BOM folder, or null if extraction fails
     */
    public static ProjectBomInfo getProjectAndBomFromSelection(Object element) {
        IProject project = null;
        IFolder bomFolder = null;
        
        // Handle IlrRuleProjectNode from Rule Project Explorer
        if (element instanceof IlrRuleProjectNode) {
            Object userObject = ((IlrRuleProjectNode) element).getUserObject();
            if (userObject instanceof IlrRuleProject) {
                IlrRuleProject ruleProject = (IlrRuleProject) userObject;
                project = (IProject) ruleProject.getResource();
                
                // Get BOM folder from model folders
                EList<IlrModelFolder> modelFolders = ruleProject.getModelFolders();
                for (IlrModelFolder modelFolder : modelFolders) {
                    if (modelFolder instanceof IlrBOMFolder) {
                    	bomFolder = modelFolder.getFolder();
                    	break;
                    }
                }
            }
        }
        
        if (element instanceof IFolder) {
        	project = ((IFolder)element).getProject();

        	IlrRuleProject ruleProject = (IlrRuleProject) ilog.rules.studio.model.IlrStudioModelPlugin.getResourceManager().getElementFromResource(project);
            EList<IlrModelFolder> modelFolders = ruleProject.getModelFolders();
            for (IlrModelFolder modelFolder : modelFolders) {
                if (modelFolder instanceof IlrBOMFolder) {
                	bomFolder = modelFolder.getFolder();
                	break;
                }
            }
        }
            
        return new ProjectBomInfo(project, bomFolder);
    }
    


}


