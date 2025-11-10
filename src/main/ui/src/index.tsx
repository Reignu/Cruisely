import React, {ComponentType, createRef} from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import {Provider} from 'react-redux'
import store from './redux/store'
import i18n from "./i18n";
import Button from "@material-ui/core/Button";
import Slide from "@material-ui/core/Slide";
import {TransitionProps} from "@material-ui/core/transitions";
import {I18nextProvider, useTranslation} from "react-i18next";
import {SnackbarKey, SnackbarProvider} from "notistack";

const Root = () => {

    const {t} = useTranslation()
    const notistackRef = createRef<SnackbarProvider>()

    const handleDismiss = (key: SnackbarKey) => () => {
        notistackRef.current?.closeSnackbar(key)
    }

    return (
        <Provider store={store}>
            <I18nextProvider i18n={i18n}>
                <SnackbarProvider
                    ref={notistackRef}

                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'left'
                    }}

                    action={key => (
                        <Button onClick={handleDismiss(key)} style={{color: 'white'}}>
                            {t('dismiss')}
                        </Button>
                    )}

                    TransitionComponent={Slide as ComponentType<TransitionProps>}
                >
                    <App/>
                </SnackbarProvider>
            </I18nextProvider>
        </Provider>);
}

ReactDOM.render(
    <Root />,
    document.getElementById('root')
);

