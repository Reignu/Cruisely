import RoundedButton from "./RoundedButton";
import React from "react";
import {useTranslation} from "react-i18next";

export default function LogOutRoundedButton() {
    const {t} = useTranslation()

    const logOut = () => {

    }

    return (
        <RoundedButton
            color="pink"
            onClick={logOut}>
            {t("logout")}
        </RoundedButton>
    )
}