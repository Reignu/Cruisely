import React, {ComponentType, createRef, useEffect} from 'react';
import Button from "@material-ui/core/Button";
import Slide from "@material-ui/core/Slide"

import {Link, Redirect} from 'react-router-dom';

import 'normalize.css'
import './styles/globals.css';

import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'

import {I18nextProvider, useTranslation} from 'react-i18next';
import i18n from './i18n'

import {ReactComponent as SVG404} from './404.svg'

import Home from './pages/home'
import Signin from './pages/signin'
import SignUpClient from './pages/signup/client'
import SignUpWorker from './pages/signup/worker'
import AdminPanel from './pages/panels/adminPanel'
import ModeratorPanel from "./pages/panels/moderatorPanel";
import Cruise from './pages/cruise';
import ClientPanel from './pages/panels/clientPanel'
import WorkerPanel from './pages/panels/workerPanel'
import PasswordReset from "./pages/reset/passwordReset";
import RequestPasswordReset from './pages/reset/requestPasswordReset';
import RequestSomeonePasswordReset from './pages/reset/requestSomeonesPasswordReset';
import VerifyAccount from './pages/verify/verifyAccount';
import ChangeEmail from './pages/reset/ChangeEmail'
import CodeSignIn from './pages/codeSignIn';

import {SnackbarKey, SnackbarProvider} from 'notistack';
import {TransitionProps} from "@material-ui/core/transitions";
import {useSelector} from "react-redux";
import {selectActiveAccessLevel, selectLanguage} from "./redux/slices/userSlice";
import {loadUserWithSavedToken, logOut} from "./Services/userService";
import RoundedButton from "./components/RoundedButton";
import useHandleError from "./errorHandler";
import {useSnackbarQueue} from "./pages/snackbar";



function App() {
    const {t} = useTranslation()

    const handleError = useHandleError()
    const showWarning = useSnackbarQueue('warning')

    useEffect(() => {
        loadUserWithSavedToken()
        .catch(error => {
            const message = error.response?.data
            handleError(message, error.response?.status)
        })
        }, [])



    const activeAccessLevel = useSelector(selectActiveAccessLevel)

    return (
                <Router basename={process.env.REACT_APP_ROUTER_BASE || ''}>

                    <Switch>
                        <Route exact={["", "CLIENT"].includes(activeAccessLevel)} path="/" component={
                            activeAccessLevel === "ADMINISTRATOR" ? AdminPanel :
                                activeAccessLevel === "MODERATOR" ? ModeratorPanel :
                                    activeAccessLevel === "BUSINESS_WORKER" ? WorkerPanel : Home
                        }
                        />

                        {
                            activeAccessLevel === "CLIENT" ? (
                                <Route path="/profile">
                                    <ClientPanel />
                                </Route>
                            ) : null
                        }

                        <Route path="/signin">
                            <Signin/>
                        </Route>

                        <Route path="/signup/client">
                            <SignUpClient/>
                        </Route>

                        <Route path="/signup/worker">
                            <SignUpWorker/>
                        </Route>

                        <Route path="/reset/passwordReset">
                            <PasswordReset/>
                        </Route>
                        <Route path="/reset/changeEmail">
                            <ChangeEmail/>
                        </Route>

                        <Route path="/reset/requestPassword">
                            <RequestPasswordReset/>
                        </Route>

                        <Route path="/verify/accountVerification">
                            <VerifyAccount/>
                        </Route>
                        <Route path="/signin-code">
                            <CodeSignIn/>
                        </Route>

                        <Route path="/cruise/:id" component={Cruise} />

                        <Route path="*">
                            <div style={{
                                display: 'flex',
                                flexDirection: 'column',
                                justifyContent: 'center',
                                alignItems: 'center',
                                width: '100%',
                                height: '100vh',
                                fontFamily: "'Montserrat', sans-serif",
                                fontSize: '2rem'
                            }}>
                                <div>
                                    <SVG404 />
                                </div>
                                <div>{t("404")}</div>
                                <Link to="/">
                                    <RoundedButton color="pink" style={{
                                        fontSize: '1.8rem',
                                        padding: 10,
                                        marginTop: 30
                                    }}>{t("go back")}</RoundedButton>
                                </Link>
                            </div>
                        </Route>
                    </Switch>
                </Router>

    );
}

export default App;