import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.function.Predicate.*;

public interface ParserPredicates {
    Predicate<Field> isNotTransientField = (field) ->
            !Modifier.isTransient(field.getModifiers());

    Predicate<Field> isNotStaticField = (field) ->
            !Modifier.isStatic(field.getModifiers());

    Predicate<Field> isStringField = (field) ->
            field.getType().equals(String.class)
                    || field.getType().equals(Character.class) || field.getType().equals(char.class);

    Predicate<Field> isBooleanField = (field) ->
            field.getType().equals(Boolean.class) || field.getType().equals(boolean.class);

    Predicate<Field> isNumberField = (field) ->
            field.getType().equals(Integer.class) || field.getType().equals(int.class)
                    || field.getType().equals(Long.class) || field.getType().equals(long.class)
                    || field.getType().equals(Short.class) || field.getType().equals(short.class)
                    || field.getType().equals(Byte.class) || field.getType().equals(byte.class)
                    || field.getType().equals(Float.class) || field.getType().equals(float.class)
                    || field.getType().equals(Double.class) || field.getType().equals(double.class);

    Predicate<Field> isArrayField = (field) ->
            field.getType().isArray() || Collection.class.isAssignableFrom(field.getType());

    Predicate<Field> isMapField = (field) ->
            Map.class.isAssignableFrom(field.getType());

    Predicate<Field> isTemporalField = (field) ->
            Calendar.class.isAssignableFrom(field.getType());

    Predicate<Field> isUnknownField = not(isNumberField)
            .and(not(isBooleanField))
            .and(not(isStringField))
            .and(not(isArrayField))
            .and(not(isMapField))
            .and(not(isTemporalField));

    Predicate<Object> isStringObject = (object) ->
            object.getClass().equals(String.class) || object.getClass().equals(Character.class);

    Predicate<Object> isBooleanObject = (object) ->
            object.getClass().equals(Boolean.class);

    Predicate<Object> isNumberObject = (object) ->
            object.getClass().equals(Integer.class)
                    || object.getClass().equals(Long.class)
                    || object.getClass().equals(Short.class)
                    || object.getClass().equals(Byte.class)
                    || object.getClass().equals(Float.class)
                    || object.getClass().equals(Double.class);

    Predicate<Object> isArrayObject = (object) ->
            object.getClass().isArray() || Collection.class.isAssignableFrom(object.getClass());

    Predicate<Object> isMapObject = (object) ->
            Map.class.isAssignableFrom(object.getClass());

    Predicate<Object> isTemporalObject = (object) ->
            Calendar.class.isAssignableFrom(object.getClass());

    Predicate<Object> isUnknownObject = not(isNumberObject)
            .and(not(isBooleanObject))
            .and(not(isStringObject))
            .and(not(isArrayObject))
            .and(not(isMapObject))
            .and(not(isTemporalObject));
}
