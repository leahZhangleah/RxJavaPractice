package com.example.android.rxjavapractice;

import android.util.Log;

import com.example.android.rxjavapractice.model.Address;
import com.example.android.rxjavapractice.model.Note;
import com.example.android.rxjavapractice.model.Person;
import com.example.android.rxjavapractice.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MoreOperatorsExampleCode {
    private static String LOG_TAG = MainActivity.class.getName();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private void testCode(){
        compositeDisposable.add(
                getUsersObservable()
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<User>() {
                            @Override
                            public boolean test(User user) throws Exception {
                                return user.getGender().equalsIgnoreCase("female");
                            }
                        })
                        .subscribeWith(new DisposableObserver<User>(){

                            @Override
                            public void onNext(User user) {
                                Log.d(LOG_TAG,user.getName()+","+user.getGender());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        })

        );
        //operator: distinct
        getNotesObservable().observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .distinct()
                .subscribeWith(new DisposableObserver<Note>() {
                    @Override
                    public void onNext(Note note) {
                        Log.d(LOG_TAG,"OnNext: "+note.getNote());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"OnComplete");
                    }
                });
        //RxJavaMath operators: Max,Min,Sum,Average,Count
        //Reduce: applies a function on each item and emits the final result. First, it applies
        // a function to first item, takes the result and feeds back to same function on second
        // item. This process continuous until the last emission. Once all the items are over,
        // it emits the final result.
        Observable.range(1,10)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer int1, Integer int2) throws Exception {
                        Log.d(LOG_TAG,"the int1 is: "+ int1+"the int2 is: "+int2 + "the result is: "+ (int1-int2));
                        return int1-int2;
                    }
                })
                .subscribeWith(new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d(LOG_TAG,"Sum of numbers from 1 - 10 is: " + integer);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"OnComplete");
                    }
                });

        //math operators on custom object
        final List<Person> persons = new ArrayList<>();
        persons.addAll(getPersons());
        rx.Observable<Person> personObservable = rx.Observable.from(persons);
        /*Observable<Person> personObservable = Observable.create(new ObservableOnSubscribe<Person>() {
            @Override
            public void subscribe(ObservableEmitter<Person> emitter) throws Exception {
                for(Person person:persons){
                    if(!emitter.isDisposed()){
                        emitter.onNext(person);
                    }
                }
                if(!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });*/

        //Mathematical Operation on Custom DataTypes
        /*
        if(Build.VERSION.SDK_INT>=24){
            MathObservable.from(personObservable)
                    .max(Comparator.comparing(new Function<Person, Integer>() {
                        @Override
                        public Integer apply(Person person) {
                            return person.getAge();
                        }
                    }, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            if(o1>o2){
                                return o1;
                            }else{
                                return o2;
                            }
                        }
                    }))
                    .subscribe(new Observer<Person>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Person person) {
                            Log.d(LOG_TAG,"person with max age: "+ person.getName()+person.getAge());
                        }
                    });
                    */
        /*
        Concat operator combines output of two or more Observables into a single Observable. Concat
         operator always maintains the sequential execution without interleaving the emissions. So
         the first Observables completes its emission before the second starts and so forth if
         there are more observables.
         */
        String[] maleNames = new String[]{"Mark", "John", "Trump", "Obama"};
        String[] femaleNames = new String[]{"Lucy", "Scarlett", "April"};
        Observable maleObservable = getObservable(maleNames,"male");
        Observable femaleObservable = getObservable(femaleNames,"female");
        Observable.concat(maleObservable,femaleObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(LOG_TAG,user.getName() + user.getGender());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        //Merge also merges multiple Observables into a single Observable but it won’t maintain the
        // sequential execution.


        /*
        Map modifies each item emitted by a source Observable and emits the modified item.

        FlatMap, SwitchMap and ConcatMap also applies a function on each emitted item but instead
        of returning the modified item, it returns the Observable itself which can emit data again.

        FlatMap and ConcatMap work is pretty much same. They merges items emitted by multiple
        Observables and returns a single Observable.

        The difference between FlatMap and ConcatMap is, the order in which the items are emitted.

        FlatMap can interleave items while emitting i.e the emitted items order is not maintained.

        ConcatMap preserves the order of items. But the main disadvantage of ConcatMap is, it has
        to wait for each Observable to complete its work thus asynchronous is not maintained.

        SwitchMap is a bit different from FlatMap and ConcatMap. SwitchMap unsubscribe from previous
         source Observable whenever new item started emitting, thus always emitting the items from
         current Observable.
         */
        getUsersObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<User, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(User user) throws Exception {
                        return getAddressObservable(user);
                    }
                })
                .subscribeWith(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(LOG_TAG,"OnSubscribe");
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(LOG_TAG,"OnNext: "+ user.getName()+","+user.getGender()+","+user.getAddress().getAddress());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"All users emmitted");
                    }
                });
        Observable<Integer> integerObservable =
                Observable.fromArray(new Integer[]{1, 2, 3, 4, 5, 6});
        integerObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer integer) throws Exception {
                        return Observable.just(integer).delay(1, TimeUnit.SECONDS);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(LOG_TAG,"OnSubscribe");
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
                        Log.d(LOG_TAG,"All users emitted");
                    }
                });
    }

    private Observable<User> getAddressObservable(final User user){
        final String[] addresses = new String[]{
                "1600 Amphitheatre Parkway, Mountain View, CA 94043",
                "2300 Traverwood Dr. Ann Arbor, MI 48105",
                "500 W 2nd St Suite 2900 Austin, TX 78701",
                "355 Main Street Cambridge, MA 02142",
                "1689 Traverwood Dr. Ann Arbor, MI 48105",
                "712 W 2nd St Suite 2900 Austin, TX 78701",
                "6 Main Street Cambridge, MA 02142"
        };
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                Address address = new Address();
                address.setAddress(addresses[new Random().nextInt(2)]);
                if(!emitter.isDisposed()){
                    user.setAddress(address);
                    int sleepTime = new Random().nextInt(1000)+500;
                    Thread.sleep(sleepTime);
                    emitter.onNext(user);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }


    private Observable<User> getObservable(String[] names, String gender){
        final List<User> users = new ArrayList<>();
        for(String name: names){
            User user = new User();
            user.setName(name);
            user.setGender(gender);
            users.add(user);
        }
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                for(User user:users){
                    if(!emitter.isDisposed()){
                        Thread.sleep(500);
                        emitter.onNext(user);
                    }
                }
                if(!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io());
    }


    private Observable<User> getUsersObservable(){
        String[] maleUsers = new String[]{"Mark", "John", "Trump", "Obama"};
        String[] femaleUsers = new String[]{"Lucy", "Scarlett", "April"};
        final List<User> users = new ArrayList<>();
        for(String name: maleUsers){
            User user = new User();
            user.setName(name);
            user.setGender("male");
            users.add(user);
        }

        for(String name:femaleUsers){
            User user = new User();
            user.setName(name);
            user.setGender("female");
            users.add(user);
        }
        return Observable.create(new ObservableOnSubscribe<User>() {
            @Override
            public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                if(!emitter.isDisposed()){
                    for(User user: users){
                        emitter.onNext(user);
                    }
                }
                if(!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    //equals() and hashCode() methods are overridden to make comparison between objects work.


    private Observable<Note> getNotesObservable(){
        final List<Note> notes = prepareNotes();
        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for(Note note: notes){
                    if(!emitter.isDisposed()){
                        emitter.onNext(note);
                    }
                }
                if (!emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes(){
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Buy tooth paste!"));
        notes.add(new Note(2, "Call brother!"));
        notes.add(new Note(3, "Call brother!"));
        notes.add(new Note(4, "Pay power bill!"));
        notes.add(new Note(5, "Watch Narcos tonight!"));
        notes.add(new Note(6, "Buy tooth paste!"));
        notes.add(new Note(7, "Pay power bill!"));
        return notes;
    }

    private List<Person> getPersons(){
        List<Person> persons = new ArrayList<>();
        Person p1 = new Person("Lucy", 24);
        persons.add(p1);

        Person p2 = new Person("John", 45);
        persons.add(p2);

        Person p3 = new Person("Obama", 51);
        persons.add(p3);

        return persons;
    }




}
