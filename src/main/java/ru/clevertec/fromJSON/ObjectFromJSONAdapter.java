package ru.clevertec.fromJSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ObjectFromJSONAdapter {
    private int position;
    private JSONStringParser.State state;

    private JSONStringParser.ValueTrigger valueTrigger = JSONStringParser.ValueTrigger.OBJECT;

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

    public JSONStringParser.State getState() {
        return state;
    }

    public Map<String, Object> getObjectMap() {
        return objectMap;
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
            case STRING_START -> {
                this.valueTrigger = JSONStringParser.ValueTrigger.STRING;
            }
            case NUMBER_START -> {
                this.valueTrigger = JSONStringParser.ValueTrigger.NUMBER;
            }
            case T, F -> {
                this.valueTrigger = JSONStringParser.ValueTrigger.BOOLEAN;
            }
            case ARRAY_START -> {
                this.valueTrigger = JSONStringParser.ValueTrigger.ARRAY;
            }
            case OBJECT_START -> {
                this.valueTrigger = JSONStringParser.ValueTrigger.OBJECT;
            }
            case N -> {
                this.valueTrigger = JSONStringParser.ValueTrigger.NULL;
            }
        }
        if (state == JSONStringParser.State.VALUE_END || state == JSONStringParser.State.OBJECT_END) {
            if (this.arrayDepth != 0) {
                switch (valueTrigger) {
                    case STRING, NUMBER -> fieldArrayValue.add(fieldValue.toString());
                    case BOOLEAN -> fieldArrayValue.add(Boolean.valueOf(fieldValue.toString()));
                    case OBJECT, ARRAY -> fieldArrayValue.add(fieldObjectValue);
                }
                fieldValue.delete(0, fieldValue.length());
            }
            setMap();
        }
    }

    private void setMap() {
        if (arrayDepth == 0) {
            setMap(this.valueTrigger);
        }
    }

    public void setMap(JSONStringParser.ValueTrigger valueTrigger) {
        if (!fieldName.isEmpty()) {
            switch (valueTrigger) {
                case STRING, NUMBER -> this.objectMap.put(fieldName.toString(), fieldValue.toString());
                case BOOLEAN -> this.objectMap.put(fieldName.toString(), Boolean.valueOf(fieldValue.toString()));
                case ARRAY -> this.objectMap.put(fieldName.toString(), fieldArrayValue);
                case OBJECT -> this.objectMap.put(fieldName.toString(), fieldObjectValue);
                case NULL -> this.objectMap.put(fieldName.toString(), null);
            }
            fieldName.delete(0, fieldName.length());
            fieldValue.delete(0, fieldValue.length());
            hexValue.delete(0, hexValue.length());
            fieldObjectValue = null;
            if (arrayDepth == 0)
                fieldArrayValue = new ArrayList<>();
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
