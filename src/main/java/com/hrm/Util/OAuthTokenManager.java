package com.hrm.Util;

import static io.restassured.RestAssured.given;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.restassured.response.Response;

public class OAuthTokenManager {

    private static final String BASE_URL =
            "http://localhost:8080";

    private static final String CLIENT_ID =
            "29ebbba52622c51bf96aef7188077027";

    private static final String REDIRECT_URI =
            "http://localhost:8080/oauth-callback";

    private static final String AUTHORIZE_ENDPOINT =
            BASE_URL + "/web/index.php/oauth2/authorize";

    private static final String TOKEN_ENDPOINT =
            BASE_URL + "/web/index.php/oauth2/token";


    private static String accessToken;
    private static String refreshToken;

    private static Instant expiresAt;


    // ==========================================
    // METHOD CHÍNH TEST SẼ GỌI
    // ==========================================
    public static synchronized String getAccessToken(
            WebDriver driver,
            String username,
            String password) {

        // Token hiện tại vẫn còn hạn
        if (accessToken != null && !isExpired()) {
            return accessToken;
        }

        // Access token hết hạn nhưng đã có refresh token
        if (refreshToken != null) {

            refreshAccessToken();

            return accessToken;
        }

        // Chưa có gì -> chạy OAuth từ đầu
        authorizeAndGetToken(
                driver,
                username,
                password
        );

        return accessToken;
    }


