package com.slicejobs.panacamera.cameralibrary.helper;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

public class RxBus {
    private final FlowableProcessor<Object> bus;

    private RxBus() {
        this.bus = PublishProcessor.create().toSerialized();
    }

    public static RxBus getInstance() {
        return RxBus.RxBusHolder.sInstance;
    }

    public void post(Object o) {
        this.bus.onNext(o);
    }

    public <T> Flowable<T> toFlowable(Class<T> eventType) {
        return this.bus.onBackpressureDrop().ofType(eventType);
    }

    public <T> Disposable toDefaultFlowable(Class<T> eventType, Consumer act) {
        return this.bus.ofType(eventType).compose(RxUtil.rxSchedulerHelper()).subscribe(act);
    }

    private static class RxBusHolder {
        private static final RxBus sInstance = new RxBus();

        private RxBusHolder() {
        }
    }
}
