package com.sample.clinic.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.sample.clinic.Interfaces.FragmentFinish;
import com.sample.clinic.R;

public class FirstFragment extends Fragment {

    private Context mContext;
    private ProgressBar pb;
    private FragmentFinish fn;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    public FirstFragment(Context context, FragmentFinish finish) {
        mContext = context;
        fn = finish;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.fragment_first, container, false);
        initViews(mView);
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 20;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            pb.setProgress(progressStatus);
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("FIRST_FRAGMENT", e.getMessage());
                    }
                }
                if (progressStatus == 100) {
                    fn.onFinishFirstFragment();
                }
            }
        }).start();
        return mView;
    }

    private void initViews(View mView) {
        pb = mView.findViewById(R.id.progress);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}