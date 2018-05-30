"use strict";
const switcher = document.getElementById("switcher");
const deleteBtn = document.getElementById("delete");
const dialog = document.getElementsByClassName("dialog")[0];
const saveBtn = document.getElementById("save");
switcher.onclick = () => {
    if (dialog.classList.contains("hidden"))
        dialog.classList.remove("hidden");
    else
        dialog.classList.add("hidden");
};
saveBtn.onclick = () => NativeMapControls.saveTrack();
