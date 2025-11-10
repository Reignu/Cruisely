import {useSnackbar} from "notistack";
import Slide from "@material-ui/core/Slide";
import {ComponentType} from "react";
import {TransitionProps} from "@material-ui/core/transitions";

export function useSnackbarQueue(variant: 'error' | 'success' | 'warning') {
    const {enqueueSnackbar} = useSnackbar();

    return (message: string) => {
        enqueueSnackbar(message, {
            variant,
            anchorOrigin: {
                vertical: 'bottom',
                horizontal: 'left',
            },
            TransitionComponent: Slide as ComponentType<TransitionProps>,
        })
    }
}