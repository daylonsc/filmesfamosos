package br.android.com.filmesfamosos.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import br.android.com.filmesfamosos.Filme;

import static br.android.com.filmesfamosos.Filme.FilmeEntry.TABLE_NAME;

public class FilmeContentProvider extends ContentProvider {

    public static final int FILMES = 100;
    public static final int FILME_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    public static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Filme.AUTHORITY, Filme.PATH_FILME, FILMES);
        uriMatcher.addURI(Filme.AUTHORITY, Filme.PATH_FILME + "/#", FILME_WITH_ID);

        return uriMatcher;
    }

    private FilmeDbHelper mFilmeDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mFilmeDbHelper = new FilmeDbHelper(context);
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mFilmeDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case FILMES:
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(Filme.FilmeEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mFilmeDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case FILMES:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FILME_WITH_ID:
                // Get the id from the URI
                String id = uri.getPathSegments().get(1);

                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                String mSelection = "_id=?";
                String[] mSelectionArgs = new String[]{id};

                // Construct a query as you would normally, passing in the selection/args
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mFilmeDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int filmesDeleted; // starts as 0

        switch (match) {
            case FILME_WITH_ID:
                String id = uri.getPathSegments().get(1);
                filmesDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (filmesDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return filmesDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
