declare function injectStylesheet(): void;
declare class ScrollerParams {
    scrollbars: boolean;
    interactiveScrollbars: boolean;
    click: boolean;
    disablePointer: boolean;
    disableTouch: boolean;
    fadeScrollbars: boolean;
    shrinkScrollbars: 'clip';
}
declare class IScroll {
    constructor(element: HTMLElement, param: ScrollerParams);
    refresh(): void;
}
declare class Scroller {
    private scrollContent;
    constructor(scrollContent: HTMLElement);
    refresh(): void;
    private iscroll;
}
declare class SuperKitShader {
    private shader;
    constructor(shader: HTMLDivElement);
    setTransitionOff(): void;
    opacity: any;
    transistionDuration: number;
    isActive: boolean;
}
declare class Drawer {
    constructor(drawerContent: HTMLDivElement);
    readonly isOpen: boolean;
    private isOpenValue;
    open(): void;
    close(withoutTransition?: boolean): void;
    refresh(): void;
    /**
     * Gibt true zurück, wenn ein Scroller eingebaut wurde
     * */
    private createDrawer();
    private destroyDrawer();
    private addTouchHandling();
    /**
     * Komplette Drawer-Schicht, bestehend aus Shader und Drawer
     */
    private drawerLayer;
    private drawer;
    private scroller;
    private readonly shader;
}
declare class ClickAnimation {
    private clickableElement;
    constructor(clickableElement: HTMLElement);
    private beginClick(evt);
    private animateClick(clickedElement, evt);
    private readonly htmlStyles;
    private readonly clickedColor;
    private readonly backgroundColor;
    private inClick;
    private getClicked;
    private onClick;
}
declare class MobileKitApp {
    constructor();
    drawer: Drawer;
    onHapticFeedback(): void;
    onDrawerOpened(): void;
    onBackPressed(): boolean;
    setOnHapticFeedback(callback: () => void): void;
    setOnDrawerOpened(callback: () => void): void;
    refreshScroller(): void;
    private initialize();
    private titleButtonLeftClicked;
    private onHapticFeedbackFunction;
    private onDrawerOpenedFunction;
    private scrollers;
}
declare const mobileKitApp: MobileKitApp;
