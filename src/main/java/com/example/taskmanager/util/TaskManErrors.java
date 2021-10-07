package com.example.taskmanager.util;

import com.example.taskmanager.config.Translator;
import java.util.HashMap;
import java.util.Map;

public class TaskManErrors {

    // component code
    public static final String TASK_SERVICE = "10";

    // feature code
    public static final String TASK_MANAGEMENT = "001";
    public static final String TASK_USER = "002";

    // error code
    public static final String INTERNAL_ERROR = "500";

    public static final String INVALID_INPUT = "004";
    public static final String USER_NOT_FOUND = "404";
    public static final String USER_ALREADY_AVAILABLE = "405";
    public static final String INVALID_CREDENTIALS = "041";
    public static final String USER_DISABLE = "042";
    public static final String INVALID_USER_CONTEXT = "040";

    public static final String PROJECT_NOT_FOUND = "406";
    public static final String TASK_NOT_FOUND = "407";
    public static final String NOT_ALLOWED_CLOSED_TASK = "409";
    public static final String FORBIDDEN_OPERATION = "408";

    public static final String EXTERNAL_SERVICE_ERROR = "007";

    // Error mapping
    public static final Map<String, String> ERROR_MAP = new HashMap<String, String>();

    static {
        ERROR_MAP.put(INTERNAL_ERROR, "INTERNAL_ERROR");
        ERROR_MAP.put(INVALID_INPUT, "INVALID_INPUT");
        ERROR_MAP.put(INVALID_CREDENTIALS, "INVALID_CREDENTIALS");
        ERROR_MAP.put(INVALID_USER_CONTEXT, "INVALID_USER_CONTEXT");
        ERROR_MAP.put(USER_NOT_FOUND, "USER_NOT_FOUND");
        ERROR_MAP.put(USER_ALREADY_AVAILABLE, "USER_ALREADY_AVAILABLE");
        ERROR_MAP.put(PROJECT_NOT_FOUND, "PROJECT_NOT_FOUND");
        ERROR_MAP.put(TASK_NOT_FOUND, "TASK_NOT_FOUND");
        ERROR_MAP.put(FORBIDDEN_OPERATION, "FORBIDDEN_OPERATION");
        ERROR_MAP.put(NOT_ALLOWED_CLOSED_TASK, "NOT_ALLOWED_CLOSED_TASK");
        ERROR_MAP.put(USER_DISABLE, "USER_DISABLE");
        ERROR_MAP.put(EXTERNAL_SERVICE_ERROR, "EXTERNAL_SERVICE_ERROR");
    }

    public static String getErrorCode(String featureCode, String errorCode) {
        return TaskManErrors.TASK_SERVICE + featureCode + errorCode;
    }

    public static String getErrorMessage(String message) {
        return Translator.toLocale(message);
    }
}
