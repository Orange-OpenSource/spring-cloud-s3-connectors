package com.orange.spring.cloud.connector.s3.core.creator;

import com.orange.spring.cloud.connector.s3.core.jcloudswrappers.SpringCloudBlobStoreContext;
import com.orange.spring.cloud.connector.s3.core.service.S3ServiceInfo;
import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.springframework.cloud.service.AbstractServiceConnectorCreator;
import org.springframework.cloud.service.ServiceConnectorConfig;

import java.util.Properties;

import static org.jclouds.Constants.*;
import static org.jclouds.s3.reference.S3Constants.PROPERTY_S3_VIRTUAL_HOST_BUCKETS;

/**
 * Copyright (C) 2016 Arthur Halet
 * <p/>
 * This software is distributed under the terms and conditions of the 'MIT'
 * license which can be found in the file 'LICENSE' in this package distribution
 * or at 'http://opensource.org/licenses/MIT'.
 * <p/>
 * Author: Arthur Halet
 * Date: 24/02/2016
 */
public class S3FactoryCreator extends AbstractServiceConnectorCreator<SpringCloudBlobStoreContext, S3ServiceInfo> {

    private static final boolean SKIP_SSL_VERIFICATION = Boolean.getBoolean("skip.ssl.verification");

    private static final String PROXY_HOST = System.getProperty("http.proxyHost", null);

    private static final String PROXY_PASSWD = System.getProperty("http.proxyPassword", null);

    private static final int PROXY_PORT = Integer.getInteger("http.proxyPort", 80);

    private static final String PROXY_USER = System.getProperty("http.proxyUsername", null);

    public SpringCloudBlobStoreContext create(S3ServiceInfo serviceInfo, ServiceConnectorConfig serviceConnectorConfig) {
        Properties storeProviderInitProperties = new Properties();
        storeProviderInitProperties.put(PROPERTY_S3_VIRTUAL_HOST_BUCKETS, serviceInfo.isVirtualHostBuckets());
        this.loadProxy(storeProviderInitProperties);
        storeProviderInitProperties.put(PROPERTY_TRUST_ALL_CERTS, SKIP_SSL_VERIFICATION);
        ContextBuilder contextBuilder = ContextBuilder.newBuilder("s3");
        contextBuilder
                .overrides(storeProviderInitProperties)
                .endpoint(serviceInfo.getS3Host())
                .credentials(serviceInfo.getAccessKeyId(), serviceInfo.getSecretAccessKey());
        return new SpringCloudBlobStoreContext(contextBuilder.buildView(BlobStoreContext.class), serviceInfo.getBucket());
    }

    public void loadProxy(Properties storeProviderInitProperties) {
        if (PROXY_HOST == null) {
            return;
        }
        storeProviderInitProperties.put(PROPERTY_PROXY_HOST, PROXY_HOST);
        storeProviderInitProperties.put(PROPERTY_PROXY_PORT, PROXY_PORT);
        if (PROXY_PASSWD == null || PROXY_USER == null) {
            return;
        }
        storeProviderInitProperties.put(PROPERTY_PROXY_USER, PROXY_USER);
        storeProviderInitProperties.put(PROPERTY_PROXY_PASSWORD, PROXY_PASSWD);
    }
}
