package me.leejay.jenkins.dateparameter;

import hudson.AbortException;
import hudson.EnvVars;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import hudson.tasks.BuildWrapper;
import hudson.util.VariableResolver;
import me.leejay.jenkins.dateparameter.utils.LocalDatePattern;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by JuHyunLee on 2017. 5. 23..
 */
public class DateParameterValue extends StringParameterValue {

    private final static Logger log = LoggerFactory.getLogger(DateParameterDefinition.class);

    private static final long serialVersionUID = 1L;

    private String value;

    private String dateFormat;

    @DataBoundConstructor
    public DateParameterValue(String name, String value, String description) {
        super(name, value, description);
        this.value = value;
        log.info(">>>>> DateParameterValue: {}, {}, {}", name, value, description);
    }

    public void setDateFormat(String dateFormat) {
        log.info("setDateFormat:{}", dateFormat);
        this.dateFormat = dateFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public VariableResolver<String> createVariableResolver(AbstractBuild<?, ?> build) {
        log.info("createVariableResolver");
        return new VariableResolver<String>() {
            @Override
            public String resolve(String s) {
                return getValue();
            }
        };
    }

    @Override
    public void buildEnvironment(Run<?, ?> build, EnvVars env) {
        super.buildEnvironment(build, env);
    }

    @Override
    public BuildWrapper createBuildWrapper(AbstractBuild<?, ?> build) {
        if (StringUtils.isEmpty(getValue())) {
            return null;
        }

        if (!LocalDatePattern.isValidLocalDateString(getDateFormat(), getValue())) {
            return new BuildWrapper() {
                @Override
                public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                    throw new AbortException("Can't parse date format '" + getValue() + "' with '" + getDateFormat() + "'");
                }
            };
        }

        return null;

    }

}
