package fromJSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectFromJSONAdapter {
    private int position;
    private JSONStringParser.State state;

    private JSONStringParser.State valueTrigger = null;

    private Map<String, Object> objectMap = new HashMap<>();

    private List<Object> fieldArrayValue = new ArrayList<>();

    private int arrayDepth = 0;

    private ObjectFromJSONAdapter fieldObjectValue = null;

    private StringBuilder fieldName = new StringBuilder();

    private StringBuilder fieldValue = new StringBuilder();

    private StringBuilder hexValue = new StringBuilder();

    public ObjectFromJSONAdapter(int i, JSONStringParser.State state) {
        this.position = i;
        this.state = state;
    }

    public List<Object> getFieldArrayValue() {
        return fieldArrayValue;
    }

    public ObjectFromJSONAdapter getFieldObjectValue() {
        return fieldObjectValue;
    }

    public JSONStringParser.State getState() {
        return state;
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
    }

    public StringBuilder getFieldName() {
        return fieldName;
    }

    public StringBuilder getFieldValue() {
        return fieldValue;
    }

    public StringBuilder getHexValue() {
        return hexValue;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setState(JSONStringParser.State state) {
        this.state = state;
        switch (state) {
            case STRING_START, NUMBER_START, BOOLEAN_START, ARRAY_START, OBJECT_START, ARRAY_END ->
                    this.valueTrigger = state;
            case T, F -> this.valueTrigger = JSONStringParser.State.BOOLEAN_START;
            case N -> this.valueTrigger = JSONStringParser.State.NULL_START;
        }
        if (state == JSONStringParser.State.VALUE_END
                || state == JSONStringParser.State.OBJECT_END
                || state == JSONStringParser.State.ARRAY_END) {
            if (this.arrayDepth != 0) {
                switch (valueTrigger) {
                    case STRING_START, NUMBER_START -> fieldArrayValue.add(fieldValue.toString());
                    case BOOLEAN_START -> fieldArrayValue.add(Boolean.valueOf(fieldValue.toString()));
                    case OBJECT_START -> fieldArrayValue.add(fieldObjectValue);
                }
            }
            setMap();
            fieldName.delete(0, fieldName.length());
            fieldValue.delete(0, fieldValue.length());
            hexValue.delete(0, hexValue.length());
            if (arrayDepth == 0 && this.valueTrigger == JSONStringParser.State.ARRAY_END) fieldArrayValue.clear();
        }
    }

    private void setMap() {
        if (arrayDepth == 0) {
            switch (valueTrigger) {
                case STRING_START, NUMBER_START -> this.objectMap.put(fieldName.toString(), fieldValue.toString());
                case BOOLEAN_START -> this.objectMap.put(fieldName.toString(), Boolean.valueOf(fieldValue.toString()));
                case ARRAY_END -> this.objectMap.put(fieldName.toString(), fieldArrayValue);
                case OBJECT_START -> this.objectMap.put(fieldName.toString(), fieldObjectValue);
            }
        }
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public void incrementArrayDepth() {
        this.arrayDepth++;
    }

    public void decrementArrayDepth() {
        this.arrayDepth--;
    }

    public void appendFieldName(char ch) {
        this.fieldName.append(ch);
    }

    public void appendHexValue(char ch) {
        this.hexValue.append(ch);
    }

    public void clearHexValue() {
        this.hexValue.delete(0, hexValue.length());
    }

    public void appendFieldValue(char ch) {
        this.fieldValue.append(ch);
    }

    public ObjectFromJSONAdapter setFieldObjectValue(ObjectFromJSONAdapter fieldObjectValue) {
        this.fieldObjectValue = fieldObjectValue;
        return this.fieldObjectValue;
    }

    public void setFieldArrayValue(List<Object> fieldArrayValue) {
        this.fieldArrayValue = fieldArrayValue;
    }

    public void addFieldArrayValue(Object object) {
        this.fieldArrayValue.add(object);
    }

    @Override
    public String toString() {
        return "fromJSON.ObjectFromJSONAdapter{" +
                "state=" + state +
                ", valueTrigger=" + valueTrigger +
                ", objectMap=" + objectMap +
                ", fieldArrayValue=" + fieldArrayValue +
                ", arrayDepth=" + arrayDepth +
                ", fieldObjectValue=" + fieldObjectValue +
                ", fieldName=" + fieldName +
                ", fieldValue=" + fieldValue +
                ", hexValue=" + hexValue +
                '}';
    }
}
