package co.closeit.licenseparser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Dependency {
    
    public Dependency(PackageManager packageManager, String projectName) {
        this.packageManager = packageManager;
        this.projectName = projectName;
    }
    
    public enum PackageManager { MAVEN, NPM }
    
    private String name;
    
    private String version;
    
    private String projectName;
    
    private PackageManager packageManager;
    
    private String source;
    
    private List<License> licenses = new ArrayList<>();
    
    public String getProvidedLicensesNames() {
        return licenses.stream().map(l -> l.getLicenseName()).collect(Collectors.joining(", "));
    }
    
    public String getFullLicenses() {
        return licenses.stream().map(l -> l.getLicenseText()).collect(Collectors.joining("\n"));
    }
    
}
