import store from "../redux/store";
import axios from "../Services/URL"
import {clearToken, setToken} from "../redux/slices/tokenSlice";
import {AccessLevelType, emptyUser, IUserSliceState, setActiveAccessLevel, setUser} from "../redux/slices/userSlice";
import jwt_decode from "jwt-decode";
import i18n from "i18next";
import useHandleError from "../errorHandler";

export function getUser(token: string) {
    return axios.get('self/account-details', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }).then(res => {
        store.dispatch(setToken(token))
        store.dispatch(setUser(res.data))
        i18n.changeLanguage(res.data.languageType)
        saveToken(token)
    })
}

export function loadUserWithSavedToken() {
    i18n.changeLanguage(store.getState().user.languageType)
    const savedToken = sessionStorage.getItem('cruisehub_token') as string;

    if (savedToken) {
        return getUser(savedToken).then(res => {
            store.dispatch(setActiveAccessLevel(sessionStorage.getItem('cruisehub_active_al') as AccessLevelType))
            return res
        })
    }

    return new Promise((res, rej) => {
        if (

            !['/', '/signin', '/signup/client', '/signup/worker'].includes(document.location.pathname) &&
            !document.location.pathname.match('accountVerification') &&
            !document.location.pathname.match('reset') &&
            !document.location.pathname.includes('cruise')

        ) {
            rej({response: {data: 'token.missing'}})
        }
        res(null)
    })
}

export function logOut() {
    store.dispatch(emptyUser())
    console.log('language', store.getState().user.languageType)
    i18n.changeLanguage(store.getState().user.languageType)
    store.dispatch(clearToken())
    sessionStorage.clear()
    document.location.replace('/')
}

export function saveToken(token: string | null = null) {
    sessionStorage.setItem('cruisehub_token', token || store.getState().token)
}

export function refreshToken() {
    const decodedToken: any = jwt_decode(store.getState().token)
    let expireTime = new Date(decodedToken.exp * 1000)
    let currentTime = new Date()
    let differenceInSeconds = (expireTime.getTime() - currentTime.getTime()) / 1000

    if (differenceInSeconds < 60 && differenceInSeconds > 0) {
        return axios.post('auth/refresh-token/', {}, {
            headers: {
                'Authorization': `Bearer ${store.getState().token}`
            }
        }).then(res => {
            store.dispatch(setToken(res.data))
        }).catch(error => {
            logOut()
        })
    }
}

// export function ref