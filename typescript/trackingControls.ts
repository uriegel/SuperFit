
interface ITrackingControls {
    toggleMode(): void
}

declare var NativeTrackingControls: ITrackingControls

const mode = document.getElementById("mode")
mode.onclick = () => {
    NativeTrackingControls.toggleMode()
}

