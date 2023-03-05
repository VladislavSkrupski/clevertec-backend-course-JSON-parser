package fromJSON;

public class JSONStringParser {
    private static final char OBJECT_START_CHAR = '{';
    private static final char OBJECT_END_CHAR = '}';
    private static final char ARRAY_START_CHAR = '[';
    private static final char ARRAY_END_CHAR = ']';
    private static final char COLON_CHAR = ':';
    private static final char COMMA_CHAR = ',';
    private static final char QUOTE_CHAR = '\"';


    public enum State {
        OBJECT_START,
        OBJECT_END,
        ARRAY_START,
        ARRAY_END,
        NAME_START,
        NAME_END,
        VALUE_START,
        VALUE_END,
        STRING_START,
        STRING_END,
        NUMBER_START,
        NUMBER_END,
        BOOLEAN_START,
        NULL_START,
        STR_ESC,
        T,
        TR,
        TRU,
        F,
        FA,
        FAL,
        FALS,
        N,
        NU,
        NUL,
        BOOLEAN_AND_NULL_END,
        HEX1,
        HEX2,
        HEX3,
        HEX4
    }


    private String json = "{\"a\":[1]}";


    public ObjectFromJSONAdapter parseJSON(String json) {
        char[] chars = json.toCharArray();
        int i = 0;
        ObjectFromJSONAdapter objectFromJSONAdapter = new ObjectFromJSONAdapter(i, null);
        for (; i < chars.length; i++) {
            if (Character.isWhitespace(chars[i])) continue;
            if (chars[i] == OBJECT_START_CHAR) {
                objectFromJSONAdapter.setState(State.OBJECT_START);
                i++;
                break;
            } else {
                throwException();
            }
        }
        return parseJSONChars(i, chars, objectFromJSONAdapter);
    }

