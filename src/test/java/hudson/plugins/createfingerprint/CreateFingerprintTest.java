/*
 *  The MIT License
 *
 *  Copyright 2011-2012 Marc Sanfacon
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package hudson.plugins.createfingerprint;

import hudson.model.Fingerprint;
import hudson.model.FreeStyleProject;
import hudson.model.FreeStyleBuild;
import hudson.tasks.Fingerprinter;
import hudson.tasks.Fingerprinter.FingerprintAction;
import org.jvnet.hudson.test.JenkinsRule;

import java.util.Collection;
import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

/**
 * @author Marc Sanfacon
 */
public class CreateFingerprintTest extends JenkinsRule {
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
