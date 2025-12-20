# Auth Server

基于 `Spring Security` 及 `Spring Authorization Server`

> https://docs.spring.io/spring-authorization-server/reference/

## OAuth2 2.1

### 授权码模式

> https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1-07#name-authorization-code-grant

```mermaid
+----------+
 | Resource |
 |   Owner  |
 +----------+
       ^
       |
       |
 +-----|----+          Client Identifier      +---------------+
 | .---+---------(1)-- & Redirection URI ---->|               |
 | |   |    |                                 |               |
 | |   '---------(2)-- User authenticates --->|               |
 | | User-  |                                 | Authorization |
 | | Agent  |                                 |     Server    |
 | |        |                                 |               |
 | |    .--------(3)-- Authorization Code ---<|               |
 +-|----|---+                                 +---------------+
   |    |                                         ^      v
   |    |                                         |      |
   ^    v                                         |      |
 +---------+                                      |      |
 |         |>---(4)-- Authorization Code ---------'      |
 |  Client |          & Redirection URI                  |
 |         |                                             |
 |         |<---(5)----- Access Token -------------------'
 +---------+       (w/ Optional Refresh Token)
```

### 授权码模式扩展`PKCE`(Proof Key for Code Exchange)

> https://datatracker.ietf.org/doc/html/rfc7636

```mermaid
                                                 +-------------------+
                                                 |   Authz Server    |
       +--------+                                | +---------------+ |
       |        |--(A)- Authorization Request ---->|               | |
       |        |       + t(code_verifier), t_m  | | Authorization | |
       |        |                                | |    Endpoint   | |
       |        |<-(B)---- Authorization Code -----|               | |
       |        |                                | +---------------+ |
       | Client |                                |                   |
       |        |                                | +---------------+ |
       |        |--(C)-- Access Token Request ---->|               | |
       |        |          + code_verifier       | |    Token      | |
       |        |                                | |   Endpoint    | |
       |        |<-(D)------ Access Token ---------|               | |
       +--------+                                | +---------------+ |
                                                 +-------------------+
```

