import store from "../redux/store";
import axios from "./URL";

export function getOwnRatings() {
    const {token} = store.getState()

    return axios.get('self/ratings', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function getClientRatings(login: string) {
    const {token} = store.getState()

    return axios.get(`ratings/${login}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function removeClientRating(login: string, cruiseGroupUUID: string) {
    const {token} = store.getState()

    return axios.delete(`ratings/${login}/${cruiseGroupUUID}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}