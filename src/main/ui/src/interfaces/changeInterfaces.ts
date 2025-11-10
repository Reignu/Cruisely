import AbstractAccount from "./AbstractAccount";

export interface ChangeEmail extends AbstractAccount {
    email: string
}

export interface ChangeAddress {
    houseNumber: string,
    street: string,
    postalCode: string,
    city: string,
    country: string
}

export interface AccountChangeData extends AbstractAccount {
    newFirstName: string,
    newSecondName: string
}

export interface ConsumerChangeData extends AccountChangeData {
    newPhoneNumber: string
}

export interface ClientChangeData extends ConsumerChangeData {
    newAddress: ChangeAddress
}

export interface BusinessWorkerChangeData extends ConsumerChangeData {

}

export interface ModeratorChangeData extends AccountChangeData {

}

export interface AdministratorChangeData extends AccountChangeData {

}

export interface ChangeLanguage extends AbstractAccount {

}

export interface ChangeMode extends AbstractAccount {
    newMode: boolean
}