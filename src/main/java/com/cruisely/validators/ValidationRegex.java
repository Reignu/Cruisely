package com.cruisely.validators;

public class ValidationRegex {
    public static final String PHONE_NUMBER = "^\\+?(?:[\\s\\-/\\\\]?\\d)+$";
    public static final String LOGIN = "^\\w{3,30}$";
    public static final String PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$";
    public static final String NAME = "^[\\p{L}' -]+\\.?$";
    public static final String FIRST_SECOND_HARBOR_CRUISEGROUP_NAME = "^[\\p{L}' -]+\\.?$";
    public static final String COUNTRY = "^(?:[\\p{L}']+[- ]?)+$";
    public static final String CITY = "^(?:[\\p{L}']+[- ]?)+$";
    public static final String STREET = "^(?:[\\p{L}'.]+[- ]?)+$";
    public static final String POST_CODE = "^[a-zA-Z\\d -]{4,10}$";
    public static final String COMPANY_NAME = "^[\\p{L} \\d]{3,}$";
    public static final String HOUSE_NUMBER = "^\\d{1,4}[a-zA-Z]{0,2}$";
    public static final String STREET_NUMBER = "^\\d{1,4}[a-zA-Z]{0,2}$";
}
