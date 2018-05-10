"use strict";
(function () {
    mobileKitApp.setOnDrawerOpened(() => {
        const OPENED = "opened";
        trackFactory = document.getElementById('trackTemplate').content.querySelector('li');
        const tracksMarker = document.getElementById("tracksMarker");
        const tracks = document.getElementById("tracks");
        const trackList = document.getElementById("trackList");
        tracks.onclick = () => {
            Native.doHapticFeedback();
            trackList.innerHTML = "";
            mobileKitApp.drawer.refresh();
            if (tracksMarker.classList.contains(OPENED))
                tracksMarker.classList.remove(OPENED);
            else {
                tracksMarker.classList.add(OPENED);
                Native.fillTracks();
            }
        };
    });
})();
var trackFactory;
function onTracks(tracks) {
    const trackList = document.getElementById("trackList");
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
        li.onclick = () => Native.onTrackSelected(Number.parseInt(li.dataset.nr));
        return li;
    });
    lis.forEach(li => trackList.appendChild(li));
    const drawerHeader = document.getElementsByClassName("drawerHeader")[0];
    const drawer = drawerHeader.parentElement;
    if (drawer.classList.contains("hidden"))
        mobileKitApp.drawer.refresh();
    else {
        drawer.style.top = "0px";
        drawer.style.transition = "top 0.5s";
        setTimeout(() => {
            drawer.style.top = `-${drawerHeader.clientHeight}px`;
        }, 50);
        const transitionend = () => {
            drawer.removeEventListener("transitionend", transitionend);
            setTimeout(() => {
                drawerHeader.classList.add("hidden");
                trackList.parentElement.style.transition = "";
                trackList.parentElement.style.top = "0px";
                mobileKitApp.drawer.refresh();
            }, 50);
        };
        drawer.addEventListener("transitionend", transitionend);
    }
    //function fillNext(lisToFill: HTMLLIElement[]) {
    //    const lis = lisToFill.slice(0, 5)
    //    lis.forEach(li => trackList.appendChild(li))
    //    const restLis = lisToFill.slice(5)
    //    if (restLis.length > 0)
    //        setTimeout(() => fillNext(restLis), 150)
    //}
    //fillNext(lis)
}
