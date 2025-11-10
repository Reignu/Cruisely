import React, {useReducer} from "react"
import './styles/popup.css'


export default function Popup(props: any) {

    function changeTrigger() {
       props.setTrigger(false);
    }

    return (props.trigger) ? (
        <>
        <div className="popup">
            <div className="popup-inner">
                <button className="close-btn" onClick={()=> props.setTrigger(false)}>{"x"}</button>
                {props.children}

            </div>

        </div>
        </>
    ) : null;

}

