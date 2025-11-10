import SettingsIcon from '@material-ui/icons/SettingsRounded'
import CruiseIcon from '@material-ui/icons/CardTravelRounded'
import StarIcon from '@material-ui/icons/CardTravelRounded'


import {useTranslation} from 'react-i18next'

import {useSelector} from "react-redux";
import {selectDarkMode} from "../../redux/slices/userSlice";
import ChangeEmail from "../../components/changeData/ChangeEmail";
import React, {useEffect, useState} from "react";
import ChangeClientData from "../../components/changeData/ChangeClientData";
import ChangePassword from "../../components/changeData/ChangePassword";
import ChangeAddress from "../../components/changeData/ChangeAddress";
import PanelLayout from "../../layouts/PanelLayout";
import {getSelfMetadataDetails, getSelfAddressMetadataDetails} from "../../Services/accountsService";
import {refreshToken} from "../../Services/userService";
import useHandleError from "../../errorHandler";
import ListOwnRatings from "./client/ListOwnRatings";
import ListSelfReservations from "./client/ListSelfReservations";
import ClientHomePage from "../../components/ClientHomePage";

export default function ClientPanel() {
    const {t} = useTranslation()
    const handleError = useHandleError()
    const [isEmailEdit, setIsEmailEdit] = useState(false)
    const [isDataEdit, setIsDataEdit] = useState(false)
    const [isAddressEdit, setIsAddressEdit] = useState(false)
    const [isPasswordEdit, setIsPasswordEdit] = useState(false)


    const handleIsEmailEdit = () => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            //          sessionStorage.setItem("changeSelfAddressDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
        setIsEmailEdit(true)
        setIsDataEdit(false)
        setIsAddressEdit(false)
        setIsPasswordEdit(false)
    }

    const handleIsDataEdit = () => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            //          sessionStorage.setItem("changeSelfAddressDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
        setIsDataEdit(true)
        setIsEmailEdit(false)
        setIsAddressEdit(false)
        setIsPasswordEdit(false)
    }

    const handleIsAddressEdit = () => {
        getSelfAddressMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAddressDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
        setIsAddressEdit(true)
        setIsDataEdit(false)
        setIsEmailEdit(false)
        setIsPasswordEdit(false)
    }

    const handleIsPasswordEdit = () => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            //    sessionStorage.setItem("changeSelfAddressDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });
        setIsPasswordEdit(true)
        setIsDataEdit(false)
        setIsEmailEdit(false)
        setIsAddressEdit(false)
    }
    useEffect(() => {
        getSelfMetadataDetails().then(res => {
            sessionStorage.setItem("changeSelfAccountDataMta", JSON.stringify(res.data));
            sessionStorage.setItem("changeSelfAddressDataMta", JSON.stringify(res.data));
            refreshToken();
        }, error => {
            const message = error.response.data
            handleError(message, error.response.status)
        });

    }, [])
    return (
        <PanelLayout
            color={{
                light: 'blue',
                dark: 'white-dark'
            }}
            menu={[
                {
                    link: '/profile/reservations',
                    text: t('reservations'),
                    Icon: CruiseIcon,
                    Component: ListSelfReservations
                },

                {
                    link: '/profile/ratings',
                    text: t('ratings'),
                    Icon: StarIcon,
                    Component: ListOwnRatings
                },
                {
                    link: '/profile/settings',
                    text: t('settings'),
                    Icon: SettingsIcon,
                    Component: () => (
                        <>
                            <ChangeClientData
                                open={isDataEdit}
                                onOpen={handleIsDataEdit}
                                onConfirm={() => {
                                    setIsDataEdit(false)
                                }}
                                onCancel={() => {
                                    setIsDataEdit(false)
                                }}
                            />
                            <ChangeAddress
                                open={isAddressEdit}
                                onOpen={handleIsAddressEdit}
                                onConfirm={() => {
                                    setIsAddressEdit(false)
                                }}
                                onCancel={() => {
                                    setIsAddressEdit(false)
                                }}
                            />
                            <ChangeEmail
                                open={isEmailEdit}
                                onOpen={handleIsEmailEdit}
                                onConfirm={() => {
                                    setIsEmailEdit(false)
                                }}
                                onCancel={() => {
                                    setIsEmailEdit(false)
                                }}
                            />
                            <ChangePassword
                                open={isPasswordEdit}
                                onOpen={handleIsPasswordEdit}
                                onConfirm={() => {
                                    setIsPasswordEdit(false)
                                }}
                                onCancel={() => {
                                    setIsPasswordEdit(false)
                                }}
                            />
                        </>
                    )
                }
            ]}
            otherRoutes={[
            {
                to: '/profile',
                Component: ClientHomePage
            }
           ] }
        />
    )
}
