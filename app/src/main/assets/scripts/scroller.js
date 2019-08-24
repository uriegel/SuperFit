var Srcoller = Vue.extend({
    template: "#scroller",
    props: [ 'showscrollbar'],
    mounted() {
        scrollers = scrollers.concat([this])
        this.iscroll = new IScroll(this.$el, {
            scrollbars: this.showscrollbar,
            interactiveScrollbars: this.showscrollbar,
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