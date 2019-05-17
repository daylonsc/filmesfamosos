package br.android.com.filmesfamosos.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.android.com.filmesfamosos.Filme.FilmeEntry;

public class FilmeDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "filme.db";

    private static final int DATABASE_VERSION = 1;

    // Constructor
    public FilmeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " + FilmeEntry.TABLE_NAME + " (" +
                FilmeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FilmeEntry.COLUMN_TITULO + " TEXT NOT NULL, " +
                FilmeEntry.COLUMN_CAMINHO_IMG_POSTER + " TEXT NOT NULL, " +
                FilmeEntry.COLUMN_CAMINHO_IMG_BACK_DROP + " TEXT NOT NULL, " +
                FilmeEntry.COLUMN_DATA_LANCAMENTO + " DATE NOT NULL, " +
                FilmeEntry.COLUMN_MEDIA_VOTOS + " DOUBLE (10, 2) NOT NULL, " +
                FilmeEntry.COLUMN_SINOPSE + " TEXT NOT NULL, " +
                FilmeEntry.COLUMN_URL_TRAILER + " TEXT, " +
                FilmeEntry.COLUMN_REVIEW + " TEXT, " +
                FilmeEntry.COLUMN_FAVORITO + " BOOLEAN " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_WAITLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FilmeEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
