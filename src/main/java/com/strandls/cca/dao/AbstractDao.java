package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.strandls.cca.CCAConstants;
import com.strandls.cca.IdInterface;

public abstract class AbstractDao<T extends IdInterface> {

	protected MongoCollection<T> dbCollection;

	@Inject
	protected AbstractDao(Class<T> collectionType, MongoDatabase db) {
		this.dbCollection = db.getCollection(collectionType.getSimpleName(), collectionType);
	}
	
	private Bson getIdFilter(String id) {
		return Filters.eq("_id", id);
	}

	public T findByProperty(String shortName, Object value) {
		Bson isDeleted = Filters.eq(CCAConstants.IS_DELETED, false);
		Bson filter = Filters.eq(shortName, value);
		return dbCollection.find(Filters.and(isDeleted, filter)).first();
	}

	public T getById(String id) {
		return dbCollection.find(getIdFilter(id)).first();
	}

	public List<T> getAll(int limit, int offset) {
		Bson filters = Filters.eq(CCAConstants.IS_DELETED, false);
		return dbCollection.find(filters).skip(offset).batchSize(limit).into(new ArrayList<T>());
	}

	public List<T> getAll() {
		Bson filters = Filters.eq(CCAConstants.IS_DELETED, false);
		return dbCollection.find(filters).into(new ArrayList<T>());
	}

	public T save(T t) {
		if (t.getId() == null) {
			ObjectId id = new ObjectId();
			t.setId(id.toHexString());
		}
		dbCollection.insertOne(t);
		return t;
	}

	public List<T> insertBulk(List<T> ts) {
		for (T t : ts) {
			if (t.getId() == null) {
				ObjectId id = new ObjectId();
				t.setId(id.toHexString());
			}
		}
		dbCollection.insertMany(ts);
		return ts;
	}

	public T remove(T t) {
		DeleteResult dResult = dbCollection.deleteOne(getIdFilter(t.getId()));
		if (dResult.getDeletedCount() == 0) {
			throw new IllegalArgumentException("Can't delete object, it is not existing the system");
		}
		return t;
	}

	public T replaceOne(T t) {
		UpdateResult updateResult = dbCollection.replaceOne(getIdFilter(t.getId()), t);
		if (updateResult.getMatchedCount() != 1) {
			throw new IllegalArgumentException("Could not found the result");
		}
		return t;
	}
}
