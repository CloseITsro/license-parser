package co.closeit.licenseparser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ApplicationParameters {
    
    public static final List<ParserParameter> NPM_FILES = Arrays.asList(ParserParameter.NPM_JSON_FILE, ParserParameter.NPM_JSON_FILE_1, ParserParameter.NPM_JSON_FILE_2, ParserParameter.NPM_JSON_FILE_3, ParserParameter.NPM_JSON_FILE_4, ParserParameter.NPM_JSON_FILE_5);
    public static final List<ParserParameter> NPM_PROJECT_NAMES = Arrays.asList(ParserParameter.NPM_PROJECT_NAME, ParserParameter.NPM_PROJECT_NAME_1, ParserParameter.NPM_PROJECT_NAME_2, ParserParameter.NPM_PROJECT_NAME_3, ParserParameter.NPM_PROJECT_NAME_4, ParserParameter.NPM_PROJECT_NAME_5);
    
    public static final List<ParserParameter> MAVEN_FILES = Arrays.asList(ParserParameter.MAVEN_XML_FILE, ParserParameter.MAVEN_XML_FILE_1, ParserParameter.MAVEN_XML_FILE_2, ParserParameter.MAVEN_XML_FILE_3, ParserParameter.MAVEN_XML_FILE_4, ParserParameter.MAVEN_XML_FILE_5);
    public static final List<ParserParameter> MAVEN_PROJECT_NAMES = Arrays.asList(ParserParameter.MAVEN_PROJECT_NAME, ParserParameter.MAVEN_PROJECT_NAME_1, ParserParameter.MAVEN_PROJECT_NAME_2, ParserParameter.MAVEN_PROJECT_NAME_3, ParserParameter.MAVEN_PROJECT_NAME_4, ParserParameter.MAVEN_PROJECT_NAME_5);
    
    
    private static final HashMap<ParserParameter, String> parameters = new HashMap<>();
    
    public enum ParserParameter{
        // absolute path to npm JSON file with licenses
        CUSTOM_JSON("customDependenciesJson"),
        CUSTOM_JSON_FILE("customDependencyFile"),
        
        NPM_JSON_FILE("npmJsonFile"), 
        NPM_JSON_FILE_1("npmJsonFile1"), 
        NPM_JSON_FILE_2("npmJsonFile2"), 
        NPM_JSON_FILE_3("npmJsonFile3"), 
        NPM_JSON_FILE_4("npmJsonFile4"), 
        NPM_JSON_FILE_5("npmJsonFile5"),
        
        NPM_PROJECT_NAME("npmProjectName"),
        NPM_PROJECT_NAME_1("npmProjectName1"),
        NPM_PROJECT_NAME_2("npmProjectName2"),
        NPM_PROJECT_NAME_3("npmProjectName3"),
        NPM_PROJECT_NAME_4("npmProjectName4"),
        NPM_PROJECT_NAME_5("npmProjectName5"),
        
        MAVEN_XML_FILE("mavenXmlFile"),
        MAVEN_XML_FILE_1("mavenXmlFile1"),
        MAVEN_XML_FILE_2("mavenXmlFile2"),
        MAVEN_XML_FILE_3("mavenXmlFile3"),
        MAVEN_XML_FILE_4("mavenXmlFile4"),
        MAVEN_XML_FILE_5("mavenXmlFile5"),
        
        MAVEN_PROJECT_NAME("mavenProjectName"),
        MAVEN_PROJECT_NAME_1("mavenProjectName1"),
        MAVEN_PROJECT_NAME_2("mavenProjectName2"),
        MAVEN_PROJECT_NAME_3("mavenProjectName3"),
        MAVEN_PROJECT_NAME_4("mavenProjectName4"),
        MAVEN_PROJECT_NAME_5("mavenProjectName5"),
        
        OUTPUT_TEXT_FILE("outputTextFile"), 
        OUTPUT_EXCEL_FILE("outputExcelFile"),
        // template for writing Dependency to text file
        TEXT_FILE_FORMAT("textFileDependencyTepmplate"),    
        // true or false. If true, license per dependency won't be filled to template. Instead, grouped license list will be provided after dependency overview
        TEXT_FILE_HEADER("textFileHeaderTemplate"),
        TEXT_FILE_FOOTER("textFileFooterTemplate"),
        EXCEL_COLUMNS("excelColumns"),
        // UNUSED
        GROUP_LICENSES("groupLicenses");

        ParserParameter(String name) {
            this.name = name;
        }
        
        private final String name;
        
        public static ParserParameter fromString(String parameter) {
            parameter = parameter.replace("-", "");
            for (ParserParameter param : values()) {
                if (param.name.equalsIgnoreCase(parameter)) {
                    return param;
                }
            }
            throw new IllegalArgumentException("Unsupported argument " + parameter);
        }
        
    }
    
    public static String getParameterValue(ParserParameter parameter) {
        if (parameters.containsKey(parameter)) {
            return parameters.get(parameter);
        }
        return null;
    }
    
    public static boolean isProvided(ParserParameter parameter) {
        return parameters.containsKey(parameter);
    }
    
    public static void loadParameters(String[] args) {
        ParserParameter parameter = null;
        boolean skipParam = false;
        
        for (String arg : args) {
            
            if (skipParam) {
                log.error("Skipping value " + arg);
                skipParam = false;
                continue;
            }
            
            if (parameter == null) {
                try {
                    parameter = ParserParameter.fromString(arg);
                } catch (IllegalArgumentException ex) {
                    log.error(ex.getMessage());
                    parameter = null;
                    skipParam = true;
                }                
            } else {
                parameters.put(parameter, arg);
                parameter = null;
            }
        }
    }
    
}
