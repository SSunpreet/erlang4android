package com.ernovation.erlangforandroid.database;

import java.util.ArrayList;
import java.util.List;

import com.ernovation.erlangforandroid.LibraryInfo;
import com.ernovation.erlangforandroid.Repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LibsDb {

	SQLiteDatabase iDb;

	public LibsDb(Context aContext) {
		iDb = new LibsOpenHelper(aContext).getWritableDatabase();
	}

	public Cursor rowsCursor() {
		return iDb
				.rawQuery(
						"SELECT _id, name, version, url FROM libraries ORDER BY name, version;",
						new String[] {});
	}

	public Cursor repositoriesCursor() {
		return iDb.query("repositories", new String[] { "_id", "name", "url" },
				null, null, null, null, null);
	}

	public void deleteAllFromRepository(int aRepo) {
		iDb.execSQL("DELETE FROM libraries WHERE repository=?",
				new String[] { "" + aRepo });
	}

	public Repository getRepositoryById(long aId) {
		Cursor c = iDb.query("repositories", new String[] {"_id",  "name",  "url"}, "_id=?",  new String[] {""+aId},  null, null, null, null);
		if (c.moveToFirst()) {
			Repository repo = new Repository();
			repo.setId(c.getLong(0));
			repo.setName(c.getString(1));
			repo.setURL(c.getString(2));
			return repo;
		}
		return null;
	}
	
	public LibraryInfo getLibraryInfoById(long aId) {
		Cursor c = iDb
				.rawQuery(
						"SELECT libraries.name, libraries.repository, "
								+ "libraries.summary, libraries.abstract, libraries.keywords, libraries.author, "
								+ "libraries.packager, libraries.category, libraries.depends, "
								+ "libraries.home, libraries.url, libraries.version, libraries.date, "
								+ "repositories.name, repositories.url, libraries.state, libraries._id "
								+ "FROM libraries, repositories "
								+ "WHERE libraries._id=? AND libraries.repository=repositories._id",
						new String[] { "" + aId });
		if (c.moveToFirst()) {
			LibraryInfo res = libraryInfoFromRow(c);
			return res;
		} else {
			return null;
		}
	}

	public List<LibraryInfo> getLibraryInfoByState(int aState) {
		/* In the query, ~libraries.state & ? == 0 is equivalent to
		 * (libraries.state & ? == ?), but requires only a single ?, no copy.
		 */
		Cursor c = iDb
				.rawQuery(
						"SELECT libraries.name, libraries.repository, "
								+ "libraries.summary, libraries.abstract, libraries.keywords, libraries.author, "
								+ "libraries.packager, libraries.category, libraries.depends, "
								+ "libraries.home, libraries.url, libraries.version, libraries.date, "
								+ "repositories.name, repositories.url, libraries.state, libraries._id "
								+ "FROM libraries, repositories "
								+ "WHERE ((~libraries.state & ?) == 0) AND libraries.repository=repositories._id",
						new String[] { "" + aState });
		ArrayList<LibraryInfo> res = new ArrayList<LibraryInfo>();
		if (c.moveToFirst()) {
			LibraryInfo info;
			do {
				info = libraryInfoFromRow(c);
				res.add(info);
			} while (c.moveToNext());
		}
		return res;
	}
	
	public void updateLib(LibraryInfo aLib) {
		iDb.execSQL(
				"INSERT OR REPLACE INTO libraries (name, " + "repository, "
						+ "summary, " + "abstract, " + "keywords, "
						+ "author, " + "packager, " + "category, "
						+ "depends, " + "home, " + "url, " + "version, "
						+ "date, state) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);",
				new String[] { "" + aLib.getName(),
						"" + aLib.getRepository().getId(), "" + aLib.getSummary(),
						"" + aLib.getAbstract(), "" + aLib.getKeywords(),
						"" + aLib.getAuthor(), "" + aLib.getPackager(),
						"" + aLib.getCategory(), "" + aLib.getDepends(),
						"" + aLib.getHome(), "" + aLib.getURL(),
						"" + aLib.getVersion(), "" + aLib.getDate() , ""+aLib.getState()});
	}
	
	public void updateRepository(Repository aRepo) {
		ContentValues values = new ContentValues();
		values.put("name", aRepo.getName());
		values.put("url",  aRepo.getURL());
		
		iDb.update("repositories", values, "_id=?", new String[] {""+aRepo.getId()});
	}
				
	public void insertRepository(Repository aRepo) {
		iDb.execSQL(
				"INSERT OR REPLACE INTO repositories (name, " + "url"
						+ ") VALUES (?,?);",
				new String[] { "" + aRepo.getName(),
						"" + aRepo.getURL()});
	}
	
	private LibraryInfo libraryInfoFromRow(Cursor c) {
		LibraryInfo res = new LibraryInfo();
		res.setName(c.getString(0));
		res.setSummary(c.getString(2));
		res.setAbstract(c.getString(3));
		res.setKeywords(c.getString(4));
		res.setAuthor(c.getString(5));
		res.setPackager(c.getString(6));
		res.setCategory(c.getString(7));
		res.setDepends(c.getString(8));
		res.setHome(c.getString(9));
		res.setURL(c.getString(10));
		res.setVersion(c.getString(11));
		res.setDate(c.getString(12));
		res.setState(c.getInt(15));
		res.setId(c.getLong(16));
		Repository repo = new Repository();
		repo.setName(c.getString(13));
		repo.setURL(c.getString(14));
		repo.setId(c.getLong(1));
		res.setRepository(repo);
		return res;
	}

}
