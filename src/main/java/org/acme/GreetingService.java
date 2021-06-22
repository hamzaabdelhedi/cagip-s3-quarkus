package org.acme;

import com.amazonaws.*;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@ApplicationScoped
public class GreetingService {

    public String greeting(String name) {

        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");

        String accessKey = System.getenv("S3_BUCKET_ACCESS_KEY");;
        String secretKey = System.getenv("S3_BUCKET_SECRET_KEY");;
        String bucketName = System.getenv("S3_BUCKET_BUCKET_NAME");;
        String fileName = "Welcome "+name+".txt";

        String fileContent = "Welcome on Scality " + name;
        try {
            Files.write(Paths.get("/tmp/myFile.txt"), fileContent.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            // Create an Amazon S3 client with endpoint
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(
                                    accessKey,
                                    secretKey
                            )
                    ))
                    .withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTP))
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    "https://s3.saas.cagip.gca",
                                    Regions.US_EAST_1.name()
                            )
                    )
                    .build();

            try {
                s3.putObject(bucketName, fileName, new File("/tmp/myFile.txt"));
            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }
            System.out.println("Done!");

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        return "The file was uploaded";
    }

    public String listAllObjects() {
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");

        String accessKey = System.getenv("S3_BUCKET_ACCESS_KEY");
        String secretKey = System.getenv("S3_BUCKET_SECRET_KEY");
        String bucketName = System.getenv("S3_BUCKET_BUCKET_NAME");

        String allFiles = "";
        try {
            // Create an Amazon S3 client with endpoint
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(
                            new BasicAWSCredentials(
                                    accessKey,
                                    secretKey
                            )
                    ))
                    .withClientConfiguration(new ClientConfiguration().withProtocol(Protocol.HTTP))
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(
                                    "https://s3.saas.cagip.gca",
                                    Regions.US_EAST_1.name()
                            )
                    )
                    .build();

            try {
                ObjectListing objectListing = s3.listObjects(bucketName);
                for (S3ObjectSummary objectSummary :
                        objectListing.getObjectSummaries()) {
                    allFiles += objectSummary.getKey() + "\n";
                    System.out.println( " - " + objectSummary.getKey() + "  " +
                            "(size = " + objectSummary.getSize() +
                            ")");
                }

            } catch (AmazonServiceException e) {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }
            System.out.println("Done!");

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        return allFiles;
    }

}