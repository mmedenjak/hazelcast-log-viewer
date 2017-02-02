package com.hazelcast;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public abstract class Util {
    public static final int DEFAULT_PAGE_SIZE = 23;

    public static final Pattern CLUSTER_PATTERN =
            Pattern.compile("([A-Z]+)\\s+" // INFO
                    + "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})\\s+" // 2017-01-13 16:07:52,455
                    + "\\[(\\S+)\\]\\s+" // [main]
                    + "(\\S+):\\s+" // com.hazelcast.simulator.worker.MemberWorker:
                    + "(?:(\\S+)\\s+)?" // [10.0.0.101]:5701
                    + "(?:\\[(\\S+)\\]\\s+)?" // [workers]
                    + "(?:\\[(\\S+)\\]\\s+)?" // [3.7.5]
                    + "(.*)", Pattern.DOTALL); // Established socket connection between /10.0.0.101:46399 and /10.0.0.40:5701
    public static final int CLUSTER_TIME_TOKEN = 1;
    public static final SimpleDateFormat CLUSTER_DATE_FORMAT = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss,SSS");

    public static final Pattern TEST_PATTERN =
            Pattern.compile("(\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+" // 23:52:51,131
                    + "([A-Z]+)\\s+" // INFO
                    + "\\|(\\S+)\\|\\s+-\\s+" // |testAtomicLongFailure| -
                    + "\\[(\\S+)\\]\\s+" // [XmlConfigLocator]
                    + "(\\S+)\\s+-\\s++" // testAtomicLongFailure -
                    + "(?:(\\[\\S+)\\s+)?" // [127.0.0.1]:17197
                    + "(?:\\[(\\S+)\\]\\s+)?" //  [dev]
                    + "(?:\\[(\\S+)\\]\\s+)?"  // [3.8-SNAPSHOT]
                    + "(.*)", Pattern.DOTALL); // Loading 'hazelcast-default.xml' from classpath.

    public static final int TEST_TIME_TOKEN = 0;
    public static final SimpleDateFormat TEST_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss,SSS");

}
