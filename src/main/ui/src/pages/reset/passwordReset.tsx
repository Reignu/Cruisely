import {useLocation} from 'react-router-dom';

import React, {useState} from 'react';
import axios from "../../Services/URL";
import AuthLayout from "../../layouts/AuthLayout";
import DarkedTextField from "../../components/DarkedTextField";
import {useTranslation} from "react-i18next";
import PasswordIcon from "@material-ui/icons/VpnKeyRounded";
import styles from '../../styles/auth.global.module.css'
import RoundedButton from "../../components/RoundedButton";
import {useSnackbarQueue} from "../snackbar";
import {refreshToken} from "../../Services/userService";
import useHandleError from "../../errorHandler";
import PopupAcceptAction from "../../PopupAcceptAction";
import {LOGIN_REGEX, PASSWORD_REGEX} from "../../regexConstants";


function PasswordReset(props: any) {
    const location = useLocation();
    const {t} = useTranslation()
    const handleError = useHandleError()

    const [login, setLogin] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const showSuccess = useSnackbarQueue('success')

    const [loginError, setLoginError] = useState(false)
    const [newPasswordRegexError, setNewPasswordRegexError] = useState(false)
    const [newPasswordConfirmRegexError, setNewPasswordConfirmRegexError] = useState(false)

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

    const resetPassword = () => {
        setLoginError(!LOGIN_REGEX.test(login))
        setNewPasswordRegexError(!PASSWORD_REGEX.test(password))
        setNewPasswordConfirmRegexError(!PASSWORD_REGEX.test(confirmPassword))

        if(!LOGIN_REGEX.test(login) || !PASSWORD_REGEX.test(password) || !PASSWORD_REGEX.test(confirmPassword)) {
            handleError('error.fields')
            return
        } else if (password != confirmPassword) {
            setNewPasswordRegexError(true);
            setNewPasswordConfirmRegexError(true);
            handleError("passwords are not equal")
            return
        } else {
            setButtonPopupAcceptAction(true)
        }
    }


    const submitPasswordReset = async () => {
        setButtonPopupAcceptAction(false)

        const json = {
            "token": location.pathname.toString().substring('/reset/passwordReset/'.length),
            login,
            password
        }

        axios.put('account/reset-password', json, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => {
            refreshToken()
            showSuccess(t('successful action'))
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
            if (message == 'error.password.reset.wrongIdentity') {
                setLoginError(true)
            }
        });
    }


    return (
        <AuthLayout>
            <div>
                <form onSubmit={submitPasswordReset}>
                    <DarkedTextField
                        label={t("login") + ' *'}
                        placeholder="login"
                        className={styles.input}
                        value={login}
                        onChange={event => {
                            setLogin(event.target.value)
                            setLoginError(!LOGIN_REGEX.test(event.target.value))
                        }}
                        regexError={loginError}
                    />

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
                            if (PASSWORD_REGEX.test(confirmPassword)) {
                                setNewPasswordConfirmRegexError(false)
                            }
                            if (!PASSWORD_REGEX.test(event.target.value)) {
                                setNewPasswordRegexError(!PASSWORD_REGEX.test(event.target.value))
                            } else {
                                if (event.target.value != confirmPassword && PASSWORD_REGEX.test(confirmPassword)) {
                                    setNewPasswordRegexError(true)
                                } else {
                                    setNewPasswordRegexError(false)
                                }
                            }
                        }}
                        regexError={newPasswordRegexError}
                    />

                    <DarkedTextField
                        type="password"
                        label={t("confirm password") + ' *'}
                        placeholder="1234567890"
                        className={styles.input}
                        icon={(<PasswordIcon/>)}
                        value={confirmPassword}
                        onChange={event => {
                            setConfirmPassword(event.target.value)
                            if (PASSWORD_REGEX.test(password)) {
                                setNewPasswordRegexError(false);
                            }
                            if (!PASSWORD_REGEX.test(event.target.value)) {
                                setNewPasswordConfirmRegexError(!PASSWORD_REGEX.test(event.target.value))
                            } else {
                                if (event.target.value != password && PASSWORD_REGEX.test(password)) {
                                    setNewPasswordConfirmRegexError(true)
                                } else {
                                    setNewPasswordConfirmRegexError(false)
                                }
                            }
                        }}
                        regexError={newPasswordConfirmRegexError}
                    />

                    <RoundedButton
                        onClick={resetPassword}
                        style={{width: '100%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                        color="pink"
                    >{t("reset btn")}</RoundedButton>
                    <PopupAcceptAction
                        open={buttonPopupAcceptAction}
                        onConfirm={submitPasswordReset}
                        onCancel={() => {setButtonPopupAcceptAction(false)
                        }}
                    />
                </form>
            </div>
        </AuthLayout>
    );
}

export default PasswordReset;
