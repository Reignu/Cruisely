import React, {useEffect, useReducer, useState} from 'react'

import Grid from '@material-ui/core/Grid'

import {Link} from 'react-router-dom'

import {useTranslation} from 'react-i18next';

import styles from '../../../styles/ManageAccount.module.css'
import tbStyles from '../../../styles/mtdTable.module.css'
import RoundedButton from '../../../components/RoundedButton';
import DarkedTextField from '../../../components/DarkedTextField';

import axios from "../../../Services/URL";
import {useSnackbarQueue} from "../../snackbar";
import store from "../../../redux/store";
import {refreshToken} from "../../../Services/userService";
import useHandleError from "../../../errorHandler";

import PopupAcceptAction from "../../../PopupAcceptAction";
import {
    CITY_REGEX, COUNTRY_REGEX,
    EMAIL_REGEX,
    HOUSE_STREET_NUMBER_REGEX,
    NAME_REGEX,
    PHONE_NUMBER_REGEX, POST_CODE_REGEX,
    STREET_REGEX
} from "../../../regexConstants";

export default function ChangeAccountData() {
    const {t} = useTranslation()
    const handleError = useHandleError()

    const showSuccess = useSnackbarQueue('success')

    const currentAccount = JSON.parse(sessionStorage.getItem("changeAccountData") as string)
    const currentAccountMTD = JSON.parse(sessionStorage.getItem("changeAccountDataMta") as string)
    const currentAccountAclMTD = JSON.parse(sessionStorage.getItem("changeAccountAclDataMta") as string)
    const currentAccountAddressMTD = JSON.parse(sessionStorage.getItem("changeAccountAddressDataMta") as string)
    const clientAccount = JSON.parse(sessionStorage.getItem("changeAccountData") as string)
    const businessAccount = JSON.parse(sessionStorage.getItem("changeAccountData") as string)
    const acLevel = currentAccount.accessLevels.map((accessLevel: any) => accessLevel.accessLevelType)
    const clientAddr = clientAccount.accessLevels.find((data: any) => (data.accessLevelType.includes("CLIENT")))
    const businnesPhone = businessAccount.accessLevels.find((data: any) => (data.accessLevelType.includes("BUSINESS_WORKER")))
    const [, forceUpdate] = useReducer(x => x + 1, 0); // used to force component refresh on forceUpdate call
    const [ChangePerData, setPerData] = useState(false)
    const [ChangAddress, setChangChangAddress] = useState(false)
    const [ChangePhone, setChangePhone] = useState(false)
    const [metadata, setMetadata] = useState(false)
    const [ChangeMail, setMail] = useState(false)
    const [firstName, setFirstName] = useState('')
    const [secondName, setSecondName] = useState('')
    const [email, setEmail] = useState('')

    const [firstNameRegexError, setFirstNameRegexError] = useState(false)
    const [secondNameRegexError, setSecondNameRegexError] = useState(false)
    const [emailRegexError, setEmailRegexError] = useState(false)
    const [phoneNumberRegexError, setPhoneNumberRegexError] = useState(false)
    const [businnesPhoneRegexError, setBusinnesPhoneRegexError] = useState(false)
    const [streetRegexError, setStreetRegexError] = useState(false)
    const [houseNumberRegexError, setHouseNumberRegexError] = useState(false)
    const [postalCodeRegexError, setPostalCodeRegexError] = useState(false)
    const [cityRegexError, setCityRegexError] = useState(false)
    const [countryRegexError, setCountryRegexError] = useState(false)


    const [houseNumber, setHouseNumber] = useState('')
    const [street, setStreet] = useState('')
    const [postalCode, setPostalCode] = useState('')
    const [city, setCity] = useState('')
    const [country, setCountry] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')

    const [alterType, setAlterType] = useState('')
    const [alteredBy, setAlteredBy] = useState('')
    const [createdBy, setCreatedBy] = useState('')
    const [creationDateTime, setCreationDateTime] = useState('')
    const [lastAlterDateTime, setLastAlterDateTime] = useState('')
    const [lastCorrectAuthenticationDateTime, setLastCorrectAuthenticationDateTime] = useState('')
    const [lastCorrectAuthenticationLogicalAddress, setLastCorrectAuthenticationLogicalAddress] = useState('')
    const [lastIncorrectAuthenticationDateTime, setLastIncorrectAuthenticationDateTime] = useState('')
    const [lastIncorrectAuthenticationLogicalAddress, setLastIncorrectAuthenticationLogicalAddress] = useState('')
    const [numberOfAuthenticationFailures, setNumberOfAuthenticationFailures] = useState('')
    const [version, setVersion] = useState('')

    const [alterTypeAdr, setAlterTypeAdr] = useState('')
    const [alteredByAdr, setAlteredByAdr] = useState('')
    const [createdByAdr, setCreatedByAdr] = useState('')
    const [creationDateTimeAdr, setCreationDateTimeAdr] = useState('')
    const [lastAlterDateTimeAdr, setLastAlterDateTimeAdr] = useState('')
    const [versionAdr, setVersionAdr] = useState('')

    const [alterTypeAcl, setAlterTypeAcl] = useState('')
    const [alteredByAcl, setAlteredByAcl] = useState('')
    const [createdByAcl, setCreatedByAcl] = useState('')
    const [creationDateTimeAcl, setCreationDateTimeAcl] = useState('')
    const [lastAlterDateTimeAcl, setLastAlterDateTimeAcl] = useState('')
    const [versionAcl, setVersionAcl] = useState('')
    const [language, setLanguage] = useState('')
    const [businessPhoneNumber, setBusinessPhoneNumber] = useState('')
    const {token} = store.getState()
    const [buttonPopupAcceptChangeNumber, setButtonPopupAcceptChangeNumber] = useState(false);
    const [buttonPopupAcceptChangeData, setButtonPopupAcceptChangeData] = useState(false);
    const [buttonPopupAcceptChangeAddress, setButtonPopupAcceptChangeAddress] = useState(false);
    const [buttonPopupAcceptChangeMail, setButtonPopupAcceptChangeMail] = useState(false);

    const closeAll = () => {
        setPerData(false)
        setChangChangAddress(false)
        setChangePhone(false)
        setMetadata(false)
        setMail(false)
    }

    //Functions for personal data change
    const handleChangePerData = () => {
        setChangChangAddress(false)
        setChangePhone(false)
        setMail(false)
        setMetadata(false)
        setPerData(state => !state)
    }
    const handleChangeMail = () => {
        setPerData(false)
        setChangChangAddress(false)
        setChangePhone(false)
        setMetadata(false)
        setMail(state => !state)
    }
    //Functions for address data change
    const handleChangAddress = () => {
        setChangePhone(false)
        setMail(false)
        setPerData(false)
        setMetadata(false)
        setChangChangAddress(state => !state)
    }
    const handleChangePhone = () => {
        setPerData(false)
        setChangChangAddress(false)
        setMail(false)
        setMetadata(false)
        setChangePhone(state => !state)
    }
    const handleMetadata = () => {
        setMetadata(state => !state)
    }

    const handleConfirmChangeMail = () => {
        setEmailRegexError(!EMAIL_REGEX.test(email))
        if(!EMAIL_REGEX.test(email)) {
            handleError('error.fields')
            return
        } else {
            setButtonPopupAcceptChangeMail(true)
        }
    }

    const changeMail = async () => {
        const {token} = store.getState()
        await axios.post('account/request-other-email-change', {
            newEmail: email,
            login: currentAccount.login,
            version: currentAccount.version
        }, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        }).then(res => {
            setButtonPopupAcceptChangeMail(false)
            refreshToken()
            handleChangeMail()
            showSuccess(t('successful action'))
        }).catch(error => {
            setButtonPopupAcceptChangeMail(false)
            const message = error.response.data
            handleError(message, error.response.status)
        })
    }

    const handleConfirmPersonalDataChange = () => {
        setFirstNameRegexError(!NAME_REGEX.test(firstName))
        setSecondNameRegexError(!NAME_REGEX.test(secondName))

        if (!NAME_REGEX.test(firstName) || !NAME_REGEX.test(secondName)) {
            handleError('error.fields')
            return
        } else {
            setButtonPopupAcceptChangeData(true)
        }
    }

    const changePersonalData = async () => {
        const {token} = store.getState()
        const json = JSON.stringify({
            login: currentAccount.login,
            newFirstName: firstName,
            newSecondName: secondName,
            newEmail: email,
            version: currentAccount.version

        })

        await axios.put("/account/change-account-data", json, {
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "If-Match": currentAccount.etag,
                'Authorization': `Bearer ${token}`
            }
        }).then(res => {
            setButtonPopupAcceptChangeData(false)
            refreshToken()
            showSuccess(t('successful action'))
        }, error => {
            setButtonPopupAcceptChangeData(false)
            const message = error.response.data
            handleError(message, error.response.status)
        })

        await axios.get(`/account/details/${currentAccount.login}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(res => {
            sessionStorage.setItem("changeAccountData", JSON.stringify(res.data));
            forceUpdate()
            handleChangePerData()
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });

    }

    const handleConfirmChangeAddress = () => {
        setHouseNumberRegexError(!HOUSE_STREET_NUMBER_REGEX.test(houseNumber))
        setStreetRegexError(!STREET_REGEX.test(street))
        setPostalCodeRegexError(!POST_CODE_REGEX.test(postalCode))
        setCityRegexError(!CITY_REGEX.test(city))
        setCountryRegexError(!COUNTRY_REGEX.test(country))
        setPhoneNumberRegexError(!PHONE_NUMBER_REGEX.test(phoneNumber))
        if (!HOUSE_STREET_NUMBER_REGEX.test(houseNumber) || !STREET_REGEX.test(street) || !POST_CODE_REGEX.test(postalCode) ||
            !CITY_REGEX.test(city) || !COUNTRY_REGEX.test(country) || !PHONE_NUMBER_REGEX.test(phoneNumber)) {
            handleError('error.fields')
            return
        } else {
            setButtonPopupAcceptChangeAddress(true)
        }
    }

    const changeAddress = async () => {
        const {token} = store.getState()
        const json = JSON.stringify({
            login: currentAccount.login,
            version: currentAccount.version,

            newPhoneNumber: phoneNumber,
            newAddress: {
                newHouseNumber: houseNumber,
                newStreet: street,
                newPostalCode: postalCode,
                newCity: city,
                newCountry: country,
            },
            accVersion: clientAddr.accVersion
        })


        await axios.put("account/change-client-data", json, {
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "If-Match": currentAccount.etag,
                "Authorization": `Bearer ${token}`
            }
        }).then(res => {
            setButtonPopupAcceptChangeAddress(false)
            showSuccess(t('successful action'))
        }).catch(error => {
            setButtonPopupAcceptChangeAddress(false)
            const message = error.response.data
            handleError(message, error.response.status)
        })


        await axios.get(`/account/details/${currentAccount.login}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(res => {
            sessionStorage.setItem("changeAccountData", JSON.stringify(res.data));
            forceUpdate()
            handleChangAddress()
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
    }

    const handleConfirmChangePhone = () => {
        setPhoneNumberRegexError(!PHONE_NUMBER_REGEX.test(businessPhoneNumber))
        if (!PHONE_NUMBER_REGEX.test(businessPhoneNumber)) {
            handleError('error.fields')
            return
        } else {
            setButtonPopupAcceptChangeNumber(true)
        }
    }

    const changeBusinessPhone = async () => {
        const json = JSON.stringify({
            login: currentAccount.login,
            version: currentAccount.version,
            newPhoneNumber: businessPhoneNumber,
            accVersion: businnesPhone.accVersion

        })
        await axios.put('account/change-business-worker-data', json, {
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "If-Match": currentAccount.etag,
                "Authorization": `Bearer ${token}`,
            }
        }).then(res => {
            setButtonPopupAcceptChangeNumber(false)
            showSuccess(t('successful action'))
        }, error => {
            const message = error.response.data
            setButtonPopupAcceptChangeNumber(false)
            handleError(message, error.response.status)
        })

        await axios.get(`/account/details/${currentAccount.login}`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(res => {
            sessionStorage.setItem("changeAccountData", JSON.stringify(res.data));
            forceUpdate()
            handleChangePhone()
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });

    }

    useEffect( ()=>{
        setFirstName(currentAccount.firstName);
        setSecondName(currentAccount.secondName);

        if (clientAddr) {
            setStreet(clientAddr.address.street);
            setPostalCode(clientAddr.address.postalCode);
            setHouseNumber(clientAddr.address.houseNumber);
            setCountry(clientAddr.address.country);
            setCity(clientAddr.address.city);
            setPhoneNumber(clientAddr.phoneNumber);
        }
        if (businnesPhone) {
            setBusinessPhoneNumber(businnesPhone.phoneNumber);
        }
    }, [])

    useEffect(() => {
        setAlterType(currentAccountMTD.alterType);
        setAlteredBy(currentAccountMTD.alteredBy);
        setCreatedBy(currentAccountMTD.createdBy);
        if (currentAccountMTD.creationDateTime != null){
            setCreationDateTime(currentAccountMTD.creationDateTime.dayOfMonth + " " + t(currentAccountMTD.creationDateTime.month) + " " + currentAccountMTD.creationDateTime.year + " " + currentAccountMTD.creationDateTime.hour + ":" + currentAccountMTD.creationDateTime.minute.toString().padStart(2, '0'))
        }
        if (currentAccountMTD.lastAlterDateTime != null)
            setLastAlterDateTime(currentAccountMTD.lastAlterDateTime.dayOfMonth + " " + t(currentAccountMTD.lastAlterDateTime.month) + " " + currentAccountMTD.lastAlterDateTime.year + " " + currentAccountMTD.lastAlterDateTime.hour + ":" + currentAccountMTD.lastAlterDateTime.minute.toString().padStart(2, '0'));
        if (currentAccountMTD.lastCorrectAuthenticationDateTime != null)
            setLastCorrectAuthenticationDateTime(currentAccountMTD.lastCorrectAuthenticationDateTime.dayOfMonth + " " + t(currentAccountMTD.lastCorrectAuthenticationDateTime.month) + " " + currentAccountMTD.lastCorrectAuthenticationDateTime.year + " " + currentAccountMTD.lastCorrectAuthenticationDateTime.hour + ":" + currentAccountMTD.creationDateTime.minute.toString().padStart(2, '0'));
        setLastCorrectAuthenticationLogicalAddress(currentAccountMTD.lastCorrectAuthenticationLogicalAddress)
        if (currentAccountMTD.lastIncorrectAuthenticationDateTime != null)
            setLastIncorrectAuthenticationDateTime(currentAccountMTD.lastIncorrectAuthenticationDateTime.dayOfMonth + " " + t(currentAccountMTD.lastIncorrectAuthenticationDateTime.month) + " " + currentAccountMTD.lastIncorrectAuthenticationDateTime.year + " " + currentAccountMTD.lastIncorrectAuthenticationDateTime.hour + ":" + currentAccountMTD.lastIncorrectAuthenticationDateTime.minute.toString().padStart(2, '0'));
        setLastIncorrectAuthenticationLogicalAddress(currentAccountMTD.lastIncorrectAuthenticationLogicalAddress);
        setNumberOfAuthenticationFailures(currentAccountMTD.numberOfAuthenticationFailures)
        setVersion(currentAccountMTD.version);
        setLanguage(currentAccountMTD.languageType);

        if (clientAddr) {
            setAlterTypeAdr(currentAccountAddressMTD.alterType);
            setAlteredByAdr(currentAccountAddressMTD.alteredBy);
            setCreatedByAdr(currentAccountAddressMTD.createdBy);
            if (currentAccountAddressMTD.creationDateTime != null)
                setCreationDateTimeAdr(currentAccountAddressMTD.creationDateTime.dayOfMonth + " " + t(currentAccountAddressMTD.creationDateTime.month) + " " + currentAccountAddressMTD.creationDateTime.year + " " + currentAccountAddressMTD.creationDateTime.hour + ":" + currentAccountAddressMTD.creationDateTime.minute.toString().padStart(2, '0'))
            if (currentAccountAddressMTD.lastAlterDateTime != null)
                setLastAlterDateTimeAdr(currentAccountAddressMTD.lastAlterDateTime.dayOfMonth + " " + t(currentAccountAddressMTD.lastAlterDateTime.month) + " " + currentAccountAddressMTD.lastAlterDateTime.year + " " + currentAccountAddressMTD.lastAlterDateTime.hour + ":" + currentAccountAddressMTD.lastAlterDateTime.minute.toString().padStart(2, '0'));
            setVersionAdr(currentAccountAddressMTD.version);
        }
        if (businnesPhone) {
            setAlterTypeAcl(currentAccountAclMTD.alterType);
            setAlteredByAcl(currentAccountAclMTD.alteredBy);
            setCreatedByAcl(currentAccountAclMTD.createdBy);
            if (currentAccountAclMTD.creationDateTime != null)
                setCreationDateTimeAcl(currentAccountAclMTD.creationDateTime.dayOfMonth + " " + t(currentAccountAclMTD.creationDateTime.month) + " " + currentAccountAclMTD.creationDateTime.year + " " + currentAccountAclMTD.creationDateTime.hour + ":" + currentAccountAclMTD.creationDateTime.minute.toString().padStart(2, '0'))
            if (currentAccountAclMTD.lastAlterDateTime != null)
                setLastAlterDateTimeAcl(currentAccountAclMTD.lastAlterDateTime.dayOfMonth + " " + t(currentAccountAclMTD.lastAlterDateTime.month) + " " + currentAccountAclMTD.lastAlterDateTime.year + " " + currentAccountAclMTD.lastAlterDateTime.hour + ":" + currentAccountAclMTD.lastAlterDateTime.minute.toString().padStart(2, '0'));
            setVersionAcl(currentAccountAclMTD.version);
        }
    })

    return (
        <>
            <Grid container className={styles.wrapper}>
                <Grid item style={{display: ChangePerData ? "none" : "block"}} className={styles.item}>
                    <h3>{t("personal data")}</h3>
                    <div>
                        <div>
                            <h4>{t("name")}</h4>
                            <p>{currentAccount.firstName}</p>
                        </div>
                        <div>
                            <h4>{t("surname")}</h4>
                            <p>{currentAccount.secondName}</p>
                        </div>
                        <div>
                            <h4>{t("Login")}</h4>
                            <p>{currentAccount.login}</p>
                        </div>
                        <RoundedButton
                            color="blue"
                            onClick={handleChangePerData}
                        >{t("personal data change btn")}</RoundedButton>
                    </div>
                </Grid>

                <Grid item style={{display: ChangePerData ? "block" : "none"}} className={styles['change-item']}>
                    <h3>{t("personal data change")}</h3>
                    <div>
                        <DarkedTextField
                            type="text"
                            label={t("name") + ' *'}
                            placeholder={currentAccount.firstName}
                            value={firstName}
                            onChange={event => {
                                setFirstName(event.target.value)
                                setFirstNameRegexError(!NAME_REGEX.test(event.target.value))
                            }}
                            regexError={firstNameRegexError}/>
                        <DarkedTextField
                            type="text"
                            label={t("surname") + ' *'}
                            placeholder={currentAccount.secondName}
                            value={secondName}
                            onChange={event => {
                                setSecondName(event.target.value)
                                setSecondNameRegexError(!NAME_REGEX.test(event.target.value))
                            }}
                            regexError={secondNameRegexError}/>
                    </div>
                    <div>
                    <RoundedButton color="blue"
                                   onClick={handleConfirmPersonalDataChange}
                    >{t("confirm")}</RoundedButton>
                    <RoundedButton color="green"
                                   onClick={handleMetadata}
                    >{t("metadata")}</RoundedButton>
                    <RoundedButton color="pink"
                                   onClick={handleChangePerData}
                    >{t("cancel")}</RoundedButton>
                    </div>
                    <Grid item style={{display: metadata ? "block" : "none"}} className={styles['change-item']}>
                        <tr>
                            <td className={tbStyles.td}><h4>{t("alterType")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("alteredBy")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("createdBy")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("creationDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastAlterDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("version")}</h4></td>
                        </tr>
                        <tr>
                            <td className={tbStyles.tdData}><h4>{t(alterType)}</h4></td>
                            <td className={tbStyles.tdData}><h4>{alteredBy}</h4></td>
                            <td className={tbStyles.tdData}><h4>{createdBy}</h4></td>
                            <td className={tbStyles.tdData}><h4>{creationDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastAlterDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{version}</h4></td>
                        </tr>
                        <tr>
                            <td className={tbStyles.td}><h4>{t("lastCorrectAuthenticationDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastCorrectAuthenticationLogicalAddress")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastIncorrectAuthenticationDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastIncorrectAuthenticationLogicalAddress")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("numberOfAuthenticationFailures")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("language")}</h4></td>
                        </tr>
                        <tr>
                            <td className={tbStyles.tdData}><h4>{lastCorrectAuthenticationDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastCorrectAuthenticationLogicalAddress}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastIncorrectAuthenticationDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastIncorrectAuthenticationLogicalAddress}</h4></td>
                            <td className={tbStyles.tdData}><h4>{numberOfAuthenticationFailures}</h4></td>
                            <td className={tbStyles.tdData}><h4>{language}</h4></td>
                        </tr>
                    </Grid>
                    </Grid>
                </Grid>
                <Grid>
                    <Grid item style={{display: ChangeMail ? "none" : "block"}} className={styles.item}>
                        <h3>{t("email")}</h3>
                        <div>
                            <p>{currentAccount.email}</p>
                            <RoundedButton
                                color="blue"
                                onClick={handleChangeMail}
                            >{t("email change btn")}</RoundedButton>
                        </div>
                    </Grid>
                    <Grid item style={{display: ChangeMail ? "block" : "none"}} className={styles['change-item']}>
                        <h3>{t("email change")}</h3>
                        <div>
                            <DarkedTextField
                                type="text"
                                label={t("new email") + ' *'}
                                placeholder={t(currentAccount.email)}
                                value={email}
                                onChange={event => {
                                    setEmail(event.target.value)
                                    setEmailRegexError(!EMAIL_REGEX.test(event.target.value))
                                }}
                            regexError={emailRegexError}/>
                        </div>
                        <div>
                            <RoundedButton color="blue"
                                           onClick={handleConfirmChangeMail}
                            >{t("confirm")}</RoundedButton>
                            <RoundedButton color="green"
                                           onClick={handleMetadata}
                            >{t("metadata")}</RoundedButton>
                            <RoundedButton color="pink"
                                           onClick={handleChangeMail}
                            >{t("cancel")}</RoundedButton>
                        </div>
                        <Grid item style={{display: metadata ? "block" : "none"}} className={styles['change-item']}>
                        <tr>
                            <td className={tbStyles.td}><h4>{t("alterType")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("alteredBy")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("createdBy")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("creationDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastAlterDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("version")}</h4></td>
                        </tr>
                        <tr>
                            <td className={tbStyles.tdData}><h4>{t(alterType)}</h4></td>
                            <td className={tbStyles.tdData}><h4>{alteredBy}</h4></td>
                            <td className={tbStyles.tdData}><h4>{createdBy}</h4></td>
                            <td className={tbStyles.tdData}><h4>{creationDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastAlterDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{version}</h4></td>
                        </tr>
                        <tr>
                            <td className={tbStyles.td}><h4>{t("lastCorrectAuthenticationDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastCorrectAuthenticationLogicalAddress")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastIncorrectAuthenticationDateTime")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("lastIncorrectAuthenticationLogicalAddress")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("numberOfAuthenticationFailures")}</h4></td>
                            <td className={tbStyles.td}><h4>{t("language")}</h4></td>
                        </tr>
                        <tr>
                            <td className={tbStyles.tdData}><h4>{lastCorrectAuthenticationDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastCorrectAuthenticationLogicalAddress}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastIncorrectAuthenticationDateTime}</h4></td>
                            <td className={tbStyles.tdData}><h4>{lastIncorrectAuthenticationLogicalAddress}</h4></td>
                            <td className={tbStyles.tdData}><h4>{numberOfAuthenticationFailures}</h4></td>
                            <td className={tbStyles.tdData}><h4>{language}</h4></td>
                        </tr>
                        </Grid>
                    </Grid>
                </Grid>
                <Grid item style={{display: acLevel.includes('CLIENT') ? "block" : "none"}} className={styles.item}>
                    <Grid item style={{display: ChangAddress ? "none" : "block"}} className={styles.item}>
                        <h3>{t("address")}</h3>
                        <div>
                            <div>
                                <h4>{t("street")}</h4>
                                <p>{clientAddr ? clientAddr.address.street : ""}</p>
                            </div>
                            <div>
                                <h4>{t("house number")}</h4>
                                <p>{clientAddr ? clientAddr.address.houseNumber : ""}</p>
                            </div>
                            <div>
                                <h4>{t("postal code")}</h4>
                                <p>{clientAddr ? clientAddr.address.postalCode : ""}</p>
                            </div>
                            <div>
                                <h4>{t("city")}</h4>
                                <p>{clientAddr ? clientAddr.address.city : ""}</p>
                            </div>
                            <div>
                                <h4>{t("country")}</h4>
                                <p>{clientAddr ? clientAddr.address.country : ""}</p>
                            </div>
                            <div>
                                <h4>{t("phone number")}</h4>
                                <p>{clientAddr ? clientAddr.phoneNumber : ""}</p>
                            </div>
                            <RoundedButton color="blue"
                                           onClick={handleChangAddress}
                            >{t("address change btn")}</RoundedButton>
                        </div>

                    </Grid>
                    <Grid item style={{display: ChangAddress ? "block" : "none"}} className={styles['change-item']}>
                        <h3>{t("address change")}</h3>
                        <div>
                            <DarkedTextField
                                type="text"
                                label={t("street") + ' *'}
                                placeholder={clientAddr ? clientAddr.address.street : ""}
                                value={street}
                                onChange={event => {
                                    setStreet(event.target.value)
                                    setStreetRegexError(!STREET_REGEX.test(event.target.value))
                                }}
                                regexError={streetRegexError}/>
                            <DarkedTextField
                                type="text"
                                label={t("house number") + ' *'}
                                placeholder={clientAddr ? clientAddr.address.houseNumber : ""}
                                value={houseNumber}
                                onChange={event => {
                                    setHouseNumber(event.target.value)
                                    setHouseNumberRegexError(!HOUSE_STREET_NUMBER_REGEX.test(event.target.value))
                                }}
                                regexError={houseNumberRegexError}/>
                            <DarkedTextField
                                type="text"
                                label={t("postal code") + ' *'}
                                placeholder={clientAddr ? clientAddr.address.postalCode : ""}
                                value={postalCode}
                                onChange={event => {
                                    setPostalCode(event.target.value)
                                    setCityRegexError(!CITY_REGEX.test(event.target.value))
                                }}
                                regexError={postalCodeRegexError}/>
                            <DarkedTextField
                                type="text"
                                label={t("city") + ' *'}
                                placeholder={clientAddr ? clientAddr.address.city : ""}
                                value={city}
                                onChange={event => {
                                    setCity(event.target.value)
                                    setPostalCodeRegexError(!POST_CODE_REGEX.test(event.target.value))
                                }}
                                regexError={cityRegexError}/>
                            <DarkedTextField
                                type="text"
                                label={t("country") + ' *'}
                                placeholder={clientAddr ? clientAddr.address.country : ""}
                                value={country}
                                onChange={event => {
                                    setCountry(event.target.value)
                                    setCountryRegexError(!COUNTRY_REGEX.test(event.target.value))
                                }}
                                regexError={countryRegexError}/>
                            <DarkedTextField
                                type="text"
                                label={t("phone number") + ' *'}
                                placeholder={clientAddr ? clientAddr.phoneNumber : ""}
                                value={phoneNumber}
                                onChange={event => {
                                    setPhoneNumber(event.target.value)
                                    setPhoneNumberRegexError(!PHONE_NUMBER_REGEX.test(event.target.value))
                                }}
                                regexError={phoneNumberRegexError}/>
                        </div>
                        <RoundedButton
                            color="blue"
                            onClick={handleConfirmChangeAddress}
                        >{t("confirm")}</RoundedButton>
                        <RoundedButton color="green"
                                       onClick={handleMetadata}
                        >{t("metadata")}</RoundedButton>
                        <RoundedButton
                        color="pink"
                        onClick={handleChangAddress}
                    >{t("cancel")}</RoundedButton>
                    <Grid item style={{display: metadata ? "block" : "none"}} className={styles['change-item']}>
                    <tr>
                        <td className={tbStyles.td}><h4>{t("alterType")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("alteredBy")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("createdBy")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("creationDateTime")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("lastAlterDateTime")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("version")}</h4></td>
                    </tr>
                    <tr>
                        <td className={tbStyles.tdData}><h4>{t(alterTypeAdr)}</h4></td>
                        <td className={tbStyles.tdData}><h4>{alteredByAdr}</h4></td>
                        <td className={tbStyles.tdData}><h4>{createdByAdr}</h4></td>
                        <td className={tbStyles.tdData}><h4>{creationDateTimeAdr}</h4></td>
                        <td className={tbStyles.tdData}><h4>{lastAlterDateTimeAdr}</h4></td>
                        <td className={tbStyles.tdData}><h4>{versionAdr}</h4></td>
                    </tr>
                    </Grid>
                    </Grid>
                </Grid>
                <Grid item style={{display: acLevel.includes('BUSINESS_WORKER') ? "block" : "none"}}
                      className={styles.item}>
                    <Grid item style={{display: ChangePhone ? "none" : "block"}} className={styles.item}>
                        <div>
                            <div>
                                <h4>{t("phone number")}</h4>
                                <p>{businnesPhone ? businnesPhone.phoneNumber : ""}</p>
                            </div>
                            <RoundedButton color="blue"
                                           onClick={handleChangePhone}
                            >{t("phone change btn")}</RoundedButton>
                        </div>

                    </Grid>
                    <Grid item style={{display: ChangePhone ? "block" : "none"}} className={styles['change-item']}>
                        <h4>{t("phone number")}</h4>
                        <div>
                            <DarkedTextField
                                type="text"
                                label={t("phone number") + ' *'}
                                placeholder={businnesPhone ? businnesPhone.phoneNumber : ""}
                                value={businessPhoneNumber}
                                onChange={event => {
                                    setBusinessPhoneNumber(event.target.value)
                                    setBusinnesPhoneRegexError(!PHONE_NUMBER_REGEX.test(event.target.value))
                                }}
                                regexError={businnesPhoneRegexError}/>
                        </div>
                        <RoundedButton
                            color="blue"
                            onClick={handleConfirmChangePhone}
                        >{t("confirm")}</RoundedButton>
                        <RoundedButton color="green"
                                       onClick={handleMetadata}
                        >{t("metadata")}</RoundedButton>
                        <RoundedButton
                            color="pink"
                            onClick={handleChangePhone}
                        >{t("cancel")}</RoundedButton>
                    <Grid item style={{display: metadata ? "block" : "none"}} className={styles['change-item']}>
                    <tr>
                        <td className={tbStyles.td}><h4>{t("alterType")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("alteredBy")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("createdBy")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("creationDateTime")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("lastAlterDateTime")}</h4></td>
                        <td className={tbStyles.td}><h4>{t("version")}</h4></td>
                    </tr>
                    <tr>
                        <td className={tbStyles.tdData}><h4>{t(alterTypeAcl)}</h4></td>
                        <td className={tbStyles.tdData}><h4>{alteredByAcl}</h4></td>
                        <td className={tbStyles.tdData}><h4>{createdByAcl}</h4></td>
                        <td className={tbStyles.tdData}><h4>{creationDateTimeAcl}</h4></td>
                        <td className={tbStyles.tdData}><h4>{lastAlterDateTimeAcl}</h4></td>
                        <td className={tbStyles.tdData}><h4>{versionAcl}</h4></td>
                    </tr>
                    </Grid>
                </Grid>
                <Grid item>
                    <Link to="/accounts">
                        <RoundedButton color="pink">
                            {t("go back")}
                        </RoundedButton>
                    </Link>
                </Grid>
            </Grid>
            <PopupAcceptAction
                open={buttonPopupAcceptChangeAddress}
                onConfirm={changeAddress}
                onCancel={() => {
                    setButtonPopupAcceptChangeAddress(false)
                }}
            />
            <PopupAcceptAction
                open={buttonPopupAcceptChangeNumber}
                onConfirm={changeBusinessPhone}
                onCancel={() => {
                    setButtonPopupAcceptChangeNumber(false)
                }}
            />
            <PopupAcceptAction
                open={buttonPopupAcceptChangeData}
                onConfirm={changePersonalData}
                onCancel={() => {
                    setButtonPopupAcceptChangeData(false)
                }}
            />
            <PopupAcceptAction
                open={buttonPopupAcceptChangeMail}
                onConfirm={changeMail}
                onCancel={() => {
                    setButtonPopupAcceptChangeMail(false)
                }}
            />
        </>
    )
}