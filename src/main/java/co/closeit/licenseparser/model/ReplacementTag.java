package co.closeit.licenseparser.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;


public enum ReplacementTag {
    
    NAME("Name", "%name%"), 
    VERSION("Version", "%version%"), 
    PACKAGE_MANAGER("Package manager", "%packageManager%"), 
    PROJECT_NAME("Project name", "%projectName%"), 
    LICENSES_NAMES("Licenses", "%licensesNames%"), 
    SOURCE("Source", "%source%"), 
    FULL_LICENSES("Full licenses", "%license%"), 
    DEPENDENCY_COUNT("Dependency count", "%dependencyCount%");
    
    ReplacementTag(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }
    
    @Getter
    String name;
    
    @Getter
    String tag;
    
    public String getValue(Dependency dependency){
        switch(this){
            case NAME:
                return dependency.getName() == null ? "Unknown dependency" : dependency.getName();
            case VERSION:
                return dependency.getVersion() == null ? "" : dependency.getVersion();
            case PACKAGE_MANAGER:
                return dependency.getPackageManager() == null ? "" : dependency.getPackageManager().toString();
            case LICENSES_NAMES:
                return dependency.getProvidedLicensesNames();
            case SOURCE:
                return dependency.getSource() == null ? "" : dependency.getSource();
            case FULL_LICENSES:
                return dependency.getFullLicenses();
            case PROJECT_NAME:
                return dependency.getProjectName() == null ? "" : dependency.getProjectName();
            case DEPENDENCY_COUNT:
                return null;
            default:
                return null;
        }
    }
    
    public static List<ReplacementTag> parseTags(String tags) {
        List<ReplacementTag> parsedTags = new ArrayList<>();
        String[] tagArray = tags.replace(" ", "").split(",");
        
        for (String tagS : tagArray) {
            for (ReplacementTag tag : values()) {
                if (tag.getTag().equalsIgnoreCase(tagS)) {
                    parsedTags.add(tag);
                    break;
                }
            }
        }
        return parsedTags;
    }
    
}
