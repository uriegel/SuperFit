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
    setClickListener(cls: string, onClick: (element: HTMLElement) => void): void;
    /**
     * Returns true, if a Scroller is included
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
    private readonly clickAnimations;
}
declare class ClickAnimation {
    private element;
    private isParent;
    constructor(element: HTMLElement, isParent: boolean);
    hasClass(cls: string): boolean;
    setClickListener(onClick: (element: HTMLElement) => void): void;
    private beginClick(evt);
    private animateClick(clickedElement, evt);
    private findDirectChild(element);
    private readonly htmlStyles;
    private readonly clickedColor;
    private inClick;
    private getClicked;
    private onMouseClick;
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
    setDrawerClickListener(cls: string, onClick: (element: HTMLElement) => void): void;
    private initialize();
    private titleButtonLeftClicked;
    private onHapticFeedbackFunction;
    private onDrawerOpenedFunction;
    private scrollers;
    private onInitialized;
}
declare const mobileKitApp: MobileKitApp;
