import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";

const tokenSlice = createSlice({
    name: 'token',
    initialState: '',
    reducers: {
        setToken: (state: string, {payload}: PayloadAction<string>) => payload,
        clearToken: () => ''
    }
})

export const {setToken, clearToken} = tokenSlice.actions

const selectSelf = (state: { token: string }) => state

export const selectToken = createSelector(selectSelf, state => state.token)

export default tokenSlice.reducer