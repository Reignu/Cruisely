import {Box} from "@material-ui/core";
import DarkedTextField from "../../../components/DarkedTextField";
import React, {useEffect, useReducer, useState} from "react";
import {useTranslation} from "react-i18next";
import RoundedButton from "../../../components/RoundedButton";
import store from "../../../redux/store";
import axios from "../../../Services/URL";
import useHandleError from "../../../errorHandler";
import {useSnackbarQueue} from "../../snackbar";
import styles from "../../../styles/auth.global.module.css";
import {
    CITY_REGEX,
    COMPANY_NAME_REGEX, COUNTRY_REGEX, HOUSE_STREET_NUMBER_REGEX, NIP_REGEX,
    PHONE_NUMBER_REGEX, POST_CODE_REGEX,
    STREET_REGEX
} from "../../../regexConstants";
import PopupAcceptAction from "../../../PopupAcceptAction";

export default function () {
    const [, forceUpdate] = useReducer(x => x + 1, 0)
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const {t} = useTranslation()

    const [companyName, setCompanyName] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [nip, setNip] = useState('')
    const [street, setStreet] = useState('')
    const [houseNumber, setHouseNumber] = useState('')
    const [city, setCity] = useState('')
    const [postalCode, setPostalCode] = useState('')
    const [country, setCountry] = useState('')

    const [companyNameRegexError, setCompanyNameRegexError] = useState(false)
    const [phoneNumberRegexError, setPhoneNumberRegexError] = useState(false)
    const [nipRegexError, setNipRegexError] = useState(false)
    const [streetRegexError, setStreetRegexError] = useState(false)
    const [houseNumberRegexError, setHouseNumberRegexError] = useState(false)
    const [cityRegexError, setCityRegexError] = useState(false)
    const [postalCodeRegexError, setPostalCodeRegexError] = useState(false)
    const [countryRegexError, setCountryRegexError] = useState(false)

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

    const handleAddCompany =  () => {
        const {token} = store.getState();
        const json = JSON.stringify({
            name: companyName,
            addressDto: {
                houseNumber: houseNumber,
                street: street,
                postalCode: postalCode,
                city: city,
                country: country
            },
            phoneNumber: phoneNumber,
            nip: nip
        })
        axios.post('company/add-company', json,{
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${token}`
            }}).then(res => {
            showSuccess(t('successful action'))
            forceUpdate()
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
            switch (message) {
                case 'companies_nip_unique_constraint':
                    setNipRegexError(true)
                    break
                case 'companies_name_unique_constraint':
                    setCompanyNameRegexError(true)
                    break
            }
        })
    }

    const addCompanyCheck = async () => {
        setCompanyNameRegexError(!COMPANY_NAME_REGEX.test(companyName))
        setPhoneNumberRegexError(!PHONE_NUMBER_REGEX.test(phoneNumber))
        setNipRegexError(!NIP_REGEX.test(nip))
        setStreetRegexError(!STREET_REGEX.test(street))
        setHouseNumberRegexError(!HOUSE_STREET_NUMBER_REGEX.test(houseNumber))
        setCityRegexError(!CITY_REGEX.test(city))
        setPostalCodeRegexError(!POST_CODE_REGEX.test(postalCode))
        setCountryRegexError(!COUNTRY_REGEX.test(country))

        if (!COMPANY_NAME_REGEX.test(companyName) || !PHONE_NUMBER_REGEX.test(phoneNumber) || !NIP_REGEX.test(nip) ||
            !STREET_REGEX.test(street) || !HOUSE_STREET_NUMBER_REGEX.test(houseNumber) || !CITY_REGEX.test(city) ||
            !POST_CODE_REGEX.test(postalCode) || !COUNTRY_REGEX.test(country)) {
            handleError("error.fields")
        } else {
            handleAddCompany()
        }
    }

    return (
        <div>
            <Box style={{
                width: '100%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between"
            }}>
                <DarkedTextField
                    label={t("company name") + ' *'}
                    placeholder={t("company name example")}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    value={companyName}
                    onChange={event => {
                        setCompanyName(event.target.value)
                        setCompanyNameRegexError(!COMPANY_NAME_REGEX.test(event.target.value))
                    }}
                    regexError={companyNameRegexError}
                />
                <DarkedTextField
                    label={t("phoneNumber") + ' *'}
                    placeholder={t("phoneNumberExample")}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    value={phoneNumber}
                    onChange={event => {
                        setPhoneNumber(event.target.value)
                        setPhoneNumberRegexError(!PHONE_NUMBER_REGEX.test(event.target.value))
                    }}
                    regexError={phoneNumberRegexError}
                />
                <DarkedTextField
                    label={'NIP'+' *'}
                    placeholder={"1234567890"}
                    className={styles.input}
                    style={{
                        marginLeft: 20
                    }}
                    value={nip}
                    onChange={event => {
                        setNip(event.target.value)
                        setNipRegexError(!NIP_REGEX.test(event.target.value))
                    }}
                    regexError={nipRegexError}
                />
            </Box>
            <Box style={{
                width: '100%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between"
            }}>
                <DarkedTextField
                    label={t("street") + ' *'}
                    placeholder={t("streetExample")}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    value={street}
                    onChange={event => {
                        setStreet(event.target.value)
                        setStreetRegexError(!STREET_REGEX.test(event.target.value))
                    }}
                    regexError={streetRegexError}
                />
                <DarkedTextField
                    label={t("houseNumber") + ' *'}
                    placeholder={t("houseNumberExample")}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    value={houseNumber}
                    onChange={event => {
                        setHouseNumber(event.target.value)
                        setHouseNumberRegexError(!HOUSE_STREET_NUMBER_REGEX.test(event.target.value))
                    }}
                    regexError={houseNumberRegexError}
                />
                <DarkedTextField
                    label={t("city") + ' *'}
                    placeholder={t("cityExample")}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    value={city}
                    onChange={event => {
                        setCity(event.target.value)
                        setCityRegexError(!CITY_REGEX.test(event.target.value))
                    }}
                    regexError={cityRegexError}
                />
                <DarkedTextField
                    label={t("postalCode") + ' *'}
                    placeholder={t("postalCodeExample")}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    value={postalCode}
                    onChange={event => {
                        setPostalCode(event.target.value)
                        setPostalCodeRegexError(!POST_CODE_REGEX.test(event.target.value))
                    }}
                    regexError={postalCodeRegexError}
                />
                <DarkedTextField
                    label={t("country") + ' *'}
                    placeholder={t("countryExample")}
                    className={styles.input}
                    style={{
                        marginLeft: 20
                    }}
                    value={country}
                    onChange={event => {
                        setCountry(event.target.value)
                        setCountryRegexError(!COUNTRY_REGEX.test(event.target.value))
                    }}
                    regexError={countryRegexError}
                />
            </Box>
            <RoundedButton
                onClick={() =>{ setButtonPopupAcceptAction(true)}}
                style={{width: '25%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                color="pink"
            >{t("add company")} </RoundedButton>
            <PopupAcceptAction
                open={buttonPopupAcceptAction}
                onConfirm={() => {
                    setButtonPopupAcceptAction(false)
                    addCompanyCheck()
                }}
                onCancel={() => {
                    setButtonPopupAcceptAction(false)
                }}/>
        </div>
    )
}