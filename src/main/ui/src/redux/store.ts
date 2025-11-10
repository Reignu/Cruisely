import { configureStore } from '@reduxjs/toolkit';
import userReducer from './slices/userSlice';
import tokenReducer from './slices/tokenSlice';
import changeAccessLevelStateAccountReducer from "./slices/changeAccessLevelStateSlice";
import historyReducer from './slices/historySlice';

const reducer = {
    user: userReducer,
    token: tokenReducer,
    changeAccessLevelStateAccount: changeAccessLevelStateAccountReducer,
    history: historyReducer,
}

const store = configureStore({
    reducer
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispath = typeof store.dispatch

export default store
