package com.example.seismicdetector.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DetectionDao_Impl implements DetectionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DetectionEntity> __insertionAdapterOfDetectionEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldDetections;

  public DetectionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDetectionEntity = new EntityInsertionAdapter<DetectionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `detections` (`id`,`timestamp`,`confidence`,`intensity`,`intensityLabel`,`pga`,`dominantFreq`,`duration`,`waveformPath`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DetectionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindDouble(3, entity.getConfidence());
        statement.bindLong(4, entity.getIntensity());
        statement.bindString(5, entity.getIntensityLabel());
        statement.bindDouble(6, entity.getPga());
        statement.bindDouble(7, entity.getDominantFreq());
        statement.bindDouble(8, entity.getDuration());
        if (entity.getWaveformPath() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getWaveformPath());
        }
      }
    };
    this.__preparedStmtOfDeleteOldDetections = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM detections WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDetection(final DetectionEntity detection,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDetectionEntity.insert(detection);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldDetections(final long cutoffTime,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldDetections.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldDetections.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DetectionEntity>> getRecentDetections() {
    final String _sql = "SELECT * FROM detections ORDER BY timestamp DESC LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"detections"}, new Callable<List<DetectionEntity>>() {
      @Override
      @NonNull
      public List<DetectionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfIntensityLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "intensityLabel");
          final int _cursorIndexOfPga = CursorUtil.getColumnIndexOrThrow(_cursor, "pga");
          final int _cursorIndexOfDominantFreq = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantFreq");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfWaveformPath = CursorUtil.getColumnIndexOrThrow(_cursor, "waveformPath");
          final List<DetectionEntity> _result = new ArrayList<DetectionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DetectionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final String _tmpIntensityLabel;
            _tmpIntensityLabel = _cursor.getString(_cursorIndexOfIntensityLabel);
            final float _tmpPga;
            _tmpPga = _cursor.getFloat(_cursorIndexOfPga);
            final float _tmpDominantFreq;
            _tmpDominantFreq = _cursor.getFloat(_cursorIndexOfDominantFreq);
            final float _tmpDuration;
            _tmpDuration = _cursor.getFloat(_cursorIndexOfDuration);
            final String _tmpWaveformPath;
            if (_cursor.isNull(_cursorIndexOfWaveformPath)) {
              _tmpWaveformPath = null;
            } else {
              _tmpWaveformPath = _cursor.getString(_cursorIndexOfWaveformPath);
            }
            _item = new DetectionEntity(_tmpId,_tmpTimestamp,_tmpConfidence,_tmpIntensity,_tmpIntensityLabel,_tmpPga,_tmpDominantFreq,_tmpDuration,_tmpWaveformPath);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getDetectionById(final long id,
      final Continuation<? super DetectionEntity> $completion) {
    final String _sql = "SELECT * FROM detections WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DetectionEntity>() {
      @Override
      @Nullable
      public DetectionEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "intensity");
          final int _cursorIndexOfIntensityLabel = CursorUtil.getColumnIndexOrThrow(_cursor, "intensityLabel");
          final int _cursorIndexOfPga = CursorUtil.getColumnIndexOrThrow(_cursor, "pga");
          final int _cursorIndexOfDominantFreq = CursorUtil.getColumnIndexOrThrow(_cursor, "dominantFreq");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfWaveformPath = CursorUtil.getColumnIndexOrThrow(_cursor, "waveformPath");
          final DetectionEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final int _tmpIntensity;
            _tmpIntensity = _cursor.getInt(_cursorIndexOfIntensity);
            final String _tmpIntensityLabel;
            _tmpIntensityLabel = _cursor.getString(_cursorIndexOfIntensityLabel);
            final float _tmpPga;
            _tmpPga = _cursor.getFloat(_cursorIndexOfPga);
            final float _tmpDominantFreq;
            _tmpDominantFreq = _cursor.getFloat(_cursorIndexOfDominantFreq);
            final float _tmpDuration;
            _tmpDuration = _cursor.getFloat(_cursorIndexOfDuration);
            final String _tmpWaveformPath;
            if (_cursor.isNull(_cursorIndexOfWaveformPath)) {
              _tmpWaveformPath = null;
            } else {
              _tmpWaveformPath = _cursor.getString(_cursorIndexOfWaveformPath);
            }
            _result = new DetectionEntity(_tmpId,_tmpTimestamp,_tmpConfidence,_tmpIntensity,_tmpIntensityLabel,_tmpPga,_tmpDominantFreq,_tmpDuration,_tmpWaveformPath);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
