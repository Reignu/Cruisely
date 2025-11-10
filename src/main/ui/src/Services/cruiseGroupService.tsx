import store from "../redux/store";
import axios from "./URL";

export function getAllCruiseGroup() {
    const {token} = store.getState()

    return axios.get('cruiseGroup/cruise-groups', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function getCruiseGroupForBusinessWorker(props: string) {
    const {token} = store.getState()
    const companyName = props
    return axios.get(`cruiseGroup/CruiseGroupForBusinessWorker/${companyName}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function getCruisesForCruiseGroup(uuid: string) {
    const {token} = store.getState()

    return axios.get(`cruise/cruises-for-group/${uuid}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}

export function publishCruise(uuid: string, version: number, etag: string,) {
    const {token} = store.getState()
    return axios.put(`cruise/publish`, {
        "cruiseVersion": version,
        "cruiseUuid": uuid
    }, {
        headers: {
            "If-Match": etag,
            'Authorization': `Bearer ${token}`
        }
    })
}
export function getCruiseGroupMetadata(uuid:string){
    const {token} = store.getState()

    return axios.get(`cruiseGroup/metadata/${uuid}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}