    // ==========================================
    // OAuth Authorization Code + PKCE
    // ==========================================
    private static void authorizeAndGetToken(
        WebDriver driver,
        String username,
        String password) {

        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        String authorizationUrl =
                buildAuthorizationUrl(codeChallenge);

        System.out.println("OAuth authorize URL: " + authorizationUrl);

        driver.get(authorizationUrl);

        WebDriverWait wait =
                new WebDriverWait(
                        driver,
                        Duration.ofSeconds(30)
                );

       Log.info("Đang chờ username...");

        WebElement usernameInput =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("input[name='username']")
                        )
                );

        WebElement passwordInput =
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(
                                By.cssSelector("input[name='password']")
                        )
                );

        usernameInput.sendKeys(username);

        Log.info(
                "Username value: "
                + usernameInput.getAttribute("value")
        );

        passwordInput.sendKeys(password);

        Log.info(
                "Password đã nhập, length = "
                + passwordInput.getAttribute("value").length()
        );

        WebElement loginButton =
                wait.until(
                        ExpectedConditions.elementToBeClickable(
                                By.cssSelector("button[type='submit']")
                        )
                );
                Log.info("Nhập Login");
        loginButton.click();

         approveOAuthClientIfNeeded(driver);


        // ======================================
        // CHỜ REDIRECT URI CHỨA CODE
        // ======================================

        String authorizationCode =
                waitForAuthorizationCode(driver);

        System.out.println(
                "Authorization code received"
        );


        // ======================================
        // ĐỔI CODE -> ACCESS TOKEN
        // ======================================

        exchangeCodeForToken(
                authorizationCode,
                codeVerifier
        );
}


    // ==========================================
    // Exchange authorization code
    // ==========================================
    private static void exchangeCodeForToken(
            String authorizationCode,
            String codeVerifier) {

        Response response =
                given()
                    .contentType(
                            "application/x-www-form-urlencoded"
                    )
                    .formParam(
                            "grant_type",
                            "authorization_code"
                    )
                    .formParam(
                            "client_id",
                            CLIENT_ID
                    )
                    .formParam(
                            "redirect_uri",
                            REDIRECT_URI
                    )
                    .formParam(
                            "code",
                            authorizationCode
                    )
                    .formParam(
                            "code_verifier",
                            codeVerifier
                    )
                .when()
                    .post(TOKEN_ENDPOINT);


        System.out.println(
                "Token response status: "
                + response.statusCode()
        );

        if (response.statusCode() != 200) {

            System.out.println(
                    response.asPrettyString()
            );

            throw new RuntimeException(
                    "Không lấy được OAuth token"
            );
        }


        saveToken(response);
    }


    // ==========================================
    // Refresh Token
    // ==========================================
    private static void refreshAccessToken() {

        System.out.println(
                "Access token expired -> refreshing..."
        );

        Response response =
                given()
                    .contentType(
                            "application/x-www-form-urlencoded"
                    )
                    .formParam(
                            "grant_type",
                            "refresh_token"
                    )
                    .formParam(
                            "client_id",
                            CLIENT_ID
                    )
                    .formParam(
                            "refresh_token",
                            refreshToken
                    )
                .when()
                    .post(TOKEN_ENDPOINT);


        if (response.statusCode() != 200) {

            System.out.println(
                    response.asPrettyString()
            );

            // Refresh token cũng không dùng được nữa
            accessToken = null;
            refreshToken = null;
            expiresAt = null;

            throw new RuntimeException(
                    "Không refresh được OAuth token"
            );
        }


        saveToken(response);

        System.out.println(
                "Access token refreshed successfully"
        );
    }


    // ==========================================
    // Save token information
    // ==========================================
    private static void saveToken(
            Response response) {

        accessToken =
                response.jsonPath()
                        .getString("access_token");

        refreshToken =
                response.jsonPath()
                        .getString("refresh_token");

        Integer expiresIn =
                response.jsonPath()
                        .getInt("expires_in");

        // Trừ 30 giây để tránh token hết hạn
        // đúng lúc request đang chạy
        expiresAt =
                Instant.now()
                       .plusSeconds(expiresIn - 30);

        System.out.println(
                "OAuth token created successfully"
        );

        System.out.println(
                "Expires in: "
                + expiresIn
                + " seconds"
        );
    }


    // ==========================================
    // Check token expiration
    // ==========================================
    private static boolean isExpired() {

        if (expiresAt == null) {
            return true;
        }

        return Instant.now()
                .isAfter(expiresAt);
    }


    // ==========================================
    // PKCE verifier
    // ==========================================
    private static String generateCodeVerifier() {

        byte[] randomBytes =
                new byte[64];

        new SecureRandom()
                .nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }


    // ==========================================
    // PKCE S256 challenge
    // ==========================================
    private static String generateCodeChallenge(
            String verifier) {

        try {

            MessageDigest digest =
                    MessageDigest
                            .getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            verifier.getBytes(
                                    StandardCharsets.US_ASCII
                            )
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không tạo được PKCE challenge",
                    e
            );
        }
    }


    // ==========================================
    // Authorization URL
    // ==========================================
    private static String buildAuthorizationUrl(
            String challenge) {

        return AUTHORIZE_ENDPOINT
                + "?response_type=code"
                + "&client_id="
                + encode(CLIENT_ID)
                + "&redirect_uri="
                + encode(REDIRECT_URI)
                + "&code_challenge="
                + encode(challenge)
                + "&code_challenge_method=S256";
    }


    private static String encode(
            String value) {

        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }


    // ==========================================
    // Login page check
    // ==========================================
    private static boolean isLoginPage(
            WebDriver driver) {

        return !driver.findElements(
                By.name("username")
        ).isEmpty();
    }


    // ==========================================
    // Click Allow / Authorize
    // ==========================================
    private static void approveOAuthClientIfNeeded(
            WebDriver driver) {

        try {

         By buttonAllow = By.xpath("//div[@class='orangehrm-oauth-button-container']/child::button[@type='submit']");
          new WebDriverWait(driver, Duration.ofSeconds(10)).until(ExpectedConditions.presenceOfAllElementsLocatedBy(buttonAllow));

          waitUtils.waitElementClick(driver, driver.findElement(buttonAllow));

        } catch (Exception ignored) {
        }
    }


    // ==========================================
    // Wait redirect and extract ?code=
    // ==========================================
    private static String waitForAuthorizationCode(
            WebDriver driver) {

        long timeout =
                System.currentTimeMillis()
                + 15_000;

        while (
                System.currentTimeMillis()
                < timeout
        ) {

            String currentUrl =
                    driver.getCurrentUrl();

            if (currentUrl.contains(
                    REDIRECT_URI
            )
                    && currentUrl.contains(
                            "code="
                    )) {

                return extractCode(
                        currentUrl
                );
            }

            try {
                Thread.sleep(300);
            } catch (
                    InterruptedException e
            ) {

                Thread.currentThread()
                        .interrupt();
            }
        }

        throw new RuntimeException(
                "Không lấy được authorization code"
        );
    }


    // ==========================================
    // Parse code query parameter
    // ==========================================
    private static String extractCode(
            String url) {

        try {

            URI uri =
                    URI.create(url);

            String query =
                    uri.getQuery();

            for (String param :
                    query.split("&")) {

                String[] pair =
                        param.split(
                                "=",
                                2
                        );

                if (pair.length == 2
                        && pair[0]
                        .equals("code")) {

                    return java.net.URLDecoder
                            .decode(
                                    pair[1],
                                    StandardCharsets.UTF_8
                            );
                }
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Không parse được authorization code",
                    e
            );
        }

        throw new RuntimeException(
                "URL không có authorization code"
        );
    }
}