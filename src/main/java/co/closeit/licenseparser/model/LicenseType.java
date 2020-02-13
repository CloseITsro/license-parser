package co.closeit.licenseparser.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;

@Log4j2
public enum LicenseType {
    
    AFL("Academic Free License 2.1", "/licenses/AFL2.1.txt", Stream.of("AFLv2.1")),
    CDDL_1("Common Development and Distribution License 1.0", "/licenses/CDDL1.0.txt", Stream.of("COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0", "Common Development and Distribution License")),
    CC0("CC0 1.0", "/licenses/CC0.txt", Stream.of("CC0-1.0", "Public Domain, per Creative Commons CC0")),
    CC3("CC by 3", "/licenses/CC3.txt", Stream.of("CC-BY-3.0")),
    CC4("CC by 4", "/licenses/CC4.txt", Stream.of("CC-BY-4.0")),
    GNU_AGPL_3("GNU Affero General Public License 3.0", "/licenses/GNUAGPL3.txt", Stream.of("GNU Affero General Public License 3.0")),
    GNU_GPL_2_CP_EXCEPTION("GNU General Public License 2.0 with Classpath exception", "/licenses/GNUGPL2.0withClassPathException.txt", Stream.of("GNU General Public License, Version 2 with the Classpath Exception", "GNU General Public License, version 2 (GPL2), with the classpath exception")),
    GNU_GPL_3("GNU General Public License 3.0", "/licenses/GNUGPL3.0.txt", Stream.of("GNU General Public Library")),
    GNU_LGPL_2_1("GNU Lesser General Public License 2.1", "/licenses/GNULGPL2.1.txt", Stream.of("LGPL 2.1")),
    GNU_LGPL_3("GNU Lesser General Public License 3", "/licenses/GNULGPL3.txt", Stream.of("GNU Lesser Public License", "GNU Lesser General Public License")),
    BSD_2("The 2-Clause BSD License", "/licenses/BSD2.txt", Stream.of("BSD-2-Clause")),
    // This version has been vetted as an Open source license by the OSI as "The BSD License"
    BSD_3("The 3-Clause BSD License", "/licenses/BSD3.txt", Stream.of("BSD licence", "BSD*", "The BSD License", "BSD License", "BSD", "3-Clause BSD License", "BSD-3-Clause", "New BSD License", "Revised BSD", "The New BSD License")),
    BOUNCY_CASTLE("Bouncy Castle Licence", "/licenses/BouncyCastle.txt", Stream.of("Bouncy Castle Licence")),
    MPL_2("Mozilla Public License 2.0", "/licenses/MPL2.0.txt", Stream.of("Mozilla Public License")),
    MPL_1_1("Mozilla Public License 1.1", "/licenses/MPL1.1.txt", Stream.of("MPL 1.1")),
    ECLIPSE_PUBLIC_1_0("Eclipse Public License 1.0", "/licenses/EclipsePublic1.0.txt", Stream.of("Eclipse Public License v1.0", "Eclipse Public License (EPL), Version 1.0", "Eclipse Public License 1.0", "Eclipse Public License - v 1.0", "Eclipse Public License, Version 1.0")),
    ECLIPSE_DISTRIBUTION_1_0("Eclipse Distribution License 1.0", "/licenses/EclipseDistribution1.0.txt", Stream.of("Eclipse Distribution License (EDL), Version 1.0")),
    MIT("The MIT License", "/licenses/MIT.txt", Stream.of("MIT", "MIT License", "The MIT License", "MIT*")),
    APACHE_LICENSE_2_0("Apache License 2.0", "/licenses/Apache2.0.txt", Stream.of("Apache 2.0", "Apache-2.0", "Apache License 2.0", "Apache 2", "Apache Public License 2.0", "The Apache Software License, Version 2.0", "Apache License, Version 2.0", "The Apache License, Version 2.0", "License Apache License 2.0", "License The Apache Software License, Version 2.0")),
    APACHE_LICENSE_1_1("Apache License 1.1", "/licenses/Apache1.1.txt", Stream.of("Apache Software License, Version 1.1")),
    INDIANA_UNIVERSITY_1_1_1("Indiana University Extreme! Lab Software License Version 1.1.1", "/licenses/IndianaExtremeLicense 1.1.1.txt", Stream.of("Indiana University Extreme! Lab Software License, vesion 1.1.1")),
    ISC("ISC License", "/licenses/ISC.txt", Stream.of("ISC")),
    WTFPL_2("WTFPL 2.0", "/licenses/WTFPL.txt", Stream.of("WTFPL")),
    JSON("JSON License", "/licenses/JSON.txt", Stream.of("The JSON License")),
    UNKNOWN("Unknown", null, null);
    
    LicenseType(String name, String resource, Stream<String> alternativeNames) {
        this.name = name;
        this.genericLicenseText = resource;
        this.alternativeNames = alternativeNames == null ? null : alternativeNames.map(n -> n.replace(" ", "").toLowerCase()).collect(Collectors.toSet());
    }
    
    private final String name;
    private final String genericLicenseText;
    private final Set<String> alternativeNames;
    
    public static LicenseType fromString(String licenseName) {
        licenseName = licenseName.replace(" ", "").toLowerCase();
        
        for (LicenseType license : values()) {
            if (UNKNOWN.equals(license)) {
                continue;
            }
            if (license.alternativeNames.contains(licenseName)) { 
                return license;
            }
        }
        return UNKNOWN;
    }
    
    public String getName() {
        return name;
    }
    
    public String getLicenseText() {
        if (UNKNOWN.equals(this)) {
            return "";
        }
        
        try (InputStream is = getClass().getResourceAsStream(genericLicenseText);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining("\n"));
       
        } catch (IOException ex) {
            log.error("Cannot read internal license file. " + ex.getMessage());
            return "";
        }
    }
    
}
