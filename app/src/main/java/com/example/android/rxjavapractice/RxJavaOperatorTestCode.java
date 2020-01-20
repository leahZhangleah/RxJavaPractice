package com.example.android.rxjavapractice;

import android.util.Log;

import com.example.android.rxjavapractice.model.Note;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RxJavaOperatorTestCode {
    //put it back to MainActivity to test the code in the future
    /*
        Basic observable, Observer, subscriber example
        Observable emits list of animal names
     */
    private static String LOG_TAG = MainActivity.class.getName();

    //You can see Disposable introduced in this example
    //private Disposable disposable;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public void testCode(){
        Observable<String> animalsObservable = getAnimalsObservable();
        DisposableObserver<String> animalsObserver = getAnimalsObserver();
        DisposableObserver<String> animalsAllCapsObserver = getAnimalsAllCapsObserver();
        /**
         * filter() is used to filter out the animal names starting with 'b'*/
        compositeDisposable.add(
                animalsObservable.observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toLowerCase().startsWith("b");
                            }
                        })
                        .subscribeWith(animalsObserver)
        );
        /**
         * filter() is used to filter out the animal names starting with 'c'
         * map() is used to transform all the characters to UPPER case
         * */
        compositeDisposable.add(
                animalsObservable.observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toLowerCase().startsWith("c");
                            }
                        })
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(String s) {
                                return s.toUpperCase();
                            }
                        })
                        .subscribeWith(animalsAllCapsObserver)
        );
        compositeDisposable.add(
                getNotesObservable().observeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Note, Note>() {
                            @Override
                            public Note apply(Note note) {
                                String mNote = note.getNote().toUpperCase();
                                note.setNote(mNote);
                                return note;
                            }
                        })
                        .subscribeWith(getNotesObserver())
        );
        compositeDisposable.add(
                //operator:range,filter,map
                Observable.range(1,20)
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<Integer>() {
                            @Override
                            public boolean test(Integer integer) throws Exception {
                                return integer % 2 == 0;
                            }
                        })
                        .map(new Function<Integer, String>() {
                            @Override
                            public String apply(Integer integer) {
                                return integer + " is even number";
                            }
                        })
                        .subscribeWith(new DisposableObserver<String>(){
                            @Override
                            public void onNext(String value) {
                                Log.d(LOG_TAG,"Number: "+value);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(LOG_TAG,"OnError: "+e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.d(LOG_TAG,"All numbers are emitted");
                            }
                        })
        );

        compositeDisposable.add(
                getIntObservable().subscribeWith(new DisposableObserver<Integer>(){
                    @Override
                    public void onNext(Integer value) {
                        Log.d(LOG_TAG,"OnNext: "+value);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"Completed");
                    }
                })
        );

        compositeDisposable.add(
                Observable.range(1,12)
                        .observeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .buffer(3)//Buffer gathers items emitted by an Observable into batches and emit the batch instead of emitting one item at a time.
                        .subscribeWith(new DisposableObserver<List<Integer>>() {
                            @Override
                            public void onNext(List<Integer> value) {
                                //get List<Integer> instead of Integer
                                Log.d(LOG_TAG,"OnNext");
                                for(Integer integer:value){
                                    Log.d(LOG_TAG,"Item: "+integer);
                                }

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                Log.d(LOG_TAG,"All items are emitted");
                            }
                        })
        );

        //Skip(n) operator skips the emission of first N items emitted by an Observable.
        Observable.range(1,10)
                .skip(4)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(LOG_TAG,"Subscribed");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(LOG_TAG,"OnNext: "+integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"Completed");
                    }
                });
        //skipLast(n) skips the last N emissions from an Observable.
        Observable.range(1,10)
                .skipLast(4)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(LOG_TAG,"Subscribed");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(LOG_TAG,"OnNext: "+integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"Completed");
                    }
                });
        //take(n) acts exactly opposite to skip. It takes first N emissions of an Observable.
        Observable.range(1,10)
                .take(4)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(LOG_TAG,"Subscribed");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(LOG_TAG,"OnNext: "+integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"Completed");
                    }
                });
        //takeLast(n) emits last N items from an Observable.
        Observable.range(1,10)
                .takeLast(4)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(LOG_TAG,"Subscribed");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(LOG_TAG,"OnNext: "+integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"Completed");
                    }
                });
        //Distinct operator filters out items emitted by an Observable by avoiding duplicate items in the list.
        Observable<Integer> numbersObservable = Observable.just(10,10, 15, 20, 100, 200, 100, 300, 20, 100);
        numbersObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .distinct()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(LOG_TAG,"OnNext: "+integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private DisposableObserver<String> getAnimalsObserver(){
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String value) {
                Log.d(LOG_TAG,"Name: "+value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG,"OnError: "+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG,"All items are emitted");
            }
        };
    }

    private DisposableObserver<String> getAnimalsAllCapsObserver(){
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String value) {
                Log.d(LOG_TAG,"Name: "+value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG,"OnError: "+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG,"All items are emitted");
            }
        };
    }


    private Observable<String> getAnimalsObservable(){
        //for more than 10 items
        return Observable.fromArray(
                "Ant", "Ape",
                "Bat", "Bee", "Bear", "Butterfly",
                "Cat", "Crab", "Cod",
                "Dog", "Dove",
                "Fox", "Frog"
        );
    }

    //disposable example
    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        Log.d(LOG_TAG,"the observer is disposed");
    }*/

    private List<Note> prepareNotes(){
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1,"buy tooth paste!"));
        notes.add(new Note(2, "call brother!"));
        notes.add(new Note(3, "watch narcos tonight!"));
        notes.add(new Note(4, "pay power bill!"));
        return notes;
    }


    private Observable<Note> getNotesObservable(){
        final List<Note> notes=prepareNotes();
        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for(Note note:notes){
                    if(!emitter.isDisposed()){
                        emitter.onNext(note);
                    }
                }
                if(!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    private DisposableObserver<Note> getNotesObserver(){
        return new DisposableObserver<Note>() {
            @Override
            public void onNext(Note value) {
                Log.d(LOG_TAG,"Id: "+value.getId()+", Note: "+value.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(LOG_TAG,"OnError: "+e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(LOG_TAG,"All notes are emitted");
            }
        };
    }

    private Observable<Integer> getIntObservable(){
        /*
        just() – Makes only 1 emission. .just(new Integer[]{1, 2, 3}) makes one emission with Observer callback as onNext(Integer[] integers)
        fromArray() – Makes N emissions. .fromArray(new Integer[]{1, 2, 3}) makes three emission with Observer callback as onNext(Integer integer)
         */
        //just
        //this will emit 10 numbers
        Observable.just(1,2,3,4,5,6,7,8,9,10);
        //this will emit one array
        Integer[] numbers = {1,2,3,4,5,6,7,8,9,10};
        Observable.just(numbers);
        //take in an Iterable, and emit each item
        Observable.fromArray(numbers);
        //Range() creates an Observable from a sequence of generated integers. The function generates sequence of integers by taking starting number and length.
        Observable.range(1,10);
        //Repeat() creates an Observable that emits an item or series of items repeatedly. You can also pass an argument to limit the number of repetitions.
        //repeat 3 times
        return Observable.range(1,4)
                .repeat(3);
    }
}
