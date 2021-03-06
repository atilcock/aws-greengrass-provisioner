package com.awslabs.aws.greengrass.provisioner.implementations.helpers;

import com.amazonaws.services.greengrass.model.Function;
import com.amazonaws.services.greengrass.model.Subscription;
import com.awslabs.aws.greengrass.provisioner.data.FunctionConf;
import com.awslabs.aws.greengrass.provisioner.data.GGDConf;
import com.awslabs.aws.greengrass.provisioner.interfaces.helpers.GGConstants;
import com.awslabs.aws.greengrass.provisioner.interfaces.helpers.GGVariables;
import com.awslabs.aws.greengrass.provisioner.interfaces.helpers.IoHelper;
import com.awslabs.aws.greengrass.provisioner.interfaces.helpers.IotHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.mock;

public class SubscriptionHelperTest {
    private BasicSubscriptionHelper basicSubscriptionHelper;
    private IoHelper ioHelper;
    private IotHelper iotHelper;
    private GGVariables ggVariables;
    private GGConstants ggConstants;

    @Before
    public void setup() {
        ioHelper = mock(IoHelper.class);
        iotHelper = mock(IotHelper.class);
        ggVariables = mock(GGVariables.class);
        ggConstants = mock(GGConstants.class);

        basicSubscriptionHelper = new BasicSubscriptionHelper();
        basicSubscriptionHelper.ioHelper = ioHelper;
        basicSubscriptionHelper.iotHelper = iotHelper;
        basicSubscriptionHelper.ggConstants = ggConstants;
        basicSubscriptionHelper.ggVariables = ggVariables;
    }

    @Test
    public void simpleDirectFunctionToFunctionTopicMappingTest() {
        List<String> topics = Arrays.asList("a", "b", "c", "d", "e");
        Map<Function, FunctionConf> map = new HashMap<>();

        FunctionConf abInput = FunctionConf.builder().inputTopics(topics).outputTopics(new ArrayList<>()).build();
        String abInputArn = "abInputArn";
        Function abInputFunction = new Function()
                .withFunctionArn(abInputArn);

        map.put(abInputFunction, abInput);

        FunctionConf abOutput = FunctionConf.builder().outputTopics(topics).inputTopics(new ArrayList<>()).build();
        String abOutputArn = "abOutputArn";
        Function abOutputFunction = new Function()
                .withFunctionArn(abOutputArn);

        map.put(abOutputFunction, abOutput);

        List<GGDConf> ggdConfList = new ArrayList<>();

        List<Subscription> output = basicSubscriptionHelper.connectFunctionsAndDevices(map, ggdConfList);

        topics.stream()
                .allMatch(topic -> oneMatches(output, abInputArn, abOutputArn, topic));
    }

    private boolean oneMatches(List<Subscription> subscriptions, String expectedTarget, String expectedSource, String expectedSubject) {
        return subscriptions.stream()
                .anyMatch(subscription -> subscription.getSource().equals(expectedSource) &&
                        subscription.getSubject().equals(expectedSubject) &&
                        subscription.getTarget().equals(expectedTarget));
    }
}
