package bignerdranch.android.earthquake;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by modus on 3/5/18.
 */

public class EarthquakeProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.bignerdranch.earthquakeprovider/earthquakes");

    public static final String KEY_ID = "_id";
    public static final String KEY_DATE = "date";
    public static final String KEY_DETAILS = "details";
    public static final String KEY_SUMMARY = "summary";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LNG = "longtitude";
    public static final String KEY_MAGNITUDE = "magnitude";
    public static final String KEY_LINK = "link";

    private static final int QUAKES = 1;
    private static final int QUAKE_ID = 2;
    private static final UriMatcher uriMAtcher;

    static {
        uriMAtcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMAtcher.addURI("com.bignerdranch.earthquakeprovider", "earthquakes", QUAKES);
        uriMAtcher.addURI("com.bignerdranch.earthquakeprovider", "earthquakes/#", QUAKE_ID );
    }


    EarthQuakeDatabaseHelper dbHelper;


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new EarthQuakeDatabaseHelper(context, EarthQuakeDatabaseHelper.DATABASE_NAME, null,EarthQuakeDatabaseHelper.DATABASE_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sort) {
    SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb =new SQLiteQueryBuilder();
        qb.setTables(EarthQuakeDatabaseHelper.EARTHQUAKE_TABLE);
        switch (uriMAtcher.match(uri)){
            case QUAKE_ID: qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
            break;
            default:break;
        }
        String orderBy;
        if (TextUtils.isEmpty(sort)){
            orderBy = KEY_DATE;
        }else {
            orderBy = sort;
        }

        Cursor c = qb.query(database, projection,selection,selectionArgs, null,null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMAtcher.match(uri)){
            case QUAKES: return "vnd.android.cursor.dir/vnd.bignerdranch.earthquake";
            case QUAKE_ID: return  "vnd.android.cursos.dir/vnd.bignerdranch.earthquake";
            default: throw new IllegalArgumentException("Usuported uri : " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri _uri, @Nullable ContentValues _initialValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long rowID = database.insert(EarthQuakeDatabaseHelper.EARTHQUAKE_TABLE, "quake", _initialValues);
        if (rowID > 0){
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLException("Failed to insert into " + _uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] whereArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int count;
        switch (uriMAtcher.match(uri)){
            case QUAKES:
                count = database.delete(EarthQuakeDatabaseHelper.EARTHQUAKE_TABLE, where,whereArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.delete(EarthQuakeDatabaseHelper.EARTHQUAKE_TABLE,
                        KEY_ID + "="
                + segment
                + (!TextUtils.isEmpty(where)? " AND (" + where + ')' : " "), whereArgs);
                break;
                default:throw new IllegalArgumentException("Usuported uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String where, @Nullable String[] whereArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count;
        switch (uriMAtcher.match(uri)){
            case QUAKES:
                count = database.update(EarthQuakeDatabaseHelper.EARTHQUAKE_TABLE, values, where,whereArgs);
                break;
            case QUAKE_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.update(EarthQuakeDatabaseHelper.EARTHQUAKE_TABLE, values, KEY_ID + "=" +segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : " "), whereArgs);
                break;

                default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
            return count;
    }

    private static class EarthQuakeDatabaseHelper extends SQLiteOpenHelper{

        private static final String TAG = "EarthquakeProvider";

        private static final String DATABASE_NAME = "earthquakes.db";
        private static final int DATABASE_VERSION = 1;
        private static final String EARTHQUAKE_TABLE = "earthquakes";

        private static final String DATABASE_CREATE ="create table " + EARTHQUAKE_TABLE + " (" + KEY_ID + "integer primary key autoincrement, "
                + KEY_DATE  + " INTEGER, "
                + KEY_DETAILS + " TEXT, "
                + KEY_SUMMARY + " TEXT, "
                + KEY_LOCATION_LAT + " FLOAT, "
                + KEY_LOCATION_LNG + " FLOAT, "
                + KEY_MAGNITUDE + " FLOAT, "
                + KEY_LINK + " TEXT);";

        private SQLiteDatabase earthquakeDB;
        public EarthQuakeDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG,"Updating database version from " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + EARTHQUAKE_TABLE);
            onCreate(db);
        }
    }


}
