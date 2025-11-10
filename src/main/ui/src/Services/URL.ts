import axios from "axios";

export default axios.create({
    baseURL: window.location.href.includes("studapp") ?
        "https://studapp.it.p.lodz.pl:8403/api" : "https://localhost:8181/api/"
});