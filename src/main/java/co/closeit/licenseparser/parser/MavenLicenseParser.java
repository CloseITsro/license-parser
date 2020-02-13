package co.closeit.licenseparser.parser;

import co.closeit.licenseparser.model.Dependency;
import co.closeit.licenseparser.model.License;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.json.XML;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Log4j2
public class MavenLicenseParser {
    
    private static final String LICENSE_FOLDER = "/licenses/";
    
    @Setter
    private String projectName;
    
    private JSONObject root;
    private String licenseFileFolder;
    
    public void parseXML(String xmlPath) {
        try (BufferedReader br = new BufferedReader(new FileReader(xmlPath))){
            File xmlFile = new File(xmlPath);
            if (xmlFile.exists()) {
                String pathToLicenses = xmlFile.getParent() + LICENSE_FOLDER;
                if (new File(pathToLicenses).exists()) {
                    licenseFileFolder = pathToLicenses;
                } else {
                    log.error("Folder with licences doesn't exist. Default license texts will be used.");
                }
            }            
                    
            JSONParser parser = new JSONParser();
            root = (JSONObject) parser.parse(XML.toJSONObject(br).toString());
        } catch (IOException ex) {
            Logger.getLogger(MavenLicenseParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(MavenLicenseParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void getDepencies(List<Dependency> dependencies) {
        if (root == null) {
            return;
        }
        
        JSONArray dependenciesArr = (JSONArray)((JSONObject)((JSONObject)root.get("licenseSummary")).get("dependencies")).get("dependency");
        Iterator<JSONObject> dependenciesIterator = dependenciesArr.iterator();
        
        int index = 1;        
        while(dependenciesIterator.hasNext()) {
            Dependency dependency = new Dependency(Dependency.PackageManager.MAVEN, projectName);
            
            JSONObject dependencyJs = dependenciesIterator.next();
            
            dependency.setName((String) dependencyJs.get("artifactId"));
            
            Object version = dependencyJs.get("version");
            if (version instanceof Long) {
                dependency.setVersion(((Long)version).toString());
            } else if (version instanceof String) {
                dependency.setVersion((String)version);
            } else if (version instanceof Double) {
                dependency.setVersion(((Double)version).toString());
            }
            
            if (dependencyJs.get("licenses") instanceof String) {
                log.error("No license defined for " + dependency.getName() + " with index " + index);
                dependencies.add(dependency);
                continue;
            }
            
            Object licenseObj = ((JSONObject)dependencyJs.get("licenses")).get("license");
            if (licenseObj instanceof JSONObject) {
                JSONObject licenseJs = (JSONObject) licenseObj;
                parseLicense(dependency, licenseJs);
                
            } else if (licenseObj instanceof JSONArray) {
                Iterator<JSONObject> licensesIterator = ((JSONArray)licenseObj).iterator();
                while (licensesIterator.hasNext()) {
                    JSONObject licenseJs = licensesIterator.next();
                    parseLicense(dependency, licenseJs);     
                }
            }
            
            index++;
            dependencies.add(dependency);
        }
        
    }
    
    private void parseLicense(Dependency dependency, JSONObject license) {
        String licenseFileName = (String)license.get("file");
        String licenseFilePath = null;
        if (licenseFileFolder != null && licenseFileName != null && !licenseFileName.isEmpty()) {
            licenseFilePath = licenseFileFolder + licenseFileName;
        }
        
        dependency.getLicenses().add(License.createLicense((String)license.get("name"), licenseFilePath));
    }
}
