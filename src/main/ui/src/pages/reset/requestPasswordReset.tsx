import React, {useState} from 'react';
import axios from "../../Services/URL";
import AuthLayout from "../../layouts/AuthLayout";
import DarkedTextField from "../../components/DarkedTextField";
import styles from "../../styles/auth.global.module.css";
import {useTranslation} from "react-i18next";
import RoundedButton from "../../components/RoundedButton";
import {useSnackbarQueue} from "../snackbar";
import useHandleError from "../../errorHandler";
import PopupAcceptAction from "../../PopupAcceptAction";
import {useHistory} from "react-router-dom";
import {LOGIN_REGEX} from "../../regexConstants";

const RequestPasswordReset = () => {
    const {t} = useTranslation()
    const showSuccess = useSnackbarQueue('success')
    const handleError = useHandleError()
    const history = useHistory()

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

    const [login, setLogin] = useState('')
    const [loginError, setLoginError] = useState(false)

    const resetPassword = () => {
        if (login === "") {
            setLoginError(true);
            handleError("fill.login.field");
        } else {
            setLoginError(!LOGIN_REGEX.test(login))
            if (!LOGIN_REGEX.test(login)) {
                handleError("error.regex.login");
            } else {
                setButtonPopupAcceptAction(true);
            }
        }
    }


    const onFormSubmit = () => {
        axios.post(`account/request-password-reset/${login}`, {})
            .then(res => {
                showSuccess(t('successful action'))
                history.push('/')
            })
            .catch(error => {
                const message = error.response.data
                handleError(message, error.response.status)
                setLoginError(true);
                setButtonPopupAcceptAction(false);
            });
    }

    return (
        <AuthLayout>
            <DarkedTextField
                colorIgnored
                label={t("login") + ' *'}
                placeholder={t("login")}
                className={styles.input}
                value={login}
                onChange={event => {
                    setLogin(event.target.value)
                    setLoginError(!LOGIN_REGEX.test(event.target.value))
                }}
                regexError={loginError}
            />
            <PopupAcceptAction
                open={buttonPopupAcceptAction}
                onConfirm={onFormSubmit}
                onCancel={() => {
                    setButtonPopupAcceptAction(false)
                }}
            />
            <RoundedButton
                onClick={resetPassword}
                style={{width: '50%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                color="pink"
            >{t("sendEmail")}</RoundedButton>
        </AuthLayout>
    );
};

export default RequestPasswordReset;