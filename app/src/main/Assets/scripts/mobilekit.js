"use strict";
function injectStylesheet() {
    const sheet = document.createElement('style');
    sheet.innerHTML =
        `:root {
    --mobilekit-color-header-background: #2196F3;
    --mobilekit-color-header: white;
}

.hidden {
    display: none !important;
}

body {
    font-family: sans-serif;
    padding: 0px;
    margin: 0px;
    overflow: hidden;
    height: 100vh;
    display: flex;
    flex-direction: column;
}

header {
    box-sizing: border-box;
    padding-right: 10px;
    background-color: var(--mobilekit-color-header-background);
    height: 60px;
    display: flex;
    align-items: center;
    font-size: 1.3em;
    font-weight: 400;
    color: var(--mobilekit-color-header);
    z-index: 20;
    width: 100%;
    box-shadow: 0px 1px 7px 2px rgba(0, 0, 0, 0.3);
    vertical-align: middle;
}

header>img {
    width: 32px;
    margin: 10px;
}

header>span {
    flex: 1;
}

.mobilekit-content {
    position: relative;
    flex-grow: 1;
    overflow-x: hidden;
    overflow-y: auto;
    margin: 0px;
    -webkit-padding-start: 0px;
}

.mobilekit-scroll {
    display: none !important;
}

.mobilekit-wrapper {
    position: absolute;
    z-index: 100;
    top: 0px;
    bottom: 0px;
    width: 100%;
    overflow: hidden;
}

.mobilekit-scroll-content {
    position: absolute;
    z-index: 1;
    width: 100%;
    padding: 0;
}

.mobilekit-drawer {
    position: absolute;
    height: 100%;
    overflow: hidden;
    width: calc(100% - 75px);
    background-color: white;
    top: 0px;
    z-index: 2000;
    left: calc(75px - 100%);
    transition-timing-function: ease-out;
}
.mobilekit-drawer.navigationDrawerOpen {
    left: 0px;
}

.mobilekit-shader {
    position: absolute;
    width: 100%;
    height: 100%;
    top: 0px;
    background-color: black;
    opacity: 0;
    z-index: 1000;
}
.mobilekit-shader.isActive {
    opacity: 0.65;
} `;
    const stsh = document.styleSheets;
    document.head.insertBefore(sheet, document.head.firstChild);
}
class Scroller {
    constructor(scrollContent) {
        this.scrollContent = scrollContent;
        const parent = scrollContent.parentElement;
        scrollContent.remove();
        const wrapper = document.createElement("div");
        scrollContent.classList.add("mobilekit-scroll-content");
        scrollContent.classList.remove("mobilekit-scroll");
        wrapper.appendChild(scrollContent);
        wrapper.classList.add("mobilekit-wrapper");
        const scroller = document.createElement("div");
        scroller.appendChild(wrapper);
        if (scrollContent.classList.contains("mobilekit-content")) {
            scrollContent.classList.remove("mobilekit-content");
            scroller.classList.add("mobilekit-content");
        }
        const scrollbar = !scrollContent.classList.contains("mobilekit-noscrollbar");
        parent.appendChild(scroller);
        this.iscroll = new IScroll(wrapper, {
            scrollbars: scrollbar,
            interactiveScrollbars: scrollbar,
            click: true,
            disablePointer: true,
            disableTouch: false,
            fadeScrollbars: true,
            shrinkScrollbars: 'clip'
        });
    }
    refresh() {
        this.iscroll.refresh();
    }
}
class SuperKitShader {
    constructor(shader) {
        this.shader = shader;
        this.shader.classList.add("mobilekit-shader");
    }
    setTransitionOff() {
        this.shader.style.transition = '';
    }
    set opacity(value) {
        if (value)
            this.shader.style.opacity = value;
        else if (value == 0)
            this.shader.style.opacity = "0";
        else
            this.shader.style.removeProperty('opacity');
    }
    set transistionDuration(value) {
        this.shader.style.transition = `opacity ${value}s`;
    }
    set isActive(value) {
        if (value)
            this.shader.classList.add('isActive');
        else
            this.shader.classList.remove('isActive');
    }
}
///<reference path="shader.ts" />
//const SUPERKIT_DRAWER_TOUCH_MARGIN = 20
class Drawer {
    constructor(drawerContent) {
        /**
         * Komplette Drawer-Schicht, bestehend aus Shader und Drawer
         */
        this.drawerLayer = document.createElement("div");
        this.drawer = document.createElement("div");
        const clickAnimations = Array.from(drawerContent.getElementsByClassName("mobilekit-click-animation"))
            .map(el => new ClickAnimation(el, false));
        this.clickAnimations = clickAnimations.concat(Array.from(drawerContent.getElementsByClassName("mobilekit-click-animation-children"))
            .map(el => new ClickAnimation(el, true)));
        this.drawerLayer.appendChild(this.drawer);
        const shader = document.createElement("div");
        this.drawerLayer.appendChild(shader);
        this.drawer.appendChild(drawerContent);
        this.drawer.classList.add("mobilekit-drawer");
        this.shader = new SuperKitShader(shader);
        this.addTouchHandling();
    }
    get isOpen() {
        return this.isOpenValue;
    }
    open() {
        let timeout = 50;
        if (!this.isOpen) {
            if (this.createDrawer())
                timeout = 250;
        }
        this.shader.opacity = null;
        this.shader.transistionDuration = 0.3;
        this.drawer.style.left = '';
        this.drawer.style.transition = "left 0.3s";
        setTimeout(() => {
            this.drawer.classList.add('navigationDrawerOpen');
            this.shader.isActive = true;
        }, timeout);
        const transitionend = () => {
            this.drawer.removeEventListener("transitionend", transitionend);
            mobileKitApp.onDrawerOpened();
        };
        this.drawer.addEventListener("transitionend", transitionend);
    }
    close(withoutTransition) {
        this.drawer.classList.remove('navigationDrawerOpen');
        this.shader.isActive = false;
        this.shader.transistionDuration = 0.3;
        this.drawer.style.transition = "left 0.3s";
        if (!withoutTransition) {
            const transitionend = (evt) => {
                this.drawer.removeEventListener("transitionend", transitionend);
                this.destroyDrawer();
            };
            this.drawer.addEventListener("transitionend", transitionend);
        }
        else
            this.destroyDrawer();
    }
    refresh() {
        if (this.scroller)
            this.scroller.refresh();
    }
    setClickListener(cls, onClick) {
        const animation = this.clickAnimations.filter(ca => ca.hasClass(cls))[0];
        animation.setClickListener(onClick);
    }
    /**
     * Returns true, if a Scroller is included
     * */
    createDrawer() {
        let result = false;
        if (!this.isOpenValue) {
            document.body.appendChild(this.drawerLayer);
            const scrollContent = document.getElementsByClassName("mobilekit-scroll")[0];
            if (scrollContent) {
                this.scroller = new Scroller(scrollContent);
                result = true;
            }
            this.isOpenValue = true;
            return result;
        }
    }
    destroyDrawer() {
        if (this.isOpen) {
            this.isOpenValue = false;
            document.body.removeChild(this.drawerLayer);
        }
    }
    addTouchHandling() {
        let touchStart = false;
        window.addEventListener('touchstart', evt => {
            //if (evt.touches.length != 1 || Activity.isDialogOpen())
            if (evt.touches.length != 1)
                return;
            var initialX = evt.touches[0].clientX;
            var initialY = evt.touches[0].clientY;
            if (!this.isOpen && initialX > 20)
                return;
            touchStart = true;
            let moving = false;
            let diff;
            let drawerWidth;
            let recentPos;
            let previousPos;
            let verticalSwipe = false;
            const touchmove = (evt) => {
                if (verticalSwipe)
                    return;
                diff = evt.touches[0].clientX - initialX;
                if (!moving) {
                    if (touchStart) {
                        touchStart = false;
                        const diffy = evt.touches[0].clientY - initialY;
                        if (Math.abs(diff) < Math.abs(diffy)) {
                            verticalSwipe = true;
                            return;
                        }
                    }
                    touchStart = false;
                    moving = true;
                    if (!this.isOpen)
                        this.createDrawer();
                    else {
                        drawerWidth = this.drawer.offsetWidth;
                        diff += drawerWidth;
                        initialX -= drawerWidth;
                    }
                    drawerWidth = this.drawer.offsetWidth;
                    this.drawer.style.transition = '';
                    this.shader.transistionDuration = 0.1;
                }
                if (diff < drawerWidth) {
                    this.shader.opacity = (diff / drawerWidth) * 0.65;
                    this.drawer.style.left = `${diff - drawerWidth}px`;
                }
                previousPos = recentPos;
                recentPos = evt.touches[0].clientX;
                evt.stopPropagation();
            };
            const touchend = (evt) => {
                window.removeEventListener('touchmove', touchmove, true);
                window.removeEventListener('touchend', touchend, true);
                if (!moving)
                    return;
                moving = false;
                const fling = recentPos - previousPos;
                let dismiss = ((drawerWidth / 2) - diff) > 0;
                if (dismiss) {
                    if (fling > 5)
                        dismiss = false;
                }
                else {
                    if (fling < -5)
                        dismiss = true;
                }
                let transitionCalc = dismiss ? diff : drawerWidth - diff;
                if (transitionCalc < 0)
                    transitionCalc = 0;
                let duration = transitionCalc / drawerWidth * 0.3;
                if (duration < 0.01)
                    duration = 0.01;
                this.shader.transistionDuration = duration;
                this.drawer.style.transition = `left ${duration}s`;
                const left = dismiss ? -drawerWidth : 0;
                const opacity = dismiss ? 0 : 0.65;
                setTimeout(() => {
                    this.drawer.style.left = `${left}px`;
                    this.shader.opacity = opacity;
                }, 50);
                const transitionend = () => {
                    this.drawer.removeEventListener("transitionend", transitionend);
                    if (dismiss)
                        this.close(true);
                    else {
                        this.drawer.style.removeProperty("left");
                        this.shader.opacity = null;
                        this.drawer.classList.add('navigationDrawerOpen');
                        this.shader.isActive = true;
                        mobileKitApp.onDrawerOpened();
                    }
                };
                this.drawer.addEventListener("transitionend", transitionend);
                evt.stopPropagation();
            };
            window.addEventListener('touchmove', touchmove, true);
            window.addEventListener('touchend', touchend, true);
        }, true);
    }
}
class ClickAnimation {
    constructor(element, isParent) {
        this.element = element;
        this.isParent = isParent;
        this.htmlStyles = window.getComputedStyle(document.querySelector("html"));
        this.clickedColor = this.htmlStyles.getPropertyValue('--button-clicked');
        this.inClick = false;
        this.onMouseClick = element.onclick;
        element.onclick = null;
        element.addEventListener("click", evt => this.beginClick(evt), true);
        this.getClicked = evt => { return isParent ? this.findDirectChild(evt.target) : element; };
    }
    hasClass(cls) {
        return (this.element.classList.contains(cls));
    }
    setClickListener(onClick) {
        this.onClick = onClick;
    }
    beginClick(evt) {
        if (this.inClick)
            return;
        const clickedElement = this.getClicked(evt);
        if (!clickedElement)
            return;
        this.inClick = true;
        mobileKitApp.onHapticFeedback();
        this.animateClick(clickedElement, evt);
    }
    animateClick(clickedElement, evt) {
        const canvas = document.createElement("canvas");
        canvas.width = clickedElement.offsetWidth;
        canvas.height = clickedElement.offsetHeight;
        const context = canvas.getContext("2d");
        let lastindex = 1;
        const dateNow = new Date().getTime();
        const centerX = evt.offsetX;
        const centerY = evt.offsetY;
        let actionExecuted = false;
        const fillStyle = getComputedStyle(clickedElement, null).getPropertyValue('background-color');
        const drawCircle = (index) => {
            const alpha = index / 10;
            if (!actionExecuted && alpha > 0.9) {
                if (this.onClick)
                    this.onClick(this.isParent ? this.findDirectChild(evt.target) : evt.target);
                else
                    this.onMouseClick(evt);
                actionExecuted = true;
            }
            if (alpha > 1) {
                this.inClick = false;
                clickedElement.style.background = "";
                return false;
            }
            const radius = (canvas.height / 2 - 6) + alpha * (canvas.width / 2 - (canvas.height / 2 - 6));
            context.clearRect(0, 0, canvas.width, canvas.height);
            context.beginPath();
            context.arc(centerX, centerY, radius, 0, 2 * Math.PI, false);
            context.fillStyle = this.clickedColor;
            context.globalAlpha = 1 - alpha;
            context.fill();
            context.fillStyle = fillStyle;
            context.globalCompositeOperation = 'destination-over';
            context.globalAlpha = 1;
            context.fillRect(0, 0, canvas.width, canvas.height);
            var url = canvas.toDataURL();
            clickedElement.style.background = `url(${url})`;
            return true;
        };
        const animate = () => {
            var date = new Date().getTime();
            var index = Math.round((date - dateNow) / 40);
            if (index == lastindex) {
                window.requestAnimationFrame(animate);
                return;
            }
            lastindex = index;
            if (!drawCircle(index))
                return;
            window.requestAnimationFrame(animate);
        };
        requestAnimationFrame(animate);
    }
    findDirectChild(element) {
        const isDirectChild = (element) => { return element.parentElement == this.element; };
        if (isDirectChild(element))
            return element;
        else
            return this.findDirectChild(element.parentElement);
    }
}
///<reference path="drawer.ts" />
class MobileKitApp {
    constructor() {
        this.scrollers = [];
        this.onInitialized = [];
        injectStylesheet();
        document.addEventListener("DOMContentLoaded", () => this.initialize());
    }
    onHapticFeedback() {
        if (this.onHapticFeedbackFunction)
            this.onHapticFeedbackFunction();
    }
    onDrawerOpened() {
        if (this.onDrawerOpenedFunction)
            this.onDrawerOpenedFunction();
    }
    onBackPressed() {
        if (this.drawer && this.drawer.isOpen) {
            this.drawer.close();
            return true;
        }
        else
            return false;
    }
    setOnHapticFeedback(callback) {
        this.onHapticFeedbackFunction = callback;
    }
    setOnDrawerOpened(callback) {
        this.onDrawerOpenedFunction = callback;
    }
    refreshScroller() {
        this.scrollers.forEach(n => n.refresh());
    }
    setDrawerClickListener(cls, onClick) {
        if (this.drawer)
            this.drawer.setClickListener(cls, onClick);
        else
            this.onInitialized.push(() => this.drawer.setClickListener(cls, onClick));
    }
    initialize() {
        const titleButtonLeft = document.getElementsByClassName("mobilekit-title-button-left")[0];
        if (titleButtonLeft)
            titleButtonLeft.onclick = this.titleButtonLeftClicked;
        const scrollContent = document.getElementsByClassName("mobilekit-scroll")[0];
        if (scrollContent)
            this.scrollers.push(new Scroller(scrollContent));
        const clickAnimations = Array.from(document.getElementsByClassName("mobilekit-click-animation"));
        clickAnimations.forEach(clickAnimation => new ClickAnimation(clickAnimation, false));
        const drawerContent = document.getElementById("mobilekit-drawer-content");
        if (drawerContent) {
            this.drawer = new Drawer(drawerContent.content.querySelector('div'));
            if (titleButtonLeft)
                titleButtonLeft.onclick = () => {
                    this.onHapticFeedback();
                    this.drawer.open();
                };
        }
        this.onInitialized.forEach(action => action());
    }
}
const mobileKitApp = new MobileKitApp();
