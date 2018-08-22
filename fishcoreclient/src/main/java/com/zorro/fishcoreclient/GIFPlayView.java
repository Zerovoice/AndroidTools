package com.zorro.fishcoreclient;

/*
 *
 * Title: .
 * Description: .
 *
 * Created by Zorro(zeroapp@126.com) on 2018/8/22.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.view.View;

public class GIFPlayView extends View {
    private Movie mMovie;
    private long mMovieStart;


    public GIFPlayView(Context context) {
        super(context);
        mMovie = Movie.decodeStream(getResources().openRawResource(0));//TODO add gif source like R.drawable.animation

    }

    public void onDraw(Canvas canvas) {

        long now = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) { // first time
            mMovieStart = now;
        }
        if (mMovie != null) {

            int dur = mMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }
            int relTime = (int) ((now - mMovieStart) % dur);
            mMovie.setTime(relTime);
            mMovie.draw(canvas, 0, 0);
            invalidate();
        }
    }

}

