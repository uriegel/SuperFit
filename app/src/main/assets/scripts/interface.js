"use strict";
var ServiceState;
(function (ServiceState) {
    ServiceState["Stopped"] = "Stopped";
    ServiceState["Starting"] = "Starting";
    ServiceState["Started"] = "Started";
    ServiceState["Stopping"] = "Stopping";
})(ServiceState || (ServiceState = {}));
function onBackPressed() {
    if (!mobileKitApp.onBackPressed())
        Native.finish();
}
function onStateChanged(state) {
    mainApp.onStateChanged(state);
}

function showControls() {
    mainApp.showControls()
}

function showTracks() {
    mainApp.showTracks()
}

mobileKitApp.setOnHapticFeedback(() => Native.doHapticFeedback());
