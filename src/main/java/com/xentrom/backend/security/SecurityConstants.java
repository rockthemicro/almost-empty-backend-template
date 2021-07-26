package com.xentrom.backend.security;

public class SecurityConstants {
    public static final String SECRET = "XentromSecretKeyToGenJWTs";
    public static final long EXPIRATION_TIME = 86_400_000; // 1 day
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String API_USER = "/api/user/**";
    public static final String API_USER_MANAGE = "/api/user/manage/**";
    public static final String ENDPOINT_USER_SELF_UPDATE = "/api/user/selfUpdate";
}
