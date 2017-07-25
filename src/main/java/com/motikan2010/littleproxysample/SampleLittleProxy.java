package com.motikan2010.littleproxysample;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import io.netty.handler.codec.http.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSource;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import io.netty.channel.ChannelHandlerContext;

public class SampleLittleProxy {

    private static final int PORT = 8080;

    public static void main(String[] args) {

        SampleHttpFiltersSource sampleHttpFiltersSource = new SampleHttpFiltersSource();

        DefaultHttpProxyServer.bootstrap()
                .withPort(PORT)
                .withFiltersSource(sampleHttpFiltersSource)
                .withAllowLocalOnly(false)
                .withName("FilteringProxy")
                .start();
    }


    static class SampleHttpFiltersSource implements HttpFiltersSource{

        private final boolean CLIENT_TO_PROXY_REQUEST = true;
        private final boolean PROXY_TO_SERVER_REQUEST_ENABLE = true;
        private final boolean SERVER_TO_PROXY_RESPONSE_ENABLE = true;
        private final boolean PROXY_TO_CLIENT_RESPONSE_ENABLE = true;

        @Override
        public int getMaximumResponseBufferSizeInBytes() {
            return 10240 * 10240;
        }

        @Override
        public int getMaximumRequestBufferSizeInBytes() {
            return 10240 * 10240;
        }

        public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {

            return new HttpFilters() {

                @Override
                public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                    if(CLIENT_TO_PROXY_REQUEST == true){
                        showRequest(httpObject);
                    }

                    return null;
                }

                @Override
                public HttpResponse proxyToServerRequest(HttpObject httpObject) {
                    if(PROXY_TO_SERVER_REQUEST_ENABLE == true){
                        showRequest(httpObject);
                    }
                    return null;
                }

                @Override
                public HttpObject serverToProxyResponse(HttpObject httpObject) {
                    if(SERVER_TO_PROXY_RESPONSE_ENABLE == true){
                        showResponse(httpObject);
                    }

                    return httpObject;
                }

                @Override
                public HttpObject proxyToClientResponse(HttpObject httpObject) {
                    if(PROXY_TO_CLIENT_RESPONSE_ENABLE == true){
                        showResponse(httpObject);
                    }

                    return httpObject;
                }

                private void showRequest(HttpObject httpObject){
                    if (httpObject instanceof HttpRequest) {
                        HttpRequest httpRequest = (HttpRequest) httpObject;

                        // メソッド
                        System.out.print(httpRequest.getMethod() + " ");

                        // URI
                        System.out.print(httpRequest.getUri() + " ");

                        // HTTPバージョン
                        System.out.println(httpRequest.getProtocolVersion());

                        // ヘッダー
                        HttpHeaders httpHeaders = httpRequest.headers();
                        List<Map.Entry<String,String>> headerList = httpHeaders.entries();
                        for (Map.Entry<String, String> header : headerList){
                            System.out.println(header.getKey() + ": " + header.getValue());
                        }
                    }

                    if (httpObject instanceof HttpContent) {
                        HttpContent httpContent = (HttpContent) httpObject;
                        showContent(httpContent);
                    }
                    System.out.println();
                }

                private void showResponse(HttpObject httpObject){
                    // レスポンスヘッダ
                    if (httpObject instanceof HttpResponse) {
                        System.out.print(((HttpResponse) httpObject).getProtocolVersion() + " ");

                        System.out.println(((HttpResponse) httpObject).getStatus());

                        // ヘッダー
                        HttpHeaders httpHeaders = ((HttpResponse) httpObject).headers();
                        List<Map.Entry<String,String>> headerList = httpHeaders.entries();
                        for (Map.Entry<String, String> header : headerList){
                            System.out.println(header.getKey() + ": " + header.getValue());
                        }
                    }

                    // レスポンスボディ
                    if (httpObject instanceof HttpContent) {
                        HttpContent httpContent = (HttpContent) httpObject;
                        showContent(httpContent);
                    }
                    System.out.println();
                }

                private void showContent(HttpContent httpContent){
                    String resposeBody = httpContent.content().toString(Charset.defaultCharset());
                    System.out.println();
                    System.out.println(resposeBody);
                }

                @Override
                public void serverToProxyResponseTimedOut() {}

                @Override
                public void serverToProxyResponseReceiving() {}

                @Override
                public void serverToProxyResponseReceived() {}

                @Override
                public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {}

                @Override
                public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
                    return null;
                }

                @Override
                public void proxyToServerResolutionFailed(String hostAndPort) {}

                @Override
                public void proxyToServerRequestSent() {}

                @Override
                public void proxyToServerRequestSending() {}

                @Override
                public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {}

                @Override
                public void proxyToServerConnectionStarted() {}

                @Override
                public void proxyToServerConnectionSSLHandshakeStarted() {}

                @Override
                public void proxyToServerConnectionQueued() {}

                @Override
                public void proxyToServerConnectionFailed() {}

            };
        }
    }

}

