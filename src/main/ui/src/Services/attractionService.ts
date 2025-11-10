import axios from './URL';
import store from "../redux/store";

export function getAttractionsByCruiseUUID(uuid: string) {
    return axios.get(`attractions/cruise/${uuid}`)
}

export function deleteAttraction(uuid: string){
    const {token} = store.getState()

    return axios.delete(`attractions/delete-attraction/${uuid}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}
export function getAttractionMetadata(uuid:string){
    const {token} = store.getState()

    return axios.get(`attractions/metadata/${uuid}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}
