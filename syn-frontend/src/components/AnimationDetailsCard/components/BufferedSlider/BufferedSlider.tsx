import React, {SyntheticEvent, useEffect} from "react";
import {Slider} from "@mui/material";

export function BufferedSlider({sliderValue, maxValue, onValueChange}: {sliderValue: number, maxValue: number, onValueChange: (newValue: number) => void}) {
    const [displayedSliderValue, setDisplayedSliderValue] = React.useState<number>(0);

    useEffect(() => {
        if (displayedSliderValue!== sliderValue)
            setDisplayedSliderValue(sliderValue)
    }, [sliderValue])

    const handleChange = (event: Event, newValue: number | number[]) => {
        setDisplayedSliderValue(newValue as number);
    };

    const handleChangeCommitted = (event: Event | SyntheticEvent<Element, Event>, newValue: number | number[]) => {
        onValueChange(newValue as number)
    };

    return <>
        {/*<LinearProgress
            sx={{marginTop: 1}}
            variant="buffer"
            value={(displayedSliderValue / (maxValue - 1)) * 100}
            valueBuffer={(displayedSliderValue / (maxValue - 1)) * 120}
        />*/}
        <Slider
            min={1}
            max={maxValue}
            onChangeCommitted={handleChangeCommitted}
            onChange={handleChange}
            value={displayedSliderValue}
            aria-label="Small"
            valueLabelDisplay="auto"
            style={{position: "absolute", left: 0, top: -6}}
        />
    </>

}

