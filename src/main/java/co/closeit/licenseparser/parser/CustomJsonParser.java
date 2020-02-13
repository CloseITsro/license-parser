package co.closeit.licenseparser.parser;

import co.closeit.licenseparser.model.Dependency;
import co.closeit.licenseparser.model.License;
import co.closeit.licenseparser.model.LicenseType;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Log4j2
public class CustomJsonParser {

    private JSONArray root;

    public void parseString(String jsonString) {
        JSONParser parser = new JSONParser();

        try {
            root = (JSONArray) parser.parse(jsonString);
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }
    }

    public void parseJson(String jsonPath) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(jsonPath)) {
            root = (JSONArray) parser.parse(reader);
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } catch (ParseException ex) {
            log.error(ex.getMessage());
        }
    }

    public void getDepencies(List<Dependency> dependencies) {
        if (root == null) {
            return;
        }
        
        Iterator<JSONObject> dependencyIterator = root.iterator();
        while (dependencyIterator.hasNext()) {
            JSONObject dependencyObj = dependencyIterator.next();
            
            Dependency dependency = new Dependency();
            dependency.setName((String) dependencyObj.get("name"));
            dependency.setVersion((String)dependencyObj.get("version"));
            dependency.setSource((String)dependencyObj.get("source"));
            dependency.setProjectName((String)dependencyObj.get("projectName"));
            
            JSONArray licenses = (JSONArray) dependencyObj.get("licenses");
            Iterator<JSONObject> licenseIterator = licenses.iterator();
            while (licenseIterator.hasNext()) {
                JSONObject licenseObj = licenseIterator.next();
                
                String licenseName = (String) licenseObj.get("license");
                String licenseText = (String) licenseObj.get("text");
                
                License license = new License();
                license.setLicenseName(licenseName);
                license.setLicenseType(LicenseType.fromString(licenseName));
                license.setLicenseText(licenseText == null ? license.getLicenseType().getLicenseText() : licenseText);
                
                dependency.getLicenses().add(license);
            }
            dependencies.add(dependency);
        }
        
    }

}
