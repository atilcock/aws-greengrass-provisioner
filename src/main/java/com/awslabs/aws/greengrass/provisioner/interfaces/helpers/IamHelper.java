package com.awslabs.aws.greengrass.provisioner.interfaces.helpers;

import com.amazonaws.services.identitymanagement.model.Role;

public interface IamHelper {
    /**
     * Returns the role object
     *
     * @param name
     * @param assumeRolePolicyDocument
     * @return
     */
    Role createRoleIfNecessary(String name, String assumeRolePolicyDocument);

    void attachRolePolicy(Role role, String policyArn);

    String getAccountId();
}
