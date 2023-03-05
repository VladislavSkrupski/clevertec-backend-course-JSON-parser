import com.google.gson.Gson;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.JSONParser;
import ru.clevertec.TestProbe;
import ru.clevertec.TestProbeHelper;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JSONParserTest {
    private Gson gson = new Gson();

    @Mock
    private TestProbeHelper testProbeHelperMock;

    @Mock
    private TestProbe testProbeMock;

    @Nested
    class toJSONTest {
        @ParameterizedTest
        @MethodSource("JSONParserTest#dataProvider")
        void toJSONShouldReturnExpectedString(Object object, String expected) {
            String actual = JSONParser.toJSON(object);

            assertThat(actual).isEqualTo(expected);
        }

        @ParameterizedTest
        @NullSource
        void toJSONShouldReturnStringWithNullWord(Object object) {
            String expected = gson.toJson(object);
            String actual = JSONParser.toJSON(object);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void toJSONShouldReturnExpectedStringForTestProbeMock() {
            String expected = gson.toJson(testProbeMock);
            String actual = JSONParser.toJSON(testProbeMock);

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void toJSONShouldReturnExpectedStringForTestProbeHelperMock() {
            String expected = gson.toJson(testProbeHelperMock);
            String actual = JSONParser.toJSON(testProbeHelperMock);

            assertThat(actual).isEqualTo(expected);
        }
    }

    @Nested
    class fromJSONTest {
        @ParameterizedTest
        @MethodSource("JSONParserTest#dataProvider")
        void fromJSONShouldReturnExpectedObject(Object object, String json) {
            Object actual = JSONParser.fromJSON(json, object);
            Object expected = gson.fromJson(json, object.getClass());

            assertThat(gson.toJson(actual)).isEqualTo(gson.toJson(expected));
        }

        @ParameterizedTest
        @NullSource()
        void fromJSONShouldReturnNull(Object object) {
            Object actual = JSONParser.fromJSON("", object);

            assertThat(actual).isNull();
        }

        @Test
        void fromJSONShouldReturnExpectedStringForTestProbeMock() {
            String jsonA = JSONParser.toJSON(testProbeMock);
            String jsonE = gson.toJson(testProbeMock);

            Object actual = JSONParser.fromJSON(jsonA, testProbeMock);
            Mockito.reset(testProbeMock);
            Object expected = gson.fromJson(jsonE, testProbeMock.getClass());

            assertThat(gson.toJson(actual)).isEqualTo(gson.toJson(expected));
        }

        @Test
        void fromJSONShouldReturnExpectedStringForTestProbeHelperMock() {
            String jsonA = JSONParser.toJSON(testProbeHelperMock);
            String jsonE = gson.toJson(testProbeHelperMock);

            Object actual = JSONParser.fromJSON(jsonA, testProbeHelperMock);
            Mockito.reset(testProbeHelperMock);
            Object expected = gson.fromJson(jsonE, testProbeHelperMock.getClass());

            assertThat(gson.toJson(actual)).isEqualTo(gson.toJson(expected));
        }
    }

    private static Stream<Arguments> dataProvider() {
        Gson gson = new Gson();
        TestProbe testProbe = new TestProbe();
        TestProbeHelper testProbeHelper = new TestProbeHelper();

        String json1 = gson.toJson(testProbe);
        String json2 = gson.toJson(testProbeHelper);
        testProbe.clear();
        String json3 = gson.toJson(testProbe);

        return Stream.of(
                Arguments.of(new TestProbe(), json1),
                Arguments.of(new TestProbeHelper(), json2),
                Arguments.of(testProbe, json3)
        );
    }
}
