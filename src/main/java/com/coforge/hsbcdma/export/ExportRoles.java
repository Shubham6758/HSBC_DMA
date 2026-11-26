package com.coforge.hsbcdma.export;

import java.util.Set;

public final class ExportRoles {

    private ExportRoles() {}

    public static final String ADMIN = "ADMIN";
    public static final String DEVELOPER = "DEVELOPER";

    public static final String PMO = "PMO";
    public static final String PM = "PM";

    public static final String TA = "TA";
    public static final String RDG = "RDG";

    public static final String LEADERSHIP = "LEADERSHIP";
    public static final String KARAT_ADMIN = "KARAT ADMIN";

    public static final String HBU_AI = "HBU-AI";
    public static final String HBU_CIMS = "HBU-CIMS";
    public static final String HBU_DATA = "HBU-DATA";
    public static final String HBU_DPA = "HBU-DPA";
    public static final String HBU_ENGINEERING = "HBU-ENGINEERING";
    public static final String HBU_EXPERIENCE = "HBU-EXPERIENCE";
    public static final String HBU_IA = "HBU-IA";
    public static final String HBU_QE = "HBU-QE";

    public static final Set<String> SUPER_USERS =
            Set.of(ADMIN, DEVELOPER);

    public static final Set<String> HBU_ROLES =
            Set.of(
                    HBU_AI,
                    HBU_CIMS,
                    HBU_DATA,
                    HBU_DPA,
                    HBU_ENGINEERING,
                    HBU_EXPERIENCE,
                    HBU_IA,
                    HBU_QE
            );
}
