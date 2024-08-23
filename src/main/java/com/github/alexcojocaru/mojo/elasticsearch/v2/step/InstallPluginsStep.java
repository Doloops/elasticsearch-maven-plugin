package com.github.alexcojocaru.mojo.elasticsearch.v2.step;

import java.util.HashMap;
import java.util.Map;

import com.github.alexcojocaru.mojo.elasticsearch.v2.util.VersionUtil;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.logging.Log;

import com.github.alexcojocaru.mojo.elasticsearch.v2.InstanceConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.PluginConfiguration;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.FilesystemUtil;
import com.github.alexcojocaru.mojo.elasticsearch.v2.util.ProcessUtil;

/**
 * Install the required plugins into the current Elasticsearch instance.
 *
 * @author Alex Cojocaru
 */
public class InstallPluginsStep
        implements InstanceStep
{
    @Override
    public void execute(InstanceConfiguration config)
    {
	 if (true)
	     return;
	
        if (config.getClusterConfiguration().getPlugins().size() > 0)
        {
            if (VersionUtil.isEqualOrGreater_6_4_0(config.getClusterConfiguration().getVersion()))
            {
                FilesystemUtil.setScriptPermission(config, "opensearch-cli");
            }
            FilesystemUtil.setScriptPermission(config, "opensearch-plugin");
        }

        Log log = config.getClusterConfiguration().getLog();
        
        for (PluginConfiguration plugin : config.getClusterConfiguration().getPlugins())
        {
            log.info(String.format(
                    "Installing plugin '%s' with options '%s'",
                    plugin.getUri(), plugin.getEsJavaOpts()));
            
            Map<String, String> environment = new HashMap<>(config.getEnvironmentVariables());
            
            if (StringUtils.isNotBlank(plugin.getEsJavaOpts()))
            {
                environment.put("ES_JAVA_OPTS", plugin.getEsJavaOpts());
            }

            CommandLine cmd = ProcessUtil.buildCommandLine("bin/opensearch-plugin")
                    .addArgument("install")
                    .addArgument("--batch")
                    .addArgument(FilesystemUtil.fixFileUrl(plugin.getUri()), true);
            
            ProcessUtil.executeScript(config, cmd, environment);
        }
    }
}
