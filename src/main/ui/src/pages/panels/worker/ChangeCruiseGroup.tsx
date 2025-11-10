import {useTranslation} from "react-i18next";
import useHandleError from "../../../errorHandler";
import {useSnackbarQueue} from "../../snackbar";
import {Box} from "@material-ui/core";
import DarkedTextField from "../../../components/DarkedTextField";
import ImageUploading, {ImageListType} from "react-images-uploading";
import RoundedButton from "../../../components/RoundedButton";
import React, {useEffect, useReducer, useState} from "react";
import store from "../../../redux/store";
import axios from "../../../Services/URL";
import {getCruiseGroupForBusinessWorker} from "../../../Services/cruiseGroupService";
import {refreshToken} from "../../../Services/userService";
import {useSelector} from "react-redux";
import {selectCompany} from "../../../redux/slices/userSlice";
import ship3 from "../../../images/ship3.jpg";
import {Link, useHistory} from "react-router-dom";
import {CITY_REGEX, COUNTRY_REGEX, HOUSE_STREET_NUMBER_REGEX, NAME_REGEX, STREET_REGEX} from "../../../regexConstants";
import styles from "../../../styles/auth.global.module.css";
import PopupAcceptAction from "../../../PopupAcceptAction";

export default function ChangeCruiseGroupData(){
    const {t} = useTranslation()
    const [, forceUpdate] = useReducer(x => x + 1, 0);
    const handleError = useHandleError()
    const showSuccess = useSnackbarQueue('success')
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

    const maxNumber = 1;
    const [images, setImages] = React.useState([]);
    const crusieGropData = JSON.parse(sessionStorage.getItem('ChangeCruiseGroupData') as string)
    const [visiblePicture,setVisiblePictrure] = useState(true)
    const history = useHistory();
    const onChange = (
        imageList: ImageListType,
    ) => {
        setImages(imageList as never[]);
        setVisiblePictrure(state => !state)
    };

    useEffect(() =>{
    setCruiseName(crusieGropData.name);
    setDescription(crusieGropData.description);
    setNumberOfSeats(crusieGropData.numberOfSeats);
    setPrice(crusieGropData.price);
    setStreetNumber(crusieGropData.cruiseAddress.streetNumber);
    setStreet(crusieGropData.cruiseAddress.street);
    setHarborName(crusieGropData.cruiseAddress.harborName);
    setCity(crusieGropData.cruiseAddress.cityName);
    setCountry(crusieGropData.cruiseAddress.countryName);


}, [])

    const [buttonPopupAcceptActionCruiseGroup, setButtonPopupAcceptActionCruiseGroup] = useState(false);

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
            handleChangeCruiseGroup()
        }
    }

    const handleChangeCruiseGroup = () => {
        const {token} = store.getState();
        const json = JSON.stringify({
            uuid: crusieGropData.uuid,
            name: cruiseName,
            numberOfSeats: numberOfSeats,
            price: price,
            cruiseAddress:{
                street: street,
                streetNumber: streetNumber,
                harborName: harborName,
                cityName: city,
                countryName: country
            },
            picture: images[0],
            description: description,
            version: crusieGropData.version
        })

       axios.put('cruiseGroup/change-cruise-group',json,{
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "If-Match": crusieGropData.etag,
                "Authorization": `Bearer ${token}`
            }}).then(res => {
                setButtonPopupAcceptActionCruiseGroup(false)
                showSuccess(t('successful action'))
                forceUpdate()
                history.push('/listCruiseGroup')
        }).catch(error => {
                setButtonPopupAcceptActionCruiseGroup(false)
                const message = error.response.data
                handleError(message, error.response.status)
               for (let messageArray of Object.values(message.errors)) {
                   for (const error of messageArray as Array<String>) {
                       switch(error) {
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
    let pictureUrl: string | undefined
    if(crusieGropData.cruisePictures.length>0){
        pictureUrl =crusieGropData.cruisePictures[0].dataURL
    }
    else {
        pictureUrl= ship3
    }
    return(
        <div>
            <h3>{t("/changeCruiseGroupData")}</h3>
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
            <Box style ={{
                width: '100%',
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between"}}>
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
                        <div className="upload__image-wrapper" >
                            <RoundedButton
                                color="blue"

                                onClick={onImageUpload}
                            >
                                {t("addPicture")}
                            </RoundedButton>
                            <img  style={{display: visiblePicture? "block":"none", marginTop: 20}} src = {pictureUrl} width="30%"/>
                            &nbsp;
                            {imageList.map((image, index) => (
                                <div key={index} className="image-item">

                                    <img src={image.dataURL} alt="" width="60%" />
                                    <div className="image-item__btn-wrapper">
                                        <RoundedButton color="blue-dark"
                                                       onClick={() => onImageRemove(index)}>{t("removePicture")}</RoundedButton>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </ImageUploading>

            </Box>
            <RoundedButton
                onClick={() => { setButtonPopupAcceptActionCruiseGroup(true)}}
                style={{width: '50%', fontSize: '1.2rem', padding: '10px 0', marginBottom: 20}}
                color="pink"
            >{t("changeData")} </RoundedButton>
            <PopupAcceptAction
                open={buttonPopupAcceptActionCruiseGroup}
                onConfirm={() => {
                    handleConfirm()
                }}
                onCancel={() => {
                    setButtonPopupAcceptActionCruiseGroup(false)
                }}/>
        </div>
    )
}