import XRegExp from "xregexp"

export const PHONE_NUMBER_REGEX = XRegExp('^\\+?(?:[\\s\\-/\\\\]?\\d){7,15}$');
export const LOGIN_REGEX = XRegExp("^\\w{3,30}$")
export const PASSWORD_REGEX = XRegExp("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).{8,}$")
export const NAME_REGEX = XRegExp("^[\\p{L}' -]+\\.?$")
export const COUNTRY_REGEX = XRegExp("^(?:[\\p{L}']+[- ]?)+$")
export const CITY_REGEX = XRegExp("^(?:[\\p{L}']+[- ]?)+$")
export const STREET_REGEX = XRegExp("^(?:[\\p{L}'.]+[- ]?)+$")
export const POST_CODE_REGEX = XRegExp("^[a-zA-Z\\d -]{4,10}$")
export const COMPANY_NAME_REGEX = XRegExp("^[\\p{L} \\d]{3,}$")
export const EMAIL_REGEX = XRegExp("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\\])")
export const HOUSE_STREET_NUMBER_REGEX = XRegExp("^\\d{1,4}[a-zA-Z]{0,2}$")
export const NIP_REGEX = XRegExp("^[1-9]\\d{9}$")