`Code Verifier`和`Code Challenge`[在线生成](https://juejin.cn/post/7241058098974720037)

> `code_verifier`: Y3MMIhTITB7UMph21cf2a-vNbscnTFtXF6JjE4sGMRQ
> `code_challenge`: xQObLnSgnZMYVTNs3U168CDV0IlSHTDqK71O3t6lduE

### 客户端模式

> https://link.juejin.cn/?target=https%3A%2F%2Fdatatracker.ietf.org%2Fdoc%2Fhtml%2Fdraft-ietf-oauth-v2-1-07%23name-client-credentials-grant

```mermaid
     +---------+                                  +---------------+
     |         |                                  |               |
     |         |>--(1)- Client Authentication --->| Authorization |
     | Client  |                                  |     Server    |
     |         |<--(2)---- Access Token ---------<|               |
     |         |                                  |               |
     +---------+                                  +---------------+
```

### 设备码模式

> https://datatracker.ietf.org/doc/html/rfc8628

```mermaid
      +----------+                                +----------------+
      |          |>---(A)-- Client Identifier --->|                |
      |          |                                |                |
      |          |<---(B)-- Device Code,      ---<|                |
      |          |          User Code,            |                |
      |  Device  |          & Verification URI    |                |
      |  Client  |                                |                |
      |          |  [polling]                     |                |
      |          |>---(E)-- Device Code       --->|                |
      |          |          & Client Identifier   |                |
      |          |                                |  Authorization |
      |          |<---(F)-- Access Token      ---<|     Server     |
      +----------+   (& Optional Refresh Token)   |                |
            v                                     |                |
            :                                     |                |
           (C) User Code & Verification URI       |                |
            :                                     |                |
            v                                     |                |
      +----------+                                |                |
      | End User |                                |                |
      |    at    |<---(D)-- End user reviews  --->|                |
      |  Browser |          authorization request |                |
      +----------+                                +----------------+
```

## 授权服务器过滤器链

### SecurityContextHolderFilter

### AuthorizationServerContextFilter

> 使用`ThreadLocal`维护授权服务器上下文默认为`DefaultAuthorizationServerContext`可以拿到授权服务器配置`AuthorizationServerSettings`

### OidcLogoutEndpointFilter

> OIDC登出端点

- 配置类`OidcLogoutEndpointConfigurer`
- 默认端点`/connect/logout` GET or POST表单

| 参数名                        | 是否必需         | 说明                                                                                 |
|:---------------------------|:-------------|:-----------------------------------------------------------------------------------|
| `id_token_hint`            | REQUIRED     | 客户端之前获得的用户的 ID Token。服务器使用它来识别用户、验证会话，并决定是否要显示登出确认屏幕。                              |
| `post_logout_redirect_uri` | RECOMMENDED  | 登出完成后，用户浏览器应被重定向回的客户端 URI。这个 URI 必须事先在授权服务器的客户端配置中注册 (postLogoutRedirectUris)。     |
| `state`                    | OPTIONAL     | 一个由客户端生成的随机字符串，用于维护状态，并在重定向回客户端时进行 CSRF 保护。                                        |
| `client_id`                | OPTIONAL     | 客户端 ID。如果 post_logout_redirect_uri 没有注册，或者客户端有多个注册的登出 URI，提供此参数有助于服务器确定使用哪个客户端配置。  |

- AuthenticationConverter: `OidcLogoutAuthenticationConverter`
- AuthenticationProvider: `OidcLogoutAuthenticationProvider`
- AuthenticationSuccessHandler: `OidcLogoutAuthenticationSuccessHandler`
- AuthenticationFailureHandler: An internal implementation that uses the OAuth2Error associated with the OAuth2AuthenticationException and returns the OAuth2Error response.

### OAuth2AuthorizationServerMetadataEndpointFilter

> 授权服务器元数据端点

- 配置类`OAuth2AuthorizationServerMetadataEndpointConfigurer`
- 默认端点`/.well-known/oauth-authorization-server` GET 以JSON形式返回授权服务器元数据信息`OAuth2AuthorizationServerMetadata`

```json
{
    "issuer": "http://localhost:9000",
    "authorization_endpoint": "http://localhost:9000/oauth2/authorize",
    "pushed_authorization_request_endpoint": "http://localhost:9000/oauth2/par",
    "device_authorization_endpoint": "http://localhost:9000/oauth2/device_authorization",
    "token_endpoint": "http://localhost:9000/oauth2/token",
    "token_endpoint_auth_methods_supported": [
        "client_secret_basic",
        "client_secret_post",
        "client_secret_jwt",
        "private_key_jwt",
        "tls_client_auth",
        "self_signed_tls_client_auth"
    ],
    "jwks_uri": "http://localhost:9000/oauth2/jwks",
    "response_types_supported": [
        "code"
    ],
    "grant_types_supported": [
        "authorization_code",
        "client_credentials",
        "refresh_token",
        "urn:ietf:params:oauth:grant-type:device_code",
        "urn:ietf:params:oauth:grant-type:token-exchange"
    ],
    "revocation_endpoint": "http://localhost:9000/oauth2/revoke",
    "revocation_endpoint_auth_methods_supported": [
        "client_secret_basic",
        "client_secret_post",
        "client_secret_jwt",
        "private_key_jwt",
        "tls_client_auth",
        "self_signed_tls_client_auth"
    ],
    "introspection_endpoint": "http://localhost:9000/oauth2/introspect",
    "introspection_endpoint_auth_methods_supported": [
        "client_secret_basic",
        "client_secret_post",
        "client_secret_jwt",
        "private_key_jwt",
        "tls_client_auth",
        "self_signed_tls_client_auth"
    ],
    "code_challenge_methods_supported": [
        "S256"
    ],
    "tls_client_certificate_bound_access_tokens": true,
    "dpop_signing_alg_values_supported": [
        "RS256",
        "RS384",
        "RS512",
        "PS256",
        "PS384",
        "PS512",
        "ES256",
        "ES384",
        "ES512"
    ]
}
```

### OAuth2AuthorizationEndpointFilter

> 授权码流程授权请求处理端点

- 配置类`OAuth2AuthorizationEndpointConfigurer`
- 默认端点`/oauth2/authorize` GET or POST

> OIDC授权请求示例
> http://127.0.0.1:9000/oauth2/authorize?response_type=code&client_id=oidc-client&scope=openid%20profile&state=PWXqMKvJvmevD9ct4xHipJQe1TU7r09xI6MrM5Wcj5U%3D&redirect_uri=http://127.0.0.1:8000/login/oauth2/code/oidc-client&nonce=m3QgmuJ1TGxaU3RX_L7IPPMeCQtMEdu_d8TufR0ul_s
> 
> `PKCE`授权请求
> http://127.0.0.1:9000/oauth2/authorize?response_type=code&client_id=pkce-client&scope=openid%20profile&state=PWXqMKvJvmevD9ct4xHipJQe1TU7r09xI6MrM5Wcj5U%3D&redirect_uri=http://127.0.0.1:8000/authorized&code_challenge=xQObLnSgnZMYVTNs3U168CDV0IlSHTDqK71O3t6lduE&code_challenge_method=S256

| 参数名                     | 是否必需                                     | 说明                                                                          |
|:------------------------|:-----------------------------------------|:----------------------------------------------------------------------------|
| `response_type`         | REQUIRED                                 | 指定客户端期望的授权流程。对于授权码流，固定为 code。                                               |
| `client_id`             | REQUIRED                                 | 客户端的唯一标识符，必须与在 SAS 中注册的 clientId 匹配。                                        |
| `redirect_uri`          | OPTIONAL                                 | 授权服务器将授权码发送到的回调 URI。必须与 SAS 中注册的 URI 之一匹配。                                  |
| `scope`                 | OPTIONAL                                 | 客户端请求的权限范围。至少应包含 openid（用于 OIDC 身份验证）。                                      |
| `state`                 | RECOMMENDED                              | 一个由客户端生成的、用于防止 CSRF 攻击的随机值。SAS 必须原封不动地返回该值。                                 |
| `code_challenge`        | REQUIRED for public clients              | PKCE 流程的一部分。它是 code_verifier（客户端生成的随机字符串）经过 SHA256 哈希后再进行 Base64 URL 编码的结果。 |
| `code_challenge_method` | OPTIONAL for public clients              | 指定 code_challenge 的生成方法。对于 SHA256，固定为 S256。                                 |
| `prompt`                | OPTIONAL for OIDC Authentication Request | 指示授权服务器是否应提示用户重新验证或重新授权。可选值包括 none、login、consent 等。                                 |

授权确认请求 POST /oauth2/authorize

| 参数名            | 是否必需          | 说明                                                                        |
|:---------------|:--------------|:--------------------------------------------------------------------------|
| `client_id`    | REQUIRED      | 客户端 ID。                                                                   |
| `state`        | REQUIRED      | 授权请求中客户端提供的原始 state 值。                                                    |
| `scope`        | OPTIONAL      | 客户端最初请求的 Scope，用户选择同意的部分。例如：openid profile                                |

- AuthenticationConverter
  - `OAuth2AuthorizationCodeRequestAuthenticationConverter`
  - `OAuth2AuthorizationConsentAuthenticationConverter`
- AuthenticationProvider
  - `OAuth2AuthorizationCodeRequestAuthenticationProvider` 处理授权请求 `OAuth2AuthorizationCodeRequestAuthenticationToken`
  - `OAuth2AuthorizationConsentAuthenticationProvider` 处理授权确认请求 `OAuth2AuthorizationConsentAuthenticationToken`
- AuthenticationSuccessHandler: An internal implementation that handles an “authenticated” OAuth2AuthorizationCodeRequestAuthenticationToken and returns the OAuth2AuthorizationResponse.
- AuthenticationFailureHandler: An internal implementation that uses the OAuth2Error associated with the OAuth2AuthorizationCodeRequestAuthenticationException and returns the OAuth2Error response.

### OAuth2DeviceVerificationEndpointFilter

> 设备激活验证端点

- 默认端点 POST `/oauth2/device_verification`
- AuthenticationConverter
  - `OAuth2DeviceVerificationAuthenticationConverter`
  - `OAuth2DeviceAuthorizationConsentAuthenticationConverter`
- AuthenticationProvider
  - `OAuth2DeviceVerificationAuthenticationProvider`
  - `OAuth2DeviceAuthorizationConsentAuthenticationProvider`

### OidcProviderConfigurationEndpointFilter

> OIDC provider配置端点

- 默认端点`/.well-known/openid-configuration` GET 以JSON形式返回授权服务器OIDC provider配置信息`OidcProviderConfiguration`


```json
{
    "issuer": "http://localhost:9000",
    "authorization_endpoint": "http://localhost:9000/oauth2/authorize",
    "pushed_authorization_request_endpoint": "http://localhost:9000/oauth2/par",
    "device_authorization_endpoint": "http://localhost:9000/oauth2/device_authorization",
    "token_endpoint": "http://localhost:9000/oauth2/token",
    "token_endpoint_auth_methods_supported": [
        "client_secret_basic",
        "client_secret_post",
        "client_secret_jwt",
        "private_key_jwt",
        "tls_client_auth",
        "self_signed_tls_client_auth"
    ],
    "jwks_uri": "http://localhost:9000/oauth2/jwks",
    "userinfo_endpoint": "http://localhost:9000/userinfo",
    "end_session_endpoint": "http://localhost:9000/connect/logout",
    "response_types_supported": [
        "code"
    ],
    "grant_types_supported": [
        "authorization_code",
        "client_credentials",
        "refresh_token",
        "urn:ietf:params:oauth:grant-type:device_code",
        "urn:ietf:params:oauth:grant-type:token-exchange"
    ],
    "revocation_endpoint": "http://localhost:9000/oauth2/revoke",
    "revocation_endpoint_auth_methods_supported": [
        "client_secret_basic",
        "client_secret_post",
        "client_secret_jwt",
        "private_key_jwt",
        "tls_client_auth",
        "self_signed_tls_client_auth"
    ],
    "introspection_endpoint": "http://localhost:9000/oauth2/introspect",
    "introspection_endpoint_auth_methods_supported": [
        "client_secret_basic",
        "client_secret_post",
        "client_secret_jwt",
        "private_key_jwt",
        "tls_client_auth",
        "self_signed_tls_client_auth"
    ],
    "code_challenge_methods_supported": [
        "S256"
    ],
    "tls_client_certificate_bound_access_tokens": true,
    "dpop_signing_alg_values_supported": [
        "RS256",
        "RS384",
        "RS512",
        "PS256",
        "PS384",
        "PS512",
        "ES256",
        "ES384",
        "ES512"
    ],
    "subject_types_supported": [
        "public"
    ],
    "id_token_signing_alg_values_supported": [
        "RS256"
    ],
    "scopes_supported": [
        "openid"
    ]
}
```

### NimbusJwkSetEndpointFilter

> JwkSet端点

- 默认端点：GET `/oauth2/jwks` 以json形式返回`JWKSet` 一般是资源服务器使用 用来初始化`JwtDecoder` 解码JWT字符串

### OAuth2ClientAuthenticationFilter

> OAuth2客户端认证（**令牌端点、令牌自省端点、令牌撤销端点**均需要对客户端进行认证）

- 配置类`OAuth2ClientAuthenticationConfigurer`
- 默认的`DelegatingAuthenticationConverter`负责将请求信息转换为待认证对象`OAuth2ClientAuthenticationToken`
- AuthenticationConverter
  - `JwtClientAssertionAuthenticationConverter`
    - 认证方法
      - client_secret_jwt(对称加密)
      - private_key_jwt(非对称加密)
    - FORM参数
      - client_assertion_type(REQUIRED): `urn:ietf:params:oauth:client-assertion-type:jwt-bearer`
      - client_assertion (REQUIRED)
      - client_id(REQUIRED)
  - `ClientSecretBasicAuthenticationConverter`
    - 认证方法 client_secret_basic
    - 解析basic认证请求头 Authorization: Basic base64(clientId:clientSecret)
  - `ClientSecretPostAuthenticationConverter`
    - 认证方法 client_secret_post
    - FORM参数
      - client_id (REQUIRED)
      - client_secret (REQUIRED)
  - `PublicClientAuthenticationConverter`
    - 认证方法 none
    - FORM参数
      - client_id (REQUIRED for public clients)
      - code_verifier (REQUIRED)
  - `X509ClientCertificateAuthenticationConverter`
- AuthenticationProvider
  - `JwtClientAssertionAuthenticationProvider`
  - `ClientSecretAuthenticationProvider`
  - `PublicClientAuthenticationProvider`
  - `X509ClientCertificateAuthenticationProvider`
- AuthenticationSuccessHandler
  - 默认在`SecurityContextHolder`保存已认证的`OAuth2ClientAuthenticationToken`
- AuthenticationFailureHandler
  - 默认响应401或400状态码并以json形式输出error信息 { "error": "invalid_client" }

> 以上`JwtClientAssertionAuthenticationProvider`在对客户端提交的jwt进行验证时主要是使用`JwtClientAssertionDecoderFactory`构建`JwtDecoder`同时执行解码操作
> 当认证方法为private_key_jwt时客户端配置签名算法需是`SignatureAlgorithm`同时客户端需要提供JwkSetUrl
> 客户端同时需要改造`DefaultAuthorizationCodeTokenResponseClient`中的`OAuth2AuthorizationCodeGrantRequestEntityConverter`，调用其addParametersConverter添加`NimbusJwtClientAuthenticationParametersConverter`
> 最终生成的请求参数包含 client_id、client_assertion_type、client_assertion
> 当认证方法为client_secret_jwt时客户端配置签名算法需是`MacAlgorithm` 客户端改造方法同上

### BearerTokenAuthenticationFilter

> 资源服务器过滤器

- 配置类`OAuth2ResourceServerConfigurer`
- AuthenticationProvider
  - `OpaqueTokenAuthenticationProvider`
  - `JwtAuthenticationProvider`

### OAuth2TokenEndpointFilter

> OAuth2令牌端点及令牌刷新端点

请求示例（客户端认证方式均采用client_secret_post）：
客户端模式获取访问令牌
```bash
curl -X POST 'http://127.0.0.1/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=xxx&client_secret=secret&grant_type=client_credentials&scope='
```
客户端模式刷新令牌
```bash
curl -X POST 'http://127.0.0.1/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=xxx&client_secret=secret&grant_type=refresh_token&refresh_token='
```
授权码模式OIDC获取访问令牌
```bash
curl -X POST 'http://127.0.0.1/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=xxx&client_secret=secret&grant_type=authorization_code&code=code&redirect_uri='
```
授权码模式OIDC刷新令牌（同客户端模式刷新令牌）
`PKCE`获取访问令牌（同授权码模式仅客户端认证参数不同）
```bash
curl -X POST 'http://127.0.0.1/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=xxx&code_verifier=Y3MMIhTITB7UMph21cf2a-vNbscnTFtXF6JjE4sGMRQ&grant_type=authorization_code&code=code&redirect_uri='
```
`PKCE`刷新令牌（框架原生不支持）
```bash
curl -X POST 'http://127.0.0.1/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=xxx&code_verifier=Y3MMIhTITB7UMph21cf2a-vNbscnTFtXF6JjE4sGMRQ&grant_type=refresh_token&refresh_token='
```
设备码模式获取访问令牌
```bash
curl -X POST 'http://127.0.0.1/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=device-client&device_code=xxx&grant_type=urn:ietf:params:oauth:grant-type:device_code'
```

- 配置类`OAuth2TokenEndpointConfigurer`
- 默认端点 POST `/oauth2/token`
- AuthenticationConverter
  - `OAuth2AuthorizationCodeAuthenticationConverter`
    - FORM参数
      - grant_type (REQUIRED): authorization_code
      - code (REQUIRED)
      - redirect_uri (REQUIRED)
  - `OAuth2RefreshTokenAuthenticationConverter`
    - FORM参数
      - grant_type (REQUIRED): refresh_token
      - refresh_token (REQUIRED)
      - scope (OPTIONAL)
  - `OAuth2ClientCredentialsAuthenticationConverter`
    - FORM参数
      - grant_type (REQUIRED): client_credentials
      - scope (OPTIONAL)
  - `OAuth2DeviceCodeAuthenticationConverter`
    - FORM参数
      - grant_type (REQUIRED): urn:ietf:params:oauth:grant-type:device_code
      - device_code (REQUIRED)
- AuthenticationProvider
  - `OAuth2AuthorizationCodeAuthenticationProvider`
  - `OAuth2RefreshTokenAuthenticationProvider`
  - `OAuth2ClientCredentialsAuthenticationProvider`
  - `OAuth2DeviceCodeAuthenticationProvider`

令牌生成`OAuth2TokenGenerator`
- `JwtGenerator` 负责 self-contained 形式的access_token 或 id_token 生成（需要配置`JWKSource`）
- `OAuth2AccessTokenGenerator` 负责reference形式的access_token生成
- `OAuth2RefreshTokenGenerator` 负责refresh_token生成（默认不会给public client签发refresh_token）

默认令牌配置`TokenSettings`
```java
public static Builder builder() {
    return new Builder().authorizationCodeTimeToLive(Duration.ofMinutes(5)) // 授权码TTL: 5min
        .accessTokenTimeToLive(Duration.ofMinutes(5)) // access token TTL: 5min
        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // access token format: SELF_CONTAINED
        .deviceCodeTimeToLive(Duration.ofMinutes(5)) // 设备码TTL: 5min
        .reuseRefreshTokens(true) // 重用refresh token: true
        .refreshTokenTimeToLive(Duration.ofMinutes(60)) // refresh token TTL: 60min
        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256) // id token签名算法: RS256
        .x509CertificateBoundAccessTokens(false);
}
```

### OAuth2TokenIntrospectionEndpointFilter

> 令牌自省端点（需要客户端认证）

- 配置类`OAuth2TokenIntrospectionEndpointConfigurer`
- 默认端点：POST `/oauth2/introspect`
- AuthenticationConverter
  - `OAuth2TokenIntrospectionAuthenticationConverter`
    - FORM参数
      - token (REQUIRED)
      - token_type_hint (OPTIONAL) 
- AuthenticationProvider
  - `OAuth2TokenIntrospectionAuthenticationProvider`
- AuthenticationSuccessHandler 默认发送令牌自省响应`OAuth2TokenIntrospection`json格式
- AuthenticationFailureHandler 
  - `OAuth2ErrorAuthenticationFailureHandler` json格式响应error信息

### OAuth2TokenRevocationEndpointFilter

> 令牌撤销端点

- 配置类`OAuth2TokenRevocationEndpointConfigurer`
- 默认端点：POST `/oauth2/revoke`
- AuthenticationConverter
  - `OAuth2TokenRevocationAuthenticationConverter`
    - FORM参数
      - token (REQUIRED)
      - token_type_hint (OPTIONAL)
- AuthenticationProvider
  - `OAuth2TokenRevocationAuthenticationProvider` 执行令牌撤销
- AuthenticationSuccessHandler 默认响应200状态码
- AuthenticationFailureHandler
  - `OAuth2ErrorAuthenticationFailureHandler` json格式响应error信息

### OAuth2DeviceAuthorizationEndpointFilter

> 设备码请求授权（设备码客户端为public client需要自定义`DeviceClientAuthenticationProvider`）

请求示例：


- 默认端点：POST `/oauth2/device_authorization`
- AuthenticationConverter
  - `OAuth2DeviceAuthorizationRequestAuthenticationConverter`
    - FORM参数
      - client_id (REQUIRED)
      - scope (OPTIONAL)
- AuthenticationProvider
  - `OAuth2DeviceAuthorizationRequestAuthenticationProvider`
- AuthenticationSuccessHandler 默认以json格式响应`OAuth2DeviceAuthorizationResponse`
- AuthenticationFailureHandler
  - `OAuth2ErrorAuthenticationFailureHandler` json格式响应error信息

设备码授权请求响应示例（之后浏览器访问验证url并输入设备激活码user_code即可）：
```json
{
    "user_code": "PGDH-CWBC",
    "device_code": "7znx4QZb8CfQ8IDsFJFRW_VPlGdNT10Q5gTd6BP7uIUlMODXPBHIkq8cvvEm5L6fePkdNL9vlUnGMP0Trvox410ody0SFSrA6oD9jBWN6Mtt0OKr022OTTX4M_5FQdiE",
    "verification_uri_complete": "http://localhost:9000/activate?user_code=PGDH-CWBC",
    "verification_uri": "http://localhost:9000/activate",
    "expires_in": 300
}
```
用户设备激活后在设备上获取访问令牌
```bash
curl -X POST 'http://127.0.0.1/oauth2/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
-d 'client_id=device-client&device_code=xxx&grant_type=urn:ietf:params:oauth:grant-type:device_code'
```

### OidcUserInfoEndpointFilter

> OIDC用户信息端点

- 配置类 OidcUserInfoEndpointConfigurer
- 默认端点 GET or POST `/userinfo` 注意：授权服务器自身也是一个资源服务器 访问此端点需携带access_token