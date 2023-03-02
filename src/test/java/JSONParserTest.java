import com.google.gson.Gson;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
        @MethodSource("dataProvider")
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

        private static Stream<Arguments> dataProvider() {
            Gson gson = new Gson();
            TestProbe testProbe = new TestProbe();
            TestProbeHelper testProbeHelper = new TestProbeHelper();

            String expected1 = gson.toJson(testProbe);
            String expected2 = gson.toJson(testProbeHelper);
            testProbe.clear();
            String expected3 = gson.toJson(testProbe);

            return Stream.of(
                    Arguments.of(new TestProbe(), expected1),
                    Arguments.of(new TestProbeHelper(), expected2),
                    Arguments.of(testProbe, expected3)
            );
        }
    }

    @Nested
    class fromJSONTest {
        @Test
        void fromJSON() {

        }
    }
}
