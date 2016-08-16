package ru.varlamova.db.dao;

import com.mongodb.*;
import ru.varlamova.entity.PingRequest;
import ru.varlamova.http.exception.ServerException;

import java.net.UnknownHostException;
import java.util.Properties;

public class PingRequestDAOImpl implements PingRequestDAO {

    private DBCollection table;

    public PingRequestDAOImpl() {
        try {
            Properties properties = System.getProperties();
            MongoClient mongo = new MongoClient(properties.getProperty("mongo.url"), Integer.parseInt(properties.getProperty("mongo.port")));
            DB db = mongo.getDB(properties.getProperty("mongo.dbname"));
            table = db.getCollection(properties.getProperty("mongo.collection"));
            table.ensureIndex("id");
        } catch (UnknownHostException e) {
            throw new ServerException("Can't establish connection with mongo, check url and port");
        }
    }

    public void saveOrUpdate(PingRequest pingRequest) {
        Object userId = pingRequest.getId();
        BasicDBObject currentPingRequest = new BasicDBObject();
        currentPingRequest.put("id", userId);
        DBObject userInfo = table.findOne(currentPingRequest);
        if (userInfo != null && userInfo.get("count") != null) {
            int count = (int) userInfo.get("count");
            table.update(userInfo, new BasicDBObject().append("id", userId).append("count", ++count));
        } else {
            currentPingRequest.put("count", 1);
            table.insert(currentPingRequest);
        }
    }

    public String getCountByUser(PingRequest pingRequest) {
        return String.valueOf(table.findOne(new BasicDBObject("id", pingRequest.getId())).get("count"));
    }
}
