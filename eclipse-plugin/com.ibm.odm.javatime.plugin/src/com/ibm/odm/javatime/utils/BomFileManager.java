/*
IBM Confidential
PID 5737-I23 5900-AN0 5900-AUD 5725-B69 5655-Y31 5725-W47 5725-A28
Copyright ILOG 1998, 2009
Copyright IBM Corp. 2009, 2026
*/

package com.ibm.odm.javatime.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.ibm.odm.javatime.Activator;

/**
 * Utility class for managing BOM file operations
 */
public class BomFileManager {
    
    private static final String[] BOM_EXTENSIONS = {".bom", ".voc", ".b2xa" };
    private static final String TEMPLATE_BASE_PATH = "resources/templates/";
    private static final String DEFAULT_LOCALE = "en_US";
    
    /**
     * Get the locale string to use for VOC files.
     * Uses the system default locale if available, otherwise defaults to en_US.
     *
     * @return locale string in format like "en_US", "fr_FR", etc.
     */
    private static String getLocaleString() {
        Locale locale = Locale.getDefault();
        if (locale != null) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            
            // If we have both language and country, use them
            if (language != null && !language.isEmpty() &&
                country != null && !country.isEmpty()) {
                return language + "_" + country;
            }
            // If we only have language, use it with default country
            else if (language != null && !language.isEmpty()) {
                return language + "_" + language.toUpperCase();
            }
        }
        // Default to en_US
        return DEFAULT_LOCALE;
    }
    
    /**
     * Check if BOM files already exist in the folder
     * 
     * @param bomFolder the BOM folder to check
     * @param baseName the base name of the BOM files
     * @return true if any BOM file exists
     */
    public static boolean checkBomExists(IFolder bomFolder, String baseName) {
        for (String ext : BOM_EXTENSIONS) {
            IFile file = bomFolder.getFile(baseName + ext);
            if (file.exists()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Copy BOM template files to the project's BOM folder
     * 
     * @param bomFolder the target BOM folder
     * @param baseName the base name for the target files
     * @param mode the mode ("compatible" or "strict")
     * @throws Exception if copy fails
     */
    public static void copyBomFiles(IFolder bomFolder, String baseName, String mode)
            throws Exception {
        Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
        
        if (bundle == null) {
            throw new Exception("Plugin bundle not found: " + Activator.PLUGIN_ID);
        }
        
        // Template path now includes mode subfolder
        String templatePath = TEMPLATE_BASE_PATH + mode + "/";
        
        String localeString = getLocaleString();
        
        for (String ext : BOM_EXTENSIONS) {
            // Template files are named with mode suffix in their respective folders
            String templateName = baseName + ext;
            String targetName = baseName + ext;
            
            // For VOC files, add locale suffix
            if (ext.equals(".voc")) {
                templateName = baseName   + "_" + localeString  + ext;
                targetName = baseName + "_" + localeString + ext;
            }
            
            URL templateUrl = FileLocator.find(
                bundle,
                new Path(templatePath + templateName),
                null
            );
            
            if (templateUrl == null) {
                throw new Exception("Template file not found: " + templatePath + templateName +
                    ". Make sure the template files are included in the plugin resources.");
            }
            
            URL fileUrl = FileLocator.toFileURL(templateUrl);
            
            // Read the template content efficiently
            // Use URI constructor that properly handles spaces in paths
            URI fileUri = new URI(fileUrl.getProtocol(), fileUrl.getPath(), null);
            String content = Files.readString(Paths.get(fileUri), StandardCharsets.UTF_8);
            
            String uuid = UUID.randomUUID().toString();
            content = content.replace("${UUID}", uuid);
            
            // Write the processed content to the target file
            try (InputStream processedStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))) {
                IFile targetFile = bomFolder.getFile(targetName);
                
                if (targetFile.exists()) {
                    // Update existing file
                    targetFile.setContents(processedStream, true, true, null);
                } else {
                    // Create new file
                    targetFile.create(processedStream, true, null);
                }
            }
        }
    }
    
    /**
     * Get a user-friendly description of the BOM files that will be added
     *
     * @param baseName the base name of the BOM files
     * @return formatted string listing the files
     */
    public static String getBomFilesList(String baseName) {
        StringBuilder sb = new StringBuilder();
        for (String ext : BOM_EXTENSIONS) {
            sb.append("- ").append(baseName).append(ext).append("\n");
        }
        return sb.toString();
    }
    
}


