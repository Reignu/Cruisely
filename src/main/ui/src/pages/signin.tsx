import {Link, useHistory} from 'react-router-dom'

import Box from '@material-ui/core/Box'
import PasswordIcon from '@material-ui/icons/VpnKeyRounded'

import AuthLayout from '../layouts/AuthLayout'
import DarkedTextField from '../components/DarkedTextField'
import RoundedButton from '../components/RoundedButton'

import {useTranslation} from 'react-i18next'

import styles from '../styles/auth.global.module.css'
import axios from "../Services/URL"
import React, {useEffect, useState} from "react"
import {ILoginUserSliceState, selectDarkMode, setLogin as setLoginAction} from '../redux/slices/userSlice'

import {useSnackbarQueue} from "./snackbar";
import useHandleError from "../errorHandler";
import store from "../redux/store";
import {useDispatch, useSelector} from "react-redux";
import PopupAcceptAction from "../PopupAcceptAction";
import i18n from "i18next";
import {LOGIN_REGEX, PASSWORD_REGEX} from "../regexConstants";

export default function SignIn() {
    const {t} = useTranslation();

    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')

    const history = useHistory();
    const dispatch = useDispatch();
    const [login, setLogin] = useState('')
    const [password, setPassword] = useState('')

    const [loginRegexError, setLoginRegexError] = useState(false)
    const [passwordRegexError, setPasswordRegexError] = useState(false)

    const darkMode = useSelector(selectDarkMode)

    const handleConfirm = async () => {
        setLoginRegexError(!LOGIN_REGEX.test(login))
        setPasswordRegexError(!PASSWORD_REGEX.test(password))

        if (!LOGIN_REGEX.test(login) || !PASSWORD_REGEX.test(password)) {
            handleError('error.fields')
        } else {
            auth()
        }
    }

    const auth = () => {
        const json = JSON.stringify({
            login: login,
            password: password,
            darkMode: darkMode,
            language: i18n.language.toUpperCase()
        })
        axios.post('/auth/sign-in', json, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res => {
            dispatch(setLoginAction(login))
            history.push('/signin-code')
            showSuccess(t('successful action'))
        }).catch(error => {
            const message = error.response.data
            console.log(message)
            handleError(message, error.response.status)
            switch (message) {
                case 'error.database.userNotExists':
                    setLoginRegexError(true)
                    break
            }
        })
    }

    return (
        <AuthLayout>
            <h1 className={styles.h1}>{t("signin.welcome")}</h1>
            <h2 className={styles.h2}>{t("signin.subtitle")}</h2>

            <DarkedTextField
                label={t("login") + ' *'}
                placeholder={t("login")}
                className={styles.input}
                value={login}
                style={{
                    width: '70%',
                    margin: '20px 0'
                }}
                onChange={event => {
                    setLogin(event.target.value)
                    setLoginRegexError(!LOGIN_REGEX.test(event.target.value))
                }}
                colorIgnored
                regexError={loginRegexError}
            />

            <DarkedTextField
                type="password"
                label={t("password") + ' *'}
                placeholder="1234567890"
                className={styles.input}
                style={{
                    width: '70%',
                    margin: '20px 0'
                }}
                icon={(<PasswordIcon/>)}
                value={password}
                onChange={event => {
                    setPassword(event.target.value)
                    setPasswordRegexError(!PASSWORD_REGEX.test(event.target.value))
                }}
                colorIgnored
                regexError={passwordRegexError}
            />

            <Box className={styles['button-wrap']}>
                <RoundedButton
                    style={{
                        width: '50%',
                        fontSize: '1.2rem',
                        padding: '10px 0',
                        marginBottom: 16,
                    }}
                    color="pink"
                    onClick={handleConfirm}
                >{t("signin")}</RoundedButton>
                <Link to="signup/client" className={styles.link}>
                    {t("i don't have an account")}
                </Link>

                <Link to="/reset/requestPassword" className={styles.link}>
                    {t("forgot password")}
                </Link>

            </Box>


        </AuthLayout>
    )
}