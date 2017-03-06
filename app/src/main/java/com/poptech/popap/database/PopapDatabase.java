package com.poptech.popap.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.poptech.popap.bean.LanguageBean;
import com.poptech.popap.bean.PhotoBean;
import com.poptech.popap.bean.SoundBean;
import com.poptech.popap.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * The Class PopapDatabase.
 */
public class PopapDatabase extends SQLiteOpenHelper {


    private static final int DB_VERSION = 1;


    private static String DATABASE_NAME = "PopapDatabase";

    private static PopapDatabase mInstance;


    @SuppressLint("SdCardPath")
    private String mTemplate = "/data/data/%s/databases/";


    private SQLiteDatabase mSQLite;


    private Context mContext;

    private String mPackageName;

    private String mPath;

    public interface Tables {
        String PHOTOS = "photos";
        String SOUNDS = "sounds";
        String LANGUAGES = "languages";
    }

    public interface PhotoColumns {
        String PHOTO_ID = "photo_id";
        String PHOTO_PATH = "photo_path";
    }

    public interface SoundColumns {
        String SOUND_ID = "sound_id";
        String SOUND_PATH = "sound_path";
        String SOUND_MARK = "sound_mark";
    }

    public interface LanguageColumns {
        String LANGUAGE_ID = "language_id";
        String LANGUAGE_ACTIVE = "language_active";
    }

