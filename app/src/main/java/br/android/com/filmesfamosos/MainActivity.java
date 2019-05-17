package br.android.com.filmesfamosos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.android.com.filmesfamosos.data.FilmeDbHelper;
import br.android.com.filmesfamosos.utilities.NetworkUtils;
import br.android.com.filmesfamosos.utilities.OpenMovieJsonUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, FilmeAdapter.FilmeAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private FilmeAdapter mFilmeAdapter;
    private static final String ORDEM_POPULAR = "popular";
    private static final String ORDEM_MAIS_BEM_AVALIADOS = "top_rated";
    private static final String ORDEM_FAVORITOS = "favoritos";
    private static final String ID_FILME = "idFilme";
    private static final String MENSAGEM_ERRO_LOAD_DATA = "Falha ao carregar dados assíncronamente.";
    private static String ordemSelecionada = "";
    private List<Filme> filmes;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;

    private SQLiteDatabase mDb;
    private static final String TAG = MainActivity.class.getSimpleName();

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Parcelable layoutManagerSavedState;

    private static final int TASK_LOADER_ID = 0;
    private final String SEM_CONEXAO = "Sem conexão com a internet, ou servidor indisponível";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FilmeDbHelper dbHelper = new FilmeDbHelper(this);
        mDb = dbHelper.getWritableDatabase();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_filmes);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setHasFixedSize(true);

        mFilmeAdapter = new FilmeAdapter(this, layoutManagerSavedState, mRecyclerView);
        mRecyclerView.setAdapter(mFilmeAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        if(ordemSelecionada.equals("")){
            ordemSelecionada = ORDEM_POPULAR;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (mBundleRecyclerViewState != null) {
            layoutManagerSavedState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);

            mFilmeAdapter = new FilmeAdapter(this, layoutManagerSavedState, mRecyclerView);
            mRecyclerView.setAdapter(mFilmeAdapter);
        }

        setTitle(getTitulo());

        if (ordemSelecionada.equals(ORDEM_FAVORITOS)) {
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        } else {
            loadMovieData(ordemSelecionada);
        }
    }

    private String getTitulo() {
        String title = "";
        if (ordemSelecionada.equals(ORDEM_MAIS_BEM_AVALIADOS)) {
            title = (String) getResources().getText(R.string.action_filmes_mais_bem_avaliados);
        } else if (ordemSelecionada.equals(ORDEM_POPULAR)) {
            title = (String) getResources().getText(R.string.action_filmes_populares);
        } else {
            title = (String) getResources().getText(R.string.action_filmes_favoritos);
        }

        return title;
    }

    private void loadMovieData(String ordem) {
        showMovieDataView();

        if (!NetworkUtils.isNetworkConnected(this)) {
            showErrorMessage(SEM_CONEXAO);
        } else {
            new FilmeTask().execute(ordem);
        }

    }

    @Override
    public void onClick(Filme filme) {
        Intent intent = new Intent(this, DetalheActivity.class);
        intent.putExtra(ID_FILME, String.valueOf(filme.getId()));
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mFilmeData = null;

            @Override
            protected void onStartLoading() {
                if (mFilmeData != null) {
                    deliverResult(mFilmeData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(Filme.FilmeEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            Filme.FilmeEntry.COLUMN_TITULO);

                } catch (Exception e) {
                    Log.e(TAG, MENSAGEM_ERRO_LOAD_DATA);
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mFilmeData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setmRecyclerViewFavoritos(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class FilmeTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            String ordem = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(ordem);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                filmes = OpenMovieJsonUtils.getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse, null, null, false);

                return null;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (filmes != null && !filmes.isEmpty()) {
                showMovieDataView();
                mFilmeAdapter.setFilmeData(filmes);
            } else {
                showErrorMessage(null);
            }
        }
    }

    private void setmRecyclerViewFavoritos(Cursor cursor) {
        List<Filme> filmes = new ArrayList<Filme>();
        if (cursor.moveToFirst()) {
            do {
                Filme filme = new Filme();
                filme = novoFilme(cursor);
                filmes.add(filme);
            } while (cursor.moveToNext());
        }
        cursor.close();

        mFilmeAdapter.setFilmeData(filmes);
    }

    private Filme novoFilme(Cursor c) {
        Filme filme = new Filme();
        filme.setId(c.getInt(c.getColumnIndex(Filme.FilmeEntry._ID)));
        filme.setTitulo(c.getString(c.getColumnIndex(Filme.FilmeEntry.COLUMN_TITULO)));
        filme.setCaminhoImgPoster(c.getString(c.getColumnIndex(Filme.FilmeEntry.COLUMN_CAMINHO_IMG_POSTER)));
        filme.setCaminhoImgBackDrop(c.getString(c.getColumnIndex(Filme.FilmeEntry.COLUMN_CAMINHO_IMG_BACK_DROP)));

        filme.setMediaVotos(c.getDouble(c.getColumnIndex(Filme.FilmeEntry.COLUMN_MEDIA_VOTOS)));
        filme.setSinopse(c.getString(c.getColumnIndex(Filme.FilmeEntry.COLUMN_SINOPSE)));

        return filme;
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_populares:
                ordemSelecionada = ORDEM_POPULAR;
                setTitle(getTitulo());
                loadMovieData(ordemSelecionada);
                return true;

            case R.id.action_bem_avaliados:
                ordemSelecionada = ORDEM_MAIS_BEM_AVALIADOS;
                setTitle(getTitulo());
                loadMovieData(ordemSelecionada);
                return true;

            case R.id.action_filmes_favoritos:
                ordemSelecionada = ORDEM_FAVORITOS;
                setTitle(getTitulo());
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showErrorMessage(String mensagem) {
        if (mensagem != null) {
            mErrorMessageDisplay.setText(mensagem);
        }
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
