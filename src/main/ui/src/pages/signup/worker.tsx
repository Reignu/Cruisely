import {Link, useHistory} from 'react-router-dom'

import Box from '@material-ui/core/Box'
import EmailIcon from '@material-ui/icons/AlternateEmail'
import PasswordIcon from '@material-ui/icons/VpnKeyRounded'

import AuthLayout from '../../layouts/AuthLayout'
import DarkedTextField from '../../components/DarkedTextField'
import DarkedSelect from '../../components/DarkedSelect'
import RoundedButton from '../../components/RoundedButton'
import styles from '../../styles/auth.global.module.css'

import {useTranslation} from 'react-i18next'
import {useEffect, useState} from "react";
import axios from "../../Services/URL";
import Recaptcha from "react-recaptcha";
import Popup from "../../PopupRecaptcha";
import {EMAIL_REGEX, LOGIN_REGEX, NAME_REGEX, PASSWORD_REGEX, PHONE_NUMBER_REGEX} from "../../regexConstants";
import {useSnackbarQueue} from "../snackbar";
import i18n from "i18next";
import useHandleError from "../../errorHandler";
import PopupAcceptAction from "../../PopupAcceptAction";


export default function WorkerSignUp() {
    const {t} = useTranslation()
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')

    const history = useHistory();
    const [firstName, setFirstName] = useState('')
    const [secondName, setSecondName] = useState('')
    const [login, setLogin] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [passwordConfirm, setPasswordConfirm] = useState('')
    const [languageType, setLanguageType] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')

    const [company, setCompany] = useState("");

    const [companiesList, setCompaniesList] = useState([]);

    const [buttonPopup, setButtonPopup] = useState(false);

    const [loginRegexError, setLoginRegexError] = useState(false)
    const [passwordRegexError, setPasswordRegexError] = useState(false)
    const [passwordConfirmRegexError, setPasswordConfirmRegexError] = useState(false)
    const [firstNameRegexError, setFirstNameRegexError] = useState(false)
    const [secondNameRegexError, setSecondNameRegexError] = useState(false)
    const [emailRegexError, setEmailRegexError] = useState(false)
    const [phoneNumberRegexError, setPhoneNumberRegexError] = useState(false)

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);
    const [isAccepted, setIsAccepted] = useState(false);

    const verifyCallback = () => {
        handleConfirm()
    }

    const handleConfirm = () => {
        const json = JSON.stringify({
            firstName,
            secondName,
            login,
            email,
            password,
            languageType: i18n.language.toUpperCase(),
            phoneNumber,
            companyName: company
        });
        axios.post('auth/business-worker/registration', json, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => {
            setButtonPopup(false)
            history.push('/')
            showSuccess(t('activate.your.account'))
        }).catch(error => {
            setButtonPopup(false)
            const message = error.response.data
            handleError(message, error.response.status)
            switch (message) {
                case 'error.database.loginReserved':
                    setLoginRegexError(true)
                    break
                case 'error.database.emailReserved':
                    setEmailRegexError(true)
                    break
            }
        });
    }

    useEffect(() => {
        const getCompaniesList = async () => {
            const {data} = await axios.get('company/companies-info', {});
            setCompaniesList(data.map((comp: { name: string }) => comp.name))
        }
        getCompaniesList()
    }, [company]);


    const workerSignUpFun = async () => {
        setLoginRegexError(!LOGIN_REGEX.test(login))
        setPasswordRegexError(!PASSWORD_REGEX.test(password))
        setPasswordConfirmRegexError(!PASSWORD_REGEX.test(passwordConfirm))
        setFirstNameRegexError(!NAME_REGEX.test(firstName))
        setSecondNameRegexError(!NAME_REGEX.test(secondName))
        setEmailRegexError(!EMAIL_REGEX.test(email))
        setPhoneNumberRegexError(!PHONE_NUMBER_REGEX.test(phoneNumber))

        if (!LOGIN_REGEX.test(login) || !PASSWORD_REGEX.test(password) || !PASSWORD_REGEX.test(passwordConfirm) ||
            !NAME_REGEX.test(firstName) || !NAME_REGEX.test(secondName) || !EMAIL_REGEX.test(email) ||
            !PHONE_NUMBER_REGEX.test(phoneNumber)) {
            handleError("invalid.form")
        } else if (password != passwordConfirm) {
            handleError("passwords are not equal")
        } else {
            setButtonPopupAcceptAction(true)
        }
    }


    return (
        <AuthLayout>
            <h1 className={styles.h1}>{t("signup.welcome")}</h1>
            <h2 className={styles.h2}>{t("signup.worker.subtitle")}</h2>

            <Box
                style={{
                    display: "flex",
                    width: '100%',
                }}
            >
                <DarkedTextField
                    label={t("name") + ' *'}
                    placeholder={t("nameExample")}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    value={firstName}
                    onChange={event => {
                        setFirstName(event.target.value)
                        setFirstNameRegexError(!NAME_REGEX.test(event.target.value))
                    }}
                    colorIgnored
                    regexError={firstNameRegexError}
                />


                <DarkedTextField
                    label={t("surname") + ' *'}
                    placeholder={t("surnameExample")}
                    className={styles.input}
                    value={secondName}
                    onChange={event => {
                        setSecondName(event.target.value)
                        setSecondNameRegexError(!NAME_REGEX.test(event.target.value))
                    }}
                    colorIgnored
                    regexError={secondNameRegexError}
                />
            </Box>

            <Box
                style={{
                    display: "flex",
                    width: '100%',
                    padding: 0
                }}
            >
                <DarkedSelect
                    label={t("company") + ' *'}
                    // options={["Firma 1", "Firma 2", "FirmaJez", "Firma 4", "Firma 5"]}
                    options={companiesList}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    onSelectedChange={setCompany}
                    colorIgnored
                />

                <DarkedTextField
                    type="email"
                    label={t("email") + ' *'}
                    placeholder={t("emailExample")}
                    className={styles.input}
                    icon={(<EmailIcon/>)}
                    value={email}
                    onChange={event => {
                        setEmail(event.target.value)
                        setEmailRegexError(!EMAIL_REGEX.test(event.target.value))
                    }}
                    colorIgnored
                    regexError={emailRegexError}
                />


            </Box>


            <Box
                style={{
                    display: "flex",
                    width: '100%',
                    padding: 0
                }}
            >

                <DarkedTextField
                    label={t("login") + ' *'}
                    placeholder={t("login")}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    value={login}
                    onChange={event => {
                        setLogin(event.target.value)
                        setLoginRegexError(!LOGIN_REGEX.test(event.target.value))
                    }}
                    colorIgnored
                    regexError={loginRegexError}
                />

                <DarkedTextField
                    label={t("phoneNumber") + ' *'}
                    placeholder={t("phoneNumberExample")}
                    className={styles.input}
                    value={phoneNumber}
                    onChange={event => {
                        setPhoneNumber(event.target.value)
                        setPhoneNumberRegexError(!PHONE_NUMBER_REGEX.test(event.target.value))
                    }}
                    colorIgnored
                    regexError={phoneNumberRegexError}
                />

            </Box>

            <Box
                style={{
                    display: "flex",
                    width: '100%',
                    padding: 0
                }}
            >
            </Box>

            <Box
                style={{
                    display: "flex",
                    width: '100%',
                    padding: 0
                }}
            >
                <DarkedTextField
                    type="password"
                    label={t("password") + ' *'}
                    placeholder="1234567890"
                    className={styles.input}
                    style={{marginRight: 20}}
                    icon={(<PasswordIcon/>)}
                    value={password}
                    onChange={event => {
                        setPassword(event.target.value)
                        if (PASSWORD_REGEX.test(passwordConfirm)) {
                            setPasswordConfirmRegexError(false)
                        }
                        if (!PASSWORD_REGEX.test(event.target.value)) {
                            setPasswordRegexError(!PASSWORD_REGEX.test(event.target.value))
                        } else {
                            if (event.target.value != passwordConfirm && PASSWORD_REGEX.test(passwordConfirm)) {
                                setPasswordRegexError(true)
                            } else {
                                setPasswordRegexError(false)
                            }
                        }
                    }}
                    colorIgnored
                    regexError={passwordRegexError}
                />

                <DarkedTextField
                    type="password"
                    label={t("confirm password") + ' *'}
                    placeholder="1234567890"
                    className={styles.input}
                    icon={(<PasswordIcon/>)}
                    value={passwordConfirm}
                    onChange={event => {
                        setPasswordConfirm(event.target.value)
                        if (PASSWORD_REGEX.test(password)) {
                            setPasswordRegexError(false);
                        }
                        if (!PASSWORD_REGEX.test(event.target.value)) {
                            setPasswordConfirmRegexError(!PASSWORD_REGEX.test(event.target.value))
                        } else {
                            if (event.target.value != password && PASSWORD_REGEX.test(password)) {
                                setPasswordConfirmRegexError(true)
                            } else {
                                setPasswordConfirmRegexError(false)
                            }
                        }
                    }}
                    colorIgnored
                    regexError={passwordConfirmRegexError}
                    />
            </Box>

            <Box
                style={{
                    width: '100%',
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "flex-end",
                    justifyContent: "space-between"
                }}
            >
                <RoundedButton
                    onClick={workerSignUpFun}
                    style={{width: '50%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                    color="pink"
                >{t("signup")}</RoundedButton>
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
                    onConfirm={() => {
                        setButtonPopup(true)
                        setButtonPopupAcceptAction(false)
                    }}
                    onCancel={() => {
                        setButtonPopupAcceptAction(false)
                    }}
                />
                <Link to="client">
                    <a className={styles.link}>{t("i am a client")}</a>
                </Link>
                <Link to="../signin">
                    <a className={styles.link}>{t("i have an account")}</a>
                </Link>
            </Box>
        </AuthLayout>
    )
}