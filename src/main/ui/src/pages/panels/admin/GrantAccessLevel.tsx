import {Box, FormControlLabel, FormGroup, Radio, RadioGroup} from "@material-ui/core";
import {Link, useHistory} from 'react-router-dom';
import React, {useState} from "react";
import {useTranslation} from 'react-i18next'
import RoundedButton from '../../../components/RoundedButton'
import {useSnackbarQueue} from "../../snackbar";
import {refreshToken} from "../../../Services/userService";
import axios from "../../../Services/URL";
import store from "../../../redux/store";
import useHandleError from "../../../errorHandler";
import PopupAcceptAction from "../../../PopupAcceptAction";


export default function Checkboxes() {
    const {token} = store.getState()
    const {t} = useTranslation()
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
    const [selectedAccessLevel, setSelectedAccessLevel] = React.useState("");
    const history = useHistory();

    const currentAccount = JSON.parse(sessionStorage.getItem("grantAccessLevelAccount") as string)

    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);


    const handleConfirm = async () => {
        if (!["Moderator", "Administrator"].includes(selectedAccessLevel)) return;

        const json = {
            accountLogin: currentAccount.login,
            accessLevel: selectedAccessLevel.toUpperCase(),
            accountVersion: currentAccount.version
        }

        await axios.put("account/grant-access-level",
            json,
            {
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                    'Authorization': `Bearer ${token}`,
                    "If-Match": currentAccount.etag
                }
            }
        ).then(() => {
            setButtonPopupAcceptAction(false)
            showSuccess(t('success.accessLevelAssigned'));
            refreshToken()
            history.push('/accounts')
        }).catch(error => {
            setButtonPopupAcceptAction(false)
            const message = error.response.data
            handleError(message, error.response.status)
        });
    }
    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectedAccessLevel(event.target.value)
    };

    return (
        <div>
            <FormGroup row>
                <RadioGroup onChange={handleChange} value={selectedAccessLevel} row>
                    {
                        currentAccount!.accessLevels.includes("MODERATOR") ? null :
                            <FormControlLabel
                                value={"Moderator"}
                                control={<Radio/>}
                                label="Moderator"
                                checked={selectedAccessLevel === "Moderator"}
                            />
                    }
                    {
                        currentAccount!.accessLevels.includes("ADMINISTRATOR") ? null :
                            <FormControlLabel
                                value="Administrator"
                                control={<Radio/>}
                                label="Administrator"
                                checked={selectedAccessLevel === "Administrator"}
                            />
                    }

                </RadioGroup>
            </FormGroup>
            <Box style={{marginTop: 16}}>
                <RoundedButton onClick={()=>setButtonPopupAcceptAction(true)} color="blue" style={{marginRight: 16}}>
                    {t("confirm")}

                </RoundedButton>
                <PopupAcceptAction
                    open={buttonPopupAcceptAction}
                    onConfirm={handleConfirm}
                    onCancel={() => {setButtonPopupAcceptAction(false)
                    }}
                />
                <Link to="/accounts">
                    <RoundedButton color="pink">
                        {t("go back")}
                    </RoundedButton>
                </Link>
            </Box>
        </div>
    )
}