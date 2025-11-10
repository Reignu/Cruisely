import {useTranslation} from "react-i18next";
import {useSelector} from "react-redux";
import {selectFirstName, selectSecondName} from "../../redux/slices/userSlice";
import React, {useEffect, useState} from "react";
import Grid from "@material-ui/core/Grid";
import styles from "../../styles/ManageAccount.module.css";
import tbStyles from "../../styles/mtdTable.module.css"
import RoundedButton from "../RoundedButton";
import DarkedTextField from "../DarkedTextField";
import {ChangeDataComponentProps} from '../interfaces'
import {ConfirmMetadataCancelButtonGroup} from "../ConfirmMetadataCancelButtonGroup";
import {changeAdministratorData} from "../../Services/changeDataService";
import Recaptcha from "react-recaptcha";
import Popup from "../../PopupRecaptcha";
import {useSnackbarQueue} from "../../pages/snackbar";
import useHandleError from "../../errorHandler";
import PopupAcceptAction from "../../PopupAcceptAction";
import {NAME_REGEX, PHONE_NUMBER_REGEX} from "../../regexConstants";

export default function ChangeAdministratorData({open, onOpen, onConfirm, onCancel}: ChangeDataComponentProps) {
    const {t} = useTranslation()

    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const currentSelfMTD = JSON.parse(sessionStorage.getItem("changeSelfAccountDataMta") as string)

    const firstName = useSelector(selectFirstName)
    const secondName = useSelector(selectSecondName)

    const [buttonPopup, setButtonPopup] = useState(false);

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);
    const [metadata, setMetadata] = useState(false)

    const [firstNameValue, setFirstNameValue] = useState(firstName)
    const [secondNameValue, setSecondNameValue] = useState(secondName)

    const [firstNameRegexError, setFirstNameRegexError] = useState(false)
    const [secondNameRegexError, setSecondNameRegexError] = useState(false)

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

    const handleErase = () => {
        setFirstNameValue(firstName)
        setSecondNameValue(secondName)
    }

    const handleCancel = () => {
        handleErase()
        setMetadata(false)
        onCancel()
    }

    const handleMetadata = () => {
        setMetadata(state => !state)
    }

    const handleConfirm = async () => {
        setFirstNameRegexError(!NAME_REGEX.test(firstNameValue))
        setSecondNameRegexError(!NAME_REGEX.test(secondNameValue))
        if (!NAME_REGEX.test(firstNameValue) || !NAME_REGEX.test(secondNameValue)) {
            handleError('error.fields')
            return
        } else {
            setButtonPopupAcceptAction(true)
        }
    }

    function verifyCallback() {
        setButtonPopup(false)

        changeAdministratorData(firstNameValue, secondNameValue).then(res => {
            showSuccess(t('successful action'))
            onConfirm()
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
            for (let messageArray of Object.values(message.errors)) {
                for (const error of messageArray as Array<String>) {
                    switch(error) {
                        case 'error.size.firstName':
                        case 'error.regex.firstName':
                            setFirstNameRegexError(true)
                            break
                        case 'error.size.secondName':
                        case 'error.regex.secondName':
                            setSecondNameRegexError(true)
                            break
                    }
                }
            }
        });
    }

    useEffect(() => {
        setFirstNameValue(firstName)
        setSecondNameValue(secondName)

        setAlterType(currentSelfMTD.alterType);
        setAlteredBy(currentSelfMTD.alteredBy);
        setCreatedBy(currentSelfMTD.createdBy);
        if(currentSelfMTD.creationDateTime !=null)
            setCreationDateTime(currentSelfMTD.creationDateTime.dayOfMonth +" "+ t(currentSelfMTD.creationDateTime.month) +" "+ currentSelfMTD.creationDateTime.year +" " + currentSelfMTD.creationDateTime.hour +":"+ currentSelfMTD.creationDateTime.minute.toString().padStart(2, '0') )
        if(currentSelfMTD.lastAlterDateTime !=null)
            setLastAlterDateTime(currentSelfMTD.lastAlterDateTime.dayOfMonth +" "+ t(currentSelfMTD.lastAlterDateTime.month) +" "+ currentSelfMTD.lastAlterDateTime.year +" "+ currentSelfMTD.lastAlterDateTime.hour +":"+ currentSelfMTD.lastAlterDateTime.minute.toString().padStart(2, '0'));
        if(currentSelfMTD.lastCorrectAuthenticationDateTime !=null)
            setLastCorrectAuthenticationDateTime(currentSelfMTD.lastCorrectAuthenticationDateTime.dayOfMonth +" "+ t(currentSelfMTD.lastCorrectAuthenticationDateTime.month) +" "+ currentSelfMTD.lastCorrectAuthenticationDateTime.year +" "+ currentSelfMTD.lastCorrectAuthenticationDateTime.hour +":"+ currentSelfMTD.creationDateTime.minute.toString().padStart(2, '0'));
        setLastCorrectAuthenticationLogicalAddress(currentSelfMTD.lastCorrectAuthenticationLogicalAddress)
        if(currentSelfMTD.lastIncorrectAuthenticationDateTime !=null)
            setLastIncorrectAuthenticationDateTime(currentSelfMTD.lastIncorrectAuthenticationDateTime.dayOfMonth +" "+ t(currentSelfMTD.lastIncorrectAuthenticationDateTime.month) + " " + currentSelfMTD.lastIncorrectAuthenticationDateTime.year +" "+ currentSelfMTD.lastIncorrectAuthenticationDateTime.hour +":"+ currentSelfMTD.lastIncorrectAuthenticationDateTime.minute.toString().padStart(2, '0'));
        setLastIncorrectAuthenticationLogicalAddress(currentSelfMTD.lastIncorrectAuthenticationLogicalAddress);
        setNumberOfAuthenticationFailures(currentSelfMTD.numberOfAuthenticationFailures)
        setVersion(currentSelfMTD.version);
        setLanguage(currentSelfMTD.languageType);
    }, [])

    const changeData = () => {
        setButtonPopup(true)
        setButtonPopupAcceptAction(false)
    }

    return (
        <>
            <Grid item style={{display: open ? "none" : "block"}} className={styles.item}>
                <h3>{t("personal data")}</h3>
                <div>
                    <div>
                        <h4>{t("name")}</h4>
                        <p>{firstName}</p>
                    </div>
                    <div>
                        <h4>{t("surname")}</h4>
                        <p>{secondName}</p>
                    </div>

                    <RoundedButton
                        color="blue"
                        onClick={onOpen}
                    >{t("personal data change btn")}</RoundedButton>
                </div>
            </Grid>

            <Grid item style={{display: open ? "block" : "none"}} className={styles['change-item']}>
                <h3>{t("personal data change")}</h3>
                <div>
                    <DarkedTextField
                        type="text"
                        label={t("name") + ' *'}
                        placeholder={t("nameExample")}
                        value={firstNameValue}
                        onChange={event => {
                            setFirstNameValue(event.target.value)
                            setFirstNameRegexError(!NAME_REGEX.test(event.target.value))
                        }}
                        regexError={firstNameRegexError}/>
                    <DarkedTextField
                        type="text"
                        label={t("surname") + ' *'}
                        placeholder={t("surnameExample")}
                        value={secondNameValue}
                        onChange={event => {
                            setSecondNameValue(event.target.value)
                            setSecondNameRegexError(!NAME_REGEX.test(event.target.value))
                        }}
                        regexError={secondNameRegexError}/>
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
                    onConfirm={changeData}
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