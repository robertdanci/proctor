package com.indeed.proctor.common.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestTestBucket {
    @Test
    public void testSerialize() throws JsonProcessingException {
        final String expected = "{"
                + "\"name\":\"grp1\","
                + "\"value\":1,"
                + "\"description\":\"group 1\""
                + "}";

        final String actual = serialize(
                TestBucket.builder()
                        .name("grp1")
                        .value(1)
                        .description("group 1")
                        .build()
        );
        assertEquals(expected, actual);
    }

    @Test
    public void testDeserialize() throws IOException {
        final TestBucket expected = TestBucket.builder()
                .name("grp1")
                .value(1)
                .description("group 1")
                .build();

        final TestBucket actual = deserialize("{"
                + "\"name\":\"grp1\""
                + ",\"value\":1"
                + ",\"description\":\"group 1\""
                + "}"
        );

        assertThat(expected.fullEquals(actual))
                .as("%s should be fully equal to %s", expected, actual)
                .isTrue();
    }

    @Test
    public void testSerializeAndDeserialize() throws IOException {
        final Payload payload = new Payload();
        payload.setMap(ImmutableMap.of(
                "k", 10,
                "v", 100.0,
                "s", ImmutableList.of(10, 20, 30)
        ));

        final TestBucket bucket = TestBucket.builder()
                .name("grp1")
                .value(1)
                .description("group 1")
                .payload(payload)
                .build();

        final TestBucket converted = deserialize(
                serialize(bucket)
        );

        assertThat(bucket.fullEquals(converted))
                .as("%s should be fully equal to %s", bucket, converted)
                .isTrue();
    }

    @Test
    public void testBuilder() {
        final Payload payload = new Payload();
        payload.setLongValue(10L);

        final TestBucket expected =
                new TestBucket(
                        "name",
                        10,
                        "desc",
                        payload
                );

        final TestBucket actual =
                TestBucket.builder()
                        .name("name")
                        .value(10)
                        .description("desc")
                        .payload(payload)
                        .build();

        assertThat(expected.fullEquals(actual))
                .as("%s should be fully equal to %s", expected, actual)
                .isTrue();
    }

    @Test
    public void testEquals() {
        assertFalse(new TestBucket().equals(null));
        assertFalse(new TestBucket().equals("hello"));
        assertEquals(new TestBucket(), new TestBucket());

        // not sure why equals compares only test name, could be a bug in the code
        assertEquals(new TestBucket("foo", 1, "d1"), new TestBucket("foo", 1, "d1"));
        assertEquals(new TestBucket("foo", 1, "d1"), new TestBucket("foo", 2, "d1"));
        assertEquals(new TestBucket("foo", 1, "d1"), new TestBucket("foo", 1, "d2"));
        final Payload p1 = new Payload();
        p1.setStringValue("p1String");
        final Payload p2 = new Payload();
        p2.setDoubleValue(0.4);
        assertEquals(new TestBucket("foo", 1, "d1", p1), new TestBucket("foo", 1, "d1", p2));
    }

    @Test
    public void testFullEquals() {
        assertFalse(new TestBucket().fullEquals(null));
        assertFalse(new TestBucket().fullEquals("hello"));
        assertTrue(new TestBucket().fullEquals(new TestBucket()));
        assertTrue(new TestBucket("foo", 1, "d1").fullEquals(new TestBucket("foo", 1, "d1")));
        assertFalse(new TestBucket("foo", 1, "d1").fullEquals(new TestBucket("foo", 2, "d1")));
        assertFalse(new TestBucket("foo", 1, "d1").fullEquals(new TestBucket("foo", 1, "d2")));
        final Payload p1 = new Payload();
        p1.setStringValue("p1String");
        final Payload p1b = new Payload();
        p1b.setStringValue("p1String");
        final Payload p2 = new Payload();
        p2.setDoubleValue(0.4);
        assertTrue(new TestBucket("foo", 1, "d1", p1).fullEquals(new TestBucket("foo", 1, "d1", p1b)));
        assertFalse(new TestBucket("foo", 1, "d1", p1).fullEquals(new TestBucket("foo", 1, "d1", p2)));
    }

    private static String serialize(final TestBucket testBucket) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(testBucket);
    }

    private static TestBucket deserialize(final String json) throws IOException {
        return new ObjectMapper().readValue(
                json,
                TestBucket.class
        );
    }

}
