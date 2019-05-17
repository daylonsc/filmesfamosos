package br.android.com.filmesfamosos;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

import br.android.com.filmesfamosos.utilities.DateUtils;
import br.android.com.filmesfamosos.utilities.NetworkUtils;
import br.android.com.filmesfamosos.utilities.OpenMovieJsonUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetalheActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    @BindView(R.id.tv_titulo)
    TextView tvTitulo;

    @BindView(R.id.tv_sinopse)
    TextView tvSinopse;

    @BindView(R.id.iv_poster_filme)
    ImageView ivPosterFilme;

    @BindView(R.id.tv_data_lancamento)
    TextView tvDataLancamento;

    @BindView(R.id.tv_media_votos)
    TextView tvMediaVotos;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.sv_detalhe)
    NestedScrollView mScrollView;

    private String idSelecionado;
    private List<Filme> filmes;

    private SQLiteDatabase mDb;

    private RecyclerView mRecyclerView;
    private TrailerAdapter mTrailerAdapter;

    private RecyclerView mRecyclerViewReview;
    private ReviewAdapter mReviewAdapter;

    private static final String ID_FILME = "idFilme";
    private static final int DEFAULT_NUMBER = 0;

    private static final String FAVORITADO_SUCESSO = "Favoritado com Sucesso!";

    int scrollX = 0;
    int scrollY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ButterKnife.bind(this);

        idSelecionado = getIntent().getStringExtra(ID_FILME);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mRecyclerView.setAdapter(mTrailerAdapter);

        mRecyclerViewReview = (RecyclerView) findViewById(R.id.rv_reviews);
        mRecyclerViewReview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewReview.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter();
        mRecyclerViewReview.setAdapter(mReviewAdapter);

        new FilmeTask().execute();
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        scrollX = mScrollView.getScrollX();
        scrollY = mScrollView.getScrollY();
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{scrollX, scrollY});
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        if (position != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(position[0], position[1]);
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalhe_filme, menu);
        MenuItem item = menu.findItem(R.id.action_favorite);

        Cursor c = getFavorite();
        int id = DEFAULT_NUMBER;
        if (c.moveToFirst()) {
            do {
                id = c.getInt(c.getColumnIndex(Filme.FilmeEntry._ID));
            } while (c.moveToNext());
        }
        if (id > 0) {
            setIconFavorite(item, true);
        }

        return true;
    }

    private void setIconFavorite(MenuItem item, boolean isFavorito) {
        if (isFavorito) {
            item.setIcon(R.drawable.ic_star_white_24dp);
        } else {
            item.setIcon(R.drawable.ic_star_border_white_24dp);
        }
    }

    private Cursor getFavorite() {
        return getContentResolver().query(Uri.parse(Filme.FilmeEntry.CONTENT_URI + "/" + idSelecionado),
                null,
                null,
                null,
                Filme.FilmeEntry.COLUMN_TITULO);
    }

    private int removerFilme(long id) {

        return getContentResolver().delete(Uri.parse(Filme.FilmeEntry.CONTENT_URI + "/" + idSelecionado),
                null,
                null);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_favorite:
                actionFavorite(item);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void actionFavorite(MenuItem item) {
        boolean isFavorito = false;
        Cursor c = getFavorite();
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(Filme.FilmeEntry._ID));
                removerFilme(id);
            } while (c.moveToNext());
        } else {
            isFavorito = true;
            adicionarFavorito();
        }
        setIconFavorite(item, isFavorito);
    }

    private void adicionarFavorito() {
        Filme filme = filmes.get(0);
        ContentValues cv = new ContentValues();
        cv.put(Filme.FilmeEntry._ID, filme.getId());
        cv.put(Filme.FilmeEntry.COLUMN_TITULO, filme.getTitulo());
        cv.put(Filme.FilmeEntry.COLUMN_CAMINHO_IMG_POSTER, filme.getCaminhoImgPoster());
        cv.put(Filme.FilmeEntry.COLUMN_CAMINHO_IMG_BACK_DROP, filme.getCaminhoImgBackDrop());
        cv.put(Filme.FilmeEntry.COLUMN_DATA_LANCAMENTO, filme.getDataLancamento().toString());
        cv.put(Filme.FilmeEntry.COLUMN_MEDIA_VOTOS, filme.getMediaVotos());
        cv.put(Filme.FilmeEntry.COLUMN_SINOPSE, filme.getSinopse());
        cv.put(Filme.FilmeEntry.COLUMN_FAVORITO, true);

        Uri uri = getContentResolver().insert(Filme.FilmeEntry.CONTENT_URI, cv);

        if (uri != null) {
            Toast.makeText(getBaseContext(), FAVORITADO_SUCESSO, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(String trailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer));
        startActivity(intent);
    }


    public class FilmeTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            URL movieRequestUrl = NetworkUtils.buildUrl(idSelecionado);
            URL trailerRequestUrl = NetworkUtils.buildUrlTrailer(idSelecionado);
            URL reviewRequestUrl = NetworkUtils.buildUrlReview(idSelecionado);

            try {
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                String jsonTrailerResponse = NetworkUtils.getResponseFromHttpUrl(trailerRequestUrl);
                String jsonReviewResponse = NetworkUtils.getResponseFromHttpUrl(reviewRequestUrl);

                filmes = OpenMovieJsonUtils.getSimpleMovieStringsFromJson(DetalheActivity.this, jsonMovieResponse, jsonTrailerResponse, jsonReviewResponse, true);

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
                setMovieDataView();
            } else {
            }
        }

        private void setMovieDataView() {
            Filme filme = filmes.get(0);
            tvTitulo.setText(filme.getTitulo());
            tvSinopse.setText(filme.getSinopse());
            tvMediaVotos.setText(String.valueOf(filme.getMediaVotos()));
            tvDataLancamento.setText(DateUtils.format(filme.getDataLancamento(), "dd/MM/yyyy"));
            filme.getUrlTrailer();
            mTrailerAdapter.setTrailerData(filme.getUrlTrailer());
            mReviewAdapter.setReviewData(filme.getReview());

            Picasso
                    .with(DetalheActivity.this)
                    .load(filme.getCaminhoImgPoster())
                    .into(ivPosterFilme);

        }
    }
}
