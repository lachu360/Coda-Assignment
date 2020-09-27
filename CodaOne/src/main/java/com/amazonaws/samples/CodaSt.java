package com.amazonaws.samples;

import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import com.amazonaws.ClientConfiguration;

import com.amazonaws.services.s3.AmazonS3EncryptionClientV2Builder;
import com.amazonaws.services.s3.AmazonS3EncryptionV2;
import com.amazonaws.services.s3.model.CryptoConfigurationV2;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;

public class CodaSt {

	public static void main(String[] args) {
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " ,e);
        }

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion("us-east-1")
            .build();
        
        String bucketName = "narayanan-coda-assignment-uhkhx";
        
        System.out.println("Creating bucket " + bucketName + "\n");
        s3.createBucket(bucketName);
        
        System.out.println("Listing buckets");
        for (Bucket bucket : s3.listBuckets()) {
            System.out.println(" - " + bucket.getName());
        }
        System.out.println();

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        SecretKey secretKey = keyGenerator.generateKey();

        String s3ObjectKey = "EncryptedContent.txt";
        String s3ObjectContent = "I got placed in coda global";

        AmazonS3EncryptionV2 s3Encryption = AmazonS3EncryptionClientV2Builder.standard()
                .withRegion(Regions.DEFAULT_REGION)
                .withClientConfiguration(new ClientConfiguration())
                .withCryptoConfiguration(new CryptoConfigurationV2().withCryptoMode(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterialsProvider(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        s3Encryption.putObject(bucketName, s3ObjectKey, s3ObjectContent);
        System.out.println(s3Encryption.getObjectAsString(bucketName, s3ObjectKey));
        s3Encryption.shutdown();
        
        

	}

}
