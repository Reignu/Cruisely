import {Box} from "@material-ui/core";
import PasswordIcon from "@material-ui/icons/VpnKeyRounded";
import DarkedTextField from "../../../components/DarkedTextField";
import React, {useEffect, useReducer, useState} from "react";
import {useTranslation} from "react-i18next";
import RoundedButton from "../../../components/RoundedButton";
import ImageUploading, {ImageListType} from "react-images-uploading";
import store from "../../../redux/store";
import {useSelector} from "react-redux";
import {selectCompany} from "../../../redux/slices/userSlice";
import axios from "../../../Services/URL";
import useHandleError from "../../../errorHandler";
import {useSnackbarQueue} from "../../snackbar";
import {Link, useHistory} from "react-router-dom";
import styles from "../../../styles/auth.global.module.css";
import {
    CITY_REGEX,
    COUNTRY_REGEX, HOUSE_STREET_NUMBER_REGEX, NAME_REGEX, POST_CODE_REGEX,
    STREET_REGEX
} from "../../../regexConstants";
import PopupAcceptAction from "../../../PopupAcceptAction";

export default function () {
    const [buttonPopupAcceptAction, setButtonPopupAcceptAction] = useState(false);

    const [, forceUpdate] = useReducer(x => x + 1, 0);
    const {t} = useTranslation();
    const [cruiseName, setCruiseName] = useState('')
    const [description, setDescription] = useState('')
    const [numberOfSeats, setNumberOfSeats] = useState('')
    const [price, setPrice] = useState('')
    const [streetNumber, setStreetNumber] = useState('')
    const [street, setStreet] = useState('')
    const [harborName, setHarborName] = useState('')
    const [city, setCity] = useState('')
    const [country, setCountry] = useState('')

    const [cruiseNameRegexError, setCruiseNameRegexError] = useState(false);
    const [descriptionRegexError, setDescriptionRegexError] = useState(false);
    const [numberOfSeatsRegexError, setNumberOfSeatsRegexError] = useState(false);
    const [priceRegexError, setPriceRegexError] = useState(false);
    const [streetRegexError, setStreetRegexError] = useState(false)
    const [streetNumberRegexError, setStreetNumberRegexError] = useState(false)
    const [harborNameRegexError, setHarborNameRegexError] = useState(false);
    const [cityRegexError, setCityRegexError] = useState(false)
    const [countryRegexError, setCountryRegexError] = useState(false)

    const handleError = useHandleError()
    const worker_Company = useSelector(selectCompany);
    const showSuccess = useSnackbarQueue('success')
    const history = useHistory();
    const [isPicture, setIsPicture] = React.useState(false)

    const maxNumber = 1;
    const [images, setImages] = React.useState([]);
    const onChange = (
        imageList: ImageListType,
    ) => {
        setIsPicture(state => !state)
        setImages(imageList as []);

    };

    const handleConfirm = async () => {
        setCruiseNameRegexError(!NAME_REGEX.test(cruiseName))
        if (description == '') {
            setDescriptionRegexError(true)
        }
        if (numberOfSeats == '' || parseInt(numberOfSeats) <= 0) {
            setNumberOfSeatsRegexError(true)
        }
        if (price == '' || parseInt(price) <= 0) {
            setPriceRegexError(true)
        }
        setStreetRegexError(!STREET_REGEX.test(street))
        setStreetNumberRegexError(!HOUSE_STREET_NUMBER_REGEX.test(streetNumber))
        setHarborNameRegexError(!NAME_REGEX.test(harborName))
        setCityRegexError(!CITY_REGEX.test(city))
        setCountryRegexError(!COUNTRY_REGEX.test(country))
        if (!NAME_REGEX.test(cruiseName) || numberOfSeats == '' || parseInt(numberOfSeats) <= 0 ||
            price == '' || parseInt(price) <= 0 || description == '' ||
            !STREET_REGEX.test(street) || !HOUSE_STREET_NUMBER_REGEX.test(streetNumber) ||
            !NAME_REGEX.test(harborName) || !CITY_REGEX.test(city) || !COUNTRY_REGEX.test(country)) {
            handleError('error.fields')
            return
        } else {
            handleAddCruise()
        }
    }

    const handleAddCruise = () => {
        const {token} = store.getState();
        const json = JSON.stringify({
            companyName: worker_Company,
            name: cruiseName,
            numberOfSeats: numberOfSeats,
            price: price,
            cruiseAddress: {
                street: street,
                streetNumber: streetNumber,
                harborName: harborName,
                cityName: city,
                countryName: country
            },
            cruisePictures: images,
            description: description,
        })
        axios.post('cruiseGroup/add-cuise-group', json, {
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${token}`
            }
        }).then(res => {
            showSuccess(t('successful action'))
            forceUpdate()
            history.push('/listCruiseGroup')
        })
            .catch(error => {
                const message = error.response.data
                handleError(message, error.response.status)
                for (let messageArray of Object.values(message.errors)) {
                    for (const error of messageArray as Array<String>) {
                        switch (error) {
                            case 'error.size.cruiseGroupName':
                            case 'error.regex.cruiseGroupName':
                                setCruiseNameRegexError(true)
                                break
                            case 'error.size.street':
                            case 'error.regex.street':
                                setStreetRegexError(true)
                                break
                            case 'error.size.streetNumber':
                            case 'error.regex.streetNumber':
                                setStreetNumberRegexError(true)
                                break
                            case 'error.size.harborName':
                            case 'error.regex.harborName':
                                setHarborNameRegexError(true)
                                break
                            case 'error.size.city':
                            case 'error.regex.city':
                                setCityRegexError(true)
                                break
                            case 'error.size.country':
                            case 'error.regex.country':
                                setCountryRegexError(true)
                                break
                        }
                    }
                }
            })
    }

    return (
        <div>
            <Box style={{
                width: '100%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between"
            }}>
                <DarkedTextField
                    type="text"
                    label={t("cruiseName") + ' *'}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    placeholder={t("cruisneNamePlaceHolder")}
                    value={cruiseName}
                    onChange={event => {
                        setCruiseName(event.target.value)
                        setCruiseNameRegexError(!NAME_REGEX.test(event.target.value))
                    }}
                    regexError={cruiseNameRegexError}
                />
                <DarkedTextField
                    type="text"
                    label={t("description") + ' *'}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    placeholder={t("descriptionPlaceHolder")}
                    value={description}
                    onChange={event => {
                        setDescription(event.target.value)
                        if (event.target.value == '') {
                            setDescriptionRegexError(true)
                        } else {
                            setDescriptionRegexError(false)
                        }
                    }}
                    regexError={descriptionRegexError}
                />
                <DarkedTextField
                    type="number"
                    label={t("numberOfSeats") + ' *'}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    placeholder={t("seatsPlaceHolder")}
                    value={numberOfSeats}
                    onChange={event => {
                        setNumberOfSeats(event.target.value)
                        if (parseInt(event.target.value) <= 0) {
                            setNumberOfSeatsRegexError(true);
                        } else {
                            setNumberOfSeatsRegexError(false);
                        }
                    }}
                    regexError={numberOfSeatsRegexError}
                />
                <DarkedTextField
                    type="number"
                    label={t("price") + ' *'}
                    className={styles.input}
                    style={{
                        marginLeft: 20
                    }}
                    placeholder={t("pricePlaceHolder")}
                    value={price}
                    onChange={event => {
                        setPrice(event.target.value)
                        console.log(event.target.value)
                        if (parseInt(event.target.value) <= 0) {
                            console.log(event.target.value)
                            setPriceRegexError(true);
                        } else {
                            setPriceRegexError(false);
                        }
                    }}
                    regexError={priceRegexError}
                />
            </Box>
            <Box style={{
                width: '100%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between"
            }}>
                <DarkedTextField
                    type="text"
                    label={t("street") + ' *'}
                    className={styles.input}
                    style={{
                        marginRight: 20
                    }}
                    placeholder={t("streetPlaceHolder")}
                    value={street}
                    onChange={event => {
                        setStreet(event.target.value)
                        setStreetRegexError(!STREET_REGEX.test(event.target.value))
                    }}
                    regexError={streetRegexError}
                />
                <DarkedTextField
                    type="text"
                    label={t("streetNumber") + ' *'}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    placeholder={t("numberPlaceHolder")}
                    value={streetNumber}
                    onChange={event => {
                        setStreetNumber(event.target.value)
                        setStreetNumberRegexError(!HOUSE_STREET_NUMBER_REGEX.test(event.target.value))
                    }}
                    regexError={streetNumberRegexError}
                />
                <DarkedTextField
                    type="text"
                    label={t("harborName") + ' *'}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    placeholder={t("harborPlaceHolder")}
                    value={harborName}
                    onChange={event => {
                        setHarborName(event.target.value)
                        setHarborNameRegexError(!NAME_REGEX.test(event.target.value))
                    }}
                    regexError={harborNameRegexError}
                />
                <DarkedTextField
                    type="text"
                    label={t("city") + ' *'}
                    className={styles.input}
                    style={{
                        marginLeft: 20,
                        marginRight: 20
                    }}
                    placeholder={t("cityPlaceHolder")}
                    value={city}
                    onChange={event => {
                        setCity(event.target.value)
                        setCityRegexError(!CITY_REGEX.test(event.target.value))
                    }}
                    regexError={cityRegexError}
                />
                <DarkedTextField
                    type="text"
                    label={t("country") + ' *'}
                    className={styles.input}
                    style={{
                        marginLeft: 20
                    }}
                    placeholder={t("countryPlaceHolder")}
                    value={country}
                    onChange={event => {
                        setCountry(event.target.value)
                        setCountryRegexError(!COUNTRY_REGEX.test(event.target.value))
                    }}
                    regexError={countryRegexError}
                />
            </Box>
            <Box style={{
                width: '100%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                marginBottom: 20
            }}>
                <ImageUploading
                    multiple
                    value={images}
                    onChange={onChange}
                    maxNumber={maxNumber}
                >
                    {({
                          imageList,
                          onImageUpload,
                          onImageRemoveAll,
                          onImageUpdate,
                          onImageRemove,
                          isDragging,
                          dragProps
                      }) => (
                        <div className="upload__image-wrapper">
                            <RoundedButton
                                color="blue"

                                onClick={onImageUpload}
                            >
                                {t("addPicture")}
                            </RoundedButton>
                            &nbsp;
                            {imageList.map((image, index) => (
                                <div key={index} className="image-item">
                                    <img src={image.dataURL} alt="" width="60%"/>
                                    <div className="image-item__btn-wrapper">
                                        <RoundedButton color="blue"
                                                       onClick={() => onImageRemove(index)}>{t("removePicture")}</RoundedButton>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </ImageUploading>
            </Box>
            <RoundedButton
                onClick={() => setButtonPopupAcceptAction(true)}
                style={{width: '50%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                color="pink"
            >{t("addCruise")} </RoundedButton>
            <PopupAcceptAction
                open={buttonPopupAcceptAction}
                onConfirm={() => {
                    setButtonPopupAcceptAction(false)
                    handleConfirm()
                }}
                onCancel={() => {
                    setButtonPopupAcceptAction(false)
                }}
            />
        </div>
    )
}