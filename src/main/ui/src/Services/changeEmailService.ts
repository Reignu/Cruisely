import store from "../redux/store";
import axios from './URL'
import {getUser} from "./userService";
import {useSnackbarQueue} from "../pages/snackbar";
import {useTranslation} from "react-i18next";

export function changeEmail(newEmail: string) {



    const {
        user: {
            login,
            version,
            etag
        },
        token
    } = store.getState()

    return axios.post('account/request-email-change', {
            newEmail,
            login,
            version
        }, {
            headers: {
                "If-Match": etag,
                "Authorization": `Bearer ${token}`
            }
        }).then(res => {
            return getUser(token)
    })
}