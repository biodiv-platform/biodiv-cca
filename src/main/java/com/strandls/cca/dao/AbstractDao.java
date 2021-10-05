package com.strandls.cca.dao;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.strandls.cca.IdInterface;

public abstract class AbstractDao<T extends IdInterface> {

	protected MongoCollection<T> dbCollection;

	@Inject
	protected AbstractDao(Class<T> collectionType, MongoDatabase db) {
		this.dbCollection = db.getCollection(collectionType.getSimpleName(), collectionType);
	}

	public T findByProperty(String shortName, Object value) {
		return dbCollection.find(Filters.eq(shortName, value)).first();
	}

	public T getById(String id) {
		return dbCollection.find(Filters.eq("id", id)).first();
	}

	public List<T> getAll(int limit, int offset) {
		return dbCollection.find().skip(offset).batchSize(limit).into(new ArrayList<T>());
	}

	public List<T> getAll() {
		return dbCollection.find().into(new ArrayList<T>());
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
		DeleteResult dResult = dbCollection.deleteOne(Filters.eq("id", t.getId()));
		if (dResult.getDeletedCount() == 0) {
			throw new IllegalArgumentException("Can't delete object, it is not existing the system");
		}
		return t;
	}

	public T replaceOne(T t) {
		UpdateResult updateResult = dbCollection.replaceOne(Filters.eq("_id", t.getId()), t);
		if (updateResult.getMatchedCount() != 1) {
			throw new IllegalArgumentException("Could not found the result");
		}
		return t;
	}

	public T updateOne(T t) {
		return t;
	}
}
