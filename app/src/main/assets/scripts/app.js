function getDisplayNumber(value, digits) {
    return value != undefined ? value.toFixed(digits || 0) : "-"
}

function getToTime(value) {

    function pad(num, size) {
        let s = num + ""
        while (s.length < size)
            s = "0" + s
        return s
    }

    if (value) {
        const hour = Math.floor(value / 3600)
        value %= 3600
        const minute = Math.floor(value / 60)
        value %= 60
        return hour 
            ? `${hour}:${pad(minute, 2)}:${pad(value, 2)}` 
            : `${pad(minute, 2)}:${pad(value, 2)}`
    } else
        return "-"
}

Vue.filter('displayNumber', getDisplayNumber)
Vue.filter('toTime', getToTime)

var app = new Vue({
    el: '#app'
})