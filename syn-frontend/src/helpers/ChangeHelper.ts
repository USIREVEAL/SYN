import {
    Change,
    isAddChange,
    isDeleteChange,
    isModifyChange,
    isMoveChange,
    isRenameChange
} from "../types/server/Project.types"


export function getChangeDisplayName(change: Change): string {
    if (isModifyChange(change)) {
        return "MODIFY"
    } else if (isMoveChange(change)) {
        return "MOVE"
    } else if (isRenameChange(change)) {
        return "RENAME"
    } else if (isDeleteChange(change)) {
        return "DELETE"
    } else if (isAddChange(change)) {
        return "ADD"
    }

    return "UNDEFINED"
}
