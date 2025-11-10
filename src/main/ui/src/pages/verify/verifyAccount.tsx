import {useHistory, useLocation} from 'react-router-dom';

import axios from "../../Services/URL";
import AuthLayout from "../../layouts/AuthLayout";
import {useTranslation} from "react-i18next";
import styles from '../../styles/auth.global.module.css'
import RoundedButton from "../../components/RoundedButton";
import {useSnackbarQueue} from "../snackbar";
import useHandleError from "../../errorHandler";
import PopupAcceptAction from "../../PopupAcceptAction";
import {useState} from "react";

function VerifyAccount(props: any) {
    const location = useLocation();
    const {t} = useTranslation()
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);
    const history = useHistory()
    const accountVerify = () =>{
        setButtonPopupAcceptAction(true)

    }
    const submitAccountVerification = async () => {
        setButtonPopupAcceptAction(false)
        const json = {
            "token": location.pathname.toString().substring('/verify/accountVerification/'.length)
        }

        await axios.put('account/verify', json, {
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(res =>{
            setButtonPopupAcceptAction(false)
            history.push("/signin")
            showSuccess(t('successful action'))
        }).catch(error => {
            setButtonPopupAcceptAction(false)
            const message = error.response.data
            handleError(message, error.response.status)
        });

    }


    return (
        <AuthLayout>
            <div>
                <form onSubmit={submitAccountVerification}>
                    <h1 className={styles.h1}>{t("verifyAccount")}</h1>
                        <RoundedButton
                            onClick={accountVerify}
                            style={{width: '100%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                            color="pink"
                        >{t("verifyAccount")} </RoundedButton>
                    <PopupAcceptAction
                        open={buttonPopupAcceptAction}
                        onConfirm={submitAccountVerification}
                        onCancel={() => {setButtonPopupAcceptAction(false)
                        }}
                    />
                </form>
            </div>
        </AuthLayout>
    );
}

export default VerifyAccount;
