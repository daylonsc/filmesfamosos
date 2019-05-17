package br.android.com.filmesfamosos;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import java.util.Date;
import java.util.List;

/**
 * Created by daylo on 24/10/2017.
 */

public class Filme implements Parcelable{

    private int id;
    private String titulo;
    private String caminhoImgPoster;
    private String caminhoImgBackDrop;
    private Date dataLancamento;
    private double mediaVotos;
    private String sinopse;
    private List<String> urlTrailer;
    private List<String> review;
    private boolean isFavorito;

    public static final String AUTHORITY = "br.android.com.filmesfamosos";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FILME = "filme";

    public Filme() {

    }

    protected Filme(Parcel in) {
        id = in.readInt();
        titulo = in.readString();
        caminhoImgPoster = in.readString();
        caminhoImgBackDrop = in.readString();
        mediaVotos = in.readDouble();
        sinopse = in.readString();
        urlTrailer = in.createStringArrayList();
        review = in.createStringArrayList();
        isFavorito = in.readByte() != 0;
    }

    public static final Creator<Filme> CREATOR = new Creator<Filme>() {
        @Override
        public Filme createFromParcel(Parcel in) {
            return new Filme(in);
        }

        @Override
        public Filme[] newArray(int size) {
            return new Filme[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(titulo);
        parcel.writeString(caminhoImgPoster);
        parcel.writeString(caminhoImgBackDrop);
        parcel.writeDouble(mediaVotos);
        parcel.writeString(sinopse);
        parcel.writeStringList(urlTrailer);
        parcel.writeStringList(review);
        parcel.writeByte((byte) (isFavorito ? 1 : 0));
    }


    public static final class FilmeEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FILME).build();
        public static final String TABLE_NAME = "filme";

        public static final String COLUMN_TITULO = "titulo";
        public static final String COLUMN_CAMINHO_IMG_POSTER = "caminhoImgPoster";
        public static final String COLUMN_CAMINHO_IMG_BACK_DROP = "caminhoImgBackDrop";
        public static final String COLUMN_DATA_LANCAMENTO = "dataLancamento";
        public static final String COLUMN_MEDIA_VOTOS = "mediaVotos";
        public static final String COLUMN_SINOPSE = "sinopse";
        public static final String COLUMN_URL_TRAILER = "urlTrailer";
        public static final String COLUMN_REVIEW = "review";
        public static final String COLUMN_FAVORITO = "favorito";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCaminhoImgPoster() {
        return caminhoImgPoster;
    }

    public void setCaminhoImgPoster(String caminhoImgPoster) {
        this.caminhoImgPoster = caminhoImgPoster;
    }

    public String getCaminhoImgBackDrop() {
        return caminhoImgBackDrop;
    }

    public void setCaminhoImgBackDrop(String caminhoImgBackDrop) {
        this.caminhoImgBackDrop = caminhoImgBackDrop;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public double getMediaVotos() {
        return mediaVotos;
    }

    public void setMediaVotos(double mediaVotos) {
        this.mediaVotos = mediaVotos;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public List<String> getUrlTrailer() {
        return urlTrailer;
    }

    public void setUrlTrailer(List<String> urlTrailer) {
        this.urlTrailer = urlTrailer;
    }

    public List<String> getReview() {
        return review;
    }

    public void setReview(List<String> review) {
        this.review = review;
    }

    public boolean isFavorito() {
        return isFavorito;
    }

    public void setFavorito(boolean favorito) {
        isFavorito = favorito;
    }
}
