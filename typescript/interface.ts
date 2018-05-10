interface INative {
    getState(): string
    doHapticFeedback(): void
    finish(): void
    start(): void
    stop(): void
    display(): void
    fillTracks(): void
    onTrackSelected(number: Number): void
}

declare var Native: INative

enum ServiceState {
    Stopped = "Stopped",
    Starting = "Starting",
    Started = "Started",
    Stopping = "Stopping"
}

function onBackPressed() {
    if (!mobileKitApp.onBackPressed())
        Native.finish()
}

function onStateChanged(state: ServiceState) {
    mainApp.onStateChanged(state)
}

mobileKitApp.setOnHapticFeedback(() => Native.doHapticFeedback())

