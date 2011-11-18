package hudson.plugins.createfingerprint;

import hudson.model.Fingerprint;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import hudson.tasks.Fingerprinter;
import hudson.tasks.Fingerprinter.FingerprintAction;
import org.jvnet.hudson.test.HudsonTestCase;
import java.util.Collection;
import java.io.*;

/**
 * @author Marc Sanfacon
 */
public class CreateFingerprintTest extends HudsonTestCase {
    public void testConfigRoundtrip() throws Exception {

        // Create a test file to create the fingerprint
        FileWriter fstream = new FileWriter("test.txt");
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Fingerprint");
        out.close();

        FreeStyleProject project = createFreeStyleProject();
        CreateFingerprint before = new CreateFingerprint("test.txt");
        project.getBuildersList().add(before);

        configRoundtrip(project);

        CreateFingerprint after = project.getBuildersList().get(CreateFingerprint.class);

        // Verify that the build runs correctly and that the Fingerprints are created
        assertNotSame(before,after);
        assertEqualDataBoundBeans(before,after);
        assertEquals(after.getTargets(), "test.txt");
        FreeStyleBuild build = assertBuildStatusSuccess(project.scheduleBuild2(0));
        String buildName = project.getName();
        Fingerprinter.FingerprintAction action = build.getAction(Fingerprinter.FingerprintAction.class);
        Collection<Fingerprint> fingerprints = action.getFingerprints().values();
        for (Fingerprint f: fingerprints) {
            assertTrue(f.getOriginal().is(build));
            assertTrue(f.getOriginal().getName().equals(buildName));
        }
    }
}
