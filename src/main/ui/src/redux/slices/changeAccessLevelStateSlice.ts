import {createSlice, PayloadAction} from "@reduxjs/toolkit";

export interface IChangeAccessLevelStateAccountSlice {
    accessLevels: Array<any>,
    active: boolean,
    confirmed: boolean,
    darkMode: boolean,
    email: string,
    etag: string,
    firstName: string,
    languageType: string,
    login: string,
    secondName: string,
    version: number,
}

const changeAccessLevelStateAccountSlice = createSlice({
    name: "changeAccessLevelStateAccount",
    initialState: {
        accessLevels: [],
        active: false,
        confirmed: false,
        darkMode: false,
        email: "",
        etag: "",
        firstName: "",
        languageType: "PL",
        login: "",
        secondName: "",
        version: 0,
    } as IChangeAccessLevelStateAccountSlice,
    reducers: {
        setChangeAccessLevelStateAccount: (state: IChangeAccessLevelStateAccountSlice, {payload}: PayloadAction<IChangeAccessLevelStateAccountSlice>) => payload
    }
})

export const {setChangeAccessLevelStateAccount} = changeAccessLevelStateAccountSlice.actions

export default changeAccessLevelStateAccountSlice.reducer