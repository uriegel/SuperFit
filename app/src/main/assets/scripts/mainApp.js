"use strict";
const tracks = document.getElementById("tracks");
const trackFactory = document.getElementById('trackTemplate').content.querySelector('li');
const trackList = document.getElementById("trackList");
trackList.onclick = evt => {
    const li = evt.target.closest("li")
    if (!li)
        alert("Hilfe")
    else
        Native.onTrackSelected(Number.parseInt(li.dataset.nr));
}
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
    showControls() {
        document.getElementsByClassName("controls")[0].classList.remove("hidden")
        document.getElementsByClassName("tracks")[0].classList.add("hidden")

    }

    showTracks() {
        document.getElementsByClassName("controls")[0].classList.add("hidden")
        document.getElementsByClassName("tracks")[0].classList.remove("hidden")
        trackList.innerHTML = "";
        Native.fillTracks();
    }

}

function onTracks(tracks) {
    const lis = tracks.map(n => {
        const li = trackFactory.cloneNode(true);
        li.dataset.nr = n.trackNr.toString();
        const row = li.querySelector(".title");
        const date = new Date(n.time);
        row.innerHTML = date.toLocaleString(undefined, {
            year: "numeric", month: "2-digit",
            day: "2-digit", hour: "2-digit", minute: "2-digit"
        });
        const speed = li.querySelector(".speed");
        speed.innerHTML = `${n.averageSpeed.toFixed(1)} km/h`;
        const distance = li.querySelector(".distance");
        distance.innerHTML = `${n.distance.toFixed(0)} km`;
        return li;
    });
    const trackList = document.getElementById("trackList");
    lis.forEach(li => trackList.appendChild(li));
    //function fillNext(lisToFill: HTMLLIElement[]) {
    //    const lis = lisToFill.slice(0, 5)
    //    lis.forEach(li => trackList.appendChild(li))
    //    const restLis = lisToFill.slice(5)
    //    if (restLis.length > 0)
    //        setTimeout(() => fillNext(restLis), 150)
    //}
    //fillNext(lis)
}

function deleteTrack(trackNr) {
    const trackList = document.getElementById("trackList");
    const lis = Array.from(trackList.children);
    const li = lis.find(li => li.dataset.nr == trackNr.toString());
    trackList.removeChild(li);
}

var mainApp = new MainApp();
