/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.tool;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.CallNote;
import net.micode.notes.data.Notes.NoteColumns;
import net.micode.notes.ui.NotesListAdapter.AppWidgetAttribute;

import java.util.ArrayList;
import java.util.HashSet;


public class DataUtils {
    public static final String TAG = "DataUtils";

    /**
     * 批量删除便签
     * @param resolver 内容解析器
     * @param ids 便签的id
     * @return
     */
    public static boolean batchDeleteNotes(ContentResolver resolver, HashSet<Long> ids) {
        if (ids == null) {
            Log.d(TAG, "the ids is null");
            return true;
        }
        if (ids.size() == 0) {
            Log.d(TAG, "no id is in the hashset");
            return true;
        }

        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        for (long id : ids) {
            // 要删除的系统的根目录
            if(id == Notes.ID_ROOT_FOLDER) {
                Log.e(TAG, "Don't delete system folder root");
                continue;
            }
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newDelete(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, id));
            operationList.add(builder.build());
        }
        try {
            ContentProviderResult[] results = resolver.applyBatch(Notes.AUTHORITY, operationList);
            // 删除失败
            if (results == null || results.length == 0 || results[0] == null) {
                Log.d(TAG, "delete notes failed, ids:" + ids.toString());
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        } catch (OperationApplicationException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        }
        return false;
    }