    public PopapDatabase(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        // TODO Auto-generated constructor stub
        mContext = context;
        mPackageName = context.getPackageName();
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            this.mPath = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            this.mPath = String.format(mTemplate, mPackageName);
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE " + Tables.PHOTOS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PhotoColumns.PHOTO_ID + " INTEGER NOT NULL,"
                + PhotoColumns.PHOTO_PATH + " TEXT NOT NULL,"
                + "UNIQUE (" + PhotoColumns.PHOTO_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.SOUNDS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SoundColumns.SOUND_ID + " TEXT NOT NULL,"
                + SoundColumns.SOUND_PATH + " TEXT NOT NULL,"
                + SoundColumns.SOUND_MARK + " TEXT NOT NULL,"
                + "UNIQUE (" + SoundColumns.SOUND_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("CREATE TABLE " + Tables.LANGUAGES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LanguageColumns.LANGUAGE_ID + " TEXT NOT NULL,"
                + LanguageColumns.LANGUAGE_ACTIVE + " TEXT,"
                + "UNIQUE (" + LanguageColumns.LANGUAGE_ID + ") ON CONFLICT REPLACE)");

        db.execSQL("PRAGMA user_version = " + DB_VERSION);
        Log.d("PopapDatabase", "onCreate");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != DB_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.PHOTOS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.SOUNDS);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.LANGUAGES);
            onCreate(db);
        }
    }


    public static synchronized PopapDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PopapDatabase(context);
        }
        return mInstance;
    }


    @Override
    public void close() {
        super.close();
    }


    private boolean checkDatabase() {
        synchronized (this) {
            boolean result = false;
            SQLiteDatabase checkdb = null;
            try {
                String myPath = mPath + DATABASE_NAME;
                File file = new File(myPath);
                if (!file.exists()) {
                    return false;
                }
                checkdb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
                if (checkdb != null) {
                    int v = checkdb.getVersion();
                    if (v == 0) {
                        checkdb.execSQL("PRAGMA user_version = 1");
                    }
                    checkdb.close();
                    result = true;
                }
            } catch (Exception e) {
                new RuntimeException(e);
            } finally {
                if (checkdb != null) {
                    checkdb.close();
                }
            }
            return result;
        }
    }

    public void checkAndCreateDatabase() {
        synchronized (this) {
            boolean dbexit = checkDatabase();
            if (!dbexit) {
            }
        }
    }


    private void openDatabase() {
        if (mSQLite != null && mSQLite.isOpen()) {
            mSQLite.close();
            mSQLite = null;
        }
        mSQLite = getWritableDatabase();
        if (!mSQLite.isReadOnly()) {
            // Enable foreign key constraints
            mSQLite.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    private void closeDatabase() {
        if (mSQLite != null) {
            mSQLite.close();
            mSQLite = null;
        }
    }

    public boolean checkPhotoExist(String id) {
        boolean ret = false;
        synchronized (this) {
            Cursor cursor = null;
            try {
                openDatabase();
                if (mSQLite == null) {
                    return false;
                }
                String queryStatement = "SELECT * FROM " + Tables.PHOTOS + " WHERE " + PhotoColumns.PHOTO_ID + " = ?";
                String[] selectionArgs = new String[]{id};
                cursor = mSQLite.rawQuery(queryStatement, selectionArgs);
                if (cursor != null && cursor.getCount() > 0)
                    ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
        return ret;
    }

    public long insertPhoto(PhotoBean photo) {
        long ret = 0;
        synchronized (this) {
            try {
                openDatabase();
                if (mSQLite == null) {
                    return -1;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(PhotoColumns.PHOTO_ID, photo.getPhotoId());
                contentValues.put(PhotoColumns.PHOTO_PATH, photo.getPhotoPath());
                ret = mSQLite.insert(Tables.PHOTOS, null, contentValues);

                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
                closeDatabase();
            }
        }
    }

    public long updatePhotoPath(String id, String path) {
        long ret = 0;
        ContentValues contentValues = new ContentValues();
        synchronized (this) {
            try {
                openDatabase();
                if (mSQLite == null) {
                    return -1;
                }
                contentValues.put(PhotoColumns.PHOTO_PATH, path);

                String sql = PhotoColumns.PHOTO_ID + " = ?";
                String[] params = {id};
                ret = mSQLite.update(Tables.PHOTOS, contentValues, sql, params);
                return ret;
            } catch (Exception exception) {
                exception.printStackTrace();
                return -1;
            } finally {
                closeDatabase();
            }
        }
    }

    public PhotoBean getPhoto(String id) {
        synchronized (this) {
            PhotoBean photo = new PhotoBean();
            Cursor cursor = null;
            try {
                openDatabase();
                if (mSQLite == null) {
                    return null;
                }
                String queryStatement = "SELECT * FROM " + Tables.PHOTOS + " WHERE " + PhotoColumns.PHOTO_ID + " = ?";
                String[] selectionArgs = new String[]{id};
                cursor = mSQLite.rawQuery(queryStatement, selectionArgs);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            photo.setPhotoId(cursor.getString(cursor.getColumnIndex(PhotoColumns.PHOTO_ID)));
                            photo.setPhotoPath(cursor.getString(cursor.getColumnIndex(PhotoColumns.PHOTO_PATH)));
                        } while (cursor.moveToNext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                closeDatabase();
            }

            return photo;
        }
    }

    public ArrayList<PhotoBean> getPhotos() {
        synchronized (this) {
            ArrayList<PhotoBean> photoList = new ArrayList<>();
            Cursor cursor = null;
            try {
                mSQLite = getWritableDatabase();
                openDatabase();
                if (mSQLite == null) {
                    return null;
                }
                String queryStatement = "SELECT * FROM " + Tables.PHOTOS;
                cursor = mSQLite.rawQuery(queryStatement, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            PhotoBean photo = new PhotoBean();
                            photo.setPhotoId(cursor.getString(cursor.getColumnIndex(PhotoColumns.PHOTO_ID)));
                            photo.setPhotoPath(cursor.getString(cursor.getColumnIndex(PhotoColumns.PHOTO_PATH)));
                            photoList.add(photo);
                        } while (cursor.moveToNext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                closeDatabase();
            }
            return photoList;
        }
    }

    public boolean checkSoundExist(String id) {
        boolean ret = false;
        synchronized (this) {
            Cursor cursor = null;
            try {
                openDatabase();
                if (mSQLite == null) {
                    return false;
                }
                String queryStatement = "SELECT * FROM " + Tables.SOUNDS + " WHERE " + SoundColumns.SOUND_ID + " = ?";
                String[] selectionArgs = new String[]{id};
                cursor = mSQLite.rawQuery(queryStatement, selectionArgs);
                if (cursor != null && cursor.getCount() > 0)
                    ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
        return ret;
    }


    public long insertSound(SoundBean sound) {
        long ret = 0;
        synchronized (this) {
            try {
                openDatabase();
                if (mSQLite == null) {
                    return -1;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(SoundColumns.SOUND_ID, sound.getSoundId());
                contentValues.put(SoundColumns.SOUND_PATH, sound.getSoundPath());
                contentValues.put(SoundColumns.SOUND_MARK, sound.getSoundMark());
                ret = mSQLite.insert(Tables.SOUNDS, null, contentValues);

                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
                closeDatabase();
            }
        }
    }

    public long updateSoundPath(String id, String path) {
        long ret = 0;
        ContentValues contentValues = new ContentValues();
        synchronized (this) {
            try {
                openDatabase();
                if (mSQLite == null) {
                    return -1;
                }
                contentValues.put(SoundColumns.SOUND_PATH, path);

                String sql = SoundColumns.SOUND_ID + " = ?";
                String[] params = {id};
                ret = mSQLite.update(Tables.SOUNDS, contentValues, sql, params);
                return ret;
            } catch (Exception exception) {
                exception.printStackTrace();
                return -1;
            } finally {
                closeDatabase();
            }
        }
    }

    public long updateSoundMark(String id, String mark) {
        long ret = 0;
        ContentValues contentValues = new ContentValues();
        synchronized (this) {
            try {
                openDatabase();
                if (mSQLite == null) {
                    return -1;
                }
                contentValues.put(SoundColumns.SOUND_MARK, mark);

                String sql = SoundColumns.SOUND_ID + " = ?";
                String[] params = {id};
                ret = mSQLite.update(Tables.SOUNDS, contentValues, sql, params);
                return ret;
            } catch (Exception exception) {
                exception.printStackTrace();
                return -1;
            } finally {
                closeDatabase();
            }
        }
    }

    public SoundBean getSound(String id) {
        synchronized (this) {
            SoundBean sound = new SoundBean();
            Cursor cursor = null;
            try {
                openDatabase();
                if (mSQLite == null) {
                    return null;
                }
                String queryStatement = "SELECT * FROM " + Tables.SOUNDS + " WHERE " + SoundColumns.SOUND_ID + " = ?";
                String[] selectionArgs = new String[]{id};
                cursor = mSQLite.rawQuery(queryStatement, selectionArgs);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            sound.setSoundId(cursor.getString(cursor.getColumnIndex(SoundColumns.SOUND_ID)));
                            sound.setSoundPath(cursor.getString(cursor.getColumnIndex(SoundColumns.SOUND_PATH)));
                            sound.setSoundMark(cursor.getString(cursor.getColumnIndex(SoundColumns.SOUND_MARK)));
                        } while (cursor.moveToNext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                closeDatabase();
            }

            return sound;
        }
    }

    public ArrayList<SoundBean> getSounds() {
        synchronized (this) {
            ArrayList<SoundBean> soundList = new ArrayList<>();
            Cursor cursor = null;
            try {
                mSQLite = getWritableDatabase();
                openDatabase();
                if (mSQLite == null) {
                    return null;
                }
                String queryStatement = "SELECT * FROM " + Tables.SOUNDS;
                cursor = mSQLite.rawQuery(queryStatement, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            SoundBean sound = new SoundBean();
                            sound.setSoundId(cursor.getString(cursor.getColumnIndex(SoundColumns.SOUND_ID)));
                            sound.setSoundPath(cursor.getString(cursor.getColumnIndex(SoundColumns.SOUND_PATH)));
                            sound.setSoundMark(cursor.getString(cursor.getColumnIndex(SoundColumns.SOUND_MARK)));
                            soundList.add(sound);
                        } while (cursor.moveToNext());
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                closeDatabase();
            }
            return soundList;
        }
    }

    public boolean checkLanguageExist(String id) {
        boolean ret = false;
        synchronized (this) {
            Cursor cursor = null;
            try {
                openDatabase();
                if (mSQLite == null) {
                    return false;
                }
                String queryStatement = "SELECT * FROM " + Tables.LANGUAGES + " WHERE " + LanguageColumns.LANGUAGE_ID + " = ?";
                String[] selectionArgs = new String[]{id};
                cursor = mSQLite.rawQuery(queryStatement, selectionArgs);
                if (cursor != null && cursor.getCount() > 0)
                    ret = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
        return ret;
    }

    public long insertLanguage(LanguageBean language) {
        long ret = 0;
        synchronized (this) {
            try {
                openDatabase();
                if (mSQLite == null) {
                    return -1;
                }
                ContentValues contentValues = new ContentValues();
                contentValues.put(LanguageColumns.LANGUAGE_ID, language.getLanguageId());
                contentValues.put(LanguageColumns.LANGUAGE_ACTIVE, language.getLanguageActive());
                ret = mSQLite.insert(Tables.LANGUAGES, null, contentValues);

                return ret;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            } finally {
                closeDatabase();
            }
        }
    }

    public LanguageBean getLanguage(String id) {
        synchronized (this) {
            LanguageBean language = new LanguageBean();
            Cursor cursor = null;
            try {
                openDatabase();
                if (mSQLite == null) {
                    return null;
                }
                String queryStatement = "SELECT * FROM " + Tables.LANGUAGES + " WHERE " + LanguageColumns.LANGUAGE_ID + " = ?";
                String[] selectionArgs = new String[]{id};
                cursor = mSQLite.rawQuery(queryStatement, selectionArgs);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            language.setLanguageId(cursor.getString(cursor.getColumnIndex(LanguageColumns.LANGUAGE_ID)));
                            language.setLanguageActive(cursor.getString(cursor.getColumnIndex(LanguageColumns.LANGUAGE_ACTIVE)));
                        } while (cursor.moveToNext());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                closeDatabase();
            }

            return language;
        }
    }

    public long getRowId(String table) {
        long ret = -1;
        synchronized (this) {
            Cursor cursor = null;
            try {
                openDatabase();
                if (mSQLite == null) {
                    return ret;
                }
                String queryStatement = "SELECT ROWID from " + table + " order by ROWID DESC limit 1";
                cursor = mSQLite.rawQuery(queryStatement, null);
                if (cursor != null && cursor.moveToFirst())
                    ret = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeDatabase();
            }
        }
        return ret;
    }

    public long getPhotoId() {
        return getRowId(Tables.PHOTOS) + 1;
    }
}
