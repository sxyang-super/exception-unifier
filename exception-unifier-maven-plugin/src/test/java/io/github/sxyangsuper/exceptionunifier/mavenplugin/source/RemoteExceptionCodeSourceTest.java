package io.github.sxyangsuper.exceptionunifier.mavenplugin.source;

import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.MojoConfiguration;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.SyncResult;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.MojoConfigurationFixture;
import io.github.sxyangsuper.exceptionunifier.mavenplugin.handler.fixture.SyncResultFixture;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RemoteExceptionCodeSourceTest {

    @Test
    void should_setup_and_check_successfully() {
        MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
        Mockito.when(mojoConfiguration.getRemoteBaseURL())
                .thenReturn("http://localhost");

        RemoteExceptionCodeSource remoteExceptionCodeSource = new RemoteExceptionCodeSource();

        assertDoesNotThrow(() -> remoteExceptionCodeSource.setupAndCheck(mojoConfiguration));
    }

    @Test
    void should_throw_exception_given_remote_base_url_is_blank() {
        MojoConfiguration mojoConfiguration = Mockito.mock(MojoConfiguration.class);
        Mockito.when(mojoConfiguration.getRemoteBaseURL())
                .thenReturn("  ");

        RemoteExceptionCodeSource remoteExceptionCodeSource = new RemoteExceptionCodeSource();

        MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class,
                () -> remoteExceptionCodeSource.setupAndCheck(mojoConfiguration));

        assertEquals("remoteBaseURL can not be blank", mojoExecutionException.getMessage());
    }

    @Nested
    @WireMockTest
    class GetExceptionCodePrefixTest {

        @Test
        void should_get_exception_code_prefix_successfully(final WireMockRuntimeInfo wmRuntimeInfo) throws MojoExecutionException {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
                Mockito.when(mavenProject.getGroupId())
                        .thenReturn("com.example");
                Mockito.when(mavenProject.getArtifactId())
                        .thenReturn("test");
            });
            Mockito.when(mojoConfiguration.getRemoteBaseURL())
                    .thenReturn("http://localhost:" + wmRuntimeInfo.getHttpPort());

            wmRuntimeInfo.getWireMock()
                    .register(get("/prefix/com.example.test")
                            .willReturn(ResponseDefinitionBuilder.like(new ResponseDefinitionBuilder().withBody("SAMPLE").build()))
                    );

            RemoteExceptionCodeSource remoteExceptionCodeSource = new RemoteExceptionCodeSource();
            remoteExceptionCodeSource.setupAndCheck(mojoConfiguration);

            Assertions.assertEquals("SAMPLE", remoteExceptionCodeSource.getExceptionCodePrefix());
            Assertions.assertEquals("SAMPLE", remoteExceptionCodeSource.getExceptionCodePrefix());
        }

        @Test
        void should_get_exception_code_prefix_with_custom_query_successfully(final WireMockRuntimeInfo wmRuntimeInfo) throws MojoExecutionException {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
                Mockito.when(mavenProject.getGroupId())
                        .thenReturn("com.example");
                Mockito.when(mavenProject.getArtifactId())
                        .thenReturn("test");
            });
            Mockito.when(mojoConfiguration.getRemoteQuery())
                    .thenReturn(
                            MapUtil.<String, String>builder()
                                    .put("secret", "abc")
                                    .build()
                    );

            Mockito.when(mojoConfiguration.getRemoteBaseURL())
                    .thenReturn("http://localhost:" + wmRuntimeInfo.getHttpPort());

            wmRuntimeInfo.getWireMock()
                    .register(get("/prefix/com.example.test?secret=abc")
                            .willReturn(ResponseDefinitionBuilder.like(new ResponseDefinitionBuilder().withBody("SAMPLE").build()))
                    );

            RemoteExceptionCodeSource remoteExceptionCodeSource = new RemoteExceptionCodeSource();
            remoteExceptionCodeSource.setupAndCheck(mojoConfiguration);

            Assertions.assertEquals("SAMPLE", remoteExceptionCodeSource.getExceptionCodePrefix());
            Assertions.assertEquals("SAMPLE", remoteExceptionCodeSource.getExceptionCodePrefix());
        }

        @Test
        void should_get_exception_code_prefix_with_retry_successfully(final WireMockRuntimeInfo wmRuntimeInfo) throws MojoExecutionException {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
                Mockito.when(mavenProject.getGroupId())
                        .thenReturn("com.example");
                Mockito.when(mavenProject.getArtifactId())
                        .thenReturn("test");
            });
            Mockito.when(mojoConfiguration.getRemoteBaseURL())
                    .thenReturn("http://localhost:" + wmRuntimeInfo.getHttpPort());

            final WireMock wireMock = wmRuntimeInfo.getWireMock();

            // Setup sequential stubs for the same request
            wireMock.register(WireMock.get("/prefix/com.example.test")
                    .inScenario("SequentialResponses")
                    .willReturn(WireMock.aResponse()
                            .withStatus(HttpStatus.HTTP_INTERNAL_ERROR))
                    .willSetStateTo("SecondResponse"));

            wireMock.register(WireMock.get("/prefix/com.example.test")
                    .inScenario("SequentialResponses")
                    .whenScenarioStateIs("SecondResponse")
                    .willReturn(WireMock.aResponse()
                            .withStatus(HttpStatus.HTTP_INTERNAL_ERROR))
                    .willSetStateTo("ThirdResponse"));

            wireMock.register(WireMock.get("/prefix/com.example.test")
                    .inScenario("SequentialResponses")
                    .whenScenarioStateIs("ThirdResponse")
                    .willReturn(WireMock.aResponse()
                            .withStatus(HttpStatus.HTTP_INTERNAL_ERROR))
                    .willSetStateTo("FourthResponse"));

            wireMock.register(WireMock.get("/prefix/com.example.test")
                    .inScenario("SequentialResponses")
                    .whenScenarioStateIs("FourthResponse")
                    .willReturn(WireMock.aResponse()
                            .withStatus(200)
                            .withBody("SAMPLE")));

            RemoteExceptionCodeSource remoteExceptionCodeSource = new RemoteExceptionCodeSource();
            remoteExceptionCodeSource.setupAndCheck(mojoConfiguration);

            Assertions.assertEquals("SAMPLE", remoteExceptionCodeSource.getExceptionCodePrefix());
            WireMock.verify(4, getRequestedFor(urlPathEqualTo("/prefix/com.example.test")));
        }

        @Test
        void should_throw_exception_given_no_exception_code_server() throws MojoExecutionException {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
                Mockito.when(mavenProject.getGroupId())
                        .thenReturn("com.example");
                Mockito.when(mavenProject.getArtifactId())
                        .thenReturn("test");
            });
            Mockito.when(mojoConfiguration.getRemoteBaseURL())
                    .thenReturn("http://localhost");

            RemoteExceptionCodeSource remoteExceptionCodeSource = new RemoteExceptionCodeSource();
            remoteExceptionCodeSource.setupAndCheck(mojoConfiguration);

            final MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, remoteExceptionCodeSource::getExceptionCodePrefix);
            assertEquals("Fail to get exception code prefix for module com.example.test, remote request failed", mojoExecutionException.getMessage());
        }

        @Test
        void should_throw_exception_given_not_found_from_exception_code_server(final WireMockRuntimeInfo wmRuntimeInfo) throws MojoExecutionException {
            final MojoConfiguration mojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
                Mockito.when(mavenProject.getGroupId())
                        .thenReturn("com.example");
                Mockito.when(mavenProject.getArtifactId())
                        .thenReturn("test");
            });
            Mockito.when(mojoConfiguration.getRemoteBaseURL())
                    .thenReturn("http://localhost:" + wmRuntimeInfo.getHttpPort());

            wmRuntimeInfo.getWireMock()
                    .register(get("/prefix/com.example.test")
                            .willReturn(ResponseDefinitionBuilder.like(new ResponseDefinitionBuilder()
                                    .withStatus(404)
                                    .withBody("Not Found")
                                    .build()))
                    );

            RemoteExceptionCodeSource remoteExceptionCodeSource = new RemoteExceptionCodeSource();
            remoteExceptionCodeSource.setupAndCheck(mojoConfiguration);

            final MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, remoteExceptionCodeSource::getExceptionCodePrefix);
            assertEquals("Fail to get exception code prefix for module com.example.test, response status is 404, message is Not Found", mojoExecutionException.getMessage());
        }
    }

    @Nested
    @WireMockTest
    class PushTest {

        RemoteExceptionCodeSource remoteExceptionCodeSource;
        MojoConfiguration mojoConfiguration;

        @BeforeEach
        void setUp(final WireMockRuntimeInfo wmRuntimeInfo) throws MojoExecutionException {
            mojoConfiguration = MojoConfigurationFixture.getMojoConfigurationWithMavenProjectConsumer(mavenProject -> {
            });
            Mockito.when(mojoConfiguration.getRemoteBaseURL())
                    .thenReturn("http://localhost:" + wmRuntimeInfo.getHttpPort());

            remoteExceptionCodeSource = new RemoteExceptionCodeSource();
            remoteExceptionCodeSource.setupAndCheck(mojoConfiguration);
        }

        @Test
        void should_push_successfully_given_push_to_remote_successfully(final WireMockRuntimeInfo wmRuntimeInfo) {
            wmRuntimeInfo.getWireMock()
                    .register(post("/exception-enum/bulk")
                            .willReturn(ResponseDefinitionBuilder.like(new ResponseDefinitionBuilder().withStatus(HttpStatus.HTTP_CREATED).build()))
                    );

            final SyncResult syncResult = SyncResultFixture.getSyncResult();

            Assertions.assertDoesNotThrow(() -> remoteExceptionCodeSource.push(syncResult));

            wmRuntimeInfo.getWireMock().verifyThat(new RequestPatternBuilder()
                    .withRequestBody(equalToJson(JSONUtil.toJsonStr(syncResult))));
        }

        @Test
        void should_push_with_custom_query_successfully_given_push_to_remote_successfully(final WireMockRuntimeInfo wmRuntimeInfo) {
            Mockito.when(mojoConfiguration.getRemoteQuery())
                    .thenReturn(
                            MapUtil.<String, String>builder()
                                    .put("secret", "secret001")
                                    .build()
                    );

            wmRuntimeInfo.getWireMock()
                    .register(post("/exception-enum/bulk?secret=secret001")
                            .willReturn(ResponseDefinitionBuilder.like(new ResponseDefinitionBuilder().withStatus(HttpStatus.HTTP_CREATED).build()))
                    );

            final SyncResult syncResult = SyncResultFixture.getSyncResult();

            Assertions.assertDoesNotThrow(() -> remoteExceptionCodeSource.push(syncResult));

            wmRuntimeInfo.getWireMock().verifyThat(new RequestPatternBuilder()
                    .withRequestBody(equalToJson(JSONUtil.toJsonStr(syncResult))));
        }

        @Test
        void should_throw_exception_given_remote_does_not_exist() {
            final SyncResult syncResult = SyncResultFixture.getSyncResult();

            final MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, () -> remoteExceptionCodeSource.push(syncResult));

            Assertions.assertTrue(mojoExecutionException.getMessage().contains("Fail to report exception enums, response status is 404"));
        }

        @Test
        void should_throw_exception_given_remote_returns_500(final WireMockRuntimeInfo wmRuntimeInfo) {
            wmRuntimeInfo.getWireMock()
                    .register(post("/exception-enum/bulk")
                            .willReturn(ResponseDefinitionBuilder.like(new ResponseDefinitionBuilder().withStatus(HttpStatus.HTTP_INTERNAL_ERROR).build()))
                    );

            final SyncResult syncResult = SyncResultFixture.getSyncResult();

            final MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, () -> remoteExceptionCodeSource.push(syncResult));
            Assertions.assertTrue(mojoExecutionException.getMessage().contains("Fail to report exception enums, response status is 500"));

            wmRuntimeInfo.getWireMock().verifyThat(new RequestPatternBuilder()
                    .withRequestBody(equalToJson(JSONUtil.toJsonStr(syncResult))));
        }

        @Test
        void should_throw_exception_given_unknown_error_happens(final WireMockRuntimeInfo wmRuntimeInfo) {
            wmRuntimeInfo.getWireMock()
                    .register(post("/exception-enum/bulk")
                            .willReturn(ResponseDefinitionBuilder.like(new ResponseDefinitionBuilder().withFault(EMPTY_RESPONSE).build()))
                    );

            final SyncResult syncResult = SyncResultFixture.getSyncResult();

            final MojoExecutionException mojoExecutionException = assertThrows(MojoExecutionException.class, () -> remoteExceptionCodeSource.push(syncResult));

            Assertions.assertEquals("Fail to push sync result to server", mojoExecutionException.getMessage());
        }
    }
}