    private ObjectFromJSONAdapter parseJSONChars(int i, char[] chars, ObjectFromJSONAdapter objectFromJSONAdapter) {
        for (; i < chars.length; i++) {
            switch (objectFromJSONAdapter.getState()) {
                case OBJECT_START: {
                    if (Character.isWhitespace(chars[i])) continue;
                    switch (chars[i]) {
                        case QUOTE_CHAR -> {
                            objectFromJSONAdapter.setState(State.NAME_START);
                            continue;
                        }
                        case OBJECT_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.OBJECT_END);
                            continue;
                        }
                        default -> throwException();
                    }
                }
                case ARRAY_START: {
                    if (Character.isWhitespace(chars[i])) continue;
                    switch (chars[i]) {
                        case ARRAY_START_CHAR -> {
                            objectFromJSONAdapter.setState(State.VALUE_START);
                            objectFromJSONAdapter.incrementArrayDepth();
                            continue;
                        }
                        case ARRAY_END_CHAR -> {
                            objectFromJSONAdapter.decrementArrayDepth();
                            objectFromJSONAdapter.setState(State.ARRAY_END);
                            continue;
                        }
                        default -> {
                            objectFromJSONAdapter.setState(State.VALUE_START);
                            i--;
                            continue;
                        }
                    }
                }
                case ARRAY_END: {
                    if (Character.isWhitespace(chars[i])) continue;
                    switch (chars[i]) {
                        case COMMA_CHAR -> {
                            objectFromJSONAdapter.setState(State.VALUE_START);
                            continue;
                        }
                        case OBJECT_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.OBJECT_END);
                            continue;
                        }
                        case ARRAY_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.ARRAY_END);
                            objectFromJSONAdapter.decrementArrayDepth();
                            continue;
                        }
                        default -> throwException();
                    }
                }
                case NAME_START: {
                    switch (chars[i]) {
                        case QUOTE_CHAR -> {
                            objectFromJSONAdapter.setState(State.NAME_END);
                            continue;
                        }
                        case '\\' -> {
                            objectFromJSONAdapter.setState(State.STR_ESC);
                            continue;
                        }
                        default -> {
                            if (Character.isISOControl(chars[i])) {
                                throwException();
                            }
                            objectFromJSONAdapter.appendFieldName(chars[i]);
                            continue;
                        }
                    }
                }
                case NAME_END: {
                    if (Character.isWhitespace(chars[i])) continue;
                    if (chars[i] == COLON_CHAR) {
                        objectFromJSONAdapter.setState(State.VALUE_START);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case VALUE_START: {
                    if (Character.isWhitespace(chars[i])) continue;
                    switch (chars[i]) {
                        case QUOTE_CHAR -> {
                            objectFromJSONAdapter.setState(State.STRING_START);
                            continue;
                        }
                        case '-', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' -> {
                            objectFromJSONAdapter.setState(State.NUMBER_START);
                            objectFromJSONAdapter.appendFieldValue(chars[i]);
                            continue;
                        }
                        case OBJECT_START_CHAR -> {
                            i++;
                            ObjectFromJSONAdapter temp = parseJSONChars(i, chars, new ObjectFromJSONAdapter(i, State.OBJECT_START));
                            objectFromJSONAdapter.setFieldObjectValue(temp);
                            i = temp.getPosition();
                            continue;
                        }
                        case OBJECT_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.OBJECT_END);
                            continue;
                        }
                        case ARRAY_START_CHAR -> {
                            objectFromJSONAdapter.setState(State.ARRAY_START);
                            objectFromJSONAdapter.incrementArrayDepth();
                            continue;
                        }
                        case ARRAY_END_CHAR -> {
                            objectFromJSONAdapter.decrementArrayDepth();
                            objectFromJSONAdapter.setState(State.ARRAY_END);
                            continue;
                        }
                        case 't' -> {
                            objectFromJSONAdapter.setState(State.T);
                            objectFromJSONAdapter.appendFieldValue(chars[i]);
                            continue;
                        }
                        case 'f' -> {
                            objectFromJSONAdapter.setState(State.F);
                            objectFromJSONAdapter.appendFieldValue(chars[i]);
                            continue;
                        }
                        case 'n' -> {
                            objectFromJSONAdapter.setState(State.N);
                            objectFromJSONAdapter.appendFieldValue(chars[i]);
                            continue;
                        }
                        case COMMA_CHAR -> {
                            if (objectFromJSONAdapter.getArrayDepth() != 0) {
                                objectFromJSONAdapter.setState(State.VALUE_END);
                            } else {
                                objectFromJSONAdapter.setState(State.NAME_START);
                            }
                            continue;
                        }
                        default -> throwException();
                    }
                }
                case VALUE_END: {

                }
                case OBJECT_END: {
                    break;
                }
                case T: {
                    if ('r' == chars[i]) {
                        objectFromJSONAdapter.setState(State.TR);
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case TR: {
                    if ('u' == chars[i]) {
                        objectFromJSONAdapter.setState(State.TRU);
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case TRU: {
                    if (chars[i] == 'e') {
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        objectFromJSONAdapter.setState(State.BOOLEAN_AND_NULL_END);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case F: {
                    if ('a' == chars[i]) {
                        objectFromJSONAdapter.setState(State.FA);
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case FA: {
                    if ('l' == chars[i]) {
                        objectFromJSONAdapter.setState(State.FAL);
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case FAL: {
                    if ('s' == chars[i]) {
                        objectFromJSONAdapter.setState(State.FALS);
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case FALS: {
                    if (chars[i] == 'e') {
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        objectFromJSONAdapter.setState(State.BOOLEAN_AND_NULL_END);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case BOOLEAN_AND_NULL_END: {
                    switch (chars[i]) {
                        case COMMA_CHAR -> {
                            objectFromJSONAdapter.setState(State.VALUE_END);
                            if (objectFromJSONAdapter.getArrayDepth() != 0)
                                objectFromJSONAdapter.setState(State.OBJECT_START);
                            continue;
                        }
                        case ARRAY_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.ARRAY_END);
                            continue;
                        }
                        case OBJECT_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.OBJECT_END);
                            continue;
                        }
                        default -> {
                            throwException();
                        }
                    }
                }
                case N: {
                    if ('u' == chars[i]) {
                        objectFromJSONAdapter.setState(State.NU);
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case NU: {
                    if ('l' == chars[i]) {
                        objectFromJSONAdapter.setState(State.NUL);
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case NUL: {
                    if (chars[i] == 'l') {
                        objectFromJSONAdapter.appendFieldValue(chars[i]);
                        objectFromJSONAdapter.setState(State.BOOLEAN_AND_NULL_END);
                        continue;
                    } else {
                        throwException();
                    }
                }
                case NUMBER_START: {
                    switch (chars[i]) {
                        case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'e', 'E', '+', '-', '.' -> {
                            objectFromJSONAdapter.appendFieldValue(chars[i]);
                            continue;
                        }
                        case COMMA_CHAR -> {
                            objectFromJSONAdapter.setState(State.VALUE_END);
                            if (objectFromJSONAdapter.getArrayDepth() != 0)
                                objectFromJSONAdapter.setState(State.OBJECT_START);
                            continue;
                        }
                        case ARRAY_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.ARRAY_END);
                            continue;
                        }
                        case OBJECT_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.OBJECT_END);
                            continue;
                        }
                        default -> {
                            throwException();
                        }
                    }
                }
                case STRING_START: {
                    switch (chars[i]) {
                        case '"' -> {
                            objectFromJSONAdapter.setState(State.STRING_END);
                            continue;
                        }
                        case '\\' -> {
                            objectFromJSONAdapter.setState(State.STR_ESC);
                            continue;
                        }
                        default -> {
                            if (Character.isISOControl(chars[i])) throwException();
                            objectFromJSONAdapter.appendFieldValue(chars[i]);
                            continue;
                        }
                    }
                }
                case STRING_END: {
                    switch (chars[i]) {
                        case COMMA_CHAR -> {
                            objectFromJSONAdapter.setState(State.VALUE_END);
                            if (objectFromJSONAdapter.getArrayDepth() != 0) {
                                objectFromJSONAdapter.setState(State.VALUE_START);
                            } else {
                                objectFromJSONAdapter.setState(State.OBJECT_START);
                            }
                            continue;
                        }
                        case ARRAY_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.ARRAY_END);
                            continue;
                        }
                        case OBJECT_END_CHAR -> {
                            objectFromJSONAdapter.setState(State.OBJECT_END);
                            continue;
                        }
                        default -> {
                            throwException();
                        }
                    }
                }
                case STR_ESC: {
                    switch (chars[i]) {
                        case '"', '/', '\\' -> objectFromJSONAdapter.appendFieldValue(chars[i]);
                        case 'b' -> objectFromJSONAdapter.appendFieldValue('\b');
                        case 'f' -> objectFromJSONAdapter.appendFieldValue('\f');
                        case 'n' -> objectFromJSONAdapter.appendFieldValue('\n');
                        case 'r' -> objectFromJSONAdapter.appendFieldValue('\r');
                        case 't' -> objectFromJSONAdapter.appendFieldValue('\t');
                        case 'u' -> {
                            objectFromJSONAdapter.setState(State.HEX1);
                            continue;
                        }
                        default -> throwException();
                    }
                    objectFromJSONAdapter.setState(State.STRING_START);
                    continue;
                }
                case HEX1: {
                    if (!isHex(chars[i])) throwException();
                    objectFromJSONAdapter.appendHexValue(chars[i]);
                    objectFromJSONAdapter.setState(State.HEX2);
                    continue;
                }
                case HEX2: {
                    if (!isHex(chars[i])) throwException();
                    objectFromJSONAdapter.appendHexValue(chars[i]);
                    objectFromJSONAdapter.setState(State.HEX3);
                    continue;
                }
                case HEX3: {
                    if (!isHex(chars[i])) throwException();
                    objectFromJSONAdapter.appendHexValue(chars[i]);
                    objectFromJSONAdapter.setState(State.HEX4);
                    continue;
                }
                case HEX4: {
                    if (!isHex(chars[i])) throwException();
                    objectFromJSONAdapter.appendHexValue(chars[i]);
                    char unicodeChar = toChar(objectFromJSONAdapter.getHexValue());
                    objectFromJSONAdapter.appendFieldValue(unicodeChar);
                    objectFromJSONAdapter.clearHexValue();
                    objectFromJSONAdapter.setState(State.STRING_START);
                    continue;
                }
                default: {
                    throwException();
                }
            }
            objectFromJSONAdapter.setPosition(i);
            if (objectFromJSONAdapter.getState() == State.OBJECT_END) break;
        }
        return objectFromJSONAdapter;
    }

    void throwException() {
        throw new RuntimeException("invalid JSON");
    }

    char toChar(CharSequence buf) {
        assert buf.length() == 4;
        return (char) Integer.parseInt(buf.toString(), 16);
    }

    boolean isHex(char ch) {
        return String.valueOf(ch).matches("[\\da-fA-F]");
    }
}

