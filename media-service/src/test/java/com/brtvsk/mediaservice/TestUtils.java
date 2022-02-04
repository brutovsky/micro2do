package com.brtvsk.mediaservice;

import com.brtvsk.mediaservice.security.model.AuthUser;
import com.brtvsk.mediaservice.security.model.AuthUserImpl;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class TestUtils {
    public static final UUID USER_ID = UUID.randomUUID();
    public static final AuthUser USER = new AuthUserImpl(USER_ID);

    public static final String TEST_FILENAME = "filename.txt";
    public static final UUID TEST_OBJECT_ID = UUID.randomUUID();

    public static final Duration TEST_DURATION = Duration.of(15, ChronoUnit.MINUTES);
    public static final String TEST_BUCKET_NAME = "bucketName";

    public static final String TEST_DESCRIPTION = "description";
    public static final long TEST_SIZE_IN_KB = 1024L;
    public static final String TEST_FILE_CONTENT = "content";
    public static final String TEST_URL = "http://localhost/test";
    public static final long TEST_MAX_SIZE = 100000L;
    public static final long FAIL_TEST_MAX_SIZE = 1L;
    public static final long TEST_USER_SIZE_IN_KB = TEST_MAX_SIZE + TEST_SIZE_IN_KB;

    private TestUtils() {
    }
}
