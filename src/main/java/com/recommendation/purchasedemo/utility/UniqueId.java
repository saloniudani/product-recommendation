package com.recommendation.purchasedemo.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Clock;
import java.util.Random;

/**
 * Entity ID generator:
 * <p>
 * <ol>
 * <li>should be monotonically increasing across reboots for same machine</li>
 * <li>should be different from 2 jvms on same machine</li>
 * <li>should be UNSIGNED</li>
 * <li>should support ~1 billion unique numbers with worst case ( highest possible numbers for other part of pk) starting number.</li>
 * </ol>
 * =======================================================
 * 63 bit sequential entity ID generator.
 * <p>
 * ID is composed of:
 * </p>
 * <ul>
 * <li>time - 13 digits (millisecond precision gives us time till 11th April 2262)</li>
 * <li>configured process id - 3 digits - gives us up to 999</li>
 * <li>machines sequence number - 3 digits - gives us up to 999 (with protection to avoid rollover in the same ms)</li>
 * </ul>
 * Partially based on <a href="https://github.com/Predictor/javasnowflake/blob/master/IdGenerator/src/org/predictor/idgenerator/BasicEntityIdGenerator.java">Twitter Snowflake ID Generator</a>
 */
//Not a Spring @Service due to allow using on POJOs and attributes directly
public enum UniqueId {
    GENERATOR;
    private static final Logger LOGGER = LoggerFactory.getLogger(UniqueId.class);

    // id format => timestamp | process | sequence
    private static final int SEQUENCE_ID_RANGE = 1000;
    private static final int PROCESS_ID_RANGE = 1000;
    private static final int TIMESTAMP_SHIFT = 1000000;
    private static final Random random = new Random();
    private static final byte[] loopback = {0x7f, 0x00, 0x00, 0x01};
    private volatile long lastTimestamp = -1L;
    protected static final long PROCESS_ID_SHIFTED = getProcessId() * 1000;
    private volatile int sequence = 0;

    //Not made final just for testability purpose
    protected volatile Clock clock = Clock.systemUTC();

    public static final long MIN = 1500000000000000000L;

    private static int getProcessId() {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int pid = 0;
        // try to read PID
        if (jvmName != null) {
            int idx = jvmName.indexOf('@');
            if (idx > 0) {
                String pidStr = jvmName.substring(0, idx);
                try {
                    pid = Integer.parseInt(pidStr);
                } catch (NumberFormatException ignored) {
                    LOGGER.debug("Operating system provided PID does not look like a number, can't use it '{}", pid);
                }
            }
        }
        if (pid == 0) {
            pid = random.nextInt(65536);
            LOGGER.info("Cannot find out PID, using random {}", pid);
        }
        // XOR pid with IP
        InetAddress localhost = null;
        int result = 33 ^ pid;
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            byte[] ip = new byte[4];
            random.nextBytes(ip);
            try {
                localhost = InetAddress.getByAddress(ip);
            } catch (UnknownHostException e1) {
                // cannot occur
            }
            LOGGER.info("Cannot find out IP, using random {}", localhost);
        }
        byte[] hostAddress = localhost != null ? localhost.getAddress() : loopback;
        for (byte b : hostAddress) {
            result ^= b;
        }
        result = Math.abs(result) % PROCESS_ID_RANGE; // apply modulo
        LOGGER.trace("Process ID {} generated from PID {} and IP {}", result, pid, localhost);
        return result;
    }

    private long waitTillNextMillisecond() {
        long timestamp = clock.millis();
        while (timestamp <= lastTimestamp) {
            timestamp = clock.millis();
        }
        return timestamp;
    }

    public synchronized long generate() {
        long timestamp = clock.millis();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds." +
                "timestamp: " + timestamp + " lastTimestamp:" + lastTimestamp);
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) % SEQUENCE_ID_RANGE;
            if (sequence == 0) {
                timestamp = waitTillNextMillisecond();
            }
        } else {
            sequence = 0;
            lastTimestamp = timestamp;
        }
        return timestamp * TIMESTAMP_SHIFT + PROCESS_ID_SHIFTED + sequence;
    }
}
