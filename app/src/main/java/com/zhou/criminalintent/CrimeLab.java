package com.zhou.criminalintent;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhou.criminalintent.database.CrimeBaseHelper;
import com.zhou.criminalintent.database.CrimeCursorWrapper;
import com.zhou.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    //    private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static CrimeLab getCrimeLab(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
//        mCrimes = new ArrayList<>();

//        for (int i = 0; i <= 100; i++) {
//            Crime crime = new Crime();
//            crime.setmTitle("Crime # " + i);
//            crime.setmSolved(i % 2 == 0);
//            mCrimes.add(crime);
//        }

    }

    public void addCrime(Crime crime) {
//        mCrimes.add(crime);
        ContentValues values = getContentValues(crime);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void removeCrime(Crime crime) {
//        mCrimes.remove(crime);
        String uuidString = crime.getmId().toString();
        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }


    public List<Crime> getCrimes() {
//        return mCrimes;
//        return new ArrayList<>();
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {
//        for (Crime crime : mCrimes) {
//            if (crime.getmId().equals(uuid)) {
//                return crime;
//            }
//        }

//        return null;
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[]{id.toString()}
        );
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getmId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    //    private Cursor queryCrimes(String whereClause, String[] whereArgs) {
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
//        return cursor;
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getmId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getmTitle());
        values.put(CrimeTable.Cols.DATE, crime.getmDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.ismSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.PHONE, crime.getmPhone());
        return values;
    }
}

