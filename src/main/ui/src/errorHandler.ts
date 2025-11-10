import {useSnackbarQueue} from "./pages/snackbar";
import {useTranslation} from "react-i18next";
import {logOut} from "./Services/userService";


function useHandleError() {
    const showError = useSnackbarQueue('error')
    const {t} = useTranslation()
    const showWarning = useSnackbarQueue('warning')

    return (error: string | any, status: number = 0) => {

        if (error === "" && status == 404) {
            showWarning(t("error.unauthorized"))
            setTimeout(logOut, 3000)
            return
        }

        if (error === undefined) {
            showError(t('unknown error'))
            return
        }

        if (!error) {
            showError(t('unknown error'))
            return
        }
        if( status == 401 && error == "error.database.userNotExists"){
            showError(t("error.database.userNotExists"))
            return
        }
        if (status == 401 && error != "auth.incorrect.password" && error != "error.facade.noSuchElement" ) {
            showWarning(t("error.unauthorized"))
            setTimeout(logOut, 3000)
            return
        }

        if (typeof error === 'string') {
            showError(t(error))
            return
        }
        for (let messageArray of Object.values(error.errors)) {
            for (const message of messageArray as Array<String>) {
                showError(t(message as string))
            }
        }

    }
}

export default useHandleError;