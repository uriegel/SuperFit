
var Display = Vue.extend({
    template: "#display",
    data() { return data }
})
Vue.component("display", Display)

var data = {
    cadence: null,
    speed: null,
    heartRate: null,
    distance: null,
    timeSpan: null,
    averageSpeed: null,
    maxSpeed: null,
    gpsActive: false
}

function setGpsActive() { data.gpsActive = true }
function onHeartRateChanged(heartRate) { data.heartRate = heartRate }
function onBikeDataChanged(speed, maxSpeed, averageSpeed, distance, timeSpan, cadence) {
    data.speed = speed
    data.maxSpeed = maxSpeed
    data.averageSpeed = averageSpeed
    data.distance = distance
    data.cadence = cadence
    data.timeSpan = timeSpan
}

