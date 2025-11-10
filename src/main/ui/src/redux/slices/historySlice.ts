import {createSelector, createSlice, PayloadAction} from "@reduxjs/toolkit";

const historySlice = createSlice({
    name: 'history',
    initialState: ['/'],
    reducers: {
        setHistory: (state: string[], {payload}: PayloadAction<string>) => state.concat(payload),
        popHistory: (state: string[]) => {
            state.pop()
        },
        clearHistory: () => ['/']
    }
})

export const {setHistory, popHistory, clearHistory} = historySlice.actions

const selectSelf = (state: { history: string }) => state

export const selectHistory = createSelector(selectSelf, state => state.history)

export default historySlice.reducer