package ru.varlamova.db.dao;

import ru.varlamova.entity.PingRequest;

public interface PingRequestDAO {

    void saveOrUpdate(PingRequest pingRequest);

    String getCountByUser(PingRequest pingRequest);

}
