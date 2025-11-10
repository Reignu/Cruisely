import React from "react";

const LineDivider = ({style = {}}: {style?: React.CSSProperties}) =>
    <div
        style={{
            height: 'calc(100% - 4px)',
            width: 3,
            backgroundColor: 'var(--white)',
            ...style
        }}
    />

export default LineDivider;