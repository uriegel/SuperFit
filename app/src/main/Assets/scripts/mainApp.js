"use strict";
class MainApp {
    constructor() {
        this.starter = document.getElementsByClassName('starter')[0];
        this.stopper = document.getElementsByClassName('stopper')[0];
        this.resetter = document.getElementsByClassName('resetter')[0];
        this.display = document.getElementsByClassName('display')[0];
        this.starter.onclick = () => Native.start();
        this.stopper.onclick = () => Native.stop();
        this.resetter.onclick = () => Native.reset();
        this.display.onclick = () => Native.display();
        const state = Native.getState();
    }
    onStateChanged(state) {
        switch (state) {
            case ServiceState.Stopped:
                this.starter.disabled = false;
                this.display.disabled = true;
                this.resetter.disabled = true;
                this.stopper.disabled = true;
                break;
            case ServiceState.Started:
                this.starter.disabled = true;
                this.display.disabled = false;
                this.resetter.disabled = false;
                this.stopper.disabled = false;
                break;
            case ServiceState.Starting:
            case ServiceState.Stopping:
                this.starter.disabled = true;
                this.display.disabled = true;
                this.resetter.disabled = true;
                this.stopper.disabled = true;
                break;
        }
    }
}
var mainApp = new MainApp();
