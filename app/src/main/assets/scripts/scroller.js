var Srcoller = Vue.extend({
    template: "#scroller",
    mounted() {
        scrollers = scrollers.concat([this])

        this.iscroll = new IScroll(this.$el, {
            scrollbars: false,
            interactiveScrollbars: false,
            click: true,
            //disablePointer: true,
            disableTouch: false,
            fadeScrollbars: true,
            shrinkScrollbars: 'clip'
        })

        this.iscroll.refresh()        
    }
})

Vue.component("scroller", Srcoller)

var scrollers = []