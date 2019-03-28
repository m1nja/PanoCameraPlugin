package com.slicejobs.panacamera.cameralibrary.helper;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;

public class RxUtil {
    public RxUtil() {
    }

    public static <T> FlowableTransformer<T, T> rxSchedulerHelper() {
        return new FlowableTransformer<T, T>() {
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Flowable<T> createData(final T t) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            public void subscribe(FlowableEmitter<T> emitter) throws Exception {
                try {
                    emitter.onNext(t);
                    emitter.onComplete();
                } catch (Exception var3) {
                    emitter.onError(var3);
                }

            }
        }, BackpressureStrategy.BUFFER);
    }
}
