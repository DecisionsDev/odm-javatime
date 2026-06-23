/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Utility class to detect if a project uses java.util.Date
 */
public class DateUsageDetector {
    
    /**
     * Detect if the project uses java.util.Date
     * 
     * @param project the ODM project
     * @param bomFolder the BOM folder to scan
     * @return true if java.util.Date usage is detected
     */
    public static boolean detectDateUsage(IProject project, IFolder bomFolder) {
        try {
            // Check BOM files for Date references
            if (bomFolder.exists()) {
                IResource[] members = bomFolder.members();
                for (IResource resource : members) {
                    if (resource instanceof IFile) {
                        IFile file = (IFile) resource;
                        String name = file.getName();
                        
                        // Check .voc, .b2xa, .bom files
                        if (name.endsWith(".voc") || name.endsWith(".b2xa") || 
                            name.endsWith(".bom")) {
                            if (fileContainsDateReference(file)) {
                                return true;
                            }
                        }
                    }
                }
            }
            
            // Could add more sophisticated detection:
            // - Check XOM folder for Date class usage
            // - Parse .ruleproject for Date references
            // - Scan rule files for Date usage
            
            return false;
            
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if a file contains references to java.util.Date
     * 
     * @param file the file to check
     * @return true if Date references are found
     */
    private static boolean fileContainsDateReference(IFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getContents()))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                // Look for java.util.Date references
                if (line.contains("java.util.Date") || 
                    line.contains("java/util/Date") ) {
                    return true;
                }
            }
            
        } catch (Exception e) {
            // If we can't read the file, assume no Date usage
            e.printStackTrace();
        }
        
        return false;
    }
}


