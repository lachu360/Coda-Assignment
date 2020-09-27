package com.amazonaws.samples;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;

public class AwsClass {

	public static void main(String[] args) {
		
		AWSCredentials AWS_CREDENTIALS = null;
        try {
        	AWS_CREDENTIALS = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " ,e);
        }
        
		AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(AWS_CREDENTIALS))
                .withRegion(Regions.US_EAST_1)
                .build();
		
		/*// Create a new security group.
        try {
            CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest(
                    "CodaSecurity1", "Getting Started Security Group");
            CreateSecurityGroupResult result = ec2Client
                    .createSecurityGroup(securityGroupRequest);
            System.out.println(String.format("Security group created: [%s]",
                    result.getGroupId()));
        } catch (AmazonServiceException ase) {
            // Likely this means that the group is already created, so ignore.
            System.out.println(ase.getMessage());
        }

        
        // Open up port 23 for TCP traffic to the associated IP from above (e.g. ssh traffic).
        IpPermission ipPermission = new IpPermission()
                .withIpProtocol("tcp")
                .withFromPort(22)
                .withToPort(22);
                

        List<IpPermission> ipPermissions = Collections.singletonList(ipPermission);

        try {
            // Authorize the ports to the used.
            AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(
                    "CodaSecurity", ipPermissions);
            ec2Client.authorizeSecurityGroupIngress(ingressRequest);
            System.out.println(String.format("Ingress port authroized: [%s]",
                    ipPermissions.toString()));
        } catch (AmazonServiceException ase) {
            // Ignore because this likely means the zone has already been authorized.
            System.out.println(ase.getMessage());
        }
	
        CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();

        createKeyPairRequest.withKeyName("codaKey1");
        CreateKeyPairResult createKeyPairResult = ec2Client.createKeyPair(createKeyPairRequest);
        KeyPair keyPair = new KeyPair();
        keyPair = createKeyPairResult.getKeyPair();
        String privateKey = keyPair.getKeyMaterial();
        
       RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId("ami-0080e4c5bc078760e")
                .withInstanceType("t1.micro")
                .withMinCount(1)
                .withMaxCount(1)
                .withSecurityGroups("CodaSecurity1");
 */
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId("ami-0080e4c5bc078760e")
                .withInstanceType("t2.micro")
                .withMinCount(1)
                .withMaxCount(1);
		
        RunInstancesResult runInstancesResult = ec2Client.runInstances(runInstancesRequest);
 
        Instance instance = runInstancesResult.getReservation().getInstances().get(0);
        String instanceId = instance.getInstanceId();
        //System.out.println("EC2 Instance Id: " + instanceId + instance.getLaunchTime() + instance.getKeyName() + instance.getState() + instance.getTags() );
        System.out.println("EC2 Instance Id: " + instanceId + instance.getLaunchTime() + instance.getKeyName() + instance.getState());
        
        CreateTagsRequest createTagsRequest = new CreateTagsRequest()
                .withResources(instance.getInstanceId())
                .withTags(new Tag("environment", "development"));
        ec2Client.createTags(createTagsRequest);
 
        
        StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(instanceId);
 
        ec2Client.startInstances(startInstancesRequest);

        
        

	}

}
