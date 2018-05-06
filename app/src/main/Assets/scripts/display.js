"use strict";
function onBikeDataChanged(speed, maxSpeed, averageSpeed, distance, timeSpan, cadence) {
    speedElement.innerText = speed.toFixed(1);
    maxSpeedElement.innerText = maxSpeed.toFixed(1);
    distanceElement.innerText = distance.toFixed(2);
    cadenceElement.innerText = cadence.toString();
    avgSpeedElement.innerText = averageSpeed.toFixed(1);
    const hour = Math.floor(timeSpan / 3600);
    timeSpan %= 3600;
    const minute = Math.floor(timeSpan / 60);
    timeSpan %= 60;
    if (hour)
        timeElement.innerText = `${hour}:${pad(minute, 2)}:${pad(timeSpan, 2)}`;
    else
        timeElement.innerText = `${pad(minute, 2)}:${pad(timeSpan, 2)}`;
}
function onHeartRateChanged(heartRate) {
    if (heartRate)
        heartRateElement.innerText = heartRate.toString();
}
function setGpsActive() {
    const gps = document.getElementsByClassName("gps")[0];
    gps.classList.remove("hidden");
}
function pad(num, size) {
    let s = num + "";
    while (s.length < size)
        s = "0" + s;
    return s;
}
const heartRateElement = document.getElementById('heartRate');
const speedElement = document.getElementById('speed');
const distanceElement = document.getElementById('distance');
const cadenceElement = document.getElementById('cadence');
const timeElement = document.getElementById('time');
const avgSpeedElement = document.getElementById('avgSpeed');
const maxSpeedElement = document.getElementById('maxSpeed');
const display = document.getElementById('display');
if (location.hash) {
    const trackNumber = location.hash.substring(1);
    getTrack(trackNumber);
}
async function getTrack(trackNumber) {
}
DisplayNative.onReady();
document.onclick = e => {
    DisplayNative.onReady();
};
