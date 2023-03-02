import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class JSONParser {

    private static final String WHITESPACE = " ";
    private static final String COLON = ":";
    private static final String COMMA = ",";
    private static final String QUOTE = "\"";
    private static final String LEFT_BRACE = "{";
    private static final String LEFT_SQUARE = "[";
    private static final String RIGHT_BRACE = "}";
    private static final String RIGHT_SQUARE = "]";

    public static String toJSON(Object object) throws RuntimeException {
        return Objects.isNull(object) ? "null" : String.valueOf(getObjectData(object));
    }

    // Так как срок по заданию продлили, то метод fromJSON перепишу без использования GSON
    public static Object fromJSON(String json, Object object) {
        if (Objects.isNull(json)||Objects.isNull(object)) return null;
        return new Gson().fromJson(json, object.getClass());
    }

    private static StringBuilder getObjectData(Object object) {
        StringBuilder builder = new StringBuilder();
        if (Objects.isNull(object)) return builder;
        builder.append(LEFT_BRACE);
        if (object.getClass().getDeclaredFields().length == 0) {
            return builder.append(RIGHT_BRACE);
        }
        List<Field> fields = getJSONableFields(object.getClass().getDeclaredFields());
        return builder.append(
                        fields.stream()
                                .map(field -> {
                                    StringBuilder pair = getPair(field, object);
                                    return pair.toString().matches("\".*\":$") ? "" : pair;
                                })
                                .filter(sb -> !sb.isEmpty())
                                .collect(Collectors.joining(COMMA))
                )
                .append(RIGHT_BRACE);
    }

    private static StringBuilder getPair(Field field, Object object) {
        try {
            setFieldAccessible(field, object);
            return new StringBuilder()
                    .append(QUOTE)
                    .append(field.getName())
                    .append(QUOTE)
                    .append(COLON)
                    .append(valueTypeSelector(field, object));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static StringBuilder valueTypeSelector(Field field, Object object) throws IllegalAccessException {
        if (ParserPredicates.isBooleanField.test(field)) {
            return getBooleanData(field, object);
        } else if (ParserPredicates.isNumberField.test(field)) {
            return new StringBuilder().append(field.get(object));
        } else if (ParserPredicates.isStringField.test(field)) {
            return getStringData(field, object);
        } else if (ParserPredicates.isTemporalField.test(field)) {
            return getTemporalData(field, object);
        } else if (ParserPredicates.isMapField.test(field)) {
            return getMapData(field, object);
        } else if (ParserPredicates.isArrayField.test(field)) {
            return getArrayData(field, object);
        } else if (ParserPredicates.isUnknownField.test(field)) {
            return getObjectData(field.get(object));
        }
        return null;
    }

    private static StringBuilder getStringData(Field field, Object object) throws IllegalAccessException {
        if (Objects.isNull(field.get(object))) return new StringBuilder();
        return new StringBuilder()
                .append(QUOTE)
                .append(escapeForJSON(field.get(object).toString()))
                .append(QUOTE);
    }

    private static StringBuilder getBooleanData(Field field, Object object) throws IllegalAccessException {
        if (Objects.isNull(field.get(object)))
            return new StringBuilder();
        else
            return new StringBuilder().append(field.get(object));
    }

    private static StringBuilder getTemporalData(Field field, Object object) throws IllegalAccessException {
        StringBuilder builder = new StringBuilder();
        Calendar calendar = ((Calendar) field.get(object));
        if (Objects.nonNull(calendar)) {
            builder.append(LEFT_BRACE)
                    .append("\"year\"").append(COLON)
                    .append(calendar.get(Calendar.YEAR)).append(COMMA)
                    .append("\"month\"").append(COLON)
                    .append(calendar.get(Calendar.MONTH)).append(COMMA)
                    .append("\"dayOfMonth\"").append(COLON)
                    .append(calendar.get(Calendar.DAY_OF_MONTH)).append(COMMA)
                    .append("\"hourOfDay\"").append(COLON)
                    .append(calendar.get(Calendar.HOUR_OF_DAY)).append(COMMA)
                    .append("\"minute\"").append(COLON)
                    .append(calendar.get(Calendar.MINUTE)).append(COMMA)
                    .append("\"second\"").append(COLON)
                    .append(calendar.get(Calendar.SECOND)).append(RIGHT_BRACE);
        }
        return builder;
    }

    private static StringBuilder getArrayData(Field field, Object object) throws IllegalAccessException {
        StringBuilder builder = new StringBuilder();
        if (Objects.isNull(field.get(object))) return builder;
        builder.append(LEFT_SQUARE);
        List<Object> elements = new ArrayList<>();
        if (field.getType().isArray()) {
            Object array = field.get(object);
            if (Objects.nonNull(array)) {
                for (int i = 0; i < Array.getLength(array); i++) {
                    elements.add(Array.get(array, i));
                }
            }
        } else {
            elements = new ArrayList<>((Collection<?>) field.get(object));
        }
        for (int i = 0; i < elements.size(); i++) {
            Object element = elements.get(i);
            if (element.getClass().isPrimitive()) {
                builder.append(element);
                if (i < elements.size() - 1) builder.append(COMMA);
            } else if (element.getClass().equals(String.class)) {
                builder.append(QUOTE)
                        .append(element)
                        .append(QUOTE);
                if (i < elements.size() - 1) builder.append(COMMA);
            } else if (ParserPredicates.isUnknownObject.test(element)) {
                builder.append(getObjectData(element));
                if (i < elements.size() - 1) builder.append(COMMA);
            } else {
                builder.append(
                        getJSONableFields(element.getClass().getDeclaredFields()).stream()
                                .map(elementField -> {
                                            setFieldAccessible(elementField, element);
                                            try {
                                                return valueTypeSelector(elementField, element);
                                            } catch (IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                )
                                .collect(Collectors.joining(COMMA))
                );
                if (i < elements.size() - 1) builder.append(COMMA);
            }
        }
        return builder.append(RIGHT_SQUARE);
    }

    private static StringBuilder getMapData(Field field, Object object) throws IllegalAccessException {
        if (Objects.isNull(field.get(object))) return new StringBuilder();
        Object map = field.get(object);
        return new StringBuilder().append(LEFT_BRACE).append(
                        ((Map<?, ?>) map).entrySet().stream()
                                .map(entry -> new StringBuilder()
                                        .append(QUOTE)
                                        .append(entry.getKey().toString())
                                        .append(QUOTE)
                                        .append(COLON)
                                        .append(mapValueSelector(entry.getValue())))
                                .collect(Collectors.joining(COMMA)))
                .append(RIGHT_BRACE);
    }

    private static StringBuilder mapValueSelector(Object object) {
        StringBuilder builder = new StringBuilder();
        if (object.getClass().isPrimitive()) {
            builder.append(object);
        } else if (object.getClass().equals(String.class)) {
            builder.append(QUOTE)
                    .append(object)
                    .append(QUOTE);
        } else if (ParserPredicates.isUnknownObject.test(object)) {
            builder.append(getObjectData(object));
        } else {
            builder.append(
                    getJSONableFields(object.getClass().getDeclaredFields()).stream()
                            .map(elementField -> {
                                        setFieldAccessible(elementField, object);
                                        try {
                                            return valueTypeSelector(elementField, object);
                                        } catch (IllegalAccessException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            )
                            .collect(Collectors.joining(COMMA))
            );
        }
        return builder;
    }

    private static List<Field> getJSONableFields(Field[] fields) {
        return Arrays.stream(fields)
                .filter(ParserPredicates.isNotTransientField.and(ParserPredicates.isNotStaticField))
                .toList();
    }

    private static void setFieldAccessible(Field field, Object object) {
        if (!field.canAccess(object))
            field.setAccessible(true);
    }

    private static String escapeForJSON(String str) {
        StringBuilder builder = new StringBuilder();

        if (str == null) return null;

        for (int index = 0; index < str.length(); index++) {
            char ch = str.charAt(index);

            switch (ch) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '/' -> builder.append("\\/");
                case '\b' -> builder.append("\\b");
                case '\f' -> builder.append("\\f");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> builder.append((ch < ' ') ? String.format("\\u%04x", (int) ch) : ch);
            }
        }
        return builder.toString();
    }
}
