import {grey} from "@material-ui/core/colors";

export interface ChangeDataComponentProps {
    open: boolean,
    onOpen(): void,
    onConfirm(): void,
    onCancel(): void
}

export type Color =
    "pink" | "pink-dark" | "pink-light" |
    "blue" | "blue-dark" | "blue-light" |
    "green" | "green-dark" | "green-light" |
    "yellow" | "yellow-dark" | "yellow-light" |
    "dark" | "dark-dark" | "dark-light" |
    "white" | "white-dark" | "white-light"|
    "gray" | "gray-dark" | "gray-light";