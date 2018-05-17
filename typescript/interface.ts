interface INative {
    getState(): string
    doHapticFeedback(): void
    finish(): void
    start(): void
    stop(): void
    display(): void
    fillTracks(): void
    fillMaps(): void
    onTrackSelected(number: Number): void
    onMapSelected(map: string): void
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

