/*
 * The MIT License
 *
 *  Copyright 2011-2012 Marc Sanfacon
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.createfingerprint;

import hudson.Launcher;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.tasks.Fingerprinter;
import hudson.tasks.BuildStepDescriptor;
import hudson.model.AbstractProject;

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;

/**
 * Create a fingerprint during the build process instead of
 * waiting for a build to complete. This is useful when using
 * plug-ins Join to trigger other jobs in the build process
 *
 * @author Marc Sanfacon
 */
public class CreateFingerprint extends Builder {
    /**
     * Comma-separated list of files/directories to be fingerprinted.
     */
    private final String targets;

    @DataBoundConstructor
    public CreateFingerprint(String targets) {
        this.targets = targets;
    }

    public String getTargets() {
        return targets;
    }

    @Override
    public boolean perform(AbstractBuild<?,?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        Fingerprinter fingerprinter = new Fingerprinter(this.targets, false);
        return fingerprinter.perform(build, launcher, listener);
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public String getDisplayName() {
            return Messages.CreateFingerprint_DisplayName();
        }

        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
