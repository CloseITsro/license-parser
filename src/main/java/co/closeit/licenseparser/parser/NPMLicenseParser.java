package co.closeit.licenseparser.parser;

import co.closeit.licenseparser.model.Dependency;
import co.closeit.licenseparser.model.License;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Log4j
public class NPMLicenseParser {
    
    @Setter
    private String projectName;

    private JSONObject root;

    public void parseJson(String jsonPath) {
        JSONParser parser = new JSONParser();

        try (Reader reader = new FileReader(jsonPath)) {
            root = (JSONObject) parser.parse(reader);
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

        Iterator iterator = root.keySet().iterator();
        while (iterator.hasNext()) {
            Dependency dependency = new Dependency(Dependency.PackageManager.NPM, projectName);

            String dependencyName = (String) iterator.next();
            
            // Remove first @ in case like @babel/core@7.0.0. Then split by @ to get version
            String[] dependencyNameVersion = dependencyName.charAt(0) == '@' ? dependencyName.substring(1).split("@") : dependencyName.split("@");
            
            dependency.setName(dependencyNameVersion[0]);
            if (dependencyNameVersion.length > 1) {
                dependency.setVersion(dependencyNameVersion[1]);
            }
       
            JSONObject dependencyMeta = (JSONObject) root.get(dependencyName);

            dependency.setSource((String) dependencyMeta.get("repository"));

            String licenseFilePath = (String) dependencyMeta.get("licenseFile");
            Object licensesObj = dependencyMeta.get("licenses");

            if (licensesObj instanceof String) {
                dependency.getLicenses().add(License.createLicense((String) licensesObj, licenseFilePath));
            } else if (licensesObj instanceof JSONArray) {
                Iterator licensesIterator = ((JSONArray) licensesObj).iterator();

                while (licensesIterator.hasNext()) {
                    dependency.getLicenses().add(License.createLicense((String) licensesIterator.next(), licenseFilePath));
                }
            }

            dependencies.add(dependency);
        }
    }

}
