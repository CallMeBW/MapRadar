package de.bwirth.mapradar.androidutil;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.TypedValue;
import android.view.Display;

/**
 * <code>
 * Project: IP App MapRadar <br>
 * Date: 10.01.2015            <br></code>
 * Description:                    <br>
 */
public class AndroidUtil {
    public static int getScreenHeight(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.y;
    }

    public static int getScreenWidth(Activity act) {
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int dipInPixels(int dp, Resources res) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    public abstract static class VoidAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected final Void doInBackground(Void... params) {
            doInBackground();
            return null;
        }

        @Override
        protected final void onPostExecute(Void aVoid) {
            onPostExecute();
        }

        protected abstract void doInBackground();

        protected void onPostExecute() {
        }

        /**
         * @param runParallel if set, this asyncTask runs parallel to other AsyncTasks as long as there is enough Threadpool size!!
         */
        public void run(boolean runParallel) {
            if (runParallel) {
                executeOnExecutor(THREAD_POOL_EXECUTOR);
                return;
            }
            execute();
        }
    }

    public abstract static class VoidThrowAsyncTask<E extends Exception> extends AsyncTask<Void, Void, Void> {
        @Override
        protected final Void doInBackground(Void... params) {
            try {
                doInBackground();
            } catch (Exception e) {
                onExceptionCaught(e);
            }
            return null;
        }

        @Override
        protected final void onPostExecute(Void aVoid) {
            onPostExecute();
        }

        protected abstract void onExceptionCaught(Exception exc);
        protected abstract void doInBackground() throws E;

        protected void onPostExecute() {
        }

        /**
         * @param runParallel if set, this asyncTask runs parallel to other AsyncTasks as long as there is enough Threadpool size!!
         */
        public void run(boolean runParallel) {
            if (runParallel) {
                executeOnExecutor(THREAD_POOL_EXECUTOR);
                return;
            }
            execute();
        }
    }
}
