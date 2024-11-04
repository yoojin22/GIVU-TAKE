package com.accepted.givutake.global.service;

import com.accepted.givutake.global.enumType.ExceptionEnum;
import com.accepted.givutake.global.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.cloudfront.domain}")
    private String cloudfrontDomain;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(String keyName, File file) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build(),
                RequestBody.fromFile(file));
    }

    public GetObjectResponse downloadFile(String keyName) {
        return s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build()).response();
    }

    public void deleteFile(String keyName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build());
    }

    public List<S3Object> listFiles() {
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build());
        return listObjectsV2Response.contents();
    }

    public void deleteProfileImage(String keyName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build());
    }

    public String uploadProfileImage(MultipartFile profile) throws IOException {

        String fileName = UUID.randomUUID() + getFileExtension(Objects.requireNonNull(profile.getOriginalFilename()));
        String keyName = "public/profiles/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(profile.getInputStream(), profile.getSize()));

        return "https://" + cloudfrontDomain + "/" + keyName;
    }

    public void deleteThumbnailImage(String keyName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build());
    }

    public String uploadThumbnailImage(MultipartFile thumbnail) throws IOException {
        String fileName = UUID.randomUUID() + getFileExtension(Objects.requireNonNull(thumbnail.getOriginalFilename()));
        String keyName = "public/posts/thumbnails/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(thumbnail.getInputStream(), thumbnail.getSize()));

        return "https://" + cloudfrontDomain + "/" + keyName;
    }

    public void deleteContentImage(String keyName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build());
    }

    public String uploadContentImage(MultipartFile content) throws IOException {
        String fileName = UUID.randomUUID() + getFileExtension(Objects.requireNonNull(content.getOriginalFilename()));
        String keyName = "public/posts/contents/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(content.getInputStream(), content.getSize()));

        return "https://" + cloudfrontDomain + "/" + keyName;
    }

    public void deleteReviewImage(String keyName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build());
    }

    public String uploadReviewImage(MultipartFile review) throws IOException {
        String fileName = UUID.randomUUID() + getFileExtension(Objects.requireNonNull(review.getOriginalFilename()));
        String keyName = "public/posts/reviews/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(review.getInputStream(), review.getSize()));

        return "https://" + cloudfrontDomain + "/" + keyName;
    }

    public String uploadCertificateImage(MultipartFile certificateImage) throws IOException {
        String fileName = UUID.randomUUID().toString() + getFileExtension(certificateImage.getOriginalFilename());
        String keyName = "public/certificates/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(certificateImage.getInputStream(), certificateImage.getSize()));

        return "https://" + cloudfrontDomain + "/" + keyName;
    }

    public void deleteCertificateImage(String keyName) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build());
    }

    public String parseObjectKeyFromCloudfrontUrl(String cloudfrontUrl) {
        try {
            URL url = new URL(cloudfrontUrl);
            String host = url.getHost();
            String path = url.getPath();

            if (!host.endsWith(cloudfrontDomain)) {
                throw new ApiException(ExceptionEnum.ILLEGAL_CLOUDFRONT_URL_EXCEPTION);
            }

            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            return path;
        } catch (MalformedURLException e) {
            throw new ApiException(ExceptionEnum.ILLEGAL_CLOUDFRONT_URL_EXCEPTION);
        }
    }

    private String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자가 없는 경우
        }
        return fileName.substring(lastIndexOf);
    }
}
