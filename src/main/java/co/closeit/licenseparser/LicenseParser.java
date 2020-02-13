package co.closeit.licenseparser;

import co.closeit.licenseparser.model.Dependency;
import co.closeit.licenseparser.parser.NPMLicenseParser;
import java.util.ArrayList;
import java.util.List;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.CUSTOM_JSON;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.CUSTOM_JSON_FILE;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.OUTPUT_TEXT_FILE;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.OUTPUT_EXCEL_FILE;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.TEXT_FILE_FORMAT;
import static co.closeit.licenseparser.ApplicationParameters.ParserParameter.EXCEL_COLUMNS;
import co.closeit.licenseparser.model.ReplacementTag;
import co.closeit.licenseparser.parser.CustomJsonParser;
import co.closeit.licenseparser.parser.MavenLicenseParser;
import co.closeit.licenseparser.writer.ExcelWriter;
import co.closeit.licenseparser.writer.TextWriter;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.BasicConfigurator;

@Log4j
public class LicenseParser {

    public static void main(String[] args) {
        BasicConfigurator.configure();

        if (args != null && args.length != 0) {
            ApplicationParameters.loadParameters(args);
        }

        List<Dependency> dependencies = new ArrayList<>();

        if (ApplicationParameters.isProvided(CUSTOM_JSON)) {
            CustomJsonParser jsonParser = new CustomJsonParser();
            jsonParser.parseString(ApplicationParameters.getParameterValue(CUSTOM_JSON));
            jsonParser.getDepencies(dependencies);
        }

        if (ApplicationParameters.isProvided(CUSTOM_JSON_FILE)) {
            CustomJsonParser jsonParser = new CustomJsonParser();
            jsonParser.parseJson(ApplicationParameters.getParameterValue(CUSTOM_JSON_FILE));
            jsonParser.getDepencies(dependencies);
        }

        int fileCounter = 0;

        // license-checker --json > license.json
        for (ApplicationParameters.ParserParameter npmJsonFile : ApplicationParameters.NPM_FILES) {
            if (ApplicationParameters.isProvided(npmJsonFile)) {
                NPMLicenseParser npmLicense = new NPMLicenseParser();
                npmLicense.setProjectName(ApplicationParameters.getParameterValue(ApplicationParameters.NPM_PROJECT_NAMES.get(fileCounter)));
                npmLicense.parseJson(ApplicationParameters.getParameterValue(npmJsonFile));
                npmLicense.getDepencies(dependencies);
            }
            fileCounter++;
        }

        // mvn license:download-licenses
        fileCounter = 0;
        for (ApplicationParameters.ParserParameter mavenXmlFile : ApplicationParameters.MAVEN_FILES) {
            if (ApplicationParameters.isProvided(mavenXmlFile)) {
                MavenLicenseParser mavenLicense = new MavenLicenseParser();
                mavenLicense.setProjectName(ApplicationParameters.getParameterValue(ApplicationParameters.MAVEN_PROJECT_NAMES.get(fileCounter)));
                mavenLicense.parseXML(ApplicationParameters.getParameterValue(mavenXmlFile));
                mavenLicense.getDepencies(dependencies);
            }
            fileCounter++;
        }

        if (dependencies.isEmpty()) {
            log.error("No dependencies found. Export won't execute!");
            return;
        }

        if (ApplicationParameters.isProvided(OUTPUT_TEXT_FILE)) {
            TextWriter textWriter = new TextWriter();
            textWriter.setProvidedFormat(ApplicationParameters.getParameterValue(TEXT_FILE_FORMAT));
            textWriter.writeTextFile(dependencies, ApplicationParameters.getParameterValue(OUTPUT_TEXT_FILE));
        }

        if (ApplicationParameters.isProvided(OUTPUT_EXCEL_FILE)) {
            ExcelWriter excelWriter = new ExcelWriter();
            
            if (ApplicationParameters.isProvided(EXCEL_COLUMNS)) {
                excelWriter.setProvidedColumns(ReplacementTag.parseTags(ApplicationParameters.getParameterValue(EXCEL_COLUMNS)));
            }
            
            excelWriter.writeExcel(dependencies, ApplicationParameters.getParameterValue(OUTPUT_EXCEL_FILE));
        }

    }
}