    /**
     * 移动便签到其他文件夹
     * @param resolver 解析器
     * @param id 便签id
     * @param srcFolderId 便签的源文件夹id
     * @param desFolderId 目标文件夹id
     */
    public static void moveNoteToFoler(ContentResolver resolver, long id, long srcFolderId, long desFolderId) {
        ContentValues values = new ContentValues();
        values.put(NoteColumns.PARENT_ID, desFolderId);
        values.put(NoteColumns.ORIGIN_PARENT_ID, srcFolderId);
        values.put(NoteColumns.LOCAL_MODIFIED, 1);
        resolver.update(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, id), values, null, null);
    }

    /**
     * 批量移动便签到文件夹
     * @param resolver 解析器
     * @param ids 便签id
     * @param folderId 文件夹id
     * @return
     */
    public static boolean batchMoveToFolder(ContentResolver resolver, HashSet<Long> ids,
            long folderId) {
        if (ids == null) {
            Log.d(TAG, "the ids is null");
            return true;
        }

        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        for (long id : ids) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newUpdate(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, id));
            builder.withValue(NoteColumns.PARENT_ID, folderId);
            builder.withValue(NoteColumns.LOCAL_MODIFIED, 1);
            operationList.add(builder.build());
        }

        try {
            ContentProviderResult[] results = resolver.applyBatch(Notes.AUTHORITY, operationList);
            if (results == null || results.length == 0 || results[0] == null) {
                Log.d(TAG, "delete notes failed, ids:" + ids.toString());
                return false;
            }
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        } catch (OperationApplicationException e) {
            Log.e(TAG, String.format("%s: %s", e.toString(), e.getMessage()));
        }
        return false;
    }

    /**
     * Get the all folder count except system folders {@link Notes#TYPE_SYSTEM}}
     */
    public static int getUserFolderCount(ContentResolver resolver) {
        Cursor cursor =resolver.query(Notes.CONTENT_NOTE_URI,
                new String[] { "COUNT(*)" },
                NoteColumns.TYPE + "=? AND " + NoteColumns.PARENT_ID + "<>?",
                new String[] { String.valueOf(Notes.TYPE_FOLDER), String.valueOf(Notes.ID_TRASH_FOLER)},
                null);

        int count = 0;
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                try {
                    count = cursor.getInt(0);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "get folder count failed:" + e.toString());
                } finally {
                    cursor.close();
                }
            }
        }
        return count;
    }

    public static boolean visibleInNoteDatabase(ContentResolver resolver, long noteId, int type) {
        Cursor cursor = resolver.query(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId),
                null,
                NoteColumns.TYPE + "=? AND " + NoteColumns.PARENT_ID + "<>" + Notes.ID_TRASH_FOLER,
                new String [] {String.valueOf(type)},
                null);

        boolean exist = false;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }

    /**
     * 检查note数据库是否存在
     * @param resolver
     * @param noteId
     * @return
     */
    public static boolean existInNoteDatabase(ContentResolver resolver, long noteId) {
        Cursor cursor = resolver.query(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, noteId),
                null, null, null, null);

        boolean exist = false;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }

    /**
     * 检查data数据库是否存在
     * @param resolver 解析器
     * @param dataId dataid
     * @return
     */
    public static boolean existInDataDatabase(ContentResolver resolver, long dataId) {
        Cursor cursor = resolver.query(ContentUris.withAppendedId(Notes.CONTENT_DATA_URI, dataId),
                null, null, null, null);

        boolean exist = false;
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }

    /**
     * 检查可见的文件夹名
     * @param resolver 解析器
     * @param name 文件夹名
     * @return
     */
    public static boolean checkVisibleFolderName(ContentResolver resolver, String name) {
        Cursor cursor = resolver.query(Notes.CONTENT_NOTE_URI, null,
                NoteColumns.TYPE + "=" + Notes.TYPE_FOLDER +
                " AND " + NoteColumns.PARENT_ID + "<>" + Notes.ID_TRASH_FOLER +
                " AND " + NoteColumns.SNIPPET + "=?",
                new String[] { name }, null);
        boolean exist = false;
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
        }
        return exist;
    }

    /**
     * 获得文件夹便签小部件
     * @param resolver 解析器
     * @param folderId 文件夹id
     * @return
     */
    public static HashSet<AppWidgetAttribute> getFolderNoteWidget(ContentResolver resolver, long folderId) {
        Cursor c = resolver.query(Notes.CONTENT_NOTE_URI,
                new String[] { NoteColumns.WIDGET_ID, NoteColumns.WIDGET_TYPE },
                NoteColumns.PARENT_ID + "=?",
                new String[] { String.valueOf(folderId) },
                null);

        HashSet<AppWidgetAttribute> set = null;
        if (c != null) {
            if (c.moveToFirst()) {
                set = new HashSet<AppWidgetAttribute>();
                do {
                    try {
                        AppWidgetAttribute widget = new AppWidgetAttribute();
                        widget.widgetId = c.getInt(0);
                        widget.widgetType = c.getInt(1);
                        set.add(widget);
                    } catch (IndexOutOfBoundsException e) {
                        Log.e(TAG, e.toString());
                    }
                } while (c.moveToNext());
            }
            c.close();
        }
        return set;
    }

    /**
     * 通过便签id获得电话号码
     * @param resolver 解析器
     * @param noteId 便签id
     * @return
     */
    public static String getCallNumberByNoteId(ContentResolver resolver, long noteId) {
        Cursor cursor = resolver.query(Notes.CONTENT_DATA_URI,
                new String [] { CallNote.PHONE_NUMBER },
                CallNote.NOTE_ID + "=? AND " + CallNote.MIME_TYPE + "=?",
                new String [] { String.valueOf(noteId), CallNote.CONTENT_ITEM_TYPE },
                null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                return cursor.getString(0);
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "Get call number fails " + e.toString());
            } finally {
                cursor.close();
            }
        }
        return "";
    }

    /**
     * 通过电话号码和呼叫日期获得便签id
     * @param resolver 解析器
     * @param phoneNumber 电话号码
     * @param callDate 呼叫日期
     * @return
     */
    public static long getNoteIdByPhoneNumberAndCallDate(ContentResolver resolver, String phoneNumber, long callDate) {
        Cursor cursor = resolver.query(Notes.CONTENT_DATA_URI,
                new String [] { CallNote.NOTE_ID },
                CallNote.CALL_DATE + "=? AND " + CallNote.MIME_TYPE + "=? AND PHONE_NUMBERS_EQUAL("
                + CallNote.PHONE_NUMBER + ",?)",
                new String [] { String.valueOf(callDate), CallNote.CONTENT_ITEM_TYPE, phoneNumber },
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                try {
                    return cursor.getLong(0);
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "Get call note id fails " + e.toString());
                }
            }
            cursor.close();
        }
        return 0;
    }

    /**
     * 通过noteid获得片段
     * @param resolver 解析器
     * @param noteId 便签id
     * @return
     */
    public static String getSnippetById(ContentResolver resolver, long noteId) {
        Cursor cursor = resolver.query(Notes.CONTENT_NOTE_URI,
                new String [] { NoteColumns.SNIPPET },
                NoteColumns.ID + "=?",
                new String [] { String.valueOf(noteId)},
                null);

        if (cursor != null) {
            String snippet = "";
            if (cursor.moveToFirst()) {
                snippet = cursor.getString(0);
            }
            cursor.close();
            return snippet;
        }
        throw new IllegalArgumentException("Note is not found with id: " + noteId);
    }

    /**
     * 获得格式化的片段
     * @param snippet
     * @return
     */
    public static String getFormattedSnippet(String snippet) {
        if (snippet != null) {
            snippet = snippet.trim();
            int index = snippet.indexOf('\n');
            if (index != -1) {
                snippet = snippet.substring(0, index);
            }
        }
        return snippet;
    }
}
