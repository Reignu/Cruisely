import React, {useState} from 'react';
import {useHistory, useLocation} from 'react-router-dom';
import axios from "../../Services/URL";
import AuthLayout from "../../layouts/AuthLayout";
import DarkedTextField from "../../components/DarkedTextField";
import styles from "../../styles/auth.global.module.css";
import {useTranslation} from "react-i18next";
import RoundedButton from "../../components/RoundedButton";
import {Link} from "react-router-dom";
import {useSnackbarQueue} from "../snackbar";
import store from "../../redux/store";
import {refreshToken} from "../../Services/userService";
import useHandleError from "../../errorHandler";
import PopupAcceptAction from "../../PopupAcceptAction";
import EmailIcon from "@material-ui/icons/AlternateEmail";
import {
    EMAIL_REGEX,
} from "../../regexConstants";

export default function RequestSomeonePasswordReset () {
    const {t} = useTranslation()
    const location = useLocation();
    const history = useHistory();
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);
    const [emailRegexError, setEmailRegexError] = useState(false);

    const currentAccount = JSON.parse(sessionStorage.getItem("resetPasswordAccount") as string)

    const [email, setEmail] = useState('')
    const {token} = store.getState()
    let login = currentAccount.login;




    const onFormSubmit = () => {
        setButtonPopupAcceptAction(false)
        axios.post(`account/request-someones-password-reset/${login}/${email}/`, null, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        }).then(res => {
            refreshToken()
             history.push('/accounts')

            showSuccess(t('successful action'))
        }).catch(error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
    }

        const resetEmailFun = async () => {
            setEmailRegexError(!EMAIL_REGEX.test(email))

            if (!EMAIL_REGEX.test(email)) {
                handleError("invalid.form")
            } else {
                setButtonPopupAcceptAction(true)
            }
        }


    return (
            <div>
            <h3>{t("reset")}{login}{t("itsPassword")}</h3>
            <DarkedTextField
                type="email"
                label={t("email") + ' *'}
                style={{
                    width: '70%',
                    margin: '20px 0'
                }}
                placeholder="E-mail"
                className={styles.input}
                icon={(<EmailIcon/>)}
                value={email}
                onChange={event => {
                    setEmail(event.target.value)
                    setEmailRegexError(!EMAIL_REGEX.test(event.target.value))
                }}
                regexError={emailRegexError}
            />

            <RoundedButton
                onClick={resetEmailFun}
                style={{width: '50%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                color="pink"
            >{t("reset password")} </RoundedButton>
            <PopupAcceptAction
                open={buttonPopupAcceptAction}
                onConfirm={onFormSubmit}
                onCancel={() => {setButtonPopupAcceptAction(false)
                }}
            />
            </div>
    );
};

