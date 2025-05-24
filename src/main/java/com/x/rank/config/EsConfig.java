package com.x.rank.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.json.jsonb.JsonbJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

@Configuration
public class EsConfig {

    @Bean
    public ElasticsearchClient getEsClient() throws Exception {


//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "TL=53_6v2Ak2g_jkZhqc"));


//        RestClientBuilder builder = RestClient.builder(
//                        new HttpHost("localhost", 9200,"https"))
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
//                        .setDefaultCredentialsProvider(credentialsProvider));


//        Path caCertificatePath = Paths.get("/Users/liqiang/http_ca.crt");
//        CertificateFactory factory = CertificateFactory.getInstance("X.509");
//        InputStream is = Files.newInputStream(caCertificatePath);
//        Certificate trustedCa = factory.generateCertificate(is);
//
//        KeyStore trustStore = KeyStore.getInstance("pkcs12");
//        trustStore.load(null, null);
//        trustStore.setCertificateEntry("ca", trustedCa);
//        SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial(trustStore, null);
//        final SSLContext sslContext = sslContextBuilder.build();
//        RestClient builder = RestClient.builder(new HttpHost("localhost", 9200, "https"))
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setSSLContext(sslContext).setDefaultCredentialsProvider(credentialsProvider)).build();


        String serverUrl = "xxxxxxx";
        String apiKey = "xxxx";

// Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    SSLContextBuilder sscb = SSLContexts.custom();
                    try {
                        sscb.loadTrustMaterial((chain, authType) -> {
                            // 在这里跳过证书信息校验
                            return true;
                        });
                        httpClientBuilder.setSSLContext(sscb.build());

                    } catch (Exception ignored) {
                    }
                    // 这里跳过主机名称校验
                    httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                    return httpClientBuilder;
                })
                .build();

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
//        RestClientTransport restClientTransport = new RestClientTransport(builder, new JsonbJsonpMapper());
        return new ElasticsearchClient(transport);
    }

}
