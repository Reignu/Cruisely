import axios from "./URL";
import store from "../redux/store";

export function createReservation(cruiseVersion: number, uuid: string, numberOfSeats: number, attractionsUUID: string[]) {

    const {token} = store.getState()
    const json = {
        cruiseVersion: cruiseVersion,
        cruiseUuid: uuid,
        numberOfSeats: numberOfSeats,
        attractionsUUID: attractionsUUID
    }
    return axios.post('cruise/reserve', json, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    })
}
export function removeClientReservation(uuid: string, login: string){
    const {token} = store.getState();

    return axios.delete(`reservation/${login}/${uuid}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })
}

export function getReservationsForCruise(uuid: any){
    const {token} = store.getState()
    const cruiseUUID = uuid
    return axios.get(`reservation/reservations-for-cruise/${cruiseUUID}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })
}

export function getReservationsForWorkerCruise(uuid: any){
    const {token} = store.getState()
    const cruiseUUID = uuid
    return axios.get(`reservation/reservations-for-worker-cruise/${cruiseUUID}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })
}

export function getSelfReservations() {
    const {token} = store.getState()
    return axios.get("/reservation/self-reservations", {
        headers: {
            "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
        }
    })
}

export function removeReservation(reservationUUID: string) {
    const {token} = store.getState();

    return axios.delete(`cruise/cancelReservation/${reservationUUID}`, {
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        }
    })
}
export function getReservationMetadata(uuid:string){
    const {token} = store.getState()

    return axios.get(`/reservation/metadata/${uuid}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })

}