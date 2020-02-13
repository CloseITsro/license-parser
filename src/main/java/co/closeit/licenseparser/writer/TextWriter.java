package co.closeit.licenseparser.writer;

import co.closeit.licenseparser.ApplicationParameters;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.TEXT_FILE_HEADER;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.TEXT_FILE_FOOTER;
import co.closeit.licenseparser.model.Dependency;
import co.closeit.licenseparser.model.ReplacementTag;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TextWriter {
    
    public static final String DEFAULT_FORMAT = 
            String.format("%s %s - %s \nSource: %s \n\n%s \n", 
                    ReplacementTag.NAME.getTag(), 
                    ReplacementTag.VERSION.getTag(), 
                    ReplacementTag.LICENSES_NAMES.getTag(), 
                    ReplacementTag.SOURCE.getTag(), 
                    ReplacementTag.FULL_LICENSES.getTag()) 
            + "---------------------------------------------------------------------------------------------------------------\n";
    
    @Setter
    private String providedFormat;
    private int dependencyCount;
    
    public void writeTextFile(List<Dependency> dependencies, String fileName) {
        dependencyCount = dependencies.size();
        
        String entryTemplate = providedFormat == null ? DEFAULT_FORMAT : providedFormat;
        
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")){
            if (ApplicationParameters.isProvided(TEXT_FILE_HEADER)) {
                writer.print(fillTemplateStatic(ApplicationParameters.getParameterValue(TEXT_FILE_HEADER)));
            }
            for (Dependency dependency : dependencies) {
                writer.print(fillTemplateDependency(entryTemplate, dependency));
            }
            if (ApplicationParameters.isProvided(TEXT_FILE_FOOTER)) {
                writer.print(fillTemplateStatic(ApplicationParameters.getParameterValue(TEXT_FILE_FOOTER)));
            }
        } catch (IOException ex) {
            log.error("Text file creation error. " + ex.getMessage());
        }
    }
    
    private String fillTemplateDependency(String template, Dependency dependency) {
        return template
                .replace(ReplacementTag.NAME.getTag(), ReplacementTag.NAME.getValue(dependency))
                .replace(ReplacementTag.VERSION.getTag(), ReplacementTag.VERSION.getValue(dependency))
                .replace(ReplacementTag.PACKAGE_MANAGER.getTag(), ReplacementTag.PACKAGE_MANAGER.getValue(dependency))
                .replace(ReplacementTag.LICENSES_NAMES.getTag(), ReplacementTag.LICENSES_NAMES.getValue(dependency))
                .replace(ReplacementTag.SOURCE.getTag(), ReplacementTag.SOURCE.getValue(dependency))
                .replace(ReplacementTag.FULL_LICENSES.getTag(), ReplacementTag.FULL_LICENSES.getValue(dependency))
                .replace(ReplacementTag.DEPENDENCY_COUNT.getTag(), String.valueOf(dependencyCount))
                .replace(ReplacementTag.PROJECT_NAME.getTag(), ReplacementTag.PROJECT_NAME.getValue(dependency));
    }
    
    private String fillTemplateStatic(String template) {
        return template
                .replace(ReplacementTag.DEPENDENCY_COUNT.getTag(), String.valueOf(dependencyCount));
    }
    
}
