import store from "../redux/store";
import axios from "./URL";

export function getAllCompanies() {
    const {token} = store.getState()

    return axios.get('company/companies', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
}
export function getCompanyMetadata(nip:string){
    const {token} = store.getState()

    return axios.get(`company/metadata/${nip}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}