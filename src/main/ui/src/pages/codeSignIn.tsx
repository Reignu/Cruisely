import {useHistory} from 'react-router-dom'

import Box from '@material-ui/core/Box'

import AuthLayout from '../layouts/AuthLayout'
import DarkedTextField from '../components/DarkedTextField'
import RoundedButton from '../components/RoundedButton'

import {useTranslation} from 'react-i18next'

import styles from '../styles/auth.global.module.css'
import React, {useState} from "react"

import {getUser} from "../Services/userService";
import {useSelector} from "react-redux";
import {selectLogin} from "../redux/slices/userSlice";
import useHandleError from "../errorHandler";
import axios from "../Services/URL"

export default function CodeSignIn() {
    const {t} = useTranslation();

    const history = useHistory();
    const login = useSelector(selectLogin);
    const [code, setCode] = useState('')
    const handleError = useHandleError()
    const [codeEmptyError, setCodeEmptyError] = useState(false)

    const codeAuth = async () => {
        if (code === "") {
            setCodeEmptyError(true)
            handleError("invalid.form")
        } else {
            const json = JSON.stringify({
                login: login,
                code: code
            })

            await axios.post('auth/code-sign-in', json, {
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(res => {
                getUser(res.data)
                .catch(error => {
                    const message = error.response?.data
                    handleError(message, error.response?.status)
                })
                history.push('/')
            }).catch(error => {
                const message = error.response.data
                handleError(message, error.response.status)
                if (message == 'error.code.incorrectCodeError') {
                    setCodeEmptyError(true);
                }
            })
        }
    }

    return (
        <AuthLayout>
            <h1 className={styles.h1}>{t("signin.welcome")}</h1>
            <h2 className={styles.h2}>{login}</h2>
            <DarkedTextField
                type="text"
                label={t("code") + ' *'}
                style={{
                    width: '70%',
                    margin: '20px 0'
                }}
                placeholder={t("code")}
                value={code}
                onChange={event => {
                    setCode(event.target.value)
                    event.target.value ? setCodeEmptyError(false) : setCodeEmptyError(true)
                }}
                colorIgnored
                regexError={codeEmptyError}
            />
            <Box style={{
                width: '70%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between"
            }}>
                <RoundedButton
                    style={{
                        width: '50%',
                        fontSize: '1.2rem',
                        padding: '10px 0'
                    }}
                    color="pink"
                    onClick={codeAuth}
                >{t("signin")}</RoundedButton>
            </Box>
        </AuthLayout>
    )
}