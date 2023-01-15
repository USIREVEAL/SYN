export const multiplierValues = [
    {
        value: 3600,
        name: "Hour"
    },{
        value: 86400,
        name: "Day"
    },{
        value: 604800,
        name: "Week"
    },{
        value: 2629743,
        name: "Month"
    },{
        value: 31556926,
        name: "Year"
    }
]

export function getUsedMultiplier(chunkSize: number) {

    let sizes = []
    let minPos = 0;
    let min = Number.MAX_VALUE;
    for (let i = 0; i < multiplierValues.length - 1; i++) {
        sizes[i] = Math.round(chunkSize) / multiplierValues[i].value;
        if (sizes[i] % 1 === 0 && sizes[i] < min) {
            min = sizes[i];
            minPos = i;
        }
    }

    return multiplierValues[minPos];
}