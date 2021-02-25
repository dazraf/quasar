package co.paralleluniverse.common.reflection;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;

/**
 * Class to define the either-or logic of looking for a field that may have one of many unique names.
 * This is relevant when dealing with a broader set of JVMs with differently named intrinsic fields.
 */
public class GetFirstDeclaredField implements PrivilegedExceptionAction<Field> {
    private final Class<?> clazz;
    private final String[] fieldNames;

    public GetFirstDeclaredField(Class<?> clazz, String... fieldNames) {
        this.clazz = clazz;
        this.fieldNames = fieldNames;
    }

    @Override
    public Field run() throws Exception {
        for (String fieldName : fieldNames) {
            Field field = getFieldOrNull(fieldName);
            if (field != null) {
                return field;
            }
        }
        throw new NoSuchFieldException("Could not find any of the fields: " + joinFieldNames());
    }

    private Field getFieldOrNull(String fieldName) {
        try {
            return new GetDeclaredField(clazz, fieldName).run();
        } catch (NoSuchFieldException ex) {
            return null;
        }
    }

    private String joinFieldNames() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String fieldName: fieldNames) {
            if (!first) {
                sb.append(", ").append(fieldName);
            } else {
                sb.append(fieldName);
                first = false;
            }
        }
        return sb.toString();
    }
}
