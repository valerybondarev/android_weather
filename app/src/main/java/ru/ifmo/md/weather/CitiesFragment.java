package ru.ifmo.md.weather;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;


import ru.ifmo.md.weather.db.WeatherContentProvider;


public class CitiesFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    private CityCursorAdapter cityCursorAdapter;

    private ListView mainView = null;

    private OnCitySelectedListener mCitySelectedListener = null;

    public interface OnCitySelectedListener {
        public void onCitySelected(int index, long cityId);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt(LoadWeatherService.RESULT);
                String error = bundle.getString(LoadWeatherService.ERROR_MSG);
                if (resultCode == 1) {
                    Toast.makeText(context,
                            "Download complete.", Toast.LENGTH_SHORT).show();
                    cityCursorAdapter.changeCursor(context.getContentResolver().query(WeatherContentProvider.CONTENT_URI_CITIES, null, null, null, null));
                    cityCursorAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Error: " + error,
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    public CitiesFragment() {
        super();
    }

    @Override
    public void onStart() {
        super.onStart();
        setListAdapter(cityCursorAdapter);
        cityCursorAdapter.notifyDataSetChanged();
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getLoaderManager().initLoader(0, null, this);
        cityCursorAdapter = new CityCursorAdapter(this.getActivity(), null, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
        cityCursorAdapter.notifyDataSetChanged();
        getActivity().registerReceiver(receiver, new IntentFilter(LoadWeatherService.NOTIFICATION));
    }

    @Override
    public void onPause() {
        super.onPause();
        cityCursorAdapter.notifyDataSetChanged();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.withAppendedPath(WeatherContentProvider.CONTENT_URI_CITIES, id + "");
                getActivity().getContentResolver().delete(uri, null, null);
                updateCursor();
                return true;
            }
        });
    }

    public void setOnCitySelectedListener(OnCitySelectedListener listener) {
        mCitySelectedListener = listener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mCitySelectedListener) {
            mCitySelectedListener.onCitySelected(position, cityCursorAdapter.getItemId(position));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this.getActivity(),
                WeatherContentProvider.CONTENT_URI_CITIES, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cityCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cityCursorAdapter.swapCursor(null);
    }

    public void updateCursor() {
        getLoaderManager().restartLoader(0, null, this);
        cityCursorAdapter.notifyDataSetChanged();
    }
}
