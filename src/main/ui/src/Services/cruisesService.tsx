import axios from "./URL";
import store from "../redux/store";

export function getPublishedCruises() {
    return axios.get('cruise/cruises')
}

export function getCruiseByUUID(uuid: string) {
    return axios.get(`cruise/get-cruise/${uuid}`)
}

export function getRelatedCruises(uuid: string) {
    return axios.get(`cruise/cruise_group/${uuid}`)
}
export function getCruiseMetadata(uuid:string){
    const {token} = store.getState()

    return axios.get(`cruise/metadata/${uuid}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}