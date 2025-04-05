package com.projectX.backend.Configuration;

public class AppConstants {

    public static final String P_N = "0";
    public static final String P_S = "2";
    public static final String S_BY_CID = "categoryId";
    public static final String S_BY_PID = "productId";
    public static final String S_BY_UID = "userId";
    public static final String S_BY_TA = "totalAmount";
    public static final Long ADMIN_ID = 101L;
    public static final Long USER_ID = 102L;
    public static final long JWT_VALIDITY = 5 * 60 * 60;
    public static final String[] PUBLIC_URLS = { "/v3/api-docs/**", "/swagger-ui/**", "/api/register/**", "/api/login" };
    public static final String[] USER_URLS = { "/api/public/**" };
    public static final String[] ADMIN_URLS = { "/api/admin/**" };
}
