"use strict";
const tracks = document.getElementById("tracks");
(function () {
    mobileKitApp.setOnDrawerOpened(() => {
        trackFactory = document.getElementById('trackTemplate').content.querySelector('li');
        const tracksMarker = document.getElementById("tracksMarker");
        const trackList = document.getElementById("trackList");
    });
})();
const OPENED = "opened";
var trackFactory;
mobileKitApp.setDrawerClickListener("tracks", element => {
    const trackList = document.getElementById("trackList");
    trackList.innerHTML = "";
    const tracksMarker = document.getElementById("tracksMarker");
    mobileKitApp.drawer.refresh();
    if (tracksMarker.classList.contains(OPENED))
        tracksMarker.classList.remove(OPENED);
    else {
        tracksMarker.classList.add(OPENED);
        Native.fillTracks();
    }
});
mobileKitApp.setDrawerClickListener("trackList", element => Native.onTrackSelected(Number.parseInt(element.dataset.nr)));
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
