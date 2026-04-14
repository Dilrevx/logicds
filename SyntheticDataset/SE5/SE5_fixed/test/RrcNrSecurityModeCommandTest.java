import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import main.RrcNr;
import commons.SecurityModeCmdS;
import commons.CritExts;
import commons.SecurityModeCmd;
import commons.SecurityCfgSmc;

import java.lang.reflect.Field;

public class RrcNrSecurityModeCommandTest {
    private RrcNr rrc;

    @Before
    public void setUp() {
        rrc = new RrcNr();
    }

    @Test
    public void testProhibitNia0IntegAlgo() throws Exception {
        SecurityModeCmdS smc = new SecurityModeCmdS();
        CritExts crit = smc.crit_exts;
        SecurityModeCmd cmd = crit.security_mode_cmd();
        SecurityCfgSmc cfg = cmd.security_cfg_smc;
        cfg.security_algorithm_cfg.integrity_prot_algorithm_present = true;
        cfg.security_algorithm_cfg.integrity_prot_algorithm.value = 0;
        cfg.security_algorithm_cfg.ciphering_algorithm.value = 1;

        rrc.handle_security_mode_command(smc);

        Field f = RrcNr.class.getDeclaredField("security_is_activated");
        f.setAccessible(true);
        boolean activated = f.getBoolean(rrc);

        assertFalse(
                "Security Mode Command with integ_algo=0 should not activate security",
                activated
        );
    }

    @Test
    public void testAcceptValidIntegrityAlgo() throws Exception {
        SecurityModeCmdS smc = new SecurityModeCmdS();
        CritExts crit = smc.crit_exts;
        SecurityModeCmd cmd = crit.security_mode_cmd();
        SecurityCfgSmc cfg = cmd.security_cfg_smc;
        cfg.security_algorithm_cfg.integrity_prot_algorithm_present = true;
        cfg.security_algorithm_cfg.integrity_prot_algorithm.value = 1;
        cfg.security_algorithm_cfg.ciphering_algorithm.value = 1;

        rrc.handle_security_mode_command(smc);



        Field f = RrcNr.class.getDeclaredField("security_is_activated");
        f.setAccessible(true);
        boolean activated = f.getBoolean(rrc);

        assertTrue(
                "Security Mode Command with integ_algo!=0 should activate security",
                activated
        );
    }
}
