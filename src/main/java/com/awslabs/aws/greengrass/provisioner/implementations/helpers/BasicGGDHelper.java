package com.awslabs.aws.greengrass.provisioner.implementations.helpers;

import com.awslabs.aws.greengrass.provisioner.data.GGDConf;
import com.awslabs.aws.greengrass.provisioner.interfaces.helpers.GGDHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BasicGGDHelper implements GGDHelper {
    public static final String GGDS = "ggds/";
    public static final String GGD_CONF = "ggd.conf";
    public static final String GGD_DEFAULTS_CONF = "ggds/ggd.defaults.conf";

    @Inject
    public BasicGGDHelper() {
    }

    @Override
    public GGDConf getGGDConf(String groupName, String ggdName) {
        File ggdConfigFile = new File(GGDS + ggdName + "/" + GGD_CONF);
        File scriptPath = new File(GGDS + ggdName);

        GGDConf.GGDConfBuilder ggdConfBuilder = GGDConf.builder();

        try {
            Config config = ConfigFactory.parseFile(ggdConfigFile);
            config = config.withValue("GROUP_NAME", ConfigValueFactory.fromAnyRef(groupName));
            Config fallback = ConfigFactory.parseFile(new File(GGD_DEFAULTS_CONF));
            config = config.withFallback(fallback);
            config = config.resolve();

            ggdConfBuilder.thingName(config.getString("conf.thingName"));
            ggdConfBuilder.connectedShadows(config.getStringList("conf.connectedShadows"));
            ggdConfBuilder.fromCloudSubscriptions(config.getStringList("conf.fromCloudSubscriptions"));
            ggdConfBuilder.toCloudSubscriptions(config.getStringList("conf.toCloudSubscriptions"));
            ggdConfBuilder.outputTopics(config.getStringList("conf.outputTopics"));
            ggdConfBuilder.inputTopics(config.getStringList("conf.inputTopics"));
            ggdConfBuilder.scriptName(ggdName);
            ggdConfBuilder.rootPath(scriptPath.toPath());
            ggdConfBuilder.dependencies(config.getStringList("conf.dependencies"));

            List<String> files = Arrays.stream(scriptPath.listFiles())
                    .map(File::getName)
                    .filter(filename -> !filename.equals(GGD_CONF))
                    .collect(Collectors.toList());

            ggdConfBuilder.files(files);
        } catch (ConfigException.Missing e) {
            log.error(e.getMessage());
            log.error("The configuration file for the GGD may be missing");
            System.exit(1);
        } catch (ConfigException e) {
            throw new UnsupportedOperationException(e);
        }

        GGDConf ggdConf = ggdConfBuilder.build();
        return ggdConf;
    }
}
