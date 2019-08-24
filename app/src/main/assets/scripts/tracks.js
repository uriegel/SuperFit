
var Tracks = Vue.extend({
    template: "#tracks",
    data() { return data }
})
Vue.component("tracks", Tracks)

var data = {
    tracks: ["Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie","Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie","Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie", "Eine§", "2", "Dreie","Eine§", "2", "Dreie"]
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

