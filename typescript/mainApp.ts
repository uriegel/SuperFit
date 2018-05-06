
class MainApp {
    constructor() {
        this.starter.onclick = () => Native.start()
        this.stopper.onclick = () => Native.stop()
        this.display.onclick = () => Native.display()
        const state = Native.getState()
    }

    onStateChanged(state: ServiceState) {
        switch (state) {
            case ServiceState.Stopped:
                this.starter.disabled = false
                this.display.disabled = true
                this.stopper.disabled = true
                break
            case ServiceState.Started:
                this.starter.disabled = true
                this.display.disabled = false
                this.stopper.disabled = false
                break
            case ServiceState.Starting:
            case ServiceState.Stopping:
                this.starter.disabled = true
                this.display.disabled = true
                this.stopper.disabled = true
                break
        }
    }

    private readonly starter = document.getElementsByClassName('starter')[0] as HTMLButtonElement
    private readonly stopper = document.getElementsByClassName('stopper')[0] as HTMLButtonElement
    private readonly display = document.getElementsByClassName('display')[0] as HTMLButtonElement
}

var mainApp = new MainApp()