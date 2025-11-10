import store from "../redux/store";
import axios from './URL';

export function createRating(rating: number, cruiseGroupUUID: string) {
    const {
        user: {
            etag
        },
        token
    } = store.getState()

    return axios.post('/ratings/create', {
        cruiseGroupUUID,
        rating,
    }, {
        headers: {
            "If-Match": etag,
            "Authorization": `Bearer ${token}`
        }
    })
}

export function removeRating(uuid: string) {
    const {
        user: {
            etag,
        },
        token,
    } = store.getState()

    return axios.delete(`/ratings/${uuid}`, {
        headers: {
            "If-Match": etag,
            "Authorization": `Bearer ${token}`
        }
    })
}
export function getRatingMetadata(uuid:string){
    const {token} = store.getState()

    return axios.get(`ratings/metadata/${uuid}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}