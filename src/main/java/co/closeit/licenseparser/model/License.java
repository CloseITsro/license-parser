package co.closeit.licenseparser.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class License {

    private String licenseName;

    private LicenseType licenseType;

    private String licenseText;

    public static License createLicense(String licenseName, String licenseFilePath) {
        LicenseType licenseType = LicenseType.fromString(licenseName);

        License license = new License();
        license.setLicenseName(licenseName);
        license.setLicenseType(licenseType);

        if (licenseFilePath != null && !licenseFilePath.isEmpty() && !licenseFilePath.endsWith(".html")) {
            try (Stream<String> stream = Files.lines(Paths.get(licenseFilePath))) {
                StringBuilder sb = new StringBuilder();
                stream.forEach(l -> sb.append(l).append("\n"));
                license.setLicenseText(sb.toString());
            } catch (IOException ex) {
                log.error("Exception during license creation. Default license will be used. " + ex.getMessage());
            }
        }

        if (license.getLicenseText() == null) {
            license.setLicenseText(licenseType.getLicenseText());
        }

        return license;
    }

}
