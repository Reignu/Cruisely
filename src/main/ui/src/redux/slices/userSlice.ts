import {createSelector, createSlice, PayloadAction} from '@reduxjs/toolkit'
import i18n from "i18next";

export interface ILoginUserSliceState {
    firstName: string,
    secondName: string,
    email: string,
    login: string,
    etag: string,
    darkMode: boolean,
    version: number,
    languageType: string,
    accessLevels: Array<IAccessLevel>,
}

export interface IUserSliceState extends ILoginUserSliceState {
    activeAccessLevel: AccessLevelType,
}

export type AccessLevelType = "CLIENT" | "BUSINESS_WORKER" | "MODERATOR" | "ADMINISTRATOR" | "";

export interface IAccessLevel {
    '@type': "client" | "businessWorker" | "moderator" | "administrator",
    accessLevelType: AccessLevelType,
    address?: {
        houseNumber: string,
        street: string,
        postalCode: string,
        city: string,
        country: string
    },
    phoneNumber?: string,
    companyName?: string
}

const userSlice = createSlice({
    name: 'user',
    initialState: {
        firstName: '',
        secondName: '',
        login: '',
        darkMode: false,
        email: '',
        languageType: window.navigator.languages[0].substring(0,2).toUpperCase() === "EN" ? "EN" : "PL",
        accessLevels: [] as Array<IAccessLevel>,
        activeAccessLevel: '',
        etag: '',
        version: 0
    } as IUserSliceState,
    reducers: {
        setUser: (state: IUserSliceState, {payload}: PayloadAction<ILoginUserSliceState>) => {
            return {
                ...payload,
                activeAccessLevel: sessionStorage.getItem('cruisehub_active_al') as AccessLevelType ||
                    payload.accessLevels.find(accessLevel => accessLevel.accessLevelType === state.activeAccessLevel)?.accessLevelType ||
                    payload.accessLevels[0].accessLevelType
                }
        },
        changeEmail: (state: IUserSliceState, {payload}: PayloadAction<string>) => {
            state.email = payload
        },
        setLogin: (state: IUserSliceState, {payload}: PayloadAction<string>) => {
            state.login = payload
        },
        emptyUser: () => {
            const languageType = window.navigator.languages[1]
                .toUpperCase() === "PL" ? "PL" : "EN"

            return {
                firstName: '',
                secondName: '',
                login: '',
                darkMode: false,
                email: '',
                languageType,
                accessLevels: [] as Array<IAccessLevel>,
                activeAccessLevel: '',
                etag: '',
                version: 0
            } as IUserSliceState
        },
        setActiveAccessLevel: (state: IUserSliceState, {payload}: PayloadAction<AccessLevelType | null>) => {
            if (payload) {
                state.activeAccessLevel = payload
                sessionStorage.setItem('cruisehub_active_al', payload)
            }
        },
        changeLanguage: (state: IUserSliceState) => {
            state.languageType = state.languageType === "PL" ? "EN" : "PL";
        },
        changeDarkMode: (state: IUserSliceState) => {
            state.darkMode = !state.darkMode
        }
    }
})

export const {
    setUser,
    changeEmail,
    emptyUser,
    setActiveAccessLevel,
    setLogin,
    changeLanguage,
    changeDarkMode,
} = userSlice.actions

const selectSelf = (state: { user: IUserSliceState }) => state

export const selectFirstName = createSelector(selectSelf, state => state.user.firstName)
export const selectSecondName = createSelector(selectSelf, state => state.user.secondName)
export const selectEmail = createSelector(selectSelf, state => state.user.email)
export const selectEtag = createSelector(selectSelf, state => state.user.etag)
export const selectLogin = createSelector(selectSelf, state => state.user.login)
export const selectVersion = createSelector(selectSelf, state => state.user.version)
export const selectDarkMode = createSelector(selectSelf, state => state.user.darkMode)
export const isUserEmpty = createSelector(selectSelf, state => {
    const {
        firstName,
        secondName,
        email,
        etag
    } = state.user

    return !firstName && !secondName  && !email && !etag
})
export const getAccessLevelLabels = createSelector(selectSelf, state =>
    state.user.accessLevels.map((accessLevel: IAccessLevel) => accessLevel.accessLevelType)
)

export const selectOtherAccessLevel = createSelector(selectSelf, state =>
    state.user.accessLevels
        .map((accessLevel: IAccessLevel) => accessLevel.accessLevelType)
        .filter(label => label !== state.user.activeAccessLevel))

export const selectPhoneNumber = (accessLevelLabel: "CLIENT" | "BUSINESS_WORKER") =>
    createSelector(selectSelf,
        state => state.user.accessLevels.find(accessLevel => accessLevel.accessLevelType === accessLevelLabel)?.phoneNumber || "-1")

export const selectAddress =
    createSelector(selectSelf,
        state => state.user.accessLevels.find(accessLevel => accessLevel.accessLevelType === "CLIENT")?.address ||
            {
                houseNumber: "",
                street: "",
                postalCode: "",
                city: "",
                country: ""
            })
export const selectCompany =
    createSelector(selectSelf,
        state => state.user.accessLevels.find(accessLevel => accessLevel.accessLevelType === "BUSINESS_WORKER")?.companyName ||
         "")

export const selectActiveAccessLevel = createSelector(selectSelf, state => state.user.activeAccessLevel)

export const selectLanguage = createSelector(selectSelf, state => state.user.languageType)

export default userSlice.reducer