import store from '../redux/store';
import axios from './URL'

export function getAllAccounts() {
    const {token} = store.getState()

    return axios.get('account/accounts', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function getAccountDetailsAbout(login: string) {
    const {token} = store.getState()

    return axios.get(`account/details/${login}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function getAccountMetadataDetailsAbout(login: string) {
    const {token} = store.getState()

    return axios.get(`account/metadata/${login}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function getAccountAccessLevelMetadata(access_level: string, login: string){
    const {token} = store.getState()

    return axios.get(`/account/metadata/access-level/${access_level}/${login}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}
export function getClientAddressMetadata(login: string){
    const {token} = store.getState()

    return axios.get(`/account/metadata/address/${login}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}


export function getSelfMetadataDetails(){
        const {token} = store.getState()

        return axios.get(`/self/metadata`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })

}

export function getSelfAddressMetadataDetails(){
    const {token} = store.getState()

    return axios.get(`/self/metadata/address`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}
