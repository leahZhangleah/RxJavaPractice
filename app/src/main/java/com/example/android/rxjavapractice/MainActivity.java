package com.example.android.rxjavapractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    /*
    SubscribeOn operator designates which thread the Observable will begin operating on, no matter
    at what point in the chain of operators that operator is called. ObserveOn, on the other hand,
    affects the thread that the Observable will use below where that operator appears. For this
    reason, you may call ObserveOn multiple times at various points during the chain of Observable
    operators in order to change on which threads certain of those operators operate.
     */

    private static String LOG_TAG = MainActivity.class.getName();
    //buffer example variable
    TextView txtTapResult, txtTapResultMax;
    Button btnTapArea;
    private Disposable disposable;
    private int maxTaps = 0;

    //debounce example variable
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    TextView searchResultTv;
    EditText searchEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //buffer example code
        txtTapResult = findViewById(R.id.tap_result);
        txtTapResultMax = findViewById(R.id.tap_result_max_control);
        btnTapArea = findViewById(R.id.layout_tap_area);
        RxView.clicks(btnTapArea)
                .map(new Function<Object, Integer>() {
                    @Override
                    public Integer apply(Object o) {
                        return 1;
                    }
                })
                .buffer(3,TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d(LOG_TAG,"OnNext: "+ integers.size() + "taps received");
                        if(integers.size()>0){
                            maxTaps = integers.size()>maxTaps? integers.size():maxTaps;
                            txtTapResult.setText(String.format("Received %d taps in 3 secs",integers.size()));
                            txtTapResultMax.setText(String.format("Maximum of %d taps received in this session", maxTaps));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(LOG_TAG,"OnError: "+e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(LOG_TAG,"OncCompleted");
                    }
                });
        //debounce example code
        searchResultTv = findViewById(R.id.query_string_tv);
        searchEditView = findViewById(R.id.search_edit_view);
        compositeDisposable.add(
                RxTextView.textChangeEvents(searchEditView)
                .skipInitialValue()
                .debounce(300,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchQuery())
        );
        searchResultTv.setText("Search query will be accumulated every 300 milliseconds.");

    }

    //debounce example code
    private DisposableObserver<TextViewTextChangeEvent> searchQuery(){
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                Log.d(LOG_TAG,"search String: "+ textViewTextChangeEvent.text());
                searchResultTv.setText("Query: "+textViewTextChangeEvent.text());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        compositeDisposable.clear();
    }
}
