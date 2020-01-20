package com.example.android.rxjavapractice;

import android.util.Log;

import com.example.android.rxjavapractice.model.Note;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

public class DifferentObservablesAndObservers {
    /*
       Single always emits only one value or throws an error. The same job can be done using
       Observable too with a single emission but Single always makes sure there is an emission.
        A use case of Single would be making a network call to get response as the response will
         be fetched at once.
        */
    private static String LOG_TAG = MainActivity.class.getName();

    /*
    Observable	Observer	    # of emissions
    Observable	Observer	    Multiple or None
    Single	    SingleObserver	One
    Maybe	    MaybeObserver	One or None
    Flowable	Observer	    Multiple or None
    Completable	CompletableObserver	None
     */
    public void test(){
        //single observable and singleobserver
        getNoteObservable().observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(getSingleObserver());

        //Maybe observable may or may not emits a value. This observable can be used when you are
        // expecting an item to be emitted optionally.
        getMaybeNoteObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getMaybeNoteObserver());

        /*
        Completable observable won’t emit any data instead it notifies the status of the task either
         success or failure. This observable can be used when you want to perform some task and not
         expect any value. A use case would be updating some data on the server by making PUT request.
         */
        Note note = new Note(1,"home work!");
        updateNote(note).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getCompletableObserver());

        /*
        Flowable observable should be used when an Observable is generating huge amount of
        events/data than the Observer can handle. As per doc, Flowable can be used when the source
         is generating 10k+ events and subscriber can’t consume it all.
         */
           /*
        When Flowable is used, the overflown emissions has to be handled using a strategy called
        Backpressure. Otherwise it throws an exception such as MissingBackpressureException or
        OutOfMemoryError
         */
        getFlowableObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .reduce(0,new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        return integer+integer2;
                    }
                })
                .subscribe(getFlowableObserver());




    }

    private SingleObserver<Integer> getFlowableObserver(){
        return new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Integer integer) {
                Log.d(LOG_TAG,"OnSuccess: "+integer);
            }

            @Override
            public void onError(Throwable e) {

            }
        };
    }

    private Flowable<Integer> getFlowableObservable(){
        return Flowable.range(1,100);
    }

    /**
     * Assume this making PUT request to server to update the Note
     */
    private Completable updateNote(Note note){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter emitter) throws Exception {
                if(!emitter.isDisposed()){
                    Thread.sleep(1000);
                    emitter.onComplete();
                }
            }
        });
    }

    private CompletableObserver getCompletableObserver(){
        return new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG,"OnComplete: Note updated successfully");
            }

            @Override
            public void onError(Throwable e) {

            }
        };
    }
    private MaybeObserver<Note> getMaybeNoteObserver(){
        return new MaybeObserver<Note>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Note note) {
                Log.d(LOG_TAG,"OnSuccess: "+note.getNote());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
    /**
     * Emits optional data (0 or 1 emission)
     * But for now it emits 1 Note always
     */
    private Maybe<Note> getMaybeNoteObservable(){
        return Maybe.create(new MaybeOnSubscribe<Note>() {
            @Override
            public void subscribe(MaybeEmitter<Note> emitter) throws Exception {
                Note note = new Note(1,"Call brother!");
                if(!emitter.isDisposed()){
                    emitter.onSuccess(note);
                }
            }
        });
    }

    private SingleObserver<Note> getSingleObserver(){
        return new SingleObserver<Note>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(LOG_TAG,"OnSubscribe");
            }

            @Override
            public void onSuccess(Note note) {
                Log.d(LOG_TAG,"OnSuccess: "+note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG,"OnError: "+e.getMessage());
            }
        };
    }

    private Single<Note> getNoteObservable(){
        return Single.create(new SingleOnSubscribe<Note>() {
            @Override
            public void subscribe(SingleEmitter<Note> emitter) throws Exception {
                Note note = new Note(1,"Buy milk!");
                emitter.onSuccess(note);
            }
        });
    }

}
