import Grid from "@material-ui/core/Grid";
import styles from "../../styles/ManageAccount.module.css";
import tbStyles from "../../styles/mtdTable.module.css"
import RoundedButton from "../RoundedButton";
import DarkedTextField from "../DarkedTextField";
import React, {createRef, useEffect, useReducer, useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {changeEmail as changeEmailAction, selectEmail} from "../../redux/slices/userSlice";
import {changeEmail as changeEmailService} from "../../Services/changeEmailService";
import {useTranslation} from "react-i18next";
import {ConfirmMetadataCancelButtonGroup} from "../ConfirmMetadataCancelButtonGroup";
import {ChangeDataComponentProps} from '../interfaces'
import Recaptcha from "react-recaptcha";
import Popup from "../../PopupRecaptcha";
import {useSnackbarQueue} from "../../pages/snackbar";
import useHandleError from "../../errorHandler";
import PopupAcceptAction from "../../PopupAcceptAction";
import {EMAIL_REGEX, PASSWORD_REGEX} from "../../regexConstants";

export default function ChangeEmail({open, onOpen, onConfirm, onCancel}: ChangeDataComponentProps) {
    // i18n
    const {t} = useTranslation()
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    // redux
    const dispatch = useDispatch()
    const email = useSelector(selectEmail)
    const currentSelfMTD = JSON.parse(sessionStorage.getItem("changeSelfAccountDataMta") as string)
    const [metadata, setMetadata] = useState(false)


    const [emailValue, setEmailValue] = useState('')
    const [emailConfirmValue, setEmailConfirmValue] = useState('')

    const [emailRegexError, setEmailRegexError] = useState(false)
    const [emailConfirmRegexError, setEmailConfirmRegexError] = useState(false)

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
    const [language, setLanguage] = useState('')
    const handleCancel = () => {
        setEmailValue('')
        setEmailConfirmValue('')
        setMetadata(false)
        onCancel()
    }

    const handleMetadata = () => {
        setMetadata(state => !state)
    }

    const handleConfirm = async () => {
        setEmailRegexError(!EMAIL_REGEX.test(emailValue))
        setEmailConfirmRegexError(!EMAIL_REGEX.test(emailConfirmValue))

        if(!EMAIL_REGEX.test(emailValue) || !EMAIL_REGEX.test(emailConfirmValue)) {
            handleError('error.fields')
            return
        } else if (emailValue != emailConfirmValue) {
            setEmailRegexError(true);
            setEmailConfirmRegexError(true);
            handleError("emails are not equal")
            return
        } else {
            setButtonPopupAcceptAction(true)
        }
    }

    const [buttonPopup, setButtonPopup] = useState(false);

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

    function verifyCallback(){
        setButtonPopup(false)

        changeEmailService(emailValue).then(res => {
            onConfirm()
            showSuccess(t('successful action'))
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
            if (message == 'error.database.emailReserved') {
                setEmailRegexError(true)
                setEmailConfirmRegexError(true)
            }
            for (let messageArray of Object.values(message.errors)) {
                for (const error of messageArray as Array<String>) {
                    switch(error) {
                        case 'error.regex.email':
                            setEmailRegexError(true)
                            setEmailConfirmRegexError(true)
                            break
                    }
                }
            }
        })
    }

    const changeEmail = () => {
        setButtonPopup(true)
        setButtonPopupAcceptAction(false)
    }

    useEffect(() => {
        setAlterType(currentSelfMTD.alterType);
        setAlteredBy(currentSelfMTD.alteredBy);
        setCreatedBy(currentSelfMTD.createdBy);
        if(currentSelfMTD.creationDateTime !=null)
            setCreationDateTime(currentSelfMTD.creationDateTime.dayOfMonth +" "+ t(currentSelfMTD.creationDateTime.month) +" "+ currentSelfMTD.creationDateTime.year +" "+ currentSelfMTD.creationDateTime.hour +":"+ currentSelfMTD.creationDateTime.minute.toString().padStart(2, '0') )
        if(currentSelfMTD.lastAlterDateTime !=null)
            setLastAlterDateTime(currentSelfMTD.lastAlterDateTime.dayOfMonth +" "+ t(currentSelfMTD.lastAlterDateTime.month) +" "+ currentSelfMTD.lastAlterDateTime.year +" "+ currentSelfMTD.lastAlterDateTime.hour +":"+ currentSelfMTD.lastAlterDateTime.minute.toString().padStart(2, '0'));
        if(currentSelfMTD.lastCorrectAuthenticationDateTime !=null)
            setLastCorrectAuthenticationDateTime(currentSelfMTD.lastCorrectAuthenticationDateTime.dayOfMonth +" "+ t(currentSelfMTD.lastCorrectAuthenticationDateTime.month) +" "+ currentSelfMTD.lastCorrectAuthenticationDateTime.year +" "+ currentSelfMTD.lastCorrectAuthenticationDateTime.hour +":"+ currentSelfMTD.creationDateTime.minute.toString().padStart(2, '0'));
        setLastCorrectAuthenticationLogicalAddress(currentSelfMTD.lastCorrectAuthenticationLogicalAddress)
        if(currentSelfMTD.lastIncorrectAuthenticationDateTime !=null)
            setLastIncorrectAuthenticationDateTime(currentSelfMTD.lastIncorrectAuthenticationDateTime.dayOfMonth +" "+ t(currentSelfMTD.lastIncorrectAuthenticationDateTime.month) +" "+ currentSelfMTD.lastIncorrectAuthenticationDateTime.year +" "+ currentSelfMTD.lastIncorrectAuthenticationDateTime.hour +":"+ currentSelfMTD.lastIncorrectAuthenticationDateTime.minute.toString().padStart(2, '0'));
        setLastIncorrectAuthenticationLogicalAddress(currentSelfMTD.lastIncorrectAuthenticationLogicalAddress);
        setNumberOfAuthenticationFailures(currentSelfMTD.numberOfAuthenticationFailures)
        setVersion(currentSelfMTD.version);
        setLanguage(currentSelfMTD.languageType);

    }, [])

    return (
        <>
            <Grid item  style={{display: open ? "none" : "block"}} className={styles.item}>
                <h3>{t("email")}</h3>
                <div>
                    <p>{email}</p>
                    <RoundedButton
                        color="blue"
                        onClick={onOpen}
                    >{t("email change btn")}</RoundedButton>
                </div>
            </Grid>
            <Grid item  style={{display: open ? "block" : "none"}} className={styles['change-item']}>
                <h3>{t("email change")}</h3>
                <div>
                    <DarkedTextField
                        type="email"
                        label={t("new email") + ' *'}
                        placeholder={t("new email")}
                        value={emailValue}
                        onChange={event => {
                            setEmailValue(event.target.value)
                            if (EMAIL_REGEX.test(emailConfirmValue)) {
                                setEmailConfirmRegexError(false)
                            }
                            if (!EMAIL_REGEX.test(event.target.value)) {
                                setEmailRegexError(!EMAIL_REGEX.test(event.target.value))
                            } else {
                                if (event.target.value != emailConfirmValue && EMAIL_REGEX.test(emailConfirmValue)) {
                                    setEmailRegexError(true)
                                } else {
                                    setEmailRegexError(false)
                                }
                            }
                        }}
                        regexError={emailRegexError}/>
                    <DarkedTextField
                        type="text"
                        label={t("new email confirm") + ' *'}
                        placeholder={t("new email confirm")}
                        value={emailConfirmValue}
                        onChange={event => {
                            setEmailConfirmValue(event.target.value)
                            if (EMAIL_REGEX.test(emailValue)) {
                                setEmailRegexError(false)
                            }
                            if (!EMAIL_REGEX.test(event.target.value)) {
                                setEmailConfirmRegexError(!EMAIL_REGEX.test(event.target.value))
                            } else {
                                if (event.target.value != emailValue && EMAIL_REGEX.test(emailValue)) {
                                    setEmailConfirmRegexError(true)
                                } else {
                                    setEmailConfirmRegexError(false)
                                }
                            }
                        }}
                        regexError={emailConfirmRegexError}/>
                </div>
                <Popup trigger={buttonPopup} setTrigger={setButtonPopup}>
                    <div>
                        <Recaptcha
                            sitekey={process.env.REACT_APP_SECRET_NAME}
                            size="normal"
                            verifyCallback={verifyCallback}
                        />
                    </div>
                </Popup>
                <PopupAcceptAction
                    open={buttonPopupAcceptAction}
                    onConfirm={changeEmail}
                    onCancel={() => {setButtonPopupAcceptAction(false)
                    }}
                />
                <ConfirmMetadataCancelButtonGroup
                    onConfirm={handleConfirm}
                    onPress={handleMetadata}
                    onCancel={handleCancel} />
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
        </>
    )
}