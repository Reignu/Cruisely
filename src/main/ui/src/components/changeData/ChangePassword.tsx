import {ChangeDataComponentProps} from "../interfaces";
import Grid from "@material-ui/core/Grid";
import styles from "../../styles/ManageAccount.module.css";
import tbStyles from "../../styles/mtdTable.module.css"
import RoundedButton from "../RoundedButton";
import DarkedTextField from "../DarkedTextField";
import React, {useEffect, useState} from "react";
import {changeOwnPassword as changeOwnPasswordService} from "../../Services/changePasswordService";
import {useTranslation} from "react-i18next";
import {ConfirmMetadataCancelButtonGroup} from "../ConfirmMetadataCancelButtonGroup";
import Recaptcha from "react-recaptcha";
import Popup from "../../PopupRecaptcha";
import {useSnackbarQueue} from "../../pages/snackbar";
import PopupAcceptAction from "../../PopupAcceptAction";
import useHandleError from "../../errorHandler";
import {EMAIL_REGEX, PASSWORD_REGEX} from "../../regexConstants";

export default function ChangePassword({open, onOpen, onConfirm, onCancel}: ChangeDataComponentProps) {
    const {t} = useTranslation()

    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')

    const currentSelfMTD = JSON.parse(sessionStorage.getItem("changeSelfAccountDataMta") as string)
    const [oldPassword, setOldPassword] = useState('')
    const [newPassword, setNewPassword] = useState('')
    const [newPasswordConfirm, setNewPasswordConfirm] = useState('')

    const [oldPasswordRegexError, setOldPasswordRegexError] = useState(false)
    const [newPasswordRegexError, setNewPasswordRegexError] = useState(false)
    const [newPasswordConfirmRegexError, setNewPasswordConfirmRegexError] = useState(false)

    const [buttonPopup, setButtonPopup] = useState(false);
    const [metadata, setMetadata] = useState(false)

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

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
        setOldPassword('')
        setNewPassword('')
        setNewPasswordConfirm('')
        setMetadata(false)
        onCancel()
    }

    const handleMetadata = () => {
        setMetadata(state => !state)
    }

    const handleConfirm = async () => {
        setOldPasswordRegexError(!PASSWORD_REGEX.test(oldPassword))
        setNewPasswordRegexError(!PASSWORD_REGEX.test(newPassword))
        setNewPasswordConfirmRegexError(!PASSWORD_REGEX.test(newPasswordConfirm))

        if(!PASSWORD_REGEX.test(oldPassword) || !PASSWORD_REGEX.test(newPassword) || !PASSWORD_REGEX.test(newPasswordConfirm)) {
            handleError('error.fields')
            return
        } else if (newPassword != newPasswordConfirm) {
            setNewPasswordRegexError(true);
            setNewPasswordConfirmRegexError(true);
            handleError("passwords are not equal")
            return
        } else {
            setButtonPopupAcceptAction(true)
        }
    }

    async function verifyCallback() {
        setButtonPopup(false)

        changeOwnPasswordService(oldPassword, newPassword).then(res => {
            setNewPasswordConfirm('')
            onConfirm()
            showSuccess(t('successful action'))
        }).catch(error => {
            const message = error.response.data
            try {
                handleError(JSON.parse(message),error.response.status);
            } catch (e) {
                handleError(message, error.response.status)
            }
            switch (message) {
                case 'error.password.change.oldPasswordError':
                    setOldPasswordRegexError(true)
                    break
                case 'error.password.change.newAndOldPasswordAreTheSameError':
                    setNewPasswordRegexError(true)
                    setNewPasswordConfirmRegexError(true)
            }
        })
    }


    const changePassword = async () => {
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
            <Grid item style={{display: open ? "none" : "block"}} className={styles.item}>
                <RoundedButton color="blue"
                               onClick={onOpen}
                >{t("password change btn")}</RoundedButton>
            </Grid>
            <Grid item style={{display: open ? "block" : "none"}} className={styles['change-item']}>
                <h3>{t("password change")}</h3>
                <div>
                    <DarkedTextField
                        type="password"
                        label={t("old password") + ' *'}
                        placeholder={t("old password")}
                        value={oldPassword}
                        onChange={event => {
                            setOldPassword(event.target.value)
                            setOldPasswordRegexError(!PASSWORD_REGEX.test(event.target.value))
                        }}
                        regexError={oldPasswordRegexError}/>

                    <DarkedTextField
                        type="password"
                        label={t("new password") + ' *'}
                        placeholder={t("new password")}
                        value={newPassword}
                        onChange={event => {
                            setNewPassword(event.target.value)
                            if (PASSWORD_REGEX.test(newPasswordConfirm)) {
                                setNewPasswordConfirmRegexError(false)
                            }
                            if (!PASSWORD_REGEX.test(event.target.value)) {
                                setNewPasswordRegexError(!PASSWORD_REGEX.test(event.target.value))
                            } else {
                                if (event.target.value != newPasswordConfirm && PASSWORD_REGEX.test(newPasswordConfirm)) {
                                    setNewPasswordRegexError(true)
                                } else {
                                    setNewPasswordRegexError(false)
                                }
                            }
                        }}
                        regexError={newPasswordRegexError}/>

                    <DarkedTextField
                        type="password"
                        label={t("new password confirm") + ' *'}
                        placeholder={t("new password confirm")}
                        value={newPasswordConfirm}
                        onChange={event => {
                            setNewPasswordConfirm(event.target.value)
                            if (PASSWORD_REGEX.test(newPassword)) {
                                setNewPasswordRegexError(false);
                            }
                            if (!PASSWORD_REGEX.test(event.target.value)) {
                                setNewPasswordConfirmRegexError(!PASSWORD_REGEX.test(event.target.value))
                            } else {
                                if (event.target.value != newPassword && PASSWORD_REGEX.test(newPassword)) {
                                    setNewPasswordConfirmRegexError(true)
                                } else {
                                    setNewPasswordConfirmRegexError(false)
                                }
                            }
                        }}
                        regexError={newPasswordConfirmRegexError}/>
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
                    onConfirm={changePassword}
                    onCancel={() => {
                        setButtonPopupAcceptAction(false)
                    }}
                />
                <ConfirmMetadataCancelButtonGroup
                    onConfirm={handleConfirm}
                    onPress={handleMetadata}
                    onCancel={handleCancel}/>
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