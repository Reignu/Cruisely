import store from "../redux/store";
import axios from './URL'
import {getUser, refreshToken} from "./userService";
import {useSnackbarQueue} from "../pages/snackbar";
import {useTranslation} from "react-i18next";

export function changeOwnPassword(oldPassword: string, newPassword: string) {
    const {
        user: {
            login,
            version,
            etag
        },
        token
    } = store.getState()


    return axios.put('self/change-password', {
        login: login,
        version: version,
        oldPassword: oldPassword,
        newPassword: newPassword
    }, {
        headers: {
            "If-Match": etag,
            "Authorization": `Bearer ${token}`
        }
    }).then(res => {
        return getUser(token).then(res => {
            refreshToken()
        })
    })
}